<template>
    <div>
        <div v-for="item in initArrayAbi">
            <div class="item">
                <span class="label">function </span>
                <span>{{item.name}}</span>
            </div>
            <div class="item">
                <span class="label">input </span>
                <div class="detail-input-content">
                    <el-table class="input-data" :data="item.inputs" style="display:inline-block;width:100%;">
                        <el-table-column prop="name" width="150" label="name" align="left"></el-table-column>
                        <el-table-column prop="type" label="type" align="left" :show-overflow-tooltip="true"></el-table-column>
                    </el-table>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'abi',

    components: {
    },

    props: ['contractAbi'],

    data() {
        return {
        }
    },

    computed: {
        initArrayAbi() {
            if (!this.contractAbi) {
                return []
            }
            let arr = []
            arr = JSON.parse(this.contractAbi)
            let list = []
            arr.forEach(item => {
                if (item.type === "function" && item.inputs.length) {
                    list.push(item)
                }
            })
            return list
        }
    },

    watch: {
    },

    created() {
    },

    mounted() {

    },

    methods: {

    }
}
</script>

<style scoped>
.label {
    display: inline-block;
    width: 65px;
    font-weight: bold;
    vertical-align: top;
    font-size: 12px;
}
.item {
    padding: 3px 0;
}
.input {
    display: inline-block;
    font-size: 0;
    vertical-align: top;
}
.input span {
    font-size: 12px;
}
.input-data {
    display: inline-block;
    width: 100%;
    padding: 10px;
    max-height: 200px;
    overflow: auto;
    word-break: break-all;
    word-wrap: break-word;
    box-sizing: border-box;
}
.input-data-from {
    display: inline-block;
    width: calc(100% - 200px);
    height: auto;
    word-break: break-all;
    word-wrap: break-word;
    box-sizing: border-box;
}
.input-label {
    padding-bottom: 15px;
}
.input-label .label {
    width: 80px;
    font-size: 12px;
}
.detail-input-content {
    display: inline-block;
    width: 500px;
    height: auto;
    border: 1px solid #eaedf3;
    border-radius: 4px;
    font-size: 12px;
}
.receipt-field {
    font-weight: bold;
}
.base-p {
    overflow: hidden;
    word-break: break-all;
    word-wrap: break-word;
}
span {
    word-break: break-all;
}
</style>
