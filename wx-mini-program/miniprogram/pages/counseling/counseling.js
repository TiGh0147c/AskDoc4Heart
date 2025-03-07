// pages/counseling/counseling.js
const app = getApp(); // 获取全局应用实例

Page({

  data: {
    counselors: [],
    showDetails: {}, // 控制每个咨询师详情是否显示的状态对象
    loading: true, // 添加一个加载状态变量
    bgColor: 'rgb(68, 88, 120)', // 初始背景颜色
    color: 'rgb(238, 239, 247)'   // 初始文字颜色
  },

  onLoad: function () {
    wx.cloud.callFunction({
      name: 'checkduty',
      success: res => {
        /* 初始化常驻咨询师 */
        const everyday = [{
          _id: "7456afe067c8eeeb007246c80553c0f5",
          name: "苏轼",
          intro: "这是苏东坡，他没有咨询师资格证，但你可以和他聊聊。",
          tag: ["东坡肉", "出去玩", "被贬"]
        }];
        /* 将传回的双层数组转为单层 */
        const counselorsData = Array.isArray(res.result.data[0]) ? res.result.data[0] : res.result.data;
        this.setData({
          counselors: everyday || counselorsData || [],
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
  },
  
  toggleDetails(e) {
    const currentBgColor = this.data.bgColor === 'rgb(68, 88, 120)' ? 'rgb(28, 29, 33)' : 'rgb(68, 88, 120)'; // 改变背景颜色
    const currentColor = this.data.color === 'rgb(238, 239, 247)' ? 'rgb(146, 205, 207)' : 'rgb(238, 239, 247)';     // 改变文字颜色
    const id = e.currentTarget.dataset.id;
    // 使用ES6计算属性名称来切换特定辅导员的详情显示状态
    this.setData({
      [`showDetails.${id}`]: !this.data.showDetails[id],
      bgColor: currentBgColor,
      color: currentColor
    });
  },

  navigateToDialogue(e) {
    const dataset = e.currentTarget.dataset;
    const counselorId = dataset.id;
    const counselorName = dataset.name;
    wx.navigateTo({
      url: `/pages/dialogue/dialogue?counselorId=${counselorId}&counselorName=${counselorName}`
    });
  }

})
