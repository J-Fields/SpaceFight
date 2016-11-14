import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpaceShip extends GameObject {
	private static final double SPACESHIP_SPEED = 400;
	private static final double ROTATION_SPEED = 4;
	private static final double FRICTION = 4;
	private static final double DECELERATE_RATE = 200;

	public SpaceShip(double startX, double startY, double startRot) {
		super(startX, startY, 0, 0, 80, 80);
		rotation = startRot;
		try {
			image = ImageIO.read(new File("resources/spaceship.png"));
		} catch (IOException e) {
			System.out.println("Could not read the file");
		}
	}
	
	public void update(double delta) {
		super.update(delta);

		if (velX > FRICTION)
			velX -= FRICTION * delta;
		else if (velX < -FRICTION)
			velX += FRICTION * delta;
		else
			velX = 0;

		if (velY > FRICTION)
			velY -= FRICTION * delta;
		else if (velY < -FRICTION)
			velY += FRICTION * delta;
		else
			velY = 0;

		bounceOffWalls();
	}
	
	public void accelerate(double delta) {
		velX += Math.cos(rotation) * SPACESHIP_SPEED * delta;
		velY += Math.sin(rotation) * SPACESHIP_SPEED * delta;
	}

	public void decelerate(double delta) {
		double slowDown = DECELERATE_RATE;
		if (velX > slowDown)
			velX -= slowDown * delta;
		else if (velX < -slowDown)
			velX += slowDown * delta;
		else
			velX = 0;
		if (velY > slowDown)
			velY -= slowDown * delta;
		else if (velY < -slowDown)
			velY += slowDown * delta;
		else
			velY = 0;
	}

	public void rotate(int direction, double delta) {
		rotation += direction * ROTATION_SPEED * delta;
	}
	
	public void bounceOffWalls() {
		if (top() < 0 || bottom() > Game.getInstance().getHeight())
			velY = -velY;
		if(left() < 0 || right() > Game.getInstance().getWidth())
			velX = -velX;	
	}

	public Bullet shoot() {
		Bullet x = new Bullet(this);
		return x;
    }
}