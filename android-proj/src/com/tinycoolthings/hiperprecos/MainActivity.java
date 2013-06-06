package com.tinycoolthings.hiperprecos;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

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
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.tinycoolthings.hiperprecos.category.CategoryListPagerAdapater;
import com.tinycoolthings.hiperprecos.models.Category;
import com.tinycoolthings.hiperprecos.models.Hyper;
import com.tinycoolthings.hiperprecos.search.SearchResults;
import com.tinycoolthings.hiperprecos.serverComm.CallWebServiceTask;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Debug;
import com.tinycoolthings.hiperprecos.utils.Utils;

public class MainActivity extends SherlockFragmentActivity {

	private ActionBar mActionBar;
	private ViewPager mPager;
	private ActionBar.TabListener tabListener;

	private Integer nrCatsListsReceived = 0;

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Constants.Actions.GET_HYPERS)) {
				// Received hypers
				HiperPrecos.getInstance().addHypers(
						intent.getStringExtra(Constants.Extras.HIPERS));
				// get categories for each hyper
				List<Hyper> hypers = HiperPrecos.getInstance().getHypers();
				for (int i = 0; i < hypers.size(); i++) {
					CallWebServiceTask getCategories = new CallWebServiceTask(
							Constants.Actions.GET_CATEGORIES, false);
					getCategories
							.addParameter(
									Constants.Server.Parameter.Name.PARENT_CATEGORY,
									-1);
					getCategories.addParameter(
							Constants.Server.Parameter.Name.HYPER, hypers
									.get(i).getId());
					getCategories.execute();
				}
			} else if (action.equals(Constants.Actions.GET_CATEGORIES)) {
				nrCatsListsReceived++;
				HiperPrecos.getInstance().addCategories(
						intent.getStringExtra(Constants.Extras.CATEGORIES));
				if (nrCatsListsReceived == HiperPrecos.getInstance()
						.getNumberOfHypers()) {
					populateHipers();
				}
			} else if (action.equals(Constants.Actions.GET_CATEGORY)) {
				Debug.PrintInfo(MainActivity.this,
						"GET_CATEGORY -> Displaying category...");
				Category category = HiperPrecos.getInstance().addCategory(
						intent.getStringExtra(Constants.Extras.CATEGORY));
				enterSubCategory(category);
			} else if (action.equals(Constants.Actions.SEARCH)) {
				String result = intent
						.getStringExtra(Constants.Extras.SEARCH_RESULT);
				Debug.PrintDebug(this, result);
				Intent searchResultsIntent = new Intent(MainActivity.this,
						SearchResults.class);
				searchResultsIntent.putExtras(intent);
				startActivity(searchResultsIntent);
			} else if (intent.getAction().equals(
					Constants.Actions.DISPLAY_CATEGORY)) {
				Debug.PrintInfo(MainActivity.this,
						"DISPLAY_CATEGORY -> Displaying category...");
				enterSubCategory(HiperPrecos.getInstance().getCategoryById(
						intent.getIntExtra(Constants.Extras.CATEGORY, -1)));
			} else if (intent.getAction().equals(
					Constants.Actions.GET_LATEST_UPDATE)) {
				String lastestUpdateStr = intent.getStringExtra(
						Constants.Extras.LATEST_UPDATE).trim();
				Date latestUpdateDate = null;
				boolean error = false;
				try {
					latestUpdateDate = Utils.convertStringToCal(
							lastestUpdateStr).getTime();
					if (latestUpdateDate == null) {
						throw new Exception("Error parsing latest update date");
					}
					Debug.PrintInfo(
							MainActivity.this,
							"Latest Server Update date: "
									+ Utils.dateToStr(latestUpdateDate));
					Date latestUpdateDbDate = HiperPrecos.getInstance()
							.getLatestDbUpdate();
					Debug.PrintInfo(
							MainActivity.this,
							"Latest DB Update date: "
									+ Utils.dateToStr(latestUpdateDate));
					if (latestUpdateDbDate.before(latestUpdateDate)) {
						Debug.PrintInfo(
								MainActivity.this,
								"\n\tLatestDbUpdateDate: "
										+ Utils.dateToStr(latestUpdateDbDate)
										+ "\n" + "\n\tlatestUpdateCal: "
										+ Utils.dateToStr(latestUpdateDate));
						Debug.PrintInfo(MainActivity.this,
								"New database available. Clean old database.");
						HiperPrecos.getInstance().deleteHypersFromDB();
						CallWebServiceTask getHypers = new CallWebServiceTask(
								Constants.Actions.GET_HYPERS, false);
						getHypers.execute();
					} else {
						Debug.PrintInfo(MainActivity.this,
								"Database up to date.");
						HiperPrecos.getInstance().hideWaitingDialog();
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

		HiperPrecos.getInstance().setAppContext(this);

		Debug.PrintDebug(this, "onResume");

		IntentFilter filterServerResp = new IntentFilter();
		filterServerResp.addAction(Constants.Actions.GET_HYPERS);
		filterServerResp.addAction(Constants.Actions.GET_CATEGORIES);
		filterServerResp.addAction(Constants.Actions.GET_CATEGORY);
		filterServerResp.addAction(Constants.Actions.SEARCH);
		filterServerResp.addAction(Constants.Actions.DISPLAY_CATEGORY);
		filterServerResp.addAction(Constants.Actions.GET_LATEST_UPDATE);
		registerReceiver(broadcastReceiver, filterServerResp);

		mActionBar.removeAllTabs();

		if (mPager != null) {
			mPager.removeAllViews();
		}

		// check for update
		CallWebServiceTask getLatestUpdate = new CallWebServiceTask(
				Constants.Actions.GET_LATEST_UPDATE, false);
		getLatestUpdate.execute();

	}

	protected void enterSubCategory(Category category) {

		Debug.PrintInfo(MainActivity.this,
				"Selected categoria -> " + category.getName());

		Intent intent = new Intent(MainActivity.this, NavigationList.class);
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
		CategoryListPagerAdapater fragmentPagerAdapter = new CategoryListPagerAdapater(
				fm);

		/** Setting the FragmentPagerAdapter object to the viewPager object */
		mPager.setAdapter(fragmentPagerAdapter);

		/** Defining tab listener */
		tabListener = new ActionBar.TabListener() {
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
		List<Hyper> hipers = HiperPrecos.getInstance().getHypers();
		for (int i = 0; i < hipers.size(); i++) {
			Hyper currHiper = hipers.get(i);
			String currHiperName = currHiper.getName();
			/** Creating Tab */
			Tab tab = mActionBar.newTab().setText(currHiperName)
					.setTabListener(tabListener);

			mActionBar.addTab(tab);
		}

		HiperPrecos.getInstance().hideWaitingDialog();

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
