package com.fuzzycat.distancenoise;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DistanceNoise {
	private Random random;
	private final long seed;
	private float scale;
	
	private final List<GridCell> cells;
	private final float influenceRadiusSq;
	
	public DistanceNoise(long seed, float scale) {
		this.random = new Random(seed);
		this.seed = seed;
		this.scale = scale;
		this.cells = new ArrayList<GridCell>();
		
		float influenceRadius = 3.0f;
		this.influenceRadiusSq = influenceRadius * influenceRadius;
		int r = (int) Math.ceil(influenceRadius);
		for (int y = -r; y <= r; ++y) {
			for (int x = -r; x <= r; ++x) {
				cells.add(new GridCell(x, y));
			}
		}
	}
	
	public float sample(float x, float y) {
		x /= scale;
		y /= scale;
		int cellX = (int) Math.floor(x);
		int cellY = (int) Math.floor(y);
		
		float result = 0.0f;
		float weightSum = 0.0f;
		for (int i = 0; i < cells.size(); ++i) {
			GridCell otherCell = cells.get(i);
			int otherCellX = otherCell.x + cellX;
			int otherCellY = otherCell.y + cellY;
			LatticePoint otherPoint = pointFromCell(otherCellX, otherCellY);
			float distSq = (x - otherPoint.x) * (x - otherPoint.x) + (y - otherPoint.y) * (y - otherPoint.y);
			float influence = smoothStep(Math.min(influenceRadiusSq, distSq) / influenceRadiusSq);
			result += influence * otherPoint.value;
			weightSum += influence;
		}
		result /= weightSum;
		
		return Math.max(0.0f, Math.min(1.0f, (result - 0.5f) * 1.7f + 0.5f));
	}
	
	private float smoothStep(float tIn) {
		float t = tIn - 1;
		float t2 = t * t;
		float t4 = t2 * t2;
		float t5 = t * t4;
		return  -t5;
	}

	private LatticePoint pointFromCell(int cellX, int cellY) {
		random.setSeed(pointSeed(cellX, cellY));
		return new LatticePoint(cellX + random.nextFloat(), 
								cellY + random.nextFloat(), 
								random.nextFloat());
	}
	
	private long pointSeed(int cellX, int cellY) {
		return seed + (cellY * 65371 + cellX);
	}

	public void setScale(float scale) {
		this.scale = scale;
	}
	
	public float getScale() {
		return scale;
	}
	
	private static class GridCell {
		public final int x;
		public final int y;

		public GridCell(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
	
	private static class LatticePoint {
		public final float x;
		public final float y;
		public final float value;

		public LatticePoint(float x, float y, float value) {
			this.x = x;
			this.y = y;
			this.value = value;
		}
	}
}
