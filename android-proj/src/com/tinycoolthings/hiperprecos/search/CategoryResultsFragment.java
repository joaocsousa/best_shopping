package com.tinycoolthings.hiperprecos.search;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
import com.tinycoolthings.hiperprecos.models.Category;
import com.tinycoolthings.hiperprecos.serverComm.CallWebServiceTask;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Constants.Actions;
import com.tinycoolthings.hiperprecos.utils.Constants.Server.Parameter.Name;
import com.tinycoolthings.hiperprecos.utils.Constants.Sort;
import com.tinycoolthings.hiperprecos.utils.Debug;

public class CategoryResultsFragment extends SherlockListFragment {

	private List<Category> categories;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		Bundle args = getArguments();

		Category category = HiperPrecos.getInstance().getCategoryById(args.getInt(Constants.Extras.CATEGORY));
		
		try {
			categories = HiperPrecos.getInstance().getSubCategoriesFromParent(category, Sort.NAME_ASCENDING);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		ArrayList<String> catsToShow = new ArrayList<String>();
		
		for (int i=0;i<categories.size();i++) {
			catsToShow.add(categories.get(i).getName());
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
		int selectedCatID = categories.get(position).getId();
		Debug.PrintInfo(this, "Selected categoria with id " + selectedCatID);
		Category selectedCat = HiperPrecos.getInstance().getCategoryById(selectedCatID);
		boolean categoryHasLoaded = false;
		try {
			categoryHasLoaded = HiperPrecos.getInstance().categoryLoaded(selectedCat);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (selectedCat!=null && categoryHasLoaded) {
			Intent intent = new Intent(Actions.DISPLAY_CATEGORY);
			intent.putExtra(Constants.Extras.CATEGORY, selectedCatID);
			HiperPrecos.getInstance().sendBroadcast(intent);
		} else {
			CallWebServiceTask getCategorias = new CallWebServiceTask(Constants.Actions.GET_CATEGORY, true);
			getCategorias.addParameter(Name.CATEGORIA_ID, selectedCatID);
			getCategorias.execute();
		}
		
	}
	
}
