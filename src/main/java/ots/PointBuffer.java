package ots;

import java.util.HashMap;

public class PointBuffer extends HashMap<String, Metric> {
	private int nbPoints = 0;

	public void add(String metricName, double x, double y) {
		var f = get(metricName);

		if (f == null) {
			put(metricName, f = new Metric());
			f.setName(metricName);
		}

		f.addPoint(x, y);
		++nbPoints;
	}

	@Override
	public void clear() {
		super.clear();
		nbPoints = 0;
	}

	public int nbPoints() {
		return nbPoints;
	}
}