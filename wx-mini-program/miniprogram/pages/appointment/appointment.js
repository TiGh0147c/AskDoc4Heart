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
    const today = new Date();
    console.log("传入日期", today);
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
    this.setData({schedule: scheduledata});
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
    this.getAppointments(this.data.userid);
    /* 
    const data = [{
      appointmentId:"0001",
      counselorName:"小陈",
      notes:"焦虑症咨询",
      appointment_time:"2025-04-20 morning"
    },{
      appointmentId:"0002",
      counselorName:"小林",
      notes:"焦虑症咨询",
      appointment_time:"2025-04-22 morning"
    }];
    console.log("获取预约数据:", data);
    const appointmentData = Array.isArray(data[0]) ? data[0] : data;
    this.setData({
      appointment: appointmentData || [],
      loading: false
    })*/

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
      .filter(item => item.date === selectedDate && item.time === selectedTime);

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

    //模拟传入预约数据
    const appointmentData = {
      appointmentDate: selectedDate,
      appointmentTime: selectedTime,
      userId: userid,
      userName: userName,
      counselorId: consultants[selectedConsultantIndex].id,
      counselorName: consultants[selectedConsultantIndex].counselorName,
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
          console.log("获取预约数据失败：",res);
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