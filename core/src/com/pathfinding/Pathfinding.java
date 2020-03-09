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
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Pathfinding extends ApplicationAdapter {
	final int WIDTH = 800;
	final int HEIGHT = 600;
	final int TILE_WIDTH = 20;

	int map_max_y;
	int map_max_x;

	Node one_node;
	boolean one_node_selected = false;

	String filename;

	ShapeRenderer shapeRenderer;
	ShapeRenderer nodeRenderer;
	ExtendViewport viewport;
	Matrix4 mapCamera;
	Matrix4 independent;
	Vector2 mouse;
	Vector3 trns = new Vector3(0,0,0);

	HashMap<Node,Node> prev;
	HashMap<Node,Integer> dist;

	BufferedReader in = null;
	ArrayList<Node> map = new ArrayList<Node>();
	ArrayList<Node> path;

	Controller controller;

	public Pathfinding(String [] args) {
		filename = args.length > 0 ? args[0] : "input1";
	}
	
	@Override
	public void create() {
		controller = new Controller();
		Gdx.input.setInputProcessor(controller);

		shapeRenderer = new ShapeRenderer();
		nodeRenderer = new ShapeRenderer();
		viewport = new ExtendViewport(WIDTH, HEIGHT);
		mapCamera = viewport.getCamera().combined;
		independent = new Matrix4(viewport.getCamera().combined);
		readInput();

		prev = new HashMap<>();
		dist = new HashMap<>();
		path = new ArrayList<>();
	}

	@Override
	public void render() {
		if(controller.left) {
			independent.translate(1,0,0);
		}
		if(controller.right) {
			independent.translate(-1,0,0);
		}
		if(controller.up) {
			independent.translate(0,-1,0);
		}
		if(controller.down) {
			independent.translate(0,1,0);
		}
		if(controller.click) {
			mouse = new Vector2(controller.mousex, controller.mousey);
			trns = independent.getTranslation(new Vector3(0,0,0));

			int mx = (int) (Math.floor(mouse.x/16)+(trns.x*-20));
			int my = (int) (Math.floor(mouse.y/16)+(trns.y*15));
			int index = mx + my*map_max_x;
			if(index >= 0 && map.size() > index) {
				Node s = map.get(index);
				s.selected = !s.selected;
				if(one_node == s) {
					one_node = null;
					one_node_selected = false;
				} else if(one_node_selected) {
					if(controller.aStar) {
						aStar(one_node, s, dist, prev);
					} else {
						dijkstra(one_node, s, dist, prev);
					}
					path = readPath(s);

					one_node.selected = false;
					s.selected = false;

					one_node = null;
					one_node_selected = false;
				} else {
					one_node = s;
					one_node_selected = true;
				}
			}

			controller.click = false;
		}

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		shapeRenderer.setProjectionMatrix(independent);
		nodeRenderer.setProjectionMatrix(independent);

		nodeRenderer.begin(ShapeType.Filled);
		for(Node n : map) {
			if(prev.containsKey(n)) {
				nodeRenderer.setColor(1, 0, 0, 1);
				nodeRenderer.rect((float)-WIDTH/2+n.pos.x*TILE_WIDTH, HEIGHT/2-(n.pos.y+1)*TILE_WIDTH, TILE_WIDTH, TILE_WIDTH);
			}
			if(path.contains(n)) {
				nodeRenderer.setColor(1, 0.7f, 0, 1);
				nodeRenderer.rect((float)-WIDTH/2+n.pos.x*TILE_WIDTH, HEIGHT/2-(n.pos.y+1)*TILE_WIDTH, TILE_WIDTH, TILE_WIDTH);
			}
			if(n.selected) {
				nodeRenderer.setColor(0, 0, 1, 1);
				nodeRenderer.rect((float)-WIDTH/2+n.pos.x*TILE_WIDTH, HEIGHT/2-(n.pos.y+1)*TILE_WIDTH, TILE_WIDTH, TILE_WIDTH);
			}
		}
		nodeRenderer.end();

		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(0, 0, 0, 1);
		for(Node n : map) {
			shapeRenderer.rect((float)-WIDTH/2+n.pos.x*TILE_WIDTH, HEIGHT/2-(n.pos.y+1)*TILE_WIDTH, TILE_WIDTH, TILE_WIDTH);
		}
		shapeRenderer.end();
	}
	
	@Override
	public void dispose() {
		shapeRenderer.dispose();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, false);
		independent = new Matrix4(viewport.getCamera().combined);
	}

	private void readInput() {
		try {
			in = new BufferedReader(new FileReader(filename));
			HashMap<Integer,Node> teleports = new HashMap<>();

			for(map_max_y = 0; in.ready(); map_max_y++) {
				String[] nodes = in.readLine().split(" ");
				boolean bottom = !in.ready();

				for(map_max_x = 0; map_max_x < nodes.length; map_max_x++) {
					ArrayList<Vec2> links = new ArrayList<>();
					setLinks(map_max_x, map_max_y, map_max_x == nodes.length - 1, bottom, links);

					switch(nodes[map_max_x].charAt(0)) {
						case 'F':
							map.add(new Node(-1, new Vec2(map_max_x,map_max_y), links));
							break;
						case 'T':
							Integer t_id = Integer.parseInt(nodes[map_max_x].substring(1));
							Node current = new Node(1, new Vec2(map_max_x, map_max_y), links, t_id);
							if(teleports.containsKey(t_id)) {
								Node tel = teleports.get(t_id);
								links.add(tel.pos);
								tel.connections.add(new Vec2(map_max_x,map_max_y));
							} else {
								teleports.put(t_id, current);
							}

							map.add(current);	
							break;
						default:
							map.add(new Node(Integer.parseInt(nodes[map_max_x]), new Vec2(map_max_x, map_max_y), links));
					}
				}
			}
			in.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	private void setLinks(int x, int y, boolean right, boolean bottom, Collection links) {
		if(x > 0) {
			links.add(new Vec2(x - 1, y));
		}
		if(y > 0) {
			links.add(new Vec2(x, y - 1));
		}
		if(!right) {
			links.add(new Vec2(x + 1, y));
		}
		if(!bottom) {
			links.add(new Vec2(x, y + 1));
		}
	}

	private void aStar(Node source, Node target, HashMap<Node,Integer> dist, HashMap<Node,Node> prev) {
		dist.clear();
		prev.clear();
		dist.put(source, 0);

		PriorityQueue<Distance> pq = new PriorityQueue<>();
		boolean tele = false;
		boolean port = false;
		double distance;

		pq.add(new Distance(source, 0));
		while(pq.peek() != null) {
			Distance u = pq.poll();
			Node nu = u.node;
			tele = nu.t_id > 0;

			for(Vec2 v : nu.connections) {
				Node nv = map.get(v.x+v.y*map_max_x);
				port = tele && nv.t_id > 0;
				int alt = dist.get(nu) + (tele && port ? 0 : nv.cost);
				if(nv.cost >= 0 && alt < dist.getOrDefault(nv, Integer.MAX_VALUE)) {
					dist.put(nv, alt);
					prev.put(nv, nu);

					if(nv == target) return;

					if(nv.t_id > 0) {
						double before_tele = nv.pos.distance(target.pos);
						double after_tele = nv.connections.get(nv.connections.size()-1).distance(target.pos);
						distance = before_tele < after_tele ? before_tele : after_tele;
					} else {
						distance = nv.pos.distance(target.pos);
					}

					pq.remove(new Distance(nv, 0));
					pq.add(new Distance(nv, alt+distance));
				}
			}
		}
	}

	private void dijkstra(Node source, Node target, HashMap<Node,Integer> dist, HashMap<Node,Node> prev) {
		dist.clear();
		prev.clear();
		dist.put(source, 0);

		PriorityQueue<Distance> pq = new PriorityQueue<>();
		boolean tele = false;
		boolean port = false;

		pq.add(new Distance(source, 0));
		while(pq.peek() != null) {
			Distance u = pq.poll();
			Node nu = u.node;
			tele = nu.t_id > 0;

			for(Vec2 v : nu.connections) {
				Node nv = map.get(v.x+v.y*map_max_x);
				port = tele && nv.t_id > 0;
				int alt = dist.get(nu) + (tele && port ? 0 : nv.cost);
				if(nv.cost >= 0 && alt < dist.getOrDefault(nv, Integer.MAX_VALUE)) {
					dist.put(nv, alt);
					prev.put(nv, nu);

					if(nv == target) return;

					pq.remove(new Distance(nv, 0));
					pq.add(new Distance(nv, alt));
				}
			}
		}
	}
	
	private ArrayList<Node> readPath(Node target) {
		ArrayList<Node> path = new ArrayList<>();
		Node u = target;
		if(prev.get(u) != null) {
			while(u != null) {
				path.add(u);
				u = prev.get(u);
			}
		}

		return path;
	}

	class Distance implements Comparable<Distance> {
		public Node node;
		public double distance;

		public Distance(Node node, double distance) {
			this.node = node;
			this.distance = distance;
		}

		public int compareTo(Distance o) {
			return Double.compare(distance, o.distance);
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
