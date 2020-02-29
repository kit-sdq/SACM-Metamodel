package sacm.design.extensions.parts;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;

public class CustomRelationshipAnchor extends AbstractConnectionAnchor{
	
	CustomRelationshipNode node;
	boolean isTargetAnchor;
	
	public CustomRelationshipAnchor(final IFigure owner, boolean isTargetAnchor, CustomRelationshipNode actualNode) {
        super(owner);
        this.node = actualNode;
        this.isTargetAnchor = isTargetAnchor;
    }
	
	@Override
	public Point getLocation(Point reference) {
		IFigure owner = getOwner();
		if(owner != null ) {
			Rectangle area = owner.getBounds();
			PrecisionPoint target = getTargetDirectionPoint();
			double radius, angle;
			switch (node.getAssertionDeclaration()) {
				case ASSERTED:
					radius = (Math.min(area.width, area.height)/1.5)/2.0;					
					angle = Math.atan2(target.preciseY(), target.preciseX());
					return (isTargetAnchor)?getPointAt(angle, radius):getPointAt(angle + Math.PI, radius);
				case ASSUMED:
					break;
				case AS_CITED:
					break;
				case AXIOMATIC:
					break;
				case DEFEATED:
					int diameter = (int)(Math.min(area.width, area.height));
					double sqrt_diameter = Math.sqrt(Math.PI*diameter);
					angle = Math.atan2(target.preciseY(), target.preciseX());
					return (isTargetAnchor)?getPointAt(angle, sqrt_diameter):getPointAt(angle + Math.PI, sqrt_diameter);					
				case NEEDS_SUPPORT:
					break;
				default:
					break;
			}
			if(reference.x >= area.right()) {
				return area.getRight();
			} else {
				return area.getLeft();
			}
		}
		else return getReferencePoint();		
	}
	
	/**
	 * Computes a point on the edge of a circle with a given angle starting from the 
	 * current center of the figure.
	 * @param angle
	 * @param radius
	 * @return 
	 */
	private Point getPointAt(double angle, double radius) {
		IFigure owner = getOwner();
		if(owner == null || owner.getBounds() == null) return new Point(0,0);
		PrecisionPoint center = new PrecisionPoint(owner.getBounds().getCenter());
		return new PrecisionPoint(
				Math.cos(angle) * radius + center.preciseX(),
				Math.sin(angle) * radius + center.preciseY());
	}
	
	private PrecisionPoint getTargetDirectionPoint(){
		if(node == null) return new PrecisionPoint();
		PrecisionPoint target = new PrecisionPoint(node.determineTargetDirection());
		target.translate(getOwner().getBounds().getCenter().negate());
		return (PrecisionPoint) target.getScaled(1.0/target.getDistance(new PrecisionPoint(0,0)));
	}

}
