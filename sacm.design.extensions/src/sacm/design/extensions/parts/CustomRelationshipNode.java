package sacm.design.extensions.parts;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gmf.runtime.draw2d.ui.figures.IOvalAnchorableFigure;
import org.eclipse.gmf.runtime.gef.ui.figures.DefaultSizeNodeFigure;
import org.eclipse.gmf.runtime.gef.ui.figures.NodeFigure;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.sirius.diagram.DDiagramElement;
import org.eclipse.sirius.diagram.DNode;
import org.eclipse.sirius.diagram.impl.DNodeImpl;
import org.eclipse.sirius.diagram.ui.business.internal.view.ShowingViewUtil;
import org.eclipse.sirius.diagram.ui.internal.edit.parts.DNodeEditPart;
import org.eclipse.sirius.diagram.ui.tools.api.figure.FoldingToggleAwareClippingStrategy;
import org.eclipse.sirius.diagram.ui.tools.api.figure.FoldingToggleImageFigure;
import org.eclipse.sirius.diagram.ui.tools.api.graphical.edit.styles.IStyleConfigurationRegistry;
import org.eclipse.sirius.diagram.ui.tools.api.graphical.edit.styles.StyleConfiguration;
import org.eclipse.sirius.ext.gmf.runtime.gef.ui.figures.AirDefaultSizeNodeFigure;
import org.eclipse.sirius.ext.gmf.runtime.gef.ui.figures.DBorderedNodeFigure;
import org.eclipse.sirius.ext.gmf.runtime.gef.ui.figures.util.AnchorProvider;
import org.eclipse.sirius.viewpoint.DStylizable;
import org.omg.sacm.argumentation.AssertedRelationship;
import org.omg.sacm.argumentation.Assertion;
import org.omg.sacm.argumentation.AssertionDeclaration;

import sacm.design.extensions.SacmConstants.SACMEdgeType;
import sacm.design.extensions.parts.shapes.RelationshipShape;
import sacm.design.extensions.parts.shapes.RelationshipShapeFactory;

/**
 * Custom Sirius node implementation for the SACM Metamodel Editor, allowing a custom realization of  different <b>rotatable</b> node shapes
 * for the AssertedRelationship SACM type.
 * 
 * @author Fabian Scheytt
 *
 */
@SuppressWarnings("restriction")
public class CustomRelationshipNode extends DNodeEditPart{
	
	private AssertedRelationship modelInstance = null;

	public CustomRelationshipNode(View view) {
		super(view);
		// Determine the AssertedRelationship model instance set upon creation.
		if (view.getElement() instanceof DNode) {
			DNodeImpl node = (DNodeImpl)view.getElement();
			EObject target = node.getTarget();
			if (target instanceof AssertedRelationship) {
				modelInstance = (AssertedRelationship) target;
				// Setup node refresh upon assertionDeclaration change notification
				modelInstance.eAdapters().add(new AdapterImpl() {
					@Override
					public void notifyChanged(org.eclipse.emf.common.notify.Notification msg) {
						super.notifyChanged(msg);
						System.out.println(msg);
						if(msg.getFeature() instanceof EAttribute &&
							((EAttribute)msg.getFeature()).getName().equals("assertionDeclaration")) {
							refresh();
						}
					}
				});
			}
		}
	}
	
	/**
	 * Determine AssertionDeclaration of the respective AssertedRelationship of this node
	 * @return current state of thed AssertionDeclaration
	 */
	AssertionDeclaration getAssertionDeclaration() {
		if(getModel() instanceof View) {
			View model = (View) getModel();
			if (model.getElement() instanceof DNode) {
				DNodeImpl node = (DNodeImpl)model.getElement();
				EObject target = node.getTarget();
				if (target instanceof Assertion)
					return ((Assertion) target).getAssertionDeclaration();
			}
		}
		return AssertionDeclaration.ASSERTED;
	}
	
	 @Override
    protected NodeFigure createNodeFigure() {		 
		DBorderedNodeFigure nodeFigure = new SACMBorderedNodeFigure(createMainFigure());
        nodeFigure.getBorderItemContainer().add(new FoldingToggleImageFigure(this));
        nodeFigure.getBorderItemContainer().setClippingStrategy(new FoldingToggleAwareClippingStrategy());
        return nodeFigure;
    }

	/**
	 * Paint relationship node based on current AssertionDeclaration selection
	 * 
	 * @param graphics eclipse draw2D api
	 * @param area client area to draw on
	 */
    protected void paintRotatedRelationship(Graphics graphics, Rectangle area) {
    	RelationshipShape s = RelationshipShapeFactory.INSTANCE.getRelationshipShape(getAssertionDeclaration());
    	s.paint(graphics, area, determineTargetDirection());		
	}

    /**
     * Determines the direction of the edge from this node to the AssertedRelationship.target node.
     * @return target direction or in case of no target: direction = right
     */
	public Point determineTargetDirection() {
		if(sourceConnections != null) {
			for (Object o : sourceConnections) {
				if (o instanceof CustomSACMEdge) {
					CustomSACMEdge edge = (CustomSACMEdge) o;
					if (edge.getTargetEditPart().getTarget().equals(modelInstance.getTarget())) {
						return edge.getConnectionFigure().getPoints().getPoint(1);
					}
				}
			}
		}
		return new Point(0,1);
	}
	
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connEditPart) {
		// Connect custom SACM edges regarding to their type and all other edges as SACM source edges.
		if(connEditPart instanceof CustomSACMEdge)
			return new CustomRelationshipAnchor(getNodeFigure(), ((CustomSACMEdge) connEditPart).getSacmEdgeType(), this);
		else 
			return new CustomRelationshipAnchor(getNodeFigure(), SACMEdgeType.SOURCE_RELATIONSHIP, this);
	}
	
	@Override
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connEditPart) {
		// Connect all edges leading <b>to</b> the node always to the SACM source anchor.
		return new CustomRelationshipAnchor(getNodeFigure(), SACMEdgeType.SOURCE_RELATIONSHIP, this);
	}
	
	@Override
	public ConnectionAnchor getTargetConnectionAnchor(org.eclipse.gef.Request request) {
		// Only the request for connecting a edge, not the final connection anchor.
		return new CustomRelationshipAnchor(getNodeFigure(), SACMEdgeType.SOURCE_RELATIONSHIP, this);
	};
	
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(org.eclipse.gef.Request request) {
		// Only the request for connecting a edge, not the final connection anchor.
		return new CustomRelationshipAnchor(getNodeFigure(), SACMEdgeType.ARGUMENT_REASONING, this);
	};
	
	/**
	 * Custom NodeFigure for SACM AssertedRelationships. This implementation provides a custom paint
	 * routine that ignores the draw duties specified in the Sirius model but instead draws the custom
	 * SACM relationship nodes in the given client area of the node.
	 * 
	 * @author Fabian Scheytt
	 *
	 */
	class SACMBorderedNodeFigure extends DBorderedNodeFigure implements IOvalAnchorableFigure {

		public SACMBorderedNodeFigure(IFigure mainFigure) {
			super(mainFigure);
		}

		@Override
		public Rectangle getOvalBounds() {
			return getBounds();
		}
		
		@Override
	    public void paint(Graphics graphics) {
    		ShowingViewUtil.initGraphicsForVisibleAndInvisibleElements(this, graphics, (View) getModel());
			if (getLocalBackgroundColor() != null)
				graphics.setBackgroundColor(getLocalBackgroundColor());
			if (getLocalForegroundColor() != null)
				graphics.setForegroundColor(getLocalForegroundColor());
			if (font != null)
				graphics.setFont(font);

			
			try {
				paintRotatedClientArea(graphics);
				graphics.restoreState();
			} finally {
				graphics.popState();
			}
	    }

		private void paintRotatedClientArea(Graphics graphics) {
			graphics.setBackgroundColor(ColorConstants.black);
			graphics.setForegroundColor(ColorConstants.black);
			Rectangle area = getClientArea();
			paintRotatedRelationship(graphics, area);
		}
		
	}
}
