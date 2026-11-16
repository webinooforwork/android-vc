# Third-party components

The TwoYi application and container renderer sources in `app/src/main/java/io/twoyi`, `app/src/main/twoyi-rs`, `app/src/main/jniLibs/arm64-v8a`, and TwoYi-derived resources are from [twoyi/twoyi](https://github.com/twoyi/twoyi), licensed under Mozilla Public License 2.0. The MPL text is in `THIRD_PARTY_LICENSES/Mozilla-Public-License-2.0.txt`. Modifications to those covered source files remain under MPL-2.0.

The CI workflow fetches the official TwoYi 0.5.4 release APK and extracts `assets/rootfs.7z` and `lib/arm64-v8a/libtwoyi.so` into the build tree. Those runtime binaries are not checked into this repository. The TwoYi project reports support for Android 8.1–12 and ARM64 hosts.

Other libraries retain their respective upstream licenses; the in-app About screen lists the bundled Java library notices.
