const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({

  transpileDependencies: true,
  devServer: {
    port: 9090, // 修改为 9090 端口
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        ws: true,  
        pathRewrite: { '^/api': '' }
      }
    },
  },
})
