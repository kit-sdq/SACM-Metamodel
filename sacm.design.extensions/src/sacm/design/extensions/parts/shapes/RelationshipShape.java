package sacm.design.extensions.parts.shapes;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;

public interface RelationshipShape {
	
	public void paint(Graphics graphics, Rectangle area);

	public void paint(Graphics graphics, Rectangle area, Point targetDirection);
	
	public Point getSourceConnectionPoint(Rectangle area, PrecisionPoint target);
	
	public Point getTargetConnectionPoint(Rectangle area, PrecisionPoint target);
	
	public Point getArgumentReasoningConnectionPoint(Rectangle area, PrecisionPoint target, Point reference);
}
