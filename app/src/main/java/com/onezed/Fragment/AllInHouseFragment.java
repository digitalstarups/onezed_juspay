package com.onezed.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onezed.Activity.HomeActivity;
import com.onezed.Activity.InhouseActivity;
import com.onezed.Activity.StartActivity;
import com.onezed.GlobalVariable.CustomAlertDialog;
import com.onezed.GlobalVariable.SharedPrefManager;
import com.onezed.R;
import com.onezed.databinding.FragmentAllInHouseBinding;


public class AllInHouseFragment extends Fragment {

   private FragmentAllInHouseBinding binding;
    SharedPrefManager sharedPrefManager;
    String storedPin;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding=FragmentAllInHouseBinding.inflate(inflater, container, false);
        sharedPrefManager = new SharedPrefManager(requireActivity());
        storedPin=sharedPrefManager.getUserDetails().get("stored_pin");

        binding.businessIncorporation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (storedPin == null) {
                    CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name), "You have not Sign In. Sign In to Continue");
                    dialog.show();
                    dialog.onPositiveButton(v -> {
                        dialog.dismiss();
                        startActivity(new Intent(getContext(), StartActivity.class));

                    });
                } else {
                    //  startActivity(new Intent(HomeActivity.this, InhouseActivity.class));
                    Intent intent = new Intent(getContext(), InhouseActivity.class);
                    intent.putExtra("FRAGMENT_TYPE", "BUSINESS_REGISTRATION");
                    startActivity(intent);
                }


            }
        });
        binding.licenceRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (storedPin == null) {
                    CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name), "You have not Sign In. Sign In to Continue");
                    dialog.show();
                    dialog.onPositiveButton(v -> {
                        dialog.dismiss();
                        startActivity(new Intent(getContext(), StartActivity.class));

                    });
                } else {
                    Intent intent = new Intent(getContext(), InhouseActivity.class);
                    intent.putExtra("FRAGMENT_TYPE", "LICENCE_REGISTRATION");
                    startActivity(intent);
                }


            }
        });
        binding.brandProtection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (storedPin == null) {
                    CustomAlertDialog dialog = new CustomAlertDialog(getContext(), R.style.WideDialog, getString(R.string.app_name), "You have not Sign In. Sign In to Continue");
                    dialog.show();
                    dialog.onPositiveButton(v -> {
                        dialog.dismiss();
                        startActivity(new Intent(getContext(), StartActivity.class));

                    });
                } else {
                    Intent intent = new Intent(getContext(), InhouseActivity.class);
                    intent.putExtra("FRAGMENT_TYPE", "BRAND_PROTECTION");
                    startActivity(intent);
                }


            }
        });

        // Back press handle
        binding.btnBackImg.setOnClickListener(view -> startActivity(new Intent(getActivity(), HomeActivity.class)));

        return binding.getRoot();
    }
}