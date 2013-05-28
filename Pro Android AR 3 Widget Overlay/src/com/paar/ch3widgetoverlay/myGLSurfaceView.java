package com.paar.ch3widgetoverlay;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class myGLSurfaceView extends GLSurfaceView{
	OpenGLRenderer model;
	public myGLSurfaceView(Context context,OpenGLRenderer openGL) {
		super(context);
		this.setEGLConfigChooser( 8, 8, 8, 8, 16, 0 );
 	    this.getHolder().setFormat( PixelFormat.TRANSLUCENT );
 	    this.setZOrderMediaOverlay(true);
		this.setRenderer(openGL);
		this.setRenderMode(RENDERMODE_CONTINUOUSLY);
 	    this.model = openGL;
	}
	
	public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
            	for(int i=0 ; i<model.roots.size() ; i++)
            		model.roots.get(i).attack(true);
            	System.out.println("Mouse X : "+x+" Mouse Y : "+y);
                break;
        }
        requestRender();
        return true;
    }

}
