// pages/dialogue/dialogue.js
const app = getApp(); // 获取全局应用实例

Page({

  data: {
    userid: '',
    scrollViewHeight: 650, // 示例高度，请根据实际情况调整
    messages: [],
    messageContent: "", // 初始化为空字符串
    messageTableName: '',
    evaluationContent: '',
    session_id: '',
    queue_id: '',
    isButtonVisible: true,
    isConfirmModalVisible: false,
    isRatingModalVisible: false,
    currentRating: 10,
    isMenuVisible: false, // 控制下拉菜单的显示状态
    isEmojiListVisible: false, // 控制表情列表的显示状态
    inputAreaBottom: 0, // 默认 bottom 值
    timer: null, // 用于存储定时器ID
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
    const sessionId = options.sessionId;
    const queue_id = options.queue_id;
    console.log('Counselor ID:', counselorId);
    console.log('Counselor name:', counselorName);
    console.log('问题简述:', description);
    app.setGlobalData('counseling', 1); 
    const userid = app.getGlobalData('userid');
    this.setData({
      userid: userid,
      counselorid: counselorId,
      session_id: sessionId,
      queue_id: queue_id
    });
    console.log("当前的排队序号：", this.data.queue_id);

    this.startTimer();
    this.getAllmessages();
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

  onUnload() {
    this.clearTimer();
  },

  startTimer() {
    const that = this;
    // 设置定时器，每2秒执行一次getMessages
    const timer = setInterval(() => {
      that.getAllmessages();
    }, 2000);
    that.setData({ timer: timer });
  },

  clearTimer() {
    const { timer } = this.data;

    if (timer) {
      clearInterval(timer); // 清除定时器
      console.log('计时器 已清除');
    };

    this.setData({ timer: null })
  },

  // 请求发送消息
  addMessage(message) {
    // 模拟插入消息数据
    const time = new Date(); // 获取当前时间
    const data = {
      sessionId: this.data.session_id,
      userId: this.data.userid.toString(),
      content: message
    };
    console.log("发送了消息：", data);

    const that = this;
    wx.request({
      url: `http://localhost:8081/api/user/chats/${data.sessionId}/messages`,
      method: 'POST',
      data: data,
      header: {'content-type': 'application/json'},
      success(res) {
        if (res.statusCode === 200) {
          console.log("发送消息成功：", res.data);
          that.getAllmessages();
        } else {
          console.log("发送消息失败：",res);
        }
      },
      fail(err) {
        console.error('请求失败:', err);
        wx.showToast({
            title: '网络错误，请稍后再试',
            icon: 'none'
        });
      }
    });
  },

  // 请求获取历史消息
  getAllmessages() {
    const url = `http://localhost:8081/api/counselor/chats/${this.data.session_id}`;
    wx.request({
      url: url,
      method: 'GET',
      header: {'content-type': 'application/json'},
      success: (res) => {
        if (res.statusCode === 200) {
          console.log("查询历史消息成功：", res.data);
          this.setData({
            messages: res.data.messages
          });
          if(res.data.status === "completed"){
            this.clearTimer();
            this.setData({ isButtonVisible: false });
            this.onConfirmEndConsultation();
            wx.showToast({
              title: '咨询师结束了此次咨询',
              icon: 'none',
              duration: 2000
            });
          }
        } else {
          console.log("查询历史消息失败：",res);
        }
      },
      fail(err) {
        console.error('请求失败:', err);
      }
    });
  },

  // 请求结束会话
  leave() {
    const url = `http://localhost:8081/api/counselor/chats/${this.data.session_id}/end`;
    wx.request({
      url: url,
      method: 'PUT',
      header: {'content-type': 'application/json'},
      success: (res) => {
        if (res.statusCode === 200) {
          this.clearTimer();
          console.log("结束会话成功", res.data);
        } else {
          console.log("结束会话失败：",res);
        }
      },
      fail(err) {
        console.error('请求失败:', err);
        wx.showToast({
          title: '网络错误，请稍后再试',
          icon: 'none'
        });
      }
    });
  },

  // 更新队伍状态
  updateQueue(queueId) {
    const url = `http://localhost:8081/api/queues/${queueId}/status?status=completed`;
    const that = this;
    wx.request({
      url: url,
      method: 'POST',
      header: { 'Content-Type': 'application/json' },
      success(res) {
        if (res.statusCode === 200) {
          console.log("更新队伍状态成功：", res.data);
        } else {
          console.log("更新队伍状态失败：",res);
        }
      },
      fail(err) {
        console.error('请求失败:', err);
        wx.showToast({
            title: '网络错误，请稍后再试',
            icon: 'none'
        });
      }
    });
  },

  bindMessageInput: function(e) {
    // 更新messageContent
    this.setData({
      messageContent: e.detail.value
    });
  },

  

  onUnload: function() {
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
      duration: 300 // 滚动动画的持续时间
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
    if(!this.data.timer){
      this.setData({ isButtonVisible: false });
      this.onConfirmEndConsultation();
    } else {
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
    }
  },
  // 监听评价输入框内容变化
  onCommentInput(e) {
    this.setData({
      evaluationContent: e.detail.value,
    });
  },
  submitRating() {
    console.log(`提交的评分是：${this.data.currentRating}`);
     // 获取当前评分和评价内容
    const rating = this.data.currentRating; // 当前评分
    const evaluationContent = this.data.evaluationContent || "无"; // 评价内容，默认值为 "无"
    const session_id = Number(this.data.session_id); // 会话 ID

    const ratingdata = {
      evaluation_content: evaluationContent,
      evaluation_time: new Date().toISOString().split('T')[0], // 当前日期，格式为 YYYY-MM-DD
      rating: rating,
      session_id: session_id
    };
    console.log("提交的评价数据：", ratingdata);
    this.rating(ratingdata);
    this.leave();
    this.updateQueue(this.data.queue_id);
    this.hideRatingModal();
    app.setGlobalData('counseling', 0); 
    wx.reLaunch({ 
      url: '/pages/index/index', 
    })
  },
  rating(data) {
    wx.request({
      url: 'http://localhost:8081/api/evaluation/user', // 后端接口地址
      method: 'POST',
      data: data,
      header: {
        'content-type': 'application/json' // 设置请求头为 JSON 格式
      },
      success(res) {
        if (res.statusCode === 200) {
          console.log("评价提交成功：", res.data);
          wx.showToast({
            title: '评价提交成功',
            icon: 'success',
            duration: 2000
          });
        } else {
          console.error("评价提交失败：", res);
          wx.showToast({
            title: '评价提交失败',
            icon: 'none',
            duration: 2000
          });
        }
      },
      fail(err) {
        console.error("请求失败：", err);
        wx.showToast({
          title: '网络错误',
          icon: 'none',
          duration: 2000
        });
      }
    });
  }
})