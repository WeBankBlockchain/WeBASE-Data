/**
 * Copyright 2014-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.webank.webase.data.collect.block;

import com.alibaba.fastjson.JSON;
import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.enums.TableName;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.base.tools.CommonTools;
import com.webank.webase.data.collect.block.entity.BlockInfo;
import com.webank.webase.data.collect.block.entity.BlockListParam;
import com.webank.webase.data.collect.block.entity.TbBlock;
import com.webank.webase.data.collect.frontinterface.FrontInterfaceService;
import com.webank.webase.data.collect.receipt.ReceiptService;
import com.webank.webase.data.collect.transaction.TransactionService;
import com.webank.webase.data.collect.transaction.entity.TbTransaction;
import com.webank.webase.data.collect.transaction.entity.TransactionInfo;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * services for block data. including pull block from chain and block service
 */
@Log4j2
@Service
public class BlockService {
    
    @Autowired
    private FrontInterfaceService frontInterface;
    @Autowired
    private BlockMapper blockMapper;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ReceiptService receiptService;
    private static final Long SAVE_TRANS_SLEEP_TIME = 5L;

    /**
     * copy chainBlock properties;
     */
    public static TbBlock chainBlock2TbBlock(BlockInfo blockInfo) {
        if (blockInfo == null) {
            return null;
        }
        BigInteger bigIntegerNumber = blockInfo.getNumber();
        LocalDateTime blockTimestamp =
                CommonTools.timestamp2LocalDateTime(Long.valueOf(blockInfo.getTimestamp()));
        int sealerIndex = Integer.parseInt(blockInfo.getSealer().substring(2), 16);

        List<TransactionInfo> transList = blockInfo.getTransactions();

        // save block info
        TbBlock tbBlock = new TbBlock(blockInfo.getHash(), bigIntegerNumber, blockTimestamp,
                transList.size(), sealerIndex);
        return tbBlock;
    }

    /**
     * save report block info.
     */
    @Transactional
    public void saveBlockInfo(BlockInfo blockInfo, Integer groupId) throws BaseException {
        // save block info
        TbBlock tbBlock = chainBlock2TbBlock(blockInfo);
        addBlockInfo(tbBlock, groupId);

        // save trans hash
        List<TransactionInfo> transList = blockInfo.getTransactions();
        for (TransactionInfo trans : transList) {
            TbTransaction tbTransaction = new TbTransaction(trans.getHash(), trans.getFrom(),
                    trans.getTo(), trans.getInput(), tbBlock.getBlockNumber(),
                    tbBlock.getBlockTimestamp());
            transactionService.addTransInfo(groupId, tbTransaction);

            // save receipt
            receiptService.handleReceiptInfo(groupId, trans.getHash());
            try {
                Thread.sleep(SAVE_TRANS_SLEEP_TIME);
            } catch (InterruptedException ex) {
                log.error("saveBLockInfo", ex);
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * add block info to db.
     */
    @Transactional
    public void addBlockInfo(TbBlock tbBlock, int groupId) throws BaseException {
        log.debug("start addBlockInfo tbBlock:{}", JSON.toJSONString(tbBlock));
        String tableName = TableName.BLOCK.getTableName(groupId);
        // save block info
        blockMapper.add(tableName, tbBlock);
    }

    /**
     * query block info list.
     */
    public List<TbBlock> queryBlockList(int groupId, BlockListParam queryParam)
            throws BaseException {
        log.debug("start queryBlockList groupId:{},queryParam:{}", groupId,
                JSON.toJSONString(queryParam));

        List<TbBlock> listOfBlock =
                blockMapper.getList(TableName.BLOCK.getTableName(groupId), queryParam);
        // check sealer
        listOfBlock.stream().forEach(block -> checkSearlerOfBlock(groupId, block));

        log.debug("end queryBlockList listOfBlockSize:{}", listOfBlock.size());
        return listOfBlock;
    }

    /**
     * query count of block.
     */
    public Integer queryCountOfBlock(Integer groupId, String blockHash, BigInteger blockNumber)
            throws BaseException {
        try {
            Integer count = blockMapper.getCount(TableName.BLOCK.getTableName(groupId), blockHash,
                    blockNumber);
            log.info("end countOfBlock groupId:{} blockHash:{} count:{}", groupId, blockHash, count);
            if (count == null) {
                return 0;
            }
            return count;
        } catch (RuntimeException ex) {
            log.error("fail countOfBlock groupId:{} blockHash:{}", groupId, blockHash, ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    public TbBlock getBlockByBlockNumber(int groupId, long blockNumber) {
        return blockMapper.findByBlockNumber(TableName.BLOCK.getTableName(groupId), blockNumber);
    }

    public Integer queryCountOfBlockByMinus(Integer groupId) {
        try {
            Integer count =
                    blockMapper.getBlockCountByMinMax(TableName.BLOCK.getTableName(groupId));
            log.info("end queryCountOfBlockByMinus groupId:{} count:{}", groupId, count);
            if (count == null) {
                return 0;
            }
            return count;
        } catch (RuntimeException ex) {
            log.error("fail queryCountOfBlockByMinus groupId:{},exception:{}", groupId, ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * get sealer by index.
     */
    public void checkSearlerOfBlock(int groupId, TbBlock tbBlock) {
        if (StringUtils.isNotBlank(tbBlock.getSealer())) {
            return;
        }

        // get sealer from chain.
        List<String> sealerList = frontInterface.getSealerList(groupId);
        String sealer = "0x0";
        if (sealerList != null && sealerList.size() > 0) {
            if (tbBlock.getSealerIndex() < sealerList.size()) {
                sealer = sealerList.get(tbBlock.getSealerIndex());
            } else {
                sealer = sealerList.get(0);
            }
        }
        tbBlock.setSealer(sealer);

        // save sealer
        blockMapper.update(TableName.BLOCK.getTableName(groupId), tbBlock);
    }


    /**
     * remove block into.
     */
    public Integer remove(Integer groupId, BigInteger blockRetainMax) throws BaseException {
        String tableName = TableName.BLOCK.getTableName(groupId);
        Integer affectRow = blockMapper.remove(tableName, blockRetainMax);
        return affectRow;
    }

    /**
     * get latest block number
     */
    public BigInteger getLatestBlockNumber(int groupId) {
        return blockMapper.getLatestBlockNumber(TableName.BLOCK.getTableName(groupId));
    }

    /**
     * get block by block from front server
     */
    public BlockInfo getBlockFromFrontByNumber(int groupId, BigInteger blockNumber) {
        return frontInterface.getBlockByNumber(groupId, blockNumber);
    }

    /**
     * get block by block from front server
     */
    public BlockInfo getblockFromFrontByHash(int groupId, String blockHash) {
        return frontInterface.getblockByHash(groupId, blockHash);
    }
}
