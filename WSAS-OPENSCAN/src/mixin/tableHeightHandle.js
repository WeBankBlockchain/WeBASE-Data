export const tableHeightHandle = {
  data () {
    return {
      tableHeight: null
    }
  },
  methods: {
    calcTableHeight () {
      this.$nextTick(() => {
        const el = document.getElementsByClassName('autoTableWrap')[0]
        el && (this.tableHeight = document.body.clientHeight - el.getBoundingClientRect().top - 80)
      })
    }
  },
  mounted () {
    this.calcTableHeight()
    window.onresize = () => {
      this.calcTableHeight()
    }
  },
  destroyed () {
    window.onresize = null
  }
}
