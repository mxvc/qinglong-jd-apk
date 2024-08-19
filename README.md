# 介绍
青龙脚本，京东cookie获取上传APP
- 手机使用app
- 登录后，自动将cookie上传到青龙脚本服务器
- 源码全部开放



## 界面
![docs/app.png](docs/app.png)

![docs/web.png](docs/web.png)

# 使用方式一、直接下载右侧apk
在青龙后台创建一个应用，赋予修改变量的权限。 依次点击【系统设置】【应用设置】【创建应用】 ，名称无所谓，权限选择 环境变量。 创建后得到Client ID，Client Secret
  ![img.png](docs/ql-app.png)




# 使用方式二、自行编译
1. 克隆 本项目
2. 直接打包即可

# 更新日志
- 202406 github action打包apk支持 Secret方式。 
- 202408 增加清除本地cookie的功能，方便操作多个京东账号
# 开发笔记, 记录开发过程中遇到的问题
接口参考文档：https://qinglong.ukenn.top








