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
import android.text.InputFilter;
import android.text.InputType;
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
import com.onezed.ExeDec;
import com.onezed.GlobalVariable.CustomAlertDialog;
import com.onezed.GlobalVariable.HttpHandler;
import com.onezed.Model.CustomerParameterBiller;
import com.onezed.Model.UserProfileModel;
import com.onezed.R;
import com.onezed.databinding.FragmentLoanRepaymentBBPSBinding;
import com.onezed.databinding.FragmentMunicipalTaxesBBPSBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;


public class MunicipalTaxesBBPS extends Fragment {

    private FragmentMunicipalTaxesBBPSBinding binding;
    private List<CustomerParameterBiller> billerList = new ArrayList<>();
    private FaderalBillerAdapter billerAdapter;

    String value,selectedBillerName,selectedBillerId,selectedBillerCategory,selectedBillerType,selectedBilId,
            refId,actionType,userId,amount,selectedBillerCoverage,selectedBillerDescription;
    boolean isCuatomParam=false;
    boolean isPayNow=false;
    // JSONObject payload = new JSONObject();
    JSONArray tagsArray = new JSONArray();
    JSONArray customerParamsArray=new JSONArray();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= FragmentMunicipalTaxesBBPSBinding.inflate(inflater, container, false);
        if (getArguments() != null) {
            value = getArguments().getString("PAGE");
        }
        billerAdapter = new FaderalBillerAdapter(getActivity(), android.R.layout.simple_list_item_1, billerList);
        binding.municipalTaxListView.setAdapter(billerAdapter);

        fetchBillerApi();

        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    // Create the customerParamsRequest and tags array

                    // Loop through the dynamic layout and collect input data
                    for (int i = 0; i < binding.dynamicLayout.getChildCount(); i++) {
                        View child = binding.dynamicLayout.getChildAt(i);
                        if (child instanceof EditText) {
                            String name = ((TextView) binding.dynamicLayout.getChildAt(i - 1)).getText().toString();
                            String value = ((EditText) child).getText().toString();

                            // Create a tag object for each input
                            JSONObject tagObject = new JSONObject();
                            tagObject.put("name", name);
                            tagObject.put("value", value);

                            // Add the tag object to the tags array
                            tagsArray.put(tagObject);
                        }
                    }
                    // Send data to the API
                    sendSubmitData(tagsArray);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error in input data", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.municipalTaxListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the clicked item
                CustomerParameterBiller selectedBiller = billerList.get(position);

                // Show a Toast message with the service provider name
                Toast.makeText(getActivity(), "Selected item: " + selectedBiller.getBillerName() , Toast.LENGTH_SHORT).show();
                selectedBillerName=selectedBiller.getBillerName();
                selectedBillerId=selectedBiller.getBillerId();
                selectedBillerCategory=selectedBiller.getBillerCategory();
                selectedBillerType = selectedBiller.getBillerType();
                selectedBillerCoverage = selectedBiller.getBillerCoverage();
                selectedBillerDescription = selectedBiller.getBillerDescription();

                binding.etMunicipalTaxes.setVisibility(View.GONE);
                binding.municipalTaxListView.setVisibility(View.GONE);
                binding.dynamicLayout.setVisibility(View.VISIBLE);
                binding.submitBtn.setVisibility(View.VISIBLE);
                getCustomerParameter(selectedBiller.getBillerId(), selectedBiller.getBillerName());
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

        binding.btnBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tagsArray = new JSONArray();
                if ( isCuatomParam && !isPayNow){
                    binding.etMunicipalTaxes.setVisibility(View.VISIBLE);
                    binding.municipalTaxListView.setVisibility(View.VISIBLE);
                    binding.dynamicLayout.setVisibility(View.GONE);
                    binding.submitBtn.setVisibility(View.GONE);
                    binding.billerDetails.setVisibility(View.GONE);
                    binding.payBtn.setVisibility(View.GONE);
                    isCuatomParam=false;
                    isPayNow=false;
                    Log.v("BBPS","listView ");

                } else if (isPayNow) {
                    binding.etMunicipalTaxes.setVisibility(View.GONE);
                    binding.municipalTaxListView.setVisibility(View.GONE);
                    binding.dynamicLayout.setVisibility(View.VISIBLE);
                    binding.submitBtn.setVisibility(View.VISIBLE);
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
                    binding.etMunicipalTaxes.setVisibility(View.VISIBLE);
                    binding.municipalTaxListView.setVisibility(View.VISIBLE);
                    binding.dynamicLayout.setVisibility(View.GONE);
                    binding.submitBtn.setVisibility(View.GONE);
                    binding.billerDetails.setVisibility(View.GONE);
                    binding.payBtn.setVisibility(View.GONE);
                    isCuatomParam=false;
                    isPayNow=false;
                    Log.v("BBPS","listView ");

                } else if (isPayNow) {
                    binding.etMunicipalTaxes.setVisibility(View.GONE);
                    binding.municipalTaxListView.setVisibility(View.GONE);
                    binding.dynamicLayout.setVisibility(View.VISIBLE);
                    binding.submitBtn.setVisibility(View.VISIBLE);
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
//                }
//                else {
//                    startActivity(new Intent(getActivity(), HomeActivity.class));
//                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);


        return  binding.getRoot();
    }

    private void fetchBillerApi() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        ExeDec eb=new ExeDec(electricityBiller);
        new HttpHandler("https://onezed.app/api/search-biller-category?search=Municipal Taxes&&page=0&&pagesize=1000", getContext())
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
                                        String biller_description = obj.getString("billerDescription");

                                        CustomerParameterBiller biller = new CustomerParameterBiller();

                                        biller.setBillerName(biller_name);
                                        biller.setBillerId(biller_id);
                                        biller.setBillerCategory(biller_category);
                                        biller.setBillerType(biller_type);
                                        biller.setBillerCoverage(biller_coverage);
                                        biller.setBillerDescription(biller_description);
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
                fetchBilApi();
            }
        }, 0000); // 5000 milliseconds = 5 seconds

    }
    private void fetchBilApi() {
        // Create a Set to remove duplicates based on the JSON object content
        Set<String> uniqueTagsSet = new HashSet<>();
        JSONArray uniqueTagsArray = new JSONArray();

        // Iterate over the original tagsArray to filter duplicates
        for (int i = 0; i < tagsArray.length(); i++) {
            try {
                JSONObject tagObject = tagsArray.getJSONObject(i); // Get each JSON object
                String tagString = tagObject.toString(); // Convert JSON object to a string for comparison
                if (uniqueTagsSet.add(tagString)) { // Add to Set and check for duplicates
                    uniqueTagsArray.put(tagObject); // Add unique JSON object back to JSONArray
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject requestObj = new JSONObject();
        try {
            // Adding top-level fields
            requestObj.put("billerId", selectedBillerId);
            requestObj.put("billerName", selectedBillerName);
            requestObj.put("billerCategory", selectedBillerCategory);
            requestObj.put("macAdress", JSONObject.NULL); // For null value
            requestObj.put("customerMobNo", UserProfileModel.getInstance().getMobileNo());

            // Create customerParamsRequest and add the unique tags
            JSONObject customerParamsRequest = new JSONObject();
            customerParamsRequest.put("tags", uniqueTagsArray);

            // Add customerParamsRequest to the main object
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
                        Log.v("fetchBillResponse", code + " " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            switch (code) {
                                case 200:
                                    JSONObject dataObject = jsonObject.getJSONObject("data");
                                    selectedBilId = dataObject.getString("billId");

//                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
//                                        //fetchBillByRequestId(selectedBilId);
//                                        fetchBillByRequestId(selectedBilId);
//                                    }, 15000); // 15 seconds delay

                                    if (jsonObject.has("status") && "success".equals(jsonObject.getString("status"))) {
                                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                            //fetchBillByRequestId(selectedBilId);
                                            fetchBillByRequestId(selectedBilId);
                                        }, 15000); // 15 seconds delay
                                    } else {
                                        CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", jsonObject.getString("message"));
                                        dialog.show();
                                        dialog.onPositiveButton(view -> dialog.dismiss());
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
    private void fetchBillByRequestId(String billId) {
        Log.v("billId",billId +" "+"https://onezed.app/api/fetch-bill-by-requestId?billId="+billId);
        String bilId="https://onezed.app/api/fetch-bill-by-requestId?billId="+billId;

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

                            switch (code) {
                                case 200:
                                    JSONObject dataObject = jsonResponse.getJSONObject("data");
                                    // tagsArray = new JSONArray();
                                    if (dataObject.has("genericResponse")){
                                        JSONObject genericResponse = dataObject.getJSONObject("genericResponse");
                                        if (genericResponse.getString("statusCode").equals("500")){
                                            CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name), genericResponse.getString("message"));
                                            dialog.show();
                                            dialog.onPositiveButton(view -> dialog.dismiss());
                                        }
                                        else if (dataObject.has("billerResponse")){
                                            JSONObject billerResponse = dataObject.getJSONObject("billerResponse");

                                            JSONArray additionalInfoArray = dataObject.getJSONArray("additionalInfo");
                                            refId=dataObject.getString("refId");
                                            actionType=dataObject.getString("actionType");
                                            userId=dataObject.getString("userId");
                                            customerParamsArray = dataObject.getJSONArray("customerParams");


                                            //  if (!billerResponse.equals(null)){

                                            binding.dynamicLayout.setVisibility(View.GONE);
                                            binding.submitBtn.setVisibility(View.GONE);
                                            binding.billerDetails.setVisibility(View.VISIBLE);
                                            binding.payBtn.setVisibility(View.VISIBLE);
                                            isCuatomParam=false;
                                            isPayNow=true;
                                            // isOperatorUI=true;
                                            // Log the entire billerResponse object for debugging
                                            Log.v("BillerResponse", billerResponse.toString());

                                            // Call a method to display this data dynamically in the UI
                                            displayBillerResponse(billerResponse,additionalInfoArray);

//                                        // Extracting data from billerResponse
//                                        String billId = billerResponse.getString("billId");
//                                        String amount = billerResponse.getString("amount");
//                                        String billDate = billerResponse.getString("billDate");
//                                        String billNumber = billerResponse.getString("billNumber");
//                                        String billPeriod = billerResponse.getString("billPeriod");
//                                        String dueDate = billerResponse.getString("dueDate");
//                                        String customerName = billerResponse.getString("customerName");
//                                        boolean amountOption = billerResponse.getBoolean("amountOption");
//
//                                        // Log extracted details for debugging
//                                        Log.v("BillerResponse", "Bill ID: " + billId);
//                                        Log.v("BillerResponse", "Amount: " + amount);
//                                        Log.v("BillerResponse", "Bill Date: " + billDate);
//                                        Log.v("BillerResponse", "Bill Number: " + billNumber);
//                                        Log.v("BillerResponse", "Due Date: " + dueDate);
//                                        Log.v("BillerResponse", "Customer Name: " + customerName);
//                                        Log.v("BillerResponse", "Amount Option: " + amountOption);
//
//                                        // Call a method to display this data dynamically in the UI
//                                        displayBillerResponse(billerResponse);
                                            // }
//                                            else {
//                                                Toast.makeText(getActivity(), "Biller Response Not Present", Toast.LENGTH_SHORT).show();
//                                            }
                                        }
                                    }else {
                                        Toast.makeText(getActivity(), "Biller Response Not Present", Toast.LENGTH_SHORT).show();

                                    }




                                    break;

                                default:
                                    showAlert("Something went wrong. Please try again.");
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.v("SelectedBilDataTry", "ValidateBill" + e.toString());
                        }
                    }
                }).sendRequest();
    }
    private void getCustomerParameter(String billerId, String selectedBiller) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        ExeDec eb = new ExeDec(electricityBiller);
        Log.v("billerIdRequest", " " + "https://onezed.app/api/get-customer-parameter?billerId=" + billerId);

        new HttpHandler("https://onezed.app/api/get-customer-parameter?billerId=" + billerId, getContext())
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
                        Log.v("billerIdResponse", code + " " + response);
                        try {
                            JSONObject responseObject = new JSONObject(response);

                            if (responseObject.getBoolean("success")) {
                                JSONArray dataArray = responseObject.getJSONArray("data");

                                // Clear previous views
                                binding.dynamicLayout.removeAllViews();

                                // Biller Name TextView
                                TextView billerNameTextView = new TextView(getContext());
                                billerNameTextView.setText(selectedBiller+" "+selectedBillerCoverage);
                                billerNameTextView.setTextSize(20);
                                billerNameTextView.setTextColor(Color.BLACK);
                                billerNameTextView.setTypeface(null, Typeface.BOLD);
                                billerNameTextView.setGravity(Gravity.CENTER);
                                billerNameTextView.setPadding(0, 16, 0, 8);
                                binding.dynamicLayout.addView(billerNameTextView);

                                // Loop through the data array
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject dataObject = dataArray.getJSONObject(i);

                                    if (dataObject.getBoolean("visibility")) {
                                        String customParamName = dataObject.getString("customParamName");
                                        String dataType = dataObject.getString("dataType");
                                        boolean optional = dataObject.getBoolean("optional");
                                        int minLength = dataObject.optInt("minLength", 0);
                                        int maxLength = dataObject.optInt("maxLength", Integer.MAX_VALUE);
                                        String regex = dataObject.optString("regex", null);

                                        // Create a new TextView for the parameter name
                                        TextView paramNameTextView = new TextView(getContext());
                                        paramNameTextView.setText(customParamName);
                                        paramNameTextView.setTextSize(16);
                                        paramNameTextView.setTextColor(Color.BLACK);
                                        paramNameTextView.setTypeface(null, Typeface.BOLD);
                                        paramNameTextView.setPadding(0, 16, 0, 8);
                                        binding.dynamicLayout.addView(paramNameTextView);

                                        // Create an EditText for user input
                                        EditText paramEditText = new EditText(getContext());
                                        paramEditText.setLayoutParams(new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                        ));
                                        paramEditText.setHint(optional ? "Optional" : "Required");
                                        paramEditText.setInputType(getInputTypeFromDataType(dataType));
                                        paramEditText.setBackgroundResource(R.drawable.transparent_border_black);

                                        int paddingInDp = 10;
                                        int paddingInPx = (int) TypedValue.applyDimension(
                                                TypedValue.COMPLEX_UNIT_DIP,
                                                paddingInDp,
                                                getResources().getDisplayMetrics()
                                        );
                                        paramEditText.setPadding(paddingInPx, 25, 0, 25);

                                        if (minLength > 0 || maxLength < Integer.MAX_VALUE) {
                                            paramEditText.setFilters(new InputFilter[]{
                                                    new InputFilter.LengthFilter(maxLength)
                                            });
                                        }

                                        if (regex != null && !regex.isEmpty()) {
                                            paramEditText.setOnFocusChangeListener((view, hasFocus) -> {
                                                if (!hasFocus) {
                                                    String input = paramEditText.getText().toString();
                                                    if (!input.matches(regex)) {
                                                        paramEditText.setError("Invalid format");
                                                    }
                                                }
                                            });
                                        }

                                        // Add EditText to the layout
                                        binding.dynamicLayout.addView(paramEditText);
                                    }
                                }

                                // Add 50dp space after the last EditText
                                View spaceView = new View(getContext());
                                LinearLayout.LayoutParams spaceParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics())
                                );
                                spaceView.setLayoutParams(spaceParams);
                                binding.dynamicLayout.addView(spaceView);

                                // Add the description at the bottom, after all EditTexts
                                if (dataArray.length() > 0) {
                                    TextView billerDescriptionTextView = new TextView(getContext());
                                    billerDescriptionTextView.setText(selectedBillerDescription);
                                    billerDescriptionTextView.setTextSize(14);
                                    billerDescriptionTextView.setTextColor(Color.DKGRAY);
                                    billerDescriptionTextView.setPadding(0, 16, 0, 16); // Padding for better spacing
                                    binding.dynamicLayout.addView(billerDescriptionTextView);
                                }

                                // No data available case
                                if (dataArray.length() == 0) {
                                    TextView noDataTextView = new TextView(getContext());
                                    noDataTextView.setText("No data available");
                                    noDataTextView.setTextSize(16);
                                    noDataTextView.setPadding(0, 16, 0, 8);
                                    binding.dynamicLayout.addView(noDataTextView);
                                }
                            } else {
                                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Invalid response format", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).sendRequest();
    }

    // Method to dynamically display details in the UI
    private void displayBillerResponse(JSONObject billerResponse, JSONArray additionalInfoArray) {
        try {
            isCuatomParam=true;
            // Clear any existing views from the details layout and additional info layout
            binding.detailsLayout.removeAllViews();
            binding.additionalInfoLayout.removeAllViews();
            binding.headerLayout.removeAllViews();
            // Create a bold title TextView
            TextView titleTextView = new TextView(getContext());
            titleTextView.setText(selectedBillerName); // Set your desired title text
            titleTextView.setTextSize(20); // Set the size of the text
            titleTextView.setTypeface(titleTextView.getTypeface(), Typeface.BOLD); // Make the text bold
            titleTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.black)); // Set the text color to black
            titleTextView.setGravity(Gravity.CENTER); // Center align the text
            titleTextView.setPadding(16, 24, 16, 24); // Add padding for better appearance

            // Add the title TextView above the details layout
            binding.headerLayout.addView(titleTextView, 0); // Add the title at the top
            // Iterate over all keys in the JSON object
            Iterator<String> keys = billerResponse.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = billerResponse.optString(key, "N/A");

                // Skip the keys 'amountOption' and 'tagList'
                if (key.equals("amountOption") || key.equals("tagList")) {
                    continue;
                }

                // Format specific keys
                if (key.equals("billId")) {
                    key = "Bill ID";
                } else if (key.equals("customerName")) {
                    key = "Customer Name";
                } else if (key.equals("amount")) {
                    key = "Amount";
                    amount=value;
                } else if (key.equals("custConvFee")) {
                    key = "Customer Convenience Fee";
                } else if (key.equals("couCustConvFee")) {
                    key = "Coupon Convenience Fee";
                }



                // Create TextViews for the key and value
                TextView keyTextView = new TextView(getContext());
                keyTextView.setText(key + ":");
                keyTextView.setTextSize(18);
                keyTextView.setTypeface(keyTextView.getTypeface(), Typeface.BOLD);
                keyTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
                keyTextView.setPadding(10, 16, 10, 8);

                TextView valueTextView = new TextView(getContext());
                valueTextView.setText(value);
                valueTextView.setTextSize(16);
                valueTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                valueTextView.setPadding(10, 16, 10, 8);

                // Create a horizontal LinearLayout to hold the key-value pair
                LinearLayout linearLayout = new LinearLayout(getContext());
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));

                // Set background for detailsLayout
                binding.detailsLayout.setBackgroundResource(R.drawable.transparent_border_black);
                //binding.dynamicLayout.setPadding(16, 10, 16, 10);

                // Add the key and value TextViews to the horizontal LinearLayout
                linearLayout.addView(keyTextView);
                linearLayout.addView(valueTextView);

                // Add the horizontal LinearLayout to the parent vertical LinearLayout
                binding.detailsLayout.addView(linearLayout);
            }

            // If additionalInfo is not null, add a title and display its content
            if (additionalInfoArray != null && additionalInfoArray.length() > 0) {
                // Add the title 'Additional Info' after details layout
//                TextView additionalInfoTitle = new TextView(getContext());
//                additionalInfoTitle.setText("Additional Info:");
//                additionalInfoTitle.setTextSize(20);
//                additionalInfoTitle.setTypeface(additionalInfoTitle.getTypeface(), Typeface.BOLD);
//                additionalInfoTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
//                additionalInfoTitle.setPadding(10, 24, 10, 8);
                binding.additionalInfoTxt.setVisibility(View.VISIBLE);
                binding.additionalInfoTxt.setText("Additional Info:");
                binding.additionalInfoTxt.setTextSize(20);
                binding.additionalInfoTxt.setTypeface( binding.additionalInfoTxt.getTypeface(), Typeface.BOLD);
                binding.additionalInfoTxt.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                // binding.additionalInfoTxt.setPadding(10, 24, 10, 8);

                // Display additional info items
                for (int i = 0; i < additionalInfoArray.length(); i++) {
                    JSONObject infoObject = additionalInfoArray.getJSONObject(i);
                    String name = infoObject.optString("name", "N/A");
                    String value = infoObject.optString("value", "N/A");

                    // Create TextViews for the additional info
                    TextView keyTextView = new TextView(getContext());
                    keyTextView.setText(name + ":");
                    keyTextView.setTextSize(18);
                    keyTextView.setTypeface(keyTextView.getTypeface(), Typeface.BOLD);
                    keyTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
                    keyTextView.setPadding(10, 16, 10, 8);

                    TextView valueTextView = new TextView(getContext());
                    valueTextView.setText(value);
                    valueTextView.setTextSize(16);
                    valueTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                    valueTextView.setPadding(10, 16, 10, 8);

                    // Create a horizontal LinearLayout to hold the key-value pair
                    LinearLayout additionalLayout = new LinearLayout(getContext());
                    additionalLayout.setOrientation(LinearLayout.HORIZONTAL);
                    additionalLayout.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));

                    // Set background for additional info layout
                    binding.additionalInfoLayout.setBackgroundResource(R.drawable.transparent_border_black);
                    binding.dynamicLayout.setPadding(16, 10, 16, 10);

                    // Add the key and value TextViews to the horizontal LinearLayout
                    additionalLayout.addView(keyTextView);
                    additionalLayout.addView(valueTextView);

                    // Add the horizontal LinearLayout to the parent vertical LinearLayout
                    binding.additionalInfoLayout.addView(additionalLayout);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DisplayBillerResponse", "Error displaying biller response: " + e.getMessage());
        }
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

    private void billPaymentRequest(String selectedBilId) {
        JSONObject requestObj = new JSONObject();
        try {
            // Adding top-level fields
            requestObj.put("actionType", actionType);
            requestObj.put("billId", selectedBilId);
            requestObj.put("billerCategory", selectedBillerCategory);
            requestObj.put("billerId", selectedBillerId);
            requestObj.put("billerName", selectedBillerName);

            requestObj.put("customerMobNo", selectedBillerName);
            requestObj.put("paymentChannel", "Agent");
            requestObj.put("paymentInformation", "payment");
            requestObj.put("paymentMode", "Cash");
            requestObj.put("refId", refId);
            requestObj.put("txnAmount",Integer.parseInt(amount) );


            // requestObj.put("macAdress", JSONObject.NULL); // For null value
            requestObj.put("customerMobNo", UserProfileModel.getInstance().getMobileNo());


            // Adding customerParamsRequest to the main object
            requestObj.put("customerParams", customerParamsArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v("billPaymentReq",requestObj.toString());
        // Setting headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        // Sending the request
        new HttpHandler("https://onezed.app/api/bill-payment-request", getContext())
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
                        Log.v("billPaymentRes ",code+" "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            switch (code) {
                                case 200:
                                    if (jsonObject.has("status") && jsonObject.getString("status").equals("success")){
                                        JSONObject dataObject = jsonObject.getJSONObject("data");
                                        // JSONObject genericResponseObj=dataObject.getJSONObject("genericResponse");
                                        String payTaxId= dataObject.getString("txnId");
                                        getTranactionDetails(payTaxId);
                                    }
//                                    JSONObject dataObject = jsonObject.getJSONObject("data");
//                                   // selectedBilId = dataObject.getString("billId");
//
//                                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            //fetchBillByRequestId(selectedBilId);
//                                            fetchBillByRequestId(selectedBilId);
//                                        }
//                                    }, 10000); // 5000 milliseconds = 5 seconds
//
//
//                                    if (jsonObject.has("status")) {
//                                        // String status = jsonObject.getString("status");
//                                        if (jsonObject.getString("status")=="success") {
//                                            CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", jsonObject.getString("message"));
//                                            dialog.show();
//                                            dialog.onPositiveButton(view -> {
//                                                dialog.dismiss();
//                                                startActivity(new Intent(getActivity(), HomeActivity.class));
//                                            });
//                                        } else {
//                                            CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", jsonObject.getString("message"));
//                                            dialog.show();
//                                            dialog.onPositiveButton(view -> dialog.dismiss());
//                                        }
//                                    }
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

    private void getTranactionDetails(String payTaxId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        //ExeDec eb=new ExeDec(electricityBiller);
        Log.v("TranactionReq","https://onezed.app/api/get-transaction-details/"+payTaxId);
        new HttpHandler("https://onezed.app/api/get-transaction-details/"+payTaxId, getContext())
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
                        Log.v("TranactionRes",code+" "+response);
//                        try {
//                            JSONArray jsonArray = new JSONArray(response);
//                            switch (code) {
//                                case 200:
//
//                                    billerList.clear();  // Clear the list before adding new data
//                                    for (int i = 0; i < jsonArray.length(); i++) {
//                                        JSONObject obj = jsonArray.getJSONObject(i);
//                                        // Add the name and code to the list
//                                        String biller_name = obj.getString("billerName");
//                                        String biller_id = obj.getString("billerId");
//                                        String biller_category = obj.getString("billerCategory");
//
//                                        CustomerParameterBiller biller = new CustomerParameterBiller();
//
//                                        biller.setBillerName(biller_name);
//                                        biller.setBillerId(biller_id);
//                                        biller.setBillerCategory(biller_category);
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
                    }
                }).sendRequest();
    }
    private void validateBillId() {
        JSONObject requestObj = new JSONObject();
        try {
            // Adding top-level fields
            requestObj.put("billerId", selectedBillerId);
            requestObj.put("billerName", selectedBillerName);
            requestObj.put("billerCategory", selectedBillerCategory);
            requestObj.put("macAdress", JSONObject.NULL); // For null value
            //  requestObj.put("customerMobNo", UserProfileModel.getInstance().getMobileNo());


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
        Log.v("validateBillIdReq",requestObj.toString());
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
                        Log.v("validateBillIdRes ",code+" "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            switch (code) {
                                case 200:
                                    if (jsonObject.has("status") && (jsonObject.getString("status").equals("success"))){
                                        JSONObject dataObject = jsonObject.getJSONObject("data");
                                        selectedBilId = dataObject.getString("billId");

                                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                //fetchBillByRequestId(selectedBilId);
                                                billPaymentRequest(selectedBilId);

                                            }
                                        }, 10000); // 5000 milliseconds = 5 seconds


//                                        if (jsonObject.has("status")) {
//                                            // String status = jsonObject.getString("status");
//                                            if (jsonObject.getString("status")=="success") {
//                                                CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", jsonObject.getString("message"));
//                                                dialog.show();
//                                                dialog.onPositiveButton(view -> {
//                                                    dialog.dismiss();
//                                                    startActivity(new Intent(getActivity(), HomeActivity.class));
//                                                });
//                                            } else {
//                                                CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", jsonObject.getString("message"));
//                                                dialog.show();
//                                                dialog.onPositiveButton(view -> dialog.dismiss());
//                                            }
//                                        }
                                    }else {
                                        Log.v("validateBillIdRes ","response");
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
}