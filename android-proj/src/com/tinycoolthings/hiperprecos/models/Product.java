package com.tinycoolthings.hiperprecos.models;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "products")
public class Product {

	public static final String PARENT_CATEGORY_FIELD_NAME = "parentCat";
	public static final String NAME_FIELD_NAME = "name";
	public static final String BRAND_FIELD_NAME = "brand";
	public static final String PRICE_FIELD_NAME = "price";
	public static final String HYPER_FIELD_NAME = "hyper";

	@DatabaseField(id = true)
	private int id;
	@DatabaseField(canBeNull = false, columnName = NAME_FIELD_NAME)
	private String name;
	@DatabaseField(canBeNull = true, columnName = BRAND_FIELD_NAME)
	private String brand;
	@DatabaseField(canBeNull = true, columnName = PRICE_FIELD_NAME)
	private Double price;
	@DatabaseField(canBeNull = true)
	private Double priceKg;
	@DatabaseField(canBeNull = true)
	private String weight;
	@DatabaseField(canBeNull = true)
	private String ulrPage;
	@DatabaseField(canBeNull = true)
	private String ulrImage;
	@DatabaseField(canBeNull = true)
	private Double discount;
	@DatabaseField(foreign = true, canBeNull = false, columnName = PARENT_CATEGORY_FIELD_NAME)
	private Category parentCat;
	@DatabaseField(canBeNull = true)
	private Date latestUpdate;
	@DatabaseField(foreign = true, canBeNull = false, columnName = HYPER_FIELD_NAME)
	private Hyper hyper;
	
	Product() {
	}
	
	public Product(int id, String name, String brand, Double price,
			Double priceKg, String weight, String ulrPage, String ulrImage,
			Double discount, Category parentCat, Date latestUpdate,
			Hyper hyper) {
		this.id = id;
		this.name = name;
		this.brand = brand;
		this.price = price;
		this.priceKg = priceKg;
		this.weight = weight;
		this.ulrPage = ulrPage;
		this.ulrImage = ulrImage;
		this.discount = discount;
		this.parentCat = parentCat;
		this.latestUpdate = latestUpdate;
		this.hyper = hyper;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getBrand() {
		return brand;
	}

	public Double getPrice() {
		return price;
	}

	public Double getPriceKg() {
		return priceKg;
	}

	public String getWeight() {
		return weight;
	}

	public String getUlrPage() {
		return ulrPage;
	}

	public String getUrlImage() {
		return ulrImage;
	}

	public Double getDiscount() {
		return discount;
	}

	public Date getLatestUpdate() {
		return latestUpdate;
	}

	public Category getParentCat() {
		return parentCat;
	}

	public Hyper getHyper() {
		return hyper;
	}

	@Override
	public int hashCode() {
		return (this.name + this.id).hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != getClass()) {
			return false;
		}
		return (name.equals(((Product) other).name)
				&& (id == ((Product) other).id) && hyper
					.equals(((Product) other).hyper));
	}

}
