import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpaceShip extends GameObject {
	boolean isAlive, canShoot;
	int lives, cool, heat;
	public SpaceShip() {
		super(0, 0, 3, 6, 80, 80);
		try {
			image = ImageIO.read(new File("resources/spaceship.png"));
		} catch (IOException e) {
			System.out.println("Could not read the file");
		}
		isAlive = true;
		lives = 3;
		cool = 10;
		heat = 0;
		canShoot = true;
	}
	public move(double posX, double posY, double velX, double velY){
		
	}
	public void shoot(int cool, int heat, boolean canShoot){ 
		/*every second cool goes up by 1; every time you shoot heat 
		goes up by 1  */
		if(heat > cool)
			canShoot = false;
	}
	public void gettingHit(int cool, int heat){
		//if collision
		lives--;
		if (lives == 0)
			isAlive = false;
			
	}
	
}
