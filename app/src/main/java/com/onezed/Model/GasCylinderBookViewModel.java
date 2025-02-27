package com.onezed.Model;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.onezed.GlobalVariable.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class GasCylinderBookViewModel extends ViewModel {

    private MutableLiveData<List<GasProviderModel>> gasProviderList = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public LiveData<List<GasProviderModel>> getGasProviderList() {
        return gasProviderList;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void fetchGasProviderApi(Context context) {
        isLoading.setValue(true);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        new HttpHandler("https://onezed.app/api/searchProvider?search=gas",context /* context */)
                .setMediaType(HttpHandler.jsonMediaType)
                .setRequestType(HttpHandler.RequestType.GET)
                .setHeaders(headers)
                .setOnResponse(new HttpHandler.HttpResponse() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        errorMessage.postValue("No response");
                        isLoading.postValue(false);
                    }

                    @Override
                    public void onResponse(int code, String response) {
                        isLoading.postValue(false);
                        try {
                            if (code == 200) {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray billerArray = jsonObject.getJSONArray("gas");
                                List<GasProviderModel> gasProviders = new ArrayList<>();
                                for (int i = 0; i < billerArray.length(); i++) {
                                    JSONObject obj = billerArray.getJSONObject(i);
                                    String serviceProvider = obj.getString("service_provider");
                                    if (serviceProvider != null && serviceProvider.contains("Gas")) {
                                        GasProviderModel biller = new GasProviderModel();
                                        biller.setServiceProvider(serviceProvider);
                                        biller.setSpKey(obj.getString("sp_key"));
                                        biller.setCategory(obj.getString("category"));
                                        biller.setImage(obj.getString("image"));
                                        gasProviders.add(biller);
                                    }
                                }
                                gasProviderList.postValue(gasProviders);
                            } else {
                                errorMessage.postValue("Something went wrong.");
                            }
                        } catch (JSONException e) {
                            errorMessage.postValue("Error parsing response.");
                        }
                    }
                }).sendRequest();
    }
}

