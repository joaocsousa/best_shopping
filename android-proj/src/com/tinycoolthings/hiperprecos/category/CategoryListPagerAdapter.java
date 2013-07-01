package com.tinycoolthings.hiperprecos.category;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tinycoolthings.hiperprecos.HiperPrecos;
import com.tinycoolthings.hiperprecos.utils.Constants;

public class CategoryListPagerAdapter extends FragmentPagerAdapter {

    public CategoryListPagerAdapter(FragmentManager fm) {
    	super(fm);
    }

	/** This method will be invoked when a page is requested to create */
    @Override
    public Fragment getItem(int selectedPos) {
    	CategoryListFragment categoryListFragment = new CategoryListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.Extras.HYPER, selectedPos);
        categoryListFragment.setArguments(bundle);
        return categoryListFragment;
    }
 
    /** Returns the number of pages */
    @Override
    public int getCount() {
        return (int) HiperPrecos.getInstance().getNumberOfHypers();
    }
	
}
