package com.tinycoolthings.hiperprecos.search;

import java.util.ArrayList;
import java.util.Iterator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tinycoolthings.hiperprecos.HiperPrecos;
import com.tinycoolthings.hiperprecos.models.Category;
import com.tinycoolthings.hiperprecos.models.Product;
import com.tinycoolthings.hiperprecos.utils.Constants.Extras;
import com.tinycoolthings.hiperprecos.utils.Constants.Sort;
import com.tinycoolthings.hiperprecos.utils.Debug;
import com.tinycoolthings.hiperprecos.utils.Filter;

public class SearchPagerAdapter extends FragmentPagerAdapter {

	private ArrayList<Integer> productsIDs = new ArrayList<Integer>();
	private ArrayList<Integer> categoriesIDs = new ArrayList<Integer>();
	private Integer sort = Sort.NAME_ASCENDING;
	private Filter filter = new Filter();
	
	public SearchPagerAdapter(FragmentManager fm, ArrayList<Product> products, ArrayList<Category> categories) {
    	super(fm);
    	Iterator<Product> prodIterator = products.iterator();
    	while (prodIterator.hasNext()) {
    		this.productsIDs.add(prodIterator.next().getId());
    	}
    	Iterator<Category> catIterator = categories.iterator();
    	while (catIterator.hasNext()) {
    		this.categoriesIDs.add(catIterator.next().getId());
    	}
    }
	
	public void setSort(Integer sort) {
		this.sort = sort;
		notifyDataSetChanged();
	}
	
	public void setFilter(Filter filter) {
		this.filter = filter;
		notifyDataSetChanged();
	}
   
	/** This method will be invoked when a page is requested to create */
    @Override
    public Fragment getItem(int selectedPos) {
    	Bundle bundle = new Bundle();
    	SearchResultFragment searchResultFrag = new SearchResultFragment();
    	if (selectedPos == 0) {
    		// Produtos
//			if (HiperPrecos.getInstance().getLatestProdSearch().size()>0) {
		    	bundle.putIntegerArrayList(Extras.PRODUCTS, this.productsIDs);
		    	bundle.putInt(Extras.PRODUCT_SORT, this.sort);
		    	bundle.putParcelable(Extras.FILTER, this.filter);
		    	bundle.putBoolean(Extras.PRODUCT, true);
//			} else {
//				return new NoResultsFragment();
//			}
    	} else if (selectedPos == 1) {
    		// Categorias
//    		if (HiperPrecos.getInstance().getLatestCatSearch().size()>0) {
//				bundle.putInt(Constants.Extras.CATEGORY, selectedPos);
//	    	    bundle.putIntegerArrayList(Extras.CATEGORIES, this.categoriesIDs);
//            } else {
//				return new NoResultsFragment();
//	    		bundle.putBoolean(Extras.CATEGORY, true);
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
