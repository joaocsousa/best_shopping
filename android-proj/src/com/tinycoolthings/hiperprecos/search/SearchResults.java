package com.tinycoolthings.hiperprecos.search;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.tinycoolthings.hiperprecos.HiperPrecos;
import com.tinycoolthings.hiperprecos.MainActivity;
import com.tinycoolthings.hiperprecos.R;
import com.tinycoolthings.hiperprecos.models.Categoria;
import com.tinycoolthings.hiperprecos.models.Produto;
import com.tinycoolthings.hiperprecos.product.ProductView;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Constants.Sort;
import com.tinycoolthings.hiperprecos.utils.Debug;
import com.tinycoolthings.hiperprecos.utils.Utils;

public class SearchResults extends SherlockFragmentActivity {
	
	ArrayList<Produto> produtos = new ArrayList<Produto>();
	ArrayList<Categoria> categorias = new ArrayList<Categoria>();
	
	private static int[] currSelectedSort = new int[] {Sort.NOME_ASCENDING, Sort.NOME_ASCENDING};
	
	private static boolean viewingProductsList = true;
	
	private SearchPagerAdapter fragmentPagerAdapter = null;
	
	private ViewPager mPager = null;
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Constants.Actions.GET_PRODUTO)) {
				String result = intent.getStringExtra(Constants.Extras.PRODUTO);
				try {
					JSONObject prodJson = new JSONObject(result);
					Produto produto = HiperPrecos.getInstance().addProduto(prodJson);
					Debug.PrintWarning(SearchResults.this, "Received data for produto " + produto.getId() + " - " + produto.getNome());
					Produto existingProd = HiperPrecos.getInstance().getProdutoById(produto.getId());
					if (existingProd!=null) {
						existingProd.merge(produto);
					} else {
						existingProd = produto;
					}
					showProduct(existingProd);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (intent.getAction().equals(Constants.Actions.DISPLAY_PRODUTO)) {
				Integer selectedProdID = intent.getIntExtra(Constants.Extras.PRODUTO, -1);
				Produto selectedProd = HiperPrecos.getInstance().getProdutoById(selectedProdID);
				showProduct(selectedProd);
			} else if (intent.getAction().equals(Constants.Actions.SEARCH)) {
				String result = intent.getStringExtra(Constants.Extras.SEARCH_RESULT);
				Debug.PrintDebug(this, result);
				Intent searchResultsIntent = new Intent(SearchResults.this, SearchResults.class);
				searchResultsIntent.addFlags(
		                Intent.FLAG_ACTIVITY_CLEAR_TOP |
		                Intent.FLAG_ACTIVITY_NEW_TASK);
				searchResultsIntent.putExtras(intent);
				startActivity(searchResultsIntent);
			}
		}
	};
	
	protected void showProduct(Produto produto) {
		Bundle bundle = new Bundle();
        bundle.putInt(Constants.Extras.PRODUTO, produto.getId());
        Intent intent = new Intent(this, ProductView.class);
        intent.putExtras(bundle);
        startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.search_layout);
		
		String searchRes = getIntent().getStringExtra(Constants.Extras.SEARCH_RESULT);
		try {
			JSONObject searchJSON = new JSONObject(searchRes);
			JSONArray prodMarcaJSON = searchJSON.getJSONArray("prodPorMarca");
			JSONArray prodNomeJSON = searchJSON.getJSONArray("prodPorNome");
			JSONArray catsJSON = searchJSON.getJSONArray("categorias");
			
			for (int i=0;i<catsJSON.length();i++) {
				categorias.add(HiperPrecos.getInstance().addCategoria(catsJSON.getJSONObject(i)));
			}
			
			for (int i=0;i<prodMarcaJSON.length();i++) {
				produtos.add(HiperPrecos.getInstance().addProduto(prodMarcaJSON.getJSONObject(i)));
			}
			
			for (int i=0;i<prodNomeJSON.length();i++) {
				produtos.add(HiperPrecos.getInstance().addProduto(prodNomeJSON.getJSONObject(i)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		/** Getting a reference to action bar of this activity */
		final ActionBar mActionBar = getSupportActionBar();
 
        /** Set tab navigation mode */
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mActionBar.setDisplayShowTitleEnabled(false);
        
        mActionBar.setDisplayHomeAsUpEnabled(true);
        
        HiperPrecos.getInstance().setAppContext(this);
        
        Utils.sortProdutos(produtos, Sort.NOME_ASCENDING);
        Utils.sortCategoriesByName(categorias, false);
        
        HiperPrecos.getInstance().setLatestProdSearch(produtos);
        HiperPrecos.getInstance().setLatestCatSearch(categorias);
        
        /** Creating an instance of FragmentPagerAdapter */
        fragmentPagerAdapter = new SearchPagerAdapter(getSupportFragmentManager());
        
        /** Getting a reference to ViewPager from the layout */
        mPager = (ViewPager)findViewById(R.id.search_results_pager);
        
        /** Defining a listener for pageChange */
        ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
            	viewingProductsList = (position == 0);
            	mActionBar.setSelectedNavigationItem(position);
                super.onPageSelected(position);
            }
        };
        
        mPager.setOnPageChangeListener(pageChangeListener);
        
        /** Setting the FragmentPagerAdapter object to the viewPager object */
        mPager.setAdapter(fragmentPagerAdapter);
        
        /** Defining tab listener */
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				mPager.setCurrentItem(tab.getPosition());
			}
			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {}
			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {}
        };
        
        Tab tabProds = mActionBar.newTab().setText("Produtos").setTabListener(tabListener);
        Tab tabCats = mActionBar.newTab().setText("Categorias").setTabListener(tabListener);
		mActionBar.addTab(tabProds);
        mActionBar.addTab(tabCats);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		Debug.PrintDebug(this, "onResume");
		IntentFilter filterServerResp = new IntentFilter();
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

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_search, menu);
		final MenuItem searchMenuItem = menu.findItem( R.id.menu_search); // get my MenuItem with placeholder submenu
		// Get the SearchView and set the searchable configuration
	    SearchView searchView = (SearchView) searchMenuItem.getActionView();
		
	    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
	    	@Override
	    	public boolean onQueryTextSubmit(String query) {
	    		if (Utils.validSearch(query)) {
	    			searchMenuItem.collapseActionView();
	    		}
	    		HiperPrecos.getInstance().search(query);
	            return false;
	        }
	        @Override
	        public boolean onQueryTextChange(String newText) {
				return false;
			}
	    });
	    searchMenuItem.expandActionView();
	    ((SearchView)searchMenuItem.getActionView()).clearFocus();
	    ((SearchView)searchMenuItem.getActionView()).setQuery(HiperPrecos.getInstance().getLatestSearchTerm(), false);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	        	Intent parentActivityIntent = new Intent(this, MainActivity.class);
	            parentActivityIntent.addFlags(
	                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
	                    Intent.FLAG_ACTIVITY_NEW_TASK);
	            startActivity(parentActivityIntent);
	            finish();
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
		final int sortType = viewingProductsList ? 0 : 1;
		int selected = currSelectedSort[sortType];
		String[] options = null;
		if (viewingProductsList) {
			options = new String[] {
					getResources().getString(R.string.nome_asc),
					getResources().getString(R.string.nome_desc),
					getResources().getString(R.string.marca_asc),
					getResources().getString(R.string.marca_desc),
					getResources().getString(R.string.preco_asc),
					getResources().getString(R.string.preco_desc)
			};
		} else {
			options = new String[] {
					getResources().getString(R.string.nome_asc),
					getResources().getString(R.string.nome_desc)
			};
		}
		builder.setSingleChoiceItems( options, selected, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,int which) {
				if (currSelectedSort[sortType] != which) {
					currSelectedSort[sortType] = which;
					
					if (viewingProductsList) {
						Utils.sortProdutos(produtos, currSelectedSort[sortType]);
						HiperPrecos.getInstance().setLatestProdSearch(produtos);
					} else {
						Utils.sortCategoriesByName(categorias, (currSelectedSort[sortType] == 1));
						HiperPrecos.getInstance().setLatestCatSearch(categorias);
					}
					Debug.PrintError(this, "DATA CHANGED");
					fragmentPagerAdapter.notifyDataSetChanged();
				}
				dialog.dismiss();
			    mPager.requestFocus();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
}
