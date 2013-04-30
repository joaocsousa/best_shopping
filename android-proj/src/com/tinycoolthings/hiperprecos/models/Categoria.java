package com.tinycoolthings.hiperprecos.models;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tinycoolthings.hiperprecos.HiperPrecos;

public class Categoria {
	
	private Integer id = null;
	private String nome = null;
	private Hiper hiper = null;
	private Categoria categoriaPai = null;
	private ArrayList<Produto> produtos = new ArrayList<Produto>();
	private ArrayList<Categoria> subCategorias = new ArrayList<Categoria>();
	
	public Categoria() {}
	
	public Categoria(JSONObject currCatJson) {
		int catID = -1;
		String catNome = null;
		int catHiperID = -1;
		int catPaiID = -1;
		JSONArray subCategorias = null;
		JSONArray produtos = null;
		try {
			catID = currCatJson.getInt("id");
		} catch (JSONException e) {}
		try {
			if (currCatJson.getString("nome")!="" && currCatJson.getString("nome")!="null") {
				catNome = currCatJson.getString("nome");
			}
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
		/* we have all the data */
		this.setId(catID);
		this.setNome(catNome);
		Hiper hiper = HiperPrecos.getInstance().getHiperById(catHiperID);
		this.setHiper(hiper);
		if (catPaiID!=-1) {
			Categoria categoriaPai = hiper.getCategoriaById(catPaiID);
			this.setCategoriaPai(categoriaPai);
		}
		if (subCategorias != null && subCategorias.length() > 0) {
			JSONObject currSubCatJson = null;
			for (int i=0;i<subCategorias.length();i++) {
				try {
					currSubCatJson = subCategorias.getJSONObject(i);
					Categoria subCategoria = new Categoria(currSubCatJson);
					subCategoria.setCategoriaPai(this);
					subCategoria.setHiper(hiper);
					this.addSubCategoria(subCategoria);
				} catch (JSONException e) {}
			}
		}
		if (produtos != null && produtos.length() > 0) {
			JSONObject currProdJson = null;
			for (int i=0;i<produtos.length();i++) {
				try {
					currProdJson = produtos.getJSONObject(i);
					Produto produto = new Produto(currProdJson);
					produto.setCategoriaPai(this);
					this.addProduto(produto);
				} catch (JSONException e) {}
			}
		}
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Categoria getCategoriaPai() {
		if (this.categoriaPai == null) {
			return new Categoria();
		}
		return categoriaPai;
	}

	public boolean hasCategoriaPai() {
		if (this.categoriaPai.id == null) {
			return false;
		}
		return true;
	}
	
	public void setCategoriaPai(Categoria categoriaPai) {
		this.categoriaPai = categoriaPai;
	}

	public Hiper getHiper() {
		return hiper;
	}
	
	public void setHiper(Hiper hiper) {
		this.hiper = hiper;
	}

	public ArrayList<Produto> getProdutos() {
		return produtos;
	}

	public ArrayList<Categoria> getSubCategorias() {
		return subCategorias;
	}
	
	public Categoria getSubCategoriaById(Integer id) {
		for (int i=0;i<this.subCategorias.size();i++) {
			if (id.equals(this.subCategorias.get(i).getId())) {
				return this.subCategorias.get(i);
			}
		}
		return null;
	}
	
	public boolean hasSubCategoria(Categoria categoria) {
		if (this.getSubCategoriaById(categoria.getId())!=null) {
			return true;
		}
		return false;
	}
	
	public void addSubCategoria(Categoria subCategoria) {
		if (this.hasSubCategoria(subCategoria)) {
			this.getSubCategoriaById(subCategoria.getId()).merge(subCategoria);
		} else {
			this.subCategorias.add(subCategoria);
		}
	}
	
	public Produto getProdutoById(Integer id) {
		for (int i=0;i<this.produtos.size();i++) {
			if (id.equals(this.produtos.get(i).getId())) {
				return this.produtos.get(i);
			}
		}
		return null;
	}
	
	public boolean hasProduto(Produto produto) {
		if (this.getProdutoById(produto.getId())!=null) {
			return true;
		}
		return false;
	}

	public boolean hasProdutoWithID(Integer prodID) {
		if (this.getProdutoById(prodID)!=null) {
			return true;
		}
		return false;
	}
	
	public void addProduto(Produto produto) {
		if (this.hasProduto(produto)) {
			this.getProdutoById(produto.getId()).merge(produto);
		} else {
			this.produtos.add(produto);
		}
	}

	public boolean hasSubCategorias() {
		if (this.subCategorias.size() > 0) {
			return true;
		}
		return false;
	}

	public boolean hasProdutos() {
		if (this.produtos.size() > 0) {
			return true;
		}
		return false;
	}

	public boolean hasLoaded() {
		return this.hasProdutos() || this.hasSubCategorias();
	}
	
	public void merge(Categoria categoria) {
		if (this.id==null && categoria.getId()!=null) {
			this.id = categoria.getId();
		}
		if ( (this.nome==null || this.nome.equals("")) && categoria.getId()!=null) {
			this.nome = categoria.getNome();
		}
		if ( (this.hiper==null) && categoria.getHiper()!=null) {
			this.hiper = categoria.getHiper();
		}
		if ( (this.categoriaPai==null) && categoria.getCategoriaPai()!=null) {
			this.categoriaPai = categoria.getCategoriaPai();
		}
		if ( ((this.produtos.size()==0) && categoria.getProdutos().size()>0) ||
			 (this.produtos.size() < categoria.getProdutos().size())
			) {
			this.produtos = categoria.getProdutos();
		}
		if ( ((this.subCategorias.size()==0) && categoria.getSubCategorias().size()>0) ||
			 (this.subCategorias.size() < categoria.getSubCategorias().size())
			) {
			this.subCategorias = categoria.getSubCategorias();
		}
	}

	public ArrayList<Categoria> getSiblings() {
		if (this.getCategoriaPai().getId()==null) {
			return this.hiper.getCategorias();
		} else {
			return this.categoriaPai.getSubCategorias();
		}
	}
	
}
