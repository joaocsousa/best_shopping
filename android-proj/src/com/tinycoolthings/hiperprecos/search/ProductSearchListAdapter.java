package com.tinycoolthings.hiperprecos.search;

import java.util.ArrayList;

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

import com.tinycoolthings.hiperprecos.HiperPrecos;
import com.tinycoolthings.hiperprecos.R;
import com.tinycoolthings.hiperprecos.models.Hyper;
import com.tinycoolthings.hiperprecos.models.Product;
import com.tinycoolthings.hiperprecos.serverComm.CallWebServiceTask;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Constants.Server.Parameter.Name;
import com.tinycoolthings.hiperprecos.utils.Debug;
import com.tinycoolthings.hiperprecos.utils.ImageStorage;

public class ProductSearchListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private ArrayList<ArrayList<Product>> produtos = new ArrayList<ArrayList<Product>>();
	private final LayoutInflater mInflater;
	
	private static class GroupViewHolder {
		public TextView txtNome;
	}
	
	private static class ChildViewHolder {
		public TextView txtNome;
		public TextView txtMarca;
		public TextView txtPreco;
		public TextView txtPeso;
		public ImageView img;
		public int position;
	}
	
	public ProductSearchListAdapter(Context context, ArrayList<Product> produtos) {
		this.context = context;
		
		this.produtos.clear();
		
		SparseIntArray mapHiperGroup = new SparseIntArray();
		for (int i = 0; i<HiperPrecos.getInstance().getNumberOfHypers(); i++) {
			Hyper currHiper = HiperPrecos.getInstance().getHypers().get(i);
			mapHiperGroup.put(currHiper.getId(), i);
		}
		for (int i = 0; i < HiperPrecos.getInstance().getNumberOfHypers(); i++) {
			Hyper currentHiper = HiperPrecos.getInstance().getHypers().get(i);
			ArrayList<Product> currProdsHiper = new ArrayList<Product>();
			for (int j = 0; j < produtos.size(); j++) {
				Product currProd = produtos.get(j);
				Integer prodHiper = currProd.getHyper().getId();
				if (prodHiper.equals(currentHiper.getId())) {
					currProdsHiper.add(currProd);
				}
			}
			this.produtos.add(currProdsHiper);
		}
		
		this.mInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return this.produtos.get(groupPosition).get(childPosition);
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
			viewHolder.txtNome = (TextView) view.findViewById(R.id.tv_item_prod_nome);
			viewHolder.txtMarca = (TextView) view.findViewById(R.id.tv_item_prod_marca);
			viewHolder.txtPreco = (TextView) view.findViewById(R.id.tv_item_prod_preco);
			viewHolder.txtPeso = (TextView) view.findViewById(R.id.tv_item_prod_peso);
			viewHolder.img = (ImageView) view.findViewById(R.id.iv_item_prod_img);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ChildViewHolder) view.getTag();
		}
		
		final Product item = produtos.get(groupPosition).get(childPosition);
		viewHolder.txtNome.setText(item.getName());
		String marca = "-";
		if (item.getBrand()!=null ) {
			marca = item.getBrand();
		}
		viewHolder.txtMarca.setText(marca);
		viewHolder.txtPreco.setText(String.valueOf(item.getPrice()) + "€");
		viewHolder.txtPeso.setText(item.getWeight());
		viewHolder.position = childPosition;
		String fileName = ImageStorage.getFileNameCompressed(ImageStorage.getFileName(item.getUrlImage(), item.getName(), item.getBrand()));
		if (android.os.Build.VERSION.SDK_INT > 11) {
			new ThumbnailTask(childPosition, viewHolder, fileName, item.getHyper().getName()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void)null);
		} else {
			new ThumbnailTask(childPosition, viewHolder, fileName, item.getHyper().getName()).execute();
		}
	
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int selectedProdID = item.getId();
				Debug.PrintInfo(ProductSearchListAdapter.this, "Selected product with id " + selectedProdID);
//				if (item.hasLoaded()) {
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

	@Override
	public int getChildrenCount(int groupPosition) {
		return this.produtos.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this.produtos.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this.produtos.size();
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
			viewHolder.txtNome = (TextView) view.findViewById(android.R.id.text1);
			view.setTag(viewHolder);
		} else {
			viewHolder = (GroupViewHolder) view.getTag();
		}
	         
	    Hyper hiper = HiperPrecos.getInstance().getHypers().get(groupPosition);
	    
	    viewHolder.txtNome.setText(hiper.getName());
	     
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
	    private int mPosition;
	    private ChildViewHolder mHolder;
	    private String mFileName;
	    private String mHiper;
	
	    public ThumbnailTask(int position, ChildViewHolder holder, String fileName, String hiper) {
	        mPosition = position;
	        mHolder = holder;
	        mFileName = fileName;
	        mHiper = hiper;
	    }
	
	    @Override
	    protected Bitmap doInBackground(Void... arg0) {
	        return ImageStorage.getFileFromStorage(HiperPrecos.getInstance(), mFileName);
	    }
	
	    @SuppressLint("NewApi")
		@Override
	    protected void onPostExecute(Bitmap bitmap) {
	    	if (mHolder.position == mPosition) {
	        	if (bitmap == null) {
	        		if (mHiper.toLowerCase().contains("continente")) {
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

