package com.onezed.Fragment;

import static com.onezed.GlobalVariable.baseUrl.termAndCondition;
import static com.onezed.GlobalVariable.baseUrl.uId;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onezed.Activity.RechargeActivity;
import com.onezed.ExeDec;
import com.onezed.R;
import com.onezed.databinding.FragmentSplashBinding;
import com.onezed.databinding.FragmentTermConditionBinding;


public class TermConditionFragment extends Fragment {

    FragmentTermConditionBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTermConditionBinding.inflate(inflater, container, false);
        binding.termAndConditionWebviewLayout.setVisibility(View.VISIBLE);
        ExeDec tc=new ExeDec(termAndCondition);
        binding.termAndConditionWebviewLayout.loadUrl(tc.getDec());

        //Back Press Handler
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.splash_frameLayout, new MobileLoginFragment());
                ft.commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);


        return binding.getRoot();
    }
}