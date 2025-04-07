Page({

  navigateToCounseling: function() {
    wx.reLaunch({
      url: '/pages/index/index', 
    })
  },

  navigateToHome: function() {
    wx.reLaunch({ 
      url: '/pages/home/home', 
    })
  }

});
