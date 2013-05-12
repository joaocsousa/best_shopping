package com.tinycoolthings.hiperprecos;

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
import com.tinycoolthings.hiperprecos.models.Category;
import com.tinycoolthings.hiperprecos.models.Hyper;
import com.tinycoolthings.hiperprecos.serverComm.CallWebServiceTask;
import com.tinycoolthings.hiperprecos.utils.Constants.Actions;
import com.tinycoolthings.hiperprecos.utils.Constants.Server.Parameter.Name;
import com.tinycoolthings.hiperprecos.utils.Utils;

public class HiperPrecos extends Application {

	private static HiperPrecos instance = null;
	private static Context appContext;
	private String latestSearchTerm = "";
	private DatabaseHelper databaseHelper = null;
	private ProgressDialog waitingDialog;
	
	/**
     * Convenient accessor, saves having to call and cast getApplicationContext() 
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
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
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
			AlertDialog.Builder altDialog = new AlertDialog.Builder(HiperPrecos.getInstance().getAppContext());
			altDialog.setMessage(R.string.short_search_term);
			altDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			altDialog.show();
		} else {
			latestSearchTerm = text;
			CallWebServiceTask search = new CallWebServiceTask(Actions.SEARCH, false);
			search.addParameter(Name.SEARCH_QUERY, text);
			search.execute();
		}
	}
	
	public String getLatestSearchTerm() {
		return latestSearchTerm;
	}
	
	public void showWaitingDialog() {
		this.waitingDialog = ProgressDialog.show(HiperPrecos.getInstance().getAppContext(), HiperPrecos.getInstance().getResources().getString(R.string.loading), HiperPrecos.getInstance().getResources().getString(R.string.wait), true);
	}

	public void hideWaitingDialog() {
		this.waitingDialog.cancel();
	}
	
	public void addCategories(String categoriesJSON) {
		try {
			JSONArray categoriesJSONObj = new JSONArray(categoriesJSON);
			for (int i=0;i<categoriesJSONObj.length();i++) {
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
			Hyper hyper = databaseHelper.getHyperRuntimeDao().queryForId(categoryJSONObj.getInt("hiper"));
			Category parentCategory = databaseHelper.getCategoryRuntimeDao().queryForId(categoryJSONObj.getInt("categoria_pai"));
			category = new Category(categoryJSONObj.getInt("id"), categoryJSONObj.getString("name"), hyper, parentCategory);
			databaseHelper.getCategoryRuntimeDao().createOrUpdate(category);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return category;
	}

	public void addHypers(String hypersJSON) {
		try {
			JSONArray hypersJSONObj = new JSONArray(hypersJSON);
			for (int i=0;i<hypersJSONObj.length();i++) {
				JSONObject hyperJSONObj = hypersJSONObj.getJSONObject(i);
				Hyper hyper = new Hyper(hyperJSONObj.getInt("id"), hyperJSONObj.getString("name"));
				databaseHelper.getHyperRuntimeDao().createOrUpdate(hyper);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public List<Hyper> getHypers() {
		return databaseHelper.getHyperRuntimeDao().queryForAll();
	}
	
	public long getNumberOfHypers() {
		return databaseHelper.getHyperRuntimeDao().countOf();
	}
	
	public boolean needsToUpdate() {
		
	}
	
}
