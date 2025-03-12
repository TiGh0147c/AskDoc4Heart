// 云函数入口文件
const cloud = require('wx-server-sdk')

cloud.init({ env: cloud.DYNAMIC_CURRENT_ENV }) // 使用当前云环境

// 云函数入口函数
exports.main = async (event, context) => {
  const db = cloud.database();
  const { openid, nickname, birthYear, gender, phonenum, password } = event;

  try {
    await db.collection('user_account').add({
      data: {
        wid: openid,
        nickname: nickname,
        birthYear: birthYear,
        gender: gender,
        phonenum: phonenum,
        password: password,
        avatarUrl: null
      }
    });
    return {
      success: true,
      message: '注册成功'
    };
  } catch (err) {
    console.error('[数据库] [新增记录] 失败：', err);
    return {
      success: false,
      message: '注册失败，请稍后再试'
    };
  }
}