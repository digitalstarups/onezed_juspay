package com.onezed.Fragment;

import static com.onezed.GlobalVariable.baseUrl.appointment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.net.ParseException;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.snackbar.Snackbar;
import com.onezed.Activity.HomeActivity;
import com.onezed.Adapter.TransactionHistoryAdapter;
import com.onezed.ExeDec;
import com.onezed.GlobalVariable.CustomAlertDialog;
import com.onezed.GlobalVariable.HttpHandler;
import com.onezed.GlobalVariable.SharedPrefManager;
import com.onezed.Model.TransactionHistoryModel;
import com.onezed.Model.UserProfileModel;
import com.onezed.R;
import com.onezed.databinding.FragmentTransactionHistoryBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


public class TransactionHistoryFragment extends Fragment implements TransactionHistoryAdapter.OnTransactionClickListener{

    private FragmentTransactionHistoryBinding binding;
    private TransactionHistoryAdapter adapter;
    private List<TransactionHistoryModel> transactionList;
    SharedPrefManager sharedPrefManager;
    int userId;
    boolean isInvoice=false;
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTransactionHistoryBinding.inflate(inflater, container, false);
        sharedPrefManager = new SharedPrefManager(requireActivity());
        userId=sharedPrefManager.getUserId();
        TransactionHistoryApi();
        binding.transactionHistoryRv.setVisibility(View.VISIBLE);
        binding.invoiceLayout.setVisibility(View.GONE);
        // Initialize RecyclerView

        binding.transactionHistoryRv.setLayoutManager(new LinearLayoutManager(getContext()));

        simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(getContext(), "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
               // Toast.makeText(getContext(), "deleted successfully ", Toast.LENGTH_SHORT).show();
                //Remove swiped item from list and notify the RecyclerView
                int position = viewHolder.getAdapterPosition();
                transactionList.remove(position);
                adapter.notifyDataSetChanged();
                // Show the custom Snackbar
                showCustomSnackbar(viewHolder.itemView);

            }
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                // Set red background
                Paint paint = new Paint();
                paint.setColor(Color.RED);
                RectF background = new RectF(
                        viewHolder.itemView.getRight() + dX, viewHolder.itemView.getTop(),
                        viewHolder.itemView.getRight(), viewHolder.itemView.getBottom()
                );
                c.drawRect(background, paint);

                // Draw the delete icon
                Drawable icon = ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_delete); // Replace with your icon
                if (icon != null) {
                    int iconMargin = (viewHolder.itemView.getHeight() - icon.getIntrinsicHeight()) / 4; // Adjust icon margin
                    int iconTop = viewHolder.itemView.getTop() + iconMargin;
                    int iconBottom = iconTop + icon.getIntrinsicHeight();
                    int iconLeft = viewHolder.itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                    int iconRight = viewHolder.itemView.getRight() - iconMargin;

                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    icon.draw(c);

                    // Draw "Delete" text below the icon
                    paint.setColor(Color.WHITE);
                    paint.setTextSize(40);
                    paint.setTextAlign(Paint.Align.CENTER);

                    float textX = (iconLeft + iconRight) / 2;  // Center the text below the icon
                    float textY = iconBottom + 50; // Adjust this value to control the distance between icon and text
                    c.drawText("Delete", textX, textY, paint);
                }
            }
        };


       // ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
      //  itemTouchHelper.attachToRecyclerView(binding.transactionHistoryRv);

        //Back Press Handler
        binding.btnBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInvoice){
                    isInvoice=false;
                    binding.transactionHistoryRv.setVisibility(View.VISIBLE);
                    binding.invoiceLayout.setVisibility(View.GONE);
                    binding.shareImg.setVisibility(View.GONE);
                } else  {
                    startActivity(new Intent(getActivity(), HomeActivity.class));
                }

            }
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
               // startActivity(new Intent(getActivity(), HomeActivity.class));
                if (isInvoice){
                    isInvoice=false;
                    binding.transactionHistoryRv.setVisibility(View.VISIBLE);
                    binding.invoiceLayout.setVisibility(View.GONE);
                    binding.shareImg.setVisibility(View.GONE);
                } else  {
                    startActivity(new Intent(getActivity(), HomeActivity.class));
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);



        return binding.getRoot();
    }

    private void showCustomSnackbar(View view) {
        // Create a Snackbar
        Snackbar snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG);

        // Inflate custom layout
        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        LayoutInflater inflater = LayoutInflater.from(view.getContext());
        View customView = inflater.inflate(R.layout.custom_snackbar_layout, null);

        // Customize the icon and text
        ImageView icon = customView.findViewById(R.id.snackbar_icon);
        TextView text = customView.findViewById(R.id.snackbar_text);
       // icon.setImageResource(R.drawable.ic_baseline_done);  // Set your custom icon
        text.setText("History deleted successfully ");        // Set your custom text

        // Remove the default text from the Snackbar
        snackbarLayout.removeAllViews();

        // Add the custom layout to the Snackbar
        snackbarLayout.addView(customView);

        // Show the Snackbar
        snackbar.show();
    }

    private void TransactionHistoryApi() {

        Map<String, String> headers = new HashMap<>();


        ExeDec appoint=new ExeDec(appointment);
        String url = String.format("https://onezed.app/api/transaction?phone=%s", UserProfileModel.getInstance().getMobileNo());
        Log.v("Url",url);
        new HttpHandler(url, getContext())
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
                        Log.v("Response", code + " " + response);
                        try {
                            Object json = new JSONTokener(response).nextValue(); // Check if response is JSONArray or JSONObject

                            switch (code) {
                                case 200:
                                    transactionList = new ArrayList<>();
                                    if (json instanceof JSONArray) {
                                        JSONArray jsonArray = (JSONArray) json;
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject obj = jsonArray.getJSONObject(i);
                                            String paymentId = obj.getString("payment_id");
                                            String amount = obj.getString("amount");
                                            String status = obj.getString("status");
                                            String contact = obj.getString("contact");
                                            String date = obj.getString("created_at");

                                            String modifyDate = formatDate(date);
                                            TransactionHistoryModel historyModel = new TransactionHistoryModel();
                                            historyModel.setStatus(status);
                                            historyModel.setTransactionId(paymentId);
                                            historyModel.setAmount(amount);
                                            historyModel.setDate(modifyDate);
                                            transactionList.add(historyModel);
                                        }
                                    } else if (json instanceof JSONObject) {
                                        JSONObject jsonObject = (JSONObject) json;
                                        // Handle JSONObject response here
                                        String paymentId = jsonObject.getString("payment_id");
                                        String amount = jsonObject.getString("amount");
                                        String status = jsonObject.getString("status");
                                        String contact = jsonObject.getString("contact");
                                        String date = jsonObject.getString("created_at");

                                        String modifyDate = formatDate(date);
                                        TransactionHistoryModel historyModel = new TransactionHistoryModel();
                                        historyModel.setStatus(status);
                                        historyModel.setTransactionId(paymentId);
                                        historyModel.setAmount(amount);
                                        historyModel.setDate(modifyDate);
                                        transactionList.add(historyModel);
                                    }

                                    // Set adapter
                                    adapter = new TransactionHistoryAdapter(transactionList, TransactionHistoryFragment.this);
                                    binding.transactionHistoryRv.setAdapter(adapter);
                                    break;

                                default:
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.has("message")) {
                                        CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", jsonObject.getString("message"));
                                        dialog.show();
                                        dialog.onPositiveButton(view -> {
                                            dialog.dismiss();
                                            startActivity(new Intent(getActivity(), HomeActivity.class));
                                        });
                                    } else {
                                        CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, "OneZed", "Something went wrong, please try again later.");
                                        dialog.show();
                                        dialog.onPositiveButton(view -> {
                                            dialog.dismiss();
                                            startActivity(new Intent(getActivity(), HomeActivity.class));
                                        });
                                    }
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }).sendRequest();
    }

    public static String formatDate(String originalDate) {
        // Original format with milliseconds and Z (timezone)
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
        // Desired format
        SimpleDateFormat desiredFormat = new SimpleDateFormat("yyyy-MM-dd h:mm:ss");

        try {
            // Parse the original date
            Date date = originalFormat.parse(originalDate);
            // Return the formatted date
            return desiredFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onTransactionClick(TransactionHistoryModel transaction) {
        //binding.appBarLayout.setVisibility(View.GONE);
        isInvoice=true;
        binding.transactionHistoryRv.setVisibility(View.GONE);
        binding.invoiceLayout.setVisibility(View.VISIBLE);
        binding.shareImg.setVisibility(View.VISIBLE);

        binding.shareImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap =getBitmapFromView(binding.invoiceLayout);
                shareImage(bitmap);

            }
        });


        String transactionId = transaction.getTransactionId();
        binding.orderId.setText(transaction.getTransactionId());
        binding.date.setText(transaction.getDate());
        binding.amount.setText("\u20B9  "+transaction.getAmount());
        binding.userName.setText(UserProfileModel.getInstance().getName());
        binding.userMobile.setText(UserProfileModel.getInstance().getMobileNo());
        binding.userEmail.setText(UserProfileModel.getInstance().getEmail());

//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
//        itemTouchHelper.attachToRecyclerView(transaction);
    }
    private Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }
    private void shareImage(Bitmap bitmap){
        // save bitmap to cache directory
        try {
            File cachePath = new File(getActivity().getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        File imagePath = new File(getActivity().getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(getActivity(), "com.onezed.provider", newFile);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, getActivity().getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.setType("image/png");
            startActivity(Intent.createChooser(shareIntent, "Choose an app"));
        }
    }
}