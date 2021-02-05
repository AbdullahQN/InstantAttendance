package com.example.instantattendance;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.instantattendance.ml.Facenet;
import com.example.instantattendance.ml.Pnet;
import com.google.android.gms.common.util.IOUtils;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PictureView extends AppCompatActivity {
    private ImageView im;
    private ImageButton backButton,doneButton ;
    private File fi;
    private final String TAG= "PicturePrev";
    long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_view);

        getSupportActionBar().hide();
        Intent i = getIntent();
        time =(long) i.getSerializableExtra("time");
        final String filePath = getFilesDir() + "/" + time + ".jpg";
        Log.d(TAG, "onCreate: "+filePath);
        fi = new File(filePath);
        im = (ImageView) findViewById(R.id.image_preview);
        backButton = (ImageButton) findViewById(R.id.back_button);
        doneButton = (ImageButton) findViewById(R.id.done);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Done: ");
                inference(time);
                //finish();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean deleted = fi.delete();
                im.setVisibility(View.GONE);
                Log.d(TAG, "Back: "+ deleted);
                finish();
            }
        });
        Glide.with(this)
                .asBitmap()
                .load(fi.toString())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        im.setVisibility(View.GONE);
                        Log.d(TAG, "onLoadFailed: "+e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        //pb.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(im);

    }

    private void inference(long time) {
        try {
            Pnet model = Pnet.newInstance(getApplicationContext());
            final String filePath = getFilesDir() + "/" + time + ".jpg";
            Log.d(TAG, "inference: "+filePath);
            fi = new File(filePath);
            byte[] getBytes = {};
            try {
                getBytes = new byte[(int) fi.length()];
                InputStream is = new FileInputStream(fi);

                is.read(getBytes);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                is.close();
                // Creates inputs for reference.
                TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 600, 800, 3}, DataType.FLOAT32);
                Log.d(TAG, "inference: "+getBytes.length);
                inputFeature0.loadBuffer(ByteBuffer.wrap(getBytes));
                // Runs model inference and gets result.
                Pnet.Outputs outputs = model.process(inputFeature0);
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                TensorBuffer outputFeature1 = outputs.getOutputFeature1AsTensorBuffer();
                TensorBuffer outputFeature2 = outputs.getOutputFeature2AsTensorBuffer();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
        /*try {
            Facenet model = Facenet.newInstance(getApplicationContext());
            final String filePath = getFilesDir() + "/" + time + ".jpg";
            Log.d(TAG, "inference: "+filePath);
            fi = new File(filePath);
            byte[] getBytes = {};
            try {
                getBytes = new byte[(int) fi.length()];
                InputStream is = new FileInputStream(fi);
                is.read(getBytes);
                is.close();
                // Creates inputs for reference.
                TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 160, 160, 3}, DataType.FLOAT32);
                inputFeature0.loadBuffer(ByteBuffer.wrap(getBytes));

                // Runs model inference and gets result.
                Facenet.Outputs outputs = model.process(inputFeature0);
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }



            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }*/
    }

}