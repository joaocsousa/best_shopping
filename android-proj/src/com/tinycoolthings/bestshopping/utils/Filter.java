package com.tinycoolthings.bestshopping.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Filter implements Parcelable {

	private String productName = "";
	private Integer minPrice = 0;
	private Integer maxPrice = 0;
	private final List<String> brands = new ArrayList<String>();
	
	private boolean initialized = false;
	
	public Filter() {
	}

	public Filter(Parcel in) {
		readFromParcel(in);
	}

	public void setProductNameFilter(String nameFilter) {
		productName = nameFilter;
	}

	public String getProductNameFilter() {
		return productName;
	}

	public void setMinPriceFilter(Integer minPriceFilter) {
		minPrice = minPriceFilter;
	}

	public Integer getMinPriceFilter() {
		return minPrice;
	}

	public void setMaxPriceFilter(Integer maxPriceFilter) {
		maxPrice = maxPriceFilter;
	}

	public Integer getMaxPriceFilter() {
		return maxPrice;
	}

	public void addBrandFilter(String brand) {
		if (!brands.contains(brand)) {
			brands.add(brand);
		}
	}
	
	public void removeBrandFilter(String brand) {
		brands.remove(brand);
	}

	public List<String> getBrandsFilter() {
		return brands;
	}

	public void reset() {
		productName = "";
		minPrice = 0;
		maxPrice = 0;
		brands.clear();
		this.initialized = false;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(productName);
		dest.writeInt(minPrice);
		dest.writeInt(maxPrice);
		dest.writeStringList(brands);
		dest.writeInt(initialized ? 1 : 0);
	}

	private void readFromParcel(Parcel in) {
		productName = in.readString();
		minPrice = in.readInt();
		maxPrice = in.readInt();
		in.readStringList(brands);
		initialized = in.readInt() == 0;
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Filter createFromParcel(Parcel in) {
			return new Filter(in);
		}

		public Filter[] newArray(int size) {
			return new Filter[size];
		}
	};
	
	@Override
	public int describeContents() {
		return 0;
	}

	public void clone(Filter currFilter) {
		this.brands.addAll(currFilter.getBrandsFilter());
		this.maxPrice = currFilter.getMaxPriceFilter();
		this.minPrice = currFilter.getMinPriceFilter();
		this.productName = currFilter.getProductNameFilter();
		this.initialized = currFilter.initialized();
	}

	public void initialize(int minPriceFilter, int maxPriceFilter, List<String> allBrands) {
		if (this.initialized) {
			return;
		}
		this.minPrice = minPriceFilter;
		this.maxPrice = maxPriceFilter;
		this.productName = "";
		this.brands.addAll(allBrands);
		this.initialized = true;
	}
	
	public boolean initialized() {
		return initialized;
	}

	public void clearBrandFilter() {
		this.brands.clear();
	}
	
}
