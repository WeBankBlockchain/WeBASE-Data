<template>
  <div class="container">
    <div class="center">
      <img class="notice" src="~Assets/images/icon_notice.svg" alt="" />
      【公告】
      <span class="title"> {{ noticeData.title.slice(0,20) }}</span>
      {{ noticeData.content.slice(0,40) }}
      <span @click="openNoticeDetail" class="btn">查看详情</span>
    </div>
    <img @click="closeNoticeBar" class="close" src="~Assets/images/icon_close.svg" alt="" />
  </div>
</template>
<script>
import { SET_AUTHORIZATION, SET_PERMISSION, SET_CHAIN_NAME, SET_SHOW_NOTICE_DETAIL } from 'Store/mutation-types.js'
import { mapMutations, mapGetters } from 'vuex'
export default {
  name: 'HeaderBar',
  data() {
    return {

    }
  },
  props: {
  },
  computed: {
    ...mapGetters(['userinfo', 'noticeData'])
  },
  created() {
  },
  methods: {
    ...mapMutations([SET_AUTHORIZATION, SET_PERMISSION, SET_CHAIN_NAME, SET_SHOW_NOTICE_DETAIL]),
    closeNoticeBar() {
      this.$emit('setBoardPriority', 2)
    },
    openNoticeDetail() {
      this.SET_SHOW_NOTICE_DETAIL(true)
    }
  },
  watch: {}
}
</script>
<style lang="less" scoped>
.container {
position: relative;
width: 100%;
background: #3071f2;
border-bottom: 1px solid #ccc;
min-width: 1200px;
height: 44px;
box-sizing: border-box;
color: white;
.center {
  width: 996px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  height: 100%;
  font-size: 14px;
  img.notice {
    margin-right: 10px;
  }
  span.title{
      margin-left: 10px;
      margin-right: 10px;
  }
  .btn {
    display: inline-block;
    width: 72px;
    height: 24px;
    background: white;
    color: #215ed9;
    text-align: center;
    line-height: 26px;
    border-radius: 4px;
    cursor: pointer;
    margin-left: 24px;
    font-size: 14px;
  }
}
img.close {
  position: absolute;
  right: 40px;
  top: 12px;
  width: 20px;
  height: auto;
  cursor: pointer;
}
}

</style>
