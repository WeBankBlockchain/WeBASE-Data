<template>
    <div>
        <el-table :data="appList" tooltip-effect="dark">
            <el-table-column v-for="head in appHead" :label="head.name" :key="head.enName" show-overflow-tooltip align="center">
                <template slot-scope="scope">
                    <template>
                        <span class="link" @click="link(scope.row, 'contractName')" v-if="head.enName=='appName'">{{scope.row[head.enName]}}</span>
                        <span v-else>{{scope.row[head.enName]}}</span>
                    </template>

                </template>
            </el-table-column>
        </el-table>
    </div>
</template>

<script>
import { groupList } from "@/util/api";
export default {
    name: 'AppInfo',

    components: {

    },

    props: {
        chainId: {
            type: Number
        }
    },

    data() {
        return {
            appHead: [
                {
                    enName: 'appName',
                    name: '应用名称'
                },
                {
                    enName: 'appVersion',
                    name: '应用版本'
                },
                {
                    enName: 'appSynopsis',
                    name: '应用概要'
                },
                {
                    enName: 'description',
                    name: '描述'
                },
                {
                    enName: 'createTime',
                    name: '创建时间'
                },
                {
                    enName: 'modifyTime',
                    name: '修改时间'
                }
            ],
            appList: [],
        }
    },

    computed: {
    },

    watch: {
    },

    created() {
    },

    mounted() {
        this.queryGroupList()
    },

    methods: {
        queryGroupList() {
            let param = {
                chainId: this.chainId
            }
            groupList(param).then(res => {
                if (res.data.code === 0) {
                    var list = res.data.data;
                    this.appList = list;
                } else {
                    this.$message({
                        type: "error",
                        message: this.$chooseLang(res.data.code)
                    })
                }
            }).catch(err => {
                this.$message({
                    type: "error",
                    message: "系统错误"
                })
            })
        },
        link(val) {
            this.$router.push({
                path: `/overview/${val.chainId}/${val.appName}/${val.groupId}`,
            })
        }
    }
}
</script>

<style scoped>
</style>
