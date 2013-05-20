package com.tinycoolthings.hiperprecos.search;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.tinycoolthings.hiperprecos.HiperPrecos;
import com.tinycoolthings.hiperprecos.R;
import com.tinycoolthings.hiperprecos.models.Category;
import com.tinycoolthings.hiperprecos.models.Hyper;
import com.tinycoolthings.hiperprecos.serverComm.CallWebServiceTask;
import com.tinycoolthings.hiperprecos.utils.Constants;
import com.tinycoolthings.hiperprecos.utils.Constants.Server.Parameter.Name;
import com.tinycoolthings.hiperprecos.utils.Debug;

public class CategorySearchListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private ArrayList<ArrayList<Category>> categorias = new ArrayList<ArrayList<Category>>();
	private final LayoutInflater mInflater;
	
	private static class GroupViewHolder {
		private TextView txtNome;
	}
	
	private static class ChildViewHolder {
		private TextView txtNome;
	}
	
	public CategorySearchListAdapter(Context context, ArrayList<Category> categorias) {
		this.context = context;
		
		this.categorias.clear();
		
		SparseIntArray mapHiperGroup = new SparseIntArray();
		for (int i = 0; i<HiperPrecos.getInstance().getNumberOfHypers(); i++) {
			Hyper currHiper = HiperPrecos.getInstance().getHypers().get(i);
			mapHiperGroup.put(currHiper.getId(), i);
		}
		for (int i = 0; i < HiperPrecos.getInstance().getNumberOfHypers(); i++) {
			Hyper currentHiper = HiperPrecos.getInstance().getHypers().get(i);
			ArrayList<Category> currCatsHiper = new ArrayList<Category>();
			for (int j = 0; j < categorias.size(); j++) {
				Category currCat = categorias.get(j);
				Integer catHiper = currCat.getHyper().getId();
				if (catHiper.equals(currentHiper.getId())) {
					currCatsHiper.add(currCat);
				}
			}
			this.categorias.add(currCatsHiper);
		}
		
		this.mInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return this.categorias.get(groupPosition).get(childPosition);
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
			view = mInflater.inflate(R.layout.sherlock_spinner_dropdown_item, parent, false);
			viewHolder = new ChildViewHolder();
			viewHolder.txtNome = (TextView) view.findViewById(android.R.id.text1);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ChildViewHolder) view.getTag();
		}
		
		final Category item = categorias.get(groupPosition).get(childPosition);
		viewHolder.txtNome.setText("   " + item.getName());
		
		view.clearFocus();
		
//		view.setFocusable(true);
//		view.setFocusableInTouchMode(true);
//		view.setClickable(true);
		view.setBackgroundResource(R.drawable.list_item_selector_holo_light);
		
//		viewHolder.txtNome.setFocusable(true);
//		viewHolder.txtNome.setFocusableInTouchMode(true);
//		viewHolder.txtNome.setClickable(true);
		
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				int selectedCatID = item.getId();
				Debug.PrintInfo(CategorySearchListAdapter.this, "Selected categoria with id " + selectedCatID);
//				if (item.hasSubCategorias()) {
//					Debug.PrintError(CategorySearchListAdapter.this, "Subcats " + item.getSubCategories().size());
//				}
//				if (item.hasProdutos()) {
//					Debug.PrintError(CategorySearchListAdapter.this, "Prods " + item.getProducts().size());
//				}
//				if (item.hasLoaded()) {
//					Intent intent = new Intent(Constants.Actions.DISPLAY_CATEGORY);
//					intent.putExtra(Constants.Extras.CATEGORY, selectedCatID);
//					HiperPrecos.getInstance().sendBroadcast(intent);
//				} else {
//					CallWebServiceTask getCategoria = new CallWebServiceTask(Constants.Actions.GET_CATEGORY, true);
//					getCategoria.addParameter(Name.CATEGORIA_ID, selectedCatID);
//					getCategoria.execute();
//				}
			}
		});
	
		return view;
	
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this.categorias.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this.categorias.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this.categorias.size();
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
	         
//	    Hyper hiper = HiperPrecos.getInstance().getHipers().get(groupPosition);
//	    
//	    viewHolder.txtNome.setText(hiper.getNome());
	   	    
	    return view;
	    
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
}

