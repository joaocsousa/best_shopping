package com.tinycoolthings.hiperprecos.search;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tinycoolthings.hiperprecos.HiperPrecos;

public class SearchPagerAdapter extends FragmentPagerAdapter {

	public SearchPagerAdapter(FragmentManager fm) {
    	super(fm);
    }
    
	/** This method will be invoked when a page is requested to create */
    @Override
    public Fragment getItem(int selectedPos) {
    	Bundle bundle = new Bundle();
    	SearchResultFragment searchResultFrag = new SearchResultFragment();
    	if (selectedPos == 0) {
    		// Produtos
//    		if (HiperPrecos.getInstance().getLatestProdSearch().size()>0) {
//    	    	bundle.putInt(Constants.Extras.PRODUCT, selectedPos);
//			} else {
//				return new NoResultsFragment();
//			}
    	} else if (selectedPos == 1) {
    		// Categorias
//    		if (HiperPrecos.getInstance().getLatestCatSearch().size()>0) {
//				bundle.putInt(Constants.Extras.CATEGORY, selectedPos);
//            } else {
//				return new NoResultsFragment();
//			}
    	}
		searchResultFrag.setArguments(bundle);
    	return searchResultFrag;
    }
 
    @Override
    public int getCount() {
    	return (int) HiperPrecos.getInstance().getNumberOfHypers();
    }
	
    @Override
    public int getItemPosition(Object object) {
    	return POSITION_NONE;
    }
    
}
