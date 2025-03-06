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
    const { birthYear, gender, nickname } = e.detail.value;
    let errorMsg = '';

    if (!nickname) errorMsg += '昵称 ';
    if (!birthYear) errorMsg += '出生年份 ';
    if (!gender) errorMsg += '性别 ';

    if (errorMsg !== '') {
      wx.showToast({
        title: `${errorMsg}未填写`,
        icon: 'none',
        duration: 3000
      });
    } else {
      // 提交表单数据到数据库
      wx.cloud.database().collection('user_account').add({
        data: {
          wid: this.data.openid,
          nickname: nickname,
          birthYear: birthYear,
          gender: gender,
          avatarUrl: null
        },
        success: res => {
          wx.showToast({
            title: '注册成功',
            icon: 'success',
            duration: 2000,
            success: () => {
              setTimeout(() => {
                wx.reLaunch({
                  url: '/pages/index/index'
                });
              }, 2000);
            }
          });
        },
        fail: err => {
          console.error('[数据库] [新增记录] 失败：', err);
          wx.showToast({
            title: '注册失败，请稍后再试',
            icon: 'none'
          });
        }
      });
    }
  }
});