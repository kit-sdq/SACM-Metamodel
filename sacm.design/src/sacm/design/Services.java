package sacm.design;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.ui.dialogs.ResourceDialog;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.sirius.diagram.impl.DNodeImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
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
	
	public EObject extractTarget(EObject self, EObject targetView) {
		if(targetView instanceof DNodeImpl) {
			return ((DNodeImpl) targetView).getTarget();
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
	
	public List<EObject> selectAndQueryInstance(EObject self) {
		final ResourceSetImpl resSet = new ResourceSetImpl();
		//Select Library File
		ResourceDialog fd = new ResourceDialog(Display.getDefault().getActiveShell(),
				"Select a Resource containing the objects to import.", SWT.OPEN);
		if (fd.open() == ResourceDialog.OK) {
			for(URI uri: fd.getURIs()) {
				try {
					resSet.createResource(uri).load(null);
				} catch (Exception e) {
					System.err.println("Could not open selected URIs.");
				}
			}
		}
		Iterable<EObject> iterable = () -> EcoreUtil.getAllContents(resSet, true);
		return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
	}
	
	public List<EObject> preSortCitedElements(EObject self, List<EObject> list) {
		// Sort selected elements with relationships being last
		Collections.sort(list, (e1, e2) -> {
			return 1;
		});
		return list;
	}
	
	
}
