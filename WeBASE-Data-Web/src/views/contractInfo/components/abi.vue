<template>
    <div>
        <div v-for="item in initArrayAbi">
            <div class="item">
                <span class="label abi-type">{{item.type}} </span>
                <span class="abi-function">{{item.name}}</span>
                <span class="abi-lparen">
                    (
                    <span v-for="(input, i) in item.inputs">
                        <span class="abi-type">{{input.type}}</span>
                        {{input.name}}
                        <i v-if="i!=item.inputs.length-1">,</i>
                    </span>
                    )
                </span>
                <!-- <span>public</span> -->
                <span v-if="item.constant" class="abi-type">constant</span>
                <span class="abi-type">returns</span>
                <span class="abi-lparen">
                    (
                    <span v-for="(output,j) in item.outputs">
                        <span class="abi-type">{{output.type}}</span>
                        <i v-if="j!=item.outputs.length-1">,</i>
                    </span>
                    )
                </span>
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
                if (item.type === "function") {
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
    width: 48px;
    vertical-align: top;
    font-size: 12px;
}
.abi-type {
    color: rgb(147, 15, 128);
}
.abi-lparen {
    color: black;
}
.item {
    padding: 3px 0;
}
.abi-function {
    color: #0000a2;
}
span {
    word-break: break-all;
}
</style>
