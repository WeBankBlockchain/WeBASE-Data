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
package com.webank.webase.data.collect.event;

import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.enums.TableName;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.base.properties.ConstantProperties;
import com.webank.webase.data.collect.base.tools.AbiUtil;
import com.webank.webase.data.collect.base.tools.JacksonUtils;
import com.webank.webase.data.collect.base.tools.MybatisExampleTools;
import com.webank.webase.data.collect.contract.ContractService;
import com.webank.webase.data.collect.contract.entity.TbContract;
import com.webank.webase.data.collect.dao.entity.TbEventExportTask;
import com.webank.webase.data.collect.dao.entity.TbEventExportTaskExample;
import com.webank.webase.data.collect.dao.entity.TbEventInfo;
import com.webank.webase.data.collect.dao.entity.TbEventInfoExample;
import com.webank.webase.data.collect.dao.mapper.TbEventExportTaskMapper;
import com.webank.webase.data.collect.dao.mapper.TbEventInfoMapper;
import com.webank.webase.data.collect.event.entity.EventExportInfo;
import com.webank.webase.data.collect.event.enums.TaskStatus;
import com.webank.webase.data.collect.parser.ParserService;
import com.webank.webase.data.collect.parser.entity.ParserEventInfo;
import com.webank.webase.data.collect.table.TableService;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import lombok.extern.log4j.Log4j2;
import org.fisco.bcos.web3j.protocol.core.methods.response.AbiDefinition;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * service of Keywords.
 */
@Log4j2
@Service
public class EventService {

    @Autowired
    private TbEventExportTaskMapper eventExportTaskMapper;
    @Autowired
    private TbEventInfoMapper tbEventInfoMapper;
    @Autowired
    private TableService tableService;
    @Autowired
    private ParserService parserService;
    @Autowired
    private ContractService contractService;
    @Autowired
    private ConstantProperties cProperties;

    /**
     * add a new eventExportInfo
     */
    @Transactional
    public TbEventExportTask newExportInfo(EventExportInfo eventExportInfo) {
        log.debug("start newExportInfo eventExportInfo:{}", eventExportInfo);
        // check param
        TbContract localContract = contractService.queryContractByAddress(
                eventExportInfo.getChainId(), eventExportInfo.getGroupId(),
                eventExportInfo.getContractName(), eventExportInfo.getContractAddress());
        if (Objects.isNull(localContract)) {
            throw new BaseException(ConstantCode.CONTRACT_NOT_EXISTS);
        }
        if (!eventExportInfo.getEventName().contains("\\(")) {
            throw new BaseException(ConstantCode.EVENT_NAME_FORMAT_ERROR);
        }
        List<AbiDefinition> eventAbis = AbiUtil.getEventAbiDefinitions(
                eventExportInfo.getEventName().split("\\(")[0], localContract.getContractAbi());
        if (CollectionUtils.isEmpty(eventAbis)) {
            throw new BaseException(ConstantCode.EVENT_NOT_EXISTS);
        }
        TbEventExportTask tbRecord = getExportInfoExample(null, null,
                eventExportInfo.getContractAddress(), eventExportInfo.getEventName());
        if (tbRecord != null) {
            throw new BaseException(ConstantCode.EVENT_EXISTS);
        }
        // copy attribute
        TbEventExportTask tbExportInfo = new TbEventExportTask();
        BeanUtils.copyProperties(eventExportInfo, tbExportInfo);
        tbExportInfo.setBlockNumber(BigInteger.ZERO);
        tbExportInfo.setCreateTime(new Date());
        tbExportInfo.setModifyTime(new Date());
        // save audit info
        int result = eventExportTaskMapper.insertSelective(tbExportInfo);
        if (result == 0) {
            log.warn("fail newExportInfo.");
            throw new BaseException(ConstantCode.SAVE_EVENT_FAIL);
        }
        // new event table
        tableService.newEvnetInfoTable(eventExportInfo.getChainId(), eventExportInfo.getGroupId(),
                tbExportInfo.getId());
        return getExportInfoById(tbExportInfo.getId());
    }

    /**
     * updateExportStatus
     */
    public TbEventExportTask updateExportStatus(Integer id, Integer status) {
        if (!checkExportInfoId(id)) {
            throw new BaseException(ConstantCode.EVENT_NOT_EXISTS);
        }
        if (!TaskStatus.isInclude(status)) {
            throw new BaseException(ConstantCode.INVALID_TASK_STATUS);
        }
        eventExportTaskMapper.updateStatus(id, status);
        return getExportInfoById(id);
    }

    /**
     * get eventExportInfo count
     */
    public int getExportInfoCount(TbEventExportTaskExample example) {
        Integer count = (int) eventExportTaskMapper.countByExample(example);
        return count == null ? 0 : count;
    }

    /**
     * get eventExportInfo list
     */
    public List<TbEventExportTask> getExportInfoList(TbEventExportTaskExample example) {
        return eventExportTaskMapper.selectByExample(example);
    }

    /**
     * get audit info
     */
    public TbEventExportTask getExportInfoById(Integer id) {
        return eventExportTaskMapper.selectByPrimaryKey(id);
    }

    /**
     * get audit info
     */
    public TbEventExportTask getExportInfoExample(Integer chainId, Integer groupId,
            String contractAddress, String eventName) {
        TbEventExportTaskExample example =
                MybatisExampleTools.initSampleExample(TbEventExportTaskExample.class,
                        new TbEventExportTask(chainId, groupId, contractAddress, eventName));
        Optional<TbEventExportTask> tbRecord = eventExportTaskMapper.getOneByExample(example);
        if (tbRecord.isPresent()) {
            return tbRecord.get();
        }
        return null;
    }

    /**
     * remove eventExportInfo
     */
    @Transactional
    public void removeExportInfo(Integer id) {
        TbEventExportTask tbEventExportTask = getExportInfoById(id);
        if (tbEventExportTask == null) {
            throw new BaseException(ConstantCode.EVENT_NOT_EXISTS);
        }
        eventExportTaskMapper.deleteByPrimaryKey(id);
        // drop event table
        tableService.dropTableByName(
                TableName.EVENT.getEventInfoTableName(tbEventExportTask.getChainId(),
                        tbEventExportTask.getGroupId(), tbEventExportTask.getId()));
    }

    @Async("asyncExecutor")
    public void exportProcess(CountDownLatch latch, TbEventExportTask tbEventExportTask) {
        int taskId = tbEventExportTask.getId();
        int chainId = tbEventExportTask.getChainId();
        int groupId = tbEventExportTask.getGroupId();
        String contractAddress = tbEventExportTask.getContractAddress();
        String eventName = tbEventExportTask.getEventName();
        log.info("start exportProcess. taskId:{} contractAddress:{} eventName:{}", taskId,
                contractAddress, eventName);
        try {
            Instant startTimem = Instant.now();
            Long useTimeSum = 0L;
            do {
                BigInteger blockNumber = eventExportTaskMapper.getBlockNumber(taskId);
                List<ParserEventInfo> eventInfoList = parserService.queryEventInfoList(chainId,
                        groupId, contractAddress, blockNumber, eventName);
                if (CollectionUtils.isEmpty(eventInfoList)) {
                    return;
                }
                eventInfoList.stream()
                        .forEach(eventInfo -> saveEventInfo(tbEventExportTask, eventInfo));

                // export useTime
                useTimeSum = Duration.between(startTimem, Instant.now()).toMillis();
            } while (useTimeSum < cProperties.getDataParserTaskFixedDelay());
        } catch (Exception ex) {
            log.error("fail exportProcess. taskId:{} ", taskId, ex);
        } finally {
            if (Objects.nonNull(latch)) {
                latch.countDown();
            }
        }
        log.info("end exportProcess. taskId:{} contractAddress:{} eventName:{}", taskId,
                contractAddress, eventName);
    }

    @Transactional
    public void saveEventInfo(TbEventExportTask tbEventExportTask, ParserEventInfo eventInfo) {
        String tableName = TableName.EVENT.getEventInfoTableName(tbEventExportTask.getChainId(),
                tbEventExportTask.getGroupId(), tbEventExportTask.getId());
        Map<String, Object> resultEntityMap = JacksonUtils.toMap(eventInfo.getLogs());
        String events =
                JacksonUtils.toJSONString(resultEntityMap.get(tbEventExportTask.getEventName()));
        TbEventInfo tbEventInfo = new TbEventInfo();
        BeanUtils.copyProperties(eventInfo, tbEventInfo);
        tbEventInfo.setDynamicTableName(tableName);
        tbEventInfo.setContractName(tbEventExportTask.getContractName());
        tbEventInfo.setEventInfo(events);
        tbEventInfo.setCreateTime(new Date());
        tbEventInfo.setModifyTime(new Date());
        // save
        tbEventInfoMapper.insert(tbEventInfo);
        // update task block number
        eventExportTaskMapper.updateBlockNumber(tbEventExportTask.getId(),
                eventInfo.getBlockNumber());
    }

    /**
     * get eventInfo count
     */
    public int getEventInfoCount(String tableName, TbEventInfoExample example) {
        Integer count = (int) tbEventInfoMapper.countByExample(tableName, example);
        return count == null ? 0 : count;
    }

    /**
     * get eventInfo list
     */
    public List<TbEventInfo> getExportInfoList(String tableName, TbEventInfoExample example) {
        return tbEventInfoMapper.selectByExampleWithBLOBs(tableName, example);
    }

    /**
     * check eventExportInfo id
     */
    private boolean checkExportInfoId(Integer id) {
        TbEventExportTask tbEventExportTask = getExportInfoById(id);
        if (tbEventExportTask == null) {
            return false;
        }
        return true;
    }

}
