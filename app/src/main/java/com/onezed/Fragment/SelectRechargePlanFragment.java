package com.onezed.Fragment;

import static com.onezed.GlobalVariable.baseUrl.allOperator;
import static com.onezed.GlobalVariable.baseUrl.createMobileOperator;
import static com.onezed.GlobalVariable.baseUrl.rechargePlain;
import static com.onezed.GlobalVariable.baseUrl.uId;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.onezed.Activity.MobilePayActivity;
import com.onezed.Activity.RechargeActivity;
import com.onezed.Adapter.DynamicTabAdapter;
import com.onezed.Adapter.OperatorAdapter;
import com.onezed.ExeDec;
import com.onezed.GlobalVariable.CustomAlertDialog;
import com.onezed.GlobalVariable.GlobalVariable;
import com.onezed.GlobalVariable.HttpHandler;
import com.onezed.GlobalVariable.SharedPrefManager;
import com.onezed.GlobalVariable.baseUrl;
import com.onezed.Model.Operator;
import com.onezed.R;
import com.onezed.databinding.FragmentSelectRechargePlanBinding;
import com.onezed.databinding.SelectOperatorBottomSheetBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


public class SelectRechargePlanFragment extends Fragment implements OperatorAdapter.OnOperatorClickListener,DynamicTabAdapter.OnPlanClickListener {

    private FragmentSelectRechargePlanBinding binding;
    String name, phone,tenDightNumber,planAmount,planValidity,uPhone,uEmail,uName;
    private int noOfTabs;
    ArrayList<HashMap<String, String>> tabList = new ArrayList<>();
    ArrayList<ArrayList<HashMap<String, String>>> planList = new ArrayList<>();
    BottomSheetDialog sheetDialog;
    private SelectOperatorBottomSheetBinding selectOperatorBinding;
    ImageView imgCancelDialog;
    List<Operator> OperatList = new ArrayList<>();
    SharedPrefManager sharedPrefManager;
    int userId;
    boolean isNumberFound = false;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSelectRechargePlanBinding.inflate(inflater, container, false);
        sharedPrefManager = new SharedPrefManager(requireActivity());
        binding.viewpager.setOffscreenPageLimit(0);
        binding.viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabs));
        userId=sharedPrefManager.getUserId();


        //  Retrieve the arguments (the bundle) from the fragment
        Bundle args = getArguments();
        if (args != null) {
            name = args.getString("name"); // Use the same key as in the first fragment
            phone = args.getString("mobileNo");
            binding.name.setText(name);
            binding.ph.setText(phone);
        }

        checkIsNewMobileNumber();

        binding.updateOperatorEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllAllOperatorApi();
            }
        });


        tenDightNumber= getTenDigitNumber(phone);
        GlobalVariable.RechargeNumber=tenDightNumber;

        binding.reviewLayout.setVisibility(View.GONE);


        binding.btnSelectRechargePlanBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RechargeActivity.class);
                intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
                startActivity(intent);
            }
        });

        //Back Press Handler
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getActivity(), RechargeActivity.class);
                intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
                startActivity(intent);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

        return binding.getRoot();
    }

    private void checkIsNewMobileNumber() {

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        ExeDec ui=new ExeDec(uId);
        String url = String.format(ui.getDec()+"%s", userId);



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
                        dialog.onPositiveButton(view ->{
                            dialog.dismiss();
                            Intent intent = new Intent(getActivity(), RechargeActivity.class);
                            intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
                            startActivity(intent);
                        } );
                    }

                    @Override
                    public void onResponse(int code, String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            switch (code) {
                                case 200:

                                    if (jsonObject.has("operators")) {
                                        JSONArray operatorsArray = jsonObject.getJSONArray("operators");
                                        // Loop through the operators array
                                        for (int i = 0; i < operatorsArray.length(); i++) {
                                            JSONObject operator = operatorsArray.getJSONObject(i);

                                            // Check if the phone number matches
                                            if (operator.has("phone")) {

                                                if(operator.getString("phone").equals(tenDightNumber)){

                                                    // Phone number found, retrieve id, name, and operator_key
                                                    int id = operator.getInt("id");
                                                    String name = operator.getString("name");
                                                    int operatorKey = operator.getInt("operator_key");
                                                    GlobalVariable.OperatorCode = String.valueOf(operatorKey);


                                                    // You can also show a toast message instead of printing
                                                    //Toast.makeText(getContext(), "ID: " + id + ", Name: " + name + ", Operator Key: " + operatorKey, Toast.LENGTH_LONG).show();

                                                    rechargePlanApiCall(String.valueOf(operatorKey));
                                                    binding.updateOperatorEt.setText(name);
                                                    isNumberFound = true;
                                                    break;
                                                }
//                                                else {
//                                                    CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name), "Mobile Number is not authorized ");
//                                                    dialog.show();
//                                                    dialog.onPositiveButton(view -> dialog.dismiss());
//
//                                                }

                                            }else {
                                                CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name), "Failed to Load in Operators Object! ");
                                                dialog.show();
                                                dialog.onPositiveButton(view -> dialog.dismiss());
                                                Intent intent = new Intent(getActivity(), RechargeActivity.class);
                                                intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
                                                startActivity(intent);
                                            }
                                        }

                                        // If the number was not found, print or display a message
                                        if (!isNumberFound) {
                                            //Toast.makeText(getContext(), "Phone number not found in operators list", Toast.LENGTH_SHORT).show();
                                            AllAllOperatorApi();
                                        }
                                    }else {
                                        CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name), "Failed to Load the Operators! ");
                                        dialog.show();
                                        dialog.onPositiveButton(view -> dialog.dismiss());
                                        Intent intent = new Intent(getActivity(), RechargeActivity.class);
                                        intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
                                        startActivity(intent);
                                    }

                                    //String
                                    break;
                                case 404:
                                    AllAllOperatorApi();
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

    private void AllAllOperatorApi() {

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        ExeDec ao=new ExeDec(allOperator);
        new HttpHandler(ao.getDec(), getContext())
                .setMediaType(HttpHandler.jsonMediaType)
                .setRequestType(HttpHandler.RequestType.GET)
                .setLoading(true)
                .setHeaders(headers)
                .setOnResponse(new HttpHandler.HttpResponse() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name), getString(R.string.no_response));
                        dialog.show();
                        dialog.onPositiveButton(view ->{
                            dialog.dismiss();
                            Intent intent = new Intent(getActivity(), RechargeActivity.class);
                            intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
                            startActivity(intent);

                        } );
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
                                    dialog.onPositiveButton(view ->{
                                        dialog.dismiss();
                                        Intent intent = new Intent(getActivity(), RechargeActivity.class);
                                        intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
                                        startActivity(intent);
                                    } );
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
                                    dialog.onPositiveButton(view ->{
                                        Intent intent = new Intent(getActivity(), RechargeActivity.class);
                                        intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
                                        startActivity(intent);
                                        dialog.dismiss();
                                    } );
                                    break;
                                case 200:
                                    if (jsonObject.has("operators")) {
                                        JSONArray AllOperatsArray = jsonObject.getJSONArray("operators");
                                        for (int i=0;i<AllOperatsArray.length();i++){
                                            JSONObject operatorObj=AllOperatsArray.getJSONObject(i);
                                            String name=operatorObj.getString("name");
                                            String OperatorCode=operatorObj.getString("code");

                                            Operator operator = new Operator(name, OperatorCode);
                                            OperatList.add(operator);
                                        }
                                        selectAllOperatorBottomSheet();
                                    }else {
                                        dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", jsonObject.getString("message"));
                                        dialog.show();
                                        dialog.onPositiveButton(view ->{
                                            Intent intent = new Intent(getActivity(), RechargeActivity.class);
                                            intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
                                            startActivity(intent);
                                            dialog.dismiss();
                                        } );
                                    }

                                    break;
                                default:
                                    dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", "Something Wrong, Please Try after some times");
                                    dialog.show();
                                    dialog.onPositiveButton(view ->{
                                        Intent intent = new Intent(getActivity(), RechargeActivity.class);
                                        intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
                                        startActivity(intent);
                                        dialog.dismiss();
                                    });
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }

                }).sendRequest();
    }

    // Method to set up the bottom sheet with operator list
    private void selectAllOperatorBottomSheet() {
        selectOperatorBinding = SelectOperatorBottomSheetBinding.inflate(getLayoutInflater());
        View v = selectOperatorBinding.getRoot();
        sheetDialog = new BottomSheetDialog(getContext());
        sheetDialog.setContentView(v);
        sheetDialog.setCancelable(false);
        sheetDialog.show();
        imgCancelDialog = v.findViewById(R.id.cancel_btn);

        imgCancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sheetDialog.dismiss();
                Intent intent = new Intent(getActivity(), RechargeActivity.class);
                intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
                startActivity(intent);
            }
        });

        // Set up RecyclerView with the adapter
        RecyclerView recyclerView = v.findViewById(R.id.AllOperator_rv);
        OperatorAdapter adapter = new OperatorAdapter(OperatList, this); // Pass the fragment as the listener
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    // Click listener implementation to show a Toast when the operator is clicked
    @Override
    public void onOperatorClick(String operatorName,String operatorCode) {
        //Toast.makeText(getContext(), "Selected Operator: " + operatorName +" "+operatorCode, Toast.LENGTH_SHORT).show();
        sheetDialog.dismiss();
        GlobalVariable.OperatorCode=operatorCode;
        rechargePlanApiCall(operatorCode);
        binding.rechargePlanLayout.setVisibility(View.VISIBLE);
        createOperator(operatorName,operatorCode);
        binding.updateOperatorEt.setText(operatorName);

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

                                    //String
                                    break;
                                default:
                                    CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", "Something Wrong, Please Try after some times");
                                    dialog.show();
                                    dialog.onPositiveButton(view ->{
                                        dialog.dismiss();
                                        Intent intent = new Intent(getActivity(), RechargeActivity.class);
                                        intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
                                        startActivity(intent);
                                    } );
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }).sendRequest();
    }

    private void rechargePlanApiCall(String OperatorCode) {
        JSONObject joObj = new JSONObject();
        try {
            joObj.put("contact",tenDightNumber );
            joObj.put("carrier", OperatorCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        ExeDec cp=new ExeDec(rechargePlain);
        new HttpHandler(cp.getDec(), getContext())
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
                        dialog.onPositiveButton(view ->{
                            dialog.dismiss();
                            Intent intent = new Intent(getActivity(), RechargeActivity.class);
                            intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
                            startActivity(intent);
                        } );
                    }

                    @Override
                    public void onResponse(int code, String response) {
                        Log.v("RechargePlanApiResponse",code+" "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            switch (code) {
                                case 200:
                                    if (jsonObject.has("userData")) {
                                        JSONObject userDataObj = jsonObject.getJSONObject("userData");
                                        if (userDataObj.has("data")) {
                                            JSONObject dataObj = userDataObj.getJSONObject("data");

                                            if (dataObj.has("statusCode") && dataObj.getInt("statusCode") == 1) {
                                                if (dataObj.has("data")) {
                                                    // Check if "data" is a JSONObject or JSONArray
                                                    Object dataField = dataObj.get("data");
                                                    if (dataField instanceof JSONObject) {
                                                        // Handle JSONObject case
                                                        JSONObject data2Obj = dataObj.getJSONObject("data");

                                                        if (data2Obj.has("types")) {

                                                            JSONArray typesArray = data2Obj.getJSONArray("types");
                                                            noOfTabs = typesArray.length();
                                                            tabList = new ArrayList<>();
                                                            planList = new ArrayList<>();
                                                            for (int i = 0; i < typesArray.length(); i++) {
                                                                JSONObject typeObj = typesArray.getJSONObject(i);
                                                                HashMap<String, String> tabItem = new HashMap<>();
                                                                tabItem.put("name", typeObj.getString("pType"));
                                                                tabList.add(tabItem);

                                                                JSONArray pDetailsArray = typeObj.getJSONArray("pDetails");
                                                                ArrayList<HashMap<String, String>> planDetailsList = new ArrayList<>();
                                                                for (int j = 0; j < pDetailsArray.length(); j++) {
                                                                    JSONObject pDetailsObj = pDetailsArray.getJSONObject(j);

                                                                    HashMap<String, String> planItem = new HashMap<>();
                                                                    planItem.put("Rs", pDetailsObj.getString("rs"));
                                                                    planItem.put("desc", pDetailsObj.getString("desc"));
                                                                    planItem.put("validity", pDetailsObj.getString("validity"));

                                                                    planDetailsList.add(planItem);
                                                                }
                                                                planList.add(planDetailsList);
                                                            }

                                                            // Once tabList and planList are populated, set up the ViewPager and TabLayout
                                                            adapterCall();
                                                        } else {

                                                            CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name), getString(R.string.no_response));
                                                            dialog.show();
                                                            dialog.onPositiveButton(view -> dialog.dismiss());
                                                        }


                                                    }
                                                    else if (dataField instanceof JSONArray) {

                                                        CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name), "Mobile Number is not Valid");
                                                        dialog.show();
                                                        dialog.onPositiveButton(view ->{
                                                            dialog.dismiss();
                                                            getActivity().finish();
                                                            Intent intent = new Intent(getActivity(), RechargeActivity.class);
                                                            intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
                                                            startActivity(intent);
                                                        });
                                                    }else {

                                                        CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name), "Something Wrong! Please try again");
                                                        dialog.show();
                                                        dialog.onPositiveButton(view ->{
                                                            dialog.dismiss();
                                                            Intent intent = new Intent(getActivity(), RechargeActivity.class);
                                                            intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
                                                            startActivity(intent);
                                                        } );
                                                    }


                                                    JSONArray dataArray = dataObj.getJSONArray("data");

                                                }
                                                else {
                                                    CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name), "Error to fetch the Data");
                                                    dialog.show();
                                                    dialog.onPositiveButton(view ->{
                                                        dialog.dismiss();
                                                        Intent intent = new Intent(getActivity(), RechargeActivity.class);
                                                        intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
                                                        startActivity(intent);
                                                    } );
                                                }



                                            }
                                            else {
                                                String msg="Fetching to get error code";
                                                msg=dataObj.getString("msg");
                                                CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name),msg );
                                                dialog.show();
                                                dialog.onPositiveButton(view ->{
                                                    dialog.dismiss();
                                                    Intent intent = new Intent(getActivity(), RechargeActivity.class);
                                                    intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
                                                    startActivity(intent);
                                                } );
                                            }

                                        }
                                        else {
                                            CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name), "Error to fetch the plan Data");
                                            dialog.show();
                                            dialog.onPositiveButton(view ->{
                                                dialog.dismiss();
                                                Intent intent = new Intent(getActivity(), RechargeActivity.class);
                                                intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
                                                startActivity(intent);
                                            } );
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

    private void adapterCall() {
        DynamicTabAdapter mDynamicFragmentAdapter = new DynamicTabAdapter(
                getActivity().getSupportFragmentManager(),
                noOfTabs,
                tabList,
                planList,
                this

        );
        // set the adapter
        binding.viewpager.setAdapter(mDynamicFragmentAdapter);
        binding.tabs.setupWithViewPager(binding.viewpager);
        binding.viewpager.setCurrentItem(0);
        mDynamicFragmentAdapter.notifyDataSetChanged();
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

    @Override
    public void onPlanClick(String rs, String desc, String validity) {
        // Toast.makeText(getActivity(), "Rss: " + rs + ", Desc: " + desc + ", Validity: " + validity, Toast.LENGTH_SHORT).show();
        Log.d("PlanClick", "Clicked Plan: Rs = " + rs + ", Desc = " + desc + ", Validity = " + validity);
        binding.amountTxt.setText(rs);
        binding.validity.setText(validity);
        binding.description.setText(desc);
        binding.rechargePlanLayout.setVisibility(View.GONE);
        binding.updateOperatorEt.setVisibility(View.GONE);
        binding.reviewLayout.setVisibility(View.VISIBLE);

        planAmount=rs;
        GlobalVariable.Rs=rs;
        planValidity=validity;

        binding.btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // startPayment();
                startActivity(new Intent(getActivity(), MobilePayActivity.class));
                binding.reviewLayout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }



}
