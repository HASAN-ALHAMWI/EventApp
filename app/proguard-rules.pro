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

# Keep Room entities
-keepclassmembers class com.hasan.eventapp.data.models.** {
    <fields>;
}

# Keep data classes used in Gson
-keepclassmembers class com.hasan.eventapp.data.models.** implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# For Gson TypeToken issue
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken

# Preserve sealed classes used in ViewModels
-keepclassmembers class com.hasan.eventapp.presentation.auth.login.LoginState { *; }
-keepclassmembers class com.hasan.eventapp.presentation.events.list.EventListState { *; }
-keepclassmembers class com.hasan.eventapp.presentation.events.list.LogoutState { *; }

# Hilt
-keepclasseswithmembers class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}

# For Navigation Components
-keepnames class androidx.navigation.fragment.NavHostFragment

# For MockDataManager (which uses TypeToken)
-keepclassmembers class com.hasan.eventapp.data.managers.MockDataManager {
    private com.google.gson.Gson gson;
    private java.lang.String usersFileName;
    private java.lang.String eventsFileName;
}

# Error prone annotations to ignore
-dontwarn com.google.errorprone.annotations.CanIgnoreReturnValue
-dontwarn com.google.errorprone.annotations.CheckReturnValue
-dontwarn com.google.errorprone.annotations.Immutable
-dontwarn com.google.errorprone.annotations.RestrictedApi


# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

