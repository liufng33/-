# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep ExoPlayer classes
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# Keep Jsoup classes
-keep class org.jsoup.** { *; }
-dontwarn org.jsoup.**

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
