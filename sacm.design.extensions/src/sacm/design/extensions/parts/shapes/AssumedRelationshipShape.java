package sacm.design.extensions.parts.shapes;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;

import com.sun.org.apache.bcel.internal.Const;

public class AssumedRelationshipShape extends AbstractRelationshipShape {
	
	private static final double TWO_ROOT = Math.sqrt(2.0);

	@Override
	public void paint(Graphics graphics, Rectangle area, Point targetDirection) {
		int max_radius = getShapeRadius(area);		
		double target_angle = calculateRelativeTargetAngle(area, targetDirection);
		Point left_1 = getPointAt(target_angle + Math.PI/4.0, max_radius, area);
		Point left_2 = getPointAt(target_angle - Math.PI/4.0, max_radius, area);
		Point right_1 = getPointAt(target_angle + 3 * Math.PI/4.0, max_radius, area);
		Point right_2 = getPointAt(target_angle - 3 * Math.PI/4.0, max_radius, area);
		graphics.setLineWidth(2);
		graphics.drawLine(left_1, left_2);
		graphics.drawLine(right_1, right_2);
	}

	@Override
	public void paint(Graphics graphics, Rectangle area) {
		paint(graphics, area, area.getRight());
	}

	@Override
	public Point getSourceConnectionPoint(Rectangle area, PrecisionPoint target) {
		return calculateConnectionPoint(area, target, true);
	}

	@Override
	public Point getTargetConnectionPoint(Rectangle area, PrecisionPoint target) {
		return calculateConnectionPoint(area, target, false);
	}
	
	private Point calculateConnectionPoint(Rectangle area, PrecisionPoint target, boolean isTargetAnchor) {
		double radius = getConnectionRadius(area);
		double angle = Math.atan2(target.preciseY(), target.preciseX());
		return (isTargetAnchor)?getPointAt(angle, radius, area):getPointAt(angle + Math.PI, radius, area);
	}
	
	private double getConnectionRadius(Rectangle area) {
		//Return width/2 of the largest square that fit's into the shapes radius
		return getShapeRadius(area) / TWO_ROOT;
	}
	
	private int getShapeRadius(Rectangle area) {
		return Math.min(area.width - 2, area.height - 2)/2;
	}
}
