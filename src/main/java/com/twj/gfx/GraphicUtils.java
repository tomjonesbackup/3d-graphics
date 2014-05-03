package com.twj.gfx;

import java.awt.image.BufferedImage;

public class GraphicUtils {
	
	private static final int EMPTY = Integer.MAX_VALUE;
	
	public static class EdgeInfo {
		int x; // scan line x coord
		int u; // texture u
		int v; // texture v
		double brightness;
	}
	
	public static class PerspectiveEdgeInfo {
		int x; // scan line x coord
		double uOverz; 
		double vOverz;
		double oneOverz;
		double brightness;
	}
	
	public static void renderLine(BufferedImage image, 
									int width, int height, 
									int x0, int y0, 
									int x1, int y1) {
		
		int dx = x1 - x0;
		int dy = y1 - y0;
		
		int udx = Math.abs(dx);
		int udy = Math.abs(dy);
		
		int xadd = (dx < 0) ? -1 : +1;
		int yadd = (dy < 0) ? -1 : +1;
		
		int error = 0;
		
		int loop = 0;
		
		if(udx > udy) {
			do {
				error += udy;
				
				if( error >= udx) {
					error -= udx;
					y0 += yadd;
				}
				
				++loop;
				
				if(x0 > 0 && x0 < width && y0 > 0 && y0 < height)
					image.setRGB(x0, y0, 0x0);
				
				x0 += xadd;
			} while(loop < udx);
		}
		else {
			do {
				error += udx;
				
				if( error >= udy) {
					error -= udy;
					x0 += xadd;
				}
				
				++loop;

				if(x0 > 0 && x0 < width && y0 > 0 && y0 < height)
					image.setRGB(x0, y0, 0x0);
								
				y0 += yadd;
			} while(loop < udy);
		}
	}
	
	public static void scanEdge(int x0, int y0, int x1, int y1, EdgeInfo[][] edgeInfo) {

		// ensure we always draw left to right
		if(x0 > x1) {
			int temp = x0;
			x0 = x1;
			x1 = temp;
			
			temp = y0;
			y0 = y1;
			y1 = temp;
		}
		
		int dx = x1 - x0;
		int dy = y1 - y0;
		
		if(dx != 0 || dy != 0) {
		
			int udx = Math.abs(dx);
			int udy = Math.abs(dy);
			
			int yadd = (dy < 0) ? -1 : +1;
			
			int error = 0;
			
			int loop = 0;
			
			if(udx > udy) {
				
				if(y0 >= 0 && y0 < edgeInfo.length) {
					if(edgeInfo[y0][0].x == EMPTY) {
						edgeInfo[y0][0].x = x0;
					}
					else {
						if(edgeInfo[y0][1].x == EMPTY && x0 != edgeInfo[y0][0].x) {
							edgeInfo[y0][1].x = x0;
						}
					}
				}
					
				do {
					error += udy;

					++loop;
					
					++x0;
					
					if( error >= udx) {
						error -= udx;
						y0 += yadd;

						if(y0 >= 0 && y0 < edgeInfo.length) {
							if(edgeInfo[y0][0].x == EMPTY) {
								edgeInfo[y0][0].x = x0;
							}
							else {
								if(x0 != edgeInfo[y0][0].x) {
									edgeInfo[y0][1].x = x0;
								}
							}
						}
					}
				} while(loop < udx);
			}
			else {
				
				if(udx < udy) {
					
					int max = udy + 1;

					do {
						
						if(y0 >= 0 && y0 < edgeInfo.length) {
							if(edgeInfo[y0][0].x == EMPTY) {
								edgeInfo[y0][0].x = x0;
							}
							else {
								if(x0 != edgeInfo[y0][0].x) {
									edgeInfo[y0][1].x = x0;
								}
							}
						}
						
						y0 += yadd;
						
						error += udx;
						
						if( error >= udy) {
							error -= udy;
							++x0;
						}
					
						++loop;
		
					
					} while(loop < max);
				}
				else { // delta x = delta y
					
					int max = udy + 1;

					do {
						
						if(y0 >= 0 && y0 < edgeInfo.length) {
							if(edgeInfo[y0][0].x == EMPTY) {
								edgeInfo[y0][0].x = x0;
							}
							else {
								if(x0 != edgeInfo[y0][0].x) {
									edgeInfo[y0][1].x = x0;
								}
							}
						}
						
						y0 += yadd;
						++x0;
						++loop;
		
					
					} while(loop < max);
				}
			}
		}
	}
	
	private static void scanGouraudEdge(int x0, int y0, 
										int x1, int y1, 
										double g0, double g1, 
										EdgeInfo[][] edgeInfo) {

		// ensure we always draw left to right
		if(x0 > x1) {
			int temp = x0;
			x0 = x1;
			x1 = temp;
			
			temp = y0;
			y0 = y1;
			y1 = temp;
			
			double dtemp = g0;
			g0 = g1;
			g1 = dtemp;
		}
		
		int dx = x1 - x0;
		int dy = y1 - y0;
		
		if(dx != 0 || dy != 0) {
		
			int udx = Math.abs(dx);
			int udy = Math.abs(dy);
			
			int yadd = (dy < 0) ? -1 : +1;
			
			int error = 0;
			
			int loop = 0;
			
			if(udx > udy) {
				
				double gadd = (g1 - g0) / udx;
				
				if(y0 >= 0 && y0 < edgeInfo.length) {
					if(edgeInfo[y0][0].x == EMPTY) {
						edgeInfo[y0][0].x = x0;
						edgeInfo[y0][0].brightness = g0;
					}
					else {
						if(edgeInfo[y0][1].x == EMPTY && x0 != edgeInfo[y0][0].x) {
							edgeInfo[y0][1].x = x0;
							edgeInfo[y0][1].brightness = g0;
						}
					}
				}
					
				do {
					error += udy;
					
					g0 += gadd;

					++loop;
					
					++x0;
					
					if( error >= udx) {
						error -= udx;
						y0 += yadd;

						if(y0 >= 0 && y0 < edgeInfo.length) {
							if(edgeInfo[y0][0].x == EMPTY) {
								edgeInfo[y0][0].x = x0;
								edgeInfo[y0][0].brightness = g0;
							}
							else {
								if(x0 != edgeInfo[y0][0].x) {
									edgeInfo[y0][1].x = x0;
									edgeInfo[y0][1].brightness = g0;
								}
							}
						}
					}
				} while(loop < udx);
			}
			else {
				
				if(udx < udy) {
					
					double gadd = (g1 - g0) / udy;
					
					int max = udy + 1;

					do {
						
						if(y0 >= 0 && y0 < edgeInfo.length) {
							if(edgeInfo[y0][0].x == EMPTY) {
								edgeInfo[y0][0].x = x0;
								edgeInfo[y0][0].brightness = g0;
							}
							else {
								if(x0 != edgeInfo[y0][0].x) {
									edgeInfo[y0][1].x = x0;
									edgeInfo[y0][1].brightness = g0;
								}
							}
						}
						
						y0 += yadd;
						
						error += udx;
						
						if( error >= udy) {
							error -= udy;
							++x0;
						}
					
						++loop;

						g0 += gadd;
					
					} while(loop < max);
				}
				else { // delta x = delta y
					
//					System.out.printf("45 deg line\n");
					
					double gadd = (g1 - g0) / udy;

					int max = udy + 1;

					do {
						
						if(y0 >= 0 && y0 < edgeInfo.length) {
							if(edgeInfo[y0][0].x == EMPTY) {
								edgeInfo[y0][0].x = x0;
								edgeInfo[y0][0].brightness = g0;

							}
							else {
								if(x0 != edgeInfo[y0][0].x) {
									edgeInfo[y0][1].x = x0;
									edgeInfo[y0][1].brightness = g0;
								}
							}
						}
						
						y0 += yadd;
						g0 += gadd;

						++x0;
						++loop;
		
					
					} while(loop < max);
				}
			}
		}
	}

	
	public static void renderFlatPoly4(int x0, int y0, 
										int x1, int y1, 
										int x2, int y2,
										int x3, int y3, 
										EdgeInfo[][] edgeInfo, 
										BufferedImage image,
										int imageWidth, 
										int rgb)	{
		
		// set initial best values.
		int top = 4096;
		int bot = -4096;

	    if(y0 < top)         // Get top and bottom values of polygon.
	    	top = y0;
	    
		if(y1 < top)
			top = y1;

	    if(y2 < top)
			top = y2;
	    
	    if(y3 < top)
			top = y3;
	    
	    if(y0 > bot)
	    	bot = y0;

	    if(y1 > bot)
	    	bot = y1;

	    if(y2 > bot)
	    	bot = y2;

	    if(y3 > bot)
	    	bot = y3;
	    
	    // clip to edge buffer
	    top = top < 0 ? 0 : top;
	    bot = bot > edgeInfo.length ? edgeInfo.length : bot;

		int height = bot - top;

		if (height == 0 )
			return;
		
		for(int yloop = 0; yloop < height; ++yloop )
		{
			edgeInfo[yloop][0].x = EMPTY;
			edgeInfo[yloop][1].x = EMPTY;
		}

		y0 -= top;
		y1 -= top;
		y2 -= top;
		y3 -= top;
		
		scanEdge(x0, y0, x1, y1, edgeInfo);
		scanEdge(x1, y1, x2, y2, edgeInfo);
		scanEdge(x2, y2, x3, y3, edgeInfo);
		scanEdge(x3, y3, x0, y0, edgeInfo);

	    for(int yloop = 0; yloop < height; ++yloop )
		{
	        if(edgeInfo[yloop][0].x != EMPTY 
	        		&& edgeInfo[yloop][1].x != EMPTY ) {
	        	
	        	drawFlatScanLine(yloop + top, 
	        			edgeInfo[yloop][0].x, 
	        			edgeInfo[yloop][1].x, 
	        			image, imageWidth, rgb);
			}
		}
	}

	
	public static void renderAffineTexturedPoly4(int x0, int y0, 
													int x1, int y1, 
													int x2, int y2,
													int x3, int y3, 
													EdgeInfo[][] edgeInfo, 
													BufferedImage image,
													int imageWidth, 
													BufferedImage texture)	{
		
		// set initial best values.
		int top = 4096;
		int bot = -4096;

	    if(y0 < top)         // Get top and bottom values of polygon.
	    	top = y0;
	    
		if(y1 < top)
			top = y1;

	    if(y2 < top)
			top = y2;
	    
	    if(y3 < top)
			top = y3;
	    
	    if(y0 > bot)
	    	bot = y0;

	    if(y1 > bot)
	    	bot = y1;

	    if(y2 > bot)
	    	bot = y2;

	    if(y3 > bot)
	    	bot = y3;
	    
	    // clip to edge buffer
	    top = top < 0 ? 0 : top;
	    bot = bot > edgeInfo.length ? edgeInfo.length : bot;

		int height = bot - top;

		if (height == 0 )
			return;
		
		for(int yloop = 0; yloop < height; ++yloop )
		{
			edgeInfo[yloop][0].x = EMPTY;
			edgeInfo[yloop][1].x = EMPTY;
		}

		y0 -= top;
		y1 -= top;
		y2 -= top;
		y3 -= top;
		
		scanAffineTextureEdge(x0, y0, x1, y1, 0, 0, 256, 0, edgeInfo);
		scanAffineTextureEdge(x1, y1, x2, y2, 256, 0, 256, 256, edgeInfo);
		scanAffineTextureEdge(x2, y2, x3, y3, 256, 256, 0, 256, edgeInfo);
		scanAffineTextureEdge(x3, y3, x0, y0, 0, 256, 0, 0, edgeInfo);

	    for(int yloop = 0; yloop < height; ++yloop )
		{
	        if(edgeInfo[yloop][0].x != EMPTY 
	        		&& edgeInfo[yloop][1].x != EMPTY ) {
	        	
	        	drawAffineTextureScanLine(yloop + top, 
	        			edgeInfo[yloop][0].x, 
	        			edgeInfo[yloop][1].x,
	        			edgeInfo[yloop][0].u,
	        			edgeInfo[yloop][0].v,
	        			edgeInfo[yloop][1].u,
	        			edgeInfo[yloop][1].v,
	        			image, imageWidth, texture);
			}
		}
	}
	
	public static void renderGouraudPoly4(int x0, int y0, double g0, 
											int x1, int y1, double g1,
											int x2, int y2, double g2,
											int x3, int y3, double g3,
											EdgeInfo[][] edgeInfo, 
											BufferedImage image,
											int imageWidth, 
											int rgb)	{
		
		// set initial best values.
		int top = 4096;
		int bot = -4096;

	    if(y0 < top)         // Get top and bottom values of polygon.
	    	top = y0;
	    
		if(y1 < top)
			top = y1;

	    if(y2 < top)
			top = y2;
	    
	    if(y3 < top)
			top = y3;
	    
	    if(y0 > bot)
	    	bot = y0;

	    if(y1 > bot)
	    	bot = y1;

	    if(y2 > bot)
	    	bot = y2;

	    if(y3 > bot)
	    	bot = y3;
	    
	    // clip to edge buffer
	    top = top < 0 ? 0 : top;
	    bot = bot > edgeInfo.length ? edgeInfo.length : bot;

		int height = bot - top;

		if (height == 0 )
			return;
		
		for(int yloop = 0; yloop < height; ++yloop )
		{
			edgeInfo[yloop][0].x = EMPTY;
			edgeInfo[yloop][1].x = EMPTY;
		}

		y0 -= top;
		y1 -= top;
		y2 -= top;
		y3 -= top;
		
		scanGouraudEdge(x0, y0, x1, y1, g0, g1, edgeInfo);
		scanGouraudEdge(x1, y1, x2, y2, g1, g2, edgeInfo);
		scanGouraudEdge(x2, y2, x3, y3, g2, g3, edgeInfo);
		scanGouraudEdge(x3, y3, x0, y0, g3, g0, edgeInfo);

	    for(int yloop = 0; yloop < height; ++yloop )
		{
	        if(edgeInfo[yloop][0].x != EMPTY 
	        		&& edgeInfo[yloop][1].x != EMPTY ) {
	        	
	        	drawGouraudScanLine(yloop + top, 
				        			edgeInfo[yloop][0].x, 
				        			edgeInfo[yloop][1].x,
				        			edgeInfo[yloop][0].brightness,
				        			edgeInfo[yloop][1].brightness,
				        			image, imageWidth, rgb);
			}
		}
	}
	
	public static void renderPerspectiveTexturedPoly4(int x0, int y0, double w0, double b0,
														int x1, int y1, double w1, double b1,
														int x2, int y2, double w2, double b2,
														int x3, int y3, double w3, double b3,
														PerspectiveEdgeInfo[][] edgeInfo, 
														BufferedImage image,
														int imageWidth, 
														BufferedImage texture, double[][] zbuffer)	{
		// set initial best values.
		int top = 4096;
		int bot = -4096;

	    if(y0 < top)         // Get top and bottom values of polygon.
	    	top = y0;
	    
		if(y1 < top)
			top = y1;

	    if(y2 < top)
			top = y2;
	    
	    if(y3 < top)
			top = y3;
	    
	    if(y0 > bot)
	    	bot = y0;

	    if(y1 > bot)
	    	bot = y1;

	    if(y2 > bot)
	    	bot = y2;

	    if(y3 > bot)
	    	bot = y3;
	    
	    // clip to edge buffer
	    top = top < 0 ? 0 : top;
	    bot = bot > edgeInfo.length ? edgeInfo.length : bot;

		int height = bot - top;

		if (height == 0 )
			return;
		
		for(int yloop = 0; yloop < height; ++yloop )
		{
			edgeInfo[yloop][0].x = EMPTY;
			edgeInfo[yloop][1].x = EMPTY;
		}

		y0 -= top;
		y1 -= top;
		y2 -= top;
		y3 -= top;
		
		scanPerspectiveTextureEdge(x0, y0, w0, b0, x1, y1, w1, b1, 0, 0, 256, 0, edgeInfo);
		scanPerspectiveTextureEdge(x1, y1, w1, b1, x2, y2, w2, b2, 256, 0, 256, 256, edgeInfo);
		scanPerspectiveTextureEdge(x2, y2, w2, b2, x3, y3, w3, b3, 256, 256, 0, 256, edgeInfo);
		scanPerspectiveTextureEdge(x3, y3, w3, b3, x0, y0, w0, b0, 0, 256, 0, 0, edgeInfo);

	    for(int yloop = 0; yloop < height; ++yloop )
		{
	        if(edgeInfo[yloop][0].x != EMPTY 
	        		&& edgeInfo[yloop][1].x != EMPTY ) {
	        	
	        	drawPerspectiveTextureScanLine(yloop + top, 
	        			edgeInfo[yloop][0].x, 
	        			edgeInfo[yloop][1].x,
	        			edgeInfo[yloop][0].uOverz,
	        			edgeInfo[yloop][0].vOverz,
	        			edgeInfo[yloop][0].oneOverz,
	        			edgeInfo[yloop][1].uOverz,
	        			edgeInfo[yloop][1].vOverz,
	        			edgeInfo[yloop][1].oneOverz,
	        			edgeInfo[yloop][0].brightness,
	        			edgeInfo[yloop][1].brightness,
	        			image, imageWidth, texture, zbuffer);
			}
		}
	}
	
	private static void drawFlatScanLine(int y, int x0, int x1, BufferedImage image, int width, int rgb) {
		
		if(x0 == x1) {
			if(x0 > 0 && x0 < width)
				image.setRGB(x0, y, rgb);
		}
		else {
			
			if(x1 < x0) {
				int temp = x1;
				x1 = x0;
				x0 = temp;
			}
			
			int leftExtent = x0 < 0 ? 0 : x0;
			int rightExtent = x1 > width ? width : x1;
        	for (int loop = leftExtent; loop < rightExtent; ++loop ) {
					image.setRGB(loop, y, rgb);
        	}
		}
	}
	
	private static void scanAffineTextureEdge(int x0, int y0, 
												int x1, int y1, 
												int u0, int v0, 
												int u1, int v1, 
												EdgeInfo[][] edgeInfo) {

		// ensure we always draw left to right
		if(x0 > x1) {
			int temp = x0;
			x0 = x1;
			x1 = temp;
			
			temp = y0;
			y0 = y1;
			y1 = temp;
			
			temp = u0;
			u0 = u1;
			u1 = temp;

			temp = v0;
			v0 = v1;
			v1 = temp;
		}
		
		int dx = x1 - x0;
		int dy = y1 - y0;
		
		if(dx != 0 || dy != 0) {
		
			int udx = Math.abs(dx);
			int udy = Math.abs(dy);
			
			int yadd = (dy < 0) ? -1 : +1;
			
			int error = 0;
			
			int loop = 0;
			
			if(udx > udy) {
				
				int uadd = ((u1 - u0) * 256) / udx;
				int vadd = ((v1 - v0) * 256) / udx;
				int u = u0 * 256;
				int v = v0 * 256;
				
				if(y0 >= 0 && y0 < edgeInfo.length) {
					if(edgeInfo[y0][0].x == EMPTY) {
						edgeInfo[y0][0].x = x0;
						edgeInfo[y0][0].u = u0;
						edgeInfo[y0][0].v = v0;
					}
					else {
						if(edgeInfo[y0][1].x == EMPTY && x0 != edgeInfo[y0][0].x) {
							edgeInfo[y0][1].x = x0;
							edgeInfo[y0][1].u = u0;
							edgeInfo[y0][1].v = v0;
						}
					}
				}
					
				do {
					error += udy;
					
					u += uadd;
					v += vadd;

					++loop;
					
					++x0;
					
					if( error >= udx) {
						error -= udx;
						y0 += yadd;

						
						if(y0 >= 0 && y0 < edgeInfo.length) {
							if(edgeInfo[y0][0].x == EMPTY) {
								edgeInfo[y0][0].x = x0;
								edgeInfo[y0][0].u = u >> 8;
								edgeInfo[y0][0].v = v >> 8;
							}
							else {
								if(x0 != edgeInfo[y0][0].x) {
									edgeInfo[y0][1].x = x0;
									edgeInfo[y0][1].u = u >> 8;
									edgeInfo[y0][1].v = v >> 8;
								}
							}
						}
					}
				} while(loop < udx);
			}
			else {
				
				if(udx < udy) {

					int uadd = ((u1 - u0) * 256) / udy;
					int vadd = ((v1 - v0) * 256) / udy;
					int u = u0 * 256;
					int v = v0 * 256;

					int max = udy + 1;

					do {
						
						if(y0 >= 0 && y0 < edgeInfo.length) {
							if(edgeInfo[y0][0].x == EMPTY) {
								edgeInfo[y0][0].x = x0;
								edgeInfo[y0][0].u = u >> 8;
								edgeInfo[y0][0].v = v >> 8;
							}
							else {
								if(x0 != edgeInfo[y0][0].x) {
									edgeInfo[y0][1].x = x0;
									edgeInfo[y0][1].u = u >> 8;
									edgeInfo[y0][1].v = v >> 8;
								}
							}
						}
						
						y0 += yadd;
						
						error += udx;
						
						if( error >= udy) {
							error -= udy;
							++x0;
						}
					
						++loop;
						
						u += uadd;
						v += vadd;
		
					
					} while(loop < max);
				}
				else { // delta x = delta y
					
					int uadd = ((u1 - u0) * 256) / udy;
					int vadd = ((v1 - v0) * 256) / udy;
					int u = u0 * 256;
					int v = v0 * 256;
					
					int max = udy + 1;

					do {
						
						if(y0 >= 0 && y0 < edgeInfo.length) {
							if(edgeInfo[y0][0].x == EMPTY) {
								edgeInfo[y0][0].x = x0;
								edgeInfo[y0][0].u = u >> 8;
								edgeInfo[y0][0].v = v >> 8;
							}
							else {
								if(x0 != edgeInfo[y0][0].x) {
									edgeInfo[y0][1].x = x0;
									edgeInfo[y0][1].u = u >> 8;
									edgeInfo[y0][1].v = v >> 8;
								}
							}
						}
						
						y0 += yadd;
						++x0;
						++loop;
						
						u += uadd;
						v += vadd;
					
					} while(loop < max);
				}
			}
		}
	}
	
	private static void drawAffineTextureScanLine(int y, 
													int x0, int x1, 
													int u0, int v0, 
													int u1, int v1, 
													BufferedImage image, 
													int width, 
													BufferedImage texture) {
		
		if(x0 != x1) {
			if(x1 < x0) {
				int temp = x1;
				x1 = x0;
				x0 = temp;
				
				temp = u1;
				u1 = u0;
				u0 = temp;

				temp = v1;
				v1 = v0;
				v0 = temp;
			}
			
			int length = x1 - x0;
			
			int uadd = ((u1 - u0) * 256) / length;
			int u = u0 * 256;
			int vadd = ((v1 - v0) * 256) / length;
			int v = v0 * 256;
			
			
        	for (int loop = x0; loop < x1; ++loop ) {
        		
        		if(loop > 0 && loop < width) {
        			int rgb = texture.getRGB((u >> 8) & 0xff, v >> 8 & 0xff);
					image.setRGB(loop, y, rgb);
        		}
					
				u += uadd;
				v += vadd;
        	}
		}
	}
	
	private static void drawGouraudScanLine(int y, 
											int x0, int x1, 
											double g0, double g1, 
											BufferedImage image, 
											int width, 
											int rgb) {
		
		if(x0 != x1) {
			if(x1 < x0) {
				int temp = x1;
				x1 = x0;
				x0 = temp;
				
				double dtemp = g0;
				g0 = g1;
				g1 = dtemp;
			}
						
			double gadd = (g1 - g0) / (x1 - x0);
			
        	for (int loop = x0; loop < x1; ++loop ) {
        		
        		if(loop > 0 && loop < width) {
					image.setRGB(loop, y, ColourUtils.getColorRGB(rgb, g0));
        		}
					
				g0 += gadd;
        	}
		}
	}

	private static void scanPerspectiveTextureEdge(int x0, int y0, double w0, double b0,
													int x1, int y1, double w1, double b1, 
													int u0, int v0, 
													int u1, int v1, 
													PerspectiveEdgeInfo[][] edgeInfo) {

		// ensure we always draw left to right
		if(x0 > x1) {
			int temp = x0;
			x0 = x1;
			x1 = temp;
			
			temp = y0;
			y0 = y1;
			y1 = temp;
			
			temp = u0;
			u0 = u1;
			u1 = temp;

			temp = v0;
			v0 = v1;
			v1 = temp;

			double dtemp = w0;
			w0 = w1;
			w1 = dtemp;
			
			dtemp = b0;
			b0 = b1;
			b1 = dtemp;

		}
		
		int dx = x1 - x0;
		int dy = y1 - y0;
		
		if(dx != 0 || dy != 0) {
		
			int udx = Math.abs(dx);
			int udy = Math.abs(dy);
			
			int yadd = (dy < 0) ? -1 : +1;
			
			int error = 0;
			
			int loop = 0;
			
			double u = u0 * w0;
			double v = v0 * w0;
			double w = w0;
			double b = b0;
			
			if(udx > udy) {
				
				double uadd = ((u1 * w1) - u) / udx;
				double vadd = ((v1 * w1) - v) / udx;
				double wadd = (w1 - w0) / udx;
				double badd = (b1 - b0) / udx;
				
				if(y0 >= 0 && y0 < edgeInfo.length) {
					if(edgeInfo[y0][0].x == EMPTY) {
						edgeInfo[y0][0].x = x0;
						edgeInfo[y0][0].uOverz = u;
						edgeInfo[y0][0].vOverz = v;
						edgeInfo[y0][0].oneOverz = w;
						edgeInfo[y0][0].brightness = b;
						
					}
					else {
						if(edgeInfo[y0][1].x == EMPTY && x0 != edgeInfo[y0][0].x) {
							edgeInfo[y0][1].x = x0;
							edgeInfo[y0][1].uOverz = u;
							edgeInfo[y0][1].vOverz = v;
							edgeInfo[y0][1].oneOverz = w;
							edgeInfo[y0][1].brightness = b;
						}
					}
				}
					
				do {
					error += udy;
					
					u += uadd;
					v += vadd;
					w += wadd;
					b += badd;

					++loop;
					
					++x0;
					
					if( error >= udx) {
						error -= udx;
						y0 += yadd;

						
						if(y0 >= 0 && y0 < edgeInfo.length) {
							if(edgeInfo[y0][0].x == EMPTY) {
								edgeInfo[y0][0].x = x0;
								edgeInfo[y0][0].uOverz = u;
								edgeInfo[y0][0].vOverz = v;
								edgeInfo[y0][0].oneOverz = w;
								edgeInfo[y0][0].brightness = b;

							}
							else {
								if(x0 != edgeInfo[y0][0].x) {
									edgeInfo[y0][1].x = x0;
									edgeInfo[y0][1].uOverz = u;
									edgeInfo[y0][1].vOverz = v;
									edgeInfo[y0][1].oneOverz = w;
									edgeInfo[y0][1].brightness = b;
								}
							}
						}
					}
				} while(loop < udx);
			}
			else {
								
				double uadd = ((u1 * w1) - u) / udy;
				double vadd = ((v1 * w1) - v) / udy;
				double wadd = (w1 - w0) / udy;
				double badd = (b1 - b0) / udy;
				
				if(udx < udy) {

					int max = udy + 1;

					do {
						
						if(y0 >= 0 && y0 < edgeInfo.length) {
							if(edgeInfo[y0][0].x == EMPTY) {
								edgeInfo[y0][0].x = x0;
								edgeInfo[y0][0].uOverz = u;
								edgeInfo[y0][0].vOverz = v;
								edgeInfo[y0][0].oneOverz = w;
								edgeInfo[y0][0].brightness = b;
							}
							else {
								if(x0 != edgeInfo[y0][0].x) {
									edgeInfo[y0][1].x = x0;
									edgeInfo[y0][1].uOverz = u;
									edgeInfo[y0][1].vOverz = v;
									edgeInfo[y0][1].oneOverz = w;
									edgeInfo[y0][1].brightness = b;
								}
							}
						}
						
						y0 += yadd;
						
						error += udx;
						
						if( error >= udy) {
							error -= udy;
							++x0;
						}
					
						++loop;
						
						u += uadd;
						v += vadd;
						w += wadd;
						b += badd;
						
					} while(loop < max);
				}
				else { // delta x = delta y

					int max = udy + 1;

					do {
						
						if(y0 >= 0 && y0 < edgeInfo.length) {
							if(edgeInfo[y0][0].x == EMPTY) {
								edgeInfo[y0][0].x = x0;
								edgeInfo[y0][0].uOverz = u;
								edgeInfo[y0][0].vOverz = v;
								edgeInfo[y0][0].oneOverz = w;
								edgeInfo[y0][0].brightness = b;
							}
							else {
								if(x0 != edgeInfo[y0][0].x) {
									edgeInfo[y0][1].x = x0;
									edgeInfo[y0][1].uOverz = u;
									edgeInfo[y0][1].vOverz = v;
									edgeInfo[y0][1].oneOverz = w;
									edgeInfo[y0][1].brightness = b;
								}
							}
						}
						
						y0 += yadd;
						++x0;
						++loop;
						
						u += uadd;
						v += vadd;
						w += wadd;
						b += badd;
					
					} while(loop < max);
				}
			}
		}
	}
	
	private static void drawPerspectiveTextureScanLine(int y, 
													int x0, int x1, 
													double u0Overz, double v0Overz, double z0Overz, 
													double u1Overz, double v1Overz, double z1Overz,
													double b0, double b1,
													BufferedImage image, 
													int width, 
													BufferedImage texture, double[][] zbuffer) {
		
		if(x0 != x1) {
			if(x1 < x0) {
				int temp = x1;
				x1 = x0;
				x0 = temp;
				
				double dtemp = u1Overz;
				u1Overz = u0Overz;
				u0Overz = dtemp;

				dtemp = v1Overz;
				v1Overz = v0Overz;
				v0Overz = dtemp;

				dtemp = z1Overz;
				z1Overz = z0Overz;
				z0Overz = dtemp;
				
				dtemp = b1;
				b1 = b0;
				b0 = dtemp;
			}
			
			int dx = x1 - x0;
			
			double uadd = (u1Overz - u0Overz) / dx;
			double vadd = (v1Overz - v0Overz) / dx;
			double zadd = (z1Overz - z0Overz) / dx;
			double badd = (b1 - b0) / dx;

			for(int loop = x0; loop < x1; ++loop ) {
        		
        		if(loop > 0 && loop < width) {
        			
        			if(z0Overz < zbuffer[loop][y]) {
	        			int texel = texture.getRGB((int)(u0Overz / z0Overz) & 0xff, (int)(v0Overz / z0Overz) & 0xff);
	        			int rgb = ColourUtils.getColorRGB(texel, b0);
						image.setRGB(loop, y, rgb);
						zbuffer[loop][y] = z0Overz;
        			}
        		}
					
				u0Overz += uadd;
				v0Overz += vadd;
				z0Overz += zadd;
				b0 += badd;
        	}
		}
	}
}
