// pages/ai-dialogue/ai-dialogue.js
const app = getApp(); // 获取全局应用实例

Page({

  data: {
    openid: '',
    scrollViewHeight: 650, // 示例高度，请根据实际情况调整
    messages: [],
    messageContent: "", // 初始化为空字符串
    messageTableName: '',
    isButtonVisible: true,
    isConfirmModalVisible: false,
    isRatingModalVisible: false,
    currentRating: 10,
    isMenuVisible: false, // 控制下拉菜单的显示状态
    isEmojiListVisible: false, // 控制表情列表的显示状态
    inputAreaBottom: 0, // 默认 bottom 值
    menuItems: [
      { label: "拍照", action: "takePhoto", icon: "/images/icons/拍摄.png" },
      { label: "照片", action: "choosePhoto", icon: "/images/icons/图片.png" },
      { label: "表情", action: "showEmojis", icon: "/images/icons/表情.png" },
    ],
    emojis: ['😊', '😂', '😍', '😎', '😢', '😭', '🤔', '🥰', '❤️', '😱', '🥺', '👍', '👎', '⭐', '😊', '😂', '😍', '😎', '😢', '😭', '🤔', '🥰', '❤️', '😱', '🥺', '👍', '👎', '⭐'], // 表情列表
    animationData: {},
  },

  onLoad(options) {
    const counselorId = options.counselorId;
    const counselorName = options.counselorName;
    const description = options.description;
    const openid = app.getGlobalData('openid');
    console.log('Counselor ID:', counselorId);
    console.log('Counselor name:', counselorName);
    //console.log('问题简述:', description);
    app.setGlobalData('counseling', 1); 
    this.setData({openid: openid});
    this.addMessage(description);
    this.animation = wx.createAnimation({
      duration: 300,
      timingFunction: 'ease-out',
    });

    // 动态计算scroll-view的高度，例如减去顶部导航栏和底部输入框的高度
    const that = this;
    wx.getSystemInfo({
      success: function(res) {
        // 动态计算scroll-view的高度，减去顶部导航栏和底部输入框的高度
        let scrollViewHeight = res.windowHeight - 0 /* 导航栏高度 */ - 80 /* 输入框高度 */;
        that.setData({
          scrollViewHeight: scrollViewHeight
        });
      }
    });

    //this.createChatRecord(counselorId, counselorName);
  },

  addMessage(message) {
    const timestamp = new Date().getTime(); // 获取当前时间戳
    const Message = {
      content: message,
      tag: 0, 
      timestamp: timestamp
    };

    // 更新messages数组并将新消息添加进去
    this.setData({
      messages: this.data.messages.concat([Message])
    }, () => {
      // 可选：滚动到底部确保最新消息可见
      this.scrollToBottom();
    });

    wx.cloud.callFunction({
      name: 'SuShi',
      data: {
        userId: this.data.openid,
        message: message
      },
      success: (res) => {
        console.log(res.result.data.data.choices[0].messages[0].content);
        const aimessage = res.result.data.data.choices[0].messages[0].content;
        // 根据返回结果更新UI
        const aitimestamp = new Date().getTime(); // 获取当前时间戳
        const aiMessage = {
          content: aimessage,
          tag: 1, 
          timestamp: aitimestamp
        };

        // 更新messages数组并将新消息添加进去
        this.setData({
          messages: this.data.messages.concat([aiMessage])
        }, () => {
          // 可选：滚动到底部确保最新消息可见
          this.scrollToBottom();
        });
      },
      fail(err) {
        console.error(err)
      }
    })
  },

  bindMessageInput: function(e) {
    // 更新messageContent
    this.setData({
      messageContent: e.detail.value
    });
  },

  // 目前还没实现，先放着
  createChatRecord: function(counselorId, counselorName) {
    const openid = app.getGlobalData('openid'); 
    if(!openid){
      wx.cloud.callFunction({
        name: 'getid',
        complete: res => {
          if (res.result.openid) {
            // 将OpenID存入全局变量
            app.setGlobalData('openid', res.result.openid); 
          } else {
            console.log('未能获取到用户OpenID');
          }
        }
      });
    }

    wx.cloud.callFunction({
      name: 'createChatRecord',
      data: {
        openid: openid,
        counselorId: counselorId,
        counselorName: counselorName
      },
      success: res => {
        if (res.result.success) {
          const messageTableName = res.result.messageTableName;
          this.setData({ messageTableName: messageTableName }, () => {
            this.listenForNewMessages();
          });
        } else {
          console.error('Failed to create chat record:', res.result.errMsg);
        }
      },
      fail: err => {
        console.error('Call cloud function failed:', err);
      }
    });
    
  },

  listenForNewMessages: function() {
    const db = wx.cloud.database();
    // 使用watch监听指定消息表的变化
    const watcher = db.collection(this.data.messageTableName).where({}).watch({
      onChange: snapshot => {
        // 更新消息列表，确保按时间顺序排序
        this.setData({
          messages: snapshot.docs.sort((a, b) => a.timestamp - b.timestamp)
        });
      },
      onError: err => {
        console.error('监听失败', err);
        // 处理错误，例如重新连接或提示用户
      }
    });
  
    // 可以考虑在onUnload生命周期函数中关闭watcher
    this.setData({ watcher: watcher });
  },

  onUnload: function() {
    // 当页面卸载时，记得关闭watcher以释放资源
    if (this.data.watcher) {
      this.data.watcher.close();
    }
  },

  sendMessage: function() {
    const messageContent = this.data.messageContent.trim();
    if (!messageContent) return;

    console.log('发送了', messageContent);
    this.addMessage(messageContent);
    this.setData({
      messageContent: '' // 清空输入框内容
    });
  },

  toggleMenuOrGoBack: function () {
    if (this.data.isEmojiListVisible) {
      this.goBackToMenu();
    } else if (this.data.isMenuVisible) {
      this.hideMenu();
    } else {
      this.showMenu();
    }
  },

  showMenu: function () {
    this.setData({ isMenuVisible: true, inputAreaBottom: -10 });
    this.animateUp();
  },

  hideMenu: function () {
    this.animateDown();
    setTimeout(() => {
      this.setData({ 
        isMenuVisible: false,
        isEmojiListVisible: false,
        inputAreaBottom: 0, // 重置 input-area 的 bottom 值
      }); 
    }, 300);
  },

  handleMenuItemTap: function (e) {
    const action = e.currentTarget.dataset.action;
    if (action === 'showEmojis') {
      this.showEmojis();
    } else if (action === 'takePhoto'){
      // 调用拍照功能
      wx.chooseMedia({
        count: 1, // 允许拍一张照片
        mediaType: ['image'], // 指定媒体类型为图片
        sourceType: ['camera'], // 指定来源为相机
        success: (res) => {
          const tempFilePaths = res.tempFiles[0].tempFilePath; // 获取拍摄的照片路径
          console.log('拍摄的照片路径:', tempFilePaths);
          wx.showToast({ title: "拍照成功", icon: "success" });
          // 在这里可以将照片路径保存到页面数据或上传到服务器
        },
        fail: () => {
          wx.showToast({ title: "拍照失败", icon: "none" });
        }
      });
    }else if (action === 'choosePhoto'){
      // 调用选择图片功能
      wx.chooseMedia({
        count: 1, // 允许选择一张图片
        mediaType: ['image'], // 指定媒体类型为图片
        sourceType: ['album'], // 指定来源为相册
        success: (res) => {
          const tempFilePaths = res.tempFiles[0].tempFilePath; // 获取选择的图片路径
          console.log('选择的图片路径:', tempFilePaths);
          wx.showToast({ title: "图片选择成功", icon: "success" });
          // 在这里可以将图片路径保存到页面数据或上传到服务器
        },
        fail: () => {
          wx.showToast({ title: "图片选择失败", icon: "none" });
        }
      });
    }
  },

  showEmojis: function () {
    this.setData({ 
      isMenuVisible: false, 
      isEmojiListVisible: true,
      inputAreaBottom: 50, // 调整 input-area 的 bottom 值
    });
  },

  goBackToMenu: function () {
    this.setData({ isMenuVisible: true, isEmojiListVisible: false,inputAreaBottom: -10, // 重置 input-area 的 bottom 值
     });
  },

  selectEmoji: function (e) {
    const emoji = e.currentTarget.dataset.emoji;
    this.setData({ messageContent: this.data.messageContent + emoji });
    this.hideMenu();
  },

  animateUp: function () {
    this.animation.translateY(-150).step();
    this.setData({ animationData: this.animation.export() });
  },

  animateDown: function (callback) {
    this.animation.translateY(0).step();
    this.setData({ animationData: this.animation.export() }, callback);
  },

  scrollToBottom: function() {
    wx.pageScrollTo({
      scrollTop: 999999, // 将页面滚动到底部
      duration: 10 // 滚动动画的持续时间
    });
  },

  showConfirmModal() {
    this.setData({ isButtonVisible: false });
    this.setData({ isConfirmModalVisible: true });
  },
  hideConfirmModal() {
    this.setData({ isButtonVisible: true });
    this.setData({ isConfirmModalVisible: false });
  },
  hideConfirmModal_() {
    this.setData({ isConfirmModalVisible: false });
  },
  onConfirmEndConsultation() {
    this.hideConfirmModal_();
    this.setData({ isRatingModalVisible: true });
  },
  hideRatingModal() {
    this.setData({ isRatingModalVisible: false });
    this.setData({ isButtonVisible: true });
  },
  onRatingChange(e) {
    const rating = e.detail.rating;
    this.setData({
      'currentRating': rating
    });
  },
  submitRating() {
    console.log(`提交的评分是：${this.data.currentRating}`);
    // 处理评分提交逻辑...
    this.hideRatingModal();
    app.setGlobalData('counseling', 0); 
    wx.reLaunch({ 
      url: '/pages/index/index', 
    })
  },
})