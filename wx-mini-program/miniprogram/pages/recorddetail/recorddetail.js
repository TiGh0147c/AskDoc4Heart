// pages/recorddetail/recorddetail.js
Page({

  data: {
    session_id: '', // 用于存储传入的id
    messages: [], // 用于存储历史消息
    scrollViewHeight: 650, // 示例高度，请根据实际情况调整
  },

  onLoad(options) {
    if (options && options.id) {
      this.setData({
        session_id: options.id,
      });
      console.log(`正在查看咨询会话${options.id}的历史记录`)
      this.getAllmessages();
    }

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
        } else {
          console.log("查询历史消息失败：",res);
        }
      },
      fail(err) {
        console.error('请求失败:', err);
      }
    });
  },

})