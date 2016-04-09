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
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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
	
    private GameState gameState = GameState.SPLASH;
    private ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
    private SpaceShip player1;
    private HashMap<Integer, Boolean> keysPressed = new HashMap<Integer, Boolean>();
    
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
	

    // create a hardware accelerated image
    public final BufferedImage create(final int width, final int height,
    		final boolean alpha) {
    	return config.createCompatibleImage(width, height, alpha
    			? Transparency.TRANSLUCENT : Transparency.OPAQUE);
    }

    // Setup
    public Game() {
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
    	player1 = new SpaceShip();
    	gameObjects.add(player1);
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

    public void run() {
    	backgroundGraphics = (Graphics2D) background.getGraphics();
    	long fpsWait = (long) (1.0 / 30 * 1000);
    	main: while (gameState != GameState.EXITED) {
    		long renderStart = System.nanoTime();
    		updateGame();

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
    		renderTime = (System.nanoTime() - renderStart) / 1000000;
    	}
    	frame.dispose();
    }

    public void updateGame() {
    	switch (gameState) {
    	case SPLASH:
    	case PAUSED:
    	case EXITED:
    		break;
    	case INGAME:
    		if (keysPressed.containsKey(KeyEvent.VK_W) && keysPressed.get(KeyEvent.VK_W) == true)
				player1.accelerate();
    		if (keysPressed.containsKey(KeyEvent.VK_A) && keysPressed.get(KeyEvent.VK_A) == true)
				player1.rotate(-1);
    		if (keysPressed.containsKey(KeyEvent.VK_S) && keysPressed.get(KeyEvent.VK_S) == true)
				player1.decelerate();
    		if (keysPressed.containsKey(KeyEvent.VK_D) && keysPressed.get(KeyEvent.VK_D) == true)
				player1.rotate(1);
    		for (GameObject obj : gameObjects) {
    			obj.update();
    		}
    		break;
    	}
    }

    public void renderGame(Graphics2D g) {
    	g.setColor(Color.BLACK);
    	g.fillRect(0, 0, width, height);
		switch (gameState) {
		case SPLASH:
			try {
				splashImage = ImageIO.read(new File("resources/SpaceDuel.JPEG"));
			} catch (IOException e) {
				System.out.println("Could not read the file");
			}
			g.drawImage(splashImage, 0, 0, null);
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
    
    public static void main(final String[] args) {
    	new Game();
    }

	@Override
	public void keyTyped(KeyEvent e) {
		return;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (gameState) {
		case SPLASH:
			gameState = GameState.INGAME;
			break;
		case PAUSED:
			if (e.getKeyChar() == 'p')
				gameState = GameState.INGAME;
			break;
		case EXITED:
		case INGAME:
			if ((!keysPressed.containsKey('p') || keysPressed.get('p') == false) && e.getKeyChar() == 'p') {
				gameState = GameState.PAUSED;
			}
		}
		keysPressed.put(e.getKeyCode(), true);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keysPressed.put(e.getKeyCode(), false);
	}
}