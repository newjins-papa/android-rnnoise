package com.rnnoiseandroid;

import android.media.*;
import android.media.AudioTrack;
import android.os.Handler;
import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

public class AudioTrackPlayer {
    private String pathAudio;
    private AudioTrack audioPlayer;
    private Thread mThread;
    private int bytesread = 0, ret = 0;
    private int size;
    private FileInputStream in = null;
    private byte[] byteData = null;
    private int count = 512 * 1024; // 512 kb
    private boolean isPlay = true;
    private boolean isLooping = false;
    private static Handler mHandler;

    public AudioTrackPlayer() {

    }

    public void prepare(String pathAudio) {
        this.pathAudio = pathAudio;
        mHandler = new Handler();
    }

    public void play() {
        stop();

        isPlay = true;
        bytesread = 0;
        ret = 0;
        if (pathAudio == null)
            return;

        audioPlayer = createAudioPlayer();
        if (audioPlayer == null) return;
        audioPlayer.play();

        mThread = new Thread(new PlayerProcess());
        mThread.start();
    }

    private final Runnable mLopingRunnable = new Runnable() {
        @Override
        public void run() {
            play();
        }
    };

    private AudioTrack createAudioPlayer() {
        int intSize = android.media.AudioTrack.getMinBufferSize(48000, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 48000, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, intSize, AudioTrack.MODE_STREAM);
        if (audioTrack == null) {
            //Log.d("TCAudio", "audio track is not initialised ");
            return null;
        }

        File file = null;
        file = new File(pathAudio);

        byteData = new byte[(int) count];
        try {
            in = new FileInputStream(file);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        size = (int) file.length();
        return audioTrack;
    }

    private class PlayerProcess implements Runnable {

        @Override
        public void run() {
            while (bytesread < size && isPlay) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                try {
                    ret = in.read(byteData, 0, count);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (ret != -1) { // Write the byte array to the track
                    audioPlayer.write(byteData, 0, ret);
                    bytesread += ret;
                } else break;
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (audioPlayer != null) {
                if (audioPlayer.getState() != AudioTrack.PLAYSTATE_STOPPED) {
                    audioPlayer.stop();
                    audioPlayer.release();
                    mThread = null;
                }
            }
            if (isLooping && isPlay) mHandler.postDelayed(mLopingRunnable, 100);
        }
    }

    public void setLooping() {
        isLooping = !isLooping;
    }

    public void pause() {

    }

    public void stop() {
        isPlay = false;
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
        if (audioPlayer != null) {
            audioPlayer.stop();
            audioPlayer.release();
            audioPlayer = null;
        }
    }

    public void reset() {

    }
}
