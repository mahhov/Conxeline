package shapes;

import engine.Camera;

public class FastCube implements FastPaintable {

	Point bottomLeftFront;
	double side;

	// oriented along axis
	public FastCube(double cx, double cy, double cz, double side) {
		this.side = side;
		side /= 2;
		double bottomZ = cz - side;
		double leftX = cx - side;
		double frontY = cy - side;
		bottomLeftFront = new Point(leftX, frontY, bottomZ);
	}

	public double[][][] paint(Camera camera) {
		// set blf at origin
		Point blf = new Point(bottomLeftFront.x - camera.x, bottomLeftFront.y
				- camera.y, bottomLeftFront.z - camera.z);
		blf = blf.toOriginRotation(camera);

		// how to get to other 7 points of cube from blf
		Point topP = new Point(0, 0, side).toOriginRotation(camera);
		Point rightP = new Point(side, 0, 0).toOriginRotation(camera);
		Point backP = new Point(0, side, 0).toOriginRotation(camera);

		// 7 other points
		Point brf = new Point(blf, rightP);
		Point blb = new Point(blf, backP);
		Point brb = new Point(blb, rightP);
		Point tlf = new Point(blf, topP);
		Point trf = new Point(tlf, rightP);
		Point tlb = new Point(tlf, backP);
		Point trb = new Point(tlb, rightP);

		// draw top
		double[][] top = Point.coords(tlf, trf, trb, tlb);
		double[][] front = Point.coords(tlf, trf, brf, blf);

		return new double[][][] { top, front };
	}

}
