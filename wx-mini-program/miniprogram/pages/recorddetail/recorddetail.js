// pages/recorddetail/recorddetail.js
Page({

  data: {
    recordId: '', // 用于存储传入的id
  },

  onLoad(options) {
    if (options && options.id) {
      this.setData({
        recordId: options.id,
      });
    }
  },

})