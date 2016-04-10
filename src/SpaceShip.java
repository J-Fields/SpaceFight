  import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpaceShip extends GameObject {
	boolean isAlive, canShoot;
	int lives, cool, heat;
	public SpaceShip(double startX, double startY, double startRot) {
		super(startX, startY, 0, 0, 80, 80);
		rotation = startRot;
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
	
	public void update() {
		super.update();
		double slowDown = .04;
		if (velX > slowDown)
			velX -= slowDown;
		else if (velX < -slowDown)
			velX += slowDown;
		else
			velX = 0;
		if (velY > slowDown)
			velY -= slowDown;
		else if (velY < -slowDown)
			velY += slowDown;
		else
			velY = 0;
	}
	
	public void accelerate() {
		velX += Math.cos(rotation)*.3;
		velY += Math.sin(rotation)*.3;
	}

	public void decelerate() {
		double slowDown = .15;
		if (velX > slowDown)
			velX -= slowDown;
		else if (velX < -slowDown)
			velX += slowDown;
		else
			velX = 0;
		if (velY > slowDown)
			velY -= slowDown;
		else if (velY < -slowDown)
			velY += slowDown;
		else
			velY = 0;
	}

	public void rotate(int direction) {
		rotation += direction*.06;
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
	public double bounce(double velY, double velX){
		if (height - posY == 0)
			return -velY;	 
		else
			return -velX;	
	}
	
}