module.exports = {
  //outputDir : npm run build로 빌드 시에 파일이 생성되는 위치
  outputDir: "../src/main/resources/static",
  //indexPath : index.html 파일이 생성될 위치
  indexPath: "../static/index.html",
  //SpringBoot 서버 접속 주소
  devServer: {
    proxy: "http://localhost:8080"
  },
  chainWebpack: config => {
    const svgRule = config.module.rule("svg");
    svgRule.uses.clear();
    svgRule.use("vue-svg-loader").loader("vue-svg-loader");
  }
};