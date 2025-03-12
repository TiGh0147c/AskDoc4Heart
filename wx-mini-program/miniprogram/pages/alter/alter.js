// pages/alter/alter.js
const app = getApp(); // 获取全局应用实例

Page({

  data: {
    openid: '',
    userName: '', // 当前昵称
    avatarUrl: '' // 头像URL
  },

  onLoad: function() {
    // 从全局变量获取用户名
    const openid = app.getGlobalData('openid'); 
    const userName = app.getGlobalData('userName');
    this.setData({
      openid: openid,
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
          that.uploadImage();
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

  uploadImage: function() {
    if (!this.data.avatarUrl) return;

    wx.cloud.uploadFile({
      cloudPath: 'avatar/' + this.data.openid + '.png', // 使用 openid 作为文件名的一部分
      filePath: this.data.avatarUrl, // 这里应为选择图片后得到的临时路径
      success: res => {
        const fileID = res.fileID;
        // 调用云函数更新数据库中的用户信息
        wx.cloud.callFunction({
          name: 'updateAvatar', // 确保这是您云函数的名字
          data: {
            fileID: fileID,
            openid: this.data.openid
          },
          success: res => {
            const result = res.result;
            if (result.success) {
              wx.showToast({
                title: '上传成功',
              });
              app.setGlobalData('avatarUrl', fileID);
            } else {
              console.error('更新用户头像失败', result.message);
            }
          },
          fail: err => {
            console.error('调用云函数失败', err);
          }
        });
      },
      fail: err => {
        console.error('上传文件失败', err);
      }
    })
  },

  onNicknameInput: function(e) {
    this.setData({
      newNickname: e.detail.value
    });
  },

  showModal: function() {
    this.setData({
      isModalVisible: true
    });
  },

  hideModal: function() {
    this.setData({
      isModalVisible: false,
      newNickname: '' // 清空输入框
    });
  },

  altername: function() {
    const newNickname = this.data.newNickname.trim();
    if (!newNickname) {
      wx.showToast({
        title: '昵称不能为空',
        icon: 'none'
      });
      return;
    }

    // 调用云函数进行昵称更新
    wx.cloud.callFunction({
      name: 'updateNickname',
      data: {
        openid: this.data.openid,
        newNickname: newNickname
      },
      success: res => {
        if(res.result.success){
          wx.showToast({
            title: '修改成功',
          });
          app.setGlobalData('userName', newNickname); 
          this.setData({
            userName: newNickname,
            newNickname: '',
            isModalVisible: false // 关闭弹窗
          });
        }else{
          wx.showToast({
            title: res.result.message || '修改失败，请稍后再试',
            icon: 'none'
          });
        }
      },
      fail: err => {
        console.error('[云函数] [updateNickname] 调用失败：', err);
        wx.showToast({
          title: '网络错误，请稍后再试',
          icon: 'none'
        });
      }
    });
  }
})