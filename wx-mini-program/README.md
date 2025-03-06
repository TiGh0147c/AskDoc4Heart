# 开发环境搭建

前提：已经将wx-mini-program文件夹克隆/下载到本地

## 导入并在开发环境打开文件

下载微信开发者工具
(https://mp.weixin.qq.com/wxamp/thirdtools/extend?token=507248289&lang=zh_CN)
->
打开微信开发者工具
点击小程序-导入
->
选择wx-mini-program文件夹
创建
->
进入页面

## 配置云环境

看页面中间的资源管理器
cloudfunctions|当前环境:undifined
右击->当前环境->cloud1
cloudfunctions|当前环境:cloud1即配置成功

# 文件介绍

## 快捷键

Ctrl+S 保存所有修改

## cloudfunctions

cloudfunctions： 云函数文件夹
- 新建云函数： 右击cloudfunctions->新建Node.js云函数->输入云函数名称
- 上传云函数： 在本地编写完云函数后，右击该云函数文件夹->上传并部署：云端安装依赖

## miniprogram

miniprogram：小程序文件夹

### components

存放自定义组件

### images

存放图片素材

### pages

存放前端页面

alter：修改信息
counseling：咨询师列表
dialogue：咨询会话
home：个人中心
index：主页/心理咨询
login：登录
register：注册

### app.js app.json app.wxss

一般不用改，放着就行

### 其他是配置文件


