/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.candlelightproject.lifemap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.util.Log;
import android.graphics.Point;
import android.view.MotionEvent;
import com.candlelightproject.lifemap.MapNode;

/**
 * View that draws, takes keystrokes, etc. for a simple LunarLander game.
 * 
 * Has a mode which RUNNING, PAUSED, etc. Has a x, y, dx, dy, ... capturing the
 * current ship physics. All x/y etc. are measured with (0,0) at the lower left.
 * updatePhysics() advances the physics based on realtime. draw() renders the
 * ship, and does an invalidate() to prompt another draw() as soon as possible
 * by the system.
 */



class MapView extends SurfaceView implements SurfaceHolder.Callback {

	class MapThread extends Thread {
		/*
		 * State-tracking constants
		 */
		public static final int STATE_LOSE = 1;
		public static final int STATE_PAUSE = 2;
		public static final int STATE_READY = 3;
		public static final int STATE_RUNNING = 4;
		public static final int STATE_WIN = 5;

		private int flash = 0;
		private int eventNum = 0;
		private int movingNode = -1;
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

		/** Message handler used by thread to interact with TextView */
		private Handler mHandler;

		/** Used to figure out elapsed time between frames */
		private long mLastTime;

		/** Paint to draw the lines on screen. */
		private Paint mLinePaint;

		/** The state of the game. One of READY, RUNNING, PAUSE, LOSE, or WIN */
		private int mMode;

		/** Indicate whether the surface has been created & is ready to draw */
		private boolean mRun = false;

		/** Scratch rect object. */
		private RectF mScratchRect;

		/** Handle to the surface manager object we interact with */
		private SurfaceHolder mSurfaceHolder;

		/** Number of wins in a row. */
		private int mWinsInARow;

		private Point surfaceCenter;


		private MapNode[] mNodes = new MapNode[10]; 


		public MapThread(SurfaceHolder surfaceHolder, Context context,
				Handler handler) {


			// Initialize paints for speedometer
			mLinePaint = new Paint();
			mLinePaint.setAntiAlias(true);
			mLinePaint.setARGB(180, 255, 0, 255);

			// Temp Rectangle 
			mScratchRect = new RectF(0, 0, 0, 0);

			// get handles to some important objects
			mSurfaceHolder = surfaceHolder;
			mHandler = handler;
			mContext = context;



			Resources res = context.getResources();
			// load background image as a Bitmap instead of a Drawable b/c
			// we don't need to transform it and it's faster to draw this way
			mBackgroundImage = BitmapFactory.decodeResource(res,
					R.drawable.earthrise);
		}

		public void init(Context context) {
			surfaceCenter = new Point();
			// set point to center of screen
			surfaceCenter.x =  mSurfaceHolder.getSurfaceFrame().centerX();

			surfaceCenter.y = mSurfaceHolder.getSurfaceFrame().centerY();
			Log.i("Center","x: " + surfaceCenter.x);
			Log.i("Center","y: " + surfaceCenter.y);
			MapNode firstNode = new MapNode(context);

			firstNode.centerOn(surfaceCenter);
			mNodes[0] = firstNode;
		}

		/**
		 * Starts the game, setting parameters for the current difficulty.
		 */
		public void doStart() {
			synchronized (mSurfaceHolder) {
				mLastTime = System.currentTimeMillis() + 100;
				setState(STATE_RUNNING);


			}
		}

		/**
		 * Pauses the physics update & animation.
		 */
		public void pause() {
			synchronized (mSurfaceHolder) {
				if (mMode == STATE_RUNNING) setState(STATE_PAUSE);
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
				setState(STATE_PAUSE);
			}
		}

		@Override
		public void run() {
			while (mRun) {
				Canvas c = null;
				try {
					c = mSurfaceHolder.lockCanvas(null);
					synchronized (mSurfaceHolder) {
						//            if (mMode == STATE_RUNNING) {
						doDraw(c);
						//         }
					}
				} catch (Exception e) {
					Log.i("EXCEPTION","Exception is: " + e.toString());
					//Log.i("EXCEPTION","Stacktrace: \n"+ e.getStackTrace().toString()); 
					System.exit(1);
				} finally {
					// do this in a finally so that if an exception is thrown
					// during the above, we don't leave the Surface in an
					// inconsistent state
					if (c != null) {
						mSurfaceHolder.unlockCanvasAndPost(c);
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
		 * Sets the current difficulty.
		 * 
		 * @param difficulty
		 */
		public void setDifficulty(int difficulty) {
			/* Log.i(this.getClass().toString(), "Setting Difficulty to : " + difficulty);
        	synchronized (mSurfaceHolder) {
                mDifficulty = difficulty;
            }*/
		}

		/*  public int getDifficulty() {
            synchronized (mSurfaceHolder) {
                return mDifficulty;
            }
        }
		 */        
		/**
		 * Sets if the engine is currently firing.
		 */
		public void setFiring(boolean firing) {
			/*  synchronized (mSurfaceHolder) {
                mEngineFiring = firing;
            } */
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
				mMode = mode;

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
				}
			}
		}

		/* Callback invoked when the surface dimensions change. */
		public void setSurfaceSize(int width, int height) {
			// synchronized to make sure these all change atomically
			synchronized (mSurfaceHolder) {
				mCanvasWidth = width;
				mCanvasHeight = height;

				// don't forget to resize the background image
				mBackgroundImage = mBackgroundImage.createScaledBitmap(
						mBackgroundImage, width, height, true); 
			}
		}

		/**
		 * Resumes from a pause.
		 */
		public void unpause() {
			// Move the real time clock up to now
			synchronized (mSurfaceHolder) {
				mLastTime = System.currentTimeMillis() + 100;
			}
			setState(STATE_RUNNING);
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
				if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
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
				} /*else if (mMode == STATE_PAUSE && okStart) {
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
				//Log.i("MotionEvent","Event" + eventNum + " ACTION_DOWN x: " + clickX + " y: " + clickY);
				boolean hitNode = false;
				for (MapNode node : mNodes) {
					if (null != node && !hitNode) {
				//		Log.i("not null", "node: " + node);
						//node.centerOn(point);
						
						hitNode = node.isClicked(clickX, clickY);
						if (hitNode) {
						//	Log.i("CLICKED", "Event" + eventNum + "node: " + node);
							movingNode = node.getID();
							//Log.i("DRAGGING","Event" + eventNum + "movingNode"+ movingNode);
							if (movingNode >= 0 && mNodes[movingNode] != null) {
								mNodes[movingNode].centerOn(clickX, clickY);
								mNodes[movingNode].changeMode(MapNode.MODE_DRAGGING);
							} else {
								Log.e(this.getClass().toString(), "Trying to select invalid node: " + movingNode);
							}
						}
					}
				}
				
				if (!hitNode) {
					int position = MapNode.getCount();
					MapNode newNode = new MapNode(mContext);
					mNodes[position] = newNode;
					newNode.centerOn(clickX, clickY);
					movingNode = newNode.getID();
					newNode.changeMode(MapNode.MODE_DRAGGING);
					
				}
				
				/* 	balID = 0;
            	for (ColorBall ball : colorballs) {
            		// check if inside the bounds of the ball (circle)
            		// get the center for the ball
            		int centerX = ball.getX() + 25;
            		int centerY = ball.getY() + 25;

            		// calculate the radius from the touch to the center of the ball
            		double radCircle  = Math.sqrt( (double) (((centerX-X)*(centerX-X)) + (centerY-Y)*(centerY-Y)));

            		// if the radius is smaller then 23 (radius of a ball is 22), then it must be on the ball
            		if (radCircle < 23){
            			balID = ball.getID();
                        break;
            		}

            		// check all the bounds of the ball (square)
            		//if (X > ball.getX() && X < ball.getX()+50 && Y > ball.getY() && Y < ball.getY()+50){
                    //	balID = ball.getID();
                    //	break;
                    //}
                  }
				 */    
				break; 


			case MotionEvent.ACTION_MOVE:   // touch drag with the ball
				Log.i("MotionEvent","Event" + eventNum + "ACTION_MOVE x: " + clickX + " y: " + clickY + "movingNode:" + movingNode);
				if (movingNode >= 0 && mNodes[movingNode] != null) {
					Log.i("MovingEvent","Event" + eventNum + "movingNode: "+ movingNode);
					mNodes[movingNode].centerOn(clickX, clickY);
					/*mNodes[movingNode].setX(clickX);
					mNodes[movingNode].setY(clickY);*/
				}
				// move the balls the same as the finger
				/*      if (balID > 0) {
                	colorballs[balID-1].setX(X-25);
                	colorballs[balID-1].setY(Y-25);
                }
				 */	
				break; 

			case MotionEvent.ACTION_UP: 
				Log.i("MotionEvent","Event" + eventNum + "ACTION_UP x: " + clickX + " y: " + clickY);
				if (movingNode >= 0 && mNodes[movingNode] != null) {
					mNodes[movingNode].changeMode(MapNode.MODE_IDLE);
					movingNode = -1;
				}
				// touch drop - just do things here after dropping

				break; 
			} 
			// redraw the canvas
			invalidate(); 
			} catch (Exception e) {
				Log.e(this.toString(), e.toString());
				
			}
			return true; 
		}

		/**
		 * Draws the ship, fuel/speed bars, and background to the provided
		 * Canvas.
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
				/*previousNode = node;
				if (previousNode != null) {
					node.lineTo(previousNode);
				}*/
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
			Resources res = mContext.getResources();
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
	}

	/** Handle to the application context, used to e.g. fetch Drawables. */
	private Context mContext;

	/** Pointer to the text view to display "Paused.." etc. */
	private TextView mStatusText;

	/** The thread that actually draws the animation */
	private MapThread thread;

	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// register our interest in hearing about changes to our surface
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		// create thread only; it's started in surfaceCreated()
		thread = new MapThread(holder, context, new Handler() {
			@Override
			public void handleMessage(Message m) {
				mStatusText.setVisibility(m.getData().getInt("viz"));
				mStatusText.setText(m.getData().getString("text"));
			}
		});

		setFocusable(true); // make sure we get key events
	}

	/**
	 * Fetches the animation thread corresponding to this LunarView.
	 * 
	 * @return the animation thread
	 */
	public MapThread getThread() {
		return thread;
	}

	/**
	 * Standard override to get key-press events.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent msg) {
		return thread.doKeyDown(keyCode, msg);
	}

	/**
	 * Standard override for key-up. We actually care about these, so we can
	 * turn off the engine or stop rotating.
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent msg) {
		return thread.doKeyUp(keyCode, msg);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return thread.doTouchEvent(event);
	}


	/**
	 * Standard window-focus override. Notice focus lost so we can pause on
	 * focus lost. e.g. user switches to take a call.
	 */
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		if (!hasWindowFocus) thread.pause();
	}

	/**
	 * Installs a pointer to the text view used for messages.
	 */
	public void setTextView(TextView textView) {
		mStatusText = textView;
	}

	/* Callback invoked when the surface dimensions change. */
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		thread.setSurfaceSize(width, height);
	}

	/*
	 * Callback invoked when the Surface has been created and is ready to be
	 * used.
	 */
	public void surfaceCreated(SurfaceHolder holder) {
		// start the thread here so that we don't busy-wait in run()
		// waiting for the surface to be created
		thread.setRunning(true);
		thread.init(mContext);
		thread.start();
	}

	/*
	 * Callback invoked when the Surface has been destroyed and must no longer
	 * be touched. WARNING: after this method returns, the Surface/Canvas must
	 * never be touched again!
	 */
	public void surfaceDestroyed(SurfaceHolder holder) {
		// we have to tell thread to shut down & wait for it to finish, or else
		// it might touch the Surface after we return and explode
		boolean retry = true;
		thread.setRunning(false);
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}
}
