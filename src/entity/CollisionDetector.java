package entity;

import java.awt.Dimension;

import main.GamePanel;

public class CollisionDetector {
	
	Limit leftSide;
	Limit rightSide;
	Limit all;
	
	GamePanel gp;
	
	public CollisionDetector (GamePanel gp) {
		this.gp = gp;
		
		leftSide = new Limit(5,
							 gp.screenWidth/2 - 81, 
							 110,
							 gp.screenHeight - 81);
		
		rightSide = new Limit(gp.screenWidth/2 + 5,
							 gp.screenWidth - 81, 
							 110,
							 gp.screenHeight - 81);
		
			  all = new Limit(5,
					  		gp.screenWidth - 5, 
					  		110,
					  		gp.screenHeight - 81);
	}
	
	
	public Boolean checkCollisionEnv(String objectName, Entity entity, Boolean upPressed, Boolean downPressed, Boolean leftPressed, Boolean rightPressed) {
		
		int newPosX = entity.x;
		int newPosY = entity.y;
		
		Limit limitToCheck = all;
		
		switch(objectName) {
			case "nave1": limitToCheck = leftSide;
							break;
			case "nave2": limitToCheck = rightSide;
							break;
			default: 	  limitToCheck = all;
							break;
		}
		
		if(objectName.indexOf("nave") != -1) {
			if(upPressed == true)
				newPosY = entity.y - entity.speed;
			
			if(downPressed == true)
				newPosY = entity.y + entity.speed;
			
			if(leftPressed == true)
				newPosX = entity.x - entity.speed;
			
			if(rightPressed == true)
				newPosX = entity.x + entity.speed;
		}
		
		
		if(objectName.indexOf("bullet") != -1) {
			if(objectName == "bullet1")
				newPosX = entity.x + entity.speed;
			
			if(objectName == "bullet2")
				newPosX = entity.x - entity.speed;
		}
		
		//System.out.println(objectName + ": (" + newPosX + "," + newPosY + ")\t\t" + "(" + limitToCheck.limitLeft + "," + limitToCheck.limitRight  + "," + limitToCheck.limitUp + "," + limitToCheck.limitDown + ")");
		
		if(newPosX < limitToCheck.limitLeft || newPosX > limitToCheck.limitRight  || newPosY < limitToCheck.limitUp || newPosY > limitToCheck.limitDown)
			return true;
		
		return false;		
	}
	
	public Boolean checkCollisionObj(String objectName, Entity entity, Entity targetShip) {
		
		int newPosX = entity.x;
		int targetHitboxLeft  = entity.y;
		int targetHitboxRight = entity.y;
		
		
		if(objectName == "bullet1") {
			newPosX 		  = entity.x + entity.speed;
			targetHitboxLeft  = targetShip.x - 5;
			targetHitboxRight = targetShip.x + 20;
			
		}
		
		if(objectName == "bullet2") {
			newPosX = entity.x - entity.speed;
			targetHitboxLeft  = targetShip.x - 20;
			targetHitboxRight = targetShip.x + 60;
		}
				
		if(newPosX > targetHitboxLeft && newPosX < targetHitboxRight && entity.y > targetShip.y - 10 && entity.y < targetShip.y + 70)
			return true;
		
		return false;		
	}

}
