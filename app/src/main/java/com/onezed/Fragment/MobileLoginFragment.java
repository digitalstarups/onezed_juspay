package com.onezed.Fragment;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.TELEPHONY_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import static com.onezed.GlobalVariable.baseUrl.otpVerify;
import static com.onezed.GlobalVariable.baseUrl.sendOtp;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.CountDownTimer;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.onezed.Activity.HomeActivity;
import com.onezed.Activity.StartActivity;
import com.onezed.ExeDec;
import com.onezed.GlobalVariable.AESMechanism;
import com.onezed.GlobalVariable.CustomAlertDialog;
import com.onezed.GlobalVariable.GlobalStrings;
import com.onezed.GlobalVariable.GlobalVariable;
import com.onezed.GlobalVariable.HttpHandler;
import com.onezed.GlobalVariable.SharedPrefManager;
import com.onezed.GlobalVariable.baseUrl;
import com.onezed.Model.UserProfileModel;
import com.onezed.R;
import com.onezed.databinding.FragmentMobileLoginBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;


public class MobileLoginFragment extends Fragment  {

    private FragmentMobileLoginBinding binding;
    BottomSheetDialog sheetDialog;
    TextView txtMsg;
    AppCompatButton verifyOtp;
    private EditText mEt1, mEt2, mEt3, mEt4,mEt5,mEt6, otpTxt;
    TextInputLayout otpLayout;
    ImageView imageViewPrint, imgCancelDialog;
    SharedPrefManager sharedPrefManager;
    String CurrentFirebaseToken, encryptPass, encryptId,mobileNo;
    UserProfileModel profileModel=new UserProfileModel();
    private Context mContext;

    View v;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMobileLoginBinding.inflate(inflater, container, false);
        sharedPrefManager = new SharedPrefManager(requireActivity());
        //String uniqueAndroidId = Settings.Secure.getString(this.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);



        binding.skipTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), HomeActivity.class));
            }
        });

        if (binding.termAndCondition != null) {
            binding.termAndCondition.setMovementMethod(LinkMovementMethod.getInstance());

        }
        binding.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (binding.checkBox.isChecked()) {
                    binding.btnSend.setEnabled(true);
                    binding.btnSend.setBackgroundResource(R.drawable.button_bg);
                    binding.btnSend.setTextColor(getResources().getColor(R.color.white));
                } else {
                    binding.btnSend.setEnabled(false);
                    binding.btnSend.setBackgroundResource(R.drawable.hide_button_bg);
                    binding.btnSend.setTextColor(getResources().getColor(R.color.textColor));

                }
            }
        });

        binding.etEnterNumber.post(new Runnable() {
            @Override
            public void run() {
                binding.etEnterNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        binding.tilNumber.setError(null);


                    }
                });
            }
        });

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.etEnterNumber.getText().toString().isEmpty()) {

                    binding.tilNumber.setError("Number Required");
                    binding.tilNumber.requestFocus();


                } else if (binding.etEnterNumber.getText().toString().length()<10) {
                    binding.tilNumber.setError("Enter a valid Mobile Number");
                    binding.tilNumber.requestFocus();
                } else {
                    otpApiCall();
//                    FragmentManager fm = getActivity().getSupportFragmentManager();
//                    FragmentTransaction ft = fm.beginTransaction();
//
//                    // Create an instance of your OTP Fragment (you need to create this fragment)
//                    OTPFragment otpFragment = new OTPFragment();
//
//                    // Pass the mobile number to the OTP fragment if needed
//                    Bundle args = new Bundle();
//                    args.putString("mobile_number", binding.etEnterNumber.getText().toString());
//                    otpFragment.setArguments(args);
//
//                    // Replace the current fragment with the OTP Fragment
//                    ft.replace(R.id.splash_frameLayout, otpFragment);
//                    ft.addToBackStack(null); // Add the transaction to the back stack
//                    ft.commit();



                }
            }
        });

        binding.signUpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.splash_frameLayout, new SignUpFragment());
                ft.commit();
            }
        });

        binding.termAndCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.splash_frameLayout, new TermConditionFragment());
                ft.commit();
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().finish();
            }
        });

        return binding.getRoot();
    }


    private void otpApiCall() {
        JSONObject joObj = new JSONObject();
        try {

            joObj.put("phone", binding.etEnterNumber.getText().toString());
            //AndroidId have to send


        } catch (JSONException e) {
            e.printStackTrace();
        }


        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        ExeDec so=new ExeDec(sendOtp);
        new HttpHandler(so.getDec(), getContext())
                .setMediaType(HttpHandler.jsonMediaType)
                .setRequestType(HttpHandler.RequestType.POST)
                .setStringData(joObj.toString())
                .setLoading(true)
                .setHeaders(headers)
                .setOnResponse(new HttpHandler.HttpResponse() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name), getString(R.string.no_response));
                        dialog.show();
                        dialog.onPositiveButton(view -> dialog.dismiss());
                    }

                    @Override
                    public void onResponse(int code, String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (code==200){
                                if (jsonObject.getBoolean("status")){
                                    if (jsonObject.has("redirect")) {
                                        if (jsonObject.getBoolean("redirect")) {
                                            UserProfileModel profileModel = UserProfileModel.getInstance();
                                            JSONObject dataObj = jsonObject.getJSONObject("data");
                                            profileModel.setName(dataObj.getString("name"));
                                            profileModel.setProfileId(dataObj.getInt("user_id"));
                                            profileModel.setEmail(dataObj.getString("email"));
                                            profileModel.setMobileNo(dataObj.getString("phone"));

                                            int userId = dataObj.getInt("user_id");
                                            sharedPrefManager.saveUserId(userId);

                                            profileModel.setProfileId(dataObj.getInt("user_id"));
                                            FragmentManager fm = getActivity().getSupportFragmentManager();
                                            FragmentTransaction ft = fm.beginTransaction();
                                            ft.replace(R.id.splash_frameLayout, new CreatePinFragment());
                                            ft.commit();
                                        }
                                    }else {
                                        FragmentManager fm = getActivity().getSupportFragmentManager();
                                        FragmentTransaction ft = fm.beginTransaction();

                                        // Create an instance of your OTP Fragment (you need to create this fragment)
                                        OTPFragment otpFragment = new OTPFragment();

                                        // Pass the mobile number to the OTP fragment if needed
                                        Bundle args = new Bundle();
                                        args.putString("mobile_number", binding.etEnterNumber.getText().toString());
                                        otpFragment.setArguments(args);

                                        // Replace the current fragment with the OTP Fragment
                                        ft.replace(R.id.splash_frameLayout, otpFragment);
                                        ft.commit();
                                    }

                                }
                                else {
                                    CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", jsonObject.getString("message"));
                                    dialog.show();
                                    dialog.onPositiveButton(view -> {
                                        dialog.dismiss();
                                        FragmentManager fm = getActivity().getSupportFragmentManager();
                                        FragmentTransaction ft = fm.beginTransaction();
                                        ft.replace(R.id.splash_frameLayout, new SignUpFragment());
                                        ft.commit();
                                    });
                                }
                            }

                            else if (code==422){
                                CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", jsonObject.getString("message"));
                                dialog.show();
                                dialog.onPositiveButton(view -> {
                                    dialog.dismiss();

                                });
                            }
                            else if (code==400){
                                CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", jsonObject.getString("message"));
                                dialog.show();
                                dialog.onPositiveButton(view -> {
                                    dialog.dismiss();

                                });
                            }

                            else if (code==404){ //When Mobile Number doesn't exist.
                                CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", jsonObject.getString("message"));
                                dialog.show();
                                dialog.onPositiveButton(view -> {
                                    dialog.dismiss();
                                    FragmentManager fm = getActivity().getSupportFragmentManager();
                                    FragmentTransaction ft = fm.beginTransaction();
                                    ft.replace(R.id.splash_frameLayout, new SignUpFragment());
                                    ft.commit();

                                });
                            }else {
                                CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", "Something Wrong, Please Try after some times");
                                dialog.show();
                                dialog.onPositiveButton(view -> {
                                    dialog.dismiss();

                                });
                            }


                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }


                    }

                }).sendRequest();
    }







}