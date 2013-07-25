package com.tinycoolthings.bestshopping.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "hypers")
public class Hyper {
	
	public static final String LATEST_UPDATE_FIELD_NAME = "latestUpdate";
	public static final String ID_FIELD_NAME = "id";
	
	@DatabaseField(id = true, columnName = ID_FIELD_NAME)
	private int id;
	@DatabaseField(canBeNull = false)
	private String name;
	@DatabaseField(canBeNull = true, columnName = LATEST_UPDATE_FIELD_NAME)
	private Date latestUpdate;
	
	Hyper() {
	}
	
	public Hyper(int id, String name, Date latestUpdate) {
		this.id = id;
		this.name = name;
		this.latestUpdate = latestUpdate;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Date getLatestUpdate() {
		return this.latestUpdate;
	}

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
        return !(other == null || other.getClass() != getClass()) && (name.equals(((Hyper) other).name) && (id == ((Hyper) other).id));
	}

}
