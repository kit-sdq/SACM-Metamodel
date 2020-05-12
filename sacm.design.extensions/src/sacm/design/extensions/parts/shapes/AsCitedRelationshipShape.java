package sacm.design.extensions.parts.shapes;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.PolylineDecoration;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;

public class AsCitedRelationshipShape extends AbstractRelationshipShape {

	@Override
	public void paint(Graphics graphics, Rectangle area, Point targetDirection) {
		//FigureSize width = 4 + lineWidth; height = 6 + lineWidth
		// => outer_diameter = sqrt(width^2 + height^2) = 10 => scale according to height
		double scaling = Math.min(area.width, area.height)/10;
		graphics.translate(area.getCenter());
		Point direction = targetDirection.getTranslated(area.getCenter().negate());
		
		PolylineDecoration[] shape = new PolylineDecoration[3];
		shape[0] = new PolylineDecoration();
		shape[0].setReferencePoint(direction);
		shape[0].setScale(scaling, scaling);
		shape[0].setLineWidth(2);
		shape[0].setTemplate(createPointList(
				new Point(-2, 1),
				new Point(-2, 3),
				new Point(2, 3),
				new Point(2, 1)
				));
		
		shape[1] = new PolylineDecoration();
		shape[1].setScale(scaling, scaling);
		shape[1].setReferencePoint(direction);
		shape[1].setLineWidth(2);
		shape[1].setTemplate(createPointList(
				new Point(-3, 0),
				new Point(3, 0)
				));
		
		shape[2] = new PolylineDecoration();
		shape[2].setScale(scaling, scaling);
		shape[2].setReferencePoint(direction);
		shape[2].setLineWidth(2);
		shape[2].setTemplate(createPointList(
				new Point(-2, -1),
				new Point(-2, -3),
				new Point(2, -3),
				new Point(2, -1)
				));	
		
		shape[0].paint(graphics);
		shape[1].paint(graphics);
		shape[2].paint(graphics);
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
		double radius = 3.0*Math.min(area.width, area.height)/10.0;					
		double angle = Math.atan2(target.preciseY(), target.preciseX());
		return (isTargetAnchor)?getPointAt(angle, radius, area):getPointAt(angle + Math.PI, radius, area);
	}
}
