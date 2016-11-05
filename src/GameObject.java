import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Abstract class that provides the basic properties and methods a visible on-screen object needs.
 */
public abstract class GameObject {
	protected double posX, posY;
	protected double velX, velY;
	protected double rotation;
	protected int width, height;
	protected BufferedImage image;
	
	public GameObject(double posX, double posY, double velX, double velY, int width, int height){
		this.posX = posX;
		this.posY = posY;
		this.velX = velX;
		this.velY = velY;
		this.width = width;
		this.height = height;
	}
		
	public void update() {
		posX += velX;
		posY += velY;
    }

	public void render(Graphics2D g) {
		AffineTransform transform = new AffineTransform();
		transform.translate(posX, posY);
		transform.rotate(rotation);
		transform.scale((double)width/(double)image.getWidth(), (double)height/(double)image.getHeight());
		transform.translate(-image.getWidth()/2, -image.getHeight()/2);
		g.drawImage(image, transform, null);
    }
}
