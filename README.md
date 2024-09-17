# gradle-versions-plugin-i906

## About

It is a sample repo demonstrating an [issue #906][issue-906] reported in [gradle-versions-plugin].

## To reproduce

```bash
./gradlew dependencyUpdates
```

In result you would see:

```text
The following dependencies have later milestone versions:
 - com.google.guava:guava [33.3.0-android -> 33.3.0-jre]
     https://github.com/google/guava
```

Tested with Gradle 8.10.1, Java Temurin 17.0.12+7 and gradle-versions-plugin 0.51.0.

[issue-906]: https://github.com/ben-manes/gradle-versions-plugin/issues/906
[gradle-versions-plugin]: https://github.com/ben-manes/gradle-versions-plugin
