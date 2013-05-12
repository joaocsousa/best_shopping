package com.tinycoolthings.hiperprecos.product;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockListFragment;
import com.tinycoolthings.hiperprecos.HiperPrecos;
import com.tinycoolthings.hiperprecos.models.Product;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Utils;
import com.tinycoolthings.hiperprecos.utils.Constants.Sort;
import com.tinycoolthings.hiperprecos.utils.Debug;

public class ProductListFragment extends SherlockListFragment {

	private ArrayList<Product> produtos;
	
	@Override
	public void onResume() {
		Debug.PrintDebug(this, "onResume");
		super.onResume();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		Bundle args = getArguments();

		produtos = HiperPrecos.getInstance().getCategoriaById(args.getInt(Constants.Extras.CATEGORY)).getProdutos();
		
		int sortType = args.getInt(Constants.Extras.PRODUTO_SORT);
		
		Utils.sortProdutos(produtos, sortType);
		
		/** Creating array adapter to set data in listview */
        ProductListAdapter adapter = new ProductListAdapter(getActivity().getBaseContext());
        adapter.setData(produtos);
        
        /** Setting the array adapter to the listview */
        setListAdapter(adapter);
        
		return super.onCreateView(inflater, container, savedInstanceState);
		
    }
	
}
