// pages/brief/brief.js
Page({

  data: {
    typeArray: ['情绪障碍', '人际关系问题', '饮食与睡眠障碍', '学习与发展障碍', '职业发展困惑', '物质依赖', '创伤后应激障碍（PTSD）', '其他'],
    typeIndex: 0,
    problemDescription: ''
  },

  bindTypeChange(e) {
    this.setData({
      typeIndex: e.detail.value
    });
  },

  formSubmit(e) {
    const formData = e.detail.value;
    // 传递问题类型和描述到下一个页面
    const problemType = this.data.typeArray[this.data.typeIndex];
    
    // 跳转至counseling页面并携带参数
    wx.reLaunch({
      url: `/pages/counseling/counseling?problemType=${encodeURIComponent(problemType)}&description=${formData.problemDescription}`
    });
  }
})