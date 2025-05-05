// pages/recorddetail/recorddetail.js
Page({

  data: {
    session_id: '', // 用于存储传入的id
    messages: [], // 用于存储历史消息
  },

  onLoad(options) {
    if (options && options.id) {
      this.setData({
        session_id: options.id,
      });
      console.log(`正在查看咨询会话${options.id}的历史记录`)
      this.getAllmessages();
    }
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