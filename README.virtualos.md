# VirtualOS Container

VirtualOS now launches the [Ananbox](https://github.com/Ananbox/ananbox) Android container engine. The dashboard opens Ananbox’s runtime activity; on first launch it asks the user to select an ARM64 or x86_64 `rootfs.7z` archive. Ananbox documents host support for Android 11 and newer and ARM64/x86_64. Its own README describes the runtime as a work in progress and notes that the container is still buggy.

The Ananbox engine and native renderer/proot sources are included under `app/src/main/cpp` and `app/src/main/java/com/github/ananbox`. The first-run screen can download the official ARM64 `rootfs_arm64.7z` from Ananbox v0.0.2, verifies its pinned SHA-256, and extracts it into private app storage. A file picker is also available for other compatible archives.

## Build

GitHub Actions installs Android SDK 36, NDK 25.2, and CMake 3.22.1, prepares the pinned Boost source required by the native renderer, and builds `assembleDebug`. This project is configured for Android host API 27+ and packages ARM64 and x86_64 native libraries.

## Source and licensing

The original VirtualOS project files retain their MIT license. The Ananbox source is attributed in [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md). The Ananbox repository describes itself as FOSS but does not provide a license file; check the upstream repository for licensing terms before redistributing modified Ananbox code. Native third-party components retain their in-source license notices.
