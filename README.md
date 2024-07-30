# Android ChatBot with Octopus v2 - Function Calling Demo

This project is an Android-based chatbot application that showcases the capabilities of Octopus v2 in understanding natural language to determine which functions to call and what parameters to use. This demo app implements several functionalities to demonstrate the effectiveness of Octopus v2 in an Android environment.

Check out our demo video on YouTube:
[Android ChatBot with Octopus v2 Demo](https://www.youtube.com/watch?v=tHQVVVZQzOM&list=PL4l1nVUEj_knXRu2k_Df35RwWYLnywZJ4&index=7)

## Features

- Android-based chat interface
- Integration with the Octopus v2 model
- Natural language processing for function calling and parameter determination
- HTTP requests for retrieving model outputs
- Demo functionalities to showcase Octopus v2's capabilities

## How It Works

This application extends the original Android-ChatBot project by incorporating the Octopus v2 model. The app processes user input to intelligently decide which functions to call and what parameters to use, demonstrating advanced language understanding. It sends these decisions to the Octopus model via HTTP requests and displays the generated responses in the chat interface.

## Demo Purpose

This app is designed as a demonstration to showcase the potential of Octopus v2 in Android applications. The implemented functionalities serve as examples of how natural language can be used to interact with various app features.

## APK Installation

To install the demonstration application on your Android device, follow these steps:

1. Download the APK:

   - Visit this link on your computer: [Download APK](https://public-storage.nexa4ai.com/android-demo-release/app-release.apk)
   - Save the APK file to a known location on your computer

2. Enable USB debugging on your Android device:

   - Go to Settings > About phone
   - Tap "Build number" seven times to enable Developer options
   - Go back to Settings > System > Developer options
   - Enable "USB debugging"

3. Connect your Android device to your computer using a USB cable

4. Open a command prompt or terminal on your computer

5. Use ADB to install the APK:

   - Ensure ADB is installed on your computer. If not, download and install Android SDK platform tools
   - Navigate to the directory containing the APK file
   - Run the following command:
     ```
     adb install app-release.apk
     ```

6. Wait for the installation to complete. You should see a success message in the terminal

7. The app should now be installed on your Android device. You can find it in your app drawer

Note: If you encounter any issues during installation, make sure your device is properly connected and recognized by ADB. You can check this by running `adb devices` in the terminal.

## Acknowledgements

This project is based on the [Android-ChatBot](https://github.com/gangulwar/Android-ChatBot) repository by gangulwar. We extend our gratitude for the initial codebase that made this project possible.
