package com.onezed.Activity;

import static com.onezed.Fragment.FingerPrintFragment.TAG;
import static com.onezed.GlobalVariable.baseUrl.aboutUs;
import static com.onezed.GlobalVariable.baseUrl.conatctUs;
import static com.onezed.GlobalVariable.baseUrl.privacyPolicy;
import static com.onezed.GlobalVariable.baseUrl.profile;
import static java.security.AccessController.getContext;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.onezed.ExeDec;
import com.onezed.Fragment.BBPSFragment;
import com.onezed.Fragment.ViewProfile;
import com.onezed.GlobalVariable.CustomAlertDialog;
import com.onezed.GlobalVariable.DeRegister_Device;
import com.onezed.GlobalVariable.FetchPublicIPTask;
import com.onezed.GlobalVariable.GlobalVariable;
import com.onezed.GlobalVariable.HttpHandler;
import com.onezed.GlobalVariable.SharedPrefManager;
import com.onezed.GlobalVariable.WhatsAppMessageSender;
import com.onezed.Model.UserProfileModel;
import com.onezed.R;
import com.onezed.databinding.ActivityHomeBinding;
import com.onezed.databinding.AppBarLayoutBinding;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private ActivityHomeBinding binding;
    private AppBarLayoutBinding appBarLayoutBinding;
    private boolean isSearchViewVisible = false;
    SharedPrefManager sharedPrefManager;
    String storedPin;
    TextView userName,userMobile;
    ImageView profileImage;
    private FirebaseAnalytics mFirebaseAnalytics;


    private ActivityResultLauncher<String> resultLauncher =registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted ->{
                if (isGranted){
                    //permission granted
                    //Get device token from firebase
                    getDeviceToken();
                }else {
                    //permission denied
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout using View Binding
        FirebaseApp.initializeApp(this);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        appBarLayoutBinding = AppBarLayoutBinding.bind(findViewById(R.id.app_bar_layout));
        sharedPrefManager = new SharedPrefManager(HomeActivity.this);
        binding.navView.setNavigationItemSelectedListener(this);

        storedPin=sharedPrefManager.getUserDetails().get("stored_pin");
        requestPermission();
        if (storedPin != null) {

            profileApiCall();
        }else {

        }
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Fetch the FCM token when the app opens
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM Token", "Fetching FCM token failed", task.getException());
                        return;
                    }

                    // Get the token
                    String token = task.getResult();
                    Log.d("FCM Token", "Token: " + token);

                    // Send the token to your server or store it locally
                  //  sendTokenToServer(token);
                });

        // MAC ADDRESS START

        //getWIFIMAC();
        Log.v("MAC ADDRESS",getWIFIMAC());



        // MAC ADDRESS END

        String ip = getIpAddress();
        Log.v("IPAddress",ip);
        new FetchPublicIPTask().execute();
        View Navview = binding.navView.getHeaderView(0);
        userName = Navview.findViewById(R.id.user_name);
        userMobile = Navview.findViewById(R.id.user_mobile_number);
        profileImage = Navview.findViewById(R.id.profile_image);

        Log.v("ImageUrl",""+UserProfileModel.getInstance().getName());
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    Log.d(TAG, "FCM Token: " + token);
                });

        // Default Set value
        UserProfileModel.getInstance().setMobileNo(null);
        UserProfileModel.getInstance().setEmail(null);
        UserProfileModel.getInstance().setName(null);
        UserProfileModel.getInstance().setState(null);
        UserProfileModel.getInstance().setCity(null);
        UserProfileModel.getInstance().setPincode(null);
        UserProfileModel.getInstance().setImageUrl(null);
        UserProfileModel.getInstance().setAccountNo(null);
        UserProfileModel.getInstance().setAddress(null);
        // Now access the views safely
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.bill_pay_banner, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.company_registration_banner, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.recharge_banner, ScaleTypes.FIT));

        ArrayList<SlideModel> slideModelsLeft = new ArrayList<>();
        slideModelsLeft.add(new SlideModel(R.drawable.recharge_banner, ScaleTypes.CENTER_CROP));
        slideModelsLeft.add(new SlideModel(R.drawable.bill_pay_banner, ScaleTypes.CENTER_CROP));
        slideModelsLeft.add(new SlideModel(R.drawable.company_registration_banner, ScaleTypes.CENTER_CROP));

        ArrayList<SlideModel> slideModelsRight = new ArrayList<>();
        slideModelsRight.add(new SlideModel(R.drawable.company_registration_banner, ScaleTypes.CENTER_CROP));
        slideModelsRight.add(new SlideModel(R.drawable.recharge_banner, ScaleTypes.CENTER_CROP));
        slideModelsRight.add(new SlideModel(R.drawable.bill_pay_banner, ScaleTypes.CENTER_CROP));


        appBarLayoutBinding.businessRegistrationTxt.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if ( appBarLayoutBinding.businessRegistrationTxt.getLineCount() > 2 ||
                    appBarLayoutBinding.businessRegistrationIhTxt.getLineCount() > 2) {
                appBarLayoutBinding.businessRegistrationTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12); // Decrease the text size
                appBarLayoutBinding.bbpsRechargeTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12); // Decrease the text size
                appBarLayoutBinding.licenceRegistrationTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12); // Decrease the text size
                appBarLayoutBinding.accountingEfilingTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12); // Decrease the text size

                appBarLayoutBinding.mobileRechargeTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12); // Decrease the text size
                appBarLayoutBinding.mobilePostpaidTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12); // Decrease the text size
                appBarLayoutBinding.electricBillTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12); // Decrease the text size
                appBarLayoutBinding.broadbandRechargeTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12); // Decrease the text size

                appBarLayoutBinding.businessRegistrationIhTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12); // Decrease the text size
                appBarLayoutBinding.licenceRegistrationIhTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12); // Decrease the text size
                appBarLayoutBinding.accountEfilingIhTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12); // Decrease the text size
                appBarLayoutBinding.brandProtectionIhTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12); // Decrease the text size

            }
        });

        // Set the image list for the image slider
        appBarLayoutBinding.bannerImageSlider.setImageList(slideModels);

        binding.transactionHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(HomeActivity.this, HomeActivity.class));
                if (storedPin==null){
                    CustomAlertDialog dialog = new CustomAlertDialog(HomeActivity.this, R.style.WideDialog, getString(R.string.app_name), "You have not Sign In. Sign In to Continue");
                    dialog.show();
                    dialog.onPositiveButton(v ->{
                        dialog.dismiss();
                        startActivity(new Intent(HomeActivity.this,StartActivity.class));

                    } );
                }else {
                    Intent intent = new Intent(HomeActivity.this, RechargeActivity.class);
                    intent.putExtra("FRAGMENT_TYPE", "TRANSACTION_HISTORY");
                    startActivity(intent);
                }

            }
        });
        binding.privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExeDec pp=new ExeDec(privacyPolicy);
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.putExtra("URL",pp.getDec() ); // Replace with actual URL
                startActivity(intent);
            }
        });
        binding.contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExeDec cs=new ExeDec(conatctUs);
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.putExtra("URL",cs.getDec() ); // Replace with actual URL
                startActivity(intent);
            }
        });
        binding.about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExeDec as=new ExeDec(aboutUs);
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.putExtra("URL", as.getDec()); // Replace with actual URL
                startActivity(intent);
            }
        });



        appBarLayoutBinding.mobileRecharge.setOnClickListener(view -> {
            if (storedPin==null){
                CustomAlertDialog dialog = new CustomAlertDialog(HomeActivity.this, R.style.WideDialog, getString(R.string.app_name), "You have not Sign In. Sign In to Continue");
                dialog.show();
                dialog.onPositiveButton(v ->{
                    dialog.dismiss();
                    startActivity(new Intent(HomeActivity.this,StartActivity.class));

                } );
            }else {
                Intent intent = new Intent(HomeActivity.this, RechargeActivity.class);
                intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
                startActivity(intent);
            }

        });
        appBarLayoutBinding.electricityBilPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (storedPin == null) {
                    CustomAlertDialog dialog = new CustomAlertDialog(HomeActivity.this, R.style.WideDialog, getString(R.string.app_name), "You have not Sign In. Sign In to Continue");
                    dialog.show();
                    dialog.onPositiveButton(v -> {
                        dialog.dismiss();
                        startActivity(new Intent(HomeActivity.this, StartActivity.class));

                    });
                } else {
                    Intent intent = new Intent(HomeActivity.this, RechargeActivity.class);
                    intent.putExtra("FRAGMENT_TYPE", "ELECTRICITY_BIL_PAY");
                    startActivity(intent);

                }

            }
        });
        appBarLayoutBinding.broadbandRecharge.setOnClickListener(view -> {
            if (storedPin==null){
                CustomAlertDialog dialog = new CustomAlertDialog(HomeActivity.this, R.style.WideDialog, getString(R.string.app_name), "You have not Sign In. Sign In to Continue");
                dialog.show();
                dialog.onPositiveButton(v ->{
                    dialog.dismiss();
                    startActivity(new Intent(HomeActivity.this,StartActivity.class));

                } );
            }else {
                Intent intent = new Intent(HomeActivity.this, RechargeActivity.class);
                intent.putExtra("FRAGMENT_TYPE", "BROADBAND_RECHARGE");
                startActivity(intent);
            }

        });
        appBarLayoutBinding.mobilePostpaidRecharge.setOnClickListener(view -> {
            if (storedPin==null){
                CustomAlertDialog dialog = new CustomAlertDialog(HomeActivity.this, R.style.WideDialog, getString(R.string.app_name), "You have not Sign In. Sign In to Continue");
                dialog.show();
                dialog.onPositiveButton(v ->{
                    dialog.dismiss();
                    startActivity(new Intent(HomeActivity.this,StartActivity.class));

                } );
            }else {
                Intent intent = new Intent(HomeActivity.this, RechargeActivity.class);
                intent.putExtra("FRAGMENT_TYPE", "MOBILE_POSTPAID");
                startActivity(intent);

            }

        });
        appBarLayoutBinding.businessRegistrationQl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (storedPin == null) {
                    CustomAlertDialog dialog = new CustomAlertDialog(HomeActivity.this, R.style.WideDialog, getString(R.string.app_name), "You have not Sign In. Sign In to Continue");
                    dialog.show();
                    dialog.onPositiveButton(v -> {
                        dialog.dismiss();
                        startActivity(new Intent(HomeActivity.this, StartActivity.class));

                    });
                } else {
                  //  startActivity(new Intent(HomeActivity.this, InhouseActivity.class));
                    Intent intent = new Intent(HomeActivity.this, InhouseActivity.class);
                    intent.putExtra("FRAGMENT_TYPE", "BUSINESS_REGISTRATION");
                    startActivity(intent);
                }


            }
        });
        appBarLayoutBinding.licenceRegistrationQl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (storedPin == null) {
                    CustomAlertDialog dialog = new CustomAlertDialog(HomeActivity.this, R.style.WideDialog, getString(R.string.app_name), "You have not Sign In. Sign In to Continue");
                    dialog.show();
                    dialog.onPositiveButton(v -> {
                        dialog.dismiss();
                        startActivity(new Intent(HomeActivity.this, StartActivity.class));

                    });
                } else {
                    Intent intent = new Intent(HomeActivity.this, InhouseActivity.class);
                    intent.putExtra("FRAGMENT_TYPE", "LICENCE_REGISTRATION");
                    startActivity(intent);
                }


            }
        });
        appBarLayoutBinding.accountingAndEfilingQl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (storedPin == null) {
                    CustomAlertDialog dialog = new CustomAlertDialog(HomeActivity.this, R.style.WideDialog, getString(R.string.app_name), "You have not Sign In. Sign In to Continue");
                    dialog.show();
                    dialog.onPositiveButton(v -> {
                        dialog.dismiss();
                        startActivity(new Intent(HomeActivity.this, StartActivity.class));

                    });
                } else {
                    Intent intent = new Intent(HomeActivity.this, InhouseActivity.class);
                    intent.putExtra("FRAGMENT_TYPE", "ACCOUNTING_EFINING");
                    startActivity(intent);
                }


            }
        });
        appBarLayoutBinding.brandProtection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (storedPin == null) {
                    CustomAlertDialog dialog = new CustomAlertDialog(HomeActivity.this, R.style.WideDialog, getString(R.string.app_name), "You have not Sign In. Sign In to Continue");
                    dialog.show();
                    dialog.onPositiveButton(v -> {
                        dialog.dismiss();
                        startActivity(new Intent(HomeActivity.this, StartActivity.class));

                    });
                } else {
                    Intent intent = new Intent(HomeActivity.this, InhouseActivity.class);
                    intent.putExtra("FRAGMENT_TYPE", "BRAND_PROTECTION");
                    startActivity(intent);
                }


            }
        });
        appBarLayoutBinding.businessRegistrationTsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (storedPin == null) {
                    CustomAlertDialog dialog = new CustomAlertDialog(HomeActivity.this, R.style.WideDialog, getString(R.string.app_name), "You have not Sign In. Sign In to Continue");
                    dialog.show();
                    dialog.onPositiveButton(v -> {
                        dialog.dismiss();
                        startActivity(new Intent(HomeActivity.this, StartActivity.class));

                    });
                } else {
                    Intent intent = new Intent(HomeActivity.this, InhouseActivity.class);
                    intent.putExtra("FRAGMENT_TYPE", "BUSINESS_REGISTRATION");
                    startActivity(intent);
                }


            }
        });
        appBarLayoutBinding.licenceRegistrationTsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (storedPin == null) {
                    CustomAlertDialog dialog = new CustomAlertDialog(HomeActivity.this, R.style.WideDialog, getString(R.string.app_name), "You have not Sign In. Sign In to Continue");
                    dialog.show();
                    dialog.onPositiveButton(v -> {
                        dialog.dismiss();
                        startActivity(new Intent(HomeActivity.this, StartActivity.class));

                    });
                } else {
                    Intent intent = new Intent(HomeActivity.this, InhouseActivity.class);
                    intent.putExtra("FRAGMENT_TYPE", "LICENCE_REGISTRATION");
                    startActivity(intent);
                }


            }
        });

        appBarLayoutBinding.accountingAndEfilingTsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (storedPin == null) {
                    CustomAlertDialog dialog = new CustomAlertDialog(HomeActivity.this, R.style.WideDialog, getString(R.string.app_name), "You have not Sign In. Sign In to Continue");
                    dialog.show();
                    dialog.onPositiveButton(v -> {
                        dialog.dismiss();
                        startActivity(new Intent(HomeActivity.this, StartActivity.class));

                    });
                } else {
                    Intent intent = new Intent(HomeActivity.this, InhouseActivity.class);
                    intent.putExtra("FRAGMENT_TYPE", "ACCOUNTING_EFINING");
                    startActivity(intent);
                }


            }
        });

        appBarLayoutBinding.bbpsRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, RechargeActivity.class);
                intent.putExtra("FRAGMENT_TYPE", "BBPS_RECHARGE");
                //intent.putExtra("FRAGMENT_PAGE", "BBPS_PAGE");
                startActivity(intent);
            }
        });
        appBarLayoutBinding.profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawerLayout.openDrawer(binding.navView);

            }
        });




        //BACK PRESS HANDLER
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {

                CustomAlertDialog dialog = new CustomAlertDialog(HomeActivity.this, R.style.WideDialog, "OneZed", getString(R.string.exit_msg));


                dialog.show();
                dialog.onPositiveButton(view -> {
                    dialog.dismiss();
                    finishAffinity();
                    // finish();
                });
                dialog.onNegativeButton(view -> {
                    dialog.dismiss();
                    // Handle negative button action
                });

            }
        };
        getOnBackPressedDispatcher().addCallback(HomeActivity.this, callback);
        //    }


    }


        public static String getWIFIMAC() {
            try {
                String interfaceName = "wlan0";
                List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
                for (NetworkInterface inter_face : interfaces) {
                    if (!inter_face.getName().equalsIgnoreCase(interfaceName)){
                        continue;
                    }
                    byte[] mac = inter_face.getHardwareAddress();
                    if (mac==null){
                        return "";
                    }

                    StringBuilder buffer = new StringBuilder();
                    for (byte aMac : mac) {
                        buffer.append(String.format("%02X:", aMac));
                    }
                    if (buffer.length()>0) {
                        buffer.deleteCharAt(buffer.length() - 1);
                    }
                    return buffer.toString();
                }
            } catch (Exception ignored) { }
            return "null";
        }


    private void profileApiCall() {

        int userId=sharedPrefManager.getUserId();

        ExeDec pf=new ExeDec(profile);
        String url = String.format(pf.getDec()+"%s", userId);

        new HttpHandler(url, HomeActivity.this)
                .setMediaType(HttpHandler.jsonMediaType)
                .setRequestType(HttpHandler.RequestType.GET)
                //.setStringData(joObj.toString())
                // .setLoading(true)
                //.setHeaders(headers)
                .setOnResponse(new HttpHandler.HttpResponse() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        CustomAlertDialog dialog = new CustomAlertDialog(HomeActivity.this, R.style.WideDialog, getString(R.string.app_name), getString(R.string.no_response));
                        dialog.show();
                        dialog.onPositiveButton(view -> dialog.dismiss());
                    }

                    @Override
                    public void onResponse(int code, String response) {
                        Log.v("Profile",response);
                        try {
                            JSONObject jsonObject = new JSONObject(response); //Value [] at data of type org.json.JSONArray cannot be converted to JSONObject
                            CustomAlertDialog dialog;
                            switch (code) {
                                case 400:
                                    dialog = new CustomAlertDialog(HomeActivity.this, R.style.WideDialog, "OneZed", jsonObject.getString("message"));
                                    dialog.show();
                                    dialog.onPositiveButton(view -> dialog.dismiss());
                                    break;
                                case 404:
                                    if (jsonObject.getBoolean("status")){

                                        dialog = new CustomAlertDialog(HomeActivity.this, R.style.WideDialog, "OneZed", jsonObject.getString("message"));
                                        dialog.show();
                                        dialog.onPositiveButton(view -> dialog.dismiss());
                                    }else {

                                        dialog = new CustomAlertDialog(HomeActivity.this, R.style.WideDialog, "OneZed", jsonObject.getString("message"));
                                        dialog.show();
                                        dialog.onPositiveButton(view -> {
                                            startActivity(new Intent(HomeActivity.this, StartActivity.class));
                                        });


                                    }
                                    break;

                                case 200:
                                    JSONObject dataObj=jsonObject.getJSONObject("data");

                                    String name= dataObj.getString("name");
                                    String email= dataObj.getString("email");
                                    String phone= dataObj.getString("phone");
                                    String city= dataObj.getString("city");
                                    String state= dataObj.getString("state");
                                    String pincode= dataObj.getString("pincode");
                                    String updatedate= dataObj.getString("updated_at");
                                    String imageUrl= dataObj.getString("profile_img");
                                    String address= dataObj.getString("address");
                                    String userId= dataObj.getString("user_id");
                                    String accountNo = dataObj.optString("AccountNo", "NULL");

                                    Log.v("image",imageUrl);
                                    //pin store in share preference
                                    sharedPrefManager.saveImageUrl(imageUrl);

                                    UserProfileModel.getInstance().setMobileNo(phone);
                                    UserProfileModel.getInstance().setEmail(email);
                                    UserProfileModel.getInstance().setName(name);
                                    UserProfileModel.getInstance().setState(state);
                                    UserProfileModel.getInstance().setCity(city);
                                    UserProfileModel.getInstance().setPincode(pincode);
                                    UserProfileModel.getInstance().setImageUrl(imageUrl);
                                    UserProfileModel.getInstance().setAccountNo(accountNo);
                                    UserProfileModel.getInstance().setAddress(address);
                                    UserProfileModel.getInstance().setProfileId(Integer.valueOf(userId));

                                    userName.setText(UserProfileModel.getInstance().getName());
                                    userMobile.setText(UserProfileModel.getInstance().getMobileNo());
                                    Glide.with(HomeActivity.this)
                                            .load(imageUrl)  // Network image URL
                                            .placeholder(R.drawable.ic_baseline_account_circle_profile)  // Optional: placeholder while loading
                                            .error(R.drawable.ic_baseline_account_circle_profile)  // Optional: error image if loading fails
                                            .into(profileImage);
                                    Glide.with(HomeActivity.this)
                                            .load( sharedPrefManager.getProfileImageUrl())  // Network image URL
                                            .placeholder(R.drawable.ic_baseline_account_circle_profile)  // Optional: placeholder while loading
                                            .error(R.drawable.ic_baseline_account_circle_profile)  // Optional: error image if loading fails
                                            .into(appBarLayoutBinding.profileImg);
                                    break;
                                default:
                                    dialog = new CustomAlertDialog(HomeActivity.this, R.style.WideDialog, "OneZed", "Something Wrong, Please Try after some times");
                                    dialog.show();
                                    dialog.onPositiveButton(view -> dialog.dismiss());
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }

                }).sendRequest();
    }
    public void requestPermission(){
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED){
                //Permission already Granted
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                //You can explain user that do you need permission by showing dialogue box or mToast message

            }else {
                resultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }else {
            //Get device Token from Firebase
            getDeviceToken();
        }
    }
    public void getDeviceToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()){

                    return;
                }
                String token= task.getResult();

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
       // Log.v("Navigation",id+"navigation");
        if (id == R.id.my_profile) {
            binding.drawerLayout.closeDrawer(Gravity.LEFT);
           // Toast.makeText(this, "My Profile", Toast.LENGTH_SHORT).show();
           // startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
           // overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            if (storedPin==null){
                CustomAlertDialog dialog = new CustomAlertDialog(HomeActivity.this, R.style.WideDialog, getString(R.string.app_name), "You have not Sign In. Sign In to Continue");
                dialog.show();
                dialog.onPositiveButton(v ->{
                    dialog.dismiss();
                    //startActivity(new Intent(HomeActivity.this,StartActivity.class));

                } );
            }else {
                Intent intent = new Intent(HomeActivity.this, RechargeActivity.class);
                intent.putExtra("FRAGMENT_TYPE", "VIEW_PROFILE");
                startActivity(intent);
            }
        } else if (id ==R.id.log_out) {
            CustomAlertDialog dialog = new CustomAlertDialog(HomeActivity.this, R.style.WideDialog, "OneZed", "Are you sure! you want to logout ?");
            dialog.show();
            dialog.onPositiveButton(view1 -> {
                dialog.dismiss();
                DeRegister_Device deRegister_device=new DeRegister_Device(HomeActivity.this);
                deRegister_device.init();
                finish();

            });
            dialog.onNegativeButton(new CustomAlertDialog.ClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
       return true;
    }
    private void sendWhatsAppMessage(String phoneNumber, String message) {
        // Check if WhatsApp is installed
        PackageManager packageManager =getPackageManager();
        try {
            // Create the Intent to send the message
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + Uri.encode(message)));

            // Check if WhatsApp is installed
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "WhatsApp is not installed.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error while sending message.", Toast.LENGTH_SHORT).show();
        }
    }
    public String getIpAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : interfaces) {
                List<InetAddress> addresses = Collections.list(networkInterface.getInetAddresses());
                for (InetAddress address : addresses) {
                    if (!address.isLoopbackAddress() && address.isSiteLocalAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "Unable to fetch IP address";
    }
}