package sacm.design.extensions;

import org.eclipse.gmf.runtime.diagram.ui.services.editpart.AbstractEditPartProvider;

import sacm.design.extensions.SacmConstants.SACMEdgeType;
import sacm.design.extensions.parts.CustomSACMEdge;
import sacm.design.extensions.parts.CustomRelationshipNode;

import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.sirius.diagram.DEdge;
import org.eclipse.sirius.diagram.DNode;
import org.eclipse.sirius.diagram.description.EdgeMapping;
import org.omg.sacm.argumentation.AssertedRelationship;

/**
 * Provides all custom edge and node implementations to Sirius via the
 * GMF "editpartProviders" extension point. 
 * 
 * @author Fabian Scheytt
 *
 */
@SuppressWarnings("rawtypes")
public class EditPartProvider extends AbstractEditPartProvider {
	
	@Override
    protected Class getNodeEditPartClass(View view) {
		if (view.getElement() instanceof DNode) {
			DNode node = (DNode) view.getElement();
			if (node.getTarget() instanceof AssertedRelationship) {
				return CustomRelationshipNode.class;
			}
		}
        return super.getNodeEditPartClass(view);
    }
	
	@Override
	protected Class getEdgeEditPartClass(View view) {
		if (view.getElement() instanceof DEdge) {
			DEdge edge = (DEdge)view.getElement();
			if(edge.getActualMapping() instanceof EdgeMapping) {
				SACMEdgeType type = SACMEdgeType.getFromName(((EdgeMapping)edge.getActualMapping()).getName());
				if (type != SACMEdgeType.NONE) {
					return CustomSACMEdge.class;
				}
			}					
		}
		return super.getEdgeEditPartClass(view);
	}
}
