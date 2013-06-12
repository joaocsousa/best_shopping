package com.tinycoolthings.hiperprecos.search;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.tinycoolthings.hiperprecos.HiperPrecos;
import com.tinycoolthings.hiperprecos.R;
import com.tinycoolthings.hiperprecos.models.Product;


public class SearchResultFragment extends SherlockFragment {
	
	private ArrayList<Integer> productsIDs = new ArrayList<Integer>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.search_results, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ExpandableListView expandableList = (ExpandableListView) getView().findViewById(R.id.el_search_results);
		
		ArrayList<Product> products = new ArrayList<Product>();
		ArrayList<Integer> productsIDs = this.productsIDs;
		try {
			products.addAll(HiperPrecos.getInstance().getProductsFromIDsList(productsIDs));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		ProductSearchListAdapter productSearchListAdapter = new ProductSearchListAdapter(HiperPrecos.getInstance().getAppContext(), products);
		
		expandableList.setAdapter(productSearchListAdapter);
		
	}
	
	public void setContents(ArrayList<Product> products) {
		this.productsIDs.clear();
    	Iterator<Product> prodIterator = products.iterator();
    	while (prodIterator.hasNext()) {
    		this.productsIDs.add(prodIterator.next().getId());
    	}
	}
	
}
