
# 接口说明

## 1 区块链管理模块

### 1.1 新增链信息

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址： **/chain/new**
- 请求方式：POST
- 请求头：Content-type: application/json
- 返回格式：JSON

#### 请求参数

***1）入参表***

| 序号 | 输入参数    | 类型   | 可为空 | 备注                           |
| ---- | ----------- | ------ | ------ | ------------------------------ |
| 1    | chainId     | Int    | 否     | 链编号（1~9999）               |
| 2    | chainName   | String | 否     | 链名称                         |
| 3    | encryptType | Int    | 否     | 链加密类型（0-非国密，1-国密） |
| 4    | description | String | 是     | 备注                           |

***2）入参示例***

```
http://localhost:5009/WeBASE-Data-Collect/chain/new
```

```
{
    "chainId": 1,
    "chainName": "链一",
    "encryptType": 0,
    "description": "test"
}
```

####  返回参数

***1）出参表***

| 序号 | 输出参数    | 类型          |      | 备注                           |
| ---- | ----------- | ------------- | ---- | ------------------------------ |
| 1    | code        | Int           | 否   | 返回码，0：成功 其它：失败     |
| 2    | message     | String        | 否   | 描述                           |
| 3    |             | Object        |      | 节点信息对象                   |
| 3.1  | chainId     | Int           | 否   | 链编号                         |
| 3.2  | chainName   | String        | 否   | 链名称                         |
| 3.3  | chainType   | Int           | 否   | 链类型（ 0-fisco 1-fabric）    |
| 3.4  | encryptType | Int           | 否   | 链加密类型（0-非国密，1-国密） |
| 3.5  | description | String        | 是   | 备注                           |
| 3.6  | createTime  | LocalDateTime | 否   | 落库时间                       |
| 3.7  | modifyTime  | LocalDateTime | 否   | 修改时间                       |

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
        "encryptType": 0,
        "description": "test"
        "createTime": "2019-02-14 17:47:00",
        "modifyTime": "2019-03-15 11:14:29"
    }
}
```

- 失败：

```
{
    "code": 209001,
    "message": "chain id already exists",
    "data": {}
}
```

### 1.2 修改链信息

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址： **/chain/update**
- 请求方式：POST
- 请求头：Content-type: application/json
- 返回格式：JSON

#### 请求参数

***1）入参表***

| 序号 | 输入参数    | 类型   | 可为空 | 备注   |
| ---- | ----------- | ------ | ------ | ------ |
| 1    | chainId     | Int    | 否     | 链编号 |
| 2    | chainName   | String | 否     | 链名称 |
| 3    | description | String | 是     | 备注   |

***2）入参示例***

```
http://localhost:5009/WeBASE-Data-Collect/chain/update
```

```
{
    "chainId": 1,
    "chainName": "链一",
    "description": "test"
}
```

####  返回参数

***1）出参表***

| 序号 | 输出参数    | 类型          |      | 备注                           |
| ---- | ----------- | ------------- | ---- | ------------------------------ |
| 1    | code        | Int           | 否   | 返回码，0：成功 其它：失败     |
| 2    | message     | String        | 否   | 描述                           |
| 3    |             | Object        |      | 节点信息对象                   |
| 3.1  | chainId     | Int           | 否   | 链编号                         |
| 3.2  | chainName   | String        | 否   | 链名称                         |
| 3.3  | chainType   | Int           | 否   | 链类型（ 0-fisco 1-fabric）    |
| 3.4  | encryptType | Int           | 否   | 链加密类型（0-非国密，1-国密） |
| 3.5  | description | String        | 是   | 备注                           |
| 3.6  | createTime  | LocalDateTime | 否   | 落库时间                       |
| 3.7  | modifyTime  | LocalDateTime | 否   | 修改时间                       |

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
        "encryptType": 0,
        "description": "test"
        "createTime": "2019-02-14 17:47:00",
        "modifyTime": "2019-03-15 11:14:29"
    }
}
```

- 失败：

```
{
    "code": 209006,
    "message": "chain id not exists",
    "data": {}
}
```

### 1.3 查询链列表 

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址：**/chain/all**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

无

***2）入参示例***

```
http://localhost:5009/WeBASE-Data-Collect/chain/all
```

#### 返回参数 

***1）出参表***

| 序号  | 输出参数    | 类型          |      | 备注                           |
| ----- | ----------- | ------------- | ---- | ------------------------------ |
| 1     | code        | Int           | 否   | 返回码，0：成功 其它：失败     |
| 2     | message     | String        | 否   | 描述                           |
| 3     | totalCount  | Int           | 否   | 总记录数                       |
| 4     | data        | List          | 否   | 组织列表                       |
| 4.1   |             | Object        |      | 节点信息对象                   |
| 4.1.1 | chainId     | Int           | 否   | 链编号                         |
| 4.1.2 | chainName   | String        | 否   | 链名称                         |
| 4.1.3 | chainType   | Int           | 否   | 链类型（ 0-fisco 1-fabric）    |
| 4.1.4 | encryptType | Int           | 否   | 链加密类型（0-非国密，1-国密） |
| 4.1.5 | description | String        | 是   | 备注                           |
| 4.1.6 | createTime  | LocalDateTime | 否   | 落库时间                       |
| 4.1.7 | modifyTime  | LocalDateTime | 否   | 修改时间                       |

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
        "encryptType": 0,
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
   "code": 109000,
   "message": "system exception",
   "data": {}
}
```

### 1.4 删除链信息

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址：**/chain/{chainId}**
- 请求方式：DELETE
- 请求头：Content-type: application/json
- 返回格式：JSON

#### 请求参数

***1）入参表***

| 序号 | 输入参数 | 类型 | 可为空 | 备注   |
| ---- | -------- | ---- | ------ | ------ |
| 1    | chainId  | Int  | 否     | 链编号 |

***2）入参示例***

```
http://localhost:5009/WeBASE-Data-Collect/chain/1
```

#### 返回参数 

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
    "code": 209004,
    "message": "invalid chain id",
    "data": {}
}
```

## 2 前置管理模块

### 2.1 新增节点前置


#### 传输协议
* 网络传输协议：使用HTTP协议
* 请求地址： **/front/new**
* 请求方式：POST
* 请求头：Content-type: application/json
* 返回格式：JSON

#### 请求参数

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


#### 返回参数

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
    "code": 109000,
    "message": "system exception",
    "data": {}
}
```


### 2.2 查询前置列表 


#### 传输协议
* 网络传输协议：使用HTTP协议
* 请求地址：**/front/list?chainId={chainId}?frontId={frontId}&groupId={groupId}**
* 请求方式：GET
* 返回格式：JSON

#### 请求参数

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


#### 返回参数 

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
      "createTime": "2021-01-11 20:22:35",
      "modifyTime": "2021-01-11 20:22:35"
    }
  ],
  "totalCount": 1
}
```

* 失败：
```
{
   "code": 109000,
   "message": "system exception",
   "data": {}
}
```


### 2.3 删除前置信息

#### 传输协议
* 网络传输协议：使用HTTP协议
* 请求地址：**/front/{frontId}**
* 请求方式：DELETE
* 请求头：Content-type: application/json
* 返回格式：JSON

#### 请求参数

***1）入参表***

| 序号 | 输入参数    | 类型          | 可为空 | 备注                                       |
|------|-------------|---------------|--------|-------------------------------|
| 1    | frontId    | Int    | 否     | 前置编号                   |


***2）入参示例***

```
http://localhost:5009/WeBASE-Data-Collect/front/1
```


#### 返回参数 

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
    "code": 109000,
    "message": "system exception",
    "data": {}
}
```

## 3 群组管理模块

### 3.1 查询群组列表

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址：**/group/list/{chainId}**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

| 序号 | 输入参数 | 类型 | 可为空 | 备注   |
| ---- | -------- | ---- | ------ | ------ |
| 1    | chainId  | Int  | 否     | 链编号 |

***2）入参示例***

```
http://localhost:5009/WeBASE-Data-Collect/group/list/1
```

#### 返回参数 

***1）出参表***

| 序号   | 输出参数         | 类型          |      | 备注                       |
| ------ | ---------------- | ------------- | ---- | -------------------------- |
| 1      | code             | Int           | 否   | 返回码，0：成功 其它：失败 |
| 2      | message          | String        | 否   | 描述                       |
| 3      | totalCount       | Int           | 否   | 总记录数                   |
| 4      | data             | List          | 否   | 列表                       |
| 4.1    |                  | Object        |      | 信息对象                   |
| 4.1.1  | chainId          | Int           | 否   | 链编号                     |
| 4.1.2  | groupId          | Int           | 否   | 群组编号                   |
| 4.1.3  | appName          | String        | 否   | 应用名称                   |
| 4.1.4  | appVersion       | String        | 是   | 应用版本号                 |
| 4.1.5  | appSummary       | String        | 是   | 应用概要介绍               |
| 4.1.6  | genesisBlockHash | String        | 否   | 创世块hash                 |
| 4.1.7  | groupStatus      | Int           | 否   | 群组状态                   |
| 4.1.8  | nodeCount        | Int           | 否   | 节点个数                   |
| 4.1.9  | description      | String        | 否   | 应用描述                   |
| 4.1.10 | createTime       | LocalDateTime | 否   | 落库时间                   |
| 4.1.11 | modifyTime       | LocalDateTime | 否   | 修改时间                   |

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
      "appName": "APP1",
      "appVersion": "v1.0.0",
      "appSummary": "存证",
      "genesisBlockHash": "0x7bc361d7d8e078ea9e8f352f2b856d6ea76ab1b9522f4b09853c861d0ed0779f",
      "groupStatus": 1,
      "nodeCount": 2,
      "description": "test",
      "createTime": "2021-01-11 20:22:35",
      "modifyTime": "2021-01-11 20:31:38"
    },
    {
      "chainId": 1,
      "groupId": 2,
      "appName": "APP2",
      "appVersion": "v1.0.0",
      "appSummary": "供应链",
      "genesisBlockHash": "0x1208de0d47dcba9447d304039d1e4512dd4ce740ec408ef83c5f7ee2aefc7468",
      "groupStatus": 1,
      "nodeCount": 2,
      "description": "test",
      "createTime": "2021-01-11 20:22:36",
      "modifyTime": "2021-01-11 20:31:38"
    }
  ],
  "totalCount": 2
}
```

- 失败：

```
{
    "code": 109000,
    "message": "system exception",
    "data": {}
}
```

## 4 Gas管理模块

### 4.1 查询Gas变更列表

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址：**/gas/list**
- 请求方式：POST
- 返回格式：JSON

#### 请求参数

***1）入参表***

| 序号 | 输入参数    | 类型   | 可为空 | 备注                                       |
| ---- | ----------- | ------ | ------ | ------------------------------------------ |
| 1    | chainId     | Int    | 否     | 链编号                                     |
| 2    | groupId     | int    | 否     | 群组编号                                   |
| 3    | pageSize    | Int    | 否     | 每页记录数                                 |
| 4    | pageNumber  | Int    | 否     | 当前页码                                   |
| 5    | userAddress | String | 是     | 用户地址，传入时查询对应用户               |
| 6    | recordType  | Int    | 是     | 记录类型（0-普通交易消耗，1-充值，2-扣费） |
| 7    | transHash   | String | 是     | 交易hash，传入时查询对应交易               |

***2）入参示例***

```
http://localhost:5009/WeBASE-Data-Collect/gas/list/
```

```
{
  "chainId": 1,
  "groupId": 1,
  "pageNumber": 1,
  "pageSize": 5,
  "userAddress": "0xab9f8bfe240a6970ddc9f7fff717b114c22589ae"
}
```

#### 返回参数 

***1）出参表***

| 序号   | 输出参数       | 类型          |      | 备注                                          |
| ------ | -------------- | ------------- | ---- | --------------------------------------------- |
| 1      | code           | Int           | 否   | 返回码，0：成功 其它：失败                    |
| 2      | message        | String        | 否   | 描述                                          |
| 3      | totalCount     | Int           | 否   | 总记录数                                      |
| 4      | data           | List          | 否   | 列表                                          |
| 4.1    |                | Object        |      | 信息对象                                      |
| 4.1.1  | chainId        | Int           | 否   | 链编号                                        |
| 4.1.2  | groupId        | Int           | 否   | 群组编号                                      |
| 4.1.3  | blockNumber    | BIgInteger    | 否   | 所属块高                                      |
| 4.1.4  | transHash      | String        | 否   | 交易hash                                      |
| 4.1.5  | transIndex     | Int           | 否   | 交易索引                                      |
| 4.1.6  | blockTimestamp | LocalDateTime | 否   | 出块时间                                      |
| 4.1.7  | userAddress    | String        | 否   | 用户地址                                      |
| 4.1.8  | gasValue       | BIgInteger    | 否   | gas变动值                                     |
| 4.1.9  | gasRemain      | BIgInteger    | 否   | gas余额                                       |
| 4.1.10 | recordType     | Int           | 否   | gas记录类型（0-普通交易消耗，1-充值，2-扣费） |
| 4.1.11 | recordMonth    | Int           | 否   | 记录年月                                      |
| 4.1.12 | createTime     | LocalDateTime | 否   | 落库时间                                      |
| 4.1.13 | modifyTime     | LocalDateTime | 否   | 修改时间                                      |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "blockNumber": 91,
      "blockTimestamp": "2021-01-14 20:28:33",
      "gasValue": -30727,
      "createTime": "2021-01-14 20:28:40",
      "modifyTime": "2021-01-14 20:28:40",
      "gasRemain": 28999996440408,
      "id": 277,
      "recordMonth": 202101,
      "chainId": 1,
      "groupId": 1,
      "transHash": "0x35efe48f87d6f8d5b189e3c24109573edc2cf158aa2fa6d2a028df981adc81d3",
      "transIndex": 0,
      "userAddress": "0xab9f8bfe240a6970ddc9f7fff717b114c22589ae",
      "recordType": 0
    }
  ],
  "totalCount": 1
}
```

- 失败：

```
{
    "code": 109000,
    "message": "system exception",
    "data": {}
}
```

### 4.2 查询Gas用户对账信息列表

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址：**/gas/reconciliationlist**
- 请求方式：POST
- 返回格式：JSON

#### 请求参数

***1）入参表***

| 序号 | 输入参数    | 类型   | 可为空 | 备注                         |
| ---- | ----------- | ------ | ------ | ---------------------------- |
| 1    | chainId     | Int    | 否     | 链编号                       |
| 2    | groupId     | int    | 否     | 群组编号                     |
| 3    | pageSize    | Int    | 否     | 每页记录数                   |
| 4    | pageNumber  | Int    | 否     | 当前页码                     |
| 5    | userAddress | String | 是     | 用户地址，传入时查询对应用户 |

***2）入参示例***

```
http://localhost:5009/WeBASE-Data-Collect/gas/reconciliationlist/
```

```
{
  "chainId": 1,
  "groupId": 1,
  "pageNumber": 1,
  "pageSize": 5,
  "userAddress": "0xab9f8bfe240a6970ddc9f7fff717b114c22589ae"
}
```

#### 返回参数 

***1）出参表***

| 序号  | 输出参数             | 类型          |      | 备注                                   |
| ----- | -------------------- | ------------- | ---- | -------------------------------------- |
| 1     | code                 | Int           | 否   | 返回码，0：成功 其它：失败             |
| 2     | message              | String        | 否   | 描述                                   |
| 3     | totalCount           | Int           | 否   | 总记录数                               |
| 4     | data                 | List          | 否   | 列表                                   |
| 4.1   |                      | Object        |      | 信息对象                               |
| 4.1.1 | chainId              | Int           | 否   | 链编号                                 |
| 4.1.2 | groupId              | Int           | 否   | 群组编号                               |
| 4.1.3 | userAddress          | String        | 否   | 用户地址                               |
| 4.1.4 | blockNumber          | BIgInteger    | 否   | 块高（用户对账块高）                   |
| 4.1.5 | transHash            | String        | 是   | 交易hash（异常时展示异常记录交易hash） |
| 4.1.6 | reconciliationStatus | Int           | 否   | 对账状态（0-正常，1-异常）             |
| 4.1.7 | createTime           | LocalDateTime | 否   | 落库时间                               |
| 4.1.8 | modifyTime           | LocalDateTime | 否   | 修改时间                               |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "blockNumber": 91,
      "createTime": "2021-01-19 10:22:51",
      "modifyTime": "2021-01-19 10:22:57",
      "id": 3,
      "chainId": 1,
      "groupId": 1,
      "userAddress": "0xab9f8bfe240a6970ddc9f7fff717b114c22589ae",
      "transHash": null,
      "reconciliationStatus": 0
    }
  ],
  "totalCount": 1
}
```

- 失败：

```
{
    "code": 109000,
    "message": "system exception",
    "data": {}
}
```

### 4.3 查询用户Gas余额

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址：**/gas/queryRemain/{chainId}/{groupId}/{userAddress}**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

| 序号 | 输入参数    | 类型   | 可为空 | 备注     |
| ---- | ----------- | ------ | ------ | -------- |
| 1    | chainId     | Int    | 否     | 链编号   |
| 2    | groupId     | int    | 否     | 群组编号 |
| 3    | userAddress | String | 是     | 用户地址 |

***2）入参示例***

```
http://localhost:5009/WeBASE-Data-Collect/gas/queryRemain/1/1/0xab9f8bfe240a6970ddc9f7fff717b114c22589ae
```

#### 返回参数 

***1）出参表***

| 序号 | 输出参数 | 类型   |      | 备注                       |
| ---- | -------- | ------ | ---- | -------------------------- |
| 1    | code     | Int    | 否   | 返回码，0：成功 其它：失败 |
| 2    | message  | String | 否   | 描述                       |
| 3    | data     | Object | 否   | Gas余额                    |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": 28999996440408
}
```

- 失败：

```
{
    "code": 109000,
    "message": "system exception",
    "data": {}
}
```

## 附录 

### 1. 返回码信息列表

| Code   | message                          | 描述               |
| ------ | -------------------------------- | ------------------ |
| 0      | success                          | 正常               |
| 109000 | system exception                 | 系统异常           |
| 109001 | param exception                  | 请求参数错误       |
| 109002 | database exception               | 数据库异常         |
| 209001 | chain id already exists          | 链编号已经存在     |
| 209002 | chain name already exists        | 链名称已经存在     |
| 209003 | save chain fail                  | 链保存失败         |
| 209004 | invalid chain id                 | 无效链编号         |
| 209005 | invalid encrypt type             | 无效链加密类型     |
| 209006 | chain id not exists              | 链编号不存在       |
| 209101 | wrong host or port               | ip或端口错误       |
| 209102 | invalid front id                 | 无效前置编号       |
| 209103 | not found any front              | 找不到前置         |
| 209104 | front already exists             | 前置已经存在       |
| 209105 | save front fail                  | 前置保存失败       |
| 209106 | request front fail               | 前置请求失败       |
| 209107 | request node exception           | 前置节点请求失败   |
| 209108 | front's encrypt type not matches | 前置类型不匹配     |
| 209109 | invalid block number             | 无效块高           |
| 209110 | invalid node id                  | 无效节点编号       |
| 209201 | invalid group id                 | 无效群组编号       |
| 209301 | user name already exists         | 用户名已存在       |
| 209302 | user address already exists      | 用户地址已存在     |
| 209401 | contract already exists          | 合约已存在         |
| 209402 | invalid contract id              | 无效合约编号       |
| 209403 | contract name cannot be repeated | 合约名重复         |
| 209501 | task is still running            | 任务正在执行       |
| 209502 | block has been reset             | 区块已重置         |
| 209601 | solc js file cannot be empty     | 编译器文件不能为空 |
| 209602 | solc js file already exist       | 编译器文件已存在   |
| 209603 | solc js file not exist           | 编译器文件不存在   |
| 209604 | save solc js file error          | 编译器文件保存失败 |
| 209605 | read solc js file error          | 编译器文件读取失败 |
| 209851 | event not exists                 | 事件不存在         |
| 209852 | event exists                     | 事件已存在         |
| 209853 | save event info fail             | 事件信息保存失败   |
| 209854 | invalid task status              | 无效事件任务状态   |
| 209901 | gas record type not exists       | gas记录类型不存在  |