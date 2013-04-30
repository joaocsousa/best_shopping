package com.tinycoolthings.hiperprecos.product;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.tinycoolthings.hiperprecos.models.Produto;
import com.tinycoolthings.hiperprecos.serverComm.CallWebServiceTask;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Constants.Server.Parameter.Name;
import com.tinycoolthings.hiperprecos.utils.Debug;
import com.tinycoolthings.hiperprecos.utils.Storage;

@SuppressLint("NewApi")
public class ProductListAdapter extends ArrayAdapter<Produto> {
	
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
	
	public void setData(List<Produto> data) {
		clear();
		if (data != null) {
			for (Produto appEntry : data) {
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
		
		Produto item = getItem(position);
		viewHolder.txtNome.setText(item.getNome());
		String marca = "-";
		if (item.getMarca()!=null ) {
			marca = item.getMarca();
		}
		viewHolder.txtMarca.setText(marca);
		viewHolder.txtPreco.setText(String.valueOf(item.getPreco()) + " Û ");
		viewHolder.txtPeso.setText(item.getPeso());
		viewHolder.position = position;
		String fileName = Storage.getFileNameCompressed(Storage.getFileName(item.getUrlImagem(), item.getNome(), item.getMarca()));
		if (android.os.Build.VERSION.SDK_INT > 11) {
			new ThumbnailTask(position, viewHolder, fileName).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void)null);
		} else {
			new ThumbnailTask(position, viewHolder, fileName).execute();
		}
	
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int selectedProdID = getItem(position).getId();
				Debug.PrintInfo(ProductListAdapter.this, "Selected produto with id " + selectedProdID);
				if (getItem(position).hasLoaded()) {
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

	private static class ThumbnailTask extends AsyncTask <Void, Void, Bitmap> {
	    private int mPosition;
	    private ViewHolder mHolder;
	    private String mFileName;
	
	    public ThumbnailTask(int position, ViewHolder holder, String fileName) {
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