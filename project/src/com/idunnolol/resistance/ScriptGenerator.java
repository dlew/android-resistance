package com.idunnolol.resistance;

import java.util.HashMap;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import com.idunnolol.resistance.Config.Option;

public class ScriptGenerator {

	public static final String UTTERANCE_ID_ENDED = "ended";

	// Switch this on to speed up phrases and skip the pauses
	private static final boolean TEST_MODE = false;

	private static final int SHORT_PAUSE = 1500;
	private static final int LONG_PAUSE = 5000;

	private Context mContext;

	private TextToSpeech mTTS;

	public ScriptGenerator(Context context, TextToSpeech tts) {
		mContext = context;
		mTTS = tts;
	}

	public void saySpeech(Config config) {
		if (TEST_MODE) {
			mTTS.setSpeechRate(2.0f);
		}

		boolean allDisabled = true;
		for (Option option : Config.OPTIONS_ORDERED) {
			if (config.isOptionEnabled(option)) {
				allDisabled = false;
			}
		}

		// If all are disabled, use the basic Resistance terminology 
		if (allDisabled) {
			speak(R.string.script_close_eyes);
			shortPause();
			speak(R.string.script_spies_find_each_other);
			longPause();
			speak(R.string.script_spies_close_eyes);
			shortPause();
		}

		// If any are enabled, use Avalon terminology
		else {
			boolean merlinEnabled = config.isOptionEnabled(Option.MERLIN_ASSASSIN);

			speak(merlinEnabled ? R.string.script_close_eyes_extend_fists : R.string.script_close_eyes);
			shortPause();

			speak(config.isOptionEnabled(Option.OBERON) ? R.string.script_evil_find_each_other_except_oberon
					: R.string.script_evil_find_each_other);
			longPause();
			speak(R.string.script_evil_close_eyes);
			shortPause();

			if (config.isOptionEnabled(Option.MERLIN_ASSASSIN)) {
				speak(config.isOptionEnabled(Option.MORDRED) ? R.string.script_evil_be_known_except_mordred
						: R.string.script_evil_be_known);
				shortPause();
				speak(R.string.script_merlin_know_evil);
				longPause();
				speak(R.string.script_evil_hide);
				speak(R.string.script_merlin_close_eyes);
				shortPause();

				if (merlinEnabled) {
					boolean morganaEnabled = config.isOptionEnabled(Option.MORGANA);
					speak(morganaEnabled ? R.string.script_merlin_morgana_be_known : R.string.script_merlin_be_known);
					shortPause();
					speak(morganaEnabled ? R.string.script_percival_know_merlin_morgana
							: R.string.script_percival_know_merlin);
					longPause();
					speak(morganaEnabled ? R.string.script_merlin_morgana_hide : R.string.script_merlin_hide);
					speak(R.string.script_percival_close_eyes);
					shortPause();
				}
			}

			if (config.isOptionEnabled(Option.LANCELOT_VARIANT_3)) {
				speak(R.string.script_lancelot_know_each_other);
				longPause();
				speak(R.string.script_lancelot_close_eyes);
				shortPause();
			}
		}

		// No matter which path we took, end with the same "open your eyes" and send the utterance id
		HashMap<String, String> endParams = new HashMap<String, String>();
		endParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, UTTERANCE_ID_ENDED);
		mTTS.speak(mContext.getString(R.string.script_open_eyes), TextToSpeech.QUEUE_ADD, endParams);
	}

	private void speak(int resId) {
		mTTS.speak(mContext.getString(resId), TextToSpeech.QUEUE_ADD, null);
	}

	private void shortPause() {
		if (!TEST_MODE) {
			mTTS.playSilence(SHORT_PAUSE, TextToSpeech.QUEUE_ADD, null);
		}
	}

	private void longPause() {
		if (!TEST_MODE) {
			mTTS.playSilence(LONG_PAUSE, TextToSpeech.QUEUE_ADD, null);
		}
	}

}
