package com.tinycoolthings.hiperprecos.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockListFragment;
import com.tinycoolthings.hiperprecos.HiperPrecos;
import com.tinycoolthings.hiperprecos.R;
import com.tinycoolthings.hiperprecos.models.Category;
import com.tinycoolthings.hiperprecos.models.Product;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Debug;
import com.tinycoolthings.hiperprecos.utils.Filter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductListFragment extends SherlockListFragment {

	private List<Product> products = new ArrayList<Product>();
	private Category category = null;
	private int sort = 0;
	private Filter filter = null;
	private ProductListAdapter adapter;
	private Double filterMinPrice = 0.0;
	private Double filterMaxPrice = 0.0;
	private List<String> brands = new ArrayList<String>();
	
	@Override
	public void onResume() {
		Debug.PrintDebug(this, "onResume");
		super.onResume();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		Bundle args = getArguments();

		category = HiperPrecos.getInstance().getCategoryById(args.getInt(Constants.Extras.CATEGORY));
		
		sort = args.getInt(Constants.Extras.PRODUCT_SORT);
		
		filter = (Filter) args.getParcelable(Constants.Extras.FILTER);
		
		try {
			products = HiperPrecos.getInstance().getProductsFromCategory(category, sort, filter);
			this.setMinimumPrice();
			this.setMaximumPrice();
			this.setBrands();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		/** Creating array adapter to set data in listview */
        adapter = new ProductListAdapter(getActivity().getBaseContext());
        adapter.setData(products);
        
        /** Setting the array adapter to the listview */
        setListAdapter(adapter);
        
        HiperPrecos.getInstance().hideWaitingDialog();
        
		return super.onCreateView(inflater, container, savedInstanceState);
		
    }
	
	public void setSort(int newSort) {
		this.sort = newSort;
		this.updateProductsList();
	}
	
	public void setFilter(Filter filter) {
		this.filter = filter;
		this.updateProductsList();
	}
	
	public int getResultCount() {
		try {
			return HiperPrecos.getInstance().getProductsFromCategory(category, this.sort, this.filter).size();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private void updateProductsList() {
		try {
			products = HiperPrecos.getInstance().getProductsFromCategory(category, this.sort, this.filter);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		adapter.setData(products);
		adapter.notifyDataSetChanged();
	}
	
	public void setMinimumPrice() {
		if (products.size()==0) {
			this.filterMinPrice = 0.0;
		}
		Double minPrice = products.get(0).getPrice();
		for (int i=1; i < products.size(); i++) {
			if (products.get(i).getPrice() < minPrice) {
				minPrice = products.get(i).getPrice();
			}
		}
		this.filterMinPrice = minPrice;
	}
	
	public void setMaximumPrice() {
		Double maxPrice = 0.0;
		if (products.size()==0) {
			this.filterMaxPrice = 0.0;
		}
		for (int i=0; i < products.size(); i++) {
			if (products.get(i).getPrice() > maxPrice) {
				maxPrice = products.get(i).getPrice();
			}
		}
		this.filterMaxPrice = maxPrice;
	}
	
	private void setBrands() {
		for (int i=0; i < products.size(); i++) {
			String currBrand = products.get(i).getBrand().equals("null") || products.get(i).getBrand().equals("") ? getResources().getString(R.string.non_available) : products.get(i).getBrand();
			if (!brands.contains(currBrand)) {
				brands.add(currBrand);
			}
		}
	}
	
	public Double getMinPriceFilter() {
		return this.filterMinPrice;
	}
	
	public Double getMaxPriceFilter() {
		return this.filterMaxPrice;
	}
	
	public List<String> getBrandsFilter() {
		return this.brands;
	}

}
