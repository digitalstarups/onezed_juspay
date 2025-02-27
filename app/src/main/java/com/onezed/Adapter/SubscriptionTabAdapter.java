package com.onezed.Adapter;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.onezed.Fragment.RechargePlanFragment;
import com.onezed.Fragment.SubscriptionPlanFragment;

import java.util.ArrayList;
import java.util.HashMap;


public class SubscriptionTabAdapter extends FragmentPagerAdapter {

    private int noOfTabs;
    private ArrayList<HashMap<String, String>> tabList;
    private ArrayList<ArrayList<HashMap<String, String>>> planList;
    private SubscriptionTabAdapter.OnPlanClickListener onPlanClickListener;
    public SubscriptionTabAdapter(FragmentManager fm, int noOfTabs, ArrayList<HashMap<String, String>> tabList,
                                  ArrayList<ArrayList<HashMap<String, String>>> planList, SubscriptionTabAdapter.OnPlanClickListener listener) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.noOfTabs = noOfTabs;
        this.tabList = tabList;
        this.planList = planList;
        this.onPlanClickListener = listener;
        Log.v("PlanResponseFragment",planList.toString());
    }

    @Override
    public Fragment getItem(int position) {
        Log.v("PlanResponseFragmentI",planList.toString());
        return SubscriptionPlanFragment.newInstance(planList.get(position), onPlanClickListener);
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

        void onPlanClick(HashMap<String, String> plan);
    }
}

