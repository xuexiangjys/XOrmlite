# XOrmlite
[![](https://jitpack.io/v/xuexiangjys/XOrmlite.svg)](https://jitpack.io/#xuexiangjys/XOrmlite)
[![api](https://img.shields.io/badge/API-14+-brightgreen.svg)](https://android-arsenal.com/api?level=14)
[![Issue](https://img.shields.io/github/issues/xuexiangjys/XOrmlite.svg)](https://github.com/xuexiangjys/XOrmlite/issues)
[![Star](https://img.shields.io/github/stars/xuexiangjys/XOrmlite.svg)](https://github.com/xuexiangjys/XOrmlite)

一个方便实用的OrmLite数据库框架，支持一键集成。

## 关于我

| 公众号   | 掘金     |  知乎    |  CSDN   |   简书   |   思否  |   哔哩哔哩  |   今日头条
|---------|---------|--------- |---------|---------|---------|---------|---------|
| [我的Android开源之旅](https://ss.im5i.com/2021/06/14/6tqAU.png)  |  [点我](https://juejin.im/user/598feef55188257d592e56ed/posts)    |   [点我](https://www.zhihu.com/people/xuexiangjys/posts)       |   [点我](https://xuexiangjys.blog.csdn.net/)  |   [点我](https://www.jianshu.com/u/6bf605575337)  |   [点我](https://segmentfault.com/u/xuexiangjys)  |   [点我](https://space.bilibili.com/483850585)  |   [点我](https://img.rruu.net/image/5ff34ff7b02dd)

## 特征

* 支持通过`@DataBase`进行数据库配置。

* 支持自动生成数据库管理仓库`DataBaseRepository`。

* 支持自动搜索所有的数据库表类，并自动创建数据库表。

* 支持内部存储和外部存储两种数据库。

* 支持自定义数据库存储位置。

* 支持自定义数据库打开、升级以及降级的接口。

* 支持事务操作、回滚等。

* 提供了常用的数据库操作API。

## 1、演示（请star支持）

![][demo-gif]

### Demo下载

[![downloads][download-svg]][download-url]

![][download-img]

## 2、如何使用

目前支持主流开发工具AndroidStudio的使用，直接配置build.gradle，增加依赖即可.

### 2.1、Android Studio导入方法，添加Gradle依赖

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
  implementation 'com.github.xuexiangjys.XOrmlite:xormlite-runtime:1.0.2'
  annotationProcessor 'com.github.xuexiangjys.XOrmlite:xormlite-compiler:1.0.2'
}
```

### 2.2、数据库注册配置

1.使用`@DataBase`进行数据库注册配置。属性如下：

* name ：数据库的名称，必填。
* version ：数据库的版本, 默认版本为1
* type ：数据库的存放类型，默认是内部存储`DataBaseType.INTERNAL`
* path ：数据库存放的路径，只对外部存储的数据库起作用，默认的地址为：/storage/emulated/0/Android/xormlite/databases/

【注意】`@DataBase`必须注册在`Application`类上才起作用。

```
@DataBase(name = "external", type = DataBaseType.EXTERNAL, path = "/storage/emulated/0/xormlite/databases")
public class ExternalApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }
}

```

2.使用`@DataBase`注册完后，请编译一下项目：build -> Rebuild Project，之后会自动生成数据库管理仓库: 数据库名 + DataBaseRepository 的Java文件。文件的生成路径如下：

![xormlite_databaserepository.png](https://ss.im5i.com/2021/06/15/6RxXS.jpg)

例如上面我们注册的数据库名为：external，自动生成的数据库管理仓库为 ExternalDataBaseRepository。

3.初始化数据库管理仓库，设置数据库实现接口。

* 内部存储的数据库需要需要实现`IDatabase`接口。

* 外部存储的数据库需要需要实现`IExternalDataBase`接口。

```
@DataBase(name = "internal", type = DataBaseType.INTERNAL)
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        InternalDataBaseRepository.getInstance()
                .setIDatabase(new InternalDataBase())  //设置内部存储的数据库实现接口
                .init(this);

        ExternalDataBaseRepository.getInstance()
                .setIDatabase(new ExternalDataBase(  //设置外部存储的数据库实现接口
                        ExternalDataBaseRepository.DATABASE_PATH,
                        ExternalDataBaseRepository.DATABASE_NAME,
                        ExternalDataBaseRepository.DATABASE_VERSION))
                .init(this);
    }
}
```

[点击查看InternalDataBase的实现](https://github.com/xuexiangjys/XOrmlite/blob/master/app/src/main/java/com/xuexiang/xormlitedemo/db/InternalDataBase.java)

### 2.3、数据库服务获取

数据库管理仓库DataBaseRepository提供了获取数据库操作服务的API `getDataBase`, 传入标有`@DatabaseTable`的数据库实体类Class, 获取对应的数据库操作服务`DBService`。

```
DBService<Student> mDBService = InternalDataBaseRepository.getInstance().getDataBase(Student.class);
```

### 2.4、数据库操作

`DBService`提供了丰富的数据库操作API，详细API请[点击参见](https://github.com/xuexiangjys/XOrmlite/blob/master/xormlite-runtime/src/main/java/com/xuexiang/xormlite/db/DBService.java)。

以下简要介绍一部分API：

#### 插入

* insert： 插入单条数据。
* insertData： 插入单条数据(返回被插入的对象）。
* insertDatas： 批量添加，返回插入数据的数量。

#### 查询

* queryAll： 查询所有的数据。
* queryAllOrderBy： 查询所有的数据并根据列名进行排序，返回一个对象集合。
* queryAndOrderBy： 有条件的排序查询，返回一个对象集合。
* queryById： 根据id查询出一条数据。
* queryByField： 根据条件查询，返回一个对象集合。
* queryByColumn： 根据条件查询，返回一个对象集合。
* queryForColumnFirst： 根据条件查询，返回第一个符合条件的对象。
* indistinctQueryForColumn： 根据条件模糊查询，返回一个对象集合。
* queryDataBySql： 根据sql语句查询，返回对象的集合。
* queryPage: 根据某个字段排序进行分页查询

#### 更新

* updateData： 使用对象更新一条记录（注意：对象必须带唯一标识ID,且该方法不能更新ID字段)。
* updateDataByColumn： 根据某一或多个条件更新对象。
* updateDataBySQL： 根据条件做update时直接使用sql语句进行更新，sql语句中必须包含关键字【INSERT，DELETE，UPDATE】。

#### 删除

* deleteData： 根据对象删除一条记录（注意：对象必须带唯一标识ID,否则方法不起作用)。
* deleteDatas： 批量删除（注意：对象必须带唯一标识ID,否则方法不起作用)【大数据量的删除不起左右，会报too many SQL variables错误】。
* deleteAll： 删除所有数据。
* deleteById： 根据id删除一条记录。

#### 其他

* executeSql： 执行sql语句。
* getQueryBuilder： 返回QueryBuilder。
* getUpdateBuilder： 返回UpdateBuilder。
* getDeleteBuilder： 返回DeleteBuilder。


#### 事务操作

* doInTransaction： 执行事务操作。
* beginTransaction： 开启数据库事务操作。
* commit： 提交事务。
* rollBack： 事务回滚。

### 2.5、数据库操作示例

1.插入单条数据
```
Student student = new Student();
student.setUserName("xuexiang");
student.setSex("男");
student.setAge((int) (Math.random() * 100));

try {
    mDBService.insert(student);
} catch (SQLException e) {
    e.printStackTrace();
}
```

2.更新数据库某一字段
```
try {
    mDBService.updateDataByColumn("username", "xxxx", "username", "xuexiang");
} catch (SQLException e1) {
    e1.printStackTrace();
}
```

3.执行事务插入
```
try {
    mDBService.doInTransaction(new Callable<Boolean>() {
        @Override
        public Boolean call() throws Exception {
            for (int i = 0; i < mTempList.size(); i++) {
                mDBService.insert(mTempList.get(i));
            }
            return true;
        }
    });
} catch (SQLException e) {
    e.printStackTrace();
    ToastUtils.toast("事务执行失败！");
}
```

4.执行事务删除
```
try {
    mDBService.doInTransaction(new Callable<Boolean>() {
        @Override
        public Boolean call() throws Exception {
            for (int i = 0; i < mTempList.size(); i++) {
                mDBService.deleteData(mTempList.get(i));
            }
            return true;
        }
    });
    mStudentAdapter.updateList(mDBService.queryAll());
} catch (SQLException e) {
    e.printStackTrace();
    ToastUtils.toast("事务执行失败！");
}
```

## 混淆配置

```
-keepattributes *DatabaseField*
-keepattributes *DatabaseTable*
-keepattributes *SerializedName*
-keep class com.j256.**
-keepclassmembers class com.j256.** { *; }
-keep enum com.j256.**
-keepclassmembers enum com.j256.** { *; }
-keep interface com.j256.**
-keepclassmembers interface com.j256.** { *; }
```

## 如果觉得项目还不错，可以考虑打赏一波

> 你的打赏是我维护的动力，我将会列出所有打赏人员的清单在下方作为凭证，打赏前请留下打赏项目的备注！

![pay.png](https://ss.im5i.com/2021/06/14/6twG6.png)

## 微信公众号

> 更多资讯内容，欢迎扫描关注我的个人微信公众号:【我的Android开源之旅】

![gzh_weixin.jpg](https://ss.im5i.com/2021/06/14/65yoL.jpg)

## 联系方式

[![](https://img.shields.io/badge/点击一键加入QQ交流群-602082750-blue.svg)](http://shang.qq.com/wpa/qunwpa?idkey=9922861ef85c19f1575aecea0e8680f60d9386080a97ed310c971ae074998887)


[demo-gif]: https://z3.ax1x.com/2021/06/15/2Hmclj.gif
[download-svg]: https://img.shields.io/badge/downloads-1.6M-blue.svg
[download-url]: https://github.com/xuexiangjys/XOrmlite/blob/master/apk/demo.apk?raw=true
[download-img]: https://ss.im5i.com/2021/06/15/6Rmdj.png