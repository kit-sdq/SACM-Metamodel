package sacm.design.extensions;

import org.eclipse.gmf.runtime.diagram.ui.services.editpart.AbstractEditPartProvider;
import sacm.design.extensions.parts.CustomEdge;
import org.eclipse.gmf.runtime.notation.View;

public class EditPartProvider extends AbstractEditPartProvider {

	@Override
    protected Class getNodeEditPartClass(View view) {
        return super.getNodeEditPartClass(view);
    }
	
	@Override
	protected Class getEdgeEditPartClass(View view) {
		return CustomEdge.class;
	}
}
