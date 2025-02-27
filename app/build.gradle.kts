    plugins {
        alias(libs.plugins.android.application)
        alias(libs.plugins.google.gms.google.services)
        // Add the Crashlytics Gradle plugin
        id("com.google.firebase.crashlytics")
        alias(libs.plugins.hypersdk)
        alias(libs.plugins.jetbrains.kotlin.android)
    }
    configurations.all {
        exclude(group = "in.juspay", module = "secure-component-prod")
    }

    android {
        namespace = "com.onezed"
        compileSdk = 35

        defaultConfig {
            applicationId = "com.onezed"
            minSdk = 26
            targetSdk = 35
            versionCode = 17
            versionName = "1.0.17"
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        viewBinding {
            enable=true
        }


        buildTypes {
            release {
                isMinifyEnabled = true
                isShrinkResources =true
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
        sourceSets {
            getByName("main") {
                assets {
                    srcDirs("src\\main\\assets", "src\\res\\assets")
                }
            }
        }
        kotlinOptions {
            jvmTarget = "1.8"
        }


            packaging {
                resources {
                    excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
                    excludes += "META-INF/LICENSE.md"
                    excludes += "META-INF/LICENSE-notice.md"
                }
            }



    }




    dependencies {

        implementation(libs.appcompat)
        implementation(libs.material)
        implementation(libs.activity)
        implementation(libs.constraintlayout)
        //FIREBASE NOTIFICATION
        implementation(libs.firebase.messaging)
        //FIREBASE IN-APP NOTIFICATION
        implementation(libs.firebase.inappmessaging.display)
        //crashlytics
        implementation(libs.firebase.crashlytics)
        implementation(libs.androidx.core.ktx)
        // implementation(libs.car.ui.lib)
        testImplementation(libs.junit)
        androidTestImplementation(libs.ext.junit)
        androidTestImplementation(libs.espresso.core)

        //BIOMETRIC
        implementation ("androidx.biometric:biometric:1.1.0")
        //SLIDER
        implementation ("com.github.bumptech.glide:glide:4.11.0")
        //VIEW PAGER
        implementation ("androidx.viewpager2:viewpager2:1.0.0")
        //OKHTTP
        implementation ("com.squareup.okhttp3:okhttp:3.9.0")
        // RAZORPAY
        implementation ("com.razorpay:checkout:1.6.40")


        annotationProcessor ("com.github.bumptech.glide:compiler:4.11.0")
        //SLIDER
        implementation ("com.github.bumptech.glide:glide:4.11.0")
        implementation ("com.github.denzcoskun:ImageSlideshow:0.1.2")

        annotationProcessor ("com.github.bumptech.glide:compiler:4.11.0")
        //MATERIAL
        implementation ("com.google.android.material:material:1.6.1")
        //IMAGE CROP
        implementation ("com.github.yalantis:ucrop:2.2.8")


        // Import the BoM for the Firebase platform
        implementation(platform("com.google.firebase:firebase-bom:33.4.0"))


        // Add the dependency for the Analytics library
        // When using the BoM, you don't specify versions in Firebase library dependencies
        implementation(libs.firebase.analytics)

        implementation("com.google.code.gson:gson:2.9.0")

        // Add the dependency for the Analytics library
        // When using the BoM, you don't specify versions in Firebase library dependencies

        implementation(libs.androidx.camera.camera2)
        implementation(libs.androidx.camera.lifecycle)
        implementation(libs.androidx.camera.view)
        implementation(libs.androidx.core.ktx)
        implementation(libs.nimbus.jose.jwt)
        implementation(libs.bcpkix.jdk18on)
        implementation(libs.jaxb.api)
        implementation(libs.secure.component.uat)
        implementation(libs.barcode.scanning)

    }

    hyperSdkPlugin {
        clientId = "dialmyca"
        sdkVersion = "2.1.32-rc.02"
    }
