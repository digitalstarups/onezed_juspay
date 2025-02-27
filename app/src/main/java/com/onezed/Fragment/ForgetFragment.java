package com.onezed.Fragment;

import static com.onezed.GlobalVariable.baseUrl.resetPassword;
import static com.onezed.GlobalVariable.baseUrl.userDetails;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.onezed.ExeDec;
import com.onezed.GlobalVariable.CustomAlertDialog;
import com.onezed.GlobalVariable.HttpHandler;
import com.onezed.R;
import com.onezed.databinding.FragmentForgetBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;


public class ForgetFragment extends Fragment {

    public FragmentForgetBinding binding;
    BottomSheetDialog sheetDialog;
    TextView txtMsg;
    AppCompatButton verifyOtp;
    EditText otpTxt;
    ImageView  imgCancelDialog;
    TextInputLayout otpLayout;
    public ForgetFragment() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentForgetBinding.inflate(inflater, container, false);

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation()){
                    checkMobileVerification();
                }
            }
        });
        binding.btnRecoverSubmit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkVerification();
            }
        });

        binding.etForgetNumber.post(new Runnable() {
            @Override
            public void run() {
                binding.etForgetNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        binding.tilForgetNumber.setError(null);
                    }
                });
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
        return binding.getRoot();
    }

    private void checkMobileVerification() {
        JSONObject joObj = new JSONObject();
        String mobileNumber=binding.etForgetNumber.getText().toString();


        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        ExeDec ud=new ExeDec(userDetails);
        String url = String.format(ud.getDec()+"%s", mobileNumber);

        new HttpHandler(url, getContext())
                .setMediaType(HttpHandler.jsonMediaType)
                .setRequestType(HttpHandler.RequestType.GET)
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
                            if (code ==404){
                                CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", jsonObject.getString("message"));
                                dialog.show();
                                dialog.onPositiveButton(view -> {
                                    dialog.dismiss();


                                });
                            } else if (code==200) {
                                if (jsonObject.getBoolean("status")){
                                    binding.forgetMobileLayout.setVisibility(View.GONE);
                                    binding.passwordRecoverLayout.setVisibility(View.VISIBLE);
                                }
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

    private void checkVerification() {
        JSONObject joObj = new JSONObject();
        try {
            // Add basic fields to the main JSON object
            joObj.put("password",binding.etRecoverPassword.getText().toString() );
            joObj.put("password_confirmation",binding.etRecoverConfirmPassword.getText().toString() );
            joObj.put("phone",binding.etRecoverPhoneNumber.getText().toString() );
            joObj.put("otp",binding.etRecoverNumberOtp.getText().toString());




        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        ExeDec rp=new ExeDec(resetPassword);
        new HttpHandler(rp.getDec(), getContext())
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
                                CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", jsonObject.getString("message"));
                                dialog.show();
                                dialog.onPositiveButton(view -> {
                                    sheetDialog.dismiss();
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

    private boolean validation() {
        if (TextUtils.isEmpty(binding.etForgetNumber.getText().toString().trim())){
            binding.tilForgetNumber.setError("Required Phone Number");
            binding.tilForgetNumber.requestFocus();
            return false;
        } else if (binding.etForgetNumber.getText().toString().trim().length()<10) {
            binding.tilForgetNumber.setError("Enter Valid mobile Number");
            binding.tilForgetNumber.requestFocus();
            return false;
        } else {
            binding.tilForgetNumber.setErrorEnabled(false);
        }



        return true;
    }
}