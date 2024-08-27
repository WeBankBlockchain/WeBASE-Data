/**
 * Copyright 2014-2023 the original author or authors.
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
package com.webank.wsbcs.chdis.common.chain;

import java.io.IOException;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.RevertMessageParser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.webank.wsbcs.chdis.base.code.ConstantCode;
import com.webank.wsbcs.chdis.base.controller.BaseController;
import com.webank.wsbcs.chdis.base.entity.BasePageResponse;
import com.webank.wsbcs.chdis.base.entity.BaseResponse;
import com.webank.wsbcs.chdis.base.entity.chain.Trans15Days;
import com.webank.wsbcs.chdis.base.entity.data.BlockListParam;
import com.webank.wsbcs.chdis.base.entity.data.TransListParam;
import com.webank.wsbcs.chdis.base.entity.trans.TransResponse;
import com.webank.wsbcs.chdis.base.entity.trans.TransResultDto;
import com.webank.wsbcs.chdis.base.entity.web3.RspBlockInfo;
import com.webank.wsbcs.chdis.base.entity.web3.RspBlockInfo.JsonTransactionResponse;
import com.webank.wsbcs.chdis.base.exception.BaseException;
import com.webank.wsbcs.chdis.base.properties.ConstantProperties;
import com.webank.wsbcs.chdis.base.util.JsonUtils;
import com.webank.wsbcs.chdis.common.dao.entity.TbBlock;
import com.webank.wsbcs.chdis.common.dao.entity.TbContractInfo;
import com.webank.wsbcs.chdis.common.dao.entity.TbMethod;
import com.webank.wsbcs.chdis.common.dao.entity.TbTrans;
import com.webank.wsbcs.chdis.common.method.MethodService;
import com.webank.wsbcs.chdis.common.transdaily.TransDailyService;
import com.webank.wsbcs.chdis.v3.data.BlockServiceV3;
import com.webank.wsbcs.chdis.v3.data.TransDataServiceV3;
import com.webank.wsbcs.chdis.v3.web3api.Web3ApiServiceV3;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;

/**
 * chain controller
 */
@Log4j2
@RestController
@RequestMapping("chain")
@Api(value = "chain")
public class ChainController extends BaseController {

    @Autowired
    ChainService chainService;
    @Autowired
    Web3ApiServiceV3 web3ApiServiceV3;
    @Autowired
    BlockServiceV3 blockServiceV3;
    @Autowired
    TransDataServiceV3 transDataServiceV3;
    @Autowired
    MethodService methodService;
    @Autowired
    TransDailyService transDailyService;
    @Autowired
    private ConstantProperties cProperties;

    @ApiOperation(value = "查询15天交易数据")
    @GetMapping("/trans15Days")
    public BaseResponse trans15Days(
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo)
            throws BaseException {
        String chainId = cProperties.getDefaultChainId();
        List<Trans15Days> result = transDailyService.listSevenDayOfTrans(chainId, null, bizSeqNo);
        return BaseResponse.success(result, bizSeqNo);
    }

    @ApiOperation(value = "查询单链概览")
    @GetMapping("/general")
    public BaseResponse general(
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo)
            throws BaseException {
        String chainId = cProperties.getDefaultChainId();
        return chainService.general(chainId, bizSeqNo);
    }

    @ApiOperation(value = "搜索交易哈希/块高")
    @GetMapping("/search/{input}")
    public BaseResponse search(@PathVariable("input") String input,
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo)
            throws BaseException {
        String chainId = cProperties.getDefaultChainId();
        Object result = web3ApiServiceV3.searchByCriteria(
                chainId.split(ConstantProperties.SPLIT_UNDERLINE)[1], chainId, input, bizSeqNo);
        return BaseResponse.success(result, bizSeqNo);
    }

    /**
     * query block list.
     */
    @ApiOperation(value = "查询区块列表")
    @GetMapping(value = "/blockList/{pageNumber}/{pageSize}")
    public BasePageResponse queryBlockList(@PathVariable("pageNumber") Integer pageNumber,
            @PathVariable("pageSize") Integer pageSize,
            @RequestParam(value = "pkHash", required = false) String pkHash,
            @RequestParam(value = "blockNumber", required = false) BigInteger blockNumber,
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo)
            throws BaseException {
        BasePageResponse pageResponse = new BasePageResponse(ConstantCode.SUCCESS, bizSeqNo);
        Instant startTime = Instant.now();
        if (StringUtils.isNotBlank(pkHash) && blockNumber != null) {
            throw new BaseException(ConstantCode.BLOCK_NUM_HASH_NOT_BOTH, bizSeqNo);
        }
        String chainId = cProperties.getDefaultChainId();
        String groupId = chainId.split(ConstantProperties.SPLIT_UNDERLINE)[1];
        if (StringUtils.isBlank(pkHash) && blockNumber == null) {
            BlockListParam queryParam = new BlockListParam();
            Integer start =
                    Optional.ofNullable(pageNumber).map(page -> (page - 1) * pageSize).orElse(0);
            queryParam.setStart(start);
            queryParam.setPageSize(pageSize);
            pageResponse =
                    blockServiceV3.queryBlockListFromChain(chainId, groupId, queryParam, bizSeqNo);
        } else {
            org.fisco.bcos.sdk.v3.client.protocol.response.BcosBlock.Block blockInfo = null;
            if (blockNumber != null) {
                log.info("did not find block, request from front. blockNumber:{} groupId:{}",
                        blockNumber, groupId);
                blockInfo = blockServiceV3.getBlockFromFrontByNumber(chainId, groupId, blockNumber);
            } else if (StringUtils.isNotBlank(pkHash)) {
                log.info("did not find block,request from front. pkHash:{} groupId:{}", pkHash,
                        groupId);
                try {
                    blockInfo = blockServiceV3.getBlockFromFrontByHash(chainId, groupId, pkHash);
                } catch (BaseException e) {
                    log.error("queryBlockList did not find block from front(chain).e:[]", e);
                    pageResponse.setData(null);
                    pageResponse.setTotalCount(0);
                }
            }
            if (blockInfo != null) {
                TbBlock tbBlock = BlockServiceV3.chainBlock2TbBlock(blockInfo);
                tbBlock.setChainId(chainId);
                pageResponse.setData(new TbBlock[] {tbBlock});
                pageResponse.setTotalCount(1);
            }
        }

        log.debug("end queryBlockList useTime:{} result:{}",
                Duration.between(startTime, Instant.now()).toMillis(),
                JsonUtils.toJSONString(pageResponse));
        return pageResponse;
    }

    @ApiOperation(value = "查询区块高度")
    @GetMapping("/getBlockNumber")
    public BaseResponse getBlockNumber(
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo)
            throws BaseException {
        String chainId = cProperties.getDefaultChainId();
        BigInteger result = web3ApiServiceV3
                .getBlockNumber(chainId.split(ConstantProperties.SPLIT_UNDERLINE)[1], chainId);
        return BaseResponse.success(result, bizSeqNo);
    }

    @ApiOperation(value = "查询同步状态")
    @GetMapping("/getSyncStatus")
    public BaseResponse getSyncStatus(
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo)
            throws BaseException {
        String chainId = cProperties.getDefaultChainId();
        Object result = web3ApiServiceV3
                .getSyncStatus(chainId.split(ConstantProperties.SPLIT_UNDERLINE)[1], chainId);
        return BaseResponse.success(result, bizSeqNo);
    }

    @ApiOperation(value = "查询区块详情")
    @GetMapping("/getBlockByNumber/{blockNumber}")
    public BaseResponse getBlockByNumber(@PathVariable("blockNumber") BigInteger blockNumber,
            @RequestParam(value = "pageNumber", defaultValue = "1",
                    required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "5",
                    required = false) Integer pageSize,
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo)
            throws BaseException {
        String chainId = cProperties.getDefaultChainId();
        RspBlockInfo result = web3ApiServiceV3.getRspBlockInfoByNumber(
                chainId.split(ConstantProperties.SPLIT_UNDERLINE)[1], chainId, blockNumber,
                pageNumber, pageSize);
        return BaseResponse.success(result, bizSeqNo);
    }

    /**
     * query trans list.
     */
    @ApiOperation(value = "查询交易列表")
    @GetMapping(value = "/transList/{pageNumber}/{pageSize}")
    public BasePageResponse queryTransList(@PathVariable("pageNumber") Integer pageNumber,
            @PathVariable("pageSize") Integer pageSize,
            @RequestParam(value = "transHash", required = false) String transHash,
            @RequestParam(value = "blockNumber", required = false) BigInteger blockNumber,
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo) {
        BasePageResponse pageResponse = new BasePageResponse(ConstantCode.SUCCESS, bizSeqNo);
        Instant startTime = Instant.now();
        String chainId = cProperties.getDefaultChainId();
        String groupId = chainId.split(ConstantProperties.SPLIT_UNDERLINE)[1];
        if (StringUtils.isBlank(transHash) && blockNumber == null) {
            TransListParam queryParam = new TransListParam();
            Integer start =
                    Optional.ofNullable(pageNumber).map(page -> (page - 1) * pageSize).orElse(0);
            queryParam.setStart(start);
            queryParam.setPageSize(pageSize);
            pageResponse = transDataServiceV3.getTransListFromChain(chainId, groupId, queryParam,
                    bizSeqNo);
        } else {
            List<TbTrans> transList = transDataServiceV3.getTransFromChain(chainId, groupId,
                    transHash, blockNumber, bizSeqNo);
            if (transList.size() > 0) {
                pageResponse.setData(transList);
                pageResponse.setTotalCount(transList.size());
            }
        }

        log.debug("end queryBlockList useTime:{} result:{}",
                Duration.between(startTime, Instant.now()).toMillis(),
                JsonUtils.toJSONString(pageResponse));
        return pageResponse;
    }

    @ApiOperation(value = "查询交易详情")
    @GetMapping("/transInfo/{transHash}")
    public BaseResponse transInfo(@PathVariable("transHash") String transHash,
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo)
            throws BaseException {
        TransResponse transResponse = new TransResponse();
        String chainId = cProperties.getDefaultChainId();
        JsonTransactionResponse result = transDataServiceV3.getTransaction(chainId,
                chainId.split(ConstantProperties.SPLIT_UNDERLINE)[1], transHash);
        if (result != null) {
            BeanUtils.copyProperties(result, transResponse);
        } else {
            return BaseResponse.success(null, bizSeqNo);
        }
        return BaseResponse.success(transResponse, bizSeqNo);
    }

    @ApiOperation(value = "查询交易回执详情")
    @GetMapping("/transactionReceipt/{transHash}")
    public BaseResponse transactionReceipt(@PathVariable("transHash") String transHash,
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo)
            throws BaseException {
        String chainId = cProperties.getDefaultChainId();
        TransResultDto result = transDataServiceV3.getTransReceipt(chainId,
                chainId.split(ConstantProperties.SPLIT_UNDERLINE)[1], transHash);
        return BaseResponse.success(result, bizSeqNo);
    }

    /**
     * query by methodId.
     */
    @ApiOperation(value = "查询方法ABI")
    @GetMapping(value = "methodAbi/{methodId}")
    public BaseResponse methodAbi(@PathVariable("methodId") String methodId,
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo) {
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS, bizSeqNo);
        TbMethod tbMethod = methodService.getByMethodId(methodId);
        baseResponse.setData(tbMethod);
        return baseResponse;
    }

    /**
     * query by methodId.
     */
    @ApiOperation(value = "查询方法ABI列表")
    @GetMapping(value = "methodAbiList")
    public BaseResponse methodAbiList(@RequestParam("methodId") String methodId,
            @RequestParam(value = "topicCnt", required = false) Integer topicCnt,
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo) {
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS, bizSeqNo);
        List<TbMethod> list = methodService.getMethodIdList(methodId, topicCnt, bizSeqNo);
        baseResponse.setData(list);
        return baseResponse;
    }

    /**
     * query contractAbi.
     * 
     * @throws IOException
     */
    @ApiOperation(value = "查询合约ABI")
    @GetMapping(value = "contractAbi")
    public BaseResponse contractAbi(@RequestParam(value = "contractAddress") String contractAddress,
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo)
            throws IOException {
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS, bizSeqNo);
        String chainId = cProperties.getDefaultChainId();
        String contractAbi = null;
        TbContractInfo tbContractInfo =
                chainService.findByChainIdAndContractAddress(chainId, contractAddress);
        if (tbContractInfo != null) {
            contractAbi = tbContractInfo.getContractAbi();
        } else {
            contractAbi = web3ApiServiceV3.getABI(
                    chainId.split(ConstantProperties.SPLIT_UNDERLINE)[1], chainId, contractAddress);
            if (StringUtils.isNotBlank(contractAbi)) {
                methodService.saveMethodFromAbiV3(chainId,
                        chainId.split(ConstantProperties.SPLIT_UNDERLINE)[1], contractAbi,
                        bizSeqNo);
                chainService.saveTbContractInfo(chainId, contractAddress, contractAbi);
            }
        }
        // baseResponse.setData(Web3Tools.loadContractDefinitionV3(abi));
        baseResponse.setData(contractAbi);
        return baseResponse;
    }

    /**
     * query contractBin.
     */
    @ApiOperation(value = "查询合约Bin")
    @GetMapping(value = "contractBin")
    public BaseResponse contractBin(@RequestParam(value = "contractAddress") String contractAddress,
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo) {
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS, bizSeqNo);
        String chainId = cProperties.getDefaultChainId();
        String result = web3ApiServiceV3.getCode(
                chainId.split(ConstantProperties.SPLIT_UNDERLINE)[1], chainId, contractAddress);
        baseResponse.setData(result);
        return baseResponse;
    }

    /**
     * revertMessageParser.
     */
    @ApiOperation(value = "查询交易失败输出信息")
    @GetMapping(value = "revertMessageParser")
    public BaseResponse revertMessageParser(@RequestParam(value = "output") String output,
            @RequestHeader(value = "bizSeqNo", required = false) String bizSeqNo) {
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS, bizSeqNo);
        String result = RevertMessageParser.tryResolveRevertMessage(16, output).getValue2();
        baseResponse.setData(result);
        return baseResponse;
    }

}
