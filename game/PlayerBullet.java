package week5;

import java.awt.*;

public class PlayerBullet extends  Sprite2D{
    public PlayerBullet(Image i, int winWidth) {
        super(i);
    }

    public boolean move(){
        if (y-10 > 0) {
            setPosition(x, y - 10);// moves the bullet vertical up the screen.
            return true;
        }
        else {
            return false;
        }
    }

    public boolean doesNotCollide(Alien alien){
        if (( (alien.x<this.x && alien.x+alien.myImage.getWidth(null)>this.x) ||(this.x<alien.x && this.x+this.myImage.getWidth(null)>alien.x) ) && ( (alien.y<this.y && alien.y+alien.myImage.getHeight(null)>this.y) || (this.y<alien.y && this.y+this.myImage.getHeight(null)>alien.y) )){
            return false;//checks if the inputted alien overlaps with the bullet, if so it returns false
        }
        else {//if they dont overlap, return true.
            return true;
        }
    }

}
