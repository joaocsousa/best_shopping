package com.tinycoolthings.hiperprecos.shoppingList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockListFragment;
import com.tinycoolthings.hiperprecos.HiperPrecos;
import com.tinycoolthings.hiperprecos.models.Hyper;
import com.tinycoolthings.hiperprecos.models.Product;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Debug;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShoppingListFragment extends SherlockListFragment {

    private List<Product> products = new ArrayList<Product>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();

        Hyper currHyper = HiperPrecos.getInstance().getHypers().get(args.getInt(Constants.Extras.HYPER));

        // fetch products from hyper
        try {
            products = HiperPrecos.getInstance().getProductsFromHyperList(currHyper);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        double sum = 0.0;
        for (Product currProd : products) {
            sum += (currProd.getPrice() * currProd.getQuantityInList() - currProd.getDiscount() * currProd.getQuantityInList());
        }
        Intent i = new Intent(Constants.Actions.SET_NEW_SHOPPING_LIST_TOTAL);
        i.putExtra(Constants.Extras.HYPER, currHyper.getId());
        i.putExtra(Constants.Extras.SHOPPING_LIST_TOTAL, sum);
        HiperPrecos.getInstance().sendBroadcast(i);

        /** Creating array adapter to set data in listview */
        ShoppingListItem adapter = new ShoppingListItem(getActivity().getBaseContext());
        adapter.setData(products);

        /** Setting the array adapter to the listview */
        setListAdapter(adapter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        Debug.PrintDebug(this, "onResume");
        super.onResume();
    }

}
