plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.readblogapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.readblogapp"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled?.and(true)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures{
        viewBinding =true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.android.car.ui:car-ui-lib:2.5.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.6.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.6.0")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    // circular imageview
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    // custom toast
    implementation ("io.github.shashank02051997:FancyToast:2.0.2")

    // dynamic size
    implementation ("com.github.MrNouri:DynamicSizes:1.0")

    // glide depandancy
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    //imageview pic
    implementation ("com.github.dhaval2404:imagepicker:2.1")

    // shimar layout
    implementation ("com.github.sharish:ShimmerRecyclerView:v1.3")

    // lottie Animation
    implementation ("com.airbnb.android:lottie:6.1.0")

    // tosty
    implementation ("com.github.GrenderG:Toasty:1.5.2")







}