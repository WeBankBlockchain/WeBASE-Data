/**
 * Copyright 2014-2019 the original author or authors.
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
package com.webank.wsbcs.chdis.v3.trans;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionBuilderJniObj;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.request.Transaction;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.codec.ContractCodec;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.codec.Utils;
import org.fisco.bcos.sdk.v3.codec.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.RevertMessageParser;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.TransactionDecoderService;
import org.fisco.bcos.sdk.v3.transaction.manager.TransactionProcessor;
import org.fisco.bcos.sdk.v3.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.v3.transaction.pusher.TransactionPusherService;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.fisco.bcos.sdk.v3.utils.Numeric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.webank.wsbcs.chdis.base.code.ConstantCode;
import com.webank.wsbcs.chdis.base.entity.trans.TransResultDto;
import com.webank.wsbcs.chdis.base.exception.BaseException;
import com.webank.wsbcs.chdis.base.properties.ConstantProperties;
import com.webank.wsbcs.chdis.base.util.CommonUtils;
import com.webank.wsbcs.chdis.base.util.JsonUtils;
import com.webank.wsbcs.chdis.v3.util.ContractAbiUtil;
import com.webank.wsbcs.chdis.v3.web3api.Web3ApiServiceV3;
import lombok.extern.log4j.Log4j2;

/**
 * services for contract data.
 */
@Log4j2
@Service
public class TransServiceV3 {
    @Autowired
    private Web3ApiServiceV3 web3ApiServiceV3;
    @Autowired
    private ConstantProperties constants;

    private static final int USE_SOLIDITY = 1;
    private static final Boolean IS_WASM = false;

    public long getBac20Amount(String groupId, String contractAddress, String abiStr,
            String funcName, List<String> funcParam, String chainId, String bizSeqNo) {
        try {
            byte[] encodeFunction = this.encodeFunction2ByteArrNew(abiStr, funcName, funcParam,
                    groupId, chainId, bizSeqNo);
            List<String> result = this.handleCall(groupId, contractAddress, encodeFunction, abiStr,
                    funcName, chainId, bizSeqNo);
            log.debug("finish exec method[getBac20Amount]. result:{}", result);
            return Long.valueOf(result.get(0));
        } catch (Exception e) {
            log.error("getBac20Amount fail. bizSeqNo:{} {}", bizSeqNo, e.getMessage());
            throw new BaseException(ConstantCode.REQUEST_CHAIN_EXCEPTION, bizSeqNo);
        }
    }

    public String getContactType(String groupId, String contractAddress, String abiStr,
            String funcName, List<String> funcParam, String chainId, String bizSeqNo) {
        try {
            byte[] encodeFunction = this.encodeFunction2ByteArrNew(abiStr, funcName, funcParam,
                    groupId, chainId, bizSeqNo);
            List<String> result = this.handleCall(groupId, contractAddress, encodeFunction, abiStr,
                    funcName, chainId, bizSeqNo);
            log.debug("finish exec method[getContactType]. result:{}", result);
            return result.get(0);
        } catch (Exception e) {
            log.error("getContactType fail. bizSeqNo:{} {}", bizSeqNo, e.getMessage());
            throw new BaseException(ConstantCode.REQUEST_CHAIN_EXCEPTION, bizSeqNo);
        }
    }

    public String getReportData(String groupId, String contractAddress, String abiStr,
            String funcName, List<String> funcParam, String chainId, String bizSeqNo) {
        try {
            byte[] encodeFunction = this.encodeFunction2ByteArrNew(abiStr, funcName, funcParam,
                    groupId, chainId, bizSeqNo);
            List<String> result = this.handleCall(groupId, contractAddress, encodeFunction, abiStr,
                    funcName, chainId, bizSeqNo);
            log.debug("finish exec method[getReportData]. result:{}", result);
            return result.get(0);
        } catch (Exception e) {
            log.warn("getReportData fail. bizSeqNo:{} {}", bizSeqNo, e.getMessage());
        }
        return null;
    }

    public TransResultDto transHandle(String groupId, String signUserId, String contractAddress,
            String abiStr, String funcName, List<Object> funcParam, String chainId,
            String bizSeqNo) {

        Client client = web3ApiServiceV3.getWeb3j(groupId, chainId);
        byte[] encodeFunction = this.encodeFunction2ByteArr(abiStr, funcName, funcParam, groupId,
                chainId, bizSeqNo);

        boolean isTxConstant = this.getABIDefinition(abiStr, funcName, bizSeqNo).isConstant();
        TransResultDto transResultDto;
        if (isTxConstant) {
            Object queryInfo = this.handleCall(groupId, contractAddress, encodeFunction, abiStr,
                    funcName, chainId, bizSeqNo);
            transResultDto = new TransResultDto(true, JsonUtils.objToString(queryInfo));
        } else {
            TransactionReceipt receipt = this.handleTransaction(client, signUserId, contractAddress,
                    encodeFunction, chainId, bizSeqNo);
            transResultDto = new TransResultDto(false, receipt, null);
        }
        log.info("finish exec method[transHandleWithSignForPrecompile]. restRsp:{}",
                JsonUtils.objToString(transResultDto));
        return transResultDto;
    }


    public TransResultDto transHandleNew(String groupId, String signUserId, String contractAddress,
            String abiStr, String funcName, List<String> funcParam, String chainId,
            String bizSeqNo) {

        Client client = web3ApiServiceV3.getWeb3j(groupId, chainId);
        byte[] encodeFunction = this.encodeFunction2ByteArrNew(abiStr, funcName, funcParam, groupId,
                chainId, bizSeqNo);

        boolean isTxConstant = this.getABIDefinition(abiStr, funcName, bizSeqNo).isConstant();
        TransResultDto transResultDto;
        if (isTxConstant) {
            Object queryInfo = this.handleCall(groupId, contractAddress, encodeFunction, abiStr,
                    funcName, chainId, bizSeqNo);
            transResultDto = new TransResultDto(true, JsonUtils.objToString(queryInfo));
        } else {
            TransactionReceipt receipt = this.handleTransaction(client, signUserId, contractAddress,
                    encodeFunction, chainId, bizSeqNo);
            transResultDto = new TransResultDto(false, receipt, null);
        }
        log.info("finish exec method[transHandleWithSignForPrecompile]. restRsp:{}",
                JsonUtils.objToString(transResultDto));
        return transResultDto;
    }

    /**
     * get encoded function for /trans/query-transaction
     * 
     * @param abiStr
     * @param funcName
     * @param funcParam
     * @return
     */
    public byte[] encodeFunction2ByteArrNew(String abiStr, String funcName, List<String> funcParam,
            String groupId, String chainId, String bizSeqNo) throws BaseException {

        funcParam = funcParam == null ? new ArrayList<>() : funcParam;
        log.debug("abiStr:{} ,funcName:{},funcParam {},groupID {}", abiStr, funcName, funcParam,
                groupId);
        ContractCodec abiCodec =
                new ContractCodec(web3ApiServiceV3.getCryptoSuite(groupId, chainId).hashImpl, IS_WASM);
        byte[] encodeFunction;
        try {
            encodeFunction = abiCodec.encodeMethodFromString(abiStr, funcName, funcParam);
        } catch (ContractCodecException e) {
            log.error("transHandleWithSign encode fail:{}|{}", bizSeqNo, e);
            throw new BaseException(ConstantCode.CONTRACT_TYPE_ENCODED_ERROR, bizSeqNo);
        }
        log.debug("encodeFunction2Str encodeFunction:{}", Hex.toHexString(encodeFunction));
        return encodeFunction;
    }

    // front's trans service

    public TransactionReceipt sendSignedTransaction(String signedStr, Boolean sync, String groupId,
            String chainId, String bizSeqNo) throws BaseException {

        Client client = web3ApiServiceV3.getWeb3j(groupId, chainId);
        TransactionPusherService txPusher = new TransactionPusherService(client);
        if (sync) {
            TransactionReceipt receipt = this.pushMessageChain(client, signedStr, bizSeqNo);
            this.decodeReceipt(client, receipt);
            return receipt;
        } else {
            log.info("sendSignedTransaction push only signedStr:{}", signedStr);

            // v3无法获取交易hash，可以发送成功
            txPusher.pushAsync(signedStr, new ProxyTransactionCallback(new CompletableFuture<>()));

            TransactionReceipt receipt = new TransactionReceipt();
            receipt.setMessage("cal hash by encodedTransaction through cryptoSuite");
            // receipt.setTransactionHash(getTransHash(web3ApiServiceV3.getCryptoSuite(groupId,
            // chainId), signedStr));
            log.info("sendSignedTransaction push only receipt:{}", JsonUtils.objToString(receipt));
            return receipt;
        }
    }

    /**
     * send message to node.
     *
     * @param signMsg signMsg
     * @return 返回的交易回执已将hex类型的数字转为十进制
     */
    public TransactionReceipt pushMessageChain(Client client, String signMsg, String bizSeqNo)
            throws BaseException {
        Instant startTime = Instant.now();
        String transactionHash = getTransHash(client.getCryptoSuite(), signMsg);

        // 设置future超时，5s等待setTiger的callback
        CompletableFuture<TransactionReceipt> future = new CompletableFuture<>();
        // 发交易，callback
        client.sendTransactionAsync(signMsg, false, new ProxyTransactionCallback(future));

        TransactionReceipt receipt;
        try {
            receipt = future.get(constants.getTransReceiptTimeout(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            log.error("++++++sendMessageChain {}|{}|{}|{}", bizSeqNo,
                    ConstantCode.SEND_TO_CHAIN_RECEIPT_FUTURE_ERROR.getCode(),
                    Duration.between(startTime, Instant.now()).toMillis(),
                    JsonUtils.toJSONString(Arrays.asList(transactionHash, e)));
            throw new BaseException(ConstantCode.SEND_TO_CHAIN_RECEIPT_FUTURE_ERROR, bizSeqNo);
        } catch (TimeoutException e) {
            log.error("++++++sendMessageChain {}|{}|{}|{}", bizSeqNo,
                    ConstantCode.SEND_TO_CHAIN_RECEIPT_TIMEOUT.getCode(),
                    Duration.between(startTime, Instant.now()).toMillis(),
                    JsonUtils.toJSONString(Arrays.asList(transactionHash, e)));
            throw new BaseException(ConstantCode.SEND_TO_CHAIN_RECEIPT_TIMEOUT, bizSeqNo);
        } // 异常时需要根据这个异常日志的hash，补齐数据
        log.info("sendSignedTransaction usedTime{}|{}",
                Duration.between(startTime, Instant.now()).toMillis(), bizSeqNo);
        // decode receipt
        TransactionDecoderService txDecoder =
                new TransactionDecoderService(client.getCryptoSuite().hashImpl, IS_WASM);
        String receiptMsg = txDecoder.decodeReceiptStatus(receipt).getReceiptMessages();
        receipt.setMessage(receiptMsg);
        return receipt;
    }



    public Object sendQueryTransaction(String encodeStr, String contractAddress, String funcName,
            List<Object> contractAbi, String groupId, String chainId, String bizSeqNo)
            throws BaseException {
        byte[] encodeStrArray = Hex.decode(encodeStr);
        Client client = web3ApiServiceV3.getWeb3j(groupId, chainId);
        String userAddress = client.getCryptoSuite().getCryptoKeyPair().getAddress();
        String callOutput =
                client.call(new Transaction(userAddress, contractAddress, encodeStrArray))
                        .getCallResult().getOutput();
        log.info("sendQueryTransaction {}|{}", bizSeqNo, callOutput);

        ABIDefinition abiDefinition =
                getABIDefinition(JsonUtils.toJSONString(contractAbi), funcName, bizSeqNo);

        List<String> funOutputTypes = ContractAbiUtil.getFuncOutputType(abiDefinition);
        List<TypeReference<?>> finalOutputs = ContractAbiUtil.outputFormat(funOutputTypes);

        FunctionReturnDecoder functionReturnDecoder = new FunctionReturnDecoder();
        List<Type> typeList = functionReturnDecoder.decode(callOutput, Utils.convert(finalOutputs));
        Object response;
        if (typeList.size() > 0) {
            response = ContractAbiUtil.callResultParse(funOutputTypes, typeList);
        } else {
            response = typeList;
        }
        return response;
    }


    /**
     * signMessage to create raw transaction and encode data
     *
     * @param groupId id
     * @param contractAddress info
     * @param data info
     * @return
     */
    public String signMessage(String groupId, String signUserId, String contractAddress,
            byte[] data, String chainId, String bizSeqNo) throws BaseException {
        log.info("signMessage functionData to sign:{}|{}", Hex.toHexString(data), bizSeqNo);
        Instant nodeStartTime = Instant.now();
        Client client = web3ApiServiceV3.getWeb3j(groupId, chainId);
        // to encode raw tx
        Pair<String, String> chainIdAndGroupId =
                TransactionProcessorFactory.getChainIdAndGroupId(client);
        long transactionData = 0L;
        String transactionDataHash = "";
        try {
            transactionData = TransactionBuilderJniObj.createTransactionData(groupId,
                    chainIdAndGroupId.getLeft(), contractAddress, Hex.toHexString(data), "",
                    client.getBlockLimit().longValue());
            // String encodedTransaction =
            // TransactionBuilderJniObj.encodeTransactionData(transactionData);
            // transactionDataHash = client.getCryptoSuite().hash(encodedTransaction);
            transactionDataHash = TransactionBuilderJniObj
                    .calcTransactionDataHash(client.getCryptoType(), transactionData);
            log.info("transactionDataHash:{}", transactionDataHash);
        } catch (JniException e) {
            log.error("createTransactionData jni error:{}|{}", bizSeqNo, e);
        }
        if (transactionData == 0L || StringUtils.isBlank(transactionDataHash)) {
            log.error("signMessage encodedTransaction:{}|{}", transactionData, transactionDataHash);
            try {
                // 必须手动销毁
                TransactionBuilderJniObj.destroyTransactionData(transactionData);
                log.debug("destroyTransactionData {}", transactionData);
            } catch (JniException e) {
                log.warn("destroyTransactionData :{}", e.getMessage());
            }
            throw new BaseException(ConstantCode.ENCODE_TX_JNI_ERROR);
        }
        // v3 sig result of sign hash
        SignatureResult signData = this.requestSignHashForSign(transactionDataHash, signUserId,
                groupId, chainId, bizSeqNo);
        String transactionDataHashSignedData = Hex.toHexString(signData.encode());
        String signedMessage = null;
        try {
            signedMessage = TransactionBuilderJniObj.createSignedTransaction(transactionData,
                    transactionDataHashSignedData, transactionDataHash, USE_SOLIDITY);
            try {
                // 必须手动销毁
                TransactionBuilderJniObj.destroyTransactionData(transactionData);
                log.debug("destroyTransactionData {}", transactionData);
            } catch (JniException e) {
                log.warn("destroyTransactionData :{}", e.getMessage());
            }
        } catch (JniException e) {
            log.error("createSignedTransactionData jni error:{}|{}", bizSeqNo, e);
        }
        log.info("***signMessage cost time***: {}",
                Duration.between(nodeStartTime, Instant.now()).toMillis());
        if (StringUtils.isBlank(signedMessage)) {
            throw new BaseException(ConstantCode.DATA_SIGN_ERROR, bizSeqNo);
        }
        return signedMessage;
    }


    /**
     * sign by hash in sign
     * 
     * @param messageHash hash
     * @param signUserId
     * @return
     */
    public SignatureResult requestSignHashForSign(String messageHash, String signUserId,
            String groupId, String chainId, String bizSeqNo) throws BaseException {
        // SignHashInfo signHashInfo = new SignHashInfo(signUserId, messageHash);

        Instant startTime = Instant.now();
        // String signDataStr = userService.getSignData(signHashInfo, bizSeqNo);
        String signDataStr = "";
        if (StringUtils.isBlank(signDataStr)) {
            // log.error("request sign return null encodeInfo{}|{}", signHashInfo, bizSeqNo);
            throw new BaseException(ConstantCode.DATA_SIGN_ERROR, bizSeqNo);
        }
        log.info("get requestSignForSign cost time: {}|{}|{}",
                signUserId, Duration.between(startTime, Instant.now()).toMillis(), bizSeqNo);
        SignatureResult signData = CommonUtils.stringToSignatureDataV3(signDataStr,
                web3ApiServiceV3.getCryptoType(groupId, chainId));
        return signData;
    }


    private ABIDefinition getABIDefinition(String abiStr, String functionName, String bizSeqNo)
            throws BaseException {
        // check function name
        ABIDefinition abiDefinition = null;
        try {
            abiDefinition = ContractAbiUtil.getAbiDefinition(functionName, abiStr);
        } catch (Exception e) {
            log.error("abi parse error. abi:{}|{}", bizSeqNo, JsonUtils.objToString(abiStr));
            throw new BaseException(ConstantCode.ABI_PARSE_ERROR, bizSeqNo);
        }
        if (Objects.isNull(abiDefinition)) {
            log.error("transaction fail. func:{} is not existed {}", functionName, bizSeqNo);
            throw new BaseException(ConstantCode.FUNCTION_NOT_EXISTS, bizSeqNo);
        }
        if (abiDefinition.getStateMutability().equals("pure")
                || abiDefinition.getStateMutability().equals("constant")
                || abiDefinition.getStateMutability().equals("view")) {
            abiDefinition.setConstant(true);
        }
        return abiDefinition;
    }

    public List<String> handleCall(String groupId, String contractAddress, byte[] encodedFunction,
            String abiStr, String funcName, String chainId, String bizSeqNo) throws BaseException {

        Client client = web3ApiServiceV3.getWeb3j(groupId, chainId);
        Pair<String, String> chainIdAndGroupId =
                TransactionProcessorFactory.getChainIdAndGroupId(client);
        TransactionProcessor transactionProcessor =
                new TransactionProcessor(client, client.getCryptoSuite().getCryptoKeyPair(),
                        String.valueOf(groupId), chainIdAndGroupId.getLeft());
        Call.CallOutput callOutput = transactionProcessor
                .executeCall(client.getCryptoSuite().getCryptoKeyPair().getAddress(),
                        contractAddress, encodedFunction)
                .getCallResult();
        // if error
        if (callOutput.getStatus() != 0) {
            Tuple2<Boolean, String> parseResult = RevertMessageParser
                    .tryResolveRevertMessage(callOutput.getStatus(), callOutput.getOutput());
            log.warn("call contract error:{}|bizSeqNo:{}", parseResult, bizSeqNo);
            String parseResultStr = parseResult.getValue1() ? parseResult.getValue2()
                    : "call contract error of status" + callOutput.getStatus();
            throw new BaseException(ConstantCode.CALL_CONTRACT_ERROR.getCode(), parseResultStr);
        } else {
            ContractCodec abiCodec = new ContractCodec(
                    web3ApiServiceV3.getCryptoSuite(groupId, chainId).hashImpl, client.isWASM());
            try {
                log.debug("========= callOutput.getOutput():{}", callOutput.getOutput());
                // [
                // {
                // "value": "Hello, World!",
                // "typeAsString": "string"
                // }
                // ]
                List<String> resultStrList = abiCodec.decodeMethodToString(abiStr, funcName,
                        Hex.decode(Numeric.cleanHexPrefix(callOutput.getOutput())));
                // // 统一返回格式
                // List<TypeResponse> typeList = resultStrList.stream().map(res -> new
                // TypeResponse("string", res)).collect(Collectors.toList());
                log.debug("call contract res:{}", JsonUtils.objToString(resultStrList));
                return resultStrList;
            } catch (ContractCodecException e) {
                log.error("handleCall decode call output fail:[]", e);
                throw new BaseException(ConstantCode.CONTRACT_TYPE_DECODED_ERROR);
            }
        }
    }

    /**
     * handle tx locally
     * 
     * @param client
     * @param cryptoKeyPair
     * @param contractAddress
     * @param encodeFunction
     * @return
     */
    public TransactionReceipt handleTransaction(Client client, CryptoKeyPair cryptoKeyPair,
            String contractAddress, byte[] encodeFunction, String bizSeqNo) {
        Instant startTime = Instant.now();
        log.info("handleTransaction start startTime:{}|{}", startTime.toEpochMilli(), bizSeqNo);
        // send tx
        TransactionProcessor txProcessor =
                TransactionProcessorFactory.createTransactionProcessor(client, cryptoKeyPair);
        TransactionReceipt receipt = txProcessor.sendTransactionAndGetReceipt(contractAddress,
                encodeFunction, cryptoKeyPair, USE_SOLIDITY);
        // cover null message through statusCode
        this.decodeReceipt(client, receipt);
        log.info("execTransaction end useTime:{},receipt:{}|{}",
                Duration.between(startTime, Instant.now()).toMillis(), receipt, bizSeqNo);
        return receipt;
    }


    /**
     * handle tx with sign
     * 
     * @param client
     * @param signUserId
     * @param contractAddress
     * @param encodeFunction
     * @return
     */
    public TransactionReceipt handleTransaction(Client client, String signUserId,
            String contractAddress, byte[] encodeFunction, String chainId, String bizSeqNo)
            throws BaseException {
        log.debug("handleTransaction signUserId:{},contractAddress:{},encodeFunction:{}",
                signUserId, contractAddress, encodeFunction);
        String groupId = client.getGroup();
        String signedMessageStr = this.signMessage(groupId, signUserId, contractAddress,
                encodeFunction, chainId, bizSeqNo);
        if (StringUtils.isBlank(signedMessageStr)) {
            log.error("signStr is empty {}", bizSeqNo);
            throw new BaseException(ConstantCode.DATA_SIGN_ERROR, bizSeqNo);
        }
        Instant nodeStartTime = Instant.now();
        // send transaction
        TransactionReceipt receipt =
                sendSignedTransaction(signedMessageStr, true, groupId, chainId, bizSeqNo);
        this.decodeReceipt(client, receipt);
        log.info("***node cost time***: {}",
                Duration.between(nodeStartTime, Instant.now()).toMillis());
        return receipt;

    }

    /**
     * get encoded function for /trans/query-transaction
     * 
     * @param abiStr
     * @param funcName
     * @param funcParam
     * @return
     */
    public byte[] encodeFunction2ByteArr(String abiStr, String funcName, List<Object> funcParam,
            String groupId, String chainId, String bizSeqNo) throws BaseException {

        funcParam = funcParam == null ? new ArrayList<>() : funcParam;
        this.validFuncParam(abiStr, funcName, funcParam, bizSeqNo);
        log.debug("abiStr:{} ,funcName:{},funcParam {},groupID {}", abiStr, funcName, funcParam,
                groupId);
        ContractCodec abiCodec =
                new ContractCodec(web3ApiServiceV3.getCryptoSuite(groupId, chainId).hashImpl, IS_WASM);
        byte[] encodeFunction;
        try {
            encodeFunction = abiCodec.encodeMethod(abiStr, funcName, funcParam);
        } catch (ContractCodecException e) {
            log.error("transHandleWithSign encode fail:{}|{}", bizSeqNo, e);
            throw new BaseException(ConstantCode.CONTRACT_TYPE_ENCODED_ERROR, bizSeqNo);
        }
        log.debug("encodeFunction2Str encodeFunction:{}", Hex.toHexString(encodeFunction));
        return encodeFunction;
    }

    /**
     * check input
     * 
     * @param contractAbiStr
     * @param funcName
     * @param funcParam
     */
    private void validFuncParam(String contractAbiStr, String funcName, List<Object> funcParam,
            String bizSeqNo) throws BaseException {
        ABIDefinition abiDefinition = this.getABIDefinition(contractAbiStr, funcName, bizSeqNo);
        List<ABIDefinition.NamedType> inputTypeList = abiDefinition.getInputs();
        if (inputTypeList.size() != funcParam.size()) {
            log.error("validFuncParam param not match {}", bizSeqNo);
            throw new BaseException(ConstantCode.FUNC_PARAM_SIZE_NOT_MATCH, bizSeqNo);
        }
        for (int i = 0; i < inputTypeList.size(); i++) {
            String type = inputTypeList.get(i).getType();
            if (type.startsWith("bytes")) {
                if (type.split("\\[").length >= 3) {
                    log.warn("validFuncParam param, not support bytes 2d array or more");
                    return;
                } else if (type.split("\\[").length <= 1) { // 非数组类型
                    // if not bytes[], bytes or bytesN
                    // update funcParam
                    String bytesHexStr = (String) (funcParam.get(i));
                    byte[] inputArray = Hex.decode(Numeric.cleanHexPrefix(bytesHexStr));
                    // bytesN: bytes1, bytes32 etc.
                    if (type.startsWith("bytes") && !type.endsWith("]")) { // 非数组类型
                        int bytesNLength = Integer.parseInt(type.substring("bytes".length()));
                        if (inputArray.length != bytesNLength) {
                            log.error("validFuncParam param of bytesN size not match {}", bizSeqNo);
                            throw new BaseException(ConstantCode.FUNC_PARAM_BYTES_SIZE_NOT_MATCH,
                                    bizSeqNo);
                        }
                    }
                    // replace hexString with array
                    funcParam.set(i, inputArray);
                } else {
                    // if bytes[] or bytes32[]
                    List<String> hexStrArray = (List<String>) (funcParam.get(i));
                    List<byte[]> bytesArray = new ArrayList<>(hexStrArray.size());
                    for (int j = 0; j < hexStrArray.size(); j++) {
                        String bytesHexStr = hexStrArray.get(j);
                        byte[] inputArray = Hex.decode(bytesHexStr);
                        // check: bytesN: bytes1, bytes32 etc.
                        // 如果不包含[]，代表是数组，但是是定长数组，如bytes[10], bytes32[100]，不做检查
                        if (type.length() > "bytes[]".length() && type.contains("[]")) {
                            // bytes32[] => 32[]
                            String temp = type.substring("bytes".length());
                            // 32[] => 32
                            int bytesNLength =
                                    Integer.parseInt(temp.substring(0, temp.length() - 2));
                            if (inputArray.length != bytesNLength) {
                                log.error("validFuncParam param of bytesN size not match {}",
                                        bizSeqNo);
                                throw new BaseException(
                                        ConstantCode.FUNC_PARAM_BYTES_SIZE_NOT_MATCH, bizSeqNo);
                            }
                        }
                        bytesArray.add(inputArray);
                    }
                    // replace hexString with array
                    funcParam.set(i, bytesArray);
                }
            }
        }
    }

    /**
     * sdk仅用于了预编译合约，其余解析需要自行完成
     * 
     * @param client
     * @param receipt
     */
    public void decodeReceipt(Client client, TransactionReceipt receipt) {
        // decode receipt
        TransactionDecoderService txDecoder =
                new TransactionDecoderService(client.getCryptoSuite().hashImpl, IS_WASM);
        String receiptMsg = txDecoder.decodeReceiptStatus(receipt).getReceiptMessages();
        receipt.setMessage(receiptMsg);
        CommonUtils.processReceiptHexNumber(receipt);
    }

    private static String getTransHash(CryptoSuite cryptoSuite, String signMsg) {
        return "0x"
                + Hex.toHexString(cryptoSuite.hash(Hex.decode(Numeric.cleanHexPrefix(signMsg))));
    }

}
