![Packup](./Readme.assets/banner.png)

# Packup

Pack up all about your university life.


## Screen Shots

* Dark Mode is not shown here.

![Screen Shots](./Readme.assets/thumbnail.png)



## Licenses

All files on the Packup repository are subject to the MIT license. Please read the License file at the root of the project.



## Compile

[Android Studio 4.1](https://developer.android.google.cn/studio).



## Unit Test

对 api 模块进行单元测试前请在 `packup/app/src/test/java/org/engrave/packup/` 新建文件 `UserLoginInfo.kt`：

```kotlin
package org.engrave.packup

const val sid = "你的学号"
const val pw = "你的密码"
```

并在 `.gitignore` 添加该文件。

