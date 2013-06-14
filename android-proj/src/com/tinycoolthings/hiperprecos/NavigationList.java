package com.tinycoolthings.hiperprecos;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.tinycoolthings.hiperprecos.category.CategoryListFragment;
import com.tinycoolthings.hiperprecos.models.Category;
import com.tinycoolthings.hiperprecos.product.ProductList;
import com.tinycoolthings.hiperprecos.search.SearchResults;
import com.tinycoolthings.hiperprecos.serverComm.CallWebServiceTask;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Constants.Server.Parameter.Name;
import com.tinycoolthings.hiperprecos.utils.Debug;
import com.tinycoolthings.hiperprecos.utils.Utils;

public class NavigationList extends SherlockFragmentActivity {

	private ActionBar mActionBar;

	private List<Category> categoriesListMenu = new ArrayList<Category>();
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Constants.Actions.GET_CATEGORY)) {
				String result = intent.getStringExtra(Constants.Extras.CATEGORY);
				try {
					JSONObject catJson = new JSONObject(result);
					Category category = HiperPrecos.getInstance().addCategory(catJson);
					enterSubCategory(category);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (intent.getAction().equals(Constants.Actions.DISPLAY_CATEGORY)) {
				Debug.PrintInfo(NavigationList.this, "Displaying category...");
				enterSubCategory(HiperPrecos.getInstance().getCategoryById(intent.getIntExtra(Constants.Extras.CATEGORY, -1)));
			} else if (intent.getAction().equals(Constants.Actions.SEARCH)) {
				Debug.PrintInfo(NavigationList.this, "Received search result.");
				Intent searchResultsIntent = new Intent(NavigationList.this, SearchResults.class);
				searchResultsIntent.putExtras(intent);
				startActivity(searchResultsIntent);
				HiperPrecos.getInstance().hideWaitingDialog();
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Bundle bundle = getIntent().getExtras();
		
		Category category = (Category) HiperPrecos.getInstance().getCategoryById(bundle.getInt(Constants.Extras.CATEGORY));
		
		/** Getting a reference to action bar of this activity */
		mActionBar = getSupportActionBar();
		
		mActionBar.setDisplayHomeAsUpEnabled(true);
		
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		mActionBar.setDisplayShowTitleEnabled(false);
		
		HiperPrecos.getInstance().setAppContext(this);
		
		enterSubCategory(category);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		menu.clear();
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.category_list_menu, menu);
		// Get the SearchView and set the searchable configuration
	    final MenuItem menuItem = menu.findItem(R.id.menu_search);
		
		SearchView searchView = (SearchView) menuItem.getActionView();
		
	    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

	    	@Override
	    	public boolean onQueryTextSubmit(String query) {
	    		if (Utils.validSearch(query)) {
	    			menuItem.collapseActionView();
	    		}
	    		HiperPrecos.getInstance().search(query);
	            return false;
	        }
	        @Override
	        public boolean onQueryTextChange(String newText) {
	            return false;
	        }
	    });
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	        	exitToMainMenu();
	    }
	    return super.onOptionsItemSelected(item);
	}

	private void exitToMainMenu() {
		// This is called when the Home (Up) button is pressed
        // in the Action Bar.
        Intent parentActivityIntent = new Intent(this, MainActivity.class);
        parentActivityIntent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(parentActivityIntent);
        finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Debug.PrintDebug(this, "onResume");
		IntentFilter filterServerResp = new IntentFilter();
		filterServerResp.addAction(Constants.Actions.GET_CATEGORY);
		filterServerResp.addAction(Constants.Actions.DISPLAY_CATEGORY);
		filterServerResp.addAction(Constants.Actions.SEARCH);
		registerReceiver(broadcastReceiver, filterServerResp);
		
		HiperPrecos.getInstance().setAppContext(this);
	}
	
	@Override
	protected void onPause() {
		unregisterReceiver(broadcastReceiver);
		Debug.PrintDebug(this, "onPause");
		super.onPause();
	}
	
	private void showProductList(Category category) {
		Bundle bundle = new Bundle();
        bundle.putInt(Constants.Extras.CATEGORY, category.getId());
        Intent intent = new Intent(NavigationList.this, ProductList.class);
        intent.putExtras(bundle);
        startActivity(intent);
	}
	
	protected void enterSubCategory(Category category) {
		
		Debug.PrintInfo(this, "Displaying category " + category.getName());
		
		// If has products, quit immediately
		try {
			if (HiperPrecos.getInstance().categoryHasProducts(category)) {
				Debug.PrintInfo(NavigationList.this, category.getName() + " has products.");
				showProductList(category);
	            return;
	    	}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		ArrayList<String> categoriesList = new ArrayList<String>();
		int preSelectedPosition = -1;
		
		// get siblings for this category, to build the actionbar list menu
		try {
			categoriesListMenu = HiperPrecos.getInstance().getCategorySiblings(category, Constants.Sort.NAME_ASCENDING);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		preSelectedPosition = categoriesListMenu.indexOf(category);
		
		Iterator<Category> iterator = categoriesListMenu.iterator();
		while (iterator.hasNext()) {
			Category currCat = iterator.next();
			categoriesList.add(currCat.getName());
		}
	
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActionBar.getThemedContext(), R.layout.sherlock_spinner_dropdown_item, categoriesList);
		
		/** Defining Navigation listener */
        mActionBar.setListNavigationCallbacks(adapter, new OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				Category selectedCat = categoriesListMenu.get(itemPosition);
		    	Debug.PrintInfo(NavigationList.class, "Selected category -> " + selectedCat.getName());
		    	boolean selectedCatHasProducts = false;
				try {
					selectedCatHasProducts = HiperPrecos.getInstance().categoryHasProducts(selectedCat);
				} catch (SQLException e) {
					e.printStackTrace();
				}
		    	boolean selectedCatHasSubCategories = false;
				try {
					selectedCatHasSubCategories = HiperPrecos.getInstance().categoryHasSubCategories(selectedCat);
				} catch (SQLException e) {
					e.printStackTrace();
				}
		    	if (selectedCatHasProducts) {
		    		Debug.PrintInfo(NavigationList.this, selectedCat.getName() + " has products.");
		    		showProductList(selectedCat);
		    	} else if (selectedCatHasSubCategories) {
		    		Debug.PrintInfo(NavigationList.class, selectedCat.getName() + " has sub categories.");
		        	CategoryListFragment categoryListFrag = new CategoryListFragment();
		        	Bundle bundle = new Bundle();
		            bundle.putInt(Constants.Extras.CATEGORY, selectedCat.getId());
		        	categoryListFrag.setArguments(bundle);
		        	getSupportFragmentManager().beginTransaction().replace(android.R.id.content, categoryListFrag).commit();
		    	} else {
		    		Debug.PrintWarning(NavigationList.this, selectedCat.getName() + " has no information.");
		    		CallWebServiceTask getCategoria = new CallWebServiceTask(Constants.Actions.GET_CATEGORY, false);
		    		getCategoria.addParameter(Name.CATEGORIA_ID, selectedCat.getId());
		    		getCategoria.execute();
		    	}
		    	return true;
			}
		});
        
        mActionBar.setSelectedNavigationItem(preSelectedPosition);
        
        HiperPrecos.getInstance().hideWaitingDialog();
	}

	@Override
	public void onBackPressed() {
		Category currCat = categoriesListMenu.get(getSupportActionBar().getSelectedNavigationIndex());
		Category parentCategory = currCat.getParentCat();
		HiperPrecos.getInstance().refreshCategory(parentCategory);
		if (parentCategory != null) {
			enterSubCategory(parentCategory);
		} else {
			exitToMainMenu();
			return;
		}
	}
	
}
