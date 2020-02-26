package sacm.design;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.diagram.impl.DNodeImpl;
import org.omg.sacm.argumentation.AssertedRelationship;

/**
 * The services class used by VSM.
 */
public class Services {
	
	public EObject extractSource(EObject self, EObject sourceView) {
		if(sourceView instanceof DNodeImpl) {
			return ((DNodeImpl) sourceView).getTarget();
		}
		return null;
	}
	
	public void removeFromSources(AssertedRelationship self, EObject element) {
		self.getSource().remove(element);
	}

	public IFigure renderTextLabel(EObject self, String text) {
		Label l = new Label(text);
		l.setBounds(new Rectangle(new Point(0, 0), l.getPreferredSize()));
		return (IFigure) l;
	}	
}
