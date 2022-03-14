package ots;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.ArrayList;
import java.util.Random;

import idawi.Component;
import idawi.Service;
import idawi.Utils;
import idawi.net.LMI;
import idawi.service.DeployerService;
import idawi.service.ServiceManager;
import idawi.service.rest.WebServer;
import ots.TimeSeriesDB.addPoints;
import ots.TimeSeriesDB.getMetricNames;
import ots.TimeSeriesDB.registerMetric;
import toools.thread.Threads;
import toools.util.Date;

public class Rest {

	public static void main(String[] args) throws Throwable {
		Component a = new Component();
		var deployer = a.lookup(DeployerService.class);
		var components = new ArrayList<>(deployer.deployInThisJVM(10, i -> "newc" + i, true, null));
		System.out.println("connecting...");
		LMI.gnp(components, 0.3);
		System.out.println("ok");
		var s = new Service(a);

		System.out.println("starting timeDB service");
		a.lookup(ServiceManager.class).ensureStarted(TimeSeriesDB.class);
		var timeDB = a.lookup(TimeSeriesDB.class);
		System.out.println("starting Rest service");
		a.lookup(ServiceManager.class).ensureStarted(WebServer.class);
		a.lookup(WebServer.class).startHTTPServer();
		var prng = new Random();
		PointBuffer buf = new PointBuffer();
		OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();

		while (true) {
			Threads.sleepMs(100);

			for (var c : components) {
				String metricName = c.descriptor().name + " load";

				if (!timeDB.lookup(getMetricNames.class).f().contains(metricName)) {
					timeDB.lookup(registerMetric.class).f(metricName);
				}

				buf.add(metricName, Date.time(), Utils.loadRatio());
			}

			timeDB.lookup(addPoints.class).f(buf);
			buf.clear();
		}
	}

}