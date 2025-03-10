// pages/home/home.js
const app = getApp(); // 获取全局应用实例

Page({

  data: {
    openid: '',
    userName: '',
    avatarUrl: '' // 头像URL
  },

  onShow: function() {
    // 从全局变量获取用户名
    const openid = app.getGlobalData('openid'); 
    const userName = app.getGlobalData('userName');
    const avatarUrl = app.getGlobalData('avatarUrl');
    this.setData({
      openid: openid,
      userName: userName,
      avatarUrl: avatarUrl
    });
  },

  navigateToCounseling: function() {
    wx.reLaunch({
      url: '/pages/index/index', 
    })
  },

  navigateToHome: function() {
    wx.reLaunch({ 
      url: '/pages/home/home', 
    })
  }

})