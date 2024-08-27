<template>
  <div class="page-container">
    <noticeBar v-if="noticeData.priority === 1 && isIndexPage && !noticeData.isRead" @setBoardPriority="setBoardPriority"></noticeBar>
    <HeaderTop @setBoardPriority="setBoardPriority"></HeaderTop>
    <div class="content_box">
      <!-- <div class="center" :style="{ backgroundColor: bgColor }"> -->
      <div class="center" style="background-color: transparent;">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
    <!-- <bottomFooter></bottomFooter> -->
    <!-- <el-dialog class="notice-con" width="540px" :visible="showNoticeDetail" :show-close="false">
      <div class="content">
        <div class="notice-title">{{ title }}</div>
        <div class="notice-time">发布时间：{{ releaseTime }}</div>
        <div class="notice-content" v-html="contentHtml"></div>
        <el-button class="btn" type="primary" @click="closeNoticeDetail">确 定</el-button>
      </div>
    </el-dialog> -->
  </div>
</template>

<script>
import HeaderTop from './headerTop'
import bottomFooter from './bottomFooter'
import noticeBar from './noticeBar'
import { commonService } from 'Api'
import { SET_AUTHORIZATION, SET_PERMISSION, SET_CHAIN_NAME, SET_NOTICE_DATA, SET_SHOW_NOTICE_DETAIL } from 'Store/mutation-types.js'
import { mapMutations, mapGetters } from 'vuex'
import { Base64 } from 'js-base64'
import dayjs from 'dayjs'
export default {
  components: {
    HeaderTop,
    bottomFooter,
    noticeBar
  },
  data() {
    return {
      isCollapse: false,
      bgColor: '',
      isIndexPage: false,
      title: '',
      content: '',
      contentHtml: '',
      releaseTime: '',
      timer: null
    }
  },
  computed: {
    ...mapGetters(['userinfo', 'noticeData', 'showNoticeDetail'])
  },
  created() {
    const { meta } = this.$route
    this.bgColor = meta.bgColor
    // this.getBoardInfo()
    // this.timer = setInterval(() => {
    //   this.getBoardInfo({ hideLoading: true })
    // }, 6000)
  },
  methods: {
    ...mapMutations([SET_AUTHORIZATION, SET_PERMISSION, SET_CHAIN_NAME, SET_NOTICE_DATA, SET_SHOW_NOTICE_DETAIL]),
    async getBoardInfo(params = {}) {
      const res = await commonService.getBoardInfo(params)
      if (res && res.code === 0) {
        const { data } = res
        if (data) {
          const { title, content, contentHtml, releaseTime } = data
          this.title = title
          this.content = content
          this.contentHtml = Base64.decode(contentHtml)
          this.releaseTime = dayjs(Number(releaseTime)).format('YYYY-MM-DD HH:mm:ss')
          this.SET_NOTICE_DATA({ ...data, releaseTime })
          this.showNotice = true
          this.isIndexPage = this.$route.name === 'indexPage'
        } else {
          this.showNotice = false
          this.SET_NOTICE_DATA({})
        }
      }
    },
    async closeNoticeDetail() {
      this.SET_SHOW_NOTICE_DETAIL(false)
    },
    async setNoticeReaded() {
      const { id } = this.noticeData
      const res = await commonService.setBoardReadByID({ boardId: id })
      if (res && res.code === 0) {
        this.getBoardInfo()
      }
    },
    async setBoardPriority(priority) {
      const { id } = this.noticeData
      const res = await commonService.setBoardPriority({ boardId: id, priority })
      if (res && res.code === 0) {
        this.getBoardInfo()
      }
    }
  },
  watch: {
    $route() {
      const { meta } = this.$route
      this.bgColor = meta.bgColor
      this.isIndexPage = this.$route.name === 'indexPage'
    },
    showNoticeDetail(newv) {
      if (newv) {
        this.setNoticeReaded()
      }
    }
  },
  beforeDestroy() {
    this.timer && clearInterval(this.timer)
  }
}
</script>
<style lang="less" scoped>
div.page-container {
  background-color: #fff;
  width: 100%;
  height: 100%;
  div.content_box{
    width: 100%;
    min-height: calc(100% - 54px);
    background-color: #F5F7FE;

    background-image: url('../../assets/images/background.png');

    background-size: 100%;
    background-repeat: no-repeat;

    div.center{
      max-width: 1200px;
      margin: 0 auto;

      box-sizing: border-box;
      min-height: calc(100% - 54px);
      position: relative;

      background-color: transparent; 

    }

  }

  div.center1 {
    /* background-color: aquamarine; */


    box-sizing: border-box;
    padding-bottom: 60px;
    min-height: calc(100% - 126px);
    position: relative;
  }
}
.layout-contaioner {
  height: 100%;
  .main-container {
    height: 100%;
    overflow: auto;
  }
  .elmain {
    height: 100%;
    padding: 30px 20px;
  }
  .el-menu {
    border-right: none;
  }
  .el-header {
    padding: 0;
  }
}
.notice-con {
  ::v-deep div.el-dialog__header {
    display: none;
  }
  ::v-deep .el-body {
    padding: 24px;
  }
}

.content {
  text-align: center;
  .notice-title {
    font-size: 16px;
    line-height: 24px;
    color: #262a32;
    text-align: center;
    font-weight: bold;
    margin-bottom: 12px;
  }
  .notice-time {
    font-size: 14px;
    line-height: 22px;
    color: #787b84;
    text-align: center;
    margin-bottom: 12px;
  }
  ::v-deep .notice-content {
    font-size: 14px;
    line-height: 22px;
    color: #262a32;
    text-align: left;
    a {
      color: #215ed9;
      cursor: pointer;
    }
  }
  .btn {
    display: inline-block;
    margin-top: 24px;
    background-color: #3071F2;
  }
}
</style>
