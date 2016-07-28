package engine;

import map.Map;
import shapes.Surface;

// convex grided engine 
public class Conxeline {

	// uses culling to support large maps
	// convex engine requires new map storage
	// paintSubMapIter (v paintSubMap) has better performance, but has glitch

	final int FRAME_SIZE = 800;

	Map map;
	Camera camera;
	Painter p;
	Control c;

	Conxeline() {
		System.out.println("CONXELINE");
		map = new Map();
		camera = new Camera();
		c = new Control(FRAME_SIZE);
		p = new Painter(FRAME_SIZE, c);
	}

	void cameraMove() {
		// KEYBOARD CAMERA MOVEMENT

		if (c.getKey('w')) {
			camera.x += camera.angleCos * camera.moveSpeed;
			camera.y -= camera.angleSin * camera.moveSpeed;
		}

		if (c.getKey('s')) {
			camera.x -= camera.angleCos * camera.moveSpeed;
			camera.y += camera.angleSin * camera.moveSpeed;
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
			camera.angle -= camera.angleSpeed / 2;
		}

		if (c.getKey('e')) {
			camera.angle += camera.angleSpeed / 2;
		}

		if (c.getKey('z')) {
			camera.z += camera.moveSpeed / 2;
		}

		if (c.getKey('x')) {
			camera.z -= camera.moveSpeed / 2;
		}

		if (c.getKey('r')) {
			camera.angleZ -= camera.angleSpeed / 2;
		}

		if (c.getKey('f')) {
			camera.angleZ += camera.angleSpeed / 2;
		}

		// MOUSE CAMERA MOVEMENT

		camera.angle += c.getMouseMoveX() * camera.angleSpeed;
		camera.angleZ += c.getMouseMoveY() * camera.angleSpeed;

		camera.x = Math3D.maxMin(camera.x, map.width - 1, 0);
		camera.y = Math3D.maxMin(camera.y, map.height - 1, 0);

		// RECALCULATE CAMERA DATA
		camera.update();

		// LIGHTING CONTROLS

		if (c.getMouseDown(Control.MIDDLE))
			map.fillColumnInFrontOfCameraWithCubes(camera);

		if (c.getMouseDown(Control.LEFT))
			map.addLightInFrontOfCamera(camera);

		if (c.getMouseState(Control.RIGHT) == Control.PRESS)
			map.removeAllLights();

		// PAINTER MODIFY

		if (c.getKey('c'))
			p.drawBorders = !p.drawBorders;

		if (c.getKey('v'))
			p.whiteMode = !p.whiteMode;
	}

	void begin() {
		int fps = -1;

		// wait(1000);
		while (true) {
			long begin = System.currentTimeMillis();

			cameraMove();
			map.paint(p, camera);

			p.write("CONXELINE ENGINE", 1);
			p.write("fps: " + fps, 2);
			p.write("polygons: " + Surface.getSurfaces(), 3);
			p.paint();

			// wait(10);

			long end = System.currentTimeMillis() + 1;
			fps = (int) (1000 / (end - begin));
		}
	}

	public static void wait(int howLong) {
		try {
			Thread.sleep(howLong);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Math3D.load();
		new Conxeline().begin();
	}
}

// culling that differentiates each side of camera
// examine why polygon/surface count aren't 0 when just off edge of map
// why is bottom side of map lighted?
// negative/positive for camera move and camera angle sin/cos?
// sky
// dynamic lights, changable enviornment, castle map
// particles
