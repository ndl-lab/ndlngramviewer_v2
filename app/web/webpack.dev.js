const { merge } = require("webpack-merge"); // webpack-merge
var common = require("./webpack.common.js"); // 汎用設定をインポート
var webpack = require("webpack");

module.exports = merge(common, {
  mode: "development",
  output: {
    path: __dirname + "/public/ngramviewer/assets/",
    publicPath: "/ngramviewer/assets/",
    filename: "[name].bundle.js",
  },
  devServer: {
    contentBase: __dirname + "/public/",
    inline: false,
    hot: true,
    host: "0.0.0.0",
    disableHostCheck: true,
    proxy: [
      {
        context: ["/ngramviewer/api/**"],
        target: "http://localhost:19981/",
      },
    ],
    historyApiFallback: {
      index: "/index.html",
    },
  },
  plugins: [
    new webpack.DefinePlugin({
      DEFINE_BASE_FULL_PATH: "'/ngramviewer/'",
    }),
  ],
  devtool: "inline-source-map",
});
