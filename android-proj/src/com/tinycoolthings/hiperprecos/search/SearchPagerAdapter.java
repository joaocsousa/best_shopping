package com.tinycoolthings.hiperprecos.search;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseIntArray;

import com.tinycoolthings.hiperprecos.HiperPrecos;
import com.tinycoolthings.hiperprecos.models.Categoria;
import com.tinycoolthings.hiperprecos.models.Hiper;
import com.tinycoolthings.hiperprecos.models.Produto;
import com.tinycoolthings.hiperprecos.product.NoResultsFragment;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Debug;

public class SearchPagerAdapter extends FragmentPagerAdapter {

	public static final int TYPE_PRODUTOS = 0;
	public static final int TYPE_CATEGORIAS = 1;
	
	private int currContentType = TYPE_PRODUTOS;

	private ArrayList<ArrayList<Integer>> allCategorias = new ArrayList<ArrayList<Integer>>();
	private ArrayList<ArrayList<Integer>> allProdutos = new ArrayList<ArrayList<Integer>>();
	
    public SearchPagerAdapter(FragmentManager fm) {
    	super(fm);
    }
    
    public void setContentType(int contentType) {
		if (currContentType!=contentType) {
			currContentType = contentType;
			notifyDataSetChanged();
		}
    }
    
	/** This method will be invoked when a page is requested to create */
    @Override
    public Fragment getItem(int selectedPos) {
    	Debug.PrintInfo(this, "Loading results for hiper " + selectedPos);
    	Bundle bundle = new Bundle();
    	switch (currContentType) {
			case TYPE_PRODUTOS:
				Debug.PrintDebug(this, "Displaying produtos");
				if (this.allProdutos.size()>0) {
					ProductResultsFragment productResultsFrag = new ProductResultsFragment();
					bundle.putIntegerArrayList(Constants.Extras.PRODUTOS, this.allProdutos.get(selectedPos));
		            productResultsFrag.setArguments(bundle);
		            return productResultsFrag;
				}
				return new NoResultsFragment();
			case TYPE_CATEGORIAS:
				Debug.PrintDebug(this, "Displaying categorias");
	            if (this.allCategorias.size()>0) {
	            	CategoryResultsFragment categoryResultsFrag = new CategoryResultsFragment();
		            bundle.putIntegerArrayList(Constants.Extras.CATEGORIAS, this.allCategorias.get(selectedPos));
		            categoryResultsFrag.setArguments(bundle);
		            return categoryResultsFrag;
	            }
				return new NoResultsFragment();
			default:
				break;
		}
    	
    	return null;
    }
 
    /** Returns the number of pages */
    @Override
    public int getCount() {
    	return HiperPrecos.getInstance().getNumberOfHipers();
    }
    
    @Override
    public CharSequence getPageTitle(int position) {
        return HiperPrecos.getInstance().getHipers().get(position).getNome();
    }

	public void setContent(ArrayList<Integer> categorias, ArrayList<Integer> produtos) {
		this.allCategorias.clear();
		this.allProdutos.clear();
		SparseIntArray mapHiperTab = new SparseIntArray();
		for (int i = 0; i<HiperPrecos.getInstance().getNumberOfHipers(); i++) {
			Hiper currHiper = HiperPrecos.getInstance().getHipers().get(i);
			mapHiperTab.put(currHiper.getId(), i);
		}
		for (int i = 0; i < categorias.size(); i++) {
			Categoria currCat = HiperPrecos.getInstance().getCategoriaById(categorias.get(i));
			int hiperTab = mapHiperTab.get(currCat.getHiper().getId());
			this.allCategorias.get(hiperTab).add(categorias.get(i));
		}
		for (int i = 0; i < produtos.size(); i++) {
			Produto currProd = HiperPrecos.getInstance().getProdutoById(produtos.get(i));
			int hiperTab = mapHiperTab.get(currProd.getHiper().getId());
			this.allProdutos.get(hiperTab).add(categorias.get(i));
		}
	}
	
}
