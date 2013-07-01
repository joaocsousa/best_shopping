package com.tinycoolthings.hiperprecos.utils;

public class Constants {
	public static final class Actions {
		public static final String GET_HYPERS = "COM.TINYCOOLTHINGS.HIPERPRECOS.GET_HIPERS";
		public static final String GET_CATEGORY = "COM.TINYCOOLTHINGS.HIPERPRECOS.GET_CATEGORIA";
		public static final String GET_CATEGORIES = "COM.TINYCOOLTHINGS.HIPERPRECOS.GET_CATEGORIAS";
		public static final String GET_PRODUTOS = "COM.TINYCOOLTHINGS.HIPERPRECOS.GET_PRODUTOS";
		public static final String GET_PRODUCT = "COM.TINYCOOLTHINGS.HIPERPRECOS.GET_PRODUTO";
		public static final String DISPLAY_CATEGORY = "COM.TINYCOOLTHINGS.HIPERPRECOS.DISPLAY_CATEGORIA";
		public static final String DISPLAY_PRODUCT = "COM.TINYCOOLTHINGS.HIPERPRECOS.DISPLAY_PRODUTO";
		public static final String SEARCH = "COM.TINYCOOLTHINGS.HIPERPRECOS.SEARCH";
		public static final String GET_LATEST_UPDATE = "COM.TINYCOOLTHINGS.HIPERPRECOS.GET_LATEST_UPDATE";
        public static final String SET_NEW_SHOPPING_LIST_TOTAL = "COM.TINYCOOLTHINGS.HIPERPRECOS.SET_NEW_SHOPPING_LIST_TOTAL";
        public static final String SHOPPING_LIST_CHANGED = "COM.TINYCOOLTHINGS.HIPERPRECOS.SHOPPING_LIST_CHANGED";
    }

	public static final class Extras {
		public static final String HYPERS = "COM.TINYCOOLTHINGS.HIPERPRECOS.HYPERS";
		public static final String HYPER = "COM.TINYCOOLTHINGS.HIPERPRECOS.HIPER";
		public static final String CATEGORIES = "COM.TINYCOOLTHINGS.HIPERPRECOS.CATEGORIAS";
		public static final String CATEGORY = "COM.TINYCOOLTHINGS.HIPERPRECOS.CATEGORIA";
		public static final String PRODUCTS = "COM.TINYCOOLTHINGS.HIPERPRECOS.PRODUTOS";
		public static final String PRODUCT = "COM.TINYCOOLTHINGS.HIPERPRECOS.PRODUTO";
		public static final String SEARCH_RESULT = "COM.TINYCOOLTHINGS.HIPERPRECOS.SEARCH";
		public static final String PRODUCT_SORT = "COM.TINYCOOLTHINGS.HIPERPRECOS.PRODUTO_SORT";
		public static final String ORIGIN = "COM.TINYCOOLTHINGS.HIPERPRECOS.ORIGIN";
		public static final String LATEST_UPDATE = "COM.TINYCOOLTHINGS.HIPERPRECOS.LATEST_UPDATE";
		public static final String FILTER = "COM.TINYCOOLTHINGS.HIPERPRECOS.FILTER";
        public static final String SHOPPING_LIST_TOTAL = "COM.TINYCOOLTHINGS.HIPERPRECOS.SHOPPING_LIST_TOTAL";
    }
	public static final class Server {
		public static final class Definitions {
			public static final Double VERSION = 0.7;
			public static final String DOMAIN = "tinycoolthings.com";
			public static final String PATH = "api";
			public static final String API_URL = "http://www."+DOMAIN+"/"+PATH/*+"/"+String.valueOf(VERSION)*/;
			public static final String CATEGORIES_URL = API_URL+"/categorias/";
			public static final String PRODUTOS_URL = API_URL+"/produtos/";
			public static final String HIPERS_URL = API_URL+"/hipers/";
			public static final String SEARCH_URL = "http://www."+DOMAIN+"/search";
			public static final String LATEST_UPDATE_URL = "http://www."+DOMAIN+"/get_latest_update";
		}
		public static final class Parameter {
			public static enum Name {
				HYPER,
				PARENT_CATEGORY,
				CATEGORIA_ID,
				DESCONTO,
				PRODUTO_ID,
				SEARCH_QUERY
			}
		}
	}
	public static final class Sort {
		public static final int NAME_ASCENDING = 0;
		public static final int NAME_DESCENDING = 1;
		public static final int BRAND_ASCENDING = 2;
		public static final int BRAND_DESCENDING = 3;
		public static final int PRICE_ASCENDING = 4;
		public static final int PRICE_DESCENDING = 5;
	}
	public static final class Debug {
		public static enum MsgType {
			ERROR,
			WARNING,
			DEBUG,
			INFO
		}
		public static final String GENERAL_TAG = "HiperPrecos";
		public static final Boolean WRITE_TO_FILE = false;
	}
}
