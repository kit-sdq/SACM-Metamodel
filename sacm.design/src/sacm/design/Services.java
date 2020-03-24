package sacm.design;

import java.util.Collection;
import java.util.List;
import java.util.Set;
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
import org.omg.sacm.LangString;
import org.omg.sacm.SacmFactory;
import org.omg.sacm.argumentation.ArgumentAsset;
import org.omg.sacm.argumentation.ArgumentGroup;
import org.omg.sacm.argumentation.ArgumentPackage;
import org.omg.sacm.argumentation.ArgumentPackageBinding;
import org.omg.sacm.argumentation.ArgumentReasoning;
import org.omg.sacm.argumentation.ArgumentationFactory;
import org.omg.sacm.argumentation.AssertedRelationship;
import org.omg.sacm.argumentation.Assertion;
import org.omg.sacm.argumentation.Claim;
import org.omg.sacm.package_.AssuranceCasePackage;

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
	
	public void removeFromMetaClaim(EObject self, EObject element) {
		if (self instanceof Assertion && element instanceof Claim) {
			((Assertion)self).getMetaClaim().remove(element);
		}
	}
	
	public void removeFromReasoning(EObject self, EObject element) {
		if (element instanceof ArgumentReasoning && self instanceof AssertedRelationship)
			((AssertedRelationship)self).setReasoning(null);
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
		} else {
			// Throw exception to cancel library selection
			throw new IllegalArgumentException("Select library operation cancelled");
		}
		/* Filter results of EcoreUtil.getAllContents <b>at least for EObjects</b>
		   as not all Contents must be EObjects 	*/
		Iterable<Object> iterable = () -> EcoreUtil.getAllContents(resSet, true);
		return StreamSupport.stream(iterable.spliterator(), false)
				.filter(e -> e instanceof ArgumentAsset  || e instanceof ArgumentGroup)
			    .map (e -> (EObject) e)
				.collect(Collectors.toList());
	}
	
	public List<EObject> getAllPackageContents(EObject self) {
		EObject root = EcoreUtil.getRootContainer(self);
		return StreamSupport.stream(((Iterable<EObject>)() ->((EObject)root).eAllContents()).spliterator(), false)
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
		Set<ArgumentPackage> packages = flatList.stream().filter(e -> e.eContainer() instanceof ArgumentPackage)
				.map(e->(ArgumentPackage)e.eContainer()).collect(Collectors.toSet());
		insertCopiedElements(self, copyList, packages);
	}
	
	public void insertCopiedElements(EObject self, Collection<EObject> copies, Set<ArgumentPackage> packages) {
		if (self instanceof ArgumentPackage) {			
			packages.add((ArgumentPackage)self);
			for(EObject e : copies) {
				if(e instanceof ArgumentAsset) {
					((ArgumentPackage) self).getArgumentationElement().add((ArgumentAsset)e);
				}
			}
			// If the ArgumentPackage and imported Packages lie within a common Package
			// create a common Package Binding if none exists
			if (self.eContainer() instanceof AssuranceCasePackage) {
				AssuranceCasePackage assurancePackage = (AssuranceCasePackage)self.eContainer();
				List<ArgumentPackage> existingBindings = assurancePackage.getArgumentPackage().stream()
						.filter(p -> p instanceof ArgumentPackageBinding)
						.map(p -> (ArgumentPackageBinding)p)
						.filter(p -> packages.containsAll(p.getArgumentationElement()) 
								&& p.getArgumentationElement().containsAll(packages)).collect(Collectors.toList());
				if (existingBindings.size() == 0) {
					ArgumentPackageBinding binding = createBinding(packages);
					assurancePackage.getArgumentPackage().add(binding);
				}				
			} else if(self.eContainer() instanceof ArgumentPackage) {
				ArgumentPackage argPackage = (ArgumentPackage)self.eContainer();
				List<ArgumentPackage> existingBindings = argPackage.getArgumentationElement().stream()
						.filter(p -> p instanceof ArgumentPackageBinding)
						.map(p -> (ArgumentPackageBinding)p)
						.filter(p -> packages.containsAll(p.getArgumentationElement()) 
								&& p.getArgumentationElement().containsAll(packages)).collect(Collectors.toList());
				if (existingBindings.size() == 0) {
					ArgumentPackageBinding binding = createBinding(packages);
					argPackage.getArgumentationElement().add(binding);
				}
			}
		}
	}
	
	private ArgumentPackageBinding createBinding(Collection<ArgumentPackage> packages) {
		ArgumentPackageBinding binding = ArgumentationFactory.eINSTANCE.createArgumentPackageBinding();
		LangString name = SacmFactory.eINSTANCE.createLangString();
		name.setLang("en");
		name.setContent("Binding   ");
		binding.getParticipantPackage().addAll(packages);
		binding.setName(name);
		return binding;
	}
}
