package com.twj.gfx;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;


import com.twj.gfx.GraphicUtils.EdgeInfo;
import com.twj.gfx.GraphicUtils.PerspectiveEdgeInfo;

@SuppressWarnings("serial")
public class GfxView extends Canvas {
	
	private World world;
	private Camera camera = new Camera();
	
    private int bufferWidth;
    private int bufferHeight;
    private BufferedImage bufferImage;
    private Graphics bufferGraphics;
    private GraphicUtils.EdgeInfo[][] edgeInfo;
    private GraphicUtils.PerspectiveEdgeInfo[][] perspectiveEdgeInfo;
	private double[][] zbuffer;
    
    private final double left = -35;
    private final double right = +35;
    private final double top = +20;
    private final double bottom = -20;
    private double near = -50;
    private final double far = -50000;
    
    private final double moveStep = 100;
    private final double rotateAngleStep = Math.PI/128;
    
	double[][] rotationX = new double[4][4];
	double[][] rotationY = new double[4][4];
	double[][] translation = new double[4][4];
	Matrix rotationMatrixX = new Matrix(rotationX);
	Matrix rotationMatrixY = new Matrix(rotationY);
	Matrix translationMatrix = new Matrix(translation);		
	
    Matrix perspectiveMatrix;

	Vector4d[] polyAsVec = new Vector4d[4];
	int[] x_coords = new int[4];
	int[] y_coords = new int[4];
	
	private enum PMode {
		WIREFRAME,
		FLAT,
		GOURAUD,
		AFFINE,
		PERSPECTIVE
	};
	
	PMode pmode = PMode.GOURAUD;
	
	public GfxView(World world) throws IOException {
		super();
		
		setSize(1280, 720);
		
		this.world = world;
		this.camera.getPosition().set(2, -7000);
		
		
		addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				
				if(e.getKeyChar() == 'w') {
					camera.move(moveStep);
				}
				else if(e.getKeyChar() == 's') {
					camera.move(-moveStep);
				}
				else if(e.getKeyChar() == 'q') {
					camera.up(moveStep);
				}
				else if(e.getKeyChar() == 'a') {
					camera.down(moveStep);
				}
				else if(e.getKeyChar() == 'z') {
					camera.strafeLeft(moveStep);
				}
				else if(e.getKeyChar() == 'x') {
					camera.strafeRight(moveStep);
				}
				else if(e.getKeyChar() == 'o') {
					camera.rotateY(-rotateAngleStep);
				}
				else if(e.getKeyChar() == 'p') {
					camera.rotateY(rotateAngleStep);
				}
				else if(e.getKeyChar() == 'i') {
					camera.rotateX(rotateAngleStep);
				}
				else if(e.getKeyChar() == 'k') {
					camera.rotateX(-rotateAngleStep);
				}
				else if(e.getKeyChar() == ',') {
					near -=1;
				}
				else if(e.getKeyChar() == '.') {
					near +=1;
				}
				else if(e.getKeyChar() == '`') {
					if(pmode == PMode.WIREFRAME)
						pmode = PMode.FLAT;
					else if(pmode == PMode.FLAT)
						pmode = PMode.GOURAUD;
					else if(pmode == PMode.GOURAUD)
						pmode = PMode.AFFINE;
					else if(pmode == PMode.AFFINE)
						pmode = PMode.PERSPECTIVE;
					else if(pmode == PMode.PERSPECTIVE)
						pmode = PMode.WIREFRAME;
				}

				
				repaint();
			}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {}
		});
	}
	
	@SuppressWarnings("unused")
	private void addKeyListener(WindowAdapter windowAdapter) {}

	public void update(Graphics gfx) {
		paint(gfx);
	}
	
	public void paint(Graphics gfx) {
		
		// checks the buffersize with the current panelsize
		// or initialises the image with the first paint
		if(bufferWidth != getSize().width 
			|| bufferHeight!=getSize().height 
			|| bufferImage==null 
			|| bufferGraphics==null) {
			resetBuffer();
		}
		
		bufferGraphics.setColor(Color.GRAY);
		bufferGraphics.fillRect(0, 0, bufferWidth, bufferHeight);
		
		renderScene(bufferGraphics);
		gfx.drawImage(bufferImage,0,0,this); 
	}

	
	public void renderScene(Graphics gfx) {
		
		// reset z-buffer
		if(pmode == PMode.PERSPECTIVE) {
			for(int i = 0; i < bufferWidth; ++i) {
				for(int j = 0; j < bufferHeight; ++j) {
					zbuffer[i][j] = 0.0;
				}
			}
		}

		// rotation around x
		rotationX[0][0] = 1;
		rotationX[1][0] = 0;
		rotationX[2][0] = 0;
		rotationX[3][0] = 0;
		rotationX[0][1] = 0;
		rotationX[1][1] = Math.cos(-camera.getRotX());
		rotationX[2][1] = Math.sin(-camera.getRotX());
		rotationX[3][1] = 0;
		rotationX[0][2] = 0;
		rotationX[1][2] = -Math.sin(-camera.getRotX());
		rotationX[2][2] = Math.cos(-camera.getRotX());			
		rotationX[3][2] = 0;
		rotationX[0][3] = 0;
		rotationX[1][3] = 0;
		rotationX[2][3] = 0;
		rotationX[3][3] = 1;
		
		// rotation around y
		rotationY[0][0] = Math.cos(-camera.getRotY());
		rotationY[1][0] = 0;
		rotationY[2][0] = -Math.sin(-camera.getRotY());
		rotationY[3][0] = 0;
		rotationY[0][1] = 0;
		rotationY[1][1] = 1;
		rotationY[2][1] = 0;
		rotationY[3][1] = 0;
		rotationY[0][2] = Math.sin(-camera.getRotY());
		rotationY[1][2] = 0;
		rotationY[2][2] = Math.cos(-camera.getRotY());
		rotationY[3][2] = 0;
		rotationY[0][3] = 0;
		rotationY[1][3] = 0;
		rotationY[2][3] = 0;
		rotationY[3][3] = 1;
		
		// translate
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
		translation[0][3] = -camera.getPosition().get(0);
		translation[1][3] = -camera.getPosition().get(1);
		translation[2][3] = -camera.getPosition().get(2);			
		translation[3][3] = 1;
		
		// TODO this was class level but moved to do interactive perspective- move back if required
		// perspective projection
		double[][] perspective = new double[4][4];
		perspective[0][0] = (2*near)/(right-left);
		perspective[1][0] = 0;
		perspective[2][0] = 0;
		perspective[3][0] = 0;
		perspective[0][1] = 0;
		perspective[1][1] = (2*near)/(top-bottom);
		perspective[2][1] = 0;
		perspective[3][1] = 0;
		perspective[0][2] = (right+left)/(right-left);
		perspective[1][2] = (top+bottom)/(top-bottom);
		perspective[2][2] = -1*(far+near)/(far-near);			
		perspective[3][2] = -1;
		perspective[0][3] = 0;
		perspective[1][3] = 0;
		perspective[2][3] = -(2*far*near)/(far-near);		
		perspective[3][3] = 0;			
		perspectiveMatrix = new Matrix(perspective);
		
		Matrix transformationMatrix = perspectiveMatrix
										.times(rotationMatrixX
										.times(rotationMatrixY
										.times(translationMatrix)));

		List<Polygon> polygonList;
		if(this.pmode == PMode.PERSPECTIVE) {
			polygonList = world.getFrontToBackPolygonList(camera.getPosition());
		}
		else {
			polygonList = world.getBackToFrontPolygonList(camera.getPosition());			
		}

		for(int idx = 0; idx < polygonList.size(); ++idx) {
			
			Polygon polygon = polygonList.get(idx);

			int cnt = 0;
			for(Vector4d p : polygon.getPoints()) {
				
				// Point in matrix form 
				Matrix pointMatrix = new Matrix(p);
				pointMatrix = transformationMatrix.times(pointMatrix);
				
				double w = pointMatrix.get(3, 0);
				double x = pointMatrix.get(0, 0) / w;
				double y = pointMatrix.get(1, 0) / w;
				double z = pointMatrix.get(2, 0) / w;
				
				polyAsVec[cnt] = new Vector4d(x, y, z, 1/w);
				
				cnt++;
			}
			
			Polygon transformedPolygon = new Polygon(polyAsVec[0],
														polyAsVec[1], 
														polyAsVec[2], 
														polyAsVec[3],
														polygon.getRGB(),
														polygon.getTexture());
			
			// back-face cull
			if(transformedPolygon.getNormal().dotProduct(new Vector4d(0,  0, -1, 0)) < 0) {
				continue; 
			}

			// outside frustum clip
			boolean polygonInView = true;
			for(Vector4d p : transformedPolygon.getPoints()) {
				
				if(polygonInView) {
					for(int i = 0; i < 3; ++i) {
						if( p.get(i) < -1 || p.get(i) > 1 ) {
							polygonInView = false;
							break;
						}
					}
				}
			}
			
			// ignore clipped polygons
			if(!polygonInView) {
				continue;
			}
			
			// viewport transform
			cnt = 0;
			for(Vector4d p : transformedPolygon.getPoints()) {
				x_coords[cnt] = (int) ((bufferWidth / 2) + (bufferWidth * p.get(0)));
				y_coords[cnt] = (int) ((bufferHeight / 2) - (bufferHeight * p.get(1)));
				cnt++;
			}
			
			if(pmode == PMode.FLAT) {
				
				GraphicUtils.renderFlatPoly4(x_coords[0], y_coords[0], 
												x_coords[1], y_coords[1],
												x_coords[2], y_coords[2], 
												x_coords[3], y_coords[3],
												edgeInfo, bufferImage, bufferWidth, 
												polygon.getRGB());
			}
			else if(pmode == PMode.GOURAUD) {
				
				GraphicUtils.renderGouraudPoly4(x_coords[0], y_coords[0], polygon.getGouraudBrightness(0),
												x_coords[1], y_coords[1], polygon.getGouraudBrightness(1),
												x_coords[2], y_coords[2], polygon.getGouraudBrightness(2),
												x_coords[3], y_coords[3], polygon.getGouraudBrightness(3),
												edgeInfo, bufferImage, bufferWidth, polygon.getRGB());
			}
			else if(pmode == PMode.AFFINE) {
				
				GraphicUtils.renderAffineTexturedPoly4(x_coords[0], y_coords[0], 
														x_coords[1], y_coords[1],
														x_coords[2], y_coords[2], 
														x_coords[3], y_coords[3],
														edgeInfo, bufferImage, bufferWidth, polygon.getTexture());
			}
			else if(pmode == PMode.PERSPECTIVE) {

				GraphicUtils.renderPerspectiveTexturedPoly4(x_coords[0], y_coords[0], polyAsVec[0].get(3), polygon.getGouraudBrightness(0),
															x_coords[1], y_coords[1], polyAsVec[1].get(3), polygon.getGouraudBrightness(1),
															x_coords[2], y_coords[2], polyAsVec[2].get(3), polygon.getGouraudBrightness(2),
															x_coords[3], y_coords[3], polyAsVec[3].get(3), polygon.getGouraudBrightness(3),
															perspectiveEdgeInfo, bufferImage, bufferWidth, 
															polygon.getTexture(),
															zbuffer);
			}
			
			if(pmode != PMode.PERSPECTIVE) {
				// render wireframe
				GraphicUtils.renderLine(bufferImage, bufferWidth, bufferHeight,
						x_coords[0], y_coords[0], x_coords[1], y_coords[1]);
				GraphicUtils.renderLine(bufferImage, bufferWidth, bufferHeight,
						x_coords[1], y_coords[1], x_coords[2], y_coords[2]);
				GraphicUtils.renderLine(bufferImage, bufferWidth, bufferHeight,
						x_coords[2], y_coords[2], x_coords[3], y_coords[3]);
				GraphicUtils.renderLine(bufferImage, bufferWidth, bufferHeight,
						x_coords[3], y_coords[3], x_coords[0], y_coords[0]);
			}
		}
	}
	
    private void resetBuffer() {
    	
        // always keep track of the image size
        bufferWidth = getSize().width;
        bufferHeight = getSize().height;

        // clean up the previous image
        if(bufferGraphics != null) {
            bufferGraphics.dispose();
            bufferGraphics = null;
        }
        if(bufferImage != null) {
            bufferImage.flush();
            bufferImage=null;
        }
        System.gc();

        // create the new image with the size of the panel
        bufferImage = new BufferedImage(bufferWidth, bufferHeight, BufferedImage.TYPE_INT_RGB);
        bufferGraphics = bufferImage.getGraphics();
        
        edgeInfo = new GraphicUtils.EdgeInfo[bufferHeight][2];
        for(int i = 0; i < bufferHeight; ++i) {
        	for(int j = 0; j < 2; ++j) {
        		edgeInfo[i][j] = new EdgeInfo();
        	}
        }
        
        perspectiveEdgeInfo = new GraphicUtils.PerspectiveEdgeInfo[bufferHeight][2];
        for(int i = 0; i < bufferHeight; ++i) {
        	for(int j = 0; j < 2; ++j) {
        		perspectiveEdgeInfo[i][j] = new PerspectiveEdgeInfo();
        	}
        }
        
        zbuffer = new double[bufferWidth][bufferHeight];
    }
}
