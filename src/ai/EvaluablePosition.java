/*
 * Created on 04.01.2007
 */
package ai;

import java.util.Comparator;

import gameelements.Position;

/**
 * Extends a positions so that it can be set with an evaluation value. Also 
 * makes the position sortable by this value. 
 */
class EvaluablePosition extends Position
{
	/**
	 * An evaluation value for this position.
	 */
	private double evaluation;
	
	/**
	 * Initializes an evaluable position with the given coordinates.
	 * @param x the x-coordinate.
	 * @param y the y-coordinate.
	 */
	EvaluablePosition(int x, int y)
	{
		super(x, y);
	}

	/**
	 * Returns the evaluation value for this position.
	 * @return the evaluation value for this position.
	 */
	double getEvaluation()
	{
		return evaluation;
	}

	/**
	 * Sets the evaluation value for this position.
	 * @param evaluation the evaluation value to set.
	 */
	void setEvaluation(double evaluation)
	{
		this.evaluation = evaluation;
	}
	
	/**
	 * Returns an ascending comparator for evaluable positions.
	 * @return an ascending comparator for evaluable positions.
	 */
	static EvaluablePositionComparatorAsc comparatorAsc()
	{
		return new EvaluablePositionComparatorAsc();
	}
	
	/**
	 * Returns a descending comparator for evaluable positions.
	 * @return a descending comparator for evaluable positions.
	 */
	static EvaluablePositionComparatorDesc comparatorDesc()
	{
		return new EvaluablePositionComparatorDesc();
	}
	
	private static class EvaluablePositionComparatorAsc implements Comparator<EvaluablePosition>
	{
		public int compare(EvaluablePosition o1, EvaluablePosition o2)
		{
			if (o1.getEvaluation() < o2.getEvaluation())
				return -1;
			else if (o1.getEvaluation() > o2.getEvaluation())
				return +1;
			return 0;
		}
	}
	
	private static class EvaluablePositionComparatorDesc implements Comparator<EvaluablePosition>
	{
		public int compare(EvaluablePosition o1, EvaluablePosition o2)
		{
			if (o1.getEvaluation() > o2.getEvaluation())
				return -1;
			else if (o1.getEvaluation() < o2.getEvaluation())
				return +1;
			return 0;
		}
	}
}
