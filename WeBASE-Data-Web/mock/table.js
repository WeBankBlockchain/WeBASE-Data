import Mock from 'mockjs'

const data = Mock.mock({
    'items|30': [{
        id: '@id',
        title: '@sentence(10, 20)',
        'status|1': ['published', 'draft', 'deleted'],
        address: '0x26335a8cf960b29f038c2847d7db51a770cd3993',
        createTime: '@datetime',
        blockNumber: '@integer(300, 5000)',
        'txHash|7': '0x020644f6c6a10072f72de9904c60947bea92fef2c6affa0b948d374dc0fe8954',
        contractName: '@string',
        input: '[{"name":"n","type":"string"}]',
        output: '[]'
    }]
})

export default [
    {
        url: '/table/list',
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
    }
]
