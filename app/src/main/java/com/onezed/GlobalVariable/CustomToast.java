package com.onezed.GlobalVariable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.onezed.R;

public class CustomToast {


    public static void showToast(Context context, String message) {
        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View customToastView = inflater.inflate(R.layout.custom_toast_layout, null);

        // Set the icon and message
        ImageView toastIcon = customToastView.findViewById(R.id.toast_icon);
        TextView toastMessage = customToastView.findViewById(R.id.toast_message);

        toastIcon.setImageResource(R.drawable.onezed_app_logo); // Set your custom icon here
        toastMessage.setText(message);

        // Create and show the Toast
        Toast toast = new Toast(context);
        toast.setView(customToastView);
        toast.setDuration(Toast.LENGTH_LONG); // You can set duration here
        toast.show();
    }
}

