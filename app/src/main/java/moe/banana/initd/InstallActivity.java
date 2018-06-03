package moe.banana.initd;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class InstallActivity extends Activity {

    public static final String TAG = InstallActivity.class.getSimpleName();

    private boolean setupInitDir(Context context) throws IOException, InterruptedException {
        if (ShellService.DATA_ROOT.exists()) {
            File initDir = ShellService.INIT_DIR;
            ComponentName com = new ComponentName(context, InstallActivity.class);
            Process process;
            if (initDir.exists()) {
                process = Runtime.getRuntime().exec(
                        new String[] { "su", "-c", "touch " + initDir.getAbsolutePath() });
            } else {
                process = Runtime.getRuntime().exec(
                        new String[] { "su", "-c", "mkdir " + initDir.getAbsolutePath() });
            }
            process.waitFor();
            PackageManager pm = context.getPackageManager();
            if (initDir.exists() && initDir.isDirectory()) {
                switch (pm.getComponentEnabledSetting(com)) {
                    case PackageManager.COMPONENT_ENABLED_STATE_DEFAULT:
                    case PackageManager.COMPONENT_ENABLED_STATE_ENABLED:
                        pm.setComponentEnabledSetting(com,
                                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                        break;
                    case PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED:
                    case PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER:
                    case PackageManager.COMPONENT_ENABLED_STATE_DISABLED:
                        break;
                }
            }
            return process.exitValue() == 0;
        } else {
            Log.e(TAG, "cannot find " + ShellService.DATA_ROOT.getAbsolutePath());
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            if (setupInitDir(getApplicationContext())) {
                Toast.makeText(this, "init.d is initialized.", Toast.LENGTH_LONG).show();
                ShellService.startJob(getApplicationContext());
            } else {
                Toast.makeText(this, "init.d cannot be initialized.", Toast.LENGTH_LONG).show();
            }
        } catch (IOException|InterruptedException exception) {
            throw new RuntimeException(exception);
        }

        finish();
    }

}
