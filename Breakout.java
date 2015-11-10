/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

/** Width and height of application window in pixels.  On some platforms 
  * these may NOT actually be the dimensions of the graphics canvas. */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

/** Dimensions of game board.  On some platforms these may NOT actually
  * be the dimensions of the graphics canvas. */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

/** Separation between bricks */
	private static final int BRICK_SEP = 4;

/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

/** Number of turns */
	private static final int NTURNS = 3;

/** The Paddle */
	private static GRect paddle;

/** The position of the paddle */
	private static double paddle_x;
	private static double paddle_y;

/** The Ball */
	private static GOval ball;
	
/** The speed of the ball */
	private static double speed_x;
	private static double speed_y;

	/** The position of the ball */
	private static double ball_x;
	private static double ball_y;	

/** Private instance variables */
	private RandomGenerator rgen = RandomGenerator.getInstance();

/** The Life Label */
	private static GLabel life_label;
	
/** lives*/
	private static int life = 3;

/* Method: run() */
/** Runs the Breakout program. */
	public void run() {
		/* You fill this in, along with any subsidiary methods */		
		int status = 2;
		setUp();
		while(life >= 1 && status == 2){
			if(life < 3){
				life_label.setLabel("Life: "+life);
				this.remove(paddle);
				this.remove(ball);
				createPaddle();
				createBall();
			}
			status = play();
			life--;
		}
		if(status == 1){
			GLabel win = new GLabel("you win!",WIDTH/4,HEIGHT/2);		
			add(win);
		}
		else{
			life_label.setLabel("Life: 0");
			GLabel lose = new GLabel("you lose!",WIDTH/4,HEIGHT/2);
			add(lose);
		}
	}
	
	private void setUp(){
		setSize(WIDTH, HEIGHT);
		initLifeLabel();
		//create bricks
		for(int row = 0 ; row < NBRICK_ROWS ; row++)
			drawBricksOneRow(row);
		createPaddle();
		createBall();
	}
	private void initLifeLabel(){
		life_label = new GLabel("Life: "+life,0,50);
		life_label.setColor(Color.RED);
		add(life_label);
	}
	
	private void drawBricksOneRow(int rowNum){
		Color brickColor = getBrickColor(rowNum);
		int y = BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * rowNum;
		for(int count = 0 ; count < NBRICKS_PER_ROW ; count++){
			int x = (BRICK_WIDTH + BRICK_SEP) * count;
			drawBrick(x , y , brickColor);
		}
	}
	
	private Color getBrickColor(int rowNum){
		int colorNum = rowNum % 10;
        if(colorNum < 2)
        	return Color.RED;
        else if(colorNum < 4)
        	return Color.ORANGE;
        else if(colorNum < 6)
        	return Color.YELLOW;
        else if(colorNum < 8)
        	return Color.GREEN;
        else
        	return Color.CYAN;
	}
	
	private void drawBrick(int x , int y , Color brickColor){
		GRect brick = new GRect(x , y , BRICK_WIDTH , BRICK_HEIGHT);
		brick.setFilled(true);
		brick.setFillColor(brickColor);
		add(brick);
	}
	
	private void createPaddle(){
		paddle_x = (WIDTH - PADDLE_WIDTH) / 2;
		paddle_y = HEIGHT - PADDLE_Y_OFFSET;
		paddle = new GRect(paddle_x , paddle_y , PADDLE_WIDTH , PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setFillColor(Color.BLACK);
		add(paddle);
		addMouseListeners();
	}
	
	public void mouseMoved(MouseEvent e){
		int x = e.getX();
		if(x + PADDLE_WIDTH >= WIDTH)
			paddle_x = WIDTH - PADDLE_WIDTH;
		else
			paddle_x = x;
		paddle_y = HEIGHT - PADDLE_Y_OFFSET;
		paddle.setLocation(paddle_x, paddle_y);
	}
	
	private void createBall(){
		ball_x = WIDTH / 2 - BALL_RADIUS;
		ball_y = HEIGHT - PADDLE_Y_OFFSET - BALL_RADIUS * 2;	
		ball = new GOval(ball_x, ball_y, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		ball.setFillColor(Color.BLUE);
		add(ball);
		
	}
	
	private void initSpeed(){
		speed_x = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) 
			speed_x = -speed_x;
		speed_y = -1.5; 
	}
	
	/**return 1 if you win the game,else return 2*/
	private int play() {
		initSpeed();
		while(!isLost()){
			pause(10);
			setNextPosition();
			ball.setLocation(ball_x, ball_y);
			GObject gb = getCollidingObject();
			if(gb == paddle)
				changeSpeedY();
			else if(gb == life_label);
			else if(gb == null){
				if(touchRightBound() || touchLeftBound())
					changeSpeedX();
				if(touchTopBound())
					changeSpeedY();
			}
			else{
				this.remove(gb);
				changeSpeedY();
			}
			if(isWinner())
				return 1;
		}
		return 2;
	}
	
	private void setNextPosition(){
		ball_x += speed_x;
		ball_y += speed_y;
		if (ball_x < 0)
			ball_x = 0;
		else if(ball_x + BALL_RADIUS * 2 > WIDTH)
			ball_x = WIDTH - BALL_RADIUS * 2;
		if(ball_y < 0)
			ball_y = 0;
		else if(ball_y + BALL_RADIUS * 2 > HEIGHT)
			ball_y = HEIGHT - BALL_RADIUS * 2;
	}
	
	private boolean touchRightBound(){
		return ball_x + BALL_RADIUS * 2 == WIDTH;
	}
	
	private boolean touchLeftBound(){
		return ball_x == 0;
	}
	
	private boolean touchTopBound(){
		return ball_y == 0; 
	}
	
	private GObject getCollidingObject(){
		//left top
		double x = ball_x;
		double y = ball_y;
		
		GObject gb;
		gb = getElementAt(x,y);		
		if(gb == null){
			//right top
			x = ball_x + BALL_RADIUS * 2;
			gb = getElementAt(x,y);
		}		
		if(gb == null){
			//right bottom
			y = ball_y + BALL_RADIUS * 2;
			gb = getElementAt(x,y);
		}
		if(gb == null){
			//left bottom
			x = ball_x - BALL_RADIUS * 2;
			gb = getElementAt(x,y);
		}
		return gb;
	}
	
	private boolean isLost(){
		return isWinner() || ball_y + BALL_RADIUS * 2 > HEIGHT - PADDLE_Y_OFFSET;
	}
	
	private boolean isWinner(){
		return getElementCount() <= 3;
	}
	
	private void changeSpeedX(){
		speed_x = 0 - speed_x;
	}
	
	private void changeSpeedY(){
		speed_y = 0 - speed_y;
	}
	
	
	
	

}
