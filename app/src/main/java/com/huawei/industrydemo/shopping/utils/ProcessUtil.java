
package com.huawei.industrydemo.shopping.utils;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/5/12]
 * @see [Related Classes/Methods]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class ProcessUtil {
    private static String currentProcessName;

    /**
     * Get current process
     *
     * @param context The application context
     * @return Current Process Name
     */
    @Nullable
    public static String getCurrentProcessName(@NonNull Context context) {
        if (!TextUtils.isEmpty(currentProcessName)) {
            return currentProcessName;
        }

        currentProcessName = getCurrentProcessNameByApplication();
        if (!TextUtils.isEmpty(currentProcessName)) {
            return currentProcessName;
        }

        currentProcessName = getCurrentProcessNameByActivityThread();
        if (!TextUtils.isEmpty(currentProcessName)) {
            return currentProcessName;
        }

        currentProcessName = getCurrentProcessNameByActivityManager(context);

        return currentProcessName;
    }

    private static String getCurrentProcessNameByApplication() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return Application.getProcessName();
        }
        return null;
    }

    private static String getCurrentProcessNameByActivityThread() {
        String processName = null;
        try {
            final Method declaredMethod =
                Class.forName("android.app.ActivityThread", false, Application.class.getClassLoader())
                    .getDeclaredMethod("currentProcessName", (Class<?>[]) new Class[0]);
            declaredMethod.setAccessible(true);
            final Object invoke = declaredMethod.invoke(null, new Object[0]);
            if (invoke instanceof String) {
                processName = (String) invoke;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return processName;
    }

    private static String getCurrentProcessNameByActivityManager(@NonNull Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            List<ActivityManager.RunningAppProcessInfo> runningAppList = am.getRunningAppProcesses();
            if (runningAppList != null) {
                for (ActivityManager.RunningAppProcessInfo processInfo : runningAppList) {
                    if (processInfo.pid == pid) {
                        return processInfo.processName;
                    }
                }
            }
        }
        return null;
    }
}
