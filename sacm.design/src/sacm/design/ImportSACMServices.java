package sacm.design;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.emf.common.ui.dialogs.ResourceDialog;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.omg.sacm.LangString;
import org.omg.sacm.SacmFactory;
import org.omg.sacm.argumentation.ArgumentAsset;
import org.omg.sacm.argumentation.ArgumentGroup;
import org.omg.sacm.argumentation.ArgumentPackage;
import org.omg.sacm.argumentation.ArgumentPackageBinding;
import org.omg.sacm.argumentation.ArgumentationFactory;
import org.omg.sacm.package_.AssuranceCasePackage;

/**
 * Sirius service class for importing/copying existing SACM instances.
 * @author Fabian Scheytt
 *
 */
public class ImportSACMServices {
	 /**
	  * Shows a dialog for the user to select an Ecore resource file. After selection all contents
	  * of type ArgumentAsset and ArgumentGroup are accumulated into a list.
	  * @param self
	  * @return List of ArgumentAssets, ArgumentGroups in selected resource.
	  */
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
	
	/**
	 * Returns all Contents of the package self is in.
	 * @param self
	 * @return all contents
	 */
	public List<EObject> getAllPackageContents(EObject self) {
		EObject root = EcoreUtil.getRootContainer(self);
		return StreamSupport.stream(((Iterable<EObject>)() ->((EObject)root).eAllContents()).spliterator(), false)
					.collect(Collectors.toList());
	}
	
	/**
	 * Copies all elements of the given list SACM conform to the parent object of self (ArgumentPackage)
	 * @param self
	 * @param list list of elements to copy
	 */
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
	
	/**
	 * Inserts a list of new/copied SACM elements into the self ArgumentPackage and creates a PackageBinding between 
	 * the self ArgumentPackage and each imported package.
	 * @param self
	 * @param copies
	 * @param packages
	 */
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
	
	/**
	 * Creates an ArgumentBindingPackage instance for a list of given packages.
	 * @param packages
	 * @return BindingPackage
	 */
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
