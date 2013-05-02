package com.tinycoolthings.hiperprecos.search;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tinycoolthings.hiperprecos.HiperPrecos;
import com.tinycoolthings.hiperprecos.R;
import com.tinycoolthings.hiperprecos.models.Categoria;
import com.tinycoolthings.hiperprecos.models.Produto;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Debug;

public class SearchResults extends SherlockFragmentActivity {
	
	ArrayList<Produto> produtos = new ArrayList<Produto>();
	ArrayList<Categoria> categorias = new ArrayList<Categoria>();
	
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
        
        HiperPrecos.getInstance().setAppContext(this);
        
        HiperPrecos.getInstance().setLatestProdSearch(produtos);
        HiperPrecos.getInstance().setLatestCatSearch(categorias);
        
        /** Creating an instance of FragmentPagerAdapter */
        final SearchPagerAdapter fragmentPagerAdapter = new SearchPagerAdapter(getSupportFragmentManager());
        
        /** Getting a reference to ViewPager from the layout */
        final ViewPager mPager = (ViewPager)findViewById(R.id.search_results_pager);
        
        /** Defining a listener for pageChange */
        ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
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
	}
	
	@Override
	protected void onPause() {
		Debug.PrintDebug(this, "onPause");
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_search, menu);		
		return super.onCreateOptionsMenu(menu);
	}
	
}
