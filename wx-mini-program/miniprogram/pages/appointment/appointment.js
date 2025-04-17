// pages/appointment/appointment.js
const app = getApp(); // 获取全局应用实例

Page({

  data: {
    userid: '',
    userName: '',
    appointment: [],
    maxAppointments: 3, // 最大可预约数
    loading: true, // 添加一个加载状态变量
    showModal: false,
    startDate: '',
    endDate: '',
    selectedDate: '',
    timeOptions: ['morning', 'afternoon'],
    selectedTime: '',
    consultants: [], // 当前时间段的咨询师列表
    consultantNames: [], // 当前时间段的咨询师名字列表
    selectedConsultantIndex: 0, // 默认选中的咨询师索引
    selectedConsultantName: '', // 当前选中的咨询师名字
    problemTypes: ['情感障碍', '其他问题'],
    selectedProblemTypeIndex: 0,
    schedule: [],
    canSubmit: false, // 标识是否可以提交预约
  },

  onLoad: function () {
    const userid = app.getGlobalData('userid');
    const userName = app.getGlobalData('userName');
    this.setData({
      userid: userid,
      userName: userName
    });
    /* 模拟获取值班表数据 */
    this.fetchSchedules();
    /* 
    const scheduledata = [
      {date: "2025-04-07", time: "morning", counselorName: "李明", id: 3},
      {date: "2025-04-07", time: "morning", counselorName: "王芳", id: 4},
      {date: "2025-04-07", time: "afternoon", counselorName: "小陈", id: 1},
      {date: "2025-04-07", time: "afternoon", counselorName: "小林", id: 2},
      {date: "2025-04-08", time: "morning", counselorName: "李明", id: 3},
      {date: "2025-04-08", time: "morning", counselorName: "王芳", id: 4},
      {date: "2025-04-08", time: "afternoon", counselorName: "小陈", id: 1},
      {date: "2025-04-08", time: "afternoon", counselorName: "小林", id: 2},
      {date: "2025-04-10", time: "morning", counselorName: "小陈", id: 1},
      {date: "2025-04-10", time: "morning", counselorName: "小林", id: 2},
      {date: "2025-04-10", time: "afternoon", counselorName: "李明", id: 3},
      {date: "2025-04-10", time: "afternoon", counselorName: "王芳", id: 4},
      {date: "2025-04-11", time: "morning", counselorName: "小陈", id: 1},
      {date: "2025-04-11", time: "morning", counselorName: "小林", id: 2},
      {date: "2025-04-11", time: "afternoon", counselorName: "李明", id: 3},
      {date: "2025-04-11", time: "afternoon", counselorName: "王芳", id: 4}
    ];
    console.log("获取值班数据：", scheduledata);
    this.setData({schedule: scheduledata});*/
    this.calculateDates();
    this.refresh();
  },

  refresh() {
    this.setData({
      loading: true
    })
    /* 模拟获取预约数据 */
    const inputdata = {
      userId: this.data.userid
    }
    this.getAppointmentsWithCounselorNames(this.data.userid);
  },

  // 标准化日期
  getTodayDate(today) {
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0'); // 月份从 0 开始，需加 1
    const day = String(today.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  },

  // 计算可选日期范围
  calculateDates() {
    const today = new Date();
    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);

    const startDate = this.formatDate(tomorrow);
    const endDate = this.formatDate(new Date(tomorrow.setDate(tomorrow.getDate() + 6)));

    this.setData({
      startDate,
      endDate,
      selectedDate: startDate,
    });
  },

  formatDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  },

  // 显示新预约模态框
  showNewAppointmentModal() {
    if (this.data.appointment.length >= this.data.maxAppointments) {
      wx.showToast({
        title: '已经达到最大可预约数',
        icon: 'none',
      });
      return;
    }
    this.setData({ showModal: true });
  },

  // 隐藏模态框
  hideModal() {
    this.setData({ showModal: false });
  },

  // 处理日期选择
  bindDateChange(e) {
    const selectedDate = e.detail.value;
    this.setData({ selectedDate }, () => {
      this.updateConsultants(); // 更新咨询师列表
    });
  },

  // 处理时段选择
  bindTimeChange(e) {
    const selectedTime = e.detail.value;
    this.setData({ selectedTime }, () => {
      this.updateConsultants(); // 更新咨询师列表
    });
  },

  // 更新咨询师列表
  updateConsultants() {
    const { selectedDate, selectedTime, schedule } = this.data;

    if (!selectedDate || !selectedTime) {
      this.setData({
        consultants: [],
        consultantNames: ['无'],
        selectedConsultantIndex: 0,
        selectedConsultantName: '无',
        canSubmit: false, // 禁止提交预约
      });
      return;
    }

    // 筛选出符合条件的咨询师
    const filteredConsultants = schedule
      .filter(item => item.date === selectedDate)
      .flatMap(item => item.schedules)
      .filter(scheduleItem => scheduleItem.timeSlot === selectedTime);
    console.log("筛选结果：", filteredConsultants);
    /*
    const filteredConsultants = schedule
      .filter(item => item.date === selectedDate && item.timeSlot === selectedTime);
    */

    if (filteredConsultants.length > 0) {
      const consultantNames = filteredConsultants.map(item => item.counselorName); // 提取咨询师名字

      this.setData({
        consultants: filteredConsultants,
        consultantNames: consultantNames,
        selectedConsultantIndex: 0,
        selectedConsultantName: consultantNames[0], // 默认选中第一个咨询师的名字
        canSubmit: true, // 可以提交预约
      });
    } else {
      this.setData({
        consultants: [],
        consultantNames: ['无'],
        selectedConsultantIndex: 0,
        selectedConsultantName: '无',
        canSubmit: false, // 禁止提交预约
      });
    }
  },

  // 处理咨询师选择
  bindConsultantChange(e) {
    const selectedIndex = e.detail.value; // 获取用户选择的索引
    const selectedConsultantName = this.data.consultantNames[selectedIndex]; // 获取对应的咨询师名字

    this.setData({
      selectedConsultantIndex: selectedIndex,
      selectedConsultantName: selectedConsultantName
    });
  },

  // 处理问题类型选择
  bindProblemTypeChange(e) {
    this.setData({ selectedProblemTypeIndex: e.detail.value });
  },

  // 提交预约
  submitAppointment() {
    if (!this.data.canSubmit) {
      wx.showToast({
        title: '当前时间段无可选咨询师',
        icon: 'none',
      });
      return;
    }

    const { selectedDate, selectedTime, consultants, selectedConsultantIndex, problemTypes, selectedProblemTypeIndex, userid, userName } = this.data;

    // 传入预约数据
    const appointmentData = {
      appointmentDate: selectedDate,
      appointmentTime: selectedTime,
      userId: userid,
      //userName: userName,
      counselorId: consultants[selectedConsultantIndex].counselorId,
      //counselorName: consultants[selectedConsultantIndex].counselorName,
      appointmentStatus: 'scheduled'
      //problemType: problemTypes[selectedProblemTypeIndex],
    };
    this.createAppointment(appointmentData);

    this.hideModal();
  },

  // 显示取消预约的模态框
  showCancelModal(e) {
    const appointmentId = e.currentTarget.dataset.id; // 获取预约记录的 ID
    wx.showModal({
      title: '提示',
      content: '确定要取消该预约吗？',
      success: (res) => {
        if (res.confirm) {
          // 用户点击了确认按钮
          this.cancelAppointment(appointmentId);
        } else if (res.cancel) {
          // 用户点击了取消按钮
          console.log('用户取消了操作');
        }
      },
    });
  },

  // 获取未来七天的值班数据
  fetchSchedules() {
      const today = new Date(); // 当前日期
      const futureSchedules = []; // 存储未来七天的值班表数据
    
      for (let i = 1; i <= 7; i++) { // 从明天开始，未来七天
        const day = new Date(today);
        day.setDate(today.getDate() + i);
        const dateStr = this.getTodayDate(day);
    
        wx.request({
          url: `http://localhost:8081/api/schedules/date/${dateStr}`,
          method: 'GET',
          success: (res) => {
            if (res.statusCode === 200) {
              //console.log(`${dateStr} 查询排班成功：`, res.data);
              futureSchedules.push({ date: dateStr, schedules: res.data }); // 存储当天的值班表数据
            } else {
              console.log(`${dateStr} 查询排班失败：`, res);
            }
          },
          fail: (err) => {
            console.error(`${dateStr} 请求失败:`, err);
            wx.showToast({
              title: `${dateStr} 网络错误，请稍后再试`,
              icon: 'none'
            });
          },
          complete: () => {
            if (futureSchedules.length === 7) {
              console.log("未来七天的值班表数据：", futureSchedules);
              this.setData({ schedule: futureSchedules });
            }
          }
        });
      }
  },

  // 获取预约数据
  getAppointments: function(data) {
    const userId = data;
    const url = `http://localhost:8081/api/appointments/user/${userId}`;

    const that = this;
    wx.request({
      url: url,
      method: 'GET',
      data: {
        userId: userId
      },
      success(res) {
        if (res.statusCode === 200) {
          console.log("获取预约数据成功：", res.data);
          // 筛选出 appointmentStatus 为 "scheduled" 的数据
          const appointments = res.data.filter(item => item.appointmentStatus === "scheduled");
          const appointmentData = Array.isArray(appointments[0]) ? appointments[0] : appointments;
          that.setData({
            appointment: appointmentData || []
          });
        } else {
          if(res.statusCode === 404) {
            console.log("暂无预约");
          } else {
            console.error("获取预约数据失败：",res);
          }
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
    that.setData({
      loading: false
    });
  },

  getAppointmentsWithCounselorNames: function(data) {
    const userId = data;
    const url = `http://localhost:8081/api/appointments/user/${userId}`;
  
    const that = this;
  
    wx.request({
      url: url,
      method: 'GET',
      data: {
        userId: userId
      },
      success(res) {
        if (res.statusCode === 200) {
          console.log("获取预约数据成功：", res.data);
  
          // 筛选出 appointmentStatus 为 "scheduled" 的数据
          const appointments = res.data.filter(item => item.appointmentStatus === "scheduled");
  
          // 如果没有符合条件的预约数据，直接返回
          if (!appointments || appointments.length === 0) {
            console.log("暂无有效预约");
            that.setData({ appointment: [] });
            return;
          }

          // 提取 counselorId 列表，并去重
          const counselorIds = appointments.reduce((uniqueIds, item) => {
            if (uniqueIds.indexOf(item.counselorId) === -1) {
              uniqueIds.push(item.counselorId);
            }
            return uniqueIds;
          }, []);

          // 根据 counselorId 获取所有咨询师信息
          const counselorPromises = counselorIds.map(id => that.getCounselor(id));

          Promise.all(counselorPromises)
          .then(counselors => {
            // 构建 counselorId -> counselorName 的映射
            const counselorMap = {};
            counselors.forEach(counselor => {
              counselorMap[counselor.id] = counselor.name;
            });

            // 填充 counselorName 到预约数据中
            const updatedAppointments = appointments.map(appointment => {
              const newAppointment = {};
              for (let key in appointment) {
                newAppointment[key] = appointment[key];
              }
              newAppointment.counselorName = counselorMap[appointment.counselorId] || "未知";
              return newAppointment;
            });

            // 更新页面数据
            that.setData({
              appointment: updatedAppointments || []
            });

            console.log("更新后的预约数据：", updatedAppointments);
          })
          .catch(err => {
            console.error("获取咨询师信息失败：", err);
            wx.showToast({
              title: '获取咨询师信息失败，请稍后再试',
              icon: 'none'
            });
          });
      } else if (res.statusCode === 404) {
        console.log("暂无预约");
        that.setData({ appointment: [] });
      } else {
        console.error("获取预约数据失败：", res);
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
  
    that.setData({
      loading: false
    });
  },
  
  // 获取咨询师信息的方法
  getCounselor(id) {
    return new Promise((resolve, reject) => {
      const url = 'http://localhost:8081/api/profile-management/counselor/profile';
      wx.request({
        url: url,
        method: 'GET',
        data: {
          counselorId: id
        },
        success(res) {
          if (res.statusCode === 200) {
            const counselor = {
              id: res.data.counselorId,
              name: res.data.name,
            }
            console.log(`查询咨询师 ${id} 信息成功：`, counselor);
            resolve(counselor); // 返回提取的数据
          } else {
            console.log(`查询咨询师 ${id} 信息失败：`, res);
            reject(new Error(`查询咨询师 ${id} 失败`));
          }
        },
        fail(err) {
          console.error(`请求咨询师 ${id} 信息失败：`, err);
          reject(err);
        }
      });
    });
  },

  // 创建预约
  createAppointment(data) {
    const url = 'http://localhost:8081/api/appointments/create';
    const appointmentData = data;
    console.log("发送了预约信息：", data);
    const that = this;

    wx.request({
      url: url,
      method: 'POST',
      header: {
        'Content-Type': 'application/json'
      },
      data: appointmentData,
      success(res) {
        if (res.statusCode === 200) {
          console.log("预约创建成功：", res.data);
          wx.showToast({
            title: '预约创建成功',
            icon: 'success'
          });
          that.refresh();
        } else {
          console.log("预约创建失败：", res);
          wx.showToast({
          title: res.data || '预约创建失败',
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

  // 取消预约
  cancelAppointment(appointmentId) {
    const url = `http://localhost:8081/api/appointments/cancel/${appointmentId}`;
    const that = this;

    wx.request({
        url: url,
        method: 'POST',
        data: {
          appointmentId: appointmentId
        },
        success(res) {
          if (res.statusCode === 200) {
            console.log("取消预约成功：",res.data);
            wx.showToast({
              title: '预约已取消',
              icon: 'success'
            });
            // 刷新预约列表
            that.refresh();
          } else {
            console.log("取消预约失败：",res);
            wx.showToast({
              title: res.data || '取消预约失败',
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
  }
})