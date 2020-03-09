package sacm.design.extensions.parts.shapes;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;

public class AxiomaticRelationshipShape extends AbstractRelationshipShape {

	@Override
	public void paint(Graphics graphics, Rectangle area, Point targetDirection) {
		int diameter = getShapeRadius(area);
		double target_angle = calculateRelativeTargetAngle(area, targetDirection);
		Point p1 = getPointAt(target_angle + Math.PI/2.0, diameter, area);
		Point p2 = getPointAt(target_angle - Math.PI/2.0, diameter, area);
		graphics.setLineWidth(4);
		graphics.drawLine(p1, p2);
		graphics.setLineWidth(2);
		p1 = getPointAt(target_angle, diameter, area);
		p2 = getPointAt(target_angle + Math.PI, diameter, area);
		graphics.drawLine(p1, p2);
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
		double radius = getShapeRadius(area); // Line Width					
		double angle = Math.atan2(target.preciseY(), target.preciseX());
		return (isTargetAnchor)?getPointAt(angle, radius, area):getPointAt(angle + Math.PI, radius, area);
	}
	
	private int getShapeRadius(Rectangle area) {
		// Minimum length - line width
		return (Math.min(area.width, area.height) - 4)/2;
	}
}
