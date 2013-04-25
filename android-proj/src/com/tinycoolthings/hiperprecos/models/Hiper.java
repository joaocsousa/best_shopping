package com.tinycoolthings.hiperprecos.models;

import java.util.ArrayList;

import com.tinycoolthings.hiperprecos.utils.Debug;

import android.os.Parcel;
import android.os.Parcelable;

public class Hiper implements Parcelable {

	private Integer id = null;
	private String nome = null;
	private ArrayList<Categoria> categorias = new ArrayList<Categoria>();
	
	public Hiper() {}
	
	public Hiper(Integer id, String nome) {
		this.id = id;
		this.nome = nome;
		this.categorias = new ArrayList<Categoria>();
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

	public ArrayList<Categoria> getCategorias() {
		return categorias;
	}
	
	public boolean hasCategoria(Categoria categoria) {
		if (this.getCategoriaById(categoria.getId())!=null) {
			return true;
		}
		return false;
	}

	public void addCategoria(Categoria categoria) {
		if (this.hasCategoria(categoria)) {
			Debug.PrintDebug(this, "Categoria -> Merged: " + categoria.getNome() + " | ID: " + categoria.getId() + " | Hiper: " + this.getNome());
			this.getCategoriaById(categoria.getId()).merge(categoria);
		} else {
			Debug.PrintDebug(this, "Categoria -> Added: " + categoria.getNome() + " | ID: " + categoria.getId() + " | Hiper: " + this.getNome());
			this.categorias.add(categoria);
		}
	}
	
	private Categoria loopCategoriaToFind(Categoria categoria, Integer id) {
		Categoria res = null;
		if (id.equals(categoria.getId())) {
			return categoria;
		}
		for (int j = 0; res == null && j < categoria.getSubCategorias().size(); j++) {         
	        res = this.loopCategoriaToFind(categoria.getSubCategorias().get(j), id);
		}
		return res;
	}
	
	public Categoria getCategoriaById(Integer id) {
		Categoria res = null;
		for (int i=0;res == null && i<this.categorias.size();i++) {
			if (id.equals(this.categorias.get(i).getId())) {
				return this.categorias.get(i);
			} else {
				res = this.loopCategoriaToFind(this.categorias.get(i), id);
			}
		}
		return res;
	}

	// Parcelable
	
	public Hiper(Parcel in) {
		this.id = in.readInt();
		this.nome = in.readString();
		in.readTypedList(this.categorias, Categoria.CREATOR);
	}
	
	@Override
	public int describeContents() {
		return hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.id);
		dest.writeString(this.nome);
		dest.writeTypedList(this.categorias);
	}

	public static final Parcelable.Creator<Hiper> CREATOR = new Parcelable.Creator<Hiper>() {
		public Hiper createFromParcel(Parcel in) {
		    return new Hiper(in);
		}
		
		public Hiper[] newArray(int size) {
		    return new Hiper[size];
		}
	};

}
