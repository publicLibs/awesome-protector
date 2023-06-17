package com.github.publiclibs.awesome.protector.api.audio.third.demo;

import javax.sound.sampled.AudioFormat;

import com.github.publiclibs.awesome.protector.api.audio.third.header.AudioHeader;
import com.github.publiclibs.awesome.protector.api.audio.third.player.AudioPlayer;
import com.github.publiclibs.awesome.protector.api.audio.third.recorder.AudioRecorder;

public class TestBothAny3 {
	public static void main(final String[] args) throws Exception {
		try (AudioRecorder recorder = new AudioRecorder()) {
			try (AudioPlayer player = new AudioPlayer()) {
				final boolean[] stoped = new boolean[] { false };
				new Thread(() -> {
					float vol = -60F;
					try {
						while (true) {
							player.setVolume(vol);
							vol += 1F;
							System.err.println(vol);
							Thread.sleep(30);
						}
					} catch (final Exception e) {
						e.printStackTrace();
						stoped[0] = true;
					}

				}).start();

				final byte[] buffer = new byte[4096];
				while (!stoped[0]) {
					final int bytesRead = recorder.read(buffer, 0, buffer.length);
					whiteNoise(buffer);
					player.write(buffer, 0, bytesRead);
				}
			}
		}
	}

	private static void whiteNoise(final byte[] buffer) {
		final AudioFormat format = AudioHeader.getAudioFormat();
		for (int i = 0; i < buffer.length; i += 2) {
			final double frequency = Math.random() * 20000;
			final short amplitude = (short) (Short.MAX_VALUE / 2
					* Math.sin(2 * Math.PI * frequency * i / format.getSampleRate()));
			buffer[i] = (byte) (amplitude & 0xff);
			buffer[i + 1] = (byte) ((amplitude >> 8) & 0xff);
		}
	}

}
