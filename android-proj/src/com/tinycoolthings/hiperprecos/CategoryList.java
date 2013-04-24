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
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tinycoolthings.hiperprecos.models.Categoria;
import com.tinycoolthings.hiperprecos.serverComm.CallWebServiceTask;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Constants.Server.Parameter.Name;
import com.tinycoolthings.hiperprecos.utils.Debug;

public class CategoryList extends SherlockFragmentActivity {

	private ActionBar mActionBar;

	private int backStackCount = 0;
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Constants.Actions.GET_CATEGORIA)) {
				String result = intent.getStringExtra(Constants.Extras.CATEGORIA);
				try {
					JSONObject catJson = new JSONObject(result);
					Categoria categoria = HiperPrecos.getInstance().addCategoria(catJson);
					Debug.PrintWarning(CategoryList.this, "Received data for categoria " + categoria.getNome());
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

		final Bundle bundle = getIntent().getExtras();
		
		Categoria categoria = (Categoria) HiperPrecos.getInstance().getCategoriaById(bundle.getInt(Constants.Extras.CATEGORIA));
		
		/** Getting a reference to action bar of this activity */
		mActionBar = getSupportActionBar();
		
		mActionBar.setDisplayHomeAsUpEnabled(true);
		
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		mActionBar.setDisplayShowTitleEnabled(false);
	
		getSupportFragmentManager().addOnBackStackChangedListener(new OnBackStackChangedListener() {
			
			@Override
			public void onBackStackChanged() {
				int stackCount = getSupportFragmentManager().getBackStackEntryCount();
				Debug.PrintError(CategoryList.this, "Stack changed: " + stackCount);
				if (stackCount < backStackCount) {
					Debug.PrintError(CategoryList.this, "Returned 1 to behind");
					if (stackCount == 1) {
						finish();
						return;
					}
				}
				backStackCount = stackCount;
			}
		});
		
		HiperPrecos.setAppContext(this);
		
		enterSubCategoria(categoria);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	   MenuInflater inflater = getSupportMenuInflater();
	   inflater.inflate(R.menu.main, menu);
	   return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // This is called when the Home (Up) button is pressed
	            // in the Action Bar.
	            Intent parentActivityIntent = new Intent(this, MainActivity.class);
	            parentActivityIntent.addFlags(
	                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
	                    Intent.FLAG_ACTIVITY_NEW_TASK);
	            startActivity(parentActivityIntent);
	            finish();
	            return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Debug.PrintDebug(this, "onResume");
		IntentFilter filterServerResp = new IntentFilter();
		filterServerResp.addAction(Constants.Actions.GET_CATEGORIA);
		registerReceiver(broadcastReceiver, filterServerResp);
	}
	
	@Override
	protected void onPause() {
		unregisterReceiver(broadcastReceiver);
		Debug.PrintDebug(this, "onPause");
		super.onPause();
	}
	
	protected void enterSubCategoria(Categoria categoria) {
		
		Debug.PrintInfo(this, "Displaying categoria " + categoria.getNome());		
		
		ArrayList<String> categorias = new ArrayList<String>();
		final SparseArray<Categoria> catPos = new SparseArray<Categoria>();
		int preSelectedPosition = -1;
		// vai buscar os siblings da categoria pai
		ArrayList<Categoria> irmaos = categoria.getSiblings();
		for (int j=0;j<irmaos.size();j++) {
			if (categoria.getId().equals(irmaos.get(j).getId())) {
				preSelectedPosition = j;
			}
			catPos.put(j, irmaos.get(j));
			categorias.add(irmaos.get(j).getNome());
		}
		
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActionBar.getThemedContext(), R.layout.sherlock_spinner_dropdown_item, categorias);
		
		/** Defining Navigation listener */
        mActionBar.setListNavigationCallbacks(adapter, new OnNavigationListener() {
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
            	Categoria selectedCat = catPos.get(itemPosition);
            	Debug.PrintInfo(CategoryList.this, "Selected categoria -> " + selectedCat.getNome());
            	if (selectedCat.hasProdutos()) {
            		Debug.PrintWarning(CategoryList.this, selectedCat.getNome() + " has produtos.");
            		ProductListFragment productListFrag = new ProductListFragment();
            		Bundle bundle = new Bundle();
                    bundle.putInt(Constants.Extras.CATEGORIA, selectedCat.getId());
                    productListFrag.setArguments(bundle);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(android.R.id.content, productListFrag);
                    transaction.addToBackStack(null);
                	transaction.commit();
            	} else if (selectedCat.hasSubCategorias()) {
                	CategoryListFragment categoryListFrag = new CategoryListFragment();
                	Bundle bundle = new Bundle();
                    bundle.putInt(Constants.Extras.CATEGORIA, selectedCat.getId());
                	categoryListFrag.setArguments(bundle);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(android.R.id.content, categoryListFrag);
                    transaction.addToBackStack(null);
                	transaction.commit();
            	} else {
            		Debug.PrintWarning(CategoryList.this, selectedCat.getNome() + " has no information.");
            		CallWebServiceTask getCategoria = new CallWebServiceTask(Constants.Actions.GET_CATEGORIA);
            		getCategoria.addParameter(Name.CATEGORIA_ID, selectedCat.getId());
            		getCategoria.execute();
            	}
            	return true;
            }
        });
        
        mActionBar.setSelectedNavigationItem(preSelectedPosition);
	}
	
}
