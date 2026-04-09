# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /sdk/tools/proguard/proguard-android.txt

# Keep the application class
-keep class com.jiuzi.jiuzijiao.** { *; }

# Keep MediaPlayer
-keep class android.media.** { *; }
