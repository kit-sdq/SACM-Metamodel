package sacm.design.extensions;

import org.eclipse.gmf.runtime.diagram.ui.services.editpart.AbstractEditPartProvider;
import sacm.design.extensions.parts.CustomEdge;
import sacm.design.extensions.parts.CustomRelationshipNode;

import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.sirius.diagram.DNode;
import org.omg.sacm.argumentation.AssertedRelationship;

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
		return CustomEdge.class;
	}
}
