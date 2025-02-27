package com.onezed.GlobalVariable;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.onezed.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpHandler {
    static OkHttpClient client = new OkHttpClient();
    public static MediaType jsonMediaType = MediaType.parse("application/json; charset=utf-8");
    public static MediaType soapMediaType = MediaType.parse("application/soap+xml; charset=utf-8");
    public static MediaType stringType = MediaType.parse("text/html");

    private String url = null;
    private String stringData = null;
    private RequestBody requestBody = null;  // Already added for handling Multipart
    private RequestType requestType;
    private long connectionTimeOut = 100000;
    private long readTimeOut = 100000;

    private Context context;
    private HttpResponse httpResponse;
    private boolean loading;
    private LoadingDialog ld = null;
    private Map<String, String> headers = new HashMap<>();
    private MediaType mediaType;

    public void uploadMultipleImages(List<MultipartBody.Part> images, RequestBody productName) {
        OkHttpClient client = new OkHttpClient();

        // Build Multipart request
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(productName);  // Add the product name as a form field

        // Add all image parts
        for (MultipartBody.Part image : images) {
            builder.addPart(image);
        }

        // Build the request
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(this.url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (httpResponse != null) {
                    httpResponse.onFailure(call, e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (httpResponse != null) {
                    httpResponse.onResponse(response.code(), response.body().string());
                }
            }
        });
    }


    public enum RequestType {
        POST, GET, PUT, DELETE, SOAP
    }

    public interface HttpResponse {
        void onFailure(Call call, IOException e);
        void onResponse(int code, String response);
    }

    public HttpHandler(String url, Context context) {
        this.url = url;
        this.context = context;
    }

    public HttpHandler setStringData(String stringData) {
        this.stringData = stringData;
        return this;
    }

    public HttpHandler setRequestBody(RequestBody requestBody) {  // Already added to accept multipart/form-data
        this.requestBody = requestBody;
        return this;
    }

    public HttpHandler setLoading(boolean loading) {
        this.loading = loading;
        return this;
    }

    public HttpHandler setOnResponse(HttpResponse onResponse) {
        this.httpResponse = onResponse;
        return this;
    }

    public HttpHandler setRequestType(RequestType requestType) {
        this.requestType = requestType;
        return this;
    }

    public HttpHandler setConnectionTimeout(long timeout) {
        this.connectionTimeOut = timeout;
        return this;
    }

    public HttpHandler setReadTimeOut(long timeout) {
        this.readTimeOut = timeout;
        return this;
    }

    public HttpHandler setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public HttpHandler setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    public HttpHandler sendRequest() {
        if (loading) {
            ld = new LoadingDialog(context, androidx.appcompat.R.style.Theme_AppCompat_Dialog);
            ld.show();
        }

        Request.Builder rb = new Request.Builder();
        RequestBody body = null;

        // If we are sending multipart/form-data, use the requestBody (added for images)
        if (requestBody != null) {
            body = requestBody;
        } else if (stringData != null && mediaType != null) {
            body = RequestBody.create(mediaType, stringData);
        }

        client = client.newBuilder()
                .connectTimeout(connectionTimeOut, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
                .build();
        rb.url(url);

        switch (requestType) {
            case POST:
                rb.post(body);
                break;
            case GET:
                rb.get();
                break;
            case PUT:
                rb.put(body);
                break;
            case DELETE:
                rb.delete(body);
                break;
            case SOAP:
                rb.post(body);
                break;
        }

        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                rb.header(entry.getKey(), entry.getValue());
            }
        }

        Request request = rb.build();
        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final Call call, IOException e) {
                        ((Activity) context).runOnUiThread(() -> {
                            if (loading) {
                                ld.dismiss();
                            }
                            final CustomAlertDialog dialog = new CustomAlertDialog(context, R.style.WideDialog, context.getString(R.string.app_name), "No response from server, do you want retry?");
                            dialog.show();
                            dialog.onPositiveButton(view -> {
                                if (loading) {
                                    ld.show();
                                }
                                call.clone().enqueue(this);
                                dialog.dismiss();
                            });
                            dialog.onNegativeButton(view -> dialog.dismiss());
                            httpResponse.onFailure(call, e);
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String res = response.body().string();
                        final int resCode = response.code();
                        final String contentType = response.header("Content-Type");

                        ((Activity) context).runOnUiThread(() -> {
                            if (loading) {
                                ld.dismiss();
                            }

                            switch (resCode) {
                                case 200:
                                    if (contentType != null && (contentType.contains("application/json") || contentType.contains("text/html") || contentType.contains("text/plain"))) {
                                        if (!res.trim().isEmpty()) {
                                            httpResponse.onResponse(resCode, res);
                                        } else {
                                            Toast.makeText(context, "Null response from server", Toast.LENGTH_SHORT).show();
                                        }
                                    } else if (contentType != null && contentType.contains("application/soap+xml")) {
                                        httpResponse.onResponse(resCode, res);
                                    } else {
                                        Toast.makeText(context, "Response format error", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case 400:
                                case 404:
                                case 422:
                                    if (contentType != null && (contentType.contains("application/json") || contentType.contains("text/html") || contentType.contains("text/plain"))) {
                                        if (!res.trim().isEmpty()) {
                                            httpResponse.onResponse(resCode, res);
                                        } else {
                                            Toast.makeText(context, "Null response from server", Toast.LENGTH_SHORT).show();
                                        }
                                    } else if (contentType != null && contentType.contains("application/soap+xml")) {
                                        httpResponse.onResponse(resCode, res);
                                    } else {
                                        Toast.makeText(context, "Response format error", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case 401:
                                    Toast.makeText(context, "Unauthorized", Toast.LENGTH_SHORT).show();
                                    break;
                                case 403:
                                    Toast.makeText(context, "Forbidden", Toast.LENGTH_SHORT).show();
                                    break;
                                case 500:
                                    Toast.makeText(context, "Server Error", Toast.LENGTH_SHORT).show();
                                    break;
                                case 503:
                                    Toast.makeText(context, "Service Unavailable", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    if (contentType != null && (contentType.contains("application/json") || contentType.contains("text/html") || contentType.contains("text/plain"))) {
                                        if (!res.trim().isEmpty()) {
                                            httpResponse.onResponse(resCode, res);
                                        } else {
                                            Toast.makeText(context, "Null response from server", Toast.LENGTH_SHORT).show();
                                        }
                                    } else if (contentType != null && contentType.contains("application/soap+xml")) {
                                        httpResponse.onResponse(resCode, res);
                                    } else {
                                        Toast.makeText(context, "Response format error", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                            }
                        });
                    }
                });
        return this;
    }

    // Method to upload an image
    public HttpHandler uploadImage(Bitmap bitmap) {
        // Convert bitmap to a file
        File imageFile = bitmapToFile(bitmap);
        if (imageFile == null) {
            Toast.makeText(context, "Failed to convert bitmap to file", Toast.LENGTH_SHORT).show();
            return this; // Return without making a request
        }

        // Prepare the request body
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("profile_img", imageFile.getName(), requestFile);

        // Prepare the request
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(body)
                .build();

        // Send the request using the existing sendRequest method
        this.setRequestBody(requestBody)
                .setRequestType(RequestType.POST)
                .sendRequest();

        return this;
    }

    // Convert bitmap to a file
    private File bitmapToFile(Bitmap bitmap) {
        File imageFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "profile_img_" + System.currentTimeMillis() + ".jpg");
        try (FileOutputStream out = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // Compress the bitmap
            return imageFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Return null if an error occurs
        }
    }
}

class LoadingDialog extends Dialog {
        private TextView messageText;
        private String message;

        public LoadingDialog(@NonNull Context context, int themeResId) {
            super(context, themeResId);
        }

        @Override
        public void show() {
            super.show();
            setContentView(R.layout.fragment_loading);
            messageText = findViewById(R.id.msg);
            messageText.setText(message);
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            setCancelable(false);
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

