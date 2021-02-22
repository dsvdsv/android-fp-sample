Android FP Sample
=====================

Functional Programing Android architecture approaches using [cats](https://typelevel.org/cats/) and [cats-effect](https://typelevel.org/cats-effect/).

# Approach used

This project uses the `Tagless-Final` Functional Programming style to show current [Euro foreign exchange reference rates](https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml)
![Demo Screenshot][1]

# How build it

Since the project uses the [Android SDK](https://developer.android.com/) you will need to set environment variable `ANDROID_SDK_ROOT=your_sdk_path`
to be able to compile and run it. You can assemble project command line::

`./gradlew assemble`

[1]:./assets/success.png