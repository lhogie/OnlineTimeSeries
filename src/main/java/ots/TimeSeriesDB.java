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
import java.util.stream.Collectors;

import idawi.Component;
import idawi.MessageQueue;
import idawi.Service;
import idawi.Streams;
import idawi.TypedInnerOperation;
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

	private Map<String, Metric> name2figure = new HashMap<>();
	private Directory baseDir = new Directory("$HOME/.timeSeriesDB");

	public TimeSeriesDB(Component node) {
		super(node);
		registerOperation(new addPoint());
		registerOperation(new addPoints());
		registerOperation(new filter());
		registerOperation(new getMetricInfo());
		registerOperation(new getMetricNames());
		registerOperation(new getNbPoints());
		registerOperation(new getPlot());
		registerOperation(new getPlot_unsubscribe());
		registerOperation(new getPlot_subscribe());
		registerOperation(new getWorkbenchList());
		registerOperation(new load());
		registerOperation(new registerMetric());
		registerOperation(new removeMetric());
		registerOperation(new retrieveAllPoints());
		registerOperation(new retrieveWorkbench());
		registerOperation(new setMetricColor());
		registerOperation(new store());
	}

	public class addPoint extends TypedInnerOperation {
		public void f(String metric, double x, double y) {
			var a = name2figure.get(metric);

			if (a == null)
				throw new Error("figure not found: " + metric);

			a.addPoint(x, y);
		}

		@Override
		public String getDescription() {
			return "add one single point";
		}
	}

	public class addPoints extends TypedInnerOperation {
		public void f(PointBuffer buf) {

			for (var f : buf.values()) {
				var a = name2figure.get(f.name);

				if (a == null)
					throw new Error("figure not found: " + f.name);

				a.addPoints(f);
			}
		}

		@Override
		public String getDescription() {
			return "add multiple points";
		}
	}

	@Override
	public String getDescription() {
		return "stores (name, x, y) triplets";
	}

	public class store extends TypedInnerOperation {
		public void f(String workbenchName) {
			var file = new RegularFile(baseDir, workbenchName);
			file.setContentAsJavaObject(name2figure);
		}

		@Override
		public String getDescription() {
			return "add multiple points";
		}
	}

	public class load extends TypedInnerOperation {
		public void f(String workbenchName) {
			var file = new RegularFile(baseDir, workbenchName);
			name2figure = (Map<String, Metric>) file.getContentAsJavaObject();
		}

		@Override
		public String getDescription() {
			return "add multiple points";
		}
	}

	public class removeMetric extends TypedInnerOperation {
		public void f(String figName) {
			name2figure.remove(figName);
		}

		@Override
		public String getDescription() {
			return "add multiple points";
		}
	}

	public class getNbPoints extends TypedInnerOperation {
		public int f(String figName) {
			return name2figure.get(figName).getNbPoints();
		}

		@Override
		public String getDescription() {
			return "add multiple points";
		}
	}

	public class getMetricNames extends TypedInnerOperation {
		public Set<String> f() {
			return new HashSet<>(name2figure.keySet());
		}

		@Override
		public String getDescription() {
			return "add multiple points";
		}
	}

	public static class MetricInfo implements Serializable {
		String name;
		Unit unit;
		int nbValues;
		double lastX, lastY;
	}

	public class getMetricInfo extends TypedInnerOperation {
		public Set<MetricInfo> f() {
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

		@Override
		public String getDescription() {
			return "add multiple points";
		}
	}

	public class getWorkbenchList extends TypedInnerOperation {
		public Set<String> f() {
			return baseDir.listRegularFiles().stream().map(f -> f.getName()).collect(Collectors.toSet());
		}

		@Override
		public String getDescription() {
			return "add multiple points";
		}
	}

	public class retrieveAllPoints extends TypedInnerOperation {
		public Metric f(String metricName) {
			return name2figure.get(metricName);
		}

		@Override
		public String getDescription() {
			return "add multiple points";
		}
	}

	public class retrieveWorkbench extends TypedInnerOperation {
		public void f(MessageQueue in) throws Throwable {
			PipedOutputStream pos = new PipedOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(pos);
			oos.writeObject(name2figure);
			Streams.split(new PipedInputStream(pos), 1000, b -> send(b, in.poll_sync().replyTo));
		}

		@Override
		public String getDescription() {
			return "add multiple points";
		}
	}

	public class registerMetric extends TypedInnerOperation {
		synchronized public void f(String name) {
			var f = new Metric();
			f.setName(name);
			// f.addRenderer(new ConnectedLineFigureRenderer());
			name2figure.put(name, f);
		}

		@Override
		public String getDescription() {
			return "add multiple points";
		}
	}

	public class setMetricColor extends TypedInnerOperation {
		synchronized public void f(String figName, Color color) {
			name2figure.get(figName).setColor(color);
		}

		@Override
		public String getDescription() {
			return "add multiple points";
		}
	}

	public class filter extends TypedInnerOperation {
		synchronized public Set<Metric> f(Filter filter) {
			Set<Metric> r = new HashSet<>();

			for (var f : name2figure.values()) {
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

		@Override
		public String getDescription() {
			return "filter";
		}
	}

	public class getPlot extends TypedInnerOperation {
		public byte[] f(Set<String> metricNames, String title, String format) {
			Plot plot = new Plot();
			metricNames.forEach(n -> plot.addFigure(name2figure.get(n).tofigure()));
			plot.getSpace().getLegend().setText(title);
			byte[] rawData = getPlotRawData(plot, format);
			return rawData;
		}

		@Override
		public String getDescription() {
			return "getPlot";
		}
	}

	private final LongSet subscriptions = new LongOpenHashSet();

	public static class PSD {
		Set<String> metricNames;
		String title;
		String format;
	}

	public class getPlot_subscribe extends TypedInnerOperation {
		public void f(MessageQueue in) {
			var msg = in.poll_sync();
			var parms = (PSD) msg.content;

			Plot plot = new Plot();
			parms.metricNames.forEach(n -> plot.addFigure(name2figure.get(n).tofigure()));
			plot.getSpace().getLegend().setText(parms.title);
			long id = ThreadLocalRandom.current().nextLong();
			subscriptions.add(id);
			send(id, msg.replyTo);
			int periodMs = 1000;
			Threads.newThread_loop_periodic(periodMs, () -> subscriptions.contains(id), () -> {
				byte[] rawData = getPlotRawData(plot, parms.format);
				send(rawData, msg.replyTo);
			});
		}

		@Override
		public String getDescription() {
			return "getPlot";
		}
	}

	public class getPlot_unsubscribe extends TypedInnerOperation {
		public void getPlot_unsubscribe(long id) {
			subscriptions.remove(id);
		}

		@Override
		public String getDescription() {
			return "getPlot";
		}
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
