package com.project.garageworkshop;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Math.round;

public class SensorsActivity extends AppCompatActivity implements SensorEventListener, LocationListener {
    private static final String TAG = "AndroidCameraApi";
    int brightness;
    private SensorManager senSensorManager;
    private LocationManager locationManager;
    private Handler handler2 =new Handler();
    private TextView xValue, yValue, zValue, coordinates, coordinatesData, direction;
    private Sensor senAccelerometer;
    private Button startStop, takePictureBtn, compass;
    private TextureView textureView;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    private String cameraId;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;

    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private Handler mBackgroundHandler;
    HandlerThread mBackgroundThread;
    private boolean informationObtained;
    long time, time1, time2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);
       /* boolean settingsCanWrite = Settings.System.canWrite(this);

        if(!settingsCanWrite) {
            settingPermission();
        }*/
        time = System.currentTimeMillis();
        informationObtained =false;
        startStop = (Button)findViewById(R.id.start_stopBtn);
        startStop.setOnClickListener(StartStopListener);
        senSensorManager =(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        xValue = findViewById(R.id.x_value);
        yValue = findViewById(R.id.y_value);
        zValue = findViewById(R.id.z_value);
        direction = findViewById(R.id.direction);
        coordinates = findViewById(R.id.coordinates);
        compass =findViewById(R.id.compass);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        compass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), compassActivity.class);
                startActivity(intent);
            }
        });
        boolean checkExtras = false;
        if(getIntent() != null) {
            Intent intent = getIntent();
            checkExtras = intent.getBooleanExtra("photo", false);
            if (checkExtras == true) {
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        takePicture();
                    }
                }, 500);
            }

        }




        textureView = (TextureView) findViewById(R.id.textureView);
        assert textureView != null;
        textureView.setSurfaceTextureListener(textureListener);

        takePictureBtn =(Button) findViewById(R.id.take_photo);
        assert takePictureBtn != null;
        takePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
    }

    /*public void settingPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 200);

            }
        }
    }*/

    TextureView.SurfaceTextureListener textureListener =new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.e(TAG, "OnOpened");
            cameraDevice =camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {

        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice =null;
        }
    };

    final CameraCaptureSession.CaptureCallback captureCallbackListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            Toast.makeText(SensorsActivity.this, "Saved: " + file, Toast.LENGTH_SHORT).show();
            createCameraPreview();
        }
    };

    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler =null;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void takePicture() {
        if (null == cameraDevice) {
            Log.e(TAG, "cameraDevice is null");
            return;
        }
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
           Size[] jpegSizes = null;
            if (characteristics != null) {
                jpegSizes =  characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width =jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            ArrayList<Surface> outputSurfaces = new ArrayList<>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));

            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            final File file = new File(Environment.getExternalStorageDirectory()+ "/pic.jpeg");

            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes =new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (image !=null) {
                            ((Image) image).close();
                        }
                    }
                }
                private void save(byte[] bytes) throws IOException {
                    OutputStream output =null;
                    try {
                        output = new FileOutputStream(file);
                        output.write(bytes);
                    } finally {
                        if (null != output) {
                            output.close();
                        }
                    }
                }
            };
            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);


            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(SensorsActivity.this, "Saved: " + file, Toast.LENGTH_SHORT).show();
                    createCameraPreview();
                }
            };

            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);

                    }catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert  texture !=null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    if (null == cameraDevice) {
                        return;
                    }
                    cameraCaptureSessions = session;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Toast.makeText(SensorsActivity.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "is camera open");
        try {
            cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SensorsActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId, stateCallback, null);
        } catch(CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "omenCamera X");

    }
    protected void updatePreview() {
        if (null == cameraDevice) {
            Log.e(TAG, "Update preview error, return");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private  void closeCamera() {
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice =null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(SensorsActivity.this, "Sorry!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    View.OnClickListener StartStopListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (senAccelerometer == null) {
                Toast.makeText(SensorsActivity.this, "Nerastas sensorius", Toast.LENGTH_LONG).show();
                return;
            }
            if (informationObtained) {
                startStop.setText("Start");
                senSensorManager.unregisterListener(SensorsActivity.this, senAccelerometer);
                informationObtained = false;
            }
            else {
                senSensorManager.registerListener(SensorsActivity.this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                startStop.setText("Stop");
                informationObtained = true;
            }
        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;
        time = System.currentTimeMillis();
        if (mySensor.getType() ==Sensor.TYPE_ACCELEROMETER && time >= time1) {
            time1 = 500 + System.currentTimeMillis();
            xValue.setText(String.valueOf(round(event.values[0]*100.0)/100.0));
            yValue.setText(String.valueOf(round(event.values[1]*100.0)/100.0));
            zValue.setText(String.valueOf(round(event.values[2]*100.0)/100.0));
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            if(x > -2 && x < 2 && y > -2 && y < 2 && z > 8)
                direction.setText("Screen direction: up");
            if(z > -2 && z < 2 && y > -2 && y < 2 && x > 8)
                direction.setText("Screen direction: left");
            if(z > -2 && z < 2 && y > -2 && y < 2 && x < -8)
                direction.setText("Screen direction: right");
            if(z > -2 && z < 2 && x > -2 && x < 2 && y > 8)
                direction.setText("Screen direction: user");
            if(z > -2 && z < 2 && x > -2 && x < 2 && y < -8)
                direction.setText("Screen direction: upside down");
            if (x > -2 && x < 1 && y > -2 && y < 0 && z < -9 && z > -10) {
                finish();
                moveTaskToBack(true);
            }
        }
        if(mySensor.getType() == Sensor.TYPE_ACCELEROMETER && time >= time2)
        {
            time2 = 200 + System.currentTimeMillis();
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            brightness = -1;
            if(x < 2 && x > -2 && y < 2 && y > -2 && z > 8) {
                brightness = 1;
            }
            if(x < 2 && x > -2 && z < 2 && z > -2 && y > 8) {
                brightness = 255;
            }
            if (brightness == 1 || brightness == 255) {
                WindowManager.LayoutParams layout = getWindow().getAttributes();
                layout.screenBrightness = brightness;
                getWindow().setAttributes(layout);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(senAccelerometer != null) {
            senSensorManager.unregisterListener(SensorsActivity.this, senAccelerometer);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.locationManager.removeUpdates(this);
        stopBackgroundThread();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (senAccelerometer != null && informationObtained) {
            senSensorManager.registerListener(SensorsActivity.this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
        startBackgroundThread();
        if(textureView.isAvailable()) {
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        if (location !=null) {
            coordinates.setText("Lat: " + location.getLatitude() + " " + "long " + location.getLongitude());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
