package com.tinycoolthings.hiperprecos.shoppingList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.michaelnovakjr.numberpicker.NumberPickerDialog;
import com.tinycoolthings.hiperprecos.HiperPrecos;
import com.tinycoolthings.hiperprecos.R;
import com.tinycoolthings.hiperprecos.models.Hyper;
import com.tinycoolthings.hiperprecos.models.Product;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Debug;
import com.tinycoolthings.hiperprecos.utils.ImageStorage;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

@SuppressLint("NewApi")
public class ShoppingListItem extends ArrayAdapter<Product> {

    private final LayoutInflater mInflater;

    static class ViewHolder {
        public TextView txtName;
        public TextView txtBrand;
        public TextView txtPrice;
        public TextView txtWeight;
        public TextView txtQuantity;
        public ImageView img;
        public Integer position;
        public Button changeQuantityBtn;
    }

    public ShoppingListItem(Context context) {
        super(context, android.R.layout.simple_list_item_1);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<Product> data) {
        clear();
        if (data != null) {
            for (Product appEntry : data) {
                add(appEntry);
            }
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;

        ViewHolder viewHolder;

        if (view == null) {
            view = mInflater.inflate(R.layout.shopping_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.txtName = (TextView) view.findViewById(R.id.tv_item_prod_nome);
            viewHolder.txtBrand = (TextView) view.findViewById(R.id.tv_item_prod_marca);
            viewHolder.txtPrice = (TextView) view.findViewById(R.id.tv_item_prod_preco);
            viewHolder.txtWeight = (TextView) view.findViewById(R.id.tv_item_prod_peso);
            viewHolder.txtQuantity = (TextView) view.findViewById(R.id.tv_quantity);
            viewHolder.img = (ImageView) view.findViewById(R.id.iv_item_prod_img);
            viewHolder.changeQuantityBtn = (Button) view.findViewById(R.id.btn_change_quantity);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final Product item = getItem(position);
        viewHolder.txtName.setText(item.getName());
        String brand = "-";
        if (item.getBrand()!=null && !item.getBrand().equals("") && !item.getBrand().equals("null")) {
            brand = item.getBrand();
        }
        viewHolder.txtBrand.setText(brand);
        DecimalFormat formatter = new DecimalFormat("#.##");
        viewHolder.txtPrice.setText(formatter.format(item.getPrice()) + " €");
        String weight = "-";
        if (item.getWeight()!=null && !item.getWeight().equals("") && !item.getWeight().equals("null")) {
            weight = item.getWeight();
        }
        viewHolder.txtWeight.setText(weight);
        viewHolder.txtQuantity.setText(""+item.getQuantityInList());
        viewHolder.changeQuantityBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final Context context = view.getContext();
                NumberPickerDialog dialog = new NumberPickerDialog(context, 0, item.getQuantityInList());
                dialog.getNumberPicker().setRange(1,200);
                dialog.setCancelable(true);
                dialog.setTitle(context.getResources().getString(R.string.quantity));
                dialog.setMessage(context.getResources().getString(R.string.choose_quantity));
                dialog.setOnNumberSetListener(new NumberPickerDialog.OnNumberSetListener() {
                    @Override
                    public void onNumberSet(int selectedNumber) {
                        item.setQuantityInList(selectedNumber);
                        HiperPrecos.getInstance().updateProduct(item);
                        HiperPrecos.getInstance().refreshProduct(item);
                        Intent i = new Intent(Constants.Actions.SHOPPING_LIST_CHANGED);
                        HiperPrecos.getInstance().sendBroadcast(i);
                    }
                });
                dialog.show();
            }
        });
        viewHolder.position = position;
        String fileName = ImageStorage.getFileNameCompressed(ImageStorage.getFileName(item.getUrlImage(), item.getName(), item.getBrand()));
        Hyper productHyper = item.getHyper();
        HiperPrecos.getInstance().refreshHyper(productHyper);
        if (android.os.Build.VERSION.SDK_INT > 11) {
            new ThumbnailTask(position, viewHolder, fileName, productHyper.getName()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void)null);
        } else {
            new ThumbnailTask(position, viewHolder, fileName, productHyper.getName()).execute();
        }

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Product selectedProd = getItem(position);
                int selectedProdID = selectedProd.getId();
                Debug.PrintInfo(ShoppingListItem.this, "Selected produto with id " + selectedProdID);
                Intent intent = new Intent();
                intent.setAction(Constants.Actions.DISPLAY_PRODUCT);
                intent.putExtra(Constants.Extras.PRODUCT, selectedProdID);
                HiperPrecos.getInstance().sendBroadcast(intent);
            }
        });

        return view;
    }

    private class ThumbnailTask extends AsyncTask <Void, Void, Bitmap> {
        private final int mPosition;
        private final ViewHolder mHolder;
        private final String mFileName;
        private final String mHyper;

        public ThumbnailTask(int position, ViewHolder holder, String fileName, String hiper) {
            mPosition = position;
            mHolder = holder;
            mFileName = fileName;
            mHyper = hiper;
        }

        @Override
        protected Bitmap doInBackground(Void... arg0) {
            return ImageStorage.getFileFromStorage(HiperPrecos.getInstance(), mFileName);
        }

        @SuppressWarnings("deprecation")
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (mHolder.position == mPosition) {
                if (bitmap == null) {
                    if (mHyper.toLowerCase(Locale.FRENCH).contains("continente")) {
                        mHolder.img.setBackgroundResource(R.drawable.continente_not_found);
                    }
                } else {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN){
                        mHolder.img.setBackground(new BitmapDrawable(HiperPrecos.getInstance().getResources(), bitmap));
                    } else{
                        mHolder.img.setBackgroundDrawable(new BitmapDrawable(HiperPrecos.getInstance().getResources(), bitmap));
                    }
                }
            }
        }
    }

}