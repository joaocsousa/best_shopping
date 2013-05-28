package com.tinycoolthings.hiperprecos.product;

import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tinycoolthings.hiperprecos.HiperPrecos;
import com.tinycoolthings.hiperprecos.R;
import com.tinycoolthings.hiperprecos.models.Category;
import com.tinycoolthings.hiperprecos.models.Product;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Debug;
import com.tinycoolthings.hiperprecos.utils.Filter;

public class ProductList  extends SherlockFragmentActivity {

	private Filter filter = new Filter();
	private int currSelectedSort = Constants.Sort.NAME_ASCENDING;
	private ProductListFragment productListFrag;
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Constants.Actions.DISPLAY_PRODUCT)) {
				Debug.PrintInfo(this, "Displaying product...");
				Product selectedProd = HiperPrecos.getInstance().getProductById(intent.getIntExtra(Constants.Extras.PRODUCT, -1));
				showProduct(selectedProd);
			}
		}
	};
	
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (savedInstanceState == null) {
			ActionBar mActionBar = getSupportActionBar();
			mActionBar.setDisplayShowHomeEnabled(false);
			productListFrag = new ProductListFragment();
			Bundle bundle = getIntent().getExtras();
			Category category = HiperPrecos.getInstance().getCategoryById(bundle.getInt(Constants.Extras.CATEGORY));
			String title = "";
			Category parentCategory = category.getParentCat();
			if (parentCategory!=null) {
				HiperPrecos.getInstance().refreshCategory(parentCategory);
				if (!parentCategory.getName().equals("") && !parentCategory.getName().equals("null")) {
					title+=parentCategory.getName()+" > ";
				}
			}
			title+=category.getName();
			mActionBar.setTitle(title);
			bundle.putInt(Constants.Extras.PRODUCT_SORT, Constants.Sort.NAME_ASCENDING);
			bundle.putParcelable(Constants.Extras.FILTER, filter);
			productListFrag.setArguments(bundle);
			getSupportFragmentManager().beginTransaction().replace(android.R.id.content, productListFrag).commit();
        }
		
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		Debug.PrintDebug(this, "onResume");
		IntentFilter filterServerResp = new IntentFilter();
		filterServerResp.addAction(Constants.Actions.DISPLAY_PRODUCT);
		registerReceiver(broadcastReceiver, filterServerResp);
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
	   inflater.inflate(R.menu.product_list_menu, menu);
	   return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
        	case R.id.menu_sort:
	        	showSortMenu();
	        	break;
	        case R.id.menu_filter:
	        	showFilterMenu();
	        	break;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	protected void showProduct(Product product) {
		Bundle bundle = new Bundle();
        bundle.putInt(Constants.Extras.PRODUCT, product.getId());
        Intent intent = new Intent(this, ProductView.class);
        intent.putExtras(bundle);
        startActivity(intent);
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
					productListFrag.setSort(currSelectedSort);
				}
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void showFilterMenu() {
		final int maxPrice = (int)Math.ceil(productListFrag.getMaxPriceFilter());
		final int minPrice = (int)Math.round(productListFrag.getMinPriceFilter());
		LayoutInflater inflater = getLayoutInflater();
		final View dialoglayout = inflater.inflate(R.layout.filter_layout, null);
		((EditText)dialoglayout.findViewById(R.id.et_filter_product_name)).setText(filter.getProductNameFilter());
		final SeekBar minSeekBar = (SeekBar)dialoglayout.findViewById(R.id.filter_min_price);
		final SeekBar maxSeekBar = (SeekBar)dialoglayout.findViewById(R.id.filter_max_price);
		minSeekBar.setMax(maxPrice-1);
		maxSeekBar.setMax(maxPrice-1);
		final TextView tvMinPrice = (TextView)dialoglayout.findViewById(R.id.tv_filter_price_min);
		final TextView tvMaxPrice = (TextView)dialoglayout.findViewById(R.id.tv_filter_price_max);
		minSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (progress > (seekBar.getMax()-1)) {
					seekBar.setProgress((seekBar.getMax()-1));
					return;
				}
				int currMinPrice = progress+minPrice;
				tvMinPrice.setText(currMinPrice+" €");
				if (progress >= maxSeekBar.getProgress()) {
					maxSeekBar.setProgress(progress+1);
				}
			}
		});
		maxSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (progress < 1) {
					seekBar.setProgress(1);
					return;
				}
				int currMaxPrice = progress+minPrice;
				tvMaxPrice.setText(currMaxPrice+" €");
				if (progress <= minSeekBar.getProgress()) {
					minSeekBar.setProgress(progress-1);
				}
			}
		});
		if (filter.getMinPriceFilter()>0) {
			minSeekBar.setProgress(filter.getMinPriceFilter()-1);
		}
		if (filter.getMaxPriceFilter()>0.0) {
			maxSeekBar.setProgress(filter.getMaxPriceFilter()-1);
		} else {
			maxSeekBar.setProgress(maxPrice-1);
		}
		final List<String> brands = productListFrag.getBransFilter();
		java.util.Collections.sort(brands);
		brands.add(0, getResources().getString(R.string.all_brands));
		final ListView brandsLv = (ListView) dialoglayout.findViewById(R.id.lv_brands);
		final ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, brands);
		brandsLv.setAdapter(listAdapter);
		for (int i=0;i<brands.size();i++) {
			brandsLv.setItemChecked(i, filter.getBrandsFilter().indexOf(brands.get(i)) == -1);
		}
		brandsLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					if (((CheckedTextView)view).isChecked()) {
						for (int i=0;i<brands.size();i++) {
							filter.removeBrandFilter(listAdapter.getItem(i));
							brandsLv.setItemChecked(i, true);
						}
					} else {
						for (int i=0;i<brands.size();i++) {
							brandsLv.setItemChecked(i, false);
							filter.addBrandFilter(listAdapter.getItem(i));
						}
					}
				} else {
					if (!((CheckedTextView)view).isChecked()) {
						brandsLv.setItemChecked(0, false);
						filter.addBrandFilter(listAdapter.getItem(0));
					} else {
						filter.removeBrandFilter(listAdapter.getItem(0));
					}
				}
			}
		});
		Button buttonOK = (Button) dialoglayout.findViewById(R.id.btn_filter_ok);
		Button buttonCancel = (Button) dialoglayout.findViewById(R.id.btn_filter_cancel);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		dialog.setTitle(getResources().getString(R.string.filter));
		dialog.setView(dialoglayout);
		dialog.show();
		buttonOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//get string for product name
				String prodNameFilter = ((EditText)dialoglayout.findViewById(R.id.et_filter_product_name)).getText().toString().trim();
				filter.setProductNameFilter(prodNameFilter);
				Debug.PrintError(this, "prodNameFilter: " + prodNameFilter);
				Integer minPriceFilter = ((SeekBar)dialoglayout.findViewById(R.id.filter_min_price)).getProgress()+minPrice;
				Integer maxPriceFilter = ((SeekBar)dialoglayout.findViewById(R.id.filter_max_price)).getProgress()+minPrice;
				filter.setMinPriceFilter(minPriceFilter);
				filter.setMaxPriceFilter(maxPriceFilter);
				Debug.PrintError(this, "minPriceFilter: " + minPriceFilter);
				Debug.PrintError(this, "maxPriceFilter: " + maxPriceFilter);
				dialog.cancel();
				productListFrag.setFilter(filter);
			}
		});
		buttonCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});
	}
	
}
