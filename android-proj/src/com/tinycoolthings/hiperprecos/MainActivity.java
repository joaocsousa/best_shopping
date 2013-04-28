package com.tinycoolthings.hiperprecos;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.tinycoolthings.hiperprecos.category.CategoryListPagerAdapater;
import com.tinycoolthings.hiperprecos.models.Categoria;
import com.tinycoolthings.hiperprecos.models.Hiper;
import com.tinycoolthings.hiperprecos.serverComm.CallWebServiceTask;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Constants.Server.Parameter.Name;
import com.tinycoolthings.hiperprecos.utils.Debug;
import com.tinycoolthings.hiperprecos.utils.Utils;

public class MainActivity extends SherlockFragmentActivity {

	private ActionBar mActionBar;
	private ViewPager mPager;
	private ActionBar.TabListener tabListener;

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Constants.Actions.GET_CATEGORIAS)) {
				String result = intent.getStringExtra(Constants.Extras.CATEGORIAS);
				try {
					JSONArray categoriasJSON = new JSONArray(result);
					for (int i=0;i<categoriasJSON.length();i++) {
						HiperPrecos.getInstance().addCategoria(categoriasJSON.getJSONObject(i));
					}
					populateHipers();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (intent.getAction().equals(Constants.Actions.GET_CATEGORIA)) {
				String result = intent.getStringExtra(Constants.Extras.CATEGORIA);
				try {
					JSONObject catJson = new JSONObject(result);
					Categoria categoria = HiperPrecos.getInstance().addCategoria(catJson);
					Debug.PrintWarning(MainActivity.this, "Received data for categoria " + categoria.getNome());
					enterSubCategoria(categoria);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (intent.getAction().equals(Constants.Actions.SEARCH)) {
				String result = intent.getStringExtra(Constants.Extras.SEARCH_RESULT);
				Debug.PrintDebug(this, result);
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		/** Getting a reference to action bar of this activity */
		mActionBar = getSupportActionBar();
 
        /** Set tab navigation mode */
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mActionBar.setDisplayShowTitleEnabled(true);
        
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		Debug.PrintDebug(this, "onResume");
		
		IntentFilter filterServerResp = new IntentFilter();
		filterServerResp.addAction(Constants.Actions.GET_CATEGORIAS);
		filterServerResp.addAction(Constants.Actions.GET_CATEGORIA);
		filterServerResp.addAction(Constants.Actions.SEARCH);
		registerReceiver(broadcastReceiver, filterServerResp);
		
		mActionBar.removeAllTabs();
		
		HiperPrecos.getInstance().clearHipers();
		
		// ADD HIPERS:
		HiperPrecos.getInstance().addHiper(new Hiper(1, "Continente"));
		HiperPrecos.getInstance().addHiper(new Hiper(2, "Jumbo"));
		////////////////////

        HiperPrecos.getInstance().setAppContext(this);
        
		CallWebServiceTask getCategorias = new CallWebServiceTask(Constants.Actions.GET_CATEGORIAS);
		getCategorias.addParameter(Name.CATEGORIA_PAI, -1);
		getCategorias.execute();
	}
	
	protected void enterSubCategoria(Categoria categoria) {
		
		Debug.PrintInfo(MainActivity.this, "Selected categoria -> " + categoria.getNome());
		
		Debug.PrintWarning(MainActivity.this, categoria.getNome() + " has subcategorias.");
        Intent intent = new Intent(MainActivity.this, NavigationList.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.Extras.CATEGORIA, categoria.getId());
        intent.putExtras(bundle);
        startActivity(intent);
        
	}

	private void populateHipers() throws JSONException {
		
		/** Getting a reference to ViewPager from the layout */
        mPager = new ViewPager(this);
        mPager.setId(Utils.getRandomInt());
        LayoutParams pagerParams = new LayoutParams();
        pagerParams.width = LayoutParams.MATCH_PARENT;
        pagerParams.height = LayoutParams.MATCH_PARENT;
        mPager.setLayoutParams(pagerParams);
       
        ((RelativeLayout)findViewById(R.id.MainLayout)).addView(mPager);
        
        /** Getting a reference to FragmentManager */
        FragmentManager fm = getSupportFragmentManager();
        
        /** Defining a listener for pageChange */
        ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                mActionBar.setSelectedNavigationItem(position);
                super.onPageSelected(position);
            }
        };
        
        /** Setting the pag
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {eChange listner to the viewPager */
        mPager.setOnPageChangeListener(pageChangeListener);
 
        /** Creating an instance of FragmentPagerAdapter */
        CategoryListPagerAdapater fragmentPagerAdapter = new CategoryListPagerAdapater(fm);
		
		/** Setting the FragmentPagerAdapter object to the viewPager object */
        mPager.setAdapter(fragmentPagerAdapter);
        
        /** Defining tab listener */
        tabListener = new ActionBar.TabListener() {
			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				mPager.setCurrentItem(tab.getPosition());
			}
			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {}
			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {}
        };
        
        /** Create Tabs */
        ArrayList<Hiper> hipers = HiperPrecos.getInstance().getHipers();
		for (int i=0;i<hipers.size();i++) {
			Hiper currHiper = hipers.get(i);
			String currHiperName = currHiper.getNome();
			/** Creating Tab */
	        Tab tab = mActionBar.newTab()
	                .setText(currHiperName)
	                .setTabListener(tabListener);
	 
	        mActionBar.addTab(tab);
		}
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
		inflater.inflate(R.menu.main, menu);
		// Get the SearchView and set the searchable configuration
	    SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
		
	    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

	    	@Override
	    	public boolean onQueryTextSubmit(String query) {
	            menu.findItem(R.id.menu_search).collapseActionView();
	    		HiperPrecos.getInstance().search(query);
	            return false;
	        }

	        @Override
	        public boolean onQueryTextChange(String newText) {
	            // suggestions go here
	            return false;
	        }
	    });
		
		return super.onCreateOptionsMenu(menu);
	}
	
}
