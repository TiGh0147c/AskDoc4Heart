// pages/ai-dialogue/ai-dialogue.js
const app = getApp(); // 获取全局应用实例

Page({

  data: {
    userid: '',
    counselorid: '',
    scrollViewHeight: 650, // 示例高度，请根据实际情况调整
    messages: [],
    messageContent: "", // 初始化为空字符串
    messageTableName: '',
    session_id: '',
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
    const userid = app.getGlobalData('userid');
    console.log('Counselor ID:', counselorId);
    console.log('Counselor name:', counselorName);
    //console.log('问题简述:', description);
    app.setGlobalData('counseling', 1); 
    this.setData({userid: userid});
    this.setData({counselorid: counselorId});

    /* 模拟创建咨询会话 */
    const now = new Date();
    const data = {
      user_id: this.data.userid,
      counselor_id: this.data.counselorid,
      Session_Start_Time: now,
      Last_Message_Sent_Time: now,
      Session_Status: "in_progress"
    }
    console.log("创建了会话:", data);
    const getdata={
      Session_Id: "0001"
    }
    this.setData({session_id: getdata.Session_Id});
    console.log("获取会话id", getdata);
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

  },

  addMessage(message) {
    // 模拟插入消息数据
    const time = new Date(); // 获取当前时间
    const data = {
      message_content: message,
      session_id: this.data.session_id,
      sender_role: 'user',
      sender_id: this.data.userid, 
      message_type: 'user-message', 
      message_sent_time: time
    };
    console.log("发送了消息：", data);

    const Message = {
      content: data.message_content,
      message_type: data.message_type, 
      timestamp: data.message_sent_time
    };

    // 更新messages数组并将新消息添加进去
    this.setData({
      messages: this.data.messages.concat([Message])
    }, () => {
      // 可选：滚动到底部确保最新消息可见
      this.scrollToBottom();
    });

    // 获取ai回复
    wx.cloud.callFunction({
      name: 'SuShi',
      data: {
        userId: this.data.userid,
        message: message
      },
      success: (res) => {
        // 模拟插入ai消息数据
        const aimessage = res.result.data.data.choices[0].messages[0].content;
        const time = new Date(); // 获取当前时间
        const data = {
          message_content: aimessage,
          session_id: this.data.session_id,
          sender_role: 'counselor',
          sender_id: this.data.counselorid, 
          message_type: 'counselor-message', 
          message_sent_time: time
        };
        console.log("接收到ai的消息：", data);

        const aiMessage = {
          content: data.message_content,
          message_type: data.message_type, 
          timestamp: data.message_sent_time
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

  sendMessage: function() {
    const messageContent = this.data.messageContent.trim();
    if (!messageContent) return;
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
  onConfirmEndConsultation() {
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
  // 显示结束咨询的模态框
  showConfirmModal() {
    wx.showModal({
      title: '提示',
      content: '确定要结束咨询吗？',
      success: (res) => {
        if (res.confirm) {
          // 用户点击了确认按钮
          this.setData({ isButtonVisible: false });
          this.onConfirmEndConsultation();
        } else if (res.cancel) {
          // 用户点击了取消按钮
          console.log('用户取消了操作');
        }
      },
    });
  },
  submitRating() {
    console.log(`提交的评分是：${this.data.currentRating}`);
    // 处理评分提交逻辑...
    
    /* 模拟结束咨询会话 */
    const now = new Date();
    const data = {
      session_id: this.data.session_id,
      Session_End_Time: now
    }
    console.log("结束了会话:", data);
    this.hideRatingModal();
    app.setGlobalData('counseling', 0); 
    wx.reLaunch({ 
      url: '/pages/index/index', 
    })
  },
})