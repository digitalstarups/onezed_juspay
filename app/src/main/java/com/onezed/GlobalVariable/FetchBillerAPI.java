package com.onezed.GlobalVariable;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.onezed.Adapter.FaderalBillerAdapter;
import com.onezed.Model.CustomerParameterBiller;
import com.onezed.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class FetchBillerAPI {

    public List<CustomerParameterBiller> billerList = new ArrayList<>();
    public FaderalBillerAdapter billerAdapter;

    public final Activity activity;
    public final Context context;

    public FetchBillerAPI(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    public void fetchBillerApi() {

        billerAdapter = new FaderalBillerAdapter(activity, android.R.layout.simple_list_item_1, billerList);

        if (context == null || activity == null) {
            throw new IllegalStateException("Context or Activity is null. Cannot proceed.");
        }
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        //ExeDec eb=new ExeDec(electricityBiller);
        new HttpHandler("https://onezed.app/api/search-biller-category?search=Housing Society&&page=0&&pagesize=10000", context)
                .setMediaType(HttpHandler.jsonMediaType)
                .setRequestType(HttpHandler.RequestType.GET)
                .setLoading(true)
                .setHeaders(headers)
                .setOnResponse(new HttpHandler.HttpResponse() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        CustomAlertDialog dialog = new CustomAlertDialog(activity, R.style.WideDialog, "OneZed", activity.getString(R.string.no_response) );
                        dialog.show();
                        dialog.onPositiveButton(view -> dialog.dismiss());
                    }

                    @Override
                    public void onResponse(int code, String response) {
                        Log.v("FetchBillerResR",code+" "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            switch (code) {
                                case 200:

                                    billerList.clear();  // Clear the list before adding new data
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject obj = jsonArray.getJSONObject(i);
                                        // Add the name and code to the list
                                        String biller_name = obj.getString("billerName");
                                        String biller_id = obj.getString("billerId");
                                        String biller_category = obj.getString("billerCategory");

                                        CustomerParameterBiller biller = new CustomerParameterBiller();

                                        biller.setBillerName(biller_name);
                                        biller.setBillerId(biller_id);
                                        biller.setBillerCategory(biller_category);

                                        billerList.add(biller);
                                    }
                                    // Notify adapter with updated data
                                    activity.runOnUiThread(() -> billerAdapter.notifyDataSetChanged());
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
         CustomAlertDialog dialog = new CustomAlertDialog(activity, R.style.WideDialog, "OneZed", message);
         dialog.show();
         dialog.onPositiveButton(view -> dialog.dismiss());
     }
}
