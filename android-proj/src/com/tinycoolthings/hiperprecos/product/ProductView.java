package com.tinycoolthings.hiperprecos.product;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tinycoolthings.hiperprecos.HiperPrecos;
import com.tinycoolthings.hiperprecos.R;
import com.tinycoolthings.hiperprecos.models.Produto;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Debug;

public class ProductView extends SherlockFragmentActivity {

	Produto currProd = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (savedInstanceState == null) {
			ProductViewFragment productViewFrag = new ProductViewFragment();
			currProd = HiperPrecos.getInstance().getProdutoById(getIntent().getExtras().getInt(Constants.Extras.PRODUTO));
			productViewFrag.setArguments(getIntent().getExtras());
			getSupportFragmentManager().beginTransaction().replace(android.R.id.content, productViewFrag).commit();
        }
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	   MenuInflater inflater = getSupportMenuInflater();
	   inflater.inflate(R.menu.menu_product, menu);
	   return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
		    case R.id.menu_icon_browser:
		    	String prodUrl = currProd.getUrlPagina();
		    	if (!prodUrl.startsWith("http://") && !prodUrl.startsWith("https://")) {
		    		prodUrl = "http://" + prodUrl;
		    	}
		    	Debug.PrintInfo(this, "Opening: "+prodUrl);
		    	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(prodUrl));
		    	startActivity(browserIntent);
		        return true;
		    default:
		        return super.onOptionsItemSelected(item);
	    }
	}
}

