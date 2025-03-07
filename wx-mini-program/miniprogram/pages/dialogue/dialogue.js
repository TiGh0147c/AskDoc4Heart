// pages/dialogue/dialogue.js
const app = getApp(); // 获取全局应用实例

Page({

  data: {
    scrollViewHeight: 650, // 示例高度，请根据实际情况调整
    messages: [],
    messageTableName: ''
  },

  onLoad(options) {
    const counselorId = options.counselorId;
    const counselorName = options.counselorName;

    console.log('Counselor ID:', counselorId);
    console.log('Counselor name:', counselorName);

    // 动态计算scroll-view的高度，例如减去顶部导航栏和底部输入框的高度
    const that = this;
    wx.getWindowInfo({
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
    
  }
})