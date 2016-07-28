package engine;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Control implements MouseMotionListener, KeyListener, MouseListener {
	final int FRAME_SIZE;

	Robot robot;

	Keys keys;
	Mouse mouse;

	static final int PRESS = 1, DOWN = 2, RELEASE = 3, UP = 4;
	static final int LEFT = 1, MIDDLE = 2, RIGHT = 0;

	class Keys {

		Key[] key;

		Keys(char[] keyChars) {
			key = new Key[keyChars.length];
			for (int i = 0; i < keyChars.length; i++)
				key[i] = new Key(keyChars[i]);
		}

		void press(char c) {
			change(c, true);
		}

		void release(char c) {
			change(c, false);
		}

		void change(char c, boolean on) {
			for (Key k : key)
				if (k.c == c)
					k.on = on;
		}

		boolean get(char c) {
			for (Key k : key)
				if (k.c == c)
					return k.on;
			return false;
		}

		class Key {
			char c;
			boolean on;

			Key(char c) {
				this.c = c;
			}
		}

	}

	class Mouse {
		double moveX, moveY; // how much moved since last get
		// state (not change)!
		int[] mouseState;

		Mouse() {
			mouseState = new int[3];
		}

		void move(int x, int y) {
			this.moveX = x;
			this.moveY = y;
		}

		void setButton(int button, int value) {
			if (button == MouseEvent.BUTTON1)
				mouseState[LEFT] = value;
			else if (button == MouseEvent.BUTTON3)
				mouseState[RIGHT] = value;
			else if (button == MouseEvent.BUTTON2)
				mouseState[MIDDLE] = value;
		}

		public int get(int m) {
			int t = mouseState[m];
			if (t == PRESS)
				mouseState[m] = DOWN;
			else if (t == RELEASE)
				mouseState[m] = UP;
			return t;
		}
	}

	Control(int frameSize) {
		FRAME_SIZE = frameSize;

		keys = new Keys(new char[] { 'w', 'a', 's', 'd', 'q', 'e', 'z', 'x',
				'r', 'f', 'c', 'v' });
		mouse = new Mouse();

		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		robot.mouseMove(FRAME_SIZE / 2, FRAME_SIZE / 2);
	}

	boolean getKey(char c) {
		return keys.get(c);
	}

	double getMouseMoveX() {
		double t = mouse.moveX;
		mouse.moveX = 0;
		return t;
	}

	double getMouseMoveY() {
		double t = mouse.moveY;
		mouse.moveY = 0;
		return t;
	}

	int getMouseState(int m) {
		return mouse.get(m);
	}

	boolean getMouseDown(int m) {
		int t = mouse.get(m);
		return t == PRESS || t == DOWN;
	}

	// int getLeftMouseButton() {
	// return mouse.get(LEFT);
	// }
	//
	// int getRightMouseButton() {
	// return mouse.get(RIGHT);
	// }
	//
	// int getMiddleMouseButton() {
	// return mouse.get(MIDDLE);
	// }

	public void keyPressed(KeyEvent e) {
		keys.press(e.getKeyChar());
	}

	public void keyReleased(KeyEvent e) {
		keys.release(e.getKeyChar());
	}

	public void keyTyped(KeyEvent e) {

	}

	public void mouseDragged(MouseEvent e) {
		mouse.moveX += (e.getX() - FRAME_SIZE / 2.0) / FRAME_SIZE * 10;
		mouse.moveY += (e.getY() - FRAME_SIZE / 2.0) / FRAME_SIZE * 10;
		robot.mouseMove(FRAME_SIZE / 2, FRAME_SIZE / 2);
	}

	public void mouseMoved(MouseEvent e) {
		mouse.moveX += (e.getX() - FRAME_SIZE / 2.0) / FRAME_SIZE * 10;
		mouse.moveY += (e.getY() - FRAME_SIZE / 2.0) / FRAME_SIZE * 10;
		robot.mouseMove(FRAME_SIZE / 2, FRAME_SIZE / 2);
	}

	public void mouseClicked(MouseEvent e) {

	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void mousePressed(MouseEvent e) {
		mouse.setButton(e.getButton(), PRESS);
	}

	public void mouseReleased(MouseEvent e) {
		mouse.setButton(e.getButton(), RELEASE);
	}

}
