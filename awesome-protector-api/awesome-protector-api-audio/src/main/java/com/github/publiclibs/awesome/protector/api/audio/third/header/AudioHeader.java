package com.github.publiclibs.awesome.protector.api.audio.third.header;

import javax.sound.sampled.AudioFormat;

public class AudioHeader {
	public static final int sampleRate = 44100;
	public static final int channels = 2;
	public static final int frameRate = sampleRate;

	public static AudioFormat format = getAudioFormat();

	public static AudioFormat getAudioFormat() {
		if (format == null) {
			format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate, 16, channels, 4, frameRate, true);
		}
		return format;
	}

}
