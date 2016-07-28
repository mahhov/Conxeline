package shapes;

import engine.Camera;

public class Point {
	double x, y, z;

	Point(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	Point(Point p, double dx, double dy, double dz) {
		x = p.x + dx;
		y = p.y + dy;
		z = p.z + dz;
	}

	Point(Point p, Point dp) {
		x = p.x + dp.x;
		y = p.y + dp.y;
		z = p.z + dp.z;
	}

	Point toOriginRotation(Camera camera) {
		// angle
		double x2 = x * camera.angleCos - y * camera.angleSin;
		y = x * camera.angleSin + y * camera.angleCos;
		x = x2;

		// angle Z
		x2 = x * camera.angleZCos - z * camera.angleZSin;
		z = x * camera.angleZSin + z * camera.angleZCos;
		x = x2;

		return new Point(x, y, z);
	}

	static double[][] coords(Point a, Point b, Point c, Point d) {
		// double[] x = new double[] { a.y / a.x, b.y / b.x, c.y / c.x, d.y /
		// d.x };
		// double[] y = new double[] { a.z / a.x, b.z / b.x, c.z / c.x, d.z /
		// d.x };
		// return new double[][] {x,y};
		return new double[][] {
				new double[] { a.y / a.x, b.y / b.x, c.y / c.x, d.y / d.x },
				new double[] { a.z / a.x, b.z / b.x, c.z / c.x, d.z / d.x } };
	}

}
