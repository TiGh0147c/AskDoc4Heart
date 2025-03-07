// pages/record/record.js
Page({

  data: {
    records: [],
    loading: true, // 添加一个加载状态变量
  },

  onLoad: function () {

    // 定义日期格式化函数
    function formatDate(date) {
      const y = date.getFullYear();
      const m = String(date.getMonth() + 1).padStart(2, '0'); // 月份从0开始，所以需要加1
      const d = String(date.getDate()).padStart(2, '0');
      const hh = String(date.getHours()).padStart(2, '0'); // 小时（24小时制）
      const mm = String(date.getMinutes()).padStart(2, '0'); // 分钟
            
      return `${y}-${m}-${d} ${hh}:${mm}`;
    }
    

    /* 待实现 
    wx.cloud.callFunction({
      name: 'checkRecord',
      success: res => {
        // 将传回的双层数组转为单层 
        const recordData = Array.isArray(res.result.data[0]) ? res.result.data[0] : res.result.data;
        this.setData({
          records: recordData || [],
          loading: false
        });
      },
      fail: err => {
        console.error(err);
        this.setData({
          loading: false // 即使加载失败也将loading设为false
        });
      }
    });
    */

    //测试用
    const currentDate = new Date();
    const recordData = [{
      _id: "001",
      date: currentDate
    }, {
      _id: "002",
      date: currentDate
    }]; 
    //测试用

    // 格式化records中的date字段
    const formattedRecords = recordData.map(record => ({
      ...record,
      date: record.date instanceof Date ? formatDate(record.date) : record.date // 如果不是Date对象，则保持原样
    }));

    this.setData({
      records: formattedRecords,
      loading: false
    });
    
  },

  toggleDetails(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/recorddetail/recorddetail?id=${id}`, 
    });
  },

})
