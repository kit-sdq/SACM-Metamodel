package sacm.design.extensions.parts;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;

import sacm.design.extensions.parts.shapes.RelationshipShape;
import sacm.design.extensions.parts.shapes.RelationshipShapeFactory;

public class CustomRelationshipAnchor extends AbstractConnectionAnchor{
	
	CustomRelationshipNode node;
	boolean isTargetAnchor;
	boolean isArgumentReasoningAnchor;
	
	public CustomRelationshipAnchor(final IFigure owner, boolean isTargetAnchor, CustomRelationshipNode actualNode) {
        super(owner);
        this.node = actualNode;
        this.isTargetAnchor = isTargetAnchor;
        this.isArgumentReasoningAnchor = false;
    }

	@Override
	public Point getLocation(Point reference) {
		IFigure owner = getOwner();
		if(owner != null ) {
			Rectangle area = new Rectangle(owner.getClientArea());
			getOwner().translateToAbsolute(area);
			PrecisionPoint target = getTargetDirectionPoint();
			RelationshipShape s = RelationshipShapeFactory.INSTANCE.getRelationshipShape(node.getAssertionDeclaration());
			if (isArgumentReasoningAnchor) {
				return s.getArgumentReasoningConnectionPoint(area, target, reference);
			} else if (isTargetAnchor)
				return s.getSourceConnectionPoint(area, target);
			else
				return s.getTargetConnectionPoint(area, target);
		}
		else return getReferencePoint();		
	}
	
	private PrecisionPoint getTargetDirectionPoint(){
		if(node == null) return new PrecisionPoint();
		PrecisionPoint target = new PrecisionPoint(node.determineTargetDirection());
		target.translate(getOwner().getBounds().getCenter().negate());
		return (PrecisionPoint) target.getScaled(1.0/target.getDistance(new PrecisionPoint(0,0)));
	}

	public boolean isArgumentReasoningAnchor() {
		return isArgumentReasoningAnchor;
	}

	public void setArgumentReasoningAnchor(boolean isArgumentReasoningAnchor) {
		this.isArgumentReasoningAnchor = isArgumentReasoningAnchor;
	}
	
}
