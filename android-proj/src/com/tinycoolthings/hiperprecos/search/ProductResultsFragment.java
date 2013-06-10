package com.tinycoolthings.hiperprecos.search;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockListFragment;
import com.tinycoolthings.hiperprecos.HiperPrecos;
import com.tinycoolthings.hiperprecos.models.Product;
import com.tinycoolthings.hiperprecos.product.ProductListAdapter;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Debug;

public class ProductResultsFragment extends SherlockListFragment {

	private ArrayList<Product> produtos;
	
	@Override
	public void onResume() {
		Debug.PrintDebug(this, "onResume");
		super.onResume();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		Bundle args = getArguments();

		ArrayList<Integer> prodsIDs = args.getIntegerArrayList(Constants.Extras.PRODUCTS);

		Debug.PrintInfo(this, "Showing " + prodsIDs.size() + " products");
		for (int i=0; i < prodsIDs.size(); i++) {
			Product produto = HiperPrecos.getInstance().getProductById(prodsIDs.get(i));
			Debug.PrintInfo(this, "Adding " + produto.getName());
			produtos.add(produto);
		}
		
		/** Creating array adapter to set data in listview */
        ProductListAdapter adapter = new ProductListAdapter(getActivity().getBaseContext());
        adapter.setData(produtos);
        
        /** Setting the array adapter to the listview */
        setListAdapter(adapter);
        
		return super.onCreateView(inflater, container, savedInstanceState);
		
    }
	
}
