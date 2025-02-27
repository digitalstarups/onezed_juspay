package com.onezed.Fragment;

import static com.onezed.GlobalVariable.baseUrl.electricBillFetch;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.onezed.Activity.BillPayActivity;
import com.onezed.Activity.HomeActivity;
import com.onezed.Activity.RechargeActivity;
import com.onezed.Adapter.FastagProviderAdapter;
import com.onezed.Adapter.GasProviderAdapter;
import com.onezed.ExeDec;
import com.onezed.GlobalVariable.CustomAlertDialog;
import com.onezed.GlobalVariable.GlobalVariable;
import com.onezed.GlobalVariable.HttpHandler;
import com.onezed.GlobalVariable.SharedPrefManager;
import com.onezed.Model.FastagProviderModel;
import com.onezed.Model.GasProviderModel;
import com.onezed.Model.UserProfileModel;
import com.onezed.R;
import com.onezed.databinding.FragmentFasTagRechargeBinding;
import com.onezed.databinding.FragmentGasCylinderBookBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


public class FasTagRechargeFragment extends Fragment {

    private FragmentFasTagRechargeBinding binding;
    SharedPrefManager sharedPrefManager;
    private List<FastagProviderModel> fastagProviderList = new ArrayList<>();
    private FastagProviderAdapter fastagProviderAdapter;
    String spKey,selectedOperatorUrl,selectedOperatorName,consumerId,message,selectGasProvider,amount;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFasTagRechargeBinding.inflate(inflater, container, false);
        sharedPrefManager = new SharedPrefManager(requireActivity());
        // Call API to fetch biller names and codes

        fetchFastagProviderApi();
        // Set up ListView with an empty adapter initially
        fastagProviderAdapter = new FastagProviderAdapter(getActivity(), fastagProviderList);
        binding.gasProviderListView.setAdapter(fastagProviderAdapter);
        binding.gasProviderListView.setOnItemClickListener((parent, view, position, id) -> {

            // Get the clicked Biller object
            FastagProviderModel clickedBiller = fastagProviderAdapter.getItem(position);
            binding.gasProviderListView.setVisibility(View.GONE);
            selectGasProvider=clickedBiller.getServiceProvider();
            //Toast.makeText(getContext(), "Provider "+clickedBiller.getServiceProvider(), Toast.LENGTH_SHORT).show();

            binding.appBarTxt.setText(clickedBiller.getServiceProvider());
            binding.bbpsImg.setVisibility(View.VISIBLE);

            binding.consumerIdLayout.setVisibility(View.VISIBLE);
            binding.btnSend.setVisibility(View.VISIBLE);


            spKey= clickedBiller.getSpKey();
            // Show a Toast with service provider name and category
            String message = "Service Provider: " + clickedBiller.getServiceProvider() + "\nCategory: " + clickedBiller.getCategory();
            //Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            selectedOperatorName=clickedBiller.getServiceProvider();
            selectedOperatorUrl=clickedBiller.getImage();

        });
//        binding.btnSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                consumerId=binding.etConsumerId.getText().toString();
//                FastagBillFetch();
//
//
//            }
//        });
        binding.etConsumerId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for this case
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed for this case
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString();
                if (!input.equals(input.toUpperCase())) {
                    binding.etConsumerId.removeTextChangedListener(this); // Temporarily remove listener to avoid infinite loop
                    binding.etConsumerId.setText(input.toUpperCase());
                    binding.etConsumerId.setSelection(binding.etConsumerId.getText().length()); // Move cursor to the end
                    binding.etConsumerId.addTextChangedListener(this); // Re-add listener after modification
                }
            }
        });

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String consumerId = binding.etConsumerId.getText().toString();
                if (consumerId.isEmpty()) {
                    binding.etConsumerId.setError("Required LPG ID");
                    binding.tilConsumerId.requestFocus();
                }  else {
                    FastagBillFetch();
                }
            }
        });

        binding.btnProceedToPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  passData("GAS_CYLINDER_BOOK",binding.etConsumerId.getText().toString(),spKey,amount);
                Intent intent = new Intent(getActivity(), BillPayActivity.class);
                intent.putExtra("Type", "FASTAG_RECHARGE");  // 'amount' is the key, 'amount' is the value you want to transfer
                intent.putExtra("amount", amount);  // 'amount' is the key, 'amount' is the value you want to transfer
                intent.putExtra("consumer_id",consumerId );  // 'amount' is the key, 'amount' is the value you want to transfer
                intent.putExtra("spkey", spKey);  // 'amount' is the key, 'amount' is the value you want to transfer
                startActivity(intent);
            }
        });







        return binding.getRoot();
    }
    private void FastagBillFetch() {
        JSONObject joObj = new JSONObject();
        try {
            joObj.put("customerMobileNo", UserProfileModel.getInstance().getMobileNo() );
            joObj.put("accounNo", binding.etConsumerId.getText().toString());
            joObj.put("spkey", spKey);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v("GasBody",joObj.toString());

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
                        Log.v("GasResponse",code+" "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            switch (code) {
                                case 200:

                                   // isDetailsView=true;
                                    String status= jsonObject.getString("status");
                                    if (status=="success" || status.equals("success")){


                                        JSONObject dataObj=jsonObject.getJSONObject("data");
                                        String customerName= dataObj.getString("customername");
                                        GlobalVariable.bbpsCustomername=customerName;
                                        String consumerId= dataObj.getString("account");
                                        String dueDate= dataObj.getString("duedate");
                                        String billDate= dataObj.getString("billdate");
                                        String errorCode=dataObj.getString("errorcode");
                                        message=dataObj.getString("msg");
                                        String dueAmount=dataObj.getString("dueamount");
                                        String billNumber = dataObj.getString("billnumber");
                                        String billPeriod = dataObj.getString("bilperiod");
                                        String billId = String.valueOf(dataObj.getInt("fetchBillID"));

                                        if (errorCode=="200" || errorCode.equals("200")){
                                            binding.consumerIdLayout.setVisibility(View.GONE);
                                            binding.btnSend.setVisibility(View.GONE);
                                            binding.payDetailsLayout.setVisibility(View.VISIBLE);
                                            binding.btnProceedToPay.setVisibility(View.VISIBLE);
                                            Glide.with(getContext())
                                                    .load(selectedOperatorUrl)  // Network image URL
                                                    .placeholder(R.drawable.ic_baseline_account_circle_profile)  // Optional: placeholder while loading
                                                    .error(R.drawable.ic_baseline_account_circle_profile)  // Optional: error image if loading fails
                                                    .into(binding.billerImg);
                                            // binding.electricBillOperator.setText(selectedOperatorName);
                                            binding.billOperator.setText(selectedOperatorName);

                                            binding.nameTxt.setText(customerName);
                                            if (billDate.equals(" ")|| billDate==null || billDate=="" || billDate.isEmpty()){
                                                binding.billDateTxt.setVisibility(View.GONE);
                                                binding.billDate.setVisibility(View.GONE);
                                                Log.v("BillResponse","bill date null");
                                            }else {
                                                binding.billDateTxt.setText(billDate);
                                                Log.v("BillResponse","bill date not null");
                                            }
                                            if (dueDate.equals(" ") || dueDate==null || dueDate=="" || dueDate.isEmpty()){
                                                binding.dueDateTxt.setVisibility(View.GONE);
                                                binding.dueDate.setVisibility(View.GONE);
                                            }else {
                                                binding.dueDateTxt.setText(dueDate);
                                            }
                                            if (billId.equals(" ") || billId==null || billId=="" || billId.isEmpty()){
                                                binding.billIdTxt.setVisibility(View.GONE);
                                                binding.billId.setVisibility(View.GONE);
                                            }else {
                                                binding.billIdTxt.setText(billId);
                                            }
                                            if (billPeriod.equals(" ") || billPeriod==null || billPeriod=="" || billPeriod.isEmpty()){
                                                binding.billPeriodTxt.setVisibility(View.GONE);
                                                binding.billPeriod.setVisibility(View.GONE);
                                            }else {
                                                binding.billPeriodTxt.setText(billPeriod);
                                            }
                                            if (billNumber.equals(" ") || billNumber==null || billNumber=="" || billNumber.isEmpty()){
                                                binding.billNumberTxt.setVisibility(View.GONE);
                                                binding.billNumber.setVisibility(View.GONE);
                                            }else {
                                                binding.billNumberTxt.setText(billNumber);
                                            }




                                            binding.customerId.setText(consumerId);
                                            binding.dueAmount.setText("\u20B9  "+dueAmount);

                                            amount=dueAmount;


//                                            if (dataObj.has("billAmountOptions") && !dataObj.isNull("billAmountOptions")) {
//                                                JSONArray billAmountOptions=dataObj.getJSONArray("billAmountOptions");
//
//                                                for (int i = 0; i < billAmountOptions.length(); i++) {
//                                                    JSONObject obj = billAmountOptions.getJSONObject(i);
//
//
//                                                    String amountName = obj.getString("amountName");
//                                                    int amountValue = obj.getInt("amountValue");
//
//
//                                                    BillAmountOptionsModel model = new BillAmountOptionsModel();
//                                                    model.setAmountName(amountName);
//                                                    model.setAmountValue(String.valueOf(amountValue));
//
//
//
//                                                }
//                                              //  BillAmountOptionsAdapter adapter = new BillAmountOptionsAdapter(getContext(), billAmountOptionsList, GasCylinderBookFragment.this);
//                                              //  binding.elctricBillerDueListView.setAdapter(adapter);
//                                            }

//                                            else {
//                                                // binding.billCycle.setVisibility(View.GONE);
//                                                binding.elctricBillerDueListView.setVisibility(View.GONE);
//                                                binding.amountLayout.setVisibility(View.VISIBLE);
//                                                binding.billCycle.setText("Bill Amount");
//                                                binding.dueAmount.setText("\u20B9  "+dueAmount);
//                                                payAmount=dueAmount;
//
//
//
//
//                                            }
                                            break;
                                        } else if (errorCode=="412" || errorCode.equals("412")) {

                                            //String message=dataObj.getString("msg");
                                            CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", message);
                                            dialog.show();
                                            dialog.onPositiveButton(view -> {
                                                //startActivity(new Intent(getActivity(), HomeActivity.class));
                                                dialog.dismiss();
                                                Intent inten = new Intent(getActivity(), RechargeActivity.class);
                                                inten.putExtra("FRAGMENT_TYPE", "FASTAG_RECHARGE");
                                                startActivity(inten);
                                            } );
                                        }
                                        else if (errorCode=="416" || errorCode.equals("416")) {

                                            //String message=dataObj.getString("msg");
                                            CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", message);
                                            dialog.show();
                                            dialog.onPositiveButton(view -> {
                                                //startActivity(new Intent(getActivity(), HomeActivity.class));
                                                dialog.dismiss();
                                                Intent inten = new Intent(getActivity(), RechargeActivity.class);
                                                inten.putExtra("FRAGMENT_TYPE", "FASTAG_RECHARGE");
                                                startActivity(inten);
                                            } );
                                        }
                                        else {

                                            CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", message);
                                            dialog.show();
                                            dialog.onPositiveButton(view -> {
                                                //startActivity(new Intent(getActivity(), HomeActivity.class));
                                                dialog.dismiss();
                                                Intent inten = new Intent(getActivity(), RechargeActivity.class);
                                                inten.putExtra("FRAGMENT_TYPE", "FASTAG_RECHARGE");
                                                startActivity(inten);
                                            } );
                                        }


                                    }
                                    else {
                                        //handle if status not success
                                        // String message=dataObj.getString("msg");
                                        CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", "Not getting biller Details");
                                        dialog.show();
                                        dialog.onPositiveButton(view -> {
                                            startActivity(new Intent(getActivity(), HomeActivity.class));
                                            dialog.dismiss();
                                        } );
                                        break;
                                    }
                                    //String
                                    break;
                                default:
                                    CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", "Something Wrong, Please Try after some times");
                                    dialog.show();
                                    dialog.onPositiveButton(view ->{
                                        dialog.dismiss();
                                        Intent inten = new Intent(getActivity(), RechargeActivity.class);
                                        inten.putExtra("FRAGMENT_TYPE", "FASTAG_RECHARGE");
                                        startActivity(inten);
                                    } );
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }).sendRequest();
    }

    private void fetchFastagProviderApi() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        //  ExeDec eb=new ExeDec(electricityBiller);
        new HttpHandler("https://onezed.app/api/searchProvider?search=fastag", getContext())
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
                        Log.v("FastagResponse",code+" "+response);
                       // binding.appBarTxt.setText("Select your provider");
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            switch (code) {
                                case 200:
                                    JSONArray billerArray = jsonObject.getJSONArray("fastag");
                                    fastagProviderList.clear();  // Clear the list before adding new data
                                    for (int i = 0; i < billerArray.length(); i++) {
                                        JSONObject obj = billerArray.getJSONObject(i);
                                        // Add the name and code to the list
                                        String service_provider = obj.getString("service_provider");
                                       // if (service_provider != null && service_provider.contains("fastag")) {
                                            // Show a Toast message
                                            // Toast.makeText(getContext(), "Gas service provider found", Toast.LENGTH_SHORT).show();
                                            int id = obj.getInt("id");
                                            int spKey = obj.getInt("sp_key");
                                            String category = obj.getString("category");
                                            String image= obj.getString("image");

                                            FastagProviderModel biller = new FastagProviderModel();
                                            biller.setbId(String.valueOf(id));
                                            biller.setServiceProvider(service_provider);
                                            biller.setSpKey(String.valueOf(spKey));
                                            biller.setCategory(category);
                                            biller.setImage(image);
                                            fastagProviderList.add(biller);
                                       // }

                                    }
                                    // Notify adapter with updated data
                                    getActivity().runOnUiThread(() -> fastagProviderAdapter.notifyDataSetChanged());
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
}