# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# 기본 컴포넌트
-keep public class * extends android.app.Activity
-keep public class * extends androidx.fragment.app.Fragment

# Annotation 유지
-keep @interface **
-keepattributes Signature, Exceptions, EnclosingMethod, RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Dao class *
-keepclassmembers class * {
    @androidx.room.* <methods>;
}

# Retrofit
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-dontwarn javax.annotation.**

# OkHttp
-dontwarn okhttp3.**

# kotlinx.serialization
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class * {
    @kotlinx.serialization.* <fields>;
}
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}

# coil
-dontwarn coil.**

# Coroutines
-keep class kotlinx.coroutines.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# WebView JavaScript Interface
-keepclassmembers class com.on.turip.ui.trip.detail.webview.WebViewVideoBridge {
    @android.webkit.JavascriptInterface <methods>;
}

-keep class com.on.turip.ui.trip.detail.webview.WebViewVideoBridge {
    public *;
}

# JavaScript Interface 일반 규칙
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keepattributes JavascriptInterface
-keepattributes *Annotation*

# WebView 관련
-keep class android.webkit.JavascriptInterface
-keep class android.webkit.WebView
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebChromeClient {
    public void *(android.webkit.WebView, java.lang.String);
}
