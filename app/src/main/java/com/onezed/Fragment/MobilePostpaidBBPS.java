package com.onezed.Fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.onezed.Activity.HomeActivity;
import com.onezed.Adapter.BillerAdapter;
import com.onezed.Adapter.contact_adapter;
import com.onezed.GlobalVariable.CustomAlertDialog;
import com.onezed.GlobalVariable.GlobalVariable;
import com.onezed.GlobalVariable.HttpHandler;
import com.onezed.GlobalVariable.SharedPrefManager;
import com.onezed.Model.Biller;
import com.onezed.R;
import com.onezed.databinding.FragmentMobilePostPaidBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


public class MobilePostpaidBBPS extends Fragment {

   private FragmentMobilePostPaidBinding binding;
    private List<Biller> billerList = new ArrayList<>();
    private BillerAdapter billerAdapter;
    String value,tenDightNumber,OperatorName,spKey;
    int userId;
    private static final int REQUEST_READ_CONTACTS = 101;
    SharedPrefManager sharedPrefManager;
    boolean isNumberFound = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= FragmentMobilePostPaidBinding.inflate(inflater, container, false);
        sharedPrefManager = new SharedPrefManager(requireActivity());
        userId=sharedPrefManager.getUserId();
        if (getArguments() != null) {
            value = getArguments().getString("PAGE");
        }
        if (getArguments() != null) {
            value = getArguments().getString("PAGE");
        }
        // Check Contact Permission
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS);
        } else {
            // Permission is already granted
            retrieveContacts();
        }
        binding.manualInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNumberKeyboard();
            }
        });
        // Set OnItemClickListener for ListView
        binding.contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the adapter
                contact_adapter adapter = (contact_adapter) parent.getAdapter();
                tenDightNumber= getTenDigitNumber(adapter.getPhoneNumber(position));

                Log.v("selctedNumber",tenDightNumber);
                String clickedName = adapter.getName(position);
                Bundle bundle = new Bundle();
                bundle.putString("name", clickedName); // Replace "key_name" and "value_name" with your actual key and value
                bundle.putString("mobileNo", tenDightNumber);

                // Create an instance of the second fragment
                MobilePostpaidPlanFragment secondFragment = new MobilePostpaidPlanFragment();
                // Set the arguments (the bundle) on the second fragment
                secondFragment.setArguments(bundle);

                // Replace the current fragment with the new one
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.recharge_frameLayout, secondFragment); // Use the container ID where the fragment should be placed
                transaction.addToBackStack(null); // Optional: add to back stack if you want to allow users to navigate back
                transaction.commit();

            }
        });
        // Set up TextWatcher
        binding.txtMobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.newNumberLayout.setVisibility(View.GONE);
                ((contact_adapter) binding.contactListView.getAdapter()).getFilter().filter(charSequence);

                // Check if the filtered results are empty
                binding.contactListView.post(() -> {
                    contact_adapter adapter = (contact_adapter) binding.contactListView.getAdapter();
                    adapter.setOnFilterCompleteListener(isEmpty -> {
                        if (isEmpty) {
                            binding.newNumberLayout.setVisibility(View.VISIBLE);
                            binding.newNumber.setText(charSequence.toString());
                            binding.newNumberLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (charSequence.length() == 10) {
                                        tenDightNumber= getTenDigitNumber(charSequence.toString());
                                        Bundle bundle = new Bundle();
                                        bundle.putString("name", "unknown");
                                        bundle.putString("mobileNo", charSequence.toString().trim());

                                        MobilePostpaidPlanFragment secondFragment = new MobilePostpaidPlanFragment();
                                        secondFragment.setArguments(bundle);

                                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                        transaction.replace(R.id.recharge_frameLayout, secondFragment);
                                        transaction.addToBackStack(null);
                                        transaction.commit();
                                    } else {
                                        binding.txtMobileNo.setError("Invalid Mobile Number");
                                        binding.txtMobileNo.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.error_edit_bg));
                                    }
                                }
                            });
                        } else {
                            binding.newNumberLayout.setVisibility(View.GONE);
                        }
                    });
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        binding.btnBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (value!=null) {
                    if (value.equals("BBPS")) {
                        BBPSFragment fm = new BBPSFragment();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.recharge_frameLayout, fm);
                        transaction.addToBackStack(null); // Optional: Add to back stack
                        transaction.commit();
                    } else {
                        startActivity(new Intent(getActivity(), HomeActivity.class));
                    }
                }
                else {
                    startActivity(new Intent(getActivity(), HomeActivity.class));
                }
            }
        });
        //Back Press Handler
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (value!=null) {
                    if (value.equals("BBPS")) {
                        BBPSFragment fm = new BBPSFragment();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.recharge_frameLayout, fm);
                        transaction.addToBackStack(null); // Optional: Add to back stack
                        transaction.commit();
                    } else {
                        startActivity(new Intent(getActivity(), HomeActivity.class));
                    }
                }
                else {
                    startActivity(new Intent(getActivity(), HomeActivity.class));
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);


        return  binding.getRoot();
    }
    private void checkIsNewMobileNumber() {


        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        String url = String.format("https://onezed.app/api/get/operator?user_id=%s", userId);
        Log.v("Response "," "+userId);
        Log.v("Response "," "+url);
        new HttpHandler(url, getContext())
                .setMediaType(HttpHandler.jsonMediaType)
                .setRequestType(HttpHandler.RequestType.GET)
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
                        Log.v("Response ",code+" "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            switch (code) {
                                case 200:
                                    binding.txtMobileNo.setVisibility(View.GONE);
                                    binding.newNumberLayout.setVisibility(View.GONE);
                                    binding.contactListView.setVisibility(View.GONE);
                                    binding.txtMobileOperator.setVisibility(View.VISIBLE);
                                    binding.operatorRecyclerView.setVisibility(View.VISIBLE);
                                    if (jsonObject.has("operators")) {
                                        JSONArray operatorsArray = jsonObject.getJSONArray("operators");

                                        // Loop through the operators array
                                        for (int i = 0; i < operatorsArray.length(); i++) {
                                            JSONObject operator = operatorsArray.getJSONObject(i);

                                            // Check if the phone number matches
                                            if (operator.has("phone") && operator.getString("phone").equals(tenDightNumber)) {
                                                // Phone number found, retrieve id, name, and operator_key
                                                int id = operator.getInt("id");
                                                OperatorName = operator.getString("name");
                                                int operatorKey = operator.getInt("operator_key");
                                                spKey= String.valueOf(operatorKey);
                                                GlobalVariable.OperatorCode=String.valueOf(operatorKey);
                                                binding.billOperator.setText(OperatorName);

                                               // BillFetch(String.valueOf(operatorKey));

                                                isNumberFound = true;
                                                break; // Exit the loop if the number is found
                                            }
                                        }

                                        // If the number was not found, print or display a message
                                        if (!isNumberFound) {
                                            //Toast.makeText(getContext(), "Phone number not found in operators list", Toast.LENGTH_SHORT).show();
                                            // AllOperatorApi();
                                        }
                                    }

                                    //String
                                    break;
//                                case 404:
//                                    AllOperatorApi();
//                                    break;
                                default:
                                    if (jsonObject.has("message")){
                                        CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed1", jsonObject.getString("message"));
                                        dialog.show();
                                        dialog.onPositiveButton(view -> dialog.dismiss());
                                        break;
                                    }else{
                                        CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed1", "Something went wrong! Try after sometimes");
                                        dialog.show();
                                        dialog.onPositiveButton(view -> dialog.dismiss());
                                        break;
                                    }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }).sendRequest();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                retrieveContacts();
            } else {
                Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(),HomeActivity.class));
            }
        }
    }
    private void retrieveContacts() {
        List<String> names = new ArrayList<>();
        List<String> numbers = new ArrayList<>();

        ContentResolver resolver = getActivity().getContentResolver();
        Cursor cursor = resolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC");

        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            while (cursor.moveToNext()) {
                names.add(cursor.getString(nameIndex));
                numbers.add(cursor.getString(numberIndex));
            }
            cursor.close();
        }

        contact_adapter adapter = new contact_adapter(getActivity(), names, numbers);
        binding.contactListView.setAdapter(adapter);
    }
    public String getTenDigitNumber(String phoneNumber) {
        // Remove any non-digit characters (like +91, spaces, etc.)
        String cleanedNumber = phoneNumber.replaceAll("[^\\d]", "");

        // Get the last 10 digits
        if (cleanedNumber.length() > 10) {
            cleanedNumber = cleanedNumber.substring(cleanedNumber.length() - 10);
        }

        return cleanedNumber;
    }

    private void showNumberKeyboard() {
        binding.txtMobileNo.requestFocus();
        binding.txtMobileNo.setInputType(InputType.TYPE_CLASS_PHONE);


        // Get the InputMethodManager
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(binding.txtMobileNo, InputMethodManager.SHOW_IMPLICIT);

        }
    }


//    private void fetchBillerApi() {
//        Map<String, String> headers = new HashMap<>();
//        headers.put("Content-Type", "application/json");
//        headers.put("Accept", "application/json");
//        ExeDec eb=new ExeDec(electricityBiller);
//        new HttpHandler("https://onezed.app/api/search-biller-category?search=Mobile Prepaid", getContext())
//                .setMediaType(HttpHandler.jsonMediaType)
//                .setRequestType(HttpHandler.RequestType.GET)
//                .setLoading(true)
//                .setHeaders(headers)
//                .setOnResponse(new HttpHandler.HttpResponse() {
//
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name), getString(R.string.no_response));
//                        dialog.show();
//                        dialog.onPositiveButton(view -> dialog.dismiss());
//                    }
//
//                    @Override
//                    public void onResponse(int code, String response) {
//
//                        try {
//                            JSONArray jsonArray = new JSONArray(response);
//                            switch (code) {
//                                case 200:
//
//                                    billerList.clear();  // Clear the list before adding new data
//                                    for (int i = 0; i < jsonArray.length(); i++) {
//                                        JSONObject obj = jsonArray.getJSONObject(i);
//                                        // Add the name and code to the list
//                                        String service_provider = obj.getString("billerName");
//                                        Biller biller = new Biller();
//
//                                        biller.setServiceProvider(service_provider);
//
//                                        billerList.add(biller);
//                                    }
//                                    // Notify adapter with updated data
//                                    getActivity().runOnUiThread(() -> billerAdapter.notifyDataSetChanged());
//                                    break;
//                                default:
//                                    showAlert("Something went wrong. Please try again.");
//                                    break;
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).sendRequest();
//    }

    private void showAlert(String message) {
        CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", message);
        dialog.show();
        dialog.onPositiveButton(view -> dialog.dismiss());
    }
}