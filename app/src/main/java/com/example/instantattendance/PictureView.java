package com.example.instantattendance;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.instantattendance.ml.Facenet;
import com.example.instantattendance.ml.Pnet;
import com.example.instantattendance.mobilefacenet.MobileFaceNet;
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
import java.io.Serializable;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PictureView extends AppCompatActivity{
    private ImageView im;
    private ImageButton backButton,doneButton ;
    int detected,recognized,percentage;
    List<String> recognizedStudents;
    private File fi;
    ProgressDialog pd;
    File RefImagesfolder;
    ArrayList<File> filesList;
    private final String TAG= "PicturePrev";
    long time;
    private Sections section;
    private MobileFaceNet facenetmodel;
    //for debug
    ImageView face_rec0,face_rec1,face_rec2, face_rec3,face_rec4,face_rec5;
    TextView text_rec0,text_rec1,text_rec2,text_rec3,text_rec4,text_rec5;
    ArrayList<Bitmap> refFaces;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_view);
        try {
            facenetmodel = new MobileFaceNet(getAssets());
        } catch (IOException e) {
            e.printStackTrace();
        }
        detected=0;
        recognized=0;
        percentage=0;
        recognizedStudents = new ArrayList<>();
        refFaces = new ArrayList<>();
        getSupportActionBar().hide();
        Intent i = getIntent();
        section = (Sections) i.getSerializableExtra("sectionN");
        Log.d(TAG, "onCreate: "+section.getCourse_Code());
        face_rec0 = (ImageView) findViewById(R.id.face_preview0);
        face_rec1 = (ImageView) findViewById(R.id.face_preview1);
        face_rec2 = (ImageView) findViewById(R.id.face_preview2);
        face_rec3 = (ImageView) findViewById(R.id.face_preview3);
        face_rec4 = (ImageView) findViewById(R.id.face_preview4);
        face_rec5 = (ImageView) findViewById(R.id.face_preview5);
        text_rec0 = (TextView) findViewById(R.id.txtV0);
        text_rec1 = (TextView) findViewById(R.id.txtV1);
        text_rec2 = (TextView) findViewById(R.id.txtV2);
        text_rec3 = (TextView) findViewById(R.id.txtV3);
        text_rec4 = (TextView) findViewById(R.id.txtV4);
        text_rec5 = (TextView) findViewById(R.id.txtV5);
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
                pd = new ProgressDialog(PictureView.this);
                pd.setMessage("Detecting and recognizing students");
                pd.setTitle("Taking Attendance In Progress..."); // Setting Title
                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // Progress Dialog Style Spinner
                pd.setCancelable(false);
                pd.setButton(DialogInterface.BUTTON_NEGATIVE, "Debug", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pd.dismiss();//dismiss dialog
                    }
                });
                pd.setButton(DialogInterface.BUTTON_POSITIVE, "View Attendance", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pd.dismiss();//dismiss dialog
                        Intent in = new Intent(getApplicationContext(),Attendance.class);
                        in.putExtra("sectionN",section);
                        in.putExtra("upd",true);
                        in.putExtra("rec", (Serializable) recognizedStudents);
                        startActivity(in);
                    }
                });
                pd.show(); // Display Progress Dialog

                long start = System.nanoTime();
                float[] i = inference(time);
                long end = System.nanoTime();
                //long Duration = (endTime - startTime);
                float second = ((end - start)/1000000000);
                //converting time into seconds didnt work because its too small!!

                pd.setProgress(100);
                pd.setMessage("Time taken: "+second+" Seconds\n"+"Detected students: "+(int)i[0]+"\n"+"Recognized students: "+(int)i[1]+"\n"+"Percentage: "+i[2]+"%");
                pd.setTitle("Finished taking the attendance");
                //pd.dismiss();







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

    private float[] inference(long time) {
        //
        File RefImagesfolder = new File(getFilesDir()+"/Sections/"+section.getSection_ID()+"/");
        ArrayList<File> filesList = new ArrayList<>(Arrays.asList(RefImagesfolder.listFiles()));
        FaceDetectorOptions opts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .build();

        Log.d(TAG, "STARTED: time 0");
        long startTime = System.nanoTime();
        FaceDetector detector = FaceDetection.getClient(opts);
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
                                        long endTime = System.nanoTime();
                                        long Duration = (endTime - startTime);
                                        //converting time into seconds didnt work because its too small!!
                                        float convert = ((endTime - startTime)/1000000000);
                                        //long convert = TimeUnit.SECONDS.convert(Duration, TimeUnit.NANOSECONDS);
                                        Log.d(TAG, "Ended: time taken by detection = " + convert+" Seconds");
                                        Log.d(TAG, "Ended: time taken by detection = " + Duration+" Nano Seconds");
                                        detected=faces.size();
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
                                            //Log.d(TAG, "onSuccess: "+RefImagesfolder);
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
                                            //
                                            int finalDebugrotate = debugrotate;
                                            new Thread() {
                                                @Override
                                                public void run() {
                                                    pd.show();


                                                    try {



                                                        // facenet code runs in a thread
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                //
                                                                if(!filesList.isEmpty()){
                                                                    for(File f: filesList){
                                                                        Bitmap m = BitmapFactory.decodeFile(f.getAbsolutePath());
                                                                        float _score = facenetmodel.compare(ret,m);
                                                                        Boolean isSame = _score > MobileFaceNet.THRESHOLD;
                                                                        if (isSame) {
                                                                            recognized++;
                                                                            filesList.remove(f);
                                                                            Log.d(TAG, "Student: "+f.getName().toString().substring(0,9)+" Has been recognized with score " + _score);
                                                                            recognizedStudents.add(f.getName().toString());
                                                                            switch (finalDebugrotate){
                                                                                case 0:{
                                                                                    text_rec0.setText(f.getName().toString().substring(0,9));
                                                                                    break;}
                                                                                case 1:text_rec1.setText(f.getName().toString().substring(0,9));
                                                                                    break;
                                                                                case 2:text_rec2.setText(f.getName().toString().substring(0,9));
                                                                                    break;
                                                                                case 3:text_rec3.setText(f.getName().toString().substring(0,9));
                                                                                    break;
                                                                                case 4:text_rec4.setText(f.getName().toString().substring(0,9));
                                                                                    break;
                                                                                default:text_rec5.setText(f.getName().toString().substring(0,9));
                                                                            }
                                                                            break;
                                                                        } else {
                                                                            Log.d(TAG, "Student is not recognized as "+f.getName().toString().substring(0,9)+" with score " + _score);
                                                                        }
                                                                    }

                                                                }else{
                                                                    Log.d(TAG, "run: List is empty");
                                                                    //

                                                                }
                                                            }
                                                        });
                                                    } catch (final Exception ex) {
                                                        Log.i("---","Exception in thread");
                                                    }


                                                }
                                            }.start();
                                            pd.setProgress((debugrotate/faces.size()*100));





                                            /*for (Bitmap b: refFaces){

                                            }*/


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


        float[] i = new float[5];
        i[0]= (float)detected;
        i[1]= (float)recognized;

        if (detected==0){
            i[2]=100;
        }
        else {
            i[2]=(recognized/detected*100);
        }
        i[3] = 0;
        i[4] = 0;
        return i;
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

    private void doLongOperation() {
        try {
            Thread.sleep(10000000);
        } catch (InterruptedException e) {
            Log.d(TAG, "doLongOperation: "+e);
        }
    }

    /*private void facenetInfra(List<File> filesList, Bitmap ret) {
        List<File> fileLis =filesList;
        Bitmap b = ret;


                
            }*/





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