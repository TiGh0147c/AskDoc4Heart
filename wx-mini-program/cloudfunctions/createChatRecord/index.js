// 云函数入口文件
const cloud = require('wx-server-sdk')

cloud.init({ env: cloud.DYNAMIC_CURRENT_ENV }) // 使用当前云环境

const db = cloud.database();

exports.main = async (event, context) => {
  const { openid, counselorId, counselorName } = event;

  // 使用当前时间戳和openid生成唯一的消息表名
  const messageTableName = `${openid}_${new Date().getTime()}`;

  try {
    // 向record_cata添加记录
    await db.collection('record_cata').add({
      data: {
        openid: openid,
        counselorId: counselorId,
        counselorName: counselorName,
        date: new Date(),
        duration: null,
        messageTableName: messageTableName
      }
    });

    console.log('Chat record created successfully');

    // 插入一条初始化记录到新的消息表中，以确保该集合被创建
    await db.collection(messageTableName).add({
      data: {
        tag: -1, // 标识这是一条初始化记录
        content: '开始咨询',
        timestamp: new Date()
      }
    });

    console.log('Message table initialized');

    return {
      success: true,
      messageTableName: messageTableName
    };
  } catch (err) {
    console.error('Failed to create chat record or initialize message table:', err);
    return {
      success: false,
      errMsg: err.message
    };
  }
};