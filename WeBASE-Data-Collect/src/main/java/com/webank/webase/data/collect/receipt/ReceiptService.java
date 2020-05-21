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
package com.webank.webase.data.collect.receipt;

import com.alibaba.fastjson.JSON;
import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.enums.TableName;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.frontinterface.FrontInterfaceService;
import com.webank.webase.data.collect.receipt.entity.TbReceipt;
import com.webank.webase.data.collect.receipt.entity.TransReceipt;
import com.webank.webase.data.collect.transaction.entity.TransListParam;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * services for receipt.
 */
@Log4j2
@Service
public class ReceiptService {

    @Autowired
    private ReceiptMapper receiptMapper;
    @Autowired
    private FrontInterfaceService frontInterface;

    /**
     * add receipt info.
     */
    public void handleReceiptInfo(int groupId, String transHash) throws BaseException {
        TransReceipt transReceipt = frontInterface.getTransReceipt(groupId, transHash);
        TbReceipt tbReceipt = new TbReceipt(transReceipt.getTransactionHash(),
                transReceipt.getContractAddress(), transReceipt.getStatus(),
                transReceipt.getOutput(), transReceipt.getBlockNumber());
        addReceiptInfo(groupId, tbReceipt);
    }

    /**
     * add receipt info.
     */
    public void addReceiptInfo(int groupId, TbReceipt tbReceipt) throws BaseException {
        String tableName = TableName.RECEIPT.getTableName(groupId);
        receiptMapper.add(tableName, tbReceipt);
    }

    /**
     * query receipt list.
     */
    public List<TbReceipt> queryReceiptList(int groupId, TransListParam param)
            throws BaseException {
        String tableName = TableName.RECEIPT.getTableName(groupId);
        List<TbReceipt> listOfTran = null;
        try {
            listOfTran = receiptMapper.getList(tableName, param);
        } catch (RuntimeException ex) {
            log.error("fail queryBlockList. TransListParam:{} ", JSON.toJSONString(param), ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
        return listOfTran;
    }

    /**
     * query count of receipt.
     */
    public Integer queryCountOfTran(int groupId, TransListParam queryParam) throws BaseException {
        String tableName = TableName.RECEIPT.getTableName(groupId);
        try {
            return receiptMapper.getCount(tableName, queryParam);
        } catch (RuntimeException ex) {
            log.error("fail queryCountOfTran. queryParam:{}", JSON.toJSONString(queryParam), ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * get transaction receipt
     */
    public TransReceipt getTransReceipt(int groupId, String transHash) {
        return frontInterface.getTransReceipt(groupId, transHash);
    }

    /**
     * Remove receipt info.
     */
    public int remove(Integer groupId, Integer subTransNum) {
        String tableName = TableName.RECEIPT.getTableName(groupId);
        int affectRow = receiptMapper.remove(tableName, subTransNum, groupId);
        return affectRow;
    }
}
