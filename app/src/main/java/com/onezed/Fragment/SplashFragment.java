package com.onezed.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onezed.GlobalVariable.SharedPrefManager;
import com.onezed.R;
import com.onezed.databinding.FragmentSplashBinding;


public class SplashFragment extends Fragment {

    private FragmentSplashBinding binding;
    SharedPrefManager sharedPrefManager;
    String msg,storedPin,pin=null;
    int flag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSplashBinding.inflate(inflater, container, false);
        sharedPrefManager = new SharedPrefManager(requireActivity());
        storedPin=sharedPrefManager.getUserDetails().get("stored_pin");
        if (storedPin==pin){

            newDevice();
            flag=1;

        }else {
            oldDevice();
            flag=2;

        }
        return binding.getRoot();
    }

    private void oldDevice() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.splash_frameLayout, new LoginWithPinFragment());
        ft.commit();
    }

    private void newDevice(){

        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.splash_frameLayout, new MobileLoginFragment());
        ft.commit();



    }




}