package shapes;

import java.awt.Color;

import map.Map;
import engine.Camera;
import engine.Math3D;

public class Surface {

	double[][] coord;
	double[] normal;
	public static int surfaces;
	public double light;
	public double tempDistanceLight;
	public Color color;

	Surface(double x, double[] y, double[] z, boolean flipNormal) {
		int n = Math.min(y.length, z.length);
		coord = new double[n][];
		for (int i = 0; i < n; i++)
			coord[i] = new double[] { x, y[i], z[i] };
		setNormal(flipNormal);
	}

	Surface(double[] x, double y, double[] z, boolean flipNormal) {
		int n = Math.min(x.length, z.length);
		coord = new double[n][];
		for (int i = 0; i < n; i++)
			coord[i] = new double[] { x[i], y, z[i] };
		setNormal(flipNormal);
	}

	Surface(double[] x, double[] y, double z, boolean flipNormal) {
		int n = Math.min(x.length, y.length);
		coord = new double[n][];
		for (int i = 0; i < n; i++)
			coord[i] = new double[] { x[i], y[i], z };
		setNormal(flipNormal);
	}

	Surface(double[] x, double[] y, double[] z, boolean flipNormal) {
		int n = Math.min(Math.min(x.length, y.length), z.length);
		coord = new double[n][];
		for (int i = 0; i < n; i++)
			coord[i] = new double[] { x[i], y[i], z[i] };
		setNormal(flipNormal);
	}

	void setNormal(boolean flipNormal) {
		// uses first 3 coordinates
		// when flipNormal is false, normal points toward right hand rule
		normal = new double[3];

		// set coord 0 to origin
		double dx1 = coord[1][0] - coord[0][0];
		double dy1 = coord[1][1] - coord[0][1];
		double dz1 = coord[1][2] - coord[0][2];
		double dx2 = coord[2][0] - coord[0][0];
		double dy2 = coord[2][1] - coord[0][1];
		double dz2 = coord[2][2] - coord[0][2];

		// cross product
		normal[0] = dy1 * dz2 - dz1 * dy2;
		normal[1] = dz1 * dx2 - dx1 * dz2;
		normal[2] = dx1 * dy2 - dy1 * dx2;

		if (flipNormal) {
			normal = Math3D.transform(normal, -1, 0);
		}
	}

	void setColor(Color c) {
		color = c;
	}

	public double[][] toScreen(Camera camera) {
		// only the surfaces with normals pointing towards camera
		if (camera.facingTowards(normal, coord[0]) && camera.inView(coord)) {
			surfaces++;
			double[] x = new double[coord.length], y = new double[coord.length];
			double ox = 0, oy, oz;
			for (int i = 0; i < coord.length; i++) {
				// set origin at camera
				ox = coord[i][0] - camera.x;
				oy = coord[i][1] - camera.y;
				oz = coord[i][2] - camera.z;

				// camera angle
				// oa = Math.atan2(oy, ox) - camera.angle;
				// d = Math.sqrt(ox * ox + oy * oy);
				// oy = d * Math.sin(oa);
				// ox = d * Math.cos(oa);
				// oa = -camera.angle;
				// double cos = Math3D.xcos(oa), sin = Math3D.xsin(oa);
				double ox2 = ox * camera.angleCos - oy * camera.angleSin;
				oy = ox * camera.angleSin + oy * camera.angleCos;
				ox = ox2;

				// camera z angle
				// oa = Math.atan2(oz, ox) + camera.angleZ;
				// d = Math.sqrt(ox * ox + oz * oz);
				// oz = d * Math.sin(oa);
				// ox = d * Math.cos(oa);
				// oa = camera.angleZ;
				// cos = Math3D.xcos(oa);
				// sin = Math3D.xsin(oa);
				ox2 = ox * camera.angleZCos - oz * camera.angleZSin;
				oz = ox * camera.angleZSin + oz * camera.angleZCos;
				ox = ox2;

				// projection
				// if (ox <= 0)
				// System.out.println("revert");
				// return null;
				x[i] = oy / ox;
				y[i] = -oz / ox;
			}
			tempDistanceLight = light < 1 ? light : 1;
			tempDistanceLight *= Math3D.pow(Map.FOG, (int) ox);
			return new double[][] { x, y };
		}
		return null;
	}

	public static int getSurfaces() {
		int t = surfaces;
		surfaces = 0;
		return t;
	}
}
