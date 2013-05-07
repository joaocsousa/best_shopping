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
import com.tinycoolthings.hiperprecos.models.Produto;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Debug;
import com.tinycoolthings.hiperprecos.utils.Storage;

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

		Produto produto = HiperPrecos.getInstance().getProdutoById(args.getInt(Constants.Extras.PRODUTO));
		
		View view = inflater.inflate(R.layout.product_view, container, false);
		
		// IMAGE
		ImageView img_prodImg = ((ImageView)view.findViewById(R.id.img_prod_image));
		
		String fileName = Storage.getFileName(produto.getUrlImagem(), produto.getNome(), produto.getMarca());
		Bitmap bm = Storage.getFileFromStorage(HiperPrecos.getInstance(), fileName);
		if (bm == null) {
			if (produto.getHiper().getNome().toLowerCase().contains("continente")) {
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
		tv_prodName.setText(produto.getNome());

		// MARCA
		TextView tv_prod_marca = ((TextView)view.findViewById(R.id.tv_prod_marca));
		String marca = "-";
		if (produto.getMarca()!=null) {
			Debug.PrintError(this, "->"+produto.getMarca());
			marca = produto.getMarca();
		} else {

			Debug.PrintError(this, ":>"+produto.getMarca());
		}
		tv_prod_marca.setText(marca);

		// PESO
		TextView tv_prod_peso = ((TextView)view.findViewById(R.id.tv_prod_peso));
		tv_prod_peso.setText(produto.getPeso());
	
		// PRECO
		TextView tv_prod_preco = ((TextView)view.findViewById(R.id.tv_prod_preco));
		tv_prod_preco.setText(produto.getPreco() + " €");

		// PRECO KG
		TextView tv_prod_preco_kg = ((TextView)view.findViewById(R.id.tv_prod_preco_kg));
		String precoKg = "-";
		if (produto.getPrecoKg()!=null) {
			precoKg = String.valueOf(produto.getPrecoKg());
		}
		tv_prod_preco_kg.setText(precoKg + " € / Kg");

		// LAST UPDATE DATE
		Calendar now = Calendar.getInstance();
		
		TextView tv_lastUpdate = ((TextView)view.findViewById(R.id.tv_prod_last_update));
		
		String dayOfLastUpdate = "";
		String formatString = "";
		if(now.get(Calendar.DATE) == produto.getLastUpdate().get(Calendar.DATE) ) {
			dayOfLastUpdate = getString(R.string.hoje) + " - ";
			formatString = "HH:mm";
		} else if (now.get(Calendar.DATE) - produto.getLastUpdate().get(Calendar.DATE) == 1 ){
			dayOfLastUpdate = getString(R.string.ontem) + " - ";
			formatString = "HH:mm";
		} else {
			formatString = "dd-MM-yyyy HH:mm";
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat(formatString, Locale.getDefault());
		
		String lastUpdateDate = sdf.format(produto.getLastUpdate().getTime());
		
		tv_lastUpdate.setText(tv_lastUpdate.getText() + ": " + dayOfLastUpdate + lastUpdateDate);
		
		return view;
		
    }
	
}
