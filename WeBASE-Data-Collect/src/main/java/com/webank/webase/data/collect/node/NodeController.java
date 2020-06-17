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
package com.webank.webase.data.collect.node;

import com.webank.webase.data.collect.base.code.ConstantCode;
import com.webank.webase.data.collect.base.controller.BaseController;
import com.webank.webase.data.collect.base.entity.BasePageResponse;
import com.webank.webase.data.collect.base.exception.BaseException;
import com.webank.webase.data.collect.node.entity.NodeParam;
import com.webank.webase.data.collect.node.entity.TbNode;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for node data.
 */
@Log4j2
@RestController
@RequestMapping("node")
public class NodeController extends BaseController {

    @Autowired
    private NodeService nodeService;

    /**
     * qurey node info list.
     */
    @GetMapping(value = "/nodeList/{chainId}/{groupId}/{pageNumber}/{pageSize}")
    public BasePageResponse queryNodeList(@PathVariable("chainId") Integer chainId,
            @PathVariable("groupId") Integer groupId,
            @PathVariable("pageNumber") Integer pageNumber,
            @PathVariable("pageSize") Integer pageSize,
            @RequestParam(value = "nodeId", required = false) String nodeId) throws BaseException {
        BasePageResponse pagesponse = new BasePageResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start queryNodeList.");

        // check node status before query
        nodeService.checkAndUpdateNodeStatus(chainId, groupId);

        // param
        NodeParam queryParam = new NodeParam();
        queryParam.setChainId(chainId);
        queryParam.setGroupId(groupId);
        queryParam.setNodeId(nodeId);
        queryParam.setPageSize(pageSize);
        Integer count = nodeService.countOfNode(queryParam);
        if (count != null && count > 0) {
            Integer start =
                    Optional.ofNullable(pageNumber).map(page -> (page - 1) * pageSize).orElse(null);
            queryParam.setStart(start);

            List<TbNode> listOfnode = nodeService.qureyNodeList(queryParam);
            pagesponse.setData(listOfnode);
            pagesponse.setTotalCount(count);
        }

        log.info("end queryNodeList useTime:{}",
                Duration.between(startTime, Instant.now()).toMillis());
        return pagesponse;
    }
}
