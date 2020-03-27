package sacm.design.extensions.parts;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;

import sacm.design.extensions.SacmConstants.SACMEdgeType;
import sacm.design.extensions.parts.shapes.RelationshipShape;
import sacm.design.extensions.parts.shapes.RelationshipShapeFactory;

/**
 * Customized ConnectionAcnhor implementation for the SACMRelationshipNodes. This Anchor regards the different
 * RelationshipShape implementations and generates anchor points based on edge type and shape type.
 * 
 * @author Fabian Scheytt
 *
 */
public class CustomRelationshipAnchor extends AbstractConnectionAnchor{
	
	private CustomRelationshipNode node;
	private SACMEdgeType sacmEdgeType;
	
	public CustomRelationshipAnchor(final IFigure owner, SACMEdgeType edgeType, CustomRelationshipNode actualNode) {
        super(owner);
        this.node = actualNode;
        this.sacmEdgeType = edgeType;
    }

	@Override
	public Point getLocation(Point reference) {
		// Determine anchor point from the respective shape implementation of the current node
		IFigure owner = getOwner();
		if(owner != null ) {
			Rectangle area = new Rectangle(owner.getClientArea());
			getOwner().translateToAbsolute(area);
			PrecisionPoint target = getTargetDirectionPoint();
			RelationshipShape s = RelationshipShapeFactory.INSTANCE.getRelationshipShape(node.getAssertionDeclaration());
			switch (sacmEdgeType) {
				case META_CLAIM:
				case ARGUMENT_REASONING:
					return s.getArgumentReasoningConnectionPoint(area, target, reference);
				case RELATIONSHIP_TARGET:
					return s.getSourceConnectionPoint(area, target);				
				case SOURCE_RELATIONSHIP:
					return s.getTargetConnectionPoint(area, target);
				default:
					return s.getTargetConnectionPoint(area, target);
			}
		}
		else return getReferencePoint();		
	}
	
	/**
	 * Determine direction to the target node of the AssertedRelationship.
	 * @return <b>Normalized</b> target direction
	 */
	private PrecisionPoint getTargetDirectionPoint(){
		if(node == null) return new PrecisionPoint();
		PrecisionPoint target = new PrecisionPoint(node.determineTargetDirection());
		target.translate(getOwner().getBounds().getCenter().negate());
		return (PrecisionPoint) target.getScaled(1.0/target.getDistance(new PrecisionPoint(0,0)));
	}
	
}
