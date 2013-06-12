package com.tinycoolthings.hiperprecos.search;

import com.actionbarsherlock.app.SherlockFragment;
import com.tinycoolthings.hiperprecos.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NoResultsFragment extends SherlockFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.no_results, container, false);
	}
	
}
