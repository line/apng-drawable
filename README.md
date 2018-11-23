# ApngDrawable

![](https://img.shields.io/badge/Version-Now%20Distributing-blue.svg)

ApngDrawable is fast and light weight Animated Portable Network Graphics(APNG) image decoder library for Android platform.
an Android library project that provides image trimming. ApngDrawable is written in Kotlin and C++.

## How to use

TBA

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
