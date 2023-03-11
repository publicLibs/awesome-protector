package com.github.publiclibs.awesome.protector.api.audio.exception;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 * Api для записи звука из системного канала
 *
 * @author freedom1b2830
 * @date 2023-февраля-24 21:27:04
 */
public class AudioRecorder implements AutoCloseable {

	// private AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

	private static final int MONO = 1;
	private final AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, MONO, 2, 44100,
			true);
	private TargetDataLine mike;

	AudioInputStream sound;

	public @Override void close() throws Exception {
		stopRecording();
	}

	public AudioInputStream getAndStartRecording() throws LineUnavailableException {
		final DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		if (!AudioSystem.isLineSupported(info)) {
			throw new LineNotSupportedException("Линия не поддерживается");
		}
		mike = (TargetDataLine) AudioSystem.getLine(info);
		mike.open(format, mike.getBufferSize());
		sound = new AudioInputStream(mike);
		mike.start();
		return sound;
	}

	public void stopRecording() {
		mike.stop();
		mike.close();
	}
}