# Android IPC Test

Tests latency 3 approaches of inter-process communication on Android between two separate apps.
The provider runs a `ForegroundService` and calls a native method. The measured latency includes the latency of JNI (< 100us)
The average results are given below, when tested on Android 11.
* `BroadcastReceiver` (Intent): 3000-6000 us (usually ~3000 us)
* `DatagramSocket` (UDP): 500-4000 us (usually ~1200 us)
* `BoundService` (AIDL): 300-800 us (usually ~500 us)

AIDL gives the best latency as expected.
