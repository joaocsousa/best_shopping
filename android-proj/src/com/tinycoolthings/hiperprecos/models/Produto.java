package com.tinycoolthings.hiperprecos.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import com.tinycoolthings.hiperprecos.HiperPrecos;

public class Produto {

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
	private Hiper hiper = null;
	
	public Produto(JSONObject prodJson) {
		try {
			Integer prodID = prodJson.getInt("id");
			this.setId(prodID);
		} catch (JSONException e) {}
		try {
			String prodNome = prodJson.getString("nome");
			this.setNome(prodNome);
		} catch (JSONException e) {}
		try {
			String prodMarca = prodJson.getString("marca");
			this.setMarca(prodMarca);
		} catch (JSONException e) {}
		try {
			String prodPeso = prodJson.getString("peso");
			this.setPeso(prodPeso);
		} catch (JSONException e) {}
		try {
			Double prodPreco = prodJson.getDouble("preco");
			this.setPreco(prodPreco);
		} catch (JSONException e) {}
		try {
			Double prodPrecoKg = prodJson.getDouble("preco_kg");
			this.setPrecoKg(prodPrecoKg);
		} catch (JSONException e) {}
		try {
			String prodUrlPagina = prodJson.getString("url_pagina");
			this.setUrlPagina(prodUrlPagina);
		} catch (JSONException e) {}
		try {
			String prodUrlImagem = prodJson.getString("url_imagem");
			this.setUrlImagem(prodUrlImagem);
		} catch (JSONException e) {}
		try {
			Double prodDesconto = prodJson.getDouble("desconto");
			this.setDesconto(prodDesconto);
		} catch (JSONException e) {}
		try {
			String prodLastUpdated = prodJson.getString("last_updated");
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
			formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
			Date value = null;
			Calendar lastUpdateCal = Calendar.getInstance();
		    try {
				value = formatter.parse(prodLastUpdated);
				lastUpdateCal.setTime(value);
				this.setLastUpdate(lastUpdateCal);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} catch (JSONException e) {}
		try {
			Integer catPai = prodJson.getInt("categoria_pai");
			Categoria categoriaPai = HiperPrecos.getInstance().getCategoriaById(catPai);
			if (catPai>0 && catPai!=null && categoriaPai == null) {
				categoriaPai = new Categoria();
				categoriaPai.setId(catPai);
			}
			this.setCategoriaPai(categoriaPai);
		} catch (JSONException e) {}
		try {
			Integer hiperID = prodJson.getInt("hiper");
			Hiper hiper = HiperPrecos.getInstance().getHiperById(hiperID);
			this.setHiper(hiper);
		} catch (JSONException e) {}
	}

	public Produto() {}
	
	public Boolean hasLoaded() {
		if (this.urlPagina!=null) {
			return true;
		}
		return false;
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

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		if (marca!="null") {
			this.marca = marca;
		}
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

	public Hiper getHiper() {
		return this.hiper;
	}
	
	public void setHiper(Hiper hiper) {
		this.hiper = hiper;
	}
	
	public void merge(Produto produto) {
		if (this.id==null && produto.getId()!=null) {
			this.setId(produto.getId());
		}
		if (this.nome==null && produto.getNome()!=null) {
			this.setNome(produto.getNome());
		}
		if (this.marca==null && produto.getMarca()!=null) {
			this.setMarca(produto.getMarca());
		}
		if (this.preco==null && produto.getPreco()!=null) {
			this.setPreco(produto.getPreco());
		}
		if (this.precoKg==null && produto.getPrecoKg()!=null) {
			this.setPrecoKg(produto.getPrecoKg());
		}
		if (this.peso==null && produto.getPeso()!=null) {
			this.setPeso(produto.getPeso());
		}
		if (this.urlPagina==null && produto.getUrlPagina()!=null) {
			this.setUrlPagina(produto.getUrlPagina());
		}
		if (this.urlImagem==null && produto.getUrlImagem()!=null) {
			this.setUrlImagem(produto.getUrlImagem());
		}
		if (this.desconto==null && produto.getDesconto()!=null) {
			this.setDesconto(produto.getDesconto());
		}
		if (this.categoriaPai==null && produto.getCategoriaPai()!=null) {
			this.setCategoriaPai(produto.getCategoriaPai());
		}
		if (this.lastUpdate==null && produto.getLastUpdate()!=null) {
			this.setLastUpdate(produto.getLastUpdate());
		}
	}
	
}
