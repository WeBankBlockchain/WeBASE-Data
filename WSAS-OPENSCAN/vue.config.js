const { defineConfig } = require('@vue/cli-service')
const path = require('path') // 引入path模块
function resolve(dir) {
  return path.join(__dirname, dir) // path.join(__dirname)设置绝对路径
}
const NodePolyfillPlugin = require("node-polyfill-webpack-plugin");

module.exports = defineConfig({
  transpileDependencies: true,
  outputDir: 'static',
  publicPath: '/weco/',
  configureWebpack: (config) => {
    config.resolve.fallback = {
      fs: false,
      crypto: require.resolve("crypto-browserify"),
      stream: require.resolve("stream-browserify")
    };

    config.plugins.push(new NodePolyfillPlugin());
    // 调试JS
    // process.env.NODE_ENV !== 'production' && (config.devtool = 'source-map')
  },
  productionSourceMap: false,
  devServer: {
    proxy: {

    },
    host: '127.0.0.1',
    allowedHosts: 'all',
    port: 3000
  },

  chainWebpack: (config) => {
    // 修改 HtmlWebpackPlugin 配置
    config.plugin('html').tap(args => {
      args[0].template = path.resolve(__dirname, 'public/index.ejs');
      return args;
    });
    config.resolve.alias
      .set('@', resolve('src'))
      .set('Components', resolve('.src/components'))
      .set('Assets', resolve('src/assets'))
      .set('Api', resolve('./src/apis'))
      .set('Utils', resolve('./src/utils'))
      .set('Mixin', resolve('./src/mixin'))
      .set('Store', resolve('./src/store'))
  }
})
