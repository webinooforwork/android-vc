# VirtualOS Container

This app integrates the actual [TwoYi](https://github.com/twoyi/twoyi) rootless Android system container. The dashboard starts TwoYi's `Render2Activity`; its original app-import and settings flows are available from the dashboard. TwoYi's Java/Rust app source and ARM64 support libraries are included under their upstream MPL-2.0 license.

The GitHub Actions build fetches the pinned official TwoYi 0.5.4 release APK and extracts its `rootfs.7z` and `libtwoyi.so` into the build tree. The large rootfs and upstream runtime binary are deliberately fetched by CI and are not checked into this repository. To build locally, run `./scripts/fetch_twoyi_runtime.sh`, then open this project in Android Studio or run Gradle from the project root.

## Runtime compatibility

TwoYi 0.5.4 runs its Android 8.1 guest on ARM64 hosts running Android 8.1 through Android 12. This project sets minSdk 27 and packages only `arm64-v8a` to match the engine. It does not claim Android 13–16 guest support or additional guest ROM versions.

## Build

The GitHub Actions workflow installs Android SDK 35 and builds the APK. This workspace was not built locally. The TwoYi upstream project is archived; this integration keeps its upstream runtime and native libraries intact, while removing its App Center crash/analytics upload hooks.

## Licenses

This project’s original files are MIT licensed. TwoYi source and modifications to those files are MPL-2.0; see [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) and `THIRD_PARTY_LICENSES/`.
