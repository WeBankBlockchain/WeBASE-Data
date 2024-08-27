<template>
  <span>
    <span @mouseover="showTooltip" @mouseleave="hideTooltip"><slot></slot></span>
    <div v-if="visible" class="tooltip" :style="tooltipStyle">
      <span class="name">待平账元豆总量</span><span class="desc">{{text}}</span><br/>
      <div class="tip">微众世界发行元豆将统一于6.30做平账处理</div>
    </div>
  </span>
</template>

<script>
export default {
  name: 'NewTooltip',
  props: {
    text: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      visible: true,
      tooltipStyle: {
        position: 'absolute',
        backgroundColor: '#FFFFFF',
        boxShadow: '0px 0px 10px 0px rgba(0, 0, 0, 0.08)',
        color: '#0F1222',
        padding: '15px 12px',
        borderRadius: '3px',
        zIndex: '9999',

        display: 'none'
      }
    };
  },
 
  methods: {
    showTooltip(event) {
      this.visible = true;
      this.tooltipStyle.display = 'block';
      // let L = event.pageX
      let L = event.offsetX
      let Y = event.offsetY
      this.tooltipStyle.left = L + 20 + 'px';
      this.tooltipStyle.top = Y + 30 + 'px';

    },
    hideTooltip() {
      this.visible = false;
      this.tooltipStyle.display = 'none';
    }
  }
};
</script>

<style scoped>
.tooltip {
  min-width: 220px;
  position: absolute;
  background-color: #FFFFFF;
  color: white;
  padding: 5px;
  z-index: 9999;
  display: none;
  border-radius: 4px;
  box-shadow: 0px 0px 10px 0px rgba(0, 0, 0, 0.08);
}

.name{
  color: #0F1222;
  font-weight: 400;
  font-size: 14px;
  line-height: 22px;
  margin-right: 4px;
}
.desc{
  font-weight: 700;
  font-size: 16px;
  line-height: 22px;
  color: #0F1222;
}
.tip{
  font-weight: 400;
  font-size: 12px;
  color: rgba(15, 18, 34, 0.65);
  margin-top: 5px;
}
</style>