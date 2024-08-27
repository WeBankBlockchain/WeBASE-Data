/*
 * Copyright 2014-2023 the original author or authors.
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
package com.webank.wsbcs.chdis.base.config;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.webank.wsbcs.chdis.base.code.ConstantCode;
import com.webank.wsbcs.chdis.base.exception.BaseException;
import com.webank.wsbcs.chdis.base.properties.ConstantProperties;
import com.webank.wsbcs.chdis.base.util.JsonUtils;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

/**
 * init web3sdk getService.
 */
@Data
@Log4j2
@Configuration
public class Web3Config {

    @Autowired
    ConstantProperties constantProperties;

    public static final String INSTANCE_V3 = "v3";
    /**
     * 证书所在目录，配置目录则修改此处
     */
    private static final String nodeSdkConfigToml = "node-sdk-config.toml";


    @Bean("tomlMap")
    public Map<String, List<String>> loadTomlPath() {
        Map<String, List<String>> instanceKeyMap = new HashMap<>(2);

        List<String> v3InstanceKeyList = getInstanceKeyList(constantProperties.getV3ConfigPath());
        if (!v3InstanceKeyList.isEmpty()) {
            instanceKeyMap.put(INSTANCE_V3, v3InstanceKeyList);
        }
        log.info("load toml result:{}", JsonUtils.objToString(instanceKeyMap));
        return instanceKeyMap;
    }

    /**
     *
     * @param configPath conf/cert-v2/ or conf/cert-v3/
     * @return
     */
    private static List<String> getInstanceKeyList(String configPath) {
        File v2ConfigFile = new File(configPath);
        File[] chainDirList = v2ConfigFile.listFiles();
        List<String> tomlPathList = new ArrayList<>();
        if (ArrayUtils.isNotEmpty(chainDirList)) {
            for (File dir : chainDirList) {
                // 目录名为链名字
                String tomlPath = dir.getName();
                // 目录中包含了证书和toml文件
                File[] fileList = dir.listFiles();
                // 目录非空才会读取
                if (ArrayUtils.isNotEmpty(fileList)) {
                    // 类型为目录，至少1个文件,一个toml （v3链不用证书）
                    if (dir.isDirectory() && fileList.length >= 1) {
                        // 至少包含toml文件，有则加入到list中
                        boolean flag = false;
                        for (File file : fileList) {
                            if (nodeSdkConfigToml.equals(file.getName())) {
                                flag = true;
                                break;
                            }
                        }
                        if (flag) {
                            // ex: tomlPath: tiger/businesschain
                            tomlPathList.add(tomlPath);
                        } else {
                            // throw new BaseException(-1, "not found node-sdk-config.toml in
                            // cert-v2/{chainId}/ directory");
                            log.error(
                                    "OnError: not found node-sdk-config.toml in directory of:{} in path:{}",
                                    tomlPath, configPath);
                        }
                    }
                }
            }
        }
        return tomlPathList;
    }

    @Bean(name = "bcosSDKV3")
    public Map<String, org.fisco.bcos.sdk.v3.BcosSDK> getBcosSDKV3(
            Map<String, List<String>> tomlMap) throws BaseException {
        log.info("start init ConfigProperty v3");
        Map<String, org.fisco.bcos.sdk.v3.BcosSDK> v3SdkMap = new ConcurrentHashMap<>();
        List<String> tomlPathList = tomlMap.get(INSTANCE_V3);
        // 1.2版本允许v3为空
        if (tomlPathList == null) {
            log.warn("not found v3 cert config in {}", constantProperties.getV3ConfigPath());
            throw new BaseException(-1, "not found v3 cert config");
        }
        for (String tomlPath : tomlPathList) {
            log.info("start init ConfigProperty path:{}", tomlPath);
            org.fisco.bcos.sdk.v3.BcosSDK bcosSDKV3 =
                    org.fisco.bcos.sdk.v3.BcosSDK.build(getChainConfigTomlFileNameV3(tomlPath));
            // init map
            v3SdkMap.put(tomlPath, bcosSDKV3);
            log.info("get v3SdkMap :{}", v3SdkMap.size());
        }
        return v3SdkMap;
    }

    @Bean(name = "v3ClientMap")
    public Map<String, Map<String, org.fisco.bcos.sdk.v3.client.Client>> getClientMapV3(
            Map<String, org.fisco.bcos.sdk.v3.BcosSDK> bcosSDKMap) throws BaseException {
        // init client(web3j)
        Map<String, Map<String, org.fisco.bcos.sdk.v3.client.Client>> v3ChainGroupClientMap =
                new ConcurrentHashMap<>();

        for (String chainId : bcosSDKMap.keySet()) {
            org.fisco.bcos.sdk.v3.BcosSDK bcosSDK = bcosSDKMap.get(chainId);
            if (bcosSDK == null) {
                throw new BaseException(ConstantCode.BCOS_SDK_INSTANTCE_IS_NULL_ERROR);
            }
            org.fisco.bcos.sdk.v3.client.Client rpcWeb3j =
                    org.fisco.bcos.sdk.v3.client.Client.build(bcosSDK.getConfig());
            List<String> groupIdList = rpcWeb3j.getGroupList().getResult().getGroupList();
            rpcWeb3j.stop();
            if (groupIdList == null || groupIdList.isEmpty()) {
                log.info("chainId:{}, group list is empty", chainId);
                continue;
            }
            Map<String, org.fisco.bcos.sdk.v3.client.Client> groupClientMap =
                    new ConcurrentHashMap<>();
            for (String groupId : groupIdList) {
                try {
                    org.fisco.bcos.sdk.v3.client.Client client = bcosSDK.getClient(groupId);
                    groupClientMap.put(groupId, client);
                    log.info("get v3GroupClientMap chainId:{}, groupId:{}", chainId, groupId);
                } catch (org.fisco.bcos.sdk.v3.BcosSDKException e) {
                    String errorMsg = e.getMessage();
                    log.error("bcosSDKMap getClient failed: {}", errorMsg);
                    // check client error type
                    if (errorMsg.contains("group not exist")) {
                        log.error("group: {} of the connected node not exist!", groupId);
                        throw new BaseException(ConstantCode.GROUPID_NOT_EXIST.getCode(),
                                "group: " + groupId + " of the connected node not exist!");
                    }
                    throw new BaseException(ConstantCode.BUILD_NEW_CLIENT_FAILED);
                }
            }
            log.info("get v3GroupClientMap chainId:{}, client size:{}", chainId,
                    groupClientMap.size());
            v3ChainGroupClientMap.put(chainId, groupClientMap);
        }
        log.info("get v3ChainGroupClientMap clientMap size:{}", v3ChainGroupClientMap.size());
        return v3ChainGroupClientMap;
    }

    @Bean(name = "rpcClientV3")
    public Map<String, org.fisco.bcos.sdk.v3.client.Client> getRpcWeb3jV3(
            Map<String, org.fisco.bcos.sdk.v3.BcosSDK> bcosSDKMap) throws BaseException {
        // init rpc client(web3j)
        Map<String, org.fisco.bcos.sdk.v3.client.Client> v3RpcClientMap = new ConcurrentHashMap<>();
        for (String key : bcosSDKMap.keySet()) {
            org.fisco.bcos.sdk.v3.BcosSDK bcosSDK = bcosSDKMap.get(key);
            if (bcosSDK == null) {
                throw new BaseException(ConstantCode.BCOS_SDK_INSTANTCE_IS_NULL_ERROR);
            }
            org.fisco.bcos.sdk.v3.client.Client rpcWeb3j =
                    org.fisco.bcos.sdk.v3.client.Client.build(bcosSDK.getConfig());
            v3RpcClientMap.put(key, rpcWeb3j);
        }
        log.info("get RpcWeb3jV3(only support rpc) client:{}", v3RpcClientMap.size());
        return v3RpcClientMap;
    }

    private String getChainConfigTomlFileNameV3(String tomlPath) {
        return constantProperties.getV3ConfigPath() + tomlPath + File.separator + nodeSdkConfigToml;
    }
}
