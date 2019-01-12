# ApngDrawable

[![Download](https://api.bintray.com/packages/line/apng-drawable/com.linecorp.apng/images/download.svg)](https://bintray.com/line/apng-drawable/com.linecorp.apng/_latestVersion)

ApngDrawable is fast and light weight Animated Portable Network Graphics(APNG) image decoder library for Android platform.
ApngDrawable is written in Kotlin and C++.

## How to use

Use Gradle to build the library. Download it from [JCenter](https://bintray.com/bintray/jcenter) and add configurations in the `build.gradle` file as follows.

```build.gradle
// In your top-level project's `build.gradle`
allprojects {
    repositories {
        google()
        jcenter()
    }
}

// In your app project's `build.gradle`
dependencies {
  implementation 'com.linecorp:apng:x.y.z'
}
```

If using Kotlin Gradle DSL, add configurations in the `build.gradle.kts` file as follows.

```build.gradle.kts
// In your top-level project's `build.gradle.kts`
allprojects {
    repositories {
        jcenter()
    }
}

// In your app project's `build.gradle.kts`
dependencies {
  implementation("com.linecorp:apng:x.y.z")
}
```

## Getting started

You can decode from a lot of source types, e.g. File, InputStream and Resources.

```kotlin
// Decode from File
val drawable1 = ApngDrawable.decode(File("path/to/file"))

// Decode from InputStream
val drawable2 = File("path/to/file").inputStream().use {
    ApngDrawable.decode(it)
}

// Decode from Resources
val drawable3 = ApngDrawable.decode(context.resources, R.raw.apng_image)
```

You can find a more advanced way of using the library from the [example](https://github.com/line/apng-drawable/tree/master/sample-app).

## How to build

Note: This operation is necessary when building from code. It's not necessary if you are reading using `implementation` as shown in "[How to use]".

The patched `libpng` sources aren't included in the repository.
You need to download `libpng` and apply APNG patch first.

```sh
$ cat libpng_version | xargs ./download_libpng_and_apply_apng_patch.sh
$ ./gradlew :sample-app:assembleDebug
```


## How to contribute to ApngDrawable

See [CONTRIBUTING.md](CONTRIBUTING.md)

## License

```
Copyright 2018 LINE Corporation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
