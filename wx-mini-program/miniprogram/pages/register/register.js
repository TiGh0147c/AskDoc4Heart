// pages/register.js
const app = getApp(); // 获取全局应用实例

Page({
  data: {
    openid: '', 
  },

  onLoad: function(options) {
    // 从全局变量获取OpenID
    const openid = app.getGlobalData('openid'); 
    this.setData({
      openid: openid
    });
  },

  formSubmit: function(e) {
    const { birthYear, gender, nickname, phonenum, password, password2 } = e.detail.value;
    let errorMsg = '';

    if (!nickname) errorMsg += '昵称未填写 ';
    if (!birthYear) errorMsg += '出生年份未填写 ';
    if (!gender) errorMsg += '性别未填写 ';
    if (!phonenum) errorMsg += '手机号未填写 ';
    if (!password || !password2) errorMsg += '密码或确认密码未填写 ';
    else if (password !== password) errorMsg += '密码与确认密码不一致 ';

    if (errorMsg !== '') {
      wx.showToast({
        title: `${errorMsg}`,
        icon: 'none',
        duration: 3000
      });
    } else {
      // 调用云函数进行注册
      wx.cloud.callFunction({
        name: 'registerUser', 
        data: {
          openid: this.data.openid,
          nickname: nickname,
          birthYear: birthYear,
          gender: gender,
          phonenum: phonenum,
          password: password // 注意：实际应用中应加密传输和存储密码
        },
        success: res => {
          if(res.result.success){
            wx.showToast({
              title: '注册成功',
              icon: 'success',
              duration: 2000,
              success: () => {
                setTimeout(() => {
                  app.setGlobalData('userName', nickname); 
                  wx.reLaunch({
                    url: '/pages/index/index'
                  });
                }, 2000);
              }
            });
          }else{
            wx.showToast({
              title: res.result.message || '注册失败，请稍后再试',
              icon: 'none'
            });
          }
        },
        fail: err => {
          console.error('[云函数] [registerUser] 调用失败：', err);
          wx.showToast({
            title: '网络错误，请稍后再试',
            icon: 'none'
          });
        }
      });
    }
  }
});