pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()



    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        //noinspection JcenterRepositoryObsolete
        jcenter()
        mavenCentral()
        maven {
            setUrl("https://jitpack.io")
        }


    }
}

rootProject.name = "ReadBlog App"
include(":app")
