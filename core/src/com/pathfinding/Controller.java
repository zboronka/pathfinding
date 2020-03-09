package com.pathfinding;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;

public class Controller extends InputAdapter {
	public boolean left, right, up, down, click;
	public int mousex, mousey;

	public boolean keyDown(int keycode) {
		switch(keycode) {
			case Keys.LEFT:
				left = true;
				break;
			case Keys.RIGHT:
				right = true;
				break;
			case Keys.UP:
				up = true;
				break;
			case Keys.DOWN:
				down = true;
		}

		return true;
	}

	public boolean keyUp(int keycode) {
		switch(keycode) {
			case Keys.LEFT:
				left = false;
				break;
			case Keys.RIGHT:
				right = false;
				break;
			case Keys.UP:
				up = false;
				break;
			case Keys.DOWN:
				down = false;
		}

		return true;
	}
	
	public boolean touchDown(int x, int y, int pointer, int button) {
		mousex = x;
		mousey = y;
		click = true;
		return true;
	}
}
