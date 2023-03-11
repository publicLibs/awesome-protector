package com.github.publiclibs.awesome.protector.api.audio.player;

import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

public class AudioPlayer implements AutoCloseable {

	private final Clip clip;
	protected AudioInputStream audioInputStream;

	/**
	 * @throws LineUnavailableException
	 *
	 */
	public AudioPlayer() throws LineUnavailableException {
		clip = AudioSystem.getClip();
	}

	public @Override void close() throws Exception {
		audioInputStream.close();
		clip.close();
	}

	public synchronized void playSound(final InputStream inputStream) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					audioInputStream = AudioSystem.getAudioInputStream(inputStream);
					clip.open(audioInputStream);
					clip.start();
				} catch (final Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}).start();
	}
}