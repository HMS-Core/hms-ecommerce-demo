/*
    Copyright 2020-2021. Huawei Technologies Co., Ltd. All rights reserved.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.huawei.industrydemo.shopping.utils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.huawei.agconnect.cloud.storage.core.AGCStorageManagement;
import com.huawei.agconnect.cloud.storage.core.DownloadTask;
import com.huawei.agconnect.cloud.storage.core.FileMetadata;
import com.huawei.agconnect.cloud.storage.core.StorageException;
import com.huawei.agconnect.cloud.storage.core.StorageReference;
import com.huawei.agconnect.cloud.storage.core.UploadTask;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.agconnect.crash.AGConnectCrash;
import com.huawei.agconnect.remoteconfig.AGConnectConfig;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseDialog;

import java.io.File;
import java.text.NumberFormat;

import static com.huawei.industrydemo.shopping.base.BaseDialog.CONTENT;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/3/27]
 * @see [Related Classes/Methods]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class AgcUtil {
    private static final String TAG = AgcUtil.class.getSimpleName();

    private static String appId;

    private static String apiKey;

    private static volatile AGConnectConfig config;

    private static volatile AGCStorageManagement storageManagement;

    public static synchronized AGConnectConfig getConfig() {
        if (config == null) {
            config = AGConnectConfig.getInstance();
        }
        return config;
    }

    public static synchronized AGCStorageManagement getStorageManagement() {
        if (storageManagement == null) {
            storageManagement = AGCStorageManagement.getInstance();
        }
        return storageManagement;
    }

    /**
     * Obtain App Id.
     *
     * @param context context
     * @return appId
     */
    public static synchronized String getAppId(Context context) {
        if (appId == null) {
            appId = AGConnectServicesConfig.fromContext(context).getString("client/app_id");
        }
        return appId;
    }

    /**
     * Obtain Api Key.
     *
     * @param context context
     * @return apiKey
     */
    public static synchronized String getApiKey(Context context) {
        if (apiKey == null) {
            apiKey = AGConnectServicesConfig.fromContext(context).getString("client/api_key");
        }
        return apiKey;
    }

    /**
     * Downloading files from cloud storage
     * 
     * @param context Context
     * @param sourcePath String
     * @param progressDialog ProgressDialog
     * @param onSuccessListener OnSuccessListener<Object>
     */
    @SuppressLint("WrongConstant")
    public static void downloadFile(Context context, String sourcePath, ProgressDialog progressDialog,
        @NonNull OnSuccessListener<Object> onSuccessListener) {
        Log.d(TAG, sourcePath);
        File targetFile =
            new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + sourcePath);
        Log.d(TAG, targetFile.getPath());
        StorageReference reference = getStorageManagement().getStorageReference(sourcePath);
        DownloadTask downloadTask = reference.getFile(targetFile);
        downloadTask.addOnFailureListener(exception -> {
            reportException(TAG, exception);
            if (StorageException.fromException(exception)
                .getErrorCode() == StorageException.ERROR_RANGE_UNSATISFIABLE) {
                onSuccessListener.onSuccess(new Object());
            } else {
                progressDialog.dismiss();
                Bundle data = new Bundle();
                data.putString(CONTENT, context.getString(R.string.download_failed));
                BaseDialog dialog = new BaseDialog(context, data, false);
                dialog.show();
            }
        }).addOnSuccessListener(downloadResult -> {
            Log.d(TAG, String.valueOf(downloadResult.getTotalByteCount()));
            onSuccessListener.onSuccess(downloadResult);
        }).addOnProgressListener(downloadResult -> {
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressNumberFormat("%1d/%2d");
            progressDialog.setProgressPercentFormat(NumberFormat.getPercentInstance());
            int value = (int) (downloadResult.getBytesTransferred() * 100 / downloadResult.getTotalByteCount());
            progressDialog.setProgress(value);
        }).addOnCompleteListener(task -> {
            progressDialog.incrementSecondaryProgressBy(25);
        });
    }

    /**
     * upload file to cloud storage
     * 
     * @param context Context
     * @param objectPath String
     * @param sha256Hash String
     */
    public static void uploadFile(Context context, String objectPath, String sha256Hash) {
        StorageReference reference = getStorageManagement().getStorageReference(objectPath);
        FileMetadata attribute = new FileMetadata();
        attribute.setSHA256Hash(sha256Hash);
        Log.d(TAG, sha256Hash);
        String rootPath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath();
        File targetFile = new File(rootPath + File.separator + objectPath);
        UploadTask task = reference.putFile(targetFile, attribute);
        task.addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                FileMetadata metadata = task1.getResult().getMetadata();
                Log.d(TAG, metadata.getName() + ": " + metadata.getSHA256Hash());
            } else {
                reportException(TAG, task1.getException());
            }
        });
    }

    /**
     * Report Exception to cloud
     *
     * @param tag TAG of the function
     * @param throwable Throwable exception
     */
    public static void reportException(String tag, Throwable throwable) {
        Log.e(tag, throwable.getMessage(), throwable);
        AGConnectCrash.getInstance().recordException(throwable);
    }

    /**
     * Report Failure to cloud
     *
     * @param tag TAG of the function
     * @param failureMsg failure message
     */
    public static void reportFailure(String tag, String failureMsg) {
        Log.w(tag, failureMsg);
        AGConnectCrash.getInstance().log(Log.WARN, failureMsg);
    }
}
