import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpaceShip extends GameObject {
	private static final double ROTATION_SPEED = .06;
	private static final double FRICTION = .04;

	public SpaceShip(double startX, double startY, double startRot) {
		super(startX, startY, 0, 0, 80, 80);
		rotation = startRot;
		try {
			image = ImageIO.read(new File("resources/spaceship.png"));
		} catch (IOException e) {
			System.out.println("Could not read the file");
		}
	}
	
	public void update() {
		super.update();

		if (velX > FRICTION)
			velX -= FRICTION;
		else if (velX < -FRICTION)
			velX += FRICTION;
		else
			velX = 0;

		if (velY > FRICTION)
			velY -= FRICTION;
		else if (velY < -FRICTION)
			velY += FRICTION;
		else
			velY = 0;

		bounceOffWalls();
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
		rotation += direction*ROTATION_SPEED;
	}
	
	public void bounceOffWalls(){	
		if (top() < 0 || bottom() > Game.getInstance().getHeight())
			velY = -velY;
		if(left() < 0 || right() > Game.getInstance().getWidth())
			velX = -velX;	
	}
}