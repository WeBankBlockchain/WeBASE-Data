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
package com.webank.webase.data.collect.contract;

import com.webank.webase.data.collect.base.enums.ContractType;
import com.webank.webase.data.collect.contract.entity.Method;
import com.webank.webase.data.collect.contract.entity.MethodInfo;
import com.webank.webase.data.collect.contract.entity.NewMethodInput;
import com.webank.webase.data.collect.contract.entity.TbContract;
import com.webank.webase.data.collect.contract.entity.TbMethod;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MethodService {

    @Autowired
    private MethodMapper methodMapper;
    @Autowired
    private ContractService contractService;

    /**
     * save method info.
     */
    public void saveMethod(NewMethodInput newMethodInput) {
        TbContract contract =
                contractService.verifyContractIdExist(null, newMethodInput.getContractId(), null);
        List<Method> methodList = newMethodInput.getMethodList();
        TbMethod tbMethod = new TbMethod();
        BeanUtils.copyProperties(contract, tbMethod);
        // save each method
        for (Method method : methodList) {
            BeanUtils.copyProperties(method, tbMethod);
            methodMapper.add(tbMethod);
        }
    }

    /**
     * query by methodId.
     */
    public MethodInfo getByMethodId(String methodId, Integer chainId, Integer groupId) {
        MethodInfo methodInfo = methodMapper.getMethodById(methodId, null, null);
        if (Objects.nonNull(methodInfo)) {
            if (ContractType.SYSTEMCONTRACT.getValue() == methodInfo.getContractType().intValue()) {
                return methodInfo;
            } else {
                return methodMapper.getMethodById(methodId, chainId, groupId);
            }
        }
        return null;
    }

    /**
     * removeByChainIdAndGroupId.
     */
    public void removeByChainIdAndGroupId(Integer chainId, Integer groupId) {
        methodMapper.removeByChainIdAndGroupId(chainId, groupId);
    }

}
