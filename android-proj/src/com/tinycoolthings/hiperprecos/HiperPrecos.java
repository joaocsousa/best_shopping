package com.tinycoolthings.hiperprecos;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Application;
import android.content.Context;

import com.tinycoolthings.hiperprecos.models.Categoria;
import com.tinycoolthings.hiperprecos.models.Hiper;
import com.tinycoolthings.hiperprecos.models.Produto;
import com.tinycoolthings.hiperprecos.serverComm.CallWebServiceTask;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Constants.Server.Parameter.Name;
import com.tinycoolthings.hiperprecos.utils.Debug;

public class HiperPrecos extends Application {

	private static HiperPrecos instance = null;

	private static Context appContext;
	
	private ArrayList<Hiper> hipers = null;
	
	private ArrayList<Produto> latestProdSearch = new ArrayList<Produto>();
	
	private ArrayList<Categoria> latestCatSearch = new ArrayList<Categoria>();
	
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
        hipers = new ArrayList<Hiper>();
    }
    
    public void clearHipers() {
    	this.hipers.clear();
    }
    
    public ArrayList<Hiper> getHipers() {
    	return this.hipers;
    }
    
    public Categoria getCategoriaById(Integer id) {
    	Categoria categoria = null;
    	for (int i=0;categoria==null && i<hipers.size();i++) {
    		categoria = hipers.get(i).getCategoriaById(id);
    	}
    	return categoria;
    }
    
    public Hiper getHiperById(Integer id) {
    	for (int i=0;i<hipers.size();i++) {
    		if (hipers.get(i).getId().equals(id)) {
    			return hipers.get(i);
    		}
    	}
    	return null;
    }
    
    public void addCategoria(Categoria categoria) {
		for (int i=0;i<hipers.size();i++) {
			if (hipers.get(i).getId().equals(categoria.getHiper().getId())) {
				hipers.get(i).addCategoria(categoria);
				break;
			}
		}
	}

	public void addHiper(Hiper hiper) {
		this.hipers.add(hiper);
	}

	public Integer getNumberOfHipers() {
		return this.hipers.size();
	}
	
	public Produto getProdutoById(Integer prodID) {
		Produto produto = null;
    	for (int i=0;produto==null && i<hipers.size();i++) {
    		produto = hipers.get(i).getProdutoById(prodID);
    	}
    	return produto;
	}

	public Categoria addCategoria(JSONObject currCatJson) {
		Categoria categoria = new Categoria(currCatJson);
		if (categoria.getCategoriaPai().getId()!=null) {
			Debug.PrintDebug(this, "Categoria: Added: " + categoria.getNome() + " | ID: " + categoria.getId() + " | Pai: " + categoria.getCategoriaPai().getNome());
			categoria.getCategoriaPai().addSubCategoria(categoria);
		} else {
			categoria.getHiper().addCategoria(categoria);
		}
		return categoria;
	}

	public Produto addProduto(JSONObject jsonObject) {
		Produto produto = new Produto(jsonObject);
		if (produto.getCategoriaPai()!=null) {
			produto.getCategoriaPai().addProduto(produto);
		}
		return produto;
	}
	
	public void setAppContext(Context ctx) {
		HiperPrecos.appContext = ctx;
	}

	public Context getAppContext() {
		return HiperPrecos.appContext;
	}
	
	public void search(String text) {
		CallWebServiceTask search = new CallWebServiceTask(Constants.Actions.SEARCH);
		search.addParameter(Name.SEARCH_QUERY, text);
		search.execute();
	}
	
	public void setLatestProdSearch(ArrayList<Produto> prodSearch) {
		this.latestProdSearch.clear();
		this.latestProdSearch.addAll(prodSearch);
	}
	
	public ArrayList<Produto> getLatestProdSearch() {
		return this.latestProdSearch;
	}
	
	public void setLatestCatSearch(ArrayList<Categoria> catSearch) {
		this.latestCatSearch.clear();
		this.latestCatSearch.addAll(catSearch);
	}
	
	public ArrayList<Categoria> getLatestCatSearch() {
		return this.latestCatSearch;
	}
	
}
