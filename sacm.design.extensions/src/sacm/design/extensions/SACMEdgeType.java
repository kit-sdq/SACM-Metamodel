package sacm.design.extensions;

public enum SACMEdgeType {
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
