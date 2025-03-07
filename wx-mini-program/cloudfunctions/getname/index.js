// 云函数入口文件
const cloud = require('wx-server-sdk')

cloud.init({ env: cloud.DYNAMIC_CURRENT_ENV }) // 使用当前云环境

// 云函数入口函数
exports.main = async (event, context) => {
  const openid = event.openid // 从调用参数中获取openid
  const db = cloud.database();
  try {
    // 查询 user_account 集合
    return await db.collection('user_account')
      .where({
        wid: openid // 根据openid查询
      })
      .field({
        nickname: true, 
        avatarUrl: true,// 只返回头像和nickname字段
        _id: false // 不返回_id字段
      })
      .get()
      .then(res => {
        if (res.data.length > 0) {
          // 成功找到用户，返回nickname
          return { success: true, nickname: res.data[0].nickname,avatarUrl: res.data[0].avatarUrl }
        } else {
          // 没有找到匹配的用户
          return { success: false, message: '未找到对应用户' }
        }
      })
  } catch (err) {
    // 查询失败处理
    console.error(err)
    return { success: false, message: '查询发生错误' }
  }
}