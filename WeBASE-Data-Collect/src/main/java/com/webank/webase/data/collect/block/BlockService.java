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

import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.enums.TableName;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.base.tools.CommonTools;
import com.webank.webase.data.collect.block.entity.BlockListParam;
import com.webank.webase.data.collect.block.entity.TbBlock;
import com.webank.webase.data.collect.frontinterface.FrontInterfaceService;
import com.webank.webase.data.collect.parser.ParserService;
import com.webank.webase.data.collect.receipt.ReceiptService;
import com.webank.webase.data.collect.receipt.entity.TbReceipt;
import com.webank.webase.data.collect.transaction.TransactionService;
import com.webank.webase.data.collect.transaction.entity.TbTransaction;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.web3j.protocol.core.methods.response.BcosBlock.Block;
import org.fisco.bcos.web3j.protocol.core.methods.response.BcosBlock.TransactionResult;
import org.fisco.bcos.web3j.protocol.core.methods.response.Transaction;
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
    @Autowired
    private ParserService parserService;

    private static final Long SAVE_TRANS_SLEEP_TIME = 5L;

    /**
     * copy chainBlock properties;
     */
    public static TbBlock chainBlock2TbBlock(Block block) {
        if (block == null) {
            return null;
        }
        LocalDateTime blockTimestamp =
                CommonTools.timestamp2LocalDateTime(block.getTimestamp().longValue());
        int sealerIndex = Integer.parseInt(block.getSealer().substring(2), 16);
        // save block info
        TbBlock tbBlock = new TbBlock(block.getHash(), block.getNumber(), blockTimestamp,
                block.getTransactions().size(), sealerIndex);
        return tbBlock;
    }

    /**
     * save report block info.
     */
    @Transactional
    @SuppressWarnings("rawtypes")
    public void saveBlockInfo(Block block, int chainId, int groupId) throws BaseException {
        // save block info
        TbBlock tbBlock = chainBlock2TbBlock(block);
        addBlockInfo(tbBlock, chainId, groupId);

        // save trans hash
        List<TransactionResult> transList = block.getTransactions();
        for (TransactionResult result : transList) {
            // save trans
            Transaction trans = (Transaction) result.get();
            TbTransaction tbTransaction =
                    new TbTransaction(trans.getHash(), trans.getFrom(), trans.getTo(),
                            trans.getInput(), trans.getBlockNumber(), tbBlock.getBlockTimestamp());
            transactionService.addTransInfo(chainId, groupId, tbTransaction);
            // save receipt
            TbReceipt tbReceipt = receiptService.handleReceiptInfo(chainId, groupId, trans.getHash());
            // parserTransaction
            parserService.parserTransaction(chainId, groupId, tbTransaction, tbReceipt);
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
    public void addBlockInfo(TbBlock tbBlock, int chainId, int groupId) throws BaseException {
        String tableName = TableName.BLOCK.getTableName(chainId, groupId);
        // save block info
        blockMapper.add(tableName, tbBlock);
    }

    /**
     * query block info list.
     */
    public List<TbBlock> queryBlockList(int chainId, int groupId, BlockListParam queryParam)
            throws BaseException {
        List<TbBlock> listOfBlock =
                blockMapper.getList(TableName.BLOCK.getTableName(chainId, groupId), queryParam);
        // check sealer
        listOfBlock.stream().forEach(block -> checkSearlerOfBlock(chainId, groupId, block));
        return listOfBlock;
    }

    /**
     * query count of block.
     */
    public Integer queryCountOfBlock(int chainId, int groupId, String blockHash,
            BigInteger blockNumber) throws BaseException {
        try {
            Integer count = blockMapper.getCount(TableName.BLOCK.getTableName(chainId, groupId),
                    blockHash, blockNumber);
            log.info("end countOfBlock groupId:{} blockHash:{} count:{}", groupId, blockHash,
                    count);
            if (count == null) {
                return 0;
            }
            return count;
        } catch (RuntimeException ex) {
            log.error("fail countOfBlock groupId:{} blockHash:{}", groupId, blockHash, ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    public TbBlock getBlockByBlockNumber(int chainId, int groupId, long blockNumber) {
        return blockMapper.findByBlockNumber(TableName.BLOCK.getTableName(chainId, groupId),
                blockNumber);
    }

    public Integer queryCountOfBlockByMinus(int chainId, int groupId) {
        try {
            Integer count = blockMapper
                    .getBlockCountByMinMax(TableName.BLOCK.getTableName(chainId, groupId));
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
    public void checkSearlerOfBlock(int chainId, int groupId, TbBlock tbBlock) {
        if (StringUtils.isNotBlank(tbBlock.getSealer())) {
            return;
        }

        // get sealer from chain.
        List<String> sealerList = frontInterface.getSealerList(chainId, groupId);
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
        blockMapper.update(TableName.BLOCK.getTableName(chainId, groupId), tbBlock);
    }


    /**
     * remove block into.
     */
    public Integer remove(int chainId, int groupId, BigInteger blockRetainMax)
            throws BaseException {
        String tableName = TableName.BLOCK.getTableName(chainId, groupId);
        Integer affectRow = blockMapper.remove(tableName, blockRetainMax);
        return affectRow;
    }

    /**
     * get latest block number
     */
    public BigInteger getLatestBlockNumber(int chainId, int groupId) {
        return blockMapper.getLatestBlockNumber(TableName.BLOCK.getTableName(chainId, groupId));
    }

    /**
     * get block by block from front server
     */
    public Block getBlockFromFrontByNumber(int chainId, int groupId, BigInteger blockNumber) {
        return frontInterface.getBlockByNumber(chainId, groupId, blockNumber);
    }

    /**
     * get block by block from front server
     */
    public Block getblockFromFrontByHash(int chainId, int groupId, String blockHash) {
        return frontInterface.getblockByHash(chainId, groupId, blockHash);
    }
}
