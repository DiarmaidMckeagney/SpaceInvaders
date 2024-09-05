package week5;

import java.awt.*;

import java.awt.Image;

public class Spaceship extends Sprite2D {
    private double xSpeed=0;

    public Spaceship(Image i) {
        super(i); // invoke constructor on superclass Sprite2D
    }

    public void setXSpeed(double dx) {
        xSpeed=dx;
    }

    public void move() {
        // apply current movement
        x+=xSpeed;

        // stop movement at screen edge?
        if (x<=0) {
            x=0;
            xSpeed=0;
        }
        else if (x>=winWidth-myImage.getWidth(null)) {
            x=winWidth-myImage.getWidth(null);
            xSpeed=0;
        }
    }

    public boolean doesNotCollide(Alien alien){
        return (!(alien.x > this.x) && !((alien.x + alien.myImage.getWidth(null)) > this.x)) || (!(alien.y > this.y) && !((alien.y + alien.myImage.getHeight(null)) > this.y));
    }//checks if the inputted alien overlaps with the player sprite return false, else return true (My IDE decided to optimise the code, so that's why it looks weird.)
}
