package com.example.music;
import android.media.MediaPlayer;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void playOrStopSongFromFirebase(View v) {
        if (isPlaying) {
            stopSong();
        } else {
            playSong();
        }
    }

    private void playSong() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(getString(R.string.gs_audio_2fffa_appspot_com));

        try {
            File localFile = File.createTempFile("tempFile", ".mp3");
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // File downloaded successfully, play the audio file
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(localFile.getAbsolutePath());
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.start(); // Start playing the audio when it's prepared
                                isPlaying = true;
                            }
                        });
                        mediaPlayer.prepareAsync(); // Prepare the media player asynchronously
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    // Handle file download failure
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopSong() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
        }
    }
}
