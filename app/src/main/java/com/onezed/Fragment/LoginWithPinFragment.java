package com.onezed.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.messaging.FirebaseMessaging;
import com.onezed.Activity.HomeActivity;
import com.onezed.GlobalVariable.AlarmHelper;
import com.onezed.GlobalVariable.CustomAlertDialog;
import com.onezed.GlobalVariable.CustomToast;
import com.onezed.GlobalVariable.DeRegister_Device;
import com.onezed.GlobalVariable.GlobalVariable;
import com.onezed.GlobalVariable.NotificationHelper;
import com.onezed.GlobalVariable.SharedPrefManager;
import com.onezed.R;
import com.onezed.databinding.FragmentLoginWithPinBinding;


public class  LoginWithPinFragment extends Fragment {

    private EditText mEt1, mEt2, mEt3, mEt4,otpTxt;
    //ImageView imageViewPrint,imgCancelDialog;
    //AppCompatButton btnVerify,verifyOtp;
    private Context mContext;
    //AppCompatTextView forgotPin;
    SharedPrefManager sharedPrefManager;
    String decryptPass, decryptRid;
    BottomSheetDialog sheetDialog;
    TextView txtMsg;

    String uniqueAndroidId;
    TextInputLayout otpLayout;
    String postHttp,getHttp;
    public FragmentLoginWithPinBinding binding;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentLoginWithPinBinding.inflate(inflater, container, false);
        sharedPrefManager = new SharedPrefManager(requireActivity());

        uniqueAndroidId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // GlobalVariable.backToMain = true;

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            // handle your code here.
            int s = bundle.getInt("key");
            if (s == 1) {
                CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", getString(R.string.fingerprint_permission));
                dialog.show();
                dialog.onPositiveButton(view1 -> {
                    SharedPreferences.Editor editor = getContext().getSharedPreferences("save", MODE_PRIVATE).edit();
                    editor.putBoolean("value", true);
                    editor.apply();
                    dialog.dismiss();

                    mContext = getActivity();
                    addTextWatcher(binding.loginPinOtpEditText1);
                    addTextWatcher(binding.loginPinOtpEditText2);
                    addTextWatcher(binding.loginPinOtpEditText3);
                    addTextWatcher(binding.loginPinOtpEditText4);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {

                                SharedPreferences editor = getContext().getSharedPreferences("save", getContext().MODE_PRIVATE);
                                Boolean fingerprint = editor.getBoolean("value", true);
                                if (fingerprint == true) {
                                    FingerPrintFragment fragment = new FingerPrintFragment();
                                    fragment.show(getActivity().getSupportFragmentManager(), "FingerPrintFragment");
                                } else {
                                    Toast.makeText(mContext, "Fingerprint is Off", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }, 1000);
                });
                dialog.onNegativeButton(view1 -> {
                    SharedPreferences.Editor editor = getContext().getSharedPreferences("save", MODE_PRIVATE).edit();
                    editor.putBoolean("value", false);
                    editor.apply();

                    binding.imgFinPrint.setVisibility(View.INVISIBLE);
                    mContext = getActivity();
                    addTextWatcher(binding.loginPinOtpEditText1);
                    addTextWatcher(binding.loginPinOtpEditText2);
                    addTextWatcher(binding.loginPinOtpEditText3);
                    addTextWatcher(binding.loginPinOtpEditText4);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {

                                SharedPreferences editor = getContext().getSharedPreferences("save", getContext().MODE_PRIVATE);
                                Boolean fingerprint = editor.getBoolean("value", true);
                                if (fingerprint == true) {
                                    FingerPrintFragment fragment = new FingerPrintFragment();
                                    fragment.show(getActivity().getSupportFragmentManager(), "FingerPrintFragment");
                                } else {
                                    // Toast.makeText(mContext, "Fingerprint is Off", Toast.LENGTH_SHORT).show();
                                    CustomToast.showToast(getContext(), "Fingerprint is Off");

                                }
                            }
                        }
                    }, 1000);
                });
            }
        } else {
            mContext = getActivity();
            addTextWatcher(binding.loginPinOtpEditText1);
            addTextWatcher(binding.loginPinOtpEditText2);
            addTextWatcher(binding.loginPinOtpEditText3);
            addTextWatcher(binding.loginPinOtpEditText4);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {

                        SharedPreferences editor = getContext().getSharedPreferences("save", getContext().MODE_PRIVATE);
                        boolean fingerprint = editor.getBoolean("value", true);
                        if (fingerprint) {
                            FingerPrintFragment fragment = new FingerPrintFragment();
                            fragment.show(getActivity().getSupportFragmentManager(), "FingerPrintFragment");
                            binding.imgFinPrint.setVisibility(View.VISIBLE);
                        } else {
                            // notificationCall();
                            binding.imgFinPrint.setVisibility(View.INVISIBLE);
                           // CustomToast.showToast(getContext(), "Fingerprint Off!");
                            // Show soft keyboard programmatically when Fingerprint is OFF
                            binding.loginPinOtpEditText1.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(binding.loginPinOtpEditText1, InputMethodManager.SHOW_IMPLICIT);

                        }
                    }
                }
            }, 1000);

        }



        binding.btnLoginWithPinSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pinVerification();

            }
        });


        binding.imgFinPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {

                    SharedPreferences editor = getContext().getSharedPreferences("save", getContext().MODE_PRIVATE);
                    boolean fingerprint = editor.getBoolean("value", true);
                    if (fingerprint) {
                        FingerPrintFragment fragment = new FingerPrintFragment();
                        fragment.show(getActivity().getSupportFragmentManager(), "FingerPrintFragment");
                    } else {
                        //Toast.makeText(mContext, "Fingerprint is Off", Toast.LENGTH_SHORT).show();
                        CustomToast.showToast(getContext(), "Fingerprint is Off");
                        // Show soft keyboard programmatically when Fingerprint is OFF
                        binding.loginPinOtpEditText1.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(binding.loginPinOtpEditText1, InputMethodManager.SHOW_IMPLICIT);

                    }
                }

            }
        });
        // BACK PRESS HANDLE
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", getString(R.string.exit_msg));
                dialog.show();
                dialog.onPositiveButton(view -> {
                    dialog.dismiss();
                    getActivity().finishAffinity();
                });
                dialog.onNegativeButton(view -> {
                    dialog.dismiss();
                });
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);


        // return view;
        return binding.getRoot();
    }
//    private void notificationCall() {
//        // Get FCM Token after signup
//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w("FCM", "Fetching FCM registration token failed", task.getException());
//                            return;
//                        }
//
//                        // Get new FCM token
//                        String token = task.getResult();
//                        Log.d("FCM", "FCM Token: " + token);
//
//                        // Send the token to your server to associate with the user
//                        // sendTokenToServer(token);
//                    }
//                });
//    }
    private void pinVerification() {
        //String inputPin = mEt1.getText().toString() + mEt2.getText().toString() + mEt3.getText().toString() + mEt4.getText().toString();
        String inputPin = binding.loginPinOtpEditText1.getText().toString() + binding.loginPinOtpEditText2.getText().toString() + binding.loginPinOtpEditText3.getText().toString() + binding.loginPinOtpEditText4.getText().toString();
        String checkPin = sharedPrefManager.getUserDetails().get("stored_pin");
        if (inputPin.equals(checkPin)) {


            binding.loginPinOtpEditText1.getText().clear();
            binding.loginPinOtpEditText2.getText().clear();
            binding.loginPinOtpEditText3.getText().clear();
            binding.loginPinOtpEditText4.getText().clear();
            binding.loginPinOtpEditText1.requestFocus();

            Intent intent = new Intent(getActivity(), HomeActivity.class);
            startActivity(intent);
            getActivity().finish();


        } else {
            CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", getString(R.string.wrong_pin));
            dialog.show();
            dialog.onPositiveButton(view -> {
                binding.loginPinOtpEditText1.getText().clear();
                binding.loginPinOtpEditText2.getText().clear();
                binding.loginPinOtpEditText3.getText().clear();
                binding.loginPinOtpEditText4.getText().clear();
                binding.loginPinOtpEditText1.requestFocus();
                dialog.dismiss();
            });
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.imgFinPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {

                    SharedPreferences editor = getContext().getSharedPreferences("save", getContext().MODE_PRIVATE);
                    boolean fingerprint = editor.getBoolean("value", true);
                    if (fingerprint){
                        FingerPrintFragment fragment = new FingerPrintFragment();
                        fragment.show(getActivity().getSupportFragmentManager(), "FingerPrintFragment");
                    }
                    else {
                    }
                }

            }
        });

        //BACK PRESS HANDLE
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", getString(R.string.exit_msg));
                dialog.show();
                dialog.onPositiveButton(view ->{
                    dialog.dismiss();
                    getActivity().finishAffinity();
                });
                dialog.onNegativeButton(view -> {
                    dialog.dismiss();
                });
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

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
            public void afterTextChanged(final Editable s) {
                int id = one.getId();
                if (id == R.id.login_pin_otp_edit_text1) {
                    if (one.length() == 1) {
                        binding.loginPinOtpEditText2.requestFocus();
                    }
                } else if (id == R.id.login_pin_otp_edit_text2) {
                    if (one.length() == 1) {
                        binding.loginPinOtpEditText3.requestFocus();
                    } else if (one.length() == 0) {
                        binding.loginPinOtpEditText1.requestFocus();
                    }
                } else if (id == R.id.login_pin_otp_edit_text3) {
                    if (one.length() == 1) {
                        binding.loginPinOtpEditText4.requestFocus();
                    } else if (one.length() == 0) {
                        binding.loginPinOtpEditText2.requestFocus();
                    }
                } else if (id == R.id.login_pin_otp_edit_text4) {
                    if (one.length() == 1) {
                        InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (inputManager != null) {
                            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                        pinVerification();
                    } else if (one.length() == 0) {
                        binding.loginPinOtpEditText3.requestFocus();
                    }
                }
            }
        });
    }



}


