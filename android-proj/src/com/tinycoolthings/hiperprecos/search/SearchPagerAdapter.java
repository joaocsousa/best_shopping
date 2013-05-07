package com.tinycoolthings.hiperprecos.search;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tinycoolthings.hiperprecos.HiperPrecos;
import com.tinycoolthings.hiperprecos.product.NoResultsFragment;
import com.tinycoolthings.hiperprecos.utils.Constants;

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
    		if (HiperPrecos.getInstance().getLatestProdSearch().size()>0) {
    	    	bundle.putInt(Constants.Extras.PRODUTO, selectedPos);
			} else {
				return new NoResultsFragment();
			}
    	} else if (selectedPos == 1) {
    		// Categorias
    		if (HiperPrecos.getInstance().getLatestCatSearch().size()>0) {
				bundle.putInt(Constants.Extras.CATEGORIA, selectedPos);
            } else {
				return new NoResultsFragment();
			}
    	}
		searchResultFrag.setArguments(bundle);
    	return searchResultFrag;
    }
 
    @Override
    public int getCount() {
    	return HiperPrecos.getInstance().getNumberOfHipers();
    }
	
    @Override
    public int getItemPosition(Object object) {
    	return POSITION_NONE;
    }
    
}
