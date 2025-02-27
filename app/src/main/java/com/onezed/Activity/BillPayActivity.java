package com.onezed.Activity;

import static com.onezed.GlobalVariable.baseUrl.getOrderIdAmount;
import static com.onezed.GlobalVariable.baseUrl.payBill;
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
import com.onezed.Model.UserProfileModel;
import com.onezed.R;
import com.onezed.databinding.ActivityElectricBillPayBinding;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class BillPayActivity extends AppCompatActivity implements PaymentResultWithDataListener {

    ActivityElectricBillPayBinding binding;
    int finalAmount;
    String payId,spKey,consumerId,amount,type,statusMessage="Something went Wrong",bbpsType="BBPS",paymentId,OptionalParamLength;
    String billPayCustomerId="NA",billPayAmount,billPayDescription="NA",billPayStatus="NA",in_house_service,in_house_type;
    Double in_house_amount;
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityElectricBillPayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        OptionalParamLength=UserProfileModel.getInstance().getMobileNo();
        // Retrieve the type from the Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("Type")) {

            type = intent.getStringExtra("Type");
            if (type.equals("ELECTRICITY_BIL_PAY")|| type=="ELECTRICITY_BIL_PAY")
            {
                bbpsType="ELECTRIC_BILL_PAY";
                amount = intent.getStringExtra("amount");  // Default value is 0.0
                billPayAmount=amount;
                consumerId = intent.getStringExtra("consumer_id");  // Default value is 0.0
                spKey = intent.getStringExtra("spkey");  // Default value is 0.0
                //finalAmount=Double.amount;
                if ((amount ==null)||(amount.equals(0))||(amount=="0")){
                    CustomAlertDialog dialog = new CustomAlertDialog(BillPayActivity.this, R.style.WideDialog, getString(R.string.app_name), "Amount filed can't be Zero");
                    dialog.show();
                    dialog.onPositiveButton(view ->{
                        dialog.dismiss();
                        if (type.equals("ELECTRIC_BILL_PAY") ) {
                            Intent inten = new Intent(BillPayActivity.this, RechargeActivity.class);
                            inten.putExtra("FRAGMENT_TYPE", "ELECTRIC_BILL_PAY");
                           // startActivity(inten);
                        }else {
                            startActivity(new Intent(BillPayActivity.this, HomeActivity.class));
                        }
                    } );
                }else {
                    startPayment(amount);
                   // Log.v("UserNumber",UserProfileModel.getInstance().getMobileNo());
                }

            }
            else if (type.equals("BROADBAND_RECHARGE")) {
                bbpsType="BROADBAND_RECHARGE";
                consumerId = intent.getStringExtra("consumerId");
                spKey = intent.getStringExtra("spKey");
                amount = intent.getStringExtra("amount");
                billPayAmount=amount;
                if ((amount ==null)||(amount.equals(0))||(amount=="0")){
                    CustomAlertDialog dialog = new CustomAlertDialog(BillPayActivity.this, R.style.WideDialog, getString(R.string.app_name), "Amount filed can't be Zero");
                    dialog.show();
                    dialog.onPositiveButton(view ->{
                        dialog.dismiss();
                        if (type.equals("BROADBAND_RECHARGE") ) {
                            Intent inten = new Intent(BillPayActivity.this, RechargeActivity.class);
                            inten.putExtra("FRAGMENT_TYPE", "BROADBAND_RECHARGE");
                            startActivity(inten);
                        }else {
                            startActivity(new Intent(BillPayActivity.this, HomeActivity.class));
                        }
                    } );
                }else {
                    startPayment(amount);
                }

            }
            else if (type.equals("MOBILE_POSTPAID")) {
                bbpsType="MOBILE_POSTPAID";
                consumerId = intent.getStringExtra("consumerId");
                spKey = intent.getStringExtra("spKey");
                amount = intent.getStringExtra("amount");
                billPayAmount=amount;
                if ((amount ==null)||(amount.equals(0))||(amount=="0")){
                    CustomAlertDialog dialog = new CustomAlertDialog(BillPayActivity.this, R.style.WideDialog, getString(R.string.app_name), "Amount filed can't be Zero");
                    dialog.show();
                    dialog.onPositiveButton(view ->{
                        dialog.dismiss();
                        if (type.equals("MOBILE_POSTPAID") ) {
                            Intent inten = new Intent(BillPayActivity.this, RechargeActivity.class);
                            inten.putExtra("FRAGMENT_TYPE", "MOBILE_POSTPAID");
                            startActivity(inten);
                        }else {
                            startActivity(new Intent(BillPayActivity.this, HomeActivity.class));
                        }
                    } );
                }else {
                    startPayment(amount);
                   // Log.v("recharge", UserProfileModel.getInstance().getMobileNo()+" "+consumerId+" "+spKey+" "+amount+" ");
                }

            }
            else if (type.equals("GAS_CYLINDER_BOOK")) {
                bbpsType="GAS_CYLINDER_BOOK";
                consumerId = intent.getStringExtra("consumerId");
                spKey = intent.getStringExtra("spKey");
                amount = intent.getStringExtra("amount");
                billPayAmount=amount;
                if ((amount ==null)||(amount.equals(0))||(amount=="0")){
                    CustomAlertDialog dialog = new CustomAlertDialog(BillPayActivity.this, R.style.WideDialog, getString(R.string.app_name), "Amount filed can't be Zero");
                    dialog.show();
                    dialog.onPositiveButton(view ->{
                        dialog.dismiss();
                        if (type.equals("GAS_CYLINDER_BOOK") ) {
                            Intent inten = new Intent(BillPayActivity.this, RechargeActivity.class);
                            inten.putExtra("FRAGMENT_TYPE", "GAS_CYLINDER_BOOK");
                            startActivity(inten);
                        }else {
                            startActivity(new Intent(BillPayActivity.this, HomeActivity.class));
                        }
                    } );
                }else {
                    startPayment(amount);
                    // Log.v("recharge", UserProfileModel.getInstance().getMobileNo()+" "+consumerId+" "+spKey+" "+amount+" ");
                }

            }
            else if (type.equals("FASTAG_RECHARGE")) {
                bbpsType="FASTAG_RECHARGE";
                consumerId = intent.getStringExtra("consumerId");
                spKey = intent.getStringExtra("spKey");
                amount = intent.getStringExtra("amount");
                billPayAmount=amount;
                if ((amount ==null)||(amount.equals(0))||(amount=="0")){
                    CustomAlertDialog dialog = new CustomAlertDialog(BillPayActivity.this, R.style.WideDialog, getString(R.string.app_name), "Amount filed can't be Zero");
                    dialog.show();
                    dialog.onPositiveButton(view ->{
                        dialog.dismiss();
                        if (type.equals("FASTAG_RECHARGE") ) {
                            Intent inten = new Intent(BillPayActivity.this, RechargeActivity.class);
                            inten.putExtra("FRAGMENT_TYPE", "FASTAG_RECHARGE");
                            startActivity(inten);
                        }else {
                            startActivity(new Intent(BillPayActivity.this, HomeActivity.class));
                        }
                    } );
                }else {
                    startPayment(amount);
                }

            }
            else if (type.equals("IN_HOUSE")) {
                in_house_service = intent.getStringExtra("service");
                 in_house_type= getIntent().getStringExtra("serviceName");
                in_house_amount= getIntent().getDoubleExtra("TotalAmount",1.0);
               // in_house_amount=1.0;
                Log.v("InhouseData", UserProfileModel.getInstance().getMobileNo()+" "+in_house_service+" "+in_house_amount+" "+in_house_type);
                if ((in_house_amount ==null)||(in_house_amount.equals(0))){
                    CustomAlertDialog dialog = new CustomAlertDialog(BillPayActivity.this, R.style.WideDialog, getString(R.string.app_name), "In Amount filed can't be Zero");
                    dialog.show();
                    dialog.onPositiveButton(view ->{
                        dialog.dismiss();
                        if (type.equals("IN_HOUSE") ) {
                            if (in_house_service.equals("Company Registration")){
                                Intent inten = new Intent(BillPayActivity.this, InhouseActivity.class);
                                inten.putExtra("FRAGMENT_TYPE", "BUSINESS_REGISTRATION");
                                startActivity(inten);
                            } else if (in_house_service.equals("Licence Registration")) {
                                Intent inten = new Intent(BillPayActivity.this, InhouseActivity.class);
                                inten.putExtra("FRAGMENT_TYPE", "LICENCE_REGISTRATION");
                                startActivity(inten);
                            }else if (in_house_service.equals("Accounting & E-filing")) {
                                Intent inten = new Intent(BillPayActivity.this, InhouseActivity.class);
                                inten.putExtra("FRAGMENT_TYPE", "ACCOUNTING_EFINING");
                                startActivity(inten);
                            }
                            else if (in_house_service.equals("Brand Protection")) {
                                Intent inten = new Intent(BillPayActivity.this, InhouseActivity.class);
                                inten.putExtra("FRAGMENT_TYPE", "BRAND_PROTECTION");
                                startActivity(inten);
                            }

                        }else {
                            startActivity(new Intent(BillPayActivity.this, HomeActivity.class));
                        }
                    } );
                }else {
                    startPayment(String.valueOf(in_house_amount));
                     Log.v("InhouseData", UserProfileModel.getInstance().getMobileNo()+" "+in_house_service+" "+in_house_amount+" ");
                }
            }

        }
        else {
            CustomAlertDialog dialog = new CustomAlertDialog(BillPayActivity.this, R.style.WideDialog, getString(R.string.app_name), "Type filed can't be Null");
            dialog.show();
            dialog.onPositiveButton(view ->{
                dialog.dismiss();
                startActivity(new Intent(BillPayActivity.this, HomeActivity.class));
            } );
        }

    }

    private void startPayment(String amount) {

        JSONObject joObj = new JSONObject();
        try {
            joObj.put("phone", UserProfileModel.getInstance().getMobileNo());
            joObj.put("name",UserProfileModel.getInstance().getName());
            joObj.put("email",UserProfileModel.getInstance().getEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");


        finalAmount = (int) (Double.parseDouble(amount) * 100);


        ExeDec goia=new ExeDec(getOrderIdAmount);
        String url = String.format(goia.getDec()+"%s",finalAmount);
        new HttpHandler(url, BillPayActivity.this)
                .setMediaType(HttpHandler.jsonMediaType)
                .setRequestType(HttpHandler.RequestType.POST)
                .setStringData(joObj.toString())
                .setLoading(true)
                .setHeaders(headers)
                .setOnResponse(new HttpHandler.HttpResponse() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        CustomAlertDialog dialog = new CustomAlertDialog(BillPayActivity.this, R.style.WideDialog, getString(R.string.app_name), getString(R.string.no_response));
                        dialog.show();
                        dialog.onPositiveButton(view ->{
                            dialog.dismiss();
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
                                        Log.v("details ",orderId+" "+UserProfileModel.getInstance().getName()+" "+finalAmount+" "+UserProfileModel.getInstance().getEmail()+" "+UserProfileModel.getInstance().getMobileNo());
                                        OpenRezerPay(orderId);
                                        GlobalVariable.orderId = orderId;

                                    }
                                    else {
                                        CustomAlertDialog dialog = new CustomAlertDialog(BillPayActivity.this, R.style.WideDialog, getString(R.string.app_name), "Unable to Open Payment Gateway");
                                        dialog.show();
                                        dialog.onPositiveButton(view ->{
                                            dialog.dismiss();
                                            if (type.equals("ELECTRICITY_BIL_PAY")) {
                                                Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                intent.putExtra("FRAGMENT_TYPE", "ELECTRICITY_BIL_PAY");
                                                startActivity(intent);
                                            } else if (type.equals("BROADBAND_RECHARGE")) {
                                                Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                intent.putExtra("FRAGMENT_TYPE", "BROADBAND_RECHARGE");
                                                startActivity(intent);
                                            }
                                            else if (type.equals("MOBILE_POSTPAID")) {
                                                Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                intent.putExtra("FRAGMENT_TYPE", "MOBILE_POSTPAID");
                                                startActivity(intent);
                                            } else if (type.equals("GAS_CYLINDER_BOOK")) {
                                                Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                intent.putExtra("FRAGMENT_TYPE", "GAS_CYLINDER_BOOK");
                                                startActivity(intent);
                                            }else if (type.equals("FASTAG_RECHARGE")) {
                                                Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                intent.putExtra("FRAGMENT_TYPE", "FASTAG_RECHARGE");
                                                startActivity(intent);
                                            }
                                            else {
                                                startActivity(new Intent(BillPayActivity.this, HomeActivity.class));
                                            }
                                        } );
                                    }
                                    break;
                                default:
                                    CustomAlertDialog dialog = new CustomAlertDialog(BillPayActivity.this, R.style.WideDialog, "OneZed", "Something Wrong, Please Try after some times");
                                    dialog.show();
                                    dialog.onPositiveButton(view ->{
                                        dialog.dismiss();
                                        if (type.equals("ELECTRICITY_BIL_PAY")) {
                                            Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                            intent.putExtra("FRAGMENT_TYPE", "ELECTRICITY_BIL_PAY");
                                            startActivity(intent);
                                        } else if (type.equals("BROADBAND_RECHARGE")) {
                                            Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                            intent.putExtra("FRAGMENT_TYPE", "BROADBAND_RECHARGE");
                                            startActivity(intent);
                                        } else if (type.equals("MOBILE_POSTPAID")) {
                                            Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                            intent.putExtra("FRAGMENT_TYPE", "MOBILE_POSTPAID");
                                            startActivity(intent);
                                        } else if (type.equals("MOBILE_PREPAID_RECHARGE")) {
                                            Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                            intent.putExtra("FRAGMENT_TYPE", "MOBILE_PREPAID_RECHARGE");
                                            startActivity(intent);
                                        } else if (type.equals("GAS_CYLINDER_BOOK")) {
                                            Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                            intent.putExtra("FRAGMENT_TYPE", "GAS_CYLINDER_BOOK");
                                            startActivity(intent);
                                        } else if (type.equals("FASTAG_RECHARGE")) {
                                            Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                            intent.putExtra("FRAGMENT_TYPE", "FASTAG_RECHARGE");
                                            startActivity(intent);
                                        }
                                        else {
                                            startActivity(new Intent(BillPayActivity.this, HomeActivity.class));
                                        }
                                    } );
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

        //checkout.setKeyID("rzp_test_4aGf1rnJhy8N21"); //TEST KEY
        checkout.setKeyID("rzp_live_2dr4ep6PhAKG3S"); // LIVE KEY

        try {
            // Create payment options
            JSONObject options = new JSONObject();
            options.put("name", UserProfileModel.getInstance().getName()); // Name of the person making the payment
            options.put("description", in_house_type); // Order description
            options.put("currency", "INR"); // Currency code
            options.put("order_id", orderId);
            options.put("amount", finalAmount); // Amount in paise (1 INR = 100 paise)
            options.put("prefill.email", UserProfileModel.getInstance().getEmail()); // User's email
            options.put("prefill.contact", UserProfileModel.getInstance().getMobileNo()); // User's phone number


            // Open Razorpay checkout
            checkout.open(BillPayActivity.this, options);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {


        // Retrieve details from the PaymentData object
        String razorpayPaymentId = paymentData.getPaymentId(); // Payment ID
        paymentId=razorpayPaymentId;
        String razorpayOrderId = paymentData.getOrderId(); // Order ID
        String razorpaySignature = paymentData.getSignature(); // Signature

        verifyPayment(razorpayPaymentId,razorpayOrderId,razorpaySignature);

    }

    private void LoaderView() {

        View v = getLayoutInflater().inflate(R.layout.payment_failed_layout, null);
        setContentView(v);
      //  Button back = v.findViewById(R.id.back_button);
        TextView msgTxt = v.findViewById(R.id.status_gif);
       // msgTxt.setText(message);

        ImageView gif = v.findViewById(R.id.status_gif);

        Glide.with(BillPayActivity.this)
                .asGif()
                .load(R.drawable.loader) // Replace with your GIF file in res/drawable
                .into(gif);
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        Log.v("StatusPayment","failed");
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
                if (type.equals("IN_HOUSE") ) {
                    if (in_house_service.equals("Company Registration")){
                        Intent inten = new Intent(BillPayActivity.this, InhouseActivity.class);
                        inten.putExtra("FRAGMENT_TYPE", "BUSINESS_REGISTRATION");
                        startActivity(inten);
                    } else if (in_house_service.equals("Licence Registration")) {
                        Intent inten = new Intent(BillPayActivity.this, InhouseActivity.class);
                        inten.putExtra("FRAGMENT_TYPE", "LICENCE_REGISTRATION");
                        startActivity(inten);
                    }else if (in_house_service.equals("Accounting & E-filing")) {
                        Intent inten = new Intent(BillPayActivity.this, InhouseActivity.class);
                        inten.putExtra("FRAGMENT_TYPE", "ACCOUNTING_EFINING");
                        startActivity(inten);
                    }
                    else if (in_house_service.equals("Brand Protection")) {
                        Intent inten = new Intent(BillPayActivity.this, InhouseActivity.class);
                        inten.putExtra("FRAGMENT_TYPE", "BRAND_PROTECTION");
                        startActivity(inten);
                    }

                }else {
                    startActivity(new Intent(BillPayActivity.this, HomeActivity.class));
                }
            }
        });
    }


    private void verifyPayment(String payId,String orderId,String sigId) {
        Log.v("StatusPayment","verifyPayment");
        JSONObject joObj = new JSONObject();
        try {
            joObj.put("payment_id",payId);
            joObj.put("order_id",orderId );
            joObj.put("razorpay_signature",sigId );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.v("VerifyBody",joObj.toString());
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        ExeDec vp=new ExeDec(verifyPayment);
        Log.v("StatusPaymentURL",vp.getDec());
        new HttpHandler(vp.getDec(), BillPayActivity.this)
                .setMediaType(HttpHandler.jsonMediaType)
                .setRequestType(HttpHandler.RequestType.POST)
                .setStringData(joObj.toString())
                .setLoading(true)
                .setHeaders(headers)
                .setOnResponse(new HttpHandler.HttpResponse() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        CustomAlertDialog dialog = new CustomAlertDialog(BillPayActivity.this, R.style.WideDialog, getString(R.string.app_name), getString(R.string.no_response));
                        dialog.show();
                        dialog.onPositiveButton(view -> dialog.dismiss());
                    }

                    @Override
                    public void onResponse(int code, String response) {
                        Log.v("VerifyBody",code+" "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            switch (code) {
                                case 200:

                                    if (jsonObject.has("status")){
                                        String status=jsonObject.getString("status");
                                        if (jsonObject.has("message")){
                                            statusMessage= jsonObject.getString("message");
                                        }else {
                                            statusMessage="Getting Error in verify payment Response! If money be deducted from your account will be refund within two to four business day.";
                                        }


                                        if (status.equals("success")){
                                            if (type.equals("IN_HOUSE")){

                                            }else {
                                                setOptionalParamLengthUsingSPKey();
                                            }


                                        }
                                        else{
                                            View v = getLayoutInflater().inflate(R.layout.payment_failed_layout, null);
                                            setContentView(v);
                                            Button back=v.findViewById(R.id.back_button);
                                            TextView msgTxt=v.findViewById(R.id.failed_msg);
                                            msgTxt.setText(statusMessage);
                                            ImageView gif=v.findViewById(R.id.status_gif);

                                            Glide.with(BillPayActivity.this)
                                                    .asGif()
                                                    .load(R.drawable.payment_failed) // Replace with your GIF file in res/drawable
                                                    .into(gif);

                                            back.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    if (type.equals("ELECTRICITY_BIL_PAY")) {
                                                        Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                        intent.putExtra("FRAGMENT_TYPE", "ELECTRICITY_BIL_PAY");
                                                        startActivity(intent);
                                                    } else if (type.equals("BROADBAND_RECHARGE")) {
                                                        Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                        intent.putExtra("FRAGMENT_TYPE", "BROADBAND_RECHARGE");
                                                        startActivity(intent);
                                                    }else if (type.equals("MOBILE_POSTPAID")) {
                                                        Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                        intent.putExtra("FRAGMENT_TYPE", "MOBILE_POSTPAID");
                                                        startActivity(intent);
                                                    }else if (type.equals("MOBILE_PREPAID_RECHARGE")) {
                                                        Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                        intent.putExtra("FRAGMENT_TYPE", "MOBILE_PREPAID_RECHARGE");
                                                        startActivity(intent);
                                                    }else if (type.equals("GAS_CYLINDER_BOOK")) {
                                                        Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                        intent.putExtra("FRAGMENT_TYPE", "GAS_CYLINDER_BOOK");
                                                        startActivity(intent);
                                                    }else if (type.equals("FASTAG_RECHARGE")) {
                                                        Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                        intent.putExtra("FRAGMENT_TYPE", "FASTAG_RECHARGE");
                                                        startActivity(intent);
                                                    }else {
                                                        startActivity(new Intent(BillPayActivity.this, HomeActivity.class));
                                                    }
                                                }
                                            });
                                        }
                                    }
                                    else {

                                        View v = getLayoutInflater().inflate(R.layout.payment_failed_layout, null);
                                        setContentView(v);
                                        Button back=v.findViewById(R.id.back_button);
                                        TextView msgTxt=v.findViewById(R.id.failed_msg);
                                        msgTxt.setText(statusMessage);
                                        ImageView gif=v.findViewById(R.id.status_gif);

                                        Glide.with(BillPayActivity.this)
                                                .asGif()
                                                .load(R.drawable.payment_failed) // Replace with your GIF file in res/drawable
                                                .into(gif);

                                        back.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if (type.equals("ELECTRICITY_BIL_PAY")) {
                                                    Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                    intent.putExtra("FRAGMENT_TYPE", "ELECTRICITY_BIL_PAY");
                                                    startActivity(intent);
                                                } else if (type.equals("BROADBAND_RECHARGE")) {
                                                    Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                    intent.putExtra("FRAGMENT_TYPE", "BROADBAND_RECHARGE");
                                                    startActivity(intent);
                                                }else if (type.equals("MOBILE_POSTPAID")) {
                                                    Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                    intent.putExtra("FRAGMENT_TYPE", "MOBILE_POSTPAID");
                                                    startActivity(intent);
                                                }else if (type.equals("MOBILE_PREPAID_RECHARGE")) {
                                                    Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                    intent.putExtra("FRAGMENT_TYPE", "MOBILE_PREPAID_RECHARGE");
                                                    startActivity(intent);
                                                }else if (type.equals("GAS_CYLINDER_BOOK")) {
                                                    Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                    intent.putExtra("FRAGMENT_TYPE", "GAS_CYLINDER_BOOK");
                                                    startActivity(intent);
                                                }else if (type.equals("FASTAG_RECHARGE")) {
                                                    Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                    intent.putExtra("FRAGMENT_TYPE", "FASTAG_RECHARGE");
                                                    startActivity(intent);
                                                }else {
                                                    startActivity(new Intent(BillPayActivity.this, HomeActivity.class));
                                                }
                                            }
                                        });
                                    }


                                    break;
                                default:

                                    View v = getLayoutInflater().inflate(R.layout.payment_failed_layout, null);
                                    setContentView(v);
                                    Button back=v.findViewById(R.id.back_button);
                                    TextView msgTxt=v.findViewById(R.id.failed_msg);
                                    msgTxt.setText(statusMessage);
                                    ImageView gif=v.findViewById(R.id.status_gif);

                                    Glide.with(BillPayActivity.this)
                                            .asGif()
                                            .load(R.drawable.payment_failed) // Replace with your GIF file in res/drawable
                                            .into(gif);

                                    back.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (type.equals("ELECTRICITY_BIL_PAY")) {
                                                Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                intent.putExtra("FRAGMENT_TYPE", "ELECTRICITY_BIL_PAY");
                                                startActivity(intent);
                                            } else if (type.equals("BROADBAND_RECHARGE")) {
                                                Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                intent.putExtra("FRAGMENT_TYPE", "BROADBAND_RECHARGE");
                                                startActivity(intent);
                                            }else if (type.equals("MOBILE_POSTPAID")) {
                                                Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                intent.putExtra("FRAGMENT_TYPE", "MOBILE_POSTPAID");
                                                startActivity(intent);
                                            }else if (type.equals("MOBILE_PREPAID_RECHARGE")) {
                                                Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                intent.putExtra("FRAGMENT_TYPE", "MOBILE_PREPAID_RECHARGE");
                                                startActivity(intent);
                                            }else if (type.equals("GAS_CYLINDER_BOOK")) {
                                                Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                intent.putExtra("FRAGMENT_TYPE", "GAS_CYLINDER_BOOK");
                                                startActivity(intent);
                                            }else if (type.equals("FASTAG_RECHARGE")) {
                                                Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                intent.putExtra("FRAGMENT_TYPE", "FASTAG_RECHARGE");
                                                startActivity(intent);
                                            }else {
                                                startActivity(new Intent(BillPayActivity.this, HomeActivity.class));
                                            }
                                        }
                                    });
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }).sendRequest();
    }

    private void setOptionalParamLengthUsingSPKey() {

        if (spKey=="206" || spKey=="208" || spKey=="204" || spKey=="801" || spKey=="915" || spKey=="911" || spKey=="957" || spKey=="917" || spKey=="905"
             || spKey=="902" || spKey=="935" || spKey=="908" || spKey=="4616"){
            if (consumerId.length() >=8){
                OptionalParamLength=consumerId.substring(consumerId.length() - 8);
            } else if (consumerId.length()<8) {
                OptionalParamLength="12345678";
            } else {
                OptionalParamLength="12345678";
            }

        } else if (spKey=="395"|| spKey=="394") {
            if (consumerId.length() >= 9) {
                OptionalParamLength = consumerId.substring(consumerId.length() - 9);
            } else {
                OptionalParamLength = "123456789";
            }
        } else if (spKey=="312" || spKey=="385" || spKey=="368" || spKey=="340" || spKey=="369" || spKey=="370" || spKey=="313"
                    || spKey=="388" || spKey=="363" || spKey=="1622" || spKey=="672" || spKey=="25047" || spKey=="2567"
                      || spKey=="25058" || spKey=="2514" || spKey=="2585" || spKey=="25095" || spKey=="2564" || spKey=="2521"
                        || spKey=="2596" || spKey=="2597" || spKey=="2599" || spKey=="2513" || spKey=="2530" || spKey=="25038"
                          || spKey=="2509" || spKey=="25064" || spKey=="25019" || spKey=="2558" || spKey=="25021" || spKey=="25124"
                            || spKey=="25053" || spKey =="2534" || spKey=="2572" || spKey=="25031" || spKey=="2505" || spKey=="25032"
                              || spKey=="2541" || spKey=="25036" || spKey=="914" || spKey=="919" || spKey=="924" || spKey=="903"
                                || spKey=="929"|| spKey=="4800" || spKey=="4900"|| spKey=="5405")
        {
            if (consumerId.length() >= 10) {
                OptionalParamLength = UserProfileModel.getInstance().getMobileNo();
            } else {
                OptionalParamLength = "1234567890";
            }
        }else if (spKey=="380") {
            if (consumerId.length() >= 2) {
                OptionalParamLength ="HT";
            } else {
                OptionalParamLength = "LT";
            }
        }else if (spKey=="362") {
            if (consumerId.length() >= 4) {
                OptionalParamLength =consumerId.substring(consumerId.length() - 4);
            } else {
                OptionalParamLength = "1234";
            }
        }else if (spKey=="1602") {
            if (consumerId.length() >= 10) {
                OptionalParamLength =consumerId.substring(consumerId.length() - 10);
            } else {
                OptionalParamLength = "1234";
            }
        }else if (spKey=="684") {
            if (consumerId.length() >= 15) {
                OptionalParamLength =consumerId.substring(consumerId.length() - 15);
            } else if (consumerId.length()<15) {
                OptionalParamLength = "123456789012345";
            } else {
                OptionalParamLength = "123456789012345";
            }
        }else if (spKey=="802"||spKey=="928" ) {
            if (consumerId.length() >= 17) {
                OptionalParamLength =consumerId.substring(consumerId.length() - 17);
            } else if (consumerId.length()<17) {
                OptionalParamLength = "12345678901234567";
            } else {
                OptionalParamLength = "12345678901234567";
            }
        }

        payBillApiCall();
    }

    private void payBillApiCall() {
        Log.v("StatusPayment","payBillApiCall");

        int number = (int) Double.parseDouble(amount);
        JSONObject joObj = new JSONObject();
        try {
            joObj.put("customerNumber", UserProfileModel.getInstance().getMobileNo() );
            joObj.put("consumer_id", consumerId);
            joObj.put("spkey", spKey);
            joObj.put("bill_amount",  String.valueOf(number));
            joObj.put("optional", OptionalParamLength);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.v("payBody",joObj.toString());
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        ExeDec pb=new ExeDec(payBill);
        Log.v("StatusPaymentURL",pb.getDec());
        new HttpHandler(pb.getDec(), BillPayActivity.this)
                .setMediaType(HttpHandler.jsonMediaType)
                .setRequestType(HttpHandler.RequestType.POST)
                .setStringData(joObj.toString())
                .setLoading(true)
                .setHeaders(headers)
                .setOnResponse(new HttpHandler.HttpResponse() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        CustomAlertDialog dialog = new CustomAlertDialog(BillPayActivity.this, R.style.WideDialog, getString(R.string.app_name), getString(R.string.no_response));
                        dialog.show();
                        dialog.onPositiveButton(view -> dialog.dismiss());
                    }

                    @Override
                    public void onResponse(int code, String response) {
                        Log.v("payBody",code+" "+response);
                        billCreateApiCall();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            switch (code) {
                                case 200:

                                   // billCreateApiCall();
                                    if (jsonObject.has("status")){

                                        boolean status=jsonObject.getBoolean("status");
                                        if (jsonObject.has("data")) {

                                            JSONObject obj = jsonObject.getJSONObject("data");
                                            if (obj.has("account")){
                                                billPayCustomerId=obj.getString("account");
                                            }
                                            if (obj.has("amount")){
                                                billPayAmount=obj.getString("amount");
                                            }
                                            if (obj.has("message")){
                                                billPayStatus=jsonObject.getString("message");
                                            }
                                            if (obj.has("msg")){
                                                billPayDescription=obj.getString("msg");
                                            }


                                            //billPayCustomerId=obj.getString("msg");


                                            if (status) {

                                                String errorcode = obj.getString("errorcode");
                                                if (errorcode == "200" || errorcode.equals("200")) {
                                                    GlobalVariable.LocalNotificationText=bbpsType;
                                                    LocalNotificationCall();
                                                    String message = "Payment Successful";

                                                    if (obj.has("msg")) {

                                                        message = obj.getString("msg");
                                                    }
//                                                    CustomAlertDialog dialog1 = new CustomAlertDialog(BillPayActivity.this, R.style.WideDialog, "OneZed", message.toString());
//                                                    dialog1.show();
//                                                    dialog1.onPositiveButton(view -> {
//                                                        dialog1.dismiss();
//                                                        startActivity(new Intent(BillPayActivity.this, HomeActivity.class));
//                                                    });

                                                    View v = getLayoutInflater().inflate(R.layout.payment_success_layout, null);
                                                    setContentView(v);
                                                    Button back = v.findViewById(R.id.back_button_success);
                                                    TextView msgTxt = v.findViewById(R.id.success_msg);
                                                    msgTxt.setText(message);

                                                    ImageView gif = v.findViewById(R.id.status_gif);

                                                    Glide.with(BillPayActivity.this)
                                                            .asGif()
                                                            .load(R.drawable.payment_success) // Replace with your GIF file in res/drawable
                                                            .into(gif);

                                                    back.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            if (type.equals("ELECTRICITY_BIL_PAY")) {
                                                                Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                                intent.putExtra("FRAGMENT_TYPE", "ELECTRICITY_BIL_PAY");
                                                                startActivity(intent);
                                                            } else if (type.equals("BROADBAND_RECHARGE")) {
                                                                Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                                intent.putExtra("FRAGMENT_TYPE", "BROADBAND_RECHARGE");
                                                                startActivity(intent);
                                                            }else if (type.equals("MOBILE_POSTPAID")) {
                                                                Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                                intent.putExtra("FRAGMENT_TYPE", "MOBILE_POSTPAID");
                                                                startActivity(intent);
                                                            }else if (type.equals("MOBILE_PREPAID_RECHARGE")) {
                                                                Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                                intent.putExtra("FRAGMENT_TYPE", "MOBILE_PREPAID_RECHARGE");
                                                                startActivity(intent);
                                                            }else if (type.equals("GAS_CYLINDER_BOOK")) {
                                                                Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                                intent.putExtra("FRAGMENT_TYPE", "GAS_CYLINDER_BOOK");
                                                                startActivity(intent);
                                                            }else if (type.equals("FASTAG_RECHARGE")) {
                                                                Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                                intent.putExtra("FRAGMENT_TYPE", "FASTAG_RECHARGE");
                                                                startActivity(intent);
                                                            }else {
                                                                startActivity(new Intent(BillPayActivity.this, HomeActivity.class));
                                                            }
                                                        }
                                                    });

                                                }
                                                else {


                                                    View v = getLayoutInflater().inflate(R.layout.payment_failed_layout, null);
                                                    setContentView(v);
                                                    Button back = v.findViewById(R.id.back_button);
                                                    TextView msgTxt = v.findViewById(R.id.failed_msg);
                                                    if (jsonObject.has("error")) {

                                                        String errorMessage = jsonObject.getString("error");
                                                        msgTxt.setText(errorMessage);
                                                    } else if (jsonObject.has("msg")) {

                                                        String msg = jsonObject.getString("msg");
                                                        msgTxt.setText(msg);
                                                    } else {

                                                        // If no error message is present, show a custom message
                                                        msgTxt.setText("Getting Server Side Error! If money be deducted from your account will be refund within two to four business day.");
                                                    }

                                                    ImageView gif = v.findViewById(R.id.status_gif);

                                                    Glide.with(BillPayActivity.this)
                                                            .asGif()
                                                            .load(R.drawable.payment_failed) // Replace with your GIF file in res/drawable
                                                            .into(gif);

                                                    back.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            if (type.equals("ELECTRICITY_BIL_PAY")) {
                                                                Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                                intent.putExtra("FRAGMENT_TYPE", "ELECTRICITY_BIL_PAY");
                                                                startActivity(intent);
                                                            } else if (type.equals("BROADBAND_RECHARGE")) {
                                                                Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                                intent.putExtra("FRAGMENT_TYPE", "BROADBAND_RECHARGE");
                                                                startActivity(intent);
                                                            }else if (type.equals("MOBILE_POSTPAID")) {
                                                                Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                                intent.putExtra("FRAGMENT_TYPE", "MOBILE_POSTPAID");
                                                                startActivity(intent);
                                                            }else if (type.equals("MOBILE_PREPAID_RECHARGE")) {
                                                                Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                                intent.putExtra("FRAGMENT_TYPE", "MOBILE_PREPAID_RECHARGE");
                                                                startActivity(intent);
                                                            }else if (type.equals("GAS_CYLINDER_BOOK")) {
                                                                Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                                intent.putExtra("FRAGMENT_TYPE", "GAS_CYLINDER_BOOK");
                                                                startActivity(intent);
                                                            }else if (type.equals("FASTAG_RECHARGE")) {
                                                                Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                                intent.putExtra("FRAGMENT_TYPE", "FASTAG_RECHARGE");
                                                                startActivity(intent);
                                                            }else {
                                                                startActivity(new Intent(BillPayActivity.this, HomeActivity.class));
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                            else {
                                                //If Status be false

                                                String errorcode = obj.getString("errorcode");


                                                View v = getLayoutInflater().inflate(R.layout.payment_failed_layout, null);
                                                setContentView(v);
                                                Button back = v.findViewById(R.id.back_button);
                                                TextView msgTxt = v.findViewById(R.id.failed_msg);
                                                if (jsonObject.has("error")) {
                                                    String errorMessage = jsonObject.getString("error");
                                                    msgTxt.setText(errorMessage);

                                                } else if (jsonObject.has("msg")) {
                                                    billPayStatus=jsonObject.getString("msg");
                                                    String msg = jsonObject.getString("msg");
                                                    msgTxt.setText(msg);
                                                } else {

                                                    // If no error message is present, show a custom message
                                                    msgTxt.setText("Getting False Status during pay bill! If money be deducted from your account will be refund within two to four business day.");
                                                }

                                                ImageView gif = v.findViewById(R.id.status_gif);

                                                Glide.with(BillPayActivity.this)
                                                        .asGif()
                                                        .load(R.drawable.payment_failed) // Replace with your GIF file in res/drawable
                                                        .into(gif);

                                                back.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        if (type.equals("ELECTRICITY_BIL_PAY")) {
                                                            Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                            intent.putExtra("FRAGMENT_TYPE", "ELECTRICITY_BIL_PAY");
                                                            startActivity(intent);
                                                        } else if (type.equals("BROADBAND_RECHARGE")) {
                                                            Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                            intent.putExtra("FRAGMENT_TYPE", "BROADBAND_RECHARGE");
                                                            startActivity(intent);
                                                        }else if (type.equals("MOBILE_POSTPAID")) {
                                                            Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                            intent.putExtra("FRAGMENT_TYPE", "MOBILE_POSTPAID");
                                                            startActivity(intent);
                                                        }else if (type.equals("MOBILE_PREPAID_RECHARGE")) {
                                                            Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                            intent.putExtra("FRAGMENT_TYPE", "MOBILE_PREPAID_RECHARGE");
                                                            startActivity(intent);
                                                        }else if (type.equals("GAS_CYLINDER_BOOK")) {
                                                            Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                            intent.putExtra("FRAGMENT_TYPE", "GAS_CYLINDER_BOOK");
                                                            startActivity(intent);
                                                        }else if (type.equals("FASTAG_RECHARGE")) {
                                                            Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                            intent.putExtra("FRAGMENT_TYPE", "FASTAG_RECHARGE");
                                                            startActivity(intent);
                                                        }else {
                                                            startActivity(new Intent(BillPayActivity.this, HomeActivity.class));
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                        else {
                                            //data Not Present
                                            View v = getLayoutInflater().inflate(R.layout.payment_failed_layout, null);
                                            setContentView(v);
                                            Button back = v.findViewById(R.id.back_button);
                                            TextView msgTxt = v.findViewById(R.id.failed_msg);
                                            if (jsonObject.has("error")) {
                                                String errorMessage = jsonObject.getString("error");
                                                msgTxt.setText(errorMessage);
                                            } else if (jsonObject.has("msg")) {
                                                String msg = jsonObject.getString("msg");
                                                billPayStatus=jsonObject.getString("msg");
                                                msgTxt.setText(msg);
                                            }else if (jsonObject.has("message")) {
                                               // String message = jsonObject.getString("msg");
                                                billPayStatus=jsonObject.getString("message");
                                                msgTxt.setText(billPayStatus);
                                            }
                                            else {
                                                // If no error message is present, show a custom message
                                                msgTxt.setText("Getting from Pay Bill Error! If money be deducted from your account will be refund within two to four business day.");
                                            }

                                            ImageView gif = v.findViewById(R.id.status_gif);

                                            Glide.with(BillPayActivity.this)
                                                    .asGif()
                                                    .load(R.drawable.payment_failed) // Replace with your GIF file in res/drawable
                                                    .into(gif);

                                            back.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    if (type.equals("ELECTRICITY_BIL_PAY")) {
                                                        Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                        intent.putExtra("FRAGMENT_TYPE", "ELECTRICITY_BIL_PAY");
                                                        startActivity(intent);
                                                    } else if (type.equals("BROADBAND_RECHARGE")) {
                                                        Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                        intent.putExtra("FRAGMENT_TYPE", "BROADBAND_RECHARGE");
                                                        startActivity(intent);
                                                    }else if (type.equals("MOBILE_POSTPAID")) {
                                                        Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                        intent.putExtra("FRAGMENT_TYPE", "MOBILE_POSTPAID");
                                                        startActivity(intent);
                                                    }else if (type.equals("MOBILE_PREPAID_RECHARGE")) {
                                                        Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                        intent.putExtra("FRAGMENT_TYPE", "MOBILE_PREPAID_RECHARGE");
                                                        startActivity(intent);
                                                    }else if (type.equals("GAS_CYLINDER_BOOK")) {
                                                        Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                        intent.putExtra("FRAGMENT_TYPE", "GAS_CYLINDER_BOOK");
                                                        startActivity(intent);
                                                    }else if (type.equals("FASTAG_RECHARGE")) {
                                                        Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                        intent.putExtra("FRAGMENT_TYPE", "FASTAG_RECHARGE");
                                                        startActivity(intent);
                                                    }else {
                                                        startActivity(new Intent(BillPayActivity.this, HomeActivity.class));
                                                    }
                                                }
                                            });
                                        }
                                    }
                                    else {
                                        //Status not present
                                        View v = getLayoutInflater().inflate(R.layout.payment_failed_layout, null);
                                        setContentView(v);
                                        Button back = v.findViewById(R.id.back_button);
                                        TextView msgTxt = v.findViewById(R.id.failed_msg);
                                        if (jsonObject.has("error")) {
                                            String errorMessage = jsonObject.getString("error");
                                            msgTxt.setText(errorMessage);
                                        } else if (jsonObject.has("msg")) {
                                            String msg = jsonObject.getString("msg");
                                            billPayStatus=jsonObject.getString("msg");
                                            msgTxt.setText(msg);
                                        } else {
                                            // If no error message is present, show a custom message
                                            msgTxt.setText("Getting from Pay Bill Error! If money be deducted from your account will be refund within two to four business day.");
                                        }

                                        ImageView gif = v.findViewById(R.id.status_gif);

                                        Glide.with(BillPayActivity.this)
                                                .asGif()
                                                .load(R.drawable.payment_failed) // Replace with your GIF file in res/drawable
                                                .into(gif);

                                        back.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if (type.equals("ELECTRICITY_BIL_PAY")) {
                                                    Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                    intent.putExtra("FRAGMENT_TYPE", "ELECTRICITY_BIL_PAY");
                                                    startActivity(intent);
                                                } else if (type.equals("BROADBAND_RECHARGE")) {
                                                    Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                    intent.putExtra("FRAGMENT_TYPE", "BROADBAND_RECHARGE");
                                                    startActivity(intent);
                                                }else if (type.equals("MOBILE_POSTPAID")) {
                                                    Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                    intent.putExtra("FRAGMENT_TYPE", "MOBILE_POSTPAID");
                                                    startActivity(intent);
                                                }else if (type.equals("MOBILE_PREPAID_RECHARGE")) {
                                                    Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                    intent.putExtra("FRAGMENT_TYPE", "MOBILE_PREPAID_RECHARGE");
                                                    startActivity(intent);
                                                }else if (type.equals("GAS_CYLINDER_BOOK")) {
                                                    Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                    intent.putExtra("FRAGMENT_TYPE", "GAS_CYLINDER_BOOK");
                                                    startActivity(intent);
                                                }else if (type.equals("FASTAG_RECHARGE")) {
                                                    Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                    intent.putExtra("FRAGMENT_TYPE", "FASTAG_RECHARGE");
                                                    startActivity(intent);
                                                }else {
                                                    startActivity(new Intent(BillPayActivity.this, HomeActivity.class));
                                                }
                                            }
                                        });
                                    }

                                    //String
                                    break;
                                default:
                                    if (jsonObject.has("data")) {
                                        JSONObject obj = jsonObject.getJSONObject("data");
                                        if (obj.has("msg"))
                                        {
                                            statusMessage=obj.getString("msg");
                                           // billPayStatus=statusMessage;
                                            billPayStatus="true";
                                        }
                                        if (statusMessage=="PENDING" || statusMessage.equals("PENDING")){
                                            View v = getLayoutInflater().inflate(R.layout.payment_failed_layout, null);
                                            setContentView(v);
                                            Button back = v.findViewById(R.id.back_button);
                                            TextView msgTxt = v.findViewById(R.id.failed_msg);
                                            msgTxt.setText("Recharge Successful and will be affect in site within 24 hours");

                                            ImageView gif = v.findViewById(R.id.status_gif);

                                            Glide.with(BillPayActivity.this)
                                                    .asGif()
                                                    .load(R.drawable.payment_success) // Replace with your GIF file in res/drawable
                                                    .into(gif);

                                            back.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    if (type.equals("ELECTRICITY_BIL_PAY")) {
                                                        Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                        intent.putExtra("FRAGMENT_TYPE", "ELECTRICITY_BIL_PAY");
                                                        startActivity(intent);
                                                    } else if (type.equals("BROADBAND_RECHARGE")) {
                                                        Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                        intent.putExtra("FRAGMENT_TYPE", "BROADBAND_RECHARGE");
                                                        startActivity(intent);
                                                    }else if (type.equals("MOBILE_POSTPAID")) {
                                                        Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                        intent.putExtra("FRAGMENT_TYPE", "MOBILE_POSTPAID");
                                                        startActivity(intent);
                                                    }else if (type.equals("MOBILE_PREPAID_RECHARGE")) {
                                                        Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                        intent.putExtra("FRAGMENT_TYPE", "MOBILE_PREPAID_RECHARGE");
                                                        startActivity(intent);
                                                    }else if (type.equals("GAS_CYLINDER_BOOK")) {
                                                        Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                        intent.putExtra("FRAGMENT_TYPE", "GAS_CYLINDER_BOOK");
                                                        startActivity(intent);
                                                    }else if (type.equals("FASTAG_RECHARGE")) {
                                                        Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                        intent.putExtra("FRAGMENT_TYPE", "FASTAG_RECHARGE");
                                                        startActivity(intent);
                                                    }else {
                                                        startActivity(new Intent(BillPayActivity.this, HomeActivity.class));
                                                    }
                                                }
                                            });
                                        }
                                        else {
                                            CustomAlertDialog dialog = new CustomAlertDialog(BillPayActivity.this, R.style.WideDialog, "OneZed", statusMessage);
                                            dialog.show();
                                            dialog.onPositiveButton(view -> {
                                                //startActivity(new Intent(BillPayActivity.this, HomeActivity.class));
                                                if (type.equals("ELECTRICITY_BIL_PAY")) {
                                                    Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                    intent.putExtra("FRAGMENT_TYPE", "ELECTRICITY_BIL_PAY");
                                                    startActivity(intent);
                                                } else if (type.equals("BROADBAND_RECHARGE")) {
                                                    Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                    intent.putExtra("FRAGMENT_TYPE", "BROADBAND_RECHARGE");
                                                    startActivity(intent);
                                                } else if (type.equals("MOBILE_POSTPAID")) {
                                                    Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                    intent.putExtra("FRAGMENT_TYPE", "MOBILE_POSTPAID");
                                                    startActivity(intent);
                                                } else if (type.equals("MOBILE_PREPAID_RECHARGE")) {
                                                    Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                    intent.putExtra("FRAGMENT_TYPE", "MOBILE_PREPAID_RECHARGE");
                                                    startActivity(intent);
                                                } else if (type.equals("GAS_CYLINDER_BOOK")) {
                                                    Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                    intent.putExtra("FRAGMENT_TYPE", "GAS_CYLINDER_BOOK");
                                                    startActivity(intent);
                                                } else if (type.equals("FASTAG_RECHARGE")) {
                                                    Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                                    intent.putExtra("FRAGMENT_TYPE", "FASTAG_RECHARGE");
                                                    startActivity(intent);
                                                } else {
                                                    startActivity(new Intent(BillPayActivity.this, HomeActivity.class));
                                                }
                                                dialog.dismiss();
                                            });
                                        }
                                    }
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
            if (ContextCompat.checkSelfPermission(BillPayActivity.this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                NotificationHelper.createNotificationChannel(BillPayActivity.this);
                long triggerAtMillis = System.currentTimeMillis() + 10000; // 10 seconds
                AlarmHelper.scheduleNotification(BillPayActivity.this, triggerAtMillis);
            } else {
                // Request the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            // For devices below API 33, no need to handle notification permission
            NotificationHelper.createNotificationChannel(BillPayActivity.this);
            long triggerAtMillis = System.currentTimeMillis() + 10000; // 10 seconds
            AlarmHelper.scheduleNotification(BillPayActivity.this, triggerAtMillis);
        }


        // LOCAL NOTIFICATION END
    }

    private void billCreateApiCall() {
        JSONObject joObj = new JSONObject();
        try {
            String customerName = GlobalVariable.bbpsCustomername != null ? GlobalVariable.bbpsCustomername : "N/A"; // Check if customer name is null and set to "N/A" if so

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
        Log.v("billCreate",joObj.toString());

       // ExeDec goia=new ExeDec(getOrderIdAmount);
       // String url = String.format(goia.getDec()+"%s",finalAmount);
        new HttpHandler("https://onezed.app/api/bill-create", BillPayActivity.this)
                .setMediaType(HttpHandler.jsonMediaType)
                .setRequestType(HttpHandler.RequestType.POST)
                .setStringData(joObj.toString())
                .setLoading(true)
                .setHeaders(headers)
                .setOnResponse(new HttpHandler.HttpResponse() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        CustomAlertDialog dialog = new CustomAlertDialog(BillPayActivity.this, R.style.WideDialog, getString(R.string.app_name), getString(R.string.no_response));
                        dialog.show();
                        dialog.onPositiveButton(view ->{
                            dialog.dismiss();
                        } );
                    }

                    @Override
                    public void onResponse(int code, String response) {
                            Log.v("BillCreate",code+" "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            switch (code) {
                                case 200:

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

                                    CustomAlertDialog dialog = new CustomAlertDialog(BillPayActivity.this, R.style.WideDialog, "OneZed", "Something Wrong, Please Try after some times");
                                    dialog.show();
                                    dialog.onPositiveButton(view ->{
                                        dialog.dismiss();
                                        if (type.equals("ELECTRICITY_BIL_PAY")) {
                                            Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                            intent.putExtra("FRAGMENT_TYPE", "ELECTRICITY_BIL_PAY");
                                            startActivity(intent);
                                        } else if (type.equals("BROADBAND_RECHARGE")) {
                                            Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                            intent.putExtra("FRAGMENT_TYPE", "BROADBAND_RECHARGE");
                                            startActivity(intent);
                                        } else if (type.equals("MOBILE_POSTPAID")) {
                                            Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                            intent.putExtra("FRAGMENT_TYPE", "MOBILE_POSTPAID");
                                            startActivity(intent);
                                        } else if (type.equals("MOBILE_PREPAID_RECHARGE")) {
                                            Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                            intent.putExtra("FRAGMENT_TYPE", "MOBILE_PREPAID_RECHARGE");
                                            startActivity(intent);
                                        } else if (type.equals("GAS_CYLINDER_BOOK")) {
                                            Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                            intent.putExtra("FRAGMENT_TYPE", "GAS_CYLINDER_BOOK");
                                            startActivity(intent);
                                        }else if (type.equals("FASTAG_RECHARGE")) {
                                            Intent intent = new Intent(BillPayActivity.this, RechargeActivity.class);
                                            intent.putExtra("FRAGMENT_TYPE", "FASTAG_RECHARGE");
                                            startActivity(intent);
                                        }
                                        else {
                                            startActivity(new Intent(BillPayActivity.this, HomeActivity.class));
                                        }
                                    } );
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }).sendRequest();
    }

    // Define ActivityResultLauncher for notification permission
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission granted, proceed with notification-related tasks
                    NotificationHelper.createNotificationChannel(BillPayActivity.this);
                    long triggerAtMillis = System.currentTimeMillis() + 10000; // 10 seconds
                    AlarmHelper.scheduleNotification(BillPayActivity.this, triggerAtMillis);
                } else {
                    // Permission denied
                    Toast.makeText(BillPayActivity.this, "Notification permission is required.", Toast.LENGTH_SHORT).show();
                }
            });

}