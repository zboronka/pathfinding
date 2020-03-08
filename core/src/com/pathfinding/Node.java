package com.pathfinding;

import java.util.ArrayList;

public class Node {
	public int cost;
	public Vector2 pos;
	public ArrayList<Vector2> connections;
	public int t_id = 0;

	public Node(int cost, Vector2 pos, ArrayList<Vector2> connections) {
		this.cost = cost;
		this.pos = pos;
		this.connections = connections;
	}

	public Node(int cost, Vector2 pos, ArrayList<Vector2> connections, int t_id) {
		this.cost = cost;
		this.pos = pos;
		this.connections = connections;
		this.t_id = t_id;
	}

	public String toString() {
		return "Node: " + pos + " Cost: " + cost; 
	}
}
