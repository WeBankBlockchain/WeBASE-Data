/**
 * Copyright 2014-2021 the original author or authors.
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
package com.webank.wsbcs.chdis.v3.data;

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.utils.Numeric;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosBlock;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosBlock.TransactionResult;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.webank.wsbcs.chdis.base.code.ConstantCode;
import com.webank.wsbcs.chdis.base.entity.BasePageResponse;
import com.webank.wsbcs.chdis.base.entity.data.AppInfo;
import com.webank.wsbcs.chdis.base.entity.data.BlockListParam;
import com.webank.wsbcs.chdis.base.exception.BaseException;
import com.webank.wsbcs.chdis.base.properties.ConstantProperties;
import com.webank.wsbcs.chdis.base.util.CommonUtils;
import com.webank.wsbcs.chdis.base.util.JsonUtils;
import com.webank.wsbcs.chdis.common.chain.ChainService;
import com.webank.wsbcs.chdis.common.config.ConfigService;
import com.webank.wsbcs.chdis.common.dao.entity.TbBac20ReceiptEvent;
import com.webank.wsbcs.chdis.common.dao.entity.TbBac20User;
import com.webank.wsbcs.chdis.common.dao.entity.TbBlock;
import com.webank.wsbcs.chdis.common.dao.entity.TbBlockExample;
import com.webank.wsbcs.chdis.common.dao.entity.TbTrans;
import com.webank.wsbcs.chdis.common.dao.mapper.TbBlockMapper;
import com.webank.wsbcs.chdis.common.data.DataService;
import com.webank.wsbcs.chdis.common.method.MethodService;
import com.webank.wsbcs.chdis.common.redis.RedisService;
import com.webank.wsbcs.chdis.v3.web3api.Web3ApiServiceV3;
import lombok.extern.log4j.Log4j2;

/**
 * services for block data. including pull block from chain and block service
 */
@Log4j2
@Service
public class BlockServiceV3 {

    @Autowired
    private ChainService chainService;
    @Autowired
    private MethodService methodService;
    @Autowired
    private TransDataServiceV3 transDataServiceV3;
    @Autowired
    private DataService dataService;
    @Autowired
    private Web3ApiServiceV3 web3ApiServiceV3;
    @Autowired
    private Bac20EventService bac20EventService;
    @Autowired
    private TbBlockMapper tbBlockMapper;
    @Autowired
    private ConstantProperties cProperties;
    @Autowired
    private RedisService redisService;

    /**
     * get block from chain by groupId ThreadPool configuration in /base/config/BeanConfig
     */
    public void pullBlockByChainIdV3(CountDownLatch latch, String chainId, String groupId,
            String bizSeqNo) {
        // start time to limit exec time
        Instant startTime = Instant.now();
        log.debug("start pullBlockByChainIdV3 chainId:{}|groupId:{} biz:{}", chainId, groupId,
                bizSeqNo);
        try {
            // max block in chain
            BigInteger maxChainBlock = web3ApiServiceV3.getBlockNumber(groupId, chainId);
            // next block
            BigInteger nextBlock = getNextBlockNumber(chainId, groupId);
            log.info("pullBlockByChainIdV3 nextBlock:{} biz:{}", nextBlock, bizSeqNo);

            // pull block
            while (Objects.nonNull(maxChainBlock) && maxChainBlock.compareTo(nextBlock) >= 0) {
                log.debug("continue pull block. maxChainBlock:{} nextBlock:{}", maxChainBlock,
                        nextBlock);
                Thread.sleep(cProperties.getPullBlockSleepTime());
                pullBlockByNumber(chainId, groupId, nextBlock, bizSeqNo);
                nextBlock = getNextBlockNumber(chainId, groupId);
                log.info(
                        "pullBlockByChainIdV3 while chainId:{} groupId:{} maxChainBlock:{} nextBlock:{} biz:{}",
                        chainId, groupId, maxChainBlock, nextBlock, bizSeqNo);

                // reset maxChainBlock
                if (maxChainBlock.compareTo(nextBlock) < 0) {
                    maxChainBlock = web3ApiServiceV3.getBlockNumber(groupId, chainId);
                }
                // 判断单次执行超过了10min，跳出
                long duration = Duration.between(startTime, Instant.now()).toMillis();
                if (duration > cProperties.getPullBlockTimeout()) {
                    log.info(
                            "this round pull block duration is over 10min, stop and wait next task start {}ms",
                            duration);
                    break;
                }
            }
        } catch (Exception ex) {
            log.error("fail pullBlockByChainIdV3. groupId:{}|{},{}", chainId, groupId, bizSeqNo,
                    ex);
        } finally {
            // finish one group, count down
            latch.countDown();
        }
        log.debug("end pullBlockByChainIdV3 usedTime:{} groupId:{}|{}, biz:{}",
                Duration.between(startTime, Instant.now()).toMillis(), chainId, groupId, bizSeqNo);
    }


    /**
     * pull block by number.
     */
    private void pullBlockByNumber(String chainId, String groupId, BigInteger blockNumber,
            String bizSeqNo) {
        // get block by number
        BcosBlock.Block blockInfo =
                web3ApiServiceV3.getBlockByNumber(groupId, chainId, blockNumber);
        if (blockInfo == null) {
            log.info("pullBlockByNumber jump over. not found new block. {}", bizSeqNo);
            return;
        }
        // save block info
        saveBlockInfo(blockInfo, chainId, groupId, bizSeqNo);
    }

    /**
     * get next blockNumber
     */
    private BigInteger getNextBlockNumber(String chainId, String groupId) {
        // get max blockNumber in table
        BigInteger localMaxBlockNumber = getLatestBlockNumber(chainId, groupId);
        if (Objects.nonNull(localMaxBlockNumber)) {
            return localMaxBlockNumber.add(BigInteger.ONE);
        }
        String isResetBlockNumber =
                redisService.getString(ConfigService.KEY_ISRESETBLOCKNUMBER, "getNextBlockNumber");
        if (cProperties.getIsBlockPullFromZero() && StringUtils.isBlank(isResetBlockNumber)) {
            log.info("=== getNextBlockNumber initBlock:{} {}|{}", 0, chainId, groupId);
            return BigInteger.ZERO;
        } else {
            BigInteger initBlock = web3ApiServiceV3.getBlockNumber(groupId, chainId);
            if (StringUtils.isNotBlank(isResetBlockNumber)) {
                Long pullFromBlock = Long.valueOf(redisService
                        .getString(ConfigService.KEY_PULLFROMBLOCK, "getNextBlockNumber"));
                if (initBlock.compareTo(BigInteger.valueOf(pullFromBlock)) > 0) {
                    initBlock = BigInteger.valueOf(pullFromBlock);
                }
                redisService.delString(ConfigService.KEY_ISRESETBLOCKNUMBER);
                redisService.delString(ConfigService.KEY_ISRESETBLOCKNUMBER);
            } else {
                if (initBlock
                        .compareTo(BigInteger.valueOf(cProperties.getPullBlockInitCnts())) > 0) {
                    initBlock = initBlock
                            .subtract(BigInteger.valueOf(cProperties.getPullBlockInitCnts() - 1));
                } else {
                    initBlock = BigInteger.ZERO;
                }
            }
            log.info("=== getNextBlockNumber init groupId:{} initBlock:{}", groupId, initBlock);
            return initBlock;
        }
    }


    /**
     * copy chainBlock properties;
     */
    public static TbBlock chainBlock2TbBlock(BcosBlock.Block blockInfo) {
        if (blockInfo == null) {
            return null;
        }
        BigInteger bigIntegerNumber = BigInteger.valueOf(blockInfo.getNumber());
        LocalDateTime blockTimestamp =
                CommonUtils.timestamp2LocalDateTime(blockInfo.getTimestamp());
        int sealerIndex = blockInfo.getSealer();
        int transSize = blockInfo.getTransactions().size();

        // save block info
        TbBlock tbBlock = new TbBlock(blockInfo.getHash(), bigIntegerNumber, blockTimestamp,
                transSize, sealerIndex);
        return tbBlock;
    }

    /**
     * save report block info
     */
    @Transactional
    @SuppressWarnings("rawtypes")
    private void saveBlockInfo(BcosBlock.Block blockInfo, String chainId, String groupId,
            String bizSeqNo) throws BaseException {
        List<TransactionResult> transList = blockInfo.getTransactions();
        // get block info
        TbBlock tbBlock = chainBlock2TbBlock(blockInfo);
        List<TbBac20ReceiptEvent> bac20SendEvents = new ArrayList<>();
        Date dateNow = new Date();
        for (int i = 0; i < transList.size(); i++) {
            BcosBlock.TransactionObject trans = (BcosBlock.TransactionObject) transList.get(i);
            String transHash = trans.getHash();
            String contractAddress = trans.getTo();
            TbTrans tbTrans = new TbTrans(transHash, trans.getFrom(), trans.getTo(),
                    tbBlock.getBlockNumber(), tbBlock.getBlockTimestamp());
            try {
                // 获取appId appName
                AppInfo appInfo = CommonUtils.getAppInfoV3(trans.getExtraData());
                // 查询回执
                TransactionReceipt receipt =
                        web3ApiServiceV3.getTransactionReceipt(groupId, chainId, transHash);
                long gasUsed = Numeric.decodeQuantity(receipt.getGasUsed()).longValue();
                tbTrans.setGasUsed(gasUsed);
                tbTrans.setStatus(receipt.getStatus());
                tbTrans.setExtraData(trans.getExtraData());
                String appId = appInfo == null ? null : appInfo.getAppId();
                // 确认合约地址（部署合约的交易）
                if (StringUtils.isBlank(contractAddress)
                        || Address.DEFAULT.getValue().equals(contractAddress)
                        || "0x".equals(contractAddress)) {
                    contractAddress = receipt.getContractAddress();
                    // save contractAbi
                    String contractAbi = web3ApiServiceV3.getABI(groupId, chainId, contractAddress);
                    if (StringUtils.isNotBlank(contractAbi)) {
                        methodService.saveMethodFromAbiV3(chainId, groupId, contractAbi, bizSeqNo);
                        chainService.saveTbContractInfo(chainId, contractAddress, contractAbi);
                    }
                }
                // 解析元豆事件
                List<TbBac20ReceiptEvent> eventList = bac20EventService.getBac20SendEvents(receipt,
                        cProperties.getBac20Address(), appId, tbBlock.getBlockNumber(),
                        blockInfo.getTimestamp(), i, bizSeqNo);
                if (eventList.size() > 0) {
                    bac20SendEvents.addAll(eventList);
                }
                // 解析充值提现事件
                if (trans.getTo().equals(cProperties.getRechargeProxyAddress())) {
                    dataService.rechargeWithdraw(receipt, trans.getTo(), groupId, chainId,
                            tbBlock.getBlockNumber(), tbBlock.getBlockTimestamp(), bizSeqNo);
                }
                // save trans
                tbTrans.setStatus(receipt.getStatus());
                tbTrans.setCreateTime(dateNow);
                tbTrans.setModifyTime(dateNow);
                transDataServiceV3.addTransInfo(chainId, groupId, tbTrans);
            } catch (Exception ex) {
                log.error("handle trans in blockServiceV3 error {}|{}|{}", transHash,
                        ex.getMessage(), bizSeqNo);
            }
        }
        try {
            if (bac20SendEvents.size() > 0) {
                // 获取元豆用户
                List<String> userList = bac20EventService.getBac20Users(bac20SendEvents, bizSeqNo);
                List<TbBac20User> bac20UserList = new ArrayList<>();
                userList.forEach(i -> {
                    TbBac20User tbBac20User = new TbBac20User();
                    tbBac20User.setUserAddress(i);
                    tbBac20User.setFirstNumber(tbBlock.getBlockNumber());
                    tbBac20User.setFirstTimestamp(blockInfo.getTimestamp());
                    tbBac20User.setBlockNumber(tbBlock.getBlockNumber());
                    tbBac20User.setBlockTimestamp(blockInfo.getTimestamp());
                    tbBac20User.setCreateTime(dateNow);
                    tbBac20User.setModifyTime(dateNow);
                    bac20UserList.add(tbBac20User);
                });
                // 保存元豆用户
                if (bac20UserList.size() > 0) {
                    dataService.saveUser(bac20UserList);
                }
                // 保存元豆事件
                int count = dataService.saveBac20Events(bac20SendEvents);
                log.info("handleSendEvents batchInsert bizSeqNo:{} count:{}", bizSeqNo, count);
            }
            // 保存区块
            tbBlock.setChainId(chainId);
            tbBlock.setCreateTime(dateNow);
            tbBlock.setModifyTime(dateNow);
            tbBlockMapper.insert(tbBlock);
        } catch (Exception e) {
            log.error("saveBlockInfo error. bizSeqNo:{}", bizSeqNo, e);
            throw new BaseException(ConstantCode.DATABASE_EXCEPTION);
        }
    }

    public BasePageResponse queryBlockListFromChain(String chainId, String groupId,
            BlockListParam queryParam, String bizSeqNo) throws BaseException {
        log.info("start queryBlockListFromChain chainId:{},groupId:{},bizSeqNo:{}", chainId,
                groupId, bizSeqNo);
        BasePageResponse pageResponse = new BasePageResponse(ConstantCode.SUCCESS, bizSeqNo);
        List<TbBlock> listOfBlock = new ArrayList<>();
        BigInteger latestBlockNumber = web3ApiServiceV3.getBlockNumber(groupId, chainId);
        for (int i = 0; i < queryParam.getPageSize(); i++) {
            BigInteger blockNumber = latestBlockNumber
                    .subtract(BigInteger.valueOf(queryParam.getStart()).add(BigInteger.valueOf(i)));
            log.info("queryBlockListFromChain blockNumber:{} bizSeqNo:{}", blockNumber, bizSeqNo);
            if (blockNumber.intValue() >= 0) {
                BcosBlock.Block blockInfo =
                        getBlockFromFrontByNumber(chainId, groupId, blockNumber);
                if (blockInfo == null) {
                    log.info("pullBlockByNumber jump over. not found block.");
                    continue;
                }
                TbBlock tbBlock = chainBlock2TbBlock(blockInfo);
                tbBlock.setChainId(chainId);
                listOfBlock.add(tbBlock);
            }
        }
        // set sealer
        listOfBlock.forEach(block -> checkSealerOfBlock(chainId, groupId, block));

        log.info("end queryBlockListFromChain. latestBlockNumber:{}|{}", latestBlockNumber,
                bizSeqNo);
        pageResponse.setData(listOfBlock);
        pageResponse.setTotalCount(latestBlockNumber.intValue());
        return pageResponse;
    }

    /**
     * todo report获取 query block info list.
     */
    public List<TbBlock> queryBlockList(String chainId, String groupId, BlockListParam queryParam,
            String bizSeqNo) throws BaseException {
        log.info("start queryBlockList chainId:{},groupId:{},queryParam:{}|{}", chainId, groupId,
                JsonUtils.toJSONString(queryParam), bizSeqNo);
        TbBlockExample example = new TbBlockExample();
        example.createCriteria();
        if (queryParam.getBlockHash() != null) {
            example.getOredCriteria().get(0).andBlockHashEqualTo(queryParam.getBlockHash());
        }
        if (queryParam.getBlockNumber() != null) {
            example.getOredCriteria().get(0)
                    .andBlockNumberEqualTo(queryParam.getBlockNumber().longValue());
        }
        if (queryParam.getMinBlockNumber() != null) {
            example.getOredCriteria().get(0)
                    .andBlockNumberGreaterThan(queryParam.getMinBlockNumber().longValue());
        }
        example.setStart(queryParam.getStart());
        example.setCount(queryParam.getPageSize());
        // example.setOrderByClause("id asc");
        List<TbBlock> listOfBlock = tbBlockMapper.selectByExample(example);

        log.info("end queryBlockList listOfBlockSize:{}|{}", listOfBlock.size(), bizSeqNo);
        return listOfBlock;
    }

    /**
     * query count of block.
     */
    public Integer queryCountOfBlock(String chainId, String groupId, BlockListParam queryParam,
            String bizSeqNo) throws BaseException {
        try {
            TbBlockExample example = new TbBlockExample();
            example.createCriteria();
            if (queryParam.getBlockHash() != null) {
                example.getOredCriteria().get(0).andBlockHashEqualTo(queryParam.getBlockHash());
            }
            if (queryParam.getBlockNumber() != null) {
                example.getOredCriteria().get(0)
                        .andBlockNumberEqualTo(queryParam.getBlockNumber().longValue());
            }
            Integer count = (int) tbBlockMapper.countByExample(example);
            log.info("end countOfBlock chainId:{}|{} pkHash:{} count:{} bizSeqNo:{}", chainId,
                    groupId, queryParam.getBlockHash(), count, bizSeqNo);
            return count;
        } catch (RuntimeException ex) {
            log.error("fail countOfBlock chainId:{}|{} pkHash:{} bizSeqNo:{}", chainId, groupId,
                    queryParam.getBlockHash(), bizSeqNo, ex);
            throw new BaseException(ConstantCode.DATABASE_EXCEPTION, bizSeqNo);
        }
    }

    /**
     * remove block info.
     */
    public Integer removeNew(String chainId, String groupId, int subBlockNum) throws BaseException {
        log.info("blockData removeNew {}|{}|{}", chainId, groupId, subBlockNum);
        Integer affectRow = tbBlockMapper.deleteBySubBlockNum(subBlockNum);
        log.info("blockData removeNew end. {}|{}|{}", chainId, groupId, subBlockNum);
        return affectRow;
    }

    /**
     * remove block info.
     */
    public void remove(String chainId, String groupId, int subBlockNum) throws BaseException {
        log.info("blockData remove {}|{}|{}", chainId, groupId, subBlockNum);
        BlockListParam queryParam = new BlockListParam();
        queryParam.setStart(0);
        queryParam.setPageSize(subBlockNum);
        List<TbBlock> list = queryBlockList(chainId, groupId, queryParam, "deleteBlock");
        list.forEach(blocck -> {
            tbBlockMapper.deleteByPrimaryKey(blocck.getBlockNumber());
        });
        log.info("blockData remove end. {}|{}|{}", chainId, groupId, subBlockNum);
    }

    /**
     * get sealer by index.
     */
    public void checkSealerOfBlock(String chainId, String groupId, TbBlock tbBlock) {
        if (StringUtils.isNotBlank(tbBlock.getSealer())) {
            return;
        }

        // get sealer from chain.
        List<String> sealerList = web3ApiServiceV3.getSealerList(groupId, chainId);
        String sealer = "0x0";
        if (sealerList != null && sealerList.size() > 0) {
            if (tbBlock.getSealerIndex() < sealerList.size()) {
                sealer = sealerList.get(tbBlock.getSealerIndex());
            } else {
                sealer = sealerList.get(0);
            }
        }
        tbBlock.setSealer(sealer);
    }

    /**
     * get latest block number
     */
    public BigInteger getLatestBlockNumber(String chainId, String groupId) {
        log.debug("getLatestBlockNumber. {}|{}", chainId, groupId);
        return tbBlockMapper.findLatestBlockNumber(chainId);
    }

    /**
     * get block by blockNumber from front server
     */
    public BcosBlock.Block getBlockFromFrontByNumber(String chainId, String groupId,
            BigInteger blockNumber) {
        return web3ApiServiceV3.getBlockByNumber(groupId, chainId, blockNumber);
    }

    /**
     * get block by hash from front server
     */
    public BcosBlock.Block getBlockFromFrontByHash(String chainId, String groupId, String pkHash) {
        return web3ApiServiceV3.getBlockByHash(groupId, chainId, pkHash);
    }

    /**
     * get block by number from front
     */
    public BcosBlock.Block getBlockHeaderByNumber(String chainId, String groupId,
            BigInteger blockNumber, boolean onlyHeader, boolean onlyTxHash) {
        return web3ApiServiceV3.getBlockByNumber(groupId, chainId, blockNumber, onlyHeader,
                onlyTxHash);
    }

    /**
     * get block header by hash from front
     */
    public BcosBlock.Block getBlockHeaderByHash(String chainId, String groupId, String pkHash,
            boolean onlyHeader, boolean onlyTxHash) {
        return web3ApiServiceV3.getBlockByHash(groupId, chainId, pkHash, onlyHeader, onlyTxHash);
    }

    public BigInteger getBlockNumberToday(String groupId, String chainId, BigInteger blockNumber) {
        BigInteger left = BigInteger.ZERO;
        BigInteger right = blockNumber.subtract(BigInteger.ONE);
        while (left.compareTo(right) <= 0) {
            BigInteger mid = left.add(right.subtract(left).divide(BigInteger.valueOf(2L)));
            BcosBlock.Block blockInfo = web3ApiServiceV3.getBlockByNumber(groupId, chainId, mid);
            if (CommonUtils.isToday(blockInfo.getTimestamp())) {
                return mid;
            } else if (CommonUtils.isBeforeToday(blockInfo.getTimestamp())) {
                left = mid.add(BigInteger.ONE);
            } else {
                right = mid.subtract(BigInteger.ONE);
            }
        }
        return left;
    }
}
