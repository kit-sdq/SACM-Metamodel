package sacm.design;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.diagram.impl.DNodeImpl;
import org.omg.sacm.argumentation.ArgumentReasoning;
import org.omg.sacm.argumentation.AssertedRelationship;
import org.omg.sacm.argumentation.Assertion;
import org.omg.sacm.argumentation.Claim;

/**
 * The services class used by the Sirius SACM model description.
 * 
 * @author Fabian Scheytt
 */
public class Services {
	
	/**
	 * Extracts the target model EObject from a DNodeImpl sourceView
	 * @param self
	 * @param sourceView
	 * @return
	 */
	public EObject extractSource(EObject self, EObject sourceView) {
		if(sourceView instanceof DNodeImpl) {
			return ((DNodeImpl) sourceView).getTarget();
		}
		return null;
	}
	
	/**
	 * Extracts the target model EObject from a DNodeImpl targetView
	 * @param self
	 * @param sourceView
	 * @return
	 */
	public EObject extractTarget(EObject self, EObject targetView) {
		if(targetView instanceof DNodeImpl) {
			return ((DNodeImpl) targetView).getTarget();
		}
		return null;
	}
	
	/**
	 * Removes a given element from the sources attribute of the AssertedRelationship
	 * @param self
	 * @param element
	 */
	public void removeFromSources(AssertedRelationship self, EObject element) {
		self.getSource().remove(element);
	}
	
	/**
	 * Removes the given element from the metaClaim attribute of the given Assertion object
	 * @param self
	 * @param element
	 */
	public void removeFromMetaClaim(EObject self, EObject element) {
		if (self instanceof Assertion && element instanceof Claim) {
			((Assertion)self).getMetaClaim().remove(element);
		}
	}
	
	/**
	 * Resets the setReasoning attribute of an ArgumentReasoning object
	 * @param self AssertedRelationship element to reset
	 * @param element the ArgumentReasoning object to clear from the attribute
	 */
	public void removeFromReasoning(EObject self, EObject element) {
		if (element instanceof ArgumentReasoning && self instanceof AssertedRelationship)
			((AssertedRelationship)self).setReasoning(null);
	}

	/**
	 * Renders the given text as a label and returns an IFigure element
	 * @param self
	 * @param text
	 * @return created label
	 */
	public IFigure renderTextLabel(EObject self, String text) {
		Label l = new Label(text);
		l.setBounds(new Rectangle(new Point(0, 0), l.getPreferredSize()));
		return (IFigure) l;
	}	

}
