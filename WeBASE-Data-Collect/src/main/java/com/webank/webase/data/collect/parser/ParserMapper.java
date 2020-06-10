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
package com.webank.webase.data.collect.parser;

import com.webank.webase.data.collect.parser.entity.ContractParserResult;
import com.webank.webase.data.collect.parser.entity.PageTransInfo;
import com.webank.webase.data.collect.parser.entity.TbParser;
import com.webank.webase.data.collect.parser.entity.UnusualContractInfo;
import com.webank.webase.data.collect.parser.entity.UnusualUserInfo;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * user transaction parser.
 */
@Repository
public interface ParserMapper {

    void add(@Param("tableName") String tableName, @Param("parser") TbParser tbParser);

    int updateUnusualUser(@Param("tableName") String tableName, @Param("userName") String userName,
            @Param("address") String address);

    void updateUnusualContract(@Param("tableName") String tableName,
            @Param("result") ContractParserResult result);

    List<String> queryUnusualTxHashByBin(@Param("tableName") String tableName,
            @Param("contractBin") String contractBin);

    List<String> queryUnusualTxHashMethodId(@Param("tableName") String tableName,
            @Param("methodId") String methodId);

    List<TbParser> parserUserList(@Param("tableName") String tableName);

    List<TbParser> parserInterfaceList(@Param("tableName") String tableName,
            @Param("userName") String userName);

    Integer countOfParserTrans(Map<String, Object> queryParam);

    List<PageTransInfo> qureyTransCountList(Map<String, Object> queryParam);

    Integer countOfUnusualUser(@Param("tableName") String tableName,
            @Param("userName") String userName);

    List<UnusualUserInfo> listOfUnusualUser(Map<String, Object> queryParam);

    Integer countOfUnusualContract(@Param("tableName") String tableName,
            @Param("contractAddress") String contractAddress);

    List<UnusualContractInfo> listOfUnusualContract(Map<String, Object> queryParam);

    void rollback(@Param("tableName") String tableName, @Param("blockNumber") long blockNumber);
}
