package com.onezed.Fragment;

import static android.app.Activity.RESULT_OK;

import static androidx.core.content.ContextCompat.getSystemService;
import static com.onezed.GlobalVariable.baseUrl.profile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;


import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.onezed.Activity.HomeActivity;
import com.onezed.Activity.StartActivity;
import com.onezed.ExeDec;
import com.onezed.GlobalVariable.CustomAlertDialog;
import com.onezed.GlobalVariable.HttpHandler;

import com.onezed.GlobalVariable.SharedPrefManager;
import com.onezed.Model.UserProfileModel;
import com.onezed.R;
import com.onezed.databinding.FragmentViewProfileBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;


public class ViewProfile extends Fragment {

    private FragmentViewProfileBinding binding;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_IMAGE_PICK = 3;
    //private String currentPhotoPath;
    SharedPrefManager sharedPrefManager;
    BottomSheetDialog sheetDialog;
    TextView txtMsg;
    AppCompatButton verifyOtp;
    ImageView imageViewPrint, imgCancelDialog;
    String profileImageUrl;
    boolean isViewImage=false;
    String selectedState=UserProfileModel.getInstance().getState();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentViewProfileBinding.inflate(inflater, container, false);
        sharedPrefManager = new SharedPrefManager(requireActivity());
        // Check SharedPreferences and update the ImageView
        updateProfileImage();
        profileImageUrl = sharedPrefManager.getProfileImageUrl();
        Log.v("URL",profileImageUrl);
        profileApiCall();
//        if (profileImageUrl.equals("")){
//            Log.v("URL1",profileImageUrl);
//                    Glide.with(getActivity())
//                .load(profileImageUrl)  // Network image URL
//                .placeholder(R.drawable.ic_baseline_account_circle_profile)  // Optional: placeholder while loading
//                .error(R.drawable.ic_baseline_account_circle_profile)  // Optional: error image if loading fails
//                .into(binding.profileImg);
//        }else {
//            Log.v("URL2",profileImageUrl);
//            Glide.with(getActivity())
//                    .load(profileImageUrl)  // Network image URL
//                    .placeholder(R.drawable.ic_baseline_account_circle_profile)  // Optional: placeholder while loading
//                    .error(R.drawable.ic_baseline_account_circle_profile)  // Optional: error image if loading fails
//                    .into(binding.profileImg);
//        }
        // Set the Spinner value based on selectedState
        if (selectedState != null) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) binding.uStateEditSpinner.getAdapter(); // Get the spinner adapter
            int position = adapter.getPosition(selectedState); // Find the position of the selected state
            if (position >= 0) {
                binding.uStateEditSpinner.setSelection(position); // Set the default selection
            }
        }

        binding.uStateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.uStateEdit.setVisibility(View.GONE);
                binding.uStateEditSpinner.setVisibility(View.VISIBLE);

            }
        });
        // Set OnItemSelectedListener for the spinner
        binding.uStateEditSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position > 0) {
                    selectedState = parentView.getItemAtPosition(position).toString();
                    binding.uStateEdit.setVisibility(View.GONE);
                } else {
                    selectedState = null; // Reset if no valid state is selected
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

        binding.cameraImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if( checkCameraPermissions()){
//                    dispatchTakePictureIntent();
//                }
                View v = getLayoutInflater().inflate(R.layout.fetch_image_layout, null);
                sheetDialog = new BottomSheetDialog(getContext());
                sheetDialog.setContentView(v);
                sheetDialog.setCancelable(false);
                sheetDialog.show();

                imgCancelDialog = v.findViewById(R.id.cancel_btn);
                ImageView cameraImg,galleryImg,deleteImg;
                cameraImg=v.findViewById(R.id.camera_img);
                deleteImg=v.findViewById(R.id.delete_img);
                galleryImg=v.findViewById(R.id.gallery_img);

                cameraImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if( checkCameraPermissions()){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");  // Or "video/*" for video files
                           // startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
                            dispatchTakePictureIntent();
                        }


                        }
                        sheetDialog.dismiss();
                    }
                });
                galleryImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, REQUEST_IMAGE_PICK);

                        sheetDialog.dismiss();
                    }
                });
                deleteImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Set the ShapeableImageView to a default image or placeholder
                        binding.profileImg.setImageResource(R.drawable.ic_baseline_account_circle_profile); // Replace with your default drawable

                        // Convert vector drawable to bitmap
                        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_account_circle_profile); // Vector drawable
                        if (drawable != null) {
                            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(bitmap);
                            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                            drawable.draw(canvas);

                            // Proceed with uploading the bitmap
                            if (bitmap != null) {
                                int userId = sharedPrefManager.getUserId();
                                String url = String.format("https://onezed.app/api/upload/image/%s", userId);

                                HttpHandler httpHandler = new HttpHandler(url, getContext());
                                httpHandler.setLoading(true) // Show loading if needed
                                        .setOnResponse(new HttpHandler.HttpResponse() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                // Handle the failure
                                                Log.e("Upload", "Upload failed: " + e.getMessage());
                                            }

                                            @Override
                                            public void onResponse(int code, String response) {
                                                // Handle the server response
                                                Log.d("Upload", "Response Code: " + code);
                                                Log.d("Upload", "Response: " + response);
                                            }
                                        }).uploadImage(bitmap); // Upload the bitmap

                                // Optionally, clear the photo path from SharedPreferences
                                clearPhotoPathFromPreferences();

                                // Dismiss the dialog or take further actions
                                sheetDialog.dismiss();
                            } else {
                                Log.e("Upload", "Bitmap is null, unable to upload image.");
                            }
                        } else {
                            Log.e("Upload", "Drawable is null, unable to convert to bitmap.");
                        }


                    }
                });
                imgCancelDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sheetDialog.dismiss();
                    }
                });

            }
        });
//        Log.v("Udata",UserProfileModel.getInstance().getName());
        binding.uName.setText(UserProfileModel.getInstance().getName());
        binding.uEmail.setText(UserProfileModel.getInstance().getEmail());
        binding.uMobile.setText(UserProfileModel.getInstance().getMobileNo());
        //binding.uMobileEdit.setText(UserProfileModel.getInstance().getMobileNo());
        binding.uCity.setText(UserProfileModel.getInstance().getCity());
        binding.uState.setText(UserProfileModel.getInstance().getState());
        binding.uPincode.setText(UserProfileModel.getInstance().getPincode());
        binding.uAddress.setText(UserProfileModel.getInstance().getAddress());
        if (UserProfileModel.getInstance().getAccountNo().equals("null") || UserProfileModel.getInstance().getAccountNo().isEmpty()){
            binding.AccountNumber.setText("null");
            binding.bankDetailsCardView.setVisibility(View.GONE);
        }else {
            binding.AccountNumber.setText("not null");
        }


        binding.personalDetailsEditImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.personalDetailsLayout.setVisibility(View.GONE);
                binding.personalDetailsEditLayout.setVisibility(View.VISIBLE);
                binding.personalDetailsEditImg.setVisibility(View.GONE);
                binding.personalDetailsUpdateImg.setVisibility(View.VISIBLE);
                binding.uNameEdit.setText(UserProfileModel.getInstance().getName());
                binding.uEmailEdit.setText(UserProfileModel.getInstance().getEmail());
                binding.uAddressEdit.setText(UserProfileModel.getInstance().getAddress());
                binding.uCityEdit.setText(UserProfileModel.getInstance().getCity());
               // binding.uStateEdit.setText(UserProfileModel.getInstance().getState());

                binding.uPincodeEdit.setText(UserProfileModel.getInstance().getPincode());

            }
        });

        binding.personalDetailsUpdateImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validation()){
                    updateProfileApi();
                    binding.personalDetailsLayout.setVisibility(View.VISIBLE);

                    binding.personalDetailsEditImg.setVisibility(View.VISIBLE);
                    binding.personalDetailsUpdateImg.setVisibility(View.GONE);

                }

            }
        });


        binding.profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.scrollView.setVisibility(View.GONE);
                binding.viewProfileImage.setVisibility(View.VISIBLE);
                isViewImage=true;
                profileApiCall();
            }
        });

        binding.btnViewProfileBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isViewImage){
                    binding.scrollView.setVisibility(View.VISIBLE);
                    binding.viewProfileImage.setVisibility(View.GONE);
                    isViewImage=false;
                }else {
                    startActivity(new Intent(getActivity(), HomeActivity.class));
                    //in
                    //getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    //out
                    //getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    getActivity().finish();
                }

            }
        });
        return binding.getRoot();
    }





    private boolean validation() {
        // Check Name
        if (TextUtils.isEmpty(binding.uNameEdit.getText().toString().trim())) {
            binding.uNameEdit.setError("Required Name");
            binding.uNameEdit.requestFocus();
            return false;
        } else {
           // binding.uNameEdit.setErrorEnabled(false);
        }
        // Check Email
        if (TextUtils.isEmpty(binding.uEmailEdit.getText().toString().trim())) {
            binding.uEmailEdit.setError("Required Email");
            binding.uEmailEdit.requestFocus();
            return false;
        } else {
            //binding.tilEmail.setErrorEnabled(false);
        }

        // Check Address
        if (TextUtils.isEmpty(binding.uAddressEdit.getText().toString().trim())) {
            binding.uAddressEdit.setError("Required Address");
            binding.uAddressEdit.requestFocus();
            return false;
        } else {
            //binding.tilEmail.setErrorEnabled(false);
        }
        // Check City
        if (TextUtils.isEmpty(binding.uCityEdit.getText().toString().trim())) {
            binding.uCityEdit.setError("Required City");
            binding.uCityEdit.requestFocus();
            return false;
        } else {
            //binding.tilEmail.setErrorEnabled(false);
        }
        // Check State
        if (selectedState==null){
            // Use this line to set the background for the spinner's dropdown (popup)
            //binding.editStateLayout.setPopupBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.error_edit_bg));
            binding.uStateEdit.setVisibility(View.VISIBLE);
            binding.uStateEdit.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.error_edit_bg));
            return false;

        }else {
            binding.uStateEdit.setVisibility(View.GONE);
           // binding.editStateLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.pin_edit_text));
        }
//        if (TextUtils.isEmpty(binding.uStateEdit.getText().toString().trim())) {
//            binding.uStateEdit.setError("Required State");
//            binding.uStateEdit.requestFocus();
//            return false;
//        } else {
//            //binding.tilEmail.setErrorEnabled(false);
//        }
        // Check Pincode
        if (TextUtils.isEmpty(binding.uPincodeEdit.getText().toString().trim())) {
            binding.uPincodeEdit.setError("Required Pincode");
            binding.uPincodeEdit.requestFocus();
            return false;
        }
        else if(binding.uPincodeEdit.getText().toString().trim().length()<6){
            //binding.tilEmail.setErrorEnabled(false);
            binding.uPincodeEdit.setError("Pincode must be in 6 digit");
            binding.uPincodeEdit.requestFocus();
            return false;
        }
        return true;
    }

    private void updateProfileApi() {
        int userId=sharedPrefManager.getUserId();
        JSONObject joObj = new JSONObject();
        try {
            joObj.put("name", binding.uNameEdit.getText().toString() );
            joObj.put("email", binding.uEmailEdit.getText().toString());
            joObj.put("address", binding.uAddressEdit.getText().toString());
            joObj.put("pincode", binding.uPincodeEdit.getText().toString());
            joObj.put("state", selectedState);
            joObj.put("city", binding.uCityEdit.getText().toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.v("ProfileEditR",joObj.toString());
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        // ExeDec pf=new ExeDec(profile);
       // String url = String.format(pf.getDec()+"%s", userId);
        String url=String.format(" https://onezed.app/api/user/edit/%s", userId);

        new HttpHandler(url, getActivity())
                .setMediaType(HttpHandler.jsonMediaType)
                .setRequestType(HttpHandler.RequestType.POST)
                .setStringData(joObj.toString())
                .setLoading(true)
                .setHeaders(headers)
                .setOnResponse(new HttpHandler.HttpResponse() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        CustomAlertDialog dialog = new CustomAlertDialog(getActivity(), R.style.WideDialog, getString(R.string.app_name), getString(R.string.no_response));
                        dialog.show();
                        dialog.onPositiveButton(view -> dialog.dismiss());
                    }

                    @Override
                    public void onResponse(int code, String response) {
                        Log.v("ProfileEdit",code+" "+response);
                        binding.personalDetailsEditLayout.setVisibility(View.GONE);
                        binding.uNameEdit.setText(null);
                        binding.uEmailEdit.setText(null);
                        binding.uCityEdit.setText(null);
                       // binding.uStateEdit.setText(null);
                        selectedState=null;
                        binding.uPincodeEdit.setText(null);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            CustomAlertDialog dialog;
                            switch (code) {
                                case 200:
                                    if (jsonObject.getBoolean("status")){
                                        profileApiCall();
                                    }

                                    break;
                                case 404:
                                    profileApiCall();
                                    break;
                                default:
                                    dialog = new CustomAlertDialog(getActivity(), R.style.WideDialog, "OneZed", "Something Wrong, Please Try after some times");
                                    dialog.show();
                                    dialog.onPositiveButton(view -> dialog.dismiss());
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }

                }).sendRequest();
    }
    private void profileApiCall() {

        int userId=sharedPrefManager.getUserId();
        Log.v("userId", String.valueOf(userId));
        ExeDec pf=new ExeDec(profile);
        String url = String.format(pf.getDec()+"%s", userId);

        new HttpHandler(url, getActivity())
                .setMediaType(HttpHandler.jsonMediaType)
                .setRequestType(HttpHandler.RequestType.GET)
                //.setStringData(joObj.toString())
                // .setLoading(true)
                //.setHeaders(headers)
                .setOnResponse(new HttpHandler.HttpResponse() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        CustomAlertDialog dialog = new CustomAlertDialog( getActivity(), R.style.WideDialog, getString(R.string.app_name), getString(R.string.no_response));
                        dialog.show();
                        dialog.onPositiveButton(view -> dialog.dismiss());
                    }

                    @Override
                    public void onResponse(int code, String response) {
                        Log.v("ProfileR",response);
                        profileImageUrl = sharedPrefManager.getProfileImageUrl();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            CustomAlertDialog dialog;
                            switch (code) {
                                case 400:
                                    dialog = new CustomAlertDialog( getActivity(), R.style.WideDialog, "OneZed", jsonObject.getString("message"));
                                    dialog.show();
                                    dialog.onPositiveButton(view -> dialog.dismiss());
                                    break;
                                case 404:
                                    if (jsonObject.getBoolean("status")){

                                        dialog = new CustomAlertDialog( getActivity(), R.style.WideDialog, "OneZed", jsonObject.getString("message"));
                                        dialog.show();
                                        dialog.onPositiveButton(view -> dialog.dismiss());
                                    }else {

                                        dialog = new CustomAlertDialog( getActivity(), R.style.WideDialog, "OneZed", jsonObject.getString("message"));
                                        dialog.show();
                                        dialog.onPositiveButton(view -> {
                                            startActivity(new Intent( getActivity(), StartActivity.class));
                                        });


                                    }
                                    break;

                                case 200:
                                    JSONObject dataObj=jsonObject.getJSONObject("data");

                                    String name= dataObj.getString("name");
                                    String email= dataObj.getString("email");
                                    String phone= dataObj.getString("phone");
                                    String city= dataObj.getString("city");
                                    String state= dataObj.getString("state");
                                    String pincode= dataObj.getString("pincode");
                                    String updatedate= dataObj.getString("updated_at");
                                    String imageUrl= dataObj.getString("profile_img");
                                    String accountNo = dataObj.optString("AccountNo", "NULL");
                                    selectedState=state;

                                    UserProfileModel.getInstance().setMobileNo(phone);
                                    UserProfileModel.getInstance().setEmail(email);
                                    UserProfileModel.getInstance().setName(name);
                                    UserProfileModel.getInstance().setState(state);
                                    UserProfileModel.getInstance().setCity(city);
                                    UserProfileModel.getInstance().setPincode(pincode);
                                    UserProfileModel.getInstance().setImageUrl(imageUrl);
                                    UserProfileModel.getInstance().setAccountNo(accountNo);

//                                    if (!UserProfileModel.getInstance().getImageUrl().equals(null)){
//                                        Glide.with(getActivity())
//                                        .load(UserProfileModel.getInstance().getImageUrl())  // Network image URL
//                                        .placeholder(R.drawable.ic_baseline_account_circle_profile)  // Optional: placeholder while loading
//                                        .error(R.drawable.ic_baseline_account_circle_profile)  // Optional: error image if loading fails
//                                        .into(binding.profileImg);
//                                    }
                                    binding.uName.setText(UserProfileModel.getInstance().getName());
                                    binding.uEmail.setText(UserProfileModel.getInstance().getEmail());
                                    binding.uMobile.setText(UserProfileModel.getInstance().getMobileNo());
                                    //binding.uMobileEdit.setText(UserProfileModel.getInstance().getMobileNo());
                                    binding.uCity.setText(UserProfileModel.getInstance().getCity());
                                    if (!(UserProfileModel.getInstance().getAddress()=="null")){
                                        binding.uState.setText(UserProfileModel.getInstance().getState());
                                    }

                                    binding.uPincode.setText(UserProfileModel.getInstance().getPincode());
                                    Glide.with(getActivity())
                                            .load(imageUrl)  // Network image URL
                                            .placeholder(R.drawable.ic_baseline_account_circle_profile)  // Optional: placeholder while loading
                                            .error(R.drawable.ic_baseline_account_circle_profile)  // Optional: error image if loading fails
                                            .into(binding.profileImg);
                                    Glide.with(getActivity())
                                            .load(imageUrl)  // Network image URL
                                            .placeholder(R.drawable.ic_baseline_account_circle_profile)  // Optional: placeholder while loading
                                            .error(R.drawable.ic_baseline_account_circle_profile)  // Optional: error image if loading fails
                                            .into(binding.viewProfileImage);
                                    break;
                                default:
                                    dialog = new CustomAlertDialog( getActivity(), R.style.WideDialog, "OneZed", "Something Wrong, Please Try after some times");
                                    dialog.show();
                                    dialog.onPositiveButton(view -> dialog.dismiss());
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }

                }).sendRequest();
    }

    private void updateProfileImage() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
       // String photoPath = sharedPreferences.getString("photoPath", null);

        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            // Check ImageView dimensions after layout
            binding.profileImg.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // Remove the listener to avoid multiple calls
                    binding.profileImg.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    int targetW = binding.profileImg.getWidth();
                    int targetH = binding.profileImg.getHeight();

                    // Ensure dimensions are valid
                    if (targetW <= 0 || targetH <= 0) {
                        binding.profileImg.setImageResource(R.drawable.ic_baseline_account_circle_profile); // Default image
                        return;
                    }

                    // Get the dimensions of the bitmap
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(profileImageUrl, bmOptions);
                    int photoW = bmOptions.outWidth;
                    int photoH = bmOptions.outHeight;

                    // Ensure bitmap dimensions are valid
                    if (photoW <= 0 || photoH <= 0) {
                        Log.e("ViewProfile", "Invalid dimensions for bitmap: Width=" + photoW + " Height=" + photoH);
                        binding.profileImg.setImageResource(R.drawable.ic_baseline_account_circle_profile); // Default image
                        return;
                    }

                    // Determine how much to scale down the image
                    int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

                    // Decode the image file into a Bitmap sized to fill the view
                    bmOptions.inJustDecodeBounds = false;
                    bmOptions.inSampleSize = scaleFactor;
                    bmOptions.inPurgeable = true;
                    bmOptions.inPreferredConfig = Bitmap.Config.ARGB_8888; // Support transparency

                    Bitmap bitmap = BitmapFactory.decodeFile(profileImageUrl, bmOptions);
                    binding.profileImg.setImageBitmap(bitmap);
                   // updateProfileImageAPI(bitmap);

                }
            });
        } else {
            // Set default app logo if no photo path is available
            binding.profileImg.setImageResource(R.drawable.ic_baseline_account_circle_profile);
        }

    }

    // Helper method to convert Bitmap to File
    private File convertBitmapToFile(Bitmap bitmap) {
        File file = new File(getContext().getCacheDir(), "image_" + System.currentTimeMillis() + ".jpg");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // Compress the bitmap
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Return null if conversion fails
        }
    }

    private Bitmap byteArrayToBitmap(byte[] imageData) {
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
    }

    private byte[] compressBitmap(Bitmap bitmap, int maxKBSize) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int quality = 100; // Start with maximum quality
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);

        // Loop to reduce quality until the image size is below the max size (in KB)
        while (outputStream.toByteArray().length / 1024 > maxKBSize && quality > 5) {
            outputStream.reset(); // Clear the outputStream
            quality -= 5; // Reduce the quality by smaller increments
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
        }

        // Downscale image if still too large
        while (outputStream.toByteArray().length / 1024 > maxKBSize) {
            int newWidth = (int) (bitmap.getWidth() * 0.8);
            int newHeight = (int) (bitmap.getHeight() * 0.8);
            bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
            outputStream.reset();
            quality = 100; // Reset quality to maximum after downscaling
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
        }

        return outputStream.toByteArray();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Get the dimensions of the ImageView
            int targetW = binding.profileImg.getWidth();
            int targetH = binding.profileImg.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(profileImageUrl, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the view
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(profileImageUrl, bmOptions);

            // Fix the orientation of the bitmap
            bitmap = fixImageOrientation(profileImageUrl, bitmap);

            // Set the bitmap to the ShapeableImageView
            binding.profileImg.setImageBitmap(bitmap);

            // Compress the bitmap to fit within 2048 KB
            byte[] compressedImage = compressBitmap(bitmap, 90); // Ensure it fits within 2048 KB

            // Check if the compressed image is within size limit
            if (compressedImage.length / 1024 <= 90) {
                uploadCompressedImage(compressedImage); // Upload the compressed image
            } else {
                Log.e("Upload", "Image is still larger than 2048KB even after compression.");
            }

        } else if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            // Handle gallery image
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                binding.profileImg.setImageURI(selectedImageUri);

                // Optionally save the selected image path to preferences
                String imagePath = getRealPathFromURI(selectedImageUri); // Get the file path from the Uri
                savePhotoPathToPreferences(imagePath); // Save the image path if needed

                // Convert the image file to a Bitmap
                Bitmap bitmap = fileToBitmap(imagePath); // Pass the file path, not the Uri
                if (bitmap != null) {
                    // Compress the bitmap to fit within 2048 KB
                    byte[] compressedImage = compressBitmap(bitmap, 90);

                    // Check if the compressed image is within size limit
                    if (compressedImage.length / 1024 <= 90) {
                        uploadCompressedImage(compressedImage); // Upload the compressed image
                    } else {
                        Log.e("Upload", "Image is still larger than 2048KB after compression.");
                    }
                } else {
                    Log.e("Upload", "Bitmap is null, unable to upload image.");
                }
            }
        } else {
            Log.v("upload", "Operation canceled or image not selected.");
        }
    }

    private void uploadCompressedImage(byte[] compressedImage) {
        Bitmap compressedBitmap = byteArrayToBitmap(compressedImage);
        Log.v("ImageBitMap",compressedBitmap.toString());
        int userId = sharedPrefManager.getUserId();
        String url = String.format("https://onezed.app/api/upload/image/%s", userId);
        HttpHandler httpHandler = new HttpHandler(url, getContext());
        httpHandler.setLoading(true)
                .setOnResponse(new HttpHandler.HttpResponse() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("Upload", "Upload failed: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(int code, String response) {
                        Log.d("Upload", "Response Code: " + code);
                        Log.d("Upload", "Response: " + response);
                    }
                }).uploadImage(compressedBitmap);
    }


    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Return null if there's an error
        }
    }

    private void savePhotoPathToPreferences(String path) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("photoPath", path);
        editor.apply();
        Log.v("photo path ",path.toString());
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        profileImageUrl = image.getAbsolutePath();
        savePhotoPathToPreferences(profileImageUrl);
        //updateProfileImageAPI(profileImageUrl);
        Log.e("Upload", "Upload failed: " +profileImageUrl);

        return image;
    }
    // Method to convert image file to Bitmap
    public static Bitmap fileToBitmap(String filePath) {
        File imgFile = new File(filePath);
        if (imgFile.exists()) {
            return BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }
        return null; // Return null if file doesn't exist
    }
//    private void updateProfileImageAPI(Bitmap imageBitmap) {
//        int userId = sharedPrefManager.getUserId();
//
//        // Convert the bitmap to a file (JPEG by default)
//        File imageFile = null;
//        try {
//            imageFile = convertBitmapToFile(imageBitmap, getContext());  // Make sure convertBitmapToFile works correctly
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if (imageFile == null) {
//            Toast.makeText(getContext(), "Image file creation failed.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Create a multipart body builder
//        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
//
//        // Add the image file to the multipart request
//        multipartBodyBuilder.addFormDataPart("image", imageFile.getName(),
//                RequestBody.create(MediaType.parse("image/jpeg"), imageFile));
//
//        // Build the multipart body
//        RequestBody requestBody = multipartBodyBuilder.build();
//
//        // Headers for the request
//        Map<String, String> headers = new HashMap<>();
//        headers.put("Accept", "application/json");  // No need to set Content-Type, OkHttp handles it
//
//        // URL for the API endpoint
//        String url = String.format("https://onezed.app/api/upload/image/%s", userId);
//        Log.v("ResponseView", url);
//
//        // Send the request using HttpHandler
//        new HttpHandler(url, getContext())
//                .setRequestType(HttpHandler.RequestType.POST)
//                .setRequestBody(requestBody)  // Set the multipart body containing the image
//                .setLoading(true)
//                .setHeaders(headers)  // Set headers
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
//                        Log.v("ResponseView", code + " " + response);
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            CustomAlertDialog dialog;
//                            switch (code) {
//                                case 200:
//                                    dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", jsonObject.getString("message"));
//                                    dialog.show();
//                                    dialog.onPositiveButton(view -> {
//                                        dialog.dismiss();
//                                        FragmentManager fm = getActivity().getSupportFragmentManager();
//                                        FragmentTransaction ft = fm.beginTransaction();
//                                        ft.replace(R.id.splash_frameLayout, new MobileLoginFragment());
//                                        ft.commit();
//                                    });
//                                    break;
//                                default:
//                                    dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", "Something went wrong, please try again later.");
//                                    dialog.show();
//                                    dialog.onPositiveButton(view -> dialog.dismiss());
//                                    break;
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            throw new RuntimeException(e);
//                        }
//                    }
//
//                }).sendRequest();
//    }




    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.onezed.provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private boolean checkCameraPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the camera permission
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
            return false; // Do not proceed until permission is granted
        }
        return true; // Permission is already granted
    }
    private String getRealPathFromURI(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        }
        return null;
    }
    private void clearPhotoPathFromPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("photoPath");  // Remove the photoPath key
        editor.apply();
    }
    private Bitmap fixImageOrientation(String imagePath, Bitmap bitmap) {
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    matrix.preScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    matrix.preScale(1, -1);
                    break;
                default:
                    return bitmap; // No rotation needed
            }

            // Create a new rotated bitmap
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }




}