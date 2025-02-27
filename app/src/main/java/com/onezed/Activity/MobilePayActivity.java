package com.onezed.Activity;

import static com.onezed.GlobalVariable.baseUrl.getOrderIdAmount;
import static com.onezed.GlobalVariable.baseUrl.prepaidRecharge;
import static com.onezed.GlobalVariable.baseUrl.verifyPayment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.onezed.ExeDec;
import com.onezed.GlobalVariable.AlarmHelper;
import com.onezed.GlobalVariable.CustomAlertDialog;
import com.onezed.GlobalVariable.GlobalVariable;
import com.onezed.GlobalVariable.HttpHandler;
import com.onezed.GlobalVariable.NotificationHelper;
import com.onezed.GlobalVariable.SharedPrefManager;
import com.onezed.GlobalVariable.baseUrl;
import com.onezed.Model.UserProfileModel;
import com.onezed.R;
import com.onezed.databinding.ActivityMobilePayBinding;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class MobilePayActivity extends AppCompatActivity implements PaymentResultWithDataListener {

    private ActivityMobilePayBinding binding;
    String statusMessage;
    int finalAmount,userId;
    SharedPrefManager sharedPrefManager;
    String payId,spKey,accountMobileNo,userMobileNo,amount;
    String billPayCustomerId="NA",billPayAmount="NA",billPayDescription="NA",billPayStatus="NA",paymentId;
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this, "Hi Soumen", Toast.LENGTH_SHORT).show();
        binding = ActivityMobilePayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPrefManager = new SharedPrefManager(MobilePayActivity.this);
        //profileApiCall();
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        userId=sharedPrefManager.getUserId();

        amount=GlobalVariable.Rs;
        spKey=GlobalVariable.OperatorCode;
        accountMobileNo=GlobalVariable.RechargeNumber;

        billPayAmount=GlobalVariable.Rs;
        Log.v("dataStart",userId+" "+amount+" "+spKey+" "+accountMobileNo+" "+billPayAmount);
        startPayment();
    }


    private void startPayment() {

        JSONObject joObj = new JSONObject();
        try {
            joObj.put("phone",UserProfileModel.getInstance().getMobileNo());
            joObj.put("name",UserProfileModel.getInstance().getName() );
            joObj.put("email",UserProfileModel.getInstance().getEmail() );
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");


        finalAmount = (int) (Double.parseDouble(GlobalVariable.Rs) * 100);

        ExeDec goia=new ExeDec(getOrderIdAmount);

        String url = String.format(goia.getDec()+"%s",finalAmount);

        new HttpHandler(url, MobilePayActivity.this)
                .setMediaType(HttpHandler.jsonMediaType)
                .setRequestType(HttpHandler.RequestType.POST)
                .setStringData(joObj.toString())
                .setLoading(true)
                .setHeaders(headers)
                .setOnResponse(new HttpHandler.HttpResponse() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        CustomAlertDialog dialog = new CustomAlertDialog(MobilePayActivity.this, R.style.WideDialog, getString(R.string.app_name), getString(R.string.no_response));
                        dialog.show();
                        dialog.onPositiveButton(view ->{
                            dialog.dismiss();
                            Intent intent = new Intent(MobilePayActivity.this, RechargeActivity.class);
                            intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
                            startActivity(intent);
                        } );
                    }

                    @Override
                    public void onResponse(int code, String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            switch (code) {
                                case 200:
                                    if (jsonObject.has("order_id")) {
                                        String orderId = jsonObject.getString("order_id");


                                        OpenRezerPay(orderId);
                                        GlobalVariable.orderId = orderId;
                                    }
                                    else {
                                        CustomAlertDialog dialog = new CustomAlertDialog(MobilePayActivity.this, R.style.WideDialog, "OneZed", "Not getting OId in start Payment Response, Please Try after some times");
                                        dialog.show();
                                        dialog.onPositiveButton(view ->{
                                            dialog.dismiss();
                                            Intent intent = new Intent(MobilePayActivity.this, RechargeActivity.class);
                                            intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
                                            startActivity(intent);
                                        });
                                    }

                                    break;
                                default:
                                    CustomAlertDialog dialog = new CustomAlertDialog(MobilePayActivity.this, R.style.WideDialog, "OneZed", "Error Occur in start Payment Response code, Please Try after some times");
                                    dialog.show();
                                    dialog.onPositiveButton(view ->{
                                        dialog.dismiss();
                                        Intent intent = new Intent(MobilePayActivity.this, RechargeActivity.class);
                                        intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
                                        startActivity(intent);
                                    });
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }).sendRequest();
    }

    private void OpenRezerPay(String orderId) {

        Checkout checkout = new Checkout();

        // checkout.setKeyID("rzp_test_4aGf1rnJhy8N21"); //TEST KEY
        checkout.setKeyID("rzp_live_2dr4ep6PhAKG3S"); // LIVE KEY

        try {
            // Create payment options
            JSONObject options = new JSONObject();
            options.put("name", UserProfileModel.getInstance().getName()); // Name of the person making the payment
            options.put("description", "MobilePrePaidRecharge"); // Order description
            options.put("currency", "INR"); // Currency code
            options.put("order_id", orderId);
            options.put("amount", finalAmount); // Amount in paise (1 INR = 100 paise)
            options.put("prefill.email", UserProfileModel.getInstance().getEmail()); // User's email
            options.put("prefill.contact", UserProfileModel.getInstance().getMobileNo()); // User's phone number



            // Open Razorpay checkout
            checkout.open(MobilePayActivity.this, options);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        // Log the success message

        // Retrieve details from the PaymentData object
        String razorpayPaymentId = paymentData.getPaymentId(); // Payment ID
        String razorpayOrderId = paymentData.getOrderId(); // Order ID
        String razorpaySignature = paymentData.getSignature(); // Signature
        paymentId=razorpayPaymentId;
        verifyPayment(razorpayPaymentId,razorpayOrderId,razorpaySignature);

        payId=razorpayPaymentId;


        // You can also show this information in a Toast
        // Toast.makeText(this, "Payment Success! Payment ID: " + razorpayPaymentId, Toast.LENGTH_LONG).show();
    }
    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {


        View v = getLayoutInflater().inflate(R.layout.payment_failed_layout, null);
        setContentView(v);
        Button back=v.findViewById(R.id.back_button);
        ImageView gif=v.findViewById(R.id.status_gif);
        Glide.with(this)
                .asGif()
                .load(R.drawable.payment_failed) // Replace with your GIF file in res/drawable
                .into(gif);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MobilePayActivity.this, RechargeActivity.class);
                intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
                startActivity(intent);
            }
        });

    }
    private void verifyPayment(String payId,String orderId,String sigId) {

        JSONObject joObj = new JSONObject();
        try {
            joObj.put("payment_id",payId);
            joObj.put("order_id",orderId );
            joObj.put("razorpay_signature",sigId );
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        ExeDec vp=new ExeDec(verifyPayment);

        new HttpHandler(vp.getDec(), MobilePayActivity.this)
                .setMediaType(HttpHandler.jsonMediaType)
                .setRequestType(HttpHandler.RequestType.POST)
                .setStringData(joObj.toString())
                .setLoading(true)
                .setHeaders(headers)
                .setOnResponse(new HttpHandler.HttpResponse() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        CustomAlertDialog dialog = new CustomAlertDialog(MobilePayActivity.this, R.style.WideDialog, getString(R.string.app_name), getString(R.string.no_response));
                        dialog.show();
                        dialog.onPositiveButton(view -> dialog.dismiss());
                    }

                    @Override
                    public void onResponse(int code, String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            switch (code) {
                                case 200:


                                    String status=jsonObject.getString("status");
                                    if (jsonObject.has("message")){
                                        statusMessage= jsonObject.getString("message");
                                    }

                                    if (status.equals("success")){

                                        rechargeApiCall();
                                    }
                                    else {
                                        // rechargeApiCall();
                                        View v = getLayoutInflater().inflate(R.layout.payment_failed_layout, null);
                                        setContentView(v);
                                        Button back=v.findViewById(R.id.back_button);
                                        TextView msgTxt=v.findViewById(R.id.failed_msg);
                                        msgTxt.setText(statusMessage);
                                        ImageView gif=v.findViewById(R.id.status_gif);

                                        Glide.with(MobilePayActivity.this)
                                                .asGif()
                                                .load(R.drawable.payment_failed) // Replace with your GIF file in res/drawable
                                                .into(gif);

                                        back.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(MobilePayActivity.this, RechargeActivity.class);
                                                intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
                                                startActivity(intent);
                                            }
                                        });
                                    }


                                    break;
                                default:
                                    CustomAlertDialog dialog = new CustomAlertDialog(MobilePayActivity.this, R.style.WideDialog, "OneZed", "Getting some error, If money be deducted from your account will be refund within two business day.");
                                    dialog.show();
                                    dialog.onPositiveButton(view -> dialog.dismiss());
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }).sendRequest();
    }

    private void rechargeApiCall() {

        JSONObject joObj = new JSONObject();
        try {
            joObj.put("account",accountMobileNo);
            joObj.put("amount",amount );
            joObj.put("spkey", spKey);
            joObj.put("payment_id", payId);
            joObj.put("paymentByUser", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        ExeDec pr=new ExeDec(prepaidRecharge);

        new HttpHandler(pr.getDec(), MobilePayActivity.this)
                .setMediaType(HttpHandler.jsonMediaType)
                .setRequestType(HttpHandler.RequestType.POST)
                .setStringData(joObj.toString())
                .setLoading(true)
                .setHeaders(headers)
                .setOnResponse(new HttpHandler.HttpResponse() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        CustomAlertDialog dialog = new CustomAlertDialog(MobilePayActivity.this, R.style.WideDialog, getString(R.string.app_name), getString(R.string.no_response));
                        dialog.show();
                        dialog.onPositiveButton(view -> dialog.dismiss());
                    }

                    @Override
                    public void onResponse(int code, String response) {
                        Log.v("PaymentResponse ",code+" "+response);
                        billCreateApiCall();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            switch (code) {
                                case 200:

                                                    String message = "Payment Successful";
                                                    //startActivity(new Intent(MobilePayActivity.this,HomeActivity.class));
                                                    View viewSuccess = getLayoutInflater().inflate(R.layout.payment_success_layout, null);
                                                    setContentView(viewSuccess);
                                                    Button backSuccess = viewSuccess.findViewById(R.id.back_button_success);
                                                    TextView msgTxtSuccess = viewSuccess.findViewById(R.id.success_msg);
                                                    msgTxtSuccess.setText(message);

                                                    ImageView gifSuccess = viewSuccess.findViewById(R.id.status_gif);

                                                    Glide.with(MobilePayActivity.this)
                                                            .asGif()
                                                            .load(R.drawable.payment_success) // Replace with your GIF file in res/drawable
                                                            .into(gifSuccess);

                                                    backSuccess.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            Intent intent = new Intent(MobilePayActivity.this, RechargeActivity.class);
                                                            intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
                                                            startActivity(intent);
                                                        }
                                                    });



                                    break;



//                                    if (jsonObject.has("status")){
//
//                                        boolean status=jsonObject.getBoolean("status");
//                                        if (jsonObject.has("data")) {
//
//                                            JSONObject obj = jsonObject.getJSONObject("data");
//                                            if (obj.has("account")){
//                                                billPayCustomerId=obj.getString("account");
//                                            }
//                                            if (obj.has("amount")){
//                                                billPayAmount=obj.getString("amount");
//                                            }
//                                            if (obj.has("message")){
//                                                billPayStatus=jsonObject.getString("message");
//                                            }
//                                            if (obj.has("msg")){
//                                                billPayDescription=obj.getString("msg");
//                                            }
//                                            if (status) {
//
//                                                String errorcode = obj.getString("errorcode");
//                                                if (errorcode == "200" || errorcode.equals("200")) {
//                                                    GlobalVariable.LocalNotificationText="Mobile Recharge in "+accountMobileNo;
//                                                    LocalNotificationCall();
//                                                    String message = "Payment Successful";
//
//                                                    if (obj.has("msg")) {
//
//                                                        message = obj.getString("msg");
//                                                    }
//
//                                                    View v = getLayoutInflater().inflate(R.layout.payment_success_layout, null);
//                                                    setContentView(v);
//                                                    Button back = v.findViewById(R.id.back_button_success);
//                                                    TextView msgTxt = v.findViewById(R.id.success_msg);
//                                                    msgTxt.setText(message);
//
//                                                    ImageView gif = v.findViewById(R.id.status_gif);
//
//                                                    Glide.with(MobilePayActivity.this)
//                                                            .asGif()
//                                                            .load(R.drawable.payment_success) // Replace with your GIF file in res/drawable
//                                                            .into(gif);
//
//                                                    back.setOnClickListener(new View.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(View view) {
//                                                            Intent intent = new Intent(MobilePayActivity.this, RechargeActivity.class);
//                                                            intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
//                                                            startActivity(intent);
//                                                        }
//                                                    });
//
//                                                }
//                                                else {
//
//
//                                                    View v = getLayoutInflater().inflate(R.layout.payment_failed_layout, null);
//                                                    setContentView(v);
//                                                    Button back = v.findViewById(R.id.back_button);
//                                                    TextView msgTxt = v.findViewById(R.id.failed_msg);
//                                                    if (jsonObject.has("error")) {
//
//                                                        String errorMessage = jsonObject.getString("error");
//                                                        msgTxt.setText(errorMessage);
//                                                    } else if (jsonObject.has("msg")) {
//
//                                                        String msg = jsonObject.getString("msg");
//                                                        msgTxt.setText(msg);
//                                                    } else {
//
//                                                        // If no error message is present, show a custom message
//                                                        msgTxt.setText("Getting Server Side Error! If money be deducted from your account will be refund within two to four business day.");
//                                                    }
//
//                                                    ImageView gif = v.findViewById(R.id.status_gif);
//
//                                                    Glide.with(MobilePayActivity.this)
//                                                            .asGif()
//                                                            .load(R.drawable.payment_failed) // Replace with your GIF file in res/drawable
//                                                            .into(gif);
//
//                                                    back.setOnClickListener(new View.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(View view) {
//                                                            Intent intent = new Intent(MobilePayActivity.this, RechargeActivity.class);
//                                                            intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
//                                                            startActivity(intent);
//                                                        }
//                                                    });
//                                                }
//                                            }
//                                            else {
//                                                //If Status be false
//
//                                                String errorcode = obj.getString("errorcode");
//
//
//                                                View v = getLayoutInflater().inflate(R.layout.payment_failed_layout, null);
//                                                setContentView(v);
//                                                Button back = v.findViewById(R.id.back_button);
//                                                TextView msgTxt = v.findViewById(R.id.failed_msg);
//                                                if (jsonObject.has("error")) {
//                                                    String errorMessage = jsonObject.getString("error");
//                                                    msgTxt.setText(errorMessage);
//
//                                                } else if (jsonObject.has("msg")) {
//
//                                                    String msg = jsonObject.getString("msg");
//                                                    msgTxt.setText(msg);
//                                                } else {
//
//                                                    // If no error message is present, show a custom message
//                                                    msgTxt.setText("Getting False Status during pay bill! If money be deducted from your account will be refund within two to four business day.");
//                                                }
//
//                                                ImageView gif = v.findViewById(R.id.status_gif);
//
//                                                Glide.with(MobilePayActivity.this)
//                                                        .asGif()
//                                                        .load(R.drawable.payment_failed) // Replace with your GIF file in res/drawable
//                                                        .into(gif);
//
//                                                back.setOnClickListener(new View.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(View view) {
//                                                        Intent intent = new Intent(MobilePayActivity.this, RechargeActivity.class);
//                                                        intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
//                                                        startActivity(intent);
//                                                    }
//                                                });
//                                            }
//                                        }
//                                        else {
//                                            //data Not Present
//                                            View v = getLayoutInflater().inflate(R.layout.payment_failed_layout, null);
//                                            setContentView(v);
//                                            Button back = v.findViewById(R.id.back_button);
//                                            TextView msgTxt = v.findViewById(R.id.failed_msg);
//                                            if (jsonObject.has("error")) {
//                                                String errorMessage = jsonObject.getString("error");
//                                                msgTxt.setText(errorMessage);
//                                            } else if (jsonObject.has("msg")) {
//                                                String msg = jsonObject.getString("msg");
//                                                billPayStatus=jsonObject.getString("msg");
//                                                msgTxt.setText(msg);
//                                            } else if (jsonObject.has("message")) {
//                                                // String message = jsonObject.getString("msg");
//                                                billPayStatus=jsonObject.getString("message");
//                                                msgTxt.setText(billPayStatus);
//                                            }
//                                            else {
//                                                // If no error message is present, show a custom message
//                                                msgTxt.setText("Getting from Pay Bill Error! If money be deducted from your account will be refund within two to four business day.");
//                                            }
//
//                                            ImageView gif = v.findViewById(R.id.status_gif);
//
//                                            Glide.with(MobilePayActivity.this)
//                                                    .asGif()
//                                                    .load(R.drawable.payment_failed) // Replace with your GIF file in res/drawable
//                                                    .into(gif);
//
//                                            back.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View view) {
//                                                    Intent intent = new Intent(MobilePayActivity.this, RechargeActivity.class);
//                                                    intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
//                                                    startActivity(intent);
//                                                }
//                                            });
//                                        }
//                                    }
//                                    else {
//                                        //Status not present
//                                        View v = getLayoutInflater().inflate(R.layout.payment_failed_layout, null);
//                                        setContentView(v);
//                                        Button back = v.findViewById(R.id.back_button);
//                                        TextView msgTxt = v.findViewById(R.id.failed_msg);
//                                        if (jsonObject.has("error")) {
//                                            String errorMessage = jsonObject.getString("error");
//                                            msgTxt.setText(errorMessage);
//                                        } else if (jsonObject.has("msg")) {
//                                            String msg = jsonObject.getString("msg");
//                                            billPayStatus=jsonObject.getString("msg");
//                                            msgTxt.setText(msg);
//                                        } else {
//                                            // If no error message is present, show a custom message
//                                            msgTxt.setText("Getting from Pay Bill Error! If money be deducted from your account will be refund within two to four business day.");
//                                        }
//
//                                        ImageView gif = v.findViewById(R.id.status_gif);
//
//                                        Glide.with(MobilePayActivity.this)
//                                                .asGif()
//                                                .load(R.drawable.payment_failed) // Replace with your GIF file in res/drawable
//                                                .into(gif);
//
//                                        back.setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View view) {
//                                                Intent intent = new Intent(MobilePayActivity.this, RechargeActivity.class);
//                                                intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
//                                                startActivity(intent);
//                                            }
//                                        });
//                                    }

                                    //String
                                   // break;
                                case 500:

                                    View v = getLayoutInflater().inflate(R.layout.payment_failed_layout, null);
                                    setContentView(v);
                                    Button back=v.findViewById(R.id.back_button);
                                    TextView msgTxt=v.findViewById(R.id.failed_msg);
                                    if (jsonObject.has("error")) {
                                        String errorMessage = jsonObject.getString("error");
                                        msgTxt.setText(errorMessage);
                                    }  else if (jsonObject.has("msg")) {
                                        billPayStatus=jsonObject.getString("msg");
                                        String msg = jsonObject.getString("msg");
                                        msgTxt.setText(msg);
                                    }
                                    else {
                                        // If no error message is present, show a custom message
                                        msgTxt.setText("Getting Server Side Error! If money be deducted from your account will be refund within two to four business day.");
                                    }

                                    ImageView gif=v.findViewById(R.id.status_gif);

                                    Glide.with(MobilePayActivity.this)
                                            .asGif()
                                            .load(R.drawable.payment_failed) // Replace with your GIF file in res/drawable
                                            .into(gif);

                                    back.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(MobilePayActivity.this, RechargeActivity.class);
                                            intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
                                            startActivity(intent);
                                        }
                                    });
                                    break;
                                default:
                                    CustomAlertDialog dialog = new CustomAlertDialog(MobilePayActivity.this, R.style.WideDialog, "OneZed", "Something Wrong, Please Try after some times");
                                    dialog.show();
                                    dialog.onPositiveButton(view ->{
                                        dialog.dismiss();
                                        startActivity(new Intent(MobilePayActivity.this,HomeActivity.class));

                                    } );
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }).sendRequest();
    }

    private void LocalNotificationCall() {
        //LOCAL NOTIFICATION START

//            // Create the notification channel
//            NotificationHelper.createNotificationChannel(getContext());
//
//            // Schedule a notification for 10 seconds from now
//            long triggerAtMillis = System.currentTimeMillis() + 10000; // 10 seconds
//            AlarmHelper.scheduleNotification(getContext(), triggerAtMillis);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(MobilePayActivity.this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                NotificationHelper.createNotificationChannel(MobilePayActivity.this);
                long triggerAtMillis = System.currentTimeMillis() + 10000; // 10 seconds
                AlarmHelper.scheduleNotification(MobilePayActivity.this, triggerAtMillis);
            } else {
                // Request the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            // For devices below API 33, no need to handle notification permission
            NotificationHelper.createNotificationChannel(MobilePayActivity.this);
            long triggerAtMillis = System.currentTimeMillis() + 10000; // 10 seconds
            AlarmHelper.scheduleNotification(MobilePayActivity.this, triggerAtMillis);
        }


        // LOCAL NOTIFICATION END
    }
    // Define ActivityResultLauncher for notification permission
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission granted, proceed with notification-related tasks
                    NotificationHelper.createNotificationChannel(MobilePayActivity.this);
                    long triggerAtMillis = System.currentTimeMillis() + 10000; // 10 seconds
                    AlarmHelper.scheduleNotification(MobilePayActivity.this, triggerAtMillis);
                } else {
                    // Permission denied
                    Toast.makeText(MobilePayActivity.this, "Notification permission is required.", Toast.LENGTH_SHORT).show();
                }
            });

    private void billCreateApiCall() {
        JSONObject joObj = new JSONObject();
        try {
            String customerName = GlobalVariable.bbpsCustomername != null ? GlobalVariable.bbpsCustomername : "NA"; // Check if customer name is null and set to "N/A" if so

            joObj.put("payment_id", paymentId);
            joObj.put("amount",billPayAmount);
            joObj.put("status",billPayStatus);
            joObj.put("contact",UserProfileModel.getInstance().getMobileNo());
            joObj.put("description",billPayDescription);
            joObj.put("customer_name",customerName);
            joObj.put("consumer_id",billPayCustomerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");


        finalAmount = (int) (Double.parseDouble(amount) * 100);
        Log.v("BillCreate",joObj.toString());

        // ExeDec goia=new ExeDec(getOrderIdAmount);
        // String url = String.format(goia.getDec()+"%s",finalAmount);
        new HttpHandler("https://onezed.app/api/bill-create", MobilePayActivity.this)
                .setMediaType(HttpHandler.jsonMediaType)
                .setRequestType(HttpHandler.RequestType.POST)
                .setStringData(joObj.toString())
                .setLoading(true)
                .setHeaders(headers)
                .setOnResponse(new HttpHandler.HttpResponse() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        CustomAlertDialog dialog = new CustomAlertDialog(MobilePayActivity.this, R.style.WideDialog, getString(R.string.app_name), getString(R.string.no_response));
                        dialog.show();
                        dialog.onPositiveButton(view ->{
                            dialog.dismiss();
                        } );
                    }

                    @Override
                    public void onResponse(int code, String response) {
                        Log.v("billCreate ",code+" "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            switch (code) {
                                case 200:
                                    Log.v("billCreate ","success");

//                                    else {
//                                        CustomAlertDialog dialog = new CustomAlertDialog(BillPayActivity.this, R.style.WideDialog, getString(R.string.app_name), "Unable to Open Payment Gateway");
//                                        dialog.show();
//                                        dialog.onPositiveButton(view ->{
//                                            dialog.dismiss();
//                                            if (type.equals("ELECTRICITY_BIL_PAY")) {
//                                                Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
//                                                intent.putExtra("FRAGMENT_TYPE", "ELECTRICITY_BIL_PAY");
//                                                startActivity(intent);
//                                            } else if (type.equals("BROADBAND_RECHARGE")) {
//                                                Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
//                                                intent.putExtra("FRAGMENT_TYPE", "BROADBAND_RECHARGE");
//                                                startActivity(intent);
//                                            }
//                                            else if (type.equals("MOBILE_POSTPAID")) {
//                                                Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
//                                                intent.putExtra("FRAGMENT_TYPE", "MOBILE_POSTPAID");
//                                                startActivity(intent);
//                                            }
//                                            else {
//                                                startActivity(new Intent(BillPayActivity.this, HomeActivity.class));
//                                            }
//                                        } );
//                                    }
                                    break;
                                default:
                                    Log.v("billCreate ","default");
//                                    CustomAlertDialog dialog = new CustomAlertDialog(BillPayActivity.this, R.style.WideDialog, "OneZed", "Something Wrong, Please Try after some times");
//                                    dialog.show();
//                                    dialog.onPositiveButton(view ->{
//                                        dialog.dismiss();
//                                        if (type.equals("ELECTRICITY_BIL_PAY")) {
//                                            Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
//                                            intent.putExtra("FRAGMENT_TYPE", "ELECTRICITY_BIL_PAY");
//                                            startActivity(intent);
//                                        } else if (type.equals("BROADBAND_RECHARGE")) {
//                                            Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
//                                            intent.putExtra("FRAGMENT_TYPE", "BROADBAND_RECHARGE");
//                                            startActivity(intent);
//                                        } else if (type.equals("MOBILE_POSTPAID")) {
//                                            Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
//                                            intent.putExtra("FRAGMENT_TYPE", "MOBILE_POSTPAID");
//                                            startActivity(intent);
//                                        } else if (type.equals("MOBILE_PREPAID_RECHARGE")) {
//                                            Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
//                                            intent.putExtra("FRAGMENT_TYPE", "MOBILE_PREPAID_RECHARGE");
//                                            startActivity(intent);
//                                        }
//                                        else {
//                                            startActivity(new Intent(BillPayActivity.this, HomeActivity.class));
//                                        }
//                                    } );
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }).sendRequest();
    }
}