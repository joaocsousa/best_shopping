package com.tinycoolthings.hiperprecos.models;

import java.util.Calendar;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "products")
public class Product {

	@DatabaseField(id = true)
	private int id;
	@DatabaseField(canBeNull = false)
	private String name;
	@DatabaseField(canBeNull = true)
	private String brand;
	@DatabaseField(canBeNull = true)
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
	@DatabaseField(canBeNull = true)
	private Calendar latestUpdate;
	@DatabaseField(foreign = true, canBeNull = false)
	private Category parentCat;
	
	Product() {}

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

	public String getUlrImage() {
		return ulrImage;
	}

	public Double getDiscount() {
		return discount;
	}

	public Calendar getLatestUpdate() {
		return latestUpdate;
	}

	public Category getParentCat() {
		return parentCat;
	}
	
	@Override
	public int hashCode() {
		return (this.name+this.id).hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != getClass()) {
			return false;
		}
		return ( name.equals(((Product) other).name) && (id==((Product) other).id) );
	}
	
}
