package com.onezed.Model;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.onezed.Activity.HomeActivity;
import com.onezed.Activity.RechargeActivity;
import com.onezed.Activity.StartActivity;
import com.onezed.GlobalVariable.CustomAlertDialog;
import com.onezed.GlobalVariable.SharedPrefManager;
import com.onezed.R;
import com.onezed.databinding.FragmentBBPSBinding;

public class BBPSViewModel extends AndroidViewModel {

    private final SharedPrefManager sharedPrefManager;
    public MutableLiveData<Boolean> isUserLoggedIn = new MutableLiveData<>();

    public BBPSViewModel(Application application) {
        super(application);
        sharedPrefManager = new SharedPrefManager(application.getApplicationContext());
        String storedPin = sharedPrefManager.getUserDetails().get("stored_pin");
        isUserLoggedIn.setValue(storedPin != null);
    }

    public void handleMobileRechargeClick(Context context) {
        if (isUserLoggedIn.getValue() != null && !isUserLoggedIn.getValue()) {
            showSignInDialog(context);
        } else {
            openRechargeActivity(context, "MOBILE_RECHARGE");
        }
    }

    public void handleElectricityBillClick(Context context) {
        if (isUserLoggedIn.getValue() != null && !isUserLoggedIn.getValue()) {
            showSignInDialog(context);
        } else {
            openRechargeActivity(context, "ELECTRICITY_BIL_PAY");
        }
    }

    public void handleBroadbandRechargeClick(Context context) {
        if (isUserLoggedIn.getValue() != null && !isUserLoggedIn.getValue()) {
            showSignInDialog(context);
        } else {
            openRechargeActivity(context, "BROADBAND_RECHARGE");
        }
    }

    public void handleMobilePostpaidRechargeClick(Context context) {
        if (isUserLoggedIn.getValue() != null && !isUserLoggedIn.getValue()) {
            showSignInDialog(context);
        } else {
            openRechargeActivity(context, "MOBILE_POSTPAID");
        }
    }
    //Gas Cylinder Book
    public void handleGasCylinderBookClick(Context context) {
        if (isUserLoggedIn.getValue() != null && !isUserLoggedIn.getValue()) {
            showSignInDialog(context);
        } else {
            openRechargeActivity(context, "GAS_CYLINDER_BOOK");
        }
    }
    //FasTag Recharge
    public void handleFasTagRechargeClick(Context context) {
        if (isUserLoggedIn.getValue() != null && !isUserLoggedIn.getValue()) {
            showSignInDialog(context);
        } else {
            openRechargeActivity(context, "FASTAG_RECHARGE");
        }
    }
    //UPI Transfer
    public void handleUpiTransferClick(Context context) {
        if (isUserLoggedIn.getValue() != null && !isUserLoggedIn.getValue()) {
            showSignInDialog(context);
        } else {
            openRechargeActivity(context, "UPI_TRANSFER");
        }
    }

    public void FaderalBankBBPSClick(Context context,String categoryType) {
        if (isUserLoggedIn.getValue() != null && !isUserLoggedIn.getValue()) {
            showSignInDialog(context);
        } else {
            openRechargeActivity(context, categoryType);
        }
    }

    public void handleBackClick(FragmentBBPSBinding binding, boolean isInvoice) {
        if (isInvoice) {
            binding.getRoot().getContext().startActivity(new Intent(binding.getRoot().getContext(), HomeActivity.class));

        } else {
            binding.getRoot().getContext().startActivity(new Intent(binding.getRoot().getContext(), HomeActivity.class));
        }
    }

    private void showSignInDialog(Context context) {
        CustomAlertDialog dialog = new CustomAlertDialog(context, R.style.WideDialog, context.getString(R.string.app_name), "You have not signed in. Sign in to continue.");
        dialog.show();
        dialog.onPositiveButton(v -> {
            dialog.dismiss();
            context.startActivity(new Intent(context, StartActivity.class));
        });
    }

    private void openRechargeActivity(Context context, String fragmentType) {
        Intent intent = new Intent(context, RechargeActivity.class);
        intent.putExtra("FRAGMENT_TYPE", fragmentType);
        intent.putExtra("FRAGMENT_PAGE", "BBPS_PAGE");
        context.startActivity(intent);
    }
}

