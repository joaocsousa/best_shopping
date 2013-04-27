package com.tinycoolthings.hiperprecos;

import java.util.ArrayList;

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
import com.tinycoolthings.hiperprecos.models.Categoria;
import com.tinycoolthings.hiperprecos.models.Produto;
import com.tinycoolthings.hiperprecos.serverComm.CallWebServiceTask;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Constants.Server.Parameter.Name;
import com.tinycoolthings.hiperprecos.utils.Debug;

public class NavigationList extends SherlockFragmentActivity implements OnNavigationListener {

	private ActionBar mActionBar;

	private ArrayList<Categoria> categoriasListMenu = new ArrayList<Categoria>();

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Constants.Actions.GET_CATEGORIA)) {
				String result = intent.getStringExtra(Constants.Extras.CATEGORIA);
				try {
					JSONObject catJson = new JSONObject(result);
					Categoria categoria = HiperPrecos.getInstance().addCategoria(catJson);
					Debug.PrintWarning(NavigationList.this, "Received data for categoria " + categoria.getNome());
					enterSubCategoria(categoria);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (intent.getAction().equals(Constants.Actions.DISPLAY_CATEGORIA)) {
				enterSubCategoria(HiperPrecos.getInstance().getCategoriaById(intent.getIntExtra(Constants.Extras.CATEGORIA, -1)));
			} else if (intent.getAction().equals(Constants.Actions.GET_PRODUTO)) {
				String result = intent.getStringExtra(Constants.Extras.PRODUTO);
				try {
					JSONObject prodJson = new JSONObject(result);
					Produto produto = new Produto(prodJson);
					Debug.PrintWarning(NavigationList.this, "Received data for produto " + produto.getId() + " - " + produto.getNome());
					Produto existingProd = HiperPrecos.getInstance().getProdutoById(produto.getId());
					existingProd.merge(produto);
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
	        	exitToMainMenu();
	            return true;
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
		filterServerResp.addAction(Constants.Actions.GET_CATEGORIA);
		filterServerResp.addAction(Constants.Actions.DISPLAY_CATEGORIA);
		filterServerResp.addAction(Constants.Actions.DISPLAY_PRODUTO);
		filterServerResp.addAction(Constants.Actions.GET_PRODUTO);
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
		int preSelectedPosition = -1;
		// vai buscar os siblings da categoria pai
		categoriasListMenu = categoria.getSiblings();
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
	
	protected void showProduct(Produto produto) {
		ProductViewFragment productViewFrag = new ProductViewFragment();
    	Bundle bundle = new Bundle();
        bundle.putInt(Constants.Extras.PRODUTO, produto.getId());
        productViewFrag.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, productViewFrag).commit();
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		Categoria selectedCat = categoriasListMenu.get(itemPosition);
    	Debug.PrintInfo(NavigationList.this, "Selected categoria -> " + selectedCat.getNome());
    	if (selectedCat.hasProdutos()) {
    		Debug.PrintWarning(NavigationList.this, selectedCat.getNome() + " has produtos.");
    		ProductListFragment productListFrag = new ProductListFragment();
    		Bundle bundle = new Bundle();
            bundle.putInt(Constants.Extras.CATEGORIA, selectedCat.getId());
            productListFrag.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(android.R.id.content, productListFrag).commit();
    	} else if (selectedCat.hasSubCategorias()) {
        	CategoryListFragment categoryListFrag = new CategoryListFragment();
        	Bundle bundle = new Bundle();
            bundle.putInt(Constants.Extras.CATEGORIA, selectedCat.getId());
        	categoryListFrag.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(android.R.id.content, categoryListFrag).commit();
    	} else {
    		Debug.PrintWarning(NavigationList.this, selectedCat.getNome() + " has no information.");
    		CallWebServiceTask getCategoria = new CallWebServiceTask(Constants.Actions.GET_CATEGORIA);
    		getCategoria.addParameter(Name.CATEGORIA_ID, selectedCat.getId());
    		getCategoria.execute();
    	}
    	return true;
	}
	
	@Override
	public void onBackPressed() {
		Categoria selectedCat = categoriasListMenu.get(getSupportActionBar().getSelectedNavigationIndex());
		if (selectedCat.hasCategoriaPai()) {
			enterSubCategoria(selectedCat.getCategoriaPai());
		} else {
			exitToMainMenu();
			return;
		}
	}
	
}
