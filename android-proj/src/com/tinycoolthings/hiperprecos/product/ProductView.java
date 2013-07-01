package com.tinycoolthings.hiperprecos.product;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.michaelnovakjr.numberpicker.NumberPickerDialog;
import com.tinycoolthings.hiperprecos.HiperPrecos;
import com.tinycoolthings.hiperprecos.R;
import com.tinycoolthings.hiperprecos.models.Product;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Debug;

public class ProductView extends SherlockFragmentActivity {

    Product currProd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            ProductViewFragment productViewFrag = new ProductViewFragment();
            currProd = HiperPrecos.getInstance().getProductById(getIntent().getExtras().getInt(Constants.Extras.PRODUCT));
            HiperPrecos.getInstance().refreshProduct(currProd);
            productViewFrag.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().replace(android.R.id.content, productViewFrag).commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.product_view_menu, menu);
        if (currProd.isInList()) {
            menu.findItem(R.id.menu_icon_save_in_list).setVisible(false);
        } else {
            menu.findItem(R.id.menu_icon_remove_from_list).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_icon_browser:
                String prodUrl = currProd.getUlrPage();
                if (!prodUrl.startsWith("http://") && !prodUrl.startsWith("https://")) {
                    prodUrl = "http://" + prodUrl;
                }
                if (prodUrl.contains("continente")) {
                    prodUrl = prodUrl.substring(0, prodUrl.indexOf("&"));
                }
                Debug.PrintInfo(this, "Opening: " + Uri.parse(prodUrl));
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(prodUrl));
                startActivity(browserIntent);
                break;
            case R.id.menu_icon_save_in_list:
                HiperPrecos.getInstance().updateProduct(currProd);
                HiperPrecos.getInstance().refreshProduct(currProd);
                final NumberPickerDialog dialog = new NumberPickerDialog(this, 0, 1);
                dialog.getNumberPicker().setRange(1,200);
                dialog.setCancelable(true);
                dialog.setTitle(getResources().getString(R.string.quantity));
                dialog.setMessage(getResources().getString(R.string.choose_quantity));
                dialog.setButton(NumberPickerDialog.BUTTON_POSITIVE, getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        currProd.setIsInList(true);
                        currProd.setQuantityInList(dialog.getNumberPicker().getCurrent());
                        HiperPrecos.getInstance().updateProduct(currProd);
                        HiperPrecos.getInstance().refreshProduct(currProd);
                        Toast.makeText(ProductView.this, getResources().getString(R.string.product_added), Toast.LENGTH_SHORT).show();
                        invalidateOptionsMenu();
                    }
                });
                dialog.setButton(NumberPickerDialog.BUTTON_NEGATIVE, getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
            case R.id.menu_icon_remove_from_list:
                currProd.setIsInList(false);
                HiperPrecos.getInstance().updateProduct(currProd);
                HiperPrecos.getInstance().refreshProduct(currProd);
                Toast.makeText(this, getResources().getString(R.string.product_removed), Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}

