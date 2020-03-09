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

import sacm.design.extensions.parts.shapes.RelationshipShape;
import sacm.design.extensions.parts.shapes.RelationshipShapeFactory;


@SuppressWarnings("restriction")
public class CustomRelationshipNode extends DNodeEditPart{
	
	private AssertedRelationship modelInstance = null;

	private NodeFigure nodePlate;

	public CustomRelationshipNode(View view) {
		super(view);
		if (view.getElement() instanceof DNode) {
			DNodeImpl node = (DNodeImpl)view.getElement();
			EObject target = node.getTarget();
			if (target instanceof AssertedRelationship) {
				modelInstance = (AssertedRelationship) target;				
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
	protected NodeFigure createNodePlate() {
        DefaultSizeNodeFigure result = new AirDefaultSizeNodeFigure(10, 10, null);
        final EObject eObj = resolveSemanticElement();
        if (eObj instanceof DStylizable && eObj instanceof DDiagramElement) {
            final DStylizable viewNode = (DStylizable) eObj;
            final StyleConfiguration styleConfiguration = IStyleConfigurationRegistry.INSTANCE.getStyleConfiguration(((DDiagramElement) eObj).getDiagramElementMapping(), viewNode.getStyle());
            final AnchorProvider anchorProvider = styleConfiguration.getAnchorProvider();
            result = new AirDefaultSizeNodeFigure(getMapMode().DPtoLP(5), getMapMode().DPtoLP(5), anchorProvider);
            nodePlate = result;
        }
        return result;
    }
	
	@Override
	public IFigure getNodePlate() {
        return nodePlate;
    }
	
	@Override
    public void activate() {
        if (nodePlate instanceof AirDefaultSizeNodeFigure)
            ((AirDefaultSizeNodeFigure) nodePlate).setZoomManager(getZoomManager());
        super.activate();
    }
	
	@Override
    public void deactivate() {
        super.deactivate();
        if (nodePlate instanceof AirDefaultSizeNodeFigure)
            ((AirDefaultSizeNodeFigure) nodePlate).setZoomManager(null);
    }
	
	 @Override
    protected NodeFigure createNodeFigure() {		 
		DBorderedNodeFigure nodeFigure = new OvalBorderedNodeFigure(createMainFigure());
        nodeFigure.getBorderItemContainer().add(new FoldingToggleImageFigure(this));
        nodeFigure.getBorderItemContainer().setClippingStrategy(new FoldingToggleAwareClippingStrategy());
        return nodeFigure;
    }

    protected void paintRotatedRelationship(Graphics graphics, Rectangle area) {
    	RelationshipShape s = RelationshipShapeFactory.INSTANCE.getRelationshipShape(getAssertionDeclaration());
    	s.paint(graphics, area, determineTargetDirection());		
	}

	public Point determineTargetDirection() {
		if(sourceConnections != null) {
			for (Object o : sourceConnections) {
				if (o instanceof CustomEdge) {
					CustomEdge edge = (CustomEdge) o;
					if (edge.getTargetEditPart().getTarget().equals(modelInstance.getTarget())) {
						return edge.getConnectionFigure().getPoints().getPoint(1);
					}
				}
			}
		}
		return new Point(0,0);
	}
	
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connEditPart) {
		return new CustomRelationshipAnchor(getNodeFigure(), true, this);
	}
	
	@Override
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connEditPart) {
		return new CustomRelationshipAnchor(getNodeFigure(), false, this);
	}
	
	@Override
	public ConnectionAnchor getTargetConnectionAnchor(org.eclipse.gef.Request request) {
		return new CustomRelationshipAnchor(getNodeFigure(), false, this);
	};
	
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(org.eclipse.gef.Request request) {
		return new CustomRelationshipAnchor(getNodeFigure(), true, this);
	};
	
	class OvalBorderedNodeFigure extends DBorderedNodeFigure implements IOvalAnchorableFigure {

		public OvalBorderedNodeFigure(IFigure mainFigure) {
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
				//paintFigure(graphics);
				graphics.restoreState();    				
				//paintClientArea(graphics);
				//paintBorder(graphics);
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
