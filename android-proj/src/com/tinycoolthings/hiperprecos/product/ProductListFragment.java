package com.tinycoolthings.hiperprecos.product;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

public class ProductListFragment extends SherlockListFragment {

	private List<Product> products = new ArrayList<Product>();
	
	@Override
	public void onResume() {
		Debug.PrintDebug(this, "onResume");
		super.onResume();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		Bundle args = getArguments();

		Category currCat = HiperPrecos.getInstance().getCategoryById(args.getInt(Constants.Extras.CATEGORY));
		
		try {
			products = HiperPrecos.getInstance().getProductsFromCategory(currCat, args.getInt(Constants.Extras.PRODUCT_SORT), (Filter) args.getParcelable(Constants.Extras.FILTER));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		/** Creating array adapter to set data in listview */
        ProductListAdapter adapter = new ProductListAdapter(getActivity().getBaseContext());
        adapter.setData(products);
        
        /** Setting the array adapter to the listview */
        setListAdapter(adapter);
        
        HiperPrecos.getInstance().hideWaitingDialog();
        
		return super.onCreateView(inflater, container, savedInstanceState);
		
    }
	
	public Double getMinimumPrice() {
		if (products.size()==0) {
			return 0.0;
		}
		Double minPrice = products.get(0).getPrice();
		for (int i=1; i < products.size(); i++) {
			if (products.get(i).getPrice() <minPrice) {
				minPrice = products.get(i).getPrice();
			}
		}
		return minPrice;
	}
	
	public Double getMaximumPrice() {
		Double maxPrice = 0.0;
		for (int i=0; i < products.size(); i++) {
			if (products.get(i).getPrice() > maxPrice) {
				maxPrice = products.get(i).getPrice();
			}
		}
		return maxPrice;
	}
	
	public List<String> getBrands() {
		List<String> brands = new ArrayList<String>();
		for (int i=0; i < products.size(); i++) {
			String currBrand = products.get(i).getBrand().equals("null") ? getResources().getString(R.string.non_available) : products.get(i).getBrand();
			if (!brands.contains(currBrand)) {
				brands.add(currBrand);
			}
		}
		return brands;
	}
	
}
