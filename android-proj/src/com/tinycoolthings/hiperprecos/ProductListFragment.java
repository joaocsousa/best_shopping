package com.tinycoolthings.hiperprecos;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockListFragment;
import com.tinycoolthings.hiperprecos.models.Produto;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Debug;

public class ProductListFragment extends SherlockListFragment {

	private ArrayList<Produto> produtos;
	
	@Override
	public void onResume() {
		Debug.PrintDebug(this, "onResume");
		super.onResume();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		Bundle args = getArguments();

		produtos = HiperPrecos.getInstance().getCategoriaById(args.getInt(Constants.Extras.CATEGORIA)).getProdutos();
		
		/** Creating array adapter to set data in listview */
        ProductListAdapter adapter = new ProductListAdapter(getActivity().getBaseContext());
        adapter.setData(produtos);
        
        /** Setting the array adapter to the listview */
        setListAdapter(adapter);
        
		return super.onCreateView(inflater, container, savedInstanceState);
		
    }
	
}
