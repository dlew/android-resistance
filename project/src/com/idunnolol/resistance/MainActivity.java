package com.idunnolol.resistance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.idunnolol.resistance.Config.Option;

public class MainActivity extends ListActivity {

	private Config mConfig;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mConfig = new Config();
		mConfig.load(this);

		// Populate a SimpleAdapter with Option data
		String[] from = {
			"text1",
			"text2"
		};
		int[] to = {
			android.R.id.text1,
			android.R.id.text2
		};

		List<Map<String, String>> data = new ArrayList<Map<String, String>>();

		for (Option option : Config.OPTIONS_ORDERED) {
			Map<String, String> row = new HashMap<String, String>();
			row.put(from[0], getString(option.getTitleResId()));
			row.put(from[1], getString(option.getDescResId()));
			data.add(row);
		}

		setListAdapter(new SimpleAdapter(this, data, R.layout.row_option, from, to));

		// Configure ListView for multiple choice mode (and set checked items)
		final ListView listView = getListView();

		listView.setItemsCanFocus(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		syncConfigToList();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		SparseBooleanArray checkedItems = l.getCheckedItemPositions();
		mConfig.setOptionEnabled(Config.OPTIONS_ORDERED[position], checkedItems.get(position));
		mConfig.save(this);

		syncConfigToList();
	}

	// Syncs the current configuration to the checked items on the ListView
	private void syncConfigToList() {
		final ListView listView = getListView();
		for (int a = 0; a < Config.OPTIONS_ORDERED.length; a++) {
			listView.setItemChecked(a, mConfig.isOptionEnabled(Config.OPTIONS_ORDERED[a]));
		}
	}

	//////////////////////////////////////////////////////////////////////////
	// Action bar

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_speak:
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
