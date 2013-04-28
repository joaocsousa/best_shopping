package com.tinycoolthings.hiperprecos.utils;

public class Constants {
	public static final class Actions {
		public static final String GET_HIPERS = "COM.TINYCOOLTHINGS.HIPERPRECOS.GET_HIPERS";
		public static final String GET_CATEGORIA = "COM.TINYCOOLTHINGS.HIPERPRECOS.GET_CATEGORIA";
		public static final String GET_CATEGORIAS = "COM.TINYCOOLTHINGS.HIPERPRECOS.GET_CATEGORIAS";
		public static final String GET_PRODUTOS = "COM.TINYCOOLTHINGS.HIPERPRECOS.GET_PRODUTOS";
		public static final String GET_PRODUTO = "COM.TINYCOOLTHINGS.HIPERPRECOS.GET_PRODUTO";
		public static final String DISPLAY_CATEGORIA = "COM.TINYCOOLTHINGS.HIPERPRECOS.DISPLAY_CATEGORIA";
		public static final String DISPLAY_PRODUTO = "COM.TINYCOOLTHINGS.HIPERPRECOS.DISPLAY_PRODUTO";
		public static final String SEARCH = "COM.TINYCOOLTHINGS.HIPERPRECOS.SEARCH";
	}

	public static final class Extras {
		public static final String HIPERS = "COM.TINYCOOLTHINGS.HIPERPRECOS.HIPERS";
		public static final String HIPER = "COM.TINYCOOLTHINGS.HIPERPRECOS.HIPER";
		public static final String CATEGORIAS = "COM.TINYCOOLTHINGS.HIPERPRECOS.CATEGORIAS";
		public static final String CATEGORIA = "COM.TINYCOOLTHINGS.HIPERPRECOS.CATEGORIA";
		public static final String PRODUTOS = "COM.TINYCOOLTHINGS.HIPERPRECOS.PRODUTOS";
		public static final String PRODUTO = "COM.TINYCOOLTHINGS.HIPERPRECOS.PRODUTO";
		public static final String SEARCH_RESULT = "COM.TINYCOOLTHINGS.HIPERPRECOS.SEARCH";
	}
	public static final class Server {
		public static final class Definitions {
			public static final Double VERSION = 0.7;
			public static final String DOMAIN = "tinycoolthings.com";
			public static final String PATH = "api";
			public static final String API_URL = "http://www."+DOMAIN+"/"+PATH+"/"+String.valueOf(VERSION);
			public static final String CATEGORIAS_URL = API_URL+"/categorias/";
			public static final String PRODUTOS_URL = API_URL+"/produtos/";
			public static final String HIPERS_URL = API_URL+"/hipers/";
			public static final String SEARCH_URL = "http://www."+DOMAIN+"/search";
		}
		public static final class Parameter {
			public static enum Name {
				HIPER,
				CATEGORIA_PAI,
				CATEGORIA_ID,
				DESCONTO,
				PRODUTO_ID,
				SEARCH_QUERY
			}
		}
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
