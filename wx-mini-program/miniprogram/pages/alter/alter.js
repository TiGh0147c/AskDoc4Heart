// pages/alter/alter.js
const app = getApp(); // 获取全局应用实例
const db = wx.cloud.database();

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
      filePath: this.data.avatarUrl,
      success: res => {
        const fileID = res.fileID;
        // 更新数据库中的用户信息
        db.collection('user_account').where({
          _openid: this.data.openid
        }).update({
          data: {
            avatarUrl: fileID
          }
        }).then(() => {
          wx.showToast({
            title: '上传成功',
          });
          app.setGlobalData('avatarUrl', fileID); 
        }).catch(err => {
          console.error('更新用户头像失败', err);
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

    db.collection('user_account').where({
      _openid: this.data.openid
    }).update({
      data: {
        nickname: newNickname
      }
    }).then(() => {
      wx.showToast({
        title: '修改成功',
      });
      app.setGlobalData('userName', newNickname); 
      this.setData({
        userName: newNickname,
        newNickname: '',
        isModalVisible: false // 关闭弹窗
      });
    }).catch(err => {
      console.error('更新昵称失败', err);
      wx.showToast({
        title: '修改失败，请稍后再试',
        icon: 'none'
      });
    });
  }
})