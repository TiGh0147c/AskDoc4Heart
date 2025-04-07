// pages/counseling/counseling.js
const app = getApp(); // 获取全局应用实例

Page({

  data: {
    userid: '',
    queue_id: '',
    counselors: [],
    description: '',
    showDetails: {}, // 控制每个咨询师详情是否显示的状态对象
    loading: true, // 添加一个加载状态变量
    bgColor: 'rgb(68, 88, 120)', // 初始背景颜色
    color: 'rgb(238, 239, 247)',  // 初始文字颜色
    buttonMargins: {}, // 按钮的 margin-bottom 样式
    showModal: false, // 控制模态框显示
    modalData: {}, // 模态框需要显示的数据，初始值为空对象
    intervalId1: null,
    intervalId2: null, // 定时器 ID，用于后续清除定时器
  },

  onLoad: function (options) {
    const userid = app.getGlobalData('userid');
    this.setData({userid: userid});
    console.log('问题类型:', decodeURIComponent(options.problemType));
    console.log('问题简述:', decodeURIComponent(options.description));
    this.setData({
      description: decodeURIComponent(options.description),
    });
    this.refresh();
    // 启动定时器，每 10 秒获取一次数据
    const intervalId1 = setInterval(() => {
      this.refresh();
    }, 10000); // 每 10 秒执行一次
    // 保存定时器 ID，以便后续清除
    this.setData({ intervalId1 });
  },

  onLaunch: function () {
    this.refresh();
  },

  refresh(){
    this.setData({
      loading: true
    });
    /* 模拟获取咨询师数据 */
    const everyday = [{
      _id: "7456afe067c8eeeb007246c80553c0f5",
      name: "苏轼",
      intro: "这是苏东坡，他没有咨询师资格证，但你可以和他聊聊。",
      tag: ["东坡肉", "出去玩", "被贬"],
      status: "空闲",
      type: "ai"
    }];
    const everydayData = Array.isArray(everyday[0]) ? everyday[0] : everyday;
    const data = [{
      _id: "2134556yhgbfd32122345yh564t321r",
      name: "张三",
      intro: "这是咨询师，真的是咨询师。",
      tag: ["犯法", "文盲", "被捕"],
      status: "繁忙"
    },{
      _id: "21345523rsbfd32122345yh564t321r",
      name: "李四",
      intro: "显而易见这里是简介。",
      tag: ["想不出来", "就先", "这样吧"],
      status: "空闲"
    }];
    const dutyData = Array.isArray(data[0]) ? data[0] : data;
    const processedEverydayData = everydayData.map(item => ({ ...item, isActive: false }));
    const processedDutyData = dutyData.map(item => ({ ...item, type: "counselor", isActive: false }));
    const counselors = [...processedDutyData, ...processedEverydayData];
    //console.log(counselors);
    this.setData({
      counselors: counselors,
      loading: false
    });
  },
  
  toggleDetails(e) {
    const id = e.currentTarget.dataset.id;
  
    // 更新所有咨询师的 isActive 状态，并关闭其他详情栏
    const updatedCounselors = this.data.counselors.map(counselor => {
      if (counselor._id === id) {
        return { ...counselor, isActive: !counselor.isActive }; // 切换当前咨询师的激活状态
      } else if (counselor.isActive === true) {
        // 其他详情隐藏，重置 margin-bottom
        const buttonMargins = { ...this.data.buttonMargins };
        buttonMargins[counselor._id] = 0;
        this.setData({ buttonMargins });
        return { ...counselor, isActive: false }; // 其他咨询师重置为未激活
      }
      return counselor;
    });

    // 关闭所有详情栏
    const showDetails = {};
    if (!this.data.showDetails[id]) {
      showDetails[id] = true; // 打开当前咨询师的详情栏
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

  addToQueue(e) {
    const { id, name } = e.currentTarget.dataset; // 获取咨询师的 ID
    const time = new Date();
    //模拟传入排队数据并获取队伍序号
    const queuedata = {
      user_id: this.data.userid,
      counselor_id: id,
      join_queue_time: time
    }
    console.log("传入排队数据：", queuedata)

    const queueid = Math.floor(Math.random() * 10);
    this.setData({
      queue_id: queueid
    })
    console.log("队伍序号为：",this.data.queue_id)

    const data = this.fetchData(queueid);
    this.setData({
      modalData: data,
      showModal: true,
    });
    // 打开模态框
    const modal = this.selectComponent("#modal"); 
    modal.show();

    // 启动定时器，每 10 秒获取一次数据
    const intervalId2 = setInterval(() => {
      const newData = this.fetchData(queueid);
      this.setData({
        modalData: newData,
      });
    }, 10000); // 每 10 秒执行一次

    // 保存定时器 ID，以便后续清除
    this.setData({ intervalId2 });
  },

  // 模拟请求队伍状态数据
  fetchData(queueid) {
    const queueCount = Math.floor(Math.random() * 10);
    return {
      queueCount: queueCount, // 随机生成排队人数
      waitingTime: Math.floor(Math.random() * 5 * queueCount), // 随机生成等待时间（分钟）
    };
  },

  // 关闭模态框
  handleClose() {
    const time = new Date();
    //模拟修改排队数据
    const queuedata = {
      queue_id: this.data.queue_id,
      exit_queue_time: time
    }
    console.log("退出了排队：", queuedata)
    this.setData({ showModal: false });
    if (this.data.intervalId2) {
      clearInterval(this.data.intervalId2); // 清除定时器
      this.setData({
        intervalId2: null, // 清空定时器 ID
        showModal: false, // 隐藏模态框
      });
    }
  },

  // 清空计时器
  clearTimers() {
    const { intervalId1, intervalId2 } = this.data;

    if (intervalId1) {
      clearInterval(intervalId1); // 清除第一个计时器
      console.log('计时器 1 已清除');
    }

    if (intervalId2) {
      clearInterval(intervalId2); // 清除第二个计时器
      console.log('计时器 2 已清除');
    }

    // 清空计时器 ID
    this.setData({
      intervalId1: null,
      intervalId2: null,
    });
  },

  navigateToDialogue(e) {
    const dataset = e.currentTarget.dataset;
    const counselorId = dataset.id;
    const counselorName = dataset.name;
    const description = this.data.description;
    if (this.data.intervalId1) {
      clearInterval(this.data.intervalId1); // 清除定时器
      this.setData({
        intervalId1: null, // 清空定时器 ID
      });
    }
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
  },

  // 页面卸载时触发
  onUnload() {
    this.clearTimers(); // 清空计时器
    console.log('页面已卸载，计时器已清除');
  },

  // 页面隐藏时触发
  onHide() {
    this.clearTimers(); // 清空计时器
    console.log('页面已隐藏，计时器已清除');
  }

})
