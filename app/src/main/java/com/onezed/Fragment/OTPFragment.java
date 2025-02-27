package com.onezed.Fragment;

import static com.onezed.GlobalVariable.baseUrl.otpVerify;
import static com.onezed.GlobalVariable.baseUrl.sendOtp;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.onezed.ExeDec;
import com.onezed.GlobalVariable.CustomAlertDialog;
import com.onezed.GlobalVariable.HttpHandler;
import com.onezed.GlobalVariable.SharedPrefManager;
import com.onezed.Model.UserProfileModel;
import com.onezed.R;
import com.onezed.databinding.FragmentMobileLoginBinding;
import com.onezed.databinding.FragmentOTPBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;


public class OTPFragment extends Fragment {

   private FragmentOTPBinding binding;

    private EditText mEt1, mEt2, mEt3, mEt4,mEt5,mEt6, otpTxt;
    TextInputLayout otpLayout;
    ImageView imageViewPrint, imgCancelDialog;
    SharedPrefManager sharedPrefManager;
    String CurrentFirebaseToken, encryptPass, encryptId,mobileNumber,inputOtp;
    UserProfileModel profileModel=new UserProfileModel();
    private Context mContext;
    boolean otpPage=false;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOTPBinding.inflate(inflater, container, false);
        sharedPrefManager = new SharedPrefManager(requireActivity());

        mobileNumber = getArguments().getString("mobile_number");
       // Log.v("data ",mobileNumber);
        mContext = getActivity();
        addTextWatcher(binding.otpEditText1);
        addTextWatcher(binding.otpEditText2);
        addTextWatcher(binding.otpEditText3);
        addTextWatcher(binding.otpEditText4);
        addTextWatcher(binding.otpEditText5);
        addTextWatcher(binding.otpEditText6);

        binding.verificationMsgTxt.setText("An SMS confirmation code has been send to "+mobileNumber);

        binding.timerTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otpApiCall();
            }
        });

        binding.btnOtpVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputOtp= binding.otpEditText1.getText().toString() + binding.otpEditText2.getText().toString() + binding.otpEditText3.getText().toString() + binding.otpEditText4.getText().toString()+binding.otpEditText5.getText().toString()+binding.otpEditText6.getText().toString();

                if (inputOtp.isEmpty()){
                    CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name), "Wrong OTP");
                    dialog.show();
                    dialog.onPositiveButton(v -> dialog.dismiss());
                }else {

                     checkVerification();

                }
            }
        });

        // Start the countdown timer for 1 minute (60,000 milliseconds)
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                // Update the timer TextView every second
                long secondsRemaining = millisUntilFinished / 1000;
                binding.timerTxt.setText(String.format(Locale.getDefault(), "%02d:%02d", secondsRemaining / 60, secondsRemaining % 60));
            }

            public void onFinish() {
                // When the timer finishes, set the text to 00:00
                binding.timerTxt.setText(" RESEND OTP");
                // Optionally, do something when the timer ends (e.g., resend OTP)
            }
        }.start();

        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.splash_frameLayout, new MobileLoginFragment());
                ft.commit();
            }
        });

        return binding.getRoot();

    }

    private void addTextWatcher(final EditText one) {
        one.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int id = one.getId();

                if (id == R.id.otp_edit_text1) {
                    if (one.length() == 1) {
                        binding.otpEditText2.requestFocus();
                    }
                } else if (id == R.id.otp_edit_text2) {
                    if (one.length() == 1) {
                        binding.otpEditText3.requestFocus();
                    } else if (one.length() == 0) {
                        binding.otpEditText1.requestFocus();
                    }
                } else if (id == R.id.otp_edit_text3) {
                    if (one.length() == 1) {
                        binding.otpEditText4.requestFocus();
                    } else if (one.length() == 0) {
                        binding.otpEditText2.requestFocus();
                    }
                }
                else if (id == R.id.otp_edit_text4) {
                    if (one.length() == 1) {
                        binding.otpEditText5.requestFocus();
                    } else if (one.length() == 0) {
                        binding.otpEditText3.requestFocus();
                    }
                }
                else if (id == R.id.otp_edit_text5) {
                    if (one.length() == 1) {
                        binding.otpEditText6.requestFocus();
                    } else if (one.length() == 0) {
                        binding.otpEditText4.requestFocus();
                    }
                }

                else if (id == R.id.otp_edit_text6) {
                    if (one.length() == 1) {
                        InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (inputManager != null) {
                            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                        //nEt1.requestFocus();

                    } else if (one.length() == 0) {
                        binding.otpEditText5.requestFocus();

                    }
                }
            }
        });
    }
    private void checkVerification() {

        JSONObject joObj = new JSONObject();
        try {
            // Add basic fields to the main JSON object
            joObj.put("phone",mobileNumber );
            joObj.put("otp",inputOtp);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        ExeDec ov=new ExeDec(otpVerify);

        new HttpHandler(ov.getDec(), getContext())
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
                            if (code ==400){
                                CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", jsonObject.getString("message"));
                                dialog.show();
                                dialog.onPositiveButton(view -> {
                                    dialog.dismiss();


                                });
                            } else if (code==200) {
                                JSONObject dataObj=jsonObject.getJSONObject("data");
                                int profileId=dataObj.getInt("id");

                                sharedPrefManager.saveUserId(profileId);

                                CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", jsonObject.getString("message"));
                                dialog.show();
                                dialog.onPositiveButton(view -> {

                                    FragmentManager fm = getActivity().getSupportFragmentManager();
                                    FragmentTransaction ft = fm.beginTransaction();
                                    ft.replace(R.id.splash_frameLayout, new CreatePinFragment());
                                    ft.commit();
                                    // dialog.dismiss();
                                });
                            }else {
                                CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", "Something Wrong, Please Try after some times");
                                dialog.show();
                                dialog.onPositiveButton(view -> {
                                    dialog.dismiss();

                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }

                }).sendRequest();
    }

    private void otpApiCall() {
        JSONObject joObj = new JSONObject();
        try {

            joObj.put("phone", mobileNumber);



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
                                    }


                                }
                                else {
                                    if (jsonObject.has("message")){
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
                                        CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", "Something went wrong! Please try after sometimes");
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
                            }
                            if (jsonObject.has("message")){
                                CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", jsonObject.getString("message"));
                                dialog.show();
                                dialog.onPositiveButton(view -> {
                                    dialog.dismiss();

                                });
                            }

                            else {
                                CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed","Something went wrong! Please try after sometimes");
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