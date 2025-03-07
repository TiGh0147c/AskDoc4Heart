// pages/login.js
const app = getApp(); // 获取全局应用实例

Page({
  onLoad: function() {
    this.loginAndCheckUser();
  },

  loginAndCheckUser: function() {
    wx.cloud.callFunction({
      name: 'getid',
      complete: res => {
        if (res.result.openid) {
          // 将OpenID存入全局变量
          app.setGlobalData('openid', res.result.openid); 
          // 调用 getname 函数查询用户名和头像
          wx.cloud.callFunction({
            name: 'getname',
            data: {
              openid: res.result.openid
            },
            success: res => {
              if (res.result.success) {
                app.setGlobalData('userName', res.result.nickname); 
                app.setGlobalData('avatarUrl', res.result.avatarUrl); 
                // 用户存在，跳转到主页
                wx.reLaunch({
                  url: '/pages/index/index'
                });
              } else {
                // 用户不存在，跳转到注册页
                wx.reLaunch({
                  url: '/pages/register/register'
                });
              }
            },
            fail: err => {
              console.error('[云函数] [checkUserExistence] 调用失败', err);
            }
          });
        } else {
          console.log('未能获取到用户OpenID');
        }
      }
    });
  }
});