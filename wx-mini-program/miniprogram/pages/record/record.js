// pages/record/record.js
const app = getApp(); // 获取全局应用实例

Page({

  data: {
    userid: '',
    records: [],
    loading: true, // 添加一个加载状态变量
  },

  onLoad: function () {

    const userid = app.getGlobalData('userid');
    this.setData({ userid: userid });
    this.getAllsession();

    /*
    //测试用
    const currentDate = new Date();
    const recordData = [{
      _id: 1,
      date: currentDate
    }, {
      _id: 2,
      date: currentDate
    }]; 
    //测试用

    // 格式化records中的date字段
    const formattedRecords = recordData.map(record => ({
      ...record,
      date: record.date instanceof Date ? this.formatDate(record.date) : record.date // 如果不是Date对象，则保持原样
    }));

    this.setData({
      records: formattedRecords,
      loading: false
    });*/
    
  },

  // 定义日期格式化函数
  formatDateTime: function(dateTimeStr) {
    if (!dateTimeStr) return '';
    
    // 创建 Date 对象
    const date = new Date(dateTimeStr);

    // 获取年、月、日、小时、分钟
    const year = date.getFullYear();
    const month = ('0' + (date.getMonth() + 1)).slice(-2); // 月份从0开始，需要加1，并且保证两位数
    const day = ('0' + date.getDate()).slice(-2);
    const hour = ('0' + date.getHours()).slice(-2);
    const minute = ('0' + date.getMinutes()).slice(-2);

    // 返回格式化后的字符串
    //return `${year}-${month}-${day}`;
    return `${year}-${month}-${day} ${hour}:${minute}`;
  },
  
  // 请求获取历史消息
  getAllsession() {
    const url = `http://localhost:8081/api/user/completed`;
    wx.request({
      url: url,
      method: 'GET',
      data: { userId: this.data.userid },
      header: {'content-type': 'application/json'},
      success:(res) => {
        if (res.statusCode === 200) {
          console.log("查询历史会话成功：", res.data);

          // 格式化时间 & 添加 counselorName
          Promise.all(
            res.data.map(session => {
              return new Promise((resolve) => {
                const formattedSession = {
                  ...session,
                  date: this.formatDateTime(session.sessionStartTime),
                };
    
                // 查询咨询师信息
                this.getCounselor(session.counselorId)
                  .then(counselor => {
                    formattedSession.counselorName = counselor.name;
                    resolve(formattedSession);
                  })
                  .catch(() => {
                    formattedSession.counselorName = '未知';
                    resolve(formattedSession);
                  });
              });
            })
          ).then(formattedSessions => {
            this.setData({ 
              records: formattedSessions,
              loading: false
             });
          });
        } else {
          console.log("查询历史会话失败：",res);
        }
      },
      fail(err) {
        console.error('请求失败:', err);
      }
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
            //console.log(`查询咨询师 ${id} 信息成功：`, counselor);
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

  toggleDetails(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/recorddetail/recorddetail?id=${id}`, 
    });
  },

})
