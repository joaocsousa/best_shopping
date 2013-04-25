package com.tinycoolthings.hiperprecos.models;

import java.util.ArrayList;

import com.tinycoolthings.hiperprecos.utils.Debug;

import android.os.Parcel;
import android.os.Parcelable;

public class Categoria implements Parcelable {
	
	private Integer id = null;
	private String nome = null;
	private Hiper hiper = null;
	private Categoria categoriaPai = null;
	private ArrayList<Produto> produtos = new ArrayList<Produto>();
	private ArrayList<Categoria> subCategorias = new ArrayList<Categoria>();
	
	public Categoria() {}
	
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
	
	// Parcelable
	
	public Categoria(Parcel in) {
		this.id = in.readInt();
		this.nome = in.readString();
		this.hiper = in.readParcelable(Hiper.class.getClassLoader());
		this.categoriaPai = in.readParcelable(Categoria.class.getClassLoader());
		in.readTypedList(this.produtos, Produto.CREATOR);
		in.readTypedList(this.subCategorias, Categoria.CREATOR);
	}
	
	@Override
	public int describeContents() {
		return hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.id);
		dest.writeString(this.nome);
		dest.writeParcelable(this.hiper, flags);
		dest.writeParcelable(this.categoriaPai, flags);
		dest.writeTypedList(this.produtos);
		dest.writeTypedList(this.subCategorias);
	}

	public static final Parcelable.Creator<Categoria> CREATOR = new Parcelable.Creator<Categoria>() {
		public Categoria createFromParcel(Parcel in) {
		    return new Categoria(in);
		}
		
		public Categoria[] newArray(int size) {
		    return new Categoria[size];
		}
	};

	public ArrayList<Categoria> getSiblings() {
		if (this.getCategoriaPai().getId()==null) {
			return this.hiper.getCategorias();
		} else {
			return this.categoriaPai.getSubCategorias();
		}
	}
	
}
