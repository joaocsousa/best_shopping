package com.tinycoolthings.bestshopping;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.tinycoolthings.bestshopping.category.CategoryListPagerAdapter;
import com.tinycoolthings.bestshopping.models.Category;
import com.tinycoolthings.bestshopping.models.Hyper;
import com.tinycoolthings.bestshopping.search.SearchResults;
import com.tinycoolthings.bestshopping.serverComm.CallWebServiceTask;
import com.tinycoolthings.bestshopping.shoppingList.ShoppingList;
import com.tinycoolthings.bestshopping.utils.Constants;
import com.tinycoolthings.bestshopping.utils.Debug;
import com.tinycoolthings.bestshopping.utils.Utils;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class MainMenu extends SherlockFragmentActivity {

    private ActionBar mActionBar;
    private ViewPager mPager;
    private Integer nrCatsListsReceived = 0;
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.Actions.GET_HYPERS)) {
                // Received hypers
                BestShopping.getInstance().addHypers(
                        intent.getStringExtra(Constants.Extras.HYPERS));
                // get categories for each hyper
                List<Hyper> hypers = BestShopping.getInstance().getHypers();
                for (Hyper hyper : hypers) {
                    CallWebServiceTask getCategories = new CallWebServiceTask(
                            Constants.Actions.GET_CATEGORIES, false);
                    getCategories
                            .addParameter(
                                    Constants.Server.Parameter.Name.PARENT_CATEGORY,
                                    -1);
                    getCategories.addParameter(
                            Constants.Server.Parameter.Name.HYPER, hyper.getId());
                    getCategories.execute();
                }
            } else if (action.equals(Constants.Actions.GET_CATEGORIES)) {
                nrCatsListsReceived++;
                BestShopping.getInstance().addCategories(
                        intent.getStringExtra(Constants.Extras.CATEGORIES));
                if (nrCatsListsReceived == BestShopping.getInstance()
                        .getNumberOfHypers()) {
                    populateHipers();
                }
            } else if (action.equals(Constants.Actions.GET_CATEGORY)) {
                Debug.PrintInfo(MainMenu.this,
                        "GET_CATEGORY -> Displaying category...");
                Category category = BestShopping.getInstance().addCategory(
                        intent.getStringExtra(Constants.Extras.CATEGORY));
                enterSubCategory(category);
            } else if (action.equals(Constants.Actions.SEARCH)) {
                Debug.PrintInfo(MainMenu.this, "Received search result.");
                Intent searchResultsIntent = new Intent(MainMenu.this, SearchResults.class);
                searchResultsIntent.putExtras(intent);
                startActivity(searchResultsIntent);
                BestShopping.getInstance().hideWaitingDialog();
            } else if (intent.getAction().equals(
                    Constants.Actions.DISPLAY_CATEGORY)) {
                Debug.PrintInfo(MainMenu.this,
                        "DISPLAY_CATEGORY -> Displaying category...");
                enterSubCategory(BestShopping.getInstance().getCategoryById(
                        intent.getIntExtra(Constants.Extras.CATEGORY, -1)));
            } else if (intent.getAction().equals(
                    Constants.Actions.GET_LATEST_UPDATE)) {
                String lastestUpdateStr = "";
                try {
                    lastestUpdateStr = intent.getStringExtra(Constants.Extras.LATEST_UPDATE).trim();
                } catch (Exception e) {
                    BestShopping.getInstance().hideWaitingDialog();
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
                    builder.setMessage(getResources().getString(R.string.server_down))
                            .setCancelable(false)
                            .setNegativeButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    return;
                }
                Date latestUpdateDate = null;
                boolean error = false;
                try {
                    latestUpdateDate = Utils.convertStringToCal(
                            lastestUpdateStr).getTime();
                    if (latestUpdateDate == null) {
                        throw new Exception("Error parsing latest update date");
                    }
                    Debug.PrintInfo(
                            MainMenu.this,
                            "Latest Server Update date: "
                                    + Utils.dateToStr(latestUpdateDate));
                    Date latestUpdateDbDate = BestShopping.getInstance()
                            .getLatestDbUpdate();
                    Debug.PrintInfo(
                            MainMenu.this,
                            "Latest DB Update date: "
                                    + Utils.dateToStr(latestUpdateDate));
                    if (latestUpdateDbDate.before(latestUpdateDate)) {
                        Debug.PrintInfo(
                                MainMenu.this,
                                "\n\tLatestDbUpdateDate: "
                                        + Utils.dateToStr(latestUpdateDbDate)
                                        + "\n" + "\n\tlatestUpdateCal: "
                                        + Utils.dateToStr(latestUpdateDate));
                        Debug.PrintInfo(MainMenu.this,
                                "New database available. Clean old database.");
                        BestShopping.getInstance().deleteHypersFromDB();
                        CallWebServiceTask getHypers = new CallWebServiceTask(
                                Constants.Actions.GET_HYPERS, false);
                        getHypers.execute();
                    } else {
                        Debug.PrintInfo(MainMenu.this,
                                "Database up to date.");
                        populateHipers();
                    }
                } catch (SQLException e) {
                    error = true;
                    e.printStackTrace();
                } catch (ParseException e) {
                    error = true;
                    e.printStackTrace();
                } catch (Exception e) {
                    error = true;
                    e.printStackTrace();
                }
                if (error) {
                    // error fetching date, get all hypers just in case
                    CallWebServiceTask getHypers = new CallWebServiceTask(
                            Constants.Actions.GET_HYPERS, false);
                    getHypers.execute();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Debug.PrintDebug(this, "onCreate");

        setContentView(R.layout.activity_main);

        BestShopping.getInstance().setAppContext(this);

        /** Getting a reference to action bar of this activity */
        mActionBar = getSupportActionBar();

        /** Set tab navigation mode */
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mActionBar.setDisplayShowTitleEnabled(true);

        checkForUpdates();

    }

    @Override
    protected void onResume() {
        super.onResume();

        BestShopping.getInstance().setAppContext(this);

        Debug.PrintDebug(this, "onResume");

        IntentFilter filterServerResp = new IntentFilter();
        filterServerResp.addAction(Constants.Actions.GET_HYPERS);
        filterServerResp.addAction(Constants.Actions.GET_CATEGORIES);
        filterServerResp.addAction(Constants.Actions.GET_CATEGORY);
        filterServerResp.addAction(Constants.Actions.SEARCH);
        filterServerResp.addAction(Constants.Actions.DISPLAY_CATEGORY);
        filterServerResp.addAction(Constants.Actions.GET_LATEST_UPDATE);
        registerReceiver(broadcastReceiver, filterServerResp);

    }

    protected void enterSubCategory(Category category) {
        Debug.PrintInfo(MainMenu.this,
                "Selected categoria -> " + category.getName());
        Intent intent = new Intent(MainMenu.this, NavigationList.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.Extras.CATEGORY, category.getId());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void populateHipers() {

        nrCatsListsReceived = 0;

        /** Getting a reference to ViewPager from the layout */
        mPager = new ViewPager(this);
        mPager.setId(Utils.getRandomInt());
        LayoutParams pagerParams = new LayoutParams();
        pagerParams.width = LayoutParams.MATCH_PARENT;
        pagerParams.height = LayoutParams.MATCH_PARENT;
        mPager.setLayoutParams(pagerParams);

        ((RelativeLayout) findViewById(R.id.MainLayout)).addView(mPager);

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
        CategoryListPagerAdapter fragmentPagerAdapter = new CategoryListPagerAdapter(
                fm);

        /** Setting the FragmentPagerAdapter object to the viewPager object */
        mPager.setAdapter(fragmentPagerAdapter);

        /** Defining tab listener */
        ActionBar.TabListener tabListener = new

                ActionBar.TabListener() {
                    @Override
                    public void onTabSelected(Tab tab, FragmentTransaction ft) {
                        mPager.setCurrentItem(tab.getPosition());
                    }

                    @Override
                    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
                    }

                    @Override
                    public void onTabReselected(Tab tab, FragmentTransaction ft) {
                    }
                };

        /** Create Tabs */
        List<Hyper> hypers = BestShopping.getInstance().getHypers();
        for (Hyper currHyper : hypers) {
            String currHyperName = currHyper.getName();
            /** Creating Tab */
            Tab tab = mActionBar.newTab().setText(currHyperName).setTabListener(tabListener);

            mActionBar.addTab(tab);
        }

        BestShopping.getInstance().hideWaitingDialog();

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
        inflater.inflate(R.menu.category_list_menu, menu);
        // Get the SearchView and set the searchable configuration
        final MenuItem menuItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (Utils.validSearch(query)) {
                    menuItem.collapseActionView();
                }
                BestShopping.getInstance().search(query);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shopping_list:
                startActivity(new Intent(this, ShoppingList.class));
                break;
            case R.id.menu_refresh:
                // check for update
                checkForUpdates();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkForUpdates() {
        mActionBar.removeAllTabs();

        if (mPager != null) {
            mPager.removeAllViews();
        }

        CallWebServiceTask getLatestUpdate = new CallWebServiceTask(Constants.Actions.GET_LATEST_UPDATE, false);
        getLatestUpdate.execute();

    }

}
