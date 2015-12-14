# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/mudar/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

###### Prkng ######
# Retrofit GSON conversion
-keep class ng.prk.prkngandroid.model.** { *; }


###### Retrofit ######
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions


###### Okio #######
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.*


###### Mapbox ######
# Square okio, ignoring warnings,
# see https://github.com/square/okio/issues/60
-dontwarn okio.**
-keep class com.mapbox.mapboxsdk.annotations.** { *; }
-keep class com.mapbox.mapboxsdk.geometry.** { *; }
-keep class com.mapbox.mapboxsdk.http.** { *; }
-keep class com.mapbox.mapboxsdk.views.** { *; }


###### Crashlytics ######
-keep class com.crashlytics.** { *; }
-keep class com.crashlytics.android.**
-keepattributes SourceFile,LineNumberTable
