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
    const { gender, nickname, phonenum, password, password2 } = e.detail.value;
    const { birthday, occupation} = this.data;
    let errorMsg = '';

    if (!nickname) errorMsg += '昵称未填写  ';
    if (!birthday) errorMsg += '出生日期未填写  ';
    if (!gender) errorMsg += '性别未填写  ';
    if (!occupation) errorMsg += '职业未填写  ';
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
        openId: this.data.openid,
        username: nickname,
        birthday: birthday,
        occupation: occupation,
        gender: gender,
        phonenumber: phonenum,
        password: password // 注意：实际应用中应加密传输和存储密码
      };
      this.registerUser(data);
    }
  },

  registerUser: function(data) {
    const url = 'http://localhost:8081/user';
    const formData = data;
    console.log("传入注册数据：", formData);

    wx.request({
      url: url,
      method: 'POST',
      header: {
        'Content-Type': 'application/json'
      },
      data: JSON.stringify(formData),
      success(res) {
        const responseData = res.data;
        // 检查后端返回的状态码
        if (responseData.code === 200) {
            console.log('注册成功:', responseData);
            wx.showToast({
                title: '注册成功',
                icon: 'success'
            });
            app.setGlobalData('userid', responseData.data.id);
            app.setGlobalData('userName', responseData.data.username);
            wx.reLaunch({url: '/pages/index/index'});
        } else {
            console.error('注册失败:', responseData.message || '未知错误');
            wx.showToast({
                title: `注册失败: ${responseData.message || '未知错误'}`,
                icon: 'none'
            });
        }
      },
      fail(err) {
        console.error('请求失败:', err);
        wx.showToast({
          title: '注册失败',
          icon: 'none'
        });
      }
    });
  }

});