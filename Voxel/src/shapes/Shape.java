package shapes;

abstract public class Shape {

	public double[] diffuse = new double[6];

	public abstract Surface[] paint();

	public abstract void updateLight(double[] light);

	// Surface[] surfaces;
	// addSurface
	// getSufraces
}
