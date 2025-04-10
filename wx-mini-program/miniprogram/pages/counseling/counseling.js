// pages/counseling/counseling.js
const app = getApp(); // 获取全局应用实例

Page({

  data: {
    userid: '',
    queue_id: '',
    queue_number: '',
    queue_counselor_id: '',
    queue_counselor_name: '',
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
    /*
    // 启动定时器，每 10 秒获取一次数据
    const intervalId1 = setInterval(() => {
      this.refresh();
    }, 10000); // 每 10 秒执行一次
    // 保存定时器 ID，以便后续清除
    this.setData({ intervalId1: intervalId1 });*/
    //this.getAllQueue(userid);
    //this.leaveQueue(1);this.leaveQueue(10);this.leaveQueue(5);
    //this.leaveQueue(6);this.leaveQueue(7);this.leaveQueue(8);
  },

  clearAllTimersGlobally() {
    let id = 1;
    while (id < 100) { // 假设最大计时器数量为 10000
      clearTimeout(id);
      clearInterval(id);
      id++;
    }
  },

  onLaunch: function () {
    this.refresh();
  },

   // 监听下拉刷新事件
   onPullDownRefresh() {
    console.log("触发下拉刷新...");
    this.refresh(); // 调用 refresh 函数
  },

  refresh(){
    this.setData({ loading: true });
    /* 模拟获取咨询师数据 */
    const everyday = [{
      _id: 100,
      name: "苏轼",
      intro: "这是苏东坡，他没有咨询师资格证，但你可以和他聊聊。",
      tag: ["东坡肉", "出去玩", "被贬"],
      status: "空闲",
      type: "ai"
    }];
    const everydayData = Array.isArray(everyday[0]) ? everyday[0] : everyday;
    const data = [{
      _id: 1,
      name: "张三",
      intro: "这是咨询师，真的是咨询师。",
      tag: ["犯法", "文盲", "被捕"],
      status: "繁忙"
    },{
      _id: 2,
      name: "李四",
      intro: "显而易见这里是简介。",
      tag: ["想不出来", "就先", "这样吧"],
      status: "空闲"
    }];
    const dutyData = Array.isArray(data[0]) ? data[0] : data;
    const processedEverydayData = everydayData.map(item => ({ ...item, isActive: false }));
    const processedDutyData = dutyData.map(item => ({ ...item, type: "counselor", isActive: false }));
    const counselors = [...processedDutyData, ...processedEverydayData];
    const showDetails = {};
    const buttonMargins = {};
    this.setData({
      counselors: counselors,
      showDetails,
      buttonMargins,
      loading: false
    });
    wx.stopPullDownRefresh();
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
    //模拟传入排队数据并获取队伍序号
    const queuedata = {
      userId: this.data.userid,
      counselorId: id,
    }
    console.log("传入排队数据：", queuedata);
    this.setData({
      queue_counselor_id: id,
      queue_counselor_name: name
    });
    this.addQueue(queuedata);
  },

  // 关闭模态框
  handleClose() {
    const time = new Date();
    //模拟修改排队数据
    const queuedata = this.data.queue_id;
    this.leaveQueue(queuedata);
    this.setData({ showModal: false });
    if (this.data.intervalId2) {
      clearInterval(this.data.intervalId2); // 清除定时器
      this.setData({
        intervalId2: null, // 清空定时器 ID
        showModal: false, // 隐藏模态框
      });
      console.log("计时器2已清除");
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

  queueToDialogue() {
    const counselorId = this.data.queue_counselor_id;
    const counselorName = this.data.queue_counselor_name;
    const description = this.data.description;
    wx.reLaunch({
      url: `/pages/dialogue/dialogue?counselorId=${counselorId}&counselorName=${counselorName}&description=${description}`
    });
  },

  addQueue: function(data) {
    const url = 'http://localhost:8081/api/queues/join';
    const that = this;
    wx.request({
      url: url,
      method: 'POST',
      data: data,
      success(res) {
        if (res.statusCode === 200) {
          console.log("加入队伍成功：", res.data);
          that.setData({
            queue_id: res.data.queueId,
            queue_number: res.data.queueNumber,
          })
          const queueId = res.data.queueId;
          console.log("加入了队列，编号为：", queueId);
          that.handleFetchAndSetData(queueId);
          
          // 启动定时器，每 10 秒获取一次数据
          const intervalId2 = setInterval(() => {
            that.handleFetchAndSetData(queueId);
          }, 10000); // 每 10 秒执行一次

          // 保存定时器 ID，以便后续清除
          that.setData({ intervalId2: intervalId2 });
        } else {
          console.log("加入队伍失败：",res);
        }
      },
      fail(err) {
        console.error('请求失败:', err);
        wx.showToast({
            title: '网络错误，请稍后再试',
            icon: 'none'
        });
     }
    });
  },

  leaveQueue: function(data) {
    const url = `http://localhost:8081/api/queues/${data}/cancel`;
    wx.request({
      url: url,
      method: 'POST',
      data: {
        queueId: data
      },
      success(res) {
        if (res.statusCode === 200) {
          console.log("退出队伍成功：", res.data);
        } else {
          console.log("退出队伍失败：",res);
        }
      },
      fail(err) {
        console.error('请求失败:', err);
        wx.showToast({
            title: '网络错误，请稍后再试',
            icon: 'none'
        });
      }
    });
  },

  // 请求队伍状态数据
  fetchData(queueId) {
    return new Promise((resolve, reject) => {
      const url = `http://localhost:8081/api/queues/position/${queueId}`;
      wx.request({
        url: url,
        method: 'GET',
        success(res) {
          if (res.statusCode === 200) {
            console.log("获取队伍信息成功：", res.data);
            resolve(res.data); // 将数据传递给调用者
          } else {
            console.log("获取队伍信息失败：", res);
            reject(new Error("获取队伍信息失败"));
          }
        },
        fail(err) {
          console.error('请求失败:', err);
          wx.showToast({
            title: '网络错误，请稍后再试',
            icon: 'none'
          });
          reject(err);
        }
      });
    });
  },

  // 获取队伍状态数据
  handleFetchAndSetData(queueId) {
    const that = this;
    that.fetchData(queueId)
      .then(data => {
        const modalData = {
          queueCount: data.aheadCount,
          waitingTime: data.estimatedMinutes
        };
        that.setData({
          modalData: modalData,
          showModal: true,
        });
        const modal = that.selectComponent("#modal"); 
        modal.show();
        /*
        const updateData = {
          queueId: queueId,
          status: 'in_progress'
        }
        console.log("传入更新数据", updateData);
        that.updateQueue(updateData);*/
      })
      .catch(err => {
        console.error("获取数据失败：", err);
        wx.showToast({
          title: '获取数据失败，请稍后再试',
          icon: 'none'
        });
      });
  },

  // 模拟更新排队状态
  updateQueue(data) {
    const url = `http://localhost:8081/api/queues/${data.queueId}/status`;
    wx.request({
      url: url,
      method: 'POST',
      data: {
        status: data.status,
      },
      success(res) {
        if (res.statusCode === 200) {
          console.log("更新排队状态成功：", res.data);
        } else {
          console.log("更新排队状态失败：",res);
        }
      },
      fail(err) {
        console.error('请求失败:', err);
        wx.showToast({
            title: '网络错误，请稍后再试',
            icon: 'none'
        });
      }
    });
  },

  getAllQueue(id) {
    const url = `http://localhost:8081/api/queues/user/${id}`;
    wx.request({
      url: url,
      method: 'GET',
      data: {
        userId: id
      },
      success(res) {
        if (res.statusCode === 200) {
          console.log("获取排队数据成功：", res.data);
        } else {
          console.log("获取排队数据失败：",res);
        }
      },
      fail(err) {
        console.error('请求失败:', err);
        wx.showToast({
            title: '网络错误，请稍后再试',
            icon: 'none'
        });
      }
    });
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
