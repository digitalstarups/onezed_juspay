package com.onezed.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onezed.Activity.HomeActivity;
import com.onezed.Activity.MainActivity;
import com.onezed.Activity.RechargeActivity;
import com.onezed.Activity.StartActivity;
import com.onezed.Adapter.CategoryAdapter;
import com.onezed.ExeDec;
import com.onezed.GlobalVariable.CustomAlertDialog;
import com.onezed.GlobalVariable.HttpHandler;
import com.onezed.GlobalVariable.SharedPrefManager;
import com.onezed.Model.BBPSViewModel;
import com.onezed.Model.JusPayCategoryModel;
import com.onezed.R;
import com.onezed.databinding.FragmentBBPSBinding;
import com.onezed.databinding.FragmentLicenceRegBinding;


//public class BBPSFragment extends Fragment {
//
//    private FragmentBBPSBinding binding;
//    SharedPrefManager sharedPrefManager;
//    String storedPin;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        binding = FragmentBBPSBinding.inflate(inflater, container, false);
//        sharedPrefManager = new SharedPrefManager(requireContext());
//        storedPin=sharedPrefManager.getUserDetails().get("stored_pin");
//        binding.mobileRecharge.setOnClickListener(view -> {
//            if (storedPin==null){
//                CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name), "You have not Sign In. Sign In to Continue");
//                dialog.show();
//                dialog.onPositiveButton(v ->{
//                    dialog.dismiss();
//                    startActivity(new Intent(getContext(), StartActivity.class));
//
//                } );
//            }else {
//                Intent intent = new Intent(getContext(), RechargeActivity.class);
//                intent.putExtra("FRAGMENT_TYPE", "MOBILE_RECHARGE");
//                intent.putExtra("FRAGMENT_PAGE", "BBPS_PAGE");
//                startActivity(intent);
//            }
//
//        });
//        binding.electricityBilPay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (storedPin == null) {
//                    CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name), "You have not Sign In. Sign In to Continue");
//                    dialog.show();
//                    dialog.onPositiveButton(v -> {
//                        dialog.dismiss();
//                        startActivity(new Intent(getContext(), StartActivity.class));
//
//                    });
//                } else {
//                    Intent intent = new Intent(getContext(), RechargeActivity.class);
//                    intent.putExtra("FRAGMENT_TYPE", "ELECTRICITY_BIL_PAY");
//                    intent.putExtra("FRAGMENT_PAGE", "BBPS_PAGE");
//                    startActivity(intent);
//
//                }
//
//            }
//        });
//        binding.broadbandRecharge.setOnClickListener(view -> {
//            if (storedPin==null){
//                CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name), "You have not Sign In. Sign In to Continue");
//                dialog.show();
//                dialog.onPositiveButton(v ->{
//                    dialog.dismiss();
//                    startActivity(new Intent(getContext(),StartActivity.class));
//
//                } );
//            }else {
//                Intent intent = new Intent(getContext(), RechargeActivity.class);
//                intent.putExtra("FRAGMENT_TYPE", "BROADBAND_RECHARGE");
//                intent.putExtra("FRAGMENT_PAGE", "BBPS_PAGE");
//                startActivity(intent);
//            }
//
//        });
//        binding.mobilePostpaidRecharge.setOnClickListener(view -> {
//            if (storedPin==null){
//                CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name), "You have not Sign In. Sign In to Continue");
//                dialog.show();
//                dialog.onPositiveButton(v ->{
//                    dialog.dismiss();
//                    startActivity(new Intent(getContext(),StartActivity.class));
//
//                } );
//            }else {
//                Intent intent = new Intent(getContext(), RechargeActivity.class);
//                intent.putExtra("FRAGMENT_TYPE", "MOBILE_POSTPAID");
//                intent.putExtra("FRAGMENT_PAGE", "BBPS_PAGE");
//                startActivity(intent);
//
//            }
//
//        });
//
//
//        // Back press handle
//      //  binding.btnBackImg.setOnClickListener(view -> startActivity(new Intent(getActivity(), HomeActivity.class)));
//        binding.btnBackImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getActivity(), HomeActivity.class));
//            }
//        });
//        return binding.getRoot();
//    }
//}

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class BBPSFragment extends Fragment {

    private FragmentBBPSBinding binding;
    private BBPSViewModel viewModel;
    private boolean isInvoice = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBBPSBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(BBPSViewModel.class);


        fetchCategoriesCall();

                // Handle back button press
        binding.btnBackImg.setOnClickListener(view -> startActivity(new Intent(getActivity(), HomeActivity.class)));

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                viewModel.handleBackClick(binding, isInvoice);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

        return binding.getRoot();
    }
    private void fetchCategoriesCall() {
        // Existing fetch logic
        String fetchCategoriesUrl = "https://onezed.app/api/get-all-biller";

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");


        new HttpHandler(fetchCategoriesUrl, getContext())
                .setMediaType(HttpHandler.jsonMediaType)
                .setRequestType(HttpHandler.RequestType.GET)
                .setLoading(true)
                .setHeaders(headers)
                .setOnResponse(new HttpHandler.HttpResponse() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        // Show error dialog
                        getActivity().runOnUiThread(() -> {
                            CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name), getString(R.string.no_response));
                            dialog.show();
                            dialog.onPositiveButton(view ->{
                              //  startActivity(new Intent(getActivity(), HomeActivity.class));
                                dialog.dismiss();
                            } );
                        });
                    }

                    @Override
                    public void onResponse(int code, String response) {
                        Log.v("masterAPIResponse ",response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            binding.mainLayout.setVisibility(View.VISIBLE);
                            if (jsonObject.has("success") && jsonObject.getBoolean("success")) {
                                JSONObject dataObject = jsonObject.getJSONObject("data");
                                JSONArray categoryArray = dataObject.getJSONArray("categoryRespList");

                                List<JusPayCategoryModel> categoryList = new ArrayList<>();
                                for (int i = 0; i < categoryArray.length(); i++) {
                                    JSONObject category = categoryArray.getJSONObject(i);
                                    String categoryId = category.getString("categoryId");
                                    String categoryName = category.getString("categoryName");
                                    String categoryIcon = category.getString("categoryIcon");

                                    categoryList.add(new JusPayCategoryModel(categoryId, categoryName, categoryIcon));
                                }

                                // Update UI with the categories
                                getActivity().runOnUiThread(() -> {
                                    // Now create rows dynamically for 4 icons per row
                                    createIconRows(categoryList);
                                });

                            } else {
                                getActivity().runOnUiThread(() -> {
                                    CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name), "Categories not found");
                                    dialog.show();
                                    dialog.onPositiveButton(view -> {
                                        startActivity(new Intent(getActivity(), HomeActivity.class));
                                                dialog.dismiss();
                                            }
                                           );
                                });
                            }

                        } catch (JSONException e) {
                            Log.v("masterAPIResponse ",e.toString());
                            e.printStackTrace();
                        }
                    }


                }).sendRequest();
    }


    private void createIconRows(List<JusPayCategoryModel> categoryList) {
        int iconsPerRow = 4; // You can adjust this based on your layout
        int totalIcons = categoryList.size();

        // Clear any existing views (to avoid duplication if you refresh data)
        binding.containerLayout.removeAllViews();

        // Loop through categories to create rows
        for (int i = 0; i < Math.ceil((double) totalIcons / iconsPerRow); i++) {
            // Create a new LinearLayout for each row
            LinearLayout rowLayout = new LinearLayout(getActivity());
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setPadding(0, 20, 0, 8);
            rowLayout.setWeightSum(iconsPerRow);

            // Add icons to the row
            for (int j = 0; j < iconsPerRow; j++) {
                int currentIndex = i * iconsPerRow + j;
                if (currentIndex >= totalIcons) {
                    break; // Stop if we've added all icons
                }

                // Get the category for this index
                JusPayCategoryModel category = categoryList.get(currentIndex);

                // Create a vertical layout for each icon + name
                LinearLayout iconLayout = new LinearLayout(getContext());
                iconLayout.setOrientation(LinearLayout.VERTICAL);
                iconLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f // Equal weight for all columns
                ));
                iconLayout.setPadding(0, 5, 5, 8);


                // Add the ImageView (icon)
                ImageView icon = new ImageView(getContext());
                icon.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        100 // Set a fixed size for the icon
                ));

                // Load image directly using BitmapFactory
                // In the createIconRows method
                new Thread(() -> {
                    try {
                        // Decode the base64 string into bytes
                        String base64Image = category.getCategoryIcon().split(",")[1]; // Remove the prefix (data:image/png;base64,)
                        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);

                        // Convert bytes into a Bitmap
                        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        // Set the image on the main thread
                        getActivity().runOnUiThread(() -> icon.setImageBitmap(decodedBitmap));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();

                icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                iconLayout.addView(icon);

                // Add the TextView (name)
                TextView name = new TextView(getContext());
                name.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                name.setText(category.getCategoryName()); // Dynamic name from category
                name.setTextColor(Color.BLACK);
                name.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                name.setPadding(0, 8, 0, 0);

                iconLayout.addView(name);
                // Add click listener for the iconLayout
                iconLayout.setOnClickListener(view -> {
                    // Show the Toast with the category name when clicked
                    Log.v("fragmentType",category.getCategoryName());
                    viewModel.FaderalBankBBPSClick(requireContext(),category.getCategoryName());
                    Toast.makeText(getActivity(), category.getCategoryName(), Toast.LENGTH_SHORT).show();

                });

                // Add the icon layout to the row
                rowLayout.addView(iconLayout);
            }

            // Add the row to the container
            binding.containerLayout.addView(rowLayout);
        }
    }




}