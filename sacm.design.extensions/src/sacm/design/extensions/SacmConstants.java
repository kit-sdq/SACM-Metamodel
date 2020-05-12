package sacm.design.extensions;

/**
 * Provides constants and enums for arrow decorators or for switching
 * between edge types, among other things.
 * 
 * @author Fabian Scheytt
 *
 */
public class SacmConstants {

	public static enum ArrowDecoratorType {
		NONE,
		CONTEXT_FILLED,
		CONTEXT_OUTLINE,
		ARROW_FILLED,
		ARROW_OUTLINE,
		META_CLAIM
	}
	
	public static enum SACMEdgeType {
		RELATIONSHIP_TARGET,
		SOURCE_RELATIONSHIP,
		META_CLAIM,
		ARGUMENT_REASONING,
		NONE;
		
		public static SACMEdgeType getFromName(String type) {
			switch (type) {				
				case "AssertedRelationshipToTarget":
					return RELATIONSHIP_TARGET;
				case "ArgumentReasoningEdge":
					return ARGUMENT_REASONING;
				case "MetaClaim":
					return META_CLAIM;			
				case "AssertedRelationshipFromSource":
					return SOURCE_RELATIONSHIP;
			    default:
			    	return NONE;
			}
		}
	}
}
