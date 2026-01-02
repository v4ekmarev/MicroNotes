rootProject.name = "MicroNotes"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

include(":composeApp")
include(":library:calendar")
include(":core:model")
include(":core:common")
include(":core:ui-kit")
include(":core:navigation")
include(":core:notification")
include(":feature")
include(":feature:note-list-impl")
include(":feature:note-list-api")
include(":feature:note-api")
include(":feature:note-impl")
include(":feature:contacts-api")
include(":feature:contacts-impl")
include(":feature:profile-api")
include(":feature:profile-impl")
include(":feature:splash-api")
include(":feature:splash-impl")
include(":data:database")
include(":data:network")
include(":server")
