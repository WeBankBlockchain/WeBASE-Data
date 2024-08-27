package com.webank.wsbcs.chdis.common.data;
/**
 * Copyright 2014-2021 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosBlock;
import org.fisco.bcos.sdk.v3.client.protocol.response.SyncStatus.SyncStatusInfo;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.webank.wsbcs.chdis.base.code.ConstantCode;
import com.webank.wsbcs.chdis.base.entity.BasePageResponse;
import com.webank.wsbcs.chdis.base.entity.BaseResponse;
import com.webank.wsbcs.chdis.base.entity.data.RspTransTotal;
import com.webank.wsbcs.chdis.base.entity.data.RspTrendChart;
import com.webank.wsbcs.chdis.base.entity.data.RspTrendChartAll;
import com.webank.wsbcs.chdis.base.entity.data.RspUserFrow;
import com.webank.wsbcs.chdis.base.entity.data.ValueAndType;
import com.webank.wsbcs.chdis.base.enums.CardTypeEnum;
import com.webank.wsbcs.chdis.base.enums.FunctionType;
import com.webank.wsbcs.chdis.base.enums.SearchTypeEnum;
import com.webank.wsbcs.chdis.base.enums.SendTypeEnum;
import com.webank.wsbcs.chdis.base.exception.BaseException;
import com.webank.wsbcs.chdis.base.properties.ConstantProperties;
import com.webank.wsbcs.chdis.base.util.CommonUtils;
import com.webank.wsbcs.chdis.base.util.JsonUtils;
import com.webank.wsbcs.chdis.base.util.logentity.LogEntity;
import com.webank.wsbcs.chdis.base.util.logentity.LogUtils;
import com.webank.wsbcs.chdis.base.util.logentity.MonitorLog;
import com.webank.wsbcs.chdis.common.config.ConfigService;
import com.webank.wsbcs.chdis.common.contract.IBAC20;
import com.webank.wsbcs.chdis.common.contract.RechargeWithdrawContract;
import com.webank.wsbcs.chdis.common.contract.RechargeWithdrawContract.BindCardsWithValuesEventResponse;
import com.webank.wsbcs.chdis.common.contract.RechargeWithdrawContract.BindPhysicalCardsEventResponse;
import com.webank.wsbcs.chdis.common.contract.RechargeWithdrawContract.CancelHistoryRechargeOrderEventResponse;
import com.webank.wsbcs.chdis.common.contract.RechargeWithdrawContract.CancelWithdrawOrderEventResponse;
import com.webank.wsbcs.chdis.common.contract.RechargeWithdrawContract.CreateRechargeOrderEventResponse;
import com.webank.wsbcs.chdis.common.contract.RechargeWithdrawContract.CreateWithdrawOrderEventResponse;
import com.webank.wsbcs.chdis.common.contract.RechargeWithdrawContract.WithdrawPhysicalCardsEventResponse;
import com.webank.wsbcs.chdis.common.contract.RechargeWithdrawContract.WithdrawVCardsEventResponse;
import com.webank.wsbcs.chdis.common.dao.entity.TbBac20ReceiptEvent;
import com.webank.wsbcs.chdis.common.dao.entity.TbBac20ReceiptEventExample;
import com.webank.wsbcs.chdis.common.dao.entity.TbBac20User;
import com.webank.wsbcs.chdis.common.dao.entity.TbBac20UserExample;
import com.webank.wsbcs.chdis.common.dao.entity.TbCardInfo;
import com.webank.wsbcs.chdis.common.dao.entity.TbCardInfoExample;
import com.webank.wsbcs.chdis.common.dao.entity.TbDepositInfo;
import com.webank.wsbcs.chdis.common.dao.entity.TbDepositInfoExample;
import com.webank.wsbcs.chdis.common.dao.entity.TbSearchCache;
import com.webank.wsbcs.chdis.common.dao.entity.TbSearchCacheExample;
import com.webank.wsbcs.chdis.common.dao.entity.TbStatBac20Amount;
import com.webank.wsbcs.chdis.common.dao.entity.TbStatBac20Transfer;
import com.webank.wsbcs.chdis.common.dao.entity.TbStatUser;
import com.webank.wsbcs.chdis.common.dao.entity.TbWithdrawInfo;
import com.webank.wsbcs.chdis.common.dao.entity.TbWithdrawInfoExample;
import com.webank.wsbcs.chdis.common.dao.mapper.TbBac20ReceiptEventMapper;
import com.webank.wsbcs.chdis.common.dao.mapper.TbBac20UserMapper;
import com.webank.wsbcs.chdis.common.dao.mapper.TbBlockMapper;
import com.webank.wsbcs.chdis.common.dao.mapper.TbCardInfoMapper;
import com.webank.wsbcs.chdis.common.dao.mapper.TbContractInfoMapper;
import com.webank.wsbcs.chdis.common.dao.mapper.TbDepositInfoMapper;
import com.webank.wsbcs.chdis.common.dao.mapper.TbSearchCacheMapper;
import com.webank.wsbcs.chdis.common.dao.mapper.TbStatBac20AmountMapper;
import com.webank.wsbcs.chdis.common.dao.mapper.TbStatBac20TransferMapper;
import com.webank.wsbcs.chdis.common.dao.mapper.TbStatUserMapper;
import com.webank.wsbcs.chdis.common.dao.mapper.TbTransDailyMapper;
import com.webank.wsbcs.chdis.common.dao.mapper.TbTransMapper;
import com.webank.wsbcs.chdis.common.dao.mapper.TbWithdrawInfoMapper;
import com.webank.wsbcs.chdis.common.redis.RedisService;
import com.webank.wsbcs.chdis.v3.data.Bac20EventService;
import com.webank.wsbcs.chdis.v3.data.BlockServiceV3;
import com.webank.wsbcs.chdis.v3.trans.TransServiceV3;
import com.webank.wsbcs.chdis.v3.web3api.Web3ApiServiceV3;
import lombok.extern.log4j.Log4j2;

/**
 * services for common data.
 */
@Log4j2
@Service
@SuppressWarnings("all") // SONAR
public class DataService {

    @Lazy
    @Autowired
    private ConfigService configService;
    @Lazy
    @Autowired
    private BlockServiceV3 blockServiceV3;
    @Autowired
    private TransServiceV3 transServiceV3;
    @Autowired
    private Web3ApiServiceV3 web3ApiServiceV3;
    @Autowired
    private ConstantProperties cProperties;
    @Autowired
    private RedisService redisService;

    @Autowired
    private TbBac20ReceiptEventMapper tbBac20ReceiptEventMapper;
    @Autowired
    private TbBac20UserMapper tbBac20UserMapper;
    @Autowired
    private TbBlockMapper tbBlockMapper;
    @Autowired
    private TbSearchCacheMapper tbSearchCacheMapper;
    @Autowired
    private TbStatBac20AmountMapper tbStatBac20AmountMapper;
    @Autowired
    private TbStatBac20TransferMapper tbStatBac20TransferMapper;
    @Autowired
    private TbStatUserMapper tbStatUserMapper;
    @Autowired
    private TbDepositInfoMapper tbDepositInfoMapper;
    @Autowired
    private TbWithdrawInfoMapper tbWithdrawInfoMapper;
    @Autowired
    private TbCardInfoMapper tbCardInfoMapper;
    @Autowired
    private TbTransMapper tbTransMapper;
    @Autowired
    private TbContractInfoMapper tbContractInfoMapper;
    @Autowired
    private TbTransDailyMapper tbTransDailyMapper;

    public static final int LOAD_DIFFERENCE = 10;
    public static final int BLOCK_SUB = 100;
    public static final int BEAN_CARD_PROPORTION = 100;
    public static final String SEARCH_GENERAL = "general";
    public static final int MAMANGER_CACHE_TIME = 300; // 管理员列表缓存更新间隔（s）

    private List<String> mamangerCache = new ArrayList<>();
    private long cacheTime = 0L;

    public TbSearchCache general(String bizSeqNo) {
        Instant startTime = Instant.now();

        // 先查下缓存，有数据且在间隔时间内直接返回
        TbSearchCacheExample cacheExample = new TbSearchCacheExample();
        cacheExample.createCriteria().andSearchValueEqualTo(SEARCH_GENERAL);
        Optional<TbSearchCache> record = tbSearchCacheMapper.getOneByExample(cacheExample);
        if (record.isPresent() && checkGeneralTime(record.get().getModifyTime())) {
            log.info("general. under cacheInterval. bizSeqNo:{}", bizSeqNo);
            TbSearchCache tbSearchCache = record.get();
            tbSearchCache.setTotalCardInTransit(
                    tbSearchCache.getTotalSupplyMerge() / BEAN_CARD_PROPORTION
                            - tbSearchCache.getTotalCardInLibrary());
            return tbSearchCache;
        }

        // 配置信息
        String chainId = cProperties.getDefaultChainId();
        String groupId = chainId.split(ConstantProperties.SPLIT_UNDERLINE)[1];
        // 同步的最新区块
        BigInteger localMaxBlockNumber = blockServiceV3.getLatestBlockNumber(chainId, groupId);
        log.info("[db] getLatestBlockNumber. usedTime:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), bizSeqNo);
        // if (record.isPresent()
        // && localMaxBlockNumber.longValue() == record.get().getBlockNumber()) {
        // log.info("general. blockNumber not change. blockNumber:{} bizSeqNo:{}",
        // localMaxBlockNumber, bizSeqNo);
        // return record.get();
        // }
        // 元豆事件统计
        startTime = Instant.now();
        log.info("historyUnderParam start. bizSeqNo:{}", bizSeqNo);
        TbSearchCache tbSearchCache = historyUnderParam(null, null, bizSeqNo);
        log.info("historyUnderParam end. usedTime:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), bizSeqNo);

        // 充值提现相关
        startTime = Instant.now();
        long totalRechargeStatusZero = tbDepositInfoMapper.totalDepositAmountStatusZero();
        log.info("[db] totalDepositAmountStatusZero. usedTime:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), bizSeqNo);

        startTime = Instant.now();
        long totalRechargeStatusNull = tbDepositInfoMapper.totalDepositAmountStatusNull();
        log.info("[db] totalRechargeStatusNull. usedTime:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), bizSeqNo);
        long totalRecharge = totalRechargeStatusZero + totalRechargeStatusNull;

        startTime = Instant.now();
        long totalWithdraw = tbWithdrawInfoMapper.totalWithdrawAmount();
        log.info("[db] totalWithdrawAmount. usedTime:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), bizSeqNo);

        startTime = Instant.now();
        long totalWithdrawAmountOut = tbWithdrawInfoMapper.totalWithdrawAmountOut();
        log.info("[db] totalWithdrawAmountOut. usedTime:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), bizSeqNo);

        startTime = Instant.now();
        long totalCardInLibrary = tbCardInfoMapper.totalCardInLibrary();
        log.info("[db] totalCardInLibrary. usedTime:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), bizSeqNo);

        // 订单剩余元豆总额 = 充值总额 - 已出库的总和
        long totalSupplyMerge = totalRecharge - totalWithdrawAmountOut;

        tbSearchCache.setSearchValue(SEARCH_GENERAL);
        tbSearchCache.setTotalSupplyMerge(totalSupplyMerge);
        tbSearchCache.setTotalRecharge(totalRecharge);
        tbSearchCache.setTotalWithdraw(totalWithdraw);
        tbSearchCache.setTotalCardInLibrary(totalCardInLibrary);
        tbSearchCache.setTotalSupplyWeworld(tbSearchCache.getTotalSupply() - totalSupplyMerge);
        tbSearchCache.setBlockNumber(localMaxBlockNumber.longValue());
        tbSearchCache.setCreateTime(new Date());
        tbSearchCache.setModifyTime(new Date());

        try {
            startTime = Instant.now();
            tbSearchCacheMapper.insertDuplicateUpdate(tbSearchCache);
            log.info("[db] insertDuplicateUpdate. usedTime:{} bizSeqNo:{}",
                    Duration.between(startTime, Instant.now()).toMillis(), bizSeqNo);
        } catch (Exception e) {
            log.error("{}",
                    MonitorLog.monitorStr("OnError", "[DB]general", bizSeqNo, "",
                            String.valueOf(ConstantCode.DATABASE_EXCEPTION.getCode()),
                            Duration.between(startTime, Instant.now()).toMillis(),
                            JsonUtils.toJSONString(tbSearchCache)),
                    e);
            LogUtils.getMonitorLogger().error(LogEntity.monitorStr("general", "",
                    String.valueOf(ConstantCode.DATABASE_EXCEPTION.getCode()), e.getMessage(),
                    bizSeqNo, Duration.between(startTime, Instant.now()).toMillis()));
            throw new BaseException(ConstantCode.DATABASE_EXCEPTION);
        }
        // 在途未入库
        tbSearchCache.setTotalCardInTransit(
                totalSupplyMerge / BEAN_CARD_PROPORTION - totalCardInLibrary);
        return tbSearchCache;
    }

    public TbSearchCache generalHistory(int type, Boolean resetEnable, String searchValue,
            String bizSeqNo) {
        // 校验参数
        if (!SearchTypeEnum.isBlockOrTime(type)) {
            throw new BaseException(ConstantCode.INVALID_TYPE, bizSeqNo);
        }
        if (StringUtils.isBlank(searchValue) || !CommonUtils.isDigit(searchValue)) {
            throw new BaseException(ConstantCode.INVALID_SEARCHVALUE, bizSeqNo);
        }
        Instant startTime = Instant.now();
        TbSearchCacheExample cacheExample = new TbSearchCacheExample();
        cacheExample.createCriteria().andSearchValueEqualTo(searchValue);
        // 判断是否重置缓存
        if (resetEnable != null && resetEnable) {
            // 删除缓存
            tbSearchCacheMapper.deleteByExample(cacheExample);
        } else {
            // 先查下缓存，有直接返回
            Optional<TbSearchCache> record = tbSearchCacheMapper.getOneByExample(cacheExample);
            if (record.isPresent()) {
                return record.get();
            }
        }

        String chainId = cProperties.getDefaultChainId();
        String groupId = chainId.split(ConstantProperties.SPLIT_UNDERLINE)[1];
        TbSearchCache tbSearchCache = new TbSearchCache();// NOSONAR
        if (SearchTypeEnum.BLOCK.getValue() == type) {
            // 根据区块查询
            long blockNumber = Long.valueOf(searchValue);
            BigInteger localMaxBlockNumber = blockServiceV3.getLatestBlockNumber(chainId, groupId);
            if (localMaxBlockNumber.longValue() < blockNumber) {
                throw new BaseException(ConstantCode.INVALID_BLOCKNUMBER, bizSeqNo);
            }
            tbSearchCache = historyUnderParam(blockNumber, null, bizSeqNo);
        } else {
            // 根据时间戳查询
            long blockTimestamp = Long.valueOf(searchValue);
            if (blockTimestamp > System.currentTimeMillis()) {
                throw new BaseException(ConstantCode.INVALID_TIME, bizSeqNo);
            }
            // 加1秒，前端只是到秒，兼容毫秒
            blockTimestamp = blockTimestamp + 1000;
            tbSearchCache = historyUnderParam(null, blockTimestamp, bizSeqNo);
        }
        // 保存缓存
        tbSearchCache.setSearchValue(searchValue);
        tbSearchCache.setCreateTime(new Date());
        tbSearchCache.setModifyTime(new Date());
        try {
            tbSearchCacheMapper.insert(tbSearchCache);
        } catch (Exception e) {
            log.error("{}",
                    MonitorLog.monitorStr("OnError", "[DB]saveSearchCache", bizSeqNo, "",
                            String.valueOf(ConstantCode.DATABASE_EXCEPTION.getCode()),
                            Duration.between(startTime, Instant.now()).toMillis(),
                            JsonUtils.toJSONString(tbSearchCache)),
                    e);
            LogUtils.getMonitorLogger().error(LogEntity.monitorStr("saveSearchCache", "",
                    String.valueOf(ConstantCode.DATABASE_EXCEPTION.getCode()), e.getMessage(),
                    bizSeqNo, Duration.between(startTime, Instant.now()).toMillis()));
            throw new BaseException(ConstantCode.DATABASE_EXCEPTION);
        }
        return tbSearchCache;
    }

    public BasePageResponse userList(Integer pageNumber, Integer pageSize, String userAddress,
            String bizSeqNo) {
        BasePageResponse pageResponse = new BasePageResponse(ConstantCode.SUCCESS, bizSeqNo);
        Integer start =
                Optional.ofNullable(pageNumber).map(page -> (page - 1) * pageSize).orElse(0);
        TbBac20UserExample example = new TbBac20UserExample();
        example.createCriteria();
        if (StringUtils.isNotBlank(userAddress)) {
            example.getOredCriteria().get(0).andUserAddressEqualTo(userAddress);
        }
        // 排除管理员
        // List<String> managerList = this.managerMerge(bizSeqNo);
        // if (managerList.size() > 0) {
        // example.getOredCriteria().get(0).andUserAddressNotIn(managerList);
        // }
        long totalCount = tbBac20UserMapper.countByExample(example);
        List<TbBac20User> userList = new ArrayList<>();
        if (totalCount > 0) {
            example.setStart(start);
            example.setCount(pageSize);
            userList = tbBac20UserMapper.selectByExample(example);
        }
        pageResponse.setData(userList);
        pageResponse.setTotalCount((int) totalCount);
        return pageResponse;
    }

    public BasePageResponse managerList(Integer pageNumber, Integer pageSize, String userAddress,
            String bizSeqNo) {
        BasePageResponse pageResponse = new BasePageResponse(ConstantCode.SUCCESS, bizSeqNo);
        Integer start =
                Optional.ofNullable(pageNumber).map(page -> (page - 1) * pageSize).orElse(0);
        List<String> managerList = this.managerMerge(bizSeqNo);
        int totalCount = managerList.size();
        if (StringUtils.isNotBlank(userAddress)) {
            if (totalCount > 0 && managerList.contains(userAddress)) {
                managerList = new ArrayList<>();
                managerList.add(userAddress);
            } else {
                managerList = new ArrayList<>();
            }
        } else {
            managerList =
                    managerList.stream().skip(start).limit(pageSize).collect(Collectors.toList());
        }
        pageResponse.setData(managerList);
        pageResponse.setTotalCount(totalCount);
        return pageResponse;
    }

    public BasePageResponse userFlowList(Integer pageNumber, Integer pageSize, String userAddress,
            Integer type, String fromAddress, String toAddress, String dateStart, String dateEnd,
            String bizSeqNo) {
        // 校验参数
        if (type != null && !SendTypeEnum.isInclude(type)) {
            throw new BaseException(ConstantCode.INVALID_TYPE, bizSeqNo);
        }

        // 查询用户记录
        TbBac20ReceiptEventExample example = new TbBac20ReceiptEventExample();
        // 管理员不查operator
        // List<String> managerList = this.managerMerge(bizSeqNo);
        // if (!managerList.contains(userAddress)) {
        // example.or().andOperatorEqualTo(userAddress);
        // }
        example.or().andFromAddressEqualTo(userAddress);
        example.or().andToAddressEqualTo(userAddress);
        // 个人最多查询n条流水
        example.setCount(cProperties.getUserFlowMaxCnt());
        example.setOrderByClause("block_number desc, tx_index desc, event_index desc");
        Instant startTime = Instant.now();
        List<RspUserFrow> userFlowList = tbBac20ReceiptEventMapper.selectFlowByExample(example);
        log.info("select event list. usedTime:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), bizSeqNo);

        // 条件过滤
        if (type != null) {
            // 转入
            if (type == SendTypeEnum.IN.getValue()) {
                userFlowList =
                        userFlowList.stream().filter(u -> u.getToAddress().equals(userAddress))
                                .collect(Collectors.toList());
            } else {// 转出
                userFlowList =
                        userFlowList.stream().filter(u -> !u.getToAddress().equals(userAddress))
                                .collect(Collectors.toList());
            }
        } else if (StringUtils.isNotBlank(fromAddress)) {
            userFlowList = userFlowList.stream().filter(u -> u.getFromAddress().equals(fromAddress))
                    .collect(Collectors.toList());
        } else if (StringUtils.isNotBlank(toAddress)) {
            userFlowList = userFlowList.stream().filter(u -> u.getToAddress().equals(toAddress))
                    .collect(Collectors.toList());
        } else if (StringUtils.isNotBlank(dateStart) || StringUtils.isNotBlank(dateEnd)) {
            if (StringUtils.isNotBlank(dateStart)) {
                long timestemp = CommonUtils.getTimestempFromString(dateStart, bizSeqNo);
                userFlowList = userFlowList.stream().filter(u -> u.getBlockTimestamp() >= timestemp)
                        .collect(Collectors.toList());
            }
            if (StringUtils.isNotBlank(dateEnd)) {
                // 截止日加24小时
                long timestemp =
                        CommonUtils.getTimestempFromString(dateEnd, bizSeqNo) + 1000 * 60 * 60 * 24;
                userFlowList = userFlowList.stream().filter(u -> u.getBlockTimestamp() < timestemp)
                        .collect(Collectors.toList());
            }
        }

        int totalCount = userFlowList.size();
        if (totalCount > 0) {
            Integer start =
                    Optional.ofNullable(pageNumber).map(page -> (page - 1) * pageSize).orElse(0);
            // 排序
            List<RspUserFrow> userFlowListAll = userFlowList;
            // 分页
            userFlowList = userFlowListAll.stream().skip(start).limit(pageSize)
                    .collect(Collectors.toList());
            // 计算当前记录余额
            userFlowList.forEach(i -> {
                if (totalCount >= cProperties.getUserFlowMaxCnt()) {
                    // 实时查询链上余额减去变动额度
                    long chainBalance = this.userBalance(SearchTypeEnum.NOW.getValue(), userAddress,
                            null, bizSeqNo);
                    long balance = this.userChangeBalanceAfterBlock(userAddress, userFlowListAll,
                            i.getBlockNumber(), i.getTxIndex(), i.getEventIndex(), bizSeqNo);
                    i.setBalance(chainBalance - balance);
                } else {
                    // 从头算
                    long balance = this.userBalanceUnderBlock(userAddress, userFlowListAll,
                            i.getBlockNumber(), i.getTxIndex(), i.getEventIndex(), bizSeqNo);
                    i.setBalance(balance);
                }
                // 判断是否是红包存取
                if (cProperties.getRedPacketProxyAddress().equals(i.getFromAddress())
                        || cProperties.getRedPacketProxyAddress().equals(i.getToAddress())) {
                    i.setIfRedPacketTransfer(true);
                } else {
                    // 判断是否是通过代理合约获取的豆
                    if (cProperties.getRechargeProxyAddress().equals(i.getContractAddress())
                            && userAddress.equals(i.getToAddress())) {
                        // 判断是否是退豆
                        if (cProperties.getRechargeProxyAddress().equals(i.getFromAddress())) {
                            // 是否是提现
                            i.setIfWithdraw(true);
                            // 是否已取消订单
                            i.setIfCanceled(true);
                        } else {
                            // 是否是充值
                            i.setIfRecharge(true);
                        }
                    }
                    if (cProperties.getRechargeProxyAddress().equals(i.getToAddress())
                            && userAddress.equals(i.getFromAddress())) {
                        TbWithdrawInfo withdrawInfo =
                                getWithdrawInfo(userAddress, i.getTxHash(), bizSeqNo);
                        // 判断是否是提现
                        if (withdrawInfo != null) {
                            i.setIfWithdraw(true);
                            // 是否已取消订单
                            i.setIfCanceled(withdrawInfo.getCancelStatus());
                        }
                        // 判断提现订单，卡是否锁定中（订单未取消）
                        if (withdrawInfo != null && !withdrawInfo.getWithdrawStatus()
                                && !withdrawInfo.getCancelStatus()) {
                            i.setIfLocking(true);
                        }
                        if (withdrawInfo != null && withdrawInfo.getOutTimestamp() != null) {
                            i.setWithdrawCardTimestamp(withdrawInfo.getOutTimestamp().getTime());
                        }
                    }
                }
            });
        }
        BasePageResponse pageResponse = new BasePageResponse(ConstantCode.SUCCESS, bizSeqNo);
        pageResponse.setData(userFlowList);
        pageResponse.setTotalCount(totalCount);
        return pageResponse;
    }

    public BasePageResponse transFlowList(Integer pageNumber, Integer pageSize, String userAddress,
            String txHash, String fromAddress, String fromAddressNE, String toAddress,
            String transAddress, Long timestemp, Long startTimestemp, Long endTimestemp,
            String bizSeqNo) {
        // 查询用户记录
        TbBac20ReceiptEventExample example = new TbBac20ReceiptEventExample();
        // 管理员不查operator
        // List<String> managerList = this.managerMerge(bizSeqNo);
        // if (!managerList.contains(userAddress)) {
        // example.or().andOperatorEqualTo(userAddress);
        // }
        example.or().andFromAddressEqualTo(userAddress);
        example.or().andToAddressEqualTo(userAddress);
        example.setCount(cProperties.getOrgFlowMaxCnt());
        example.setOrderByClause("block_number desc, tx_index desc, event_index desc");
        Instant startTime = Instant.now();
        List<TbBac20ReceiptEvent> userFlowList = tbBac20ReceiptEventMapper.selectByExample(example);
        log.info("select event list. usedTime:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), bizSeqNo);

        // 条件过滤
        if (StringUtils.isNotBlank(txHash)) {
            userFlowList = userFlowList.stream().filter(u -> u.getTxHash().equals(txHash))
                    .collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(fromAddress)) {
            userFlowList = userFlowList.stream().filter(u -> u.getFromAddress().equals(fromAddress))
                    .collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(fromAddressNE)) {
            userFlowList =
                    userFlowList.stream().filter(u -> !u.getFromAddress().equals(fromAddressNE))
                            .collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(toAddress)) {
            userFlowList = userFlowList.stream().filter(u -> u.getToAddress().equals(toAddress))
                    .collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(transAddress)) {
            userFlowList =
                    userFlowList.stream()
                            .filter(u -> u.getFromAddress().equals(transAddress)
                                    || u.getToAddress().equals(transAddress))
                            .collect(Collectors.toList());
        }
        if (timestemp != null) {
            userFlowList = userFlowList.stream()
                    .filter(u -> CommonUtils.isSameDate(u.getBlockTimestamp(), timestemp))
                    .collect(Collectors.toList());
        }
        if (startTimestemp != null) {
            userFlowList =
                    userFlowList.stream().filter(u -> u.getBlockTimestamp() >= startTimestemp)
                            .collect(Collectors.toList());
        }
        if (endTimestemp != null) {
            userFlowList = userFlowList.stream().filter(u -> u.getBlockTimestamp() <= endTimestemp)
                    .collect(Collectors.toList());
        }

        int totalCount = userFlowList.size();
        if (totalCount > 0) {
            Integer start =
                    Optional.ofNullable(pageNumber).map(page -> (page - 1) * pageSize).orElse(0);
            // 分页
            userFlowList =
                    userFlowList.stream().skip(start).limit(pageSize).collect(Collectors.toList());
        }

        BasePageResponse pageResponse = new BasePageResponse(ConstantCode.SUCCESS, bizSeqNo);
        pageResponse.setData(userFlowList);
        pageResponse.setTotalCount(totalCount);
        return pageResponse;
    }

    public BaseResponse transTotal(String userAddress, String txHash, String fromAddress,
            String fromAddressNE, String toAddress, String transAddress, Long timestemp,
            Long startTimestemp, Long endTimestemp, String bizSeqNo) {
        // 查询用户记录
        TbBac20ReceiptEventExample example = new TbBac20ReceiptEventExample();
        example.or().andFromAddressEqualTo(userAddress);
        example.or().andToAddressEqualTo(userAddress);
        Instant startTime = Instant.now();
        List<TbBac20ReceiptEvent> userFlowList = tbBac20ReceiptEventMapper.selectByExample(example);
        log.info("select event list. usedTime:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), bizSeqNo);

        // 条件过滤
        if (StringUtils.isNotBlank(txHash)) {
            userFlowList = userFlowList.stream().filter(u -> u.getTxHash().equals(txHash))
                    .collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(fromAddress)) {
            userFlowList = userFlowList.stream().filter(u -> u.getFromAddress().equals(fromAddress))
                    .collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(fromAddressNE)) {
            userFlowList =
                    userFlowList.stream().filter(u -> !u.getFromAddress().equals(fromAddressNE))
                            .collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(toAddress)) {
            userFlowList = userFlowList.stream().filter(u -> u.getToAddress().equals(toAddress))
                    .collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(transAddress)) {
            userFlowList =
                    userFlowList.stream()
                            .filter(u -> u.getFromAddress().equals(transAddress)
                                    || u.getToAddress().equals(transAddress))
                            .collect(Collectors.toList());
        }
        if (timestemp != null) {
            userFlowList = userFlowList.stream()
                    .filter(u -> CommonUtils.isSameDate(u.getBlockTimestamp(), timestemp))
                    .collect(Collectors.toList());
        }
        if (startTimestemp != null) {
            userFlowList =
                    userFlowList.stream().filter(u -> u.getBlockTimestamp() >= startTimestemp)
                            .collect(Collectors.toList());
        }
        if (endTimestemp != null) {
            userFlowList = userFlowList.stream().filter(u -> u.getBlockTimestamp() <= endTimestemp)
                    .collect(Collectors.toList());
        }

        int totalCount = userFlowList.size();
        // 充值总量
        long depositSum = 0;
        // 转账总量
        long transferSum = 0;
        // 接收总量
        long receiveSum = 0;
        if (totalCount > 0) {
            for (TbBac20ReceiptEvent e : userFlowList) {
                if (e.getFuncName().equals(FunctionType.issue.getValue())) {
                    depositSum += e.getAmount();
                }
                if (!e.getFuncName().equals(FunctionType.issue.getValue())
                        && e.getFromAddress().equals(userAddress)) {
                    transferSum += e.getAmount();
                }
                if (!e.getFuncName().equals(FunctionType.issue.getValue())
                        && e.getToAddress().equals(userAddress)) {
                    receiveSum += e.getAmount();
                }
            }
        }

        BaseResponse response = new BaseResponse(ConstantCode.SUCCESS, bizSeqNo);
        response.setData(new RspTransTotal(depositSum, transferSum, receiveSum));
        return response;
    }

    public BasePageResponse transFlowByTxHashBatch(List<String> txHashList, String bizSeqNo) {
        BasePageResponse pageResponse = new BasePageResponse(ConstantCode.SUCCESS, bizSeqNo);
        if (txHashList == null || txHashList.size() == 0) {
            return pageResponse;
        }
        TbBac20ReceiptEventExample example = new TbBac20ReceiptEventExample();
        example.createCriteria().andTxHashIn(txHashList);
        Instant startTime = Instant.now();
        List<TbBac20ReceiptEvent> userFlowList = tbBac20ReceiptEventMapper.selectByExample(example);
        log.info("select event list. usedTime:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), bizSeqNo);
        pageResponse.setData(userFlowList);
        pageResponse.setTotalCount(userFlowList.size());
        return pageResponse;
    }

    public BasePageResponse issueAndDestroyFlowList(Integer pageNumber, Integer pageSize,
            String userAddress, String dateStart, String dateEnd, String bizSeqNo) {
        // 查询发放和销毁记录
        TbBac20ReceiptEventExample example = new TbBac20ReceiptEventExample();
        List<String> funcList = new ArrayList<>();
        funcList.add(FunctionType.issue.getValue());
        funcList.add(FunctionType.destroyFrom.getValue());
        example.createCriteria().andOperatorEqualTo(userAddress).andFuncNameIn(funcList);
        if (StringUtils.isNotBlank(dateStart)) {
            long timestemp = CommonUtils.getTimestempFromString(dateStart, bizSeqNo);
            example.getOredCriteria().get(0).andBlockTimestampGreaterThanOrEqualTo(timestemp);
        }
        if (StringUtils.isNotBlank(dateEnd)) {
            // 截止日加24小时
            long timestemp =
                    CommonUtils.getTimestempFromString(dateEnd, bizSeqNo) + 1000 * 60 * 60 * 24;
            example.getOredCriteria().get(0).andBlockTimestampLessThan(timestemp);
        }
        long totalCount = tbBac20ReceiptEventMapper.countByExample(example);
        List<RspUserFrow> userFlowList = new ArrayList<>();
        if (totalCount > 0) {
            Integer start =
                    Optional.ofNullable(pageNumber).map(page -> (page - 1) * pageSize).orElse(0);
            example.setStart(start);
            example.setCount(pageSize);
            example.setOrderByClause("id desc");
            userFlowList = tbBac20ReceiptEventMapper.selectFlowByExample(example);
            // 计算当前记录发放额
            userFlowList.forEach(i -> {
                long balance = this.totalSupplyUnderParam(cProperties.getBac20Address(),
                        userAddress, i.getBlockNumber(), null, bizSeqNo);
                i.setBalance(balance);
            });
        }
        BasePageResponse pageResponse = new BasePageResponse(ConstantCode.SUCCESS, bizSeqNo);
        pageResponse.setData(userFlowList);
        pageResponse.setTotalCount((int) totalCount);
        return pageResponse;
    }

    public long totalSupplyFromSync(String bizSeqNo) {
        return totalSupplyUnderParam(cProperties.getBac20Address(), null, null, null, bizSeqNo);
    }

    public long userBalance(int type, String userAddress, String searchValue, String bizSeqNo) {
        if (!SearchTypeEnum.isInclude(type)) {
            throw new BaseException(ConstantCode.INVALID_TYPE, bizSeqNo);
        }
        if (userAddress.length() != ConfigService.ADDRESS_LENGTH) {
            throw new BaseException(ConstantCode.INVALID_USERADDRESS, bizSeqNo);
        }

        String bac20Address = cProperties.getBac20Address();
        String chainId = cProperties.getDefaultChainId();
        String groupId = chainId.split(ConstantProperties.SPLIT_UNDERLINE)[1];

        long userBalance = 0;
        // 实时查询链上余额
        if (SearchTypeEnum.NOW.getValue() == type) {
            List<String> funcParam = new ArrayList<>();
            funcParam.add(userAddress);
            userBalance = transServiceV3.getBac20Amount(groupId, bac20Address, IBAC20.ABI,
                    IBAC20.FUNC_BALANCEOF, funcParam, chainId, bizSeqNo);
        } else if (SearchTypeEnum.BLOCK.getValue() == type) {
            // 根据区块查询余额
            if (StringUtils.isBlank(searchValue) || !CommonUtils.isDigit(searchValue)) {
                throw new BaseException(ConstantCode.INVALID_SEARCHVALUE, bizSeqNo);
            }
            long blockNumber = Long.valueOf(searchValue);
            BigInteger localMaxBlockNumber = blockServiceV3.getLatestBlockNumber(chainId, groupId);
            if (localMaxBlockNumber.longValue() < blockNumber) {
                throw new BaseException(ConstantCode.INVALID_BLOCKNUMBER, bizSeqNo);
            }
            userBalance = this.userBalanceUnderParam(userAddress, bac20Address, blockNumber, null,
                    bizSeqNo);
        } else if (SearchTypeEnum.TIME.getValue() == type) {
            if (StringUtils.isBlank(searchValue) || !CommonUtils.isDigit(searchValue)) {
                throw new BaseException(ConstantCode.INVALID_SEARCHVALUE, bizSeqNo);
            }
            long blockTimestamp = Long.valueOf(searchValue);
            if (blockTimestamp > System.currentTimeMillis()) {
                throw new BaseException(ConstantCode.INVALID_TIME, bizSeqNo);
            }
            // 加1秒，前端只是到秒，兼容毫秒
            blockTimestamp = blockTimestamp + 1000;
            userBalance = this.userBalanceUnderParam(userAddress, bac20Address, null,
                    blockTimestamp, bizSeqNo);
        }
        return userBalance;
    }

    public RspTrendChartAll trendChartByHour(String date, String bizSeqNo) {
        Instant startTime = Instant.now();
        RspTrendChartAll rspTrendChartAll = new RspTrendChartAll();

        // 0-用户量
        List<RspTrendChart> userList = tbStatUserMapper.selectByCheckDate(date);
        log.info("[db] selectByCheckDate. usedTime:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), bizSeqNo);
        String dateToday = CommonUtils.getTodayString();
        // 0-23小时
        int hours = 23;
        // 今日到当前时间
        if (dateToday.equals(date)) {
            hours = CommonUtils.getHour(new Date());
        }
        if (userList.size() == 0) {
            // 当日没记录，查当日之前最大记录值
            long lastMaxUserAmount = tbStatUserMapper.selectMaxAmountByCheckDate(date);
            for (int i = 0; i <= hours; i++) {
                RspTrendChart record = new RspTrendChart();
                record.setAmount(lastMaxUserAmount);
                record.setDate(date);
                record.setHour(i);
                userList.add(record);
            }
        } else if (userList.size() < 24) {
            // 不够24小时，补全
            if (!dateToday.equals(date) || (dateToday.equals(date) && userList.size() != hours)) {
                List<RspTrendChart> userListTemp = new ArrayList<>();
                userListTemp.addAll(userList);
                userList = new ArrayList<>();
                long amountTemp = tbStatUserMapper.selectMaxAmountByCheckDate(date);
                for (int i = 0; i <= hours; i++) {
                    // 当前没统计，则等同上一次统计数据
                    for (RspTrendChart rspTrendChart : userListTemp) {
                        if (rspTrendChart.getHour() == i) {
                            amountTemp = rspTrendChart.getAmount();
                        }
                    }
                    RspTrendChart record = new RspTrendChart();
                    record.setAmount(amountTemp);
                    record.setDate(date);
                    record.setHour(i);
                    userList.add(record);
                }
            } else {
                log.debug("trendChartByHour pass. bizSeqNo:{}", bizSeqNo);
            }
        }

        // 1-发行量
        startTime = Instant.now();
        List<RspTrendChart> issueList = tbStatBac20AmountMapper.selectIssueAmountByCheckDate(date);
        log.info("[db] selectIssueAmountByCheckDate. usedTime:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), bizSeqNo);

        // 2-官方回收量
        startTime = Instant.now();
        List<RspTrendChart> usedList = tbStatBac20AmountMapper.selectUsedAmountByCheckDate(date);
        log.info("[db] selectUsedAmountByCheckDate. usedTime:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), bizSeqNo);

        if (issueList.size() == 0) {
            // 当日没记录，查当日之前最大记录值
            TbStatBac20Amount lastMaxBlockRecord =
                    tbStatBac20AmountMapper.selectMaxBlockRecordByCheckDate(date);
            long lastMaxIssueAmount = 0;
            long lastMaxUsedAmount = 0;
            if (lastMaxBlockRecord != null) {
                lastMaxIssueAmount = lastMaxBlockRecord.getIssueAmount();
                lastMaxUsedAmount = lastMaxBlockRecord.getUsedAmount();
            }
            for (int i = 0; i <= hours; i++) {
                RspTrendChart record = new RspTrendChart();
                record.setAmount(lastMaxIssueAmount);
                record.setDate(date);
                record.setHour(i);
                issueList.add(record);

                RspTrendChart usedRecord = new RspTrendChart();
                usedRecord.setAmount(lastMaxUsedAmount);
                usedRecord.setDate(date);
                usedRecord.setHour(i);
                usedList.add(usedRecord);
            }
        } else if (issueList.size() < 24) {
            // 不够24小时，补全
            if (!dateToday.equals(date) || (dateToday.equals(date) && issueList.size() != hours)) {
                // issue信息
                List<RspTrendChart> issueListTemp = new ArrayList<>();
                issueListTemp.addAll(issueList);
                issueList = new ArrayList<>();
                // used信息
                List<RspTrendChart> usedListTemp = new ArrayList<>();
                usedListTemp.addAll(usedList);
                usedList = new ArrayList<>();
                TbStatBac20Amount lastMaxBlockRecord =
                        tbStatBac20AmountMapper.selectMaxBlockRecordByCheckDate(date);
                long issueAmountTemp = 0;
                long usedAmountTemp = 0;
                if (lastMaxBlockRecord != null) {
                    issueAmountTemp = lastMaxBlockRecord.getIssueAmount();
                    usedAmountTemp = lastMaxBlockRecord.getUsedAmount();
                }
                for (int i = 0; i <= hours; i++) {
                    // 当前没统计，则等同上一次统计数据
                    // issue信息
                    for (RspTrendChart rspTrendChart : issueListTemp) {
                        if (rspTrendChart.getHour() == i) {
                            issueAmountTemp = rspTrendChart.getAmount();
                        }
                    }
                    RspTrendChart record = new RspTrendChart();
                    record.setAmount(issueAmountTemp);
                    record.setDate(date);
                    record.setHour(i);
                    issueList.add(record);
                    // used信息
                    for (RspTrendChart rspTrendChart : usedListTemp) {
                        if (rspTrendChart.getHour() == i) {
                            usedAmountTemp = rspTrendChart.getAmount();
                        }
                    }
                    RspTrendChart usedRecord = new RspTrendChart();
                    usedRecord.setAmount(usedAmountTemp);
                    usedRecord.setDate(date);
                    usedRecord.setHour(i);
                    usedList.add(usedRecord);
                }
            } else {
                log.debug("trendChartByHour pass. bizSeqNo:{}", bizSeqNo);
            }
        }

        // 转移量
        startTime = Instant.now();
        List<RspTrendChart> transferList =
                tbStatBac20TransferMapper.selectTransferAmountByCheckDate(date);
        log.info("[db] selectTransferAmountByCheckDate. usedTime:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), bizSeqNo);

        if (transferList.size() == 0) {
            for (int i = 0; i <= hours; i++) {
                RspTrendChart record = new RspTrendChart();
                record.setAmount(0L);
                record.setDate(date);
                record.setHour(i);
                transferList.add(record);
            }
        } else if (transferList.size() < 24) {
            // 不够24小时，补全
            if (!dateToday.equals(date)
                    || (dateToday.equals(date) && transferList.size() != hours)) {
                List<RspTrendChart> transferListTemp = new ArrayList<>();
                transferListTemp.addAll(transferList);
                transferList = new ArrayList<>();
                for (int i = 0; i <= hours; i++) {
                    long transferAmountTemp = 0L;
                    for (RspTrendChart rspTrendChart : transferListTemp) {
                        if (rspTrendChart.getHour() == i) {
                            transferAmountTemp = rspTrendChart.getAmount();
                            break;
                        }
                    }
                    RspTrendChart record = new RspTrendChart();
                    record.setAmount(transferAmountTemp);
                    record.setDate(date);
                    record.setHour(i);
                    transferList.add(record);
                }
            } else {
                log.debug("trendChartByHour pass. bizSeqNo:{}", bizSeqNo);
            }
        }

        // 流通量
        List<RspTrendChart> circulateList = new ArrayList<>();
        for (int i = 0; i < issueList.size(); i++) {
            RspTrendChart circulate = new RspTrendChart();
            circulate.setDate(issueList.get(i).getDate());
            circulate.setHour(issueList.get(i).getHour());
            circulate.setAmount(issueList.get(i).getAmount() - usedList.get(i).getAmount());
            circulateList.add(circulate);
        }

        rspTrendChartAll.setUserList(userList);
        rspTrendChartAll.setIssueList(issueList);
        rspTrendChartAll.setUsedList(usedList);
        rspTrendChartAll.setCirculateList(circulateList);
        rspTrendChartAll.setTransferList(transferList);
        return rspTrendChartAll;
    }

    public RspTrendChartAll trendChartByDay(String dateStart, String dateEnd, String bizSeqNo) {
        Instant startTime = Instant.now();
        RspTrendChartAll rspTrendChartAll = new RspTrendChartAll();

        // 0-用户量
        List<RspTrendChart> userList = tbStatUserMapper.selectBetweenCheckDate(dateStart, dateEnd);
        log.info("[db] selectBetweenCheckDate. usedTime:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), bizSeqNo);
        int days = CommonUtils.getDaysBetweenDates(dateStart, dateEnd, bizSeqNo);
        if (userList.size() == 0) {
            long lastMaxUserAmount = tbStatUserMapper.selectMaxAmountByCheckDate(dateStart);
            if (lastMaxUserAmount > 0) {
                String dateTemp = dateStart;
                for (int i = 0; i < days; i++) {
                    RspTrendChart record = new RspTrendChart();
                    record.setAmount(lastMaxUserAmount);
                    record.setDate(dateTemp);
                    userList.add(record);
                    if (dateTemp.equals(dateEnd)) {
                        break;
                    }
                    dateTemp = CommonUtils.getNextDateStr(dateTemp);
                }
            }
        } else if (userList.size() < days) {
            // 不够区间天数，补全
            List<RspTrendChart> userListTemp = new ArrayList<>();
            userListTemp.addAll(userList);
            userList = new ArrayList<>();
            long amountTemp = tbStatUserMapper.selectMaxAmountByCheckDate(dateStart);
            String dateTemp = dateStart;
            for (int i = 0; i <= days; i++) {
                // 当前没统计，则等同上一次统计数据
                for (RspTrendChart rspTrendChart : userListTemp) {
                    if (rspTrendChart.getDate().equals(dateTemp)) {
                        amountTemp = rspTrendChart.getAmount();
                    }
                }
                RspTrendChart record = new RspTrendChart();
                record.setAmount(amountTemp);
                record.setDate(dateTemp);
                userList.add(record);
                if (dateTemp.equals(dateEnd)) {
                    break;
                }
                dateTemp = CommonUtils.getNextDateStr(dateTemp);
            }
        }

        // 1-发行量
        startTime = Instant.now();
        List<RspTrendChart> issueList =
                tbStatBac20AmountMapper.selectIssueAmountBetweenCheckDate(dateStart, dateEnd);
        log.info("[db] selectIssueAmountBetweenCheckDate. usedTime:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), bizSeqNo);

        // 2-官方回收量
        startTime = Instant.now();
        List<RspTrendChart> usedList =
                tbStatBac20AmountMapper.selectUsedAmountBetweenCheckDate(dateStart, dateEnd);
        log.info("[db] selectUsedAmountBetweenCheckDate. usedTime:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), bizSeqNo);

        if (issueList.size() == 0) {
            // 当日没记录，查当日之前最大记录值
            TbStatBac20Amount lastMaxBlockRecord =
                    tbStatBac20AmountMapper.selectMaxBlockRecordByCheckDate(dateStart);
            long lastMaxIssueAmount = 0;
            long lastMaxUsedAmount = 0;
            if (lastMaxBlockRecord != null) {
                lastMaxIssueAmount = lastMaxBlockRecord.getIssueAmount();
                lastMaxUsedAmount = lastMaxBlockRecord.getUsedAmount();
            }
            String dateTemp = dateStart;
            for (int i = 0; i < days; i++) {
                // issue信息
                RspTrendChart record = new RspTrendChart();
                record.setAmount(lastMaxIssueAmount);
                record.setDate(dateTemp);
                issueList.add(record);
                // used信息
                RspTrendChart usedRecord = new RspTrendChart();
                usedRecord.setAmount(lastMaxUsedAmount);
                usedRecord.setDate(dateTemp);
                usedList.add(usedRecord);

                if (dateTemp.equals(dateEnd)) {
                    break;
                }
                dateTemp = CommonUtils.getNextDateStr(dateTemp);
            }
        } else if (issueList.size() < days) {
            // 不够区间天数，补全
            // issue信息
            List<RspTrendChart> issueListTemp = new ArrayList<>();
            issueListTemp.addAll(issueList);
            issueList = new ArrayList<>();
            // used信息
            List<RspTrendChart> usedListTemp = new ArrayList<>();
            usedListTemp.addAll(usedList);
            usedList = new ArrayList<>();
            TbStatBac20Amount lastMaxBlockRecord =
                    tbStatBac20AmountMapper.selectMaxBlockRecordByCheckDate(dateStart);
            long issueAmountTemp = 0;
            long usedAmountTemp = 0;
            if (lastMaxBlockRecord != null) {
                issueAmountTemp = lastMaxBlockRecord.getIssueAmount();
                usedAmountTemp = lastMaxBlockRecord.getUsedAmount();
            }
            String dateTemp = dateStart;
            for (int i = 0; i <= days; i++) {
                // 当前没统计，则等同上一次统计数据
                // issue信息
                for (RspTrendChart rspTrendChart : issueListTemp) {
                    if (rspTrendChart.getDate().equals(dateTemp)) {
                        issueAmountTemp = rspTrendChart.getAmount();
                    }
                }
                RspTrendChart record = new RspTrendChart();
                record.setAmount(issueAmountTemp);
                record.setDate(dateTemp);
                issueList.add(record);
                // used信息
                for (RspTrendChart rspTrendChart : usedListTemp) {
                    if (rspTrendChart.getDate().equals(dateTemp)) {
                        usedAmountTemp = rspTrendChart.getAmount();
                    }
                }
                RspTrendChart usedRecord = new RspTrendChart();
                usedRecord.setAmount(usedAmountTemp);
                usedRecord.setDate(dateTemp);
                usedList.add(usedRecord);

                if (dateTemp.equals(dateEnd)) {
                    break;
                }
                dateTemp = CommonUtils.getNextDateStr(dateTemp);
            }
        }

        // 转移量
        startTime = Instant.now();
        List<RspTrendChart> transferList =
                tbStatBac20TransferMapper.selectTransferAmountBetweenCheckDate(dateStart, dateEnd);
        log.info("[db] selectTransferAmountBetweenCheckDate. usedTime:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), bizSeqNo);

        if (transferList.size() == 0) {
            String dateTemp = dateStart;
            for (int i = 0; i < days; i++) {
                RspTrendChart record = new RspTrendChart();
                record.setAmount(0L);
                record.setDate(dateTemp);
                transferList.add(record);
                if (dateTemp.equals(dateEnd)) {
                    break;
                }
                dateTemp = CommonUtils.getNextDateStr(dateTemp);
            }
        } else if (transferList.size() < days) {
            // 不够区间天数，补全
            List<RspTrendChart> transferListTemp = new ArrayList<>();
            transferListTemp.addAll(transferList);
            transferList = new ArrayList<>();
            String dateTemp = dateStart;
            for (int i = 0; i <= days; i++) {
                long transferAmountTemp = 0L;
                for (RspTrendChart rspTrendChart : transferListTemp) {
                    if (rspTrendChart.getDate().equals(dateTemp)) {
                        transferAmountTemp = rspTrendChart.getAmount();
                        break;
                    }
                }
                RspTrendChart record = new RspTrendChart();
                record.setAmount(transferAmountTemp);
                record.setDate(dateTemp);
                transferList.add(record);

                if (dateTemp.equals(dateEnd)) {
                    break;
                }
                dateTemp = CommonUtils.getNextDateStr(dateTemp);
            }
        }

        // 流通量
        List<RspTrendChart> circulateList = new ArrayList<>();
        for (int i = 0; i < issueList.size(); i++) {
            RspTrendChart circulate = new RspTrendChart();
            circulate.setDate(issueList.get(i).getDate());
            circulate.setHour(issueList.get(i).getHour());
            circulate.setAmount(issueList.get(i).getAmount() - usedList.get(i).getAmount());
            circulateList.add(circulate);
        }

        rspTrendChartAll.setUserList(userList);
        rspTrendChartAll.setIssueList(issueList);
        rspTrendChartAll.setUsedList(usedList);
        rspTrendChartAll.setCirculateList(circulateList);
        rspTrendChartAll.setTransferList(transferList);
        return rspTrendChartAll;
    }

    public boolean checkStatus(String bizSeqNo) {
        try {
            String chainId = cProperties.getDefaultChainId();
            SyncStatusInfo syncStatusInfo = web3ApiServiceV3
                    .getSyncStatus(chainId.split(ConstantProperties.SPLIT_UNDERLINE)[1], chainId);
            BigInteger dbBlockNumber = blockServiceV3.getLatestBlockNumber(chainId,
                    chainId.split(ConstantProperties.SPLIT_UNDERLINE)[1]);
            log.info("syncStatusInfo:{} blockNumber:{} bizSeqNo:{}",
                    JsonUtils.toJSONString(syncStatusInfo), dbBlockNumber, bizSeqNo);
            // 判断节点是否正在同步
            // 链上和db相差指定数量，显示loading
            if (syncStatusInfo == null
                    || syncStatusInfo.getKnownHighestNumber()
                            - syncStatusInfo.getBlockNumber() > LOAD_DIFFERENCE
                    || dbBlockNumber == null || syncStatusInfo.getKnownHighestNumber()
                            - dbBlockNumber.intValue() > BLOCK_SUB) {
                return true;
            }
        } catch (Exception e) {
            throw new BaseException(ConstantCode.REQUEST_CHAIN_EXCEPTION, bizSeqNo);
        }
        return false;
    }

    public BasePageResponse rechargeOrderList(Integer pageNumber, Integer pageSize,
            String userAddress, Boolean status, String bizSeqNo) {
        BasePageResponse pageResponse = new BasePageResponse(ConstantCode.SUCCESS, bizSeqNo);
        Integer start =
                Optional.ofNullable(pageNumber).map(page -> (page - 1) * pageSize).orElse(0);
        TbDepositInfoExample example = new TbDepositInfoExample();
        example.createCriteria();
        if (StringUtils.isNotBlank(userAddress)) {
            example.getOredCriteria().get(0).andDepositAddressEqualTo(userAddress);
        }
        if (status != null) {
            example.getOredCriteria().get(0).andDepositStatusEqualTo(status);
        }
        long totalCount = tbDepositInfoMapper.countByExample(example);
        List<TbDepositInfo> infoList = new ArrayList<>();
        if (totalCount > 0) {
            example.setStart(start);
            example.setCount(pageSize);
            infoList = tbDepositInfoMapper.selectByExampleWithBLOBs(example);
        }
        pageResponse.setData(infoList);
        pageResponse.setTotalCount((int) totalCount);
        return pageResponse;
    }

    public BasePageResponse withdrawOrderList(Integer pageNumber, Integer pageSize,
            String userAddress, Boolean status, String bizSeqNo) {
        BasePageResponse pageResponse = new BasePageResponse(ConstantCode.SUCCESS, bizSeqNo);
        Integer start =
                Optional.ofNullable(pageNumber).map(page -> (page - 1) * pageSize).orElse(0);
        TbWithdrawInfoExample example = new TbWithdrawInfoExample();
        example.createCriteria();
        if (StringUtils.isNotBlank(userAddress)) {
            example.getOredCriteria().get(0).andWithdrawAddressEqualTo(userAddress);
        }
        if (status != null) {
            example.getOredCriteria().get(0).andWithdrawStatusEqualTo(status);
        }
        long totalCount = tbWithdrawInfoMapper.countByExample(example);
        List<TbWithdrawInfo> infoList = new ArrayList<>();
        if (totalCount > 0) {
            example.setStart(start);
            example.setCount(pageSize);
            infoList = tbWithdrawInfoMapper.selectByExampleWithBLOBs(example);
        }
        pageResponse.setData(infoList);
        pageResponse.setTotalCount((int) totalCount);
        return pageResponse;
    }

    public BasePageResponse cardList(Integer pageNumber, Integer pageSize, Boolean status,
            String bizSeqNo) {
        BasePageResponse pageResponse = new BasePageResponse(ConstantCode.SUCCESS, bizSeqNo);
        Integer start =
                Optional.ofNullable(pageNumber).map(page -> (page - 1) * pageSize).orElse(0);
        TbCardInfoExample example = new TbCardInfoExample();
        example.createCriteria();
        if (status != null) {
            example.getOredCriteria().get(0).andWithdrawStatusEqualTo(status);
        }
        long totalCount = tbCardInfoMapper.countByExample(example);
        List<TbCardInfo> infoList = new ArrayList<>();
        if (totalCount > 0) {
            example.setStart(start);
            example.setCount(pageSize);
            infoList = tbCardInfoMapper.selectByExample(example);
        }
        pageResponse.setData(infoList);
        pageResponse.setTotalCount((int) totalCount);
        return pageResponse;
    }

    public void userStatSaveAndUpdate(BcosBlock.Block blockInfo, String bizSeqNo) {
        int count = (int) tbBac20UserMapper.countByExample(new TbBac20UserExample());
        if (count == 0) {
            return;
        }
        int managersCnt =
                tbBac20ReceiptEventMapper.getBac20ManagersCnt(null, blockInfo.getNumber());
        // 解析区块时间获取日期
        Date statDate = new Date(blockInfo.getTimestamp());
        String checkDate = CommonUtils.getDateString(statDate);
        int checkHour = CommonUtils.getHour(statDate);
        // 动态更新用户量
        Date currentDate = new Date();
        TbStatUser record = new TbStatUser();
        record.setCheckDate(checkDate);
        record.setCheckHour(checkHour);
        record.setUserAmount(count - managersCnt);
        record.setBlockNumber(blockInfo.getNumber());
        record.setBlockTimestamp(blockInfo.getTimestamp());
        record.setCreateTime(currentDate);
        record.setModifyTime(currentDate);
        log.info("userStatSaveAndUpdate bizSeqNo:{} record:{}", bizSeqNo,
                JsonUtils.toJSONString(record));
        tbStatUserMapper.insertDuplicateUpdate(record);
    }

    public void dataStat(String bizSeqNo) {
        Instant startTime = Instant.now();
        String bac20Address = cProperties.getBac20Address();

        // 查询之前统计的最新记录
        TbStatBac20Amount lastStatBlock = tbStatBac20AmountMapper.selectMaxBlockNumberRecord();
        log.info("dataStat selectMaxBlockNumberRecord. bizSeqNo:{} lastStatBlock:{}", bizSeqNo,
                JsonUtils.toJSONString(lastStatBlock));

        // 查询上次统计到截止目前解析的流水区块，如果差距大于100则减100（预留100个区块）
        long maxBlockNumber = tbBac20ReceiptEventMapper.getMaxBlockNumber();
        long blockStart = lastStatBlock == null ? 0 : lastStatBlock.getBlockNumber();
        long blockEnd = maxBlockNumber;
        if (maxBlockNumber > 0 && maxBlockNumber - blockStart > BLOCK_SUB) {
            blockEnd = maxBlockNumber - BLOCK_SUB;
            // 单个任务最多取一万个块
            if (blockEnd - blockStart > 10000) {
                blockEnd = blockStart + 10000;
            }
        }
        List<TbBac20ReceiptEvent> eventList = tbBac20ReceiptEventMapper
                .getBac20EventRecordByBlockNumber(bac20Address, blockStart, blockEnd);
        log.info("[DB]getBac20EventRecordByBlockNumber. count:{} bizSeqNo:{} usedTime:{}",
                eventList.size(), bizSeqNo, Duration.between(startTime, Instant.now()).toMillis());
        // 如果查不到，再查一下截止解析的
        if (eventList.size() == 0 && blockEnd != maxBlockNumber) {
            startTime = Instant.now();
            eventList = tbBac20ReceiptEventMapper.getBac20EventRecordByBlockNumber(bac20Address,
                    blockStart, maxBlockNumber);
            log.info("[DB]getBac20EventRecordByBlockNumber again. count:{} bizSeqNo:{} usedTime:{}",
                    eventList.size(), bizSeqNo,
                    Duration.between(startTime, Instant.now()).toMillis());
        }

        List<TbStatBac20Amount> statList = new ArrayList<>();
        List<TbStatUser> userList = new ArrayList<>();
        if (eventList.size() > 0) {
            startTime = Instant.now();
            Date currentDate = new Date();
            // 累加上次统计的数据
            long issueAmount = lastStatBlock == null ? 0 : lastStatBlock.getIssueAmount();
            long usedAmount = lastStatBlock == null ? 0 : lastStatBlock.getUsedAmount();
            String tempDate = "";
            int tempHour = 0;
            List<String> recycleList = this.recycleList(bizSeqNo);
            // 统计发放量和回收量
            for (int i = 0; i < eventList.size(); i++) {
                // 解析区块时间获取日期
                Date statDate = new Date(eventList.get(i).getBlockTimestamp());
                String checkDate = CommonUtils.getDateString(statDate);
                int checkHour = CommonUtils.getHour(statDate);

                if (i == 0) {
                    tempDate = checkDate;
                    tempHour = checkHour;
                }

                // 时间变化（最后一笔另外处理）
                if ((!checkDate.equals(tempDate) || checkHour != tempHour)
                        && i != eventList.size() - 1) {
                    // 元豆数据量
                    TbStatBac20Amount record = new TbStatBac20Amount();
                    record.setCheckDate(tempDate);
                    record.setCheckHour(tempHour);
                    record.setBlockNumber(eventList.get(i - 1).getBlockNumber());
                    record.setBlockTimestamp(eventList.get(i - 1).getBlockTimestamp());
                    record.setIssueAmount(issueAmount);
                    record.setUsedAmount(usedAmount);
                    record.setCreateTime(currentDate);
                    record.setModifyTime(currentDate);
                    statList.add(record);

                    // 用户数据量
                    int count = tbBac20ReceiptEventMapper.getBac20UsersCnt(bac20Address,
                            eventList.get(i - 1).getBlockNumber());
                    int managersCnt = tbBac20ReceiptEventMapper.getBac20ManagersCnt(bac20Address,
                            eventList.get(i - 1).getBlockNumber());
                    TbStatUser userRecord = new TbStatUser();
                    userRecord.setCheckDate(tempDate);
                    userRecord.setCheckHour(tempHour);
                    userRecord.setBlockNumber(eventList.get(i - 1).getBlockNumber());
                    userRecord.setBlockTimestamp(eventList.get(i - 1).getBlockTimestamp());
                    userRecord.setUserAmount(count - managersCnt);
                    userRecord.setCreateTime(currentDate);
                    userRecord.setModifyTime(currentDate);
                    userList.add(userRecord);
                }

                // issue累加
                if (FunctionType.issue.getValue().equals(eventList.get(i).getFuncName())) {
                    issueAmount = issueAmount + eventList.get(i).getAmount();
                    // 给回收账号发放也算回收量
                    if (recycleList.contains(eventList.get(i).getToAddress())) {
                        usedAmount = usedAmount + eventList.get(i).getAmount();
                    }
                }
                // 发放量 = issue - destroy
                if (FunctionType.destroyFrom.getValue().equals(eventList.get(i).getFuncName())) {
                    issueAmount = issueAmount - eventList.get(i).getAmount();
                    // 销毁，回收量减去相关元豆
                    if (recycleList.contains(eventList.get(i).getFromAddress())) {
                        usedAmount = usedAmount - eventList.get(i).getAmount();
                    }
                }
                // 回收量（转给管理员）
                if (eventList.get(i).getFuncName().contains(FunctionType.send.getValue())
                        && !eventList.get(i).getFromAddress()
                                .equals(eventList.get(i).getToAddress())) {
                    // 转入
                    if (recycleList.contains(eventList.get(i).getToAddress())) {
                        usedAmount = usedAmount + eventList.get(i).getAmount();
                    }
                    // 转出
                    if (recycleList.contains(eventList.get(i).getFromAddress())) {
                        usedAmount = usedAmount - eventList.get(i).getAmount();
                    }
                }

                // 最后一笔记录一条
                if (i == eventList.size() - 1) {
                    // 元豆数据量
                    TbStatBac20Amount record = new TbStatBac20Amount();
                    record.setCheckDate(checkDate);
                    record.setCheckHour(checkHour);
                    record.setBlockNumber(eventList.get(i).getBlockNumber());
                    record.setBlockTimestamp(eventList.get(i).getBlockTimestamp());
                    record.setIssueAmount(issueAmount);
                    record.setUsedAmount(usedAmount);
                    record.setCreateTime(currentDate);
                    record.setModifyTime(currentDate);
                    statList.add(record);

                    // 用户数据量
                    int count = tbBac20ReceiptEventMapper.getBac20UsersCnt(bac20Address,
                            eventList.get(i).getBlockNumber());
                    int managersCnt = tbBac20ReceiptEventMapper.getBac20ManagersCnt(bac20Address,
                            eventList.get(i).getBlockNumber());
                    TbStatUser userRecord = new TbStatUser();
                    userRecord.setCheckDate(checkDate);
                    userRecord.setCheckHour(checkHour);
                    userRecord.setBlockNumber(eventList.get(i).getBlockNumber());
                    userRecord.setBlockTimestamp(eventList.get(i).getBlockTimestamp());
                    userRecord.setUserAmount(count - managersCnt);
                    userRecord.setCreateTime(currentDate);
                    userRecord.setModifyTime(currentDate);
                    userList.add(userRecord);
                }

                // 更新标志
                tempDate = checkDate;
                tempHour = checkHour;
            }
            log.info("dataStat statList. bizSeqNo:{} usedTime:{}", bizSeqNo,
                    Duration.between(startTime, Instant.now()).toMillis());
        }
        if (statList.size() > 0) {
            startTime = Instant.now();
            try {
                int count = tbStatBac20AmountMapper.batchInsertDuplicateUpdate(statList);
                int countUser = tbStatUserMapper.batchInsertDuplicateUpdate(userList);;
                log.info("[DB]dataStat. count:{} countUser:{} bizSeqNo:{} usedTime:{}", count,
                        countUser, bizSeqNo, Duration.between(startTime, Instant.now()).toMillis());
            } catch (Exception e) {
                log.error("{}",
                        MonitorLog.monitorStr("OnError", "[DB]dataStat", bizSeqNo, "",
                                String.valueOf(ConstantCode.DATABASE_EXCEPTION.getCode()),
                                Duration.between(startTime, Instant.now()).toMillis(), null),
                        e);
                LogUtils.getMonitorLogger().error(LogEntity.monitorStr("dataStat", "",
                        String.valueOf(ConstantCode.DATABASE_EXCEPTION.getCode()), e.getMessage(),
                        bizSeqNo, Duration.between(startTime, Instant.now()).toMillis()));
            }
        }
    }

    public void transferStat(String bizSeqNo) {
        Instant startTime = Instant.now();
        String bac20Address = cProperties.getBac20Address();

        // 查询之前统计的最新记录
        TbStatBac20Transfer lastStatBlock = tbStatBac20TransferMapper.selectMaxBlockNumberRecord();

        // 查询上次统计到截止目前解析的流水区块，如果差距大于100则减100（预留100个区块）
        long maxBlockNumber = tbBac20ReceiptEventMapper.getMaxBlockNumber();
        log.info(
                "transferStat selectMaxBlockNumberRecord. bizSeqNo:{} lastStatBlock:{} maxBlockNumber:{}",
                bizSeqNo, JsonUtils.toJSONString(lastStatBlock), maxBlockNumber);
        long blockStart = lastStatBlock == null ? 0 : lastStatBlock.getBlockNumber();
        long blockEnd = maxBlockNumber;
        if (maxBlockNumber > 0 && maxBlockNumber - blockStart > BLOCK_SUB) {
            blockEnd = maxBlockNumber - BLOCK_SUB;
            // 单个任务最多取一万个块
            if (blockEnd - blockStart > 10000) {
                blockEnd = blockStart + 10000;
            }
        }
        List<TbBac20ReceiptEvent> eventList = tbBac20ReceiptEventMapper
                .getBac20EventRecordByBlockNumber(bac20Address, blockStart, blockEnd);
        log.info("[DB]getBac20EventRecordByBlockNumber. count:{} bizSeqNo:{} usedTime:{}",
                eventList.size(), bizSeqNo, Duration.between(startTime, Instant.now()).toMillis());
        // 如果查不到，再查一下截止解析的
        if (eventList.size() == 0 && blockEnd != maxBlockNumber) {
            startTime = Instant.now();
            eventList = tbBac20ReceiptEventMapper.getBac20EventRecordByBlockNumber(bac20Address,
                    blockStart, maxBlockNumber);
            log.info("[DB]getBac20EventRecordByBlockNumber again. count:{} bizSeqNo:{} usedTime:{}",
                    eventList.size(), bizSeqNo,
                    Duration.between(startTime, Instant.now()).toMillis());
        }

        List<TbStatBac20Transfer> statList = new ArrayList<>();
        if (eventList.size() > 0) {
            startTime = Instant.now();
            Date currentDate = new Date();
            // 累加上次统计的数据
            long transferAmount = lastStatBlock == null ? 0 : lastStatBlock.getTransferAmount();
            String tempDate = "";
            int tempHour = 0;
            // 统计转移量
            for (int i = 0; i < eventList.size(); i++) {
                TbBac20ReceiptEvent event = eventList.get(i);
                // 解析区块时间获取日期
                Date statDate = new Date(event.getBlockTimestamp());
                String checkDate = CommonUtils.getDateString(statDate);
                int checkHour = CommonUtils.getHour(statDate);

                if (i == 0) {
                    tempDate = checkDate;
                    tempHour = checkHour;
                    // 新任务检查上次任务的日期
                    if (lastStatBlock != null && (!checkDate.equals(lastStatBlock.getCheckDate())
                            || (checkDate.equals(lastStatBlock.getCheckDate())
                                    && checkHour != lastStatBlock.getCheckHour()))) {
                        transferAmount = 0;
                    }
                }

                // 时间变化（最后一笔另外处理）
                if ((!checkDate.equals(tempDate) || checkHour != tempHour)
                        && i != eventList.size() - 1 && transferAmount > 0) {
                    // 元豆数据量
                    TbStatBac20Transfer record = new TbStatBac20Transfer();
                    record.setCheckDate(tempDate);
                    record.setCheckHour(tempHour);
                    record.setBlockNumber(eventList.get(i - 1).getBlockNumber());
                    record.setBlockTimestamp(eventList.get(i - 1).getBlockTimestamp());
                    record.setTransferAmount(transferAmount);
                    record.setCreateTime(currentDate);
                    record.setModifyTime(currentDate);
                    statList.add(record);
                    // 每小时重新计算
                    transferAmount = 0;
                }

                // transfer累加（不包含充值、兑卡）
                if (event.getFuncName().contains(FunctionType.send.getValue())
                        && !cProperties.getRechargeProxyAddress().equals(event.getFromAddress())
                        && !cProperties.getRechargeProxyAddress().equals(event.getToAddress())) {
                    transferAmount = transferAmount + event.getAmount();
                }

                // 最后一笔记录一条
                if (i == eventList.size() - 1) {
                    // 元豆数据量
                    TbStatBac20Transfer record = new TbStatBac20Transfer();
                    record.setCheckDate(checkDate);
                    record.setCheckHour(checkHour);
                    record.setBlockNumber(event.getBlockNumber());
                    record.setBlockTimestamp(event.getBlockTimestamp());
                    record.setTransferAmount(transferAmount);
                    record.setCreateTime(currentDate);
                    record.setModifyTime(currentDate);
                    statList.add(record);
                }

                // 更新标志
                tempDate = checkDate;
                tempHour = checkHour;
            }
            log.info("transferStat statList. bizSeqNo:{} usedTime:{}", bizSeqNo,
                    Duration.between(startTime, Instant.now()).toMillis());
        }
        if (statList.size() > 0) {
            startTime = Instant.now();
            try {
                int count = tbStatBac20TransferMapper.batchInsertDuplicateUpdate(statList);
                log.info("[DB]transferStat. count:{} bizSeqNo:{} usedTime:{}", count, bizSeqNo,
                        Duration.between(startTime, Instant.now()).toMillis());
            } catch (Exception e) {
                log.error("{}",
                        MonitorLog.monitorStr("OnError", "[DB]transferStat", bizSeqNo, "",
                                String.valueOf(ConstantCode.DATABASE_EXCEPTION.getCode()),
                                Duration.between(startTime, Instant.now()).toMillis(), null),
                        e);
                LogUtils.getMonitorLogger().error(LogEntity.monitorStr("transferStat", "",
                        String.valueOf(ConstantCode.DATABASE_EXCEPTION.getCode()), e.getMessage(),
                        bizSeqNo, Duration.between(startTime, Instant.now()).toMillis()));
            }
        }
    }

    @Transactional
    public void truncateTable(String bizSeqNo) {
        Instant startTime = Instant.now();
        try {
            String chainId = cProperties.getDefaultChainId();
            BigInteger latestBlock = web3ApiServiceV3
                    .getBlockNumber(chainId.split(ConstantProperties.SPLIT_UNDERLINE)[1], chainId);
            // 是否在同步数据（判断db和链块高差距）
            BigInteger localMaxBlockNumber = blockServiceV3.getLatestBlockNumber(chainId,
                    chainId.split(ConstantProperties.SPLIT_UNDERLINE)[1]);
            if (latestBlock.subtract(localMaxBlockNumber).compareTo(new BigInteger("50")) > 0) {
                log.warn(
                        "resetDataFromBlockNumber refused. being sync. bizSeqNo:{} dbBlockNumber:{} latestBlock:{}",
                        bizSeqNo, localMaxBlockNumber, latestBlock);
                throw new BaseException(ConstantCode.BLOCK_BEING_SYNC);
            }
            // 是否在重置区块
            String resetBlockNumber =
                    redisService.getString(ConfigService.KEY_RESETBLOCKNUMBER, bizSeqNo);
            if (StringUtils.isNotBlank(resetBlockNumber)) {
                log.warn("resetDataFromBlockNumber. being resetBlockNumber. bizSeqNo:{}", bizSeqNo);
                return;
            }
            // redis加锁，没有锁才区块同步任务才执行
            redisService.setStringIfNotExist(ConfigService.KEY_RESETBLOCKNUMBER, "true", bizSeqNo);
            tbSearchCacheMapper.truncateTable();
            tbStatBac20AmountMapper.truncateTable();
            tbStatBac20TransferMapper.truncateTable();
            tbStatUserMapper.truncateTable();
            tbBac20UserMapper.truncateTable();
            tbBac20ReceiptEventMapper.truncateTable();
            tbBlockMapper.truncateTable();
            tbDepositInfoMapper.truncateTable();
            tbWithdrawInfoMapper.truncateTable();
            tbCardInfoMapper.truncateTable();
            tbTransMapper.truncateTable();
            tbContractInfoMapper.truncateTable();
            tbTransDailyMapper.truncateTable();
            log.info("[DB]truncateTable. bizSeqNo:{} usedTime:{}", bizSeqNo,
                    Duration.between(startTime, Instant.now()).toMillis());
            // 删锁，区块重置后删除
            redisService.delString(ConfigService.KEY_RESETBLOCKNUMBER);
        } catch (BaseException be) {
            throw be;
        } catch (Exception e) {
            log.error("{}",
                    MonitorLog.monitorStr("OnError", "[DB]truncateTable", bizSeqNo, "",
                            String.valueOf(ConstantCode.DATABASE_EXCEPTION.getCode()),
                            Duration.between(startTime, Instant.now()).toMillis(), null),
                    e);
            LogUtils.getMonitorLogger().error(LogEntity.monitorStr("truncateTable", "",
                    String.valueOf(ConstantCode.DATABASE_EXCEPTION.getCode()), e.getMessage(),
                    bizSeqNo, Duration.between(startTime, Instant.now()).toMillis()));
            throw new BaseException(ConstantCode.DATABASE_EXCEPTION);
        }
    }

    @Transactional
    public void resetDataFromBlockNumber(Long blockNumber, String bizSeqNo) {
        String chainId = cProperties.getDefaultChainId();
        BigInteger latestBlock = web3ApiServiceV3
                .getBlockNumber(chainId.split(ConstantProperties.SPLIT_UNDERLINE)[1], chainId);
        if (blockNumber.compareTo(latestBlock.longValue()) >= 0) {
            log.warn("resetDataFromBlockNumber fail. blockNumber:{} latestBlock:{}", bizSeqNo,
                    blockNumber, latestBlock);
            throw new BaseException(ConstantCode.BLOCK_NUMBER_ERROR);
        }
        Instant startTime = Instant.now();
        try {
            // 是否在同步数据（判断db和链块高差距）
            BigInteger localMaxBlockNumber = blockServiceV3.getLatestBlockNumber(chainId,
                    chainId.split(ConstantProperties.SPLIT_UNDERLINE)[1]);
            if (latestBlock.subtract(localMaxBlockNumber).compareTo(new BigInteger("50")) > 0) {
                log.warn(
                        "resetDataFromBlockNumber refused. being sync. bizSeqNo:{} dbBlockNumber:{} latestBlock:{}",
                        bizSeqNo, localMaxBlockNumber, latestBlock);
                throw new BaseException(ConstantCode.BLOCK_BEING_SYNC);
            }
            // 是否在重置区块
            String resetBlockNumber =
                    redisService.getString(ConfigService.KEY_RESETBLOCKNUMBER, bizSeqNo);
            if (StringUtils.isNotBlank(resetBlockNumber)) {
                log.warn("resetDataFromBlockNumber. being resetBlockNumber. bizSeqNo:{}", bizSeqNo);
                return;
            }
            // redis加锁，没有锁才区块同步任务才执行
            redisService.setString(ConfigService.KEY_RESETBLOCKNUMBER, "true", bizSeqNo);
            // 根据区块删除数据
            tbSearchCacheMapper.deleteBiggerThanBlockNumber(blockNumber);
            log.info("[DB]tbSearchCache deleteBiggerThanBlockNumber. bizSeqNo:{} usedTime:{}",
                    bizSeqNo, Duration.between(startTime, Instant.now()).toMillis());

            startTime = Instant.now();
            tbStatBac20AmountMapper.deleteBiggerThanBlockNumber(blockNumber);
            log.info("[DB]tbStatBac20Amount deleteBiggerThanBlockNumber. bizSeqNo:{} usedTime:{}",
                    bizSeqNo, Duration.between(startTime, Instant.now()).toMillis());

            startTime = Instant.now();
            tbStatBac20TransferMapper.deleteBiggerThanBlockNumber(blockNumber);
            log.info("[DB]tbStatBac20Transfer deleteBiggerThanBlockNumber. bizSeqNo:{} usedTime:{}",
                    bizSeqNo, Duration.between(startTime, Instant.now()).toMillis());

            startTime = Instant.now();
            tbStatUserMapper.deleteBiggerThanBlockNumber(blockNumber);
            log.info("[DB]tbStatUser deleteBiggerThanBlockNumber. bizSeqNo:{} usedTime:{}",
                    bizSeqNo, Duration.between(startTime, Instant.now()).toMillis());

            startTime = Instant.now();
            tbBac20UserMapper.deleteBiggerThanBlockNumber(blockNumber);
            log.info("[DB]tbBac20User deleteBiggerThanBlockNumber. bizSeqNo:{} usedTime:{}",
                    bizSeqNo, Duration.between(startTime, Instant.now()).toMillis());

            startTime = Instant.now();
            tbBac20ReceiptEventMapper.deleteBiggerThanBlockNumber(blockNumber);
            log.info("[DB]tbBac20Receipt deleteBiggerThanBlockNumber. bizSeqNo:{} usedTime:{}",
                    bizSeqNo, Duration.between(startTime, Instant.now()).toMillis());

            startTime = Instant.now();
            tbDepositInfoMapper.deleteBiggerThanBlockNumber(blockNumber);
            log.info("[DB]tbDepositInfo deleteBiggerThanBlockNumber. bizSeqNo:{} usedTime:{}",
                    bizSeqNo, Duration.between(startTime, Instant.now()).toMillis());

            startTime = Instant.now();
            tbWithdrawInfoMapper.deleteBiggerThanBlockNumber(blockNumber);
            log.info("[DB]tbWithdrawInfo deleteBiggerThanBlockNumber. bizSeqNo:{} usedTime:{}",
                    bizSeqNo, Duration.between(startTime, Instant.now()).toMillis());

            startTime = Instant.now();
            tbCardInfoMapper.deleteBiggerThanBlockNumber(blockNumber);
            log.info("[DB]tbCardInfo deleteBiggerThanBlockNumber. bizSeqNo:{} usedTime:{}",
                    bizSeqNo, Duration.between(startTime, Instant.now()).toMillis());

            startTime = Instant.now();
            tbTransMapper.deleteBiggerThanBlockNumber(blockNumber);
            log.info("[DB]tbTrans deleteBiggerThanBlockNumber. bizSeqNo:{} usedTime:{}", bizSeqNo,
                    Duration.between(startTime, Instant.now()).toMillis());

            startTime = Instant.now();
            tbTransDailyMapper.deleteBiggerThanBlockNumber(blockNumber);
            log.info("[DB]tbTransDaily deleteBiggerThanBlockNumber. bizSeqNo:{} usedTime:{}",
                    bizSeqNo, Duration.between(startTime, Instant.now()).toMillis());

            // 重置区块
            startTime = Instant.now();
            redisService.setString(ConfigService.KEY_ISRESETBLOCKNUMBER, "true", bizSeqNo);
            redisService.setString(ConfigService.KEY_PULLFROMBLOCK, String.valueOf(blockNumber),
                    bizSeqNo);
            tbBlockMapper.truncateTable();
            log.info("[DB]tbBlock truncateTable. bizSeqNo:{} usedTime:{}", bizSeqNo,
                    Duration.between(startTime, Instant.now()).toMillis());
            // 删锁，区块重置后删除
            redisService.delString(ConfigService.KEY_RESETBLOCKNUMBER);
        } catch (BaseException be) {
            throw be;
        } catch (Exception e) {
            redisService.delString(ConfigService.KEY_RESETBLOCKNUMBER);
            log.error("{}",
                    MonitorLog.monitorStr("OnError", "[DB]resetDataFromBlockNumber", bizSeqNo, "",
                            String.valueOf(ConstantCode.DATABASE_EXCEPTION.getCode()),
                            Duration.between(startTime, Instant.now()).toMillis(), null),
                    e);
            LogUtils.getMonitorLogger().error(LogEntity.monitorStr("resetDataFromBlockNumber", "",
                    String.valueOf(ConstantCode.DATABASE_EXCEPTION.getCode()), e.getMessage(),
                    bizSeqNo, Duration.between(startTime, Instant.now()).toMillis()));
            throw new BaseException(ConstantCode.DATABASE_EXCEPTION);
        }
    }

    public int saveUser(List<TbBac20User> bac20UserList) {
        return tbBac20UserMapper.batchInsertDuplicateUpdate(bac20UserList);
    }

    public int saveBac20Events(List<TbBac20ReceiptEvent> bac20SendEvents) {
        return tbBac20ReceiptEventMapper.batchInsert(bac20SendEvents);
    }

    public void rechargeWithdraw(TransactionReceipt transactionReceipt, String rechargeProxyAddress,
            String groupId, String chainId, long blockNumber, Date blockTimestamp,
            String bizSeqNo) {

        if (!transactionReceipt.isStatusOK()) {
            return;
        }
        log.info("rechargeWithdraw bizSeqNo:{} txHash:{} ", bizSeqNo,
                transactionReceipt.getTransactionHash());
        RechargeWithdrawContract rechargeWithdrawContract =
                cProperties.getRechargeWithdrawContract();
        if (rechargeWithdrawContract == null) {
            Client client = web3ApiServiceV3.getWeb3j(groupId, chainId);
            CryptoKeyPair cryptoKeyPair =
                    client.getCryptoSuite().getKeyPairFactory().generateKeyPair();
            rechargeWithdrawContract =
                    RechargeWithdrawContract.load(rechargeProxyAddress, client, cryptoKeyPair);
            cProperties.setRechargeWithdrawContract(rechargeWithdrawContract);
        }

        Instant startTime = Instant.now();
        Date dateNow = new Date();
        String txHash = transactionReceipt.getTransactionHash();
        try {
            // 创建充值订单
            List<CreateRechargeOrderEventResponse> createRechargeOrderEvents =
                    rechargeWithdrawContract.getCreateRechargeOrderEvents(transactionReceipt);
            if (createRechargeOrderEvents.size() > 0) {
                for (CreateRechargeOrderEventResponse event : createRechargeOrderEvents) {
                    TbDepositInfo record = new TbDepositInfo();
                    record.setDepositOrderBiz(event.orderID);
                    record.setDepositAddress(event.userAddress);
                    record.setDepositAmount(event.amount.intValue());
                    record.setDepositStatus(false);
                    record.setCancelStatus(false);
                    record.setDepositBlockNumber(blockNumber);
                    record.setDepositTimestamp(blockTimestamp);
                    record.setDepositTxHash(txHash);
                    record.setDescription(event.extraData);
                    record.setCardType(CardTypeEnum.Physical.getValue());
                    record.setCreateTime(dateNow);
                    record.setModifyTime(dateNow);
                    log.info("createRechargeOrderEvents {}|{}", bizSeqNo,
                            JsonUtils.toJSONString(record));
                    tbDepositInfoMapper.insert(record);
                }
            }

            // 取消充值订单
            List<CancelHistoryRechargeOrderEventResponse> cancelHistoryRechargeOrderEvent =
                    rechargeWithdrawContract
                            .getCancelHistoryRechargeOrderEvents(transactionReceipt);
            if (cancelHistoryRechargeOrderEvent.size() > 0) {
                for (CancelHistoryRechargeOrderEventResponse event : cancelHistoryRechargeOrderEvent) {
                    TbDepositInfo record = new TbDepositInfo();
                    record.setCancelStatus(true);
                    record.setModifyTime(dateNow);
                    TbDepositInfoExample example = new TbDepositInfoExample();
                    example.createCriteria().andDepositOrderBizEqualTo(event.orderID);
                    log.info("cancelHistoryRechargeOrderEvent {}|{}", bizSeqNo,
                            JsonUtils.toJSONString(record));
                    tbDepositInfoMapper.updateByExampleSelective(record, example);
                }
                return;
            }

            // 充值订单关联卡
            List<BindPhysicalCardsEventResponse> bindPhysicalCardsEvents =
                    rechargeWithdrawContract.getBindPhysicalCardsEvents(transactionReceipt);
            if (bindPhysicalCardsEvents.size() > 0) {
                for (BindPhysicalCardsEventResponse event : bindPhysicalCardsEvents) {
                    String cardStr = JsonUtils.toJSONString(event.physicalCards);
                    List<ValueAndType> physicalCards =
                            JsonUtils.toJavaObjectList(cardStr, ValueAndType.class);

                    List<String> cardList = new ArrayList<>();
                    List<TbCardInfo> cardRecordList = new ArrayList<>();
                    for (ValueAndType valueAndType : physicalCards) {
                        // 获取实际值
                        cardList.add(valueAndType.getValue());

                        TbCardInfo cardRecord = new TbCardInfo();
                        cardRecord.setCardNumber(valueAndType.getValue());
                        cardRecord.setCardAmount(event.amount.intValue() / BEAN_CARD_PROPORTION
                                / physicalCards.size());
                        cardRecord.setDepositOrderBiz(event.orderID);
                        cardRecord.setType(CardTypeEnum.Physical.getValue());
                        cardRecord.setWithdrawStatus(false);
                        cardRecord.setInBlockNumber(blockNumber);
                        cardRecord.setInTimestamp(blockTimestamp);
                        cardRecord.setInTxHash(txHash);
                        cardRecord.setCreateTime(dateNow);
                        cardRecord.setModifyTime(dateNow);
                        log.info("bindPhysicalCardsEvents cardRecord {}|{}", bizSeqNo,
                                JsonUtils.toJSONString(cardRecord));
                        cardRecordList.add(cardRecord);
                    }
                    // 记录卡
                    tbCardInfoMapper.batchInsert(cardRecordList);

                    TbDepositInfo record = new TbDepositInfo();
                    record.setPhysicalCards(JsonUtils.toJSONString(cardList));
                    record.setCardsCnt(cardList.size());
                    record.setDepositStatus(true);
                    record.setInBlockNumber(blockNumber);
                    record.setInTimestamp(blockTimestamp);
                    record.setInTxHash(txHash);
                    record.setModifyTime(dateNow);
                    TbDepositInfoExample example = new TbDepositInfoExample();
                    example.createCriteria().andDepositOrderBizEqualTo(event.orderID);
                    // 更新充值订单状态
                    log.info("bindPhysicalCardsEvents {}|{}", bizSeqNo,
                            JsonUtils.toJSONString(record));
                    tbDepositInfoMapper.updateByExampleSelective(record, example);
                }
                return;
            }

            // 创建提现订单
            List<CreateWithdrawOrderEventResponse> withdrawOrderEvents =
                    rechargeWithdrawContract.getCreateWithdrawOrderEvents(transactionReceipt);
            if (withdrawOrderEvents.size() > 0) {
                for (CreateWithdrawOrderEventResponse event : withdrawOrderEvents) {
                    TbWithdrawInfo record = new TbWithdrawInfo();
                    record.setWithdrawAddress(event.userAddress);
                    record.setWithdrawOrderBiz(event.withdrawID.toString());
                    record.setWithdrawAmount(event.amount.intValue());
                    record.setWithdrawStatus(false);
                    record.setCancelStatus(false);
                    record.setWithdrawBlockNumber(blockNumber);
                    record.setWithdrawTimestamp(blockTimestamp);
                    record.setWithdrawTxHash(txHash);
                    record.setSendTxHash(StringUtils.isBlank(event.txHash) ? txHash : event.txHash);
                    record.setDescription(event.extraData);
                    record.setCardType(CardTypeEnum.Physical.getValue());
                    record.setCreateTime(dateNow);
                    record.setModifyTime(dateNow);
                    log.info("withdrawOrderEvents {}|{}", bizSeqNo, JsonUtils.toJSONString(record));
                    tbWithdrawInfoMapper.insert(record);
                }
                return;
            }

            // 取消提现订单
            List<CancelWithdrawOrderEventResponse> cancelWithdrawOrderEvents =
                    rechargeWithdrawContract.getCancelWithdrawOrderEvents(transactionReceipt);
            if (cancelWithdrawOrderEvents.size() > 0) {
                for (CancelWithdrawOrderEventResponse event : cancelWithdrawOrderEvents) {
                    TbWithdrawInfo record = new TbWithdrawInfo();
                    record.setCancelStatus(true);
                    record.setModifyTime(dateNow);
                    TbWithdrawInfoExample example = new TbWithdrawInfoExample();
                    example.createCriteria().andWithdrawAddressEqualTo(event.userAddress)
                            .andWithdrawOrderBizEqualTo(event.withdrawID.toString());
                    log.info("cancelWithdrawOrderEvents {}|{}", bizSeqNo,
                            JsonUtils.toJSONString(record));
                    tbWithdrawInfoMapper.updateByExampleSelective(record, example);
                }
                return;
            }

            // 提现订单关联卡
            List<WithdrawPhysicalCardsEventResponse> withdrawPhysicalCardsEvents =
                    rechargeWithdrawContract.getWithdrawPhysicalCardsEvents(transactionReceipt);
            if (withdrawPhysicalCardsEvents.size() > 0) {
                for (WithdrawPhysicalCardsEventResponse event : withdrawPhysicalCardsEvents) {
                    String cardStr = JsonUtils.toJSONString(event.physicalCards);
                    List<ValueAndType> physicalCards =
                            JsonUtils.toJavaObjectList(cardStr, ValueAndType.class);
                    List<String> cardList = new ArrayList<>();
                    for (ValueAndType valueAndType : physicalCards) {
                        // 获取实际值
                        cardList.add(valueAndType.getValue());

                        TbCardInfo cardRecord = new TbCardInfo();
                        cardRecord.setWithdrawAddress(event.userAddress);
                        cardRecord.setWithdrawOrderBiz(event.withdrawID.toString());
                        cardRecord.setWithdrawStatus(true);
                        cardRecord.setOutBlockNumber(blockNumber);
                        cardRecord.setOutTimestamp(blockTimestamp);
                        cardRecord.setOutTxHash(txHash);
                        cardRecord.setModifyTime(dateNow);
                        TbCardInfoExample cardExample = new TbCardInfoExample();
                        cardExample.createCriteria().andCardNumberEqualTo(valueAndType.getValue());
                        // 更新卡状态
                        log.info("withdrawPhysicalCardsEvents cardRecord {}|{}", bizSeqNo,
                                JsonUtils.toJSONString(cardRecord));
                        tbCardInfoMapper.updateByExampleSelective(cardRecord, cardExample);
                    }

                    TbWithdrawInfo record = new TbWithdrawInfo();
                    record.setPhysicalCards(JsonUtils.toJSONString(cardList));
                    record.setCardsCnt(cardList.size());
                    record.setWithdrawStatus(true);
                    record.setOutBlockNumber(blockNumber);
                    record.setOutTimestamp(blockTimestamp);
                    record.setOutTxHash(txHash);
                    record.setModifyTime(dateNow);
                    TbWithdrawInfoExample example = new TbWithdrawInfoExample();
                    example.createCriteria().andWithdrawAddressEqualTo(event.userAddress)
                            .andWithdrawOrderBizEqualTo(event.withdrawID.toString());
                    // 更新提现订单状态
                    log.info("withdrawPhysicalCardsEvents {}|{}", bizSeqNo,
                            JsonUtils.toJSONString(record));
                    tbWithdrawInfoMapper.updateByExampleSelective(record, example);
                }
                return;
            }

            // 2024-05-23 V2 虚拟卡
            // 虚拟卡充值并关联卡
            List<BindCardsWithValuesEventResponse> bindCardsWithValuesEvents =
                    rechargeWithdrawContract.getBindCardsWithValuesEvents(transactionReceipt);
            if (bindCardsWithValuesEvents.size() > 0) {
                for (BindCardsWithValuesEventResponse event : bindCardsWithValuesEvents) {
                    String valuesStr = JsonUtils.toJSONString(event.values);
                    List<ValueAndType> values =
                            JsonUtils.toJavaObjectList(valuesStr, ValueAndType.class);
                    String cardsStr = JsonUtils.toJSONString(event.cards);
                    List<ValueAndType> cards =
                            JsonUtils.toJavaObjectList(cardsStr, ValueAndType.class);
                    List<String> cardList = new ArrayList<>();
                    List<TbCardInfo> cardRecordList = new ArrayList<>();
                    for (int i = 0; i < cards.size(); i++) {
                        // 获取实际值
                        cardList.add(cards.get(i).getValue());

                        TbCardInfo cardRecord = new TbCardInfo();
                        cardRecord.setCardNumber(cards.get(i).getValue());
                        cardRecord.setCardAmount(Integer.valueOf(values.get(i).getValue()));
                        cardRecord.setDepositOrderBiz(event.orderID);
                        cardRecord.setType(CardTypeEnum.Virtual.getValue());
                        cardRecord.setWithdrawStatus(false);
                        cardRecord.setInBlockNumber(blockNumber);
                        cardRecord.setInTimestamp(blockTimestamp);
                        cardRecord.setInTxHash(txHash);
                        cardRecord.setCreateTime(dateNow);
                        cardRecord.setModifyTime(dateNow);
                        log.info("bindCardsWithValuesEvents cardRecord {}|{}", bizSeqNo,
                                JsonUtils.toJSONString(cardRecord));
                        cardRecordList.add(cardRecord);
                    }
                    // 记录卡
                    tbCardInfoMapper.batchInsert(cardRecordList);
                    // 充值信息
                    TbDepositInfo record = new TbDepositInfo();
                    record.setDepositOrderBiz(event.orderID);
                    record.setDepositAddress(event.userAddress);
                    record.setDepositAmount(event.amount.intValue());
                    record.setDepositStatus(true);
                    record.setDepositBlockNumber(blockNumber);
                    record.setDepositTimestamp(blockTimestamp);
                    record.setDepositTxHash(txHash);
                    record.setDescription(event.extraData);
                    record.setCardType(CardTypeEnum.Virtual.getValue());
                    record.setPhysicalCards(JsonUtils.toJSONString(cardList));
                    record.setCardsCnt(cardList.size());
                    record.setInBlockNumber(blockNumber);
                    record.setInTimestamp(blockTimestamp);
                    record.setInTxHash(txHash);
                    record.setCreateTime(dateNow);
                    record.setModifyTime(dateNow);
                    log.info("bindCardsWithValuesEvents {}|{}", bizSeqNo,
                            JsonUtils.toJSONString(record));
                    tbDepositInfoMapper.insert(record);
                }
                return;
            }

            // 虚拟卡提现订单并关联卡
            List<WithdrawVCardsEventResponse> withdrawVCardsEventResponse =
                    rechargeWithdrawContract.getWithdrawVCardsEvents(transactionReceipt);
            if (withdrawVCardsEventResponse.size() > 0) {
                for (WithdrawVCardsEventResponse event : withdrawVCardsEventResponse) {
                    String cardsStr = JsonUtils.toJSONString(event.vCards);
                    List<ValueAndType> cards =
                            JsonUtils.toJavaObjectList(cardsStr, ValueAndType.class);
                    List<String> cardList = new ArrayList<>();
                    for (ValueAndType valueAndType : cards) {
                        // 获取实际值
                        cardList.add(valueAndType.getValue());

                        TbCardInfo cardRecord = new TbCardInfo();
                        cardRecord.setWithdrawAddress(event.userAddress);
                        cardRecord.setWithdrawOrderBiz(event.withdrawOrderIndex.toString());
                        cardRecord.setWithdrawStatus(true);
                        cardRecord.setOutBlockNumber(blockNumber);
                        cardRecord.setOutTimestamp(blockTimestamp);
                        cardRecord.setOutTxHash(txHash);
                        cardRecord.setModifyTime(dateNow);
                        TbCardInfoExample cardExample = new TbCardInfoExample();
                        cardExample.createCriteria().andCardNumberEqualTo(valueAndType.getValue());
                        // 更新卡状态
                        log.info("withdrawVCardsEvent cardRecord {}|{}", bizSeqNo,
                                JsonUtils.toJSONString(cardRecord));
                        tbCardInfoMapper.updateByExampleSelective(cardRecord, cardExample);
                    }

                    TbWithdrawInfo record = new TbWithdrawInfo();
                    record.setWithdrawAddress(event.userAddress);
                    record.setWithdrawOrderBiz(event.withdrawOrderIndex.toString());
                    record.setWithdrawAmount(event.amount.intValue());
                    record.setWithdrawBlockNumber(blockNumber);
                    record.setWithdrawTimestamp(blockTimestamp);
                    record.setWithdrawTxHash(txHash);
                    record.setSendTxHash(StringUtils.isBlank(event.txHash) ? txHash : event.txHash);
                    record.setDescription(event.extraData);
                    record.setCardType(CardTypeEnum.Virtual.getValue());
                    record.setPhysicalCards(JsonUtils.toJSONString(cardList));
                    record.setCardsCnt(cardList.size());
                    if (cardList.size() == 0) {
                        record.setCancelStatus(true);
                        record.setWithdrawStatus(false);
                    } else {
                        record.setCancelStatus(false);
                        record.setWithdrawStatus(true);
                    }
                    record.setOutBlockNumber(blockNumber);
                    record.setOutTimestamp(blockTimestamp);
                    record.setOutTxHash(txHash);
                    record.setCreateTime(dateNow);
                    record.setModifyTime(dateNow);
                    log.info("withdrawVCardsEvent {}|{}", bizSeqNo, JsonUtils.toJSONString(record));
                    tbWithdrawInfoMapper.insert(record);
                }
            }
            log.info("rechargeWithdraw. bizSeqNo:{} usedTime:{}", bizSeqNo,
                    Duration.between(startTime, Instant.now()).toMillis());
        } catch (Exception e) {
            log.error("{}",
                    MonitorLog.monitorStr("OnError", "[DB]rechargeWithdraw", bizSeqNo, "",
                            String.valueOf(ConstantCode.DATABASE_EXCEPTION.getCode()),
                            Duration.between(startTime, Instant.now()).toMillis(), null),
                    e);
            LogUtils.getMonitorLogger().error(LogEntity.monitorStr("rechargeWithdraw", "",
                    String.valueOf(ConstantCode.DATABASE_EXCEPTION.getCode()), e.getMessage(),
                    bizSeqNo, Duration.between(startTime, Instant.now()).toMillis()));
            throw new BaseException(ConstantCode.DATABASE_EXCEPTION);
        }
    }

    private boolean checkGeneralTime(Date modifyTime) {
        if (System.currentTimeMillis() - modifyTime.getTime() <= cProperties.getCacheInterval()) {
            return true;
        }
        return false;
    }

    private TbWithdrawInfo getWithdrawInfo(String userAddress, String txHash, String bizSeqNo) {
        TbWithdrawInfoExample example = new TbWithdrawInfoExample();
        example.createCriteria().andWithdrawAddressEqualTo(userAddress)
                .andSendTxHashEqualTo(txHash);
        Optional<TbWithdrawInfo> withdrawInfo = tbWithdrawInfoMapper.getOneByExample(example);
        if (!withdrawInfo.isPresent()) {
            return null;
        }
        log.info("checkWithdrawStatus txHash:{} bizSeqNo:{}", txHash, bizSeqNo);
        return withdrawInfo.get();
    }

    private List<String> managerMerge(String bizSeqNo) {
        Instant startTime = Instant.now();
        // 同步的管理员账户
        List<String> managerList = this.mamangerCache;
        if (managerList.size() == 0 || (System.currentTimeMillis() / 1000 - this.cacheTime)
                % MAMANGER_CACHE_TIME == 0) {
            managerList = tbBac20ReceiptEventMapper.getAllBac20Managers(null);
            managerList = managerList.stream().distinct().collect(Collectors.toList());
            this.cacheTime = System.currentTimeMillis() / 1000;
            this.mamangerCache = managerList;
        }
        // 配置的代理合约地址
        if (!managerList.contains(cProperties.getRechargeProxyAddress())) {
            managerList.add(cProperties.getRechargeProxyAddress());
        }
        // 配置的微众世界管理员账户
        String[] managerConfig =
                cProperties.getWeworldManageAddress().split(ConfigService.ADDRESS_SPLIT);
        for (int i = 0; i < managerConfig.length; i++) {
            String manager = managerConfig[i];
            if (StringUtils.isBlank(manager) || manager.equals("null")) {
                break;
            }
            if (!managerList.contains(manager)) {
                managerList.add(manager);
            }
        }
        log.info("managerMerge. usedTime:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), bizSeqNo);
        return managerList;
    }

    private List<String> recycleList(String bizSeqNo) {
        List<String> recycleList = new ArrayList<>();
        // 配置的回收账户
        String[] recycleConfig =
                cProperties.getBeanSystemRecycleAddress().split(ConfigService.ADDRESS_SPLIT);
        for (int i = 0; i < recycleConfig.length; i++) {
            if (StringUtils.isBlank(recycleConfig[i]) || recycleConfig[i].equals("null")) {
                break;
            }
            recycleList.add(recycleConfig[i]);
        }
        log.debug("recycleList. bizSeqNo:{}", bizSeqNo);
        return recycleList;
    }

    /**
     * 统计截止区块的用户余额
     * 
     * @param userAddress
     * @param bac20Address
     * @param blockNumber
     * @param bizSeqNo
     * @return
     */
    private long userBalanceUnderParam(String userAddress, String bac20Address, Long blockNumber,
            Long blockTimestamp, String bizSeqNo) {
        Instant startTime = Instant.now();
        List<TbBac20ReceiptEvent> eventList = tbBac20ReceiptEventMapper
                .getBac20UserEventRecord(bac20Address, userAddress, blockNumber, blockTimestamp);
        log.info("[db] getBac20UserEventRecord. usedTime:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), bizSeqNo);
        return userBalanceFromEvent(userAddress, eventList, bizSeqNo);
    }

    /**
     * 统计用户余额变动数量（链余额倒推）
     * 
     * @return
     */
    private long userChangeBalanceAfterBlock(String userAddress, List<RspUserFrow> eventList,
            Long blockNumber, int txIndex, int eventIndex, String bizSeqNo) {
        Instant startTime = Instant.now();
        long userBalance = 0;
        for (int i = 0; i < eventList.size(); i++) {
            RspUserFrow event = eventList.get(i);
            // 优化一个区块内多笔流水
            if (event.getBlockNumber().longValue() > blockNumber.longValue()
                    || (event.getBlockNumber().longValue() == blockNumber.longValue()
                            && (event.getTxIndex() > txIndex || (event.getTxIndex() == txIndex
                                    && event.getEventIndex() > eventIndex)))) {
                if (userAddress.equals(event.getToAddress())) {
                    // 给自己转不加，其他获得加
                    if (!event.getFromAddress().equals(event.getToAddress())) {
                        userBalance = userBalance + event.getAmount();
                    }
                } else {
                    // 不是获得就减
                    userBalance = userBalance - event.getAmount();
                }
            }
        }
        log.info("userBalanceUnderBlock. usedTime:{} blockNumber:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), blockNumber, bizSeqNo);
        return userBalance;
    }

    /**
     * 统计截止区块的用户余额
     * 
     * @param userAddress
     * @param bac20Address
     * @param blockNumber
     * @param bizSeqNo
     * @return
     */
    private long userBalanceUnderBlock(String userAddress, List<RspUserFrow> eventList,
            Long blockNumber, int txIndex, int eventIndex, String bizSeqNo) {
        Instant startTime = Instant.now();
        long userBalance = 0;
        for (RspUserFrow event : eventList) {
            // 优化一个区块内多笔流水
            if (event.getBlockNumber().longValue() < blockNumber.longValue()
                    || (event.getBlockNumber().longValue() == blockNumber.longValue()
                            && (event.getTxIndex() < txIndex || (event.getTxIndex() == txIndex
                                    && event.getEventIndex() <= eventIndex)))) {
                if (userAddress.equals(event.getToAddress())) {
                    // 给自己转不加，其他获得加
                    if (!event.getFromAddress().equals(event.getToAddress())) {
                        userBalance = userBalance + event.getAmount();
                    }
                } else {
                    // 不是获得就减
                    userBalance = userBalance - event.getAmount();
                }
            }
        }
        log.info("userBalanceUnderBlock. usedTime:{} blockNumber:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), blockNumber, bizSeqNo);
        return userBalance;
    }


    /**
     * 统计截止区块的历史数据量
     * 
     * @param config
     * @param blockNumber
     * @param bizSeqNo
     * @return
     */
    private TbSearchCache historyUnderParam(Long blockNumber, Long blockTimestamp,
            String bizSeqNo) {
        Instant startTime = Instant.now();
        String bac20Address = cProperties.getBac20Address();

        // 财政账户余额之和（回收量）
        long totalRecycle = 0;
        List<String> recycleList = this.recycleList(bizSeqNo);
        for (int i = 0; i < recycleList.size(); i++) {
            List<TbBac20ReceiptEvent> eventList = tbBac20ReceiptEventMapper.getBac20UserEventRecord(
                    bac20Address, recycleList.get(i), blockNumber, blockTimestamp);
            totalRecycle =
                    totalRecycle + userBalanceFromEvent(recycleList.get(i), eventList, bizSeqNo);
        }
        log.info("[db] usedTime:{} totalRecycle:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), totalRecycle, bizSeqNo);

        // 僵尸账户余额之和
        long totalZombie = 0;
        startTime = Instant.now();
        String[] zombieAddressList =
                cProperties.getZombieAddress().split(ConfigService.ADDRESS_SPLIT);
        for (int i = 0; i < zombieAddressList.length; i++) {
            if (StringUtils.isBlank(zombieAddressList[i]) || zombieAddressList[i].equals("null")) {
                break;
            }
            List<TbBac20ReceiptEvent> eventList = tbBac20ReceiptEventMapper.getBac20UserEventRecord(
                    bac20Address, zombieAddressList[i], blockNumber, blockTimestamp);
            totalZombie =
                    totalZombie + userBalanceFromEvent(zombieAddressList[i], eventList, bizSeqNo);
        }
        log.info("[db] usedTime:{} totalZombie:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), totalRecycle, bizSeqNo);

        // 微众世界余额
        startTime = Instant.now();
        long totalBalanceWeworld = 0;
        String[] managerConfig =
                cProperties.getWeworldManageAddress().split(ConfigService.ADDRESS_SPLIT);
        for (int i = 0; i < managerConfig.length; i++) {
            List<TbBac20ReceiptEvent> eventList = tbBac20ReceiptEventMapper.getBac20UserEventRecord(
                    bac20Address, managerConfig[i], blockNumber, blockTimestamp);
            totalBalanceWeworld = totalBalanceWeworld
                    + userBalanceFromEvent(managerConfig[i], eventList, bizSeqNo);
        }
        log.info("[db] usedTime:{} totalBalanceWeworld:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), totalRecycle, bizSeqNo);

        // 总发行量
        long totalSupply =
                totalSupplyUnderParam(bac20Address, null, blockNumber, blockTimestamp, bizSeqNo);

        TbSearchCache result = new TbSearchCache();
        result.setTotalSupply(totalSupply);
        result.setTotalBalanceWeworld(totalBalanceWeworld);
        result.setTotalUsed(totalRecycle);
        result.setTotalZombie(totalZombie);
        result.setTotalCirculate(totalSupply - totalRecycle);
        return result;
    }

    /**
     * 统计截止区块的发放量
     * 
     * @param userAddress
     * @param bac20Address
     * @param blockNumber
     * @param bizSeqNo
     * @return
     */
    private long totalSupplyUnderParam(String bac20Address, String operator, Long blockNumber,
            Long blockTimestamp, String bizSeqNo) {
        Instant startTime = Instant.now();
        // db发放流水之和
        long totalIssue = tbBac20ReceiptEventMapper.getBac20IssueRecordAmount(bac20Address,
                operator, blockNumber, blockTimestamp);
        log.info("[db] getBac20IssueRecordAmount. usedTime:{} totalIssue:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), totalIssue, bizSeqNo);
        // db销毁流水之和
        startTime = Instant.now();
        long totalDestroy = tbBac20ReceiptEventMapper.getBac20DestroyRecordAmount(bac20Address,
                operator, blockNumber, blockTimestamp);
        log.info("[db] getBac20DestroyRecordAmount. usedTime:{} totalDestroy:{} bizSeqNo:{}",
                Duration.between(startTime, Instant.now()).toMillis(), totalDestroy, bizSeqNo);
        // db发放流水之和 - db销毁流水之和
        return totalIssue - totalDestroy;
    }

    /**
     * 根据用户流水统计余额
     * 
     * @param userAddress
     * @param eventList
     * @param bizSeqNo
     * @return
     */
    private static long userBalanceFromEvent(String userAddress,
            List<TbBac20ReceiptEvent> eventList, String bizSeqNo) {
        long userBalance = 0;
        for (TbBac20ReceiptEvent event : eventList) {
            if (userAddress.equals(event.getToAddress())) {
                // 给自己转不加，其他获得加
                if (!event.getFromAddress().equals(event.getToAddress())) {
                    userBalance = userBalance + event.getAmount();
                }
            } else {
                // 不是获得就减
                userBalance = userBalance - event.getAmount();
            }
        }
        log.debug("userBalanceFromEvent userBalance:{} bizSeqNo:{}", userBalance, bizSeqNo);
        return userBalance;
    }
}
