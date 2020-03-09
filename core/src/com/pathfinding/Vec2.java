package com.pathfinding;

public class Vec2 {
	public int x;
	public int y;
	
	public Vec2(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public String toString() {
		return "(" + x + "," + y + ")";
	}

	public double distance(Vec2 o) {
		return Math.sqrt(Math.pow((x-o.x),2)+Math.pow((y-o.y),2));
	}
}
