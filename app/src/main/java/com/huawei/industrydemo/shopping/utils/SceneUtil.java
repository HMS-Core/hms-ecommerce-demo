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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.industrydemo.shopping.R;
import com.huawei.industrydemo.shopping.base.BaseActivity;
import com.huawei.industrydemo.shopping.base.BaseDialog;
import com.huawei.industrydemo.shopping.constants.KeyConstants;
import com.huawei.industrydemo.shopping.entity.Product;
import com.huawei.industrydemo.shopping.page.SceneViewActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static com.huawei.industrydemo.shopping.base.BaseDialog.CONTENT;
import static com.huawei.industrydemo.shopping.utils.AgcUtil.uploadFile;

/**
 * @version [Ecommerce-Demo 1.0.2.300, 2021/5/12]
 * @see [Related Classes/Methods]
 * @since [Ecommerce-Demo 1.0.2.300]
 */
public class SceneUtil {
    private static final String TAG = SceneUtil.class.getSimpleName();

    private static final String MAC_IGNORE = "__MACOSX/";

    /**
     * Scene model folder
     */
    public static final String SCENE_FOLDER = "SceneView";

    private static final String[] BDG_MODEL_NAMES = {"bgd_model_1.dds", "bgd_model_2.dds", "bgd_model_3.dds"};

    private static final boolean IS_PREPARING = false;

    private static final Map<String, String> SCENE_RESOURCES = new HashMap<String, String>() {
        {
            put("bgd_model_1.dds", "6CF9F3254F3B27B707C680258F6742B913A5AB99D485C0533748428C2FEC2C3A");
            put("bgd_model_2.dds", "470CADFA1B6BC30498FBB6796AA00A8B27C267F22516F145497191FF5A2DAD24");
            put("bgd_model_3.dds", "D3D68C2A7F1BF2E83ED5827FEF83DA7954D4DB504AE342190EF2E8EA7C72136A");
            put("p40.zip", "53F15B1DB7CE87B589B90ADCE64F0EE29F5A998A9D1A0A942003B4CD0CBF3E23");
            put("handbag.zip", "B18487E7288EBD7F78E6F43892CC9EEF33B9B3A44B2DCF597BC13A8FEF94E495");
            put("sunglasses.zip", "3020D7813A645E562A54F5CC88CAEDDBEA7B53C4C3F8667AFC169B16DEFD971A");
        }
    };

    /**
     * download 3d Resources
     * 
     * @param context Context
     * @param product Product
     */
    public static void download3dResources(Context context, Product product) {
        String rootPath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath();
        Log.d(TAG, "rootPath: " + rootPath);
        File sceneDir = new File(rootPath + File.separator + SCENE_FOLDER);
        if (!sceneDir.exists()) {
            if (!sceneDir.mkdir()) {
                Log.e(TAG, "mkdir error!");
                return;
            }
        }
        if (IS_PREPARING) {
            preparingCloudResources(context);
        } else {
            ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(context.getString(R.string.downloading));
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setProgress(0);
            progressDialog.setIndeterminate(true);
            progressDialog.setProgressNumberFormat(null);
            progressDialog.setProgressPercentFormat(null);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();

            downloadInSequence(context, progressDialog,
                o -> downloadKeyResources(context, progressDialog, rootPath, product));
        }
    }

    private static void downloadKeyResources(Context context, ProgressDialog progressDialog, String rootPath,
        Product product) {
        String threeDimensional = product.getThreeDimensional();
        String targetFilePath =
            SCENE_FOLDER + File.separator + threeDimensional.substring(0, threeDimensional.lastIndexOf("/")) + ".zip";
        File targetFile = new File(rootPath + File.separator + targetFilePath);
        Log.d(TAG, targetFile.getPath());
        AgcUtil.downloadFile(context, targetFilePath, progressDialog, downloadResult -> {
            decompressFile(targetFile.getParent(), targetFile);
            progressDialog.dismiss();
            Bundle data = new Bundle();
            data.putString(CONTENT, context.getString(R.string.downloaded));
            BaseDialog dialog = new BaseDialog(context, data, false);
            dialog.setConfirmListener(v -> {
                dialog.dismiss();
                show3dModel(context, product);
            });
            dialog.show();
        });
    }

    private static void downloadInSequence(Context context, ProgressDialog progressDialog,
        OnSuccessListener<Object> onSuccessListener) {
        String bdgModelPath1 = SCENE_FOLDER + File.separator + BDG_MODEL_NAMES[0];
        AgcUtil.downloadFile(context, bdgModelPath1, progressDialog, downloadResult -> {
            String bdgModelPath2 = SCENE_FOLDER + File.separator + BDG_MODEL_NAMES[1];
            AgcUtil.downloadFile(context, bdgModelPath2, progressDialog, downloadResult1 -> {
                String bdgModelPath3 = SCENE_FOLDER + File.separator + BDG_MODEL_NAMES[2];
                AgcUtil.downloadFile(context, bdgModelPath3, progressDialog, downloadResult2 -> {
                    onSuccessListener.onSuccess(new Object());
                });
            });
        });
    }

    /**
     * Only files uploaded using code can carry the SHA256 value for resumable download.
     * 
     * @param context Context
     */
    private static void preparingCloudResources(Context context) {
        Set<Map.Entry<String, String>> entries = SCENE_RESOURCES.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            uploadFile(context, SCENE_FOLDER + File.separator + entry.getKey(), entry.getValue());
        }
    }

    private static void show3dModel(Context context, Product product) {
        if (context instanceof BaseActivity) {
            Intent intent3d = new Intent(context, SceneViewActivity.class);
            intent3d.putExtra(KeyConstants.THREE_DIMENSIONAL_DATA, product.getThreeDimensional());
            context.startActivity(intent3d);
            AnalyticsUtil.reportProductViewEvent("AR View", product);
        }
    }

    /**
     * decompress File
     *
     * @param target String
     * @param file File
     */
    private static void decompressFile(String target, File file) {
        if (TextUtils.isEmpty(target) || !file.exists()) {
            return;
        }
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
            ZipFile zipFile = new ZipFile(file)) {
            ZipEntry zipEntry = null;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String fileName = zipEntry.getName();
                if (fileName != null && fileName.contains(MAC_IGNORE)) {
                    continue;
                }
                File temp = new File(target + File.separator + fileName);
                if (zipEntry.isDirectory()) {
                    File dir = new File(target + File.separator + fileName);
                    if (!dir.mkdirs()) {
                        Log.e(TAG, "mkdirs error!");
                        return;
                    }
                    continue;
                }
                if (temp.getParentFile() != null && !temp.getParentFile().exists()) {
                    if (!temp.getParentFile().mkdirs()) {
                        Log.e(TAG, "mkdirs error!");
                        return;
                    }
                }
                byte[] buffer = new byte[1024];
                try (OutputStream os = new FileOutputStream(temp); InputStream is = zipFile.getInputStream(zipEntry)) {
                    int len = 0;
                    while ((len = is.read(buffer)) != -1) {
                        os.write(buffer, 0, len);
                    }
                }
            }
        } catch (IOException e) {
            AgcUtil.reportException(TAG, e);
        }
    }
}
