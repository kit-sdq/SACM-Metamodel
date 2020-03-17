package sacm.design;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.omg.sacm.SACMElement;
import org.omg.sacm.SacmPackage;

public class SACMElementCopier extends Copier {

	private static final long serialVersionUID = -8257392934910701678L;

	public SACMElementCopier(boolean resolveProxies, boolean useOriginalReferences) {
		super(resolveProxies, useOriginalReferences);
	}
	
	@Override
	public EObject copy(EObject eObject) {
		// Copy eObjects regularly 
		EObject copyEObject = super.copy(eObject);
		// Modify isCitation and set citedElement
		if(copyEObject instanceof SACMElement) {
			((SACMElement)copyEObject).setIsCitation(true);
			((SACMElement)copyEObject).setCitedElement((SACMElement)eObject);
		}
		return copyEObject;
	}

	@Override
	protected void copyAttributeValue(EAttribute eAttribute, EObject eObject, Object value, Setting setting) {
		// Generate own unique UUID for copied SACM element
		if (eAttribute == SacmPackage.Literals.SACM_ELEMENT__GID) {
			value = EcoreUtil.generateUUID();
		}
		super.copyAttributeValue(eAttribute, eObject, value, setting);
	}
}
