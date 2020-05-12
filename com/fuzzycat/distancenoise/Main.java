package com.fuzzycat.distancenoise;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Main extends JPanel {

	private final int width, height;
	private BufferedImage image;
	private Graphics2D g2d;
	
	private DistanceNoise distanceNoise;
	
	public Main(int width, int height) {
		this.width = width;
		this.height = height;
		
		setPreferredSize(new Dimension(width, height));
		setMinimumSize(new Dimension(width, height));
		setMaximumSize(new Dimension(width, height));
		
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		g2d = image.createGraphics();
		
		distanceNoise = new DistanceNoise(System.currentTimeMillis(), 10.0f);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, width, height);
		
		int[] pixels = ((DataBufferInt) image.getData().getDataBuffer()).getData();
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				float value = distanceNoise.sample(x, y);
				int gray = (int) (value * 255);
				int color = gray << 16 | gray << 8 | gray;
				image.setRGB(x, height - 1 - y, color);
			}
		}
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		g.drawImage(image, 0, 0, null);
	}
	
	public static void main(String[] args) {
		Main demo = new Main(800, 800);
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				JFrame frame = new JFrame("Distance Noise");
				frame.add(demo);
				frame.setResizable(false);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});
	}
}
