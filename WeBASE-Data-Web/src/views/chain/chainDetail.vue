<template>
    <el-tabs type="border-card" v-model="currentTab" @tab-click="handleTab">
        <el-tab-pane v-for="(tab, index) in tabList" :label="`${tab.label}`" :name="tab.type" :key="`${tab.type}_${scope.row.chainId}`">
            <component v-bind:is="`${currentTab}Info`" :key="`${tab.type}_${scope.row.chainId}`" :chainId="scope.row.chainId" :chainName="scope.row.chainName"></component>
        </el-tab-pane>
    </el-tabs>
</template>

<script>
import { AppInfo, NodeInfo } from "./components/index.js"
export default {
    name: 'chainDetail',

    components: { AppInfo, NodeInfo },

    props: ['scope'],

    data() {
        return {
            currentTab: 'app',
            tabList: [
                {
                    label: '应用',
                    type: 'app',
                    index: ''
                },
                {
                    label: '节点',
                    type: 'node',
                    index: ''
                },
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
        handleTab(tab) {
            this.currentTab = tab.name
        },
    }
}
</script>

<style scoped>
</style>
