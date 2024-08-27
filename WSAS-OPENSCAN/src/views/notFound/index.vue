<template>
    <div class="application">
      <div class="bread-con">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: 'chain', query: { chainId } }">{{ $t(`${chainName}`) }}</el-breadcrumb-item>
          <el-breadcrumb-item><a>{{$t('notFound.title')}}</a></el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      <empty @reload="back"></empty>
    </div>
  </template>
<script>
import { mapGetters } from 'vuex'
import empty from '@/components/empty.vue'
export default {
  name: 'groupManage',
  props: {},
  components: {
    empty
  },
  data() {
    return {
      chainId: ''
    }
  },
  created() {
    this.init()
  },
  computed: {
    ...mapGetters(['userinfo', 'chainName'])
  },
  methods: {
    init() {
      const { chainId } = this.$route.query
      this.chainId = chainId
    },
    back() {
      history.go(-1)
    }
  },
  watch: {}
}
</script>
  <style lang="less" scoped>
  .application {
    position: absolute;
    height: 100%;
    width: 100%;
    div.bread-con {
      width: 1200px;
      margin: 0 auto;
      padding-top: 24px;
    }
  }
  </style>
