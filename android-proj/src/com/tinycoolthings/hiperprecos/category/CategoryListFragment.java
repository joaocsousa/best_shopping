package com.tinycoolthings.hiperprecos.category;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.tinycoolthings.hiperprecos.HiperPrecos;
import com.tinycoolthings.hiperprecos.R;
import com.tinycoolthings.hiperprecos.R.layout;
import com.tinycoolthings.hiperprecos.models.Categoria;
import com.tinycoolthings.hiperprecos.serverComm.CallWebServiceTask;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Constants.Actions;
import com.tinycoolthings.hiperprecos.utils.Constants.Server.Parameter.Name;
import com.tinycoolthings.hiperprecos.utils.Debug;

public class CategoryListFragment extends SherlockListFragment {

	private ArrayList<Categoria> categorias;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		Bundle args = getArguments();

		if (args.containsKey(Constants.Extras.HIPER)) {
			categorias = HiperPrecos.getInstance().getHipers().get(args.getInt(Constants.Extras.HIPER)).getCategorias();
		} else if (args.containsKey(Constants.Extras.CATEGORIA)) {
			categorias = HiperPrecos.getInstance().getCategoriaById(args.getInt(Constants.Extras.CATEGORIA)).getSubCategorias();
		} else {
			return null;
		}
		
		ArrayList<String> catsToShow = new ArrayList<String>();
		
		for (int i=0;i<categorias.size();i++) {
			catsToShow.add(categorias.get(i).getNome());
		}
		
		/** Creating array adapter to set data in listview */
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(), R.layout.sherlock_spinner_dropdown_item, catsToShow);
 
        /** Setting the array adapter to the listview */
        setListAdapter(adapter);
        
		return super.onCreateView(inflater, container, savedInstanceState);
    }
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		int selectedCatID = categorias.get(position).getId();
		Debug.PrintInfo(this, "Selected categoria with id " + selectedCatID);
		Categoria selectedCat = HiperPrecos.getInstance().getCategoriaById(selectedCatID);
		if (selectedCat!=null && selectedCat.hasLoaded()) {
			Intent intent = new Intent(Actions.DISPLAY_CATEGORIA);
			intent.putExtra(Constants.Extras.CATEGORIA, selectedCatID);
			HiperPrecos.getInstance().sendBroadcast(intent);
		} else {
			CallWebServiceTask getCategorias = new CallWebServiceTask(Constants.Actions.GET_CATEGORIA);
			getCategorias.addParameter(Name.CATEGORIA_ID, selectedCatID);
			getCategorias.execute();
		}
		
	}
	
}
