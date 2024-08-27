package com.webank.wsbcs.chdis.v3.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.fisco.bcos.sdk.abi.EventEncoder;
import org.fisco.bcos.sdk.abi.EventValues;
import org.fisco.bcos.sdk.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.datatypes.Address;
import org.fisco.bcos.sdk.abi.datatypes.Event;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.utils.Numeric;
import org.springframework.stereotype.Service;
import com.webank.wsbcs.chdis.base.util.JsonUtils;
import com.webank.wsbcs.chdis.common.dao.entity.TbBac20ReceiptEvent;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Bac20EventService {

    public final static int ENCRYPT_TYPE = CryptoType.ECDSA_TYPE;
    private static CryptoSuite cryptoSuite = new CryptoSuite(ENCRYPT_TYPE);

    public final static String EVENT_ISSUE = "issue";
    public final static String EVENT_SEND = "send";
    public final static String EVENT_SENDFROM = "sendFrom";
    public final static String EVENT_DESTROYFROM = "destroyFrom";

    public static final Event TRANSFER_EVENT = new Event("Transfer",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {},
                    new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));

    public List<TbBac20ReceiptEvent> getBac20SendEvents(TransactionReceipt transactionReceipt,
            String bac20Address, String appId, long blockNumber, long blockTimestamp, int txIndex,
            String bizSeqNo) {
        log.info("getBac20SendEvents bizSeqNo:{} txHash:{} ", bizSeqNo, transactionReceipt.getTransactionHash());
        List<TbBac20ReceiptEvent> eventList = new ArrayList<>();
        if (transactionReceipt.isStatusOK()) {
            if (transactionReceipt.getLogEntries() != null) {// NOSONAR
                List<EventValuesWithLog> valueList =
                        extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
                for (int i = 0; i < valueList.size(); i++) {
                    EventValuesWithLog eventValues = valueList.get(i);
                    // 只获取元豆事件
                    String eventAddress = Numeric.prependHexPrefix(eventValues.getLog().getAddress());
                    if (!eventAddress.equals(bac20Address)) {
                        continue;
                    }
                    // 元豆事件信息
                    TbBac20ReceiptEvent receiptSendEvent = new TbBac20ReceiptEvent();
                    String fromAddress = (String) eventValues.getIndexedValues().get(0).getValue();
                    String toAddress = (String) eventValues.getIndexedValues().get(1).getValue();
                    String funcName = EVENT_SENDFROM;
                    if (Address.DEFAULT.getValue().equals(fromAddress)) {
                        funcName = EVENT_ISSUE;
                    } else if (Address.DEFAULT.getValue().equals(toAddress)) {
                        funcName = EVENT_DESTROYFROM;
                    } else if (transactionReceipt.getFrom().equals(fromAddress)) {
                        funcName = EVENT_SEND;
                    }
                    receiptSendEvent.setFuncName(funcName);
                    receiptSendEvent.setFromAddress(fromAddress);
                    receiptSendEvent.setToAddress(toAddress);
                    receiptSendEvent.setAmount(Long.valueOf(
                            eventValues.getNonIndexedValues().get(0).getValue().toString()));
                    // 基础信息
                    receiptSendEvent.setAppId(appId);
                    receiptSendEvent.setBac20Address(bac20Address);
                    receiptSendEvent.setContractAddress(transactionReceipt.getTo());
                    receiptSendEvent.setTxHash(transactionReceipt.getTransactionHash());
                    receiptSendEvent.setOperator(transactionReceipt.getFrom());
                    receiptSendEvent.setBlockNumber(blockNumber);
                    receiptSendEvent.setBlockTimestamp(blockTimestamp);
                    receiptSendEvent.setTxIndex(txIndex);
                    receiptSendEvent.setEventIndex(i);
                    receiptSendEvent.setCreateTime(new Date());
                    receiptSendEvent.setModifyTime(new Date());

                    log.info("getBac20SendEvents {}|{}", bizSeqNo,
                            JsonUtils.toJSONString(receiptSendEvent));
                    eventList.add(receiptSendEvent);
                }
                log.info("getBac20SendEvents end. bizSeqNo:{} appId:{} count:{}", bizSeqNo, appId,
                        eventList.size());
            }
        }
        return eventList;
    }

    public List<String> getBac20Users(List<TbBac20ReceiptEvent> bac20SendEvents, String bizSeqNo) {
        log.info("getBac20Users bizSeqNo:{} ", bizSeqNo);
        List<String> userList = new ArrayList<>();
        bac20SendEvents.forEach(i -> {
            // operator不考虑send，可能是代理合约
            if (!userList.contains(i.getOperator())
                    && !Address.DEFAULT.getValue().equals(i.getOperator())
                    && !i.getFuncName().contains("send")) {
                userList.add(i.getOperator());
            }
            if (!userList.contains(i.getFromAddress())
                    && !Address.DEFAULT.getValue().equals(i.getFromAddress())) {
                userList.add(i.getFromAddress());
            }
            if (!userList.contains(i.getToAddress())
                    && !Address.DEFAULT.getValue().equals(i.getToAddress())) {
                userList.add(i.getToAddress());
            }
        });
        log.info("getBac20Users end. bizSeqNo:{} count:{}", bizSeqNo, userList.size());
        return userList;
    }

    protected List<EventValuesWithLog> extractEventParametersWithLog(Event event,
            TransactionReceipt transactionReceipt) {
        return transactionReceipt.getLogEntries().stream()
                .map(log -> extractEventParametersWithLog(event, log)).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    protected EventValuesWithLog extractEventParametersWithLog(Event event,
            TransactionReceipt.Logs log) {
        final EventValues eventValues = extractEventParameters(event, log);
        return (eventValues == null) ? null : new EventValuesWithLog(eventValues, log);
    }

    protected EventValues extractEventParameters(Event event, TransactionReceipt.Logs log) {
        EventEncoder eventEncoder = new EventEncoder(cryptoSuite);
        return staticExtractEventParameters(eventEncoder, event, log);
    }

    @SuppressWarnings("rawtypes")
    public static EventValues staticExtractEventParameters(EventEncoder eventEncoder, Event event,
            TransactionReceipt.Logs log) {
        List<String> topics = log.getTopics();
        String eventTopic = eventEncoder.encode(event);
        if (!topics.get(0).equals(eventTopic)) {
            return null;
        }

        List<Type> indexedValues = new ArrayList<>();
        List<Type> nonIndexedValues =
                FunctionReturnDecoder.decode(log.getData(), event.getNonIndexedParameters());

        List<TypeReference<Type>> indexedParameters = event.getIndexedParameters();
        for (int i = 0; i < indexedParameters.size(); i++) {
            Type value = FunctionReturnDecoder.decodeIndexedValue(topics.get(i + 1),
                    indexedParameters.get(i));
            indexedValues.add(value);
        }
        return new EventValues(indexedValues, nonIndexedValues);
    }

    public static class EventValuesWithLog {
        private final EventValues eventValues;
        private final TransactionReceipt.Logs log;

        private EventValuesWithLog(EventValues eventValues, TransactionReceipt.Logs log) {
            this.eventValues = eventValues;
            this.log = log;
        }

        public List<Type> getIndexedValues() {
            return eventValues.getIndexedValues();
        }

        public List<Type> getNonIndexedValues() {
            return eventValues.getNonIndexedValues();
        }

        public TransactionReceipt.Logs getLog() {
            return log;
        }
    }

    @SuppressWarnings("unused")
    private static String eventToStr(byte[] buffer) {
        try {
            int length = 0;
            for (int i = 0; i < buffer.length; ++i) {
                if (buffer[i] == 0) {
                    length = i;
                    break;
                }
            }
            return new String(buffer, 0, length, "UTF-8");
        } catch (Exception e) {
            log.error(e.toString());
            return "";
        }
    }

}
