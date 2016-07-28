package engine;

import map.MapGeneratorOld;

public class Bingine {

	// uses different lighting
	// painting order fix
	// uses culling to support large maps
	// new lighting requires different map storage (double v boolean) and mapGen
	// paintSubMapIter (v paintSubMap) has better performance, but has glitch

	final int FRAME_SIZE = 800;

	double[][][] map;
	Camera camera; // x,y,z, angle, moveSpeed, angleSpeed
	Painter p;
	Control c;
	int cull = 150;

	Bingine() {
		System.out.println("created Bingine");
		Math3D.loadTrig(100);
		double[] sun = new double[] { 15, 15, 20 };
		map = MapGeneratorOld.blastHeightMapLighted(200, 10, .2, sun);
		camera = new Camera();
		c = new Control(FRAME_SIZE);
		p = new Painter(FRAME_SIZE, c);
	}

	void paintMap() {
		int x0 = (int) Math.max(0, camera.x - cull);
		int x1 = (int) Math.min(map.length, camera.x + cull);
		int y0 = (int) Math.max(0, camera.y - cull);
		int y1 = (int) Math.min(map[0].length, camera.y + cull);
		paintSubMap(x0, x1 - x0, y0, y1 - y0);
	}

	void paintSubMap(int x0, int dx, int y0, int dy) {
		// System.out.println("recursing " + x0 + " ( " + dx + " ), " + y0
		// + "+ ( " + dy + " )");
		if (dx == 1 && dy == 1) {
			// System.out.println("painting " + x0 + ", " + y0);
			paintCell(x0, y0);
			// for (int z = map[x0][y0].length - 1; z >= 0; z--)
			// if (map[x0][y0][z] > 0) {
			// double[] xysc = Math3D.toScreen(Math3D.origin(
			// Math3D.point(x0, y0, z), camera, map[x0][y0][z]));
			// p.rectangle(xysc, true);
			// if (onlyPaintTop)
			// break;
			// }
		} else if (dx > dy) {
			int x1 = x0 + dx / 2;
			if (camera.x > x1) {
				paintSubMap(x0, x1 - x0, y0, dy);
				paintSubMap(x1, x0 + dx - x1, y0, dy);
			} else {
				paintSubMap(x1, x0 + dx - x1, y0, dy);
				paintSubMap(x0, x1 - x0, y0, dy);
			}

		} else {
			int y1 = y0 + dy / 2;
			if (camera.y > y1) {
				paintSubMap(x0, dx, y0, y1 - y0);
				paintSubMap(x0, dx, y1, y0 + dy - y1);
			} else {
				paintSubMap(x0, dx, y1, y0 + dy - y1);
				paintSubMap(x0, dx, y0, y1 - y0);
			}
		}
	}

	void paintSubMapIter(int x0, int dx, int y0, int dy) {
		// top
		for (int y = y0; y < camera.y; y++)
			for (int x = x0; x < x0 + dx; x++)
				paintCell(x, y);
		// botom
		for (int y = y0 + dy - 1; y > camera.y; y--)
			for (int x = x0; x < x0 + dx; x++)
				paintCell(x, y);
		// left
		int y = (int) camera.y;
		for (int x = x0; x < camera.x; x++)
			paintCell(x, y);
		// right
		for (int x = x0 + dx - 1; x > camera.x; x--)
			paintCell(x, y);
		// center
		int x = (int) camera.x;
		paintCell(x, y);
	}

	void paintCell(int x, int y) {
		for (int z = map[x][y].length - 1; z >= 0; z--)
			if (map[x][y][z] > 0) {
				double[] xysc = Math3D.toScreen(Math3D.origin(
						Math3D.point(x, y, z), camera, map[x][y][z]));
				p.rectangle(xysc, true);
			}
	}

	void cameraMove() {
		if (c.getKey('w')) {
			camera.x += Math.cos(camera.angle) * camera.moveSpeed;
			camera.y += Math.sin(camera.angle) * camera.moveSpeed;
		}

		if (c.getKey('s')) {
			camera.x -= Math.cos(camera.angle) * camera.moveSpeed;
			camera.y -= Math.sin(camera.angle) * camera.moveSpeed;
		}

		if (c.getKey('a')) {
			camera.x -= Math.cos(camera.angle + Math.PI / 2) * camera.moveSpeed;
			camera.y -= Math.sin(camera.angle + Math.PI / 2) * camera.moveSpeed;
		}

		if (c.getKey('d')) {
			camera.x += Math.cos(camera.angle + Math.PI / 2) * camera.moveSpeed;
			camera.y += Math.sin(camera.angle + Math.PI / 2) * camera.moveSpeed;
		}

		if (c.getKey('q')) {
			camera.angle -= camera.angleSpeed;
		}

		if (c.getKey('e')) {
			camera.angle += camera.angleSpeed;
		}

		if (c.getKey('z')) {
			camera.z += camera.moveSpeed;
		}

		if (c.getKey('x')) {
			camera.z -= camera.moveSpeed;
		}

		camera.angle += c.getMouseMoveX() * camera.angleSpeed;
		camera.angleZ += c.getMouseMoveY() * camera.angleSpeed;
	}

	void begin() {
		while (true) {
			long begin = System.currentTimeMillis();

			cameraMove();
			paintMap();
			p.paint();
			wait(10);

			long end = System.currentTimeMillis();
			System.out.println("fps " + (1000 / (end - begin)));

		}
	}

	public void wait(int howLong) {
		try {
			Thread.sleep(howLong);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Bingine().begin();
	}
}
