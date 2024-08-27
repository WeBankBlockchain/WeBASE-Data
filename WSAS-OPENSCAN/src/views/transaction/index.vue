<template>
  <div class="application">
    <div class="bread-con">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: 'chain', query: { chainId } }">{{ $t(`${chainName}`) }}</el-breadcrumb-item>
        <el-breadcrumb-item><a>{{$t('trans.trans_detail')}}</a></el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="top">
      <p>{{$t('trans.trans_hash')}}：</p>
      <div class="text">{{ transHash }}</div>
    </div>
    <div class="center" id="blockarea">
      <el-tabs v-model="activeName">
        <el-tab-pane :label="$t('trans.trans_info')" name="first">
          <div class="item" v-if="transDetail.appName">
            <label>dapp ：</label>
            <p class="jump" @click="openAppPage" v-if="!transDetail.isDecChain">{{ transDetail.appName }}</p>
            <p v-else>{{ transDetail.appName }}</p>
          </div>
          <div class="item">
            <label>from ：</label>
            <p>{{ transDetail.from }}</p>
          </div>
          <div class="item" v-if="toShow">
            <label>to ：</label>
            <p v-if="!transDetail.isDecChain && !isBulidIn(transDetail.to)">{{ transDetail.to }}</p>
            <!-- <p class="jump" @click="openContractPage(transDetail.to)" v-if="!transDetail.isDecChain && !isBulidIn(transDetail.to)">{{ transDetail.to }}</p> -->
            <p v-else>{{ transDetail.to }}</p>
          </div>
          <div class="item">
            <label>{{$t('trans.trans_receipt')}} ：</label>
            <p>{{ transReceipt.blockTimestamp | timeTrans(transReceipt.blockTimestamp) }}</p>
          </div>
          <!-- <div class="item">
            <label>区块哈希 ：</label>
            <p>{{ transDetail.hash }}</p>
          </div> -->
          <div class="item">
            <label>Input ：</label>
            <p class="item-input" v-if="!inputParseShow">{{ transDetail.input }}</p>
            <div class="detail-input" v-else>
              <div class="detail-input-content">
                <div class="input-data">
                  <div class="input-label">
                    <span class="label">function：</span>
                    <span>{{ funcData + '(' + abiType + ')' + outputType }}</span>
                  </div>
                  <!-- <div class="input-label">
                    <span class="label">methodId：</span>
                    <span>{{ methodId }}</span>
                  </div> -->
                  <div class="input-label" v-if="dataShow">
                    <span class="label">data：</span>
                    <el-table class="detail-table" :data="inputData" v-if="inputData.length" style="display: inline-block">
                      <el-table-column prop="name" label="name" align="left" v-if="inputData[0].name"></el-table-column>
                      <el-table-column prop="type" label="type" align="left"></el-table-column>
                      <el-table-column prop="data" label="data" align="left" :show-overflow-tooltip="true">
                        <template slot-scope="scope">
                          <span class="data-con">{{ scope.row.dataStr }}</span>
                          <img class="copy-img" @click="copyData(scope.row.dataStr, $t('copy_succ'))" src="../../assets/images/copy.png" />
                        </template>
                      </el-table-column>
                    </el-table>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="item" v-if="inputBtnShow">
            <label></label>
            <div class="parse" v-if="!inputParseShow" @click="checkInputParse">{{$t('trans.decode')}}</div>
            <div class="parse" v-else @click="checkInputParse">{{$t('trans.revert')}}</div>
          </div>
        </el-tab-pane>
        <el-tab-pane :label="$t('trans.trans_receipt')" name="second">
          <div class="item">
            <label>from ：</label>
            <p>{{ transReceipt.from }}</p>
          </div>
          <div class="item" v-if="toShow">
            <label>to ：</label>
            <p v-if="!transDetail.isDecChain && !isBulidIn(transReceipt.to)">{{ transReceipt.to }}</p>
            <!-- <p class="jump" @click="openContractPage(transReceipt.to)" v-if="!transDetail.isDecChain && !isBulidIn(transReceipt.to)">{{ transReceipt.to }}</p> -->
            <p v-else>{{ transReceipt.to }}</p>
          </div>
          <div class="item" v-if="contractAddressShow">
            <label>contractAddress ：</label>
            <p>{{ transReceipt.contractAddress }}</p>
          </div>
          <div class="item">
            <label>{{$t('trans.block_height')}} ：</label>
            <p class="jump" @click="openBlockPage(transReceipt.blockNumber)">{{ transReceipt.blockNumber }}</p>
          </div>
          <div class="item">
            <label>{{$t('trans.trans_time')}} ：</label>
            <p>{{ transReceipt.blockTimestamp | timeTrans(transReceipt.blockTimestamp) }}</p>
          </div>
          <div class="item">
            <label>status ：</label>
            <p><span :class="transReceipt.status === '0' ? 'success status' : 'fail status'"></span> {{ statusZH(transReceipt.status) }}</p>
          </div>
          <div class="item">
            <label>output ：</label>
            <p class="item-input" v-if="!outputParseShow">{{ transReceipt.output }}</p>
            <div style="width:90%" v-else>
            <div class="detail-input">
              <div class="item" v-if="!errorParseShow">
                <span class="label">function：</span>
                <!-- <span>{{ funcOutData + '(' + abiOutType + outputType + ')' }}</span> -->
                <span>{{ funcData + '(' + abiType + ')' + outputType }}</span>
              </div>
              <!-- <div class="item">
                <span class="label">methodId：</span>
                <span>{{ methodId }}</span>
              </div> -->
              <div class="item" v-if="!errorParseShow">
                <span class="label">data：</span>
                <el-table class="detail-table" :data="outputData" v-if="outputData.length" style="display: inline-block">
                  <el-table-column prop="name" label="name" align="left" v-if="outputData[0].name"></el-table-column>
                  <el-table-column prop="type" label="type" align="left"></el-table-column>
                  <el-table-column prop="data" label="data" align="left" :show-overflow-tooltip="true">
                    <template slot-scope="scope">
                      <span class="data-con">{{ scope.row.data }}</span>
                      <img v-if="scope.row.data" class="copy-img" @click="copyData(scope.row.data, $t('copy_succ'))" src="../../assets/images/copy.png" />
                    </template>
                  </el-table-column>
                </el-table>
              </div>
              <div class="item" v-else style="line-height: 31px">
                <span class="label">data：</span>
                <span>{{ errorOutputData }}</span>
              </div>
            </div>
            </div>
          </div>
          <div class="item" v-if="outputBtnShow">
            <label></label>
            <div class="parse" v-if="!outputParseShow" @click="checkOutputParse">{{$t('trans.decode')}}</div>
            <div class="parse" v-else @click="checkOutputParse">{{$t('trans.revert')}}</div>
          </div>
          <div class="item">
            <label>logs ：</label>
            <p class="item-input" v-if="!logsParseShow">{{ transReceipt.logs }}</p>
            <div style="width:90%" v-else>
              <div v-for="(item, num) in eventLog" :key="num" class="detail-input">
                <div class="item">
                  <span class="label">Address：</span>
                  <span>{{ item.address }}</span>
                </div>
                <div class="item">
                  <span class="label">Name：</span>
                  <span>{{ item.eventName }}</span>
                </div>
                <div class="item">
                  <span class="label">Topics：</span>
                  <div style="display: inline-block; width: 800px">
                    <div v-for="(val, index) in item.topics" :key="val">[{{ index }}] {{ val }}</div>
                  </div>
                </div>
                <div class="item">
                  <span class="label">Data：</span>
                  <span v-if="!item.eventDataShow" class="input-data">{{ item.data }}</span>
                  <el-table class="detail-table" :data="item.eventLgData" v-show="item.eventDataShow" style="display: inline-block">
                    <el-table-column prop="name" label="name" align="left"></el-table-column>
                    <el-table-column prop="data" label="data" align="left" :show-overflow-tooltip="true">
                      <template slot-scope="scope">
                        <span class="data-con">{{ scope.row.dataStr }}</span>
                        <img class="copy-img" @click="copyData(scope.row.dataStr, 'data复制成功')" src="../../assets/images/copy.png" />
                      </template>
                    </el-table-column>
                  </el-table>
                </div>
              </div>
            </div>
          </div>
          <div class="item" v-if="logsBtnShow">
            <label></label>
            <div class="parse" v-if="!logsParseShow" @click="checkLogsParse">{{$t('trans.decode')}}</div>
            <div class="parse" v-else @click="checkLogsParse">{{$t('trans.revert')}}</div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>
<script>
import { tableHeightHandle } from 'Mixin/tableHeightHandle.js'
import { transManageServer } from 'Api'
import { mapGetters } from 'vuex'
import { copy } from 'Utils'
export default {
  name: 'groupManage',
  mixins: [tableHeightHandle],
  props: {},
  components: {},
  data() {
    return {
      activeName: 'first',
      pageData: {},
      transDetail: {},
      transHash: '',
      transReceipt: {},
      funcData: '',
      funcOutData: '',
      abiType: [],
      abiOutType: [],
      outputType: '',
      methodId: '',
      inputData: [],
      outputData: [],
      inputParseShow: true,
      outputParseShow: false,
      logsParseShow: false,
      inputBtnShow: true,
      outputBtnShow: true,
      toShow: true,
      logsBtnShow: true,
      eventLog: [],
      chainId: '',
      appId: '',
      deployBin: '',
      deployAbi: '',
      deployConstructor: '',
      contractAddressShow: false,
      dataShow: true,
      errorParseShow: false,
      errorOutputData: ''
    }
  },
  computed: {
    ...mapGetters(['userinfo', 'chainName'])
  },
  created() {
    const { chainId, transHash } = this.$route.query
    this.transHash = transHash
    this.chainId = chainId
    this.getTransInfo({
      chainId,
      transHash
    })
  },
  methods: {
    isBulidIn(to) {
      const BuildInAddress = [
        '0x0000000000000000000000000000000000001000',
        '0x000000000000000000000000000000000000100a',
        '0x000000000000000000000000000000000000100c',
        '0x000000000000000000000000000000000000100e',
        '0x000000000000000000000000000000000000100f',
        '0x0000000000000000000000000000000000001001',
        '0x0000000000000000000000000000000000001002',
        '0x0000000000000000000000000000000000001003',
        '0x0000000000000000000000000000000000001004',
        '0x0000000000000000000000000000000000001005',
        '0x0000000000000000000000000000000000001006',
        '0x0000000000000000000000000000000000001007',
        '0x0000000000000000000000000000000000001008',
        '0x0000000000000000000000000000000000001009',
        '0x0000000000000000000000000000000000001010',
        '0x0000000000000000000000000000000000001011',
        '0x0000000000000000000000000000000000005002',
        '0x0000000000000000000000000000000000005003',
        '0x0000000000000000000000000000000000005004',
        '0x0000000000000000000000000000000000005005',
        '0x0000000000000000000000000000000000005006',
        '0x0000000000000000000000000000000000005100',
        '0x0000000000000000000000000000000000010001',
        '0x0000000000000000000000000000000000010002',
        '0x0000000000000000000000000000000000010003',
        '0x0000000000000000000000000000000000010004'
      ]
      return BuildInAddress.includes(to)
    },
    openBlockPage(num) {
      this.$router.push({ path: 'block', query: { blockNumber: num, chainId: this.chainId } })
    },
    openContractPage(addr) {
      return
      this.$router.push({ path: 'contract', query: { contractAddress: addr, appId: this.transDetail.appId, chainId: this.chainId } })
    },
    openAppPage() {
      this.$router.push({ path: 'application', query: { appId: this.transDetail.appId, chainId: this.chainId } })
    },
    paginationHandle() {},
    async getTransInfo(params) {
      const res = await transManageServer.getTransInfo(params)
      console.log(res)
      if (res && res.code === 0) {
        this.transDetail = res.data
        this.getTransactionReceipt(res.data.to, res.data.input, {
          chainId: this.$route.query.chainId,
          transHash: this.$route.query.transHash
        })
        // const { input } = this.transDetail
        // this.inputMethodAbi({ methodId: input.slice(0, 10) })
      }
    },
    async inputMethodAbi(input, output) {
      const res = await transManageServer.inputMethodAbi({ methodId: input.slice(0, 10) })
      console.log(res)
      if (res && res.code === 0) {
      
        if (res.data) {
          console.log(JSON.parse(res.data.abiInfo))
          this.transDetail.abiData = res.data
          this.decodefun(this.transDetail.input, res.data)
        } else {
          this.inputParseShow = false
          this.inputBtnShow = false
        }
        // 判断是否成功 成功解析output 不成功再截取output请求abi再去解析
        if (this.transReceipt.status === '0') {
         
          if (output && output !== '0x') {
            // decode method id
            this.decodeOutPutfun(output, res.data)
            this.outputParseShow = true
          } else {
            this.outputParseShow = false
            this.outputBtnShow = false
            this.transReceipt.output = ''
          }
        } else {
          if (output && output !== '0x' && (output.substring(0, 10) === '0x08c379a0' || output.substring(0, 10) === '0xc703cb12')) {
            // decode method id
            // 请求abi
            const res = await transManageServer.getDeloyErrorParse({ output })
            console.log(res)
            if (res && res.code === 0) {
              if (res.data) {
                this.errorOutputData = res.data
                this.errorParseShow = true
                this.outputParseShow = true
              } else {
                this.outputParseShow = false
                this.outputBtnShow = false
              }
            } else {
              this.outputParseShow = false
              this.outputBtnShow = false
            }
          } else {
            this.outputParseShow = false
            this.outputBtnShow = false
          }
        }
      }
    },
    async getTransactionReceipt(to, input, params) {
      const res = await transManageServer.getTransactionReceipt(params)
      console.log(res)
      if (res && res.code === 0) {
        this.transReceipt = res.data
        this.eventLog = res.data.logs
        // if not deploy contract trans
        if (to && to !== '0x') {
          this.inputMethodAbi(input, res.data.output)
        } else {
          // else contract deploy trans not decode method
          this.getDeloyBin(input, res.data.output, res.data.contractAddress)
          this.outputBtnShow = false
          this.toShow = false
          this.contractAddressShow = true
          this.transReceipt.output = ''
        }
        if (this.eventLog) {
          this.decodeEventClick()
        } else {
          this.logsBtnShow = false
        }
        // if (res.data.output !== '0x' && this.transDetasil.abiData) this.decodeOutPutfun(res.data.output, this.transDetail.abiData)
      }
    },
    getDeloyBin: function (deployInput, output, contractAddress) {
      if (deployInput && deployInput !== '0x') {
        const data = {
          chainId: this.chainId,
          contractAddress: contractAddress
        }
        transManageServer.getDeloyContractBin(data).then((res) => {
          if (res.code === 0) {
            this.deployBin = res.data
            // this.decodeDeployOutput(output, res.data.data)
            this.getDeloyAbi(contractAddress, deployInput)
          }
        })
      }
    },
    getDeloyAbi: function (contractAddress, deployInput) {
      const data = {
        chainId: this.chainId,
        contractAddress: contractAddress
      }
      transManageServer.getDeloyAbi(data).then((res) => {
        if (res.code === 0) {
          this.deployAbi = res.data
          // this.decodeDeployOutput(output, res.data.data)
          this.decodeDeloy(deployInput)
        }
      })
    },
    decodeDeployOutput: function (output, data) {
      this.showOutputDecode = true
    },
    // deloy-contract-transaction-decode
    decodeDeloy: function (deployInput) {
      if (this.deployAbi) {
        const deployAbi = JSON.parse(this.deployAbi)
        deployAbi.some((element) => {
          if (element.type === 'constructor') {
            this.deployConstructor = element.inputs
            return true
          } else {
            return false
          }
        })
        const bin128 = this.deployBin.substring(this.deployBin.length - 128)
        const constructorCode = deployInput.split(bin128)
        console.log(constructorCode)
        this.outputParseShow = false
        this.outputBtnShow = false
        this.decodeDeloyFun(this.deployConstructor, constructorCode)
      }
    },
    decodeDeloyFun(deployConstructor, constructorCode) {
      const Web3EthAbi = require('web3-eth-abi')
      if (deployConstructor) {
        deployConstructor.forEach((val, index) => {
          if (val && val.type && val.name) {
            this.abiType[index] = val.type + ' ' + val.name
          } else if (val && val.name) {
            this.abiType[index] = val.name
          } else if (val && val.type) {
            this.abiType[index] = val.type
          } else if (val) {
            this.abiType[index] = val
          }
        })
        console.log(this.abiType)
        // if (abiData.abiInfo.outputs && abiData.abiInfo.outputs.length) {
        //   const outputType = []
        //   abiData.abiInfo.outputs.forEach((val, index) => {
        //     if (val && val.type && val.name) {
        //       outputType[index] = val.type + ' ' + val.name
        //     } else if (val && val.name) {
        //       outputType[index] = val.name
        //     } else if (val && val.type) {
        //       outputType[index] = val.type
        //     } else if (val) {
        //       outputType[index] = val
        //     }
        //   })
        //   this.outputType = ''
        //   for (let i = 0; i < outputType.length; i++) {
        //     this.outputType = this.outputType + outputType[i]
        //   }
        // } else {
        //   this.outputType = ''
        // }
        this.funcData = 'constructor'
        if (constructorCode) {
          const typesArray = []
          deployConstructor.forEach((item) => {
            typesArray.push(item.type)
          })
          this.decodeData = Web3EthAbi.decodeParameters(deployConstructor, constructorCode[1])
          if (JSON.stringify(this.decodeData) !== '{}') {
            for (const key in this.decodeData) {
              deployConstructor.forEach((val, index) => {
                if (val && val.name && val.type) {
                  if (key === val.name) {
                    this.inputData[index] = {}
                    this.inputData[index].name = val.name
                    this.inputData[index].type = val.type
                    debugger
                    this.inputData[index].data = this.decodeData[key]
                    this.inputData[index].dataStr = this.handleDataToString(this.decodeData[key])
                  }
                } else if (val) {
                  if (index === Number(key)) {
                    this.inputData[index] = {}
                    this.inputData[index].type = val.type
                    debugger

                    this.inputData[index].data = this.decodeData[key]
                    this.inputData[index].dataStr = this.handleDataToString(this.decodeData[key])
                  }
                }
              })
            }
          }
        }
        if (deployConstructor.length === 0) {
          this.dataShow = false
        }
        this.inputParseShow = true
        this.inputBtnShow = true
      }
    },
    // transactionDecode
    decodefun: function (input, abiData) {
      // eslint-disable-next-line
      const Web3EthAbi = require('web3-eth-abi')
      this.methodId = input.substring(0, 10)
      // this.methodId = data;
      const inputDatas = '0x' + input.substring(10)
      if (abiData) {
        abiData.abiInfo = JSON.parse(abiData.abiInfo)
        abiData.abiInfo.inputs.forEach((val, index) => {
          if (val && val.type && val.name) {
            this.abiType[index] = val.type + ' ' + val.name
          } else if (val && val.name) {
            this.abiType[index] = val.name
          } else if (val && val.type) {
            this.abiType[index] = val.type
          } else if (val) {
            this.abiType[index] = val
          }
        })
        console.log(this.abiType)
        if (abiData.abiInfo.outputs && abiData.abiInfo.outputs.length) {
          const outputType = []
          abiData.abiInfo.outputs.forEach((val, index) => {
            if (val && val.type && val.name) {
              outputType[index] = val.type + ' ' + val.name
            } else if (val && val.name) {
              outputType[index] = val.name
            } else if (val && val.type) {
              outputType[index] = val.type
            } else if (val) {
              outputType[index] = val
            }
          })
          this.outputType = ' returns ('
          for (let i = 0; i < outputType.length; i++) {
            this.outputType = this.outputType + ' ' + outputType[i] + ' '
          }
          this.outputType += ')'
        } else {
          this.outputType = ''
        }
        console.log(this.outputType);
        this.funcData = abiData.abiInfo.name
        if (abiData.abiInfo.inputs.length) {
          this.decodeData = Web3EthAbi.decodeParameters(abiData.abiInfo.inputs, inputDatas)
          if (JSON.stringify(this.decodeData) !== '{}') {
            for (const key in this.decodeData) {
              abiData.abiInfo.inputs.forEach((val, index) => {
                if (val && val.name && val.type) {
                  if (key === val.name) {
                    this.inputData[index] = {}
                    this.inputData[index].name = val.name
                    this.inputData[index].type = val.type
                    debugger
                    this.inputData[index].data = this.decodeData[key]
                    this.inputData[index].dataStr = this.handleDataToString(this.decodeData[key])
                  }
                } else if (val) {
                  if (index === key) {
                    this.inputData[index] = {}
                    this.inputData[index].type = val.type
                    this.inputData[index].data = this.decodeData[key]
                    this.inputData[index].dataStr = this.handleDataToString(this.decodeData[key])
                  }
                }
              })
            }
          }
        }
      }
      this.inputParseShow = true
      this.inputBtnShow = true
    },
    handleDataToString(data) {
      if (data === null || data === undefined) {
        return ''
      }
      if (typeof data === 'object') {
        try {
          return JSON.stringify(data)
        } catch (e) {
          return data
        }
      } else {
        return data.toString()
      }
    },
    checkInputParse() {
      this.inputParseShow = !this.inputParseShow
    },
    checkOutputParse() {
      this.outputParseShow = !this.outputParseShow
    },
    checkLogsParse() {
      this.logsParseShow = !this.logsParseShow
    },
    statusZH(status) {
      let str = ''
      switch (status) {
        case '0':
          str = this.$t('succ')
          break
        case '1':
          str = this.$t('fail')
          break
        default:
          str = this.$t('fail')
          break
      }
      return str
    },
    copyData(str, msg) {
      copy(str, msg)
    },
    // 解析output(method id)
    decodeOutPutfun: function (output, abiData) {
      // eslint-disable-next-line
      const web3 = new Web3(Web3.givenProvider)
      if (abiData) {
        if (this.transReceipt.status !== '0') {
          abiData.abiInfo = JSON.parse(abiData.abiInfo)
        }
        // abiData.abiInfo.outputs.forEach((val, index) => {
        //   if (val && index < abiData.abiInfo.outputs.length - 1) {
        //     this.abiOutType = this.abiOutType + val.type + ' ' + val.name + ','
        //   } else if (val && index === abiData.abiInfo.outputs.length - 1) {
        //     this.abiOutType = this.abiOutType + val.type + ' ' + val.name
        //   }
        // })
        this.funcOutData = abiData.abiInfo.name
        if (abiData.abiInfo.outputs.length) {
          this.showOutputDecode = false
          this.showOutDecode = true
          this.decodeOutData = web3.eth.abi.decodeParameters(abiData.abiInfo.outputs, output)
          // console.log(this.decodeOutData)
          if (JSON.stringify(this.decodeOutData) !== '{}') {
            for (let index = 0; index < abiData.abiInfo.outputs.length; index++) {
              this.outputData[index] = {}
              this.outputData[index].name = abiData.abiInfo.outputs[index].name
              this.outputData[index].type = abiData.abiInfo.outputs[index].type
              this.outputData[index].data = this.decodeOutData[index]
              // console.log(typeof(this.outputData[index].data))
              if (typeof this.outputData[index].data === 'boolean') {
                this.outputData[index].data = this.handleDataToString(this.outputData[index].data)
                this.outputData[index].dataStr = this.handleDataToString(this.outputData[index].data)
              }
            }
          }
          // console.log(this.outputData)
        } else {
          this.showOutDecode = false
          this.showOutputDecode = true
          try {
            const data = '0x' + output.substring(10)
            this.decodeOutData = web3.eth.abi.decodeParameter('string', data)
            // console.log(this.decodeOutData)
          } catch (error) {
            console.log(error)
          }
        }
      }
    },
    // decodeEvent
    async decodeEventClick() {
      for (let i = 0; i < this.eventLog.length; i++) {
        // if (this.eventLog[i].data === '0x') {
        //   this.logsBtnShow = false
        //   return
        // }
        const res = await transManageServer.inputMethodAbiList({ methodId: this.eventLog[i].topics[0].slice(0, 10), topicCnt: this.eventLog[i].topics.length })
        console.log(res)
        if (res.code === 0 && res.data.length !== 0) {
          this.eventLog[i] = this.decodeEvent(res.data[0], this.eventLog[i])
          setTimeout(() => {
            this.logsParseShow = true
          }, 200)
        }
      }
    },
    // decodeEventLog
    decodeEvent: function (eventData, data) {
      // eslint-disable-next-line
      const Web3EthAbi = require('web3-eth-abi')
      eventData.abiInfo = JSON.parse(eventData.abiInfo)
      const list = data
      list.eventTitle = this.$t('trans.revert')
      list.eventDataShow = true
      list.eventButtonShow = true
      list.eventName = eventData.abiInfo.name + '('
      for (let i = 0; i < eventData.abiInfo.inputs.length; i++) {
        console.log(eventData.abiInfo.inputs[i])
        if (i === eventData.abiInfo.inputs.length - 1) {
          if (eventData.abiInfo.inputs[i].indexed) {
            list.eventName = list.eventName + eventData.abiInfo.inputs[i].type + ' ' + 'indexed' + ' ' + eventData.abiInfo.inputs[i].name
          } else {
            list.eventName = list.eventName + eventData.abiInfo.inputs[i].type + ' ' + eventData.abiInfo.inputs[i].name
          }
        } else {
          if (eventData.abiInfo.inputs[i].indexed) {
            list.eventName = list.eventName + eventData.abiInfo.inputs[i].type + ' ' + 'indexed' + ' ' + eventData.abiInfo.inputs[i].name + ','
          } else {
            list.eventName = list.eventName + eventData.abiInfo.inputs[i].type + ' ' + eventData.abiInfo.inputs[i].name + ','
          }
        }
      }
      list.eventName = list.eventName + ')'
      console.log(eventData.abiInfo.inputs, '=====')
      const eventResult = Web3EthAbi.decodeLog(eventData.abiInfo.inputs, list.data, list.topics.slice(1))
      list.outData = {}
      list.eventLgData = []
      for (const key in eventResult) {
        if (+key || +key === 0) {
          list.outData[key] = eventResult[key]
        }
      }
      if (eventData.abiInfo.inputs.length && Object.keys(list.outData).length > 0) {
      // if (eventData.abiInfo.inputs.length && list.outData !== {}) {
        for (const key in list.outData) {
          eventData.abiInfo.inputs.forEach((items, index) => {
            // eslint-disable-next-line
            if (index == key) {
              list.eventLgData[index] = {}
              list.eventLgData[index].name = items.name
              list.eventLgData[index].data = list.outData[key]
              list.eventLgData[index].dataStr = this.handleDataToString(list.outData[key])
            }
          })
        }
      }
      return list
    }
  }
}
</script>
<style lang="less" scoped>
.application {
  /* background-color: rgb(245, 247, 251); */
  padding-bottom: 80px;
  ::v-deep .el-tabs__item.is-active{
    color: #215ed9;
  }
  ::v-deep .el-tabs__item:hover{
    color: #215ed9;
  }
  ::v-deep .el-tabs__active-bar{
    background-color: #215ed9;
  }
  div.bread-con {
    width: 1200px;
    margin: 0 auto;
    padding-top: 24px;
  }
  div.top {
    width: 1200px;
    margin: 0 auto;
    display: flex;
    padding: 24px 0;
    p {
      text-align: center;
      font-size: 18px;
      font-weight: 500;
      font-family: PingFangSC-Medium;
      text-align: left;
      line-height: 26px;
    }
    div {
      font-size: 14px;
      line-height: 26px;
      color: #525660;
      font-weight: 400;
      color: #262a32;
    }
    img {
      width: 14px;
      height: 14px;
      margin-top: 7px;
      margin-left: 10px;
      cursor: pointer;
    }
    div.text {
      flex: 1;
    }
    div.pag {
      span {
        cursor: pointer;
        color: #215ed9;
        margin-left: 10px;
      }
    }
  }
  #blockarea {
    width: 1200px;
    margin: 0 auto;
    background-color: white;
    padding: 32px 24px;
    box-sizing: border-box;
    box-shadow: 0px 0px 20px 0px #1e53a414;
  }
  div.center {
    background-color: #f5f7fb;
    div.item {
      display: flex;
      padding-bottom: 12px;
      
      label {
        width: 190px;
        text-align: right;
      }
      p {
        
        white-space: wrap;
        word-wrap: break-word;
        line-height: 22px;
        span.status {
          width: 8px;
          height: 8px;
          display: inline-block;
          border-radius: 50%;
          transform: translateY(-1px);
          margin-right: 7px;
        }
        .success {
          background-color: #69cb92;
        }
        .fail {
          background-color: red;
        }
      }
      .item-input {
        padding: 12px;
        align-items: flex-start;
        gap: 10px;
        align-self: stretch;
        border-radius: 4px;
        border: 1px solid var(--grey5, #e0e4ed);
        background: var(--grey-bg2, #f6f7fb);
      }
      .detail-input {
        width: 90%;
        padding: 24px 24px 12px 24px;
        flex-direction: column;
        align-items: flex-start;
        gap: 10px;
        align-self: stretch;
        border-radius: 4px;
        border: 1px solid var(--grey5, #e0e4ed);
        background: #f7f8fb;
        .label {
          width: 85px;
          display: inline-block;
          text-align: right;
          vertical-align: top;
        }
        .input-label:nth-child(2) {
          margin: 12px 0;
        }
        .detail-table {
          width: calc(100% - 85px);
          /deep/ .cell {
            color: var(--grey1, #262a32);
            /* menu 14 M */
            font-family: PingFang SC;
            font-size: 14px;
            font-style: normal;
            
            line-height: 22px;
          }
          /deep/ th.el-table__cell {
            background: #edf0f6;
          }
        }
      }
      .tableContent {
        width: 1044px;
      }
      .parse {
        font-family: PingFang SC;
        font-size: 14px;
        font-style: normal;
        font-weight: 400;
        line-height: 22px;
        padding: 5px 16px;
        align-items: center;
        gap: 10px;
        border-radius: 4px;
        border: 1px solid var(--grey5, #e0e4ed);
        cursor: pointer;
      }
      .jump {
        color: #215ed9;
        cursor: pointer;
      }
    }
  }
  .data-con{
    max-width: calc(100% - 22px);
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
    display: inline-block;
  }
  .copy-img {
    width: 16px;
    height: auto;
    transform: translateY(-2px);
    margin-left: 6px;
    cursor: pointer;
  }
  div.bottom {
    margin-top: 40px;
    .title {
      color: #262a32;
      font-size: 16px;
      padding-bottom: 24px;
      font-weight: 600;
    }
  }
}
</style>
