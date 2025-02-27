package com.onezed.Fragment;

import static com.onezed.GlobalVariable.baseUrl.electricBillFetch;
import static com.onezed.GlobalVariable.baseUrl.getBroadbandBiller;
import static com.onezed.GlobalVariable.baseUrl.getBroadbandBillerSearch;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.onezed.Activity.HomeActivity;
import com.onezed.Adapter.BroadbandBillerAdapter;
import com.onezed.ExeDec;
import com.onezed.GlobalVariable.CustomAlertDialog;
import com.onezed.GlobalVariable.GlobalVariable;
import com.onezed.GlobalVariable.HttpHandler;
import com.onezed.GlobalVariable.SharedPrefManager;
import com.onezed.Model.Biller;
import com.onezed.Model.UserProfileModel;
import com.onezed.R;
import com.onezed.databinding.FragmentBroadbandRechargeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


public class BroadbandRechargeFragment extends Fragment {

    private FragmentBroadbandRechargeBinding binding;
    private List<Biller> billerList = new ArrayList<>();
    private BroadbandBillerAdapter billerAdapter;
    SharedPrefManager sharedPrefManager;
    int totalPage;
    String spKey,selectedOperatorName,amount,value;
    String message="Not getting Biller Details ! Try after some times";

    // Define an interface to send data to RechargeActivity
    public interface OnDataPass {
        void onDataPass(String type,String consumerId,String spKey,String amount);
    }

    private OnDataPass dataPasser;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            dataPasser = (OnDataPass) context;  // Cast RechargeActivity to OnDataPass
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnDataPass");
        }
    }

    // Call this method to pass data to RechargeActivity
    public void passData(String type,String consumerId, String spKey, String amount) {
        dataPasser.onDataPass(type,consumerId,spKey,amount);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBroadbandRechargeBinding.inflate(inflater, container, false);
        sharedPrefManager = new SharedPrefManager(requireActivity());
        billerAdapter = new BroadbandBillerAdapter(getActivity(), billerList);
        binding.broadbandListView.setAdapter(billerAdapter);
        if (getArguments() != null) {
            value = getArguments().getString("PAGE");
        }
        // Start by fetching the first page of billers
        fetchBillerApi();

        // Search functionality
        binding.txtBroadband.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                searchApiCall(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        binding.broadbandListView.setOnItemClickListener((parent, view, position, id) -> {
            // Get the clicked Biller object
            Biller clickedBiller = billerAdapter.getItem(position);

            binding.broadbandListView.setVisibility(View.GONE);
            binding.txtBroadband.setVisibility(View.GONE);
            binding.customerIdLayout.setVisibility(View.VISIBLE);
            binding.btnSend.setVisibility(View.VISIBLE);
            binding.bbpsImg.setVisibility(View.VISIBLE);


            String message = "Service Provider: " + clickedBiller.getServiceProvider() + "\nCategory: " + clickedBiller.getCategory();
               // Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            spKey=clickedBiller.getSpKey();
            selectedOperatorName=clickedBiller.getServiceProvider();
        });

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(getActivity(), ElectricBillPayActivity.class));
                BillFetch();
            }
        });

        binding.btnProccedToPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(getActivity(), ElectricBillPayActivity.class));
                passData("BROADBAND_RECHARGE",binding.etConsumerId.getText().toString(),spKey,amount);

            }
        });



        // Back press handle
       // binding.btnBackImg.setOnClickListener(view -> startActivity(new Intent(getActivity(), HomeActivity.class)));
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
               // startActivity(new Intent(getActivity(), HomeActivity.class));
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

        return binding.getRoot();
    }

    private void BillFetch() {
        JSONObject joObj = new JSONObject();
        try {
            joObj.put("customerMobileNo", UserProfileModel.getInstance().getMobileNo() );
            joObj.put("accounNo", binding.etConsumerId.getText().toString());
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
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            switch (code) {
                                case 200:
                                    if (jsonObject.has("status")) {
                                        String status = jsonObject.getString("status");

                                        if (status == "success" || status.equals("success")) {

                                            if (jsonObject.has("data")){

                                                JSONObject dataObj = jsonObject.getJSONObject("data");

                                            String customerName = dataObj.getString("customername");
                                                GlobalVariable.bbpsCustomername=customerName;
                                            String consumerId = dataObj.getString("account");
                                            String dueDate = dataObj.getString("duedate");
                                            String billDate = dataObj.getString("billdate");
                                            String errorCode = dataObj.getString("errorcode");
                                            String billNumber = dataObj.getString("billnumber");
                                            String billPeriod = dataObj.getString("bilperiod");
                                            String billId = String.valueOf(dataObj.getInt("fetchBillID"));
                                            String dueAmount=String.valueOf(dataObj.getInt("dueamount"));

                                            if (errorCode == "200" || errorCode.equals("200")) {
                                                binding.customerIdLayout.setVisibility(View.GONE);
                                                binding.btnSend.setVisibility(View.GONE);

                                                binding.payDetailsLayout.setVisibility(View.VISIBLE);
                                                binding.btnProccedToPay.setVisibility(View.VISIBLE);
//                                                Glide.with(getContext())
//                                                        .load(selectedOperatorUrl)  // Network image URL
//                                                        .placeholder(R.drawable.ic_baseline_account_circle_profile)  // Optional: placeholder while loading
//                                                        .error(R.drawable.ic_baseline_account_circle_profile)  // Optional: error image if loading fails
//                                                        .into(binding.billerImg);

                                                binding.billOperator.setText(selectedOperatorName);
                                                binding.customerId.setText(consumerId);
                                                binding.nameTxt.setText(customerName);
                                                binding.billNumberTxt.setText(billNumber);
                                                binding.billIdTxt.setText(billId);
                                                binding.billDateTxt.setText(billDate);
                                                binding.dueDateTxt.setText(dueDate);
                                                binding.billPeriodTxt.setText(billPeriod);

                                                binding.dueAmount.setText("\u20B9  "+dueAmount);

                                                amount=dueAmount;

                                                break;
                                            } else if (errorCode == "412" || errorCode.equals("412")) {

                                                if (dataObj.has("msg")){
                                                     message = dataObj.getString("msg");
                                                }

                                                CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", message);
                                                dialog.show();
                                                dialog.onPositiveButton(view -> {
                                                    dialog.dismiss();
                                                });
                                            } else if (errorCode == "416" || errorCode.equals("416")) {

                                                if (dataObj.has("msg")){
                                                    message = dataObj.getString("msg");
                                                }
                                                CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", message);
                                                dialog.show();
                                                dialog.onPositiveButton(view -> {
                                                    dialog.dismiss();
                                                });
                                            } else {

                                                CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", message);
                                                dialog.show();
                                                dialog.onPositiveButton(view -> {
                                                    startActivity(new Intent(getActivity(), HomeActivity.class));
                                                    dialog.dismiss();
                                                });
                                            }


                                        }
                                    }
                                    else {
                                        break;
                                    }
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

    private void searchApiCall(String search) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        ExeDec ebs=new ExeDec(getBroadbandBillerSearch);
        String url = String.format(ebs.getDec()+"%s",
                Uri.encode(search));


        // Clear the list before starting a new search
        getActivity().runOnUiThread(() -> {
            billerList.clear();
            billerAdapter.notifyDataSetChanged();
        });

        new HttpHandler(url, getContext())
                .setMediaType(HttpHandler.jsonMediaType)
                .setRequestType(HttpHandler.RequestType.GET)
                .setLoading(false)
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
                                    if (jsonObject.getBoolean("status")) {
                                        JSONArray billerArray = jsonObject.getJSONArray("data");

                                        if (billerArray.length() > 0) {
                                            // Clear the list and add new data
                                            billerList.clear();
                                            for (int i = 0; i < billerArray.length(); i++) {
                                                JSONObject obj = billerArray.getJSONObject(i);

                                                // Extract data
                                                String service_provider = obj.getString("service_provider");
                                                int id = obj.getInt("id");
                                                int spKey = obj.getInt("sp_key");
                                                String category = obj.getString("category");
                                                String image = obj.getString("image");

                                                // Create and add new Biller object
                                                Biller biller = new Biller();
                                                biller.setbId(String.valueOf(id));
                                                biller.setServiceProvider(service_provider);
                                                biller.setSpKey(String.valueOf(spKey));
                                                biller.setCategory(category);
                                                biller.setImage(image);

                                                billerList.add(biller);
                                            }

                                            // Log the size of the billerList to verify if data is being added

                                            // Notify adapter on UI thread
                                            getActivity().runOnUiThread(() -> {
                                                billerAdapter.notifyDataSetChanged();
                                            });
                                        } else {
                                            // No results, clear list
                                            getActivity().runOnUiThread(() -> {
                                                billerList.clear();
                                                billerAdapter.notifyDataSetChanged();
                                            });
                                        }
                                    } else {
                                        // Status false, clear list
                                        getActivity().runOnUiThread(() -> {
                                            billerList.clear();
                                            billerAdapter.notifyDataSetChanged();
                                        });
                                    }
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

    private void fetchBillerApi() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        ExeDec gbb=new ExeDec(getBroadbandBiller);
        new HttpHandler(gbb.getDec(), getContext())
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
                            if (code == 200 && jsonObject.getBoolean("status")) {
                                //JSONObject dataObj = jsonObject.getJSONObject("data");
                                JSONArray billerArray = jsonObject.getJSONArray("data");
                                billerList.clear();  // Clear the list before adding new data

                                // Add all billers in one go
                                addBillersToList(billerArray);
                            } else {
                                showAlert("Something went wrong. Please try again.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).sendRequest();
    }

    // Helper method to add billers to the list
    private void addBillersToList(JSONArray billerArray) throws JSONException {
        for (int i = 0; i < billerArray.length(); i++) {
            JSONObject obj = billerArray.getJSONObject(i);

            // Add the name and code to the list
            String service_provider = obj.getString("service_provider");
            int id = obj.getInt("id");
            int spKey = obj.getInt("sp_key");
            String category = obj.getString("category");
            String image = obj.getString("image");

            Biller biller = new Biller();
            biller.setbId(String.valueOf(id));
            biller.setServiceProvider(service_provider);
            biller.setSpKey(String.valueOf(spKey));
            biller.setCategory(category);
            biller.setImage(image);
            billerList.add(biller);
        }

        // Notify the adapter of updated data on the main thread
        getActivity().runOnUiThread(() -> billerAdapter.notifyDataSetChanged());
    }

    private void showAlert(String message) {
        CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", message);
        dialog.show();
        dialog.onPositiveButton(view -> dialog.dismiss());
    }

}