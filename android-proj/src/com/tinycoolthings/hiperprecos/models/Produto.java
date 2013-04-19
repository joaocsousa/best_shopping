package com.tinycoolthings.hiperprecos.models;

import java.util.Calendar;

import android.os.Parcel;
import android.os.Parcelable;

public class Produto implements Parcelable {

	private Integer id = null;
	private String nome = null;
	private String marca = null;
	private Double preco = null;
	private Double precoKg = null;
	private String peso = null;
	private String urlPagina = null;
	private String urlImagem = null;
	private Double desconto = null;
	private Categoria categoriaPai = null;
	private Calendar lastUpdate = null;
	
	public Produto() {}

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

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public Double getPreco() {
		return preco;
	}

	public void setPreco(Double d) {
		this.preco = d;
	}

	public Double getPrecoKg() {
		return precoKg;
	}

	public void setPrecoKg(Double precoKg) {
		this.precoKg = precoKg;
	}

	public String getPeso() {
		return peso;
	}

	public void setPeso(String peso) {
		this.peso = peso;
	}

	public String getUrlPagina() {
		return urlPagina;
	}

	public void setUrlPagina(String urlPagina) {
		this.urlPagina = urlPagina;
	}

	public String getUrlImagem() {
		return urlImagem;
	}

	public void setUrlImagem(String urlImagem) {
		this.urlImagem = urlImagem;
	}

	public Double getDesconto() {
		return desconto;
	}

	public void setDesconto(Double desconto) {
		this.desconto = desconto;
	}

	public Categoria getCategoriaPai() {
		return categoriaPai;
	}

	public void setCategoriaPai(Categoria categoriaPai) {
		this.categoriaPai = categoriaPai;
	}

	public Calendar getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Calendar lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public void merge(Produto produto) {
		if (this.id==null && produto.getId()!=null) {
			this.id = produto.getId();
		}
		if (this.nome==null && produto.getNome()!=null) {
			this.nome = produto.getNome();
		}
		if (this.marca==null && produto.getMarca()!=null) {
			this.marca = produto.getMarca();
		}
		if (this.preco==null && produto.getPreco()!=null) {
			this.preco = produto.getPreco();
		}
		if (this.precoKg==null && produto.getPrecoKg()!=null) {
			this.precoKg = produto.getPrecoKg();
		}
		if (this.peso==null && produto.getPeso()!=null) {
			this.peso = produto.getPeso();
		}
		if (this.urlPagina==null && produto.getUrlPagina()!=null) {
			this.urlPagina = produto.getUrlPagina();
		}
		if (this.urlImagem==null && produto.getUrlImagem()!=null) {
			this.urlImagem = produto.getUrlImagem();
		}
		if (this.desconto==null && produto.getDesconto()!=null) {
			this.desconto = produto.getDesconto();
		}
		if (this.categoriaPai==null && produto.getCategoriaPai()!=null) {
			this.categoriaPai = produto.getCategoriaPai();
		}
		if (this.lastUpdate==null && produto.getLastUpdate()!=null) {
			this.lastUpdate = produto.getLastUpdate();
		}
	}
	
	// Parcelable
	
	public Produto(Parcel in) {
		this.id = in.readInt();
		this.nome = in.readString();
		this.marca = in.readString();
		this.preco = in.readDouble();
		this.precoKg = in.readDouble();
		this.peso = in.readString();
		this.urlPagina = in.readString();
		this.urlImagem = in.readString();
		this.desconto = in.readDouble();
		this.categoriaPai = in.readParcelable(Categoria.class.getClassLoader());
		this.lastUpdate = Calendar.getInstance();
		this.lastUpdate.setTimeInMillis(in.readLong());
	}
	
	@Override
	public int describeContents() {
		return hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.id);
		dest.writeString(this.nome);
		dest.writeString(this.marca);
		dest.writeDouble(this.preco);
		dest.writeDouble(this.precoKg);
		dest.writeString(this.peso);
		dest.writeString(this.urlPagina);
		dest.writeString(this.urlImagem);
		dest.writeDouble(this.desconto);
		dest.writeParcelable(this.categoriaPai, flags);
		dest.writeLong(this.lastUpdate.getTimeInMillis());
	}
	
	public static final Parcelable.Creator<Produto> CREATOR = new Parcelable.Creator<Produto>() {
		public Produto createFromParcel(Parcel in) {
		    return new Produto(in);
		}
		
		public Produto[] newArray(int size) {
		    return new Produto[size];
		}
	};
	
}
