// 云函数入口文件
const cloud = require('wx-server-sdk')
const axios = require('axios')

cloud.init({ env: cloud.DYNAMIC_CURRENT_ENV }) // 使用当前云环境

// 云函数入口函数
exports.main = async (event, context) => {
  const api_key = 'lm-hhsdMXpRHHvZUj8UYzBvLw==' 
  const service_name = 'aca-chat-send'
  const url = 'https://nlp.aliyuncs.com/v2/api/chat/send'

  const payload = {
    input: {
      messages: [{ role: "user", content: event.message }],
      aca: {
        botProfile: {
          characterId: "45c1cd3641564741bddeb02f8dec39af"
        },
        userProfile: {
          userId: event.userId,
          basicInfo: ""
        },
        scenario: {
          description: ""
        },
        context: {
          useChatHistory: true
        }
      }
    }
  }

  try {
    const response = await axios.post(url, payload, {
      headers: {
        'Content-Type': 'application/json',
        'X-AcA-DataInspection': 'enable',
        'x-fag-servicename': service_name,
        'x-fag-appcode': 'aca',
        'Authorization': `Bearer ${api_key}`
      }
    })

    return {
      success: true,
      data: response.data
    }
  } catch (error) {
    return {
      success: false,
      errMsg: error.toString()
    }
  }
}