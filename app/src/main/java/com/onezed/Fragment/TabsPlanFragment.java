package com.onezed.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.onezed.Adapter.DynamicTabAdapter;
import com.onezed.Adapter.PlanAdapter;
import com.onezed.Adapter.RechargePlanAdapter;
import com.onezed.Adapter.RechargeTabAdapter;
import com.onezed.R;

import java.util.ArrayList;
import java.util.HashMap;

public class TabsPlanFragment extends Fragment {

    private static final String ARG_PLAN_LIST = "planList";
    private ArrayList<HashMap<String, String>> planList;
    private RechargeTabAdapter.OnPlanClickListener onPlanClickListener;

    public static TabsPlanFragment newInstance(ArrayList<HashMap<String, String>> planList, RechargeTabAdapter.OnPlanClickListener listener) {
        TabsPlanFragment fragment = new TabsPlanFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PLAN_LIST, planList);
        fragment.setArguments(args);
        fragment.onPlanClickListener = listener;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plan, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        if (getArguments() != null) {
            planList = (ArrayList<HashMap<String, String>>) getArguments().getSerializable(ARG_PLAN_LIST);
        }

        // Set up the RecyclerView with planList data
        RechargePlanAdapter adapter = new RechargePlanAdapter(planList, onPlanClickListener);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }
}
