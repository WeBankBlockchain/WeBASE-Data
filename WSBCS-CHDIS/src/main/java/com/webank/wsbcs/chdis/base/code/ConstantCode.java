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
package com.webank.wsbcs.chdis.base.code;

import java.util.Arrays;
import java.util.List;

/**
 * AAAA-B-CCC. <br/>
 * A:system number. <br/>
 * B:error level <br/>
 * 0:system exception <br/>
 * 1:param exception <br/>
 * 2:business exception <br/>
 * C:error code <br/>
 */
public class ConstantCode {

    /* return success */
    public static final RetCode SUCCESS = RetCode.mark(0, "success");

    /* system exception */
    public static final RetCode SYSTEM_EXCEPTION = RetCode.mark(43780001, "system exception");
    public static final RetCode DATABASE_EXCEPTION = RetCode.mark(43780002, "database exception");
    public static final RetCode PARAM_EXCEPTION = RetCode.mark(43780003, "param exception");
    public static final RetCode REQUEST_CHAIN_EXCEPTION = RetCode.mark(43780004, "request chain exception");
    public static final RetCode REDIS_INIT_ERROR = RetCode.mark(43780005, "redis init error");
    public static final RetCode REDIS_REQUIRE_EXCEPTION = RetCode.mark(43780006, "redis require exception");
    
    /* param exception */
    public static final String BAC20ADDRESS_IS_EMPTY_STR = "{\"code\":43781001,\"msg\":\"bac20Address can not be empty\"}";
    public static final RetCode BAC20ADDRESS_IS_EMPTY = RetCode.mark(43781001, "bac20Address can not be empty");
    public static final String ZOMBIEADDRESS_IS_EMPTY = "{\"code\":43781002,\"msg\":\"zombieAddress can not be empty\"}";
    public static final RetCode ZOMBIEADDRESS_IS_EMPTY_STR = RetCode.mark(43781002, "zombieAddress can not be empty");
    public static final String MANAGEADDRESS_IS_EMPTY = "{\"code\":43781003,\"msg\":\"manageAddress can not be empty\"}";
    public static final RetCode MANAGEADDRESS_IS_EMPTY_STR = RetCode.mark(43781003, "manageAddress can not be empty");

    public static final RetCode INVALID_BAC20ADDRESS = RetCode.mark(43781101, "invalid bac20Address");
    public static final RetCode INVALID_ZOMBIEADDRESS = RetCode.mark(43781102, "invalid zombieAddress");
    public static final RetCode INVALID_MANAGEADDRESS = RetCode.mark(43781103, "invalid manageAddress");
    public static final RetCode INVALID_TYPE = RetCode.mark(43781104, "invalid type");
    public static final RetCode INVALID_USERADDRESS = RetCode.mark(43781105, "invalid userAddress");
    public static final RetCode INVALID_SEARCHVALUE = RetCode.mark(43781106, "invalid searchValue");
    public static final RetCode INVALID_BLOCKNUMBER = RetCode.mark(43781107, "search blockNumber is greater than sync blockNumber");
    public static final RetCode INVALID_TIME = RetCode.mark(43781108, "search time is greater than current time");
    public static final RetCode INVALID_DATE = RetCode.mark(43781109, "invalid date");
    public static final RetCode CONTRACTTYPE_ERROR = RetCode.mark(43781110, "contractType not BAC20");
    public static final RetCode CONFIG_NOT_SET = RetCode.mark(43781111, "rule config not set yet");

    /* business exception */
    public static final RetCode CHAIN_ID_NOT_EXIST = RetCode.mark(43782001, "this chainId not exist");
    public static final RetCode SYSTEM_ERROR_WEB3J_NULL = RetCode.mark(43782002, "web3j instance of groupId is null, please try again");
    public static final RetCode SYSTEM_ERROR_GROUP_LIST_EMPTY = RetCode.mark(43782003, "No group belongs to this groupId(node not belongs to this group)");
    public static final RetCode SYSTEM_ERROR_NODE_INACTIVE = RetCode.mark(43782004, "No available node to connect");
    public static final RetCode WEB3J_CLIENT_IS_NULL = RetCode.mark(43782005, "get web3j client failed!");
    public static final RetCode BCOS_SDK_INSTANTCE_IS_NULL_ERROR = RetCode.mark(43782006, "This bcosSDK instantce not exist");
    
    public static final RetCode SIGN_USERID_ERROR = RetCode.mark(43782007, "signUserId check failed");
    public static final RetCode DATA_SIGN_ERROR = RetCode.mark(43782008, "data request sign error");
    public static final RetCode REQUEST_SIGN_EXCEPTION = RetCode.mark(43782009, "request sign server exception");
    public static final RetCode INVALID_SIGN_USER_ID_OF_ENCRYPT_TYPE = RetCode.mark(43782010, "signUserId's encrypt type is not match with this group's encrypt type");
    
    // contract type param error
    public static final RetCode FAIL_PARSE_JSON = RetCode.mark(43782101, "Fail to parse json");
    public static final RetCode ABI_PARSE_ERROR = RetCode.mark(43782102, "abi parse error");
    public static final RetCode INVALID_ENCRYPT_TYPE = RetCode.mark(43782103, "invalid encrypt type");
    public static final RetCode FUNCTION_NOT_EXISTS = RetCode.mark(43782104, "function is not exists");
    public static final RetCode CONTRACT_TYPE_ENCODED_ERROR = RetCode.mark(43782105, "Unsupported contract param type to encoded");
    public static final RetCode CONTRACT_TYPE_DECODED_ERROR = RetCode.mark(43782106, "Unsupported contract param type to decoded");
    public static final RetCode CONTRACT_TYPE_PARAM_ERROR = RetCode.mark(43782107, "unable to create instance of type, check input params");
    public static final RetCode FUNC_PARAM_SIZE_NOT_MATCH = RetCode.mark(43782108, "contract funcParam size not match with ABI");
    public static final RetCode FUNC_PARAM_BYTES_SIZE_NOT_MATCH = RetCode.mark(43782109, "contract funcParam bytes array size not match");
    public static final RetCode IN_FUNCPARAM_ERROR = RetCode.mark(43782110, "contract funcParam is error");
    public static final RetCode INVALID_HASH = RetCode.mark(43782111, "invalid hash");
    public static final RetCode INVALID_CHAINID = RetCode.mark(43782112, "invalid chainId");
    public static final RetCode INVALID_CONTRACTADDRESS = RetCode.mark(43782113, "invalid contract address");
    public static final RetCode CONTRACT_ABI_EMPTY = RetCode.mark(43782114, "abiInfo cannot be empty");

    // web3
    public static final RetCode BUILD_NEW_CLIENT_FAILED = RetCode.mark(43782301, "Build client instance of new group failed");
    public static final RetCode GROUPID_NOT_EXIST = RetCode.mark(43782302, "groupId not exist");
    public static final RetCode CLIENT_NOT_CONNECTED_WITH_THIS_GROUP = RetCode.mark(43782303, "This group not connected with front's rpc peers");
    public static final RetCode BLOCK_NUMBER_ERROR = RetCode.mark(43782304, "request blockNumber is invalid(number greater than latest block height)");
    public static final RetCode BLOCK_HASH_ERROR = RetCode.mark(43782305, "request block pkHash not exist");
    public static final RetCode BLOCK_NUM_HASH_NOT_BOTH = RetCode.mark(43782306, "block number and block hash cannot both exist");
    public static final RetCode SEND_TO_CHAIN_RETURN_RECEIPT_NULL = RetCode.mark(43782307, "send to chain and get null receipt");
    public static final RetCode SEND_TO_CHAIN_RETURN_RECEIPT_ERROR = RetCode.mark(43782308, "send to chain and get error receipt");
    public static final RetCode SEND_TO_CHAIN_RECEIPT_TIMEOUT = RetCode.mark(43782309, "send to chain and get receipt timeout");
    public static final RetCode SEND_TO_CHAIN_RECEIPT_FUTURE_ERROR = RetCode.mark(43782310, "send to chain and get receipt future interrupted");
    public static final RetCode TX_RECEIPT_HASH_NOT_ON_CHAIN = RetCode.mark(43782311, "The receipt of this trans hash not exist on chain yet");
    public static final RetCode BLOCK_BEING_SYNC = RetCode.mark(43782312, "block being sync. please try again later.");
    // trans
    public static final RetCode IN_FUNCTION_ERROR = RetCode.mark(43782401, "request function is error");
    public static final RetCode CALL_CONTRACT_ERROR = RetCode.mark(43782402, "call contract constant method fail");
    public static final RetCode ENCODE_TX_JNI_ERROR = RetCode.mark(43782403, "encode transaction in jni fail!");

    private static final List<RetCode> CODE_NOT_ALARM_LIST = Arrays.asList(SYSTEM_EXCEPTION,
            DATABASE_EXCEPTION, SEND_TO_CHAIN_RETURN_RECEIPT_NULL, SEND_TO_CHAIN_RETURN_RECEIPT_ERROR);

    public static boolean isAlarmCode(int code) {
        long count = CODE_NOT_ALARM_LIST.stream().filter(c -> c.getCode()==code).count();
        return count > 0;
    }

}
