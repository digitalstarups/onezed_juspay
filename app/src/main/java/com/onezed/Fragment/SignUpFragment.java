package com.onezed.Fragment;

import static com.onezed.GlobalVariable.baseUrl.create;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.onezed.ExeDec;
import com.onezed.GlobalVariable.CustomAlertDialog;
import com.onezed.GlobalVariable.CustomToast;
import com.onezed.GlobalVariable.HttpHandler;
import com.onezed.GlobalVariable.baseUrl;
import com.onezed.R;
import com.onezed.databinding.FragmentSignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;


public class SignUpFragment extends Fragment {

    private FragmentSignUpBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);


        setupClickListener(binding.etName, binding.tilName);
        setupClickListener(binding.etEmail, binding.tilEmail);
        setupClickListener(binding.etPhone, binding.tilPhone);
        setupClickListener(binding.etPassword, binding.tilPassword);
        setupClickListener(binding.etConfirmPassword, binding.tilConfirmPassword);


        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validation()){
                    if (binding.etPassword.getText().toString().trim().equals(binding.etConfirmPassword.getText().toString().trim())){
                        SignUpApiCall();

                    }else {
                        //Toast.makeText(getActivity(), "continue...", Toast.LENGTH_SHORT).show();
                        binding.tilPassword.setError("Password Mismatch");
                        //binding.tilPassword.requestFocus();
                        binding.tilConfirmPassword.setError("Password Mismatch");
                        //binding.tilConfirmPassword.requestFocus();
                        CustomToast.showToast(getContext(),"Password Mismatch");
                    }

                }
            }
        });











        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event

//                FragmentManager fm=getActivity().getSupportFragmentManager();
//                FragmentTransaction ft=fm.beginTransaction();
//                ft.replace(R.id.splash_frameLayout ,new MobileLoginFragment());
//                ft.commit();

                if (isAdded()) {
                    FragmentManager fm = requireActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.addToBackStack( null);
                    ft.replace(R.id.splash_frameLayout, new MobileLoginFragment());
                    ft.commit();
                } else {
                   // Log.e("SignUpFragment", "Fragment is not attached to an activity.");
                    getActivity().finish();
                }

            }

        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

        return binding.getRoot();
    }

    private void SignUpApiCall() {


        JSONObject joObj = new JSONObject();
        try {
            // Add basic fields to the main JSON object
            joObj.put("name", binding.etName.getText().toString());
            joObj.put("email", binding.etEmail.getText().toString());
            joObj.put("phone", binding.etPhone.getText().toString());
            joObj.put("password", binding.etPassword.getText().toString());
            joObj.put("password_confirmation", binding.etConfirmPassword.getText().toString());



        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        ExeDec cre=new ExeDec(create);
        new HttpHandler(cre.getDec(), getContext())
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
                            CustomAlertDialog dialog;
                            switch (code) {
                                case 400:
                                    dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", jsonObject.getString("message"));
                                    dialog.show();
                                    dialog.onPositiveButton(view -> dialog.dismiss());
                                    break;
                                case 422:
                                    StringBuilder errorMessages = new StringBuilder();
                                    JSONObject errorObj = jsonObject.getJSONObject("errors");
                                    String[] keys = {"name", "password", "phone"};
                                    for (String key : keys) {
                                        if (errorObj.has(key)) {
                                            JSONArray errorsArray = errorObj.getJSONArray(key);
                                            for (int i = 0; i < errorsArray.length(); i++) {
                                                errorMessages.append(errorsArray.getString(i)).append("\n");
                                            }
                                        }
                                    }
                                    dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", errorMessages.toString());
                                    dialog.show();
                                    dialog.onPositiveButton(view -> dialog.dismiss());
                                    break;
                                case 200:
                                    dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", jsonObject.getString("message"));
                                    dialog.show();
                                    dialog.onPositiveButton(view -> {
                                        dialog.dismiss();

                                        FragmentManager fm = getActivity().getSupportFragmentManager();
                                        FragmentTransaction ft = fm.beginTransaction();
                                        ft.replace(R.id.splash_frameLayout, new MobileLoginFragment());
                                        ft.commit();
                                    });
                                    break;
                                default:
                                    dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", "Something Wrong, Please Try after some times");
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

    private void setupClickListener(final AppCompatEditText editText, final TextInputLayout textInputLayout) {
        editText.post(new Runnable() {
            @Override
            public void run() {
                editText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        textInputLayout.setError(null);
                    }
                });
            }
        });
    }


    private boolean validation() {

        // Check Name
        if (TextUtils.isEmpty(binding.etName.getText().toString().trim())) {
            binding.tilName.setError("Required Name");
            binding.tilName.requestFocus();
            return false;
        } else {
            binding.tilName.setErrorEnabled(false);
        }

        // Check Email
        if (TextUtils.isEmpty(binding.etEmail.getText().toString().trim())) {
            binding.tilEmail.setError("Required Email");
            binding.tilEmail.requestFocus();
            return false;
        } else {
            binding.tilEmail.setErrorEnabled(false);
        }

        // Check Phone
        String phone = binding.etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            binding.tilPhone.setError("Required Phone");
            binding.tilPhone.requestFocus();
            return false;
        } else if (phone.length() < 10) {
            binding.tilPhone.setError("Enter Valid Mobile Number");
            binding.tilPhone.requestFocus();
            return false;
        } else if (!phone.matches("\\d+")) {  // Check if phone contains only digits
            binding.tilPhone.setError("Enter a Valid Number");
            binding.tilPhone.requestFocus();
            return false;
        } else {
            binding.tilPhone.setErrorEnabled(false);
        }

        // Check Password
        if (TextUtils.isEmpty(binding.etPassword.getText().toString().trim())) {
            binding.tilPassword.setError("Required Password");
            binding.tilPassword.requestFocus();
            return false;
        } else {
            binding.tilPassword.setErrorEnabled(false);
        }

        // Check Confirm Password
        if (TextUtils.isEmpty(binding.etConfirmPassword.getText().toString().trim())) {
            binding.tilConfirmPassword.setError("Required Confirm Password");
            binding.tilConfirmPassword.requestFocus();
            return false;
        } else {
            binding.tilConfirmPassword.setErrorEnabled(false);
        }

        return true;
    }

}