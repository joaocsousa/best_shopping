package com.tinycoolthings.bestshopping.shoppingList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.tinycoolthings.bestshopping.BestShopping;
import com.tinycoolthings.bestshopping.models.Hyper;
import com.tinycoolthings.bestshopping.utils.Constants;

import java.sql.SQLException;

public class ShoppingListFragmentAdapter extends FragmentStatePagerAdapter {

    public ShoppingListFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    /** This method will be invoked when a page is requested to create */
    @Override
    public Fragment getItem(int selectedPos) {
        Hyper currHyper = BestShopping.getInstance().getHypers().get(selectedPos);
        int nrProducts = 0;
        try {
            nrProducts = BestShopping.getInstance().getProductsFromHyperList(currHyper).size();
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
        return (int) BestShopping.getInstance().getNumberOfHypers();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
