package week5;

import java.awt.*;

public class Sprite2D {

    // member data
    protected double x,y;
    protected Image myImage;
    protected Image myImage2;
    protected boolean isAlien;
    protected int framesDrawn = 0;

    // static member data
    protected static int winWidth;

    // constructor
    public Sprite2D(Image i) {
        myImage = i;
        isAlien = false;
    }

    public Sprite2D(Image i, Image j){//the aliens need 2 images
        this(i);
        myImage2 = j;
        isAlien = true;
    }

    public void setPosition(double xx, double yy) {
        x=xx;
        y=yy;
    }

    public void paint(Graphics g) {
        if (!isAlien) { // if the sprite is not an alien
            g.drawImage(myImage, (int)x, (int)y, null);
        }
        else {//if the sprite is an alien
            framesDrawn++;
            if ( framesDrawn%100<50 ) {//change the alien images every 50 frames
                g.drawImage(myImage, (int) x, (int) y, null);
            }
            else {
                g.drawImage(myImage2, (int)x, (int)y, null);
            }
        }
    }

    public static void setWinWidth(int w) {
        winWidth = w;
    }
}
