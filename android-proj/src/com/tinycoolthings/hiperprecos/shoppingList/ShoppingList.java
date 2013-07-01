package com.tinycoolthings.hiperprecos.shoppingList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.tinycoolthings.hiperprecos.HiperPrecos;
import com.tinycoolthings.hiperprecos.R;
import com.tinycoolthings.hiperprecos.models.Hyper;
import com.tinycoolthings.hiperprecos.models.Product;
import com.tinycoolthings.hiperprecos.product.ProductView;
import com.tinycoolthings.hiperprecos.search.SearchResults;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Debug;

import java.text.DecimalFormat;
import java.util.List;

public class ShoppingList extends SherlockFragmentActivity {

    private ActionBar mActionBar;
    private ViewPager mPager;
    private ShoppingListFragmentAdapter shoppingListFragmentAdapter;
    private final SparseArray<Double> shoppingSums = new SparseArray<Double>();
    private List<Hyper> hypers;
    private int currSelectedPos = 0;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.Actions.DISPLAY_PRODUCT)) {
                Debug.PrintInfo(this, "Displaying product...");
                Product selectedProd = HiperPrecos.getInstance().getProductById(intent.getIntExtra(Constants.Extras.PRODUCT, -1));
                showProduct(selectedProd);
            } else if (intent.getAction().equals(Constants.Actions.SEARCH)) {
                Debug.PrintInfo(ShoppingList.this, "Received search result.");
                Intent searchResultsIntent = new Intent(ShoppingList.this, SearchResults.class);
                searchResultsIntent.putExtras(intent);
                startActivity(searchResultsIntent);
                HiperPrecos.getInstance().hideWaitingDialog();
            } else if (intent.getAction().equals(Constants.Actions.SET_NEW_SHOPPING_LIST_TOTAL)) {
                int hyperID = intent.getIntExtra(Constants.Extras.HYPER, 0);
                double total = intent.getDoubleExtra(Constants.Extras.SHOPPING_LIST_TOTAL, 0.0);
                shoppingSums.put(hyperID, total);
                Debug.PrintInfo(ShoppingList.this, "Received new shopping list total.hyper-" + hyperID + "|total-" + total);
                updateTotalShoppingSum();
            } else if (intent.getAction().equals(Constants.Actions.SHOPPING_LIST_CHANGED)) {
                shoppingListFragmentAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.shopping_list);

        /** Getting a reference to action bar of this activity */
        mActionBar = getSupportActionBar();

        /** Set tab navigation mode */
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mActionBar.setDisplayShowTitleEnabled(true);

        HiperPrecos.getInstance().setAppContext(this);

        /** Getting a reference to ViewPager from the layout */
        mPager = (ViewPager)findViewById(R.id.vp_shopping_list_pager);

        mPager.setSaveEnabled(false);

        /** Getting a reference to FragmentManager */
        FragmentManager fm = getSupportFragmentManager();

        /** Defining a listener for pageChange */
        ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mActionBar.setSelectedNavigationItem(position);
                super.onPageSelected(position);
            }
        };

        mPager.setOnPageChangeListener(pageChangeListener);

        /** Creating an instance of FragmentPagerAdapter */
        shoppingListFragmentAdapter = new ShoppingListFragmentAdapter(fm);

        /** Setting the FragmentPagerAdapter object to the viewPager object */
        mPager.setAdapter(shoppingListFragmentAdapter);

        /** Defining tab listener */
        ActionBar.TabListener tabListener = new

                ActionBar.TabListener() {
                    @Override
                    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                        mPager.setCurrentItem(tab.getPosition());
                        currSelectedPos = tab.getPosition();
                        updateTotalShoppingSum();
                    }

                    @Override
                    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                    }

                    @Override
                    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                    }
                };

        /** Create Tabs */
        hypers = HiperPrecos.getInstance().getHypers();
        for (Hyper currHyper : hypers) {
            shoppingSums.put(currHyper.getId(), 0.0);
            String currHyperName = currHyper.getName();
            /** Creating Tab */
            ActionBar.Tab tab = mActionBar.newTab().setText(currHyperName).setTabListener(tabListener);

            mActionBar.addTab(tab);
        }

    }

    private void resetTotalShoppingSum() {
        for (Hyper currHyper : hypers) {
            shoppingSums.put(currHyper.getId(), 0.0);
        }
        updateTotalShoppingSum();
    }

    private void updateTotalShoppingSum() {
        int currHyperID = hypers.get(currSelectedPos).getId();
        double currValue = shoppingSums.get(currHyperID);
        DecimalFormat formatter = new DecimalFormat("#.##");
        ((TextView)findViewById(R.id.tv_total_shopping)).setText(formatter.format(currValue) + " €");
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetTotalShoppingSum();
        Debug.PrintDebug(this, "onResume");
        IntentFilter filterServerResp = new IntentFilter();
        filterServerResp.addAction(Constants.Actions.DISPLAY_PRODUCT);
        filterServerResp.addAction(Constants.Actions.SEARCH);
        filterServerResp.addAction(Constants.Actions.SET_NEW_SHOPPING_LIST_TOTAL);
        filterServerResp.addAction(Constants.Actions.SHOPPING_LIST_CHANGED);
        registerReceiver(broadcastReceiver, filterServerResp);
        HiperPrecos.getInstance().setAppContext(this);
        shoppingListFragmentAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(broadcastReceiver);
        Debug.PrintDebug(this, "onPause");
        HiperPrecos.getInstance().hideWaitingDialog();
        super.onPause();
    }

    protected void showProduct(Product product) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.Extras.PRODUCT, product.getId());
        Intent intent = new Intent(this, ProductView.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }


}
