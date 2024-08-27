/*
 * Copyright 2014-2023 the original author or authors.
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

package com.webank.wsbcs.chdis.base.entity.trans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.utils.Numeric;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TransResultDto.
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransResultDto {
    private boolean constant;
    private String queryInfo;
    private String transactionHash;
    private String blockHash;
    private BigInteger blockNumber;
    private Long blockTimestamp;
    private BigInteger gasUsed;
    private String status;
    private String from;
    private String to;
    private String input;
    private String output;
    private String contractAddress;
    private String message;
    private List<Logs> logs;
    private String logsBloom;
    /**
     * 第一个区块中的交易proof为区块本身，所以为null
     */
    private List<MerkleProofUnit> txProof;
    private List<MerkleProofUnit> receiptProof;
    /**
     * 兼容性字段，只在v2存在
     */
    // @Deprecated
    // private String root;
    // @Deprecated
    // private String transactionIndex;
    // @Deprecated
    // protected String statusMsg;
    // @Deprecated
    // protected Boolean statusOK;


    /**
     * v2
     * 
     * @param constant
     * @param receipt 解析了receipt后再传入
     */
    public TransResultDto(boolean constant, TransactionReceipt receipt, Long blockTimestamp) {
        this.constant = constant;
        this.queryInfo = "";
        this.transactionHash = receipt.getTransactionHash();
        this.blockHash = receipt.getBlockHash();
        this.blockNumber = Numeric.decodeQuantity(receipt.getBlockNumber());
        this.blockTimestamp = blockTimestamp;
        this.gasUsed = Numeric.decodeQuantity(receipt.getGasUsed());
        this.status = Numeric.decodeQuantity(receipt.getStatus()).toString();
        this.from = receipt.getFrom();
        this.to = receipt.getTo();
        this.input = receipt.getInput();
        this.output = receipt.getOutput();
        this.contractAddress = receipt.getContractAddress();
        this.message = receipt.getMessage();
        this.logsBloom = receipt.getLogsBloom();
        if (receipt.getLogs() != null && !receipt.getLogs().isEmpty()) {
            this.logs = receipt.getLogs().stream().map(Logs::new).collect(Collectors.toList());
        }
        if (receipt.getTxProof() != null && !receipt.getTxProof().isEmpty()) {
            this.txProof = receipt.getTxProof().stream().map(MerkleProofUnit::new)
                    .collect(Collectors.toList());
        }
        if (receipt.getReceiptProof() != null && !receipt.getReceiptProof().isEmpty()) {
            this.receiptProof = receipt.getReceiptProof().stream().map(MerkleProofUnit::new)
                    .collect(Collectors.toList());
        }
    }

    /**
     * v3
     * 
     * @param constant
     * @param receipt
     */
    public TransResultDto(boolean constant, org.fisco.bcos.sdk.v3.model.TransactionReceipt receipt,
            Long blockTimestamp) {
        this.constant = constant;
        this.queryInfo = "";
        this.transactionHash = receipt.getTransactionHash();
        this.blockNumber = receipt.getBlockNumber();
        this.blockTimestamp = blockTimestamp;
        this.gasUsed = Numeric.decodeQuantity(receipt.getGasUsed());
        this.status = String.valueOf(receipt.getStatus());
        this.from = receipt.getFrom();
        this.to = receipt.getTo();
        this.input = receipt.getInput();
        this.output = receipt.getOutput();
        this.contractAddress = receipt.getContractAddress();
        this.message = receipt.getMessage();
        this.logsBloom = "";
        if (receipt.getLogEntries() != null && !receipt.getLogEntries().isEmpty()) {
            this.logs =
                    receipt.getLogEntries().stream().map(Logs::new).collect(Collectors.toList());
        }
        // if (receipt.getTransactionProof() != null && !receipt.getTransactionProof().isEmpty()) {
        // this.txProof =
        // receipt.getTransactionProof().stream().map(MerkleProofUnit::new).collect(Collectors.toList());
        // }
        // if (receipt.getReceiptProof() != null && !receipt.getReceiptProof().isEmpty()) {
        // this.receiptProof =
        // receipt.getReceiptProof().stream().map(MerkleProofUnit::new).collect(Collectors.toList());
        // }
    }

    public TransResultDto(boolean constant, String queryInfo) {
        this.constant = constant;
        this.queryInfo = queryInfo;
    }

    @Data
    public static class Logs {
        private String address;
        private List<String> topics;
        private String data;
        private String blockNumber;

        public Logs(TransactionReceipt.Logs log) {
            this.address = log.getAddress();
            this.topics = log.getTopics();
            this.data = log.getData();
            this.blockNumber = log.getBlockNumber();
        }

        public Logs(org.fisco.bcos.sdk.v3.model.TransactionReceipt.Logs log) {
            this.address = log.getAddress();
            this.topics = log.getTopics();
            this.data = log.getData();
            this.blockNumber = log.getBlockNumber();
        }
    }

    @Data
    public static class MerkleProofUnit {
        private List<String> left;
        private List<String> right;

        public MerkleProofUnit(org.fisco.bcos.sdk.model.MerkleProofUnit merkleProofUnit) {
            this.left = merkleProofUnit.getLeft();
            this.right = merkleProofUnit.getRight();
        }

        public MerkleProofUnit(org.fisco.bcos.sdk.v3.model.MerkleProofUnit merkleProofUnit) {
            this.left = merkleProofUnit.getLeft();
            this.right = merkleProofUnit.getRight();
        }
    }


}
