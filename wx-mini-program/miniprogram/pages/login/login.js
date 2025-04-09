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
          // 查询用户数据
          this.Login(res.result.openid);
        } else {
          console.log('未能获取到用户OpenID');
        }
      }
    });
  },

  Login: function(openid) {
    const url = 'http://localhost:8081/user/login';

    wx.request({
      url: url,
      method: 'GET',
      header: {
        'Content-Type': 'application/json'
      },
      data: {
        open_id: openid, // 请求参数
      },
      success(res) {
        const responseData = res.data;
        // 检查后端返回的状态码
        if (responseData.code === 200) {
          console.log('登录成功:', responseData);
          app.setGlobalData('userid', responseData.data.id);
          app.setGlobalData('userName', responseData.data.username);
          //app.setGlobalData('email', responseData.email);
          //app.setGlobalData('avatarUrl', responseData.avatarUrl);
          wx.reLaunch({
            url: '/pages/index/index'
          });
        } else if (responseData.code === 500) {
          console.log('登录失败:', responseData.message);
          wx.reLaunch({
            url: '/pages/register/register'
          });
        } else {
            console.error(responseData || '未知错误');
            console.error('登录失败:', responseData.message || '未知错误');
        }
      },
      fail(err) {
        console.error('请求失败:', err);
      }
    });
  }
});