import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Bullet extends GameObject{
	private static final double SPEED = 10;
	
	public Bullet(SpaceShip s) {
		super(s.posX, 
				s.posY,
				SPEED * Math.cos(s.rotation), //velX
				SPEED * Math.sin(s.rotation), //velY
				5, 
				5);
		try {
			image = ImageIO.read(new File("resources/BulletRed.png"));
		} catch (IOException e) {
			System.out.println("Could not read the file");
		}
	}
	
}


