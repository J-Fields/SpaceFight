import java.awt.Color;
import java.awt.Graphics2D;

public class GameObject {
	int posX;
	int posY;
	int speed;
	int direction;
	int width;
	int height;
	static boolean image;
	
	
	public GameObject(int posX, int posY, int speed, int direction, int width, int height, boolean image){
		posX = 0;
		posY = 0;
		speed = 0;
		direction = 0;
		width = 5;
		height = 5;
		image = true;
	}
		
	public void update() {
    	
    }
	public void renderGame(Graphics2D g) {
    	
    }
	
	
}
