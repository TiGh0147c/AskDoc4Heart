// 云函数入口文件
const cloud = require('wx-server-sdk')

cloud.init({ env: cloud.DYNAMIC_CURRENT_ENV }) // 使用当前云环境

// 云函数入口函数
exports.main = async (event, context) => {
  const db = cloud.database();
  const todayStart = new Date();
  todayStart.setHours(0, 0, 0, 0); // 设置时间为当天开始时间
  const todayEnd = new Date(todayStart);
  todayEnd.setDate(todayStart.getDate() + 1); // 设置时间为第二天的开始时间
  try {
    // 查询今日值班的记录
    const dutyResult = await db.collection('duty')
    .where({
      date: db.command.gte(todayStart).and(db.command.lt(todayEnd)) // 比较日期部分
    })
    .get();
    
    if (dutyResult.data.length === 0) return { data: [] }; // 如果没有找到任何值班记录，则返回空数组
    
    // 提取所有需要查询的咨询师的phone
    const phones = dutyResult.data.map(item => item.phone);
    
    // 根据提取的phones查询对应的咨询师信息
    const counselorPromises = phones.map(phone =>
      db.collection('counselor')
      .where({
        phone: phone // 根据phone字段查询
      })
      .field({
        name: true,
        intro: true,
        tag: true,
        _id: true,
      })
      .get(),
    );
    
    // 等待所有查询完成并收集结果
    const counselors = (await Promise.all(counselorPromises)).map(result => result.data).filter(item => item); // 过滤掉可能的undefined
    
    return { data: counselors };
  } catch (e) {
    console.error(e);
    return { errMsg: e.message };
  }
}