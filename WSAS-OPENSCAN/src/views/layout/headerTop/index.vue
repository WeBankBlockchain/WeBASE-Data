<template>
  <div class="container">
    <div class="center">
      <div class="logo-con">
        <!-- <img class="logo" @click="goChainPage" src="~Assets/images/logo.png" alt="" /> -->
        <!-- <div class="logoText">监管</div> -->
      </div>
      <!-- <div class="top-con">
        <div class="menu" @click="goIndexPage">数据监控</div>
        <div class="menu" @click="goUserPage">用户列表</div>
        <div class="menu" @click="goAdminPage">管理员列表</div>
      </div> -->
      <div class="top-con">
        <!-- <div class="menu" :id="active == '0' ? 'active' : ''" @click="goIndexPage">首页</div>
        <div class="menu" :id="active == '1' ? 'active' : ''"  @click="goChainPage">交易数据</div>
        <div class="menu" :id="active == '2' ? 'active' : ''"  @click="goUserPage">用户数据</div> -->
        <div class="menu"  @click="changeLang">{{lang === 'zh' ? 'English' : '中文'}}</div>

      </div>
    </div>
  </div>
</template>
<script>
import { SET_AUTHORIZATION, SET_PERMISSION, SET_CHAIN_NAME,SET_INDEX_ACTIVE, SET_I18N_LANG} from 'Store/mutation-types.js'
import { mapMutations, mapGetters } from 'vuex'
import { chainManageServer } from 'Api'
export default {
  name: 'HeaderBar',
  data() {
    return {
      // active: '0',

      chainList: []
    }
  },
  props: {},
  computed: {
    ...mapGetters(['userinfo', 'chainName', 'noticeData','active', 'lang'])
  },
  created() {
    // this.getChainList()
  },
  methods: {
    ...mapMutations([SET_AUTHORIZATION, SET_PERMISSION, SET_CHAIN_NAME,SET_INDEX_ACTIVE,SET_I18N_LANG]),
    changeLang(){
      this.SET_I18N_LANG()
    },
    handleCommand(item) {
      const { chainId, chainName } = item
      this.SET_CHAIN_NAME(chainName)
      this.$router.push({ path: 'chain', query: { chainId } })
    },
    async getChainList() {
      const res = await chainManageServer.getChainList()
      console.log(res)
      if (res && res.code === 0) {
        const { data = [] } = res
        this.chainList = data
      }
    },
    goIndexPage() {
      // this.active = '0'
  
      this.SET_INDEX_ACTIVE('0')

      this.SET_CHAIN_NAME(this.$t('home'))
 
      if(this.$route.path !== '/indexPage'){
        this.$router.push({ path: 'indexPage' })
      }
    },
    goUserPage() {
      // this.active = '2'
      this.SET_INDEX_ACTIVE('2')
      
      this.SET_CHAIN_NAME(this.$t('userData'))
      if(this.$route.path !== '/user'){
        this.$router.push({ path: 'user' })
      }
    },
    goAdminPage() {
      this.SET_CHAIN_NAME('浏览')
      this.$router.push({ path: 'admin' })
    },
    goChainPage(){
      // this.active = '1'
      this.SET_INDEX_ACTIVE('1')

      this.SET_CHAIN_NAME('transData')
      // this.SET_CHAIN_NAME(this.$t('transData'))

      if(this.$route.path !== '/chain'){
        this.$router.push({ path: 'chain'})
      }
    },
    goOcBrowser() {
      this.SET_CHAIN_NAME('浏览')
      this.$router.push({ path: 'indexPage' })
    },
    openSupPAge() {
      this.SET_CHAIN_NAME('浏览')
      if (this.noticeData && this.noticeData.id && !this.noticeData.isRead) {
        this.$emit('setBoardPriority', 3)
      }
      this.$router.push({ path: 'support' })
    }
  },
  watch: {}
}
</script>
<style lang="less" scoped>

.container {
  width: 100%;
  background: #fff;

    max-width: 1200px; 
    margin: 0 auto; 
    padding: 0; 
    box-sizing: border-box; 

  /* border-bottom: 1px solid #ccc; */
  /* min-width: 1200px; */
  height: 54px;
  /* box-sizing: border-box; */
  .center {
    /* width: 100%; */
    /* margin: 0 auto; */
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 100%;
  }
  .logo-con {
    /* flex: 1; */
    height: auto;
    img {
      width: 118px;
      height: auto;
      cursor: pointer;
      vertical-align: middle;
    }
    .logoText {
      vertical-align: middle;
      margin-left: 12px;
      display: inline-flex;
      padding: 3px 10px;
      justify-content: center;
      align-items: center;
      gap: 10px;
      border-radius: 4px;
      background: var(--main, #3071f2);
      color: var(--white, #fff);
      font-family: PingFang SC;
      font-size: 14px;
      font-style: normal;
      font-weight: 500;
      line-height: 22px;
    }
  }

  div.top-con {
    padding: 0 50px;
    padding-right: 0;
    text-align: right;
    box-sizing: border-box;
    display: flex;
    align-items: center;
    text-align: right;
    .menu {
      padding: 10px 16px;
      cursor: pointer;
      color: #262a32;
      position: relative;
    }
    #active{
      color: #5384FF;
    }
    span.notice-point {
      width: 8px;
      height: 8px;
      position: absolute;
      background-color: red;
      border-radius: 50%;
      right: 0;
      top: 0;
    }
  }
  .el-dropdown-link {
    color: #262a32;
  }
  .el-dropdown-menu__item {
    color: #262a32;
  }
}
</style>
