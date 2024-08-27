# 	接口说明

## 公共header

**注意**：所有接口可以在接口的header中传入流水号

| 序号 | 输入参数 | 类型   | 可空 | 备注                           |
| ---- | -------- | ------ | ---- | ------------------------------ |
| 1    | bizSeqNo | String | 是   | 流水号，不传后台则自动生成一个 |



## 1 配置模块

### 1.1 重置数据

调用后，会重新解析区块。

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址：**/config/resetData**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

无

***2）入参示例***

```
curl -X GET "http://127.0.0.1:43781/wsbcs-chdis/config/resetData" -H "accept: */*" -H "bizSeqNo: dd"
```

#### 返回参数 

***1）出参表***

| 序号 | 输出参数 | 类型   | 可空 | 备注                       |
| ---- | -------- | ------ | ---- | -------------------------- |
| 1    | code     | Int    | 否   | 返回码，0：成功 其它：失败 |
| 2    | message  | String | 否   | 描述                       |
| 3    | data     | object | 是   | 返回信息实体               |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": null,
  "attachment": null,
  "bizSeqNo": "fa7b58353bf84b23a7cbc7f980d2633d",
  "success": true
}
```



## 2 元豆数据模块

### 2.1 查询统计数据

后台数据每10分钟更新一次。

- 接口返回的数据量说明

  元豆总量（totalSupply） =  db发放流水之和 - db销毁流水之和，即所有有效的元豆总量（元豆管理系统+微众世界发行，链上所有的，不包括已销毁的）

  待分享量（totalUsed） =  财政账户db流水，根据是转入还是转出加减算余额，所有财政账户余额之和，即财政账户的待分享元豆量。

  流通量（totalCirculate） =  元豆总量 -  待分享量

  京东卡在库总额（totalCardInLibrary） = 卡在库总额（RMB）

  京东卡在途未入库总额（totalCardInTransit） = 充值总额 - 卡在库总额 - 卡出库总额

  待平账元豆总量（totalSupplyWeworld）= 微众世界发行的未平账的元豆总量：元豆总量 - 京东卡在库总额 * 100 - 京东卡在途未入库总额 * 100

- 需另外计算的数据量说明

  京东E卡总面值 = 元豆总量 / 100（所有在库、在途未入库、待平账入库的京东E卡总面值）

  待平账入库京东E卡 = 待平账元豆总量 / 100

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址：**/data/general**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

无

***2）入参示例***

```
curl -X GET "http://127.0.0.1:43781/wsbcs-chdis/data/general" -H "accept: */*" -H "bizSeqNo: test"
```

#### 返回参数 

***1）出参表***

| 序号   | 输出参数           | 类型   | 可空 | 备注                                       |
| ------ | ------------------ | ------ | ---- | ------------------------------------------ |
| 1      | code               | Int    | 否   | 返回码，0：成功 其它：失败                 |
| 2      | message            | String | 否   | 描述                                       |
| 3      | data               | List   | 是   | 信息列表                                   |
| 3.1    |                    | Object |      | 信息对象                                   |
| 3.1.1  | totalSupply        | Long   | 是   | 元豆总量 = 合约totalSupply                 |
| 3.1.2  | totalUsed          | Long   | 是   | 财政回收量（待分享量）                     |
| 3.1.3  | totalZombie        | Long   | 是   | 僵尸账号总量                               |
| 3.1.4  | totalCirculate     | Long   | 是   | 流通量                                     |
| 3.1.5  | totalSupplyMerge   | Long   | 是   | 订单剩余元豆总额 = 充值总额 - 已出库的总和 |
| 3.1.6  | totalRecharge      | Long   | 是   | 充值总额（在库 + 在途）                    |
| 3.1.7  | totalWithdraw      | Long   | 是   | 提现总额（在库 + 已出库）                  |
| 3.1.8  | totalCardInLibrary | Long   | 是   | 卡总额RMB（在库）                          |
| 3.1.9  | totalCardInTransit | Long   | 是   | 卡总额RMB（在途）                          |
| 3.1.10 | totalSupplyWeworld | Long   | 是   | 待平账元豆总量，即微众世界元豆总量         |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": {
    "totalSupply": 3290000,
    "totalUsed": 180000,
    "totalZombie": 0,
    "totalCirculate": 3110000,
    "totalSupplyMerge": 0,
    "totalRecharge": 10000,
    "totalWithdraw": 60000,
    "totalCardInLibrary": 100,
    "totalCardInTransit": 100
  },
  "attachment": null,
  "bizSeqNo": "fa8506ddf12d4db3922932420b05c4b5",
  "success": true
}
```

### 2.2 查询用户列表（分页）

查询用户地址列表（包括普通用户和管理员），默认解析先后顺序。

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址： **/data/userList/{pageNumber}/{pageSize}**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

| 序号 | 输入参数    | 类型   | 可空 | 备注                   |
| ---- | ----------- | ------ | ---- | ---------------------- |
| 1    | pageNumber  | Int    | 否   | 页码                   |
| 2    | pageSize    | Int    | 否   | 每页条数               |
| 3    | userAddress | String | 是   | 不为空时，查询指定用户 |

***2）入参示例***

```
curl -X GET "http://127.0.0.1:43781/wsbcs-chdis/data/userList/1/1" -H "accept: */*" -H "bizSeqNo: test"
```

#### 返回参数

***1）出参表***

| 序号 | 输出参数       | 类型         | 可空 | 备注                       |
| ---- | -------------- | ------------ | ---- | -------------------------- |
| 1    | code           | Int          | 否   | 返回码，0：成功 其它：失败 |
| 2    | message        | String       | 否   | 错误信息                   |
| 3    | data           | List<Object> | 否   | 返回信息列表               |
| 3.1  | id             | int          | 否   | 编号                       |
| 3.2  | userAddress    | String       | 否   | 用户地址                   |
| 3.3  | blockTimestamp | String       | 否   | 第一次解析时的区块时间     |
| 4    | totalCount     | Int          | 否   | 总数                       |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 1,
      "userAddress": "0x4207820f5f8391073e8b38e8964610c7c5b72170",
      "blockTimestamp": "1704435762348",
      "createTime": 1704438923000,
      "modifyTime": 1704438923000
    }
  ],
  "attachment": null,
  "bizSeqNo": "test",
  "totalCount": 3,
  "success": true
}
```

### 2.3 查询管理员列表（分页）

查询管理员列表，由事件解析得来的和配置的合并。

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址： **/data/managerList/{pageNumber}/{pageSize}**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

| 序号 | 输入参数    | 类型   | 可空 | 备注                   |
| ---- | ----------- | ------ | ---- | ---------------------- |
| 1    | pageNumber  | Int    | 否   | 页码                   |
| 2    | pageSize    | Int    | 否   | 每页条数               |
| 3    | userAddress | String | 是   | 不为空时，查询指定用户 |

***2）入参示例***

```
managerListcurl -X GET "http://127.0.0.1:43781/wsbcs-chdis/data/managerList/1/1" -H "accept: */*" -H "bizSeqNo: test"
```

#### 返回参数

***1）出参表***

| 序号 | 输出参数   | 类型         | 可空 | 备注                       |
| ---- | ---------- | ------------ | ---- | -------------------------- |
| 1    | code       | Int          | 否   | 返回码，0：成功 其它：失败 |
| 2    | message    | String       | 否   | 错误信息                   |
| 3    | data       | List<String> | 否   | 返回管理员列表             |
| 44   | totalCount | Int          | 否   | 总数                       |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": [
  	"0x4207820f5f8391073e8b38e8964610c7c5b72170"
  ],
  "attachment": null,
  "bizSeqNo": "test",
  "totalCount": 3,
  "success": true
}
```

### 2.4 查询用户流水（分页）

​	内部判断查的是管理员还是普通用户。

​	`fromAddress`为`0x0000000000000000000000000000000000000000`时，为管理员发放（issue）操作，`operator`为管理员，`toAddress`为获得者。`toAddress`为`0x0000000000000000000000000000000000000000`时，为销毁（destroy）操作，`operator`为管理员，`fromAddress`为转出者。

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址： **/data/userFlowList/{pageNumber}/{pageSize}?userAddress=**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

| 序号 | 输入参数    | 类型   | 可空 | 备注                   |
| ---- | ----------- | ------ | ---- | ---------------------- |
| 1    | pageNumber  | int    | 否   | 页码                   |
| 2    | pageSize    | int    | 否   | 每页条数               |
| 3    | userAddress | String | 否   | 用户地址               |
| 4    | type        | int    | 是   | 类型：0-转入；1-转出   |
| 5    | fromAddress | String | 是   | from地址               |
| 6    | toAddress   | String | 是   | to地址                 |
| 7    | dateStart   | String | 是   | 日期（如：2023-01-01） |
| 8    | dateEnd     | String | 是   | 日期（如：2023-01-01） |

***2）入参示例***

```
curl -X GET "http://127.0.0.1:43781/wsbcs-chdis/data/userFlowList/1/2?userAddress=0x8615fccafae317512c587e7973daf410d590186b" -H "accept: */*"
```

#### 返回参数

***1）出参表***

| 序号 | 输出参数              | 类型         | 可空 | 备注                                    |
| ---- | --------------------- | ------------ | ---- | --------------------------------------- |
| 1    | code                  | Int          | 否   | 返回码，0：成功 其它：失败              |
| 2    | message               | String       | 否   | 错误信息                                |
| 3    | data                  | List<Object> | 否   | 返回信息列表                            |
| 3.1  | id                    | int          | 否   | 编号                                    |
| 3.2  | amount                | String       | 否   | 交易数量                                |
| 3.3  | blockTimestamp        | Long         | 否   | 区块时间戳                              |
| 3.4  | blockNumber           | String       | 否   | 块高                                    |
| 3.5  | operator              | String       | 否   | 交易发起者                              |
| 3.6  | fromAddress           | String       | 否   | 转出者                                  |
| 3.7  | toAddress             | String       | 否   | 获得者                                  |
| 3.8  | funcName              | String       | 否   | 方法名                                  |
| 3.9  | txIndex               | Int          | 否   | 交易索引                                |
| 3.10 | eventIndex            | Int          | 否   | 事件索引                                |
| 3.11 | balance               | Long         | 否   | 当前余额                                |
| 3.12 | ifRecharge            | bool         | 否   | 是否是充值交易                          |
| 3.13 | ifWithdraw            | bool         | 否   | 是否是兑卡交易                          |
| 3.14 | ifCanceled            | bool         | 否   | 兑卡是否已经取消                        |
| 3.15 | ifLocking             | bool         | 否   | 是否锁定中（true-锁定中；false-已确认） |
| 3.16 | withdrawCardTimestamp | Long         | 否   | 领卡时间戳                              |
| 3.17 | ifRedPacketTransfer   | bool         | 否   | 是否是红包存取                          |
| 4    | totalCount            | Int          | 否   | 总数                                    |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "balance": 910,
      "id": 6,
      "appId": "49",
      "txHash": "0x0761323c604fcb8801f6193b42bdacced4f199226e845f32f633d728c7cf0905",
      "contractAddress": "0xcda12999402eb172ccb81c8532c62024073e7d47",
      "funcName": "issue",
      "operator": "0x4207820f5f8391073e8b38e8964610c7c5b72170",
      "fromAddress": "0x0000000000000000000000000000000000000000",
      "toAddress": "0x8615fccafae317512c587e7973daf410d590186b",
      "amount": 500,
      "blockNumber": 18718,
      "blockTimestamp": 1704859800964,
      "txIndex": 0,
      "eventIndex": 0,
      "ifRecharge": false,
      "ifWithdraw": false,
      "ifLocking": false,
      "ifCanceled": false,
      "ifRedPacketTransfer": false,
      "description": "",
      "createTime": 1704859824000,
      "modifyTime": 1704859824000
    },
    {
      "balance": 410,
      "id": 5,
      "appId": "49",
      "txHash": "0xa9474f77a7e49848c68af46ef2b6f30e6a2c58f630ad7e9590bb4965fec3ac7d",
      "contractAddress": "0xcda12999402eb172ccb81c8532c62024073e7d47",
      "funcName": "send",
      "operator": "0x4207820f5f8391073e8b38e8964610c7c5b72170",
      "fromAddress": "0x4207820f5f8391073e8b38e8964610c7c5b72170",
      "toAddress": "0x8615fccafae317512c587e7973daf410d590186b",
      "amount": 200,
      "blockNumber": 18715,
      "blockTimestamp": "1704681000222",
      "txIndex": 0,
      "eventIndex": 0,
      "ifRecharge": false,
      "ifWithdraw": false,
      "ifLocking": false,
      "ifCanceled": false,
      "ifRedPacketTransfer": false,
      "description": "",
      "createTime": 1704790088000,
      "modifyTime": 1704790088000
    }
  ],
  "attachment": null,
  "bizSeqNo": "c8b9d0e207914255a2b81fb8a26b68f7",
  "totalCount": 4,
  "success": true
}
```

### 2.5 查询发放和销毁流水（分页）

​	查询管理员的发放和销毁流水。

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址： **/data/issueAndDestroyFlowList/{pageNumber}/{pageSize}**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

| 序号 | 输入参数    | 类型   | 可空 | 备注                   |
| ---- | ----------- | ------ | ---- | ---------------------- |
| 1    | pageNumber  | int    | 否   | 页码                   |
| 2    | pageSize    | int    | 否   | 每页条数               |
| 3    | userAddress | String | 否   | 用户地址               |
| 4    | dateStart   | String | 是   | 日期（如：2023-01-01） |
| 5    | dateEnd     | String | 是   | 日期（如：2023-01-01） |

***2）入参示例***

```
curl -X GET "http://127.0.0.1:43781/wsbcs-chdis/data/issueAndDestroyFlowList/1/1?userAddress=0x4207820f5f8391073e8b38e8964610c7c5b72170" -H "accept: */*"
```

#### 返回参数

***1）出参表***

| 序号 | 输出参数       | 类型         | 可空 | 备注                       |
| ---- | -------------- | ------------ | ---- | -------------------------- |
| 1    | code           | Int          | 否   | 返回码，0：成功 其它：失败 |
| 2    | message        | String       | 否   | 错误信息                   |
| 3    | data           | List<Object> | 否   | 返回信息列表               |
| 3.1  | id             | int          | 否   | 编号                       |
| 3.2  | amount         | String       | 否   | 交易数量                   |
| 3.3  | blockTimestamp | Long         | 否   | 区块时间戳                 |
| 3.4  | blockNumber    | String       | 否   | 块高                       |
| 3.5  | operator       | String       | 否   | 交易发起者                 |
| 3.6  | fromAddress    | String       | 否   | 转出者                     |
| 3.7  | toAddress      | String       | 否   | 获得者                     |
| 3.8  | funcName       | String       | 否   | 方法名                     |
| 3.9  | txIndex        | Int          | 否   | 交易索引                   |
| 3.10 | eventIndex     | Int          | 否   | 事件索引                   |
| 3.11 | balance        | Long         | 否   | 当前发放额                 |
| 4    | totalCount     | Int          | 否   | 总数                       |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "balance": 128210,
      "id": 33,
      "appId": "49",
      "txHash": "0x1833338fc5fb5c9b0da577a52e1d500e81ab21650849ad3b68b943deb818a12b",
      "contractAddress": "0xcda12999402eb172ccb81c8532c62024073e7d47",
      "funcName": "issue",
      "operator": "0x4207820f5f8391073e8b38e8964610c7c5b72170",
      "fromAddress": "0x0000000000000000000000000000000000000000",
      "toAddress": "0x38f9b2960753bf86b405ae25fbec0648208d2669",
      "amount": 2000,
      "blockNumber": 18752,
      "blockTimestamp": 1705478413263,
      "txIndex": 0,
      "eventIndex": 0,
      "description": "",
      "createTime": 1705637435000,
      "modifyTime": 1705637435000
    }
  ],
  "attachment": null,
  "bizSeqNo": "dd1d902fc1744a4ab1744a0121bc643a",
  "totalCount": 17,
  "success": true
}
```

### 2.6 查询同步的总发放量

​	db同步的总发放量 = db发放流水之和 - db销毁流水之和 - 僵尸账号db的余额。

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址：**/data/totalSupplyFromSync**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

无

***2）入参示例***

```
curl -X GET "http://127.0.0.1:43781/wsbcs-chdis/data/userBalance?searchValue=1704439124947&type=2&userAddress=0x8615fccafae317512c587e7973daf410d590186b" -H "accept: */*"
```

#### 返回参数 

***1）出参表***

| 序号 | 输出参数 | 类型   | 可空 | 备注                       |
| ---- | -------- | ------ | ---- | -------------------------- |
| 1    | code     | Int    | 否   | 返回码，0：成功 其它：失败 |
| 2    | message  | String | 否   | 描述                       |
| 3    | data     | Object |      | 余额                       |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": 21000,
  "attachment": null,
  "bizSeqNo": "0e6a1239a659400391d9a4ebd281731e",
  "success": true
}
```

### 2.7 查询用户余额

​	根据类型查询用户余额（0-实时；1-块高；2-时间戳）。

​	0：实时从链上查询用户当前余额

​	1：查询截止所传块高的用户流水，根据进出加减，统计余额

​	2：先根据所传时间戳，查询最近匹配的区块时间，获取块高。再根据块高统计余额，同1

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址：**/data/userBalance**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

| 序号 | 输入参数    | 类型   | 可空 | 备注                                  |
| ---- | ----------- | ------ | ---- | ------------------------------------- |
| 1    | type        | Int    | 否   | 查询类型（0-实时；1-块高；2-时间戳）  |
| 2    | userAddress | String | 否   | 用户地址                              |
| 3    | searchValue | String | 是   | 查询值，块高或时间戳，类型为1/2时必传 |

***2）入参示例***

```
curl -X GET "http://127.0.0.1:43781/wsbcs-chdis/data/userBalance?searchValue=1704439124947&type=2&userAddress=0x8615fccafae317512c587e7973daf410d590186b" -H "accept: */*"
```

#### 返回参数 

***1）出参表***

| 序号 | 输出参数 | 类型   | 可空 | 备注                       |
| ---- | -------- | ------ | ---- | -------------------------- |
| 1    | code     | Int    | 否   | 返回码，0：成功 其它：失败 |
| 2    | message  | String | 否   | 描述                       |
| 3    | data     | Object |      | 余额                       |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": 210,
  "attachment": null,
  "bizSeqNo": "0e6a1239a659400391d9a4ebd281731e",
  "success": true
}
```

### 2.8 查询历史统计数据

​	根据类型查询历史数据（1-块高；2-时间戳）。查询到结果，会将结果存到缓存表，下次相同的查询会先查缓存表。重算的话会先删除查询值对应的缓存内容。

- 1：查询截止所传块高的流水，统计发行和回收

- 2：先根据所传时间戳，查询最近匹配的区块时间，获取块高。再根据块高统计，同1

  截止所查块高的统计数据：

- 历史发行量 =  db发放流水之和 - db销毁流水之和

- 历史官方回收量 =  db管理员账号流水，根据是转入还是转出加减算余额，所有管理员余额之和

- 历史僵尸账号总量 =  db僵尸账号账号流水，根据是转入还是转出加减算余额，所有僵尸账号余额之和

- 历史流通量 =  历史发行量 -  历史官方回收量 - 历史僵尸账号总量

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址：**/data/generalHistory**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

| 序号 | 输入参数    | 类型    | 可空 | 备注                         |
| ---- | ----------- | ------- | ---- | ---------------------------- |
| 1    | type        | Int     | 否   | 查询类型（1-块高；2-时间戳） |
| 2    | searchValue | String  | 否   | 查询值，块高或时间戳         |
| 3    | resetEnable | Boolean | 是   | 是否实时重算，默认false      |

***2）入参示例***

```
curl -X GET "http://127.0.0.1:43781/wsbcs-chdis/data/generalHistory?resetEnable=false&searchValue=1704680826363&type=2" -H "accept: */*"
```

#### 返回参数 

***1）出参表***

| 序号  | 输出参数       | 类型   | 可空 | 备注                       |
| ----- | -------------- | ------ | ---- | -------------------------- |
| 1     | code           | Int    | 否   | 返回码，0：成功 其它：失败 |
| 2     | message        | String | 否   | 描述                       |
| 3     | data           | List   | 是   | 信息列表                   |
| 3.1   |                | Object |      | 信息对象                   |
| 3.1.1 | totalSupply    | Long   | 是   | 发行量                     |
| 3.1.2 | totalUsed      | Long   | 是   | 官方回收量                 |
| 3.1.3 | totalZombie    | Long   | 是   | 僵尸账号总量               |
| 3.1.4 | totalCirculate | Long   | 是   | 流通量                     |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": {
    "totalSupply": 10310,
    "totalUsed": 10000,
    "totalZombie": 100,
    "totalCirculate": 210
  },
  "attachment": null,
  "bizSeqNo": "6d0c28f6c9864e948b170309e0232dcd",
  "success": true
}
```

### 2.9 按小时查询数据趋势图

查询某日24小时数据趋势图。

用户量：每小时记录一条，截止上一小时用户数

发行量（总量）：每小时记录一条，截止上一小时发行量

流通量：每小时记录一条，截止上一小时流通量

转移量（交易量）：每小时记录一条，一小时内转移量（不包含充值、兑卡）

#### 传输协议规范

- 网络传输协议：使用HTTP协议
- 请求地址：**/data/trendChartByHour**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

| 序号 | 输入参数 | 类型   | 可空 | 备注                   |
| ---- | -------- | ------ | ---- | ---------------------- |
| 1    | date     | String | 否   | 日期（如：2023-01-01） |

***2）入参示例***

```
curl -X GET "http://127.0.0.1:43781/wsbcs-chdis/data/trendChartByHour?date=2024-01-12" -H "accept: */*"
```

#### 返回参数 

***1）出参表***

| 序号  | 输出参数      | 类型   |      | 备注                       |
| ----- | ------------- | ------ | ---- | -------------------------- |
| 1     | code          | Int    | 否   | 返回码，0：成功 其它：失败 |
| 2     | message       | String | 否   | 描述                       |
| 3     | data          | List   |      | 返回信息                   |
| 3.1   | userList      | List   | 否   | 用户数据列表               |
| 3.1.1 | amount        | Long   | 否   | 数据量                     |
| 3.1.1 | date          | String | 否   | 日期                       |
| 3.1.3 | hour          | Int    | 否   | 小时                       |
| 3.2   | issueList     | List   | 否   | 发行数据列表               |
| 3.2.1 | amount        | Long   | 否   | 数据量                     |
| 3.2.2 | date          | String | 否   | 日期                       |
| 3.2.3 | hour          | Int    | 否   | 小时                       |
| 3.3   | usedList      | List   | 否   | 回收数据列表               |
| 3.3.1 | amount        | Long   | 否   | 数据量                     |
| 3.3.2 | date          | String | 否   | 日期                       |
| 3.3.3 | hour          | Int    | 否   | 小时                       |
| 3.4   | circulateList | List   | 否   | 流通数据列表               |
| 3.4.1 | amount        | Long   | 否   | 数据量                     |
| 3.4.2 | date          | String | 否   | 日期                       |
| 3.4.3 | hour          | Int    | 否   | 小时                       |
| 3.5   | transferList  | List   | 否   | 转移数据列表               |
| 3.5.1 | amount        | Long   | 否   | 数据量                     |
| 3.5.2 | date          | String | 否   | 日期                       |
| 3.5.3 | hour          | Int    | 否   | 小时                       |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": {
    "userList": [
      {
        "amount": 12,
        "date": "2024-01-14",
        "hour": 0
      }
    ],
    "issueList": [
      {
        "amount": 0,
        "date": "2024-01-14",
        "hour": 0
      }
    ],
    "usedList": [
      {
        "amount": 0,
        "date": "2024-01-14",
        "hour": 0
      }
    ],
    "circulateList": [
      {
        "amount": 0,
        "date": "2024-01-14",
        "hour": 0
      }
    ],
    "transferList": [
      {
        "amount": 0,
        "date": "2024-01-14",
        "hour": 0
      }
    ]
  },
  "attachment": null,
  "bizSeqNo": "7de6fba5cfe14070814aca520fa0a1fb",
  "success": true
}
```

### 2.10 按天查询数据趋势图

根据日期区间查询数据趋势图。除转移量之外，数据是累加的，按天查询，查询每日最大的值即可。转移量是每日所有小时数据相加。

用户量：每日最大的用户数。

发行量（总量）：每日最大的发行量。

流通量：每日最大的流通量。

转移量（交易量）：每日的转移量（不包含充值、兑卡）

#### 传输协议规范

- 网络传输协议：使用HTTP协议
- 请求地址：**/data/trendChartByDay**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

| 序号 | 输入参数  | 类型   | 可空 | 备注                       |
| ---- | --------- | ------ | ---- | -------------------------- |
| 1    | dateStart | String | 否   | 开始日期（如：2023-01-01） |
| 2    | dateEnd   | String | 否   | 结束日期（如：2023-01-10） |

***2）入参示例***

```
curl -X GET "http://127.0.0.1:43781/wsbcs-chdis/data/trendChartByDay?dateEnd=2024-01-12&dateStart=2024-01-11" -H "accept: */*"
```

#### 返回参数 

***1）出参表***

| 序号  | 输出参数  | 类型   |      | 备注                       |
| ----- | --------- | ------ | ---- | -------------------------- |
| 1     | code      | Int    | 否   | 返回码，0：成功 其它：失败 |
| 2     | message   | String | 否   | 描述                       |
| 3     | data      | List   |      | 返回信息                   |
| 3.1   | userList  | List   | 否   | 用户数据列表               |
| 3.1.1 | amount    | Long   | 否   | 数据量                     |
| 3.1.1 | date      | String | 否   | 日期                       |
| 3.2   | issueList | List   | 否   | 发行数据列表               |
| 3.2.1 | amount    | Long   | 否   | 数据量                     |
| 3.2.2 | date      | String | 否   | 日期                       |
| 3.3   | usedList  | List   | 否   | 回收数据列表               |
| 3.3.1 | amount    | Long   | 否   | 数据量                     |
| 3.3.2 | date      | String | 否   | 日期                       |
| 3.4   | circulateList | List   | 否   | 流通数据列表               |
| 3.4.1 | amount        | Long   | 否   | 数据量                     |
| 3.4.2 | date          | String | 否   | 日期                       |
| 3.5   | transferList  | List   | 否   | 转移数据列表               |
| 3.5.1 | amount | Long | 否 | 数据量 |
| 3.5.2 | date | String | 否 | 日期 |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": {
    "userList": [
      {
        "amount": 12,
        "date": "2024-01-14",
        "hour": null
      }
    ],
    "issueList": [
      {
        "amount": 0,
        "date": "2024-01-14",
        "hour": null
      }
    ],
    "usedList": [
      {
        "amount": 0,
        "date": "2024-01-14",
        "hour": null
      }
    ],
    "circulateList": [
      {
        "amount": 0,
        "date": "2024-01-14",
        "hour": null
      }
    ],
    "transferList": [
      {
        "amount": 0,
        "date": "2024-01-14",
        "hour": null
      }
    ]
  },
  "attachment": null,
  "bizSeqNo": "7de6fba5cfe14070814aca520fa0a1fb",
  "success": true
}
```

### 2.11 查询数据同步状态

`true`：数据同步中；`false`：数据已同步。

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址：**/data/isLoading**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

无

***2）入参示例***

```
curl -X GET "http://127.0.0.1:43781/occ/data/isLoading" -H "accept: */*"
```

#### 返回参数 

***1）出参表***

| 序号 | 输出参数 | 类型   | 可空 | 备注                       |
| ---- | -------- | ------ | ---- | -------------------------- |
| 1    | code     | Int    | 否   | 返回码，0：成功 其它：失败 |
| 2    | message  | String | 否   | 描述                       |
| 3    | data     | object | 是   | 返回信息                   |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": false,
  "attachment": null,
  "bizSeqNo": "6da8efec435948e3a5c7fe8717b85770",
  "success": true
}
```

### 2.12 查询交易流水（分页）

查询交易流水，供机构钱包使用。

只需要from是机构地址的，就userAddress和fromAddress都传机构地址

只需要to是机构地址的，就userAddress和toAddress都传机构地址

对手交易地址过滤：userAddress传机构地址，transAddress传对手交易地址（from是机构地址，to是对手地址；以及from是对手地址，to是机构地址）

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址： **/data/transFlowList/{pageNumber}/{pageSize}?userAddress=**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

**1）入参表**

| 序号 | 输入参数      | 类型   | 可空 | 备注                     |
| ---- | ------------- | ------ | ---- | ------------------------ |
| 1    | pageNumber    | int    | 否   | 页码                     |
| 2    | pageSize      | int    | 否   | 每页条数                 |
| 3    | userAddress   | String | 否   | 用户地址（机构账户地址） |
| 4    | fromAddress   | String | 是   | 转出地址                 |
| 5    | toAddress     | String | 是   | 接收地址                 |
| 6    | timestamp     | Long   | 是   | 日期时间戳               |
| 7    | txHash        | String | 是   | 交易哈希                 |
| 8    | transAddress  | String | 是   | 交易对手地址             |
| 9    | fromAddressNE | String | 是   | 排除转出地址             |
| 10   | startTime     | Long   | 是   | 开始日期时间戳           |
| 11   | endTime       | Long   | 是   | 截止日期时间戳           |

**2）入参示例**

```
curl -X GET "http://127.0.0.1:43781/wsbcs-chdis/data/transFlowList/1/2?userAddress=0x8615fccafae317512c587e7973daf410d590186b" -H "accept: */*"
```

#### 返回参数

**1）出参表**

| 序号 | 输出参数       | 类型         | 可空 | 备注                       |
| ---- | -------------- | ------------ | ---- | -------------------------- |
| 1    | code           | Int          | 否   | 返回码，0：成功 其它：失败 |
| 2    | message        | String       | 否   | 错误信息                   |
| 3    | data           | List<Object> | 否   | 返回信息列表               |
| 3.1  | txHash         | String       | 否   | 交易哈希                   |
| 3.2  | amount         | String       | 否   | 交易数量                   |
| 3.3  | blockTimestamp | Long         | 否   | 区块时间戳                 |
| 3.4  | blockNumber    | String       | 否   | 块高                       |
| 3.5  | operator       | String       | 否   | 交易发起者                 |
| 3.6  | fromAddress    | String       | 否   | 转出地址                   |
| 3.7  | toAddress      | String       | 否   | 获得地址                   |
| 3.8  | funcName       | String       | 否   | 方法名                     |
| 3.9  | txIndex        | Int          | 否   | 交易索引                   |
| 3.10 | eventIndex     | Int          | 否   | 事件索引                   |
| 4    | totalCount     | Int          | 否   | 总数                       |

**2）出参示例**

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "txHash": "0x0761323c604fcb8801f6193b42bdacced4f199226e845f32f633d728c7cf0905",
      "funcName": "issue",
      "operator": "0x4207820f5f8391073e8b38e8964610c7c5b72170",
      "fromAddress": "0x0000000000000000000000000000000000000000",
      "toAddress": "0x8615fccafae317512c587e7973daf410d590186b",
      "amount": 500,
      "blockNumber": 18718,
      "blockTimestamp": 1704859800964,
      "txIndex": 0,
      "eventIndex": 0
      "createTime": 1704859824000,
      "modifyTime": 1704859824000
    }
  ],
  "attachment": null,
  "bizSeqNo": "c8b9d0e207914255a2b81fb8a26b68f7",
  "totalCount": 4,
  "success": true
}
```

 

### 2.13 根据交易哈希查询流水（批量）

​	传入交易哈希列表进行查询。

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址： **/data/transFlowByTxHashBatch**
- 请求方式：POST
- 返回格式：JSON

#### 请求参数

**1）入参表**

| 序号 | 输入参数   | 类型         | 可空 | 备注         |
| ---- | ---------- | ------------ | ---- | ------------ |
| 1    | txHashList | List<String> | 否   | 交易哈希列表 |

**2）入参示例**

```
curl -X POST "http://127.0.0.1:43781/wsbcs-chdis/data/transFlowByTxHashBatch" -H "accept: */*"
```

#### 返回参数

**1）出参表**

| 序号 | 输出参数       | 类型         | 可空 | 备注                       |
| ---- | -------------- | ------------ | ---- | -------------------------- |
| 1    | code           | Int          | 否   | 返回码，0：成功 其它：失败 |
| 2    | message        | String       | 否   | 错误信息                   |
| 3    | data           | List<Object> | 否   | 返回信息列表               |
| 3.1  | txHash         | String       | 否   | 交易哈希                   |
| 3.2  | amount         | String       | 否   | 交易数量                   |
| 3.3  | blockTimestamp | Long         | 否   | 区块时间戳                 |
| 3.4  | blockNumber    | String       | 否   | 块高                       |
| 3.5  | operator       | String       | 否   | 交易发起者                 |
| 3.6  | fromAddress    | String       | 否   | 转出地址                   |
| 3.7  | toAddress      | String       | 否   | 获得地址                   |
| 3.8  | funcName       | String       | 否   | 方法名                     |
| 3.9  | txIndex        | Int          | 否   | 交易索引                   |
| 3.10 | eventIndex     | Int          | 否   | 事件索引                   |
| 4    | totalCount     | Int          | 否   | 总数                       |

**2）出参示例**

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "txHash": "0x0761323c604fcb8801f6193b42bdacced4f199226e845f32f633d728c7cf0905",
      "funcName": "issue",
      "operator": "0x4207820f5f8391073e8b38e8964610c7c5b72170",
      "fromAddress": "0x0000000000000000000000000000000000000000",
      "toAddress": "0x8615fccafae317512c587e7973daf410d590186b",
      "amount": 500,
      "blockNumber": 18718,
      "blockTimestamp": 1704859800964,
      "txIndex": 0,
      "eventIndex": 0
      "createTime": 1704859824000,
      "modifyTime": 1704859824000
    }
  ],
  "attachment": null,
  "bizSeqNo": "c8b9d0e207914255a2b81fb8a26b68f7",
  "totalCount": 4,
  "success": true
}
```

 

## 3 充值提现模块

### 3.1 查询充值订单记录（分页）

​	支持过滤用户和卡是否入库。

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址： **/data/rechargeOrderList/{pageNumber}/{pageSize}**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

| 序号 | 输入参数      | 类型   | 可空 | 备注                                             |
| ---- | ------------- | ------ | ---- | ------------------------------------------------ |
| 1    | pageNumber    | int    | 否   | 页码                                             |
| 2    | pageSize      | int    | 否   | 每页条数                                         |
| 3    | userAddress   | String | 是   | 用户地址                                         |
| 4    | depositStatus | bool   | 是   | 订单状态（卡是否已入库），0-卡待入库，1-卡已入库 |

***2）入参示例***

```
curl -X GET "http://127.0.0.1:43781/wsbcs-chdis/data/rechargeOrderList/1/1" -H "accept: */*"
```

#### 返回参数

***1）出参表***

| 序号 | 输出参数           | 类型         | 可空 | 备注                       |
| ---- | ------------------ | ------------ | ---- | -------------------------- |
| 1    | code               | Int          | 否   | 返回码，0：成功 其它：失败 |
| 2    | message            | String       | 否   | 错误信息                   |
| 3    | data               | List<Object> | 否   | 返回信息列表               |
| 3.1  | id                 | int          | 否   | 编号                       |
| 3.2  | depositAmount      | String       | 否   | 充值元豆数量               |
| 3.3  | depositTimestamp   | Long         | 否   | 充值区块时间戳             |
| 3.4  | depositBlockNumber | String       | 否   | 充值块高                   |
| 3.5  | depositOrderBiz    | String       | 否   | 充值订单号                 |
| 3.6  | depositAddress     | String       | 否   | 充值用户地址               |
| 3.7  | depositTxHash      | String       | 否   | 充值交易hash               |
| 3.8  | physicalCards      | String       | 否   | 关联的卡列表（json）       |
| 3.9  | description        | Int          | 否   | 描述                       |
| 3.10 | createTime         | Int          | 否   | 入库时间                   |
| 3.11 | modifyTime         | Long         | 否   | 修改时间                   |
| 4    | totalCount         | Int          | 否   | 总数                       |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 1,
      "depositOrderBiz": "1",
      "depositAddress": "0x74a221c4ef6071a5800d9825f4941c2a2864fdcc",
      "depositStatus": true,
      "depositAmount": 20000,
      "depositBlockNumber": 18890,
      "depositTimestamp": 1710313114000,
      "depositTxHash": "0xe6c2550933c2c39eca2b3db451275a2fab979ef9ddca1af40a2d8e1f753d5de0",
      "physicalCards": "[\"card001\",\"card002\"]",
      "description": "recharge",
      "createTime": 1710385240000,
      "modifyTime": 1710385240000
    }
  ],
  "attachment": null,
  "bizSeqNo": "3222a882d84245bfbf686c9f21261760",
  "totalCount": 2,
  "success": true
}
```

### 3.2 查询提现订单记录（分页）

​	支持过滤用户和卡是否出库。

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址： **/data/withdrawOrderList/{pageNumber}/{pageSize}**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

| 序号 | 输入参数       | 类型   | 可空 | 备注                                             |
| ---- | -------------- | ------ | ---- | ------------------------------------------------ |
| 1    | pageNumber     | int    | 否   | 页码                                             |
| 2    | pageSize       | int    | 否   | 每页条数                                         |
| 3    | userAddress    | String | 是   | 用户地址                                         |
| 4    | withdrawStatus | bool   | 是   | 提现状态（卡是否已出库），0-卡未出库，1-卡已出库 |

***2）入参示例***

```
curl -X GET "http://127.0.0.1:43781/wsbcs-chdis/data/withdrawOrderList/1/2?withdrawStatus=true" -H "accept: */*"
```

#### 返回参数

***1）出参表***

| 序号 | 输出参数           | 类型         | 可空 | 备注                       |
| ---- | ------------------ | ------------ | ---- | -------------------------- |
| 1    | code               | Int          | 否   | 返回码，0：成功 其它：失败 |
| 2    | message            | String       | 否   | 错误信息                   |
| 3    | data               | List<Object> | 否   | 返回信息列表               |
| 3.1  | id                 | int          | 否   | 编号                       |
| 3.2  | withdrawAmount      | String       | 否   | 提现元豆数量               |
| 3.3  | withdrawTimestamp   | Long         | 否   | 提现区块时间戳             |
| 3.4  | withdrawBlockNumber | String       | 否   | 提现块高                   |
| 3.5  | withdrawOrderBiz    | String       | 否   | 提现订单号                 |
| 3.6  | withdrawAddress     | String       | 否   | 提现用户地址               |
| 3.7  | withdrawTxHash      | String       | 否   | 提现交易hash               |
| 3.8  | physicalCards      | String       | 否   | 关联的卡列表（json）       |
| 3.9  | description        | Int          | 否   | 描述               |
| 3.10 | createTime         | Int          | 否   | 入库时间                   |
| 3.11 | modifyTime         | Long         | 否   | 修改时间                   |
| 4    | totalCount         | Int          | 否   | 总数                       |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 1,
      "withdrawOrderBiz": "1",
      "withdrawAddress": "0x74a221c4ef6071a5800d9825f4941c2a2864fdcc",
      "withdrawAmount": 10000,
      "withdrawStatus": true,
      "withdrawBlockNumber": 18889,
      "withdrawTimestamp": 1710312432000,
      "withdrawTxHash": "0xab39d0bd9a3c577069e69d845439793b6be353c01d6d4f252a91609c2194b4bd",
      "physicalCards": "[\"card001\"]",
      "description": "test",
      "createTime": 1710385240000,
      "modifyTime": 1710385241000
    },
    {
      "id": 2,
      "withdrawOrderBiz": "2",
      "withdrawAddress": "0x74a221c4ef6071a5800d9825f4941c2a2864fdcc",
      "withdrawAmount": 20000,
      "withdrawStatus": true,
      "withdrawBlockNumber": 18903,
      "withdrawTimestamp": 1710317494000,
      "withdrawTxHash": "0xb6b1f6ebd4a1208af6b5a7e6472c956c38b948fc02dfb7a127df2babe4ddac46",
      "physicalCards": "[\"card002\",\"card003\"]",
      "description": "test",
      "createTime": 1710385241000,
      "modifyTime": 1710385241000
    }
  ],
  "attachment": null,
  "bizSeqNo": "f2c80773b29441adb50f22f8f1587ae3",
  "totalCount": 2,
  "success": true
}
```

### 3.3 查询卡信息（分页）

​	支持过滤卡是否出库。

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址： **/data/rechargeOrderList/{pageNumber}/{pageSize}**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

| 序号 | 输入参数       | 类型 | 可空 | 备注                                         |
| ---- | -------------- | ---- | ---- | -------------------------------------------- |
| 1    | pageNumber     | int  | 否   | 页码                                         |
| 2    | pageSize       | int  | 否   | 每页条数                                     |
| 4    | withdrawStatus | bool | 是   | 状态（卡是否已出库），0-卡未出库，1-卡已出库 |

***2）入参示例***

```
curl -X GET "http://127.0.0.1:43781/wsbcs-chdis/data/cardList/1/1" -H "accept: */*"
```

#### 返回参数

***1）出参表***

| 序号 | 输出参数         | 类型         | 可空 | 备注                                             |
| ---- | ---------------- | ------------ | ---- | ------------------------------------------------ |
| 1    | code             | Int          | 否   | 返回码，0：成功 其它：失败                       |
| 2    | message          | String       | 否   | 错误信息                                         |
| 3    | data             | List<Object> | 否   | 返回信息列表                                     |
| 3.1  | id               | int          | 否   | 编号                                             |
| 3.2  | cardNumber       | String       | 否   | 卡的唯一号码                                     |
| 3.3  | cardAmount       | Long         | 否   | 卡的面额                                         |
| 3.4  | type             | String       | 否   | 卡类型，1-JD卡                                   |
| 3.5  | depositOrderBiz  | String       | 否   | 充值订单号                                       |
| 3.6  | withdrawOrderBiz | String       | 否   | 取现订单号                                       |
| 3.7  | withdrawAddress  | String       | 否   | 取现用户地址                                     |
| 3.8  | withdrawStatus   | String       | 否   | 提现状态（卡是否已出库），0-卡未出库，1-卡已出库 |
| 3.9  | inBlockNumber    | Long         | 否   | 充值区块                                         |
| 3.10 | inTxHash         | String       | 否   | 充值哈希                                         |
| 3.11 | outBlockNumber   | Long         | 否   | 提现区块                                         |
| 3.12 | outTxHash        | String       | 否   | 提现哈希                                         |
| 3.13 | description      | Int          | 否   | 描述                                             |
| 3.14 | createTime       | Int          | 否   | 入库时间                                         |
| 3.15 | modifyTime       | Long         | 否   | 修改时间                                         |
| 4    | totalCount       | Int          | 否   | 总数                                             |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 1,
      "cardNumber": "card001",
      "cardAmount": 100,
      "type": 1,
      "depositOrderBiz": "1",
      "withdrawOrderBiz": "1",
      "withdrawAddress": "0x74a221c4ef6071a5800d9825f4941c2a2864fdcc",
      "withdrawStatus": true,
      "inBlockNumber": 18891,
      "inTxHash": "0x76eb4b6f620e53c867077f462763f22eb42a10b18188479a18c8f5749b1c3cb7",
      "outBlockNumber": 18899,
      "outTxHash": "0xcf0e9132ad6175eaf93fd1e8e4a5e71356790daacf72310066445963694f3afe",
      "description": null,
      "createTime": 1710385240000,
      "modifyTime": 1710385241000
    }
  ],
  "attachment": null,
  "bizSeqNo": "6b04e210090e42958f81f7ada2b0978e",
  "totalCount": 3,
  "success": true
}
```

### 

## 4 链数据模块

### 4.1 查询链概览

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址：**/chain/general**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

***2）入参示例***

```
curl -X GET "http://127.0.0.1:43781/wsbcs-chdis/chain/general" -H "accept: */*" -H "bizSeqNo: dd"
```

#### 返回参数 

***1）出参表***

| 序号 | 输出参数 | 类型   | 可空 | 备注                       |
| ---- | -------- | ------ | ---- | -------------------------- |
| 1    | code     | Int    | 否   | 返回码，0：成功 其它：失败 |
| 2    | message  | String | 否   | 描述                       |
| 3    | data     | Object | 是   | 信返回信息实体             |
| 3.1  | txs      | long   | 是   | 交易数量                   |
| 3.2  | blocks   | long   | 是   | 区块数量                   |

***2）出参示例***

- 成功：

```
{
    "code": 0,
    "data": {
        "txs": 100,
        "blocks": 100
    },
    "attachment": "loading",
    "message": "success",
    "bizSeqNo": "123",
    "success": true
}
```

### 4.2 查询15天交易数据

查询15天内的交易量数据。

#### 传输协议规范

- 网络传输协议：使用HTTP协议
- 请求地址：**/chain/trans15Days**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

***2）入参示例***

```
curl -X GET "http://127.0.0.1:43781/wsbcs-chdis/chain/trans15Days" -H "accept: */*" -H "bizSeqNo: 123"
```

#### 返回参数 

***1）出参表***

| 序号 | 输出参数  | 类型   |      | 备注                       |
| ---- | --------- | ------ | ---- | -------------------------- |
| 1    | code      | Int    | 否   | 返回码，0：成功 其它：失败 |
| 2    | message   | String | 否   | 描述                       |
| 3    | data      | List   |      | 交易信息                   |
| 3.1  | txs       | Int    | 否   | 日交易量                   |
| 3.2  | timestamp | Long   | 否   | 交易日期                   |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "timestamp": 1695711760202,
      "txs": 100
    }
  ],
  "bizSeqNo": "123",
  "success": true
}
```

### 4.3 查询区块列表

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址： **/chain/blockList/{pageNumber}/{pageSize}**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

| 序号 | 输入参数    | 类型 | 可空 | 备注     |
| ---- | ----------- | ---- | ---- | -------- |
| 1    | pageNumber  | int  | 否   | 页码     |
| 2    | pageSize    | int  | 否   | 每页条数 |
| 3    | blockNumber | Int  | 是   | 块高     |

***2）入参示例***

```
curl -X GET "http://127.0.0.1:43781/wsbcs-chdis/chain/blockList/1/1" -H "accept: */*" -H "bizSeqNo: test"
```

#### 返回参数

***1）出参表***

| 序号 | 输出参数       | 类型         | 可空 | 备注                       |
| ---- | -------------- | ------------ | ---- | -------------------------- |
| 1    | code           | Int          | 否   | 返回码，0：成功 其它：失败 |
| 2    | message        | String       | 否   | 错误信息                   |
| 3    | data           | List<Object> | 否   | 返回信息列表               |
| 3.1  | id             | int          | 否   | 编号                       |
| 3.2  | pkHash         | String       | 否   | 区块哈希                   |
| 3.3  | blockTimestamp | Long         | 否   | 区块时间戳                 |
| 3.4  | blockNumber    | Long         | 否   | 块高                       |
| 3.5  | sealer         | String       | 否   | 出块者                     |
| 3.6  | sealerIndex    | int          | 否   | 共识节点index              |
| 3.7  | dateTimeStr    | String       | 否   | 出块时间                   |
| 3.8  | transCount     | int          | 否   | 区块的交易数               |
| 4    | totalCount     | Int          | 否   | 总数                       |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 200,
      "pkHash": "0x8590f97359133e041de6557743d19c9539226715e1b747dc1f4b66c0ad1654ba",
      "blockTimestamp": 1694420949543,
      "blockNumber": 425897,
      "sealerIndex": 1,
      "sealer": "9cfc33f6b6bdf3f9f5c01dfe8eb9edd17fd9889fbf13715ef06a80926e4c8d03111f6aff8a1f946d5c3e7831abb399977dfe83c17b8ee50886c01fa2494b469b",
      "transCount": 1,
      "gasUsed": 77211
    }
  ],
  "bizSeqNo": "test",
  "totalCount": 100,
  "success": true
}
```

### 4.4 查询区块详情

​	根据区块高度获取区块信息

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址：**/chain/getBlockByNumber/{blockNumber}**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

| 序号 | 输入参数    | 类型       | 可空 | 备注                        |
| ---- | ----------- | ---------- | ---- | --------------------------- |
| 1    | blockNumber | BigInteger | 否   | 区块高度                    |
| 2    | pageNumber  | int        | 是   | 交易列表页码，不传默认1     |
| 3    | pageSize    | int        | 是   | 交易列表每页条数，不传默认5 |

***2）入参示例***

```
curl -X GET "http://127.0.0.1:43781/wsbcs-chdis/chain/getBlockByNumber/100?pageNumber=1&pageSize=5" -H "accept: */*"
```

#### 返回参数 

***1）出参表***

| 序号     | 输出参数         | 类型                          | 可空 | 备注                       |
| -------- | ---------------- | ----------------------------- | ---- | -------------------------- |
| 1        | code             | Int                           | 否   | 返回码，0：成功 其它：失败 |
| 2        | message          | String                        | 否   | 描述                       |
| 3        | data             | Object                        |      | 区块信息                   |
| 1        | code             | String                        | 否   | 返回码                     |
| 2        | message          | String                        | 否   | 提示信息                   |
| 3        | data             | Object                        | 否   | 返回数据                   |
| 3.1      | hash             | String                        | 否   | 区块哈希                   |
| 3.2      | parentHash       | String                        | 否   | 父区块哈希                 |
| 3.3      | number           | BigInteger                    | 否   | 区块块高                   |
| 3.4      | transactionsRoot | String                        | 否   | 交易根哈希                 |
| 3.5      | logsBloom        | String                        | 否   | 布隆过滤值                 |
| 3.6      | gasUsed          | Int                           | 否   | Gas消耗                    |
| 3.7      | receiptsRoot     | String                        | 否   | 交易回执根哈希             |
| 3.8      | stateRoot        | String                        | 否   | 交易状态根                 |
| 3.9      | sealer           | int                           | 否   | 区块sealer的index下标      |
| 3.10     | sealerList       | List<String>                  | 否   | 共识的sealer列表           |
| 3.10     | index            | int                           | 否   | 共识的sealer下标           |
| 3.10     | signature        | String                        | 否   | 共识的sealer签名值         |
| 3.11     | timestamp        | String                        | 否   | 区块时间戳                 |
| 3.12     | signatureList    | List<Signature>               | 否   | 区块签名列表               |
| 3.13     | consensusWeights | String                        | 否   | 区块共识权重               |
| 3.14     | transactions     | List<JsonTransactionResponse> | 否   | 区块交易列表               |
| 3.14.1   | chainId          | Bool                          | 否   | 链id                       |
| 3.14.2   | groupId          | String                        | 否   | 群组id                     |
| 3.14.3   | signature        | SignatureResponse             | 否   | 交易签名                   |
| 3.14.3.1 | signature        | String                        | 否   | 签名值本身                 |
| 3.14.3.2 | r                | String                        | 否   | 签名值r                    |
| 3.14.3.3 | s                | String                        | 否   | 签名值s                    |
| 3.14.3.4 | v                | String                        | 否   | 签名值v                    |
| 3.14.4   | hash             | String                        | 否   | 交易哈希                   |
| 3.14.5   | nonce            | Int                           | 否   | 交易none                   |
| 3.14.6   | blockLimit       | Int                           | 否   | 交易blockLimit             |
| 3.14.7   | from             | String                        | 否   | 发送者的地址               |
| 3.14.8   | to               | String                        | 否   | 接收者的地址               |
| 3.14.9   | input            | String                        | 否   | 输入                       |
| 3.15     | transCounts      | Int                           | 否   | 交易数量                   |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": {
    "parentHash": "0x10a1300ba23df56a01f9d5cce257bb540564ee0262edadea1d4f3b000429e4fd",
    "number": 10000,
    "hash": "0xb6350920e26719cd778b2cf6bc6afa76ae4198ba32d2fa56125529ecb3445a44",
    "logsBloom": "0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "transactionsRoot": "0x138971c3588a5f55323d34ee77459f3994258774b558f9c00f55473af1644322",
    "receiptsRoot": "0x4f31276335097bc3eb8a6263c873ef1697382bf5d73410a3a286d13eab0cb762",
    "stateRoot": "0x0000000000000000000000000000000000000000000000000000000000000000",
    "sealer": 2,
    "sealerList": [
      "3390ff0de29591deaf844f651997801a9122fefb3363f0156264a9c6aeadba80e26690bfc26d171fa4dcf9e42d9c51d5c9f80f4aaa6e36f416e6e591f1fbb5ae",
      "5949438664640101c361bde4837451dca5bdd71e590055ff53316ac342e80226427ef6387fec833701cbe4b01ab7cbb75eba9cc27bc1ed32216ed80de7d02c0d",
      "6267410e3bdf7a21c8c9683ce023d30931f79a6970706f21902afb3aa33b634a3c8b0b7fc4cbe74b1a4fb9f49eb6f1a212376ff9fdd64c143bd3874c65c77444",
      "a29f9b0dc0b5013179c6a0a50d684ace0f2a842b51569e3636afd82e1bf1f613a1b6e8c07b761373547dc6f4c16f2d71a6e621691a63fc7454e9dc811f989522"
    ],
    "gasUsed": "0",
    "timestamp": "1657024809690",
    "signatureList": [
      {
        "index": 0,
        "signature": "0xb8d8eb2d93926259fa8ef8ed2b049ac3323b6fbecec1879c00cc55537d8911ae55237bbaf23abb9b1bcbed2d6361654216161a25ff5c928cb4c21fd9357f884601"
      },
      {
        "index": 1,
        "signature": "0x6af24e81ac93ed35dfa89e278fa8753ef68a7c7b75a64a1e8412218c6aababe76bd7c51df460e81f138614f2eb4b4e1aa6289102b4bb9262aad44ce915f6a69100"
      },
      {
        "index": 3,
        "signature": "0xd82a32bca6b979a7ef64cb0c2191833552be0d50a1648d21b8da216d24d31b5f4568ce2a83f72525f9ca2a7d1f550d34ddd47171e9e6971efe69d711dc82822501"
      }
    ],
    "consensusWeights": null,
    "transactions": [
      {
        "chainId": "1",
        "groupId": "1",
        "signature": {
          "r": "0xb00ca85fc7ef1a371721eb8b429d080e89f735ed31640d5601cf1e1e53fad4a6",
          "s": "0x1a5708425b14a8a98752c051cc1edf9db0a809e4abd24e06b9ca276f7ecb516e",
          "v": "0x0",
          "signature": "0xb00ca85fc7ef1a371721eb8b429d080e89f735ed31640d5601cf1e1e53fad4a61a5708425b14a8a98752c051cc1edf9db0a809e4abd24e06b9ca276f7ecb516e00"
        },
        "hash": "0xe17b65c12c56a0fc0f16c46005afe408ddec7d552d9e60edc7f4d7ec6f12a474",
        "nonce": "0x3aed64338df2f8055819996519d5b5d68ca89e0ff99f097652bea5d9d6b58a",
        "blockLimit": 10499,
        "to": "0x11342de7698d9a77cb14c94136a2526ffbb42d10",
        "from": "0x5c242923728d6ea3123f30da86455fae490a3e29",
        "input": "0x4ed3885e0000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000667686a6668670000000000000000000000000000000000000000000000000000",
        "extraData": "0x"
      }
    ],
    "transCounts": 1
  },
  "bizSeqNo": "1f1a30662e824847a98c86b3741cb7ec",
  "success": true
}
```

### 4.5 查询交易列表

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址： **/chain/transList/{pageNumber}/{pageSize}**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

| 序号 | 输入参数    | 类型   | 可空 | 备注     |
| ---- | ----------- | ------ | ---- | -------- |
| 1    | pageNumber  | int    | 否   | 页码     |
| 2    | pageSize    | int    | 否   | 每页条数 |
| 3    | transHash   | String | 是   | 交易hash |
| 4    | blockNumber | String | 是   | 块高     |

***2）入参示例***

```
curl -X GET "http://127.0.0.1:43781/wsbcs-chdis/chain/transList/1/1" -H "accept: */*" -H "bizSeqNo: dd"
```

#### 返回参数

***1）出参表***

| 序号 | 输出参数       | 类型         | 可空 | 备注                       |
| ---- | -------------- | ------------ | ---- | -------------------------- |
| 1    | code           | Int          | 否   | 返回码，0：成功 其它：失败 |
| 2    | message        | String       | 否   | 错误信息                   |
| 3    | data           | List<Object> | 否   | 返回信息列表               |
| 3.1  | transHash      | String       | 否   | 交易哈希                   |
| 3.2  | transNumber    | Long         | 否   | 交易编号                   |
| 3.3  | blockNumber    | Long         | 否   | 块高                       |
| 3.4  | transFrom      | String       | 否   | 交易发起地址               |
| 3.5  | transTo        | String       | 否   | 交易目标地址               |
| 3.6  | blockTimestamp | Long         | 否   | 区块时间戳                 |
| 3.7  | gasUsed        | Long         | 否   | 消耗的gas                  |
| 4    | totalCount     | Int          | 否   | 总数                       |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 199,
      "groupId": "1",
      "transHash": "0x444f11526b7a8f932719c7e068864d036d383c9ee63ce94fa0708bb6103808df",
      "transNumber": null,
      "blockNumber": 425897,
      "transFrom": "0x3ff32862c1d7a04e53011ca4251741255a4d0b30",
      "transTo": "0xdb6d33c885018e86468152ec16d0a50760c86d23",
      "blockTimestamp": 1694420949543,
      "gasUsed": 77211,
      "status": 0,
      "extraData": "0x32317c57475831"
    }
  ],
  "bizSeqNo": "dd",
  "totalCount": 100,
  "success": true
}
```

### 4.6 查询交易回执信息

​	根据交易hash获取交易回执信息。

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址：**/chain/transactionReceipt/{transHash}**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

| 序号 | 输入参数  | 类型   | 可空 | 备注     |
| ---- | --------- | ------ | ---- | -------- |
| 1    | transHash | String | 否   | 交易hash |

***2）入参示例***

```
curl -X GET "http://127.0.0.1:43781/wsbcs-chdis/chain/transactionReceipt/0x444f11526b7a8f932719c7e068864d036d383c9ee63ce94fa0708bb6103808df" -H "accept: */*" -H "bizSeqNo: dd"
```

#### 返回参数 

***1）出参表***

| 序号   | 输出参数        | 类型                  | 可空 | 备注                       |
| ------ | --------------- | --------------------- | ---- | -------------------------- |
| 1      | code            | Int                   | 否   | 返回码，0：成功 其它：失败 |
| 2      | message         | String                | 否   | 描述                       |
| 3      | data            | Object                |      | 交易回执信息               |
| 3.1    | constant        | Bool                  | 否   | 调用类型                   |
| 3.2    | queryInfo       | String                | 否   | 查询信息                   |
| 3.3    | transactionHash | String                | 否   | 交易哈希                   |
| 3.4    | blockHash       | String                | 否   | 块哈希                     |
| 3.5    | blockNumber     | Int                   | 否   | 块高                       |
| 3.6    | gasUsed         | Int                   | 否   | Gas使用值                  |
| 3.7    | status          | String                | 否   | 交易状态                   |
| 3.8    | from            | String                | 否   | 发送者的地址               |
| 3.9    | to              | String                | 否   | 接收者的地址               |
| 3.10   | input           | String                | 否   | 输入                       |
| 3.11   | output          | String                | 否   | 输出                       |
| 3.12   | contractAddress | String                | 否   | 合约地址                   |
| 3.13   | message         | String                | 否   | 交易回执状态信息           |
| 3.14   | logsBloom       | String                | 否   | 布隆过滤器值               |
| 3.15   | logs            | List<Logs>            | 否   | 事件EventLog               |
| 3.15.1 | address         | String                | 否   | Logs地址                   |
| 3.15.2 | topics          | List<String>          | 否   | Logs topic列表             |
| 3.15.3 | data            | String                | 否   | Logs 数据                  |
| 3.15.4 | blockNumber     | String                | 否   | Logs 块高                  |
| 3.16   | txProof         | List<MerkleProofUnit> | 否   | 交易证明                   |
| 3.16.1 | left            | List<String>          | 否   | MerkleProofUnit 左证明     |
| 3.16.1 | right           | List<String>          | 否   | MerkleProofUnit 右证明     |
| 3.17   | receiptProof    | String                | 否   | 回执证明                   |
| 3.17.1 | left            | List<String>          | 否   | MerkleProofUnit 左证明     |
| 3.17.1 | right           | List<String>          | 否   | MerkleProofUnit 右证明     |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": {
    "constant": false,
    "queryInfo": "",
    "transactionHash": "0x444f11526b7a8f932719c7e068864d036d383c9ee63ce94fa0708bb6103808df",
    "blockHash": "0x8590f97359133e041de6557743d19c9539226715e1b747dc1f4b66c0ad1654ba",
    "blockNumber": 425897,
    "gasUsed": 77211,
    "status": "0",
    "from": "0x3ff32862c1d7a04e53011ca4251741255a4d0b30",
    "to": "0xdb6d33c885018e86468152ec16d0a50760c86d23",
    "input": "0x78ac81c6000000000000000000000000000000000000000000000000000000000000004000000000000000000000000000000000000000000000000000000000000000a00000000000000000000000000000000000000000000000000000000000000002000000000000000000000000eb972401477d5e7c2786141eb017c7adafd5a1c70000000000000000000000000fd64e3434363bc6a628e94db7f25e16ca5e08dd0000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000006500000000000000000000000000000000000000000000000000000000000000c9",
    "output": "0x",
    "contractAddress": "0x0000000000000000000000000000000000000000",
    "message": "Success",
    "logs": [
      {
        "address": "0xdb6d33c885018e86468152ec16d0a50760c86d23",
        "topics": [
          "0x9d20565718265f97488ca9219f6dc4582917219c9c3afce9fe56b6da3d3656ea",
          "0x4d446f7400000000000000000000000000000000000000000000000000000000",
          "0x6973737565000000000000000000000000000000000000000000000000000000"
        ],
        "data": "0x0000000000000000000000003ff32862c1d7a04e53011ca4251741255a4d0b300000000000000000000000000000000000000000000000000000000000000000000000000000000000000000eb972401477d5e7c2786141eb017c7adafd5a1c70000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000006500000000000000000000000000000000000000000000000000000000000000e0000000000000000000000000db6d33c885018e86468152ec16d0a50760c86d230000000000000000000000000000000000000000000000000000000000000000",
        "blockNumber": null
      },
      {
        "address": "0xdb6d33c885018e86468152ec16d0a50760c86d23",
        "topics": [
          "0x9d20565718265f97488ca9219f6dc4582917219c9c3afce9fe56b6da3d3656ea",
          "0x4d446f7400000000000000000000000000000000000000000000000000000000",
          "0x6973737565000000000000000000000000000000000000000000000000000000"
        ],
        "data": "0x0000000000000000000000003ff32862c1d7a04e53011ca4251741255a4d0b3000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000fd64e3434363bc6a628e94db7f25e16ca5e08dd000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000c900000000000000000000000000000000000000000000000000000000000000e0000000000000000000000000db6d33c885018e86468152ec16d0a50760c86d230000000000000000000000000000000000000000000000000000000000000000",
        "blockNumber": null
      }
    ],
    "logsBloom": "0x00000000000000000000000000000000000000000000000000000001000000000000000000000000000040000000000000000000000000000000000000000000008000000000000000000000001000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000000000000000000000000000400000400000000000000000000000800000000000000000000000000000000002000000000000000004000000000000000000000200000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "txProof": null,
    "receiptProof": null
  },
  "bizSeqNo": "dd",
  "success": true
}
```

### 4.7 查询交易详情

​	根据交易hash获取交易信息。

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址：**/chain/transInfo/{transHash}**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

| 序号 | 输入参数  | 类型   | 可空 | 备注     |
| ---- | --------- | ------ | ---- | -------- |
| 1    | transHash | String | 否   | 交易hash |

***2）入参示例***

```
curl -X GET "http://127.0.0.1:43781/wsbcs-chdis/chain/transInfo/0x444f11526b7a8f932719c7e068864d036d383c9ee63ce94fa0708bb6103808df" -H "accept: */*" -H "bizSeqNo: ss"
```

#### 返回参数 

***1）出参表***

| 序号  | 输出参数   | 类型              | 可空 | 备注                                  |
| ----- | ---------- | ----------------- | ---- | ------------------------------------- |
| 1     | code       | Int               | 否   | 返回码，0：成功 其它：失败            |
| 2     | message    | String            | 否   | 描述                                  |
| 3     | data       | Object            |      | 交易信息                              |
| 3.1   | chainId    | String            | 否   | 链id                                  |
| 3.2   | groupId    | String            | 否   | 群组id                                |
| 3.3   | signature  | SignatureResponse | 否   | 交易签名                              |
| 3.3.1 | signature  | String            | 否   | 签名值本身                            |
| 3.3.2 | r          | String            | 否   | 签名值r                               |
| 3.3.3 | s          | String            | 否   | 签名值s                               |
| 3.3.4 | v          | String            | 否   | 签名值v                               |
| 3.4   | hash       | String            | 否   | 交易哈希                              |
| 3.5   | nonce      | String            | 否   | 交易none                              |
| 3.6   | blockLimit | Int               | 否   | 交易blockLimit                        |
| 3.8   | from       | String            | 否   | 发送者的地址                          |
| 3.9   | to         | String            | 否   | 接收者的地址                          |
| 3.10  | input      | String            | 否   | 输入                                  |
| 3.11  | extraData  | String            | 否   | 扩展数据                              |
| 3.12  | appId      | String            | 否   | 应用编号                              |
| 3.13  | appName    | String            | 否   | 应用名称                              |
| 3.14  | isDecChain | bool              | 是   | 是否数据快递链：是-to和应用不支持跳转 |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": {
    "groupId": "1",
    "signature": {
      "r": "0x336f411a95959923cab57902f71d419fadfed2b06abdc7996bfa852968bbc448",
      "s": "0x6029a2ca57ed31d9ff2d1ba5f82465ad8c5fed72196863557ea101d5e6e9c85f",
      "v": "0x0",
      "signature": "0x336f411a95959923cab57902f71d419fadfed2b06abdc7996bfa852968bbc4486029a2ca57ed31d9ff2d1ba5f82465ad8c5fed72196863557ea101d5e6e9c85f00"
    },
    "hash": "0x444f11526b7a8f932719c7e068864d036d383c9ee63ce94fa0708bb6103808df",
    "nonce": "0x335e8a213c3f0f867d59a03790d88c4b7e549202136c67baa89b698f2752d26",
    "blockLimit": 426396,
    "to": "0xdb6d33c885018e86468152ec16d0a50760c86d23",
    "from": "0x3ff32862c1d7a04e53011ca4251741255a4d0b30",
    "input": "0x78ac81c6000000000000000000000000000000000000000000000000000000000000004000000000000000000000000000000000000000000000000000000000000000a00000000000000000000000000000000000000000000000000000000000000002000000000000000000000000eb972401477d5e7c2786141eb017c7adafd5a1c70000000000000000000000000fd64e3434363bc6a628e94db7f25e16ca5e08dd0000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000006500000000000000000000000000000000000000000000000000000000000000c9",
    "extraData": "38|explorer",
    "appId": "38",
    "appName": "explorer",
    "isDecChain": "true"
  },
  "bizSeqNo": "ss",
  "success": true
}
```

### 4.8 查询合约方法ABI

​	通过methodId查询合约方法ABI。

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址：**/chain/methodAbi/{methodId}**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

| 序号 | 输入参数 | 类型   | 可空 | 备注                      |
| ---- | -------- | ------ | ---- | ------------------------- |
| 1    | methodId | String | 否   | 方法Id（input截取前十位） |

***2）入参示例***

```
curl -X GET "http://127.0.0.1:43781/wsbcs-chdis/chain/methodAbi/0xb16c6ee7" -H "accept: */*" -H "bizSeqNo: dd"
```

#### 返回参数 

***1）出参表***

| 序号 | 输出参数   | 类型   | 可空 | 备注                       |
| ---- | ---------- | ------ | ---- | -------------------------- |
| 1    | code       | Int    | 否   | 返回码，0：成功 其它：失败 |
| 2    | message    | String | 否   | 描述                       |
| 3    | data       | Object |      | 返回信息                   |
| 3.1  | abiInfo    | String | 否   | 方法abi（json）            |
| 3.2  | methodType | String | 否   | 方法类型                   |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": {
    "methodId": "0xb16c6ee7",
    "methodType": "function",
    "abiInfo": "{\"constant\":true,\"inputs\":[{\"name\":\"hash\",\"type\":\"String\"}],\"name\":\"getEvidence\",\"outputs\":[{\"name\":\"\",\"type\":\"String\"},{\"name\":\"\",\"type\":\"String\"},{\"name\":\"\",\"type\":\"String\"},{\"name\":\"\",\"type\":\"String\"},{\"name\":\"\",\"type\":\"String\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"}"
  },
  "bizSeqNo": "dd",
  "success": true
}
```

### 4.9 搜索交易哈希/块高

​	根据交易哈希或块高进行搜索，返回区块详情【参考1.4】或交易详情【参考1.7】。

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址：**/chain/search/{input}**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

| 序号 | 输入参数 | 类型   | 可空 | 备注           |
| ---- | -------- | ------ | ---- | -------------- |
| 1    | input    | String | 否   | 交易哈希或块高 |

***2）入参示例***

```
curl -X GET "http://127.0.0.1:43781/wsbcs-chdis/chain/search/1000" -H "accept: */*" -H "bizSeqNo: dd"
```

#### 返回参数 

***1）出参表***

| 序号 | 输出参数 | 类型   | 可空 | 备注                                     |
| ---- | -------- | ------ | ---- | ---------------------------------------- |
| 1    | code     | Int    | 否   | 返回码，0：成功 其它：失败               |
| 2    | message  | String | 否   | 描述                                     |
| 3    | data     | object | 是   | 区块详情【参考1.4】或交易详情【参考1.7】 |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": {
    "number": 1000,
    "hash": "0x4dba19b151047540af13e0489728904b78be6f4d6f4effc848353817162f6996",
    "parentHash": "0xf0318c2518531920025ed48cd21d96d6072c521dd8b325c279a66806d203adeb",
    "logsBloom": "0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "transactionsRoot": "0x493c05d428aed26e719345a588d3f9434f7db49e2ca2be66be50a8208b468cb5",
    "receiptsRoot": "0x9ca740eb6b9e82704dc339190fb646d9815c1fc5f428be5c78859027f362f49c",
    "dbHash": "0x7171459f8094cd70631420d5030a59f928ebf80fde9adf7a5b3f0155311d7697",
    "stateRoot": "0x7171459f8094cd70631420d5030a59f928ebf80fde9adf7a5b3f0155311d7697",
    "sealer": "0x1",
    "sealerList": [
      "739e43de1e4a7b91211faf048c68761ab4a53db9a128455479fa5623ffb4ced355164cd8d2c345e8bcc7cfb4ccd4d9cec9dd951492d4d5a2f4c7feef879705c8",
      "9cfc33f6b6bdf3f9f5c01dfe8eb9edd17fd9889fbf13715ef06a80926e4c8d03111f6aff8a1f946d5c3e7831abb399977dfe83c17b8ee50886c01fa2494b469b"
    ],
    "extraData": [],
    "gasLimit": "0",
    "gasUsed": "0",
    "timestamp": "1658232027616",
    "signatureList": [
      {
        "index": "0x0",
        "signature": "0x23187d5401cc11a8c4361001e12fbaa0ca98d1bdbbee4761cd1aafbdf9172af6205fd38074685eb0eb7c8acfe2e8d5b458d8c86689437ff3da3e868c54665a3101"
      },
      {
        "index": "0x1",
        "signature": "0x556ca7d544b22113a53123a6bc6d9a179cc1f3dab700f9ae19889da0d75c8cb40ddb4f39644468d32718aa3636120cfb20fa05e77aacd35375f053de337124de01"
      }
    ],
    "transactions": [
      {
        "blockHash": "0x4dba19b151047540af13e0489728904b78be6f4d6f4effc848353817162f6996",
        "blockNumber": 1000,
        "from": "0xb08c0992049bd635856bba1bfff6a993b3dce668",
        "gas": "0x419ce0",
        "hash": "0x79f17cfc19283f25b6e6351df6ca4281299a17dc34a4b40f20ac1db5069243c7",
        "input": "0x1f6320d30000000000000000000000007c64a3e71dfa5036cecbe083a598277743808cee00000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000080000000000000000000000000000000000000000000000000000000000000026000000000000000000000000000000000000000000000000000000000000001a27b22636f6e74656e7448617368223a2262363861326437636136663135313065613735343130383862613434646466643766616139303431383132313530386631373830333266353730623832646135222c22617574686f72223a2267656f6666726d61656e65284de69f90e69f90e69f90e69f90e69f90e69f90e69f90e69f90e69f90e69f90e69f90e69f9029222c22636f6e74656e744e616d65223a22e5ad90e7b1bbe8bf87e69c9f222c226d65746144617461223a227b5c22636f6c6c656374696f6e4e616d655c223a5c22e5ad90e7b1bbe8bf87e69c9f5c222c5c2270726f6a6563744e6f5c223a5c22303930385c222c5c22636f6c6c656374696f6e4e6f5c223a5c22303930383234373345384542424342455c222c5c22646349645c223a5c22303930385f325c222c5c22636f6c6c656374696f6e436f64655c223a5c2230393038303030325c222c5c227075626c69736854696d655c223a5c22323032322d30372d31392032303a30303a32375c222c5c22637265617469766554696d655c223a5c22323032322d30372d31392032303a30303a31395c227d227d000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000003c497373756520636f6c6c656374696f6e20746f203763363461336537316466613530333663656362653038336135393832373737343338303863656500000000",
        "nonce": "0xd2e0aa0f7ee8d0628af631330516e53ddaaac3494384a5b93e5b62233a4c6c",
        "to": "0x2fe57c8c5353465b52718d3ca5ce0c72cf08de9b",
        "transactionIndex": "0x0",
        "value": "0x0",
        "gasPrice": "0x51f4d5c00",
        "blockLimit": "0x4af",
        "chainId": "0x1",
        "groupId": "0x1",
        "extraData": "0x",
        "signature": {
          "r": "0x2d680e6c27921426968bdba9df242accc10d100a17e90432cc73c45b89fc8922",
          "s": "0x7a00dea42d7aa771d3b5ded706e5088b35864632585d13b23b38a2c71e55007e",
          "v": "0x0",
          "signature": "0x2d680e6c27921426968bdba9df242accc10d100a17e90432cc73c45b89fc89227a00dea42d7aa771d3b5ded706e5088b35864632585d13b23b38a2c71e55007e00"
        }
      }
    ]
  },
  "bizSeqNo": "dd",
  "success": true
}
```

### 4.10 查询合约完整ABI

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址：**/chain/contractAbi**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

| 序号 | 输入参数        | 类型   | 可空 | 备注     |
| ---- | --------------- | ------ | ---- | -------- |
| 1    | contractAddress | String | 否   | 合约地址 |

***2）入参示例***

```
curl -X GET "http://127.0.0.1:43781/wsbcs-chdis/chain/contractAbi?contractAddress=0x284c2e3e6e1efe7a9ac8bf046270f105fb613224" -H "accept: */*"
```

#### 返回参数 

***1）出参表***

| 序号 | 输出参数 | 类型   | 可空 | 备注                       |
| ---- | -------- | ------ | ---- | -------------------------- |
| 1    | code     | Int    | 否   | 返回码，0：成功 其它：失败 |
| 2    | message  | String | 否   | 描述                       |
| 3    | data     | Object | 是   | 信息对象                   |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": "",
  "bizSeqNo": "d9c91773aafd4285b165bd1c70716118",
  "success": true
}
```

### 4.11 查询合约Bin

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址：**/chain/contractBin**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

| 序号 | 输入参数        | 类型   | 可空 | 备注     |
| ---- | --------------- | ------ | ---- | -------- |
| 1    | contractAddress | String | 否   | 合约地址 |

***2）入参示例***

```
curl -X GET "http://127.0.0.1:43781/wsbcs-chdis/chain/contractBin?contractAddress=0x284c2e3e6e1efe7a9ac8bf046270f105fb613224" -H "accept: */*"
```

#### 返回参数 

***1）出参表***

| 序号 | 输出参数 | 类型   | 可空 | 备注                       |
| ---- | -------- | ------ | ---- | -------------------------- |
| 1    | code     | Int    | 否   | 返回码，0：成功 其它：失败 |
| 2    | message  | String | 否   | 描述                       |
| 3    | data     | Object | 是   | 信息对象                   |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": "0x60806040526004361061004c576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680638f0644d214610051578063b11b68831461016d575b600080fd5b34801561005d57600080fd5b506100b8600480360381019080803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091929192905050506101fd565b60405180806020018481526020018373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001828103825285818151815260200191508051906020019080838360005b83811015610130578082015181840152602081019050610115565b50505050905090810190601f16801561015d5780820380516001836020036101000a031916815260200191505b5094505050505060405180910390f35b34801561017957600080fd5b506101826103a4565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156101c25780820151818401526020810190506101a7565b50505050905090810190601f1680156101ef5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b60606000808360009080519060200190610218929190610446565b507f555ef627aec601c26684393f2ea1c0496d8f65eef407ce4c8bb9a4f06b578bd66000606460405180806020018381526020018281038252848181546001816001161561010002031660029004815260200191508054600181600116156101000203166002900480156102cd5780601f106102a2576101008083540402835291602001916102cd565b820191906000526020600020905b8154815290600101906020018083116102b057829003601f168201915b5050935050505060405180910390a16000606473284c2e3e6e1efe7a9ac8bf046270f105fb613224828054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561038a5780601f1061035f5761010080835404028352916020019161038a565b820191906000526020600020905b81548152906001019060200180831161036d57829003601f168201915b505050505092508191508090509250925092509193909250565b606060008054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561043c5780601f106104115761010080835404028352916020019161043c565b820191906000526020600020905b81548152906001019060200180831161041f57829003601f168201915b5050505050905090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061048757805160ff19168380011785556104b5565b828001600101855582156104b5579182015b828111156104b4578251825591602001919060010190610499565b5b5090506104c291906104c6565b5090565b6104e891905b808211156104e45760008160009055506001016104cc565b5090565b905600a165627a7a72305820a397d85977ca17d894015cbb457d16c5c0cdfe94816c0dbab676bfa1614192230029",
  "bizSeqNo": "7380e873905c4626ba876dc365db62e2",
  "success": true
}
```

### 4.12 查询交易失败输出信息

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址：**/chain/revertMessageParser**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

| 序号 | 输入参数 | 类型   | 可空 | 备注     |
| ---- | -------- | ------ | ---- | -------- |
| 1    | output   | String | 否   | 输出信息 |

***2）入参示例***

```
curl -X GET "http://127.0.0.1:43781/wsbcs-chdis/chain/revertMessageParser?output=0x08c379a00000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000002d4241433732313a2063616c6c6572206973206e6f7420746f6b656e206f776e6572206f7220617070726f76656400000000000000000000000000000000000000" -H "accept: */*" -H "bizSeqNo: dd"
```

#### 返回参数 

***1）出参表***

| 序号 | 输出参数 | 类型   | 可空 | 备注                       |
| ---- | -------- | ------ | ---- | -------------------------- |
| 1    | code     | Int    | 否   | 返回码，0：成功 其它：失败 |
| 2    | message  | String | 否   | 描述                       |
| 3    | data     | Object | 是   | 信息对象                   |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": "BAC721: caller is not token owner or approved",
  "attachment": null,
  "bizSeqNo": "dd",
  "success": true
}
```

### 4.13 查询合约方法ABI列表

​	查询合约方法ABI列表。

#### 传输协议

- 网络传输协议：使用HTTP协议
- 请求地址：**/chain/methodAbiList**
- 请求方式：GET
- 返回格式：JSON

#### 请求参数

***1）入参表***

| 序号 | 输入参数 | 类型   | 可空 | 备注                      |
| ---- | -------- | ------ | ---- | ------------------------- |
| 1    | methodId | String | 否   | 方法Id（input截取前十位） |
| 2    | topicCnt | int    | 是   | topic数量                 |

***2）入参示例***

```
curl -X GET "http://127.0.0.1:43781/wsbcs-chdis/chain/methodAbiList?methodId=0xd94d5ba6&topicCnt=" -H "accept: */*"
```

#### 返回参数 

***1）出参表***

| 序号  | 输出参数   | 类型   | 可空 | 备注                       |
| ----- | ---------- | ------ | ---- | -------------------------- |
| 1     | code       | Int    | 否   | 返回码，0：成功 其它：失败 |
| 2     | message    | String | 否   | 描述                       |
| 3     | data       | List   | 是   | 信息列表                   |
| 3.1   |            | Object |      | 信息对象                   |
| 3.1.1 | name       | String | 是   | 方法名                     |
| 3.1.2 | methodId   | String | 是   | 方法ID                     |
| 3.1.3 | methodType | String | 是   | 方法类型                   |
| 3.1.4 | abiInfo    | String | 是   | abi信息                    |
| 3.1.5 | indexCnt   | int    | 是   | index个数                  |

***2）出参示例***

- 成功：

```
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 33,
      "name": "newEvidence",
      "methodId": "0xd94d5ba6",
      "methodType": "function",
      "abiInfo": "{\"constant\":false,\"inputs\":[{\"name\":\"hash\",\"type\":\"string\"},{\"name\":\"remark\",\"type\":\"string\"},{\"name\":\"reserve1\",\"type\":\"string\"},{\"name\":\"reserve2\",\"type\":\"string\"},{\"name\":\"reserve3\",\"type\":\"string\"}],\"name\":\"newEvidence\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}",
      "indexCnt": 0
    }
  ],
  "attachment": null,
  "bizSeqNo": "41c25d38f7b0455dbd889e13bc631cbb",
  "success": true
}
```

