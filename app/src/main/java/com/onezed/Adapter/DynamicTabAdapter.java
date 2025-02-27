package com.onezed.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.onezed.Fragment.PlanFragment;

import java.util.ArrayList;
import java.util.HashMap;

public class DynamicTabAdapter extends FragmentPagerAdapter {
    private int noOfTabs;
    private ArrayList<HashMap<String, String>> tabList;
    private ArrayList<ArrayList<HashMap<String, String>>> planList;
    private OnPlanClickListener onPlanClickListener;

    public DynamicTabAdapter(FragmentManager fm, int noOfTabs, ArrayList<HashMap<String, String>> tabList,
                             ArrayList<ArrayList<HashMap<String, String>>> planList, OnPlanClickListener listener) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.noOfTabs = noOfTabs;
        this.tabList = tabList;
        this.planList = planList;
        this.onPlanClickListener = listener;
    }

    @Override
    public Fragment getItem(int position) {
        return PlanFragment.newInstance(planList.get(position), onPlanClickListener);
    }

    @Override
    public int getCount() {
        return noOfTabs;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabList.get(position).get("name");
    }

    public interface OnPlanClickListener {
        void onPlanClick(String rs, String desc, String validity);
    }
}


