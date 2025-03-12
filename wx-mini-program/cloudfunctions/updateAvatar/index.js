// 云函数入口文件
const cloud = require('wx-server-sdk')

cloud.init({ env: cloud.DYNAMIC_CURRENT_ENV }) // 使用当前云环境

// 云函数入口函数
exports.main = async (event, context) => {
  const { fileID, openid } = event;
  const db = cloud.database();
  try {
    // 更新数据库中的用户信息
    await db.collection('user_account').where({
      wid: openid
    }).update({
      data: {
        avatarUrl: fileID
      }
    });

    return {
      success: true,
      message: '头像更新成功'
    };
  } catch (err) {
    console.error('[数据库] [更新记录] 失败：', err);
    return {
      success: false,
      message: '头像更新失败，请稍后再试'
    };
  }
}