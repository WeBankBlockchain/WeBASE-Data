/**
 * Copyright 2014-2020 the original author or authors.
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
package com.webank.webase.data.collect.transaction;

import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.enums.TableName;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.base.tools.JacksonUtils;
import com.webank.webase.data.collect.block.entity.MinMaxBlock;
import com.webank.webase.data.collect.frontinterface.FrontInterfaceService;
import com.webank.webase.data.collect.transaction.entity.TbTransaction;
import com.webank.webase.data.collect.transaction.entity.TransListParam;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.fisco.bcos.web3j.protocol.core.methods.response.Transaction;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;

/**
 * services for block data.
 */
@Log4j2
@Service
public class TransactionService {

    @Autowired
    private TransactionMapper transactionMapper;
    @Autowired
    private FrontInterfaceService frontInterface;

    /**
     * add trans hash info.
     */
    public void addTransInfo(int chainId, int groupId, TbTransaction tbTransaction)
            throws BaseException {
        String tableName = TableName.TRANS.getTableName(chainId, groupId);
        transactionMapper.add(tableName, tbTransaction);
    }

    /**
     * query trans list.
     */
    public List<TbTransaction> queryTransList(int chainId, int groupId, TransListParam param)
            throws BaseException {
        String tableName = TableName.TRANS.getTableName(chainId, groupId);
        List<TbTransaction> listOfTran = null;
        try {
            listOfTran = transactionMapper.getList(tableName, param);
        } catch (RuntimeException ex) {
            log.error("fail queryBlockList.", ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
        return listOfTran;
    }

    /**
     * query count of trans hash.
     */
    public Integer queryCountOfTran(int chainId, int groupId, TransListParam queryParam)
            throws BaseException {
        String tableName = TableName.TRANS.getTableName(chainId, groupId);
        try {
            Integer count = transactionMapper.getCount(tableName, queryParam);
            return count;
        } catch (RuntimeException ex) {
            log.error("fail queryCountOfTran.", ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * query count of trans by minus max and min trans_number
     */
    public Integer queryCountOfTranByMinus(int chainId, int groupId) throws BaseException {
        String tableName = TableName.TRANS.getTableName(chainId, groupId);
        try {
            Integer count = transactionMapper.getCountByMinMax(tableName);
            log.info("end queryCountOfTranByMinus. count:{}", count);
            if (count == null) {
                return 0;
            }
            return count;
        } catch (BadSqlGrammarException ex) {
            log.info("restart from queryCountOfTranByMinus to queryCountOfTran: []", ex.getCause());
            TransListParam queryParam = new TransListParam(null, null);
            Integer count = queryCountOfTran(chainId, groupId, queryParam);
            return count;
        } catch (RuntimeException ex) {
            log.error("fail queryCountOfTranByMinus. ", ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * query min and max block number.
     */
    public List<MinMaxBlock> queryMinMaxBlock(int chainId, int groupId) throws BaseException {
        log.debug("start queryMinMaxBlock");
        String tableName = TableName.TRANS.getTableName(chainId, groupId);
        try {
            List<MinMaxBlock> listMinMaxBlock = transactionMapper.queryMinMaxBlock(tableName);
            int listSize = Optional.ofNullable(listMinMaxBlock).map(list -> list.size()).orElse(0);
            log.info("end queryMinMaxBlock listMinMaxBlockSize:{}", listSize);
            return listMinMaxBlock;
        } catch (RuntimeException ex) {
            log.error("fail queryMinMaxBlock", ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * query un statistics transaction hash list.
     */
    public List<String> qureyUnStatTransHashList(int chainId, int groupId) {
        List<String> list = transactionMapper
                .listOfUnStatTransHash(TableName.TRANS.getTableName(chainId, groupId));
        return list;
    }

    /**
     * update trans statistic flag.
     */
    public void updateTransStatFlag(int chainId, int groupId, String transHash) {
        String tableName = TableName.TRANS.getTableName(chainId, groupId);
        transactionMapper.updateTransStatFlag(tableName, transHash);
    }

    /**
     * getTbTransByHash.
     */
    public TbTransaction getTbTransByHash(int chainId, Integer groupId, String transHash) {
        return transactionMapper.getByHash(TableName.TRANS.getTableName(chainId, groupId),
                transHash);
    }

    /**
     * request front for transaction by hash.
     */
    public TbTransaction getTbTransFromFrontByHash(int chainId, Integer groupId, String transHash)
            throws BaseException {
        Transaction trans = frontInterface.getTransaction(chainId, groupId, transHash);
        TbTransaction tbTransaction = null;
        if (trans != null) {
            tbTransaction = new TbTransaction(transHash, trans.getBlockNumber(), null,
                    JacksonUtils.objToString(trans));
        }
        return tbTransaction;
    }

    /**
     * get transaction info
     */
    public Transaction getTransaction(int chainId, int groupId, String transHash) {
        return frontInterface.getTransaction(chainId, groupId, transHash);
    }

    /**
     * get transaction receipt
     */
    public TransactionReceipt getTransReceipt(int chainId, int groupId, String transHash) {
        return frontInterface.getTransReceipt(chainId, groupId, transHash);
    }
}
