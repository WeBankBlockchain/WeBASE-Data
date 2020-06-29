<template>
    <div>
        <content-head :headTitle="`链${chainId}`" :headSubTitle="'群组'" :icon="true"></content-head>
        <div class="module-wrapper">
            <div class="search-table">
            <el-table :data="groupList" class="search-table-content" v-loading="loading">
                    <el-table-column v-for="head in groupHead" :label="head.name" :key="head.enName" :prop="head.enName" :width="head.width" show-overflow-tooltip>
                        <template slot-scope="scope">
                            <template v-if="head.enName=='groupName'">
                                <span @click="toOverview(scope.row)" class="link">{{scope.row[head.enName]}}</span>
                            </template>
                            <template v-else-if="head.enName=='groupId'">
                                <span @click="toOverview(scope.row)" class="link">{{scope.row[head.enName]}}</span>
                            </template>
                            <template v-else>
                                <span>{{scope.row[head.enName]}}</span>
                            </template>
                        </template>
                    </el-table-column>

                </el-table>
            </div>
        </div>

    </div>
</template>

<script>
import contentHead from "@/components/contentHead";
import { groupList } from "@/util/api";
export default {
    components: {
        contentHead,
    },
    filters: {
        type(val) {
            if (val) {
                return "sm2/sm3"
            } else {
                return "secp256k1/sha3"
            }
        },
    },
    computed: {
        groupHead() {
            let data = [
                {
                    enName: "groupId",
                    name: "群组编号",
                    width: ''
                },
                {
                    enName: "groupName",
                    name: "群组名称",
                    width: ''
                },
                {
                    enName: "nodeCount",
                    name: "节点个数",
                    width: ''
                },
                {
                    enName: "genesisBlockHash",
                    name: "创世块Hash",
                    width: ''
                },
                {
                    enName: "groupStatus",
                    name: "群组状态",
                    width: ''
                },
                {
                    enName: "createTime",
                    name: "创建时间",
                    width: ''
                }
            ];
            return data
        }
    },
    watch: {

    },
    data() {
        return {
            loading: false,
            chainId: '',
            groupList: []
        }
    },
    mounted() {
        if (this.$route.query.chainId) {
            this.chainId = this.$route.query.chainId;
            this.queryGrouplist()
        }

    },
    methods: {
        queryGrouplist() {
            this.loading = true
            groupList(this.chainId)
                .then(res => {
                    this.loading = false;
                    if (res.data.code === 0) {
                        this.groupList = res.data.data
                    } else {

                    }
                })
        },
        toOverview(row) {
            this.$router.push({
                path: '/overview',
                query: {
                    groupId: row.groupId,
                    chainId: row.chainId
                }
            })
        }
    }
}
</script>

<style scoped>
</style>
