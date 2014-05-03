package com.twj.gfx;

public class Camera {
	private Vector4d position;
	private Vector4d direction;
	private double x_rot;
	private double y_rot;
	
	Camera() {
		position = new Vector4d(0, 0, 0, 1);
		direction = new Vector4d(0, 0, 1, 0);
	}
	
	Vector4d getPosition() {
		return position;
	}
	
	void move(double units) {
		position = position.add(direction.scale(units));
	}
	
	void rotateX(double theta) {
		x_rot += theta;
		
		recalcDirection();
	}
	
	void rotateY(double theta) {
		y_rot += theta;
		
		recalcDirection();
	}
	
	void recalcDirection() {
		
		// rotation around x
		double[][] rotationX = new double[4][4];
		rotationX[0][0] = 1;
		rotationX[1][0] = 0;
		rotationX[2][0] = 0;
		rotationX[3][0] = 0;
		rotationX[0][1] = 0;
		rotationX[1][1] = Math.cos(x_rot);
		rotationX[2][1] = Math.sin(x_rot);
		rotationX[3][1] = 0;
		rotationX[0][2] = 0;
		rotationX[1][2] = -Math.sin(x_rot);
		rotationX[2][2] = Math.cos(x_rot);			
		rotationX[3][2] = 0;
		rotationX[0][3] = 0;
		rotationX[1][3] = 0;
		rotationX[2][3] = 0;
		rotationX[3][3] = 1;
		Matrix rotationMatrixX = new Matrix(rotationX);
		
		// rotation around y
		double[][] rotationY = new double[4][4];
		rotationY[0][0] = Math.cos(y_rot);
		rotationY[1][0] = 0;
		rotationY[2][0] = -Math.sin(y_rot);
		rotationY[3][0] = 0;
		rotationY[0][1] = 0;
		rotationY[1][1] = 1;
		rotationY[2][1] = 0;
		rotationY[3][1] = 0;
		rotationY[0][2] = Math.sin(y_rot);
		rotationY[1][2] = 0;
		rotationY[2][2] = Math.cos(y_rot);
		rotationY[3][2] = 0;
		rotationY[0][3] = 0;
		rotationY[1][3] = 0;
		rotationY[2][3] = 0;
		rotationY[3][3] = 1;
		Matrix rotationMatrixY = new Matrix(rotationY);
		
		Matrix startingDirection = new Matrix(new Vector4d(0, 0, 1, 0));
		
		direction = rotationMatrixY.times(rotationMatrixX.times(startingDirection)).getAsVector();
	}
	
	Vector4d getDirection() {
		return direction;
	}
	
	double getRotX() {
		return x_rot;
	}
	
	double getRotY() {
		return y_rot;
	}

	public void up(double moveStep) {
		position.set(1, position.get(1) + moveStep);
		
	}

	public void down(double moveStep) {
		position.set(1, position.get(1) - moveStep);
	}

	
	public void strafeLeft(double moveStep) {
		Vector4d strafe = new Vector4d(0, 1, 0, 0).crossProduct(direction).unitVector().scale(-moveStep);
		position = position.add(strafe);
	}

	public void strafeRight(double moveStep) {
		Vector4d strafe = new Vector4d(0, 1, 0, 0).crossProduct(direction).unitVector().scale(moveStep);
		position = position.add(strafe);
		
	}
}
