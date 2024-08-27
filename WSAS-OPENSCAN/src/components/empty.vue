<template>
    <div class="content">
      <div class="msg-con">
        <img src="../assets/images/image_no_result.png" alt="" />
        <p>{{$t('notFound.noData')}}. <span @click="reload">{{$t('notFound.restart')}}</span></p>
      </div>
    </div>
  </template>
<script>
import { mapGetters } from 'vuex'
export default {
  name: 'groupManage',
  props: {},
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
    reload() {
      this.$emit('reload')
    }
  },
  watch: {}
}
</script>
  <style lang="less" scoped>
  .content {
    width: 1200px;
    height: calc(100vh - 120px);
    margin: 0 auto;
    background-color: transparent;
    position: relative;
    div.msg-con {
      width: 182px;
      height: 166px;
      position: absolute;
      left: 50%;
      top: 50%;
      transform: translate(-50%, -50%);
      text-align: center;
      img {
        width: 110px;
        height: auto;
        margin-bottom: 24px;
      }
      p {
        color: #787b84;
      }
      span {
        color: #215ed9;
        cursor: pointer;
      }
    }
  }
  </style>
