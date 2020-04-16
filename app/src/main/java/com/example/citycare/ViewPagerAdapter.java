package com.example.citycare;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.citycare.fragment.SigninFragment;
import com.example.citycare.fragment.SignupFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private int tabsNumber;

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior,int tabs) {
        super(fm, behavior);
        this.tabsNumber = tabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new SigninFragment();
            case 1:
                return new SignupFragment();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return tabsNumber;
    }

    
}
