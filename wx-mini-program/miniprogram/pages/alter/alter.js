// pages/alter/alter.js
const app = getApp(); // 获取全局应用实例

Page({

  data: {
    userid: '',
    userName: '', // 当前昵称
    email: '123456789@qq.com', // 邮箱
    newUserName: '', // 新昵称
    newEmail: '', // 新邮箱
    avatarUrl: '', // 头像URL
    avatarFile: null // 上传的头像文件路径
  },

  onLoad: function() {
    // 从全局变量获取用户名
    const userid = app.getGlobalData('userid'); 
    const userName = app.getGlobalData('userName');
    this.setData({
      userid: userid,
      userName: userName
    });
  },

  chooseImage: function () {
    const that = this; // 在此处保存 this 的引用
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      camera: 'back',
      success: res => {
        console.log('Choose Media Result:', res); // 打印结果用于调试
        if (res.tempFiles && res.tempFiles.length > 0) {
          that.setData({
            avatarUrl: res.tempFiles[0].tempFilePath
          });
          app.setGlobalData('avatarUrl', that.avatarUrl); 
          that.setData({ newUserName: this.data.userName, newEmail: this.data.email });
          that.sendRequest();
        } else {
          console.error('没有选择任何图片');
        }
      },
      fail: err => {
        console.error('选择图片失败', err);
        // 处理可能的用户取消或权限问题
        if (err.errMsg.includes("cancel")) {
          console.log("用户取消了选择");
        } else if (err.errMsg.includes("auth")) {
          console.error("权限问题", err);
        }
      }
    })
  },

  // 监听昵称输入
  onNicknameInput(e) {
    this.setData({
      newNickname: e.detail.value
    });
  },

  // 监听邮箱输入
  onEmailInput(e) {
    this.setData({
      newEmail: e.detail.value
    });
  },

  showModal: function(e) {
    const mode = e.currentTarget.dataset.mode; // 获取按钮传递的 mode 参数
    this.setData({
      isModalVisible: true,
      mode: mode,
      newNickname: mode === 'nickname' ? '' : this.data.newNickname, // 清空新昵称输入框
      newEmail: mode === 'email' ? '' : this.data.newEmail // 清空新邮箱输入框
    });
  },

  hideModal: function() {
    this.setData({
      isModalVisible: false,
      newNickname: '' // 清空输入框
    });
  },

  alter: function() {
    const { mode, newNickname, newEmail } = this.data;

    if (mode === 'nickname') {
      // 修改昵称逻辑
      if (!newNickname.trim()) {
        wx.showToast({
          title: '昵称不能为空',
          icon: 'none'
        });
        return;
      }
      this.setData({ newUserName: newNickname, newEmail: this.data.email });
      this.sendRequest();
    } else if (mode === 'email') {
      // 修改邮箱逻辑
      if (!newEmail.trim()) {
        wx.showToast({
          title: '邮箱不能为空',
          icon: 'none'
        });
        return;
      }
      this.setData({ newUserName: this.data.userName, newEmail: newEmail });
      this.sendRequest();
    }
  },

  // 获取用户输入的值
  getUserInput() {
    const formData = {
      userId: this.data.userid,
      nickname: this.data.newUserName || '',
      email: this.data.newEmail || ''
    };

    return formData;
  },

  // 模拟发送请求
  sendRequest() {
    const url = 'https://your-api-endpoint.com/api/profile-management/user/modification';
    const formData = this.getUserInput();
    console.log("发送了修改请求：", formData);
    this.hideModal();
    /*
    // 检查是否有头像文件
    if (this.data.avatarFile) {
      // 使用 wx.uploadFile 上传文件和其他表单数据
      wx.uploadFile({
        url: url,
        filePath: this.data.avatarFile, // 头像文件路径
        name: 'avatarFile', // 文件字段名
        formData: formData, // 其他表单数据
        success(res) {
          console.log('请求成功:', res.data);
          wx.showToast({
            title: '修改成功',
            icon: 'success'
          });
          this.hideModal();
        },
        fail(err) {
          console.error('请求失败:', err);
          wx.showToast({
            title: '修改失败',
            icon: 'none'
          });
        }
      });
    } else {
      // 如果没有头像文件，直接使用 wx.request 发送表单数据
      wx.request({
        url: url,
        method: 'POST',
        header: {
          'Content-Type': 'application/json'
        },
        data: formData,
        success(res) {
          console.log('请求成功:', res.data);
          wx.showToast({
            title: '修改成功',
            icon: 'success'
          });
          this.hideModal();
        },
        fail(err) {
          console.error('请求失败:', err);
          wx.showToast({
            title: '修改失败',
            icon: 'none'
          });
        }
      });
    }*/
  }
})