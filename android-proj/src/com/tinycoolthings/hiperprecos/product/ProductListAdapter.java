package com.tinycoolthings.hiperprecos.product;

import java.util.List;
import java.util.Locale;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.tinycoolthings.hiperprecos.HiperPrecos;
import com.tinycoolthings.hiperprecos.R;
import com.tinycoolthings.hiperprecos.models.Product;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Debug;
import com.tinycoolthings.hiperprecos.utils.ImageStorage;

@SuppressLint("NewApi")
public class ProductListAdapter extends ArrayAdapter<Product> {
	
	private final LayoutInflater mInflater;

	static class ViewHolder {
		public TextView txtNome;
		public TextView txtMarca;
		public TextView txtPreco;
		public TextView txtPeso;
		public ImageView img;
		public Integer position;
	}
	
	public ProductListAdapter(Context context) {
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
			view = mInflater.inflate(R.layout.product_list_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.txtNome = (TextView) view.findViewById(R.id.tv_item_prod_nome);
			viewHolder.txtMarca = (TextView) view.findViewById(R.id.tv_item_prod_marca);
			viewHolder.txtPreco = (TextView) view.findViewById(R.id.tv_item_prod_preco);
			viewHolder.txtPeso = (TextView) view.findViewById(R.id.tv_item_prod_peso);
			viewHolder.img = (ImageView) view.findViewById(R.id.iv_item_prod_img);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		Product item = getItem(position);
		viewHolder.txtNome.setText(item.getName());
		String marca = "-";
		if (item.getBrand()!=null && !item.getBrand().equals("")) {
			marca = item.getBrand();
		}
		viewHolder.txtMarca.setText(marca);
		viewHolder.txtPreco.setText(String.valueOf(item.getPrice()) + " €");
		String peso = "-";
		if (item.getWeight()!=null && !item.getWeight().equals("")) {
			peso = item.getWeight();
		}
		Debug.PrintError(this, "|"+peso+"|");
		viewHolder.txtPeso.setText(peso);
		viewHolder.position = position;
		String fileName = ImageStorage.getFileNameCompressed(ImageStorage.getFileName(item.getUrlImage(), item.getName(), item.getBrand()));
		if (android.os.Build.VERSION.SDK_INT > 11) {
			new ThumbnailTask(position, viewHolder, fileName, item.getHyper().getName()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void)null);
		} else {
			new ThumbnailTask(position, viewHolder, fileName, item.getHyper().getName()).execute();
		}
	
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Product selectedProd = getItem(position);
				int selectedProdID = selectedProd.getId();
				Debug.PrintInfo(ProductListAdapter.this, "Selected produto with id " + selectedProdID);
//				boolean productHasLoaded = HiperPrecos.getInstance().productHasLoaded(selectedProd);
//				if (productHasLoaded) {
					Intent intent = new Intent();
					intent.setAction(Constants.Actions.DISPLAY_PRODUCT);
					intent.putExtra(Constants.Extras.PRODUCT, selectedProdID);
					HiperPrecos.getInstance().sendBroadcast(intent);
//				} else {
//					CallWebServiceTask getProduto = new CallWebServiceTask(Constants.Actions.GET_PRODUCT, true);
//					getProduto.addParameter(Name.PRODUTO_ID, selectedProdID);
//					getProduto.execute();
//				}
			}
		});
	
		return view;
	}

	private class ThumbnailTask extends AsyncTask <Void, Void, Bitmap> {
	    private int mPosition;
	    private ViewHolder mHolder;
	    private String mFileName;
	    private String mHiper;
	
	    public ThumbnailTask(int position, ViewHolder holder, String fileName, String hiper) {
	        mPosition = position;
	        mHolder = holder;
	        mFileName = fileName;
	        mHiper = hiper;
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
	        		if (mHiper.toLowerCase(Locale.FRENCH).contains("continente")) {
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