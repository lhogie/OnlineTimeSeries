package ots;

import java.io.Serializable;
import java.util.Set;

class Subscribe implements Serializable {
	Set<String> metricNames;
	String id;
}