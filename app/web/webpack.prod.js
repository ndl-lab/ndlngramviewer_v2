const { merge } = require("webpack-merge"); // webpack-merge
var common = require("./webpack.common.js"); // 汎用設定をインポート
var webpack = require("webpack");

module.exports = merge(common, {
  mode: "production",
  output: {
    path: __dirname + "/public/ngramviewer/assets/",
    publicPath: "/ngramviewer/assets/",
    filename: "[name].bundle.js",
  },
  plugins: [
    new webpack.DefinePlugin({
      DEFINE_BASE_FULL_PATH: "'/ngramviewer/'",
    }),
  ],
});
