# MapsApp

Google Maps application built in **Kotlin**.

![](https://camo.githubusercontent.com/5f8e3380acd174df4d50a0a775642065ba60f00c49dfbd5a496882705dfd98d7/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f2d4b6f746c696e2d3030393564353f7374796c653d666f722d7468652d6261646765266c6f676f3d6b6f746c696e266c6f676f436f6c6f723d666666)

### Implementation

The application was developed in **Kotlin**, **MVVM** was used as the architectural pattern to decouple the user interface from the application logic, and the implementation also aims to adhere to the principles specified for **Clean Architecture**.

The application connects to Google Maps Utils (maps, directions, places), to run this app you must create a project and enable all required Google APIs and generate your own API Key.

Required APIs to run this app:

- Maps SDK for Android
- Directions API
- Places API

### How to set up?

#### Google Developers Console

First, you must create a project in Google Developers Console, enable the required APIs and generate your own API Key to use this services.

- Create project: https://developers.home.google.com/matter/project/create
- Enable Google APIs: https://support.google.com/googleapi/answer/6158841?hl=en
- Create your own API Key: https://support.google.com/googleapi/answer/6158862?hl=en

#### Android Studio

This project was created in the Android Studio latest version (Hedgehog) and enable Google Play Services.

#### Screenshots

<img src="https://github.com/hajc1294/MapsApp/assets/61942641/452ccb97-9fc6-4eba-a72e-0abba0bd1993" width="250">   <img src="https://github.com/hajc1294/MapsApp/assets/61942641/0ced1211-ccb6-468d-ad76-970c9894dbd1" width="250">   <img src="https://github.com/hajc1294/MapsApp/assets/61942641/70115510-aba5-40de-addf-a0cfd3dbbf59" width="250">

#### Documentation

Here some important links that can help you:

- ReactiveX: https://github.com/ReactiveX/RxAndroid
- Retrofit: https://square.github.io/retrofit/
- Room: https://developer.android.com/reference/androidx/room/package-summary
- Logo: https://looka.com/

#### Test

This project contains unit test for view models only.
