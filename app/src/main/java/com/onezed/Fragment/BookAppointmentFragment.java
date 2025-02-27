package com.onezed.Fragment;

import static android.content.Intent.getIntent;
import static com.onezed.GlobalVariable.baseUrl.appointment;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.onezed.Activity.HomeActivity;
import com.onezed.Activity.InhouseActivity;
import com.onezed.ExeDec;
import com.onezed.GlobalVariable.CustomAlertDialog;
import com.onezed.GlobalVariable.GlobalVariable;
import com.onezed.GlobalVariable.HttpHandler;
import com.onezed.Model.UserProfileModel;
import com.onezed.R;
import com.onezed.databinding.FragmentBookAppointmentBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


import okhttp3.Call;


public class BookAppointmentFragment extends Fragment {

    private FragmentBookAppointmentBinding binding;
    String selectedService=null;
    String type;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding=FragmentBookAppointmentBinding.inflate(inflater, container, false);
        // Get the intent and fragment type
        if (getArguments() != null) {
             type = getArguments().getString("FRAGMENT_TYPE");
            // Do something with the value
        }

        Log.v("type ", GlobalVariable.inhouseCategory);
        selectedService=GlobalVariable.inhouseCategory;


       binding.bookAppointmentSpinner.setText(GlobalVariable.inhouseCategory);

//        binding.bookAppointmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                if (position == 0) {
//                    // Position 0 is the "not selected" state
//                    //selectedService = null;
//                    selectedService=GlobalVariable.inhouseComRegSelectedCategory;
//                } else {
//                    // Set the selected service
//                    selectedService = parentView.getItemAtPosition(position).toString();
//                }
//
//                // Optionally hide any previous error if a valid selection is made
//                if (selectedService != null) {
//                   // binding.spinnerError.setVisibility(View.GONE);
//                    binding.bookAppointmentSpinner.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.pin_edit_text));
//
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//                // Do nothing
//            }
//        });

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation()){
                   AppointmentBookAPI();
                   // Toast.makeText(getContext(), "Continue"+binding.etName.getText().toString()+" "+binding.etEmail.getText().toString()+" "+binding.etPhone.getText().toString()+selectedService+binding.etMessage.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });



        // Back press handle
        binding.btnBackImg.setOnClickListener(view -> {
            // Pop the back stack to return to the previous fragment
            //requireActivity().finish();
            //startActivity(new Intent(getActivity(), HomeActivity.class));
            if ("BUSINESS_REGISTRATION".equals(type)) {
                Intent intent = new Intent(getActivity(), InhouseActivity.class);
                intent.putExtra("FRAGMENT_TYPE", "BUSINESS_REGISTRATION");
                startActivity(intent);

            }else if ("LICENCE_REGISTRATION".equals(type)) {
                Intent intent = new Intent(getActivity(), InhouseActivity.class);
                intent.putExtra("FRAGMENT_TYPE", "LICENCE_REGISTRATION");
                startActivity(intent);

            }else if ("BRAND_PROTECTION".equals(type)) {
                Intent intent = new Intent(getActivity(), InhouseActivity.class);
                intent.putExtra("FRAGMENT_TYPE", "BRAND_PROTECTION");
                startActivity(intent);

            }else if ("ACCOUNTING_EFINING".equals(type)) {
                Intent intent = new Intent(getActivity(), InhouseActivity.class);
                intent.putExtra("FRAGMENT_TYPE", "ACCOUNTING_EFINING");
                startActivity(intent);

            }

        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                if ("BUSINESS_REGISTRATION".equals(type)) {
                    Intent intent = new Intent(getActivity(), InhouseActivity.class);
                    intent.putExtra("FRAGMENT_TYPE", "BUSINESS_REGISTRATION");
                    startActivity(intent);

                }else if ("LICENCE_REGISTRATION".equals(type)) {
                    Intent intent = new Intent(getActivity(), InhouseActivity.class);
                    intent.putExtra("FRAGMENT_TYPE", "LICENCE_REGISTRATION");
                    startActivity(intent);

                }else if ("BRAND_PROTECTION".equals(type)) {
                    Intent intent = new Intent(getActivity(), InhouseActivity.class);
                    intent.putExtra("FRAGMENT_TYPE", "BRAND_PROTECTION");
                    startActivity(intent);

                }else if ("ACCOUNTING_EFINING".equals(type)) {
                    Intent intent = new Intent(getActivity(), InhouseActivity.class);
                    intent.putExtra("FRAGMENT_TYPE", "ACCOUNTING_EFINING");
                    startActivity(intent);

                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);


        return binding.getRoot();
    }

    private void AppointmentBookAPI() {
            JSONObject joObj = new JSONObject();
            try {
                joObj.put("name", binding.etName.getText().toString() );
                joObj.put("email", binding.etEmail.getText().toString());
                joObj.put("phone", binding.etPhone.getText().toString());
                joObj.put("state", selectedService);
                joObj.put("message", binding.etMessage.getText().toString());


            } catch (JSONException e) {
                e.printStackTrace();
            }
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Accept", "application/json");
            ExeDec appoint=new ExeDec(appointment);
            new HttpHandler(appoint.getDec(), getContext())
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
                                switch (code) {
                                    case 200:
                                        if (jsonObject.has("state")) {
                                            boolean status = jsonObject.getBoolean("state");
                                            if (status){
                                                CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", jsonObject.getString("message"));
                                                dialog.show();
                                                dialog.onPositiveButton(view ->{
                                                    dialog.dismiss();
                                                    startActivity(new Intent(getActivity(),HomeActivity.class));
                                                } );
                                            }else {
                                                CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", jsonObject.getString("message"));
                                                dialog.show();
                                                dialog.onPositiveButton(view -> dialog.dismiss());
                                            }

                                        }

                                        break;
                                    default:
                                        CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", "Something Wrong, Please Try after some times");
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

        // Check if an item is selected in the spinner
        if (TextUtils.isEmpty(selectedService)) {
           // binding.spinnerError.setVisibility(View.VISIBLE);
            //binding.spinnerError.setText("Please select a service");
            binding.bookAppointmentSpinner.requestFocus();

            binding.bookAppointmentSpinner.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.error_edit_bg));


            return false;
        } else {
            //binding.spinnerError.setVisibility(View.GONE); // Hide the error message if a valid selection is made
        }

        // Message box check
        if (TextUtils.isEmpty(binding.etMessage.getText().toString())) {
            binding.tilMessage.setError("Required Message");
            binding.tilMessage.requestFocus();
            return false;
        } else if (binding.etMessage.getText().toString().length() < 10) {
            binding.tilMessage.setError("Message should be at least 10 characters");
            binding.tilMessage.requestFocus();
            return false;
        } else {
            binding.tilMessage.setErrorEnabled(false);
        }

        return true;
    }


}