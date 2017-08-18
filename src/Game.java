import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Game extends Thread implements KeyListener {
	public enum GameState {
		SPLASH,
		EXITED,
		PAUSED,
		INGAME
	}
	
	private final static Game game = new Game();
    private GameState gameState = GameState.SPLASH;
    private ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
    private SpaceShip player1;
    private SpaceShip player2;
    private HashSet<Integer> keysPressed = new HashSet<Integer>();
    private Canvas canvas;
    private BufferStrategy strategy;
    private BufferedImage background;
    private Graphics2D backgroundGraphics;
    private Graphics2D graphics;
    private JFrame frame;
    private int width = 800;
    private int height = 800;
    private GraphicsConfiguration config =
    		GraphicsEnvironment.getLocalGraphicsEnvironment()
    			.getDefaultScreenDevice()
    			.getDefaultConfiguration();
	private BufferedImage splashImage;
	
	public static Game getInstance() {
		return game;
	}

    // create a hardware accelerated image
    public final BufferedImage create(final int width, final int height,
    		final boolean alpha) {
    	return config.createCompatibleImage(width, height, alpha
    			? Transparency.TRANSLUCENT : Transparency.OPAQUE);
    }

    // Setup
    private Game() {
    	initGame();

    	// JFrame
    	frame = new JFrame();
    	frame.addWindowListener(new FrameClose());
    	frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    	frame.setSize(width, height);
    	frame.setVisible(true);
    	frame.addKeyListener(this);

    	// Canvas
    	canvas = new Canvas(config);
    	canvas.setSize(width, height);
    	frame.add(canvas, 0);

    	// Background & Buffer
    	background = create(width, height, false);
    	canvas.createBufferStrategy(2);
    	do {
    		strategy = canvas.getBufferStrategy();
    	} while (strategy == null);
    	start();
    }
    
    private void initGame() {
		try {
			splashImage = ImageIO.read(new File("resources/SpaceDuel.JPEG"));
		} catch (IOException e) {
			System.out.println("Could not read the file");
		}
    	player1 = new SpaceShip(200, 200, Math.PI/4.0);
    	gameObjects.add(player1);
    	player2 = new SpaceShip(width-200, height-200, 5.0*Math.PI/4.0);
    	gameObjects.add(player2);
    }

    private class FrameClose extends WindowAdapter {
    	@Override
    	public void windowClosing(final WindowEvent e) {
    		gameState = GameState.EXITED;
    	}
    }

    // Screen and buffer stuff
    private Graphics2D getBuffer() {
    	if (graphics == null) {
    		try {
    			graphics = (Graphics2D) strategy.getDrawGraphics();
    		} catch (IllegalStateException e) {
    			return null;
    		}
    	}
    	return graphics;
    }

    private boolean updateScreen() {
    	graphics.dispose();
    	graphics = null;
    	try {
    		strategy.show();
    		Toolkit.getDefaultToolkit().sync();
    		return (!strategy.contentsLost());
    	} catch (NullPointerException e) {
    		return true;

    	} catch (IllegalStateException e) {
    		return true;
    	}
    }

    /**
     * Specifies the game loop.
     */
    public void run() {
    	backgroundGraphics = (Graphics2D) background.getGraphics();
    	long fpsWait = (long) (1.0 / 60 * 1000);
    	main: while (gameState != GameState.EXITED) {
    		long renderStart = System.nanoTime();

    		// TODO:  Passing fpsWait into here assumes we're running at 60 FPS
    		updateGame(fpsWait/1000.0);

    		// Update Graphics
    		do {
    			Graphics2D bg = getBuffer();
    			if (gameState == GameState.EXITED) {
    				break main;
    			}
    			renderGame(backgroundGraphics);
    			bg.drawImage(background, 0, 0, null);
    			bg.dispose();
    		} while (!updateScreen());

    		// Better do some FPS limiting here
    		long renderTime = (System.nanoTime() - renderStart) / 1000000;
    		try {
    			Thread.sleep(Math.max(0, fpsWait - renderTime));
    		} catch (InterruptedException e) {
    			Thread.interrupted();
    			break;
    		}
    	}
    	frame.dispose();
    }

    /**
     * Applies game logic each frame.
     */
    public void updateGame(double delta) {
    	switch (gameState) {
    	case SPLASH:
    	case PAUSED:
    	case EXITED:
    		break;
    	case INGAME:
    		// TODO:  Should be in the SpaceShip class
    		// Make SpaceShip constructor take keybindings
    		if (keysPressed.contains(KeyEvent.VK_W))
				player1.accelerate(delta);
    		if (keysPressed.contains(KeyEvent.VK_A))
				player1.rotate(-1, delta);
    		if (keysPressed.contains(KeyEvent.VK_S))
				player1.decelerate(delta);
    		if (keysPressed.contains(KeyEvent.VK_D))
				player1.rotate(1, delta);
    		if(keysPressed.contains(KeyEvent.VK_SHIFT)){
    			Bullet b = player2.shoot();
    			if(b != null)
    				gameObjects.add(b);
    		}

    		if (keysPressed.contains(KeyEvent.VK_UP))
				player2.accelerate(delta);
    		if (keysPressed.contains(KeyEvent.VK_LEFT))
				player2.rotate(-1, delta);
    		if (keysPressed.contains(KeyEvent.VK_DOWN))
				player2.decelerate(delta);
    		if (keysPressed.contains(KeyEvent.VK_RIGHT))
				player2.rotate(1, delta);
    		if(keysPressed.contains(KeyEvent.VK_SPACE)){
    			Bullet b = player2.shoot();
    			if(b != null)
    				gameObjects.add(b);
    		}
    		for (GameObject obj : gameObjects) {
    			obj.update(delta);
    		}
    		System.out.println(gameObjects.size());
    		break;
    		
    	}
    }

    public void renderGame(Graphics2D g) {
    	g.setColor(Color.BLACK);
    	g.fillRect(0, 0, width, height);
		switch (gameState) {
		case SPLASH:
			AffineTransform transform = new AffineTransform();
			transform.scale((double)width/(double)splashImage.getWidth(), (double)height/(double)splashImage.getHeight());
			g.drawImage(splashImage, transform, null);
		case EXITED:
			break;
		case PAUSED:
		case INGAME:
    		for (GameObject obj : gameObjects) {
    			obj.render(g);
    		}
			break;
		}
    }

	@Override
	public void keyTyped(KeyEvent e) {
		return;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (gameState) {
		case SPLASH:
			if(e.getKeyCode() == KeyEvent.VK_SPACE)
				gameState = GameState.INGAME;
			break;
		case PAUSED:
			if (e.getKeyChar() == 'p')
				gameState = GameState.INGAME;
			break;
		case EXITED:
		case INGAME:
			if (e.getKeyChar() == 'p') {
				gameState = GameState.PAUSED;
			}
		}
		keysPressed.add(e.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keysPressed.remove(e.getKeyCode());
	}

    public static void main(final String[] args) {}
    
    public double getWidth() {
    	return width;
    }

    public double getHeight() {
    	return height;
    }
    
 
}