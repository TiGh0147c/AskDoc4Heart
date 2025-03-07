// pages/reportdetail/reportdetail.js
Page({

  data: {
    reportId: '', // 用于存储传入的id
  },

  onLoad(options) {
    if (options && options.id) {
      this.setData({
        reportId: options.id,
      });
    }
  },
  
})