package com.tinycoolthings.hiperprecos;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
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
		registerReceiver(broadcastReceiver, filterServerResp);
		
		mActionBar.removeAllTabs();
		
		HiperPrecos.getInstance().clearHipers();
		
		// ADD HIPERS:
		HiperPrecos.getInstance().addHiper(new Hiper(1, "Continente"));
		HiperPrecos.getInstance().addHiper(new Hiper(2, "Jumbo"));
		////////////////////

        HiperPrecos.setAppContext(this);
        
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
		
//		mActionBar.setDisplayShowHomeEnabled(true);
//		mActionBar.setDisplayShowTitleEnabled(false);
//		mActionBar.setDisplayHomeAsUpEnabled(true);
//		
//		/** Hide view pager */
//		if (mPager.getVisibility() != ViewPager.GONE) {
//			mPager.setVisibility(ViewPager.GONE);
//		}
//		
//		/** Hide Tabs and Set Navigation List */
//		if (mActionBar.getNavigationMode() != ActionBar.NAVIGATION_MODE_LIST) {
//			
//		}
//		
//		ArrayList<String> categorias = new ArrayList<String>();
//		final SparseArray<Categoria> catPos = new SparseArray<Categoria>();
//		int preSelectedPosition = -1;
//		// vai buscar os siblings da categoria pai
//		ArrayList<Categoria> irmaos = categoria.getSiblings();
//		for (int j=0;j<irmaos.size();j++) {
//			if (categoria.getId().equals(irmaos.get(j).getId())) {
//				preSelectedPosition = j;
//			}
//			catPos.put(j, irmaos.get(j));
//			categorias.add(irmaos.get(j).getNome());
//		}
//		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActionBar.getThemedContext(), R.layout.sherlock_spinner_dropdown_item, categorias);
//		/** Defining Navigation listener */
//        OnNavigationListener navigationListener = new OnNavigationListener() {
//            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
//            	Categoria selectedCat = catPos.get(itemPosition);
            	
//            	if (selectedCat.hasProdutos()) {
//            		Debug.PrintWarning(MainActivity.this, selectedCat.getNome() + " has produtos.");
//            		ProductListFragment productListFrag = new ProductListFragment();
//                    Bundle bundle = new Bundle();
//                    bundle.putParcelableArrayList(Constants.Extras.PRODUTOS, selectedCat.getProdutos());
//                    productListFrag.setArguments(bundle);
//                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                    transaction.replace(R.id.MainLayout, productListFrag);
//                    transaction.addToBackStack(null);
//                	transaction.commit();
//            	} else if (selectedCat.hasSubCategorias()) {
            		
//            	} else {
//            		Debug.PrintWarning(MainActivity.this, selectedCat.getNome() + " has no information.");
//            		CallWebServiceTask getCategorias = new CallWebServiceTask(MainActivity.this, Constants.Actions.GET_CATEGORIA);
//            		getCategorias.addParameter(Name.CATEGORIA_ID, selectedCat.getId());
//            		getCategorias.execute();
//            	}
//            	return true;
//            }
//        };
//        mActionBar.setListNavigationCallbacks(adapter, navigationListener);
//        mActionBar.setSelectedNavigationItem(preSelectedPosition);
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
        
        /** Setting the pageChange listner to the viewPager */
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
	public boolean onCreateOptionsMenu(Menu menu) {
	   MenuInflater inflater = getSupportMenuInflater();
	   inflater.inflate(R.menu.main, menu);
	   return true;
	}
	
}
