package com.tinycoolthings.hiperprecos.search;

import java.sql.SQLException;
import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.tinycoolthings.hiperprecos.HiperPrecos;
import com.tinycoolthings.hiperprecos.R;
import com.tinycoolthings.hiperprecos.models.Category;
import com.tinycoolthings.hiperprecos.models.Product;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Debug;
import com.tinycoolthings.hiperprecos.utils.Constants.Extras;
import com.tinycoolthings.hiperprecos.utils.Filter;


public class SearchResultFragment extends SherlockFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.search_results, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ExpandableListView expandableList = (ExpandableListView) getView().findViewById(R.id.el_search_results);
		
		Bundle args = getArguments();
		
		if (args.containsKey(Constants.Extras.PRODUCT)) {
			
			ArrayList<Product> products = new ArrayList<Product>();
			ArrayList<Integer> productsIDs = getArguments().getIntegerArrayList(Extras.PRODUCTS);
			int sort = getArguments().getInt(Extras.PRODUCT_SORT);
			Filter filter = getArguments().getParcelable(Extras.FILTER);
			try {
				products.addAll(HiperPrecos.getInstance().getProductsFromIDsList(productsIDs, sort, filter));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		
			ProductSearchListAdapter productSearchListAdapter = new ProductSearchListAdapter(HiperPrecos.getInstance().getAppContext(), products);
			
			expandableList.setAdapter(productSearchListAdapter);
			
		} else if (args.containsKey(Constants.Extras.CATEGORY)) {
			
			ArrayList<Category> categories = new ArrayList<Category>();
			ArrayList<Integer> categoriesIDs = getArguments().getIntegerArrayList(Extras.CATEGORIES);
			for (int i=0;i<categoriesIDs.size();i++) {
				categories.add(HiperPrecos.getInstance().getCategoryById(categoriesIDs.get(i)));
			}
			
			Debug.PrintError(this, "Displaying categorias!");
			
			CategorySearchListAdapter categorySearchListAdapter = new CategorySearchListAdapter(HiperPrecos.getInstance().getAppContext(), categories);
			
			expandableList.setAdapter(categorySearchListAdapter);
			
		}
		
	}
}
