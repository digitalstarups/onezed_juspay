package com.onezed.Activity;

import static java.lang.System.in;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

//HyperServices imports
import java.util.UUID;



public class IntegrationReferenceActivityDirectPSP extends AppCompatActivity {
//    public Helpers helper;
//    private JSONObject processData;
//    private JSONObject initiateData;
//    private JSONObject merchantData;
//    private boolean hasProcessInQueue;
//    private HyperServiceHolder hyperServicesHolder;

    /*******************Integration reference start*****************************/
    //HyperSDK instance used for all operation on the sdk.
    //code-copy-marker

    @Override
    protected void onStart() {
        super.onStart();
        //block:start:create-hyper-services-instance

        //hyperServicesHolder = new HyperServiceHolder(this);

        //block:end:create-hyper-services-instance
        initiateUpiSDK();
        //hyperServicesHolder.setCallback(createHyperPaymentsCallbackAdapter());
    }

    //block:start:initiate-sdk
    //This function initiate the Juspay SDK
    private void initiateUpiSDK() {
//        if(!hyperServicesHolder.isInitialised()){
//            initiatePayload = createInitiatePayload();
//            hyperServicesHolder.initiate(initiatePayload);
//        }
    }
    //block:end:initiate-sdk

    //block:start:create-initiate-payload
    // This function creates intiate payload.
    private JSONObject createInitiatePayload() {
        JSONObject sdkPayload = new JSONObject();
        JSONObject innerPayload = new JSONObject();
        JSONObject signaturePayload = new JSONObject();
        try {
            // generating inner payload
            innerPayload.put("action", "initiate");
            innerPayload.put("merchantKeyId", "<MERCHANT_KEY_ID>");   // Put your Merchant ID here
            innerPayload.put("clientId", "CLIENT_ID");                // Put your Client ID here
            innerPayload.put("environment", "production");
            innerPayload.put("issuingPsp", "YES_BIZ");
            innerPayload.put("signature", "....");                    // Generated signature for signature payload
            innerPayload.put("signaturePayload", signaturePayload);

            // block:start:generatingRequestId
            sdkPayload.put("requestId",  ""+ UUID.randomUUID());
            // block:end:generatingRequestId
            sdkPayload.put("service", "in.juspay.hyperapi");
            sdkPayload.put("payload", innerPayload);

            signaturePayload.put("merchant_id", "<MERCHANT_ID>");     // Put your Merchant ID here
            signaturePayload.put("customer_id", "<CUSTOMER_ID>");     // Put Customer ID here
            signaturePayload.put("timestamp", "<timestamp>");         // Time when request is created in milliseconds.

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sdkPayload;
    }
    //block:end:create-initiate-payload

//    // block:start:create-hyper-callback
//    private HyperPaymentsCallbackAdapter createHyperPaymentsCallbackAdapter() {
//        return new HyperPaymentsCallbackAdapter() {
//            @Override
//            public void onEvent(JSONObject data, JuspayResponseHandler handler) {
//                try {
//                    // block:start:handle-sdk-response
//                    String event = data.getString("event");
//
//                    // block:start:show-loader
//                    if (event.equals("show_loader")) {
//                        // Show some loader here
//                    }
//                    // block:end:show-loader
//
//                    // block:start:hide-loader
//                    else if (event.equals("hide_loader")) {
//                        // Hide Loader
//                    }
//                    // block:end:hide-loader
//
//                    // block:start:initiate-result
//                    else if (event.equals("initiate_result")) {
//                        // Get the response
//                        JSONObject response = data.optJSONObject("payload");
//                    }
//                    // block:end:initiate-result
//
//                    // block:start:process-result
//                    else if (event.equals("process_result")) {
//                        // Get the response
//                        JSONObject response = data.optJSONObject("payload");
//                        //Merchant handling
//                    }
//                    // block:end:process-result
//
//                    // block:start:log-stream
//                    else if (event.equals("log_stream")){
//                        Log.i("=>Clickstream", data.toString());
//                    }
//                    // block:end:log-stream
//
//                    // block:start:session-expiry
//                    else if (event.equals("session_expiry")){
//                        // Handle Session Expiry
//                    }
//                    // block:end:session-expiry
//
//                } catch (Exception e) {
//                    // merchant code...
//                }
//                // block:end:handle-sdk-response
//            }
//        };
//    }
//    // block:end:create-hyper-callback

    public void callProcess() {
        // block:start:process-sdk

        JSONObject processPayload = new JSONObject();
        JSONObject innerPayload = new JSONObject();

        try {
            // generating inner payload
            innerPayload.put("action", "upiCheckPermission");

            processPayload.put("requestId",  ""+ UUID.randomUUID());
            processPayload.put("service", "in.juspay.hyperapi");
            processPayload.put("payload", innerPayload);

        } catch (Exception e) {
            e.printStackTrace();
        }

//        if (hyperServicesHolder.isInitialised()) {
//            hyperServicesHolder.process(processPayload);
//        }
        // block:end:process-sdk

    }
    }
//    // block:start:terminate
//    public void callTerminate() {
//        hyperServicesHolder.terminate();
//    }
//    // block:start:terminate

    //block:start:onBackPressed
//    @Override
//    public void onBackPressed() {
//        boolean handleBackpress = hyperServicesHolder.onBackPressed();
//        if(!handleBackpress) {
//            super.onBackPressed();
//        }
//    }
//    //block:end:onBackPressed
//
//    // block:start:onActivityResult
//    @Override
//    public void onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//
//        // If super.onActivityResult is available use following:
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // In case super.onActivityResult is NOT available please use following:
//        if (data != null) {
//            hyperServices.onActivityResult(requestCode, resultCode, data);
//        }
//    }
//
//    // block:end:onActivityResult
//
//    //block:start:onRequestPermissionsResult
//    @Override
//    public void onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//
//        // If super.onRequestPermissionsResult is available use following:
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        // In case super.onActivityResult is NOT available please use following:
//        hyperServicesHolder.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//    //block:end:onRequestPermissionsResult
//}