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
import com.tinycoolthings.hiperprecos.models.Category;
import com.tinycoolthings.hiperprecos.models.Product;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Debug;

public class ProductListFragment extends SherlockListFragment {

	private List<Product> produtos = new ArrayList<Product>();
	
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
			produtos = HiperPrecos.getInstance().getProductsFromCategory(currCat, args.getInt(Constants.Extras.PRODUCT_SORT));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		/** Creating array adapter to set data in listview */
        ProductListAdapter adapter = new ProductListAdapter(getActivity().getBaseContext());
        adapter.setData(produtos);
        
        /** Setting the array adapter to the listview */
        setListAdapter(adapter);
        
		return super.onCreateView(inflater, container, savedInstanceState);
		
    }
	
}
