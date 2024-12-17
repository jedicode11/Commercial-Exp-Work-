// const { defineConfig } = require('@vue/cli-service') // eslint-disable-line

const path = require('path') // eslint-disable-line
const StyleLintPlugin = require('stylelint-webpack-plugin') // eslint-disable-line
const webpack = require('webpack') // eslint-disable-line
const PACKAGE = require('./package.json') // eslint-disable-line

const GitRevisionPlugin = require('git-revision-webpack-plugin') // eslint-disable-line
const gitRevisionPlugin = new GitRevisionPlugin()

const hashShort = JSON.stringify(gitRevisionPlugin.version())
const branch = JSON.stringify(gitRevisionPlugin.branch())

const banner = PACKAGE.name + ' - ver. ' + PACKAGE.version + ' | ' + new Date().toLocaleString('de-DE') + ' ' + hashShort + '-' + branch

const cssForProduction = (process.env.NODE_ENV === 'production') ? {
  extract: false
} : {}

const configureWebpackForProduction = (process.env.NODE_ENV === 'production') ? {
  optimization: {
    splitChunks: false
  },
  output: {
    libraryExport: 'default'
  }
} : {}

// vue.config.js
module.exports = {
  transpileDependencies: ['vue-loading-overlay'],
  lintOnSave: true,
  filenameHashing: false,
  productionSourceMap: false,
  publicPath: process.env.BASE_URL,

  css: {
    loaderOptions: {
      sass: {
        additionalData: (content) => {
          let additionalContent = ''

          if (process.env.VUE_APP_NO_FONTS) {
            additionalContent = additionalContent + '$finder-disable-font-loading: true;'
          }

          if (process.env.VUE_APP_COLOR_PRIMARY) {
            additionalContent = additionalContent + '$finder-color-primary: ' + process.env.VUE_APP_COLOR_PRIMARY + ';'
          }

          if (process.env.VUE_APP_COLOR_SECONDARY) {
            additionalContent = additionalContent + '$finder-color-secondary: ' + process.env.VUE_APP_COLOR_SECONDARY + ';'
          }

          if (process.env.VUE_APP_COLOR_TINTING1) {
            additionalContent = additionalContent + '$finder-color-tinting1: ' + process.env.VUE_APP_COLOR_TINTING1 + ';'
          }

          if (process.env.VUE_APP_COLOR_TINTING2) {
            additionalContent = additionalContent + '$finder-color-tinting2: ' + process.env.VUE_APP_COLOR_TINTING2 + ';'
          }

          if (process.env.VUE_APP_COLOR_SHADING1) {
            additionalContent = additionalContent + '$finder-color-shading1: ' + process.env.VUE_APP_COLOR_SHADING1 + ';'
          }

          if (process.env.VUE_APP_COLOR_SHADING2) {
            additionalContent = additionalContent + '$finder-color-shading2: ' + process.env.VUE_APP_COLOR_SHADING2 + ';'
          }

          if (process.env.VUE_APP_COLOR_SHADING3) {
            additionalContent = additionalContent + '$finder-color-shading3: ' + process.env.VUE_APP_COLOR_SHADING3 + ';'
          }

          if (process.env.VUE_APP_COLOR_SHADING) {
            additionalContent = additionalContent + '$finder-color-shading: ' + process.env.VUE_APP_COLOR_SHADING + ';'
          }

          additionalContent = additionalContent + '@import "@/assets/styles/vue.scss";'
          return additionalContent + content
        }
      }
    },
    ...cssForProduction
  },

  pages: {
    app: {
      entry: 'src/main.ts',
      template: 'public/index.html',
      filename: 'index.html'
    },
    integration: {
      entry: 'src/main.ts',
      template: 'public/integration.html',
      filename: 'integration.html'
    }
  },

  pluginOptions: {
    svgSprite: {
      dir: 'src/assets/icons'
    },
    storybook: {
      allowedPlugins: [
        'svg-sprite'
      ]
    },
    i18n: {
      locale: 'de',
      fallbackLocale: 'en',
      localeDir: 'locales',
      enableLegacy: false,
      runtimeOnly: false,
      compositionOnly: false,
      fullInstall: true
    }
  },

  configureWebpack: {
    plugins: [
      new StyleLintPlugin({
        files: ['src/**/*.{vue,css,scss}']
      }),
      /* eslint-disable-next-line no-undef */
      new webpack.BannerPlugin(banner)
    ],
    ...configureWebpackForProduction
  },

  chainWebpack: config => {
    config.module
      .rule('svg-sprite')
      .use('svgo-loader')
      .loader('svgo-loader')
  },

  devServer: {
		// open: true,
		// compress: true,
		// https: true,
		hot: true,
		// allowedHosts: 'all',
		historyApiFallback: true,
		port: 8080,
		host: '0.0.0.0',
		// static: {
		// 	directory: path.resolve(__dirname, './app'),
		// 	staticOptions: {},
		// 	watch: true,
		// },

	},
}
