package com.onezed.Fragment;

import static com.onezed.GlobalVariable.baseUrl.createMobileOperator;
import static com.onezed.GlobalVariable.baseUrl.electricBillFetch;
import static com.onezed.GlobalVariable.baseUrl.getPostpaidBiller;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.onezed.Activity.HomeActivity;
import com.onezed.Activity.RechargeActivity;
import com.onezed.Adapter.OperatorAdapter;
import com.onezed.Adapter.contact_adapter;
import com.onezed.ExeDec;
import com.onezed.GlobalVariable.CustomAlertDialog;
import com.onezed.GlobalVariable.GlobalVariable;
import com.onezed.GlobalVariable.HttpHandler;
import com.onezed.GlobalVariable.SharedPrefManager;
import com.onezed.Model.Operator;
import com.onezed.Model.UserProfileModel;
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


public class MobilePostPaidFragment extends Fragment implements OperatorAdapter.OnOperatorClickListener {

    FragmentMobilePostPaidBinding binding;
    private static final int REQUEST_READ_CONTACTS = 101;
    SharedPrefManager sharedPrefManager;
    boolean isNumberFound = false;
    String tenDightNumber;
    List<Operator> OperatList = new ArrayList<>();
    String OperatorName;
    String amount,spKey,mobileNo,value;

    int userId;

    // Define an interface to send data to RechargeActivity
    public interface OnDataPass {
        void onDataPass(String type,String consumerId,String spKey,String amount);
    }

    private BroadbandRechargeFragment.OnDataPass dataPasser;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            dataPasser = (BroadbandRechargeFragment.OnDataPass) context;  // Cast RechargeActivity to OnDataPass
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnDataPass");
        }
    }

    // Call this method to pass data to RechargeActivity
    public void passData(String type,String consumerId, String spKey, String amount) {
        dataPasser.onDataPass(type,consumerId,spKey,amount);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMobilePostPaidBinding.inflate(inflater, container, false);
        sharedPrefManager = new SharedPrefManager(requireActivity());
        userId=sharedPrefManager.getUserId();
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
                                        checkIsNewMobileNumber();
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

        // Set OnItemClickListener for ListView
        binding.contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the adapter
                contact_adapter adapter = (contact_adapter) parent.getAdapter();
                tenDightNumber= getTenDigitNumber(adapter.getPhoneNumber(position));

                checkIsNewMobileNumber();

            }
        });

        binding.billOperator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.txtMobileNo.setVisibility(View.GONE);
                binding.newNumberLayout.setVisibility(View.GONE);
                binding.contactListView.setVisibility(View.GONE);
                binding.txtMobileOperator.setVisibility(View.VISIBLE);
                binding.operatorRecyclerView.setVisibility(View.VISIBLE);
                binding.payDetailsLayout.setVisibility(View.GONE);
                binding.btnProccedToPay.setVisibility(View.GONE);

                AllOperatorApi();
            }
        });

        binding.billerWrongOperator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.txtMobileOperator.setVisibility(View.VISIBLE);
                binding.operatorRecyclerView.setVisibility(View.VISIBLE);
                binding.wrongOperatorDetailsLayout.setVisibility(View.GONE);
                AllOperatorApi();
            }
        });
        binding.btnProccedToPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passData("MOBILE_POSTPAID",mobileNo,spKey,amount);
            }
        });

        // Back press handle
        //binding.btnBackImg.setOnClickListener(view -> startActivity(new Intent(getActivity(), HomeActivity.class)));
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

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                //startActivity(new Intent(getActivity(), HomeActivity.class));
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

        return binding.getRoot();  // Moved return statement to the end
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

                                                BillFetch(String.valueOf(operatorKey));

                                                isNumberFound = true;
                                                break; // Exit the loop if the number is found
                                            }
                                        }

                                        // If the number was not found, print or display a message
                                        if (!isNumberFound) {
                                            //Toast.makeText(getContext(), "Phone number not found in operators list", Toast.LENGTH_SHORT).show();
                                            AllOperatorApi();
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



    private void AllOperatorApi() {


        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        ExeDec gpb=new ExeDec(getPostpaidBiller);
        new HttpHandler(gpb.getDec(), getContext())
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
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            CustomAlertDialog dialog;
                            switch (code) {
                                case 400:
                                    dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed","2"+ jsonObject.getString("message"));
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
                                    dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed3", errorMessages.toString());
                                    dialog.show();
                                    dialog.onPositiveButton(view -> dialog.dismiss());
                                    break;
                                case 200:
                                    if (jsonObject.has("data")) {
                                        OperatList.clear();
                                        JSONArray AllOperatsArray = jsonObject.getJSONArray("data");
                                        for (int i=0;i<AllOperatsArray.length();i++){
                                            JSONObject operatorObj=AllOperatsArray.getJSONObject(i);
                                            String name=operatorObj.getString("service_provider");
                                            String OperatorCode=operatorObj.getString("sp_key");
                                            Operator operator = new Operator(name, OperatorCode);
                                            OperatList.add(operator);
                                        }
                                        // Set up RecyclerView with the adapter
                                       // RecyclerView recyclerView = v.findViewById(R.id.AllOperator_rv);
                                        OperatorAdapter adapter = new OperatorAdapter(OperatList,MobilePostPaidFragment.this); // Pass the fragment as the listener
                                        binding.operatorRecyclerView.setAdapter(adapter);
                                        binding.operatorRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                        //selectAllOperatorBottomSheet();
                                    }else {
                                        dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed4", jsonObject.getString("message"));
                                        dialog.show();
                                        dialog.onPositiveButton(view -> dialog.dismiss());
                                    }

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


    @Override
    public void onOperatorClick(String operatorName, String code) {
        GlobalVariable.OperatorCode=code;

        BillFetch(code);
        binding.operatorRecyclerView.setVisibility(View.GONE);
        binding.txtMobileOperator.setVisibility(View.GONE);
        createOperator(operatorName,code);

    }

    private void createOperator(String name,String code) {
        JSONObject joObj = new JSONObject();
        try {
            joObj.put("phone",tenDightNumber );
            joObj.put("name", name);
            joObj.put("user_id", userId);
            joObj.put("operator_key", code);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        ExeDec cmo=new ExeDec(createMobileOperator);
        new HttpHandler(cmo.getDec(), getContext())
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
                                    OperatorName=name;
                                    //String

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

    private void BillFetch(String spKey) {
        JSONObject joObj = new JSONObject();
        try {
            joObj.put("customerMobileNo", UserProfileModel.getInstance().getMobileNo() );
            joObj.put("accounNo", tenDightNumber);
            joObj.put("spkey", spKey);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        ExeDec ebf=new ExeDec(electricBillFetch);
        new HttpHandler(ebf.getDec(), getContext())
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
                        Log.v("MobileBillFetchR",code+" "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            switch (code) {
                                case 200:
                                    String status= jsonObject.getString("status");
                                    if (status=="success" || status.equals("success")){

                                        JSONObject dataObj=jsonObject.getJSONObject("data");
                                        // String billNumber= dataObj.getString("billnumber");
                                        String account= dataObj.getString("account");
                                        String customerName= dataObj.getString("customername");
                                        GlobalVariable.bbpsCustomername=customerName;
                                        String consumerId= dataObj.getString("account");
                                        String dueDate= dataObj.getString("duedate");
                                        String billDate= dataObj.getString("billdate");
                                        String errorCode=dataObj.getString("errorcode");
                                        String dueAmount=String.valueOf(dataObj.getDouble("dueamount"));
                                        if (errorCode=="200" || errorCode.equals("200")){
                                            binding.txtMobileOperator.setVisibility(View.GONE);
                                            binding.operatorRecyclerView.setVisibility(View.GONE);
                                            binding.payDetailsLayout.setVisibility(View.VISIBLE);
                                            binding.btnProccedToPay.setVisibility(View.VISIBLE);
                                            binding.bbpsImg.setVisibility(View.VISIBLE);

//                                            Glide.with(getContext())
//                                                    .load(selectedOperatorUrl)  // Network image URL
//                                                    .placeholder(R.drawable.ic_baseline_account_circle_profile)  // Optional: placeholder while loading
//                                                    .error(R.drawable.ic_baseline_account_circle_profile)  // Optional: error image if loading fails
//                                                    .into(binding.billerImg);
//                                            binding.electricBillOperator.setText(selectedOperatorName);

                                            binding.nameTxt.setText(customerName);
                                            binding.billDateTxt.setText(billDate);
                                            binding.dueDateTxt.setText(dueDate);
                                            binding.customerId.setText(consumerId);
                                            binding.dueAmount.setText("\u20B9 "+dueAmount);
                                            binding.billOperator.setText(OperatorName);
                                            amount=dueAmount;
                                            mobileNo=account;
                                            JSONArray billAmountOptions=dataObj.getJSONArray("billAmountOptions");
                                            for (int i=0;i<billAmountOptions.length();i++){
                                                JSONObject obj=billAmountOptions.getJSONObject(i);


                                                String amountName= obj.getString("amountName");
                                                int amountValue= obj.getInt("amountValue");


                                            }
                                            break;
                                        }

                                        else if (!errorCode.isEmpty()) {
                                            String message=dataObj.getString("msg");
                                            binding.wrongOperatorDetailsLayout.setVisibility(View.VISIBLE);
                                            binding.payDetailsLayout.setVisibility(View.GONE);
                                            binding.customerWrongId.setText(consumerId);
                                            binding.billerWrongOperator.setText(OperatorName);
                                            binding.messageTxt.setText(message);
                                            binding.txtMobileOperator.setVisibility(View.GONE);
                                            binding.operatorRecyclerView.setVisibility(View.GONE);
                                            binding.changeOperatorButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    binding.wrongOperatorDetailsLayout.setVisibility(View.GONE);
                                                    binding.txtMobileOperator.setVisibility(View.VISIBLE);
                                                    binding.operatorRecyclerView.setVisibility(View.VISIBLE);
                                                    AllOperatorApi();
                                                }
                                            });
                                            binding.backButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent intent = new Intent(getContext(), RechargeActivity.class);
                                                    intent.putExtra("FRAGMENT_TYPE", "MOBILE_POSTPAID");
                                                    startActivity(intent);
                                                }
                                            });
                                        }

                                        else {

                                            CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", "Something Wrong, Please Try after some times");
                                            dialog.show();
                                            dialog.onPositiveButton(view -> {
                                                startActivity(new Intent(getActivity(), HomeActivity.class));
                                                dialog.dismiss();
                                            } );
                                        }


                                    }
                                    else {
                                        //handle if status not success
                                        break;
                                    }
                                    //String
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

    public String getTenDigitNumber(String phoneNumber) {
        // Remove any non-digit characters (like +91, spaces, etc.)
        String cleanedNumber = phoneNumber.replaceAll("[^\\d]", "");

        // Get the last 10 digits
        if (cleanedNumber.length() > 10) {
            cleanedNumber = cleanedNumber.substring(cleanedNumber.length() - 10);
        }

        return cleanedNumber;
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
}