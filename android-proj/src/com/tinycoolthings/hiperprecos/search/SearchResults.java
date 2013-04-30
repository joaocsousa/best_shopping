package com.tinycoolthings.hiperprecos.search;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tinycoolthings.hiperprecos.HiperPrecos;
import com.tinycoolthings.hiperprecos.R;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Debug;
import com.viewpagerindicator.TabPageIndicator;

public class SearchResults extends SherlockFragmentActivity {
	
	ArrayList<Integer> produtos = new ArrayList<Integer>();
	ArrayList<Integer> categorias = new ArrayList<Integer>();
	
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
				categorias.add(HiperPrecos.getInstance().addCategoria(catsJSON.getJSONObject(i)).getId());
			}
			
			for (int i=0;i<prodMarcaJSON.length();i++) {
				produtos.add(HiperPrecos.getInstance().addProduto(prodMarcaJSON.getJSONObject(i)).getId());
			}
			
			for (int i=0;i<prodNomeJSON.length();i++) {
				produtos.add(HiperPrecos.getInstance().addProduto(prodNomeJSON.getJSONObject(i)).getId());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		/** Getting a reference to action bar of this activity */
		ActionBar mActionBar = getSupportActionBar();
 
        /** Set tab navigation mode */
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        mActionBar.setDisplayShowTitleEnabled(false);
        
        HiperPrecos.getInstance().setAppContext(this);
        
        /** Getting a reference to ViewPager from the layout */
        ViewPager mPager = (ViewPager)findViewById(R.id.search_results_pager);
     
        /** Creating an instance of FragmentPagerAdapter */
        final SearchPagerAdapter fragmentPagerAdapter = new SearchPagerAdapter(getSupportFragmentManager());
	
        fragmentPagerAdapter.setContent(categorias, produtos);
        
		/** Setting the FragmentPagerAdapter object to the viewPager object */
        mPager.setAdapter(fragmentPagerAdapter);

        TabPageIndicator tabs = (TabPageIndicator)findViewById(R.id.search_results_tabs);
        
        tabs.setViewPager(mPager);
    	
        ArrayList<String> options = new ArrayList<String>();
        options.add("Produtos");
        options.add("Categorias");
		
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActionBar.getThemedContext(), R.layout.sherlock_spinner_dropdown_item, options);
		
		/** Defining Navigation listener */
        mActionBar.setListNavigationCallbacks(adapter, new OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				if (itemPosition == 0) {
					//produtos
					Debug.PrintInfo(this, "Selected Produtos");
					fragmentPagerAdapter.setContentType(SearchPagerAdapter.TYPE_PRODUTOS);
				} else if (itemPosition == 1) {
					//categorias
					Debug.PrintInfo(this, "Selected Categorias");
					fragmentPagerAdapter.setContentType(SearchPagerAdapter.TYPE_CATEGORIAS);
				}
				return false;
			}
		});
        
        mActionBar.setSelectedNavigationItem(0);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		Debug.PrintDebug(this, "onResume");
	}
	
	@Override
	protected void onPause() {
		Debug.PrintDebug(this, "onPause");
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);		
		return super.onCreateOptionsMenu(menu);
	}
	
}
