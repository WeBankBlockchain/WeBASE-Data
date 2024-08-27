<template>
  <div class="support">
    <div class="left">
      <ul>
        <li @click="jump('#qa', '常见问答')" :class="active === '常见问答' ? 'active' : ''">常见问答</li>
        <li @click="jump('#use', '使用指南')" :class="active === '使用指南' ? 'active' : ''">使用指南</li>
        <li @click="jump('#connect', '联系我们')" :class="active === '联系我们' ? 'active' : ''">联系我们</li>
        <li @click="jump('#notice', '公告')" :class="active === '公告' ? 'active' : ''">
          公告 <span class="notice-point" v-if="noticeData.priority === 3 && !noticeData.isRead"></span>
        </li>
      </ul>
    </div>
    <div class="right">
      <div id="qa" class="tab">
        <div class="title"><span></span>常见问答</div>
        <div class="part mulpart">
          <p>我可以做什么？</p>
          <div class="graph">作为公众：进行链上信息的读取、查验和监督。</div>
          <div class="graph">作为dapp/项目方：可以申请接入微众区块链，提升信息透明度，让dapp/项目更真实可行。</div>
        </div>
        <div class="part mulpart">
          <p>公众如何查验链上数据？</p>
          <div class="graph">
            您可以通过公众号【微众区块链】菜单栏进入对应的申请通道，申请获取观察节点专属安装包，运行安装和启动脚本后，即可通过浏览器进行链上信息的读取、查验和监督。
          </div>
        </div>
        <div class="part mulpart">
          <p>dapp/项目接入微众区块链？</p>
          <div class="graph">微众区块链欢迎来自不同行业、不同场景的dapp与项目接入，如您有意向，可联系微众区块链小助手(微信号：WeBank_Blockchain)咨询详情。</div>
        </div>
        <div class="part">
          <p>我想要更全面了解微众区块链</p>
          <div class="graph">
            微众银行于2015年即组建了区块链团队，2016年联合国内多家金融机构和科技企业共同发起成立了深圳市金融区块链发展促进会（金链盟），并牵头组建金链盟开源工作组协作研发了金融级、国产安全可控的区块链底层平台FISCO
            BCOS。2017年起，微众区块链陆续将核心研发成果面向全球开源，迄今已发布开源项目13个，构建起一整套覆盖底层、中间件、dapp组件的联盟链核心技术体系。您可以通过代码仓库了解各开源项目最新特性，获取项目源码。
          </div>
          <div class="link" @click="linkTo('https://github.com/WeBankBlockchain')">微众区块链开源项目代码仓库 ></div>
          <div class="link" @click="linkTo('https://github.com/FISCO-BCOS')">区块链底层平台FISCO BCOS代码仓库 ></div>
        </div>
      </div>
      <div id="use" class="tab">
        <div class="title"><span></span>使用指南</div>
        <div class="part">
          <div class="graph">
            您可以点击查看<span @click="linkTo('https://gitee.com/WeBank/webase-occ-doc')"> 微众区块链浏览器《用户使用指南》</span>了解并高效接入微众区块链浏览器。
          </div>
        </div>
      </div>
      <div id="connect" class="tab">
        <div class="title"><span></span>联系我们</div>
        <div class="part">
          <!-- <div class="graph addr">电话：0755-88243677</div>
          <div class="graph addr">地址：深圳市南山区沙河西路1819号深圳湾科技生态园7栋A座</div> -->
          <div class="qr-container">
            <p>微众区块链公众号</p>
            <img src="../../assets/images/qrcode.png" alt="" />
          </div>
          <div class="qr-container">
            <p>微众区块链小助手</p>
            <img src="../../assets/images/qrcode_account2.png" alt="" />
          </div>
        </div>
      </div>
      <div id="notice">
        <div class="notice-container">
          <img class="notice-image" src="~Assets/images/icon_notice_blue.svg" alt="" />
          【公告】
          <p class="notice-title" @click="openNoticeDetail" v-if="noticeData.id">
            {{ noticeData.title.slice(0, 20) }} <span class="notice-point" v-if="noticeData.priority === 3 && !noticeData.isRead"></span>
          </p>
          <span class="more" @click="linkTo('https://gitee.com/WeBank/webase-occ-doc/raw/master/HistoricalBulletinBoard.md')">查看更多公告</span>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { mapMutations, mapGetters } from 'vuex'
import { SET_SHOW_NOTICE_DETAIL } from 'Store/mutation-types.js'
export default {
  name: 'supportManage',
  props: {},
  components: {},
  computed: {
    ...mapGetters(['userinfo', 'noticeData'])
  },
  data() {
    return {
      active: '常见问答'
    }
  },
  created() {},
  methods: {
    ...mapMutations([SET_SHOW_NOTICE_DETAIL]),
    linkTo(url) {
      location.href = url
    },
    jump(id, active) {
      this.active = active
      document.querySelector(id).scrollIntoView({ behavior: 'smooth', block: 'start', inline: 'nearest' })
    },
    openNoticeDetail() {
      this.SET_SHOW_NOTICE_DETAIL(true)
    }
  }
}
</script>
<style lang="less" scoped>
.support {
  display: flex;
  width: 1200px;
  margin: 0 auto;
  padding-top: 24px;
  padding-bottom: 24px;
  height: calc(100% - 36px);
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  .left {
    width: 200px;
    height: 100%;
    background-color: white;
    margin-right: 20px;
    padding-top: 24px;
    box-sizing: border-box;
    li {
      width: 200px;
      height: 48px;
      line-height: 48px;
      border-right: 2px solid white;
      box-sizing: border-box;
      color: #525660;
      cursor: pointer;
      text-align: center;
      font-size: 14px;
      padding-left: 24px;
      text-align: left;
      .notice-point {
        width: 8px;
        height: 8px;
        display: inline-block;
        background-color: red;
        border-radius: 50%;
        transform: translate(-1px, -12px);
      }
    }
    li.active {
      border-right: 3px solid #215ed9;
      color: #215ed9;
      background-color: #f6f7fb;
    }
  }
  .right {
    flex: 1;
    background-color: white;
    padding: 32px;
    box-sizing: border-box;
    overflow-y: auto;
    padding-top: 0;
    #notice {
      padding-top: 32px;
    }
    .notice-container {
      display: flex;
      align-items: center;
      color: #262a32;
      font-size: 16px;
      font-weight: 500;
      font-family: PingFangSC-Medium;
      img {
        width: 20px;
        height: 20px;
        margin-right: 10px;
      }
      p {
        flex: 1;
        margin-left: 10px;
        cursor: pointer;
        span.notice-point {
          width: 8px;
          height: 8px;
          display: inline-block;
          background-color: red;
          border-radius: 50%;
          transform: translate(5px, -2px);
        }
      }
      span.more {
        cursor: pointer;
        color: #215ed9;
        margin-left: 20px;
      }
    }
    .title {
      color: #262a32;
      font-size: 18px;
      font-weight: 600;
      text-indent: 6px;
      padding-top: 32px;
      padding-bottom: 28px;
      span {
        background-color: #215ed9;
        width: 3px;
        height: 18px;
        float: left;
        transform: translateY(4px);
      }
    }
    .part {
      overflow: hidden;
      .qr-container {
        float: left;
        margin-right: 48px;
        text-align: center;
        p {
          font-size: 14px;
        }
      }
      p {
        color: #262a32;
        font-size: 16px;
        line-height: 24px;
        font-weight: 600;
        margin-bottom: 8px;
      }
      div.graph {
        color: #525660;
        font-size: 14px;
        line-height: 24px;
        span {
          color: #215ed9;
          cursor: pointer;
        }
      }
      div.addr {
        color: #525660;
      }
      div.code {
        color: #262a32;
        font-weight: 600;
        margin-top: 20px;
      }
      img {
        width: 124px;
        height: auto;
      }
      div.link {
        font-size: 14px;
        line-height: 24px;
        color: #215ed9;
        cursor: pointer;
        margin-top: 8px;
      }
    }
    .mulpart {
      margin-bottom: 28px;
    }
  }
}
</style>
