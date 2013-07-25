package com.tinycoolthings.bestshopping.product;

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

import com.tinycoolthings.bestshopping.BestShopping;
import com.tinycoolthings.bestshopping.models.Hyper;
import com.tinycoolthings.bestshopping.models.Product;
import com.tinycoolthings.bestshopping.utils.Constants;
import com.tinycoolthings.bestshopping.utils.Debug;
import com.tinycoolthings.bestshopping.utils.ImageStorage;
import com.tinycoolthings.bestshopping.R;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

@SuppressLint("NewApi")
public class ProductListAdapter extends ArrayAdapter<Product> {
	
	private final LayoutInflater mInflater;

	static class ViewHolder {
		public TextView txtName;
		public TextView txtBrand;
		public TextView txtPrice;
		public TextView txtWeight;
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
			viewHolder.txtName = (TextView) view.findViewById(R.id.tv_item_prod_name);
			viewHolder.txtBrand = (TextView) view.findViewById(R.id.tv_item_prod_brand);
			viewHolder.txtPrice = (TextView) view.findViewById(R.id.tv_item_prod_price);
			viewHolder.txtWeight = (TextView) view.findViewById(R.id.tv_item_prod_weight);
			viewHolder.img = (ImageView) view.findViewById(R.id.iv_item_prod_img);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		Product item = getItem(position);
		viewHolder.txtName.setText(item.getName());
		String brand = "-";
		if (item.getBrand()!=null && !item.getBrand().equals("") && !item.getBrand().equals("null")) {
			brand = item.getBrand();
		}
		viewHolder.txtBrand.setText(brand);
        DecimalFormat formatter = new DecimalFormat("#.##");
		viewHolder.txtPrice.setText(formatter.format(item.getPrice()) + "Û");
		String peso = "-";
		if (item.getWeight()!=null && !item.getWeight().equals("") && !item.getWeight().equals("null")) {
			peso = item.getWeight();
		}
		viewHolder.txtWeight.setText(peso);
		viewHolder.position = position;
		String fileName = ImageStorage.getFileNameCompressed(ImageStorage.getFileName(item.getUrlImage(), item.getName(), item.getBrand()));
		Hyper productHyper = item.getHyper();
		BestShopping.getInstance().refreshHyper(productHyper);
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
				Debug.PrintInfo(ProductListAdapter.this, "Selected produto with id " + selectedProdID);
				Intent intent = new Intent();
				intent.setAction(Constants.Actions.DISPLAY_PRODUCT);
				intent.putExtra(Constants.Extras.PRODUCT, selectedProdID);
				BestShopping.getInstance().sendBroadcast(intent);
			}
		});
	
		return view;
	}

	private class ThumbnailTask extends AsyncTask <Void, Void, Bitmap> {
	    private final int mPosition;
	    private final ViewHolder mHolder;
	    private final String mFileName;
	    private final String mHiper;
	
	    public ThumbnailTask(int position, ViewHolder holder, String fileName, String hiper) {
	        mPosition = position;
	        mHolder = holder;
	        mFileName = fileName;
	        mHiper = hiper;
	    }
	
	    @Override
	    protected Bitmap doInBackground(Void... arg0) {
	        return ImageStorage.getFileFromStorage(BestShopping.getInstance(), mFileName);
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
	        			mHolder.img.setBackground(new BitmapDrawable(BestShopping.getInstance().getResources(), bitmap));
	    			} else{
	    				mHolder.img.setBackgroundDrawable(new BitmapDrawable(BestShopping.getInstance().getResources(), bitmap));
	    			}
	        	}
	        }
	    }
	}
	
} 