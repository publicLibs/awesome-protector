package com.github.publiclibs.awesome.protector.api.audio.third.recorder;

import static com.github.publiclibs.awesome.protector.api.audio.third.header.AudioHeader.format;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class AudioRecorder implements AutoCloseable {
	private final TargetDataLine line;

	public AudioRecorder() throws LineUnavailableException {
		line = AudioSystem.getTargetDataLine(format);
		line.open(format);
		line.start();
	}

	public @Override void close() throws Exception {
		line.stop();
		line.close();
	}

	public final int read(final byte[] b, final int off, final int len) {
		return line.read(b, off, len);
	}
}
