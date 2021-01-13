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
package com.webank.webase.data.collect.gas;

import com.webank.webase.data.collect.dao.entity.TbGas;
import com.webank.webase.data.collect.dao.entity.TbGasExample;
import com.webank.webase.data.collect.dao.mapper.TbGasMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * service of gas.
 */
@Service
public class GasService {

    @Autowired
    private TbGasMapper tbGasMapper;

    /**
     * save gas
     */
    public void saveGas(TbGas tbGas) {
        tbGasMapper.insert(tbGas);
    }

    /**
     * get gas count
     */
    public int getGasCount(TbGasExample example) {
        Integer count = (int) tbGasMapper.countByExample(example);
        return count == null ? 0 : count;
    }

    /**
     * get gas list
     */
    public List<TbGas> getGasList(TbGasExample example) {
        return tbGasMapper.selectByExample(example);
    }
}
