// pages/counseling/counseling.js
const app = getApp(); // 获取全局应用实例

Page({

  data: {
    userid: '3',
    queue_id: '',
    queue_number: '',
    queue_counselor_id: '',
    queue_counselor_name: '',
    progressnum: '',
    counselorIds: [],
    counselors: [],
    description: '',
    showDetails: {}, // 控制每个咨询师详情是否显示的状态对象
    loading: true, // 添加一个加载状态变量
    bgColor: 'rgb(68, 88, 120)', // 初始背景颜色
    color: 'rgb(238, 239, 247)',  // 初始文字颜色
    buttonMargins: {}, // 按钮的 margin-bottom 样式
    showModal: false, // 控制模态框显示
    modalData: {}, // 模态框需要显示的数据，初始值为空对象
    intervalId: null, // 定时器 ID，用于后续清除定时器
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
    //this.addSchedules(0); this.addSchedules(1); this.addSchedules(2);
    //this.deleteSchedules(3);
    this.fetchSchedules();
    this.refresh();

    // 启动定时器，每 10 秒获取一次数据
    const intervalId = setInterval(() => {
      this.refresh();
    }, 10000); // 每 10 秒执行一次

    // 保存定时器 ID，以便后续清除
    this.setData({ intervalId2: intervalId });
  },

  // 标准化日期
  getTodayDate(today) {
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0'); // 月份从 0 开始，需加 1
    const day = String(today.getDate()).padStart(2, '0');
    console.log(`获取了今日日期：${year}-${month}-${day}`);
    return `${year}-${month}-${day}`;
  },

  // 获取当前时间段
  getCurrentTimeSlot() {
    const now = new Date();
    const hours = now.getHours();

    if (hours >= 9 && hours < 11) {
      return "morning";
    } else if (hours >= 14 && hours < 17) {
      return "afternoon";
    } else {
      return null;
    }
  },

  filterDataByTimeSlot(data, currentTimeSlot) {
    return data.filter(item => item.timeSlot === currentTimeSlot);
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
    // this.setData({ loading: true });
    const showDetails = {};
    const buttonMargins = {};
    /* 模拟获取咨询师数据 */
    const everyday = [{
      _id: 100,
      name: "苏轼",
      intro: "这是苏东坡，他没有咨询师资格证，但你可以和他聊聊。",
      tag: "被贬",
      // tag: ["东坡肉", "出去玩", "被贬"],
      queuestatus: "ai咨询师 [空闲]",
      type: "ai",
      average: 0,
      count: 0
    }];
    const everydayData = Array.isArray(everyday[0]) ? everyday[0] : everyday;
    const currentTimeSlot = this.getCurrentTimeSlot(); // 获取当前时间段
    console.log("现在的时间段为:", currentTimeSlot);
    if (currentTimeSlot) {
      this.fetchAllCounselors()
        .then(counselors => {
          console.log("所有咨询师信息加载完成：", counselors);
          const dutyData = Array.isArray(counselors[0]) ? counselors[0] : counselors;
          const processedEverydayData = everydayData.map(item => ({ ...item, isActive: false }));
          const promises = dutyData.map(async (item) => {
            let queueStatus = null;
            try {
              queueStatus = await this.fetchEveryQueue(item._id);
            } catch (err) {
              console.error(`获取队列状态失败 for id=${item._id}`, err);
            }
            return {
              ...item,
              type: "counselor",
              isActive: false,
              queuestatus: queueStatus
            };
          });
          Promise.all(promises).then(processedDutyData => {
            const processedCounselors = [...processedDutyData, ...processedEverydayData];
            this.setData({
              counselors: processedCounselors,
              showDetails,
              buttonMargins,
              loading: false
            });
            wx.stopPullDownRefresh();
          });
        })
        .catch(error => {
          console.error("加载咨询师信息失败：", error);
          wx.showToast({
            title: '加载咨询师信息失败',
            icon: 'none'
          });
          this.setData({
            showDetails,
            buttonMargins,
            loading: false
          });
          wx.stopPullDownRefresh();
        });
    } else {
      const processedEverydayData = everydayData.map(item => ({ ...item, isActive: false }));
      this.setData({
        counselors: processedEverydayData,
        showDetails,
        buttonMargins,
        loading: false
      });
      wx.stopPullDownRefresh();
    }
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
    if (this.data.intervalId) {
      clearInterval(this.data.intervalId); // 清除定时器
      this.setData({
        intervalId: null, // 清空定时器 ID
        showModal: false, // 隐藏模态框
      });
      console.log("计时器已清除");
    }
  },

  // 清空计时器
  clearTimers() {
    const { intervalId, intervalId2 } = this.data;

    if (intervalId) {
      clearInterval(intervalId); // 清除计时器
      console.log('计时器 1 已清除');
    }
    if (intervalId2) {
      clearInterval(intervalId2); // 清除定时器
      console.log("计时器 2 已清除");
    }

    // 清空计时器 ID
    this.setData({
      intervalId: null,
      intervalId2: null
    });
  },

  // 进入咨询会话
  navigateToDialogue(e) {
    const dataset = e.currentTarget.dataset;
    const counselorId = dataset.id;
    const counselorName = dataset.name;
    const userid = this.data.userid;
    const description = this.data.description;
    if(dataset.type == "ai"){
      console.log('选择了ai咨询师');
      const enterdata = {
        userId: userid,
        counselorId: counselorId,
        counselorName: counselorName,
        description: description,
        type: 'ai'
      };
      console.log('尝试进入咨询会话:', enterdata);
      this.enterDialogue(enterdata);
      /*wx.reLaunch({
        url: `/pages/ai-dialogue/ai-dialogue?counselorId=${counselorId}&counselorName=${counselorName}&description=${description}`
      });*/
    } else {
      this.addToQueue(e);
    }
  },

  // 进入咨询会话
  enterDialogue: function(inputdata) {
    const url = `http://localhost:8081/api/user/chats/counselor/${inputdata.counselorId}`;
    const that = this;
    wx.request({
      url: url,
      method: 'GET',
      data: {
        userId: inputdata.userId,
        counselorId: inputdata.counselorId
      },
      success(res) {
        if (res.statusCode === 200) {
          console.log("进入咨询会话成功：", res.data);
          that.clearTimers(); 
          if(inputdata.type === 'human'){
            wx.reLaunch({
              url: `/pages/dialogue/dialogue?sessionId=${res.data.sessionId}&counselorId=${inputdata.counselorId}&counselorName=${inputdata.counselorName}&description=${inputdata.description}&queue_id=${inputdata.queue_id}`
            });
          } else {
            wx.reLaunch({
              url: `/pages/ai-dialogue/ai-dialogue?sessionId=${res.data.sessionId}&counselorId=${inputdata.counselorId}&counselorName=${inputdata.counselorName}&description=${inputdata.description}`
            });
          }
        } else {
          console.log("进入咨询会话失败：",res);
          wx.showToast({
            title: '进入咨询会话失败，请稍后再试',
            icon: 'none'
        });
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
          const intervalId = setInterval(() => {
            that.handleFetchAndSetData(queueId);
          }, 10000); // 每 10 秒执行一次

          // 保存定时器 ID，以便后续清除
          that.setData({ intervalId: intervalId });
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

  // 更新队伍状态
  updateQueue(queueId) {
    const url = `http://localhost:8081/api/queues/${queueId}/status?status=in_progress`;
    const that = this;
    wx.request({
      url: url,
      method: 'POST',
      header: { 'Content-Type': 'application/json' },
      success(res) {
        if (res.statusCode === 200) {
          console.log("更新队伍状态成功：", res.data);
          const enterdata = {
            userId: that.data.userid,
            counselorId: that.data.queue_counselor_id,
            counselorName: that.data.queue_counselor_name,
            description: that.data.description,
            queue_id: that.data.queue_id,
            type: 'human'
          };
          console.log('尝试进入咨询会话:', enterdata);
          that.enterDialogue(enterdata);
        } else {
          console.log("更新队伍状态失败：",res);
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

  fetchQueuedata(counselorId) {
    return new Promise((resolve, reject) => {
      const url = `http://localhost:8081/api/queues/${counselorId}/stats`;
      wx.request({
        url: url,
        method: 'GET',
        success:(res) => {
          if (res.statusCode === 200) {
            console.log("获取队伍信息成功：", res.data);
            let data = res.data;
            let progressRegex = /咨询中:\s*(\d+)\/\d+/;
            let progressmatch = data.match(progressRegex);
            let progressnum = progressmatch[1];
            this.setData({ progressnum: progressnum });
            this.fetchData(this.data.queue_id)
              .then(data => {
                const waitingnum = data.aheadCount;
                const queuedata = {
                  waitingnum: waitingnum,
                  waitingtime: (waitingnum + 1) * 5
                }
                if (progressnum < 3 && waitingnum === 0) {
                  this.updateQueue(this.data.queue_id);
                }
                // 将数据传递给调用者
                resolve(queuedata);
              })
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

  fetchEveryQueue(counselorId) {
    return new Promise((resolve, reject) => {
      const url = `http://localhost:8081/api/queues/${counselorId}/stats`;
      wx.request({
        url: url,
        method: 'GET',
        success:(res) => {
          if (res.statusCode === 200) {
            console.log("获取队伍信息成功：", res.data);
            resolve(res.data);
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
    that.fetchQueuedata(that.data.queue_counselor_id)
      .then(data => {
        const modalData = {
          queueCount: data.waitingnum,
          waitingTime: data.waitingtime
          //data.estimatedMinutes
        };
        that.setData({
          modalData: modalData,
          showModal: true,
        });
        const modal = that.selectComponent("#modal"); 
        modal.show();
        // that.checkQueue(queueId);
      })
      .catch(err => {
        console.error("获取数据失败：", err);
        wx.showToast({
          title: '获取数据失败，请稍后再试',
          icon: 'none'
        });
      });
  },

  // 获取咨询师信息+均分
  getCounselorMore(id) {
    return new Promise(async (resolve, reject) => {
      try {
        // 获取咨询师基本信息
        const profileResponse = await new Promise((resolve, reject) => {
          wx.request({
            url: 'http://localhost:8081/api/profile-management/counselor/profile',
            method: 'GET',
            data: { counselorId: id },
            success(res) {
              if (res.statusCode === 200) {
                resolve(res);
              } else {
                reject(new Error(`查询咨询师 ${id} 基本信息失败`));
              }
            },
            fail(err) {
              reject(err);
            }
          });
        });
  
        // 获取平均评分和评价数量
        const averageResponse = await new Promise((resolve, reject) => {
          wx.request({
            url: 'http://localhost:8081/evaluation/average',
            method: 'GET',
            data: { counselorId: id },
            success(res) {
              if (res.statusCode === 200) {
                resolve(res);
              } else {
                reject(new Error(`查询咨询师 ${id} 平均评分失败`));
              }
            },
            fail(err) {
              reject(err);
            }
          });
        });

        // 检查 averageResponse.data.data 是否为 null
        const averageData = averageResponse.data.data || { count: 0, average: 0 };
  
        // 合并数据
        const profileData = profileResponse.data;
  
        const status = profileData.status === "available" ? "空闲" : "繁忙";
        const counselor = {
          _id: id,
          name: profileData.name,
          avatar: profileData.avatar,
          counselor_certificate: profileData.counselorCertificate,
          tag: profileData.expertiseArea,
          intro: profileData.intro || "无",
          status: status,
          average: averageData.average,
          count: averageData.count
        };
  
        console.log(`查询咨询师 ${id} 信息成功：`, counselor);
        resolve(counselor);
      } catch (error) {
        console.error(`查询咨询师 ${id} 信息失败：`, error);
        reject(error);
      }
    });
  },

  // 根据存储的值班咨询师id，获取值班咨询师状态
  fetchAllCounselors() {
    const { counselorIds } = this.data;

    // 使用 Promise.all 并发请求所有咨询师信息
    return Promise.all(counselorIds.map(id => this.getCounselorMore(id)))
      .then(results => {
        // 将所有成功的结果存入页面数据
        console.log("所有咨询师信息：", results);
        return results;
      })
      .catch(err => {
        console.error("获取咨询师信息失败：", err);
        wx.showToast({
          title: '获取咨询师信息失败',
          icon: 'none'
        });
      });
  },

  checkQueue: function(data) {
    const url = `http://localhost:8081/api/queues/getById`;
    const that = this;
    wx.request({
      url: url,
      method: 'GET',
      data: {
        queueId: data
      },
      success(res) {
        if (res.statusCode === 200) {
          console.log("查询排队状态成功：", res.data);
          if(res.data.queueStatus === "in_progress"){
            that.queueToDialogue();
          }
        } else {
          console.log("查询排队状态失败：",res);
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

  // 获取当前时间段排班
  fetchSchedules() {
    const today = new Date();
    const todayDate = this.getTodayDate(today); // 获取当前日期
    const currentTimeSlot = this.getCurrentTimeSlot(); // 获取当前时间段
    wx.request({
      url: `http://localhost:8081/api/schedules/date/${todayDate}`, 
      method: 'GET',
      success: (res) => {
        if (res.statusCode === 200) {
          console.log("查询今日排班成功：", res.data);
          if (currentTimeSlot) {
            // 筛选数据
            const filteredData = this.filterDataByTimeSlot(res.data, currentTimeSlot);
      
            // 提取 counselorId 并更新页面数据
            const counselorIds = filteredData.map(item => item.counselorId);
            console.log("查询当前时间段排班成功：", counselorIds);
            this.setData({ counselorIds });
            this.refresh();
          } else {
            wx.showToast({
              title: '当前时间不在有效时间段内',
              icon: 'none'
            });
            this.setData({ loading: false });
          }
        } else {
          console.log("查询今日排班失败：",res);
        }
      },
      fail(err) {
        console.error('请求失败:', err);
        wx.showToast({
            title: '网络错误，请稍后再试',
            icon: 'none'
        });
      }
    })
  },

  // 获取全部排班
  getAllschedules() {
    const url = 'http://localhost:8081/api/schedules/all';
    wx.request({
      url: url,
      method: 'GET',
      success(res) {
        if (res.statusCode === 200) {
          console.log("查询排班成功：", res.data);
        } else {
          console.log("查询排班失败：",res);
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

  // 新建排班
  addSchedules(n) {
    const today = new Date();
    const day = new Date(today);
    day.setDate(today.getDate() + n);
    console.log("获取日期：", day);
    const Thedate = this.getTodayDate(day);
    const url = 'http://localhost:8081/api/schedules/create';
    const data =  {
      date: Thedate,
      timeSlot: "afternoon",
      //timeSlot: "morning",
      counselorId: 1,
      status: 'working'
    }

    wx.request({
      url: url, 
      method: 'POST',
      data: data,
      header: {
        'Content-Type': 'application/json'
      },
      success: (res) => {
        if (res.statusCode === 200) {
          console.log("新增排班成功：",res.data);
        } else {
          console.log("新增排班失败：",res);
        }
      },
      fail(err) {
        console.error('请求失败:', err);
        wx.showToast({
            title: '网络错误，请稍后再试',
            icon: 'none'
        });
      }
    })
  },

  // 删除排班
  deleteSchedules(n) {
    const url = `http://localhost:8081/api/schedules/clear/${n}`;
    wx.request({
      url: url, 
      method: 'DELETE',
      header: {
        'Content-Type': 'application/json'
      },
      success: (res) => {
        if (res.statusCode === 200) {
          console.log("删除排班成功：",res.data);
        } else {
          console.log("删除排班失败：",res);
        }
      },
      fail(err) {
        console.error('请求失败:', err);
        wx.showToast({
            title: '网络错误，请稍后再试',
            icon: 'none'
        });
      }
    })
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
