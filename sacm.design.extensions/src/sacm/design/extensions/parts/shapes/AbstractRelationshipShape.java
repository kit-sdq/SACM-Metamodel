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
				targetDirection.preciseX() - area.preciseX(),
				targetDirection.preciseY() - area.preciseY());
		return Math.atan2(realtive_target.preciseY(), realtive_target.preciseX());
	}
	
	protected PointList createPointList(Point... points) {
		PointList list = new PointList(points.length);
		for(Point p : points)
			list.addPoint(p);
		return list;
	}

}
