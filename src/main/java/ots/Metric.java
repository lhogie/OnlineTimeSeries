package ots;

import java.awt.Color;
import java.io.Serializable;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import xycharter.Figure;

public class Metric implements Serializable {
	public String name;
	public Unit unit;
	private Color color;

	DoubleList x = new DoubleArrayList(), y = new DoubleArrayList();

	public void setName(String metricName) {
		this.name = metricName;
	}

	public void addPoint(double x, double y) {
		this.x.add(x);
		this.y.add(y);
	}

	public void addPoints(Metric f) {
		f.x.addAll(f.x);
		f.y.addAll(f.y);
	}

	public int getNbPoints() {
		return x.size();
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public double y(int i) {
		return this.y(i);
	}

	public double x(int i) {
		return this.x(i);
	}
	
	 public Figure tofigure() {
		var f = new Figure();

		int sz = getNbPoints();

		for (int i = 0; i < sz; ++i) {
			f.addPoint(x(i), y(i));
		}

		return f;
	}
}
