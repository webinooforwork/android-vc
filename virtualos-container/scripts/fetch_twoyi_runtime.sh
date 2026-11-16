#!/usr/bin/env bash
set -euo pipefail
# Twoyi 0.5.4 includes the prebuilt Android 8.1 rootfs and Rust renderer in its official APK.
version="0.5.4"
apk_name="twoyi_0.5.4-03211927-release.apk"
apk_url="https://github.com/twoyi/twoyi/releases/download/${version}/${apk_name}"
tmp_apk="${RUNNER_TEMP:-/tmp}/${apk_name}"
mkdir -p app/src/main/assets app/src/main/jniLibs/arm64-v8a
curl --fail --location --retry 3 --output "$tmp_apk" "$apk_url"
unzip -p "$tmp_apk" assets/rootfs.7z > app/src/main/assets/rootfs.7z
unzip -p "$tmp_apk" lib/arm64-v8a/libtwoyi.so > app/src/main/jniLibs/arm64-v8a/libtwoyi.so
test -s app/src/main/assets/rootfs.7z
test -s app/src/main/jniLibs/arm64-v8a/libtwoyi.so
