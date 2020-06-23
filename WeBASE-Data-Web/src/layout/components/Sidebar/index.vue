<template>
    <div :class="{'has-logo':showLogo}">
        <logo v-if="showLogo" :collapse="isCollapse" />
        <el-scrollbar wrap-class="scrollbar-wrapper">
            <el-menu :default-active="activeMenu" :collapse="isCollapse" :background-color="variables.menuBg" :text-color="variables.menuText" :unique-opened="false" :active-text-color="variables.menuActiveText" :collapse-transition="false" mode="vertical">
                <sidebar-item v-for="route in list" :key="route.path" :item="route" :base-path="route.path" />
            </el-menu>
        </el-scrollbar>
    </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Logo from './Logo'
import SidebarItem from './SidebarItem'
import variables from '@/styles/variables.scss'
import { chainAll } from '@/api/chain'
export default {
    components: { SidebarItem, Logo },
    computed: {
        ...mapGetters([
            'sidebar'
        ]),
        activeMenu() {
            const route = this.$route
            const { meta, path } = route
            // if set path, the sidebar will highlight the path you set
            if (meta.activeMenu) {
                return meta.activeMenu
            }
            return path
        },
        showLogo() {
            return this.$store.state.settings.sidebarLogo
        },
        variables() {
            return variables
        },
        isCollapse() {
            return !this.sidebar.opened
        }
    },
    data() {
        return {
            chainList: [],
            list: [],
        }
    },
    created() {
        this.queryChainAll()
    },
    mounted() {

    },
    methods: {
        queryChainAll() {
            chainAll()
                .then(response => {
                    const { code, data, message } = response;
                    if (code === 0) {
                        this.chainList = data;
                        var routeList = this.$router.options.routes;
                        var list = this.chainList, cList = [];
                        let chainRoute = routeList.find(item => item.path === '/chain');

                        this.$set(chainRoute, 'children', list.map(item => {
                            return {
                                path: `chain${item.chainId}`,
                                name: `chain${item.chainId}`,
                                meta: {
                                    icon: 'tree',
                                    title: `chain${item.chainId}`
                                }
                            }
                        }));
                        this.$set(this, 'list', routeList)
                    } else {
                        this.$message({
                            type: 'error',
                            message: message
                        })
                    }
                })
                .catch(err => {
                    this.$message({
                        type: 'error',
                        message: '系统错误'
                    })
                })
        }
    }
}
</script>
