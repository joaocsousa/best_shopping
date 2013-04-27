package com.tinycoolthings.hiperprecos;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.Context;

import com.tinycoolthings.hiperprecos.models.Categoria;
import com.tinycoolthings.hiperprecos.models.Hiper;
import com.tinycoolthings.hiperprecos.models.Produto;
import com.tinycoolthings.hiperprecos.utils.Debug;

public class HiperPrecos extends Application {

	private static HiperPrecos instance = null;

	private static Context appContext;
	
	private ArrayList<Hiper> hipers = null;
	
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
        //provide an instance for our static accessors
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
    	for (int i=0;i<hipers.size();i++) {
    		categoria = hipers.get(i).getCategoriaById(id);
    		if (categoria != null) {
    			break;
    		}
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
		int catID = 0;
		String catNome = "";
		int catHiperID = 0;
		int catPaiID = 0;
		JSONArray subCategorias = null;
		JSONArray produtos = null;
		try {
			catID = currCatJson.getInt("id");
		} catch (JSONException e) {}
		try {
			catNome = currCatJson.getString("nome");
		} catch (JSONException e) {}
		try {
			catHiperID = currCatJson.getInt("hiper");
		} catch (JSONException e) {}
		try {
			catPaiID = currCatJson.getInt("categoria_pai");
		} catch (JSONException e) {}
		try {
			subCategorias = currCatJson.getJSONArray("sub_categorias");
		} catch (JSONException e) {}
		try {
			produtos = currCatJson.getJSONArray("produtos");
		} catch (JSONException e) {}
		Categoria categoria = new Categoria();
		categoria.setId(catID);
		categoria.setNome(catNome);
		Hiper hiper = getHiperById(catHiperID);
		categoria.setHiper(hiper);
		Categoria categoriaPai = hiper.getCategoriaById(catPaiID);
		categoria.setCategoriaPai(categoriaPai);
		if (subCategorias != null && subCategorias.length() > 0) {
			JSONObject currSubCatJson = null;
			int subCatID = 0;
			String subCatNome = "";
			for (int i=0;i<subCategorias.length();i++) {
				try {
					currSubCatJson = subCategorias.getJSONObject(i);
					subCatID = 0;
					subCatNome = "";
					try {
						subCatID = currSubCatJson.getInt("id");
					} catch (JSONException e) {}
					try {
						subCatNome = currSubCatJson.getString("nome");
					} catch (JSONException e) {}
					Categoria subCategoria = new Categoria();
					subCategoria.setCategoriaPai(categoria);
					subCategoria.setHiper(hiper);
					subCategoria.setNome(subCatNome);
					subCategoria.setId(subCatID);
					categoria.addSubCategoria(subCategoria);
				} catch (JSONException e) {}
			}
		}
		if (produtos != null && produtos.length() > 0) {
			JSONObject currProdJson = null;
			int prodID = 0;
			String prodNome = "";
			String prodMarca = "";
			String prodPeso = "";
			Double prodPreco = 0.0;
			String prodUrlImagem = "";
			Double prodDesconto = 0.0;
			for (int i=0;i<produtos.length();i++) {
				try {
					currProdJson = produtos.getJSONObject(i);
					try {
						prodID = currProdJson.getInt("id");
					} catch (JSONException e) {}
					try {
						prodNome = currProdJson.getString("nome");
					} catch (JSONException e) {}
					try {
						prodMarca = currProdJson.getString("marca");
					} catch (JSONException e) {}
					try {
						prodPeso = currProdJson.getString("peso");
					} catch (JSONException e) {}
					try {
						prodPreco = currProdJson.getDouble("preco");
					} catch (JSONException e) {}
					try {
						prodUrlImagem = currProdJson.getString("url_imagem");
					} catch (JSONException e) {}
					try {
						prodDesconto = currProdJson.getDouble("desconto");
					} catch (JSONException e) {}
					Produto produto = new Produto();
					produto.setId(prodID);
					produto.setNome(prodNome);
					produto.setMarca(prodMarca);
					produto.setPeso(prodPeso);
					produto.setPreco(prodPreco);
					produto.setUrlImagem(prodUrlImagem);
					produto.setDesconto(prodDesconto);
					categoria.addProduto(produto);
				} catch (JSONException e) {}
			}
		}
		
		if (categoriaPai!=null) {
			Debug.PrintDebug(this, "Categoria: Added: " + categoria.getNome() + " | ID: " + categoria.getId() + " | Pai: " + categoria.getCategoriaPai().getNome());
			categoriaPai.addSubCategoria(categoria);
		} else {
			hiper.addCategoria(categoria);
		}
		return categoria;
	}

	public static void setAppContext(Context ctx) {
		HiperPrecos.appContext = ctx;
	}

	public static Context getAppContext() {
		return HiperPrecos.appContext;
	}
}
