// app.js
App({
  onLaunch: function () {
    if (!wx.cloud) {
      console.error("请使用 2.2.3 或以上的基础库以使用云能力");
    } else {
      wx.cloud.init({
        env: "cloud1-8g70jwfb3e7eb245",
        traceUser: true,
      });
    }

    this.globalData = {};
  },
  globalData: {
    openid: null, // 定义一个全局变量用于存储OpenID
    userName: null, // 定义一个全局变量用于存储用户名
    avatarUrl: null, // 定义一个全局变量用于存储用户头像Url
    counseling: 0, // 定义一个全局变量用于存储用户是否在咨询中
  },
  setGlobalData: function(key, value) {
    this.globalData[key] = value;
  },
  getGlobalData: function(key) {
    return this.globalData[key];
  },
});
