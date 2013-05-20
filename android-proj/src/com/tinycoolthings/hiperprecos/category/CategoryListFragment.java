package com.tinycoolthings.hiperprecos.category;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
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
import com.tinycoolthings.hiperprecos.models.Hyper;
import com.tinycoolthings.hiperprecos.serverComm.CallWebServiceTask;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Constants.Actions;
import com.tinycoolthings.hiperprecos.utils.Constants.Server.Parameter.Name;
import com.tinycoolthings.hiperprecos.utils.Debug;

public class CategoryListFragment extends SherlockListFragment {

	private List<Category> categories;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		Bundle args = getArguments();

		if (args.containsKey(Constants.Extras.HYPER)) {
			Hyper currHyper = HiperPrecos.getInstance().getHypers().get(args.getInt(Constants.Extras.HYPER));
			try {
				categories = HiperPrecos.getInstance().getSubCategoriesFromHyper(currHyper, Constants.Sort.NAME_ASCENDING);
				Debug.PrintError(this, "Displaying " + categories.size() + " categories.");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (args.containsKey(Constants.Extras.CATEGORY)) {
			Category currCat = HiperPrecos.getInstance().getCategoryById(args.getInt(Constants.Extras.CATEGORY));
			try {
				categories = HiperPrecos.getInstance().getSubCategoriesFromParent(currCat, Constants.Sort.NAME_ASCENDING);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			return null;
		}
		
		ArrayList<String> catsToShow = new ArrayList<String>();
		
		Iterator<Category> iterator = categories.iterator();
		while (iterator.hasNext()) {
			Category currCat = iterator.next();
			catsToShow.add(currCat.getName());
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
		Category selectedCat = categories.get(position);
		int selectedCatID = selectedCat.getId();
		Debug.PrintInfo(this, "Selected category with id " + selectedCatID);
		boolean categoryHasLoaded = false;
		try {
			categoryHasLoaded = HiperPrecos.getInstance().categoryLoaded(selectedCat);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (categoryHasLoaded) {
			Debug.PrintInfo(this, "Category has loaded. Display.");
			Intent intent = new Intent(Actions.DISPLAY_CATEGORY);
			intent.putExtra(Constants.Extras.CATEGORY, selectedCatID);
			HiperPrecos.getInstance().sendBroadcast(intent);
		} else {
			Debug.PrintInfo(this, "Category has not loaded. Get info from web.");
			CallWebServiceTask getCategorias = new CallWebServiceTask(Constants.Actions.GET_CATEGORY, true);
			getCategorias.addParameter(Name.CATEGORIA_ID, selectedCatID);
			getCategorias.execute();
		}
		
	}
	
}
