package com.gdxrpg.game.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class GameModel {

	private Map map;
	private MainCharacter mainCharacter;
	private Array<Character> characters; // NPCs

	public GameModel(String mapName, String sheet, float x, float y) {
		map = new Map(mapName);
		mainCharacter = new MainCharacter(sheet, x, y);
		characters = new Array<Character>();
	}

	public void addCharacter(String sheet, float x, float y) {
		Character c = new Character(sheet, x, y);
		characters.add(c);
	}

	/**
	 * Returns character object which has collided with rectangle.
	 * Otherwise, returns null if there is no collision. Does not
	 * check for overlap with (it)self.
	 * 
	 * @param r the collision rectangle
	 * @param self character to not check for overlap with
	 * @return the character object or null
	 */
	private Character getCharacterCollision(Rectangle r, Character self) {
		for (Character c : characters) {
			boolean isNotItself = self == null || !self.equals(c);
			if (isNotItself && r.overlaps(c.getCollisionRectangle()))
				return c;
		}
		return null;
	}

	private boolean isFutureCollision(Character c, Vector2 v) {
		Vector2 pos = c.getPosition();
		Vector2 newPos = new Vector2(pos).add(v);

		float w = c.getCollisionRectangleWidth();
		float h = c.getCollisionRectangleHeight();

		Rectangle r = new Rectangle(newPos.x, newPos.y, w, h);

		return map.isCollision(r);
	}

	public void update() {
		Vector2 pos = mainCharacter.getPosition();
		Vector2 vNor = mainCharacter.getVelocityNor();

		// Vector2 tilePos = map.getTile(pos);
		// System.out.println((int) tilePos.x + ", " + (int) tilePos.y);

		Vector2 newPosX = new Vector2(pos).add(vNor.x, 0);
		Vector2 newPosY = new Vector2(pos).add(0, vNor.y);

		float w = mainCharacter.getCollisionRectangleWidth();
		float h = mainCharacter.getCollisionRectangleHeight();

		Rectangle playerRectangleX = new Rectangle(newPosX.x, newPosX.y, w, h);
		Rectangle playerRectangleY = new Rectangle(newPosY.x, newPosY.y, w, h);

		Character charX = getCharacterCollision(playerRectangleX, mainCharacter);
		Character charY = getCharacterCollision(playerRectangleY, mainCharacter);

		if (charX != null) {
			float ps = charX.getPushSpeed();
			if (charX.getX() > mainCharacter.getX()) {
				if (!isFutureCollision(charX, new Vector2(ps, 0)))
					charX.pushRight();
			}
			else
				if (!isFutureCollision(charX, new Vector2(-ps, 0)))
					charX.pushLeft();
		}

		if (charY != null) {
			float ps = charY.getPushSpeed();
			if (charY.getY() > mainCharacter.getY()) {
				if (!isFutureCollision(charY, new Vector2(0, ps)))
					charY.pushUp();
			}
			else
				if (!isFutureCollision(charY, new Vector2(0, -ps)))
					charY.pushDown();
		}

		boolean noCollisionX = !map.isCollision(playerRectangleX) &&
				charX == null;
		boolean noCollisionY = !map.isCollision(playerRectangleY) &&
				charY == null;

		if (noCollisionX && noCollisionY)
			mainCharacter.changePosition(vNor.x, vNor.y);
		else if (noCollisionX)
			mainCharacter.changePosition(vNor.x, 0);
		else if (noCollisionY)
			mainCharacter.changePosition(0, vNor.y *
					mainCharacter.getCollisionSpeed());
	}

	public Map getMap() {
		return map;
	}

	public MainCharacter getMainCharacter() {
		return mainCharacter;
	}

	public Array<Character> getCharacters() {
		return characters;
	}

}
