package com.onezed.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.onezed.Fragment.BBPSFragment;
import com.onezed.Fragment.BroadBandFragment;
import com.onezed.Fragment.BroadbandRechargeFragment;
import com.onezed.Fragment.CableTvBBPS;
import com.onezed.Fragment.ClubAndAssociationsBBPS;
import com.onezed.Fragment.CreditCardBBPS;
import com.onezed.Fragment.DthBBPS;
import com.onezed.Fragment.EducationFeesBBPS;
import com.onezed.Fragment.ElectricityFragment;
import com.onezed.Fragment.FasTagRechargeFragment;
import com.onezed.Fragment.FastagBBPS;
import com.onezed.Fragment.FetchBillerFragment;
import com.onezed.Fragment.GasBBPS;
import com.onezed.Fragment.GasCylinderBookFragment;
import com.onezed.Fragment.HealthInsuranceBBPS;
import com.onezed.Fragment.HospitalBBPS;
import com.onezed.Fragment.HousingSocietyBBPS;
import com.onezed.Fragment.InsuranceBBPS;
import com.onezed.Fragment.LPGGasBBPS;
import com.onezed.Fragment.LandlineBBPS;
import com.onezed.Fragment.LifeInsuranceBBPS;
import com.onezed.Fragment.LoanRepaymentBBPS;
import com.onezed.Fragment.MetroRechargeBBPS;
import com.onezed.Fragment.MobilePostPaidFragment;
import com.onezed.Fragment.MobilePostpaidBBPS;
import com.onezed.Fragment.MobilePrepaidBBPS;
import com.onezed.Fragment.MobileRechargeFragment;
import com.onezed.Fragment.MunicipalServicesBBPS;
import com.onezed.Fragment.MunicipalTaxesBBPS;
import com.onezed.Fragment.NCMCRechargeBBPS;
import com.onezed.Fragment.NationalPensionSystemBBPS;
import com.onezed.Fragment.PrepaidMeterBBPS;
import com.onezed.Fragment.RecurringDepositBBPS;
import com.onezed.Fragment.RentalBBPS;
import com.onezed.Fragment.SubscriptionBBPS;
import com.onezed.Fragment.TransactionHistoryFragment;
import com.onezed.Fragment.UPITransferFragment;
import com.onezed.Fragment.ViewProfile;
import com.onezed.Fragment.WaterBBPS;
import com.onezed.R;
import com.onezed.databinding.ActivityRechargeBinding;


public class RechargeActivity extends AppCompatActivity implements BroadbandRechargeFragment.OnDataPass{
    private ActivityRechargeBinding binding;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRechargeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // Get the intent and fragment type
        Intent intent = getIntent();
        String fragmentType = intent.getStringExtra("FRAGMENT_TYPE");
        String fragmentPage = intent.getStringExtra("FRAGMENT_PAGE");
        Log.v("fragmentType",fragmentType+" "+fragmentPage);
        if (savedInstanceState == null) {
            // Only replace fragments if there's no saved instance state
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            if ("MOBILE_RECHARGE".equals(fragmentType)) {

                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new MobileRechargeFragment();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new MobileRechargeFragment());
                }
            }
            else if ("ELECTRICITY_BIL_PAY".equals(fragmentType)) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new FetchBillerFragment();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new FetchBillerFragment());
                }

            }
            //ELECTRICITY

           // else if ("Electricity".equals(fragmentType)) {
            else if (fragmentType.toLowerCase().contains("electricity")) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new ElectricityFragment();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new ElectricityFragment());
                }

            }

            // LPG GAS
            else if ("LPG Gas".equals(fragmentType)) {
                // else if (fragmentType.contains("Broadband")) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new LPGGasBBPS();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new LPGGasBBPS());
                }

            }
            // GAS
            //else if (fragmentType.toLowerCase().contains("lpg gas")) {
            else if (fragmentType.contains("Gas")) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new GasBBPS();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new GasBBPS());
                }

            }

            // WATER
            else if (fragmentType.toLowerCase().contains("water")) {
                // else if (fragmentType.contains("Broadband")) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new WaterBBPS();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new WaterBBPS());
                }

            }

            // BroadBand

           // else if ("Broadband Postpaid".equals(fragmentType)) {
            else if (fragmentType.toLowerCase().contains("broadband")) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new BroadBandFragment();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new BroadBandFragment());
                }

            }

            // Mobile Postpaid
            else if ("Mobile Postpaid".equals(fragmentType)) {
                // else if (fragmentType.contains("Broadband")) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new MobilePostpaidBBPS();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new MobilePostpaidBBPS());
                }

            }

            // LANDLINE POSTPAID
            else if (fragmentType.toLowerCase().contains("landline postpaid")) {
                // else if (fragmentType.contains("Broadband")) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new LandlineBBPS();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new LandlineBBPS());
                }

            }
            // CABLE TV
            else if (fragmentType.toLowerCase().contains("cable tv")) {
                // else if (fragmentType.contains("Broadband")) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new CableTvBBPS();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new CableTvBBPS());
                }

            }
            // DTH
            else if (fragmentType.toLowerCase().contains("dth")) {
                // else if (fragmentType.contains("Broadband")) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new DthBBPS();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new DthBBPS());
                }

            }

            // FASTAG
            else if (fragmentType.toLowerCase().contains("fastag")) {
                // else if (fragmentType.contains("Broadband")) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new FastagBBPS();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new FastagBBPS());
                }

            }
            // LOAN REPAYMENT
            // else if (fragmentType.toLowerCase().contains("loan repayment")) {
            else if (fragmentType.contains("Loan Repayment")) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new LoanRepaymentBBPS();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new LoanRepaymentBBPS());
                }

            }

            // INSURANCE
           // else if (fragmentType.toLowerCase().contains("insurance")) {
            else if ("Insurance".equals(fragmentType)) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new InsuranceBBPS();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new InsuranceBBPS());
                }

            }

            // INSURANCE
            // else if (fragmentType.toLowerCase().contains("insurance")) {
            else if ("Health Insurance".equals(fragmentType)) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new HealthInsuranceBBPS();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new HealthInsuranceBBPS());
                }

            }
            // RECURRING DEPOSIT
            else if (fragmentType.toLowerCase().contains("recurring deposit")) {
                // else if (fragmentType.contains("Broadband")) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new RecurringDepositBBPS();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new RecurringDepositBBPS());
                }

            }

            // EDUCATION FEES
            else if (fragmentType.toLowerCase().contains("education fees")) {
                // else if (fragmentType.contains("Broadband")) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new EducationFeesBBPS();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new EducationFeesBBPS());
                }

            }
            // CREDIT CARD
            else if (fragmentType.toLowerCase().contains("credit card")) {
                // else if (fragmentType.contains("Broadband")) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new CreditCardBBPS();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new CreditCardBBPS());
                }

            }
            // HOUSING SOCIETY
            else if (fragmentType.toLowerCase().contains("housing society")) {
                // else if (fragmentType.contains("Broadband")) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new HousingSocietyBBPS();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new HousingSocietyBBPS());
                }

            }
            //SUBSCRIPTION
            else if (fragmentType.toLowerCase().contains("subscription")) {
                // else if (fragmentType.contains("Broadband")) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new SubscriptionBBPS();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new SubscriptionBBPS());
                }

            }
            //MOBILE PREPAID
            else if (fragmentType.toLowerCase().contains("mobile prepaid")) {
                // else if (fragmentType.contains("Broadband")) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new MobilePrepaidBBPS();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new MobilePrepaidBBPS());
                }

            }
            //Clubs and Associations
            else if (fragmentType.toLowerCase().contains("clubs and associations")) {
                // else if (fragmentType.contains("Broadband")) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new ClubAndAssociationsBBPS();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new ClubAndAssociationsBBPS());
                }

            }
            //LPG Gas
            else if (fragmentType.toLowerCase().contains("lpg gas")) {
                // else if (fragmentType.contains("Broadband")) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new LPGGasBBPS();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new LPGGasBBPS());
                }

            }

            // LIFE INSURANCE
            else if (fragmentType.toLowerCase().contains("life insurance")) {
                // else if (fragmentType.contains("Broadband")) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new LifeInsuranceBBPS();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new LifeInsuranceBBPS());
                }

            }

            // NATIONAL PAYMENT SYSTEM
            else if (fragmentType.toLowerCase().contains("national pension system")) {
                // else if (fragmentType.contains("Broadband")) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new NationalPensionSystemBBPS();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new NationalPensionSystemBBPS());
                }

            }

            // HOSPITAL
            else if (fragmentType.toLowerCase().contains("hospital")) {
                // else if (fragmentType.contains("Broadband")) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new HospitalBBPS();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new HospitalBBPS());
                }

            }
            // HOSPITAL
            else if (fragmentType.toLowerCase().contains("municipal taxes")) {
                // else if (fragmentType.contains("Broadband")) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new MunicipalTaxesBBPS();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new MunicipalTaxesBBPS());
                }

            }
            // RENTAL
            else if (fragmentType.toLowerCase().contains("rental")) {
                // else if (fragmentType.contains("Broadband")) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new RentalBBPS();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new RentalBBPS());
                }

            }

            // Municipal Services
            else if (fragmentType.toLowerCase().contains("municipal services")) {
                // else if (fragmentType.contains("Broadband")) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new MunicipalServicesBBPS();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new MunicipalServicesBBPS());
                }

            }

            // Prepaid Meter
            else if (fragmentType.toLowerCase().contains("prepaid meter")) {
                // else if (fragmentType.contains("Broadband")) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new PrepaidMeterBBPS();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new PrepaidMeterBBPS());
                }

            }

            // Metro Recharge
            else if (fragmentType.toLowerCase().contains("metro recharge")) {
                // else if (fragmentType.contains("Broadband")) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new MetroRechargeBBPS();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new MetroRechargeBBPS());
                }

            }

            // NCMC Recharge
            else if (fragmentType.toLowerCase().contains("ncmc recharge")) {
                // else if (fragmentType.contains("Broadband")) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new NCMCRechargeBBPS();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new NCMCRechargeBBPS());
                }

            }



            else if ("BROADBAND_RECHARGE".equals(fragmentType)) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new BroadbandRechargeFragment();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new BroadbandRechargeFragment());
                }


            }
            else if ("MOBILE_POSTPAID".equals(fragmentType)) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new MobilePostPaidFragment();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new MobilePostPaidFragment());
                }



            }
            else if ("GAS_CYLINDER_BOOK".equals(fragmentType)) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new GasCylinderBookFragment();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new GasCylinderBookFragment());
                }
            }
            else if ("FASTAG_RECHARGE".equals(fragmentType)) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new FasTagRechargeFragment();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new FasTagRechargeFragment());
                }
            }
            else if ("UPI_TRANSFER".equals(fragmentType)) {
                if ("BBPS_PAGE".equals(fragmentPage)){
                    Bundle bundle = new Bundle();
                    bundle.putString("PAGE", "BBPS");

                    Fragment exampleFragment = new UPITransferFragment();
                    exampleFragment.setArguments(bundle);

                    // Load the fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recharge_frameLayout, exampleFragment)
                            .commit();
                }else {
                    ft.replace(binding.rechargeFrameLayout.getId(), new UPITransferFragment());
                }



            }
            else if ("BBPS_RECHARGE".equals(fragmentType)) {
//                if ("BBPS_PAGE".equals(fragmentPage)){
//                    Bundle bundle = new Bundle();
//                    bundle.putString("PAGE", "BBPS");
//
//                    Fragment exampleFragment = new BBPSFragment();
//                    exampleFragment.setArguments(bundle);
//
//                    // Load the fragment
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.recharge_frameLayout, exampleFragment)
//                            .commit();
//                }else {
//                    ft.replace(binding.rechargeFrameLayout.getId(), new BBPSFragment());
//                }

                ft.replace(binding.rechargeFrameLayout.getId(), new BBPSFragment());
            }
            else if ("TRANSACTION_HISTORY".equals(fragmentType)) {
                ft.replace(binding.rechargeFrameLayout.getId(), new TransactionHistoryFragment());

            } else if ("VIEW_PROFILE".equals(fragmentType)) {
                ft.replace(binding.rechargeFrameLayout.getId(), new ViewProfile());

            }


            ft.commit();
        }
    }

    @Override
    public void onDataPass(String type,String consumerId, String spKey, String amount) {

        Intent intent = new Intent(RechargeActivity.this, BillPayActivity.class);
        intent.putExtra("Type", type);
        intent.putExtra("consumerId", consumerId);
        intent.putExtra("spKey", spKey);
        intent.putExtra("amount", amount);
        startActivity(intent);  // Start ActivityB and pass the data
    }
}
