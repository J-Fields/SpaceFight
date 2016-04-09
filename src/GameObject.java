import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;

public abstract class GameObject {
	protected double posX;
	protected double posY;
	protected double velX;
	protected double velY;
	protected double rotation;
	protected int width;
	protected int height;
	protected BufferedImage image;
	
	public GameObject(double posX, double posY, double velX, double velY, int width, int height, BufferedImage image){
		this.posX = posX;
		this.posY = posY;
		this.velX = velX;
		this.velY = velY;
		this.width = width;
		this.height = height;
		this.image = image;
	}
		
	public void update() {
		posX += velX;
		posY += velY;
    }

	public void render(Graphics2D g) {
		g.setColor(Color.CYAN);
		g.fillRect((int)posX, (int)posY, width, height);
    }
}
