// pages/register.js
const app = getApp(); // 获取全局应用实例

Page({
  data: {
    openid: '', 
    birthday: '',
    occupation: '',
    jobOptions: ['学生', '专业人士（如教师/医生/律师等）', '服务业人员', '自由职业者', '工人', '公司职员', '雇主', '个体户', '公务员', '家庭主妇', '农民/牧民/渔夫', '无业/失业', '其他'], // 职业选项列表
  },

  onLoad: function(options) {
    // 从全局变量获取OpenID
    const openid = app.getGlobalData('openid'); 
    this.setData({
      openid: openid
    });
  },

  // 处理日期选择器的事件
  onDateChange(e) {
    const birthday = e.detail.value; // 获取用户选择的日期
    this.setData({
      birthday: birthday
    });
  },

  // 处理职业选择器的事件
  onJobChange(e) {
    const selectedIndex = e.detail.value; // 获取用户选择的索引
    const occupation = this.data.jobOptions[selectedIndex]; // 根据索引获取职业名称
    this.setData({
      occupation: occupation
    });
  },

  formSubmit: function(e) {
    const { gender, nickname, email, phonenum, password, password2 } = e.detail.value;
    const { birthday, occupation} = this.data;
    let errorMsg = '';

    if (!nickname) errorMsg += '昵称未填写  ';
    if (!birthday) errorMsg += '出生日期未填写  ';
    if (!gender) errorMsg += '性别未填写  ';
    if (!occupation) errorMsg += '职业未填写  ';
    if (!email) errorMsg += '邮箱未填写  ';
    if (!phonenum) errorMsg += '手机号未填写  ';
    if (!password || !password2) errorMsg += '密码或确认密码未填写  ';
    else if (password !== password) errorMsg += '密码与确认密码不一致  ';

    if (errorMsg !== '') {
      wx.showToast({
        title: `${errorMsg}`,
        icon: 'none',
        duration: 3000
      });
    } else {
      // 模拟传入注册数据
      const data = {
        open_id: this.data.openid,
        nickname: nickname,
        birthday: birthday,
        occupation: occupation,
        gender: gender,
        email: email,
        phone_number: phonenum,
        password: password // 注意：实际应用中应加密传输和存储密码
      };
      console.log("传入注册数据：", data);
      // 调用云函数进行注册
      wx.cloud.callFunction({
        name: 'registerUser', 
        data: data,
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
                    url: '/pages/login/login'
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