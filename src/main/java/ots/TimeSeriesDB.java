package ots;

import java.awt.Color;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import idawi.AsMethodOperation.OperationID;
import idawi.Component;
import idawi.IdawiOperation;
import idawi.Message;
import idawi.MessageQueue;
import idawi.Service;
import idawi.Streams;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import toools.io.file.Directory;
import toools.io.file.RegularFile;
import toools.thread.Threads;
import xycharter.Figure;
import xycharter.PNGPlotter;
import xycharter.Plot;

/**
 * Sends an empty message on a queue that is created specifically for the peer
 * to bench.
 */

public class TimeSeriesDB extends Service {

	private Map<String, Figure> name2figure = new HashMap<>();
	private Directory baseDir = new Directory("$HOME/.timeSeriesDB");

	public TimeSeriesDB(Component node) {
		super(node);
	}

	public static OperationID addPoint;

	@IdawiOperation
	public void addPoint(PointBuffer buf) {

		for (Figure f : buf.values()) {
			Figure a = name2figure.get(f.name);

			if (a == null)
				throw new Error("figure not found: " + f.name);

			a.addPoints(f);
		}
	}

	public static OperationID store;

	@IdawiOperation
	public void store(String workbenchName) {
		var file = new RegularFile(baseDir, workbenchName);
		file.setContentAsJavaObject(name2figure);
	}

	public static OperationID load;

	@IdawiOperation
	public void load(String workbenchName) {
		var file = new RegularFile(baseDir, workbenchName);
		name2figure = (Map<String, Figure>) file.getContentAsJavaObject();
	}

	public static OperationID removeFigure;

	@IdawiOperation
	public void removeMetric(String figName) {
		name2figure.remove(figName);
	}

	public static OperationID getNbPoints;

	@IdawiOperation
	public int getNbPoints(String figName) {
		return name2figure.get(figName).getNbPoints();
	}

	public static OperationID getMetricNames;

	@IdawiOperation
	public Set<String> getMetricNames() {
		return new HashSet<>(name2figure.keySet());
	}

	public static OperationID getMetricInfo;

	public static class MetricInfo implements Serializable {
		String name;
		Unit unit;
		int nbValues;
		double lastX, lastY;
	}

	@IdawiOperation
	public Set<MetricInfo> getMetricInfo() {
		var r = new HashSet<MetricInfo>();

		for (var e : name2figure.entrySet()) {
			var i = new MetricInfo();
			i.name = e.getKey();
			var f = e.getValue();
			i.nbValues = f.getNbPoints();
			i.lastX = f.x(i.nbValues - 1);
			i.lastY = f.y(i.nbValues - 1);
			r.add(i);
		}

		return r;
	}

	public static OperationID getWorkbenchList;

	@IdawiOperation
	public Set<String> getWorkbenchList() {
		return baseDir.listRegularFiles().stream().map(f -> f.getName()).collect(Collectors.toSet());
	}

	public static OperationID retrieveFigure;

	@IdawiOperation
	public Set<Figure> retrieveFigure(String re) {
		Set<Figure> r = new HashSet<>();

		for (var e : name2figure.entrySet()) {
			if (e.getKey().matches(re)) {
				r.add(e.getValue());
			}
		}

		return r;
	}

	public static OperationID retrieveWorkbench;

	@IdawiOperation
	public void retrieveWorkbench(MessageQueue in) throws Throwable {
		PipedOutputStream pos = new PipedOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(pos);
		oos.writeObject(name2figure);
		Streams.split(new PipedInputStream(pos), 1000, b -> send(b, in.get_blocking().requester));
	}

	public static OperationID createFigure;

	public void saySomething() {
		System.out.println("Hello, this is non-static method.");
	}

	@IdawiOperation
	synchronized public void createFigure(String name) {
		Figure f = new Figure();
		f.setName(name);
		// f.addRenderer(new ConnectedLineFigureRenderer());
		name2figure.put(name, f);
	}

	public static OperationID setFigureColor;

	@IdawiOperation
	synchronized public void setFigureColor(String figName, Color color) {
		name2figure.get(figName).setColor(color);
	}

	public static OperationID filter;

	@IdawiOperation
	synchronized public Set<Figure> filter(Filter filter) {
		Set<Figure> r = new HashSet<>();

		for (Figure f : name2figure.values()) {
			if (filter.fp.accept(f.name)) {
				Figure subFigure = new Figure();
				subFigure.name = f.name;
				int sz = f.getNbPoints();

				for (int i = 0; i < sz; ++i) {
					double x = f.x(i), y = f.y(i);

					if (filter.pp.accept(x, y)) {
						subFigure.addPoint(x, y);
					}
				}

				r.add(f);
			}
		}

		return r;
	}

	public static OperationID getPlot;

	@IdawiOperation
	public byte[] getPlot(Set<String> metricNames, String title, String format) {
		Plot plot = new Plot();
		metricNames.forEach(n -> plot.addFigure(name2figure.get(n)));
		plot.getSpace().getLegend().setText(title);
		byte[] rawData = getPlotRawData(plot, format);
		return rawData;
	}

	private final LongSet subscriptions = new LongOpenHashSet();

	public static OperationID getPlot_subscribe;

	public static class PSD {
		Set<String> metricNames; String title; String format;
	}
	
	@IdawiOperation
	public void getPlot_subscribe(MessageQueue in) {
		var msg = in.get_blocking();
		var parms = (PSD) msg.content;
	
		Plot plot = new Plot();
		parms.metricNames.forEach(n -> plot.addFigure(name2figure.get(n)));
		plot.getSpace().getLegend().setText(parms.title);
		long id = ThreadLocalRandom.current().nextLong();
		subscriptions.add(id);
		send(id, msg.requester);
		int periodMs = 1000;
		Threads.newThread_loop_periodic(periodMs, () -> subscriptions.contains(id), () -> {
			byte[] rawData = getPlotRawData(plot, parms.format);
			send(rawData, msg.requester);
		});
	}

	public static OperationID getPlot_unsubscribe;

	@IdawiOperation
	private void getPlot_unsubscribe(long id) {
		subscriptions.remove(id);
	}

	private byte[] getPlotRawData(Plot plot, String format) {
		if (format.equalsIgnoreCase("svg")) {
			return SVGPlotter.plot(plot).getBytes();
		} else if (format.equalsIgnoreCase("png")) {
			return new PNGPlotter().plot(plot);
		}

		throw new IllegalArgumentException("unknow image format: " + format);
	}
}
