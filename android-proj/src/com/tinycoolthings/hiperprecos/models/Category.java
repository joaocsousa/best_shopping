package com.tinycoolthings.hiperprecos.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tinycoolthings.hiperprecos.utils.Utils;

import java.util.Date;

@DatabaseTable(tableName = "categories")
public class Category {

	public static final String PARENT_CATEGORY_FIELD_NAME = "parentCat";
	public static final String HYPER_FIELD_NAME = "hyper";
	public static final String NAME_FIELD_NAME = "name";
	public static final String ID_FIELD_NAME = "id";

	@DatabaseField(id = true)
	private int id;
	@DatabaseField(canBeNull = false, columnName = NAME_FIELD_NAME)
	private String name;
	@DatabaseField(foreign = true, canBeNull = false, columnName = HYPER_FIELD_NAME)
	private Hyper hyper;
	@DatabaseField(foreign = true, canBeNull = true, columnName = PARENT_CATEGORY_FIELD_NAME)
	private Category parentCat;
	@DatabaseField(canBeNull = true)
	private Date latestUpdate;

	Category() {
	}

	public Category(int id, String name, Hyper hiper, Category parentCat,
			Date latestUpdate) {
		this.id = id;
		this.name = name;
		this.hyper = hiper;
		this.parentCat = parentCat;
		this.latestUpdate = latestUpdate;
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
		return (this.name + this.id).hashCode();
	}

	@Override
	public boolean equals(Object other) {
        return !(other == null || other.getClass() != getClass()) && (name.equals(((Category) other).name) && (id == ((Category) other).id));
	}
	
	@Override
	public String toString() {
		String msg = "";
		try {
			msg += "Category:\n\tID: "+this.id+"\n\tName: "+this.name+"\n\tLast Update: "+Utils.dateToStr(this.latestUpdate)+"\n\tHyper: "+this.hyper.getName()+"\n\tParent: "+this.parentCat.getId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}

}
