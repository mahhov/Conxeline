package engine;

import map.Map;

public class Camera {
	public double x, y, z;
	double angle, angleZ;
	double moveSpeed, angleSpeed;
	double[] normal; // , normalLeft, normalRight;
	public double angleSin, angleCos, angleZSin, angleZCos;
	double angleViewCos, angleViewSin, angleView;
	int maxCull;
	double angleViewTop, angleViewTopCos, angleViewTopSin;

	Camera() {
		maxCull = (int) (Math.log(Painter.MIN_LIGHT) / Math.log(Map.FOG)) + 1;
		if (maxCull > 80) {
			System.out.println("culling reduced to 80 from " + maxCull);
			maxCull = 80;
		}

		double sqrt5 = Math.sqrt(5);
		angleViewCos = 2.0 / sqrt5;
		angleViewSin = 1.0 / sqrt5;
		angleView = Math.atan2(angleViewSin, angleViewCos);

		angleViewTop = angleView - Math.PI / 2;
		angleViewTopCos = Math3D.xcos(angleViewTop);
		angleViewTopSin = Math3D.xsin(angleViewTop);

		moveSpeed = 2;
		angleSpeed = .3;

		normal = new double[3];
		// normalLeft = new double[2];
		// normalRight = new double[2];

		z = 20.1;
		angleZ = 1;

		update();
	}

	public boolean facingTowards(double[] normal, double[] position) {
		// vectorize camera direction
		// double dirx = Math.cos(angleZ) * Math.cos(angle);
		// double diry = Math.cos(angleZ) * Math.sin(angle);
		// double dirz = Math.sin(angleZ);

		// dot product unit vectors
		// return true;
		return 0 < (Math3D.dotProduct(x - position[0], y - position[1], z
				- position[2], normal[0], normal[1], normal[2]));
	}

	public void update() {
		// z = Math3D.maxMin(z, 27, 0);
		// angleZ = Math3D.maxMin(angleZ, Math.PI / 2, .5);
		angleZ = Math3D.maxMin(angleZ, Math.PI / 2, 0);
		// angleZ = Math.PI * z / 27 / 2;

		// sin/cos
		angleSin = Math3D.xsin(-angle);
		angleCos = Math3D.xcos(-angle);
		angleZSin = Math3D.xsin(angleZ);
		angleZCos = Math3D.xcos(angleZ);

		// normal 2.0
		double angleZBottomPlaneSin = Math3D.sin2(angleZSin, angleZCos,
				angleViewTopSin, angleViewTopCos);
		double angleZBottomPlaneCos = Math3D.cos2(angleZSin, angleZCos,
				angleViewTopSin, angleViewTopCos);

		// alternate for normal 2.0
		// slower than above if you loop here a lot (99,999,999)
		// else, no noticeable speed difference
		// double angleZBottomPlane = angleZ + angleView - Math.PI / 2;
		// double angleZBottomPlaneSin = Math3D.xsin(angleZBottomPlane);
		// double angleZBottomPlaneCos = Math3D.xcos(angleZBottomPlane);

		normal[0] = angleCos * angleZBottomPlaneCos;
		normal[1] = -angleSin * angleZBottomPlaneCos;
		normal[2] = -angleZBottomPlaneSin;

		// old method for normal
		// no noticed speed loss even with tremendous loop (479,999,999)
		// because they work at same speed, but this results in more inView pass
		// no polygon increase (extra surfaces cut away later)
		// calculated surface significant increase
		// normal[0] = angleCos * angleZCos;
		// normal[1] = -angleSin * angleZCos;
		// normal[2] = -angleZSin;

		// side normals
		// double AsAvc = -angleSin * angleViewCos;
		// double AcAvs = angleCos * angleViewSin;
		// double AcAvc = angleCos * angleViewCos;
		// double AsAvs = -angleSin * angleViewSin;
		// normalRight[0] = -(AsAvc + AcAvs);
		// normalRight[1] = (AcAvc - AsAvs);
		// normalLeft[0] = (AsAvc - AcAvs);
		// normalLeft[1] = -(AcAvc + AsAvs);
	}

	public boolean inView(double[][] coord) {
		// double[] c = coord[0];
		for (double[] c : coord) {
			double dx = x - c[0];
			double dy = y - c[1];
			// not off right
			// if (normalRight[0] * dx + normalRight[1] * dy < 0)
			// return false;

			// not off left
			// if (normalLeft[0] * dx + normalLeft[1] * dy < 0)
			// return false;

			// in front
			if (Math3D.dotProduct(dx, dy, z - c[2], normal[0], normal[1],
					normal[2]) < 0)
				return true;
		}
		return false;
	}

	public int cull() {
		// maxCull=80;

		// farthest reaching ray
		double angleZTopPlane = angleZ - angleView;
		double angleZTopPlaneSin = Math3D.xsin(angleZTopPlane);
		double angleZTopPlaneCos = Math3D.xcos(angleZTopPlane);

		// alternate way for getting ray, no noticed speed difference
		// double angleZTopPlaneSin = Math3D.sin2(angleZSin, angleZCos,
		// -angleViewSin, angleViewCos);
		// double angleZTopPlaneCos = Math3D.cos2(angleZSin, angleZCos,
		// -angleViewSin, angleViewCos);

		// if horizon in view (infinite vision)
		if (angleZTopPlaneSin <= 0)
			return maxCull;

		// how far away ray hits 0-z-plane
		double distance = z * angleZTopPlaneCos / angleZTopPlaneSin;

		// expand to see corners
		// distance /= Math.max(Math.abs(Math3D.xsin(angle)),
		// Math.abs(Math3D.xcos(angle)));

		// old, safe way of expansion, speed loss
		distance *= Math3D.sqrt2;

		// no need to see farther than fog's extend
		return (int) Math.min((distance), maxCull);
	}
}
