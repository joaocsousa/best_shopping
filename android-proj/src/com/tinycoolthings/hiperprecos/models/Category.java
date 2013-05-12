package com.tinycoolthings.hiperprecos.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "categories")
public class Category {
	
	@DatabaseField(id = true)
	private int id;
	@DatabaseField(canBeNull = false)
	private String name;
	@DatabaseField(foreign = true, canBeNull = false)
	private Hyper hyper;
	@DatabaseField(foreign = true, canBeNull = true)
	private Category parentCat;
	
	Category() {}
	
	public Category(int id, String name, Hyper hiper, Category parentCat) {
		this.id = id;
		this.name = name;
		this.hyper = hiper;
		this.parentCat = parentCat;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Hyper getHyper() {
		return this.hyper;
	}
	
	public Category getParentCat() {
		return this.parentCat;
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
		return ( name.equals(((Category) other).name) && (id==((Category) other).id) );
	}
	
}
