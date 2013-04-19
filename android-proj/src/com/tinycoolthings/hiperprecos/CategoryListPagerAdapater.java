package com.tinycoolthings.hiperprecos;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tinycoolthings.hiperprecos.utils.Constants;

public class CategoryListPagerAdapater extends FragmentPagerAdapter {

    public CategoryListPagerAdapater(FragmentManager fm) {
    	super(fm);
    }

	/** This method will be invoked when a page is requested to create */
    @Override
    public Fragment getItem(int selectedPos) {
    	CategoryListFragment categoryListFragment = new CategoryListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.Extras.HIPER, selectedPos);
        categoryListFragment.setArguments(bundle);
        return categoryListFragment;
    }
 
    /** Returns the number of pages */
    @Override
    public int getCount() {
        return HiperPrecos.getInstance().getNumberOfHipers();
    }
	
}
