package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Calendar;
import java.awt.Font;

import javax.swing.JPanel;

import entity.*;

public class GamePanel extends JPanel implements Runnable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final int originalTileSize = 16;	//16x16 tile
	final int scale = 5;
	
	public final int tileSize = originalTileSize * scale;	//48x48 tile
	final int maxScreenCol = 20;
	final int maxScreenRow = 11;
	public final int screenWidth = tileSize * maxScreenCol; //48*20 -> 960 pixels
	public final int screenHeight = tileSize * maxScreenRow; //48*11 -> 528 pixels
	
	//FPS
	int FPS = 60;
	
	KeyHandler keyH = new KeyHandler();
	Thread gameThread;	//responsible to keep game loop running
	Graphics2D g2;
	
	public Entity screenSplitter;
	Entity gameplayBackground, winnerBackground;
	Entity vidaNave1, vidaNave2, logo, explosao;
	Entity nave1, nave2;
	Entity bala1, bala2;
	
	Calendar dateBulletDelayYellow = Calendar.getInstance();
	Calendar dateBulletDelayRed	   = Calendar.getInstance();
	Calendar dateNextGame		   = null;
	
	CollisionDetector collision = new CollisionDetector(this);
	
	
	public GamePanel() {
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true); //improves game rendering performance
		this.addKeyListener(keyH);
		this.setFocusable(true);
		
		initializeEntities();
	}
	
	private void initializeEntities() {

		gameplayBackground =  new Entity("Space.jpg", 0, 0, screenWidth, screenHeight);
		logo = new Entity("logo.png", 460, 20, tileSize*8, tileSize);
		
		vidaNave1 = new Entity("vida4.png", 50, 20, tileSize*4, tileSize);
		vidaNave2 = new Entity("vida4.png", screenWidth-400, 20, tileSize*4, tileSize);
		
		nave1 = new Entity("nave_amarela.png", 100, screenHeight/2, tileSize, tileSize);
		nave1.setSpeed(10);
		nave2 = new Entity("nave_vermelha.png", screenWidth-175, screenHeight/2, tileSize, tileSize);
		nave2.setSpeed(10);
		
		//end game
		explosao = new Entity("explosao.png", 0, 0, 100, 100);
		winnerBackground =  new Entity(null, 0, 0, screenWidth, screenHeight);
		dateNextGame = null;
		
	}	
	
	public void startGameThread() {
		gameThread = new Thread(this);
		gameThread.start();	// will start method run
	}

	@Override
	public void run() {
		
		while(gameThread != null) {
			
			int OneSec = 1000000000;
			
			double drawInterval = OneSec/FPS; // 60 FPS means drawing will happen each 0.01666 seconds (1 second = 1 000 000 000 nanoseconds)
			double nextDrawTime = System.nanoTime() + drawInterval;
			
			//display updated information in the screen
			if(nave1.score > 0 && nave2.score > 0)
				update();
			
			//updates information to be drawn like position, scores, new objects, etc
			//calls paintComponent
			repaint();
			
			try {
				double remainingTime = nextDrawTime - System.nanoTime();
				remainingTime = remainingTime/1000000; 	//Convert nanoseconds into miliseconds so it can be used in the sleep
				
				//Avoid interval time go below 0, if so it's time to draw again
				if(remainingTime < 0)
					remainingTime = 0;
				
				Thread.sleep((long) remainingTime);
				
				nextDrawTime += drawInterval;
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
		}
		
	}
	
	
	public void update() {
		
		//update ship positions
		nave1.update("nave1", collision, keyH.upPressed, keyH.downPressed, keyH.leftPressed, keyH.rightPressed);
		nave2.update("nave2", collision, keyH.upPressed2, keyH.downPressed2, keyH.leftPressed2, keyH.rightPressed2);
		
		//update bullets positions
		for(int i = 0; i < nave1.bullets.size(); i++) {
			String response = nave1.bullets.get(i).updateBullet("bullet1", collision, nave2);
			if(response != "Success") {
				nave1.bullets.remove(i);
				i--;
				
				if(response == "CollisionObj" && nave2.score > 0) {
					nave2.score--;
					vidaNave2.imageName = "vida" + nave2.score + ".png";
					vidaNave2.getPlayerImage();
				}
			}
		}
		
		for(int i = 0; i < nave2.bullets.size(); i++) {
			String response = nave2.bullets.get(i).updateBullet("bullet2", collision, nave1);
			if(response != "Success") {
				nave2.bullets.remove(i);
				i--;
				
				if(response == "CollisionObj" && nave1.score > 0) {
					nave1.score--;
					vidaNave1.imageName = "vida" + nave1.score + ".png";
					vidaNave1.getPlayerImage();
				}
			}
		}
		
	}
	
	
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);	//super is JPanel because GamePanel (this) is a subclass of JPanel
		
		g2 = (Graphics2D) g;
		
		if(nave1.score > 0 && nave2.score > 0) {
			
			var currentTime = Calendar.getInstance().getTime();
			
			gameplayBackground.draw(g2);
			logo.draw(g2);
			screenSplitter = new Entity(Color.black, screenWidth/2 - 5, 85, 10, 800, g2);
			screenSplitter.createRectangle();
			
			vidaNave1.draw(g2);
			vidaNave2.draw(g2);	

			nave1.draw(g2);
			nave2.draw(g2);
			
			//Create nave1 bullets 
			if(keyH.spacePressed == true && currentTime.after(dateBulletDelayYellow.getTime())) {
				Entity bullet = new Entity(Color.yellow, nave1.x + 90, nave1.y + 47, 15, 10, g2);
				bullet.speed = 13;
				nave1.createBullet(bullet);
				
				dateBulletDelayYellow = Calendar.getInstance();
				dateBulletDelayYellow.add(Calendar.MILLISECOND, 300);
			}
			
			//Create nave2 bullets 
			if(keyH.ctrlPressed == true && currentTime.after(dateBulletDelayRed.getTime())) {
				Entity bullet = new Entity(Color.red, nave2.x - 25, nave2.y + 47, 15, 10, g2);
				bullet.speed = 13;
				nave2.createBullet(bullet);
				
				dateBulletDelayRed = Calendar.getInstance();
				dateBulletDelayRed.add(Calendar.MILLISECOND, 300);
			}
			
			
			
			//Draw nave1 bullets
			for(int i = 0; i < nave1.bullets.size(); i++) {
				g2.setColor(Color.yellow);
				g2.fillRect(nave1.bullets.get(i).x, nave1.bullets.get(i).y, nave1.bullets.get(i).width, nave1.bullets.get(i).height);
				//System.out.println("[" + i + "]\t" + nave1.bullets.get(i).x + "\t" + nave1.bullets.get(i).y + "\t" + nave1.bullets.get(i).speed);
				//System.out.println("-----------------------------------------");
			}
			
			//Draw nave2 bullets
			for(int i = 0; i < nave2.bullets.size(); i++) {
				g2.setColor(Color.red);
				g2.fillRect(nave2.bullets.get(i).x, nave2.bullets.get(i).y, nave2.bullets.get(i).width, nave2.bullets.get(i).height);
				//System.out.println("[" + i + "]\t" + nave2.bullets.get(i).x + "\t" + nave2.bullets.get(i).y + "\t" + nave2.bullets.get(i).speed);
				//System.out.println("-----------------------------------------");
			}
		}
		
		if(nave1.score == 0)
			finishGame(nave1, "nave2");
			
		if(nave2.score == 0)
			finishGame(nave2, "nave1");
		
		g2.dispose();
		
	}
	
	public void finishGame(Entity defeatedShip, String winner) {
		String winnerText, winnerImage;
		int remainingSeconds = 0;
		var currentTime = Calendar.getInstance();
				
		explosao.x = defeatedShip.x - 20;
		explosao.y = defeatedShip.y;
		
		if(dateNextGame == null) {			
			dateNextGame	 = Calendar.getInstance();
			dateNextGame.add(Calendar.SECOND, 6);
			remainingSeconds = 5;
			
		} else 
			remainingSeconds = (int) ((dateNextGame.getTimeInMillis() - currentTime.getTimeInMillis()) / 1000);
		
		if(winner == "nave1") {
			winnerText  = "Amarelo Venceu!";
			winnerImage = "SPACE3.jpeg";
		}
		else {
			winnerText  = "Vermelho Venceu!";
			winnerImage = "SPACE2.jpeg";
		}
				
		if(winnerBackground.imageName == null) {
			winnerBackground.imageName = winnerImage;
			winnerBackground.getPlayerImage();
		}
		
		winnerBackground.draw(g2);
		
		nave1.draw(g2);
		nave2.draw(g2);
		
		explosao.draw(g2);
		
		drawText(winnerText, screenWidth/2 - 200, screenHeight/2 + 20, Color.black, 50);
		drawText("Novo jogo vai começar em " + remainingSeconds + " segundos!", screenWidth/2 - 300, screenHeight/2 + screenHeight/4, Color.black, 30);

		if(remainingSeconds == 0)
			initializeEntities();
	}
	
	public void drawText(String text, int posX, int posY, Color color, int textSize) {
		Font font = new Font("Arial", Font.BOLD, textSize);
		g2.setFont(font);
		g2.setColor(color);
		g2.drawString(text, posX, posY);
	}	
}
