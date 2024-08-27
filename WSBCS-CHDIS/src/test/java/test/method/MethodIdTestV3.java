/**
 * Copyright 2014-2021 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test.method;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wsbcs.chdis.base.util.Web3Tools;
import com.webank.wsbcs.chdis.v3.web3api.Web3ApiServiceV3;
import test.TestBase;

/**
 * 配置sdk的encryptType，配置为国密或标密
 */
public class MethodIdTestV3 extends TestBase {
    @Autowired 
    private Web3ApiServiceV3 web3ApiServiceV3;

    public static final String contract_CertificateMgr = "[{\"constant\":true,\"inputs\":[{\"name\":\"account\",\"type\":\"address\"}],\"name\":\"isIssuer\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"account\",\"type\":\"address\"}],\"name\":\"addIssuer\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"id\",\"type\":\"string\"}],\"name\":\"queryAllById\",\"outputs\":[{\"name\":\"vhash\",\"type\":\"string\"},{\"name\":\"status\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"id\",\"type\":\"string\"}],\"name\":\"queryById\",\"outputs\":[{\"name\":\"vhash\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"id\",\"type\":\"string\"},{\"name\":\"s\",\"type\":\"uint256\"}],\"name\":\"updateStatus\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"id\",\"type\":\"string\"},{\"name\":\"h\",\"type\":\"string\"}],\"name\":\"updateHash\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"name\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"renounceIssuer\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"id\",\"type\":\"string\"},{\"name\":\"h\",\"type\":\"string\"}],\"name\":\"create\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"name\":\"name_\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"id\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"h\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"Newcontract\",\"type\":\"address\"}],\"name\":\"create_LOG\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"id\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"h\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"Newcontract\",\"type\":\"address\"}],\"name\":\"updateHash_LOG\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"id\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"s\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"Newcontract\",\"type\":\"address\"}],\"name\":\"updateStatus_LOG\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"account\",\"type\":\"address\"}],\"name\":\"IssuerAdded\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"account\",\"type\":\"address\"}],\"name\":\"IssuerRemoved\",\"type\":\"event\"}]";
    public static final String contract_report = "[{\"inputs\":[{\"internalType\":\"string\",\"name\":\"_name\",\"type\":\"string\"}],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"ManagerAdded\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"ManagerRemoved\",\"type\":\"event\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"addManager\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"key\",\"type\":\"string\"}],\"name\":\"getData\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"value\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"isManager\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"name\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"renounceManager\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"key\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"value\",\"type\":\"string\"}],\"name\":\"setData\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"components\":[{\"internalType\":\"string\",\"name\":\"key1\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"value1\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"key2\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"value2\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"key3\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"value3\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"key4\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"value4\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"key5\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"value5\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"key6\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"value6\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"key7\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"value7\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"key8\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"value8\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"key9\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"value9\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"key10\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"value10\",\"type\":\"string\"}],\"internalType\":\"struct ChainDataMgr.DataInfo\",\"name\":\"info\",\"type\":\"tuple\"},{\"internalType\":\"int256\",\"name\":\"listlength\",\"type\":\"int256\"}],\"name\":\"setDataByStruct\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";
    public static final String contract_evidence = "[{\"constant\":true,\"inputs\":[{\"name\":\"hash\",\"type\":\"string\"}],\"name\":\"getEvidence\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"hash\",\"type\":\"string\"},{\"name\":\"remark\",\"type\":\"string\"},{\"name\":\"reserve1\",\"type\":\"string\"},{\"name\":\"reserve2\",\"type\":\"string\"},{\"name\":\"reserve3\",\"type\":\"string\"}],\"name\":\"newEvidence\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"retCode\",\"type\":\"int256\"}],\"name\":\"newEvidenceEvent\",\"type\":\"event\"}]";
    public static final String contract_hello = "[{\"constant\":false,\"inputs\":[{\"name\":\"name_\",\"type\":\"string\"}],\"name\":\"setName\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"name\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"name\":\"name_\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"name\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"cnt\",\"type\":\"uint256\"}],\"name\":\"SetName\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"name\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"add\",\"type\":\"address\"}],\"name\":\"SetAddress\",\"type\":\"event\"}]";
    public static final String contract_ownable = "[{\"inputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"address\",\"name\":\"previousOwner\",\"type\":\"address\"},{\"indexed\":true,\"internalType\":\"address\",\"name\":\"newOwner\",\"type\":\"address\"}],\"name\":\"OwnershipTransferred\",\"type\":\"event\"},{\"inputs\":[],\"name\":\"owner\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"renounceOwnership\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"newOwner\",\"type\":\"address\"}],\"name\":\"transferOwnership\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";
    @Test
    public void testMethodIdV3() throws IOException {

        // load abi
        List<ABIDefinition> abiList = Web3Tools.loadContractDefinitionV3(contract_ownable);
        CryptoSuite cryptoSuite = web3ApiServiceV3.getCryptoSuite("group0", "10009_group0");

        // 在application.yml中切换sdk-encryptType的值
        if(cryptoSuite.cryptoTypeConfig == 1) {
            // encryptType = 1 guomi:
            System.out.println("guomi");
            for (ABIDefinition abiDefinition : abiList) {
                // support guomi sm3
                String buildMethodId = Web3Tools.buildMethodIdV3(abiDefinition, cryptoSuite);
                System.out.println(abiDefinition.getName() + ": " + abiDefinition.getType() + ": " + buildMethodId);
            }
        } else if(cryptoSuite.cryptoTypeConfig == 0) {
            System.out.println("standard");
            for (ABIDefinition abiDefinition : abiList) {
                // support guomi sm3
                String buildMethodId = Web3Tools.buildMethodIdV3(abiDefinition, cryptoSuite);
                System.out.println(abiDefinition.getName() + ": " + abiDefinition.getType() + ": " + buildMethodId);
            }
        }
    }

//    @Test
//    public void testGetAllMethod() throws IOException {
//        // 在application.yml中切换sdk-encryptType的值
//        CryptoSuite cryptoSuite = web3ApiService.getCryptoSuite("1", "1");
//        if(cryptoSuite.cryptoTypeConfig == 1) {
//            // encryptType = 1 guomi:
//            System.out.println("guomi");
//            System.out.println();
//        } else if(cryptoSuite.cryptoTypeConfig == 0) {
//            System.out.println("standard");
//            System.out.println();
//        }
//        getAllPreMethodId();
//    }
//
//    String a = "{\"constant\":true,\"inputs\":[{\"name\":\"addr\",\"type\":\"address\"}],\"name\":\"queryManager\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"address[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"}";
//    public void getAllPreMethodId() throws IOException {
//        CryptoSuite cryptoSuite = web3ApiService.getCryptoSuite("1", "1");
//        // load abi
//        List<ABIDefinition> sys_config = Web3Tools.loadContractDefinition(sys_config_Abi);
//        List<ABIDefinition> table_factory = Web3Tools.loadContractDefinition(table_factory_Abi);
//        List<ABIDefinition> crud = Web3Tools.loadContractDefinition(crud_Abi);
//        List<ABIDefinition> consensus = Web3Tools.loadContractDefinition(consensus_Abi);
//        List<ABIDefinition> cns = Web3Tools.loadContractDefinition(cns_Abi);
//        List<ABIDefinition> permission = Web3Tools.loadContractDefinition(permission_Abi);
//        List<ABIDefinition> chainGovernance = Web3Tools.loadContractDefinition(chain_governance_Abi);
//        List<ABIDefinition> contractLife = Web3Tools.loadContractDefinition(contract_life_cycle_Abi);
//
//        System.out.println("sys_config");
//        for (ABIDefinition abiDefinition : sys_config) {
//            if ("function".equals(abiDefinition.getType())) {
//                // support guomi sm3
//                String buildMethodId = Web3Tools.buildMethodId(abiDefinition, cryptoSuite);
//                System.out.println(abiDefinition.getName() + ": " + buildMethodId);
//            }
//        }
//        System.out.println("table_factory");
//        for (ABIDefinition abiDefinition : table_factory) {
//            if ("function".equals(abiDefinition.getType())) {
//                // support guomi sm3
//                String buildMethodId = Web3Tools.buildMethodId(abiDefinition, cryptoSuite);
//                System.out.println(abiDefinition.getName() + ": " + buildMethodId);
//            }
//        }
//        System.out.println("crud");
//        for (ABIDefinition abiDefinition : crud) {
//            if ("function".equals(abiDefinition.getType())) {
//                // support guomi sm3
//                String buildMethodId = Web3Tools.buildMethodId(abiDefinition, cryptoSuite);
//                System.out.println(abiDefinition.getName() + ": " + buildMethodId);
//            }
//        }
//        System.out.println("consensus");
//        for (ABIDefinition abiDefinition : consensus) {
//            if ("function".equals(abiDefinition.getType())) {
//                // support guomi sm3
//                String buildMethodId = Web3Tools.buildMethodId(abiDefinition, cryptoSuite);
//                System.out.println(abiDefinition.getName() + ": " + buildMethodId);
//            }
//        }
//        System.out.println("cns");
//        for (ABIDefinition abiDefinition : cns) {
//            if ("function".equals(abiDefinition.getType())) {
//                // support guomi sm3
//                String buildMethodId = Web3Tools.buildMethodId(abiDefinition, cryptoSuite);
//                System.out.println(abiDefinition.getName() + ": " + buildMethodId);
//            }
//        }
//        System.out.println("permission");
//        for (ABIDefinition abiDefinition : permission) {
//            if ("function".equals(abiDefinition.getType())) {
//                // support guomi sm3
//                String buildMethodId = Web3Tools.buildMethodId(abiDefinition, cryptoSuite);
//                System.out.println(abiDefinition.getName() + ": " + buildMethodId);
//            }
//        }
//        System.out.println("chainGovernance");
//        for (ABIDefinition abiDefinition : chainGovernance) {
//            if ("function".equals(abiDefinition.getType())) {
//                // support guomi sm3
//                String buildMethodId = Web3Tools.buildMethodId(abiDefinition, cryptoSuite);
//                System.out.println(abiDefinition.getName() + ": " + buildMethodId);
//            }
//        }
//        System.out.println("contractLife");
//        for (ABIDefinition abiDefinition : contractLife) {
//            if ("function".equals(abiDefinition.getType())) {
//                // support guomi sm3
//                String buildMethodId = Web3Tools.buildMethodId(abiDefinition, cryptoSuite);
//                System.out.println(abiDefinition.getName() + ": " + buildMethodId);
//            }
//        }
//    }
}
