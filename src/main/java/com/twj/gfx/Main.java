package com.twj.gfx;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class Main {

	public static void main(String[] args) throws IOException {
		
		World world = new World();
		
		world.setLightPosition(new Vector4d(0, 0, -6000, 1));

        InputStream is = Main.class.getClassLoader().getResourceAsStream("random.bmp");
		BufferedImage texture = ImageIO.read(is);

		final int COUNT = 11;
		final int EDGE_LENGTH = 256;
		
		for(int i = 0; i < COUNT; ++i) {
			for(int j = 0; j < COUNT; ++j) {
				for(int k = 0; k < COUNT; ++k) {
					
					// world coords, z increases +ve into the screen
					
					Vector4d leftBottomFront = new Vector4d(-EDGE_LENGTH, -EDGE_LENGTH, -EDGE_LENGTH, 1);
					Vector4d rightBottomFront = new Vector4d(EDGE_LENGTH, -EDGE_LENGTH,-EDGE_LENGTH, 1);
					Vector4d leftTopFront = new Vector4d(-EDGE_LENGTH, EDGE_LENGTH, -EDGE_LENGTH, 1);
					Vector4d rightTopFront = new Vector4d(EDGE_LENGTH, EDGE_LENGTH, -EDGE_LENGTH, 1);
					Vector4d leftBottomBack = new Vector4d(-EDGE_LENGTH, -EDGE_LENGTH, EDGE_LENGTH, 1);
					Vector4d rightBottomBack = new Vector4d(EDGE_LENGTH, -EDGE_LENGTH, EDGE_LENGTH, 1);
					Vector4d leftTopBack = new Vector4d(-EDGE_LENGTH, EDGE_LENGTH, EDGE_LENGTH, 1);
					Vector4d rightTopBack = new Vector4d(EDGE_LENGTH, EDGE_LENGTH, EDGE_LENGTH, 1);
					
					// translate
					double[][] translation = new double[4][4];
					translation[0][0] = 1;
					translation[1][0] = 0;
					translation[2][0] = 0;
					translation[3][0] = 0;
					translation[0][1] = 0;
					translation[1][1] = 1;
					translation[2][1] = 0;
					translation[3][1] = 0;
					translation[0][2] = 0;
					translation[1][2] = 0;
					translation[2][2] = 1;			
					translation[3][2] = 0;			
					translation[0][3] = (i-COUNT/2) * EDGE_LENGTH * 4;
					translation[1][3] = (j-COUNT/2) * EDGE_LENGTH * 4;
					translation[2][3] = (k-COUNT/2) * EDGE_LENGTH * 4;			
					translation[3][3] = 1;			
					Matrix translationMatrix = new Matrix(translation);	
					
					leftBottomFront = translationMatrix.times(new Matrix(leftBottomFront)).getAsVector();
					rightBottomFront = translationMatrix.times(new Matrix(rightBottomFront)).getAsVector();
					leftTopFront = translationMatrix.times(new Matrix(leftTopFront)).getAsVector();
					rightTopFront = translationMatrix.times(new Matrix(rightTopFront)).getAsVector();
					leftBottomBack = translationMatrix.times(new Matrix(leftBottomBack)).getAsVector();
					rightBottomBack = translationMatrix.times(new Matrix(rightBottomBack)).getAsVector();
					leftTopBack = translationMatrix.times(new Matrix(leftTopBack)).getAsVector();
					rightTopBack = translationMatrix.times(new Matrix(rightTopBack)).getAsVector();
					
					Color base = new Color(32,32,120);
					
					Polygon front = new Polygon(leftTopFront, rightTopFront, rightBottomFront, leftBottomFront, base.getRGB(), texture);
					Polygon back = new Polygon(rightTopBack, leftTopBack, leftBottomBack, rightBottomBack, base.getRGB(), texture);
					Polygon bottom = new Polygon(rightBottomFront, rightBottomBack, leftBottomBack, leftBottomFront, base.getRGB(), texture);
					Polygon top = new Polygon(leftTopFront, leftTopBack, rightTopBack, rightTopFront, base.getRGB(), texture);
					Polygon left = new Polygon(leftTopBack, leftTopFront, leftBottomFront, leftBottomBack, base.getRGB(), texture);
					Polygon right = new Polygon(rightTopFront, rightTopBack, rightBottomBack, rightBottomFront, base.getRGB(), texture);
					
					double leftBottomFrontBrightness = getVertexGouraudBrightness(left, bottom, front, leftBottomFront, world.getLightPosition());
					double rightBottomFrontBrightness = getVertexGouraudBrightness(right, bottom, front, rightBottomFront, world.getLightPosition());
					double leftTopFrontBrightness = getVertexGouraudBrightness(left, top, front, leftTopFront, world.getLightPosition());
					double rightTopFrontBrightness = getVertexGouraudBrightness(right, top, front, rightTopFront, world.getLightPosition());
					double leftBottomBackBrightness = getVertexGouraudBrightness(left, bottom, back, leftBottomBack, world.getLightPosition());
					double rightBottomBackBrightness = getVertexGouraudBrightness(right, bottom, back, rightBottomBack, world.getLightPosition());
					double leftTopBackBrightness = getVertexGouraudBrightness(left, top, back, leftTopBack, world.getLightPosition());
					double rightTopBackBrightness = getVertexGouraudBrightness(right, top, back, rightTopBack, world.getLightPosition());
					
					front.setGouraudBrightness(0, leftTopFrontBrightness);
					front.setGouraudBrightness(1, rightTopFrontBrightness);
					front.setGouraudBrightness(2, rightBottomFrontBrightness);
					front.setGouraudBrightness(3, leftBottomFrontBrightness);

					back.setGouraudBrightness(0, rightTopBackBrightness);
					back.setGouraudBrightness(1, leftTopBackBrightness);
					back.setGouraudBrightness(2, leftBottomBackBrightness);
					back.setGouraudBrightness(3, rightBottomBackBrightness);

					bottom.setGouraudBrightness(0, rightBottomFrontBrightness);
					bottom.setGouraudBrightness(1, rightBottomBackBrightness);
					bottom.setGouraudBrightness(2, leftBottomBackBrightness);
					bottom.setGouraudBrightness(3, leftBottomFrontBrightness);
					
					top.setGouraudBrightness(0, leftTopFrontBrightness);
					top.setGouraudBrightness(1, leftTopBackBrightness);
					top.setGouraudBrightness(2, rightTopBackBrightness);
					top.setGouraudBrightness(3, rightTopFrontBrightness);
					
					left.setGouraudBrightness(0, leftTopBackBrightness);
					left.setGouraudBrightness(1, leftTopFrontBrightness);
					left.setGouraudBrightness(2, leftBottomFrontBrightness);
					left.setGouraudBrightness(3, leftBottomBackBrightness);

					right.setGouraudBrightness(0, rightTopFrontBrightness);
					right.setGouraudBrightness(1, rightTopBackBrightness);
					right.setGouraudBrightness(2, rightBottomBackBrightness);
					right.setGouraudBrightness(3, rightBottomFrontBrightness);
					
					world.addPolygon(front);
					world.addPolygon(back);
					world.addPolygon(bottom);
					world.addPolygon(top);
					world.addPolygon(left);
					world.addPolygon(right);
				}
			}
		}
		
		world.buildTree();
		
		final MainWindow mw = new MainWindow(world);
		mw.setVisible(true);
	}
	
	static double getVertexGouraudBrightness(Polygon p1, Polygon p2, Polygon p3, Vector4d vertex, Vector4d lightSource) {
		Vector4d avgUnitVector = p1.getNormal().add(p2.getNormal().add(p3.getNormal())).unitVector();
		Vector4d v = vertex.minus(lightSource);
		return (1 - v.unitVector().dotProduct(avgUnitVector))/2 / (0.2 + v.magnitude()*v.magnitude()/20000000);
	}
}
