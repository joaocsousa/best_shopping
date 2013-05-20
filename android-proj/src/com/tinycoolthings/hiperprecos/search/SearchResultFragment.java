package com.tinycoolthings.hiperprecos.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.tinycoolthings.hiperprecos.HiperPrecos;
import com.tinycoolthings.hiperprecos.R;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Debug;

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
			
//			ProductSearchListAdapter productSearchListAdapter = new ProductSearchListAdapter(HiperPrecos.getInstance().getAppContext(), HiperPrecos.getInstance().getLatestProdSearch());
			
//			expandableList.setAdapter(productSearchListAdapter);
			
		} else if (args.containsKey(Constants.Extras.CATEGORY)) {
			
			Debug.PrintError(this, "Displaying categorias!");
			
//			CategorySearchListAdapter categorySearchListAdapter = new CategorySearchListAdapter(HiperPrecos.getInstance().getAppContext(), HiperPrecos.getInstance().getLatestCatSearch());
			
//			expandableList.setAdapter(categorySearchListAdapter);
			
		}
		
		
	}
}
