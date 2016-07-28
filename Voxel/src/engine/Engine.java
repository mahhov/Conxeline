package engine;

import map.MapGeneratorOld;

public class Engine {

	final int FRAME_SIZE = 800;

	double[] sun;

	boolean[][][] map;
	Camera camera; // x,y,z, angle, moveSpeed, angleSpeed
	Painter p;
	Control c;

	Engine() {
		System.out.println("created ENGINE");
		Math3D.loadTrig(100);
		map = MapGeneratorOld.blastHeightMap(100, 10, .2);
		sun = new double[] { 15, 15, 40 };
		camera = new Camera();
		c = new Control(FRAME_SIZE);
		p = new Painter(FRAME_SIZE, c);
	}

	void paintMap(boolean onlyPaintTop, boolean lighting) {
		for (int x = 0; x < map.length; x++)
			for (int y = 0; y < map[x].length; y++)
				for (int z = map[x][y].length - 1; z >= 0; z--)
					if (map[x][y][z]) {
						double[] xysc = Math3D.toScreen(Math3D.origin(
								Math3D.point(x, y, z), camera, sun));
						p.rectangle(xysc, lighting);
						if (onlyPaintTop)
							break;
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
			cameraMove();
			paintMap(false, true);
			p.paint();
			wait(10);
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
		new Engine().begin();
	}

}
