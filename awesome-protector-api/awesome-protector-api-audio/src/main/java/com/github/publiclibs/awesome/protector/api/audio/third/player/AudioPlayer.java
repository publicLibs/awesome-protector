package com.github.publiclibs.awesome.protector.api.audio.third.player;

import static com.github.publiclibs.awesome.protector.api.audio.third.header.AudioHeader.format;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class AudioPlayer implements AutoCloseable {
	private final SourceDataLine outputLine;

	public AudioPlayer() throws LineUnavailableException {
		outputLine = AudioSystem.getSourceDataLine(format);
		outputLine.open(format);
		outputLine.start();
	}

	public @Override void close() throws Exception {
		outputLine.drain();
		outputLine.stop();
		outputLine.close();
	}

	public void setVolume(final float newValue) {
		final Control control = outputLine.getControl(FloatControl.Type.MASTER_GAIN);
		if (control instanceof FloatControl) {
			final FloatControl floatControl = (FloatControl) control;
			floatControl.setValue(newValue);
		} else {
			throw new UnsupportedOperationException(control.getClass().getName());
		}
	}

	public final int write(final byte[] b, final int off, final int len) {
		return outputLine.write(b, off, len);
	}
}
