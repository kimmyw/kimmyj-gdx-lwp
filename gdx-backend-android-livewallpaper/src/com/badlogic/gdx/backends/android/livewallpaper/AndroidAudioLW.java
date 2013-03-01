/*
 * Copyright 2010 Mario Zechner (contact@badlogicgames.com), Nathan Sweet (admin@esotericsoftware.com)
 * 
 *  Modified by Elijah Cornell
 *   
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.badlogic.gdx.backends.android.livewallpaper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.android.AndroidAudioRecorder;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * An implementation of the {@link Audio} interface for Android.
 * 
 * @author mzechner
 * 
 */
public final class AndroidAudioLW implements Audio {
	private SoundPool soundPool;
	private final AudioManager manager;
	protected final List<AndroidMusicLW> musics = new ArrayList<AndroidMusicLW>();
	protected final List<Boolean> wasPlaying = new ArrayList<Boolean>();
	
	public AndroidAudioLW (Service context) {
		soundPool = new SoundPool(16, AudioManager.STREAM_MUSIC, 100);
		manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		//context.setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	protected void pause () {
		wasPlaying.clear();
		for (AndroidMusicLW music : musics) {
			if (music.isPlaying()) {
				music.pause();
				wasPlaying.add(true);
			} else
				wasPlaying.add(false);
		}
	}

	protected void resume () {
		for (int i = 0; i < musics.size(); i++) {
			if (wasPlaying.get(i)) musics.get(i).play();
		}
	}

//	/**
//	 * {@inheritDoc}
//	 */
//	@Override public AudioDevice newAudioDevice (boolean isMono) {
//		return new AndroidAudioDeviceLW(isMono);
//	}

	/**
	 * {@inheritDoc}
	 */
	@Override public Music newMusic (FileHandle file) {
		AndroidFileHandleLW aHandle = (AndroidFileHandleLW)file;

		MediaPlayer mediaPlayer = new MediaPlayer();

		if (aHandle.type() == FileType.Internal) {
			try {
				AssetFileDescriptor descriptor = aHandle.assets.openFd(aHandle.path());
				mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
				descriptor.close();
				mediaPlayer.prepare();
				AndroidMusicLW music = new AndroidMusicLW(this, mediaPlayer);
				musics.add(music);
				return music;
			} catch (Exception ex) {
				throw new GdxRuntimeException("Error loading audio file: " + file
					+ "\nNote: Internal audio files must be placed in the assets directory.", ex);
			}
		} else {
			try {
				mediaPlayer.setDataSource(aHandle.path());
				mediaPlayer.prepare();
				AndroidMusicLW music = new AndroidMusicLW(this, mediaPlayer);
				musics.add(music);
				return music;
			} catch (Exception ex) {
				throw new GdxRuntimeException("Error loading audio file: " + file, ex);
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override public Sound newSound (FileHandle file) {
		AndroidFileHandleLW aHandle = (AndroidFileHandleLW)file;
		if (aHandle.type() == FileType.Internal) {
			try {				
				AssetFileDescriptor descriptor = aHandle.assets.openFd(aHandle.path());
				AndroidSoundLW sound = new AndroidSoundLW(soundPool, manager, soundPool.load(descriptor, 1));
				descriptor.close();
				return sound;
			} catch (IOException ex) {
				throw new GdxRuntimeException("Error loading audio file: " + file
					+ "\nNote: Internal audio files must be placed in the assets directory.", ex);
			}
		} else {
			try {
				return new AndroidSoundLW(soundPool, manager, soundPool.load(aHandle.path(), 1));
			} catch (Exception ex) {
				throw new GdxRuntimeException("Error loading audio file: " + file, ex);
			}
		}
	}

	

	/**
	 * Kills the soundpool and all other resources
	 */
	public void dispose () {
		soundPool.release();
	}

	@Override
	public AudioDevice newAudioDevice(int arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AudioRecorder newAudioRecorder(int samplingRate, boolean isMono) {
		// TODO Auto-generated method stub
		return null;
	}
}
