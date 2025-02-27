package com.onezed.GlobalVariable;

import android.content.Context;
import android.content.Intent;

import com.onezed.Activity.StartActivity;

public class DeRegister_Device {
    Context context;

    public DeRegister_Device(Context context) {
        this.context = context;
    }

    public void init()
    {

        SharedPrefManager sharedPrefManager=new SharedPrefManager(context);
        sharedPrefManager.clear();
        Intent intent=new Intent(context, StartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);

    }
}
