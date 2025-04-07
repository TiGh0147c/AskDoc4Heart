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
          // 模拟查询用户数据
          const data = {openid: res.result.openid};
          console.log("传入openid", data);
          const getdata = {
            success: true,
            user_id: "0001",
            nickname: "未命名",
            avatarUrl: null
          }
          if(getdata.success){
            app.setGlobalData('userid', getdata.user_id);
            app.setGlobalData('userName', getdata.nickname);
            app.setGlobalData('avatarUrl', getdata.avatarUrl);
            console.log("用户", getdata.nickname, "已登录 用户id为", getdata.user_id, " 头像为", getdata.avatarUrl);
            wx.reLaunch({
              url: '/pages/index/index'
            });
          } else {
            wx.reLaunch({
              url: '/pages/register/register'
            });
          }

          /*
          // 调用 getname 函数查询用户名和头像
          wx.cloud.callFunction({
            name: 'getname',
            data: data,
            success: res => {
              if (res.result.success) {
                app.setGlobalData('userid', res.result.userid); 
                app.setGlobalData('userName', res.result.nickname); 
                app.setGlobalData('avatarUrl', res.result.avatarUrl); 
                console.log("用户",res.result.nickname,"已登录 用户id为", res.result.userid," 头像为", res.result.avatarUrl)
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
          });*/
        } else {
          console.log('未能获取到用户OpenID');
        }
      }
    });
  }
});