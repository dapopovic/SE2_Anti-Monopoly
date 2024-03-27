# WebSocket Android app demo project

This Android application connects to a basic WebSocket server to send and receive messages.

## Dependencies

- [OkHttp](https://square.github.io/okhttp/) - HTTP client for WebSocket communication.

## Requirements for Networking
- Internet Permission
```xml
<uses-permission android:name="android.permission.INTERNET" />
```
- Cleartext messages via network (cf. [here](https://stackoverflow.com/questions/45940861/android-8-cleartext-http-traffic-not-permitted))
```xml 
android:usesCleartextTraffic="true"
```



Find details on how to connect to the localhost machine from an Android emulator [here](https://developer.android.com/studio/run/emulator-networking).

