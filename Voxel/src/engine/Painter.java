package engine;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Painter extends JFrame {

	BufferedImage canvas;
	Graphics2D brush;
	final int FRAME_SIZE;
	int count;
	Color cFill = new Color(100, 0, 100);
	Color cLine = new Color(100, 200, 100);
	Color cBlack = new Color(0, 0, 0);
	public static final double MIN_LIGHT = .02;
	public boolean drawBorders;
	public boolean whiteMode;

	Painter(int frameSize, Control control) {
		FRAME_SIZE = frameSize;

		setResizable(false);
		setSize(FRAME_SIZE, FRAME_SIZE);
		setUndecorated(true);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		canvas = new BufferedImage(FRAME_SIZE, FRAME_SIZE,
				BufferedImage.TYPE_INT_RGB);
		System.out.println("accelerated: "
				+ canvas.getCapabilities(null).isAccelerated());

		brush = (Graphics2D) canvas.getGraphics();
		brush.setFont(new Font("monospaced", Font.PLAIN, 25));

		addKeyListener(control);
		addMouseMotionListener(control);
		addMouseListener(control);
	}

	public void paint() {
		paint(getGraphics());
	}

	public void paint(Graphics g) {
		if (g != null) {
			write("paint count: " + count, 4);
			count = 0;

			// draw
			g.drawImage(canvas, 0, 0, getWidth(), getHeight(), null);

			// erase
			brush.setColor(Color.black);
			brush.fillRect(0, 0, getWidth(), getHeight());
		}
	}

	public void write(String s, int line) {
		brush.setColor(Color.BLUE);
		brush.drawString(s, 10, 30 * line);
	}

	public void rectangle(double[] xysc, boolean lighting) {
		if (xysc != null) {
			if (xysc[0] > -.5 && xysc[0] < .5 && xysc[1] < .5 && xysc[1] > -.5) {
				count++;
				if (lighting)
					brush.setColor(new Color(0, 0, (int) (xysc[3] * 250)));
				else
					brush.setColor(new Color(0, 0, 100));
				brush.fillRect(
						(int) ((xysc[0] - xysc[2] / 2 + 0.5) * FRAME_SIZE),
						(int) ((xysc[1] - xysc[2] / 2 + 0.5) * FRAME_SIZE),
						(int) (xysc[2] * FRAME_SIZE),
						(int) (xysc[2] * FRAME_SIZE));
			}
		}
	}

	public void polygon(double[][] xy) {
		if (xy != null) {
			boolean draw = false;
			for (int i = 0; i < xy[0].length; i++)
				if (xy[0][i] > -.5 && xy[0][i] < .5 && xy[1][i] < .5
						&& xy[1][i] > -.5) {
					draw = true;
					break;
				}
			if (draw) {
				// rectangle(new double[] { xy[0][0], xy[1][0], .03 }, false);
				count++;
				int[][] xyScaled = Math3D.transform(xy, FRAME_SIZE,
						FRAME_SIZE / 2);
				brush.setColor(cFill);
				brush.fillPolygon(xyScaled[0], xyScaled[1], xyScaled[0].length);
				brush.setColor(cLine);
				brush.drawPolygon(xyScaled[0], xyScaled[1], xyScaled[0].length);
			}
		}
	}

	void setColor(double light) {
		if (light < MIN_LIGHT)
			brush.setColor(Color.black);
		else {
			int lightConvert = (int) Math.min(250, light * 250);
			brush.setColor(new Color(lightConvert, 0, lightConvert));
		}
	}

	void setColor(double light, Color color) {
		if (light < MIN_LIGHT)
			brush.setColor(Color.black);
		else {
			if (whiteMode)
				color = Color.WHITE;
			light = Math.min(1, light);
			brush.setColor(new Color((int) (light * color.getRed()),
					(int) (light * color.getGreen()), (int) (light * color
							.getBlue())));
		}
	}

	public void polygon(double[][] xy, double light, Color color) {
		if (xy != null) {
			boolean draw = false;
			for (int i = 0; i < xy[0].length; i++)
				if (xy[0][i] > -.5 && xy[0][i] < .5 && xy[1][i] < .5
						&& xy[1][i] > -.5) {
					draw = true;
					break;
				}
			if (draw) {
				count++;
				int[][] xyScaled = Math3D.transform(xy, FRAME_SIZE,
						FRAME_SIZE / 2);
				setColor(light, color);
				brush.fillPolygon(xyScaled[0], xyScaled[1], xyScaled[0].length);
				if (drawBorders) {
					setColor(light, cLine);
					brush.drawPolygon(xyScaled[0], xyScaled[1],
							xyScaled[0].length);
				}
			}
		}
	}

}