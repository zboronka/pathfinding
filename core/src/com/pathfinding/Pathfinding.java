package com.pathfinding;

import java.io.*;
import java.util.Collection;
import java.util.ArrayList;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.math.Vector2;

public class Pathfinding extends ApplicationAdapter {
	final int WIDTH = 800;
	final int HEIGHT = 600;

	int map_max_y;
	int map_max_x;
	int tile_width;

	ShapeRenderer shapeRenderer;
	ExtendViewport viewport;

	BufferedReader in = null;
	Node[][] map = new Node[10000][10000];
	
	@Override
	public void create () {
		shapeRenderer = new ShapeRenderer();
		viewport = new ExtendViewport(WIDTH, HEIGHT);
		readInput();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
		shapeRenderer.begin(ShapeType.Filled);
		for(int y = 0; y < map_max_y; y++) {
			for(int x = 0; x < map_max_x; x++) {
				if(map[x][y].t_id > 0) {
					shapeRenderer.setColor(0, 0.1f * map[x][y].t_id, 1.0f/map[x][y].t_id, 1);
				} else {
					shapeRenderer.setColor(0.1f * map[x][y].cost, 0.1f * (10-map[x][y].cost), 0.1f * (10-map[x][y].cost), 1);
				}
				shapeRenderer.rect(-WIDTH/2+x*tile_width, HEIGHT/2-(y+1)*tile_width, tile_width, tile_width);
			}
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
					ArrayList<int[]> links = new ArrayList<int[]>();
					setLinks(map_max_x, map_max_y, map_max_x == nodes.length - 1, bottom,  links);

					switch(nodes[map_max_x].charAt(0)) {
						case 'F':
							map[map_max_x][map_max_y] = new Node(10, links);
							break;
						case 'T':
							map[map_max_x][map_max_y] = new Node(1, links, Integer.parseInt(nodes[map_max_x].substring(1)));
							break;
						default:
							map[map_max_x][map_max_y] = new Node(Integer.parseInt(nodes[map_max_x]), links);
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
			links.add(new int[] {x - 1, y});
		}
		if(y > 0) {
			links.add(new int[] {x, y - 1});
		}
		if(!right) {
			links.add(new int[] {x + 1, y});
		}
		if(!bottom) {
			links.add(new int[] {x, y + 1});
		}
	}
}
