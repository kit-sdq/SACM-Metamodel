/**
 */
package org.omg.sacm.argumentation.provider;


import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.omg.sacm.LangString;
import org.omg.sacm.argumentation.ArgumentPackage;
import org.omg.sacm.argumentation.ArgumentationFactory;
import org.omg.sacm.argumentation.ArgumentationPackage;

/**
 * This is the item provider adapter for a {@link org.omg.sacm.argumentation.ArgumentPackage} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class ArgumentPackageItemProvider extends ArgumentationElementItemProvider {
	/**
	 * This constructs an instance from a factory and a notifier.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ArgumentPackageItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * This returns the property descriptors for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
		if (itemPropertyDescriptors == null) {
			super.getPropertyDescriptors(object);

		}
		return itemPropertyDescriptors;
	}

	/**
	 * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
	 * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
	 * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object) {
		if (childrenFeatures == null) {
			super.getChildrenFeatures(object);
			childrenFeatures.add(ArgumentationPackage.Literals.ARGUMENT_PACKAGE__ARGUMENTATION_ELEMENT);
		}
		return childrenFeatures;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EStructuralFeature getChildFeature(Object object, Object child) {
		// Check the type of the specified child object and return the proper feature to use for
		// adding (see {@link AddCommand}) it as a child.

		return super.getChildFeature(object, child);
	}

	/**
	 * This returns ArgumentPackage.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
		return overlayImage(object, getResourceLocator().getImage("full/obj16/ArgumentPackage"));
	}

	/**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated not
	 */
	@Override
	public String getText(Object object) {
		String label = ((ArgumentPackage)object).getGid();
		LangString name = ((ArgumentPackage)object).getName();
		String compositeLabel = getString("_UI_ArgumentPackage_type");
		if (name != null && name.getContent() != null)
			compositeLabel += " " + name.getContent().trim();
		if (label != null && label.length() != 0)
			compositeLabel += " " + label;
		return  compositeLabel;
	}


	/**
	 * This handles model notifications by calling {@link #updateChildren} to update any cached
	 * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void notifyChanged(Notification notification) {
		updateChildren(notification);

		switch (notification.getFeatureID(ArgumentPackage.class)) {
			case ArgumentationPackage.ARGUMENT_PACKAGE__ARGUMENTATION_ELEMENT:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
				return;
		}
		super.notifyChanged(notification);
	}

	/**
	 * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
	 * that can be created under this object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
		super.collectNewChildDescriptors(newChildDescriptors, object);

		newChildDescriptors.add
			(createChildParameter
				(ArgumentationPackage.Literals.ARGUMENT_PACKAGE__ARGUMENTATION_ELEMENT,
				 ArgumentationFactory.eINSTANCE.createArgumentGroup()));

		newChildDescriptors.add
			(createChildParameter
				(ArgumentationPackage.Literals.ARGUMENT_PACKAGE__ARGUMENTATION_ELEMENT,
				 ArgumentationFactory.eINSTANCE.createArgumentPackage()));

		newChildDescriptors.add
			(createChildParameter
				(ArgumentationPackage.Literals.ARGUMENT_PACKAGE__ARGUMENTATION_ELEMENT,
				 ArgumentationFactory.eINSTANCE.createArgumentPackageBinding()));

		newChildDescriptors.add
			(createChildParameter
				(ArgumentationPackage.Literals.ARGUMENT_PACKAGE__ARGUMENTATION_ELEMENT,
				 ArgumentationFactory.eINSTANCE.createArgumentPackageInterface()));

		newChildDescriptors.add
			(createChildParameter
				(ArgumentationPackage.Literals.ARGUMENT_PACKAGE__ARGUMENTATION_ELEMENT,
				 ArgumentationFactory.eINSTANCE.createArtifactReference()));

		newChildDescriptors.add
			(createChildParameter
				(ArgumentationPackage.Literals.ARGUMENT_PACKAGE__ARGUMENTATION_ELEMENT,
				 ArgumentationFactory.eINSTANCE.createClaim()));

		newChildDescriptors.add
			(createChildParameter
				(ArgumentationPackage.Literals.ARGUMENT_PACKAGE__ARGUMENTATION_ELEMENT,
				 ArgumentationFactory.eINSTANCE.createArgumentReasoning()));

		newChildDescriptors.add
			(createChildParameter
				(ArgumentationPackage.Literals.ARGUMENT_PACKAGE__ARGUMENTATION_ELEMENT,
				 ArgumentationFactory.eINSTANCE.createAssertedInference()));

		newChildDescriptors.add
			(createChildParameter
				(ArgumentationPackage.Literals.ARGUMENT_PACKAGE__ARGUMENTATION_ELEMENT,
				 ArgumentationFactory.eINSTANCE.createAssertedEvidence()));

		newChildDescriptors.add
			(createChildParameter
				(ArgumentationPackage.Literals.ARGUMENT_PACKAGE__ARGUMENTATION_ELEMENT,
				 ArgumentationFactory.eINSTANCE.createAssertedContext()));

		newChildDescriptors.add
			(createChildParameter
				(ArgumentationPackage.Literals.ARGUMENT_PACKAGE__ARGUMENTATION_ELEMENT,
				 ArgumentationFactory.eINSTANCE.createAssertedArtifactSupport()));

		newChildDescriptors.add
			(createChildParameter
				(ArgumentationPackage.Literals.ARGUMENT_PACKAGE__ARGUMENTATION_ELEMENT,
				 ArgumentationFactory.eINSTANCE.createAssertedArtifactContext()));
	}

}
