package com.tinycoolthings.hiperprecos.utils;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Filter implements Parcelable {

	private String productName = "";
	private Integer minPrice = 0;
	private Integer maxPrice = 0;
	private List<String> brands = new ArrayList<String>();

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
		brands.add(brand);
	}

	public List<String> getBrandsFilter() {
		return brands;
	}

	public void reset() {
		productName = "";
		minPrice = 0;
		maxPrice = 0;
		brands.clear();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(productName);
		dest.writeInt(minPrice);
		dest.writeInt(maxPrice);
		dest.writeStringList(brands);
	}

	private void readFromParcel(Parcel in) {
		productName = in.readString();
		minPrice = in.readInt();
		maxPrice = in.readInt();
		in.readStringList(brands);
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
	
}
