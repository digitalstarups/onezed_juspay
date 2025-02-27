package com.onezed.Fragment;

import static com.onezed.GlobalVariable.baseUrl.electricityBiller;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.onezed.Activity.HomeActivity;
import com.onezed.Adapter.FaderalBillerAdapter;
import com.onezed.Adapter.FaderalBillerSubscriptionAdapter;
import com.onezed.Adapter.SubscriptionTabAdapter;
import com.onezed.ExeDec;
import com.onezed.GlobalVariable.CustomAlertDialog;
import com.onezed.GlobalVariable.GlobalVariable;
import com.onezed.GlobalVariable.HttpHandler;
import com.onezed.Model.CustomerParameterBiller;
import com.onezed.Model.UserProfileModel;
import com.onezed.R;
import com.onezed.databinding.FragmentSubscriptionBBPSBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


public class SubscriptionBBPS extends Fragment implements FaderalBillerSubscriptionAdapter.OnBillerClickListener, SubscriptionTabAdapter.OnPlanClickListener{


    private FragmentSubscriptionBBPSBinding binding;
    private List<CustomerParameterBiller> billerList = new ArrayList<>();
    private FaderalBillerAdapter billerAdapter;
    String value,selectedBillerName,selectedBillerId,selectedBillerCategory,selectedBillerType,selectedBilId,
            refId,actionType,userId,amount,isAdhoc,planId,circle,phone,selectedBillerCoverage;
    boolean isCuatomParam=false;
    boolean isPayNow=false;
    // JSONObject payload = new JSONObject();
    JSONArray tagsArray = new JSONArray();
    JSONArray customerParamsArray=new JSONArray();
    private int noOfTabs;
    ArrayList<HashMap<String, String>> tabList = new ArrayList<>();
    ArrayList<ArrayList<HashMap<String, String>>> planList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSubscriptionBBPSBinding.inflate(inflater, container, false);
        if (getArguments() != null) {
            value = getArguments().getString("PAGE");
        }
        billerAdapter = new FaderalBillerAdapter(getActivity(), android.R.layout.simple_list_item_1, billerList);
        binding.subscriptionListView.setAdapter(billerAdapter);

        fetchBillerApi();

        binding.subscriptionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the clicked item
                CustomerParameterBiller selectedBiller = billerList.get(position);

                // Show a Toast message with the service provider name
                Toast.makeText(getActivity(), "Selected item: " + selectedBiller.getBillerName(), Toast.LENGTH_SHORT).show();
                selectedBillerName = selectedBiller.getBillerName();
                selectedBillerId = selectedBiller.getBillerId();
                selectedBillerCategory = selectedBiller.getBillerCategory();
                selectedBillerType = selectedBiller.getBillerType();
                selectedBillerCoverage = selectedBiller.getBillerCoverage();

                isAdhoc=selectedBiller.getAdhocBiller();
                GlobalVariable.selectedBillerName=selectedBiller.getBillerName();
                GlobalVariable.selectedBillerId=selectedBiller.getBillerId();
                GlobalVariable.selectedBillerCategory=selectedBiller.getBillerCategory();


                getPlanCategory(selectedBiller.getBillerId());
                isCuatomParam=true;
                isPayNow = false;
            }
        });


        binding.payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateBillId();
            }
        });

        if(GlobalVariable.isSelected){
            binding.rechargePlanLayout.setVisibility(View.GONE);
        }


        binding.btnBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tagsArray = new JSONArray();
                if ( isCuatomParam && !isPayNow){
                    binding.etSubscription.setVisibility(View.VISIBLE);
                    binding.subscriptionListView.setVisibility(View.VISIBLE);
                    binding.billerDetails.setVisibility(View.GONE);
                    binding.payBtn.setVisibility(View.GONE);
                    isCuatomParam=false;
                    isPayNow=false;
                    Log.v("BBPS","listView ");

                } else if (isPayNow) {
                    binding.etSubscription.setVisibility(View.GONE);
                    binding.subscriptionListView.setVisibility(View.GONE);
                    binding.billerDetails.setVisibility(View.GONE);
                    binding.payBtn.setVisibility(View.GONE);

                    isCuatomParam=true;
                    isPayNow=false;

                    Log.v("BBPS","custom param");
                } else {
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
            }
        });

        //Back Press Handler
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                tagsArray = new JSONArray();
                if ( isCuatomParam && !isPayNow){
                    binding.etSubscription.setVisibility(View.VISIBLE);
                    binding.subscriptionListView.setVisibility(View.VISIBLE);
                    binding.billerDetails.setVisibility(View.GONE);
                    binding.payBtn.setVisibility(View.GONE);
                    isCuatomParam=false;
                    isPayNow=false;
                    Log.v("BBPS","listView ");

                } else if (isPayNow) {
                    binding.etSubscription.setVisibility(View.GONE);
                    binding.subscriptionListView.setVisibility(View.GONE);
                    binding.billerDetails.setVisibility(View.GONE);
                    binding.payBtn.setVisibility(View.GONE);

                    isCuatomParam=true;
                    isPayNow=false;

                    Log.v("BBPS","custom param");
                } else {
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
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);


        return binding.getRoot();
    }

    private void getPlanCategory(String billerId) {
        binding.rechargePlanLayout.setVisibility(View.VISIBLE);
        binding.subscriptionListView.setVisibility(View.GONE);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");


        Log.v("billerIdRequest", " " + "https://onezed.app/api/get-plan-category?billerId=" + billerId);
        new HttpHandler("https://onezed.app/api/get-plan-category?billerId=" + billerId, getContext())
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
                        Log.v("billerIdRes", code + " " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("plans")) {
                                JSONObject plansObj = jsonObject.getJSONObject("plans");

                                tabList = new ArrayList<>();
                                planList = new ArrayList<>();
                                noOfTabs = plansObj.length();
                                Iterator<String> keys = plansObj.keys(); // Get all keys (array names) from the "plans" object
                                while (keys.hasNext()) {
                                    String key = keys.next(); // Key represents the tab name, e.g., "1 MONTH"

                                    if (key == null || key.trim().isEmpty()) {
                                        Log.v("billerIdRes", "Empty List");
                                        CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name), "Plan is not Available");
                                        dialog.show();
                                        dialog.onPositiveButton(view ->{
                                            dialog.dismiss();
                                            binding.rechargePlanLayout.setVisibility(View.GONE);
                                            binding.subscriptionListView.setVisibility(View.VISIBLE);

                                        } );
                                        //   continue; // Skip this iteration
                                    }

                                    HashMap<String, String> tabItem = new HashMap<>();
                                    tabItem.put("name", key); // Store tab name
                                    tabList.add(tabItem);

                                    // Get the array corresponding to the current key
                                    JSONArray planArray = plansObj.getJSONArray(key);
                                    ArrayList<HashMap<String, String>> planDetailsList = new ArrayList<>();
                                    // noOfTabs = planArray.length();
                                    for (int i = 0; i < planArray.length(); i++) {
                                        JSONObject planObj = planArray.getJSONObject(i);
                                        HashMap<String, String> planItem = new HashMap<>();

                                        // Store "amountInRupees"
                                        if (planObj.has("amountInRupees")) {
                                            planItem.put("amountInRupees", String.valueOf(planObj.getInt("amountInRupees")));
                                        }
                                        if (planObj.has("description")) {
                                            planItem.put("description", (planObj.getString("description")));
                                        }
                                        if (planObj.has("planId")){
                                            planItem.put("planId", (planObj.getString("planId")));
                                        }
                                        planId=planObj.getString("planId");

                                        // Store additional "name"-"value" pairs from "additionalInfo"
                                        if (planObj.has("additionalInfo")) {
                                            JSONObject additionalInfo = planObj.getJSONObject("additionalInfo");
                                            if (additionalInfo.has("tags")) {
                                                JSONArray tagsArray = additionalInfo.getJSONArray("tags");
                                                for (int j = 0; j < tagsArray.length(); j++) {
                                                    JSONObject tagObj = tagsArray.getJSONObject(j);
                                                    if (tagObj.has("name") && tagObj.has("value")) {
                                                        planItem.put(tagObj.getString("name"), tagObj.getString("value"));
                                                        if (tagObj.getString("name").equals("Circle")) { // Removed extra semicolon
                                                            circle = tagObj.getString("value");
                                                        }
                                                    }


                                                }
                                            }
                                        }

                                        // Add the plan item to the planDetailsList
                                        planDetailsList.add(planItem);
                                    }

                                    // Add the list of plans for the current tab to planList
                                    planList.add(planDetailsList);
                                }

                               // getCustomerParameter(billerId);
                                Log.v("billerIdResA"," "+planList);
                                // Call the method to set up the ViewPager and TabLayout
                                if (planList.size() == 1 && planList.get(0).isEmpty()) {
                                    Toast.makeText(getContext(), "No plans available", Toast.LENGTH_SHORT).show();
                                }else{
                                    adapterCall();
                                }


                            } else {
                                // Handle the case when "plans" is missing
                                CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name), "Error to fetch the plans data");
                                dialog.show();
                                dialog.onPositiveButton(view -> dialog.dismiss());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }).sendRequest();
    }

    private void adapterCall() {
        Log.v("billerIdRes", "adapterCall " + planList);

        if (binding.viewpager.getAdapter() != null) {
            binding.viewpager.setAdapter(null);  // Remove old adapter
        }

        SubscriptionTabAdapter mDynamicFragmentAdapter = new SubscriptionTabAdapter(
                getActivity().getSupportFragmentManager(),
                noOfTabs,
                tabList,
                planList,
                this
        );

        // Set the adapter
        binding.viewpager.setAdapter(mDynamicFragmentAdapter);
        binding.tabs.setupWithViewPager(binding.viewpager);
        binding.viewpager.setCurrentItem(0);

        // Notify adapter about dataset change
        mDynamicFragmentAdapter.notifyDataSetChanged();
    }
//    private void getCustomerParameter(String BilerId) {
//        Map<String, String> headers = new HashMap<>();
//        headers.put("Content-Type", "application/json");
//        headers.put("Accept", "application/json");
//
//        Log.v("gCPRequest", " " + "https://onezed.app/api/get-customer-parameter?billerId=" + BilerId);
//        new HttpHandler("https://onezed.app/api/get-customer-parameter?billerId=" + BilerId, getContext())
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
//                        Log.v("gCPResponse", code + " " + response);
//
//                        try {
//                            JSONObject jsonResponse = new JSONObject(response);
//                            if (jsonResponse.getBoolean("success")) {
//                                JSONArray dataArray = jsonResponse.getJSONArray("data");
//                                JSONArray tagsArray = new JSONArray();
//
//                                for (int i = 0; i < dataArray.length(); i++) {
//                                    JSONObject paramObject = dataArray.getJSONObject(i);
//
//                                    JSONObject tagObject = new JSONObject();
//                                    tagObject.put("name", paramObject.getString("customParamName"));
//                                    if (paramObject.getString("customParamName").equals("APPLICATION NUMBER")){
//                                        tagObject.put("value", circle);
//                                    }
//                                    if (paramObject.getString("customParamName").equals("Id")){
//                                        tagObject.put("value", planId);
//                                    }
//                                    if (paramObject.getString("customParamName").equals("Mobile Number")){
//                                        tagObject.put("value", phone);
//                                    }
//                                    //tagObject.put("value", ""); // Initially empty, will be updated from another API
//
//                                    tagsArray.put(tagObject);
//                                }
//
//                                // Log the created tags array
//                                Log.v("tagsArray", tagsArray.toString());
//                                GlobalVariable.tags=tagsArray;
//
//                                // You can store this tagsArray and update its values later from another API response.
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Log.e("JSONParseError", "Error parsing JSON response "+e);
//                        }
//                    }
//
//                }).sendRequest();
//    }

    private void fetchBillerApi() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        ExeDec eb = new ExeDec(electricityBiller);
        new HttpHandler("https://onezed.app/api/search-biller-category?search=Subscription&&page=0&&pagesize=1000", getContext())
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
                        Log.v("billerCategoryResponse ",code+" "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            switch (code) {
                                case 200:
                                    billerList.clear();  // Clear the list before adding new data
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject obj = jsonArray.getJSONObject(i);
                                        String biller_name = obj.getString("billerName");
                                        String biller_id = obj.getString("billerId");
                                        String biller_category = obj.getString("billerCategory");
                                        String biller_type= obj.getString("billerType");
                                        String biller_coverage = obj.getString("billerCoverage");

                                        CustomerParameterBiller biller = new CustomerParameterBiller();

                                        biller.setBillerName(biller_name);
                                        biller.setBillerId(biller_id);
                                        biller.setBillerCategory(biller_category);
                                        biller.setBillerType(biller_type);
                                        biller.setBillerCoverage(biller_coverage);
                                        billerList.add(biller);
                                    }
                                    // Notify adapter with updated data
                                    getActivity().runOnUiThread(() -> billerAdapter.notifyDataSetChanged());
                                    break;
                                default:
                                    showAlert("Something went wrong. Please try again.");
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).sendRequest();
    }

    private void sendSubmitData(JSONArray payload) {
        // Add your API call logic here
        Log.d("Payload", payload.toString());
        Toast.makeText(getContext(), "Submit clicked with data: " + payload.toString(), Toast.LENGTH_SHORT).show();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //fetchBillByRequestId(selectedBilId);
               // fetchBilApi();
            }
        }, 10000); // 5000 milliseconds = 5 seconds

    }

    private void fetchBilApi() {
        JSONObject requestObj = new JSONObject();
        try {
            // Adding top-level fields
            requestObj.put("billerId", selectedBillerId);
            requestObj.put("billerName", selectedBillerName);
            requestObj.put("billerCategory", selectedBillerCategory);
            requestObj.put("macAdress", JSONObject.NULL); // For null value
            requestObj.put("customerMobNo", UserProfileModel.getInstance().getMobileNo());


            JSONObject customerParamsRequest = new JSONObject();
            // Add the tags array to customerParamsRequest
            customerParamsRequest.put("tags", tagsArray);

            // Add customerParamsRequest to the payload
            //  payload.put("customerParamsRequest", customerParamsRequest);


            // Adding customerParamsRequest to the main object
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
        new HttpHandler("https://onezed.app/api/fetch-bill", getContext())
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
                        Log.v("fetchBillResponse ", code + " " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            switch (code) {
                                case 200:
                                    JSONObject dataObject = jsonObject.getJSONObject("data");
                                    selectedBilId = dataObject.getString("billId");

                                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //fetchBillByRequestId(selectedBilId);
                                          //  fetchBillByRequestId(selectedBilId);
                                        }
                                    }, 15000); // 5000 milliseconds = 5 seconds

                                    if (jsonObject.has("status")) {
                                        // String status = jsonObject.getString("status");
                                        if (jsonObject.getString("status") == "success") {
//                                            CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", jsonObject.getString("message"));
//                                            dialog.show();
//                                            dialog.onPositiveButton(view -> {
//                                                dialog.dismiss();
//                                                startActivity(new Intent(getActivity(), HomeActivity.class));
//                                            });


                                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //fetchBillByRequestId(selectedBilId);
                                                  //  fetchBillByRequestId(selectedBilId);
                                                }
                                            }, 15000); // 5000 milliseconds = 5 seconds

                                        } else {
                                            CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", jsonObject.getString("message"));
                                            dialog.show();
                                            dialog.onPositiveButton(view -> dialog.dismiss());
                                        }
                                    }
                                    break;
                                default:
                                    CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", "Something Wrong, Please Try after some time");
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

    private int getInputTypeFromDataType(String dataType) {
        switch (dataType) {
            case "NUMERIC":
                return InputType.TYPE_CLASS_NUMBER;
            case "ALPHANUMERIC":
                return InputType.TYPE_CLASS_TEXT;
            default:
                return InputType.TYPE_CLASS_TEXT;
        }
    }

    private void showAlert(String message) {
        CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", message);
        dialog.show();
        dialog.onPositiveButton(view -> dialog.dismiss());
    }

    private void billPaymentRequest() {
        Log.v("SelectedBilData", "requestObj.toString()");
        JSONObject requestObj = new JSONObject();
        try {
            // Adding top-level fields
            requestObj.put("actionType", selectedBillerType);
            requestObj.put("bilId", selectedBilId);
            requestObj.put("billerCategory", selectedBillerCategory);
            requestObj.put("billerId", selectedBillerId);
            requestObj.put("billerName", selectedBillerName);


            JSONObject customerParamsRequest = new JSONObject();
            // Add the tags array to customerParamsRequest
            customerParamsRequest.put("tags", tagsArray);
            // Adding customerParamsRequest to the main object
            requestObj.put("customerParamsRequest", customerParamsRequest);

            requestObj.put("paymentChannel", "Agent");
            requestObj.put("paymentInformation", "payment");
            requestObj.put("paymentMode", "Cash");
            requestObj.put("refId", "mD0hn1ZIzkcHtMWU1SE2Mpnq9X832901405");
            // requestObj.put("tenantId", selectedBillerName);
            requestObj.put("txnAmount", "10");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.v("billPaymentRequest", requestObj.toString());

//        // Setting headers
//        Map<String, String> headers = new HashMap<>();
//        headers.put("Content-Type", "application/json");
//        headers.put("Accept", "application/json");
//
//        // Sending the request
//        new HttpHandler("https://onezed.app/api/bill-payment-request", getContext())
//                .setMediaType(HttpHandler.jsonMediaType)
//                .setRequestType(HttpHandler.RequestType.POST)
//                .setStringData(requestObj.toString()) // Pass the JSON payload as a string
//                .setLoading(true)
//                .setHeaders(headers)
//                .setOnResponse(new HttpHandler.HttpResponse() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name), getString(R.string.no_response));
//                        dialog.show();
//                        dialog.onPositiveButton(view -> dialog.dismiss());
//                    }
//
//                    @Override
//                    public void onResponse(int code, String response) {
//                        Log.v("SelectedBilData ",code+" "+response);
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            switch (code) {
//                                case 200:
//                                    if (jsonObject.has("status")) {
//                                        // String status = jsonObject.getString("status");
//                                        Log.v("error",jsonObject.toString());
//                                        if (jsonObject.getString("status").equals("success")) {
//                                            billPaymentRequest();
//                                        } else {
//                                            CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", "message");
//                                            dialog.show();
//                                            dialog.onPositiveButton(view -> dialog.dismiss());
//                                        }
//                                    }
//                                    break;
//                                default:
//                                    CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", "Something Wrong, Please Try after some time");
//                                    dialog.show();
//                                    dialog.onPositiveButton(view -> dialog.dismiss());
//                                    break;
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Log.v("error",e.toString());
//                        }
//                    }
//                }).sendRequest();
//
//
    }


    private void validateBillId() {

        Log.v("SelectedBilData", "requestObj.toString()");
        JSONObject requestObj = new JSONObject();
        try {
            // Adding top-level fields
            requestObj.put("billerId", selectedBillerId);
            requestObj.put("billerName", selectedBillerName);
            requestObj.put("macAdress", JSONObject.NULL); // For null value
            requestObj.put("billerCategory", selectedBillerCategory); // For null value


            JSONObject customerParamsRequest = new JSONObject();
            // Add the tags array to customerParamsRequest
            customerParamsRequest.put("tags", tagsArray);
            // Adding customerParamsRequest to the main object
            requestObj.put("customerParamsRequest", customerParamsRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v("validateBillIdRequest", requestObj.toString());
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
                        Log.v("validateBillIdResponse ", code + " " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            switch (code) {
                                case 200:
                                    if (jsonObject.has("status")) {
                                        // String status = jsonObject.getString("status");
                                        Log.v("error", jsonObject.toString());
                                        if (jsonObject.getString("status").equals("success")) {
                                             billPaymentRequest();
                                        } else {
                                            CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", "message");
                                            dialog.show();
                                            dialog.onPositiveButton(view -> dialog.dismiss());
                                        }
                                    }
                                    break;
                                default:
                                    CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", "Something Wrong, Please Try after some time");
                                    dialog.show();
                                    dialog.onPositiveButton(view -> dialog.dismiss());
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.v("error", e.toString());
                        }
                    }
                }).sendRequest();
    }

    @Override
    public void onBillerClick(CustomerParameterBiller biller) {
        Log.v("BillerDetails",biller.toString());
        GlobalVariable.selectedBillerName=biller.getBillerName();
        GlobalVariable.selectedBillerId=biller.getBillerId();
        GlobalVariable.selectedBillerCategory=biller.getBillerCategory();
    }

    @Override
    public void onPlanClick(HashMap<String, String> plan) {
        Log.v("BillerDetails", "Clicked Plan: " );
        binding.subscriptionListView.setVisibility(View.GONE);
    }
}