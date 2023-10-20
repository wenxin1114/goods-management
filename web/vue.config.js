var webpack = require('webpack')

module.exports = {
    devServer: {
        port: 8081 // 此处修改你想要的端口号
    },
    lintOnSave: false,
    configureWebpack: {
        plugins: [
            new webpack.ProvidePlugin({
                $: "jquery",
                jQuery: "jquery",
                Popper: ['popper.js', 'default']
            })
        ]
    },
}
