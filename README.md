# Bmob毕业论文选题系统
## 推送和拉取代码时间
* 当天写的代码一定要当天推送到Github，时间是晚上十二点之前
* 早上开始写代码的时候要拉取代码，保证代码是跟远程同步的
***
## 配置git账号邮箱

* 首先找到自己项目的目录，进去右键
![3QVGH2CL00J6E H%0`(~YHD](https://user-images.githubusercontent.com/72325667/176997234-c3e84d39-5961-4036-96d0-686b46c2baf6.png)
* 在git页面中执行下面的代码</br>
```Kotlin
git config --global user.name "你自己的用户名，可以随便设置"
git config --global user.email "你自己的邮箱，可以随便设置"
```
![Untitled](https://user-images.githubusercontent.com/72325667/176995723-ee0970c1-c38c-4c20-82dc-d2ac42c0afde.png)
***
## 拉取代码步骤
* git pull origin</br>
![M WGT30 E4IE~O6S4KP9PIW](https://user-images.githubusercontent.com/72325667/176995772-1fdadf20-4cc8-41f9-8b6a-7ce09d508fc5.png)

## 推送代码步骤
### 1. 没有发生合并代码冲突
* add(git代码：git add .)</br>
![add](https://user-images.githubusercontent.com/72325667/176986255-09262d6c-f22d-44a7-9233-1286c17628d7.png)

* commit(git代码：git commit -m"增加了邮箱登录功能"),注意：每次commit的时候都要注明自己做了什么，比如“增加了邮箱登录功能”</br>
![commit](https://user-images.githubusercontent.com/72325667/176986294-9104c596-2e15-4727-8a17-1cb65f512ac5.png)

* push(git代码：git push origin master，origin)</br>
![push](https://user-images.githubusercontent.com/72325667/176986302-96139519-479c-45d4-bae0-1b4272cb380e.png)

### 2. 推送或拉取时发生冲突
 * 建议先搜索了解冲突是什么原因造成的
 
 
