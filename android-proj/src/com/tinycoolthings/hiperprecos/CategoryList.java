package com.tinycoolthings.hiperprecos;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class CategoryList extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
        	CategoryListFragment categoryListFrag = new CategoryListFragment();
            categoryListFrag.setArguments(getIntent().getExtras());
        	getSupportFragmentManager().beginTransaction().add(android.R.id.content, categoryListFrag).commit();
        }
	}
	
}
