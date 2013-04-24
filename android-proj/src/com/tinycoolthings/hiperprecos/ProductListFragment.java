package com.tinycoolthings.hiperprecos;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.tinycoolthings.hiperprecos.models.Produto;
import com.tinycoolthings.hiperprecos.serverComm.CallWebServiceTask;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Debug;
import com.tinycoolthings.hiperprecos.utils.Constants.Server.Parameter.Name;

public class ProductListFragment extends SherlockListFragment {

	private ArrayList<Produto> produtos;
	
	@Override
	public void onResume() {
		Debug.PrintError(this, "onResume");
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
   
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		int selectedProdID = produtos.get(position).getId();
		CallWebServiceTask getProduto = new CallWebServiceTask(Constants.Actions.GET_PRODUTO);
		getProduto.addParameter(Name.PRODUTO_ID, selectedProdID);
		getProduto.execute();
	}
	
}
