# Init.d

Simple android application running scripts in /data/local/init.d as super-user.

## Usage

Build and install the application

```
./gradlew installDebug
```

Then, start Init.d from launcher, grant root permission.

Add shell scripts to `/data/local/init.d` and reboot.

### Compatibility

Init.d works on Android M (6.1) and above with compatibility to [new background execution model](https://developer.android.com/reference/android/app/job/JobScheduler)
introduced by Android Oreo (8.0).

Init.d requires `su` command with `find`/`xargs` under root shell.

## Debug

To ensure the scripts running, you can either look into `adb logcat` or add following script to `/data/local/init.d`:

```
setprop banana.initd.active 1
```

Then check if the flag is set after reboot:

```
adb shell "su -c 'getprop banana.initd.active'"
```

## License

(The MIT License)

