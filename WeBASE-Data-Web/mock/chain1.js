import Mock from 'mockjs'

const data = Mock.mock({
    'items|30': [{
        chainId: '@integer(1000, 1100)',
        chainName: '国密',
        chainType: '@integer(0, 1)',
        createTime: '@datetime',
    }]
})
var groupData = Mock.mock({
    'items|30': [{
        groupId: '@integer(1, 1100)',
        groupName: 'group1',
        nodeCount: '@integer(1, 10)',
        createTime: '@datetime',
    }]
})
var overviewData = Mock.mock({
    'contractCount': 1,
    'nodeCount': 10,
    'transactionCount': 2,
    'latestBlock': 2,
})
var nodeList = Mock.mock({
    'items|10': [{
        blockNumber: '@integer(1, 1100)',
        "nodeId|31": /[a-z]{2}[A-Z]{2}[0-9]/,
        pbftView: '@integer(1, 99990)',
        createTime: '@datetime',
        nodeActive: '@integer(1, 2)'
    }]
})
var txInfo = Mock.mock({
    'items|2': [{
        blockNumber: '@integer(1, 110)',
        transHash: '0x5f195c380a776b4cfc344c1c361d6d4fc4c7af18de36f3825a0b00b10768d99a',
        blockTimestamp: '@datetime',
    }]
})
var blockInfo = Mock.mock({
    'items|2': [{
        blockNumber: '@integer(1, 110)',
        pkHash: '0xa0e4298bf15407f43d6af6375120bab3a1db72ab77cc37801b46037829093957',
        blockTimestamp: '@datetime',
        transCount: '@integer(1, 10)'
    }]
})
export default [
    {
        url: '/chain1/list',
        type: 'get',
        response: config => {
            const items = data.items
            return {
                code: 20000,
                data: {
                    total: items.length,
                    items: items
                }
            }
        }
    },
    {
        url: '/group/list',
        type: 'get',
        response: config => {
            const items = groupData.items
            return {
                code: 20000,
                data: {
                    total: items.length,
                    items: items
                }
            }
        }
    },
    {
        url: '/group/overview',
        type: 'get',
        response: config => {
            return {
                code: 20000,
                data: {
                    data: overviewData
                }
            }
        }
    },
    {
        url: '/group/nodeList',
        type: 'get',
        response: config => {
            const items = nodeList.items
            return {
                code: 20000,
                data: {
                    total: items.length,
                    items: items
                }
            }
        }
    }, 
    {
        url: '/transaction/transList',
        type: 'get',
        response: config => {
            const items = txInfo.items
            return {
                code: 20000,
                data: {
                    total: items.length,
                    items: items
                }
            }
        }
    },
    {
        url: '/block/blockList',
        type: 'get',
        response: config => {
            const items = blockInfo.items
            return {
                code: 20000,
                data: {
                    total: items.length,
                    items: items
                }
            }
        }
    },
]
