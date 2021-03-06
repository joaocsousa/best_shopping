package com.tinycoolthings.bestshopping.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
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
import java.util.ArrayList;
import java.util.Locale;

public class ProductSearchListAdapter extends BaseExpandableListAdapter {

	private final ArrayList<ArrayList<Product>> products = new ArrayList<ArrayList<Product>>();
	private final LayoutInflater mInflater;
	
	private static class GroupViewHolder {
		public TextView txtName;
	}
	
	private static class ChildViewHolder {
		public TextView txtName;
		public TextView txtBrand;
		public TextView txtPrice;
		public TextView txtWeight;
		public ImageView img;
		public int position;
	}
	
	public ProductSearchListAdapter(Context context, ArrayList<Product> produtos) {

        this.products.clear();
		
		SparseIntArray mapHiperGroup = new SparseIntArray();
		for (int i = 0; i<BestShopping.getInstance().getNumberOfHypers(); i++) {
			Hyper currHiper = BestShopping.getInstance().getHypers().get(i);
			mapHiperGroup.put(currHiper.getId(), i);
		}
		for (int i = 0; i < BestShopping.getInstance().getNumberOfHypers(); i++) {
			Hyper currentHiper = BestShopping.getInstance().getHypers().get(i);
			ArrayList<Product> currProdsHiper = new ArrayList<Product>();
            for (Product currProd : produtos) {
                Integer prodHiper = currProd.getHyper().getId();
                if (prodHiper.equals(currentHiper.getId())) {
                    currProdsHiper.add(currProd);
                }
            }
			this.products.add(currProdsHiper);
		}
		
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return this.products.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@SuppressLint("NewApi")
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

		View view = convertView;
		
		ChildViewHolder viewHolder;

		if (view == null) {
			view = mInflater.inflate(R.layout.product_list_item, parent, false);
			viewHolder = new ChildViewHolder();
			viewHolder.txtName = (TextView) view.findViewById(R.id.tv_item_prod_name);
			viewHolder.txtBrand = (TextView) view.findViewById(R.id.tv_item_prod_brand);
			viewHolder.txtPrice = (TextView) view.findViewById(R.id.tv_item_prod_price);
			viewHolder.txtWeight = (TextView) view.findViewById(R.id.tv_item_prod_weight);
			viewHolder.img = (ImageView) view.findViewById(R.id.iv_item_prod_img);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ChildViewHolder) view.getTag();
		}
		
		final Product item = products.get(groupPosition).get(childPosition);
		viewHolder.txtName.setText(item.getName());
		String marca = "-";
		if (item.getBrand()!=null ) {
			marca = item.getBrand();
		}
		viewHolder.txtBrand.setText(marca);
        DecimalFormat formatter = new DecimalFormat("#.##");
		viewHolder.txtPrice.setText(formatter.format(item.getPrice()) + "€");
		viewHolder.txtWeight.setText(item.getWeight());
		viewHolder.position = childPosition;
		String fileName = ImageStorage.getFileNameCompressed(ImageStorage.getFileName(item.getUrlImage(), item.getName(), item.getBrand()));
		Hyper productHyper = item.getHyper();
		BestShopping.getInstance().refreshHyper(productHyper);
		if (android.os.Build.VERSION.SDK_INT > 11) {
			new ThumbnailTask(childPosition, viewHolder, fileName, productHyper.getName()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void)null);
		} else {
			new ThumbnailTask(childPosition, viewHolder, fileName, productHyper.getName()).execute();
		}
	
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int selectedProdID = item.getId();
				Debug.PrintInfo(ProductSearchListAdapter.this, "Selected product with id " + selectedProdID);
				Intent intent = new Intent();
				intent.setAction(Constants.Actions.DISPLAY_PRODUCT);
				intent.putExtra(Constants.Extras.PRODUCT, selectedProdID);
				BestShopping.getInstance().sendBroadcast(intent);
			}
		});
	
		return view;
	
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this.products.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this.products.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this.products.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		
		View view = convertView;
		
		GroupViewHolder viewHolder;

		if (view == null) {
			view = mInflater.inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
			viewHolder = new GroupViewHolder();
			viewHolder.txtName = (TextView) view.findViewById(android.R.id.text1);
			view.setTag(viewHolder);
		} else {
			viewHolder = (GroupViewHolder) view.getTag();
		}
	         
	    Hyper hiper = BestShopping.getInstance().getHypers().get(groupPosition);
	    
	    viewHolder.txtName.setText(hiper.getName());
	     
	    return view;
	    
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	private class ThumbnailTask extends AsyncTask <Void, Void, Bitmap> {
	    private final int mPosition;
	    private final ChildViewHolder mHolder;
	    private final String mFileName;
	    private final String mHiper;
	
	    public ThumbnailTask(int position, ChildViewHolder holder, String fileName, String hiper) {
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
		@SuppressLint("NewApi")
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

