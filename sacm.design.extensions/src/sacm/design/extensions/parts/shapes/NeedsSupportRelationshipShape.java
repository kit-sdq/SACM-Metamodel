package sacm.design.extensions.parts.shapes;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;

public class NeedsSupportRelationshipShape extends AbstractRelationshipShape {

	@Override
	public void paint(Graphics graphics, Rectangle area, Point targetDirection) {
		// fit three equally sized dot's with equal amount of space in between
		int diameter = (int)(Math.min(area.width, area.height));
		int space = (int)(diameter/4.0);
		int outer_radius = (int)(1.5 * space);
		double target_angle = calculateRelativeTargetAngle(area, targetDirection);
		Point center = area.getCenter();		
		Point oval_1 = getPointAt(target_angle, outer_radius, area);
		Point oval_2 = getPointAt(target_angle + Math.PI, outer_radius, area);

		graphics.fillOval(center.x - space/2, center.y - space/2, space, space);
		graphics.fillOval(oval_1.x - space/2, oval_1.y - space/2, space, space);
		graphics.fillOval(oval_2.x - space/2, oval_2.y - space/2, space, space);
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
		double radius = (Math.min(area.width, area.height) + 2)/2.0;					
		double angle = Math.atan2(target.preciseY(), target.preciseX());
		return (isTargetAnchor)?getPointAt(angle, radius, area):getPointAt(angle + Math.PI, radius, area);
	}
}
