package com.onezed.Fragment;

import static com.onezed.GlobalVariable.baseUrl.getBusinessIncorporation;
import static com.onezed.GlobalVariable.baseUrl.privacyPolicy;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.onezed.Activity.HomeActivity;
import com.onezed.Activity.InhouseActivity;
import com.onezed.Adapter.ComRegViewAdapter;
import com.onezed.ExeDec;
import com.onezed.GlobalVariable.CustomAlertDialog;
import com.onezed.GlobalVariable.GlobalVariable;
import com.onezed.GlobalVariable.HttpHandler;

import com.onezed.R;
import com.onezed.databinding.FragmentCompanyRegistrationBinding;

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


public class CompanyRegistrationFragment extends Fragment {

    FragmentCompanyRegistrationBinding binding;
    private ArrayList<String> spinnerNames = new ArrayList<>(); // List to store company names
    private ArrayAdapter<String> spinnerAdapter; // Adapter for the Spinner
    private HashMap<String, String> whatWeProvideMap = new HashMap<>(); // HashMap to store what_we_provide values
    private HashMap<String, String> documentRequiredMap = new HashMap<>(); // HashMap to store document_required values
    private ComRegViewAdapter itemAdapter;
    private List<String> itemList;
    private RecyclerView recyclerView;
    String payAmount;
    private final Map<String, String> priceMap = new HashMap<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GlobalVariable.inhouseComRegSelectedType = "Register Company"; // Default type

        // Inflate layout and setup bindings
        binding = FragmentCompanyRegistrationBinding.inflate(inflater, container, false);

        // Setup Spinner
        spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, spinnerNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.firmSpinner.setAdapter(spinnerAdapter);

        // Initialize the RecyclerView and List
        recyclerView = binding.recyclerView; // Assuming binding is already set
        itemList = new ArrayList<>();

        itemAdapter = new ComRegViewAdapter(getContext(), itemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(itemAdapter); // Set RecyclerView adapter

        // Set default checked state for radio buttons
        binding.comRegNoRb.setChecked(true); // Check "Register Company" by default

        // Set default background for tabs
        binding.provideTabLayout.setBackgroundResource(R.drawable.transparent_border);
        binding.documentTabLayout.setBackgroundResource(0);

        // Load data for the default selection
        UnStructureDataCall();

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
        binding.firmSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedName = spinnerNames.get(position);
               // String selectedPrice = parent.getItemAtPosition(position).toString();
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
            UnStructureDataCall(); // Re-fetch the data and update the spinner
            binding.provideTabLayout.setBackgroundResource(R.drawable.transparent_border);
            binding.documentTabLayout.setBackgroundResource(0);
            // Ensure the spinner is reset to the first item when the radio button changes
         //   binding.firmSpinner.setSelection(0);

            if (checkedId == R.id.firm_rb) {
                GlobalVariable.inhouseComRegSelectedType = "Firm";
            } else if (checkedId == R.id.com_reg_no_rb) {
                GlobalVariable.inhouseComRegSelectedType = "Register Company";
            } else if (checkedId == R.id.ngo_society_no_rb) {
                GlobalVariable.inhouseComRegSelectedType = "NGO";
            }

            // Reload the data for the new selection and update RecyclerView
            UnStructureDataCall();
        });

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("FRAGMENT_TYPE", "BUSINESS_REGISTRATION");

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
                bundle.putString("pagename", "Company Registration");

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
            }
        });





        // Back press handle
        binding.btnBackImg.setOnClickListener(view -> {
            // Pop the back stack to return to the previous fragment
            //requireActivity().finish();
            startActivity(new Intent(getActivity(), HomeActivity.class));

        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                startActivity(new Intent(getActivity(), HomeActivity.class));
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

        return binding.getRoot();
    }






    // Helper method to update item list based on selected spinner item
    private void updateItemList(HashMap<String, String> map) {
        int selectedPosition = binding.firmSpinner.getSelectedItemPosition();
        if (selectedPosition != AdapterView.INVALID_POSITION) {
            String selectedName = spinnerNames.get(selectedPosition);
            String data = map.get(selectedName);
           // Log.v("TypeC ",selectedName);
            GlobalVariable.inhouseCategory=selectedName;
            if (data != null) {
                // Split the string by \n and update the RecyclerView
                itemList.clear(); // Clear the previous items
                itemList.addAll(Arrays.asList(data.split("\n")));
                itemAdapter.notifyDataSetChanged();

                // Show RecyclerView
                recyclerView.setVisibility(View.VISIBLE);
            } else {
            }
        } else {
        }
    }

    // Method to fetch the data and update the spinner and map
    private void UnStructureDataCall() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        ExeDec gbi=new ExeDec(getBusinessIncorporation);
        new HttpHandler(gbi.getDec(), getContext())
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
                        Log.v("UnStructureRes",code+" "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.has("status") && jsonObject.getBoolean("status")) {
                                JSONObject dataObj = jsonObject.getJSONObject("data");

                                spinnerNames.clear(); // Clear the spinner list
                                whatWeProvideMap.clear(); // Clear the map

                                // Depending on the selected type, parse the corresponding JSON array
                                if (GlobalVariable.inhouseComRegSelectedType.equals("Register Company") && dataObj.has("Registration Co.")) {
                                    populateSpinnerAndMap(dataObj.getJSONArray("Registration Co."));
                                } else if (GlobalVariable.inhouseComRegSelectedType.equals("Firm") && dataObj.has("Firm")) {
                                    populateSpinnerAndMap(dataObj.getJSONArray("Firm"));
                                } else if (GlobalVariable.inhouseComRegSelectedType.equals("NGO") && dataObj.has("NGO/Society")) {
                                    populateSpinnerAndMap(dataObj.getJSONArray("NGO/Society"));
                                } else {
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
                                        binding.firmSpinner.setSelection(0); // Select the first item
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
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject regObj = jsonArray.getJSONObject(i);
            String name = regObj.getString("name");
            String price="0";
            if (regObj.has("price")){
                 price = regObj.getString("price");
                priceMap.put(name,price);

            }
//            binding.price.setText("Price "+price);
            if (regObj.has("document_required")) {
                Object documentRequired = regObj.get("document_required");

                if (documentRequired instanceof JSONArray) {
                    // It's a JSONArray
                    JSONArray documentArray = (JSONArray) documentRequired;

                    // Loop through the array and handle each string
                    for (int j = 0; j < documentArray.length(); j++) {
                        String document = documentArray.getString(j);

                        documentRequiredMap.put(name, document);
                    }


                } else if (documentRequired instanceof JSONObject) {
                    // If it's a JSONObject, handle accordingly (in case other responses differ)
                    JSONObject documentObject = (JSONObject) documentRequired;
                    // Iterate through the JSONObject
                    Iterator<String> keys = documentObject.keys();

                    while (keys.hasNext()) {
                        String key = keys.next();
                        String value = documentObject.optString(key); // Get the value associated with the key

                        // Now you can handle each key-value pair
                        documentRequiredMap.put(name, value);
                    }
                } else {
                    // It's neither an object nor an array
                    String document_required= regObj.getString("document_required");
                    documentRequiredMap.put(name, document_required);
                }
            }


            String whatWeProvide = regObj.getString("what_we_provide");
            // String documentRequired = regObj.getString("document_required");

            // Add the name to the spinnerNames list
            spinnerNames.add(name);

            // Map the name to its corresponding what_we_provide and document_required values
            whatWeProvideMap.put(name, whatWeProvide);
            //  documentRequiredMap.put(name, documentRequired); // Add this line
        }
    }


}




