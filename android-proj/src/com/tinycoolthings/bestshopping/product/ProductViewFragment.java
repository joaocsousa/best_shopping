package com.tinycoolthings.bestshopping.product;

import java.text.DecimalFormat;
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
import com.tinycoolthings.bestshopping.BestShopping;
import com.tinycoolthings.bestshopping.R;
import com.tinycoolthings.bestshopping.models.Hyper;
import com.tinycoolthings.bestshopping.models.Product;
import com.tinycoolthings.bestshopping.utils.Constants;
import com.tinycoolthings.bestshopping.utils.Debug;
import com.tinycoolthings.bestshopping.utils.ImageStorage;
import com.tinycoolthings.bestshopping.utils.Utils;

public class ProductViewFragment extends SherlockFragment {

	@Override
	public void onResume() {
		Debug.PrintDebug(this, "onResume");
		super.onResume();
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Bundle args = getArguments();

		Product product = BestShopping.getInstance().getProductById(
				args.getInt(Constants.Extras.PRODUCT));

		View view = inflater.inflate(R.layout.product_view, container, false);

		// IMAGE
		ImageView img_prodImg = ((ImageView) view
				.findViewById(R.id.img_prod_image));

		String fileName = ImageStorage.getFileName(product.getUrlImage(),
				product.getName(), product.getBrand());
		Bitmap bm = ImageStorage.getFileFromStorage(BestShopping.getInstance(),
				fileName);
		if (bm == null) {
			Hyper productHyper = product.getHyper();
			BestShopping.getInstance().refreshHyper(productHyper);
			if (productHyper.getName().toLowerCase(Locale.FRENCH)
					.contains("continente")) {
				img_prodImg
						.setBackgroundResource(R.drawable.continente_not_found);
			}
		} else {
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
				img_prodImg.setBackground(new BitmapDrawable(BestShopping
						.getInstance().getResources(), bm));
			} else {
				img_prodImg.setBackgroundDrawable(new BitmapDrawable(
						BestShopping.getInstance().getResources(), bm));
			}

		}

		// NAME
		TextView tv_prodName = ((TextView) view.findViewById(R.id.tv_prod_name));
		tv_prodName.setText(product.getName());

		// BRAND
		TextView tv_prod_brand = ((TextView) view
				.findViewById(R.id.tv_prod_brand));
		String brand = "-";
		if (product.getBrand() != null && !product.getBrand().equals("null")
				&& !product.getBrand().equals("")) {
			brand = product.getBrand();
		}
		tv_prod_brand.setText(brand);

		// WEIGHT
		TextView tv_prod_weight = ((TextView) view
				.findViewById(R.id.tv_prod_weight));
		String weight = "-";
		if (product.getWeight() != null && !product.getWeight().equals("null")
				&& !product.getWeight().equals("")) {
			weight = product.getWeight();
		}
		tv_prod_weight.setText(weight);

		// PRICE
		TextView tv_prod_price = ((TextView) view
				.findViewById(R.id.tv_prod_price));
		DecimalFormat formatter = new DecimalFormat("#.##");
		tv_prod_price.setText(formatter.format(product.getPrice()) + "Û");

		// PRICE KG
		TextView tv_prod_price_kg = ((TextView) view
				.findViewById(R.id.tv_prod_price_kg));
		String priceKg = "-";
		if (product.getPriceKg() != null) {
			priceKg = formatter.format(product.getPriceKg());
		}
		tv_prod_price_kg.setText(priceKg + "Û / Kg");

		// LAST UPDATE DATE

		TextView tv_lastUpdate = ((TextView) view
				.findViewById(R.id.tv_prod_last_update));

		String dayOfLastUpdate = "";

		tv_lastUpdate.setText(tv_lastUpdate.getText() + ": " + dayOfLastUpdate
				+ Utils.dateToStr(product.getLatestUpdate()));

		return view;

	}

}
