### 项目配置
数据存储依懒于Bmob,为了隐私安全和方便管理需要在`local.properties`文件,
新增一个`bmobApplicationId`配置用于存放应用秘钥,
需要创建一个`Posts`表, 结构字段如下
```
user: String
title: String
content: String
version: String
```