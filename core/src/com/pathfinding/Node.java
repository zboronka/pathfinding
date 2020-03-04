package com.pathfinding;

import java.util.Arrays;
import java.util.ArrayList;

public class Node {
	public int cost;
	public ArrayList<int[]> connections;
	public int t_id = 0;

	public Node(int cost, ArrayList<int[]> connections) {
		this.cost = cost;
		this.connections = connections;
	}

	public Node(int cost, ArrayList<int[]> connections, int t_id) {
		this.cost = cost;
		this.connections = connections;
		this.t_id = t_id;
	}

	public Node(int cost) {
		this.cost = cost;
		connections = new ArrayList<>();
	}

	public String toString() {
		String ret = "Cost " + cost + " Links to: "; 
		for(int[] link : connections) {
			ret += "(" + link[0] + ", " + link[1] + ") ";
		}
		return ret;
	}
}
