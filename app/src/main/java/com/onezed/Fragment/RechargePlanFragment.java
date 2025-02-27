package com.onezed.Fragment;

import static com.onezed.GlobalVariable.GlobalVariable.selectedBillerCategory;
import static com.onezed.GlobalVariable.GlobalVariable.selectedBillerId;
import static com.onezed.GlobalVariable.baseUrl.electricityBiller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.onezed.Adapter.RechargePlanAdapter;
import com.onezed.Adapter.RechargeTabAdapter;
import com.onezed.ExeDec;
import com.onezed.GlobalVariable.CustomAlertDialog;
import com.onezed.GlobalVariable.GlobalVariable;
import com.onezed.GlobalVariable.HttpHandler;
import com.onezed.Model.UserProfileModel;
import com.onezed.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;


public class RechargePlanFragment extends Fragment {

    private static final String ARG_PLAN_LIST = "planList";
    private ArrayList<HashMap<String, String>> planList;
    private RechargeTabAdapter.OnPlanClickListener onPlanClickListener;
    String selectedBilId;

    public static RechargePlanFragment newInstance(ArrayList<HashMap<String, String>> planList,
                                                   RechargeTabAdapter.OnPlanClickListener listener) {
        RechargePlanFragment fragment = new RechargePlanFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PLAN_LIST, planList);
        fragment.setArguments(args);
        fragment.onPlanClickListener = listener;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plan, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        if (getArguments() != null) {
            planList = (ArrayList<HashMap<String, String>>) getArguments().getSerializable(ARG_PLAN_LIST);
        }

        // Set up the RecyclerView with planList data
        RechargePlanAdapter adapter = new RechargePlanAdapter(planList, plan -> {

            Toast.makeText(getContext(), "Amount: ₹" + plan.toString(), Toast.LENGTH_SHORT).show();
            Log.v("validateBil",plan.toString()+" " +selectedBillerId);
            if (GlobalVariable.selectedBillerType.equals("VALIDATE_PAY")){
                validateBill(plan);
            }
           // validateBill(plan);  // Pass only the clicked plan

        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    private void validateBill( HashMap<String, String> plans) {
        JSONObject requestObj = new JSONObject();
        try {
            // Adding top-level fields
            requestObj.put("billerId", selectedBillerId);
            requestObj.put("billerName", GlobalVariable.selectedBillerName);
            requestObj.put("billerCategory", selectedBillerCategory);
            requestObj.put("macAdress", JSONObject.NULL); // For null value
            requestObj.put("customerMobNo", UserProfileModel.getInstance().getMobileNo());

//            // Convert planListArray into the required JSON structure
//            JSONArray tagsArray = new JSONArray();
//
//            for (Map.Entry<String, String> entry : plans.entrySet()) {
//
//                if ("amountInRupees".equals(entry.getKey())) {
//                    continue; // Skip this iteration and move to the next one
//                }
//                JSONObject tagObject = new JSONObject();
//                tagObject.put("name", entry.getKey());
//                tagObject.put("value", entry.getValue() != null ? entry.getValue() : ""); // Handle null values
//                tagsArray.put(tagObject);
//            }


            // Create customerParamsRequest and add tagsArray
            JSONObject customerParamsRequest = new JSONObject();
            customerParamsRequest.put("tags", GlobalVariable.tags);

            // Add customerParamsRequest to the main request object
            requestObj.put("customerParamsRequest", customerParamsRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.v("fetchBillRequest", requestObj.toString());

        // Setting headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        // Sending the request
        new HttpHandler("https://onezed.app/api/validate-bill", getContext())
                .setMediaType(HttpHandler.jsonMediaType)
                .setRequestType(HttpHandler.RequestType.POST)
                .setStringData(requestObj.toString()) // Pass the JSON payload as a string
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
                        Log.v("fetchBillResponseVB", code + " " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            switch (code) {
                                case 200:
                                    JSONObject dataObject = jsonObject.getJSONObject("data");
                                    selectedBilId = dataObject.getString("billId");

                                    if (jsonObject.has("status") && "success".equals(jsonObject.getString("status"))) {
                                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                            validateBillByRequestId(selectedBilId);
                                        }, 20000); // 20 seconds delay
                                    } else {
                                        CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", jsonObject.getString("message"));
                                        dialog.show();
                                        dialog.onPositiveButton(view -> dialog.dismiss());
                                    }
                                    break;
                                default:
                                    CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", "Something Wrong!, Please Try after some time");
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

    private void validateBillByRequestId(String billId) {
        Log.v("billId",billId +" "+"https://onezed.app/api/validate-bill-id?billId="+billId);
        String bilId="https://onezed.app/api/validate-bill-id?billId="+billId;

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        // ExeDec eb=new ExeDec(electricityBiller);

        new HttpHandler(bilId, getContext())
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
                    public void onResponse(int code, String res) {
                        Log.v("fetchBillByIdResponse", code + " OK " + res);
                        try {
                            JSONObject jsonResponse = new JSONObject(res);
                            Log.v("SelectedBilDataTry", "ValidateBill");

//                            switch (code) {
//                                case 200:
//                                    JSONObject dataObject = jsonResponse.getJSONObject("data");
//                                    // tagsArray = new JSONArray();
//                                    if (dataObject.has("genericResponse")){
//                                        JSONObject genericResponse = dataObject.getJSONObject("genericResponse");
//                                        if (genericResponse.getString("statusCode").equals("500")){
//                                            CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name), genericResponse.getString("message"));
//                                            dialog.show();
//                                            dialog.onPositiveButton(view -> dialog.dismiss());
//                                        }
//                                        else if (dataObject.has("billerResponse")){
//                                            JSONObject billerResponse = dataObject.getJSONObject("billerResponse");
//
//                                            JSONArray additionalInfoArray = dataObject.getJSONArray("additionalInfo");
//                                            refId=dataObject.getString("refId");
//                                            actionType=dataObject.getString("actionType");
//                                            userId=dataObject.getString("userId");
//                                            customerParamsArray = dataObject.getJSONArray("customerParams");
//
//
//                                            //  if (!billerResponse.equals(null)){
//
//                                            binding.dynamicLayout.setVisibility(View.GONE);
//                                            binding.submitBtn.setVisibility(View.GONE);
//                                            binding.billerDetails.setVisibility(View.VISIBLE);
//                                            binding.payBtn.setVisibility(View.VISIBLE);
//                                            isCuatomParam=false;
//                                            isPayNow=true;
//                                            // isOperatorUI=true;
//                                            // Log the entire billerResponse object for debugging
//                                            Log.v("BillerResponse", billerResponse.toString());
//
//                                            // Call a method to display this data dynamically in the UI
//                                            displayBillerResponse(billerResponse,additionalInfoArray);
//
////                                        // Extracting data from billerResponse
////                                        String billId = billerResponse.getString("billId");
////                                        String amount = billerResponse.getString("amount");
////                                        String billDate = billerResponse.getString("billDate");
////                                        String billNumber = billerResponse.getString("billNumber");
////                                        String billPeriod = billerResponse.getString("billPeriod");
////                                        String dueDate = billerResponse.getString("dueDate");
////                                        String customerName = billerResponse.getString("customerName");
////                                        boolean amountOption = billerResponse.getBoolean("amountOption");
////
////                                        // Log extracted details for debugging
////                                        Log.v("BillerResponse", "Bill ID: " + billId);
////                                        Log.v("BillerResponse", "Amount: " + amount);
////                                        Log.v("BillerResponse", "Bill Date: " + billDate);
////                                        Log.v("BillerResponse", "Bill Number: " + billNumber);
////                                        Log.v("BillerResponse", "Due Date: " + dueDate);
////                                        Log.v("BillerResponse", "Customer Name: " + customerName);
////                                        Log.v("BillerResponse", "Amount Option: " + amountOption);
////
////                                        // Call a method to display this data dynamically in the UI
////                                        displayBillerResponse(billerResponse);
//                                            // }
////                                            else {
////                                                Toast.makeText(getActivity(), "Biller Response Not Present", Toast.LENGTH_SHORT).show();
////                                            }
//                                        }
//                                    }else {
//                                        Toast.makeText(getActivity(), "Biller Response Not Present", Toast.LENGTH_SHORT).show();
//
//                                    }
//
//
//
//
//                                    break;
//
//                                default:
//                                    showAlert("Something went wrong. Please try again.");
//                                    break;
//                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.v("SelectedBilDataTry", "ValidateBill" + e.toString());
                        }
                    }
                }).sendRequest();
    }

}


