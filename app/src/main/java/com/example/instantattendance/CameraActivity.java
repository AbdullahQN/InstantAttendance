package com.example.instantattendance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.SurfaceRequest;
import androidx.camera.core.impl.ImageAnalysisConfig;
import androidx.camera.core.impl.ImageCaptureConfig;
import androidx.camera.core.impl.PreviewConfig;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
import com.greentoad.turtlebody.imagepreview.ImagePreview;
import com.greentoad.turtlebody.imagepreview.core.ImagePreviewConfig;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class CameraActivity extends AppCompatActivity {

    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageButton capture;
    private String sectionNumber;
    private final String TAG= "CameraAct";
    long time;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        previewView = findViewById(R.id.previewView);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        capture = findViewById(R.id.camera_capture_button);
        getSupportActionBar().hide();
        Intent x = getIntent();
        sectionNumber = x.getSerializableExtra("sectionN").toString();
        Log.d(TAG, "onCreate: "+sectionNumber);
        cameraProviderFuture.addListener(() -> {
            try {
                // Camera provider is now guaranteed to be available
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Set up the view finder use case to display camera preview
                Preview preview = new Preview.Builder().build();

                // Set up the capture use case to allow users to take photos
                ImageCapture imgCap = new ImageCapture.Builder().setTargetRotation(Surface.ROTATION_0).setTargetResolution(new Size(600,800)).build();

                /*Date c = Calendar.getInstance().getTime();
                System.out.println("Current time => " + c);

                SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                String formattedDate = df.format(c);*/
                //final String fi = getFilesDir() + "/" + formattedDate + ".jpg";


                // Choose the camera by requiring a lens facing
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                // Attach use cases to the camera with the same lifecycle owner
                cameraProvider.bindToLifecycle(
                        ((LifecycleOwner) this),
                        cameraSelector,
                        preview,
                        imgCap);

                // Connect the preview use case to the previewView
                preview.setSurfaceProvider(
                        previewView.getSurfaceProvider());
                capture.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.P)
                    @Override
                    public void onClick(View v) {
                        time  = System.currentTimeMillis();
                        String fi = getFilesDir() + "/" + time + ".jpg";
                        Log.d(TAG, "run: "+fi);
                        File x = new File(fi);
                        ImageCapture.OutputFileOptions f =
                                new ImageCapture.OutputFileOptions.Builder(x).build();

                        //File f = new File(Environment.getExternalStorageDirectory() + "/" + System.currentTimeMillis() + ".jpg");
                        imgCap.takePicture(f, getMainExecutor(), new ImageCapture.OnImageSavedCallback(){
                            @Override
                            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                                Uri savedUri = Uri.fromFile(x);
                                Log.d(TAG, "Photo capture succeeded: "+savedUri.toString());
                                String msg = "Photo capture succeeded ";
                                previewTakenPic();
                                Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onError(@NonNull ImageCaptureException exception) {
                                //super(imageCaptureError, message, cause);
                                Log.d("CAMMS", "onError: Photo capture failed: " + exception);
                                Toast.makeText(getBaseContext(), "Error taking pic",Toast.LENGTH_LONG).show();

                            }
                        });





                }
            });

            } catch (InterruptedException | ExecutionException e) {
                Log.d("Image", "Exception");
                // Currently no exceptions thrown. cameraProviderFuture.get()
                // shouldn't block since the listener is being called, so no need to
                // handle InterruptedException.
            }
        }, ContextCompat.getMainExecutor(this));



    }

    public void previewTakenPic(){
        Intent in = new Intent(getApplicationContext(),PictureView.class);
        in.putExtra("time",time);
        in.putExtra("sectionN",sectionNumber);
        startActivity(in);

        //finish();


    }
}