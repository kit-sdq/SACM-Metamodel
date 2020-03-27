package sacm.design.extensions.parts.shapes;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * A RelationshipShape defines a custom way to draw a SACM AssertedRelationship status, e.g. based
 * on it's AssertedDeclaration status. Further it allows a Shape to define custom anchor points.
 * 
 * @author Fabian Scheytt
 *
 */
public interface RelationshipShape {
	
	/**
	 * Paints the RelationshipShape in the given area not regarding
	 * any target direction.
	 * @param graphics
	 * @param area
	 */
	public void paint(Graphics graphics, Rectangle area);

	/**
	 * Paints the RelationshipShape in the given area rotated towards
	 * the given targetDirection.
	 * @param graphics
	 * @param area
	 * @param targetDirection
	 */
	public void paint(Graphics graphics, Rectangle area, Point targetDirection);
	
	/**
	 * Determines a source connection anchor point for the RelationshipShape.
	 * @param area
	 * @param target
	 * @return anchor point
	 */
	public Point getSourceConnectionPoint(Rectangle area, PrecisionPoint target);
	
	/**
	 * Determines a target connection anchor point for the RelationshipShape.
	 * @param area
	 * @param target
	 * @return anchor point
	 */
	public Point getTargetConnectionPoint(Rectangle area, PrecisionPoint target);
	 
	/**
	  * Determines a connection anchor point to the RelationshipShape for an
	  * ArgumentReasoning edge connection to the shape.
	  * @param area
	  * @param target
	  * @param reference
	  * @return anchor point
	  */
	public Point getArgumentReasoningConnectionPoint(Rectangle area, PrecisionPoint target, Point reference);
}
