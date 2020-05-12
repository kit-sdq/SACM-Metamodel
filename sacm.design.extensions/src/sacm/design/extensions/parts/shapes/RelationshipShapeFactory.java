package sacm.design.extensions.parts.shapes;

import java.util.EnumMap;
import java.util.Map;

import org.omg.sacm.argumentation.AssertionDeclaration;

/**
 * Factory for the different SACM RelationshipShapes.
 * RelationshipShapes are regarded as Singletons and only instantiated on first use to avoid
 * unnecessary instantiation on each draw call and for each different node. 
 * 
 * @author Fabian Scheytt
 *
 */
public enum RelationshipShapeFactory {
	INSTANCE;
	
	private Map<AssertionDeclaration, RelationshipShape> shapes;
	
	private RelationshipShapeFactory() {
		shapes = new EnumMap<>(AssertionDeclaration.class);
	}
	
	/**
	 * Create a RelationshipShape fitting for the given AssertionDeclaration type
	 * @param type
	 * @return RelationshipShape
	 */
	public RelationshipShape getRelationshipShape(AssertionDeclaration type) {
		return shapes.computeIfAbsent(type, (t) -> createRelationshipShape(t));		
	}
	
	private RelationshipShape createRelationshipShape(AssertionDeclaration type) {
		switch (type) {
			case ASSERTED:
				return new AssertedRelationshipShape();
			case ASSUMED:
				return new AssumedRelationshipShape();
			case AS_CITED:
				return new AsCitedRelationshipShape();
			case AXIOMATIC:
				return new AxiomaticRelationshipShape();
			case DEFEATED:
				return new DefeatedRelationshipShape();
			case NEEDS_SUPPORT:
				return new NeedsSupportRelationshipShape();
			default:
				break;
		}
		return null;
	}
}
