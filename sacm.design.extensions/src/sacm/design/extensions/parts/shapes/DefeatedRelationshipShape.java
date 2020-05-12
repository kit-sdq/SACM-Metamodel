package sacm.design.extensions.parts.shapes;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;

public class DefeatedRelationshipShape extends AbstractRelationshipShape {

	@Override
	public void paint(Graphics graphics, Rectangle area, Point targetDirection) {
		graphics.setBackgroundColor(ColorConstants.black);
		graphics.setForegroundColor(ColorConstants.black);
		graphics.setLineWidth(2);
		Point center = area.getCenter();
		int diameter = (int)(Math.min(area.width, area.height));
		double sqrt_diameter = Math.sqrt(Math.PI*diameter);
		
		graphics.translate(center);
		PolygonDecoration shape = new PolygonDecoration();
		shape.setTemplate(createPointList(
			new PrecisionPoint(-sqrt_diameter, -sqrt_diameter),
			new Point(0,0),
			new PrecisionPoint(+sqrt_diameter, +sqrt_diameter),
			new Point(0,0),
			new PrecisionPoint(+sqrt_diameter, -sqrt_diameter),
			new Point(0,0),
			new PrecisionPoint(-sqrt_diameter, +sqrt_diameter),
			new Point(0,0)
			));
		shape.setScale(1, 1);
		shape.setReferencePoint(new Point(targetDirection.x - area.x, targetDirection.y - area.y));
		shape.paint(graphics);
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
		int diameter = (int)(Math.min(area.width, area.height));
		double sqrt_diameter = Math.sqrt(Math.PI*diameter);
		double angle = Math.atan2(target.preciseY(), target.preciseX());
		return (isTargetAnchor)?getPointAt(angle, sqrt_diameter, area):getPointAt(angle + Math.PI, sqrt_diameter, area);
	}
}
