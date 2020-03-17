package sacm.design;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
import org.omg.sacm.argumentation.ArgumentAsset;
import org.omg.sacm.argumentation.ArgumentGroup;
import org.omg.sacm.argumentation.ArgumentPackage;
import org.omg.sacm.argumentation.AssertedRelationship;
import org.omg.sacm.argumentation.Assertion;

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
		/* Filter results of EcoreUtil.getAllContents <b>at least for EObjects</b>
		   as not all Contents must be EObjects 	*/
		Iterable<Object> iterable = () -> EcoreUtil.getAllContents(resSet, true);
		return StreamSupport.stream(iterable.spliterator(), false)
				.filter(e -> e instanceof ArgumentAsset  || e instanceof ArgumentGroup)
			    .map (e -> (EObject) e)
				.collect(Collectors.toList());
	}
	
	public void copySelectedElements(EObject self, List<EObject> list) {
		// TODO: recursively flat map ArgumentGroups
		List<EObject> flatList = list.stream()
				.flatMap(x -> (x instanceof ArgumentGroup)?
						((ArgumentGroup)x).getArgumentationElement().stream():Stream.of(x))
				.collect(Collectors.toList());;
		SACMElementCopier cp = new SACMElementCopier(true, false);
		Collection<EObject> copyList = cp.copyAll(flatList);
		cp.copyReferences();
		insertCopiedElements(self, copyList);
	}
	
	public void insertCopiedElements(EObject self, Collection<EObject> copies) {
		if (self instanceof ArgumentPackage) {
			for(EObject e : copies) {
				if(e instanceof Assertion) {
					((ArgumentPackage) self).getArgumentationElement().add((Assertion)e);
				}
			}
		}
	}
	
	
}
