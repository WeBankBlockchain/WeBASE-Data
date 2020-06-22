
# 接口说明

## 1 区块链管理模块

### 1.1 新增链信息

#### 1.1.1 传输协议规范

- 网络传输协议：使用HTTP协议
- 请求地址： **/chain/new**
- 请求方式：POST
- 请求头：Content-type: application/json
- 返回格式：JSON

#### 1.1.2 请求参数

***1）入参表***

| 序号 | 输入参数    | 类型   | 可为空 | 备注                       |
| ---- | ----------- | ------ | ------ | -------------------------- |
| 1    | chainId     | Int    | 否     | 链编号（1~9999）           |
| 2    | chainName   | String | 否     | 链名称                     |
| 3    | chainType   | Int    | 否     | 链类型（0-非国密，1-国密） |
| 4    | description | String | 是     | 备注                       |

***2）入参示例***

```
http://localhost:5009/WeBASE-Data-Collect/chain/new
```

```
{
    "chainId": 1,
    "chainName": "链一",
    "chainType": 0,
    "description": "test"
}
```

#### 1.1.3 返回参数

***1）出参表***

| 序号 | 输出参数    | 类型          |      | 备注                       |
| ---- | ----------- | ------------- | ---- | -------------------------- |
| 1    | code        | Int           | 否   | 返回码，0：成功 其它：失败 |
| 2    | message     | String        | 否   | 描述                       |
| 3    |             | Object        |      | 节点信息对象               |
| 3.1  | chainId     | Int           | 否   | 链编号                     |
| 3.2  | chainName   | String        | 否   | 链名称                     |
| 3.3  | chainType   | Int           | 否   | 链类型（0-非国密，1-国密） |
| 3.4  | description | String        | 是   | 备注                       |
| 3.5  | createTime  | LocalDateTime | 否   | 落库时间                   |
| 3.6  | modifyTime  | LocalDateTime | 否   | 修改时间                   |

***2）出参示例***

- 成功：

```
{
    "code": 0,
    "message": "success",
    "data": {
        "chainId": 1,
        "chainName": "链一",
        "chainType": 0,
        "description": "test"
        "createTime": "2019-02-14 17:47:00",
        "modifyTime": "2019-03-15 11:14:29"
    }
}
```

- 失败：

```
{
    "code": 202001,
    "message": "chain id already exists",
    "data": {}
}
```

### 1.2 获取所有链列表 

#### 1.2.1 传输协议规范

- 网络传输协议：使用HTTP协议
- 请求地址：**/chain/all**
- 请求方式：GET
- 返回格式：JSON

#### 1.2.2 请求参数

***1）入参表***

无

***2）入参示例***

```
http://localhost:5009/WeBASE-Data-Collect/chain/all
```

#### 1.2.3 返回参数 

***1）出参表***

| 序号  | 输出参数    | 类型          |      | 备注                       |
| ----- | ----------- | ------------- | ---- | -------------------------- |
| 1     | code        | Int           | 否   | 返回码，0：成功 其它：失败 |
| 2     | message     | String        | 否   | 描述                       |
| 3     | totalCount  | Int           | 否   | 总记录数                   |
| 4     | data        | List          | 否   | 组织列表                   |
| 4.1   |             | Object        |      | 节点信息对象               |
| 4.1.1 | chainId     | Int           | 否   | 链编号                     |
| 4.1.2 | chainName   | String        | 否   | 链名称                     |
| 4.1.3 | chainType   | Int           | 否   | 链类型（0-非国密，1-国密） |
| 4.1.4 | description | String        | 是   | 备注                       |
| 4.1.5 | createTime  | LocalDateTime | 否   | 落库时间                   |
| 4.1.6 | modifyTime  | LocalDateTime | 否   | 修改时间                   |

***2）出参示例***

- 成功：

```
{
    "code": 0,
    "message": "success",
    "data": [
        {
        "chainId": 1,
        "chainName": "链一",
        "chainType": 0,
        "description": "test"
        "createTime": "2019-02-14 17:47:00",
        "modifyTime": "2019-03-15 11:14:29"
    	}
    ],
    "totalCount": 1
}
```

- 失败：

```
{
   "code": 102000,
   "message": "system exception",
   "data": {}
}
```

### 1.3 删除链信息

#### 1.3.1 传输协议规范

- 网络传输协议：使用HTTP协议
- 请求地址：**/chain/{chainId}**
- 请求方式：DELETE
- 请求头：Content-type: application/json
- 返回格式：JSON

#### 1.3.2 请求参数

***1）入参表***

| 序号 | 输入参数 | 类型 | 可为空 | 备注   |
| ---- | -------- | ---- | ------ | ------ |
| 1    | chainId  | Int  | 否     | 链编号 |

***2）入参示例***

```
http://localhost:5009/WeBASE-Data-Collect/chain/1
```

#### 1.3.3 返回参数 

***1）出参表***

| 序号 | 输出参数 | 类型   |      | 备注                       |
| ---- | -------- | ------ | ---- | -------------------------- |
| 1    | code     | Int    | 否   | 返回码，0：成功 其它：失败 |
| 2    | message  | String | 否   | 描述                       |
| 3    | data     | object | 是   | 返回信息实体（空）         |

***2）出参示例***

- 成功：

```
{
    "code": 0,
    "data": {},
    "message": "success"
}
```

- 失败：

```
{
    "code": 202004,
    "message": "invalid chain id",
    "data": {}
}
```

## 2 前置管理模块

### 2.1 新增节点前置


#### 2.1.1 传输协议规范
* 网络传输协议：使用HTTP协议
* 请求地址： **/front/new**
* 请求方式：POST
* 请求头：Content-type: application/json
* 返回格式：JSON

#### 2.1.2 请求参数

***1）入参表***

| 序号 | 输入参数  | 类型   | 可为空 | 备注         |
| ---- | --------- | ------ | ------ | ------------ |
| 1    | chainId   | Int    | 否     | 链编号       |
| 2    | frontIp   | String | 否     | 前置ip       |
| 3    | frontPort | Int    | 否     | 前置服务端口 |
| 4    | agency    | Int    | 否     | 所属机构     |

***2）入参示例***

```
http://localhost:5009/WeBASE-Data-Collect/front/new
```

```
{
    "chainId": 1,
    "frontIp": "localhost",
    "frontPort": "5002",
    "agency": "test"
}
```


#### 2.1.3 返回参数

***1）出参表***

| 序号 | 输出参数   | 类型          | 可为空 | 备注                       |
| ---- | ---------- | ------------- | ------ | -------------------------- |
| 1    | code       | Int           | 否     | 返回码，0：成功 其它：失败 |
| 2    | message    | String        | 否     | 描述                       |
| 3    |            | Object        |        | 节点信息对象               |
| 3.1  | frontId    | Int           | 否     | 前置编号                   |
| 3.2  | chainId    | Int           | 否     | 链编号                     |
| 3.3  | frontIp    | String        | 否     | 前置ip                     |
| 3.4  | frontPort  | Int           | 否     | 前置端口                   |
| 3.5  | nodeId     | String        | 否     | 节点编号                   |
| 3.6  | agency     | String        | 否     | 所属机构                   |
| 3.7  | createTime | LocalDateTime | 是     | 落库时间                   |
| 3.8  | modifyTime | LocalDateTime | 是     | 修改时间                   |

***2）出参示例***
* 成功：
```
{
  "code": 0,
  "message": "success",
  "data": {
    "chainId": 1,
    "frontId": 1,
    "nodeId": "944607f7e83efe2ba72476dc39a269a910811db8caac34f440dd9c9dd8ec2490b8854b903bd6c9b95c2c79909649977b8e92097c2f3ec32232c4f655b5a01850",
    "frontIp": "localhost",
    "frontPort": 5002,
    "agency": "test",
    "createTime": null,
    "modifyTime": null
  }
}
```

* 失败：
```
{
    "code": 102000,
    "message": "system exception",
    "data": {}
}
```


### 2.2 获取所有前置列表 


#### 2.2.1 传输协议规范
* 网络传输协议：使用HTTP协议
* 请求地址：**/front/list?chainId={chainId}?frontId={frontId}&groupId={groupId}**
* 请求方式：GET
* 返回格式：JSON

#### 2.2.2 请求参数

***1）入参表***

| 序号 | 输入参数    | 类型          | 可为空 | 备注                 |
|------|-------------|---------------|--------|-------------------------------|
| 1 | chainId | Int | 是 | 链编号 |
| 2     | frontId       | Int           | 是     | 前置编号                  |
| 3    | groupId       | Int           | 是     | 群组编号                |


***2）入参示例***

```
http://localhost:5009/WeBASE-Data-Collect/front/list
```


#### 2.2.3 返回参数 

***1）出参表***

| 序号 | 输出参数    | 类型          |        | 备注                                       |
|------|-------------|---------------|--------|-------------------------------|
| 1     | code          | Int           | 否     | 返回码，0：成功 其它：失败 |
| 2     | message       | String        | 否     | 描述                       |
| 3     | totalCount    | Int           | 否     | 总记录数                   |
| 4     | data          | List          | 否     | 组织列表                   |
| 4.1   |               | Object        |        | 节点信息对象               |
| 4.1.1 | frontId       | Int           | 否     | 前置编号                   |
| 4.1.2 | chainId | Int | 否 | 链编号 |
| 4.1.3 | frontIp       | String        | 否     | 前置ip                     |
| 4.1.4 | frontPort     | Int           | 否     | 前置端口                   |
| 4.1.5 | nodeId | String | 否 | 节点编号 |
| 4.1.6 | agency | String | 否 | 所属机构 |
| 4.1.7 | createTime    | LocalDateTime | 否     | 落库时间                   |
| 4.1.8 | modifyTime    | LocalDateTime | 否     | 修改时间                   |

***2）出参示例***
* 成功：
```
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "chainId": 1,
      "frontId": 1,
      "nodeId": "944607f7e83efe2ba72476dc39a269a910811db8caac34f440dd9c9dd8ec2490b8854b903bd6c9b95c2c79909649977b8e92097c2f3ec32232c4f655b5a01850",
      "frontIp": "localhost",
      "frontPort": 5002,
      "agency": "test",
      "createTime": "2020-05-20 20:22:35",
      "modifyTime": "2020-05-20 20:22:35"
    }
  ],
  "totalCount": 1
}
```

* 失败：
```
{
   "code": 102000,
   "message": "system exception",
   "data": {}
}
```


### 2.3 删除前置信息

#### 2.3.1 传输协议规范
* 网络传输协议：使用HTTP协议
* 请求地址：**/front/{frontId}**
* 请求方式：DELETE
* 请求头：Content-type: application/json
* 返回格式：JSON

#### 2.3.2 请求参数

***1）入参表***

| 序号 | 输入参数    | 类型          | 可为空 | 备注                                       |
|------|-------------|---------------|--------|-------------------------------|
| 1    | frontId    | Int    | 否     | 前置编号                   |


***2）入参示例***

```
http://localhost:5009/WeBASE-Data-Collect/front/1
```


#### 2.3.3 返回参数 

***1）出参表***

| 序号 | 输出参数    | 类型          |        | 备注                                       |
|------|-------------|---------------|--------|-------------------------------|
| 1    | code       | Int    | 否     | 返回码，0：成功 其它：失败 |
| 2    | message    | String | 否     | 描述                       |
| 3    | data       | object | 是     | 返回信息实体（空）         |


***2）出参示例***
* 成功：
```
{
  "code": 0,
  "message": "success",
  "data": null
}
```

* 失败：
```
{
    "code": 102000,
    "message": "system exception",
    "data": {}
}
```

## 3 群组管理模块

### 3.1 获取群组列表

#### 3.1.1 传输协议规范

- 网络传输协议：使用HTTP协议
- 请求地址：**/group/list/{chainId}**
- 请求方式：GET
- 返回格式：JSON

#### 3.1.2 请求参数

***1）入参表***

无

***2）入参示例***

```
http://localhost:5009/WeBASE-Data-Collect/group/list/1
```

#### 3.1.3 返回参数 

***1）出参表***

| 序号  | 输出参数         | 类型          |      | 备注                       |
| ----- | ---------------- | ------------- | ---- | -------------------------- |
| 1     | code             | Int           | 否   | 返回码，0：成功 其它：失败 |
| 2     | message          | String        | 否   | 描述                       |
| 3     | totalCount       | Int           | 否   | 总记录数                   |
| 4     | data             | List          | 否   | 列表                       |
| 4.1   |                  | Object        |      | 信息对象                   |
| 4.1.1 | chainId          | Int           | 否   | 链编号                     |
| 4.1.2 | groupId          | Int           | 否   | 群组编号                   |
| 4.1.3 | groupName        | String        | 否   | 群组名称                   |
| 4.1.4 | genesisBlockHash | String        | 否   | 创世块hash                 |
| 4.1.5 | groupStatus      | Int           | 否   | 群组状态                   |
| 4.1.6 | nodeCount        | Int           | 否   | 节点个数                   |
| 4.1.7 | createTime       | LocalDateTime | 否   | 落库时间                   |
| 4.1.8 | modifyTime       | LocalDateTime | 否   | 修改时间                   |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "chainId": 1,
      "groupId": 1,
      "groupName": "group1",
      "genesisBlockHash": "0x7bc361d7d8e078ea9e8f352f2b856d6ea76ab1b9522f4b09853c861d0ed0779f",
      "groupStatus": 1,
      "nodeCount": 2,
      "createTime": "2020-05-20 20:22:35",
      "modifyTime": "2020-05-20 20:31:38"
    },
    {
      "chainId": 1,
      "groupId": 2,
      "groupName": "group2",
      "genesisBlockHash": "0x1208de0d47dcba9447d304039d1e4512dd4ce740ec408ef83c5f7ee2aefc7468",
      "groupStatus": 1,
      "nodeCount": 2,
      "createTime": "2020-05-20 20:22:36",
      "modifyTime": "2020-05-20 20:31:38"
    }
  ],
  "totalCount": 2
}
```

- 失败：

```
{
    "code": 102000,
    "message": "system exception",
    "data": {}
}
```

## 4 区块管理模块

### 4.1 查询区块列表

#### 4.1.1 传输协议规范

- 网络传输协议：使用HTTP协议
- 请求地址：**/block/list/{chainId}/{groupId}/{pageNumber}/{pageSize}}?blockHash={blockHash}&blockNumber={blockNumber}**
- 请求方式：GET
- 返回格式：JSON

#### 4.1.2 请求参数

***1）入参表***

| 序号 | 输入参数    | 类型       | 可为空 | 备注       |
| ---- | ----------- | ---------- | ------ | ---------- |
| 1    | chainId     | Int        | 否     | 链编号     |
| 2    | groupId     | Int        | 否     | 群组编号   |
| 3    | pageSize    | Int        | 否     | 每页记录数 |
| 4    | pageNumber  | Int        | 否     | 当前页码   |
| 5    | blockHash   | String     | 是     | 区块hash   |
| 6    | blockNumber | BigInteger | 是     | 块高       |

***2）入参示例***

```
http://localhost:5009/WeBASE-Data-Collect/block/list/1/1/1/2?blockHash=
```

#### 4.1.3 返回参数 

***1）出参表***

| 序号  | 输出参数       | 类型          |      | 备注                       |
| ----- | -------------- | ------------- | ---- | -------------------------- |
| 1     | code           | Int           | 否   | 返回码，0：成功 其它：失败 |
| 2     | message        | String        | 否   | 描述                       |
| 3     | totalCount     | Int           | 否   | 总记录数                   |
| 4     | data           | List          | 是   | 区块列表                   |
| 4.1   |                | Object        |      | 区块信息对象               |
| 4.1.1 | blockHash      | String        | 否   | 块hash                     |
| 4.1.2 | blockNumber    | BigInteger    | 否   | 块高                       |
| 4.1.3 | blockTimestamp | LocalDateTime | 否   | 出块时间                   |
| 4.1.4 | transCount     | Int           | 否   | 交易数                     |
| 4.1.5 | sealerIndex    | Int           | 否   | 打包节点索引               |
| 4.1.6 | sealer         | String        | 否   | 打包节点                   |
| 4.1.7 | createTime     | LocalDateTime | 否   | 创建时间                   |
| 4.1.8 | modifyTime     | LocalDateTime | 否   | 修改时间                   |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 26,
      "blockHash": "0x1d0a57a6ee2b73e537ef6d929c8d0bdb2a9799dd6357f04dc5f38e4e0c6c5ac2",
      "blockNumber": 35,
      "blockTimestamp": "2020-05-13 19:47:37",
      "transCount": 1,
      "sealerIndex": 0,
      "sealer": "944607f7e83efe2ba72476dc39a269a910811db8caac34f440dd9c9dd8ec2490b8854b903bd6c9b95c2c79909649977b8e92097c2f3ec32232c4f655b5a01850",
      "createTime": "2020-05-20 20:22:41",
      "modifyTime": "2020-05-20 20:22:41"
    },
    {
      "id": 8,
      "blockHash": "0x4c29bb921f4bf346ad1f92704e225f6323c85f16f2fa4eb0e3f126355ff9fa12",
      "blockNumber": 34,
      "blockTimestamp": "2020-05-13 19:12:20",
      "transCount": 1,
      "sealerIndex": 0,
      "sealer": "944607f7e83efe2ba72476dc39a269a910811db8caac34f440dd9c9dd8ec2490b8854b903bd6c9b95c2c79909649977b8e92097c2f3ec32232c4f655b5a01850",
      "createTime": "2020-05-20 20:22:41",
      "modifyTime": "2020-05-20 20:22:41"
    }
  ],
  "totalCount": 36
}
```

- 失败：

```
{
    "code": 102000,
    "message": "system exception",
    "data": {}
}
```

### 4.2 根据块高查询区块信息

#### 4.2.1 传输协议规范

- 网络传输协议：使用HTTP协议
- 请求地址：**/block/blockByNumber/{chainId}/{groupId}/{blockNumber}**
- 请求方式：GET
- 返回格式：JSON

#### 4.2.2 请求参数

***1）入参表***

| 序号 | 输入参数    | 类型       | 可为空 | 备注     |
| ---- | ----------- | ---------- | ------ | -------- |
| 1    | chainId     | Int        | 否     | 链编号   |
| 2    | groupId     | Int        | 否     | 群组编号 |
| 3    | blockNumber | BigInteger | 是     | 块高     |

***2）入参示例***

```
http://localhost:5009/WeBASE-Data-Collect/block/blockByNumber/1/1/1
```

#### 4.2.3 返回参数 

***1）出参表***

| 序号      | 输出参数            | 类型       |      | 备注                       |
| --------- | ------------------- | ---------- | ---- | -------------------------- |
| 1         | code                | Int        | 否   | 返回码，0：成功 其它：失败 |
| 2         | message             | String     | 否   | 描述                       |
| 3         |                     | Object     |      | 区块信息对象               |
| 3.1       | number              | BigInteger | 否   | 块高                       |
| 3.2       | hash                | String     | 否   | 区块hsah                   |
| 3.3       | parentHash          | String     | 否   | 父块hash                   |
| 3.4       | nonce               | String     | 否   | 随机数                     |
| 3.5       | sealer              | String     | 否   | 打包节点索                 |
| 3.6       | logsBloom           | String     | 否   | log的布隆过滤值            |
| 3.7       | transactionsRoot    | String     | 否   |                            |
| 3.8       | stateRoot           | String     | 否   |                            |
| 3.9       | difficulty          | String     | 否   |                            |
| 3.10      | totalDifficulty     | String     | 否   |                            |
| 3.11      | extraData           | String     | 否   |                            |
| 3.12      | size                | Int        | 否   |                            |
| 3.13      | gasLimit            | long       | 否   | 限制gas值                  |
| 3.14      | gasUsed             | long       | 否   | 已使用的gas值              |
| 3.15      | timestamp           | String     | 否   | 出块时间                   |
| 3.16      | gasLimitRaw         | String     | 否   |                            |
| 3.17      | timestampRaw        | String     | 否   |                            |
| 3.18      | gasUsedRaw          | String     | 否   |                            |
| 3.19      | numberRaw           | String     | 否   |                            |
| 3.20      | transactions        | List       | 否   |                            |
| 3.20.1    |                     | Object     |      | 交易信息对象               |
| 3.20.1.1  | hash                | String     | 否   | 交易hash                   |
| 3.20.1.2  | blockHash           | String     | 否   | 区块hash                   |
| 3.20.1.3  | blockNumber         | BigInteger | 否   | 所属块高                   |
| 3.20.1.4  | transactionIndex    | Int        | 否   | 在区块中的索引             |
| 3.20.1.5  | from                | String     | 否   | 交易发起者                 |
| 3.20.1.6  | to                  | String     | 否   | 交易目标                   |
| 3.20.1.7  | value               | String     | 否   |                            |
| 3.20.1.8  | gasPrice            | long       | 否   |                            |
| 3.20.1.9  | gas                 | long       | 否   |                            |
| 3.20.1.10 | input               | String     | 否   |                            |
| 3.20.1.11 | v                   | Int        | 否   |                            |
| 3.20.1.12 | nonceRaw            | String     | 否   |                            |
| 3.20.1.13 | blockNumberRaw      | String     | 否   |                            |
| 3.20.1.14 | transactionIndexRaw | String     | 否   |                            |
| 3.20.1.15 | gasPriceRaw         | String     | 否   |                            |
| 3.20.1.16 | gasRaw              | String     | 否   |                            |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": {
    "number": 1,
    "hash": "0xadcd64e3744c4b71eb628598c5455409be152e5d721d04da3503ffc169c322ca",
    "parentHash": "0x7bc361d7d8e078ea9e8f352f2b856d6ea76ab1b9522f4b09853c861d0ed0779f",
    "nonce": "0",
    "sealer": "0x1",
    "logsBloom": "0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "transactionsRoot": "0x710710af3bcf4ff06ab330a4eff36ce5da5e0d787d44a4431357e30cd585bc76",
    "stateRoot": "0x793cd1bcafc6cb16300eb62bd52c61c5d2f72ca4ff7087c158c1c5f2eb3074a8",
    "difficulty": 0,
    "totalDifficulty": 0,
    "extraData": [],
    "size": 0,
    "gasLimit": 0,
    "gasUsed": 0,
    "timestamp": "1587110719512",
    "gasLimitRaw": "0x0",
    "timestampRaw": "0x171872bb018",
    "gasUsedRaw": "0x0",
    "numberRaw": "0x1",
    "transactions": [
      {
        "hash": "0x72c8fd1724b308f596e373c432814f37b1058ac241ceaa4e19b1f0b22dfe63fb",
        "blockHash": "0xadcd64e3744c4b71eb628598c5455409be152e5d721d04da3503ffc169c322ca",
        "blockNumber": 1,
        "transactionIndex": 0,
        "from": "0x95824169cf64b29a9bcd5036fe0fa78ca0ecaa6f",
        "to": null,
        "value": 0,
        "gasPrice": 1,
        "gas": 100000000,
        "input": "0x608060405234801561001057600080fd5b506040805190810160405280600581526020017f48656c6c6f0000000000000000000000000000000000000000000000000000008152506000908051906020019061005c929190610062565b50610107565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106100a357805160ff19168380011785556100d1565b828001600101855582156100d1579182015b828111156100d05782518255916020019190600101906100b5565b5b5090506100de91906100e2565b5090565b61010491905b808211156101005760008160009055506001016100e8565b5090565b90565b610373806101166000396000f30060806040526004361061004c576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff168063299f7f9d146100515780633590b49f146100e1575b600080fd5b34801561005d57600080fd5b5061006661014a565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156100a657808201518184015260208101905061008b565b50505050905090810190601f1680156100d35780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156100ed57600080fd5b50610148600480360381019080803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091929192905050506101ec565b005b606060008054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156101e25780601f106101b7576101008083540402835291602001916101e2565b820191906000526020600020905b8154815290600101906020018083116101c557829003601f168201915b5050505050905090565b7f05432a43e07f36a8b98100b9cb3631e02f8e796b0a06813610ce8942e972fb81816040518080602001828103825283818151815260200191508051906020019080838360005b8381101561024e578082015181840152602081019050610233565b50505050905090810190601f16801561027b5780820380516001836020036101000a031916815260200191505b509250505060405180910390a1806000908051906020019061029e9291906102a2565b5050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106102e357805160ff1916838001178555610311565b82800160010185558215610311579182015b828111156103105782518255916020019190600101906102f5565b5b50905061031e9190610322565b5090565b61034491905b80821115610340576000816000905550600101610328565b5090565b905600a165627a7a72305820663f556d1fabbdfa0c83c4df6bbcd7d78c47cf47a9def109f71085e1518c04310029",
        "v": 0,
        "nonceRaw": "0x3ba172d6bbd82f45b297d12fae27013943100719889b8bc0723309d4a40b3aa",
        "blockNumberRaw": "0x1",
        "transactionIndexRaw": "0x0",
        "gasPriceRaw": "0x1",
        "gasRaw": "0x5f5e100"
      }
    ]
  }
}
```

- 失败：

```
{
    "code": 102000,
    "message": "system exception",
    "data": {}
}
```

## 5 交易信息模块


### 5.1 查询交易信息列表

#### 5.1.1 传输协议规范

* 网络传输协议：使用HTTP协议
* 请求地址：
```
/transaction/list/{chainId}/{groupId}/{pageNumber}/{pageSize}?transHash={transHash}&blockNumber={blockNumber}
```
* 请求方式：GET
* 返回格式：JSON

#### 5.1.2 请求参数

***1）入参表***

| 序号 | 输入参数    | 类型          | 可为空 | 备注                                       |
|------|-------------|---------------|--------|-------------------------------|
| 1 | chainId | Int | 否 | 链编号 |
| 2     | groupId         | Int           | 否     | 群组编号               |
| 3 | pageNumber | Int | 否 | 当前页码 |
| 4 | pageSize | Int | 否 | 每页记录数 |
| 5     | transHash   | String        | 是     | 交易hash                   |
| 6     | blockNumber     | BigInteger    | 是     | 块高                       |


***2）入参示例***

```
http://localhost:5009/WeBASE-Data-Collect/transaction/list/1/1/1/2?transHash=0x4933b1e0a7d6913a2179b879cdf716096d8da1c162fe400a492b0d61259e2ab2
```


#### 5.1.3 返回参数 

***1）出参表***

| 序号 | 输出参数    | 类型          |        | 备注                                       |
|------|-------------|---------------|--------|-------------------------------|
| 1     | code            | Int           | 否     | 返回码，0：成功 其它：失败 |
| 2     | message         | String        | 否     | 描述                       |
| 3     | totalCount      | Int           | 否     | 总记录数                   |
| 4     | data            | List          | 否     | 交易信息列表               |
| 4.1   |                 | Object        |        | 交易信息对象               |
| 4.1.1 | transHash       | String        | 否     | 交易hash                   |
| 4.1.2 | transFrom | String     | 否     | 发送方              |
| 4.1.3 | transTo | String | 否 | 接收方 |
| 4.1.4 | blockNumber     | BigInteger    | 否     | 所属块高                   |
| 4.1.5 | blockTimestamp | LocalDateTime | 否 | 所属块出块时间 |
| 4.1.6 | transDetail | String | 否 | 交易详情 |
| 4.1.7 | auditFlag | Int           | 否     | 是否已统计（1-未审计，2-已审计） |
| 4.1.8 | createTime      | LocalDateTime | 否     | 落库时间                   |
| 4.1.9 | modifyTime      | LocalDateTime | 否     | 修改时间                   |


***2）出参示例***
* 成功：
```
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 26,
      "transHash": "0x4933b1e0a7d6913a2179b879cdf716096d8da1c162fe400a492b0d61259e2ab2",
      "transFrom": "0x95824169cf64b29a9bcd5036fe0fa78ca0ecaa6f",
      "transTo": "0xd221da074ac2f34d6b453d3d456576c45e3f0843",
      "blockNumber": 35,
      "blockTimestamp": "2020-05-13 19:47:37",
      "transDetail": "{}",
      "auditFlag": 1,
      "createTime": "2020-05-20 20:22:41",
      "modifyTime": "2020-05-20 20:22:41"
    }
  ],
  "totalCount": 1
}
```

* 失败：
```
{
    "code": 102000,
    "message": "system exception",
    "data": {}
}
```

### 5.2 根据交易hash查询交易信息 

#### 5.2.1 传输协议规范

- 网络传输协议：使用HTTP协议
- 请求地址：**/transaction/transInfo/{chainId}/{groupId}/{transHash}**
- 请求方式：GET
- 返回格式：JSON

#### 5.2.2 参数信息详情

请求参数

***1）入参表***

| 序号 | 输入参数  | 类型   | 可为空 | 备注     |
| ---- | --------- | ------ | ------ | -------- |
| 1    | chainId   | Int    | 否     | 链编号   |
| 2    | groupId   | Int    | 否     | 群组编号 |
| 3    | transHash | String | 是     | 交易hash |

***2）入参示例***

```
http://localhost:5009/WeBASE-Data-Collect/transaction/transInfo/1/1/0x4933b1e0a7d6913a2179b879cdf716096d8da1c162fe400a492b0d61259e2ab2
```

#### 5.2.3 返回参数 

***1）出参表***

| 序号 | 输出参数            | 类型       |      | 备注                       |
| ---- | ------------------- | ---------- | ---- | -------------------------- |
| 1    | code                | Int        | 否   | 返回码，0：成功 其它：失败 |
| 2    | message             | String     | 否   | 描述                       |
| 3    |                     | Object     |      | 交易信息对象               |
| 3.1  | hash                | String     | 否   | 交易hash                   |
| 3.2  | blockHash           | String     | 否   | 区块hash                   |
| 3.3  | blockNumber         | BigInteger | 否   | 所属块高                   |
| 3.4  | transactionIndex    | Int        | 否   | 在区块中的索引             |
| 3.5  | from                | String     | 否   | 交易发起者                 |
| 3.6  | to                  | String     | 否   | 交易目标                   |
| 3.7  | value               | String     | 否   |                            |
| 3.8  | gasPrice            | long       | 否   |                            |
| 3.9  | gas                 | long       | 否   |                            |
| 3.10 | input               | String     | 否   |                            |
| 3.11 | v                   | Int        | 否   |                            |
| 3.12 | nonceRaw            | String     | 否   |                            |
| 3.13 | blockNumberRaw      | String     | 否   |                            |
| 3.14 | transactionIndexRaw | String     | 否   |                            |
| 3.15 | gasPriceRaw         | String     | 否   |                            |
| 3.16 | gasRaw              | String     | 否   |                            |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": {
    "hash": "0x4933b1e0a7d6913a2179b879cdf716096d8da1c162fe400a492b0d61259e2ab2",
    "blockHash": "0x1d0a57a6ee2b73e537ef6d929c8d0bdb2a9799dd6357f04dc5f38e4e0c6c5ac2",
    "blockNumber": 35,
    "transactionIndex": 0,
    "from": "0x95824169cf64b29a9bcd5036fe0fa78ca0ecaa6f",
    "to": "0xd221da074ac2f34d6b453d3d456576c45e3f0843",
    "value": 0,
    "gasPrice": 1,
    "gas": 100000000,
    "input": "0x3590b49f000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000047465737400000000000000000000000000000000000000000000000000000000",
    "v": 0,
    "nonceRaw": "0x1a00f64575c78412465016b2efb7f80bece2474db763e4f014ce8bf1fa5aa50",
    "blockNumberRaw": "0x23",
    "transactionIndexRaw": "0x0",
    "gasPriceRaw": "0x1",
    "gasRaw": "0x5f5e100"
  }
}
```

- 失败：

```
{
    "code": 102000,
    "message": "system exception",
    "data": {}
}
```

### 5.3 根据交易hash查询交易回执 

#### 5.3.1 传输协议规范

* 网络传输协议：使用HTTP协议
* 请求地址：**/transaction/receipt/{chainId}/{groupId}/{transHash}**
* 请求方式：GET
* 返回格式：JSON

#### 4.3.2 请求参数

***1）入参表***

| 序号 | 输入参数    | 类型          | 可为空 | 备注                                       |
|------|-------------|---------------|--------|-------------------------------|
| 1 | chainId | Int | 否 | 链编号 |
| 2     | groupId         | Int           | 否     | 群组编号               |
| 3     | transHash | String        | 是     | 交易hash                   |


***2）入参示例***

```
http://localhost:5009/WeBASE-Data-Collect/transaction/receipt/1/1/0x4933b1e0a7d6913a2179b879cdf716096d8da1c162fe400a492b0d61259e2ab2
```


#### 5.3.3 返回参数 

***1）出参表***

| 序号 | 输出参数    | 类型          |        | 备注                                       |
|------|-------------|---------------|--------|-------------------------------|
| 1     | code            | Int           | 否     | 返回码，0：成功 其它：失败 |
| 2     | message         | String        | 否     | 描述                       |
| 3     |                 | Object        |        | 交易信息对象               |
| 3.1 | transactionHash       | String        | 否     | 交易hash                   |
| 3.2 | transactionIndex         | Int           | 否     | 在区块中的索引               |
| 3.2 | blockHash         | String           | 否     | 区块hash               |
| 3.3 | blockNumber     | BigInteger    | 否     | 所属块高                   |
| 3.4 | cumulativeGasUsed  | Int           | 否     |                |
| 3.5 | gasUsed      | Int | 否     | 交易消耗的gas                   |
| 3.6 | contractAddress      | String | 否     | 合约地址                   |
| 3.7 | status      | String | 否     | 交易的状态值                   |
| 3.8 | from      | String | 否     | 交易发起者                   |
| 3.9 | to      | String | 否     | 交易目标                   |
| 3.10 | input | String | 否 | 交易输入信息 |
| 3.11 | output      | String | 否     | 交易输出内容                   |
| 3.12 | logs      | String | 否     | 日志                   |
| 3.13 | logsBloom      | String | 否     | log的布隆过滤值                   |


***2）出参示例***
* 成功：
```
{
  "code": 0,
  "message": "success",
  "data": {
    "transactionHash": "0x4933b1e0a7d6913a2179b879cdf716096d8da1c162fe400a492b0d61259e2ab2",
    "transactionIndex": 0,
    "blockHash": "0x1d0a57a6ee2b73e537ef6d929c8d0bdb2a9799dd6357f04dc5f38e4e0c6c5ac2",
    "blockNumber": 35,
    "cumulativeGasUsed": 0,
    "gasUsed": 34949,
    "contractAddress": "0x0000000000000000000000000000000000000000",
    "status": "0x0",
    "from": "0x95824169cf64b29a9bcd5036fe0fa78ca0ecaa6f",
    "to": "0xd221da074ac2f34d6b453d3d456576c45e3f0843",
    "input": "0x608060405234801561001057600080fd5b50610373806100206000396000f30060806040526004361061004c576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680634ed3885e146100515780636d4ce63c146100ba575b600080fd5b34801561005d57600080fd5b506100b8600480360381019080803590602001908201803590602001908080601f016020809104026020016040519081016040528093929190818152602001838380828437820191505050505050919291929050505061014a565b005b3480156100c657600080fd5b506100cf610200565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561010f5780820151818401526020810190506100f4565b50505050905090810190601f16801561013c5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b7f4df9dcd34ae35f40f2c756fd8ac83210ed0b76d065543ee73d868aec7c7fcf02816040518080602001828103825283818151815260200191508051906020019080838360005b838110156101ac578082015181840152602081019050610191565b50505050905090810190601f1680156101d95780820380516001836020036101000a031916815260200191505b509250505060405180910390a180600090805190602001906101fc9291906102a2565b5050565b606060008054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156102985780601f1061026d57610100808354040283529160200191610298565b820191906000526020600020905b81548152906001019060200180831161027b57829003601f168201915b5050505050905090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106102e357805160ff1916838001178555610311565b82800160010185558215610311579182015b828111156103105782518255916020019190600101906102f5565b5b50905061031e9190610322565b5090565b61034491905b80821115610340576000816000905550600101610328565b5090565b905600a165627a7a72305820baa9e2a7ab055843a8a3de62b50fba49e6309323fb92358b598491fa5a76b9e90029",
    "output": "0x",
    "logs": [
      {
        "logIndex": null,
        "transactionIndex": null,
        "transactionHash": null,
        "blockHash": null,
        "blockNumber": null,
        "address": "0xd221da074ac2f34d6b453d3d456576c45e3f0843",
        "data": "0x000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000047465737400000000000000000000000000000000000000000000000000000000",
        "topics": [
          "0x05432a43e07f36a8b98100b9cb3631e02f8e796b0a06813610ce8942e972fb81"
        ]
      }
    ],
    "logsBloom": "0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000100000004000000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000080000000000000000080000000000000000000000000000000000000000000000000000000000000000000000000000000000"
  }
}
```

* 失败：
```
{
    "code": 102000,
    "message": "system exception",
    "data": {}
}
```

## 6 用户管理模块

### 6.1 新增用户

#### 6.1.1 传输协议规范

- 网络传输协议：使用HTTP协议
- 请求地址： **/user/add**
- 请求方式：POST
- 请求头：Content-type: application/json
- 返回格式：JSON

#### 6.1.2 请求参数

***1）入参表***

| 序号 | 输入参数    | 类型   | 可为空 | 备注     |
| ---- | ----------- | ------ | ------ | -------- |
| 1    | chainId     | Int    | 否     | 链编号   |
| 2    | groupId     | Int    | 否     | 群组编号 |
| 3    | userName    | String | 否     | 用户名   |
| 4    | address     | String | 否     | 用户地址 |
| 5    | description | String | 是     | 描述     |

***2）入参示例***

```
http://localhost:5009/WeBASE-Data-Collect/user/add
```

```
{
  "address": "0x056a6b8bd27e861773ec2419a871ff245291a2d6",
  "chainId": 1,
  "description": "string",
  "groupId": 1,
  "userName": "alice"
}
```

#### 6.1.3 返回参数

***1）出参表***

| 序号 | 输出参数    | 类型          | 可为空 | 备注                       |
| ---- | ----------- | ------------- | ------ | -------------------------- |
| 1    | code        | Int           | 否     | 返回码，0：成功 其它：失败 |
| 2    | message     | String        | 否     | 描述                       |
| 3    |             | Object        |        | 节点信息对象               |
| 3.1  | userId      | Int           | 否     | 用户编号                   |
| 3.2  | chainId     | Int           | 否     | 链编号                     |
| 3.3  | groupId     | Int           | 否     | 群组编号                   |
| 3.2  | userName    | String        | 否     | 用户名                     |
| 3.3  | address     | String        | 否     | 用户地址                   |
| 3.4  | description | String        | 是     | 描述                       |
| 3.5  | createTime  | LocalDateTime | 是     | 落库时间                   |
| 3.6  | modifyTime  | LocalDateTime | 是     | 修改时间                   |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": {
    "userId": 1,
    "chainId": 1,
    "groupId": 1,
    "userName": "alice",
    "address": "0x056a6b8bd27e861773ec2419a871ff245291a2d6",
    "description": "test",
    "createTime": "2020-06-02 20:35:20",
    "modifyTime": "2020-06-02 20:35:20"
  }
}
```

- 失败：

```
{
    "code": 102000,
    "message": "system exception",
    "data": {}
}
```

### 6.2 获取用户列表 

#### 6.2.1 传输协议规范

- 网络传输协议：使用HTTP协议
- 请求地址：**/user/list/{pageNumber}/{pageSize}?chainId={chainId}&groupId={groupId}&userParam={userParam}**
- 请求方式：GET
- 返回格式：JSON

#### 6.2.2 请求参数

***1）入参表***

| 序号 | 输入参数   | 类型   | 可为空 | 备注               |
| ---- | ---------- | ------ | ------ | ------------------ |
| 1    | pageNumber | Int    | 否     | 当前页码           |
| 2    | pageSize   | Int    | 否     | 每页记录数         |
| 3    | chainId    | Int    | 是     | 链编号             |
| 4    | groupId    | Int    | 是     | 群组编号           |
| 5    | userParam  | String | 是     | 参数，用户名或地址 |

***2）入参示例***

```
http://localhost:5009/WeBASE-Data-Collect/user/list/1/2
```

#### 6.2.3 返回参数 

***1）出参表***

| 序号  | 输出参数    | 类型          |      | 备注                       |
| ----- | ----------- | ------------- | ---- | -------------------------- |
| 1     | code        | Int           | 否   | 返回码，0：成功 其它：失败 |
| 2     | message     | String        | 否   | 描述                       |
| 3     | totalCount  | Int           | 否   | 总记录数                   |
| 4     | data        | List          | 否   | 列表                       |
| 4.1   |             | Object        |      | 对象                       |
| 4.1.1 | userId      | Int           | 否   | 用户编号                   |
| 4.1.2 | chainId     | Int           | 否   | 链编号                     |
| 4.1.3 | groupId     | Int           | 否   | 群组编号                   |
| 4.1.4 | userName    | String        | 否   | 用户名                     |
| 4.1.5 | address     | String        | 否   | 用户地址                   |
| 4.1.6 | description | String        | 是   | 描述                       |
| 4.1.7 | createTime  | LocalDateTime | 否   | 落库时间                   |
| 4.1.8 | modifyTime  | LocalDateTime | 否   | 修改时间                   |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "userId": 1,
      "chainId": 1,
      "groupId": 1,
      "userName": "alice",
      "address": "0x056a6b8bd27e861773ec2419a871ff245291a2d6",
      "description": "test",
      "createTime": "2020-06-02 20:35:20",
      "modifyTime": "2020-06-02 20:35:20"
    }
  ],
  "totalCount": 1
}
```

- 失败：

```
{
   "code": 102000,
   "message": "system exception",
   "data": {}
}
```

### 6.3 删除用户

#### 6.3.1 传输协议规范

- 网络传输协议：使用HTTP协议
- 请求地址：**/user/{userId}**
- 请求方式：DELETE
- 请求头：Content-type: application/json
- 返回格式：JSON

#### 6.3.2 请求参数

***1）入参表***

| 序号 | 输入参数 | 类型 | 可为空 | 备注     |
| ---- | -------- | ---- | ------ | -------- |
| 1    | userId   | Int  | 否     | 用户编号 |

***2）入参示例***

```
http://localhost:5009/WeBASE-Data-Collect/user/1
```

#### 6.3.3 返回参数 

***1）出参表***

| 序号 | 输出参数 | 类型   |      | 备注                       |
| ---- | -------- | ------ | ---- | -------------------------- |
| 1    | code     | Int    | 否   | 返回码，0：成功 其它：失败 |
| 2    | message  | String | 否   | 描述                       |
| 3    | data     | object | 是   | 返回信息实体（空）         |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": null
}
```

- 失败：

```
{
    "code": 102000,
    "message": "system exception",
    "data": {}
}
```

## 7 合约管理模块

### 7.1 保存合约和更新

#### 7.1.1 传输协议规范

- 网络传输协议：使用HTTP协议
- 请求地址：**/contract/save**
- 请求方式：POST
- 请求头：Content-type: application/json
- 返回格式：JSON

#### 7.1.2 请求参数

***1）入参表***

| 序号 | 输入参数       | 类型   | 可为空 | 备注                                       |
| ---- | -------------- | ------ | ------ | ------------------------------------------ |
| 1    | chainId        | Int    | 否     | 链编号                                     |
| 2    | groupId        | Int    | 否     | 群组编号                                   |
| 3    | contractName   | String | 否     | 合约名称                                   |
| 4    | contractSource | String | 是     | 合约源码，Base64编码                       |
| 5    | contractAbi    | String | 是     | 编译合约生成的abi文件内容                  |
| 6    | contractBin    | String | 是     | 合约运行时binary，用于合约解析             |
| 7    | bytecodeBin    | String | 是     | 合约bytecode binary，用于部署合约          |
| 8    | contractId     | String | 是     | 合约编号（为空时表示新增，不为空表示更新） |
| 9    | contractPath   | String | 否     | 合约所在目录                               |

***2）入参示例***

```
http://localhost:5009/WeBASE-Data-Collect/contract/save
```

```
{
  "bytecodeBin": "608060405234801561001057600080fd5b50610373806100206000396000f30060806040526004361061004c576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff168063299f7f9d146100515780633590b49f146100e1575b600080fd5b34801561005d57600080fd5b5061006661014a565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156100a657808201518184015260208101905061008b565b50505050905090810190601f1680156100d35780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156100ed57600080fd5b50610148600480360381019080803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091929192905050506101ec565b005b606060008054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156101e25780601f106101b7576101008083540402835291602001916101e2565b820191906000526020600020905b8154815290600101906020018083116101c557829003601f168201915b5050505050905090565b7f05432a43e07f36a8b98100b9cb3631e02f8e796b0a06813610ce8942e972fb81816040518080602001828103825283818151815260200191508051906020019080838360005b8381101561024e578082015181840152602081019050610233565b50505050905090810190601f16801561027b5780820380516001836020036101000a031916815260200191505b509250505060405180910390a1806000908051906020019061029e9291906102a2565b5050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106102e357805160ff1916838001178555610311565b82800160010185558215610311579182015b828111156103105782518255916020019190600101906102f5565b5b50905061031e9190610322565b5090565b61034491905b80821115610340576000816000905550600101610328565b5090565b905600a165627a7a72305820cff924cb0783dc84e2e107aae1fd09e1e04154b80834c9267a4eaa630997b2b90029",
  "chainId": 1,
  "contractAbi": "[{\"constant\":true,\"inputs\":[],\"name\":\"get\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"n\",\"type\":\"string\"}],\"name\":\"set\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"name\",\"type\":\"string\"}],\"name\":\"SetName\",\"type\":\"event\"}]",
  "contractBin": "xxx",
  "contractName": "HelloWorld",
  "contractPath": "/",
  "contractSource": "cHJhZ21hIHNvbGlkaXR5IF4wLjQuMjsNCmNvbnRyYWN0IEhlbGxvV29ybGR7DQogICAgc3RyaW5nIG5hbWU7DQogICAgZXZlbnQgU2V0TmFtZShzdHJpbmcgbmFtZSk7DQogICAgZnVuY3Rpb24gZ2V0KCljb25zdGFudCByZXR1cm5zKHN0cmluZyl7DQogICAgICAgIHJldHVybiBuYW1lOw0KICAgIH0NCiAgICBmdW5jdGlvbiBzZXQoc3RyaW5nIG4pew0KICAgICAgICBlbWl0IFNldE5hbWUobik7DQogICAgICAgIG5hbWU9bjsNCiAgICB9DQp9",
  "groupId": 1
}
```

#### 7.1.3 返回参数 

***1）出参表***

| 序号 | 输出参数       | 类型          |      | 备注                                    |
| ---- | -------------- | ------------- | ---- | --------------------------------------- |
| 1    | code           | Int           | 否   | 返回码，0：成功 其它：失败              |
| 2    | message        | String        | 否   | 描述                                    |
| 3    |                | Object        |      | 返回信息实体                            |
| 3.1  | contractId     | Int           | 否   | 合约编号                                |
| 3.2  | contractPath   | String        | 否   | 合约所在目录                            |
| 3.3  | contractName   | String        | 否   | 合约名称                                |
| 3.4  | chainId        | Int           | 否   | 链编号                                  |
| 3.5  | groupId        | Int           | 否   | 群组编号                                |
| 3.6  | contractType   | Int           | 否   | 合约类型(0-普通合约，1-系统合约，默认0) |
| 3.7  | contractSource | String        | 否   | 合约源码                                |
| 3.8  | contractAbi    | String        | 是   | 编译合约生成的abi文件内容               |
| 3.9  | contractBin    | String        | 是   | 合约运行时binary，用于合约解析          |
| 3.10 | bytecodeBin    | String        | 是   | 合约bytecode binary，用于部署合约       |
| 3.11 | description    | String        | 是   | 备注                                    |
| 3.12 | createTime     | LocalDateTime | 否   | 创建时间                                |
| 3.13 | modifyTime     | LocalDateTime | 是   | 修改时间                                |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": {
    "contractId": 1,
    "chainId": 1,
    "groupId": 1,
    "contractType": 0,
    "contractPath": "/",
    "contractName": "HelloWorld",
    "contractSource": "cHJhZ21hIHNvbGlkaXR5IF4wLjQuMjsNCmNvbnRyYWN0IEhlbGxvV29ybGR7DQogICAgc3RyaW5nIG5hbWU7DQogICAgZXZlbnQgU2V0TmFtZShzdHJpbmcgbmFtZSk7DQogICAgZnVuY3Rpb24gZ2V0KCljb25zdGFudCByZXR1cm5zKHN0cmluZyl7DQogICAgICAgIHJldHVybiBuYW1lOw0KICAgIH0NCiAgICBmdW5jdGlvbiBzZXQoc3RyaW5nIG4pew0KICAgICAgICBlbWl0IFNldE5hbWUobik7DQogICAgICAgIG5hbWU9bjsNCiAgICB9DQp9",
    "contractAbi": "[{\"constant\":true,\"inputs\":[],\"name\":\"get\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"n\",\"type\":\"string\"}],\"name\":\"set\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"name\",\"type\":\"string\"}],\"name\":\"SetName\",\"type\":\"event\"}]",
    "contractBin": "xxx",
    "bytecodeBin": "608060405234801561001057600080fd5b50610373806100206000396000f30060806040526004361061004c576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff168063299f7f9d146100515780633590b49f146100e1575b600080fd5b34801561005d57600080fd5b5061006661014a565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156100a657808201518184015260208101905061008b565b50505050905090810190601f1680156100d35780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156100ed57600080fd5b50610148600480360381019080803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091929192905050506101ec565b005b606060008054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156101e25780601f106101b7576101008083540402835291602001916101e2565b820191906000526020600020905b8154815290600101906020018083116101c557829003601f168201915b5050505050905090565b7f05432a43e07f36a8b98100b9cb3631e02f8e796b0a06813610ce8942e972fb81816040518080602001828103825283818151815260200191508051906020019080838360005b8381101561024e578082015181840152602081019050610233565b50505050905090810190601f16801561027b5780820380516001836020036101000a031916815260200191505b509250505060405180910390a1806000908051906020019061029e9291906102a2565b5050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106102e357805160ff1916838001178555610311565b82800160010185558215610311579182015b828111156103105782518255916020019190600101906102f5565b5b50905061031e9190610322565b5090565b61034491905b80821115610340576000816000905550600101610328565b5090565b905600a165627a7a72305820cff924cb0783dc84e2e107aae1fd09e1e04154b80834c9267a4eaa630997b2b90029",
    "description": null,
    "createTime": "2020-06-02 20:50:58",
    "modifyTime": "2020-06-02 20:50:58"
  }
}
```

- 失败：

```
{
    "code": 102000,
    "message": "system exception",
    "data": {}
}
```

### 7.2 查询合约列表 

#### 7.2.1 传输协议规范

- 网络传输协议：使用HTTP协议
- 请求地址：**/contract/list**
- 请求方式：POST
- 返回格式：JSON

#### 7.2.2 请求参数

***1）入参表***

| 序号 | 输入参数     | 类型   | 可为空 | 备注       |
| ---- | ------------ | ------ | ------ | ---------- |
| 1    | chainId      | Int    | 否     | 链编号     |
| 2    | groupId      | Int    | 否     | 群组id     |
| 3    | contractName | String | 是     | 合约名     |
| 4    | pageSize     | Int    | 是     | 每页记录数 |
| 5    | pageNumber   | Int    | 是     | 当前页码   |

***2）入参示例***

```
http://localhost:5009/WeBASE-Data-Collect/contract/list
```

```
{
  "chainId": 1,
  "groupId": 1,
  "pageNumber": 1,
  "pageSize": 2
}
```

#### 7.2.3 返回参数 

***1）出参表***

| 序号   | 输出参数       | 类型          |      | 备注                              |
| ------ | -------------- | ------------- | ---- | --------------------------------- |
| 1      | code           | Int           | 否   | 返回码，0：成功 其它：失败        |
| 2      | message        | String        | 否   | 描述                              |
| 3      | totalCount     | Int           | 否   | 总记录数                          |
| 4      | data           | List          | 是   | 列表                              |
| 5.1    |                | Object        |      | 返回信息实体                      |
| 5.1.1  | contractId     | Int           | 否   | 合约编号                          |
| 5.1.2  | contractPath   | String        | 否   | 合约所在目录                      |
| 5.1.3  | contractName   | String        | 否   | 合约名称                          |
| 5.1.4  | chainId        | Int           | 否   | 链编号                            |
| 5.1.5  | groupId        | Int           | 否   | 群组编号                          |
| 5.1.6  | contractType   | Int           | 否   | 合约类型(0-普通合约，1-系统合约)  |
| 5.1.7  | contractSource | String        | 否   | 合约源码                          |
| 5.1.8  | contractAbi    | String        | 是   | 编译合约生成的abi文件内容         |
| 5.1.9  | contractBin    | String        | 是   | 合约运行时binary，用于合约解析    |
| 5.1.10 | bytecodeBin    | String        | 是   | 合约bytecode binary，用于部署合约 |
| 5.1.11 | description    | String        | 是   | 备注                              |
| 5.1.12 | createTime     | LocalDateTime | 否   | 创建时间                          |
| 5.1.13 | modifyTime     | LocalDateTime | 是   | 修改时间                          |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "contractId": 1,
      "chainId": 1,
      "groupId": 1,
      "contractPath": "/",
      "contractName": "HelloWorld",
      "contractSource": "cHJhZ21hIHNvbGlkaXR5IF4wLjQuMjsNCmNvbnRyYWN0IEhlbGxvV29ybGR7DQogICAgc3RyaW5nIG5hbWU7DQogICAgZXZlbnQgU2V0TmFtZShzdHJpbmcgbmFtZSk7DQogICAgZnVuY3Rpb24gZ2V0KCljb25zdGFudCByZXR1cm5zKHN0cmluZyl7DQogICAgICAgIHJldHVybiBuYW1lOw0KICAgIH0NCiAgICBmdW5jdGlvbiBzZXQoc3RyaW5nIG4pew0KICAgICAgICBlbWl0IFNldE5hbWUobik7DQogICAgICAgIG5hbWU9bjsNCiAgICB9DQp9",
      "contractAbi": "[{\"constant\":true,\"inputs\":[],\"name\":\"get\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"n\",\"type\":\"string\"}],\"name\":\"set\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"name\",\"type\":\"string\"}],\"name\":\"SetName\",\"type\":\"event\"}]",
      "contractBin": "xxx",
      "bytecodeBin": "608060405234801561001057600080fd5b50610373806100206000396000f30060806040526004361061004c576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff168063299f7f9d146100515780633590b49f146100e1575b600080fd5b34801561005d57600080fd5b5061006661014a565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156100a657808201518184015260208101905061008b565b50505050905090810190601f1680156100d35780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156100ed57600080fd5b50610148600480360381019080803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091929192905050506101ec565b005b606060008054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156101e25780601f106101b7576101008083540402835291602001916101e2565b820191906000526020600020905b8154815290600101906020018083116101c557829003601f168201915b5050505050905090565b7f05432a43e07f36a8b98100b9cb3631e02f8e796b0a06813610ce8942e972fb81816040518080602001828103825283818151815260200191508051906020019080838360005b8381101561024e578082015181840152602081019050610233565b50505050905090810190601f16801561027b5780820380516001836020036101000a031916815260200191505b509250505060405180910390a1806000908051906020019061029e9291906102a2565b5050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106102e357805160ff1916838001178555610311565b82800160010185558215610311579182015b828111156103105782518255916020019190600101906102f5565b5b50905061031e9190610322565b5090565b61034491905b80821115610340576000816000905550600101610328565b5090565b905600a165627a7a72305820cff924cb0783dc84e2e107aae1fd09e1e04154b80834c9267a4eaa630997b2b90029",
      "contractType": 0,
      "description": null,
      "createTime": "2020-06-02 20:50:58",
      "modifyTime": "2020-06-02 20:50:58"
    }
  ],
  "totalCount": 1
}
```

- 失败：

```
{
    "code": 102000,
    "message": "system exception",
    "data": {}
}
```

### 7.3 删除合约

#### 7.3.1 传输协议规范

- 网络传输协议：使用HTTP协议
- 请求地址：**/contract/{contractId}**
- 请求方式：DELETE
- 请求头：Content-type: application/json
- 返回格式：JSON

#### 7.3.2 请求参数

***1）入参表***

| 序号 | 输入参数   | 类型 | 可为空 | 备注     |
| ---- | ---------- | ---- | ------ | -------- |
| 1    | contractId | Int  | 否     | 合约编号 |

***2）入参示例***

```
http://localhost:5009/WeBASE-Data-Collect/contract/1
```

#### 7.3.3 返回参数 

***1）出参表***

| 序号 | 输出参数 | 类型   |      | 备注                       |
| ---- | -------- | ------ | ---- | -------------------------- |
| 1    | code     | Int    | 否   | 返回码，0：成功 其它：失败 |
| 2    | message  | String | 否   | 描述                       |
| 3    | data     | object | 是   | 返回信息实体（空）         |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": null
}
```

- 失败：

```
{
    "code": 102000,
    "message": "system exception",
    "data": {}
}
```

### 7.4 保存合约方法信息

#### 7.4.1 传输协议规范

- 网络传输协议：使用HTTP协议
- 请求地址：**/contract/addMethod**
- 请求方式：POST
- 请求头：Content-type: application/json
- 返回格式：JSON

#### 7.4.2 请求参数

***1）入参表***

| 序号  | 输入参数   | 类型   | 可为空 | 备注     |
| ----- | ---------- | ------ | ------ | -------- |
| 1     | contractId | Int    | 否     | 合约编号 |
| 2     | methodList | List   | 否     | 方法列表 |
| 2.1   |            | Object | 否     | 方法实体 |
| 2.1.1 | methodId   | String | 否     | 方法编号 |
| 2.1.2 | methodName | String | 否     | 方法名   |
| 2.1.4 | methodType | String | 否     | 方法类型 |

***2）入参示例***

```
http://127.0.0.1:5009/WeBASE-Data-Collect/contract/addMethod
```

```
{
  "contractId": 2,
  "methodList": [
    {
      "methodId": "0x3590b49f",
      "methodName": "set",
      "methodType": "function"
    }，
    {
      "methodId": "0x9bd13510",
      "methodName": "get",
      "methodType": "function"
    }
  ]
}
```

#### 7.4.3 返回参数 

***1）出参表***

| 序号 | 输出参数 | 类型   |      | 备注                       |
| ---- | -------- | ------ | ---- | -------------------------- |
| 1    | code     | Int    | 否   | 返回码，0：成功 其它：失败 |
| 2    | message  | String | 否   | 描述                       |
| 3    | data     | object | 是   | 返回信息实体（空）         |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": null
}
```

- 失败：

```
{
    "code": 102000,
    "message": "system exception",
    "data": {}
}
```

## 8. 编译器管理模块

### 8.1. 查询编译器列表

#### 8.1.1 传输协议规范

- 网络传输协议：使用HTTP协议
- 请求地址：**/solc/list?encryptType={encryptType}**
- 请求方式：GET
- 返回格式：JSON

#### 8.1.2 请求参数

***1）入参表***

| 序号 | 输入参数    | 类型 | 可为空 | 备注                     |
| ---- | ----------- | ---- | ------ | ------------------------ |
| 1    | encryptType | Int  | 是     | 类型（0-ecdsa；1-guomi） |

***2）入参示例***

```
http://localhost:5009/WeBASE-Data-Collect/solc/list
```

#### 8.1.3 返回参数 

***1）出参表***

| 序号  | 输出参数    | 类型          |      | 备注                       |
| ----- | ----------- | ------------- | ---- | -------------------------- |
| 1     | code        | Int           | 否   | 返回码，0：成功 其它：失败 |
| 2     | message     | String        | 否   | 描述                       |
| 3     | totalCount  | Int           | 否   | 总记录数                   |
| 4     | data        | List          | 否   | 列表                       |
| 4.1   |             | Object        |      | 对象                       |
| 4.1.1 | id          | Int           | 否   | 编号                       |
| 4.1.2 | solcName    | Int           | 否   | 编译器文件名               |
| 4.1.3 | encryptType | Int           | 否   | 类型                       |
| 4.1.4 | md5         | String        | 否   | md5值                      |
| 4.1.5 | fileSize    | Long          | 否   | 文件长度                   |
| 4.1.6 | description | String        | 是   | 描述                       |
| 4.1.7 | createTime  | LocalDateTime | 否   | 落库时间                   |
| 4.1.8 | modifyTime  | LocalDateTime | 否   | 修改时间                   |

***2）出参示例***

```
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 1,
      "solcName": "soljson-v0.4.25-gm.js",
      "encryptType": 1,
      "md5": "c0810103136fb9177df943346b2dcad4",
      "fileSize": 8273598,
      "description": "guomi",
      "createTime": "2020-06-14 11:05:56",
      "modifyTime": "2020-06-14 11:05:56"
    },
    {
      "id": 2,
      "solcName": "soljson-v0.4.25+commit.59dbf8f1.js",
      "encryptType": 0,
      "md5": "e201c5913e0982cb90cdb2a711e36f63",
      "fileSize": 8276063,
      "description": "ecdsa",
      "createTime": "2020-06-14 11:19:10",
      "modifyTime": "2020-06-14 11:19:10"
    }
  ]
}
```
