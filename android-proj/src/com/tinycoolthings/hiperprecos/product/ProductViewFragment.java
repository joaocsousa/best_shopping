package com.tinycoolthings.hiperprecos.product;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.tinycoolthings.hiperprecos.HiperPrecos;
import com.tinycoolthings.hiperprecos.R;
import com.tinycoolthings.hiperprecos.models.Hyper;
import com.tinycoolthings.hiperprecos.models.Product;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Debug;
import com.tinycoolthings.hiperprecos.utils.ImageStorage;

public class ProductViewFragment extends SherlockFragment {
	
	@Override
	public void onResume() {
		Debug.PrintDebug(this, "onResume");
		super.onResume();
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		Bundle args = getArguments();

		Product product = HiperPrecos.getInstance().getProductById(args.getInt(Constants.Extras.PRODUCT));
		
		View view = inflater.inflate(R.layout.product_view, container, false);
		
		// IMAGE
		ImageView img_prodImg = ((ImageView)view.findViewById(R.id.img_prod_image));
		
		String fileName = ImageStorage.getFileName(product.getUrlImage(), product.getName(), product.getBrand());
		Bitmap bm = ImageStorage.getFileFromStorage(HiperPrecos.getInstance(), fileName);
		if (bm == null) {
			Hyper productHyper = product.getHyper();
			HiperPrecos.getInstance().refreshHyper(productHyper);
			if (productHyper.getName().toLowerCase(Locale.FRENCH).contains("continente")) {
				img_prodImg.setBackgroundResource(R.drawable.continente_not_found);
    		}
		} else {
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN){
				img_prodImg.setBackground(new BitmapDrawable(HiperPrecos.getInstance().getResources(), bm));
			} else{
				img_prodImg.setBackgroundDrawable(new BitmapDrawable(HiperPrecos.getInstance().getResources(), bm));
			}
			
		}
		
		// NAME
		TextView tv_prodName = ((TextView)view.findViewById(R.id.tv_prod_name));
		tv_prodName.setText(product.getName());

		// MARCA
		TextView tv_prod_brand = ((TextView)view.findViewById(R.id.tv_prod_brand));
		String brand = "-";
		if (product.getBrand()!=null && !product.getBrand().equals("null") && !product.getBrand().equals("")) {
			brand = product.getBrand();
		}
		tv_prod_brand.setText(brand);

		// PESO
		TextView tv_prod_weight = ((TextView)view.findViewById(R.id.tv_prod_weight));
		String weight = "-";
		if (product.getWeight()!=null && !product.getWeight().equals("null") && !product.getWeight().equals("")) {
			weight = product.getWeight();
		}
		tv_prod_weight.setText(weight);
	
		// PRECO
		TextView tv_prod_price = ((TextView)view.findViewById(R.id.tv_prod_price));
		tv_prod_price.setText(product.getPrice() + " €");

		// PRECO KG
		TextView tv_prod_price_kg = ((TextView)view.findViewById(R.id.tv_prod_price_kg));
		String priceKg = "-";
		if (product.getPriceKg()!=null) {
			priceKg = String.valueOf(product.getPriceKg());
		}
		tv_prod_price_kg.setText(priceKg + " € / Kg");

		// LAST UPDATE DATE
		Calendar now = Calendar.getInstance();
		
		TextView tv_lastUpdate = ((TextView)view.findViewById(R.id.tv_prod_last_update));
		
		String dayOfLastUpdate = "";
		String formatString = "";
		Calendar latestUpdateDate = Calendar.getInstance();
		latestUpdateDate.setTime(product.getLatestUpdate());
		if(now.get(Calendar.DATE) == latestUpdateDate.get(Calendar.DATE) ) {
			dayOfLastUpdate = getString(R.string.today) + " - ";
			formatString = "HH:mm";
		} else if (now.get(Calendar.DATE) - latestUpdateDate.get(Calendar.DATE) == 1 ){
			dayOfLastUpdate = getString(R.string.yesterday) + " - ";
			formatString = "HH:mm";
		} else {
			formatString = "dd-MM-yyyy HH:mm";
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat(formatString, Locale.getDefault());
		
		String lastUpdateDate = sdf.format(product.getLatestUpdate().getTime());
		
		tv_lastUpdate.setText(tv_lastUpdate.getText() + ": " + dayOfLastUpdate + lastUpdateDate);
		
		return view;
		
    }
	
}
