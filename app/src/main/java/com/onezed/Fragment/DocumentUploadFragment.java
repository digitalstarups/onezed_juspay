package com.onezed.Fragment;

import static android.app.Activity.RESULT_OK;

import static com.onezed.Fragment.ViewProfile.fileToBitmap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.onezed.Activity.BillPayActivity;
import com.onezed.Activity.HomeActivity;
import com.onezed.Activity.JusPayActivity;
import com.onezed.Activity.RechargeActivity;
import com.onezed.GlobalVariable.CustomAlertDialog;
import com.onezed.GlobalVariable.GlobalVariable;
import com.onezed.GlobalVariable.HttpHandler;
import com.onezed.GlobalVariable.SharedPrefManager;
import com.onezed.Model.UserProfileModel;
import com.onezed.R;
import com.onezed.databinding.FragmentCompanyRegistrationBinding;
import com.onezed.databinding.FragmentDocumentUploadBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;


public class DocumentUploadFragment extends Fragment {


    private FragmentDocumentUploadBinding binding;
    ImageView galleryIcon,imgCancelDialog;
    BottomSheetDialog sheetDialog;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_IMAGE_PICK = 3;

    String profileImageUrl,pageName,payAmount;
    double totalAmount;
    SharedPrefManager sharedPrefManager;
    private ImageView selectedImageView;



    List<String> sentences = new ArrayList<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDocumentUploadBinding.inflate(inflater, container, false);
        sharedPrefManager = new SharedPrefManager(requireActivity());
       // profileImageUrl = sharedPrefManager.getProfileImageUrl();

        // Get the intent and fragment type
        if (getArguments() != null) {
            pageName = getArguments().getString("pagename");
            payAmount = getArguments().getString("payAmount");
            binding.appBarTxt.setText(pageName);

            // Retrieve the ArrayList and convert it back to List<String>
            sentences = getArguments().getStringArrayList("customParameter");
        }
      //  Log.v("Amount",payAmount);
        totalAmount = calculateTotalWithGST(payAmount);
        generateDynamicFields();


        binding.btnUploadPay.setText("UPLOAD & PAY"+"\n"+totalAmount);
        binding.btnUploadWithoutPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchAllData();
            }
        });
        binding.btnUploadPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), JusPayActivity.class);
                intent.putExtra("amount", String.valueOf(totalAmount)); // Passing amount as String
                startActivity(intent);

//                Intent intent = new Intent(getActivity(), BillPayActivity.class);
//                Log.v("InhouseData", UserProfileModel.getInstance().getMobileNo()+" "+totalAmount+" "+pageName+" ");
//                intent.putExtra("Type", "IN_HOUSE");  // 'amount' is the key, 'amount' is the value you want to transfer
//                intent.putExtra("TotalAmount", totalAmount);  // 'amount' is the key, 'amount' is the value you want to transfer
//                intent.putExtra("service", pageName);
//                intent.putExtra("serviceName", GlobalVariable.inhouseComRegSelectedCategory);
//             //   intent.putExtra("consumer_id",consumerId );  // 'amount' is the key, 'amount' is the value you want to transfer
//              //  intent.putExtra("spkey", spKey);  // 'amount' is the key, 'amount' is the value you want to transfer
//                startActivity(intent);

            }
        });

        binding.btnBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                if (getFragmentManager() != null) {
//                    getFragmentManager().popBackStack();
//                }
                if (pageName.equals("Company Registration")){
                    CompanyRegistrationFragment fm = new CompanyRegistrationFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.inhouse_frameLayout, fm);
                    transaction.addToBackStack(null); // Optional: Add to back stack
                    transaction.commit();
                } else if (pageName.equals("Licence Registration")) {
                    LicenceRegFragment fm = new LicenceRegFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.inhouse_frameLayout, fm);
                    transaction.addToBackStack(null); // Optional: Add to back stack
                    transaction.commit();
                }else if (pageName.equals("Accounting & E-filing")) {
                    AccountEfilingFragment fm = new AccountEfilingFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.inhouse_frameLayout, fm);
                    transaction.addToBackStack(null); // Optional: Add to back stack
                    transaction.commit();
                }else if (pageName.equals("Brand Protection")) {
                    BrandProtectionFragment fm = new BrandProtectionFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.inhouse_frameLayout, fm);
                    transaction.addToBackStack(null); // Optional: Add to back stack
                    transaction.commit();
                }else {
                    startActivity(new Intent(getActivity(), HomeActivity.class));
                }
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {

//                if (getFragmentManager() != null) {
//                    getFragmentManager().popBackStack();
//                }
                if (pageName.equals("Company Registration")){
                    CompanyRegistrationFragment fm = new CompanyRegistrationFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.inhouse_frameLayout, fm);
                    transaction.addToBackStack(null); // Optional: Add to back stack
                    transaction.commit();
                } else if (pageName.equals("Licence Registration")) {
                    LicenceRegFragment fm = new LicenceRegFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.inhouse_frameLayout, fm);
                    transaction.addToBackStack(null); // Optional: Add to back stack
                    transaction.commit();
                }else if (pageName.equals("Accounting & E-filing")) {
                    AccountEfilingFragment fm = new AccountEfilingFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.inhouse_frameLayout, fm);
                    transaction.addToBackStack(null); // Optional: Add to back stack
                    transaction.commit();
                }else if (pageName.equals("Brand Protection")) {
                    BrandProtectionFragment fm = new BrandProtectionFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.inhouse_frameLayout, fm);
                    transaction.addToBackStack(null); // Optional: Add to back stack
                    transaction.commit();
                }else {
                    startActivity(new Intent(getActivity(), HomeActivity.class));
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);
        return binding.getRoot();



    }
    public static int calculateTotalWithGST(String priceText) {
        // Stop counting if "/-" is present
        if (priceText.contains("/-")) {
            priceText = priceText.substring(0, priceText.indexOf("/-")); // Extract only the number part
        }

        // Remove all non-numeric characters except commas
        String numericValue = priceText.replaceAll("[^0-9,]", ""); // Removes Rs., spaces, etc.

        try {
            // Remove commas for proper conversion
            numericValue = numericValue.replace(",", "");

            int payAmount = Integer.parseInt(numericValue); // Convert to int
            int gstAmount = (int) Math.round(payAmount * 0.18); // Calculate 18% GST
            return payAmount + gstAmount; // Return total with GST
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0; // Return 0 in case of error
        }
    }



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
       // profileImageUrl = image.getAbsolutePath();
        savePhotoPathToPreferences(profileImageUrl);
        //updateProfileImageAPI(profileImageUrl);
       // Log.e("Upload", "Upload failed: " +profileImageUrl);

        return image;
    }
    private void savePhotoPathToPreferences(String path) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("photoPath", path);
        editor.apply();
        Log.v("photo path ",path.toString());
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
    private void generateDynamicFields() {
        float density = getContext().getResources().getDisplayMetrics().density;
        int heightInPx = (int) (50 * density); // Convert 50dp to pixels

        // Ensure sentences is not null
        if (sentences == null || sentences.isEmpty()) {
            return; // Exit if there are no sentences
        }

        for (String sentence : sentences) {  // Iterate over List<String>
            // Create a vertical LinearLayout to hold TextView and EditText
            LinearLayout containerLayout = new LinearLayout(getContext());
            containerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            containerLayout.setOrientation(LinearLayout.VERTICAL);

            // Create TextView for the label
            TextView textView = new TextView(getContext());
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            textView.setText(sentence);
            textView.setTextSize(14f);
            textView.setPadding(8, 8, 8, 4);
            textView.setTextColor(Color.BLACK); // Change text color if needed

            // Create a horizontal LinearLayout for EditText and ImageView
            LinearLayout rowLayout = new LinearLayout(getContext());
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);

            // EditText with weight = 1 and fixed 50dp height
            EditText editText = new EditText(getContext());
            LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                    0, // Width set to 0 so weight takes effect
                    heightInPx, // Fixed height of 50dp
                    1  // Weight so it fills remaining space
            );
            editText.setLayoutParams(editTextParams);
            editText.setHint("Enter " + sentence);
            editText.setPadding(16, 16, 16, 16);
            editText.setTextSize(14f);
            editText.setBackgroundResource(R.drawable.transparent_border_black);

            // ImageView with fixed 50dp width and height
            ImageView galleryIcon = new ImageView(getContext()); // Declare inside loop
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                    heightInPx, // Width = 50dp
                    heightInPx  // Height = 50dp
            );
            iconParams.setMargins(16, 0, 0, 0);
            galleryIcon.setLayoutParams(iconParams);
            galleryIcon.setImageResource(android.R.drawable.ic_menu_gallery);
            galleryIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // Set Click Listener to ImageView
            galleryIcon.setOnClickListener(view -> {
                Toast.makeText(getActivity(), sentence, Toast.LENGTH_SHORT).show();
                openBottomSheet(galleryIcon);
            });

            // Add EditText and ImageView to horizontal row layout
            rowLayout.addView(editText);
            rowLayout.addView(galleryIcon);

            // Add TextView and rowLayout to vertical containerLayout
            containerLayout.addView(textView);
            containerLayout.addView(rowLayout);

            // Add containerLayout to the main dynamic container
            binding.dynamicContainer.addView(containerLayout);
        }
    }

    private void openBottomSheet(ImageView clickedImageView) {
        View v = getLayoutInflater().inflate(R.layout.fetch_image_layout, null);
        sheetDialog = new BottomSheetDialog(getContext());
        sheetDialog.setContentView(v);
        sheetDialog.setCancelable(true);
        sheetDialog.show();

        imgCancelDialog = v.findViewById(R.id.cancel_btn);
        ImageView cameraImg, galleryImg, deleteImg;
        cameraImg = v.findViewById(R.id.camera_img);
        deleteImg = v.findViewById(R.id.delete_img);
        galleryImg = v.findViewById(R.id.gallery_img);

        // Handle Camera Click
        cameraImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkCameraPermissions()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        dispatchTakePictureIntent(); // Capture Image
                    }
                }
                sheetDialog.dismiss();
            }
        });

        // Handle Gallery Click
        galleryImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_PICK);

                // Store the clicked image view in a global variable so we can update it later
                selectedImageView = clickedImageView;
                sheetDialog.dismiss();
            }
        });

        // Handle Delete Image Click
        deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedImageView.setImageResource(android.R.drawable.ic_menu_gallery); // Reset to default
                sheetDialog.dismiss();
            }
        });

        imgCancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sheetDialog.dismiss();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                // Handle Image Selection from Gallery
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null && selectedImageView != null) {
                    selectedImageView.setImageURI(selectedImageUri);

//                    // Convert selected image to compressed byte array
//                    byte[] compressedImage = compressImage(selectedImageUri);
//
//                    if (compressedImage != null) {
//                        List<byte[]> compressedImages = new ArrayList<>();
//                        compressedImages.add(compressedImage);
//
//                        // Upload the compressed image
//                       // uploadCompressedImages(compressedImages);
//                    }
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Handle Image Capture from Camera
                File imgFile = new File(profileImageUrl);  // Ensure profileImageUrl stores the captured file path
                if (imgFile.exists() && selectedImageView != null) {
                    selectedImageView.setImageURI(Uri.fromFile(imgFile));

//                    // Convert captured image to compressed byte array
//                    byte[] compressedImage = compressImage(Uri.fromFile(imgFile));
//
//                    if (compressedImage != null) {
//                        List<byte[]> compressedImages = new ArrayList<>();
//                        compressedImages.add(compressedImage);
//
//                        // Upload the compressed image
//                      //  uploadCompressedImages(compressedImages);
//                    }
                }
            }
        }
    }



    private void uploadCompressedImages(List<byte[]> compressedImages) {
       // int userId = sharedPrefManager.getUserId();
       // String url = String.format("https://onezed.app/api/upload/image/%s", userId);
        String url = String.format("https://onezed.app/api/gallery/upload");
        HttpHandler httpHandler = new HttpHandler(url, getContext());

        // Convert byte arrays to MultipartBody.Part
        List<MultipartBody.Part> imageParts = new ArrayList<>();
        for (int i = 0; i < compressedImages.size(); i++) {
            MultipartBody.Part part = createImagePart(compressedImages.get(i), "images[" + i + "]");
            if (part != null) {
                imageParts.add(part);
            }
        }

        // Add product_name as a key-value pair
        RequestBody productName = RequestBody.create(MediaType.parse("text/plain"), "Private Limited Company");

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
                }).uploadMultipleImages(imageParts, productName);
    }


    private MultipartBody.Part createImagePart(byte[] imageData, String partName) {
        if (imageData == null) {
            return null;
        }

        // Convert byte array to RequestBody
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), imageData);

        // Create MultipartBody.Part
        return MultipartBody.Part.createFormData(partName, "image.jpg", requestBody);
    }

    private Bitmap byteArrayToBitmap(byte[] imageData) {
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
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


    // FETCH DATA
    private void fetchAllData() {
        int count = binding.dynamicContainer.getChildCount();
        List<File> imageFiles = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<String> inputTexts = new ArrayList<>();
        Map<String, String> additionalInfo = new HashMap<>();

        for (int i = 0; i < count; i++) {
            View containerView = binding.dynamicContainer.getChildAt(i);

            if (containerView instanceof LinearLayout) {
                LinearLayout containerLayout = (LinearLayout) containerView;

                // Get Label
                TextView textView = (TextView) containerLayout.getChildAt(0);
                String label = textView.getText().toString();
                labels.add(label);

                // Get Row Layout
                LinearLayout rowLayout = (LinearLayout) containerLayout.getChildAt(1);

                // Get EditText Input
                EditText editText = (EditText) rowLayout.getChildAt(0);
                String inputText = editText.getText().toString();
                inputTexts.add(inputText);

                // Get ImageView & Convert to Bitmap
                ImageView imageView = (ImageView) rowLayout.getChildAt(1);
                Bitmap bitmap = getBitmapFromImageView(imageView);

                if (bitmap != null) {
                    File imageFile = compressAndSaveImage(bitmap, "image_" + i + ".jpg");
                    if (imageFile != null) {
                        imageFiles.add(imageFile);
                    }
                }
                additionalInfo.put(label, inputText);
            }
        }
        Log.v("additionalInput",additionalInfo.toString());

        // Call uploadCompressedImages with collected data
        if (!imageFiles.isEmpty()) {
            uploadCompressedImages(imageFiles, labels, inputTexts);
        }
    }

    public static byte[] convertImageFileToByteArray(File imageFile) {
    try {
        // Convert File to Bitmap
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(imageFile));

        // Compress Bitmap to ByteArrayOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream); // Use PNG if you need lossless compression

        // Convert ByteArrayOutputStream to byte array
        return byteArrayOutputStream.toByteArray();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return null;
}
    private void uploadCompressedImages(List<File> imageFiles, List<String> labels, List<String> inputTexts) {
        int userId = sharedPrefManager.getUserId();
        String url = "https://onezed.app/api/gallery/upload";

        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        // Add extra form fields
        multipartBuilder.addFormDataPart("product_name", GlobalVariable.inhouseComRegSelectedCategory);
        multipartBuilder.addFormDataPart("user_id", String.valueOf(userId));
        multipartBuilder.addFormDataPart("user_phone", UserProfileModel.getInstance().getMobileNo());

        // Construct JSON for additional_info
        JSONObject additionalInfoJson = new JSONObject();
        try {
            for (int i = 0; i < labels.size(); i++) {
                additionalInfoJson.put(labels.get(i), inputTexts.get(i));
            }
        } catch (JSONException e) {
            Log.e("Upload", "JSON Error: " + e.getMessage());
        }

        // Add additional_info JSON as a form field
        multipartBuilder.addFormDataPart("additional_info", additionalInfoJson.toString());

        // Compress and add images
        for (int i = 0; i < imageFiles.size(); i++) {
            File imageFile = imageFiles.get(i);
            File compressedFile = compressImage(imageFile, 1000 * 1024); // 1MB limit

            multipartBuilder.addFormDataPart("images[]", compressedFile.getName(),
                    RequestBody.create(MediaType.parse("image/jpeg"), compressedFile));

            Log.d("Upload", "Adding Compressed Image: " + compressedFile.getName());
        }

        RequestBody requestBody = multipartBuilder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Accept", "application/json")
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Upload", "Upload failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("Upload", "Upload Success: " + responseBody);

                    try {
                        JSONObject object = new JSONObject(responseBody);
                        if (object.has("code") && object.getInt("code") == 200 && object.has("success") && object.getBoolean("success")) {
                            String message = object.getString("message");

                            new Handler(Looper.getMainLooper()).post(() -> {
                                CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name), message);
                                dialog.show();
                                dialog.onPositiveButton(view ->{
                                    dialog.dismiss();
                                    CompanyRegistrationFragment secondFragment = new CompanyRegistrationFragment();
                                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                    transaction.replace(R.id.inhouse_frameLayout, secondFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                });
                            });
                        }
                    } catch (JSONException e) {
                        Log.e("Upload", "JSON Parsing error: " + e.getMessage());
                    }
                } else {
                    Log.e("Upload", "Upload Failed with code: " + response.code());
                }
            }
        });
    }

    private File compressImage(File imageFile, long maxSize) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            int quality = 100;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            do {
                stream.reset();
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
                quality -= 5;  // Reduce quality in steps of 5
            } while (stream.toByteArray().length > maxSize && quality > 5);

            // Save the compressed image to a new file
            File compressedFile = new File(imageFile.getParent(), "compressed_" + imageFile.getName());
            FileOutputStream fos = new FileOutputStream(compressedFile);
            fos.write(stream.toByteArray());
            fos.flush();
            fos.close();

            return compressedFile;
        } catch (IOException e) {
            e.printStackTrace();
            return imageFile;  // Return the original file if compression fails
        }
    }


    private File compressAndSaveImage(Bitmap bitmap, String fileName) {
        File filesDir = getContext().getCacheDir(); // Use cache directory for temporary storage
        File imageFile = new File(filesDir, fileName);

        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos); // Compress to 80% quality
            fos.flush();
            return imageFile;
        } catch (IOException e) {
            Log.e("ImageCompress", "Failed to save image: " + e.getMessage());
            return null;
        }
    }


    // Convert ImageView to Bitmap
    private Bitmap getBitmapFromImageView(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable != null) {
            // Convert vector drawable to Bitmap
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
        return null;
    }
}