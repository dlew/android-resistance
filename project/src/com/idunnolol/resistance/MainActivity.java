package com.idunnolol.resistance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.idunnolol.resistance.Config.Option;

public class MainActivity extends SherlockListActivity implements OnInitListener, OnUtteranceCompletedListener {

	private Config mConfig;

	private TextToSpeech mTTS;

	private ScriptGenerator mGenerator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mTTS = new TextToSpeech(this, this);

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
	protected void onDestroy() {
		super.onDestroy();

		mTTS.shutdown();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		if (mIsSpeaking) {
			shutUp();
		}

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

	private MenuItem mSpeakMenuItem;
	private MenuItem mShutUpMenuItem;

	// Sometimes starting/stopping is not immediate; use this to determine state
	private boolean mIsSpeaking = false;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		mSpeakMenuItem = menu.findItem(R.id.menu_speak);
		mShutUpMenuItem = menu.findItem(R.id.menu_shut_up);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		mSpeakMenuItem.setVisible(!mIsSpeaking);
		mShutUpMenuItem.setVisible(mIsSpeaking);

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_speak:
			mGenerator.saySpeech(mConfig);
			setIsSpeaking(true);
			return true;
		case R.id.menu_shut_up:
			shutUp();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void shutUp() {
		mTTS.stop();
		setIsSpeaking(false);
	}

	private void setIsSpeaking(boolean isSpeaking) {
		mIsSpeaking = isSpeaking;

		supportInvalidateOptionsMenu();
	}

	//////////////////////////////////////////////////////////////////////////
	// android.speech.tts.TextToSpeech.OnInitListener

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			mGenerator = new ScriptGenerator(this, mTTS);
			mTTS.setOnUtteranceCompletedListener(this);
		}
		if (status == TextToSpeech.ERROR) {
			// TODO: Handle error situation
		}
	}

	//////////////////////////////////////////////////////////////////////////
	// android.speech.tts.TextToSpeech.OnUtteranceCompletedListener

	@Override
	public void onUtteranceCompleted(String utteranceId) {
		setIsSpeaking(false);
	}
}
