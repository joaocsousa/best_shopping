package com.tinycoolthings.hiperprecos.category;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tinycoolthings.hiperprecos.utils.Constants;

public class SearchPagerAdapter extends FragmentPagerAdapter {

    public SearchPagerAdapter(FragmentManager fm) {
    	super(fm);
    }

	/** This method will be invoked when a page is requested to create */
    @Override
    public Fragment getItem(int selectedPos) {
    	if (selectedPos == 0) {
    		// Productos
    		ProductResultsFragment productResultsFrag = new CategoryListFragment();
            Bundle bundle = new Bundle();
            ArrayList<Integer> prodsIDS = new ArrayList<Integer>();
            bundle.putIntegerArrayList(Constants.Extras.PRODUTOS, prodsIDS);
            productResultsFrag.setArguments(bundle);
            return productResultsFrag;
    	} else if (selectedPos == 1) {
    		// Categorias
    		CategoryResultsFragment categoryResultsFrag = new CategoryListFragment();
            Bundle bundle = new Bundle();
            ArrayList<Integer> catsIDs = new ArrayList<Integer>();
            bundle.putIntegerArrayList(Constants.Extras.CATEGORIAS, catsIDs);
            categoryResultsFrag.setArguments(bundle);
            return categoryResultsFrag;
    	}
    	return null;
    }
 
    /** Returns the number of pages */
    @Override
    public int getCount() {
        return 2;
    }
	
}
