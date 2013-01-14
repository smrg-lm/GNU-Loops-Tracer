package com.smrglm.loops;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

import java.util.*;

public class Loops extends Activity {

	private static class Circle
	{
		public double x;
		public double y;
		private double rad;
		private double diameter;
		
		public Circle(double cx, double cy, double r) {
			x = cx;
			y = cy;
			setRad(r);
		}
		
		public void setRad(double r) {
			rad = r;
			diameter = r * 2;
		}
		
		public double getRad() {
			return rad;
		}
		
		public void setDiameter(double d) {
			diameter = d;
			rad = d * 0.5;
		}
		
		public double getDiameter() {
			return diameter;
		}
		
		public boolean contains(double px, double py) {
			if(Math.pow(rad, 2) >= (Math.pow(px - x, 2) + Math.pow(py - y, 2))) {
				return true;
			}
			return false;
		}
	}
	
	private static class KeyCircle extends Circle
	{
		public int id;
		
		public KeyCircle(double x, double y, double r) {
			super(x, y, r);
			id = -1;
		}
	}
	
	private static class KeyTrace
	{
		public Vector<Integer> list;
		private KeyView view;
		
		public KeyTrace(KeyView v) {
			list = new Vector<Integer>();
			view = v;
		}
		
		public void add(int id) {
			if(!list.isEmpty() && id != list.lastElement()) {
				if(id == -1) {
					view.traceCompleteAction();
					list.clear();
					return;
				}
				if(list.firstElement() == id) {
					view.traceCompleteAction();
					list.clear();
				}
				list.add(id);
			} else if(list.isEmpty()) {
				list.add(id);
			}
		}
	}
	
	private static class KeyABC
	{
		Vector<Vector<Integer>> dict;
		
		public KeyABC() {
			dict = new Vector<Vector<Integer>>();
			Vector<Integer> auxv;
			
			// loops simples a la derecha
			for(int i = 0; i < 7; i++) { // 8(numPoints) - 1
				auxv = new Vector<Integer>();
				auxv.add(0);
				auxv.add(1 + i);
				auxv.add(2 + i);
				dict.add(auxv);
			}
			auxv = new Vector<Integer>();
			auxv.add(0);
			auxv.add(8);
			auxv.add(1);
			dict.add(auxv);
			
			// loops simples a la izquierda
			for(int i = 0; i < 7; i++) { // 8(numPoints) - 1
				auxv = new Vector<Integer>();
				auxv.add(0);
				auxv.add(2 + i);
				auxv.add(1 + i);
				dict.add(auxv);
			}
			auxv = new Vector<Integer>();
			auxv.add(0);
			auxv.add(1);
			auxv.add(8);
			dict.add(auxv);
			
			// loops dobles a la derecha
			for(int i = 0; i < 6; i++) { // 8(numPoints) - 2
				auxv = new Vector<Integer>();
				auxv.add(0);
				auxv.add(1 + i);
				auxv.add(2 + i);
				auxv.add(3 + i);
				dict.add(auxv);
			}
			auxv = new Vector<Integer>();
			auxv.add(0);
			auxv.add(7);
			auxv.add(8);
			auxv.add(1);
			dict.add(auxv);
			auxv = new Vector<Integer>();
			auxv.add(0);
			auxv.add(8);
			auxv.add(1);
			auxv.add(2);
			dict.add(auxv);
			
			// loops dobles a la izquierda
			for(int i = 0; i < 6; i++) { // 8(numPoints) - 2
				auxv = new Vector<Integer>();
				auxv.add(0);
				auxv.add(3 + i);
				auxv.add(2 + i);
				auxv.add(1 + i);
				dict.add(auxv);
			}
			auxv = new Vector<Integer>();
			auxv.add(0);
			auxv.add(1);
			auxv.add(8);
			auxv.add(7);
			dict.add(auxv);
			auxv = new Vector<Integer>();
			auxv.add(0);
			auxv.add(2);
			auxv.add(1);
			auxv.add(8);
			dict.add(auxv);
		}
		
		public int atTrace(KeyTrace trace) {
			Iterator<Vector<Integer>> it = dict.iterator();
			while(it.hasNext()) {
				Vector<Integer> aux = it.next();
				if(aux.equals(trace.list)) {
					//globalText = "nxp: " + aux + "tr: " + trace.list;
					return (dict.indexOf(aux) + 97);
				}
			}
			return 0;
		}
	}
	
	static String globalText = "";
	
	private static class KeyView extends View {
		private KeyCircle initPos;
		private int numPoints = 8;
		private double startAngle = 0;
		private double maxDist = 150;
		private double prad = 55;
		private Vector<KeyCircle> points;
		private KeyTrace trace;
		private KeyABC abc;
		private Paint circlePaint;
		private Paint textPaint;
		public String text;
		
		public KeyView(Context context) {
    		super(context);
    		points = new Vector<KeyCircle>();
    		trace = new KeyTrace(this);
    		abc = new KeyABC();
    		text = "";
    		initPaint();
    	}
		
		private void initPaint() {
			circlePaint = new Paint();
			circlePaint.setAntiAlias(true);
			circlePaint.setColor(Color.BLACK);
			
    		textPaint = new Paint();
    		textPaint.setAntiAlias(true);
    		textPaint.setTextAlign(Align.LEFT);
    		textPaint.setTextSize(30);
    		textPaint.setColor(Color.BLACK);
		}
		
    	@Override
    	protected void onDraw(Canvas canvas) {
    		canvas.drawText(text, 0, 30, textPaint);
    		//canvas.drawText(globalText, 0, 30, textPaint);
    		Iterator<KeyCircle> it = points.iterator();
    		while(it.hasNext()) {
    			KeyCircle aux = it.next();
    			canvas.drawCircle((float)aux.x, (float)aux.y, (float)aux.getRad(), circlePaint);
    		}
    	}
    	
    	@Override
    	public boolean onTouchEvent(MotionEvent event) {
    		KeyCircle auxPoint = null;
    		
    		if(event.getAction() == MotionEvent.ACTION_DOWN) {
    			initPos = new KeyCircle(event.getX(), event.getY(), prad);
    			initPos.id = 0;
    			calcPoints();
    			auxPoint = pointFromPos(event.getX(), event.getY());
    			if(auxPoint != null) {
    				trace.add(auxPoint.id);
        			//text = " id = " + auxPoint.id + "; x = " + (int)auxPoint.x + "; y = " + (int)auxPoint.y + ";";
    			};
    			this.invalidate();
    		} else if(event.getAction() == MotionEvent.ACTION_MOVE) {
    			auxPoint = pointFromPos(event.getX(), event.getY());
    			if(auxPoint != null) {
    				trace.add(auxPoint.id);
    				//text = " id = " + auxPoint.id + "; x = " + (int)auxPoint.x + "; y = " + (int)auxPoint.y + ";";
    			};
    		} else {
    			initPos = null;
    			calcPoints();
    			trace.add(-1); // limpia y deber'ia llamar a la accion
    			this.invalidate();
    		}
    		
    		//this.invalidate();
    		return true;
    	}
    	
    	public void calcPoints() {
    		double step = startAngle;
    		KeyCircle auxPoint;
    		
    		points.clear();
    		
    		if(initPos != null) {
    			points.add(initPos);
    			
    			for(int i = 0; i < numPoints; i++) {
    				auxPoint = new KeyCircle(
    						Math.sin(step) * maxDist + initPos.x,
    						Math.cos(step) * maxDist + initPos.y,
    						prad
    				);
    				auxPoint.id = i + 1;
    				points.add(auxPoint);
    				step = (Math.PI * 2 / numPoints) + step;
    			}
    		};
    	}
    	
    	public KeyCircle pointFromPos(double x, double y) {
    		Iterator<KeyCircle> it = points.iterator();
    		while(it.hasNext()) {
    			KeyCircle aux = it.next();
    			if(aux.contains(x, y)) {
    				return aux;
    			}
    		}
    		return null;
    	}
    	
    	// callback...
    	public void traceCompleteAction() {
    		Vector<Integer> space = new Vector<Integer>();
    		space.add(0); space.add(3);
    		//Vector<Integer> delback = new Vector<Integer>();
    		//delback.add(0); delback.add(7);
    		Vector<Integer> enter = new Vector<Integer>();
    		enter.add(0); enter.add(3); enter.add(2); enter.add(3);
    		
    		Vector<Integer> auxt = trace.list;
    		if(auxt.equals(space)) text = text + " ";
    		//else if(auxt.equals(delback)) text = text + '\r';
    		else if(auxt.equals(enter)) text = "";
    		else text = text + (char)abc.atTrace(trace);
    		this.invalidate();
    	}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new KeyView(this));
	}

	// Project default
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
