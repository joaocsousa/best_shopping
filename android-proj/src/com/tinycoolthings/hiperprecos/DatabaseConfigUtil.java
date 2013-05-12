package com.tinycoolthings.hiperprecos;

import java.io.IOException;
import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;
import com.tinycoolthings.hiperprecos.models.Category;
import com.tinycoolthings.hiperprecos.models.Hyper;
import com.tinycoolthings.hiperprecos.models.Product;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {

	private static final Class<?>[] classes = new Class[] {
		Hyper.class, Category.class, Product.class
	};
	
	public static void main(String[] args) throws SQLException, IOException {
		writeConfigFile("ormlite_config.txt", classes);
	}
}
