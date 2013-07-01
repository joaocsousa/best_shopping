package com.tinycoolthings.hiperprecos.search;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.tinycoolthings.hiperprecos.HiperPrecos;
import com.tinycoolthings.hiperprecos.MainActivity;
import com.tinycoolthings.hiperprecos.R;
import com.tinycoolthings.hiperprecos.models.Product;
import com.tinycoolthings.hiperprecos.product.ProductView;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Debug;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchResults extends SherlockFragmentActivity {

    private final ArrayList<Product> products = new ArrayList<Product>();
	
	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Constants.Actions.GET_PRODUCT)) {
				String result = intent.getStringExtra(Constants.Extras.PRODUCT);
				try {
					JSONObject prodJson = new JSONObject(result);
					Product product = HiperPrecos.getInstance().addProduct(prodJson);
					Debug.PrintWarning(SearchResults.this, "Received data for produto " + product.getId() + " - " + product.getName());
					showProduct(product);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (intent.getAction().equals(Constants.Actions.DISPLAY_PRODUCT)) {
				Integer selectedProdID = intent.getIntExtra(Constants.Extras.PRODUCT, -1);
				Product selectedProd = HiperPrecos.getInstance().getProductById(selectedProdID);
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
	
	protected void showProduct(Product produto) {
		Bundle bundle = new Bundle();
        bundle.putInt(Constants.Extras.PRODUCT, produto.getId());
        Intent intent = new Intent(this, ProductView.class);
        intent.putExtras(bundle);
        startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.search_layout);
		
        HiperPrecos.getInstance().setAppContext(this);
        
        HiperPrecos.getInstance().showWaitingDialog();
		
		String searchRes = getIntent().getStringExtra(Constants.Extras.SEARCH_RESULT);
		
		Debug.PrintDebug(this, searchRes);
		
		new SaveResults(searchRes).execute();
		
		/** Getting a reference to action bar of this activity */
		ActionBar mActionBar = getSupportActionBar();

		mActionBar.setTitle(HiperPrecos.getInstance().getLatestSearchTerm());
		
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
		

	}

	@Override
	protected void onResume() {
		super.onResume();
		Debug.PrintDebug(this, "onResume");
		IntentFilter filterServerResp = new IntentFilter();
		filterServerResp.addAction(Constants.Actions.DISPLAY_PRODUCT);
		filterServerResp.addAction(Constants.Actions.GET_PRODUCT);
		filterServerResp.addAction(Constants.Actions.SEARCH);
		registerReceiver(broadcastReceiver, filterServerResp);
		HiperPrecos.getInstance().setAppContext(SearchResults.this);
	}
	
	@Override
	protected void onPause() {
		unregisterReceiver(broadcastReceiver);
		Debug.PrintDebug(this, "onPause");
		super.onPause();
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
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	private class SaveResults extends AsyncTask<Void, Void, Void> {

		private String searchRes = "";
		
		public SaveResults(String searchRes) {
			this.searchRes = searchRes;
		}
		
        @Override
        protected Void doInBackground(Void... params) {
			try {
				JSONObject searchJSON = new JSONObject(searchRes);
				JSONArray prodMarcaJSON = searchJSON.getJSONArray("prodPorMarca");
				JSONArray prodNomeJSON = searchJSON.getJSONArray("prodPorNome");
				
				for (int i=0;i<prodMarcaJSON.length();i++) {
					products.add(HiperPrecos.getInstance().addProduct(prodMarcaJSON.getJSONObject(i)));
				}
				
				for (int i=0;i<prodNomeJSON.length();i++) {
					products.add(HiperPrecos.getInstance().addProduct(prodNomeJSON.getJSONObject(i)));
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			} 
			return null;
        }

        @Override
        protected void onPostExecute(Void params) {
        	if (products.size()>0) {
        		SearchResultFragment searchResultFrag = new SearchResultFragment();
                searchResultFrag.setContents(products);
        		getSupportFragmentManager().beginTransaction().replace(android.R.id.content, searchResultFrag).commit();
        	} else {
        		NoResultsFragment noResultsFrag = new NoResultsFragment();
        		getSupportFragmentManager().beginTransaction().replace(android.R.id.content, noResultsFrag).commit();
        	}
        	
			HiperPrecos.getInstance().hideWaitingDialog();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
  }   
	
}
