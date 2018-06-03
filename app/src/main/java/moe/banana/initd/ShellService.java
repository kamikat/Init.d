package moe.banana.initd;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class ShellService extends JobIntentService {

    public static final String TAG = ShellService.class.getSimpleName();

    public static final File DATA_ROOT = new File(Environment.getDataDirectory(), "local");
    public static final File INIT_DIR = new File(DATA_ROOT, "init.d");

    public static final int JOB_ID = 1;

    public static void startJob(Context context) {
        enqueueWork(context, ShellService.class, JOB_ID, new Intent(context, ShellService.class));
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.i(TAG, "handle " + intent.toString());
        if (DATA_ROOT.exists()) {
            if (INIT_DIR.exists() && INIT_DIR.isDirectory()) {
                try {
                    Process process = Runtime.getRuntime().exec(
                            new String[] { "su", "-c", "find " + INIT_DIR.getAbsolutePath() + " -type f | xargs -n1 sh" });
                    process.waitFor();
                    Log.i(TAG, "user init scripts exited with code " + process.exitValue());
                } catch (IOException|InterruptedException exception) {
                    Log.e(TAG, "failed to execute script.", exception);
                }
            } else {
                PackageManager pm = getPackageManager();
                ComponentName com = new ComponentName(this, InstallActivity.class);
                Log.w(TAG, "cannot find " + INIT_DIR.getAbsolutePath() + ", please run " + com.flattenToString() + " to setup.");
                if (PackageManager.COMPONENT_ENABLED_STATE_DISABLED == pm.getComponentEnabledSetting(com)) {
                    pm.setComponentEnabledSetting(com,
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                }
            }
        } else {
            Log.e(TAG, "cannot find " + DATA_ROOT.getAbsolutePath());
        }
    }

}
