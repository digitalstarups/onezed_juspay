package com.onezed.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onezed.R;
import com.onezed.databinding.FragmentFetchBillerBinding;
import com.onezed.databinding.FragmentUPITransferBinding;


public class UPITransferFragment extends Fragment {


    private FragmentUPITransferBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentUPITransferBinding.inflate(inflater, container, false);





        return binding.getRoot();
    }
}