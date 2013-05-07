package com.tinycoolthings.hiperprecos.utils;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Random;

import com.tinycoolthings.hiperprecos.models.Categoria;
import com.tinycoolthings.hiperprecos.models.Produto;
import com.tinycoolthings.hiperprecos.utils.Constants.Sort;

public class Utils {
	
	/**
	 * Converts a cal to "yyyy-MM-dd HH:mm:ss.SSS"
	 * @param cal - Calendar in local TZ
	 * @return String in format "yyyy-MM-dd HH:mm:ss.SSS"
	 */
	public static String convertCalToString(Calendar cal) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
		return(sdf.format(cal.getTime()));
	}
	
	public static Integer getRandomInt() {
		int maxValue = 100000;
		Random random = new Random();
		return random.nextInt(maxValue);
	}
	
	public static Integer getRandomInt(Integer n) {
		Random random = new Random();
		return random.nextInt(n);
	}
	
	public static boolean validSearch(String text) {
		if (text.length()<3) {
			return false;
		}
		return true;
	}
	
	private static class CategoryComparator implements Comparator<Categoria> {
	    @Override
	    public int compare(Categoria o1, Categoria o2) {
	    	Locale pt = new Locale("pt_PT");
	    	Collator ptCollator = Collator.getInstance(pt);
	    	return ptCollator.compare(o1.getNome(), o2.getNome());
	    }
	}
	
	public static void sortCategoriesByName(ArrayList<Categoria> categorias, boolean reverse) {
		if (reverse) {
			Collections.sort(categorias, new CategoryComparator());
			Collections.reverse(categorias);
		} else {
			Collections.sort(categorias, new CategoryComparator()); 
		}
	}
	
	private static class ProductNameComparator implements Comparator<Produto> {
	    @Override
	    public int compare(Produto o1, Produto o2) {
	    	Locale pt = new Locale("pt_PT");
	    	Collator ptCollator = Collator.getInstance(pt);
	    	return ptCollator.compare(o1.getNome(), o2.getNome());
	    }
	}
	
	private static class ProductMarcaComparator implements Comparator<Produto> {
	    @Override
	    public int compare(Produto o1, Produto o2) {
	    	Locale pt = new Locale("pt_PT");
	    	Collator ptCollator = Collator.getInstance(pt);
	    	if (o1.getMarca()==null) {
	    		return 1;
	    	}
	    	if (o2.getMarca()==null) {
	    		return -1;
	    	}
	    	return ptCollator.compare(o1.getMarca(), o2.getMarca());
	    }
	}
	
	private static class ProductPrecoComparator implements Comparator<Produto> {
	    @Override
	    public int compare(Produto o1, Produto o2) {
	    	if (o1.getPreco() < o2.getPreco()) return -1;
	        if (o1.getPreco() > o2.getPreco()) return 1;
	        return 0;
	    }
	}
	
	public static void sortProdutos(ArrayList<Produto> produtos, int sortType) {

		switch(sortType) {
			case Sort.NOME_ASCENDING:
				Collections.sort(produtos, new ProductPrecoComparator());
				Collections.sort(produtos, new ProductNameComparator());
				break;
			case Sort.NOME_DESCENDING:
				Collections.sort(produtos, new ProductPrecoComparator());
				Collections.sort(produtos, new ProductNameComparator());
				Collections.reverse(produtos);
				break;
			case Sort.MARCA_ASCENDING:
				Collections.sort(produtos, new ProductPrecoComparator());
				Collections.sort(produtos, new ProductMarcaComparator());
				break;
			case Sort.MARCA_DESCENDING:
				Collections.sort(produtos, new ProductPrecoComparator());
				Collections.sort(produtos, new ProductMarcaComparator());
				Collections.reverse(produtos);
				break;
			case Sort.PRECO_ASCENDING:
				Collections.sort(produtos, new ProductPrecoComparator());
				break;
			case Sort.PRECO_DESCENDING:
				Collections.sort(produtos, new ProductPrecoComparator());
				Collections.reverse(produtos);
				break;
		}
	}

}
