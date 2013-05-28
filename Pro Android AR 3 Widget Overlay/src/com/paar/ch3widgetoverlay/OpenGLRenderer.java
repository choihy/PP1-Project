package com.paar.ch3widgetoverlay;


import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;

public class OpenGLRenderer implements Renderer {
	
	public ArrayList<Mesh> roots = new ArrayList<Mesh>();
	public OpenGLRenderer() {
		Cube cube1 = new Cube(1, 1, 1);
		//roots.add(cube1);
	}
	
	public void add(Mesh obj)
	{
		roots.add(obj);
	}
	
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
	}

	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		gl.glTranslatef(0, 0, -5);
		gameSystem(gl);
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f,100.0f);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
	public void gameSystem(GL10 gl)
	{
		for(int i=0 ; i<roots.size() ; i++)
		{
			float rangeMax = 2, rangeMid = 0, rangeMin = -2;
			float speed1 = 0.01f*rangeMax, speed2 = 0.02f*rangeMax;
			Mesh model = roots.get(i);
			if(model.attacked && model.z >-100)
			{
				model.z -= 0.5;
				model.ry = model.rx+=5;
				if(model.rx == 360)
					model.ry = model.rx = 0;
				speed2 *= (100 + model.z)/10;
				model.x -= speed2;
				if(model.goLeft)
					model.x += (speed2*2);
				model.y -= speed2;
				if(model.goTop)
					model.y += speed2*2;
			}
			else if (model.attacked)
			{
				model.attacked = false;
				model.location(0,0,-10);
				model.setGO();
				System.out.println(model.goLeft + "  " +model.goTop);
			}
			else 
			{
				if(model.y <= rangeMax && model.x == rangeMid && model.top)
				{
					model.y += speed1;
					model.right = true;
				}
				else if (model.y > rangeMin && model.x < rangeMax && model.right)
				{
					model.x += speed1;
					model.y -= speed2;
					model.top = false;
					model.left = true;
				}
				else if (model.left && model.x > rangeMin)
				{
					model.x -= speed2;
					model.right = false;
				}
				else if (model.y < rangeMax && model.x < rangeMid)
				{
					model.x += speed1;
					model.y += speed2;
					model.left = false;
				}
				else
				{
					model.top = true;
					model.x = rangeMid; 
					model.y = rangeMax;
				}
				
				model.ry = model.rx++;
				if(model.rx >=360)
					model.ry = model.rx = 0;
			}
			model.draw(gl);
		}
	}
}
