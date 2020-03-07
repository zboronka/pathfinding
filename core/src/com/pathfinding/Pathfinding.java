package com.pathfinding;

import java.io.*;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class Pathfinding extends ApplicationAdapter {
	final int WIDTH = 800;
	final int HEIGHT = 600;

	int map_max_y;
	int map_max_x;
	int tile_width;

	ShapeRenderer shapeRenderer;
	ExtendViewport viewport;

	BufferedReader in = null;
	ArrayList<Node> map = new ArrayList<Node>();
	HashMap<Integer,Node> teleports = new HashMap<>();
	
	@Override
	public void create () {
		shapeRenderer = new ShapeRenderer();
		viewport = new ExtendViewport(WIDTH, HEIGHT);
		readInput();
		HashMap<Node,Integer> dist = new HashMap<>();
		HashMap<Node,Node> prev = new HashMap<>();
		aStar(map.get(0), map.get(2), dist, prev);
		Node u = map.get(2);
		while(u != null) {
			System.out.println(u);
			u = prev.get(u);
		}
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
		shapeRenderer.begin(ShapeType.Filled);
		for(Node n : map) {
			if(n.t_id > 0) {
				shapeRenderer.setColor(0, 0.1f * n.t_id, 1.0f/n.t_id, 1);
			} else {
				shapeRenderer.setColor(0.1f * n.cost, 0.1f * (10-n.cost), 0.1f * (10-n.cost), 1);
			}
			shapeRenderer.rect(-WIDTH/2+n.pos.x*tile_width, HEIGHT/2-(n.pos.y+1)*tile_width, tile_width, tile_width);
		}
		shapeRenderer.end();
	}
	
	@Override
	public void dispose () {
		shapeRenderer.dispose();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, false);
	}

	public void readInput() {
		try {
			in = new BufferedReader(new FileReader("input.txt"));

			for(map_max_y = 0; in.ready(); map_max_y++) {
				String[] nodes = in.readLine().split(" ");
				boolean bottom = !in.ready();

				for(map_max_x = 0; map_max_x < nodes.length; map_max_x++) {
					ArrayList<Vector2> links = new ArrayList<>();
					setLinks(map_max_x, map_max_y, map_max_x == nodes.length - 1, bottom, links);

					switch(nodes[map_max_x].charAt(0)) {
						case 'F':
							map.add(new Node(10, new Vector2(map_max_x,map_max_y), links));
							break;
						case 'T':
							Integer t_id = Integer.parseInt(nodes[map_max_x].substring(1));
							Node current = new Node(1, new Vector2(map_max_x, map_max_y), links, t_id);
							if(teleports.containsKey(t_id)) {
								Node tel = teleports.get(t_id);
								links.add(tel.pos);
								tel.connections.add(new Vector2(map_max_x,map_max_y));
							} else {
								teleports.put(t_id, current);
							}

							map.add(current);	
							break;
						default:
							map.add(new Node(Integer.parseInt(nodes[map_max_x]), new Vector2(map_max_x, map_max_y), links));
					}
				}
			}

			tile_width = WIDTH/map_max_x;
			in.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void setLinks(int x, int y, boolean right, boolean bottom, Collection links) {
		if(x > 0) {
			links.add(new Vector2(x - 1, y));
		}
		if(y > 0) {
			links.add(new Vector2(x, y - 1));
		}
		if(!right) {
			links.add(new Vector2(x + 1, y));
		}
		if(!bottom) {
			links.add(new Vector2(x, y + 1));
		}
	}

	public void aStar(Node start, Node end, HashMap<Node,Integer> dist, HashMap<Node,Node> prev) {
		dist.put(start, 0);

		PriorityQueue<Distance> pq = new PriorityQueue<>();

		pq.add(new Distance(start, 0));
		while(pq.peek() != null) {
			Distance u = pq.poll();
			for(Vector2 v : u.node.connections) {
				Node nv = map.get(v.x+v.y*map_max_x);
				int alt = dist.get(u.node) + nv.cost;
				if(alt < dist.getOrDefault(nv, Integer.MAX_VALUE)) {
					dist.put(nv, alt);
					prev.put(nv, u.node);
					pq.remove(new Distance(nv, 0));
					pq.add(new Distance(nv, alt));
				}
			}
		}
	}

	class Distance implements Comparable<Distance> {
		public Node node;
		public int distance;

		public Distance(Node node, int distance) {
			this.node = node;
			this.distance = distance;
		}

		public int compareTo(Distance o) {
			return Integer.compare(distance, o.distance);
		}

		@Override
		public boolean equals(Object o) {
			if(o == this) {
				return true;
			}
			if(!(o instanceof Distance)) {
				return false;
			}

			Distance d = (Distance) o;
			return d.node.pos == this.node.pos;
		}

		public String toString() {
			return "(" + node.pos + ", " + distance + ")";
		}
	}
}
