-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}
-keep public class android.net.http.SslError
-keep public class android.webkit.WebViewClient

-dontwarn android.webkit.WebView
-dontwarn android.net.http.SslError
-dontwarn android.webkit.WebViewClient

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

-keepattributes Exceptions, Signature, InnerClasses

# Keep - Library. Keep all public and protected classes, fields, and methods.
#-keep public class * {
#    public protected <fields>;
#    public protected <methods>;
#}

# Also keep - Enumerations. Keep the special static methods that are required in
# enumeration classes.
-keepclassmembers enum  * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep names - Native method names. Keep all native class/method names.
-keepclasseswithmembers,allowshrinking class * {
    native <methods>;
}

# 不做预校验
-dontpreverify

### 忽略警告
#-ignorewarning

-dontwarn com.google.**
-dontwarn retrofit2.**

#如果引用了v4或者v7包
-dontwarn android.support.**

-keepattributes EnclosingMethod

## 注解View Click Event on XML File
-keepclassmembers class *{
   void *(android.view.View);
}

#保护注解
-keepattributes *Annotation*

# 不混淆枚举类中的values()和valueOf()方法
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
# 不混淆Parcelable实现类中的CREATOR字段，以保证Parcelable机制正常工作
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}
# 不混淆R文件中的所有静态字段，以保证正确找到每个资源的id
-keepclassmembers class **.R$* {
    public static <fields>;
}

# 不混淆Keep类
-keep class android.support.annotation.Keep
# 不混淆使用了注解的类及类成员
-keep @android.support.annotation.Keep class * {*;}
# 如果类中有使用了注解的方法，则不混淆类和类成员
-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}
# 如果类中有使用了注解的字段，则不混淆类和类成员
-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}
# 如果类中有使用了注解的构造函数，则不混淆类和类成员
-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}

#保护Android四大组件等
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Service
-keep public class * extends android.android.content.BroadcastReceiver
-keep public class * extends android.android.content.ContentProvider


-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

#如果有其它包有warning，在报出warning的包加入下面类似的-dontwarn 报名
-keep class com.squareup.okhttp.**{*;}
-dontwarn com.squareup.okhttp.**
#不混淆类及其成员名
-keepclasseswithmembernames class com.squareup.okhttp{*;}


-keep class android.android.content.android.content.pm.**{*;}
-dontwarn android.android.content.android.content.pm.**
-keepclasseswithmembernames class android.android.content.android.content.pm{*;}

#不混淆xutils jar包代码
-keep class org.**{*;}
-dontwarn org.**
-keepclasseswithmembernames class org{*;}

-keep class javax.**{*;}
-dontwarn javax.**
-keepclasseswithmembernames class javax{*;}

-keep class okio.**{*;}
-dontwarn okio.**
-keepclasseswithmembernames class okio{*;}

-keep class com.networkbench.**{*;}
-dontwarn com.networkbench.**
-keepclasseswithmembernames class com.networkbench{*;}

-keep class android.os.**{*;}
-dontwarn android.os.**

-keep class android.service.**{*;}
-dontwarn android.service.**

-keep class com.alibaba.sdk.**{*;}
-dontwarn com.alibaba.sdk.**

-keep class mediaplayer.**{*;}
-dontwarn mediaplayer.**

-keep class me.jessyan.autosize.** { *; }
-keep interface me.jessyan.autosize.** { *; }
