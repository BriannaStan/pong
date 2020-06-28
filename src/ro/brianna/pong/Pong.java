package ro.brianna.pong;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;


public class Pong extends JComponent implements ActionListener, MouseMotionListener, KeyListener {

    //windows dimensions - played a little because the drawable area is smaller than this
    //those are static since are used in main (before Pong class is instantiated)
    private static int dimX = 1024;
    private static int dimY = 602;

    //drawable area dimension
    public Dimension drawableArea;

    //ball position - at all times these variables contain ball position
    private int ballPosX = 0;
    private int ballPosY = 0;

    //ball size - parameter for ballsize to play with different sizes
    private int ballSize = 10;

    //ball speeds - these variables contain speed on X and Y axes for each step
    private int ballSpeedX = 3;
    private int ballSpeedY = 5;

    //paddle initial position and dimensions
    private int paddlePosX = 0;
    private int paddleHeight = 20;
    private int paddleWidth = 100;
    private int paddleKeyStep = 10; //how much should move paddle on key press

    //hits in a row
    public int hits = 0;
    //highest score
    public int highest = 0;

    public static void main(String[] args) {
        //This is the game frame (window)
        JFrame window = new JFrame("Pong");
        //Instantiating game
        Pong game = new Pong();
        //settings window size
        window.setPreferredSize(new Dimension(dimX, dimY));
        //set window bounds
        window.setBounds(0,0, dimX, dimY);
        //window should not be resizable since it may get awkward dimensions
        window.setResizable(false);
        //when x icon is pressed it will exit
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //listen for mouse events
        window.addMouseMotionListener(game);
        //listen for key events
        window.addKeyListener(game);
        //set game logic to window
        window.add(game);
        //display windows using above settings
        window.pack();
        //make gamewindow sure it's visible
        window.setVisible(true);
        //obtain drawable area (the area where we can draw elements (not including title bars, borders etc)
        game.drawableArea = game.getSize();

        //base on time run actionPerformed every 16ms 62.5 frames/s
        //to get good results this should be around refresh rate of monitor
        Timer t = new Timer(16, game);
        t.start();

    }

    //this sets dimension of game window
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(dimX, dimY);
    }

    //Redraw game window - this should be improved to draw only what changed, it will be more complex but much faster
    //this function needs special attention since it will be called very often so it's execution time should be as short as possible
    // so it will not slow down the ball
    @Override
    protected void paintComponent(Graphics graphics) {

        //draw background (this will also delete everything that was before)
        graphics.setColor(Color.PINK);
        graphics.fillRect(0, 0, drawableArea.width, drawableArea.height);

        //draw the paddle at the bottom of the window based on position obtained from mouse events
        graphics.setColor(Color.BLUE);
        graphics.fillRect(paddlePosX-paddleWidth/2, drawableArea.height-paddleHeight, paddleWidth, paddleHeight);

        //draw the ball at it's position
        graphics.setColor(Color.RED);
        graphics.fillOval(ballPosX, ballPosY, ballSize, ballSize);

        //hits - how many times ball was returned by paddle
        graphics.setFont(new Font("Arial", 8, 12));
        graphics.setColor(Color.BLACK);
        graphics.drawString("hits: "+String.valueOf(hits), 10, 30);

        //display highest score - best number of hits in a row
        graphics.setFont(new Font("Arial", 8, 12));
        graphics.setColor(Color.black);
        graphics.drawString("highest: "+String.valueOf(highest), 800, 30);

    }

    //this function is called from timer so basically each time it will adjust the ball position, paddle score
    //calling this periodically ensures a steady speed of the ball
    @Override
    public void actionPerformed(ActionEvent e) {

        //ball moves with X and Y speeds each time
        ballPosX = ballPosX + ballSpeedX;
        ballPosY = ballPosY + ballSpeedY;

        // ball hits the paddle - reverse Y speed
        if (ballPosX >= paddlePosX-paddleWidth/2 && ballPosX <= paddlePosX + paddleWidth/2 && ballPosY >= drawableArea.height-paddleHeight-ballSize && ballSpeedY>0) {
            ballSpeedY = -ballSpeedY; // Y speed gets reversed
            hits++;
            if(hits>=highest)
                highest = hits;
        }

        //ball missed paddle - restart
        if (ballPosY >= drawableArea.height-ballSize && ballSpeedY>0) {
            hits = 0;
            ballPosY = 1;
            ballPosX = 1;
        }

        // balls reached window top margin - reverse Y speed
        if (ballPosY <= 0 && ballSpeedY<0) {
            ballSpeedY = -ballSpeedY;
        }

        // ball hits window right margin - reverse X speed
        if (ballPosX >= drawableArea.width-ballSize && ballSpeedX>0) {
            ballSpeedX = -ballSpeedX;
        }

        // Window left - reverse the X speed
        if (ballPosX <= 0 && ballSpeedX<0) {
            ballSpeedX = -ballSpeedX;
        }

        //redraw all elements
        repaint();

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //get the mouse X position in window so we can place the paddle
        paddlePosX = e.getX();
        //paddle position changed so we need to repaint
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    // this is called each time a key is pressed
    @Override
    public void keyPressed(KeyEvent event) {
        switch(event.getKeyCode()){
            //check of left arrow was pressed
            case KeyEvent.VK_LEFT:
                //left pressed so we adjust paddle position with set step
                paddlePosX -= paddleKeyStep;
                //in case we try to go out of screen we forbidden
                if (paddlePosX<0 ) {
                    paddlePosX = 0;
                }
                //paddle position changed so we need to repaint
                repaint();
                break;
            //check if right arrow was pressed
            case KeyEvent.VK_RIGHT:
                //right pressed so we adjust paddle position with step
                paddlePosX += paddleKeyStep;
                //in case we try to go out of screen we forbidden
                if (paddlePosX>drawableArea.width ) {
                    paddlePosX = drawableArea.width;
                }
                //paddle position changed so we need to repaint
                repaint();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}