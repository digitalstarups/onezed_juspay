package com.onezed.Fragment;

import static com.onezed.GlobalVariable.baseUrl.electricityBiller;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import com.onezed.Activity.HomeActivity;
import com.onezed.Adapter.DynamicTabAdapter;
import com.onezed.Adapter.FaderalBillerRechargeAdapter;
import com.onezed.Adapter.PlanPagerAdapter;
import com.onezed.Adapter.RechargeTabAdapter;
import com.onezed.ExeDec;
import com.onezed.GlobalVariable.CustomAlertDialog;
import com.onezed.GlobalVariable.GlobalVariable;
import com.onezed.GlobalVariable.HttpHandler;
import com.onezed.GlobalVariable.SharedPrefManager;
import com.onezed.Model.CustomerParameterBiller;
import com.onezed.Model.Operator;

import com.onezed.R;
import com.onezed.databinding.FragmentMobilePrepaidPlanBinding;
import com.onezed.databinding.OperatorBottomSheetBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


public class MobilePrepaidPlanFragment extends Fragment implements FaderalBillerRechargeAdapter.OnBillerClickListener, RechargeTabAdapter.OnPlanClickListener{

    private FragmentMobilePrepaidPlanBinding binding;
    String name, phone,tenDightNumber,billerId;

    ArrayList<HashMap<String, String>> tabList = new ArrayList<>();
    ArrayList<ArrayList<HashMap<String, String>>> planList = new ArrayList<>();
    //List<Plan> planList;
    BottomSheetDialog sheetDialog;
    private OperatorBottomSheetBinding OperatorBinding;
    ImageView imgCancelDialog;
    List<Operator> OperatList = new ArrayList<>();
    SharedPrefManager sharedPrefManager;
    int userId;
    boolean isNumberFound = false;
    String categoryName,planId,circle;
    private List<CustomerParameterBiller> billerList = new ArrayList<>();
    private FaderalBillerRechargeAdapter billerAdapter;
    private int noOfTabs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMobilePrepaidPlanBinding.inflate(inflater, container, false);
        sharedPrefManager = new SharedPrefManager(requireActivity());
//        binding.viewpager.setOffscreenPageLimit(0);
//        binding.viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabs));
        userId=sharedPrefManager.getUserId();

        billerAdapter = new FaderalBillerRechargeAdapter(getActivity(), android.R.layout.simple_list_item_1, billerList,this);
        //  Retrieve the arguments (the bundle) from the fragment
        Bundle args = getArguments();
        if (args != null) {
            name = args.getString("name"); // Use the same key as in the first fragment
            phone = args.getString("mobileNo");
            binding.name.setText(name);
            binding.ph.setText(phone);
        }

        // checkIsNewMobileNumber();
        fetchBillerApi();
        //getCustomerParameter();
        binding.updateOperatorEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  AllAllOperatorApi();

                selectAllOperatorBottomSheet();

            }
        });


        //  tenDightNumber= getTenDigitNumber(phone);
        GlobalVariable.RechargeNumber=tenDightNumber;

        binding.reviewLayout.setVisibility(View.GONE);
        // Assuming you have a TabLayout named 'tabLayout'
        binding.tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Show a Toast with the name of the selected tab
                Toast.makeText(getContext(), "Selected Tab: " + tab.getText(), Toast.LENGTH_SHORT).show();
                //  getPlanAPI(billerId,tab.getText().toString());



            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Do nothing for unselected tab
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Optional: Handle reselection if needed
                Toast.makeText(getContext(), "Re-selected Tab: " + tab.getText(), Toast.LENGTH_SHORT).show();
            }
        });


        binding.btnSelectRechargePlanBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobilePrepaidBBPS secondFragment = new MobilePrepaidBBPS();
                // Replace the current fragment with the new one
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.recharge_frameLayout, secondFragment); // Use the container ID where the fragment should be placed
                transaction.addToBackStack(null); // Optional: add to back stack if you want to allow users to navigate back
                transaction.commit();
            }
        });

        //Back Press Handler
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                MobilePrepaidBBPS secondFragment = new MobilePrepaidBBPS();
                // Replace the current fragment with the new one
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.recharge_frameLayout, secondFragment); // Use the container ID where the fragment should be placed
                transaction.addToBackStack(null); // Optional: add to back stack if you want to allow users to navigate back
                transaction.commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

        return binding.getRoot();
    }

    // tag Array is initiating here
    private void getCustomerParameter(String BilerId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        ExeDec eb = new ExeDec(electricityBiller);
        Log.v("gCPRequest", " " + "https://onezed.app/api/get-customer-parameter?billerId=" + BilerId);
        new HttpHandler("https://onezed.app/api/get-customer-parameter?billerId=" + BilerId, getContext())
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
                        Log.v("gCPResponse", code + " " + response);

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getBoolean("success")) {
                                JSONArray dataArray = jsonResponse.getJSONArray("data");
                                JSONArray tagsArray = new JSONArray();

                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject paramObject = dataArray.getJSONObject(i);

                                    JSONObject tagObject = new JSONObject();
                                    tagObject.put("name", paramObject.getString("customParamName"));
                                    if (paramObject.getString("customParamName").equals("Circle")){
                                        tagObject.put("value", circle);
                                    }
                                    if (paramObject.getString("customParamName").equals("Id")){
                                        tagObject.put("value", planId);
                                    }
                                    if (paramObject.getString("customParamName").equals("Mobile Number")){
                                        tagObject.put("value", phone);
                                    }
                                    if (paramObject.getString("customParamName").equals("Customer Mobile Number")){
                                        tagObject.put("value", phone);
                                    }
                                    //tagObject.put("value", ""); // Initially empty, will be updated from another API

                                    tagsArray.put(tagObject);
                                }

                                // Log the created tags array
                                Log.v("tagsArray", tagsArray.toString());
                                GlobalVariable.tags=tagsArray;

                                // You can store this tagsArray and update its values later from another API response.
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("JSONParseError", "Error parsing JSON response "+e);
                        }
                    }

                }).sendRequest();
    }

    private void selectAllOperatorBottomSheet() {
        OperatorBinding = OperatorBottomSheetBinding.inflate(getLayoutInflater());
        View v = OperatorBinding.getRoot();
        sheetDialog = new BottomSheetDialog(getContext());
        sheetDialog.setContentView(v);
        sheetDialog.setCancelable(false);
        sheetDialog.show();

        // Close dialog on cancel button click
        imgCancelDialog = v.findViewById(R.id.cancel_btn);
        imgCancelDialog.setOnClickListener(view -> {
            sheetDialog.dismiss();

        });

        // Set up ListView
        ListView listView = v.findViewById(R.id.AllOperator_lv); // Make sure the ListView ID matches your layout
        listView.setAdapter(billerAdapter);

        // Refresh the adapter to ensure data is displayed
        billerAdapter.notifyDataSetChanged();

    }


    private void fetchBillerApi() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        ExeDec eb=new ExeDec(electricityBiller);
        new HttpHandler("https://onezed.app/api/search-biller-category?search=Mobile Prepaid&&page=0&&pagesize=1000", getContext())
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

                                        GlobalVariable.selectedBillerId=biller_id;
                                        GlobalVariable.selectedBillerType=biller_type;
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

    private void showAlert(String message) {
        CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", message);
        dialog.show();
        dialog.onPositiveButton(view -> dialog.dismiss());
    }

    @Override
    public void onBillerClick(CustomerParameterBiller biller) {
        if (biller != null) {
            Toast.makeText(getContext(), "Selected Operator: " + biller.getBillerName(), Toast.LENGTH_SHORT).show();
            binding.updateOperatorEt.setText(biller.getBillerName());
            GlobalVariable.selectedBillerName=biller.getBillerName();
            GlobalVariable.selectedBillerId=biller.getBillerId();
            GlobalVariable.selectedBillerCategory=biller.getBillerCategory();
            sheetDialog.dismiss();
            // getCusParamByBillerId(biller.getBillerId());
            getPlanCategory(biller.getBillerId());


            billerId= biller.getBillerId();
        }else {
            Toast.makeText(getContext(), "Operator Not Selected : " , Toast.LENGTH_SHORT).show();
        }
    }

    private void getPlanCategory(String billerId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        ExeDec eb = new ExeDec(electricityBiller);
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
                                        CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name), getString(R.string.no_response));
                                        dialog.show();
                                        dialog.onPositiveButton(view ->{
                                            dialog.dismiss();
                                            //startActivity(new Intent(getActivity(), HomeActivity.class));
                                            MobilePrepaidBBPS secondFragment = new MobilePrepaidBBPS();
                                            // Replace the current fragment with the new one
                                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                            transaction.replace(R.id.recharge_frameLayout, secondFragment); // Use the container ID where the fragment should be placed
                                            transaction.addToBackStack(null); // Optional: add to back stack if you want to allow users to navigate back
                                            transaction.commit();
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
                                        planId=planObj.getString("planId");
                                        Log.v("planId",planId);
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

                                getCustomerParameter(billerId);
                                Log.v("billerIdResA"," "+planList);
                                // Call the method to set up the ViewPager and TabLayout
                                adapterCall();
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

        RechargeTabAdapter mDynamicFragmentAdapter = new RechargeTabAdapter(
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


    @Override
    public void onPlanClick(HashMap<String, String> plan) {
                // Toast.makeText(getActivity(), "Rss: " + rs + ", Desc: " + desc + ", Validity: " + validity, Toast.LENGTH_SHORT).show();
        Log.d("PlanClick", "Clicked Plan: " + plan.toString());
    }
}