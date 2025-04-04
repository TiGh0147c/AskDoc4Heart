// pages/appointment/appointment.js
Page({

  data: {
    appointment: [],
    maxAppointments: 3, // 最大可预约数
    loading: true, // 添加一个加载状态变量
    showModal: false,
    startDate: '',
    endDate: '',
    selectedDate: '',
    timeOptions: ['morning', 'afternoon'],
    selectedTime: '',
    consultants: [],
    selectedConsultantIndex: 0,
    problemTypes: ['情感障碍', '其他问题'],
    selectedProblemTypeIndex: 0,
    schedule: { // 排班表数据
      "2025-04-04": {"morning": ["小陈", "小林"], "afternoon": ["李明", "王芳"]},
      "2025-04-07": {"morning": ["李明", "王芳"], "afternoon": ["小陈", "小林"]},
      "2025-04-08": {"morning": ["李明", "王芳"], "afternoon": ["小陈", "小林"]},
      "2025-04-09": {"morning": ["小陈", "小林"], "afternoon": ["李明", "王芳"]},
      "2025-04-10": {"morning": ["小陈", "小林"], "afternoon": ["李明", "王芳"]}
    },
    canSubmit: false, // 标识是否可以提交预约
  },

  onLoad: function () {
    this.calculateDates();
    this.refresh();
  },

  refresh() {
    this.setData({
      loading: true
    })
    /* 获取数据(模拟) */
    const data1 = [{
      Appointment_Id:"0001",
      Name:"小陈",
      Notes:"焦虑症咨询",
      Appointment_Time:"2025-04-20 上午"
    },{
      Appointment_Id:"0002",
      Name:"小林",
      Notes:"焦虑症咨询",
      Appointment_Time:"2025-04-22 上午"
    }];
    const data2 = [];
    const appointmentData = Array.isArray(data1[0]) ? data1[0] : data1;
    this.setData({
      appointment: appointmentData || [],
      loading: false
    })
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

    if (selectedDate && selectedTime && schedule[selectedDate] && schedule[selectedDate][selectedTime]) {
      this.setData({
        consultants: schedule[selectedDate][selectedTime],
        selectedConsultantIndex: 0,
        canSubmit: true, // 可以提交预约
      });
    } else {
      this.setData({
        consultants: ['无'],
        selectedConsultantIndex: 0,
        canSubmit: false, // 禁止提交预约
      });
    }
  },

  // 处理咨询师选择
  bindConsultantChange(e) {
    this.setData({ selectedConsultantIndex: e.detail.value });
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

    const { selectedDate, selectedTime, consultants, selectedConsultantIndex, problemTypes, selectedProblemTypeIndex } = this.data;
    const appointmentData = {
      date: selectedDate,
      time: selectedTime,
      consultant: consultants[selectedConsultantIndex],
      problemType: problemTypes[selectedProblemTypeIndex],
    };

    console.log('提交的预约数据:', appointmentData);

    wx.showToast({
      title: '预约成功',
      icon: 'success',
    });

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

  // 取消预约
  cancelAppointment(appointmentId) {
    // 调用 API 或更新本地数据
    console.log("取消了预约",appointmentId);
    this.refresh();
  },

})