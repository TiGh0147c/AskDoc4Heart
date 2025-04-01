// pages/counseling/counseling.js
const app = getApp(); // 获取全局应用实例

Page({

  data: {
    counselors: [{
      _id: "7456afe067c8eeeb007246c80553c0f5",
      type: "ai",
      name: "苏轼",
      intro: "这是苏东坡，他没有咨询师资格证，但你可以和他聊聊。",
      tag: ["东坡肉", "出去玩", "被贬"],
      isActive: false
    }, {
      _id: "2134556yhgbfd32122345yh564t321r",
      type: "counselor",
      name: "张三",
      intro: "这是咨询师，真的是咨询师。",
      tag: ["犯法", "文盲", "被捕"],
      isActive: false
    }],
    description: '',
    showDetails: {}, // 控制每个咨询师详情是否显示的状态对象
    loading: true, // 添加一个加载状态变量
    bgColor: 'rgb(68, 88, 120)', // 初始背景颜色
    color: 'rgb(238, 239, 247)',  // 初始文字颜色
    buttonMargins: {}, // 按钮的 margin-bottom 样式
  },

  onLoad: function (options) {
    console.log('问题类型:', decodeURIComponent(options.problemType));
    console.log('问题简述:', decodeURIComponent(options.description));
    this.setData({
      description: decodeURIComponent(options.description),
      loading: false // 启用云函数时删掉
    });
    /* 
    wx.cloud.callFunction({
      name: 'checkduty',
      success: res => {
        // 初始化常驻咨询师 
        const everyday = [{
          _id: "7456afe067c8eeeb007246c80553c0f5",
          type: "ai",
          name: "苏轼",
          intro: "这是苏东坡，他没有咨询师资格证，但你可以和他聊聊。",
          tag: ["东坡肉", "出去玩", "被贬"]
        }];
        // 将传回的双层数组转为单层 
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
    */
  },
  
  toggleDetails(e) {
    const id = e.currentTarget.dataset.id;
  
    // 更新所有顾问的 isActive 状态，并关闭其他详情栏
    const updatedCounselors = this.data.counselors.map(counselor => {
      if (counselor._id === id) {
        return { ...counselor, isActive: !counselor.isActive }; // 切换当前顾问的激活状态
      } else {
        return { ...counselor, isActive: false }; // 其他顾问重置为未激活
      }
    });

    // 关闭所有详情栏
    const showDetails = {};
    if (!this.data.showDetails[id]) {
      showDetails[id] = true; // 打开当前顾问的详情栏
    }

    this.setData({
      counselors: updatedCounselors,
      showDetails: showDetails, // 更新详情栏状态
    });
    
    // 动态调整按钮样式
    if (showDetails[id]) {
      // 如果详情显示，获取高度并设置 margin-bottom
      this.getDetailsHeight(id);
    } else {
      // 如果详情隐藏，重置 margin-bottom
      const buttonMargins = { ...this.data.buttonMargins };
      buttonMargins[id] = 0;
      this.setData({ buttonMargins });
    }
  },

  // 获取 details 高度并设置按钮的 margin-bottom
  getDetailsHeight(id) {
    const query = wx.createSelectorQuery();
    query.select(`#details-${id}`).boundingClientRect((rect) => {
      if (rect) {
        const buttonMargins = { ...this.data.buttonMargins };
        buttonMargins[id] = rect.height + 10; // 设置 margin-bottom 为详情高度 + 10px
        this.setData({ buttonMargins });
      }
    }).exec();
  },

  navigateToDialogue(e) {
    const dataset = e.currentTarget.dataset;
    const counselorId = dataset.id;
    const counselorName = dataset.name;
    const description = this.data.description;
    if(dataset.type == "ai"){
      console.log('选择了ai咨询师');
      wx.reLaunch({
        url: `/pages/ai-dialogue/ai-dialogue?counselorId=${counselorId}&counselorName=${counselorName}&description=${description}`
      });
    } else {
      wx.reLaunch({
        url: `/pages/dialogue/dialogue?counselorId=${counselorId}&counselorName=${counselorName}&description=${description}`
      });
    }
  }

})
