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
import idawi.service.ServiceManager;
import idawi.service.rest.RESTService;
import toools.gui.Utilities;
import toools.thread.Threads;

public class RunTimeDBServer {
	public static void main(String[] args) throws Throwable {
		System.out.println("start");
		var c = new Component();
		// starts the TimeSeriesDB service
		c.lookupService(ServiceManager.class).start(TimeSeriesDB.class);
		var tsd = c.lookupService(TimeSeriesDB.class);

		int port = 8084;
		 c.lookupService(RESTService.class).startHTTPServer(port);
			System.out.println("URL: http://localhost:" + port + "/api/" + c.friendlyName);
			System.out.println("Website URL: http://localhost:" + port + "/web/ots/display/index.html");

		// creates the figure that will be fed
		tsd.registerMetric("testMetric");
		tsd.registerMetric("Metric1");

		// startGUI2(server, serverDescriptor);
		var r = new Random();

		// runs the simulation
		for (double step = 0;; step += r.nextDouble()) {
			// computes something
			Threads.sleepMs(1000);
			System.out.println("sending point");
			// send point
			//tsd.addPoint("testMetric", step, Double.longBitsToDouble(r.nextLong()));
			tsd.addPoint("testMetric", step, r.nextDouble());
			tsd.addPoint("Metric1", step, r.nextDouble());
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
				// TODO Auto-generated catch block
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
