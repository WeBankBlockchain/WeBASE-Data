/**
 * Copyright 2014-2020 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.webank.webase.data.fetcher.group;

import com.webank.webase.data.fetcher.base.code.ConstantCode;
import com.webank.webase.data.fetcher.base.entity.BaseQueryParam;
import com.webank.webase.data.fetcher.base.enums.TableName;
import com.webank.webase.data.fetcher.base.exception.BaseException;
import com.webank.webase.data.fetcher.group.entity.BlockInfoDto;
import com.webank.webase.data.fetcher.group.entity.BlockListParam;
import com.webank.webase.data.fetcher.group.entity.ContractInfoDto;
import com.webank.webase.data.fetcher.group.entity.GroupGeneral;
import com.webank.webase.data.fetcher.group.entity.GroupInfoDto;
import com.webank.webase.data.fetcher.group.entity.NodeInfoDto;
import com.webank.webase.data.fetcher.group.entity.OrgInfoDto;
import com.webank.webase.data.fetcher.group.entity.TransListParam;
import com.webank.webase.data.fetcher.group.entity.TransactionInfoDto;
import com.webank.webase.data.fetcher.group.entity.TxnDailyDto;
import com.webank.webase.data.fetcher.group.entity.UserInfoDto;
import java.math.BigInteger;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * services for group data.
 */
@Log4j2
@Service
public class GroupService {

    @Autowired
    private GroupMapper groupMapper;

    /**
     * query count of group.
     */
    public int countOfGroup(Integer chainId, Integer groupId, Integer groupStatus)
            throws BaseException {
        try {
            Integer count = groupMapper.countOfGroup(chainId, groupId, groupStatus);
            return count == null ? 0 : count;
        } catch (RuntimeException ex) {
            log.error("fail countOfGroup", ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * query all group info.
     */
    public List<GroupInfoDto> getGroupList(Integer chainId, Integer groupId, Integer groupStatus)
            throws BaseException {
        try {
            List<GroupInfoDto> groupList = groupMapper.getGroupList(chainId, groupId, groupStatus);
            return groupList;
        } catch (RuntimeException ex) {
            log.error("fail getGroupList", ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * query Trading within seven days.
     */
    public List<TxnDailyDto> listSeventDayOfTrans(int chainId, int groupId) throws BaseException {
        try {
            // query
            List<TxnDailyDto> transList = groupMapper.listSeventDayOfTransDaily(chainId, groupId);
            return transList;
        } catch (RuntimeException ex) {
            log.error("fail listSeventDayOfTrans groupId:{}", groupId, ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * query group overview information.
     */
    public GroupGeneral queryGroupGeneral(Integer chainId, Integer groupId) throws BaseException {
        // check groupId
        checkGroupId(chainId, groupId);
        // getGeneral
        GroupGeneral generalInfo = groupMapper.getGeneral(chainId, groupId);
        BaseQueryParam queryParam = new BaseQueryParam();
        queryParam.setChainId(chainId);
        queryParam.setGroupId(groupId);
        generalInfo.setUserCount(countOfUser(queryParam));
        generalInfo.setContractCount(countOfContract(queryParam));
        return generalInfo;
    }
    
    /**
     * query count of all block counts.
     */
    public BigInteger getBlockCounts() throws BaseException {
        try {
            BigInteger count = groupMapper.getBlockCounts();
            return count == null ? BigInteger.ZERO : count;
        } catch (RuntimeException ex) {
            log.error("fail getBlockCounts. ", ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }
    
    /**
     * query count of all transaction counts.
     */
    public BigInteger getTxnCounts() throws BaseException {
        try {
            BigInteger count = groupMapper.getTxnCounts();
            return count == null ? BigInteger.ZERO : count;
        } catch (RuntimeException ex) {
            log.error("fail getTxnCounts. ", ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * query count of node.
     */
    public int countOfNode(BaseQueryParam queryParam) throws BaseException {
        try {
            Integer count = groupMapper.countOfNode(queryParam);
            return count == null ? 0 : count;
        } catch (RuntimeException ex) {
            log.error("fail countOfNode . queryParam:{}", queryParam, ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * query node list by page.
     */
    public List<NodeInfoDto> queryNodeList(BaseQueryParam queryParam) throws BaseException {
        try {
            List<NodeInfoDto> listOfNode = groupMapper.queryNodeList(queryParam);
            return listOfNode;
        } catch (RuntimeException ex) {
            log.error("fail queryNodeList. queryParam:{}", queryParam, ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * query count of org node.
     */
    public Integer countOfOrgNode(BaseQueryParam queryParam) throws BaseException {
        try {
            Integer nodeCount = groupMapper.getOrgNodeCount(queryParam);
            return nodeCount;
        } catch (RuntimeException ex) {
            log.error("fail countOfOrgNode . queryParam:{}", queryParam, ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * query org node list by page.
     */
    public List<OrgInfoDto> queryOrgNodeList(BaseQueryParam queryParam) throws BaseException {
        // query node list
        List<OrgInfoDto> listOfNode = groupMapper.getOrgNodeList(queryParam);
        return listOfNode;
    }

    /**
     * query count of block.
     */
    public int countOfBlock(BlockListParam queryParam) throws BaseException {
        try {
            Integer count = groupMapper.countOfBlock(
                    TableName.BLOCK.getTableName(queryParam.getChainId(), queryParam.getGroupId()),
                    queryParam);
            return count == null ? 0 : count;
        } catch (RuntimeException ex) {
            log.error("fail countOfBlock queryParam:{} ", queryParam, ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * query block info list.
     */
    public List<BlockInfoDto> queryBlockList(BlockListParam queryParam) throws BaseException {
        try {
            List<BlockInfoDto> listOfBlock = groupMapper.queryBlockList(
                    TableName.BLOCK.getTableName(queryParam.getChainId(), queryParam.getGroupId()),
                    queryParam);
            return listOfBlock;
        } catch (RuntimeException ex) {
            log.error("fail queryBlockList queryParam:{}", queryParam, ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * query count of trans.
     */
    public int countOfTrans(TransListParam queryParam) throws BaseException {
        try {
            Integer count = groupMapper.countOfTrans(
                    TableName.TRANS.getTableName(queryParam.getChainId(), queryParam.getGroupId()),
                    queryParam);
            return count == null ? 0 : count;
        } catch (RuntimeException ex) {
            log.error("fail countOfTrans.", ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * query trans list.
     */
    public List<TransactionInfoDto> queryTransList(TransListParam queryParam) throws BaseException {
        try {
            List<TransactionInfoDto> listOfTran = groupMapper.queryTransList(
                    TableName.TRANS.getTableName(queryParam.getChainId(), queryParam.getGroupId()),
                    TableName.RECEIPT.getTableName(queryParam.getChainId(),
                            queryParam.getGroupId()),
                    queryParam);
            return listOfTran;
        } catch (RuntimeException ex) {
            log.error("fail queryBlockList.", ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * query count of user.
     */
    public int countOfUser(BaseQueryParam queryParam) throws BaseException {
        try {
            Integer count = groupMapper.countOfUser(
                    TableName.PARSER.getTableName(queryParam.getChainId(), queryParam.getGroupId()),
                    queryParam);
            return count == null ? 0 : count;
        } catch (RuntimeException ex) {
            log.error("fail countOfTrans.", ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * query user list.
     */
    public List<UserInfoDto> queryUserList(BaseQueryParam queryParam) throws BaseException {
        try {
            List<UserInfoDto> listOfTran = groupMapper.queryUserList(
                    TableName.PARSER.getTableName(queryParam.getChainId(), queryParam.getGroupId()),
                    queryParam);
            return listOfTran;
        } catch (RuntimeException ex) {
            log.error("fail queryBlockList.", ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * query count of contract.
     */
    public int countOfContract(BaseQueryParam queryParam) throws BaseException {
        try {
            Integer count = groupMapper.countOfContract(
                    TableName.PARSER.getTableName(queryParam.getChainId(), queryParam.getGroupId()),
                    queryParam);
            return count == null ? 0 : count;
        } catch (RuntimeException ex) {
            log.error("fail countOfTrans.", ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * query contract list.
     */
    public List<ContractInfoDto> queryContractList(BaseQueryParam queryParam) throws BaseException {
        try {
            List<ContractInfoDto> listOfTran = groupMapper.queryContractList(
                    TableName.PARSER.getTableName(queryParam.getChainId(), queryParam.getGroupId()),
                    queryParam);
            return listOfTran;
        } catch (RuntimeException ex) {
            log.error("fail queryBlockList.", ex);
            throw new BaseException(ConstantCode.DB_EXCEPTION);
        }
    }

    /**
     * Check the validity of the groupId.
     */
    public void checkGroupId(Integer chainId, Integer groupId) throws BaseException {
        Integer groupCount = countOfGroup(chainId, groupId, null);
        if (groupCount == null || groupCount == 0) {
            throw new BaseException(ConstantCode.INVALID_GROUP_ID);
        }
    }
}
