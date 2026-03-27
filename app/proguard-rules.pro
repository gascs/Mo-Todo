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

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.coroutines.internal.MainDispatcherFactory {
    public *;
}
-keepclassmembernames class kotlinx.coroutines.CoroutineExceptionHandler {
    public *;
}

# Keep data classes and models from being obfuscated
-keep class com.motut.mo.data.** { *; }
-keep class com.motut.mo.util.Announcement { *; }

# Keep ViewModel classes
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    public <init>(...);
}

# Keep Compose-related classes
-keep @androidx.compose.runtime.Composable class *
-keep @androidx.compose.runtime.Stable class *
-keep @androidx.compose.runtime.Immutable class *

# Keep Serialization
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-dontnote kotlinx.serialization.**
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class kotlinx.serialization.** { *; }
-keep class * implements kotlinx.serialization.KSerializer { *; }

# Keep Coil (Image Loading)
-dontwarn coil.**
-keep class coil.** { *; }
-keep interface coil.** { *; }

# Keep Biometric classes
-keep class androidx.biometric.** { *; }

# Keep DataStore
-keep class androidx.datastore.** { *; }
-keep class * implements androidx.datastore.core.Serializer { *; }

# General ProGuard optimizations
-optimizationpasses 5
-allowaccessmodification
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# String encryption for better security (commented out as requires extra config)
#-applymapping mapping.txt

# Keep the main entry point
-keep class com.motut.mo.MainActivity { *; }

# Keep Application class
-keep class com.motut.mo.MoApplication { *; }

# Optimization for Kotlin
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}