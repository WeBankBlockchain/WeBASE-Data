package com.webank.wsbcs.chdis.v3.trans;

import java.util.concurrent.CompletableFuture;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import com.webank.wsbcs.chdis.base.util.JsonUtils;
import lombok.extern.log4j.Log4j2;

/**
 * @author marsli
 * @date 2021/12/30
 */
@Log4j2
public class ProxyTransactionCallback extends TransactionCallback {

    private CompletableFuture<TransactionReceipt> receiptCompletableFuture;

    public ProxyTransactionCallback(
            CompletableFuture<TransactionReceipt> receiptCompletableFuture) {
        super();
        this.receiptCompletableFuture = receiptCompletableFuture;
    }

    @Override
    public void onResponse(TransactionReceipt receipt) {
        log.info("OnResponse ProxyTransactionCallback {}", JsonUtils.toJSONString(receipt));
        this.receiptCompletableFuture.complete(receipt);
    }
}
