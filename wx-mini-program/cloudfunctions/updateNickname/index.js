// 云函数入口文件
const cloud = require('wx-server-sdk')

cloud.init({ env: cloud.DYNAMIC_CURRENT_ENV }) // 使用当前云环境

// 云函数入口函数
exports.main = async (event, context) => {
  const { openid, newNickname } = event;
  const db = cloud.database();

  try {
    await db.collection('user_account').where({
      wid: openid
    }).update({
      data: {
        nickname: newNickname
      }
    });

    return {
      success: true,
      message: '昵称更新成功'
    };
  } catch (err) {
    console.error('[数据库] [更新记录] 失败：', err);
    return {
      success: false,
      message: '昵称更新失败，请稍后再试'
    };
  }
}