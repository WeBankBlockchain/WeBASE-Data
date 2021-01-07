/*
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

package com.webank.webase.data.collect.base.tools;

import java.util.ArrayList;
import java.util.List;
import org.fisco.bcos.web3j.protocol.core.methods.response.AbiDefinition;
import org.fisco.bcos.web3j.protocol.core.methods.response.AbiDefinition.NamedType;
import org.fisco.bcos.web3j.tx.txdecode.ConstantProperties;

/**
 * AbiUtil.
 */
public class AbiUtil {

    /**
     * get constructor abi info.
     * 
     * @param contractAbi contractAbi
     * @return
     */
    public static AbiDefinition getAbiDefinition(String contractAbi) {
        List<AbiDefinition> abiArr =
                JacksonUtils.toJavaObjectList(contractAbi, AbiDefinition.class);
        AbiDefinition result = null;
        for (AbiDefinition abiDefinition : abiArr) {
            if (ConstantProperties.TYPE_CONSTRUCTOR.equals(abiDefinition.getType())) {
                result = abiDefinition;
                break;
            }
        }
        return result;
    }

    /**
     * get function abi info.
     * 
     * @param name name
     * @param contractAbi contractAbi
     * @return
     */
    public static AbiDefinition getAbiDefinition(String name, String contractAbi) {
        List<AbiDefinition> abiArr =
                JacksonUtils.toJavaObjectList(contractAbi, AbiDefinition.class);
        AbiDefinition result = null;
        for (AbiDefinition abiDefinition : abiArr) {
            if (ConstantProperties.TYPE_FUNCTION.equals(abiDefinition.getType())
                    && name.equals(abiDefinition.getName())) {
                result = abiDefinition;
                break;
            }
        }
        return result;
    }

    /**
     * get event abi info.
     * 
     * @param contractAbi contractAbi
     * @return
     */
    public static List<AbiDefinition> getEventAbiDefinitions(String contractAbi) {
        List<AbiDefinition> abiArr =
                JacksonUtils.toJavaObjectList(contractAbi, AbiDefinition.class);
        List<AbiDefinition> result = new ArrayList<>();
        for (AbiDefinition abiDefinition : abiArr) {
            if (ConstantProperties.TYPE_EVENT.equals(abiDefinition.getType())) {
                result.add(abiDefinition);
            }
        }
        return result;
    }
    
    /**
     * get event abi info.
     * 
     * @param contractAbi contractAbi
     * @return
     */
    public static List<AbiDefinition> getEventAbiDefinitions(String name, String contractAbi) {
        List<AbiDefinition> abiArr =
                JacksonUtils.toJavaObjectList(contractAbi, AbiDefinition.class);
        List<AbiDefinition> result = new ArrayList<>();
        for (AbiDefinition abiDefinition : abiArr) {
            if (ConstantProperties.TYPE_EVENT.equals(abiDefinition.getType())
                    && name.equals(abiDefinition.getName())) {
                result.add(abiDefinition);
            }
        }
        return result;
    }

    /**
     * getFuncInputType.
     * 
     * @param abiDefinition abiDefinition
     * @return
     */
    public static List<String> getFuncInputType(AbiDefinition abiDefinition) {
        List<String> inputList = new ArrayList<>();
        if (abiDefinition != null) {
            List<NamedType> inputs = abiDefinition.getInputs();
            for (NamedType input : inputs) {
                inputList.add(input.getType());
            }
        }
        return inputList;
    }
}
