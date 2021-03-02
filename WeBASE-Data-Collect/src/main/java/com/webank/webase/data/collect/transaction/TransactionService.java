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
import com.webank.webase.data.collect.base.properties.ConstantProperties;
import com.webank.webase.data.collect.base.tools.CommonTools;
import com.webank.webase.data.collect.base.tools.JacksonUtils;
import com.webank.webase.data.collect.block.entity.MinMaxBlock;
import com.webank.webase.data.collect.frontinterface.FrontInterfaceService;
import com.webank.webase.data.collect.parser.ParserService;
import com.webank.webase.data.collect.receipt.ReceiptService;
import com.webank.webase.data.collect.receipt.entity.TbReceipt;
import com.webank.webase.data.collect.table.TableService;
import com.webank.webase.data.collect.transaction.entity.TbTransaction;
import com.webank.webase.data.collect.transaction.entity.TransListParam;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import lombok.extern.log4j.Log4j2;
import org.fisco.bcos.web3j.protocol.core.methods.response.Transaction;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

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
    @Autowired
    @Lazy
    private ParserService parserService;
    @Autowired
    private ReceiptService receiptService;
    @Autowired
    private ConstantProperties cProperties;
    @Autowired
    private TableService tableService;

    /**
     * add trans hash info.
     */
    public void addTransInfo(int chainId, int groupId, TbTransaction tbTransaction)
            throws BaseException {
        String tableName = TableName.TRANS.getTableName(chainId, groupId);
        tbTransaction.setRecordPatition(CommonTools.getYearMonthDay(tbTransaction.getBlockTimestamp()));
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
    public List<String> queryUnStatTransHashList(int chainId, int groupId) {
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

    @Async("asyncExecutor")
    public void parserProcess(CountDownLatch latch, int chainId, int groupId) {
        log.info("start parserProcess. chainId:{} groupId:{}", chainId, groupId);
        try {
            // check table
            tableService.newSubTable(chainId, groupId);
            Instant startTimem = Instant.now();
            Long useTimeSum = 0L;
            do {
                List<String> transHashList = queryUnStatTransHashList(chainId, groupId);
                if (CollectionUtils.isEmpty(transHashList)) {
                    return;
                }
                // parser
                transHashList.stream()
                        .forEach(transHash -> parserTransaction(chainId, groupId, transHash));

                // parser useTime
                useTimeSum = Duration.between(startTimem, Instant.now()).toMillis();
            } while (useTimeSum < cProperties.getDataParserTaskFixedDelay());
        } catch (Exception ex) {
            log.error("fail parserProcess chainId:{} groupId:{} ", chainId, groupId, ex);
        } finally {
            if (Objects.nonNull(latch)) {
                latch.countDown();
            }
        }
        log.info("end parserProcess. chainId:{} groupId:{}", chainId, groupId);
    }

    private void parserTransaction(int chainId, int groupId, String transHash) {
        try {
            log.info("parser chainId:{} groupId:{} transHash:{}", chainId, groupId, transHash);
            TbReceipt tbReceipt = receiptService.getTbReceiptByHash(chainId, groupId, transHash);
            if (ObjectUtils.isEmpty(tbReceipt)) {
                return;
            }
            parserService.parserTransaction(chainId, groupId, tbReceipt);
        } catch (Exception ex) {
            log.error("fail parserTransaction chainId:{} groupId:{} ", chainId, groupId, ex);
        }
    }
}
