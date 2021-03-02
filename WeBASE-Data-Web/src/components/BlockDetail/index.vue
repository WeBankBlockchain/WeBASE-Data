<template>
    <div>
        <li v-for="(val, key) in blockData">
            <template v-if="key=='transactions'">
                <!-- <span class="item-detail-key">{{key}}：</span>
                                    <span class="item-datail-val-table">
                                        {{val}}
                                        <el-table :data="val" tooltip-effect="dark" :border="true">
                                            <el-table-column prop="hash" label="hash" show-overflow-tooltip width="200" align="center">
                                                <template slot-scope="scope">
                                                    <span>{{scope.row.hash}}</span>
                                                </template>
                                            </el-table-column>
                                            <el-table-column prop="from" label="from" show-overflow-tooltip width="" align="center">
                                                <template slot-scope="scope">
                                                    <span>{{scope.row.from}}</span>
                                                </template>
                                            </el-table-column>
                                            <el-table-column prop="to" label="to" show-overflow-tooltip width="" align="center">
                                                <template slot-scope="scope">
                                                    <span>{{scope.row.to}}</span>
                                                </template>
                                            </el-table-column>
                                        </el-table>
                                    </span> -->
            </template>
            <template v-else>
                <span class="item-detail-key">{{key}}：</span>
                <span class="item-detail-val" style="word-break: break-all;">{{val}}</span>
            </template>
        </li>
    </div>
</template>
<script>
export default {
    name: "BlockDetail",
    components: {
    },

    props: ['blockData'],
}
</script>
<style scoped>
.item-detail-key {
    width: 150px;
    display: inline-block;
}
.item-detail-val {
}
.item-datail-val-table {
    display: inline-block;
    width: 900px;
}
</style>