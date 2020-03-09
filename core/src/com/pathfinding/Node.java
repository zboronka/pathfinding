package com.pathfinding;

import java.util.ArrayList;

public class Node {
	public int cost;
	public Vec2 pos;
	public ArrayList<Vec2> connections;
	public int t_id = 0;
	public boolean selected = false;

	public Node(int cost, Vec2 pos, ArrayList<Vec2> connections) {
		this.cost = cost;
		this.pos = pos;
		this.connections = connections;
	}

	public Node(int cost, Vec2 pos, ArrayList<Vec2> connections, int t_id) {
		this.cost = cost;
		this.pos = pos;
		this.connections = connections;
		this.t_id = t_id;
	}

	public String toString() {
		return "Node: " + pos + " Cost: " + cost; 
	}
}
