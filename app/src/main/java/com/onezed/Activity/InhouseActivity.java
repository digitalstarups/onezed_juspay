package com.onezed.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.onezed.Fragment.AccountEfilingFragment;
import com.onezed.Fragment.AllInHouseFragment;
import com.onezed.Fragment.BrandProtectionFragment;
import com.onezed.Fragment.BroadbandRechargeFragment;
import com.onezed.Fragment.CompanyRegistrationFragment;
import com.onezed.Fragment.FetchBillerFragment;
import com.onezed.Fragment.LicenceRegFragment;
import com.onezed.Fragment.MobilePostPaidFragment;
import com.onezed.Fragment.MobileRechargeFragment;
import com.onezed.databinding.ActivityInhouseBinding;

public class InhouseActivity extends AppCompatActivity {

    private ActivityInhouseBinding binding;
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityInhouseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // Get the intent and fragment type
        Intent intent = getIntent();
        String fragmentType = intent.getStringExtra("FRAGMENT_TYPE");

        if (savedInstanceState == null) {
            // Only replace fragments if there's no saved instance state
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            if ("BUSINESS_REGISTRATION".equals(fragmentType)) {
                ft.replace(binding.inhouseFrameLayout.getId(), new CompanyRegistrationFragment());
            }
            else if ("LICENCE_REGISTRATION".equals(fragmentType)) {
                ft.replace(binding.inhouseFrameLayout.getId(), new LicenceRegFragment());
            }
//            else if ("MORE_ACCOUNTING".equals(fragmentType)) {
//                ft.replace(binding.inhouseFrameLayout.getId(), new AllInHouseFragment());
//            }
            else if ("BRAND_PROTECTION".equals(fragmentType)) {
                ft.replace(binding.inhouseFrameLayout.getId(), new BrandProtectionFragment());
            }
            else if ("ACCOUNTING_EFINING".equals(fragmentType)) {
                ft.replace(binding.inhouseFrameLayout.getId(), new AccountEfilingFragment());
            }


            ft.commit();
        }

    }
}