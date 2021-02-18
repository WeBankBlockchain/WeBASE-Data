/*
 * Copyright 2014-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
<template>
    <div class="contract-content" v-loading="loading">
        <v-content-head :headTitle="$t('title.contractTitle')" style="font-size: 14px;" @changeChain="changeChain" @changeGroup="changeGroup"></v-content-head>
        <div class="code-menu-wrapper" :style="{width: menuWidth+'px'}">
            <v-menu @change="changeCode($event)" ref="menu" v-show="menuHide">
                <template #footer>
                    <div class="version-selector">
                        <el-select v-model="version" placeholder="请选择" @change="onchangeLoadVersion">
                            <el-option v-for="item in allVersion" :key="item.id" :label="item.solcName" :value="item.solcName">
                            </el-option>
                        </el-select>
                    </div>
                </template>
            </v-menu>
            <div class="move" @mousedown="dragDetailWeight($event)"></div>
        </div>
        <div :class="[!menuHide ?  'code-detail-wrapper' : 'code-detail-reset-wrapper']" :style="{width: contentWidth}">
            <v-code :changeStyle="changeWidth" :data="contractData" :show="showCode" @add="add($event)" @compile="compile($event)" @deploy="deploy($event)">
            </v-code>
        </div>
    </div>
</template>

<script>
import menu from "./components/contractCatalog";
import codes from "./components/code";
import contentHead from "@/components/contentHead";
import { solcList } from "@/util/api";
export default {
    name: "contract",
    components: {
        "v-menu": menu,
        "v-code": codes,
        "v-content-head": contentHead
    },
    watch: {
        $route: function() {
            console.log(this,'sss')
        },
        menuHide(val) {
            if (val) {
                this.menuWidth = 180;
            } else {
                this.menuWidth = 0;
            }
        }
    },
    data() {
        return {
            contractData: {},
            showCode: false,
            menuHide: true,
            changeWidth: false,
            contractHide: false,
            menuWidth: 240,
            loading: false,
            allVersion: [],
            version: localStorage.getItem('solcName') ? localStorage.getItem('solcName') : '',
            baseURLWasm: './static/js',
            versionId: localStorage.getItem('versionId') ? localStorage.getItem('versionId') : '',
        };
    },
    computed: {
        contentWidth() {
            if (this.menuWidth) {
                return `calc(100% - ${this.menuWidth}px)`;
            } else {
                return `100%`;
            }
        }
    },
    created() {
        this.querySolcList(this.initSolc)
    },
    mounted() {
        // this.addBaseLoad()
    },
    methods: {
        querySolcList(callback, type) {
            var data = {
                encryptType: localStorage.getItem('encryptType')
            }
            solcList(data)
                .then(res => {
                    if (res.data.code === 0) {
                        var list = res.data.data;
                        this.allVersion = list;
                        if (!list.length) return;
                        if (!localStorage.getItem('solcName')) {
                            this.version = list[0]['solcName'];
                            this.versionId = list[0]['id'];
                            localStorage.setItem("solcName", list[0]['solcName'])
                            localStorage.setItem("versionId", list[0]['versionId'])
                        }
                        callback(this.version)
                    } else {
                        this.$message({
                            message: this.$chooseLang(res.data.code),
                            type: "error",
                            duration: 2000
                        });
                    }
                })
                .catch(err => {
                    this.$message({
                        message: "系统错误",
                        type: "error",
                        duration: 2000
                    });
                });

        },
        initSolc(version) {
            var head = document.head;
            var script = document.createElement("script");
            script.src = `${this.baseURLWasm}/${version}`;
            script.setAttribute('id', 'soljson');
            if (!document.getElementById('soljson')) {
                head.append(script)
            }
        },
        addBaseLoad() {
            var head = document.head;
            var script = document.createElement("script");
            script.src = `./static/js/base-loading.js`;
            head.append(script)
        },
        initVersion() {
            localStorage.removeItem('solcName')
        },
        changeChain(type) {
            this.initVersion()
            this.querySolcList(this.initSolc, 'changeChain')
            this.$router.go(0)
            this.$refs.menu.getContracts()
        },
        onchangeLoadVersion(version) {
            localStorage.setItem('solcName', version)
            var versionId = '';
            this.allVersion.forEach(item => {
                if (item.solcName == version) {
                    versionId = item.id
                }
            });
            localStorage.setItem('versionId', versionId)
            this.initSolc(version)
            this.$router.go(0)
            this.$refs.menu.getContracts()
        },
        changeGroup() {
            this.$refs.menu.getContracts()
        },
        dragDetailWeight(e) {
            let startX = e.clientX,
                menuWidth = this.menuWidth;
            document.onmousemove = e => {
                let moveX = e.clientX - startX;
                if (menuWidth + moveX > 180) {
                    this.menuWidth = menuWidth + moveX;
                }
            };

            document.onmouseup = e => {
                document.onmousemove = null;
                document.onmouseup = null;
            };
        },
        changeCode(val) {
            this.contractData = val;
            this.showCode = true;
        },
        add(val) {
            this.$refs.menu.saveContact(val);
        },
        compile(val) {
            this.$refs.menu.saveContact(val);
        },
        deploy(val) {
            this.$refs.menu.saveContact(val);
        }
    }
};
</script>
<style scoped>
.contract-content {
    width: 100%;
    height: 100%;
    font-size: 0;
}
.contract-content::after {
    display: block;
    content: "";
    clear: both;
}
.code-menu-wrapper {
    float: left;
    position: relative;
    height: calc(100% - 57px);
    font-size: 12px;
    box-sizing: border-box;
}
.move {
    position: absolute;
    top: 0;
    left: 98%;
    width: 3px;
    height: 100%;
    z-index: 9999;
    cursor: w-resize;
}
.menu-spread {
    position: relative;
    width: 31px;
    height: 47px;
    line-height: 47px;
    border: 1px solid #e7ebf0;
    border-bottom: 2px solid #e7ebf0;
    color: #aeb1b5;
    background-color: #fff;
    text-align: center;
    cursor: pointer;
}
.menu-spread i {
    font-size: 12px;
}
.code-detail-wrapper {
    float: left;
    height: calc(100% - 57px);
    font-size: 12px;
}
.code-detail-reset-wrapper {
    float: left;
    height: calc(100% - 57px);
    font-size: 12px;
}
.menu-drag {
    position: absolute;
    width: 36px;
    height: 36px;
    line-height: 32px;
    border: 1px solid #e8e8e8;
    left: 370px;
    top: 50%;
    background-color: #fff;
    cursor: pointer;
}
.version-selector {
    position: absolute;
}
.version-selector >>> .el-select {
    width: 100%;
}
</style>