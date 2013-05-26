package com.tinycoolthings.hiperprecos;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.tinycoolthings.hiperprecos.category.CategoryListFragment;
import com.tinycoolthings.hiperprecos.models.Category;
import com.tinycoolthings.hiperprecos.models.Product;
import com.tinycoolthings.hiperprecos.product.ProductListFragment;
import com.tinycoolthings.hiperprecos.product.ProductView;
import com.tinycoolthings.hiperprecos.search.SearchResults;
import com.tinycoolthings.hiperprecos.serverComm.CallWebServiceTask;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Constants.Server.Parameter.Name;
import com.tinycoolthings.hiperprecos.utils.Constants.Sort;
import com.tinycoolthings.hiperprecos.utils.Debug;
import com.tinycoolthings.hiperprecos.utils.Filter;
import com.tinycoolthings.hiperprecos.utils.Utils;

public class NavigationList extends SherlockFragmentActivity implements OnNavigationListener {

	private ActionBar mActionBar;

	private List<Category> categoriesListMenu = new ArrayList<Category>();

	private static boolean viewingProductsList = false;
	
	private static int currSelectedSort = Sort.NAME_ASCENDING;

	private static int currSelectedCat = 0;
	
	private ProductListFragment productListFrag;
	
	private Filter filter = new Filter();
	
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
			} else if (intent.getAction().equals(Constants.Actions.GET_PRODUCT)) {
				String result = intent.getStringExtra(Constants.Extras.PRODUCT);
				try {
					JSONObject prodJson = new JSONObject(result);
					Product product = HiperPrecos.getInstance().addProduct(prodJson);
					Debug.PrintWarning(NavigationList.this, "Received data for produto " + product.getId() + " - " + product.getName());
					showProduct(product);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (intent.getAction().equals(Constants.Actions.DISPLAY_PRODUCT)) {
				Debug.PrintInfo(this, "Displaying product...");
				Product selectedProd = HiperPrecos.getInstance().getProductById(intent.getIntExtra(Constants.Extras.PRODUCT, -1));
				showProduct(selectedProd);
			} else if (intent.getAction().equals(Constants.Actions.SEARCH)) {
				String result = intent.getStringExtra(Constants.Extras.SEARCH_RESULT);
				Debug.PrintDebug(this, result);
				Intent searchResultsIntent = new Intent(NavigationList.this, SearchResults.class);
				searchResultsIntent.putExtras(intent);
				startActivity(searchResultsIntent);
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
		if (viewingProductsList) {
			inflater.inflate(R.menu.product_list_menu, menu);
		} else {
			inflater.inflate(R.menu.category_list_menu, menu);
		}
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
	            // suggestions go here
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
	            break;
	        case R.id.menu_sort:
	        	showSortMenu();
	        	break;
	        case R.id.menu_filter:
	        	showFilterMenu();
	        	break;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	public void showSortMenu() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.order));         
		int selected = currSelectedSort;
		String[] options = new String[] {
				getResources().getString(R.string.nome_asc),
				getResources().getString(R.string.nome_desc),
				getResources().getString(R.string.marca_asc),
				getResources().getString(R.string.marca_desc),
				getResources().getString(R.string.preco_asc),
				getResources().getString(R.string.preco_desc)
		};
		builder.setSingleChoiceItems( options, selected, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,int which) {
				if (currSelectedSort != which) {
					currSelectedSort = which;
					onNavigationItemSelected(currSelectedCat, 0);
				}
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void showFilterMenu() {
		final int maxPrice = (int)Math.ceil(productListFrag.getMaximumPrice());
		final int minPrice = (int)Math.round(productListFrag.getMinimumPrice());
		LayoutInflater inflater = getLayoutInflater();
		final View dialoglayout = inflater.inflate(R.layout.filter_layout, null);
		final SeekBar minSeekBar = (SeekBar)dialoglayout.findViewById(R.id.filter_min_price);
		final SeekBar maxSeekBar = (SeekBar)dialoglayout.findViewById(R.id.filter_max_price);
		minSeekBar.setMax(maxPrice-1);
		maxSeekBar.setMax(maxPrice-1);
		maxSeekBar.setProgress(maxPrice-1);
		final TextView tvMinPrice = (TextView)dialoglayout.findViewById(R.id.tv_filter_price_min);
		final TextView tvMaxPrice = (TextView)dialoglayout.findViewById(R.id.tv_filter_price_max);
		tvMinPrice.setText(minPrice+" €");
		tvMaxPrice.setText(maxPrice+" €");
		minSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (progress > (seekBar.getMax()-1)) {
					seekBar.setProgress((seekBar.getMax()-1));
					return;
				}
				int currMinPrice = progress+minPrice;
				tvMinPrice.setText(currMinPrice+" €");
				if (progress >= maxSeekBar.getProgress()) {
					maxSeekBar.setProgress(progress+1);
				}
			}
		});
		maxSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (progress < 1) {
					seekBar.setProgress(1);
					return;
				}
				int currMaxPrice = progress+minPrice;
				tvMaxPrice.setText(currMaxPrice+" €");
				if (progress <= minSeekBar.getProgress()) {
					minSeekBar.setProgress(progress-1);
				}
			}
		});
		final List<String> brands = productListFrag.getBrands();
		java.util.Collections.sort(brands);
		brands.add(0, getResources().getString(R.string.all_brands));
		final ListView brandsLv = (ListView) dialoglayout.findViewById(R.id.lv_brands);
		final ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, brands);
		brandsLv.setAdapter(listAdapter);
		for (int i=0;i<brands.size();i++) {
			brandsLv.setItemChecked(i, true);
		}
		brandsLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					if (((CheckedTextView)view).isChecked()) {
						for (int i=0;i<brands.size();i++) {
							brandsLv.setItemChecked(i, true);
						}
					} else {
						for (int i=0;i<brands.size();i++) {
							brandsLv.setItemChecked(i, false);
						}
					}
				} else {
					if (!((CheckedTextView)view).isChecked()) {
						brandsLv.setItemChecked(0, false);
					}
				}
			}
		});
		Button buttonOK = (Button) dialoglayout.findViewById(R.id.btn_filter_ok);
		Button buttonCancel = (Button) dialoglayout.findViewById(R.id.btn_filter_cancel);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		dialog.setTitle(getResources().getString(R.string.filter));
		dialog.setView(dialoglayout);
		dialog.show();
		buttonOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//get string for product name
				String prodNameFilter = ((EditText)dialoglayout.findViewById(R.id.et_filter_product_name)).getText().toString();
				filter.setProductNameFilter(prodNameFilter);
				Debug.PrintError(this, "prodNameFilter: " + prodNameFilter);
				Integer minPriceFilter = ((SeekBar)dialoglayout.findViewById(R.id.filter_min_price)).getProgress()+minPrice;
				Integer maxPriceFilter = ((SeekBar)dialoglayout.findViewById(R.id.filter_max_price)).getProgress()+minPrice;
				filter.setMinPriceFilter(minPriceFilter);
				filter.setMaxPriceFilter(maxPriceFilter);
				Debug.PrintError(this, "minPriceFilter: " + minPriceFilter);
				Debug.PrintError(this, "maxPriceFilter: " + maxPriceFilter);
				SparseBooleanArray selectedItems = ((ListView)dialoglayout.findViewById(R.id.lv_brands)).getCheckedItemPositions();
				for (int i=0;i<selectedItems.size();i++) {
					if (selectedItems.get(i)) {
						filter.addBrandFilter(listAdapter.getItem(i));
						Debug.PrintError(this, "Position " + i + " ("+listAdapter.getItem(i)+")-> " + selectedItems.get(i));
					}
				}
				dialog.cancel();
				// FILTER
				onNavigationItemSelected(currSelectedCat, 0);
			}
		});
		buttonCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});
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
		filterServerResp.addAction(Constants.Actions.DISPLAY_PRODUCT);
		filterServerResp.addAction(Constants.Actions.GET_PRODUCT);
		filterServerResp.addAction(Constants.Actions.SEARCH);
		registerReceiver(broadcastReceiver, filterServerResp);
	}
	
	@Override
	protected void onPause() {
		unregisterReceiver(broadcastReceiver);
		filter.reset();
		Debug.PrintDebug(this, "onPause");
		super.onPause();
	}
	
	protected void enterSubCategory(Category category) {
		
		Debug.PrintInfo(this, "Displaying category " + category.getName());
		
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
        mActionBar.setListNavigationCallbacks(adapter, this);
        
        mActionBar.setSelectedNavigationItem(preSelectedPosition);
	}
	
	protected void showProduct(Product produto) {
		Bundle bundle = new Bundle();
        bundle.putInt(Constants.Extras.PRODUCT, produto.getId());
        Intent intent = new Intent(this, ProductView.class);
        intent.putExtras(bundle);
        startActivity(intent);
	}
	
	@SuppressLint("NewApi")
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		
		currSelectedCat = itemPosition;
		
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
    		Debug.PrintWarning(NavigationList.this, selectedCat.getName() + " has produtos.");
    		productListFrag = new ProductListFragment();
    		productListFrag.getMaximumPrice();
    		Bundle bundle = new Bundle();
            bundle.putInt(Constants.Extras.CATEGORY, selectedCat.getId());
            bundle.putInt(Constants.Extras.PRODUCT_SORT, currSelectedSort);
            bundle.putParcelable(Constants.Extras.FILTER, filter);
            productListFrag.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(android.R.id.content, productListFrag).commit();
            viewingProductsList = true;
            if (android.os.Build.VERSION.SDK_INT > 11) {
                invalidateOptionsMenu();
            }
    	} else if (selectedCatHasSubCategories) {
    		Debug.PrintInfo(NavigationList.class, selectedCat.getName() + " has sub categories.");
        	CategoryListFragment categoryListFrag = new CategoryListFragment();
        	Bundle bundle = new Bundle();
            bundle.putInt(Constants.Extras.CATEGORY, selectedCat.getId());
        	categoryListFrag.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(android.R.id.content, categoryListFrag).commit();
            viewingProductsList = false;
            if (android.os.Build.VERSION.SDK_INT > 11) {
                invalidateOptionsMenu();
            }
    	} else {
    		Debug.PrintWarning(NavigationList.this, selectedCat.getName() + " has no information.");
    		CallWebServiceTask getCategoria = new CallWebServiceTask(Constants.Actions.GET_CATEGORY, false);
    		getCategoria.addParameter(Name.CATEGORIA_ID, selectedCat.getId());
    		getCategoria.execute();
    	}
    	return true;
	}

	@Override
	public void onBackPressed() {
		filter.reset();
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
