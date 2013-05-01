package com.tinycoolthings.hiperprecos.search;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.tinycoolthings.hiperprecos.models.Hiper;
import com.tinycoolthings.hiperprecos.models.Produto;
import com.tinycoolthings.hiperprecos.serverComm.CallWebServiceTask;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Constants.Server.Parameter.Name;
import com.tinycoolthings.hiperprecos.utils.Debug;
import com.tinycoolthings.hiperprecos.utils.Storage;

public class ProductSearchListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private ArrayList<ArrayList<Produto>> produtos = new ArrayList<ArrayList<Produto>>();
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
	
	public ProductSearchListAdapter(Context context, ArrayList<Produto> produtos) {
		this.context = context;
		
		this.produtos.clear();
		
		SparseIntArray mapHiperGroup = new SparseIntArray();
		for (int i = 0; i<HiperPrecos.getInstance().getNumberOfHipers(); i++) {
			Hiper currHiper = HiperPrecos.getInstance().getHipers().get(i);
			mapHiperGroup.put(currHiper.getId(), i);
		}
		for (int i = 0; i < HiperPrecos.getInstance().getNumberOfHipers(); i++) {
			Hiper currentHiper = HiperPrecos.getInstance().getHipers().get(i);
			ArrayList<Produto> currProdsHiper = new ArrayList<Produto>();
			for (int j = 0; j < produtos.size(); j++) {
				Produto currProd = produtos.get(j);
				Integer prodHiper = currProd.getHiper().getId();
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
		
		final Produto item = produtos.get(groupPosition).get(childPosition);
		viewHolder.txtNome.setText(item.getNome());
		String marca = "-";
		if (item.getMarca()!=null ) {
			marca = item.getMarca();
		}
		viewHolder.txtMarca.setText(marca);
		viewHolder.txtPreco.setText(String.valueOf(item.getPreco()) + "€");
		viewHolder.txtPeso.setText(item.getPeso());
		viewHolder.position = childPosition;
		String fileName = Storage.getFileNameCompressed(Storage.getFileName(item.getUrlImagem(), item.getNome(), item.getMarca()));
		if (android.os.Build.VERSION.SDK_INT > 11) {
			new ThumbnailTask(childPosition, viewHolder, fileName).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void)null);
		} else {
			new ThumbnailTask(childPosition, viewHolder, fileName).execute();
		}
	
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int selectedProdID = item.getId();
				Debug.PrintInfo(ProductSearchListAdapter.this, "Selected produto with id " + selectedProdID);
				if (item.hasLoaded()) {
					Intent intent = new Intent();
					intent.setAction(Constants.Actions.DISPLAY_PRODUTO);
					intent.putExtra(Constants.Extras.PRODUTO, selectedProdID);
					HiperPrecos.getInstance().sendBroadcast(intent);
				} else {
					CallWebServiceTask getProduto = new CallWebServiceTask(Constants.Actions.GET_PRODUTO);
					getProduto.addParameter(Name.PRODUTO_ID, selectedProdID);
					getProduto.execute();
				}
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
	         
	    Hiper hiper = HiperPrecos.getInstance().getHipers().get(groupPosition);
	    
	    viewHolder.txtNome.setText(hiper.getNome());
	     
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
	
	    public ThumbnailTask(int position, ChildViewHolder holder, String fileName) {
	        mPosition = position;
	        mHolder = holder;
	        mFileName = fileName;
	    }
	
	    @Override
	    protected Bitmap doInBackground(Void... arg0) {
	        return Storage.getFileFromStorage(HiperPrecos.getInstance(), mFileName);
	    }
	
	    @Override
	    protected void onPostExecute(Bitmap bitmap) {
	        if (mHolder.position == mPosition) {
	        	mHolder.img.setImageBitmap(bitmap);
	        }
	    }
	}
	
}

