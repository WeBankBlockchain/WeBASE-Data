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
package data.collect.test.frontInterface;

import com.webank.webase.data.collect.Application;
import com.webank.webase.data.collect.base.tools.JacksonUtils;
import com.webank.webase.data.collect.block.entity.BlockInfo;
import com.webank.webase.data.collect.front.entity.TotalTransCountInfo;
import com.webank.webase.data.collect.frontinterface.FrontInterfaceService;
import com.webank.webase.data.collect.frontinterface.entity.PeerInfo;
import com.webank.webase.data.collect.frontinterface.entity.SyncStatus;
import com.webank.webase.data.collect.monitor.ChainTransInfo;
import com.webank.webase.data.collect.receipt.entity.TransReceipt;
import com.webank.webase.data.collect.transaction.entity.TransactionInfo;
import java.math.BigInteger;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class FrontServiceTest {

    @Autowired
    private FrontInterfaceService frontInterface;
    private Integer chainId = 1;
    private Integer groupId = 1;
    private BigInteger blockNumber = new BigInteger("12");
    private String transHash = "0x1d99fdfa84b90d9478f09b722bb85b7d804e6b4d0273ec94fa4418c56a415211";
    private String blockHash = "0x337eb77084c0e6b09c508cc0f7dbc125af459c2aab19b9ca43c731ffc9fe604f";
    private String frontIp = "localhost";
    private Integer frontPort = 5002;

    @Test
    public void getContractCodeTest() {
        String contractAddress = "0xb68b0ca60cc4d8b207875c9a0ab6c3a782db9318";
        String str = frontInterface.getContractCode(chainId, groupId, contractAddress, blockNumber);
        assert (str != null);
        System.out.println(str);
    }

    @Test
    public void getTransReceiptTest() {
        TransReceipt transReceipt = frontInterface.getTransReceipt(chainId, groupId, transHash);
        assert (transReceipt != null);
        System.out.println(JacksonUtils.objToString(transReceipt));
    }

    @Test
    public void getTransactionTest() {
        TransactionInfo transactionInfo =
                frontInterface.getTransaction(chainId, groupId, transHash);
        assert (transactionInfo != null);
        System.out.println(JacksonUtils.objToString(transactionInfo));
    }

    @Test
    public void getBlockByNumberTest() {
        BlockInfo blockInfo = frontInterface.getBlockByNumber(chainId, groupId, blockNumber);
        assert (blockInfo != null);
        System.out.println(JacksonUtils.objToString(blockInfo));
    }

    @Test
    public void getblockFromFrontByHashTest() {
        BlockInfo blockInfo = frontInterface.getblockByHash(chainId, groupId, blockHash);
        assert (blockInfo != null);
        System.out.println(JacksonUtils.objToString(blockInfo));
    }

    @Test
    public void getTransFromFrontByHashTest() {
        ChainTransInfo chainTransInfo =
                frontInterface.getTransInfoByHash(chainId, groupId, transHash);
        assert (chainTransInfo != null);
        System.out.println(JacksonUtils.objToString(chainTransInfo));
    }

    @Test
    public void getAddressFromFrontByHashTest() {
        String contractAddress = frontInterface.getAddressByHash(chainId, groupId, transHash);
        assert (contractAddress != null);
        System.out.println(contractAddress);
    }

    @Test
    public void getCodeFromFronthTest() {
        String contractAddress = "0xb68b0ca60cc4d8b207875c9a0ab6c3a782db9318";
        String code =
                frontInterface.getCodeFromFront(chainId, groupId, contractAddress, blockNumber);
        assert (code != null);
        System.out.println(code);
    }



    @Test
    public void getTotalTransactionCountTest() {
        TotalTransCountInfo totalTransCount =
                frontInterface.getTotalTransactionCount(chainId, groupId);
        assert (totalTransCount != null);
        System.out.println(JacksonUtils.objToString(totalTransCount));
    }

    @Test
    public void getTransByBlockNumberTest() {
        List<TransactionInfo> list =
                frontInterface.getTransByBlockNumber(chainId, groupId, blockNumber);
        assert (list != null && list.size() > 0);
        System.out.println(JacksonUtils.objToString(list));
    }

    @Test
    public void getGroupPeersTest() {
        List<String> list = frontInterface.getGroupPeers(chainId, groupId);
        assert (list != null && list.size() > 0);
        System.out.println(JacksonUtils.objToString(list));
    }


    @Test
    public void getGroupListTest() {
        List<String> list = frontInterface.getGroupListFromSpecificFront(frontIp, frontPort);
        assert (list != null && list.size() > 0);
        System.out.println("=====================list:" + JacksonUtils.objToString(list));
    }

    @Test
    public void getPeersTest() {
        PeerInfo[] list = frontInterface.getPeers(chainId, groupId);
        assert (list != null && list.length > 0);
        System.out.println("=====================list:" + JacksonUtils.objToString(list));
    }

    @Test
    public void getConsensusStatusTest() {
        String consensunsStatus = frontInterface.getConsensusStatus(chainId, groupId);
        assert (consensunsStatus != null);
        System.out.println("=====================consensunsStatus:" + consensunsStatus);
    }

    @Test
    public void syncStatusTest() {
        SyncStatus status = frontInterface.getSyncStatus(chainId, groupId);
        assert (status != null);
        System.out.println("=====================status:" + JacksonUtils.objToString(status));
    }

    @Test
    public void getClientVersion() {
        String clientVersion = frontInterface.getClientVersion(frontIp, frontPort, groupId);
        System.out.println(clientVersion);
        assert (StringUtils.isNotEmpty(clientVersion));
    }
}
