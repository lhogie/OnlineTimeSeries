package ots;

import java.io.Serializable;

public interface FigurePredicate extends Serializable {
	boolean accept(String figureName);
}