<template>
    <div>
        <v-content-head :headTitle="'告警列表'"></v-content-head>
        <div class="module-wrapper">
            <!-- <div class="search-part" style="padding-top: 20px;">
                <div class="search-part-left">
                    <el-button type="primary" class="search-part-left-btn" @click="createKeyword">新增关键字</el-button>
                </div>
            </div>
            <div>
                <el-table :data="keywordList" tooltip-effect="dark">
                    <el-table-column v-for="head in keywordHead" :label="head.name" :key="head.enName" show-overflow-tooltip align="center">
                        <template slot-scope="scope">
                            <template v-if="head.enName!='operate'">
                                <span>{{scope.row[head.enName]}}</span>
                            </template>
                            <template v-else>
                                <el-button type="text" size="small" @click="deleteKeyword(scope.row,'modify')">删除</el-button>
                                <el-button type="text" size="small" @click="handleBtn(scope.row)">{{btnText(scope.row['statusType'])}}</el-button>
                            </template>
                        </template>
                    </el-table-column>
                </el-table>
            </div> -->
           
        </div>
        <div  class="module-wrapper">
            <!-- <div style="padding: 10px 0 0 40px; font-weight: bold;font-size: 16px;">告警列表</div> -->
             <div class="search-table">
                <el-table :data="alarmList" tooltip-effect="dark">
                    <el-table-column v-for="head in alarmHead" :label="head.name" :key="head.enName" show-overflow-tooltip align="center">
                        <template slot-scope="scope">
                            <template v-if="head.enName!='operate'">
                                <span>{{scope.row[head.enName]}}</span>
                            </template>
                            <template v-else>
                                <el-button type="text" size="small" @click="deleteKeyword(scope.row,'modify')">删除</el-button>
                                <el-button type="text" size="small" @click="handleBtn(scope.row)">{{btnText(scope.row['statusType'])}}</el-button>
                            </template>
                        </template>
                    </el-table-column>
                </el-table>

                <!-- <el-pagination class="page" @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage" :page-sizes="[10, 20, 30, 50]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper" :total="total">
                </el-pagination> -->
            </div>
        </div>
    </div>
</template>

<script>
import contentHead from "@/components/contentHead";
export default {
    name: 'keywordConfig',

    components: {
        "v-content-head": contentHead,
        
    },

    props: {
    },

    data() {
        return {
            keywordVisible: false,
            keywordHead: [
                {
                    enName: 'keyword',
                    name: '关键字'
                },
                {
                    enName: "operate",
                    name: '操作'
                }
            ],
            keywordList: [
                {
                    keyword: '新冠病毒'
                },
                {
                    keyword: '禽流感'
                }
            ],
            alarmHead: [
                {
                    enName: 'keyword',
                    name: '关键字'
                },
                {
                    enName: 'chainName',
                    name: '链名称'
                },
                {
                    enName: 'appName',
                    name: '应用名称'
                },
                {
                    enName: 'hash',
                    name: '交易Hash'
                },
                {
                    enName: 'user',
                    name: '用户'
                },
                {
                    enName: 'status',
                    name: '状态'
                },
                {
                    enName: 'modifyTime',
                    name: '修改时间'
                },
                {
                    enName: "operate",
                    name: '操作'
                }
            ],
            alarmList: [
                {
                    chainName: '存证链',
                    appName: '碳排放',
                    hash: '0x19843jsdf9834dff3',
                    user: '0x5743r4545fe3r',
                    status: '未处理',
                    statusType: '1',
                    modifyTime: '2019-03-15 11:14:29',
                    keyword: '新冠病毒'
                },
                {
                    chainName: '存证链',
                    appName: '产品质量码',
                    hash: '0x19843jsdf9834dff3',
                    user: '0x5743r4545fe3r',
                    status: '已处理',
                    statusType: '2',
                    modifyTime: '2019-03-14 11:14:29',
                    keyword: '新冠病毒'
                }
            ],

        }
    },

    computed: {
    },

    watch: {
    },

    created() {
    },

    mounted() {
    },

    methods: {
        createKeyword: function () {
           this.keywordVisible = true
        },
        deleteKeyword(){
            
        },
        handleBtn(){

        },
        success(){
           
        },
        close(val){
            this.keywordVisible = val
        },
        btnText(key){
            switch (key) {
                case '1':
                    return '确认'
                    break;
            
                case '2':
                    return ''
                    break;
            }
        }
    }
}
</script>

<style scoped>
</style>
