package com.twj.gfx;

import java.awt.image.BufferedImage;

public class Polygon {
	
	private final int rgb;
	private final Vector4d[] pts = new Vector4d[4];
	private double[] gouraudBrightness = new double[4];
	private final Vector4d normal;
	private final Vector4d centrePoint;
	private final double D;
	private final BufferedImage texture;
	
	public Polygon(Vector4d a, Vector4d b, Vector4d c, Vector4d d, int rgb, BufferedImage texture) {
		
		pts[0] = a;
		pts[1] = b;
		pts[2] = c;
		pts[3] = d;
		
		this.normal = b.minus(a).crossProduct(c.minus(a)).unitVector();
		
		Vector4d centre = a.add(b).add(c).add(d).scale(0.25);
		centre.set(3, 1);
		this.centrePoint = centre;
		
		D = -this.normal.dotProduct(this.centrePoint);
		
		this.rgb = rgb;
		this.texture = texture;
	}
	
	public Vector4d[] getPoints() {
		return pts;
	}
	
	public Vector4d getPoint(int idx) {
		return pts[idx];
	}

	
	public Vector4d getNormal() {
		return normal;
	}
	
	public Vector4d getCentrePoint() {
		return centrePoint;
	}
	
	public double D() {
		return this.D;
	}
	
	public int getRGB() {
		return rgb;
	}

	public BufferedImage getTexture() {
		return texture;
	}

	public double getGouraudBrightness(int vertexIdx) {
		return gouraudBrightness[vertexIdx];
	}

	public void setGouraudBrightness(int vertexIdx, double brightness) {
		gouraudBrightness[vertexIdx] = brightness;
	}
}
