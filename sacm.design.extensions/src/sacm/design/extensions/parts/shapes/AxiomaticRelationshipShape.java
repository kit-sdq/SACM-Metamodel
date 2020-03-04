package sacm.design.extensions.parts.shapes;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;

public class AxiomaticRelationshipShape extends AbstractRelationshipShape {

	@Override
	public void paint(Graphics graphics, Rectangle area, Point targetDirection) {
		paint(graphics, area);
	}

	@Override
	public void paint(Graphics graphics, Rectangle area) {
		int diameter = (int)(Math.min(area.width, area.height)/1.5);
		Point center = area.getCenter();
		graphics.fillOval(center.x - diameter/2, center.y - diameter/2, diameter, diameter);
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
		double radius = (Math.min(area.width, area.height)/1.5)/2.0;					
		double angle = Math.atan2(target.preciseY(), target.preciseX());
		return (isTargetAnchor)?getPointAt(angle, radius, area):getPointAt(angle + Math.PI, radius, area);
	}
}
