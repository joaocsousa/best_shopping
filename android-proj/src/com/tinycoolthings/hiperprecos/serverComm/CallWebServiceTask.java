package com.tinycoolthings.hiperprecos.serverComm;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.tinycoolthings.hiperprecos.HiperPrecos;
import com.tinycoolthings.hiperprecos.serverComm.RestClient.RequestMethod;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Debug;
import com.tinycoolthings.hiperprecos.utils.Slugify;
import com.tinycoolthings.hiperprecos.utils.Storage;
import com.tinycoolthings.hiperprecos.utils.Constants.Server.Parameter.Name;

public class CallWebServiceTask extends AsyncTask <Void, Void, String> {
	private ProgressDialog dialog;
	private Context context;
	private String action;
	private Map<Name, Integer> params;
	
	public CallWebServiceTask(Context applicationContext, String action) {
		this.context = applicationContext;
		this.action = action;
		this.params = new HashMap<Name, Integer>();
	}

	public void addParameter(Name name, Integer value) {
		this.params.put(name, value);
	}
	
	@Override
	protected void onPreExecute() {
		this.dialog = ProgressDialog.show(this.context, "Calling", "Time Service...", true);
	}

	@Override
	protected String doInBackground(Void... params) {
		try {
			String URL = "";
			if (this.action == Constants.Actions.GET_HIPERS) {
				URL = Constants.Server.Definitions.HIPERS_URL;
			} else if (this.action == Constants.Actions.GET_PRODUTOS) {
				URL = Constants.Server.Definitions.PRODUTOS_URL;
			} else if (this.action == Constants.Actions.GET_CATEGORIAS) {
				URL = Constants.Server.Definitions.CATEGORIAS_URL;
			} else if (this.action == Constants.Actions.GET_CATEGORIA) {
				URL = Constants.Server.Definitions.CATEGORIAS_URL;
				if (!this.params.containsKey(Constants.Server.Parameter.Name.CATEGORIA_ID)) {
					Debug.PrintError(this, "No categoria ID! Did you specify it?");
				} else {
					int catID = this.params.get(Constants.Server.Parameter.Name.CATEGORIA_ID);
					URL += catID;
				}
			}
			
			RestClient client = new RestClient(URL);
			
			client.AddParam("format", "json");
			
			String logParams = "?format=json&";
			
			Iterator<Entry<Name, Integer>> it = this.params.entrySet().iterator();
		    while (it.hasNext()) {
		    	Entry<Name, Integer> pair = (Entry<Name, Integer>)it.next();
		        switch (pair.getKey()) {
			        case HIPER:
			        	logParams+="hiper="+String.valueOf(pair.getValue())+"&";
			        	client.AddParam("hiper", String.valueOf(pair.getValue()));
			        	break;
			        case CATEGORIA_PAI:
			        	logParams+="categoria_pai="+String.valueOf(pair.getValue())+"&";
			        	client.AddParam("categoria_pai", String.valueOf(pair.getValue()));
			        	break;
			        case DESCONTO:
			        	logParams+="desconto="+String.valueOf(pair.getValue())+"&";
			        	client.AddParam("desconto", String.valueOf(pair.getValue()));
					default:
						break;
		        }
		        it.remove(); // avoids a ConcurrentModificationException
		    }
		    
		    try {
		    	Debug.PrintWarning(this, URL+logParams);
		        client.Execute(RequestMethod.GET);
		    } catch (Exception e) {
		        e.printStackTrace();
		    }

		    String response = client.getResponse();
		    
		    Object json = new JSONTokener(response).nextValue();
		    if (json instanceof JSONObject) {
		    	JSONObject jsonObj = (JSONObject)json;
		    	if (jsonObj.has("produtos")) {
		    		JSONArray produtos = jsonObj.getJSONArray("produtos");
		    		if (produtos.length()>0) {
		    			ExecutorService executor = Executors.newFixedThreadPool(produtos.length());
			    		for (int i=0;i<produtos.length();i++) {
			    			JSONObject currProdJson = produtos.getJSONObject(i);
			    			try {
			    				String currProdUrl = currProdJson.getString("url_imagem");
			    				// test url
			    				URL u = new URL(currProdUrl);
				    			u.toURI();
				    			//////////////
				    			String fileName = Storage.getFileName(currProdUrl, currProdJson.getString("nome"), currProdJson.getString("marca"));
				    			Runnable worker = new CallWebServiceTask.FetchImage(currProdUrl, fileName);
				    			executor.execute(worker);
			    			} catch (Exception e) {}
			    		}
			    		executor.shutdown();
			    		// Wait until all threads are finish
			    	    while (!executor.isTerminated()) {}
		    		}
		    	}
		    }
		    
		    return response;
		    
		} catch (Exception e) {
			Debug.PrintError(this, "Did you specify the action?");
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {
		this.dialog.cancel();
		Intent intent = new Intent();
		intent.setAction(this.action);
		if (this.action == Constants.Actions.GET_HIPERS) {
			intent.putExtra(Constants.Extras.HIPERS, result);
		} else if (this.action == Constants.Actions.GET_PRODUTOS) {
			intent.putExtra(Constants.Extras.PRODUTOS, result);
		} else if (this.action == Constants.Actions.GET_CATEGORIAS) {
			intent.putExtra(Constants.Extras.CATEGORIAS, result);
		} else if (this.action == Constants.Actions.GET_CATEGORIA) {
			intent.putExtra(Constants.Extras.CATEGORIA, result);
		}
		this.context.getApplicationContext().sendBroadcast(intent);
	}
	
	public class FetchImage implements Runnable {
		private final String url;
		private final String fileName;
		
		FetchImage(String url, String fileName) {
			this.url = url;
			this.fileName = fileName;
		}

		@Override
		public void run() {
			if (!Storage.fileExists(HiperPrecos.getInstance(), this.fileName)) {
				Storage.storeFileToStorage(HiperPrecos.getInstance(), this.url, this.fileName);
			}
		}
	}
}