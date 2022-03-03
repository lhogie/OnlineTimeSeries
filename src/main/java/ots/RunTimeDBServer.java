package ots;

import java.io.StringReader;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;

import idawi.Component;
import idawi.ComponentDescriptor;
import idawi.net.LMI;
import idawi.service.ServiceManager;
import idawi.service.rest.RESTService;
import ots.TimeSeriesDB.addPoint;
import ots.TimeSeriesDB.registerMetric;
import toools.gui.Utilities;
import toools.thread.Threads;

public class RunTimeDBServer {
	public static void main(String[] args) throws Throwable {
		System.out.println("start");
		var c = new Component("a");
		var c2 = new Component("b");
		LMI.connect(c, c2);
		// starts the TimeSeriesDB service

		int port = 8081;

		c.lookup(RESTService.class).startHTTPServer(port);
		System.out.println("API: http://localhost:" + port + "/api/" + c.name);
		System.out.println("Web: http://localhost:" + port + "/web/ots/display/index.html");
		
//			System.out.println("Website URL: http://localhost:" + port + "/web/og/display/ls.html");

		// creates the figure that will be fed
		c.lookupO(ServiceManager.start.class).f(TimeSeriesDB.class);
		var tsd = c.lookup(TimeSeriesDB.class);
		tsd.lookup(registerMetric.class).f("testMetric");
		// startGUI2(server, serverDescriptor);

		
		
		var r = new Random();
Threads.sleepForever();
		// runs the simulation
		for (double step = 0;; step += r.nextDouble()) {
			// computes something
			Threads.sleepMs(1000);
			System.out.println("sending point");
			// send point
			tsd.lookup(addPoint.class).f("testMetric", step, Double.longBitsToDouble(r.nextLong()));
		}
	}

	private static void startGUI(TimeSeriesDBStub localDB, ComponentDescriptor remoteDB) {
		String parser = XMLResourceDescriptor.getXMLParserClassName();
		SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
		JSVGCanvas c = new JSVGCanvas();
		JFrame frame = Utilities.displayInJFrame(c, "demo for Julien");
		frame.setSize(800, 600);
		long startDate = System.currentTimeMillis();
		AtomicInteger i = new AtomicInteger();

		Threads.newThread_loop(() -> {

			try {
				String svg = new String(localDB.getPlot(Set.of("some metric"), "my first plot", "svg"));
				SVGDocument document = factory.createSVGDocument(null, new StringReader(svg));
				c.setSVGDocument(document);
			} catch (Throwable e) {
				e.printStackTrace();
			}

			double durationS = ((System.currentTimeMillis() - startDate) / 1000);

			if (durationS > 0) {
				double freq = i.get() / durationS;
				System.out.println("updated " + freq + "frame/s");
			}

			i.incrementAndGet();
		});
	}

	private static void startGUI2(TimeSeriesDBStub client, ComponentDescriptor server) {
		JLabel c = new JLabel();
		JFrame frame = Utilities.displayInJFrame(c, "demo for Julien");
		frame.setSize(800, 600);

		long startDate = System.currentTimeMillis();
		AtomicInteger i = new AtomicInteger();

		Threads.newThread_loop(() -> {
			try {
				var fig = client.download("some metric");
				System.out.println(fig);
				byte[] png = client.getPlot(Set.of("some metric"), "my first plot", "png");

				if (png != null) {
					c.setIcon(new ImageIcon(png));

					double durationS = ((System.currentTimeMillis() - startDate) / 1000);

					if (durationS > 0) {
						double freq = i.get() / durationS;
						System.out.println("updated " + freq + "frame/s");
					}

				}
				i.incrementAndGet();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		});
	}
}
