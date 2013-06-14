package com.tinycoolthings.hiperprecos.models;

import java.util.Iterator;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "productList")
public class ProductList {
	
	@DatabaseField(id = true)
	private int id;
	
    ForeignCollection<Product> products;
	
	ProductList() {
	}
	
	public int getId() {
		return this.id;
	}
	
	public ForeignCollection<Product> getProducts() {
		return products;
	}
	
	@Override
	public boolean equals(Object other) {
		boolean equal = true;
		Iterator<Product> productIter = products.iterator();
		Iterator<Product> otherProdIter = ((ProductList)other).getProducts().iterator();
		while (productIter.hasNext()) {
			Product currProd = productIter.next();
			while (otherProdIter.hasNext()) {
				if (currProd.getId()!=otherProdIter.next().getId()) {
					equal = false;
				}
			}
		}
		
		return equal;
	}

}
