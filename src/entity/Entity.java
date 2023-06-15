package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.KeyHandler;

public class Entity {
	
	public int x, y, width, height, speed, score;
	public BufferedImage entity;
	public String imageName;
	public Color color; 
	
	Graphics2D g2;
	public ArrayList<Entity> bullets = new ArrayList<Entity>(); 
	
	public Entity() {	
	}
	
	public Entity(String imageName, int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.speed = 0;
		this.score = 4;
		
		if(imageName != null) {
			this.imageName = imageName;
			getPlayerImage();
		}
	}
	
	public Entity(Color color, int x, int y, int width, int height, Graphics2D g2) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.speed = 0;
		
		this.g2 = g2;
		this.color = color;
	}
	
	public void getPlayerImage() {
		try {
			entity = ImageIO.read(getClass().getResourceAsStream("/assets/" + imageName));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	public void draw(Graphics2D g2) {
		
		g2.drawImage(entity, x, y, width, height, null);
	}
	
	public void update(String objectName, CollisionDetector collision, Boolean upPressed, Boolean downPressed, Boolean leftPressed, Boolean rightPressed) {
		
		if(upPressed == true && collision.checkCollisionEnv(objectName, this, true, false, false, false) == false)
			y -= speed;
		
		if(downPressed == true && collision.checkCollisionEnv(objectName, this, false, true, false, false) == false)
			y += speed;
		
		if(leftPressed == true && collision.checkCollisionEnv(objectName, this, false, false, true, false) == false)
			x -= speed;
		
		if(rightPressed == true && collision.checkCollisionEnv(objectName, this, false, false, false, true) == false)
			x += speed;
	}
	
	
	public String updateBullet(String objectName, CollisionDetector collision, Entity targetShip) {
		
		//Check Environmental Collisions
		boolean collisionDetected = collision.checkCollisionEnv(objectName, this, null, null, null, null);
		if(collisionDetected)
			return "CollisionEnv";
		
		//Check Object Collisions
		boolean collisionObj = collision.checkCollisionObj(objectName, this, targetShip);
		if(collisionObj)
			return "CollisionObj";
		
		if(objectName == "bullet1" && !collisionDetected)
			x += speed;
		
		else if(objectName == "bullet2" && !collisionDetected)
			x -= speed;
		
		return "Success";
	}
	
	
	public void createRectangle() {
		
		if(color != null)
			g2.setColor(color);
				
		g2.fillRect(x, y, width, height);
		
	}
	
	
	public void createBullet(Entity bullet) {
		
		bullets.add(bullet);
			
	}
	
}
