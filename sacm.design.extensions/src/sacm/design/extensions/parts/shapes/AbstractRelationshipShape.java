package sacm.design.extensions.parts.shapes;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;

public abstract class AbstractRelationshipShape implements RelationshipShape {

	/**
	 * Computes a point on the edge of a circle with a given angle starting from the 
	 * center of the given area.
	 * @param angle
	 * @param radius
	 * @param area
	 * @return 
	 */
	protected Point getPointAt(double angle, double radius, Rectangle area) {
		if(area == null) return new Point(0,0);
		PrecisionPoint center = new PrecisionPoint(area.getCenter());
		return new PrecisionPoint(
				Math.cos(angle) * radius + center.preciseX(),
				Math.sin(angle) * radius + center.preciseY());
	}
	
	protected double calculateRelativeTargetAngle(Rectangle area, Point targetDirection) {
		PrecisionPoint realtive_target = new PrecisionPoint(
				targetDirection.preciseX() - area.getCenter().preciseX(),
				targetDirection.preciseY() - area.getCenter().preciseY());
		return Math.atan2(realtive_target.preciseY(), realtive_target.preciseX());
	}
	
	protected PointList createPointList(Point... points) {
		PointList list = new PointList(points.length);
		for(Point p : points)
			list.addPoint(p);
		return list;
	}
	
	@Override
	public Point getArgumentReasoningConnectionPoint(Rectangle area, PrecisionPoint target, Point reference) {
		double radius = 1.2 * Math.min(area.preciseWidth(), area.preciseHeight()) / 2.0;
		double angle = Math.atan2(target.preciseY(), target.preciseX());
		Point right = getPointAt(angle + Math.PI * .5,	radius, area);
		Point left = getPointAt(angle - Math.PI * .5,	radius, area);
		return (left.getDistance(reference) > right.getDistance(reference))? right : left;
	}

}
