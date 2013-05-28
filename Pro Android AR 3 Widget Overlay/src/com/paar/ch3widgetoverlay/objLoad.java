package com.paar.ch3widgetoverlay;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import android.content.Context;

public class objLoad extends Mesh{

	public objLoad(Context ctx,int file)
	{
		z = -20;
		ArrayList<float[]> vertices = new ArrayList<float[]>();
		ArrayList<float[]> faces = new ArrayList<float[]>();
		try {
			System.out.println("File : "+file);
			InputStream inputStream = ctx.getResources().openRawResource(file);
            InputStreamReader inputreader = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(inputreader);
            String strLine;
			int count = 0;
			while ((strLine = br.readLine()) != null)
			{
				count++;
				if(strLine.startsWith("v"))
				{
					try{
						float[] val = new float[3];
						StringTokenizer tok = new StringTokenizer(strLine," ");
						int numTok = tok.countTokens()-1;
						tok.nextToken();
						for(int i=0 ; i<numTok ; i++)
							val[i] = Float.parseFloat(tok.nextToken());
						
	                    vertices.add(val);
					}catch (Exception e){}
				}
				else if(strLine.startsWith("f"))
				{
					try{
						float[] val = new float[8];
						float[] val1 = new float[3];
						float[] val2 = new float[3];
						StringTokenizer tok = new StringTokenizer(strLine," /");
						int numTok = tok.countTokens()-1;
						tok.nextToken();
						for(int i=0 ; i<numTok ; i++)
							val[i] = Float.parseFloat(tok.nextToken());
							
	                    val2[0] = val1[0] = val[0];
	                    val1[1] = val[2];
	                    val2[1] = val1[2] = val[4];
	                    val2[2] = val[6];
	                    
	                    faces.add(val1);
	                    if(numTok > 7)
	                    	faces.add(val2);
					}catch (Exception e){}
				}
			}
		} catch (Exception e) {
			System.out.println("Not Work !! "+e);
		}
		System.out.println("==================================");
		System.out.println("vertices.size() = "+vertices.size());
		System.out.println("faces.size() = "+faces.size());
		System.out.println("==================================");
		
		float verticesObj[] = new float[vertices.size()*3];
		short indicesObj[] = new short[faces.size()*3];
		int max = Math.max(vertices.size(), faces.size());
		for(int i=0 ; i<max ; i+=3)
		{
			float temp[] = new float[3];
			if(vertices.size()>i) {
				temp = vertices.get(i);
				verticesObj[i] = temp[0];
				verticesObj[i+1] = temp[1];
				verticesObj[i+2] = temp[2];
			}
			if(faces.size()>i)
			{
				temp = faces.get(i);
				indicesObj[i] = (short) temp[0] ;
				indicesObj[i+1] = (short) temp[1];
				indicesObj[i+2] = (short) temp[2];
			}
		}

		System.out.println("==================================");
		System.out.println("verticesObj = "+verticesObj.length);
		System.out.println("indicesObj = "+indicesObj.length);
		System.out.println("==================================");
        setIndices(indicesObj);
        setVertices(verticesObj);
	}
	
}

