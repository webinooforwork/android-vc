pluginManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }
dependencyResolutionManagement { repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS); repositories { google(); mavenCentral(); maven("https://jitpack.io"); maven("https://jcenter.bintray.com"); maven("https://verve.jfrog.io/artifactory/verve-gradle-release/") } }
rootProject.name = "VirtualOSContainer"
include(":app")
