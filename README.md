# My Pass

**My Pass** is a streamlined offline-first trip planner application designed to keep all your travel information, itineraries, and boarding passes in a single, endlessly scrollable timeline list. Experience a distraction-free, beautifully curated dark-mode interface that gives you quick access to your upcoming and in-progress events without needing an active internet connection.

This project was generated and rapidly prototyped using **Google AI Studio**, demonstrating how artificial intelligence can dramatically accelerate modern Android application development.

## 🚀 Features

- **Unified Itinerary Timeline:** View flights, hotel reservations, notes, and links in a single continuous timeline.
- **Offline Reliability:** Fully functional offline state management using Room Database caching. Your trip details are always available.
- **Beautiful Dark Mode Interface:** Designed with deeply immersive aesthetics, high-contrast indicators, and Jetpack Compose responsive styling.
- **Automated Workflow:** Built-in GitHub Actions CI/CD pipeline to automatically compile development APKs for quick device side-loading.

## 🛠 Technology Stack

This application is built with modern native Android capabilities out-of-the-box:
- **Language:** Kotlin
- **UI Toolkit:** Jetpack Compose (Material Design 3)
- **Architecture:** Clean Architecture & MVVM (Model-View-ViewModel)
- **Asynchronous Data:** Kotlin Coroutines & Flow
- **Local Persistence:** Room Database for robust local caching

## 🤖 Accelerated by Google AI Studio

This application serves as a testament to the capabilities of **Google AI Studio** and agent-assisted development.
The fundamental components of this app—from setting up the Room database schema and Coroutines Flow repositories to generating the heavily-styled aesthetic timeline views—were built dynamically via natural language prompts.
By utilizing AI Studio's agentic workspace, boilerplate reduction was immediate, letting the focus remain entirely on product flow, data structure, and UI/UX design. 

## 📦 How to Download the App (Via GitHub)

This repository includes a GitHub Action workflow that automatically builds the application's Android APK and attaching it as an artifact.

If you don't have Android Studio installed and just want to test the app on your phone:
1. Navigate to the **Actions** tab of this GitHub repository.
2. Click on the latest workflow run on the `main` branch.
3. Scroll down to the **Artifacts** section at the bottom of the right-hand panel.
4. Download the `my-pass-app-debug.apk` file.
5. Transfer the APK to your Android device and install it (ensure 'Install from unknown sources' is permitted).

*(Additionally, if you create a Release tag starting with `v` like `v1.0.0`, GitHub Actions will automatically publish the APK to the repository's Releases page!)*

## 💻 Development Setup

If you prefer to compile the application locally:
1. Clone this repository.
2. Open the project in **Android Studio** (Koala or newer recommended).
3. Let Gradle sync and resolve dependencies.
4. Run the app on a physical device or emulator running Android 14+ (Min SDK 34).

## 📄 License
This project is open-source and available under the MIT License.
