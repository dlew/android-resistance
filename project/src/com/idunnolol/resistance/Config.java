package com.idunnolol.resistance;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Config {

	public enum Option {
		MERLIN_ASSASSIN(R.string.option_merlin_assassin, R.string.option_merlin_assassin_description),
		PERCIVAL(R.string.option_percival, R.string.option_percival_description),
		MORDRED(R.string.option_mordred, R.string.option_mordred_description),
		OBERON(R.string.option_oberon, R.string.option_oberon_description),
		MORGANA(R.string.option_morgana, R.string.option_morgana_description),
		LANCELOT_VARIANT_3(R.string.option_lancelot_v3, R.string.option_lancelot_v3_description);

		private int mTitleResId;
		private int mDescResId;

		private Option(int titleResId, int descResId) {
			mTitleResId = titleResId;
			mDescResId = descResId;
		}

		public int getTitleResId() {
			return mTitleResId;
		}

		public int getDescResId() {
			return mDescResId;
		}
	}

	// All options, ordered as they should appear in the UI
	public static final Option[] OPTIONS_ORDERED = {
		Option.MERLIN_ASSASSIN,
		Option.PERCIVAL,
		Option.MORDRED,
		Option.OBERON,
		Option.MORGANA,
		Option.LANCELOT_VARIANT_3,
	};

	private Map<Option, Boolean> mConfig = new HashMap<Config.Option, Boolean>();

	public void setOptionEnabled(Option option, boolean enabled) {
		mConfig.put(option, enabled);

		// Some special logic, to make sure you can't set up anything wacky
		if (option == Option.MERLIN_ASSASSIN && !enabled) {
			// If Merlin/Assassin are disabled, there is no purpose to Percival/Morgana/Mordred
			mConfig.put(Option.PERCIVAL, false);
			mConfig.put(Option.MORDRED, false);
			mConfig.put(Option.MORGANA, false);
		}
		else if (option == Option.MORDRED && enabled) {
			// If Mordred is in the game, you're going to need Merlin/Assassin
			mConfig.put(Option.MERLIN_ASSASSIN, true);
		}
		else if (option == Option.PERCIVAL && enabled) {
			// If Percival is in the game, you're going to need Merlin/Assassin
			mConfig.put(Option.MERLIN_ASSASSIN, true);
		}
		else if (option == Option.MORGANA && enabled) {
			// If Morgana is in the game, you will necessarily need Percival and Merlin/Assassin
			mConfig.put(Option.MERLIN_ASSASSIN, true);
			mConfig.put(Option.PERCIVAL, true);
		}
	}

	public boolean isOptionEnabled(Option option) {
		if (mConfig.containsKey(option)) {
			return mConfig.get(option);
		}

		return false;
	}

	//////////////////////////////////////////////////////////////////////////
	// Serialization

	private static final String PREF_KEY = "com.idunnolol.resistance.config";

	public void save(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		try {
			JSONObject obj = new JSONObject();

			for (Option option : OPTIONS_ORDERED) {
				obj.put(option.name(), isOptionEnabled(option));
			}

			prefs.edit().putString(PREF_KEY, obj.toString()).commit();
		}
		catch (JSONException e) {
			// Not going to happen
		}
	}

	public void load(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String json = prefs.getString(PREF_KEY, "{}");

		try {
			JSONObject obj = new JSONObject(json);

			for (Option option : OPTIONS_ORDERED) {
				mConfig.put(option, obj.optBoolean(option.name(), false));
			}
		}
		catch (JSONException e) {
			// Not going to happen
		}
	}
}
