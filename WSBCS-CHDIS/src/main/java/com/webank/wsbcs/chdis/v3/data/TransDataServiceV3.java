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
package com.webank.wsbcs.chdis.v3.data;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.protocol.model.JsonTransactionResponse;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosBlock;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosBlock.TransactionResult;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.webank.wsbcs.chdis.base.code.ConstantCode;
import com.webank.wsbcs.chdis.base.entity.BasePageResponse;
import com.webank.wsbcs.chdis.base.entity.data.TransListParam;
import com.webank.wsbcs.chdis.base.entity.trans.TransResultDto;
import com.webank.wsbcs.chdis.base.entity.web3.RspBlockInfo;
import com.webank.wsbcs.chdis.base.entity.web3.RspTransCountInfo;
import com.webank.wsbcs.chdis.base.exception.BaseException;
import com.webank.wsbcs.chdis.base.util.JsonUtils;
import com.webank.wsbcs.chdis.common.dao.entity.TbBlock;
import com.webank.wsbcs.chdis.common.dao.entity.TbTrans;
import com.webank.wsbcs.chdis.common.dao.entity.TbTransExample;
import com.webank.wsbcs.chdis.common.dao.mapper.TbTransMapper;
import com.webank.wsbcs.chdis.v3.web3api.Web3ApiServiceV3;
import lombok.extern.log4j.Log4j2;

/**
 * services for trans data.
 */
@Log4j2
@Service
public class TransDataServiceV3 {

    @Autowired
    private TbTransMapper tbTransMapper;
    @Autowired
    private Web3ApiServiceV3 web3ApiServiceV3;

    /**
     * add trans hash info.
     */
    public void addTransInfo(String chainId, String groupId, TbTrans tbTrans) throws BaseException {
        log.debug("start addTransInfo groupId:{} tbTrans:{}", groupId,
                JsonUtils.toJSONString(tbTrans));
        tbTrans.setChainId(chainId);
        tbTransMapper.insert(tbTrans);
        log.debug("end addTransInfo chainId:{},groupId:{},hash{}", chainId, groupId,
                tbTrans.getTransHash());
    }

    /**
     * query trans list.
     */
    public List<TbTrans> queryTransList(String chainId, String groupId, TransListParam queryParam,
            String bizSeqNo) throws BaseException {
        log.debug("start queryTransList. chainId:{},groupId:{},bizSeqNo:{}", chainId, groupId,
                bizSeqNo);

        TbTransExample example = new TbTransExample();
        example.createCriteria();
        if (queryParam.getTransHash() != null) {
            example.getOredCriteria().get(0).andTransHashEqualTo(queryParam.getTransHash());
        }
        if (queryParam.getBlockNumber() != null) {
            example.getOredCriteria().get(0)
                    .andBlockNumberEqualTo(queryParam.getBlockNumber().longValue());
        }
        example.setCount(queryParam.getPageSize());
        example.setStart(queryParam.getStart());
        List<TbTrans> listOfTran = tbTransMapper.selectByExample(example);

        log.info("end queryTransList. listOfTran size:{} bizSeqNo:{}", listOfTran.size(), bizSeqNo);
        return listOfTran;
    }

    /**
     * query count of trans hash.
     */
    public Integer queryCountOfTran(String chainId, String groupId, TransListParam queryParam,
            String bizSeqNo) throws BaseException {
        log.debug("start queryCountOfTran. chainId:{},groupId:{},queryParam:{}", chainId, groupId,
                JsonUtils.toJSONString(queryParam));

        TbTransExample example = new TbTransExample();
        example.createCriteria();
        if (queryParam.getTransHash() != null) {
            example.getOredCriteria().get(0).andTransHashEqualTo(queryParam.getTransHash());
        }
        if (queryParam.getBlockNumber() != null) {
            example.getOredCriteria().get(0)
                    .andBlockNumberEqualTo(queryParam.getBlockNumber().longValue());
        }
        Integer count = (int) tbTransMapper.countByExample(example);
        log.info("end queryCountOfTran groupId:{} pkHash:{} count:{} bizSeqNo:{}", groupId,
                queryParam.getTransHash(), count, bizSeqNo);
        return count;
    }

    public void removeNew(String chainId, String groupId, Integer subTransNum) {
        log.info("transData removeNew {}|{}|{}", chainId, groupId, subTransNum);
        TransListParam queryParam = new TransListParam();
        queryParam.setStart(0);
        queryParam.setPageSize(subTransNum);
        List<TbTrans> list = queryTransList(chainId, groupId, queryParam, "deleteTrans");
        int cnt = 0;
        for (TbTrans i : list) {
            int result = tbTransMapper.deleteByPrimaryKey(i.getTransHash());
            if (result != 0) {
                cnt = cnt + result;
            }
        }
        log.info("transData removeNew end. {}|{}|{}", chainId, groupId, cnt);
    }

    /**
     * get tbTransInfo from chain
     */
    @SuppressWarnings("rawtypes")
    public BasePageResponse getTransListFromChain(String chainId, String groupId,
            TransListParam queryParam, String bizSeqNo) {
        log.debug("start getTransListFromChain.");
        List<TbTrans> list = new ArrayList<>();
        BasePageResponse pageResponse = new BasePageResponse(ConstantCode.SUCCESS, bizSeqNo);
        BigInteger latestBlockNumber = web3ApiServiceV3.getBlockNumber(groupId, chainId);
        RspTransCountInfo transCountInfo = web3ApiServiceV3.getTransCnt(groupId, chainId);
        outCycle: for (int i = 0; i < queryParam.getPageSize(); i++) {// NOSONAR
            BigInteger blockNumber = latestBlockNumber.subtract(BigInteger.valueOf(i));
            log.info("getTransListFromChain blockNumber:{} bizSeqNo:{}", blockNumber, bizSeqNo);
            if (blockNumber.intValue() >= 0) {
                BcosBlock.Block blockInfo =
                        web3ApiServiceV3.getBlockByNumber(groupId, chainId, blockNumber);
                if (blockInfo == null) {
                    log.info("pullBlockByNumber jump over. not found block.");
                    continue;
                }
                TbBlock tbBlock = BlockServiceV3.chainBlock2TbBlock(blockInfo);
                List<TransactionResult> transList = blockInfo.getTransactions();
                for (TransactionResult t : transList) {
                    if (list.size() >= queryParam.getPageSize()) {
                        break outCycle;
                    }
                    JsonTransactionResponse trans = (JsonTransactionResponse) t;
                    TbTrans tbTrans = new TbTrans(trans.getHash(), trans.getFrom(), trans.getTo(),
                            tbBlock.getBlockNumber(), tbBlock.getBlockTimestamp());
                    tbTrans.setExtraData(trans.getExtraData());
                    list.add(tbTrans);
                }
            }
        }
        log.info("end getTransListFromChain. txSum:{}|{}", transCountInfo.getTxSum(), bizSeqNo);
        pageResponse.setData(list);
        pageResponse.setTotalCount(transCountInfo.getTxSum().intValue());
        return pageResponse;
    }

    /**
     * get tbTransInfo from chain
     */
    public List<TbTrans> getTransFromChain(String chainId, String groupId, String transHash,
            BigInteger blockNumber, String bizSeqNo) {
        log.debug("start getTransFromChain.");
        List<TbTrans> transList = new ArrayList<>();
        // find by transHash
        if (transHash != null) {
            TbTrans tbTrans = getTbTransFromFrontByHash(chainId, groupId, transHash, bizSeqNo);
            if (tbTrans != null) {
                transList.add(tbTrans);
            }
        }
        // find trans by block number
        if (transList.size() == 0 && blockNumber != null) {
            List<JsonTransactionResponse> transInBlock =
                    web3ApiServiceV3.getTransByBlockNumber(groupId, chainId, blockNumber, bizSeqNo);
            if (transInBlock != null && transInBlock.size() != 0) {
                transInBlock.forEach(tran -> {
                    TbTrans tbTrans = new TbTrans(tran.getHash(), tran.getFrom(), tran.getTo(),
                            blockNumber.longValue(), null);
                    transList.add(tbTrans);
                });
            }
        }
        log.debug("end getTransFromChain.");
        return transList;
    }

    /**
     * request front for transaction by hash.
     */
    public TbTrans getTbTransFromFrontByHash(String chainId, String groupId, String transHash,
            String bizSeqNo) throws BaseException {// NOSONAR
        log.debug("start getTransFromFrontByHash. groupId:{}  transaction:{}", groupId, transHash);
        TransactionReceipt trans =
                web3ApiServiceV3.getTransactionReceipt(groupId, chainId, transHash);
        TbTrans tbTrans = null;
        if (trans != null) {
            tbTrans = new TbTrans(transHash, trans.getFrom(), trans.getTo(),
                    trans.getBlockNumber().longValue(), null);
        }
        log.debug("end getTransFromFrontByHash. tbTrans:{}", JsonUtils.toJSONString(tbTrans));
        return tbTrans;
    }

    /**
     * get transaction receipt
     */
    public TransResultDto getTransReceipt(String chainId, String groupId, String transHash) {
        TransactionReceipt receipt =
                web3ApiServiceV3.getTransactionReceipt(groupId, chainId, transHash);
        if (receipt == null) {
            return null;
        }
        BcosBlock.Block blockInfo =
                web3ApiServiceV3.getBlockByNumber(groupId, chainId, receipt.getBlockNumber());
        return new TransResultDto(false, receipt, blockInfo.getTimestamp());
    }


    /**
     * get transaction info
     */
    public RspBlockInfo.JsonTransactionResponse getTransaction(String chainId, String groupId,
            String transHash) {
        JsonTransactionResponse transaction =
                web3ApiServiceV3.getTransactionByHash(groupId, chainId, transHash, true);
        return new RspBlockInfo.JsonTransactionResponse(transaction);
    }

}
