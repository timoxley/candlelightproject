package com.candlelightproject.lifemap.view.graphview;

import java.util.LinkedList;

import com.candlelightproject.lifemap.R;
import com.candlelightproject.lifemap.R.drawable;
import com.candlelightproject.lifemap.graph.Graph;
import com.candlelightproject.lifemap.graph.Node;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

class GraphThread extends Thread {
	private Matrix mMatrix;


	/** Handle to the application context, used to e.g. fetch Drawables. */
	private Context mContext;

	private int eventNum = 0;
	
	
	private int nodeFocus = -1;
	
	/*
	 * Member (state) fields
	 */
	/** The drawable to use as the background of the animation canvas */
	private Bitmap mBackgroundImage;

	/**
	 * Current height of the surface/canvas.
	 * 
	 * @see #setSurfaceSize
	 */
	private int mCanvasHeight = 1;

	/**
	 * Current width of the surface/canvas.
	 * 
	 * @see #setSurfaceSize
	 */
	private int mCanvasWidth = 1;

	///** Message handler used by thread to interact with TextView */
	//private Handler mHandler;

	/** Used to figure out elapsed time between frames */
	//private long mLastTime;

	/** Paint to draw the lines on screen. */
	//private Paint mLinePaint;

	/** The state of the game. One of READY, RUNNING, PAUSE, LOSE, or WIN */
	//private int mMode;

	/** Indicate whether the surface has been created & is ready to draw */
	private boolean mRun = false;

	/** Scratch rect object. */
	//private RectF mScratchRect;

	/** Handle to the surface manager object we interact with */
	private SurfaceHolder mSurfaceHolder;

	private Point surfaceCenter;

	private Matrix translation; 
	
	private Camera mCamera;


	private float mYaw = 0f;
	// Set these to whatever you like 
	private float mRoll = 0f; 
	private float mPitch = 0f; 

	private PointF centeredOn;

	private LinkedList<NodeView> mNodes = new LinkedList<NodeView>();


	public GraphThread(SurfaceHolder surfaceHolder, Context context) {

		// Initialize paints for speedometer
		//mLinePaint = new Paint();
		//mLinePaint.setAntiAlias(true);
		///mLinePaint.setARGB(180, 255, 0, 255);

		// Temp Rectangle 
//		mScratchRect = new RectF(0, 0, 0, 0);

		// get handles to some important objects
		mSurfaceHolder = surfaceHolder;
		
		mContext = context;

		mMatrix = new Matrix();

		translation = new Matrix();
		// Member variables at top of class 

		mCamera = new Camera();

		// Setup the camera rotations 
		mCamera.rotateZ(-mYaw);
		mCamera.rotateY(-mRoll);
		mCamera.rotateX(-mPitch);

		centeredOn = new PointF();
		centeredOn.x = surfaceHolder.getSurfaceFrame().centerX();
		centeredOn.y = surfaceHolder.getSurfaceFrame().centerY();

		//mCamera.translate(0, 0, 0);
		//mCamera.translate(x, y, z)


		// Output the camera rotations to a matrix 
		mCamera.getMatrix(translation);

		Resources res = context.getResources();
		// load background image as a Bitmap instead of a Drawable b/c
		// we don't need to transform it and it's faster to draw this way
		mBackgroundImage = BitmapFactory.decodeResource(res,
				R.drawable.earthrise);
	}


	public void init(int startNode) {
		centeredOn.x = mSurfaceHolder.getSurfaceFrame().centerX();
		centeredOn.y = mSurfaceHolder.getSurfaceFrame().centerY();
		
		
		surfaceCenter = new Point();
		// set point to center of screen
		surfaceCenter.x =  mSurfaceHolder.getSurfaceFrame().centerX();
		surfaceCenter.y = mSurfaceHolder.getSurfaceFrame().centerY();
		
	    nodeFocus = startNode;
	    Node node = Graph.getNode(startNode);
		NodeView firstNode = new NodeView(node, mContext);

		firstNode.centerOn(surfaceCenter);
		mNodes.add(firstNode);
	}



	public void zoomOut() {
		//	mCamera.translate(0, 0, -100);
		//	zoomLevel -= 100;
		//	invalidate();
	}

	public void zoomIn(){
		//mCamera.translate(0, 0, 100);
		//zoomLevel += 100;
		//invalidate();
	}

	public void centerOn (float x, float y) {

		mCamera.translate(-(x - centeredOn.x), (y - centeredOn.y), 0);//-(newPosX - centeredOn.x),  -(newPosY - centeredOn.y), 0);
		//mCamera.translate(0, 0, 0);
		centeredOn.x = x;
		centeredOn.y = y;



	}



	/**
	 * Starts the game, setting parameters for the current difficulty.
	 */
	public void doStart() {
		synchronized (mSurfaceHolder) {
			//mLastTime = System.currentTimeMillis() + 100;
			//setState(STATE_RUNNING);
		}
	}

	/**
	 * Pauses the physics update & animation.
	 */
	public void pause() {
		synchronized (mSurfaceHolder) {
			//if (mMode == STATE_RUNNING) setState(STATE_PAUSE);
		}
	}

	/**
	 * Restores game state from the indicated Bundle. Typically called when
	 * the Activity is being restored after having been previously
	 * destroyed.
	 * 
	 * @param savedState Bundle containing the game state
	 */
	public synchronized void restoreState(Bundle savedState) {
		synchronized (mSurfaceHolder) {
			//      	Log.i(this.getClass().toString(), "Restoring State. Difficulty is " + savedState.getInt(KEY_DIFFICULTY));
			//setState(STATE_PAUSE);
		}
	}

	@Override
	public void run() {
		while (mRun) {
			Canvas canvas = null;
			try {
				canvas = mSurfaceHolder.lockCanvas(null);
				synchronized (mSurfaceHolder) {
					//mCamera.rotateY(1);
					mCamera.getMatrix(translation);
					canvas.concat(translation);
					doDraw(canvas);
				}
			} catch (Exception e) {
				Log.i("EXCEPTION","Exception is: " + e.toString());
				System.exit(1);
			} finally {
				// Do this in a finally so that if an exception is thrown
				// during the above, we don't leave the Surface in an
				// inconsistent state
				if (canvas != null) {
					mSurfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}

	/**
	 * Dump game state to the provided Bundle. Typically called when the
	 * Activity is being suspended.
	 * 
	 * @return Bundle with this view's state
	 */
	public Bundle saveState(Bundle map) {
		synchronized (mSurfaceHolder) {
			if (map != null) {

			}
		}
		return map;
	}

	/**
	 * Used to signal the thread whether it should be running or not.
	 * Passing true allows the thread to run; passing false will shut it
	 * down if it's already running. Calling start() after this was most
	 * recently called with false will result in an immediate shutdown.
	 * 
	 * @param b true to run, false to shut down
	 */
	public void setRunning(boolean b) {
		mRun = b;
	}

	/**
	 * Sets the game mode. That is, whether we are running, paused, in the
	 * failure state, in the victory state, etc.
	 * 
	 * @see #setState(int, CharSequence)
	 * @param mode one of the STATE_* constants
	 */
	public void setState(int mode) {
		synchronized (mSurfaceHolder) {
			setState(mode, null);
		}
	}

	/**
	 * Sets the game mode. That is, whether we are running, paused, in the
	 * failure state, in the victory state, etc.
	 * 
	 * @param mode one of the STATE_* constants
	 * @param message string to add to screen or null
	 */
	public void setState(int mode, CharSequence message) {
		/*
		 * This method optionally can cause a text message to be displayed
		 * to the user when the mode changes. Since the View that actually
		 * renders that text is part of the main View hierarchy and not
		 * owned by this thread, we can't touch the state of that View.
		 * Instead we use a Message + Handler to relay commands to the main
		 * thread, which updates the user-text View.
		 */
		synchronized (mSurfaceHolder) {
			/*mMode = mode;

			if (mMode == STATE_RUNNING) {
				Message msg = mHandler.obtainMessage();
				Bundle b = new Bundle();
				b.putString("text", "");
				b.putInt("viz", View.INVISIBLE);
				msg.setData(b);
				mHandler.sendMessage(msg);
			} else {
				Resources res = mContext.getResources();
				CharSequence str = "";
				//					if (mMode == STATE_READY)
				//						str = res.getText(R.string.mode_ready);
				//					else if (mMode == STATE_PAUSE)
				//						str = res.getText(R.string.mode_pause);
				//					else if (mMode == STATE_LOSE)
				//						str = res.getText(R.string.mode_lose);
				//					else if (mMode == STATE_WIN)
				//						str = res.getString(R.string.mode_win_prefix)
				//						+ mWinsInARow + " "
				//						+ res.getString(R.string.mode_win_suffix);

				if (message != null) {
					str = message + "\n" + str;
				}

				if (mMode == STATE_LOSE) mWinsInARow = 0;

				Message msg = mHandler.obtainMessage();
				Bundle b = new Bundle();
				b.putString("text", str.toString());
				b.putInt("viz", View.VISIBLE);
				msg.setData(b);
				mHandler.sendMessage(msg);
			}*/
		}
	}

	/* Callback invoked when the surface dimensions change. */
	public void setSurfaceSize(int width, int height) {
		// synchronized to make sure these all change atomically
		synchronized (mSurfaceHolder) {
			mCanvasWidth = width;
			mCanvasHeight = height;

			/*// don't forget to resize the background image
			mBackgroundImage = mBackgroundImage.createScaledBitmap(
					mBackgroundImage, width, height, true); 
			 */
		}
	}

	/**
	 * Resumes from a pause.
	 */
	public void unpause() {
		// Move the real time clock up to now
		synchronized (mSurfaceHolder) {
			//mLastTime = System.currentTimeMillis() + 100;
		}
		//setState(STATE_RUNNING);
	}

	/**
	 * Handles a key-down event.
	 * 
	 * @param keyCode the key that was pressed
	 * @param msg the original event object
	 * @return true
	 */
	boolean doKeyDown(int keyCode, KeyEvent msg) {
		synchronized (mSurfaceHolder) {

			if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
				zoomIn();
			}

			if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
				zoomOut();
			}

			/*if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
				//Log.i("THIS","DPAD CENTER MFS");
				// moveStuff();
			}
			boolean okStart = false;
			if (keyCode == KeyEvent.KEYCODE_DPAD_UP) okStart = true;
			if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) okStart = true;
			if (keyCode == KeyEvent.KEYCODE_S) okStart = true;

			boolean center = (keyCode == KeyEvent.KEYCODE_DPAD_UP);

			if (okStart
					&& (mMode == STATE_READY || mMode == STATE_LOSE || mMode == STATE_WIN)) {
				// ready-to-start -> start
				doStart();
				return true;
			} else if (mMode == STATE_PAUSE && okStart) {
                // paused -> running
                unpause();
                return true;
            } else if (mMode == STATE_RUNNING) {
                // center/space -> fire
                if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER
                        || keyCode == KeyEvent.KEYCODE_SPACE) {
                    setFiring(true);
                    return true;
                    // left/q -> left
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                        || keyCode == KeyEvent.KEYCODE_Q) {
                    mRotating = -1;
                    return true;
                    // right/w -> right
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
                        || keyCode == KeyEvent.KEYCODE_W) {
                    mRotating = 1;
                    return true;
                    // up -> pause
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    pause();
                    return true;
                }
            }*/
			return false;
			//return false;
		}

	}

	/**
	 * Handles a key-up event.
	 * 
	 * @param keyCode the key that was pressed
	 * @param msg the original event object
	 * @return true if the key was handled and consumed, or else false
	 */
	boolean doKeyUp(int keyCode, KeyEvent msg) {

		boolean handled = false;

		/*synchronized (mSurfaceHolder) {
			if (mMode == STATE_RUNNING) {
				     if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER
                        || keyCode == KeyEvent.KEYCODE_SPACE) {
                    setFiring(false);
                    handled = true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                        || keyCode == KeyEvent.KEYCODE_Q
                        || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
                        || keyCode == KeyEvent.KEYCODE_W) {
                    mRotating = 0;
                    handled = true;
                }
				 }
		}
		 */
		return handled;
	}

	/*
	 * Returns the id of the node hit by the current event, or -1 if none were hit.
	 */
	public int findHitNode(MotionEvent event) {
		int clickX = (int)event.getX(); 
		int clickY = (int)event.getY();

		int hitNode = -1;

		for (MapNode node : mNodes) {
			if (null != node && hitNode == -1) {
				if (node.isClicked(clickX, clickY)) {
					hitNode = node.getID();
					break;
				}
			}
		}
		return hitNode;
	}

	public boolean doSingleTapUp(MotionEvent event) {

		/*nodeFocus = findHitNode(event);

		if (nodeFocus >= 0) {	
			//Log.i("DRAGGING","Event" + eventNum + "nodeFocus"+ nodeFocus);
			if (nodeFocus >= 0 && mNodes[nodeFocus] != null) {
				//mNodes[nodeFocus].centerOn((int) event.getX(), (int) event.getY());
				mNodes[nodeFocus].changeMode(MapNode.MODE_DRAGGING);
				return true;
			} else {
				Log.e(this.getClass().toString(), "Trying to select invalid node: " + nodeFocus);
			}
		}*/



		return false;
	}

	public void doShowPress(MotionEvent event) {
		// TODO Auto-generated method stub

	}

	public boolean doScroll(MotionEvent event1, MotionEvent event2,
			float distanceX, float distanceY) {
		Log.i("doScroll","X: " + event1.getX() + "Y: " + event1.getY() + "distanceX: " + distanceX + "distanceY: " + distanceY);

		return false;
	}

	public void doLongPress(MotionEvent event) {

		// Ensure we don't interrupt a dragging event.
		/*if (nodeFocus >= 0) {	
			if (mNodes[nodeFocus].getMode() == MapNode.MODE_DRAGGING) {
				return;
			}
		}

		nodeFocus = findHitNode(event);

		if (nodeFocus >= 0) {	
			// center camera on node

			//get current center

			// calculate difference

			// do animated transform
		}*/



		/*int clickX = (int)event.getX(); 
		int clickY = (int)event.getY(); 

		boolean hitNode = false;
		for (MapNode node : mNodes) {
			if (null != node && !hitNode) {
				hitNode = node.isClicked(clickX, clickY);
				if (hitNode) {
					nodeFocus = node.getID();


					//Log.i("DRAGGING","Event" + eventNum + "nodeFocus"+ nodeFocus);
					if (nodeFocus >= 0 && mNodes[nodeFocus] != null) {
						mNodes[nodeFocus].centerOn(clickX, clickY);
						mNodes[nodeFocus].changeMode(MapNode.MODE_DRAGGING);
					} else {
						Log.e(this.getClass().toString(), "Trying to select invalid node: " + nodeFocus);
					}
				}
			}
		}

		if (!hitNode) {
			int position = MapNode.getCount();
			MapNode newNode = new MapNode(mContext);
			mNodes[position] = newNode;
			newNode.centerOn(clickX, clickY);
			nodeFocus = newNode.getID();
			newNode.changeMode(MapNode.MODE_DRAGGING);
		}
		 */
	}

	public boolean doFling(MotionEvent event1, MotionEvent event2, float velocityX,
			float velocityY) {

		/* nodeFocus = findHitNode(event);

		if (nodeFocus >= 0) {	

		}
		 */
		// TODO Auto-generated method stub
		return false;
	}

	public boolean doDown(MotionEvent event) {
		int clickX = (int)event.getX(); 
		int clickY = (int)event.getY(); 

		nodeFocus = findHitNode(event);

		if (nodeFocus >= 0) {
			mNodes[nodeFocus].centerOn(clickX, clickY);
			mNodes[nodeFocus].changeMode(MapNode.MODE_DRAGGING);
		} else { 
			int position = MapNode.getCount();
			MapNode newNode = new MapNode(mContext);
			mNodes[position] = newNode;
			newNode.centerOn(clickX, clickY);
			nodeFocus = newNode.getID();
			newNode.changeMode(MapNode.MODE_DRAGGING);
		}
		return true;
	}
	
	public MotionEvent translateEvent(MotionEvent event) {
		if (translation != null) {
			/* 
			 * Translate our touch events to the translation of the canvas.
			 */
			/*float posX = event.getX();
			float[] srcPoints;
			float[] destPoints;
			srcPoints = new float[2];
			destPoints = new float[2];
			                       
			srcPoints[0] = event.getX();
			srcPoints[1] = event.getY();
			Matrix inverse = new Matrix();
			translation.invert(inverse);
			inverse.mapPoints(destPoints, srcPoints);
			event.setLocation(destPoints[0], destPoints[1]);
			*/
		}
		return event;
	}
	
	// events when touching the screen
	public boolean doTouchEvent(MotionEvent event) {
		
		
		try {
			eventNum++;
			int eventaction = event.getAction(); 

			int clickX = (int)event.getX(); 
			int clickY = (int)event.getY(); 


			//gestureDetector = new TrackballGestureDetector();

			switch (eventaction ) { 

			case MotionEvent.ACTION_DOWN: // touch down so check if the finger is on a ball
				/*	boolean hitNode = false;
				for (MapNode node : mNodes) {
					if (null != node && !hitNode) {
						hitNode = node.isClicked(clickX, clickY);
						if (hitNode) {
							nodeFocus = node.getID();
							//Log.i("DRAGGING","Event" + eventNum + "nodeFocus"+ nodeFocus);
							if (nodeFocus >= 0 && mNodes[nodeFocus] != null) {
								mNodes[nodeFocus].centerOn(clickX, clickY);
								mNodes[nodeFocus].changeMode(MapNode.MODE_DRAGGING);
							} else {
								Log.e(this.getClass().toString(), "Trying to select invalid node: " + nodeFocus);
							}
						}
					}
				}

				if (!hitNode) {
					int position = MapNode.getCount();
					MapNode newNode = new MapNode(mContext);
					mNodes[position] = newNode;
					newNode.centerOn(clickX, clickY);
					nodeFocus = newNode.getID();
					newNode.changeMode(MapNode.MODE_DRAGGING);

				}

				break; 
				 */
				return false;
				/* 
				 * Only use this when we are dragging, otherwise let the gesture detector handle it.
				 * 
				 */
			case MotionEvent.ACTION_MOVE:   // touch drag with the ball
				if (nodeFocus >= 0 && mNodes[nodeFocus] != null) {
					if (mNodes[nodeFocus].getMode() == MapNode.MODE_DRAGGING) {
						centerOn(event.getX(), event.getY());
						mNodes[nodeFocus].centerOn(clickX, clickY);

						//invalidate();
						return true;
					} else {
						return false;
					}				
				}

				break; 

			case MotionEvent.ACTION_UP: 
				// touch drop - just do things here after dropping

				if (nodeFocus >= 0 && mNodes[nodeFocus] != null) {
					mNodes[nodeFocus].changeMode(MapNode.MODE_IDLE);
					nodeFocus = -1;
				}
				//invalidate();
				return true; 
			} 

			// redraw the canvas

		} catch (Exception e) {
			Log.e(this.toString(), e.toString());

		}
		return false; 
	}

	/**
	 * Draws the ship, fuel/speed bars, and background to the provided
	 * Canvas.
	 * @param matrix 
	 */
	private void doDraw(Canvas canvas) {

		// Draw the background image. Operations on the Canvas accumulate
		// so this is like clearing the screen.
		//canvas.drawBitmap(mBackgroundImage, 0, 0, null);
		canvas.drawARGB(255, 80, 80, 180);
		mScratchRect.set(0, 0, 50, 50);
		if (flash > 0) {
			canvas.drawRect(mScratchRect, mLinePaint);
			flash--;

		}


		/*         int yTop = mCanvasHeight - ((int) mY + mLanderHeight / 2);
        int xLeft = (int) mX - mLanderWidth / 2;
		 */


		// Draw the fuel gauge
		/* int fuelWidth = (int) (UI_BAR * mFuel / PHYS_FUEL_MAX);*/

		//mScratchRect.set
		//mScratchRect.
		//   mScratchRect.set//4 + fuelWidth, 4, 4 + UI_BAR_HEIGHT, 4);


		//testNode.getNode().draw(canvas);
		MapNode previousNode;
		MapNode node;
		for (int i = mNodes.length - 1; i >= 0; i--) {
			node = mNodes[i];
			if (null != node) {
				node.draw(canvas);
			}
		}
		/*
        // Draw the speed gauge, with a two-tone effect
        double speed = Math.sqrt(mDX * mDX + mDY * mDY);
        int speedWidth = (int) (UI_BAR * speed / PHYS_SPEED_MAX);

        if (speed <= mGoalSpeed) {
            mScratchRect.set(4 + UI_BAR + 4, 4,
                    4 + UI_BAR + 4 + speedWidth, 4 + UI_BAR_HEIGHT);
            canvas.drawRect(mScratchRect, mLinePaint);
        } else {
            // Draw the bad color in back, with the good color in front of
            // it
            mScratchRect.set(4 + UI_BAR + 4, 4,
                    4 + UI_BAR + 4 + speedWidth, 4 + UI_BAR_HEIGHT);
            canvas.drawRect(mScratchRect, mLinePaintBad);
            int goalWidth = (UI_BAR * mGoalSpeed / PHYS_SPEED_MAX);
            mScratchRect.set(4 + UI_BAR + 4, 4, 4 + UI_BAR + 4 + goalWidth,
                    4 + UI_BAR_HEIGHT);
            canvas.drawRect(mScratchRect, mLinePaint);
        }

        // Draw the landing pad
        canvas.drawLine(mGoalX, 1 + mCanvasHeight - TARGET_PAD_HEIGHT,
                mGoalX + mGoalWidth, 1 + mCanvasHeight - TARGET_PAD_HEIGHT,
                mLinePaint);


        // Draw the ship with its current rotation
        canvas.save();
        canvas.rotate((float) mHeading, (float) mX, mCanvasHeight
                - (float) mY);
        if (mMode == STATE_LOSE) {
            mCrashedImage.setBounds(xLeft, yTop, xLeft + mLanderWidth, yTop
                    + mLanderHeight);
            mCrashedImage.draw(canvas);
        } else if (mEngineFiring) {
            mFiringImage.setBounds(xLeft, yTop, xLeft + mLanderWidth, yTop
                    + mLanderHeight);
            mFiringImage.draw(canvas);
        } else {
            mLanderImage.setBounds(xLeft, yTop, xLeft + mLanderWidth, yTop
                    + mLanderHeight);
            mLanderImage.draw(canvas);
        }
        canvas.restore(); */
	}

	/**
	 * Figures the lander state (x, y, fuel, ...) based on the passage of
	 * realtime. Does not invalidate(). Called at the start of draw().
	 * Detects the end-of-game and sets the UI to the next state.
	 */
	private void updatePhysics() {
		/*double friction = 0.8;
		MapNode node;
		for (int i = mNodes.length - 1; i >= 0; i--) {
			node = mNodes[i];

			node.setVelY(node.getVelY() * friction);
			if (node.getVelX() > 0) {
				node.setVelX(node.getVelX() * friction);	

			}
			if (node.getVelY() > 0) {	
				node.setVelY(node.getVelY() * friction);
				node.setX((int) (node.getX() + node.getVelX()));
				node.setY((int) (node.getY() + node.getVelY()));
			}
		}*/

		/* long now = System.currentTimeMillis();

        // Do nothing if mLastTime is in the future.
        // This allows the game-start to delay the start of the physics
        // by 100ms or whatever.
        if (mLastTime > now) return;

        double elapsed = (now - mLastTime) / 1000.0;

        // mRotating -- update heading
        if (mRotating != 0) {
            mHeading += mRotating * (PHYS_SLEW_SEC * elapsed);

            // Bring things back into the range 0..360
            if (mHeading < 0)
                mHeading += 360;
            else if (mHeading >= 360) mHeading -= 360;
        }

        // Base accelerations -- 0 for x, gravity for y
        double ddx = 0.0;
        double ddy = -PHYS_DOWN_ACCEL_SEC * elapsed;

        if (mEngineFiring) {
            // taking 0 as up, 90 as to the right
            // cos(deg) is ddy component, sin(deg) is ddx component
            double elapsedFiring = elapsed;
            double fuelUsed = elapsedFiring * PHYS_FUEL_SEC;

            // tricky case where we run out of fuel partway through the
            // elapsed
            if (fuelUsed > mFuel) {
                elapsedFiring = mFuel / fuelUsed * elapsed;
                fuelUsed = mFuel;

                // Oddball case where we adjust the "control" from here
                mEngineFiring = false;
            }

            mFuel -= fuelUsed;

            // have this much acceleration from the engine
            double accel = PHYS_FIRE_ACCEL_SEC * elapsedFiring;

            double radians = 2 * Math.PI * mHeading / 360;
            ddx = Math.sin(radians) * accel;
            ddy += Math.cos(radians) * accel;
        }

        double dxOld = mDX;
        double dyOld = mDY;

        // figure speeds for the end of the period
        mDX += ddx;
        mDY += ddy;

        // figure position based on average speed during the period
        mX += elapsed * (mDX + dxOld) / 2;
        mY += elapsed * (mDY + dyOld) / 2;

        mLastTime = now;

        // Evaluate if we have landed ... stop the game
        double yLowerBound = TARGET_PAD_HEIGHT + mLanderHeight / 2
                - TARGET_BOTTOM_PADDING;
        if (mY <= yLowerBound) {
            mY = yLowerBound;

            int result = STATE_LOSE;
            CharSequence message = "";
		 */
		//Resources res = mContext.getResources();
		/*double speed = Math.sqrt(mDX * mDX + mDY * mDY);
            boolean onGoal = (mGoalX <= mX - mLanderWidth / 2 && mX
                    + mLanderWidth / 2 <= mGoalX + mGoalWidth);

            // "Hyperspace" win -- upside down, going fast,
            // puts you back at the top.
            if (onGoal && Math.abs(mHeading - 180) < mGoalAngle
                    && speed > PHYS_SPEED_HYPERSPACE) {
                result = STATE_WIN;
                mWinsInARow++;
                doStart();

                return;
                // Oddball case: this case does a return, all other cases
                // fall through to setMode() below.
            } else if (!onGoal) {
                message = res.getText(R.string.message_off_pad);
            } else if (!(mHeading <= mGoalAngle || mHeading >= 360 - mGoalAngle)) {
                message = res.getText(R.string.message_bad_angle);
            } else if (speed > mGoalSpeed) {
                message = res.getText(R.string.message_too_fast);
            } else {
                result = STATE_WIN;
                mWinsInARow++;
            }

            setState(result, message);
        } */
	}

	public boolean doTouchMotionEvent(MotionEvent e1, MotionEvent e2,
			float velocityX, float velocityY) {

		int eventAction = e1.getAction();

		Log.i("doTouchMotionEvent", "Action is: " + eventAction);

		//switch eventAction;

		return true;
	} 
}