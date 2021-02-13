package com.example.instantattendance;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

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
import java.util.List;
import java.util.Locale;

public class PictureView extends AppCompatActivity {
    private ImageView im;
    private ImageButton backButton,doneButton ;
    private File fi;
    private final String TAG= "PicturePrev";
    long time;
    //for debug
    ImageView face_rec0,face_rec1,face_rec2, face_rec3,face_rec4,face_rec5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_view);

        getSupportActionBar().hide();
        Intent i = getIntent();
        face_rec0 = (ImageView) findViewById(R.id.face_preview0);
        face_rec1 = (ImageView) findViewById(R.id.face_preview1);
        face_rec2 = (ImageView) findViewById(R.id.face_preview2);
        face_rec3 = (ImageView) findViewById(R.id.face_preview3);
        face_rec4 = (ImageView) findViewById(R.id.face_preview4);
        face_rec5 = (ImageView) findViewById(R.id.face_preview5);
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
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .build();


        FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);
        final String filePath = getFilesDir() + "/" + time + ".jpg";
        Log.d(TAG, "inference: "+filePath);
        fi = new File(filePath);
        Bitmap m =BitmapFactory.decodeFile(fi.getAbsolutePath());
        InputImage image = InputImage.fromByteArray(bitmapToNV21(m),
                /* image width */ m.getWidth(),
                /* image height */ m.getHeight(),
                0,
                InputImage.IMAGE_FORMAT_NV21 // or IMAGE_FORMAT_YV12
        );
        Task<List<Face>> result =
                detector.process(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<Face>>() {
                                    @Override
                                    public void onSuccess(List<Face> faces) {
                                        int debugrotate = 0;
                                        for(Face x : faces){
                                            int width = x.getBoundingBox().width();
                                            int height = x.getBoundingBox().height();
                                            /*Bitmap croppedBitmap = Bitmap.createBitmap(
                                            irotateBitmap( source , -90f )!! else source,
                                                    rect.left,
                                                    rect.top,
                                                    width,
                                                    height )
                                            int w = x.getBoundingBox().right - x.getBoundingBox().left;
                                            int h = x.getBoundingBox().bottom - x.getBoundingBox().top;*/
                                            Bitmap ret = Bitmap.createBitmap(width,height ,m.getConfig());
                                            Canvas canvas = new Canvas(ret);
                                            canvas.drawBitmap(m, -x.getBoundingBox().left, -x.getBoundingBox().top, null);
                                            switch (debugrotate){
                                                case 0:{
                                                    im.setVisibility(View.INVISIBLE);
                                                    face_rec0.setImageBitmap(ret);
                                                    break;}
                                                case 1:face_rec1.setImageBitmap(ret);
                                                    break;
                                                case 2:face_rec2.setImageBitmap(ret);
                                                    break;
                                                case 3:face_rec3.setImageBitmap(ret);
                                                    break;
                                                case 4:face_rec4.setImageBitmap(ret);
                                                    break;
                                                default:face_rec5.setImageBitmap(ret);
                                            }
                                            debugrotate++;

                                            //im.setRotation(-90);
                                            Log.d(TAG, "onSuccess: "+x.getBoundingBox()+" "+x.getBoundingBox().top+" "+x.getBoundingBox().bottom+" "+x.getBoundingBox().right+" "+x.getBoundingBox().left);
                                        }
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                        Log.d(TAG, "onFailure: "+e);
                                    }
                                });






        /*try {
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
        }*/
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
    private final byte[] bitmapToNV21(Bitmap bitmap) {
        int[] argb = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(argb, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        byte[] yuv = new byte[bitmap.getHeight() * bitmap.getWidth() + 2 * (int)Math.ceil((double)bitmap.getHeight() / 2.0D) * (int)Math.ceil((double)bitmap.getWidth() / 2.0D)];
        this.encodeYUV420SP(yuv, argb, bitmap.getWidth(), bitmap.getHeight());
        return yuv;
    }

    private final void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width, int height) {
        int frameSize = width * height;
        int yIndex = 0;
        int uvIndex = frameSize;
        int R ;
        int G ;
        int B ;
        int Y ;
        int U ;
        int V ;
        int index = 0;
        int j = 0;

        for(int var16 = height; j < var16; ++j) {
            int var17 = 0;

            for(int var18 = width; var17 < var18; ++var17) {
                 R = (argb[index] & 16711680) >> 16;
                 G = (argb[index] & '\uff00') >> 8;
                 B = (argb[index] & 255) >> 0;
                 Y = (66 * R + 129 * G + 25 * B + 128 >> 8) + 16;
                 U = (-38 * R - 74 * G + 112 * B + 128 >> 8) + 128;
                 V = (112 * R - 94 * G - 18 * B + 128 >> 8) + 128;
                yuv420sp[yIndex++] = (byte)(Y < 0 ? 0 : (Y > 255 ? 255 : Y));
                if (j % 2 == 0 && index % 2 == 0) {
                    yuv420sp[uvIndex++] = (byte)(V < 0 ? 0 : (V > 255 ? 255 : V));
                    yuv420sp[uvIndex++] = (byte)(U < 0 ? 0 : (U > 255 ? 255 : U));
                }

                ++index;
            }
        }

    }

}