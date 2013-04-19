package com.tinycoolthings.hiperprecos;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tinycoolthings.hiperprecos.models.Produto;
import com.tinycoolthings.hiperprecos.utils.Storage;

@SuppressLint("NewApi")
public class ProductListAdapter extends ArrayAdapter<Produto> {
	
	private final LayoutInflater mInflater;

	static class ViewHolder {
		public TextView txtNome;
		public TextView txtMarca;
		public TextView txtPreco;
		public ImageView img;
		public Integer position;
	}

	public ProductListAdapter(Context context) {
		super(context, android.R.layout.simple_list_item_2);
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
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view;
		
		if (convertView == null) {
			view = mInflater.inflate(R.layout.product_list_item, parent, false);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.txtNome = (TextView) view.findViewById(R.id.tv_prod_nome);
			viewHolder.txtMarca = (TextView) view.findViewById(R.id.tv_prod_marca);
			viewHolder.txtPreco = (TextView) view.findViewById(R.id.tv_prod_preco);
			viewHolder.img = (ImageView) view.findViewById(R.id.im_prod_img);
			view.setTag(viewHolder);
		} else {
			view = convertView;
		}
		
		Produto item = getItem(position);
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.txtNome.setText(item.getNome());
		holder.txtMarca.setText(item.getMarca());
		holder.txtPreco.setText(String.valueOf(item.getPreco()));
		holder.position = position;
		String fileName = Storage.getFileNameCompressed(Storage.getFileName(item.getUrlImagem(), item.getNome(), item.getMarca()));
		if (android.os.Build.VERSION.SDK_INT > 11) {
			new ThumbnailTask(position, holder, fileName).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void)null);
		} else {
			new ThumbnailTask(position, holder, fileName).execute();
		}
		
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