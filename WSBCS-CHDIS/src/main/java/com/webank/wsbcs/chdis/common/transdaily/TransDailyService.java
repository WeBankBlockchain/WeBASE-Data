/**
 * Copyright 2014-2021 the original author or authors.
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
package com.webank.wsbcs.chdis.common.transdaily;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.webank.wsbcs.chdis.base.code.ConstantCode;
import com.webank.wsbcs.chdis.base.entity.chain.Trans15Days;
import com.webank.wsbcs.chdis.base.exception.BaseException;
import com.webank.wsbcs.chdis.base.util.JsonUtils;
import com.webank.wsbcs.chdis.common.dao.entity.TbTransDaily;
import com.webank.wsbcs.chdis.common.dao.mapper.TbTransDailyMapper;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class TransDailyService {

    @Autowired
    private TbTransDailyMapper tbTransDailyMapper;

    /**
     * query Trading within seven days. 没有交易则是空
     */
    public List<Trans15Days> listSevenDayOfTrans(String chainId, String groupId, String bizSeqNo)
            throws BaseException {
        log.debug("start listSevenDayOfTrans chainId:{}|{}|{}", chainId, groupId, bizSeqNo);
        try {
            List<TbTransDaily> dailyList = tbTransDailyMapper.list15DaysOfTransDaily();
            List<Trans15Days> transList =
                    dailyList.stream().map(Trans15Days::new).collect(Collectors.toList());
            log.debug("end listSevenDayOfTrans transList:{}", JsonUtils.toJSONString(transList));
            return transList;
        } catch (RuntimeException ex) {
            log.error("fail listSevenDayOfTrans chainId:{}", chainId, ex);
            throw new BaseException(ConstantCode.DATABASE_EXCEPTION, bizSeqNo);
        }
    }

    /**
     * save trans daily info.
     */
    public void saveTbTransDailyInfo(String chainId, String groupId, LocalDate transDay,
            Integer transCount, Long blockNumber) throws BaseException {
        try {
            TbTransDaily rowParam = new TbTransDaily(chainId, transDay, transCount, blockNumber);
            Date date = new Date();
            rowParam.setCreateTime(date);
            rowParam.setModifyTime(date);
            tbTransDailyMapper.insertDuplicateUpdate(rowParam);
        } catch (RuntimeException ex) {
            log.error(
                    "start saveTbTransDailyInfo chainId:{}|{} transDay:{} transCount:{} blockNumber:{}",
                    chainId, groupId, JsonUtils.toJSONString(transDay), transCount, blockNumber,
                    ex);
            throw new BaseException(ConstantCode.DATABASE_EXCEPTION);
        }
    }

    public TbTransDaily selectLatestStatisticalTrans() {
        return tbTransDailyMapper.selectLatestStatisticalTrans();
    }

}
