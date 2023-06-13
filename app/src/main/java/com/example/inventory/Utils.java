package com.example.inventory;

import android.app.Activity;
import com.google.zxing.integration.android.IntentIntegrator;

public class Utils {

    public void scanner(Activity context) {

        IntentIntegrator intentIntegrator = new IntentIntegrator(context);
        intentIntegrator.setPrompt("Наведіть на штрих-код.");
        intentIntegrator.setCaptureActivity(BarcodeActivity.class);
        intentIntegrator.setCameraId(0);
        intentIntegrator.initiateScan();
    }
}
