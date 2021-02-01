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
package com.webank.webase.data.collect.block.taskpool;

import com.webank.webase.data.collect.base.enums.TableName;
import com.webank.webase.data.collect.block.BlockMapper;
import com.webank.webase.data.collect.parser.ParserMapper;
import com.webank.webase.data.collect.receipt.ReceiptMapper;
import com.webank.webase.data.collect.transaction.TransactionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * RollBackService.
 *
 */
@Service
public class RollBackService {

    @Autowired
    private BlockMapper blockMapper;
    @Autowired
    private TransactionMapper transactionMapper;
    @Autowired
    private ReceiptMapper receiptMapper;
    @Autowired
    private ParserMapper parserMapper;

    /**
     * Do rollback by blockNumber.
     * 
     * @param chainId
     * @param groupId
     * @param blockNumber
     */
    @Transactional
    public void rollback(int chainId, int groupId, long blockNumber) {
        blockMapper.rollback(TableName.BLOCK.getTableName(chainId, groupId), blockNumber);
        transactionMapper.rollback(TableName.TRANS.getTableName(chainId, groupId), blockNumber);
        receiptMapper.rollback(TableName.RECEIPT.getTableName(chainId, groupId), blockNumber);
        parserMapper.rollback(TableName.PARSER.getTableName(chainId, groupId), blockNumber);
    }
}
