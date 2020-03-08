package com.pathfinding;

public class Vector2 {
	public int x;
	public int y;
	
	public Vector2(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public String toString() {
		return "(" + x + "," + y + ")";
	}

	public double distance(Vector2 o) {
		return Math.sqrt((x-o.x)^2+(y-o.y)^2);
	}
}
