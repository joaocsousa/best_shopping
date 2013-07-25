package com.tinycoolthings.bestshopping.serverComm;

import android.content.Intent;
import android.os.AsyncTask;

import com.tinycoolthings.bestshopping.BestShopping;
import com.tinycoolthings.bestshopping.utils.Constants;
import com.tinycoolthings.bestshopping.utils.Debug;
import com.tinycoolthings.bestshopping.utils.ImageStorage;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CallWebServiceTask extends AsyncTask <Void, Void, String> {
	private final String action;
	private final Map<Constants.Server.Parameter.Name, Object> params;
	private final Boolean hideDialogOnFinish;
	
	/**
	 * Starts a request to the web services
	 * @param action
	 * @param hideDialogOnFinish - to hide or not the waiting dialog once the request has finished
	 */
	public CallWebServiceTask(String action, boolean hideDialogOnFinish) {
		this.action = action;
		this.params = new HashMap<Constants.Server.Parameter.Name, Object>();
		this.hideDialogOnFinish = hideDialogOnFinish;
	}

	public void addParameter(Constants.Server.Parameter.Name name, Object value) {
		this.params.put(name, value);
	}
	
	@Override
	protected void onPreExecute() {
		BestShopping.getInstance().showWaitingDialog();
	}

	@Override
	protected String doInBackground(Void... params) {
		try {
			String URL = "";
			if (this.action.equals(Constants.Actions.GET_HYPERS)) {
				URL = Constants.Server.Definitions.HIPERS_URL;
			} else if (this.action.equals(Constants.Actions.GET_PRODUTOS)) {
				URL = Constants.Server.Definitions.PRODUTOS_URL;
			} else if (this.action.equals(Constants.Actions.GET_PRODUCT)) {
				URL = Constants.Server.Definitions.PRODUTOS_URL;
				if (!this.params.containsKey(Constants.Server.Parameter.Name.PRODUTO_ID)) {
					Debug.PrintError(this, "No produto ID! Did you specify it?");
				} else {
					int prodID = (Integer) this.params.get(Constants.Server.Parameter.Name.PRODUTO_ID);
					URL += prodID;
				}
			} else if (this.action.equals(Constants.Actions.GET_CATEGORIES)) {
				URL = Constants.Server.Definitions.CATEGORIES_URL;
			} else if (this.action.equals(Constants.Actions.GET_CATEGORY)) {
				URL = Constants.Server.Definitions.CATEGORIES_URL;
				if (!this.params.containsKey(Constants.Server.Parameter.Name.CATEGORIA_ID)) {
					Debug.PrintError(this, "No categoria ID! Did you specify it?");
				} else {
					int catID = (Integer) this.params.get(Constants.Server.Parameter.Name.CATEGORIA_ID);
					URL += catID;
				}
			} else if (this.action.equals(Constants.Actions.SEARCH)) {
				URL = Constants.Server.Definitions.SEARCH_URL;
			} else if (this.action.equals(Constants.Actions.GET_LATEST_UPDATE)) {
				URL = Constants.Server.Definitions.LATEST_UPDATE_URL;
			}
			
			RestClient client = new RestClient(URL);
			
			client.AddParam("format", "json");
			
			String logParams = "?format=json&";
			
			Iterator<Entry<Constants.Server.Parameter.Name, Object>> it = this.params.entrySet().iterator();
		    while (it.hasNext()) {
		    	Entry<Constants.Server.Parameter.Name, Object> pair = (Entry<Constants.Server.Parameter.Name, Object>)it.next();
		        switch (pair.getKey()) {
			        case HYPER:
			        	logParams+="hiper="+String.valueOf(pair.getValue())+"&";
			        	client.AddParam("hiper", String.valueOf(pair.getValue()));
			        	break;
			        case PARENT_CATEGORY:
			        	logParams+="categoria_pai="+String.valueOf(pair.getValue())+"&";
			        	client.AddParam("categoria_pai", String.valueOf(pair.getValue()));
			        	break;
			        case DESCONTO:
			        	logParams+="desconto="+String.valueOf(pair.getValue())+"&";
			        	client.AddParam("desconto", String.valueOf(pair.getValue()));
						break;
			        case SEARCH_QUERY:
			        	logParams+="q="+String.valueOf(pair.getValue())+"&";
			        	client.AddParam("q", String.valueOf(pair.getValue()));
						break;
					default:
						break;
		        }
		        it.remove(); // avoids a ConcurrentModificationException
		    }
		    
		    try {
		    	Debug.PrintWarning(this, URL+logParams);
		        client.Execute(RestClient.RequestMethod.GET);
		    } catch (Exception e) {
		        e.printStackTrace();
		    }

		    String response = client.getResponse();
		    
		    Object json = new JSONTokener(response).nextValue();
		    if (json instanceof JSONObject) {
		    	JSONObject jsonObj = (JSONObject)json;
		    	ArrayList<String> prodJsonNames = new ArrayList<String>();
		    	if (jsonObj.has("produtos")) {
		    		prodJsonNames.add("produtos");
		    	} else if (jsonObj.has("prodPorNome")) {
		    		prodJsonNames.add("prodPorNome");
		    	} else if (jsonObj.has("prodPorMarca")) {
		    		prodJsonNames.add("prodPorMarca");
		    	}
                for (String prodJsonName : prodJsonNames) {
                    JSONArray produtos = jsonObj.getJSONArray(prodJsonName);
                    if (produtos.length() > 0) {
                        ExecutorService executor = Executors.newFixedThreadPool(produtos.length());
                        for (int i = 0; i < produtos.length(); i++) {
                            JSONObject currProdJson = produtos.getJSONObject(i);
                            try {
                                String currProdUrl = currProdJson.getString("url_imagem");
                                // test url
                                URL u = new URL(currProdUrl);
                                u.toURI();
                                //////////////
                                String fileName = ImageStorage.getFileName(currProdUrl, currProdJson.getString("nome"), currProdJson.getString("marca"));
                                Runnable worker = new FetchImage(currProdUrl, fileName);
                                executor.execute(worker);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        executor.shutdown();
                        // Wait until all threads are finish
                        while (!executor.isTerminated()) {
                        }
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
		if (hideDialogOnFinish) {
			BestShopping.getInstance().hideWaitingDialog();
		}
		Intent intent = new Intent();
		intent.setAction(this.action);
		if (this.action == Constants.Actions.GET_HYPERS) {
			intent.putExtra(Constants.Extras.HYPERS, result);
		} else if (this.action == Constants.Actions.GET_PRODUTOS) {
			intent.putExtra(Constants.Extras.PRODUCTS, result);
		} else if (this.action == Constants.Actions.GET_PRODUCT) {
			intent.putExtra(Constants.Extras.PRODUCT, result);
		} else if (this.action == Constants.Actions.GET_CATEGORIES) {
			intent.putExtra(Constants.Extras.CATEGORIES, result);
		} else if (this.action == Constants.Actions.GET_CATEGORY) {
			intent.putExtra(Constants.Extras.CATEGORY, result);
		} else if (this.action == Constants.Actions.SEARCH) {
			intent.putExtra(Constants.Extras.SEARCH_RESULT, result);
		} else if (this.action == Constants.Actions.GET_LATEST_UPDATE) {
			intent.putExtra(Constants.Extras.LATEST_UPDATE, result);
		}
		BestShopping.getInstance().sendBroadcast(intent);
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
			if (!ImageStorage.fileExists(BestShopping.getInstance(), this.fileName)) {
				Debug.PrintError(this, "Storing " + this.fileName);
				ImageStorage.storeFileToStorage(BestShopping.getInstance(), this.url, this.fileName);
			}
		}
	}
}