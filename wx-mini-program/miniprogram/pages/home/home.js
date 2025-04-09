// pages/home/home.js
const app = getApp(); // 获取全局应用实例

Page({

  data: {
    userName: '',
    avatarUrl: '' // 头像URL
  },

  onShow: function() {
    // 从全局变量获取用户名
    const userName = app.getGlobalData('userName');
    const avatarUrl = app.getGlobalData('avatarUrl');
    this.setData({
      userName: userName,
      avatarUrl: avatarUrl
    });
    this.getUser(app.getGlobalData('userid'));
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
  },

  getUser: function(userid) {
    const url = 'http://localhost:8081/api/profile-management/user/profile';
    const formData = {
      userId: userid
    };
    console.log('输入用户id:', formData);
    const that = this;
    wx.request({
      url: url,
      method: 'GET',
      header: {
        'Content-Type': 'application/json'
      },
      data: formData,
      success(res) {
        const responseData = res.data;
        // 检查后端返回的状态码
        if (res.statusCode === 200) {
            console.log('获取用户信息成功:', responseData);
            that.setData({
              userName: responseData.nickname,
              avatarUrl: responseData.avatar
            });
            app.setGlobalData('avatarUrl', responseData.avatar);
            app.setGlobalData('userName', responseData.nickname);
            app.setGlobalData('email', responseData.email);
        } else {
            console.error('获取用户信息失败:', responseData.message || '未知错误');
        }
      },
      fail(err) {
        console.error('请求失败:', err);
      }
    });
  }

})