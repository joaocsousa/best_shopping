package com.tinycoolthings.hiperprecos.product;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.tinycoolthings.double_seekbar.DoubleSeekBar;
import com.tinycoolthings.hiperprecos.HiperPrecos;
import com.tinycoolthings.hiperprecos.R;
import com.tinycoolthings.hiperprecos.models.Category;
import com.tinycoolthings.hiperprecos.models.Product;
import com.tinycoolthings.hiperprecos.search.SearchResults;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Debug;
import com.tinycoolthings.hiperprecos.utils.Filter;
import com.tinycoolthings.hiperprecos.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ProductList extends SherlockFragmentActivity {

    private Filter currFilter = new Filter();
    private Filter originalFilter = null;
    private int currSelectedSort = Constants.Sort.NAME_ASCENDING;
    private ProductListFragment productListFrag;
    private int origMinPrice = 0;
    private int origMaxPrice = 0;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.Actions.DISPLAY_PRODUCT)) {
                Debug.PrintInfo(this, "Displaying product...");
                Product selectedProd = HiperPrecos.getInstance().getProductById(intent.getIntExtra(Constants.Extras.PRODUCT, -1));
                showProduct(selectedProd);
            } else if (intent.getAction().equals(Constants.Actions.SEARCH)) {
                Debug.PrintInfo(ProductList.this, "Received search result.");
                Intent searchResultsIntent = new Intent(ProductList.this, SearchResults.class);
                searchResultsIntent.putExtras(intent);
                startActivity(searchResultsIntent);
                HiperPrecos.getInstance().hideWaitingDialog();
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
            title += category.getName();
            mActionBar.setTitle(title);
            bundle.putInt(Constants.Extras.PRODUCT_SORT, Constants.Sort.NAME_ASCENDING);
            bundle.putParcelable(Constants.Extras.FILTER, currFilter);
            productListFrag.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(android.R.id.content, productListFrag).commit();
        }

    }

    ;

    @Override
    protected void onResume() {
        super.onResume();
        Debug.PrintDebug(this, "onResume");
        IntentFilter filterServerResp = new IntentFilter();
        filterServerResp.addAction(Constants.Actions.DISPLAY_PRODUCT);
        filterServerResp.addAction(Constants.Actions.SEARCH);
        registerReceiver(broadcastReceiver, filterServerResp);
        HiperPrecos.getInstance().setAppContext(this);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(broadcastReceiver);
        Debug.PrintDebug(this, "onPause");
        HiperPrecos.getInstance().hideWaitingDialog();
        super.onPause();
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.clear();
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.product_list_menu, menu);
        // Get the SearchView and set the searchable configuration
        final MenuItem menuItem = menu.findItem(R.id.menu_search);

        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (Utils.validSearch(query)) {
                    menuItem.collapseActionView();
                }
                HiperPrecos.getInstance().search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onPrepareOptionsMenu(menu);
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
        String[] options = new String[]{
                getResources().getString(R.string.nome_asc),
                getResources().getString(R.string.nome_desc),
                getResources().getString(R.string.marca_asc),
                getResources().getString(R.string.marca_desc),
                getResources().getString(R.string.preco_asc),
                getResources().getString(R.string.preco_desc)
        };
        builder.setSingleChoiceItems(options, selected, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
        List<String> brandsList = new ArrayList<String>();
        brandsList.addAll(productListFrag.getBrandsFilter());
        java.util.Collections.sort(brandsList);
        brandsList.add(0, getResources().getString(R.string.all_brands));
        if (!currFilter.initialized()) {
            origMinPrice = (int) Math.floor(productListFrag.getMinPriceFilter());
            origMaxPrice = (int) Math.ceil(productListFrag.getMaxPriceFilter());
            currFilter.initialize(origMinPrice, origMaxPrice, "", brandsList);
        }
        View dialogLayout = getLayoutInflater().inflate(R.layout.filter_layout, null);
        ((EditText) dialogLayout.findViewById(R.id.et_filter_product_name)).setText(currFilter.getProductNameFilter());
        final DoubleSeekBar doubleSeekBar = (DoubleSeekBar) dialogLayout.findViewById(R.id.filter_price_double_sb);
        doubleSeekBar.setMinValue(origMinPrice);
        doubleSeekBar.setMaxValue(origMaxPrice);
        doubleSeekBar.setCurrentMinValue(currFilter.getMinPriceFilter());
        doubleSeekBar.setCurrentMaxValue(currFilter.getMaxPriceFilter());
        final ListView brandsLv = (ListView) dialogLayout.findViewById(R.id.lv_brands);
        final ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, R.layout.simple_list_item_multiple_choice, brandsList);
        Debug.PrintError(this, ""+Build.VERSION.SDK_INT+"|"+Build.VERSION_CODES.GINGERBREAD);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            brandsLv.setVisibility(View.GONE);
        } else {
            brandsLv.setAdapter(listAdapter);
            // set if brand was previously checked or not
            for (int i = 0; i < listAdapter.getCount(); i++) {
                brandsLv.setItemChecked(i, currFilter.getBrandsFilter().contains(listAdapter.getItem(i)));
            }
            brandsLv.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        // All brands
                        if (((CheckedTextView) view).isChecked()) {
                            //add all brands to filter and check them
                            for (int i = 0; i < listAdapter.getCount(); i++) {
                                brandsLv.setItemChecked(i, true);
                            }
                        } else {
                            //remove all brands from filter and uncheck them
                            for (int i = 0; i < listAdapter.getCount(); i++) {
                                brandsLv.setItemChecked(i, false);
                            }
                        }
                    } else {
                        if (!((CheckedTextView) view).isChecked()) {
                            // Deselected one brand, uncheck "all brands"
                            brandsLv.setItemChecked(0, false);
                        }
                    }
                }
            });
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setInverseBackgroundForced(true);
        builder.setTitle(getResources().getString(R.string.filter));
        builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //get string for product name
                AlertDialog clickedDialog = (AlertDialog) dialogInterface;
                String prodNameFilter = ((EditText) clickedDialog.findViewById(R.id.et_filter_product_name)).getText().toString().trim();
                currFilter.setProductNameFilter(prodNameFilter);
                Integer minPriceFilter = doubleSeekBar.getCurrentMinValue();
                Integer maxPriceFilter = doubleSeekBar.getCurrentMaxValue();
                currFilter.setMinPriceFilter(minPriceFilter);
                currFilter.setMaxPriceFilter(maxPriceFilter);
                if (originalFilter == null) {
                    originalFilter = new Filter();
                    originalFilter.clone(currFilter);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    for (int j = 0; j < brandsLv.getCount(); j++) {
                        if (brandsLv.isItemChecked(j)) {
                            currFilter.addBrandFilter(listAdapter.getItem(j));
                        } else {
                            currFilter.removeBrandFilter(listAdapter.getItem(j));
                        }
                    }
                }
                productListFrag.setFilter(currFilter);
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setView(dialogLayout);
        builder.show();
    }

}
