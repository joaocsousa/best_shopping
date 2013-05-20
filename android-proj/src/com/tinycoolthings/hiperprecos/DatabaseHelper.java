package com.tinycoolthings.hiperprecos;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.tinycoolthings.hiperprecos.models.Category;
import com.tinycoolthings.hiperprecos.models.Hyper;
import com.tinycoolthings.hiperprecos.models.Product;
import com.tinycoolthings.hiperprecos.utils.Debug;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	// name of the database file for your application -- change to something appropriate for your app
	private static final String DATABASE_NAME = "hiperprecos.db";
	// any time you make changes to your database objects, you may have to increase the database version
	private static final int DATABASE_VERSION = 1;

	// the DAO object we use to access the SimpleData table
	// the DAO object we use to access the SimpleData table
	private Dao<Hyper, Integer> hyperDao = null;
	private Dao<Category, Integer> categoryDao = null;
	private Dao<Product, Integer> productDao = null;
	
	private RuntimeExceptionDao<Hyper, Integer> hyperRuntimeDao = null;
	private RuntimeExceptionDao<Category, Integer> categoryRuntimeDao = null;
	private RuntimeExceptionDao<Product, Integer> productRuntimeDao = null;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
	}
	
	/**
	 * This is called when the database is first created. Usually you should call createTable statements here to create
	 * the tables that will store your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Debug.PrintInfo(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, Hyper.class);
			TableUtils.createTable(connectionSource, Category.class);
			TableUtils.createTable(connectionSource, Product.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
		
	}

	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
	 * the various data to match the new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			Debug.PrintInfo(DatabaseHelper.class, "onUpgrade");
			TableUtils.dropTable(connectionSource, Hyper.class, true);
			TableUtils.dropTable(connectionSource, Category.class, true);
			TableUtils.dropTable(connectionSource, Product.class, true);
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Debug.PrintError(DatabaseHelper.class, "Can't drop databases" + e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
	 * value.
	 */
	public Dao<Hyper, Integer> getHyperDao() throws SQLException {
		if (hyperDao == null) {
			hyperDao = getDao(Hyper.class);
		}
		return hyperDao;
	}
	
	public Dao<Category, Integer> getCategoryDao() throws SQLException {
		if (categoryDao == null) {
			categoryDao = getDao(Category.class);
		}
		return categoryDao;
	}
	
	public Dao<Product, Integer> getProductDao() throws SQLException {
		if (productDao == null) {
			productDao = getDao(Product.class);
		}
		return productDao;
	}

	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<Hyper, Integer> getHyperRuntimeDao() {
		if (hyperRuntimeDao == null) {
			hyperRuntimeDao = getRuntimeExceptionDao(Hyper.class);
		}
		return hyperRuntimeDao;
	}
	
	public RuntimeExceptionDao<Category, Integer> getCategoryRuntimeDao() {
		if (categoryRuntimeDao == null) {
			categoryRuntimeDao = getRuntimeExceptionDao(Category.class);
		}
		return categoryRuntimeDao;
	}
	
	public RuntimeExceptionDao<Product, Integer> getProductRuntimeDao() {
		if (productRuntimeDao == null) {
			productRuntimeDao = getRuntimeExceptionDao(Product.class);
		}
		return productRuntimeDao;
	}

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		
		hyperDao = null;
		categoryDao = null;
		productDao = null;
		
		hyperRuntimeDao = null;
		categoryRuntimeDao = null;
		productRuntimeDao = null;
	}
}
