package com.tinycoolthings.hiperprecos.shoppingList;

/**
 * Created by joaosousa on 6/26/13.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.tinycoolthings.hiperprecos.HiperPrecos;
import com.tinycoolthings.hiperprecos.models.Hyper;
import com.tinycoolthings.hiperprecos.utils.Constants;

import java.sql.SQLException;

public class ShoppingListFragmentAdapter extends FragmentStatePagerAdapter {

    public ShoppingListFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    /** This method will be invoked when a page is requested to create */
    @Override
    public Fragment getItem(int selectedPos) {
        Hyper currHyper = HiperPrecos.getInstance().getHypers().get(selectedPos);
        int nrProducts = 0;
        try {
            nrProducts = HiperPrecos.getInstance().getProductsFromHyperList(currHyper).size();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (nrProducts == 0) {
            return new NoProductsFragment();
        }
        ShoppingListFragment shoppingListFragment = new ShoppingListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.Extras.HYPER, selectedPos);
        shoppingListFragment.setArguments(bundle);
        return shoppingListFragment;
    }

    /** Returns the number of pages */
    @Override
    public int getCount() {
        return (int) HiperPrecos.getInstance().getNumberOfHypers();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
