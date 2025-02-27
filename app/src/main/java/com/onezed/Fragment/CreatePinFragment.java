package com.onezed.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.onezed.GlobalVariable.AlarmHelper;
import com.onezed.GlobalVariable.CustomAlertDialog;
import com.onezed.GlobalVariable.GlobalVariable;
import com.onezed.GlobalVariable.NotificationHelper;
import com.onezed.GlobalVariable.SharedPrefManager;
import com.onezed.R;


public class CreatePinFragment extends Fragment {

    private EditText mEt1, mEt2, mEt3, mEt4;
    private EditText nEt1, nEt2, nEt3, nEt4;

    SharedPrefManager sharedPrefManager;
    private Context mContext;
    AppCompatButton button;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //binding = FragmentCreatPinBinding.inflate(inflater, container, false);
        View view = inflater.inflate(R.layout.fragment_create_pin, container, false);
        button = view.findViewById(R.id.btn_verify);
        mEt1 = view.findViewById(R.id.otp_edit_text1);
        mEt2 = view.findViewById(R.id.otp_edit_text2);
        mEt3 = view.findViewById(R.id.otp_edit_text3);
        mEt4 = view.findViewById(R.id.otp_edit_text4);
        sharedPrefManager = new SharedPrefManager(requireActivity());
        nEt1 = view.findViewById(R.id.s_otp_edit_text1);
        nEt2 = view.findViewById(R.id.s_otp_edit_text2);
        nEt3 = view.findViewById(R.id.s_otp_edit_text3);
        nEt4 = view.findViewById(R.id.s_otp_edit_text4);


        mContext = getActivity();
        addTextWatcher(mEt1);
        addTextWatcher(mEt2);
        addTextWatcher(mEt3);
        addTextWatcher(mEt4);

        addSecondTextWatcher(nEt1);
        addSecondTextWatcher(nEt2);
        addSecondTextWatcher(nEt3);
        addSecondTextWatcher(nEt4);
        ImageView ShowPIn = view.findViewById(R.id.show_pin_img);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidationCheck();
            }
        });


        ShowPIn.setOnClickListener(new View.OnClickListener() {
            boolean flag = false;

            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if (flag == false) {
                    mEt1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mEt2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mEt3.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mEt4.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ShowPIn.setColorFilter(ShowPIn.getContext().getResources().getColor(R.color.logo_blue), PorterDuff.Mode.SRC_ATOP);
                    Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_visibility_off);
                    ShowPIn.setImageDrawable(drawable);

                    flag = true;

                } else {
                    mEt1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mEt2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mEt3.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mEt4.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ShowPIn.setColorFilter(ShowPIn.getContext().getResources().getColor(R.color.logo_blue), PorterDuff.Mode.SRC_ATOP);
                    Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_remove_red_eye);
                    ShowPIn.setImageDrawable(drawable);
                    flag = false;
                }


            }
        });

        //Back Press
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event

                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.splash_frameLayout, new MobileLoginFragment());
                //ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                ft.commit();

            }

        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);
        // return binding.getRoot();
        return view;
    }

    private void addSecondTextWatcher(final EditText second) {
        second.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged( Editable editable) {
                int id = second.getId();

                if (id == R.id.s_otp_edit_text1) {
                    if (second.length() == 1) {
                        nEt2.requestFocus();
                    }
                } else if (id == R.id.s_otp_edit_text2) {
                    if (second.length() == 1) {
                        nEt3.requestFocus();
                    } else if (second.length() == 0) {
                        nEt1.requestFocus();
                    }
                } else if (id == R.id.s_otp_edit_text3) {
                    if (second.length() == 1) {
                        nEt4.requestFocus();
                    } else if (second.length() == 0) {
                        nEt2.requestFocus();
                    }
                } else if (id == R.id.s_otp_edit_text4) {
                    if (second.length() == 1) {
                        InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (inputManager != null) {
                            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    } else if (second.length() == 0) {
                        nEt3.requestFocus();
                    }
                }
            }
        });
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
                        mEt2.requestFocus();
                    }
                } else if (id == R.id.otp_edit_text2) {
                    if (one.length() == 1) {
                        mEt3.requestFocus();
                    } else if (one.length() == 0) {
                        mEt1.requestFocus();
                    }
                } else if (id == R.id.otp_edit_text3) {
                    if (one.length() == 1) {
                        mEt4.requestFocus();
                    } else if (one.length() == 0) {
                        mEt2.requestFocus();
                    }
                } else if (id == R.id.otp_edit_text4) {
                    if (one.length() == 1) {
                        InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (inputManager != null) {
                            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                        nEt1.requestFocus();
                        showKeyboard(nEt1);
                    } else if (one.length() == 0) {
                        mEt3.requestFocus();

                    }
                }
            }
        });
    }

    private void ValidationCheck() {
        String s1 = mEt1.getText().toString() + mEt2.getText().toString() + mEt3.getText().toString() + mEt4.getText().toString();
        String s2 = nEt1.getText().toString() + nEt2.getText().toString() + nEt3.getText().toString() + nEt4.getText().toString();
        if (s1 == null || s2 == null) {
            CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", "" + "Enter pin First");
            dialog.getWindow().setGravity(Gravity.CENTER);

            dialog.show();
            dialog.onPositiveButton(view -> {
                dialog.dismiss();

            });
        } else if (s1.equals(s2)) {
            String s = mEt1.getText().toString() + mEt2.getText().toString() + mEt3.getText().toString() + mEt4.getText().toString();

            //pin store in share preference
            sharedPrefManager.saveUserPin(s);

            Bundle bundle = new Bundle();
            bundle.putInt("key",1);
            LoginWithPinFragment fragment2 = new LoginWithPinFragment();
            fragment2.setArguments(bundle);

            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.splash_frameLayout, fragment2)
                    .commit();
//            //LOCAL NOTIFICATION START

//            // Create the notification channel
//            NotificationHelper.createNotificationChannel(getContext());
//
//            // Schedule a notification for 10 seconds from now
//            long triggerAtMillis = System.currentTimeMillis() + 10000; // 10 seconds
//            AlarmHelper.scheduleNotification(getContext(), triggerAtMillis);

            GlobalVariable.LocalNotificationText="Login Successfully";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted
                    NotificationHelper.createNotificationChannel(getContext());
                    long triggerAtMillis = System.currentTimeMillis() + 10000; // 10 seconds
                    AlarmHelper.scheduleNotification(getContext(), triggerAtMillis);
                } else {
                    // Request the permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                }
            } else {
                // For devices below API 33, no need to handle notification permission
                NotificationHelper.createNotificationChannel(getContext());
                long triggerAtMillis = System.currentTimeMillis() + 10000; // 10 seconds
                AlarmHelper.scheduleNotification(getContext(), triggerAtMillis);
            }


//            // LOCAL NOTIFICATION END

        } else {
            CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", "" + "Pin Mismatch");
            dialog.getWindow().setGravity(Gravity.CENTER);

            dialog.show();
            dialog.onPositiveButton(view -> {
                dialog.dismiss();

            });


        }
    }

    private void showKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }
    // Define ActivityResultLauncher for notification permission
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission granted, proceed with notification-related tasks
                    NotificationHelper.createNotificationChannel(getContext());
                    long triggerAtMillis = System.currentTimeMillis() + 10000; // 10 seconds
                    AlarmHelper.scheduleNotification(getContext(), triggerAtMillis);
                } else {
                    // Permission denied
                    Toast.makeText(getContext(), "Notification permission is required.", Toast.LENGTH_SHORT).show();
                }
            });
}