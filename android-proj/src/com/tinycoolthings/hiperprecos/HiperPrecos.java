package com.tinycoolthings.hiperprecos;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.tinycoolthings.hiperprecos.models.Category;
import com.tinycoolthings.hiperprecos.models.Hyper;
import com.tinycoolthings.hiperprecos.models.Product;
import com.tinycoolthings.hiperprecos.serverComm.CallWebServiceTask;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Constants.Actions;
import com.tinycoolthings.hiperprecos.utils.Constants.Server.Parameter.Name;
import com.tinycoolthings.hiperprecos.utils.Constants.Sort;
import com.tinycoolthings.hiperprecos.utils.Debug;
import com.tinycoolthings.hiperprecos.utils.Filter;
import com.tinycoolthings.hiperprecos.utils.Utils;

public class HiperPrecos extends Application {

	private static HiperPrecos instance = null;
	private static Context appContext = null;
	private String latestSearchTerm = "";
	private DatabaseHelper databaseHelper = null;
	private ProgressDialog waitingDialog = null;

	/**
	 * Convenient accessor, saves having to call and cast
	 * getApplicationContext()
	 */
	public static HiperPrecos getInstance() {
		checkInstance();
		return instance;
	}

	private static void checkInstance() {
		if (instance == null)
			throw new IllegalStateException("Application not created yet!");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(this,
					DatabaseHelper.class);
		}
	}

	public void destroyDBHelper() {
		if (databaseHelper != null) {
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}
	}

	public void setAppContext(Context ctx) {
		HiperPrecos.appContext = ctx;
	}

	public Context getAppContext() {
		return HiperPrecos.appContext;
	}

	public void search(String text) {
		text = text.trim();
		if (!Utils.validSearch(text)) {
			AlertDialog.Builder altDialog = new AlertDialog.Builder(HiperPrecos
					.getInstance().getAppContext());
			altDialog.setMessage(R.string.short_search_term);
			altDialog.setNeutralButton("OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			altDialog.show();
		} else {
			latestSearchTerm = text;
			CallWebServiceTask search = new CallWebServiceTask(Actions.SEARCH,
					true);
			search.addParameter(Name.SEARCH_QUERY, text);
			search.execute();
		}
	}

	public String getLatestSearchTerm() {
		return latestSearchTerm;
	}

	public void showWaitingDialog() {
		if (this.waitingDialog == null) {
			this.waitingDialog = ProgressDialog.show(HiperPrecos.getInstance()
					.getAppContext(), HiperPrecos.getInstance().getResources()
					.getString(R.string.loading), HiperPrecos.getInstance()
					.getResources().getString(R.string.wait), true);
		}
	}

	public void hideWaitingDialog() {
		if (this.waitingDialog != null) {
			this.waitingDialog.cancel();
		}
		this.waitingDialog = null;
	}

	public void addCategories(String categoriesJSON) {
		try {
			JSONArray categoriesJSONObj = new JSONArray(categoriesJSON);
			for (int i = 0; i < categoriesJSONObj.length(); i++) {
				addCategory(categoriesJSONObj.getJSONObject(i));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public Category addCategory(String categoryJSON) {
		Category category = null;
		try {
			category = addCategory(new JSONObject(categoryJSON));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return category;
	}

	public Category addCategory(JSONObject categoryJSONObj) {
		Category category = null;
		try {
			Hyper hyper = databaseHelper.getHyperRuntimeDao().queryForId(
					categoryJSONObj.getInt("hiper"));
			Integer parentCat = null;
			try {
				parentCat = categoryJSONObj.getInt("categoria_pai");
			} catch (JSONException e) {
			}
			Category parentCategory = parentCat == null ? null : databaseHelper
					.getCategoryRuntimeDao().queryForId(parentCat);
			Long latestUpdate = 0L;
			try {
				latestUpdate = categoryJSONObj.getLong("latest_update");
			} catch (Exception e) {
				try {
					latestUpdate = categoryJSONObj.getLong("latestUpdate");
				} catch (Exception e1) {
				}
			}
			category = new Category(categoryJSONObj.getInt("id"),
					categoryJSONObj.getString("nome"), hyper, parentCategory,
					Utils.convertLongToDate(latestUpdate));
			// parse sub categories
			try {
				JSONArray subCategories = categoryJSONObj
						.getJSONArray("sub_categorias");
				for (int i = 0; i < subCategories.length(); i++) {
					addCategory(subCategories.getJSONObject(i));
				}
			} catch (Exception e) {
			}
			// parse produtos
			try {
				JSONArray products = categoryJSONObj.getJSONArray("produtos");
				for (int i = 0; i < products.length(); i++) {
					addProduct(products.getJSONObject(i));
				}
			} catch (Exception e) {
			}
			databaseHelper.getCategoryRuntimeDao().createOrUpdate(category);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return category;
	}

	public void addHypers(String hypersJSON) {
		try {
			JSONArray hypersJSONObj = new JSONArray(hypersJSON);
			for (int i = 0; i < hypersJSONObj.length(); i++) {
				JSONObject hyperJSONObj = hypersJSONObj.getJSONObject(i);
				addHyper(hyperJSONObj);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public Hyper addHyper(String hyperJSON) {
		Hyper hyper = null;
		try {
			hyper = addHyper(new JSONObject(hyperJSON));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return hyper;
	}

	public Hyper addHyper(JSONObject hyperJSONObj) {
		Hyper hyper = null;
		try {
			hyper = new Hyper(hyperJSONObj.getInt("id"),
					hyperJSONObj.getString("nome"),
					Utils.convertLongToDate(hyperJSONObj
							.getLong("latestUpdate") * 1000));
			databaseHelper.getHyperRuntimeDao().createOrUpdate(hyper);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return hyper;
	}

	public void addProducts(String productsJSON) {
		try {
			JSONArray productsJSONObj = new JSONArray(productsJSON);
			for (int i = 0; i < productsJSONObj.length(); i++) {
				addProduct(productsJSONObj.getJSONObject(i));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public Product addProduct(String productJSON) {
		Product product = null;
		try {
			product = addProduct(new JSONObject(productJSON));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return product;
	}

	// public Product addSearchResult(JSONObject producJSONObj) {
	//
	// }

	public Product addProduct(JSONObject productJSONObj) {
		Product product = null;
		try {
			Hyper hyper = databaseHelper.getHyperRuntimeDao().queryForId(
					productJSONObj.getInt("hiper"));
			Category parentCategory = databaseHelper.getCategoryRuntimeDao()
					.queryForId(productJSONObj.getInt("categoria_pai"));
			Double desconto = 0d;
			try {
				desconto = productJSONObj.getDouble("desconto");
			} catch (Exception e) {
			}
			String brand = null;
			Double price = null;
			Double priceKg = null;
			String weight = null;
			Long latestUpdate = null;
			try {
				brand = productJSONObj.getString("marca");
			} catch (Exception e) {
			}
			try {
				price = productJSONObj.getDouble("preco");
			} catch (Exception e) {
			}
			try {
				priceKg = productJSONObj.getDouble("preco_kg");
			} catch (Exception e) {
			}
			try {
				weight = productJSONObj.getString("peso");
			} catch (Exception e) {
			}
			try {
				latestUpdate = productJSONObj.getLong("latest_update");
			} catch (Exception e) {
				try {
					latestUpdate = productJSONObj.getLong("latestUpdate");
				} catch (Exception e1) {
				}
			}
			product = new Product(productJSONObj.getInt("id"),
					productJSONObj.getString("nome"), brand, price, priceKg,
					weight, productJSONObj.getString("url_pagina"),
					productJSONObj.getString("url_imagem"), desconto,
					parentCategory, Utils.convertLongToDate(latestUpdate),
					hyper);
			databaseHelper.getProductRuntimeDao().createOrUpdate(product);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return product;
	}

	public Category getCategoryById(int id) {
		return databaseHelper.getCategoryRuntimeDao().queryForId(id);
	}

	public Product getProductById(int id) {
		return databaseHelper.getProductRuntimeDao().queryForId(id);
	}

	public List<Hyper> getHypers() {
		return databaseHelper.getHyperRuntimeDao().queryForAll();
	}

	public long getNumberOfHypers() {
		return databaseHelper.getHyperRuntimeDao().countOf();
	}

	public List<Category> getSubCategoriesFromParent(Category category, int sort)
			throws SQLException {
		boolean ascending = true;
		if (sort == Constants.Sort.NAME_DESCENDING) {
			ascending = false;
		}
		QueryBuilder<Category, Integer> queryBuilder = databaseHelper
				.getCategoryRuntimeDao().queryBuilder()
				.orderBy(Category.NAME_FIELD_NAME, ascending);
		if (category != null) {
			queryBuilder.where().eq(Category.PARENT_CATEGORY_FIELD_NAME,
					category);
		} else {
			queryBuilder.where().isNull(Category.PARENT_CATEGORY_FIELD_NAME);
		}

		PreparedQuery<Category> preparedQuery = queryBuilder.prepare();
		return databaseHelper.getCategoryRuntimeDao().query(preparedQuery);
	}

	public boolean categoryHasProducts(Category selectedCat)
			throws SQLException {
		QueryBuilder<Product, Integer> queryBuilder = databaseHelper
				.getProductRuntimeDao().queryBuilder();
		queryBuilder.where()
				.eq(Product.PARENT_CATEGORY_FIELD_NAME, selectedCat);
		PreparedQuery<Product> preparedQuery = queryBuilder.prepare();
		return databaseHelper.getProductRuntimeDao().query(preparedQuery)
				.size() > 0;
	}

	public boolean categoryHasSubCategories(Category selectedCat)
			throws SQLException {
		QueryBuilder<Category, Integer> queryBuilder = databaseHelper
				.getCategoryRuntimeDao().queryBuilder();
		queryBuilder.where().eq(Category.PARENT_CATEGORY_FIELD_NAME,
				selectedCat);
		PreparedQuery<Category> preparedQuery = queryBuilder.prepare();
		return databaseHelper.getCategoryRuntimeDao().query(preparedQuery)
				.size() > 0;
	}

	public List<Category> getSubCategoriesFromHyper(Hyper currHyper, int sort)
			throws SQLException {
		boolean ascending = true;
		if (sort == Constants.Sort.NAME_DESCENDING) {
			ascending = false;
		}
		QueryBuilder<Category, Integer> queryBuilder = databaseHelper
				.getCategoryRuntimeDao().queryBuilder()
				.orderBy(Category.NAME_FIELD_NAME, ascending);
		queryBuilder.where().isNull(Category.PARENT_CATEGORY_FIELD_NAME).and()
				.eq(Category.HYPER_FIELD_NAME, currHyper);
		PreparedQuery<Category> preparedQuery = queryBuilder.prepare();
		return databaseHelper.getCategoryRuntimeDao().query(preparedQuery);
	}

	public List<Category> getCategorySiblings(Category category, int sort)
			throws SQLException {
		boolean ascending = true;
		if (sort == Constants.Sort.NAME_DESCENDING) {
			ascending = false;
		}
		QueryBuilder<Category, Integer> queryBuilder = databaseHelper
				.getCategoryRuntimeDao().queryBuilder()
				.orderBy(Category.NAME_FIELD_NAME, ascending);
		if (category.getParentCat() != null) {
			queryBuilder.where().eq(Category.PARENT_CATEGORY_FIELD_NAME,
					category.getParentCat());
		} else {
			queryBuilder.where().isNull(Category.PARENT_CATEGORY_FIELD_NAME)
					.and().eq(Category.HYPER_FIELD_NAME, category.getHyper());
		}

		PreparedQuery<Category> preparedQuery = queryBuilder.prepare();
		return databaseHelper.getCategoryRuntimeDao().query(preparedQuery);
	}

	public boolean categoryLoaded(Category selectedCat) throws SQLException {
		return categoryHasSubCategories(selectedCat)
				|| categoryHasProducts(selectedCat);
	}

	public List<Product> getProductsFromIDsList(List<Integer> productsIDs,
			int sortType, Filter filter) throws SQLException {
		QueryBuilder<Product, Integer> queryBuilder = databaseHelper
				.getProductRuntimeDao().queryBuilder();
		switch (sortType) {
		case Sort.NAME_ASCENDING:
			queryBuilder.orderBy(Product.NAME_FIELD_NAME, true);
			break;
		case Sort.NAME_DESCENDING:
			queryBuilder.orderBy(Product.NAME_FIELD_NAME, false);
			break;
		case Sort.BRAND_ASCENDING:
			queryBuilder.orderBy(Product.BRAND_FIELD_NAME, true);
			break;
		case Sort.BRAND_DESCENDING:
			queryBuilder.orderBy(Product.BRAND_FIELD_NAME, false);
			break;
		case Sort.PRICE_ASCENDING:
			queryBuilder.orderBy(Product.PRICE_FIELD_NAME, true);
			break;
		case Sort.PRICE_DESCENDING:
			queryBuilder.orderBy(Product.PRICE_FIELD_NAME, false);
			break;
		}
		Where<Product, Integer> where = queryBuilder.where();
		if (!filter.getProductNameFilter().equals("")) {
			Debug.PrintWarning(this,
					"Filter: Product Name -> " + filter.getProductNameFilter());
			where.like(Product.NAME_FIELD_NAME,
					"%" + filter.getProductNameFilter() + "%").and();
		}
		if (filter.getMinPriceFilter() > 0) {
			Debug.PrintWarning(this,
					"Filter: Min Price -> " + filter.getMinPriceFilter());
			where.gt(Product.PRICE_FIELD_NAME, filter.getMinPriceFilter())
					.and();
		}
		if (filter.getMaxPriceFilter() > 0) {
			Debug.PrintWarning(this,
					"Filter: Max Price -> " + filter.getMaxPriceFilter());
			where.lt(Product.PRICE_FIELD_NAME, filter.getMaxPriceFilter())
					.and();
		}
		List<String> brandsToShow = new ArrayList<String>();
		brandsToShow.addAll(filter.getBrandsFilter());
		try {
			if (brandsToShow.indexOf(getResources().getString(
					R.string.non_available)) != -1) {
				brandsToShow.add("");
				brandsToShow.add("null");
			}
		} catch (Exception e) {
		}
		if (brandsToShow.size() > 0) {
			where.in(Product.BRAND_FIELD_NAME, brandsToShow).and();
		}
		where.in(Product.ID_FIELD_NAME, productsIDs);
		PreparedQuery<Product> preparedQuery = queryBuilder.prepare();
		return databaseHelper.getProductRuntimeDao().query(preparedQuery);
	}
	
	public List<Category> getCategoriesFromIDsList(List<Integer> categoriesIDs,
			int sortType) throws SQLException {
		QueryBuilder<Category, Integer> queryBuilder = databaseHelper
				.getCategoryRuntimeDao().queryBuilder();
		switch (sortType) {
			case Sort.NAME_ASCENDING:
				queryBuilder.orderBy(Product.NAME_FIELD_NAME, true);
				break;
			case Sort.NAME_DESCENDING:
				queryBuilder.orderBy(Product.NAME_FIELD_NAME, false);
				break;
		}
		Where<Category, Integer> where = queryBuilder.where();
		where.in(Category.ID_FIELD_NAME, categoriesIDs);
		PreparedQuery<Category> preparedQuery = queryBuilder.prepare();
		return databaseHelper.getCategoryRuntimeDao().query(preparedQuery);
	}

	public List<Product> getProductsFromCategory(Category currCat,
			int sortType, Filter filter) throws SQLException {
		QueryBuilder<Product, Integer> queryBuilder = databaseHelper
				.getProductRuntimeDao().queryBuilder();
		switch (sortType) {
		case Sort.NAME_ASCENDING:
			queryBuilder.orderBy(Product.NAME_FIELD_NAME, true);
			break;
		case Sort.NAME_DESCENDING:
			queryBuilder.orderBy(Product.NAME_FIELD_NAME, false);
			break;
		case Sort.BRAND_ASCENDING:
			queryBuilder.orderBy(Product.BRAND_FIELD_NAME, true);
			break;
		case Sort.BRAND_DESCENDING:
			queryBuilder.orderBy(Product.BRAND_FIELD_NAME, false);
			break;
		case Sort.PRICE_ASCENDING:
			queryBuilder.orderBy(Product.PRICE_FIELD_NAME, true);
			break;
		case Sort.PRICE_DESCENDING:
			queryBuilder.orderBy(Product.PRICE_FIELD_NAME, false);
			break;
		}
		Where<Product, Integer> where = queryBuilder.where();
		if (!filter.getProductNameFilter().equals("")) {
			Debug.PrintWarning(this,
					"Filter: Product Name -> " + filter.getProductNameFilter());
			where.like(Product.NAME_FIELD_NAME,
					"%" + filter.getProductNameFilter() + "%").and();
		}
		if (filter.getMinPriceFilter() > 0) {
			Debug.PrintWarning(this,
					"Filter: Min Price -> " + filter.getMinPriceFilter());
			where.gt(Product.PRICE_FIELD_NAME, filter.getMinPriceFilter())
					.and();
		}
		if (filter.getMaxPriceFilter() > 0) {
			Debug.PrintWarning(this,
					"Filter: Max Price -> " + filter.getMaxPriceFilter());
			where.lt(Product.PRICE_FIELD_NAME, filter.getMaxPriceFilter())
					.and();
		}
		List<String> brandsToShow = new ArrayList<String>();
		brandsToShow.addAll(filter.getBrandsFilter());
		try {
			if (brandsToShow.indexOf(getResources().getString(
					R.string.non_available)) != -1) {
				brandsToShow.add("");
				brandsToShow.add("null");
			}
		} catch (Exception e) {
		}
		if (brandsToShow.size() > 0) {
			where.in(Product.BRAND_FIELD_NAME, brandsToShow).and();
		}
		where.eq(Product.PARENT_CATEGORY_FIELD_NAME, currCat);
		PreparedQuery<Product> preparedQuery = queryBuilder.prepare();
		return databaseHelper.getProductRuntimeDao().query(preparedQuery);
	}

	public void deleteHypersFromDB() throws SQLException {
		// delete all products from this hyper
		DeleteBuilder<Product, Integer> prodDeleteBuilder = databaseHelper
				.getProductRuntimeDao().deleteBuilder();
		databaseHelper.getProductRuntimeDao().delete(
				prodDeleteBuilder.prepare());
		// delete all categories from this hyper
		DeleteBuilder<Category, Integer> catDeleteBuilder = databaseHelper
				.getCategoryRuntimeDao().deleteBuilder();
		databaseHelper.getCategoryRuntimeDao().delete(
				catDeleteBuilder.prepare());
		// delete hyper
		DeleteBuilder<Hyper, Integer> hyperDeleteBuilder = databaseHelper
				.getHyperRuntimeDao().deleteBuilder();
		databaseHelper.getHyperRuntimeDao()
				.delete(hyperDeleteBuilder.prepare());
	}

	public Date getLatestDbUpdate() throws SQLException {
		QueryBuilder<Hyper, Integer> queryBuilder = databaseHelper
				.getHyperRuntimeDao().queryBuilder();
		queryBuilder.orderBy(Hyper.LATEST_UPDATE_FIELD_NAME, false);
		return databaseHelper.getHyperRuntimeDao()
				.query(queryBuilder.prepare()).get(0).getLatestUpdate();
	}

	public void refreshCategory(Category category) {
		databaseHelper.getCategoryRuntimeDao().refresh(category);
	}

	public void refreshHyper(Hyper hyper) {
		databaseHelper.getHyperRuntimeDao().refresh(hyper);
	}

	public void refreshProduct(Product product) {
		databaseHelper.getProductRuntimeDao().refresh(product);
	}
}
