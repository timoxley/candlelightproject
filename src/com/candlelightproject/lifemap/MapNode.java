package com.candlelightproject.lifemap;

import android.content.Context;
import android.graphics.drawable.Drawable;
//import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Canvas;
import android.graphics.Rect;
import java.util.ArrayList;

public class MapNode  {
	private Drawable node; // the image of the ball
	private int coordX = 0; // the x coordinate at the canvas
	private int coordY = 0; // the y coordinate at the canvas
	private double velX = 0;
	private double velY = 0;
	private int id; // gives every ball his own id, for now not necessary
	private static int count = 0;
	private boolean goRight = true;
	private boolean goDown = true;
	private int width = 0;
	private int height = 0;
	private int flash = 0;

	private int mode = 0;

	public static final int MODE_IDLE = 0;
	public static final int MODE_DRAGGING = 1;
	public static final int MODE_SELECTED = 2;
	public static final int MODE_EDIT= 3;
	
	//private ArrayList<MapNode> connections;
	
	// Always ensure this mode has index 1 greater than second highest mode
	// so we can ensure modes are in range  0 < mode < MODE_INVALID
	public final int MODE_INVALID= 4;
	private Context context;
	private int drawable;
	private static final int CLICK_TOLERANCE = 3;

	public MapNode(Context context) {
		//connections= new ArrayList<MapNode>();
		/*BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
		 */
		//        node = BitmapFactory.decodeResource(context.getResources(), drawable.node); 
		this.context = context;
		
		
		/// TODO: MAKE MapNode have a default image. Select image from mode change.
		
		//mode = MODE_IDLE; 
		changeMode(MODE_IDLE);

		id=count++;

	}

	public MapNode(Context context, int drawable, Point point) {

		/*BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        node = BitmapFactory.decodeResource(context.getResources(), drawable.node);
		 */
		node = context.getResources().getDrawable(drawable);

		width = node.getIntrinsicWidth();
		height = node .getIntrinsicHeight();
		id=count;
		count++;
		coordX= point.x;
		coordY = point.y;

	}

	public void centerOn(Point point) {
		coordX = point.x - (int) (width/2);
		coordY = point.y - (int) (height/2);
	}

	public void centerOn(int x, int y) {
		coordX = x - (int) (width/2);
		coordY = y - (int) (height/2);
	}

	public boolean isClicked(int clickX, int clickY) {
		flash = 10;

		return (Rect.intersects(clickSize(clickX, clickY), boundingBox()));
	}

	public Rect clickSize(int clickX, int clickY) {
		Rect clickSize = new Rect();
		clickSize.set(clickX - CLICK_TOLERANCE, clickY - CLICK_TOLERANCE, clickX + CLICK_TOLERANCE, clickY + CLICK_TOLERANCE);
		return clickSize;
	}

	public Rect boundingBox() {
		Rect boundingBox = new  Rect();

		boundingBox.set(coordX, coordY, coordX + width, coordY + height);
		return boundingBox;
	}

	public static int getCount() {
		return count;
	}

	void setX(int newValue) {
		coordX = newValue;
	}

	public int getX() {
		return coordX;
	}

	void setY(int newValue) {
		coordY = newValue;
	}

	public int getY() {
		return coordY;
	}
	
	public void setVelX(double newX) {
		velX = newX;
	}

	public void setVelY(double newY) {
		velY = newY;
	}
	
	public double getVelX() {
		return velX;
	}

	public double getVelY() {
		return  velY;
	}
	
	public int getID() {
		return id;
	}

	public Drawable getNode() {
		return node;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getMode() {
		return mode;
	}
	
	public int changeMode(int newMode) {
		if (mode <= MODE_EDIT && mode >= 0) {
			mode = newMode;
			
		} else {
			return -1;
		}
		
		switch (newMode) {
			case MODE_IDLE:
				drawable = R.drawable.node_idle;
			break;
			case MODE_DRAGGING:
				//drawable = R.drawable.node_dragging;
				drawable = R.drawable.node_selected;
			break;
			case MODE_SELECTED:
				drawable = R.drawable.node_selected;
			break;
			case MODE_EDIT:
				drawable = R.drawable.node_edit;
			break;
		}
		
		node = context.getResources().getDrawable(drawable);
		width = node.getIntrinsicWidth();
		height = node .getIntrinsicHeight();
		
		return mode;
	}
	
	public void moveNode(int goX, int goY) {
		// check the borders, and set the direction if a border has reached
		if (coordX > 270){
			goRight = false;
		}
		if (coordX < 0){
			goRight = true;
		}
		if (coordY > 400){
			goDown = false;
		}
		if (coordY < 0){
			goDown = true;
		}
		// move the x and y 
		if (goRight){
			coordX += goX;
		} else	 {
			coordX -= goX;
		}
		if (goDown){
			coordY += goY;
		} else  {
			coordY -= goY;
		}

	}

	public void lineTo(MapNode connectedTo) {
		//lineTo
	}
	
	public void draw(Canvas canvas) { //Context context) {
		node.setBounds(coordX, coordY, coordX + width, coordY + height);	
		node.draw(canvas);
		Paint mLinePaint = new Paint();
		mLinePaint.setAntiAlias(true);
		mLinePaint.setARGB(180, 255, 0, 255);
		//if () {
			
			
		//}
		/*if (flash > 0) {
			canvas.drawRect(boundingBox(), mLinePaint);
			flash--;
		}*/
	}

}