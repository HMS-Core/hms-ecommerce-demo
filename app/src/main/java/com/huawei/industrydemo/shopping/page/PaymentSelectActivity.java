/*
    Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

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

package com.huawei.industrydemo.shopping.page;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.huawei.hms.mlplugin.card.bcr.MLBcrCapture;
import com.huawei.hms.mlplugin.card.bcr.MLBcrCaptureConfig;
import com.huawei.hms.mlplugin.card.bcr.MLBcrCaptureFactory;
import com.huawei.hms.mlplugin.card.bcr.MLBcrCaptureResult;
import com.huawei.industrydemo.shopping.MainActivity;
import com.huawei.industrydemo.shopping.R;

/**
 * Payment Succeeded Activity
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/28]
 * @see MainActivity
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class PaymentSelectActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = PaymentSelectActivity.class.getSimpleName();

    private TextView btnOhterPay;
    private TextView btnBcrPay;
    private int totalPrice;
    private int CAMERA_PERMISSION_CODE = 1;
    private int READ_EXTERNAL_STORAGE_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_select);
        initView();
        initAction();
        totalPrice = getIntent().getIntExtra("total_price", 0);
        ((TextView) findViewById(R.id.payment_total)).setText(getString(R.string.payment_need_total, totalPrice));

        if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
            this.requestCameraPermission();
        }
        if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            this.requestCameraPermission();
        }
    }

    private void initAction() {
        btnOhterPay.setOnClickListener(this);
        btnBcrPay.setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
    }

    private void initView() {
        TextView textTitle = findViewById(R.id.tv_title);
        textTitle.setText(R.string.payment_succeed);
        btnOhterPay = findViewById(R.id.other_payment);
        btnBcrPay = findViewById(R.id.bcr_payment);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bcr_payment:
                initDialog();
                break;
            case R.id.other_payment:
                startActivity(new Intent(this, PaymentSucceededActivity.class)
                        .putExtra("total_price", totalPrice));
                break;
            case R.id.iv_back:
                finish();
                break;
            default:
                break;
        }
    }

    private void initDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(PaymentSelectActivity.this);
        builder.setTitle(getApplicationContext().getResources().getString(R.string.payment_prompt));
        builder.setMessage(getApplicationContext().getResources().getString(R.string.payment_prompt_content));
        builder.setPositiveButton(getApplicationContext().getResources().getString(R.string.privacyaccept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startCaptureActivity(banCallback);
            }
        });
        builder.setNegativeButton(getApplicationContext().getResources().getString(R.string.privacyReject), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * Use the bank card pre-processing plug-in to identify video stream bank cards.
     * Create a recognition result callback function to process the identification result of the card.
     */
    private MLBcrCapture.Callback banCallback = new MLBcrCapture.Callback() {
        // Identify successful processing.
        @Override
        public void onSuccess(MLBcrCaptureResult bankCardResult) {
            Log.i(TAG, "CallBack onRecSuccess");
            if (bankCardResult == null) {
                Log.i(TAG, "CallBack onRecSuccess idCardResult is null");
                return;
            }
            startActivity(new Intent(PaymentSelectActivity.this, BcrAnalyseActivity.class)
                    .putExtra("total_price", totalPrice).putExtra("resultData",formatIdCardResult(bankCardResult)));
        }

        // User cancellation processing.
        @Override
        public void onCanceled() {
            Log.i(TAG, "CallBackonRecCanceled");
        }

        // Identify failure processing.
        @Override
        public void onFailure(int retCode, Bitmap bitmap) {
            Log.i(TAG, "CallBack onFailure retCode is "+retCode);
        }

        @Override
        public void onDenied() {
            Log.i(TAG, "CallBack onDenied ");
        }
    };

    /**
     * Set the recognition parameters, call the recognizer capture interface for recognition,
     * and the recognition result will be returned through the callback function.
     *
     * @param Callback The callback of band cards analyse.
     */
    private void startCaptureActivity(MLBcrCapture.Callback Callback) {
        MLBcrCaptureConfig config = new MLBcrCaptureConfig.Factory()
                // Set the expected result type of bank card recognition.
                // MLBcrCaptureConfig.SIMPLE_RESULT: Recognize only the card number and effective date.
                // MLBcrCaptureConfig.ALL_RESULT: Recognize information such as the card number, effective date, card issuing bank, card organization, and card type.
                .setResultType(MLBcrCaptureConfig.RESULT_ALL)
                // Set the screen orientation of the plugin page.
                // MLBcrCaptureConfig.ORIENTATION_AUTO: Adaptive mode, the display direction is determined by the physical sensor.
                // MLBcrCaptureConfig.ORIENTATION_LANDSCAPE: Horizontal screen.
                // MLBcrCaptureConfig.ORIENTATION_PORTRAIT: Vertical screen.
                .setOrientation(MLBcrCaptureConfig.ORIENTATION_AUTO)
                .create();
        MLBcrCapture bcrCapture = MLBcrCaptureFactory.getInstance().getBcrCapture(config);
        bcrCapture.captureFrame(this, Callback);
    }

    private String formatIdCardResult(MLBcrCaptureResult bankCardResult) {
        StringBuilder resultBuilder = new StringBuilder();

        resultBuilder.append("Number：");
        resultBuilder.append(bankCardResult.getNumber());
        resultBuilder.append(System.lineSeparator());

        resultBuilder.append("Issuer：");
        resultBuilder.append(bankCardResult.getIssuer());
        resultBuilder.append(System.lineSeparator());

        resultBuilder.append("Expire: ");
        resultBuilder.append(bankCardResult.getExpire());
        resultBuilder.append(System.lineSeparator());

        resultBuilder.append("Type: ");
        resultBuilder.append(bankCardResult.getType());
        resultBuilder.append(System.lineSeparator());

        resultBuilder.append("Organization: ");
        resultBuilder.append(bankCardResult.getOrganization());
        resultBuilder.append(System.lineSeparator());

        return resultBuilder.toString();
    }

    private void requestCameraPermission() {
        final String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, this.CAMERA_PERMISSION_CODE);
        }
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, permissions, this.READ_EXTERNAL_STORAGE_CODE);
        }
    }
}