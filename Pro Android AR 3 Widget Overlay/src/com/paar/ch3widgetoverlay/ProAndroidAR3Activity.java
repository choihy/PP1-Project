package com.paar.ch3widgetoverlay;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ProAndroidAR3Activity extends Activity{
	SurfaceView cameraPreview;
	SurfaceHolder previewHolder;
	Camera camera;
	boolean inPreview;
	
	GLSurfaceView view = null;
	OpenGLRenderer openGL = new OpenGLRenderer();
	boolean onOpenGL;
	
	final static String TAG = "PAAR";	
	SensorManager sensorManager;
	
	int orientationSensor;
	float headingAngle , pitchAngle , rollAngle;
	
	int accelerometerSensor;
	float xAxis , yAxis , zAxis;
	float distanceX = 0;
	
	LocationManager locationManager;
	Location location;
	double latitude , longitude , altitude;
	
	TextView xAxisValue , yAxisValue , zAxisValue;
	TextView headingValue , pitchValue , rollValue;
	TextView altitudeValue , latitudeValue , longitudeValue;
	
	boolean mAutoFocus = false ,mAutoFocusTimer = false;
	long timeAF = 0 , startAF = 0 , endAF = 0 , AFCounter = 0;
	float mLastX,mLastY,mLastZ;
	float mLastH,mLastP,mLastR;
	
	Timer mTimer;
	TimerTask mTimerTask;
	
	Camera.PreviewCallback previewCallback = new Camera.PreviewCallback(){
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {}
	};

	Camera.AutoFocusCallback mAutoFocusCallBack = new Camera.AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			mAutoFocus = true;
			camera.setOneShotPreviewCallback(previewCallback);
		}
	};
	
	Camera.AutoFocusMoveCallback mAutoFocusMoveCallBack = new Camera.AutoFocusMoveCallback() {
		@Override
		public void onAutoFocusMoving(boolean start, Camera camera) {
    		//latitudeValue.setText(String.valueOf(start));
		}
	};
	
	SurfaceHolder.Callback surfaceCallback=new SurfaceHolder.Callback() {
    	public void surfaceCreated(SurfaceHolder holder) {
    		try {
    			camera.setPreviewDisplay(previewHolder);
    		}catch (Throwable t) {}
    	}
    	
    	public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {
    		Camera.Parameters parameters=camera.getParameters();
    		Camera.Size size=getBestPreviewSize(width, height, parameters);
    		if (size!=null) {
    			parameters.setPreviewSize(size.width, size.height);
    			camera.setParameters(parameters);
    			parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
    			camera.startPreview();
    			inPreview=true;
    		}
	}
    	
    	public void surfaceDestroyed(SurfaceHolder holder) {
    		camera.release();
    	    camera=null;
    	}
    	
    };
    
    class CameraTimerTask extends TimerTask {
    	@Override
    	public void run() {
    		if (camera != null && !mAutoFocus) 
    		{
    			camera.autoFocus(mAutoFocusCallBack);
    			camera.setAutoFocusMoveCallback(mAutoFocusMoveCallBack);
    			mAutoFocus = true;
    			endAF = System.currentTimeMillis();
    		}
    	}
    }
    
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        orientationSensor = Sensor.TYPE_ORIENTATION;
        accelerometerSensor = Sensor.TYPE_ACCELEROMETER;
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(orientationSensor), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(accelerometerSensor), SensorManager.SENSOR_DELAY_NORMAL);

        inPreview = false;
        camera();
        
        xAxisValue = (TextView) findViewById(R.id.xAxisValue);
        yAxisValue = (TextView) findViewById(R.id.yAxisValue);
        zAxisValue = (TextView) findViewById(R.id.zAxisValue);
        headingValue = (TextView) findViewById(R.id.headingValue);
        pitchValue = (TextView) findViewById(R.id.pitchValue);
        rollValue = (TextView) findViewById(R.id.rollValue);
        altitudeValue = (TextView) findViewById(R.id.altitudeValue);
        longitudeValue = (TextView) findViewById(R.id.longitudeValue);
        latitudeValue = (TextView) findViewById(R.id.latitudeValue);

        onOpenGL = false;
        openGL();
        
        mTimer = new Timer();
        mTimerTask = new CameraTimerTask();
        mTimer.schedule(mTimerTask, 0, 1000);
    }
    
    @SuppressWarnings("deprecation")
	public void camera()
    {
    	cameraPreview = (SurfaceView)findViewById(R.id.cameraPreview);
        previewHolder = cameraPreview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
    public void openGL()
    {
    	openGL.add(new objLoad(this,R.raw.ghost));
    	view = new myGLSurfaceView(this,openGL);
        this.addContentView(view,new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
        onOpenGL = true;
    }
    
    final SensorEventListener sensorEventListener = new SensorEventListener() {
    	@SuppressWarnings("deprecation")
		public void onSensorChanged(SensorEvent sensorEvent) {
    		if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
    		{
    			mLastX = xAxis;
    			mLastY = yAxis;
    			mLastZ = zAxis;
    			xAxisValue.setText(String.valueOf(xAxis = sensorEvent.values[0]));
    			yAxisValue.setText(String.valueOf(yAxis = sensorEvent.values[1]));
    			zAxisValue.setText(String.valueOf(zAxis = sensorEvent.values[2]));
    			/*
	    			Log.d(TAG, "X Axis: " + String.valueOf(xAxis));
	    			Log.d(TAG, "Y Axis: " + String.valueOf(yAxis));
	    			Log.d(TAG, "Z Axis: " + String.valueOf(zAxis));
				*/
    		}
    		else if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION)
    		{
    			mLastH = headingAngle;
    			mLastP = pitchAngle;
    			mLastR = rollAngle;
    			headingValue.setText(String.valueOf(headingAngle = (int) Math.ceil(sensorEvent.values[0])));
    			pitchValue.setText(String.valueOf(pitchAngle = (int) Math.ceil(sensorEvent.values[1])));
    			rollValue.setText(String.valueOf(rollAngle = (int) Math.ceil(sensorEvent.values[2])));
    			/*
	    			Log.d(TAG, "Heading: " + String.valueOf(headingAngle));
	    			Log.d(TAG, "Pitch: " + String.valueOf(pitchAngle));
	    			Log.d(TAG, "Roll: " + String.valueOf(rollAngle));
	    		*/
    		}
    		//controlAutoFocus();
    		if(onOpenGL)
    			controlModel();
    	}
    	
    	public void onAccuracyChanged (Sensor senor, int accuracy) {}
    };
    
    public void updateLocation()
    {
    	boolean GPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean NET = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if(GPS && NET)
		{
			location =  locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			
    		latitude = location.getLatitude();
    		longitude = location.getLongitude();
    		altitude = location.getAltitude();
    		/*
    		Log.d(TAG, "Latitude: " + String.valueOf(latitude));
    		Log.d(TAG, "Longitude: " + String.valueOf(longitude));
    		Log.d(TAG, "Altitude: " + String.valueOf(altitude));
    		*/
    		latitudeValue.setText(String.valueOf(latitude));
    		longitudeValue.setText(String.valueOf(longitude));
    		altitudeValue.setText(String.valueOf(altitude));
		}
    }
      
	public void controlModel()
    {
    	for(int i=0 ; i<openGL.roots.size() ; i++)
    	{
			if(headingAngle < 180)
				openGL.roots.get(i).X = (headingAngle / 10 * -1 );
			else 
				openGL.roots.get(i).X = ((headingAngle - 360) * -1 / 10);
	
			openGL.roots.get(i).Y = zAxis;
	    }
    }
    
    public void controlAutoFocus()
    {
		float deltaX  = Math.abs(mLastX - xAxis);
		float deltaY = Math.abs(mLastY - yAxis);
		float deltaZ = Math.abs(mLastZ - zAxis);

		if (deltaX > 1 && mAutoFocus)
			mAutoFocus = false;
		else if (deltaY > 1 && mAutoFocus)
			mAutoFocus = false;
		else if (deltaZ > 1 && mAutoFocus)
			mAutoFocus = false;
		
		float deltaH  = Math.abs(mLastH - headingAngle);
		float deltaP = Math.abs(mLastP - pitchAngle);
		float deltaR = Math.abs(mLastR - rollAngle);
		
		if (deltaH > 10 && mAutoFocus)
			mAutoFocus = false;
		else if (deltaP > 10 && mAutoFocus)
			mAutoFocus = false;
		else if (deltaR > 10 && mAutoFocus)
			mAutoFocus = false;
		
		if(timeAF < 0)
			mAutoFocus = false;
		if(mAutoFocus)
			AFCounter++;
		if(AFCounter >= 100f || !mAutoFocus)
		{
			AFCounter = 0;
			mAutoFocus = false;
		}
		latitudeValue.setText(String.valueOf(AFCounter));
		
		longitudeValue.setText(String.valueOf(mAutoFocus));
		timeAF = 0;
		if(mAutoFocus)
			timeAF = (endAF - startAF);
		else
			startAF = System.currentTimeMillis();
		altitudeValue.setText(String.valueOf(timeAF));
    }
    
    @Override
    public void onResume() {
      sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(orientationSensor), SensorManager.SENSOR_DELAY_NORMAL);
      sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(accelerometerSensor), SensorManager.SENSOR_DELAY_NORMAL);
      camera=Camera.open();
      if(onOpenGL)
      	view.onResume();
      super.onResume();
    }
      
    @Override
    public void onPause() {
      if (inPreview) 
        camera.stopPreview();
      sensorManager.unregisterListener(sensorEventListener);
      camera.release();
      camera=null;
      inPreview=false;
      if(onOpenGL)
    	  view.onPause();
      super.onPause();
    }
    
    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
    	Camera.Size result=null;

    	for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
    		if (size.width<=width && size.height<=height) {
    			if (result==null) {
    				result=size;
    			}
    			else {
    				int resultArea=result.width*result.height;
    				int newArea=size.width*size.height;

    				if (newArea>resultArea) {
    					result=size;
    				}
    			}
    		}
    	}
    	return(result);
    }

    public void distance()
    {	
		Camera.Parameters parameters = camera.getParameters();
		float[] focusDistances = new float[3];
		parameters.getFocusDistances(focusDistances);

		float near = focusDistances[Camera.Parameters.FOCUS_DISTANCE_NEAR_INDEX]; // 0.15
		float optimal = focusDistances[Camera.Parameters.FOCUS_DISTANCE_OPTIMAL_INDEX];// 1.2
		float far =  focusDistances[Camera.Parameters.FOCUS_DISTANCE_FAR_INDEX]; // infinity
		
		latitudeValue.setText(String.valueOf(near));
    	longitudeValue.setText(String.valueOf(optimal));
    	altitudeValue.setText(String.valueOf(far));
    }
    
}