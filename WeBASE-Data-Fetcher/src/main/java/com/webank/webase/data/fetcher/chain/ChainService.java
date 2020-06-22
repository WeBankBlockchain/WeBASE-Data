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
package com.webank.webase.data.fetcher.chain;

import com.webank.webase.data.fetcher.base.code.ConstantCode;
import com.webank.webase.data.fetcher.base.exception.BaseException;
import com.webank.webase.data.fetcher.chain.entity.ChainInfoDto;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * service of chain.
 */
@Log4j2
@Service
public class ChainService {

    @Autowired
    private ChainMapper chainMapper;

    /**
     * get chain count
     */
    public int getChainCount() {
        try {
            Integer count = chainMapper.getCount();
            return count == null ? 0 : count;
        } catch (RuntimeException ex) {
            log.error("fail getChainCount", ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * get chain list
     */
    public List<ChainInfoDto> getChainList() {
        try {
            return chainMapper.getList();
        } catch (RuntimeException ex) {
            log.error("fail getChainList", ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }
}
