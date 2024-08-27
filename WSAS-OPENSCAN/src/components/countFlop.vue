<template>
  <div class="count-flop" :title="val">
    <div
      :class="numberStrList.includes(item) ? 'count-flop-box' : item === 'M' || item === 'T' || item === 'B' ? 'count-flop-point-m' : 'count-flop-point'"
      v-for="(item, index) in value"
      :key="index"
    >
      <div v-show="numberStrList.includes(item)" class="count-flop-content" :class="['rolling_' + item]">
        <div v-for="(item2, index2) in numberList" :key="index2 + '_' + item2" class="count-flop-num">
          {{ item2 }}
        </div>
      </div>
      <div v-show="item === ','" class="count-flop-content">,</div>
      <div v-show="item === 'M'" class="numberM">M</div>
      <div v-show="item === 'T'" class="numberT">T</div>
      <div v-show="item === 'B'" class="numberT">B</div>
      <div v-show="item === ' '" class="count-flop-content"></div>
    </div>
    <div v-if="suffix" class="count-flop-unit">{{ suffix }}</div>
  </div>
</template>
<script>
export default {
  data () {
    return {
      value: [],
      numberList: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
      compKey: 0,
      numberStrList: ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9'],
      changeAble: true
    }
  },
  props: ['val', 'suffix'],
  watch: {
    val () {
      this.updateValue()
      // this.compKey += 1
    }
  },

  created () {
    console.log('enter')
    this.updateValue()
  },
  methods: {
    updateValue () {
      console.log(this.value)

      if (this.changeAble) {
        this.value = this.val.toString().split('')
        console.log(this.value, 'this.value')
        this.changeAble = false
      } else {
        // this.$set(this.value, 0, 9)
        console.log('enter', this.value)
        this.value[0] = 9
      }
    }
  }
}
</script>
<style scoped>
.count-flop {
  display: inline-block;
  font-size: 0;
  /* 可更改 */
  height: 28px;
  line-height: 28px;
  font-size: 28px;
  color: #4898f1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: var(--grey1, #262a32);
  font-family: PingFang SC;
  font-weight: 500;
}

.count-flop > div {
  position: relative;
  display: inline-block;
  overflow: hidden;
  height: 100%;
}

.count-flop-box {
  /* 可更改 */
  /* margin-right: 5px; */
  width: 15px;
  /* border: 1px solid rgba(72, 152, 241, 0.3); */
  line-height: 30px;
  /* border-radius: 6px; */
  font-family: 'Helvetica Neue';
}

.count-flop-point {
  /* 可更改 */
  /* margin-right: 5px; */
  width: 10px;
}
.count-flop-point-m {
  /* 可更改 */
  /* margin-right: 5px; */
  width: 24px;
}
.count-flop-content {
  text-align: center;
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  animation-fill-mode: forwards !important;
}
.numberM {
  width: 20px;
}
.numberT {
  width: 20px;
  transform: translateY(-2px);
}
.rolling_0 {
  animation: rolling_0 2.1s ease;
}

@keyframes rolling_0 {
  from {
    transform: translateY(-90%);
  }
  to {
    transform: translateY(0);
  }
}

.rolling_1 {
  animation: rolling_1 3s ease;
}

@keyframes rolling_1 {
  from {
    transform: translateY(0);
  }
  to {
    transform: translateY(-10%);
  }
}

.rolling_2 {
  animation: rolling_2 2.1s ease;
}

@keyframes rolling_2 {
  from {
    transform: translateY(0);
  }
  to {
    transform: translateY(-20%);
  }
}

.rolling_3 {
  animation: rolling_3 3s ease;
}

@keyframes rolling_3 {
  from {
    transform: translateY(0);
  }
  to {
    transform: translateY(-30%);
  }
}

.rolling_4 {
  animation: rolling_4 2.1s ease;
}

@keyframes rolling_4 {
  from {
    transform: translateY(0);
  }
  to {
    transform: translateY(-40%);
  }
}

.rolling_5 {
  animation: rolling_5 3s ease;
}

@keyframes rolling_5 {
  from {
    transform: translateY(0);
  }
  to {
    transform: translateY(-50%);
  }
}

.rolling_6 {
  animation: rolling_6 2.1s ease;
}

@keyframes rolling_6 {
  from {
    transform: translateY(0);
  }
  to {
    transform: translateY(-60%);
  }
}

.rolling_7 {
  animation: rolling_7 3.1s ease;
}

@keyframes rolling_7 {
  from {
    transform: translateY(0);
  }
  to {
    transform: translateY(-70%);
  }
}

.rolling_8 {
  animation: rolling_8 2.1s ease;
}

@keyframes rolling_8 {
  from {
    transform: translateY(0);
  }
  to {
    transform: translateY(-80%);
  }
}

.rolling_9 {
  animation: rolling_9 3.6s ease;
}

@keyframes rolling_9 {
  from {
    transform: translateY(0);
  }
  to {
    transform: translateY(-90%);
  }
}
</style>
