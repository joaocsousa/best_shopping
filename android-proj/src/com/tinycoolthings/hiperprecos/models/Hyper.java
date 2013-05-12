package com.tinycoolthings.hiperprecos.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "hypers")
public class Hyper {
	
	@DatabaseField(id = true)
	private int id;
	@DatabaseField(canBeNull = false)
	private String name;
	
	Hyper() {
	}
	
	public Hyper(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != getClass()) {
			return false;
		}
		return ( name.equals(((Hyper) other).name) && (id==((Hyper) other).id) );
	}

}
