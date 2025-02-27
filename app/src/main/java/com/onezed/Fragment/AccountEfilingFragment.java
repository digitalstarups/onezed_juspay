package com.onezed.Fragment;

import static com.onezed.GlobalVariable.baseUrl.getAccountingEfiling;
import static com.onezed.GlobalVariable.baseUrl.privacyPolicy;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.onezed.Activity.HomeActivity;
import com.onezed.Adapter.ComRegViewAdapter;
import com.onezed.ExeDec;
import com.onezed.GlobalVariable.CustomAlertDialog;
import com.onezed.GlobalVariable.GlobalVariable;
import com.onezed.GlobalVariable.HttpHandler;
import com.onezed.R;
import com.onezed.databinding.FragmentAccountEfilingBinding;
import com.onezed.databinding.FragmentBrandProtectionBinding;

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


public class AccountEfilingFragment extends Fragment {

    private FragmentAccountEfilingBinding binding;
    private ArrayAdapter<String> spinnerAdapter;
    private ArrayList<String> spinnerNames = new ArrayList<>();
    private ComRegViewAdapter itemAdapter;
    private List<String> itemList;
    private RecyclerView recyclerView;
    private HashMap<String, String> whatWeProvideMap = new HashMap<>(); // HashMap to store what_we_provide values
    private HashMap<String, String> documentRequiredMap = new HashMap<>();
    String payAmount;
    private final Map<String, String> priceMap = new HashMap<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentAccountEfilingBinding.inflate(inflater, container, false);
        GlobalVariable.inhouseComRegSelectedType = "Income Tax";
        AccountingEfilingCall();
        // Setup Spinner
        spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, spinnerNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.accountEfilingSpinner.setAdapter(spinnerAdapter);

        // Setup Recycler View
        recyclerView = binding.recyclerView; // Assuming binding is already set
        itemList = new ArrayList<>();
        itemAdapter = new ComRegViewAdapter(getContext(), itemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(itemAdapter);


        // Set default checked state for radio buttons
        binding.incomeTaxRb.setChecked(true);
        // Set default background for tabs
        binding.provideTabLayout.setBackgroundResource(R.drawable.transparent_border);
        binding.documentTabLayout.setBackgroundResource(0);



        // Tab click listeners
        binding.provideTabLayout.setOnClickListener(view -> {
            binding.provideTabLayout.setBackgroundResource(R.drawable.transparent_border);
            binding.documentTabLayout.setBackgroundResource(0);

            // Update RecyclerView with "What We Provide" data
            updateItemList(whatWeProvideMap);
        });

        binding.documentTabLayout.setOnClickListener(view -> {
            binding.provideTabLayout.setBackgroundResource(0);
            binding.documentTabLayout.setBackgroundResource(R.drawable.transparent_border);

            // Update RecyclerView with "Document Required" data
            updateItemList(documentRequiredMap);
        });

        // Set OnItemSelectedListener for the spinner
        binding.accountEfilingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedName = spinnerNames.get(position);
                GlobalVariable.inhouseComRegSelectedCategory = selectedName;

                // Load the corresponding "What We Provide" on selection
                updateItemList(whatWeProvideMap); // Show the default "What We Provide" on selection

                if (GlobalVariable.inhouseComRegSelectedCategory.equals(selectedName)){
                    binding.price.setText((priceMap.get(selectedName)));
                    payAmount = priceMap.get(selectedName);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        // RadioGroup listener
        binding.radioGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            spinnerNames.clear();
            AccountingEfilingCall(); // Re-fetch the data and update the spinner
            binding.provideTabLayout.setBackgroundResource(R.drawable.transparent_border);
            binding.documentTabLayout.setBackgroundResource(0);
            // Ensure the spinner is reset to the first item when the radio button changes
            //   binding.firmSpinner.setSelection(0);

            if (checkedId == R.id.income_tax_rb) {
                GlobalVariable.inhouseComRegSelectedType = "Income Tax";
            } else if (checkedId == R.id.goods_services_tax_rb) {
                GlobalVariable.inhouseComRegSelectedType = "Goods & Service Tax";
            }else if (checkedId == R.id.roc_compliances_rb) {
                GlobalVariable.inhouseComRegSelectedType = "ROC Compliances";
            }else if (checkedId == R.id.free_consultancy_rb) {
                GlobalVariable.inhouseComRegSelectedType = "Free Consultancy";
            }else if (checkedId == R.id.free_consultancy_three_day_rb) {
                GlobalVariable.inhouseComRegSelectedType = "Free Consultancy,\nDelivery in 3 Days";
            }else if (checkedId == R.id.tax_deducted_source_rb) {
                GlobalVariable.inhouseComRegSelectedType = "Tax Deducted at Source";
            }else if (checkedId == R.id.professional_tax_rb) {
                GlobalVariable.inhouseComRegSelectedType = "Professional Tax";
            }else if (checkedId == R.id.labour_department_filing_rb) {
                GlobalVariable.inhouseComRegSelectedType = "Labour Department Filing";
            }else if (checkedId == R.id.other_rb) {
                GlobalVariable.inhouseComRegSelectedType = "Other";
            }

            // Reload the data for the new selection and update RecyclerView
            AccountingEfilingCall();
        });
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("FRAGMENT_TYPE", "ACCOUNTING_EFINING");

                Fragment receivingFragment = new BookAppointmentFragment();
                receivingFragment.setArguments(bundle);

                // Begin the transaction and replace the fragment
                getFragmentManager().beginTransaction()
                        .replace(R.id.inhouse_frameLayout, receivingFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        binding.btnDocUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ensure itemList is updated with "Document Required" data
                updateItemList(documentRequiredMap);

                Log.v("RequiredDoc", itemList.toString() + "\n" + itemList.toArray(new String[0]));

                Bundle bundle = new Bundle();
                bundle.putString("pagename", "Accounting & E-filing");
                if (!payAmount.isEmpty()){
                    bundle.putString("payAmount",payAmount);
                }
                // Convert List<String> to ArrayList<String> before passing it
                bundle.putStringArrayList("customParameter", new ArrayList<>(itemList));

                DocumentUploadFragment secondFragment = new DocumentUploadFragment();
                secondFragment.setArguments(bundle);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.inhouse_frameLayout, secondFragment);
                transaction.addToBackStack(null);
                transaction.commit();

                //   GlobalVariable.inhouseComRegSelectedType="Reg. (Central Govt.)";
            }
        });

//        binding.btnDocUpload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Ensure itemList is updated with "Document Required" data
//                updateItemList(documentRequiredMap);
//
//                Log.v("RequiredDoc", itemList.toString() + "\n" + itemList.toArray(new String[0]));
//
//                Bundle bundle = new Bundle();
//                bundle.putString("pagename", "Accounting & E-filing");
//
//                // Convert List<String> to ArrayList<String> before passing it
//                bundle.putStringArrayList("customParameter", new ArrayList<>(itemList));
//
//                DocumentUploadFragment secondFragment = new DocumentUploadFragment();
//                secondFragment.setArguments(bundle);
//
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                transaction.replace(R.id.inhouse_frameLayout, secondFragment);
//                transaction.addToBackStack(null);
//                transaction.commit();
//            }
//        });


        // Back press handle
        binding.btnBackImg.setOnClickListener(view -> startActivity(new Intent(getActivity(), HomeActivity.class)));
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                startActivity(new Intent(getActivity(), HomeActivity.class));
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);
        return  binding.getRoot();
    }

    // Helper method to update item list based on selected spinner item
    private void updateItemList(HashMap<String, String> map) {
        int selectedPosition = binding.accountEfilingSpinner.getSelectedItemPosition();
        if (selectedPosition != AdapterView.INVALID_POSITION) {
            String selectedName = spinnerNames.get(selectedPosition);
            String data = map.get(selectedName);
            GlobalVariable.inhouseCategory=selectedName;
            if (data != null) {
                // Split the string by \n and update the RecyclerView
                itemList.clear(); // Clear the previous items
                itemList.addAll(Arrays.asList(data.split("\n")));
                itemAdapter.notifyDataSetChanged();

                // Show RecyclerView
                recyclerView.setVisibility(View.VISIBLE);
            } else {
               // binding.accountEfilingSpinner.setVisibility(View.GONE);
            }
        } else {

        }
    }

    // Method to fetch the data and update the spinner and map
    private void AccountingEfilingCall() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        ExeDec gae=new ExeDec(getAccountingEfiling);
        new HttpHandler(gae.getDec(), getContext())
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
                        Log.v("AccountResponse",code+" "+response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.has("status") && jsonObject.getBoolean("status")) {
                                JSONObject dataObj = jsonObject.getJSONObject("data");

                                spinnerNames.clear(); // Clear the spinner list
                                whatWeProvideMap.clear(); // Clear the map

                                // Depending on the selected type, parse the corresponding JSON array
                                if (GlobalVariable.inhouseComRegSelectedType.equals("Income Tax") && dataObj.has("Income Tax")) {
                                    populateSpinnerAndMap(dataObj.getJSONArray("Income Tax"));
                                } else if (GlobalVariable.inhouseComRegSelectedType.equals("Goods & Service Tax") && dataObj.has("Goods & Service Tax")) {
                                    populateSpinnerAndMap(dataObj.getJSONArray("Goods & Service Tax"));
                                } else if (GlobalVariable.inhouseComRegSelectedType.equals("ROC Compliances") && dataObj.has("ROC Compliances")) {
                                    populateSpinnerAndMap(dataObj.getJSONArray("ROC Compliances"));
                                } else if (GlobalVariable.inhouseComRegSelectedType.equals("Free Consultancy") && dataObj.has("Free Consultancy")) {
                                    populateSpinnerAndMap(dataObj.getJSONArray("Free Consultancy"));
                                } else if (GlobalVariable.inhouseComRegSelectedType.equals("Free Consultancy,\nDelivery in 3 Days") && dataObj.has("Free Consultancy,\nDelivery in 3 Days")) {
                                    populateSpinnerAndMap(dataObj.getJSONArray("Free Consultancy,\nDelivery in 3 Days"));
                                }else if (GlobalVariable.inhouseComRegSelectedType.equals("Tax Deducted at Source") && dataObj.has("Tax Deducted at Source")) {
                                    populateSpinnerAndMap(dataObj.getJSONArray("Tax Deducted at Source"));
                                }else if (GlobalVariable.inhouseComRegSelectedType.equals("Professional Tax") && dataObj.has("Professional Tax")) {
                                    populateSpinnerAndMap(dataObj.getJSONArray("Professional Tax"));
                                }else if (GlobalVariable.inhouseComRegSelectedType.equals("Labour Department Filing") && dataObj.has("Labour Department Filing")) {
                                    populateSpinnerAndMap(dataObj.getJSONArray("Labour Department Filing"));
                                }else if (GlobalVariable.inhouseComRegSelectedType.equals("Other") && dataObj.has("Other")) {
                                    populateSpinnerAndMap(dataObj.getJSONArray("Other"));
                                }else {
                                    CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name), "Data Not Found");
                                    dialog.show();
                                    dialog.onPositiveButton(view ->{
                                        dialog.dismiss();
                                        startActivity(new Intent(getActivity(), HomeActivity.class));
                                    } );
                                }

                                // Update the spinner adapter on the main thread
                                getActivity().runOnUiThread(() -> {
                                    spinnerAdapter.notifyDataSetChanged();

                                    // Check if the spinner has items and select the first item
                                    if (!spinnerNames.isEmpty()) {
                                        binding.accountEfilingSpinner.setSelection(0); // Select the first item
                                        String firstItem = spinnerNames.get(0);
                                        String whatWeProvide = whatWeProvideMap.get(firstItem);
                                        // Update RecyclerView with the first item's what_we_provide
                                        updateItemList(whatWeProvideMap);
                                    }
                                });
                            } else {
                                CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name), "Status Not Found");
                                dialog.show();
                                dialog.onPositiveButton(view ->{
                                    dialog.dismiss();
                                    startActivity(new Intent(getActivity(), HomeActivity.class));
                                } );
                            }

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }).sendRequest();
    }
    private void populateSpinnerAndMap(JSONArray jsonArray) throws JSONException {
        if (jsonArray.length() == 0) {
            binding.accountEfilingSpinner.setVisibility(View.GONE);
            spinnerNames.add("We will be available soon");
            whatWeProvideMap.put("We will be available soon", "We will be available soon");
            documentRequiredMap.put("We will be available soon", "We will be available soon");
        } else {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject regObj = jsonArray.getJSONObject(i);
                String name = regObj.getString("name");
                String price="0";
                if (regObj.has("price")){
                    price = regObj.getString("price");
                    priceMap.put(name,price);

                }
                // Prepare lists to collect values for what_we_provide and document_required
                StringBuilder whatWeProvideBuilder = new StringBuilder();
                StringBuilder documentRequiredBuilder = new StringBuilder();

                // Handle document_required
                if (regObj.has("document_required")) {
                    Object documentRequired = regObj.get("document_required");

                    if (documentRequired instanceof JSONArray) {
                        JSONArray documentArray = (JSONArray) documentRequired;

                        // Loop through the array and concatenate the values
                        for (int j = 0; j < documentArray.length(); j++) {
                            documentRequiredBuilder.append(documentArray.getString(j));
                            if (j < documentArray.length() - 1) {
                                documentRequiredBuilder.append("\n"); // Add a new line between items
                            }
                        }

                    } else if (documentRequired instanceof JSONObject) {
                        JSONObject documentObject = (JSONObject) documentRequired;
                        Iterator<String> keys = documentObject.keys();

                        while (keys.hasNext()) {
                            String key = keys.next();
                            String value = documentObject.optString(key);
                            documentRequiredBuilder.append(key).append(": ").append(value).append("\n");
                        }
                    } else {
                        String document_required = regObj.getString("document_required");
                        documentRequiredBuilder.append(document_required);
                    }

                    // Add the concatenated string to the map
                    documentRequiredMap.put(name, documentRequiredBuilder.toString());
                }

                // Handle what_we_provide
                if (regObj.has("what_we_provide")) {
                    Object whatWeProvideRequired = regObj.get("what_we_provide");

                    if (whatWeProvideRequired instanceof JSONArray) {
                        JSONArray whatWeProvideArray = (JSONArray) whatWeProvideRequired;

                        for (int j = 0; j < whatWeProvideArray.length(); j++) {
                            whatWeProvideBuilder.append(whatWeProvideArray.getString(j));
                            if (j < whatWeProvideArray.length() - 1) {
                                whatWeProvideBuilder.append("\n"); // Add a new line between items
                            }
                        }

                    } else if (whatWeProvideRequired instanceof JSONObject) {
                        JSONObject whatWeProvideObject = (JSONObject) whatWeProvideRequired;
                        Iterator<String> keys = whatWeProvideObject.keys();

                        while (keys.hasNext()) {
                            String key = keys.next();
                            String value = whatWeProvideObject.optString(key);
                            whatWeProvideBuilder.append(key).append(": ").append(value).append("\n");
                        }
                    } else {
                        String whatWeProvide = regObj.getString("what_we_provide");
                        whatWeProvideBuilder.append(whatWeProvide);
                    }

                    // Add the concatenated string to the map
                    whatWeProvideMap.put(name, whatWeProvideBuilder.toString());
                }

                spinnerNames.add(name);
            }
        }
    }



}