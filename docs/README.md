# XOrmlite
[![xormlite][xormlite-svg]][xormlite]  [![api][apisvg]][api]

一个方便实用的OrmLite数据库框架，支持一键集成。

## 特征

* 支持通过`@DataBase`进行数据库配置。

* 支持自动生成数据库管理仓库`DataBaseRepository`。

* 支持自动搜索所有的数据库表类，并自动创建数据库表。

* 支持内部存储和外部存储两种数据库。

* 支持自定义数据库存储位置。

* 支持自定义数据库打开、升级以及降级的接口。

* 支持事务操作、回滚等。

* 提供了常用的数据库操作API。

### Demo下载

[![downloads][download-svg]][download-url]

## 如何添加Gradle依赖

1.先在项目根目录的 build.gradle 的 repositories 添加:

```
allprojects {
     repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

2.然后在dependencies添加:

```
dependencies {
  ...
  implementation 'com.github.xuexiangjys.XOrmlite:xormlite-runtime:1.0.1'
  annotationProcessor 'com.github.xuexiangjys.XOrmlite:xormlite-compiler:1.0.1'
}
```

## 联系方式

[![](https://img.shields.io/badge/点击一键加入QQ交流群-602082750-blue.svg)](http://shang.qq.com/wpa/qunwpa?idkey=9922861ef85c19f1575aecea0e8680f60d9386080a97ed310c971ae074998887)

[xormlite-svg]: https://img.shields.io/badge/XOrmlite-v1.0.1-brightgreen.svg
[xormlite]: https://github.com/xuexiangjys/XOrmlite
[apisvg]: https://img.shields.io/badge/API-14+-brightgreen.svg
[api]: https://android-arsenal.com/api?level=14

[demo-gif]: https://github.com/xuexiangjys/XOrmlite/blob/master/img/xormlite.gif
[download-svg]: https://img.shields.io/badge/downloads-1.6M-blue.svg
[download-url]: https://github.com/xuexiangjys/XOrmlite/blob/master/apk/demo.apk?raw=true
[download-img]: https://github.com/xuexiangjys/XOrmlite/blob/master/img/download.png