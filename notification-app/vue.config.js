module.exports = {
    devServer: {
        proxy: {
            "/api/*": {
                warnings: true,
                errors: true,
                target: "http://localhost:8083/", //todo vk: move to gateway port
                secure: false
            }
        }
    },
    configureWebpack: {
        optimization: {
            runtimeChunk: 'single',
            splitChunks: {
                chunks: 'all',
                maxInitialRequests: Infinity,
                minSize: 0,
                cacheGroups: {
                    vendor: {
                        test: /[\\/]node_modules[\\/]/,
                        name(module) {
                            const packageName = module.context.match(/[\\/]node_modules[\\/](.*?)([\\/]|$)/)[1];
                            return `npm.${packageName.replace('@', '')}`;
                        }
                    }
                }
            }
        }
    }
}