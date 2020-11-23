-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class org.engrave.packup.**$$serializer { *; }
-keepclassmembers class org.engrave.packup {
    *** Companion;
}
-keepclasseswithmembers class org.engrave.packup.** {
    kotlinx.serialization.KSerializer serializer(...);
}