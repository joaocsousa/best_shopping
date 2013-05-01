package com.tinycoolthings.hiperprecos.search;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tinycoolthings.hiperprecos.HiperPrecos;
import com.tinycoolthings.hiperprecos.models.Categoria;
import com.tinycoolthings.hiperprecos.models.Produto;
import com.tinycoolthings.hiperprecos.product.NoResultsFragment;
import com.tinycoolthings.hiperprecos.utils.Constants;

public class SearchPagerAdapter extends FragmentPagerAdapter {

	public SearchPagerAdapter(FragmentManager fm) {
    	super(fm);
    }
    
	/** This method will be invoked when a page is requested to create */
    @Override
    public Fragment getItem(int selectedPos) {
    	Bundle bundle = new Bundle();
    	if (selectedPos == 0) {
    		// Produtos
    		if (HiperPrecos.getInstance().getLatestProdSearch().size()>0) {
				SearchResultFragment productSearchFrag = new SearchResultFragment();
				bundle.putInt(Constants.Extras.PRODUTO, selectedPos);
				productSearchFrag.setArguments(bundle);
	            return productSearchFrag;
			}
			return new NoResultsFragment();
    	} else if (selectedPos == 1) {
    		// Categorias
    		if (HiperPrecos.getInstance().getLatestCatSearch().size()>0) {
            	CategoryResultsFragment categoryResultsFrag = new CategoryResultsFragment();
				bundle.putInt(Constants.Extras.CATEGORIA, selectedPos);
				categoryResultsFrag.setArguments(bundle);
	            return categoryResultsFrag;
            }
			return new NoResultsFragment();
    	}
    	
    	return null;
    }
 
    @Override
    public int getCount() {
    	return HiperPrecos.getInstance().getNumberOfHipers();
    }

	public void setContent(ArrayList<Integer> categorias, ArrayList<Integer> produtos) {
//		this.allCategorias.clear();
//		this.allProdutos.clear();
//		SparseIntArray mapHiperTab = new SparseIntArray();
//		for (int i = 0; i<HiperPrecos.getInstance().getNumberOfHipers(); i++) {
//			Hiper currHiper = HiperPrecos.getInstance().getHipers().get(i);
//			mapHiperTab.put(currHiper.getId(), i);
//		}
//		for (int i = 0; i < categorias.size(); i++) {
//			Categoria currCat = HiperPrecos.getInstance().getCategoriaById(categorias.get(i));
//			int hiperTab = mapHiperTab.get(currCat.getHiper().getId());
//			this.allCategorias.get(hiperTab).add(categorias.get(i));
//		}
//		for (int i = 0; i < produtos.size(); i++) {
//			Produto currProd = HiperPrecos.getInstance().getProdutoById(produtos.get(i));
//			int hiperTab = mapHiperTab.get(currProd.getHiper().getId());
//			this.allProdutos.get(hiperTab).add(categorias.get(i));
//		}
	}
	
}
