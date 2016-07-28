package map;

import shapes.LightCube;
import shapes.Surface;
import engine.Camera;
import engine.Math3D;
import engine.Painter;

//before changing lighting to use subtraction rather than multiplication

public class MapMultDiffuse {

	public final static double DIFFUSE = .85;
	public final static double FOG = 0.97;
	// combine MIN_LIGHT with painter.MIN_LIGHT?
	public final static double MIN_LIGHT = 0.1;

	Cell[][][] grid;
	Cell[] hgrid;
	public int width, height, zheight;

	public MapMultDiffuse() {
		int size = 200;
		width = size;
		height = size;
		zheight = 10;

		grid = MapGenerator.castle();
		width = grid.length;
		height = grid[0].length;
		zheight = grid[0][0].length;
		// grid = MapGenerator.blastHeightMapCellsTops(size, zheight, .2);
		// grid = MapGenerator.wallsHeightMap(size, zheight);

		// hgrid = new Cell[width * height * zheight];
		// for (int x = 0; x < width; x++)
		// for (int y = 0; y < height; y++)
		// for (int z = 0; z < zheight; z++)
		// setCell(x, y, z, grid[x][y][z]);

		for (int i = 0; i < 50; i++) {
			int x = (int) (Math.random() * width);
			int y = (int) (Math.random() * height);
			addLight(x, y, zheight - 1, 15);
		}
		addLight(5, 5, zheight - 1, 15);
	}

	/*
	 * Cell getCell(int x, int y, int z) { int n = (z * height + y) * width + x;
	 * return hgrid[n]; }
	 * 
	 * void setCell(int x, int y, int z, Cell c) { int n = (z * height + y) *
	 * width + x; hgrid[n] = c; }
	 */

	// LIGHTING

	public void addLightInFrontOfCamera(Camera c) {
		int d = (int) (c.z * c.angleZCos / c.angleZSin);
		int x = (int) (c.x + d * c.angleCos);
		int y = (int) (c.y - d * c.angleSin);
		if (Math3D.inRange(x, y, width, height)) {
			int z = zheight - 1;
			addLight(x, y, z, 100);
		}
	}

	public void removeLightInFrontOfCamera(Camera c) {
		int d = (int) (c.z * c.angleZCos / c.angleZSin);
		int x = (int) (c.x + d * c.angleCos);
		int y = (int) (c.y - d * c.angleSin);
		if (Math3D.inRange(x, y, width, height)) {
			int z = zheight - 1;
			removeLight(x, y, z);
		}
	}

	public void removeAllLights() {
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				for (int z = 0; z < zheight; z++) {
					grid[x][y][z].light = 0;
					grid[x][y][z].dirLight = new double[6];
					if (grid[x][y][z].content != null)
						if (grid[x][y][z].content instanceof LightCube)
							grid[x][y][z].content = null;
						else
							grid[x][y][z].content
									.updateLight(grid[x][y][z].dirLight);
				}
	}

	void removeLight(int x, int y, int z) {
		// grid[x][y][z].removeContent();
		double power = grid[x][y][z].blacken();
		if (power != 0)
			removeLightHelper(x, y, z, power, -1, true);
	}

	void removeLightHelper(int x, int y, int z, double power, int direction,
			boolean source) {
		if (Math3D.inRange(x, y, z, width, height, zheight))
			System.out.println("remove: " + x + ", " + y + ", " + z);
		if ((source || grid[x][y][z].darken(power, direction))) {
			for (int dir = 0; dir < 6; dir++) {
				if ((source || dir != Math3D.DIRECTION[direction][3])
						&& power > MIN_LIGHT) {
					double dimmed = grid[x][y][z].getLight(power, dir);
					int newX = x + Math3D.DIRECTION[dir][0];
					int newY = y + Math3D.DIRECTION[dir][1];
					int newZ = z + Math3D.DIRECTION[dir][2];
					removeLightHelper(newX, newY, newZ, dimmed, dir, false);
				}
			}
		} else {
			System.out.println("add: " + x + ", " + y + ", " + z);
			addLightHelper(x, y, z, grid[x][y][z].light, -1, true);
		}
	}

	void addLight(int x, int y, int z, double power) {
		// if (Math3D.inRange(x, y, z, width, height, zheight)) {
		grid[x][y][z].addContent(new LightCube(x, y, z, power));
		grid[x][y][z].lightSource(power);
		addLightHelper(x, y, z, power, -1, true);
		// }
	}

	void addLightHelper(int x, int y, int z, double power, int direction,
			boolean source) {
		if (Math3D.inRange(x, y, z, width, height, zheight)
				&& (source || grid[x][y][z].brighten(power, direction))) {
			// System.out.println(lightCount);
			// System.out.println(x + ", " + y + ", " + z + ", " + power);
			for (int dir = 0; dir < 6; dir++) {
				if ((source || dir != Math3D.DIRECTION[direction][3])
						&& power > MIN_LIGHT) {
					power = grid[x][y][z].getLight(dir);
					int newX = x + Math3D.DIRECTION[dir][0];
					int newY = y + Math3D.DIRECTION[dir][1];
					int newZ = z + Math3D.DIRECTION[dir][2];
					addLightHelper(newX, newY, newZ, power, dir, false);
				}
			}
		}
	}

	// PAINTING

	public void paint(Painter p, Camera camera) {
		// extent of draw area
		// !!! math.max cull to size
		int cull = camera.cull();
		int x0 = (int) Math.max(0, camera.x - cull);
		int x1 = (int) Math.min(width - 1, camera.x + cull + 1);
		int y0 = (int) Math.max(0, camera.y - cull);
		int y1 = (int) Math.min(height - 1, camera.y + cull + 1);
		paintSubMap(x0, x1 - x0, y0, y1 - y0, camera, p);
		// paintSubMapIter(x0, x1, y0, y1);
		p.write("cull: " + cull, 5);
	}

	void paintSubMap(int x0, int dx, int y0, int dy, Camera camera, Painter p) {
		if (dx == 1 && dy == 1) {
			paintStack(x0, y0, camera, p);
		} else if (dx < 1 || dy < 1) {
		} else if (dx > dy) {
			int x1 = x0 + dx / 2;
			if (camera.x > x1) {
				paintSubMap(x0, x1 - x0, y0, dy, camera, p);
				paintSubMap(x1, x0 + dx - x1, y0, dy, camera, p);
			} else {
				paintSubMap(x1, x0 + dx - x1, y0, dy, camera, p);
				paintSubMap(x0, x1 - x0, y0, dy, camera, p);
			}

		} else {
			int y1 = y0 + dy / 2;
			if (camera.y > y1) {
				paintSubMap(x0, dx, y0, y1 - y0, camera, p);
				paintSubMap(x0, dx, y1, y0 + dy - y1, camera, p);
			} else {
				paintSubMap(x0, dx, y1, y0 + dy - y1, camera, p);
				paintSubMap(x0, dx, y0, y1 - y0, camera, p);
			}
		}
	}

	/*
	 * void paintSubMapIter(int x0, int x1, int y0, int y1) { double xCap =
	 * Math.min(camera.x, x1); for (int x = x0; x < xCap; x++) paintSubMapRow(x,
	 * y0, y1);
	 * 
	 * xCap = Math.max(camera.x, x0); for (int x = x1; x > xCap; x--)
	 * paintSubMapRow(x, y0, y1);
	 * 
	 * // if (camera.x < x1 && camera.x > x0) // paintSubMapRow((int) camera.x,
	 * y0, y1); }
	 * 
	 * void paintSubMapRow(int x, int y0, int y1) { double yCap =
	 * Math.min(camera.y, y1); for (int y = y0; y < yCap; y++) paintCell(x, y);
	 * 
	 * yCap = Math.max(camera.y, y0); for (int y = y1; y > yCap; y--)
	 * paintCell(x, y);
	 * 
	 * // if (camera.y < y1 && camera.y > y0) // paintCell(x, (int) camera.y); }
	 */

	void paintStack(int x, int y, Camera camera, Painter p) {
		int camZ = (int) Math3D.maxMin(camera.z, zheight - 1, 0);

		// under camera.z
		for (int z = 0; z < camZ; z++)
			paintCell(x, y, z, camera, p);

		// above camera.z
		for (int z = zheight - 1; z > camZ; z--)
			paintCell(x, y, z, camera, p);

		// at camera.z
		paintCell(x, y, camZ, camera, p);
	}

	void paintCell(int x, int y, int z, Camera camera, Painter p) {
		if (grid[x][y][z] != null
				&& (!(grid[x][y][z].content instanceof LightCube) || p.drawBorders)) {
			// double[] xysc = Math3D.toScreen(Math3D.origin(
			// Math3D.point(x, y, z), camera, 1));
			// p.rectangle(xysc, true);

			Surface[] ss = grid[x][y][z].paint();
			if (ss != null)
				for (Surface s : ss) {
					p.polygon(s.toScreen(camera), s.tempDistanceLight, s.color);
				}

			// double[][][] ss = map[x][y][z].paint(camera);
			// for (double[][] s : ss)
			// p.polygon(s);
		}
	}

}
