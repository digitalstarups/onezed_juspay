package com.onezed.Fragment;

import static com.onezed.GlobalVariable.baseUrl.electricBillFetch;
import static com.onezed.GlobalVariable.baseUrl.electricBillSearch;
import static com.onezed.GlobalVariable.baseUrl.electricityBiller;

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

import com.bumptech.glide.Glide;
import com.onezed.Activity.BillPayActivity;
import com.onezed.Activity.HomeActivity;
import com.onezed.Activity.RechargeActivity;
import com.onezed.Adapter.BillAmountOptionsAdapter;
import com.onezed.Adapter.BillerAdapter;
import com.onezed.ExeDec;
import com.onezed.GlobalVariable.CustomAlertDialog;
import com.onezed.GlobalVariable.GlobalVariable;
import com.onezed.GlobalVariable.HttpHandler;
import com.onezed.GlobalVariable.SharedPrefManager;
import com.onezed.Model.BillAmountOptionsModel;
import com.onezed.Model.Biller;
import com.onezed.Model.UserProfileModel;
import com.onezed.R;
import com.onezed.databinding.FragmentFetchBillerBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


public class FetchBillerFragment extends Fragment implements BillAmountOptionsAdapter.FragmentCommunication{

    private FragmentFetchBillerBinding binding;
    private List<Biller> billerList = new ArrayList<>();
    private BillerAdapter billerAdapter;
    String payAmount,spKey,selectedOperatorUrl,selectedOperatorName,value,consumerId,message="Something went wrong. Please try again.";
    SharedPrefManager sharedPrefManager;
    List<BillAmountOptionsModel> billAmountOptionsList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFetchBillerBinding.inflate(inflater, container, false);
        sharedPrefManager = new SharedPrefManager(requireActivity());
        // Set up ListView with an empty adapter initially
        billerAdapter = new BillerAdapter(getActivity(), billerList);
        binding.elctricBillerListView.setAdapter(billerAdapter);
        if (getArguments() != null) {
            value = getArguments().getString("PAGE");
        }

        // Call API to fetch biller names and codes

        fetchBillerApi();

        binding.btnProccedToPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(getActivity(), BillPayActivity.class);
                intent.putExtra("Type", "ELECTRICITY_BIL_PAY");  // 'amount' is the key, 'amount' is the value you want to transfer
                intent.putExtra("amount", payAmount);  // 'amount' is the key, 'amount' is the value you want to transfer
                intent.putExtra("consumer_id",consumerId );  // 'amount' is the key, 'amount' is the value you want to transfer
                intent.putExtra("spkey", spKey);  // 'amount' is the key, 'amount' is the value you want to transfer
                startActivity(intent);

            }
        });


        // Search functionality
        binding.txtElectricBiller.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                searchApiCall(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        binding.elctricBillerListView.setOnItemClickListener((parent, view, position, id) -> {
            // Get the clicked Biller object
            Biller clickedBiller = billerAdapter.getItem(position);
            binding.elctricBillerListView.setVisibility(View.GONE);
            binding.txtElectricBiller.setVisibility(View.GONE);
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

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                consumerId=binding.etConsumerId.getText().toString();
                electricBillFetch();


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


        return binding.getRoot();
    }




    private void electricBillFetch() {
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
                        Log.v("ElectricBillResponse",code+" "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            switch (code) {
                                case 200:


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

                                        if (errorCode=="200" || errorCode.equals("200")){
                                            binding.consumerIdLayout.setVisibility(View.GONE);
                                            binding.btnSend.setVisibility(View.GONE);
                                            binding.payDetailsLayout.setVisibility(View.VISIBLE);
                                            binding.btnProccedToPay.setVisibility(View.VISIBLE);
                                            Glide.with(getContext())
                                                    .load(selectedOperatorUrl)  // Network image URL
                                                    .placeholder(R.drawable.ic_baseline_account_circle_profile)  // Optional: placeholder while loading
                                                    .error(R.drawable.ic_baseline_account_circle_profile)  // Optional: error image if loading fails
                                                    .into(binding.billerImg);
                                            binding.electricBillOperator.setText(selectedOperatorName);

                                            binding.nameTxt.setText(customerName);
                                            binding.billDateTxt.setText(billDate);
                                            binding.dueDateTxt.setText(dueDate);
                                            binding.customerId.setText(consumerId);



                                            if (dataObj.has("billAmountOptions") && !dataObj.isNull("billAmountOptions")) {
                                                JSONArray billAmountOptions=dataObj.getJSONArray("billAmountOptions");

                                                for (int i = 0; i < billAmountOptions.length(); i++) {
                                                    JSONObject obj = billAmountOptions.getJSONObject(i);


                                                    String amountName = obj.getString("amountName");
                                                    int amountValue = obj.getInt("amountValue");


                                                    BillAmountOptionsModel model = new BillAmountOptionsModel();
                                                    model.setAmountName(amountName);
                                                    model.setAmountValue(String.valueOf(amountValue));

                                                    billAmountOptionsList.add(model);

                                                }
                                                BillAmountOptionsAdapter adapter = new BillAmountOptionsAdapter(getContext(), billAmountOptionsList, FetchBillerFragment.this);
                                                binding.elctricBillerDueListView.setAdapter(adapter);
                                            }else {
                                                // binding.billCycle.setVisibility(View.GONE);
                                                binding.elctricBillerDueListView.setVisibility(View.GONE);
                                                binding.amountLayout.setVisibility(View.VISIBLE);
                                                binding.billCycle.setText("Bill Amount");
                                                binding.dueAmount.setText("\u20B9  "+dueAmount);
                                                payAmount=dueAmount;




                                            }
                                            break;
                                        } else if (errorCode=="412" || errorCode.equals("412")) {

                                            //String message=dataObj.getString("msg");
                                            CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", message);
                                            dialog.show();
                                            dialog.onPositiveButton(view -> {
                                                //startActivity(new Intent(getActivity(), HomeActivity.class));
                                                dialog.dismiss();
                                                Intent inten = new Intent(getActivity(), RechargeActivity.class);
                                                inten.putExtra("FRAGMENT_TYPE", "ELECTRICITY_BIL_PAY");
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
                                                inten.putExtra("FRAGMENT_TYPE", "ELECTRICITY_BIL_PAY");
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
                                                inten.putExtra("FRAGMENT_TYPE", "ELECTRICITY_BIL_PAY");
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
                                        inten.putExtra("FRAGMENT_TYPE", "ELECTRICITY_BIL_PAY");
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

    private void searchApiCall(String search){
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        ExeDec ebs=new ExeDec(electricBillSearch);
        String url = String.format(ebs.getDec()+"%s",
                Uri.encode(search));


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
                                    JSONArray billerArray = jsonObject.getJSONArray("data");
                                    billerList.clear();  // Clear the list before adding new data
                                    for (int i = 0; i < billerArray.length(); i++) {
                                        JSONObject obj = billerArray.getJSONObject(i);
                                        // Add the name and code to the list
                                        String service_provider = obj.getString("service_provider");
                                        int id = obj.getInt("id");
                                        int spKey = obj.getInt("sp_key");
                                        String category = obj.getString("category");
                                        String image= obj.getString("image");
                                        Biller biller = new Biller();
                                        biller.setbId(String.valueOf(id));
                                        biller.setServiceProvider(service_provider);
                                        biller.setSpKey(String.valueOf(spKey));
                                        biller.setCategory(category);
                                        biller.setImage(image);
                                        billerList.add(biller);
                                    }
                                    // Notify adapter with updated data
                                    getActivity().runOnUiThread(() -> billerAdapter.notifyDataSetChanged());
                                    break;
                                default:
                                    // showAlert("Something went wrong. Please try again.");
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
        ExeDec eb=new ExeDec(electricityBiller);
        new HttpHandler(eb.getDec(), getContext())
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
                            switch (code) {
                                case 200:
                                    JSONArray billerArray = jsonObject.getJSONArray("data");
                                    billerList.clear();  // Clear the list before adding new data
                                    for (int i = 0; i < billerArray.length(); i++) {
                                        JSONObject obj = billerArray.getJSONObject(i);
                                        // Add the name and code to the list
                                        String service_provider = obj.getString("service_provider");
                                        int id = obj.getInt("id");
                                        int spKey = obj.getInt("sp_key");
                                        String category = obj.getString("category");
                                        String image= obj.getString("image");

                                        Biller biller = new Biller();
                                        biller.setbId(String.valueOf(id));
                                        biller.setServiceProvider(service_provider);
                                        biller.setSpKey(String.valueOf(spKey));
                                        biller.setCategory(category);
                                        biller.setImage(image);
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
    public void onAmountSelected(String selectedAmount) {
        payAmount=selectedAmount;


    }
}
