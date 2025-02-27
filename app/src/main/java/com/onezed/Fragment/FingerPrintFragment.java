package com.onezed.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;
import com.onezed.Activity.HomeActivity;
import com.onezed.GlobalVariable.CustomAlertDialog;
import com.onezed.GlobalVariable.CustomToast;
import com.onezed.GlobalVariable.SharedPrefManager;
import com.onezed.R;

import java.util.concurrent.Executor;


public class FingerPrintFragment extends BottomSheetDialogFragment {

    public static final String TAG = "FingurePrintFragment";
    SharedPrefManager sharedPrefManager;
    String decryptPass, decryptRid;
    private EditText mEt1, mEt2, mEt3, mEt4, mEt5, mEt6,otpTxt;
    TextView txtView;
    AppCompatButton btn_submit;
    ImageView imgCancel,imgCancelDialog;
    String user_otp;
    BottomSheetDialog sheetDialog;
    TextView txtMsg;
    AppCompatButton verifyOtp;
    String uniqueAndroidId;
    TextInputLayout otpLayout;
    String postHttp,getHttp;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_finger_print, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPrefManager = new SharedPrefManager(requireActivity());
        uniqueAndroidId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        BiometricManager manager = BiometricManager.from(getContext());
        String info="";
        switch (manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL | BiometricManager.Authenticators.BIOMETRIC_WEAK))
        {
            case BiometricManager.BIOMETRIC_SUCCESS:
                info = "App can authenticate using biometrics.";

                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                info = "No biometric features available on this device.";
                FingerPrintFragment.this.dismiss();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                info = "Biometric features are currently unavailable.";
                FingerPrintFragment.this.dismiss();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                info = "Need register at least one finger print";
                FingerPrintFragment.this.dismiss();
                break;
            default:
                info= "Unknown cause";
                FingerPrintFragment.this.dismiss();

        }

        // creating a variable for our Executor
        Executor executor = ContextCompat.getMainExecutor(getContext());
        // this will give us result of AUTHENTICATION
        final BiometricPrompt biometricPrompt = new BiometricPrompt(getActivity(), executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                FingerPrintFragment.this.dismiss();
            }

            // THIS METHOD IS CALLED WHEN AUTHENTICATION IS SUCCESS
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                startActivity(new Intent(getActivity(), HomeActivity.class));
                getActivity().finish();

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();

                CustomToast.showToast(getContext(), "Authentication failed");
                CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed","Authentication failed" );
                dialog.getWindow().setGravity(Gravity.CENTER);
                dialog.show();
                dialog.onPositiveButton(view -> {

                    dialog.dismiss();


                });

            }
        });
        // creating a variable for our promptInfo
        // BIOMETRIC DIALOG
        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("OneZed")
                /* .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL | BiometricManager.Authenticators.BIOMETRIC_WEAK)*/

                .setDescription("Use your fingerprint to login ").setNegativeButtonText("Cancel").build();
        Thread timer = new Thread() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        biometricPrompt.authenticate(promptInfo);
                    }
                });
            }
        };
        timer.start();

    }

}
