# TyranoFramework_for_Android   
修正过的tyranobuilder安卓构建框架，使用IDEA IDE进行编译构建

# 使用教程   
官方教程: <https://shikemokumk.hateblo.jp/entry/2016/11/30/155036>   
想要清楚明了的教程请见B站视频: <https://www.bilibili.com/video/BV1jS4y1S73a>(本来想嵌入README的，试了好几遍都不行，放弃了)

## 需求:   
Android Studio，Android SDK 11.0(API level:30), jdk 8

## 步骤：   
1.打开AS，从VCS导入新项目   
2.等待`Gradle Sync`(可挂梯子)  
3.在app/src/main文件夹中创建空文件夹assets，并把tyranobuilder导出的**data,tyrano(均为文件夹)以及index.html**拖入其中   
4.在res文件夹中可以修改app名称以及图标，名称在string.xml中修改，图标则是ic_launcher   
5.点击上方绿色箭头可以在AVD中运行(前提是有装载)，或者可以在工具栏中直接选择build APK(s)来导出APK文件   

## 说明   
1.Android SDK**务必选择**Android 11.0!!!否则构建时会报错   
2.构建时**请勿开启热点**，否则会直接导致失败   
3.若tyranobuilder导出的文件和上述不同或者缺失，请确认导出模式是"安卓(Android)"，并再次导出
