package com.tinycoolthings.hiperprecos;

import java.util.ArrayList;

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
import com.tinycoolthings.hiperprecos.models.Product;
import com.tinycoolthings.hiperprecos.product.ProductListFragment;
import com.tinycoolthings.hiperprecos.product.ProductView;
import com.tinycoolthings.hiperprecos.search.SearchResults;
import com.tinycoolthings.hiperprecos.serverComm.CallWebServiceTask;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Constants.Server.Parameter.Name;
import com.tinycoolthings.hiperprecos.utils.Constants.Sort;
import com.tinycoolthings.hiperprecos.utils.Debug;
import com.tinycoolthings.hiperprecos.utils.Utils;

public class NavigationList extends SherlockFragmentActivity implements OnNavigationListener {

	private ActionBar mActionBar;

	private ArrayList<Category> categoriasListMenu = new ArrayList<Category>();

	private static boolean viewingProductsList = false;
	
	private static int currSelectedSort = Sort.NOME_ASCENDING;
	
	private static int currSelectedCat = 0;
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Constants.Actions.GET_CATEGORY)) {
				String result = intent.getStringExtra(Constants.Extras.CATEGORY);
				String origin = intent.getStringExtra(Constants.Extras.ORIGIN);
				try {
					JSONObject catJson = new JSONObject(result);
					int categoryID = HiperPrecos.getInstance().addCategory(catJson);
					if (NavigationList.class.getSimpleName().equals(origin)) {
						enterSubCategoria(categoryID);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (intent.getAction().equals(Constants.Actions.DISPLAY_CATEGORIA)) {
				enterSubCategoria(HiperPrecos.getInstance().getCategoriaById(intent.getIntExtra(Constants.Extras.CATEGORY, -1)));
			} else if (intent.getAction().equals(Constants.Actions.GET_PRODUTO)) {
				String result = intent.getStringExtra(Constants.Extras.PRODUTO);
				try {
					JSONObject prodJson = new JSONObject(result);
					Product produto = new Product(prodJson);
					Debug.PrintWarning(NavigationList.this, "Received data for produto " + produto.getId() + " - " + produto.getNome());
					Product existingProd = HiperPrecos.getInstance().getProdutoById(produto.getId());
					existingProd.merge(produto);
					showProduct(existingProd);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (intent.getAction().equals(Constants.Actions.DISPLAY_PRODUTO)) {
				Integer selectedProdID = intent.getIntExtra(Constants.Extras.PRODUTO, -1);
				Product selectedProd = HiperPrecos.getInstance().getProdutoById(selectedProdID);
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
		
		Category categoria = (Category) HiperPrecos.getInstance().getCategoriaById(bundle.getInt(Constants.Extras.CATEGORY));
		
		/** Getting a reference to action bar of this activity */
		mActionBar = getSupportActionBar();
		
		mActionBar.setDisplayHomeAsUpEnabled(true);
		
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		mActionBar.setDisplayShowTitleEnabled(false);
		
		HiperPrecos.getInstance().setAppContext(this);
		
		enterSubCategoria(categoria);
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getSupportMenuInflater();
//		inflater.inflate(R.menu.category_list_menu, menu);
//		return super.onCreateOptionsMenu(menu);
//	}
	
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
		filterServerResp.addAction(Constants.Actions.DISPLAY_CATEGORIA);
		filterServerResp.addAction(Constants.Actions.DISPLAY_PRODUTO);
		filterServerResp.addAction(Constants.Actions.GET_PRODUTO);
		filterServerResp.addAction(Constants.Actions.SEARCH);
		registerReceiver(broadcastReceiver, filterServerResp);
	}
	
	@Override
	protected void onPause() {
		unregisterReceiver(broadcastReceiver);
		Debug.PrintDebug(this, "onPause");
		super.onPause();
	}
	
	protected void enterSubCategoria(Category categoria) {
		
		Debug.PrintInfo(this, "Displaying categoria " + categoria.getNome());
		
		ArrayList<String> categorias = new ArrayList<String>();
		int preSelectedPosition = -1;
		// vai buscar os siblings da categoria pai
		categoriasListMenu = categoria.getSiblings();
		
		Utils.sortCategoriesByName(categoriasListMenu, false);
		
		Debug.PrintError(this, "Siblings: " + categoriasListMenu.size());
		for (int j=0;j<categoriasListMenu.size();j++) {
			if (categoria.getId().equals(categoriasListMenu.get(j).getId())) {
				preSelectedPosition = j;
			}
			categorias.add(categoriasListMenu.get(j).getNome());
		}
	
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActionBar.getThemedContext(), R.layout.sherlock_spinner_dropdown_item, categorias);
		
		/** Defining Navigation listener */
        mActionBar.setListNavigationCallbacks(adapter, this);
        
        mActionBar.setSelectedNavigationItem(preSelectedPosition);
	}
	
	protected void showProduct(Product produto) {
		Bundle bundle = new Bundle();
        bundle.putInt(Constants.Extras.PRODUTO, produto.getId());
        Intent intent = new Intent(this, ProductView.class);
        intent.putExtras(bundle);
        startActivity(intent);
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		currSelectedCat = itemPosition;
		Category selectedCat = categoriasListMenu.get(itemPosition);
    	Debug.PrintInfo(NavigationList.this, "Selected categoria -> " + selectedCat.getNome());
    	if (selectedCat.hasProdutos()) {
    		Debug.PrintWarning(NavigationList.this, selectedCat.getNome() + " has produtos.");
    		ProductListFragment productListFrag = new ProductListFragment();
    		Bundle bundle = new Bundle();
            bundle.putInt(Constants.Extras.CATEGORY, selectedCat.getId());
            bundle.putInt(Constants.Extras.PRODUTO_SORT, currSelectedSort);
            productListFrag.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(android.R.id.content, productListFrag).commit();
            viewingProductsList = true;
            if (android.os.Build.VERSION.SDK_INT > 11) {
                invalidateOptionsMenu();
            }
    	} else if (selectedCat.hasSubCategorias()) {
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
    		Debug.PrintWarning(NavigationList.this, selectedCat.getNome() + " has no information.");
    		CallWebServiceTask getCategoria = new CallWebServiceTask(Constants.Actions.GET_CATEGORY);
    		getCategoria.addParameter(Name.CATEGORIA_ID, selectedCat.getId());
    		getCategoria.execute();
    	}
    	return true;
	}
	
	@Override
	public void onBackPressed() {
		Category selectedCat = categoriasListMenu.get(getSupportActionBar().getSelectedNavigationIndex());
		if (selectedCat.hasCategoriaPai()) {
			enterSubCategoria(selectedCat.getCategoriaPai());
		} else {
			exitToMainMenu();
			return;
		}
	}
	
}
