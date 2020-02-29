package sacm.design.extensions.parts;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.omg.sacm.argumentation.AssertedArtifactSupport;
import org.omg.sacm.argumentation.AssertedContext;
import org.omg.sacm.argumentation.AssertedEvidence;
import org.omg.sacm.argumentation.AssertedInference;
import org.omg.sacm.argumentation.AssertedRelationship;
import org.omg.sacm.argumentation.Assertion;
import org.omg.sacm.argumentation.Claim;

import sacm.design.extensions.SacmConstants.ArrowDecoratorType;

import org.eclipse.gmf.runtime.diagram.ui.editpolicies.ConnectionBendpointEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.diagram.ui.internal.editpolicies.ConnectionLineSegEditPolicy;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.sirius.diagram.DEdge;
import org.eclipse.sirius.diagram.DNode;
import org.eclipse.sirius.diagram.EdgeStyle;
import org.eclipse.sirius.diagram.impl.DEdgeImpl;
import org.eclipse.sirius.diagram.ui.edit.api.part.AbstractDiagramEdgeEditPart;
import org.eclipse.sirius.diagram.ui.edit.api.part.IDiagramEdgeEditPart;
import org.eclipse.sirius.diagram.ui.edit.internal.part.DiagramEdgeEditPartOperation;
import org.eclipse.sirius.diagram.ui.graphical.edit.policies.DeleteFromDiagramEditPolicy;
import org.eclipse.sirius.diagram.ui.graphical.edit.policies.HideSiriusElementEditPolicy;
import org.eclipse.sirius.diagram.ui.graphical.edit.policies.SiriusGraphicalNodeEditPolicy;
import org.eclipse.sirius.diagram.ui.graphical.edit.policies.TreeLayoutConnectionLineSegEditPolicy;
import org.eclipse.sirius.diagram.ui.internal.edit.parts.DEdgeBeginNameEditPart;
import org.eclipse.sirius.diagram.ui.internal.edit.parts.DEdgeEndNameEditPart;
import org.eclipse.sirius.diagram.ui.internal.edit.parts.DEdgeNameEditPart;
import org.eclipse.sirius.diagram.ui.internal.edit.policies.DEdgeItemSemanticEditPolicy;
import org.eclipse.sirius.diagram.ui.internal.edit.policies.SiriusConnectionBendpointEditPolicy;
import org.eclipse.sirius.diagram.ui.tools.api.policy.CompoundEditPolicy;
import org.eclipse.sirius.diagram.ui.tools.internal.ui.SiriusSelectConnectionEditPartTracker;
import org.eclipse.sirius.ui.tools.api.color.VisualBindingManager;

public class CustomEdge extends AbstractDiagramEdgeEditPart {
	
	private ArrowDecoratorType sourceDecorationType = ArrowDecoratorType.NONE; 
	private ArrowDecoratorType targetDecorationType = ArrowDecoratorType.NONE; 

    public CustomEdge(View view) {
        super(view);
        if(view.getElement() instanceof DEdge)
        	refreshDecoratorTypes((DEdge) view.getElement());
    }
    
    private void refreshDecoratorTypes(DEdge diagramElement) {
    	DEdgeImpl edge = (DEdgeImpl)diagramElement;
    	EObject sourceNode = edge.getSourceNode();
    	Object source = sourceNode.eGet(sourceNode.eClass().getEStructuralFeature("target"));
    	EObject targetNode = edge.getTargetNode();
    	Object target = targetNode.eGet(targetNode.eClass().getEStructuralFeature("target"));
    	if (source instanceof AssertedRelationship && target instanceof Assertion) {
    		AssertedRelationship rel = (AssertedRelationship)source;
    		Assertion partner = (Assertion) target;
    		if (rel.getTarget() != null && rel.getTarget().getGid().equals(partner.getGid())) {
    			if (rel instanceof AssertedContext || rel instanceof AssertedArtifactSupport) {
    				targetDecorationType = (rel.isIsCounter()) ?
    					ArrowDecoratorType.CONTEXT_OUTLINE :
    					ArrowDecoratorType.CONTEXT_FILLED;
    			}
    			if (rel instanceof AssertedEvidence 
    					|| rel instanceof AssertedInference
    					|| rel instanceof AssertedArtifactSupport) {
    				targetDecorationType = (rel.isIsCounter()) ?
    					ArrowDecoratorType.ARROW_OUTLINE :
    					ArrowDecoratorType.ARROW_FILLED;
    			}
    		}
    	} else if (source instanceof Assertion && target instanceof Claim) {
    		sourceDecorationType = ArrowDecoratorType.META_CLAIM;
    	}
    }
    
    @Override
    public void refreshTargetDecoration() {    	
    	if (!DiagramEdgeEditPartOperation.isSelected(this) && !DiagramEdgeEditPartOperation.isLabelSelected(this)) {
            final EObject diagramElement = this.resolveSemanticElement();            
            if (diagramElement instanceof DEdge) {
                final DEdge edge = (DEdge) diagramElement;
                refreshDecoratorTypes(edge);
                this.getPolylineConnectionFigure().setTargetDecoration(getDecoration(targetDecorationType));
            }
        }
    }
    
    @Override
    public void refreshSourceDecoration() {
    	if (!DiagramEdgeEditPartOperation.isSelected(this) && !DiagramEdgeEditPartOperation.isLabelSelected(this)) {
            final EObject diagramElement = this.resolveSemanticElement();
            if (diagramElement instanceof DEdge) {
                final DEdge edge = (DEdge) diagramElement;
                refreshDecoratorTypes(edge);
                this.getPolylineConnectionFigure().setSourceDecoration(getDecoration(sourceDecorationType));
            }
        }
    }
    
    private PolygonDecoration getDecoration(ArrowDecoratorType type) {
    	switch(type) {
		case ARROW_FILLED:
			return getArrowFillDecoration();
		case ARROW_OUTLINE:
			return getArrowOutlineDecoration();
		case CONTEXT_FILLED:
			return getContextFilledDecoration();
		case CONTEXT_OUTLINE:
			return getContextOutlineDecoration();
		case META_CLAIM:
			return getMetaClaimDecoration();
		case NONE:
		default:
			return null;
    	}
    }
    
    private PolygonDecoration getArrowFillDecoration() {
    	final PolygonDecoration decoration = new PolygonDecoration();
        decoration.setScale(8, 4);
        applyLineWidth(decoration, this);
        return decoration;
    }
    
    private PolygonDecoration getArrowOutlineDecoration() {
    	final PolygonDecoration decoration = getArrowFillDecoration();
        decoration.setBackgroundColor(ColorConstants.white);
        return decoration;
    }
    
    private PolygonDecoration getMetaClaimDecoration() {
    	final PolygonDecoration decoration = new PolygonDecoration();
        final PointList decorationPointList = new PointList();
        decorationPointList.addPoint(-1, 0);
        decorationPointList.addPoint(0, 1);
        decorationPointList.addPoint(-1, 0);
        decorationPointList.addPoint(0, 0);
        decorationPointList.addPoint(-1, 0);
        decorationPointList.addPoint(0, -1);
        decoration.setTemplate(decorationPointList);
        decoration.setScale(16, 8);
        applyLineWidth(decoration, this);
        return decoration;
    }
    
    private PolygonDecoration getContextFilledDecoration() {
    	final PolygonDecoration decoration = new PolygonDecoration();
        final PointList decorationPointList = new PointList();
        decorationPointList.addPoint(-2, 0);
        decorationPointList.addPoint(-2, 1);
        decorationPointList.addPoint(-4, 1);
        decorationPointList.addPoint(-4, -1);
        decorationPointList.addPoint(-2, -1);
        decorationPointList.addPoint(-2, 0);
        decorationPointList.addPoint(0, 0);
        decoration.setTemplate(decorationPointList);
        decoration.setScale(6, 6);
        applyLineWidth(decoration, this);
        return decoration;
    }
    
    private PolygonDecoration getContextOutlineDecoration() {
    	final PolygonDecoration decoration = getContextFilledDecoration();
        decoration.setBackgroundColor(ColorConstants.white);
        return decoration;
    }    
    
    
    /**
     * Apply the line width of the editPart to the decoration.
     * 
     * @param decoration
     *            the decoration
     * @param self
     *            the edge edit part.
     * @return the created decoration.
     */
    private static void applyLineWidth(final Polyline decoration, final IDiagramEdgeEditPart self) {
        final DEdge edge = (DEdge) self.resolveSemanticElement();
        int size = getLineWidth(edge);
        if (size != 0) {
            decoration.setLineWidth(size);
        }
    }
    
    private static int getLineWidth(final DEdge edge) {
        Integer sizeObject = edge.getStyle() != null ? ((EdgeStyle) edge.getStyle()).getSize() : null;
        int size = sizeObject != null ? sizeObject.intValue() : 0;

        // avoid to have only the 1 or 2 pixels shadow lines when the
        // size is 0 or less.
        if (size < 1) {
            size = 1;
        }
        return VisualBindingManager.getDefault().getLineWidthFromValue(size);
    }

    @Override
    protected void createDefaultEditPolicies() {
        super.createDefaultEditPolicies();
        removeEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE);
        removeEditPolicy(EditPolicy.COMPONENT_ROLE);
        CompoundEditPolicy compoundEditPolicy = new CompoundEditPolicy();
        compoundEditPolicy.addEditPolicy(new SiriusGraphicalNodeEditPolicy());
        compoundEditPolicy.addEditPolicy(new HideSiriusElementEditPolicy());
        compoundEditPolicy.addEditPolicy(new DeleteFromDiagramEditPolicy());
        installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, compoundEditPolicy);
        installEditPolicy(EditPolicyRoles.SEMANTIC_ROLE, new DEdgeItemSemanticEditPolicy());
    }

    protected boolean addFixedChild(EditPart childEditPart) {
        if (childEditPart instanceof DEdgeNameEditPart) {
            ((DEdgeNameEditPart) childEditPart).setLabel(getPrimaryShape().getFigureViewEdgeNameFigure());
            return true;
        }
        if (childEditPart instanceof DEdgeEndNameEditPart) {
            ((DEdgeEndNameEditPart) childEditPart).setLabel(getPrimaryShape().getFigureViewEndEdgeNameFigure());
            return true;
        }
        if (childEditPart instanceof DEdgeBeginNameEditPart) {
            ((DEdgeBeginNameEditPart) childEditPart).setLabel(getPrimaryShape().getFigureViewBeginEdgeNameFigure());
            return true;
        }
        return false;
    }

    @Override
    protected void addChildVisual(EditPart childEditPart, int index) {
        if (addFixedChild(childEditPart)) {
            return;
        }
        super.addChildVisual(childEditPart, -1);
    }

    /**
     * Creates figure for this edit part.
     */
    @Override
    protected Connection createConnectionFigure() {
    	ViewEdgeFigure figure =  new ViewEdgeFigure();
    	figure.setTargetDecoration(new PolygonDecoration());
    	return figure;
    }

    public AbstractDiagramEdgeEditPart.ViewEdgeFigure getPrimaryShape() {
        return (AbstractDiagramEdgeEditPart.ViewEdgeFigure) getFigure();
    }

    @Override
    protected void deactivateFigure() {
        // Can happen when semantic element associated to the edge has been
        // deleted
        if (getRoot() == null || figure.getParent() != getLayer(CONNECTION_LAYER)) {
            return;
        }
        super.deactivateFigure();
    }

    /**
     * Override to install the specific connection bendpoints EditPolicy to
     * correctly handle tree router.
     */
    @Override
    public void installEditPolicy(Object key, EditPolicy editPolicy) {
        if (EditPolicy.CONNECTION_BENDPOINTS_ROLE.equals(key)) {
            if (editPolicy instanceof ConnectionLineSegEditPolicy) {
                super.installEditPolicy(key, new TreeLayoutConnectionLineSegEditPolicy());
            } else if (editPolicy != null && editPolicy.getClass().equals(ConnectionBendpointEditPolicy.class)) {
                super.installEditPolicy(key, new SiriusConnectionBendpointEditPolicy());
            } else {
                super.installEditPolicy(key, editPolicy);
            }
        } else {
            super.installEditPolicy(key, editPolicy);
        }
    }

    @Override
    public DragTracker getDragTracker(Request req) {
        return new SiriusSelectConnectionEditPartTracker(this);
    }
    
    class CustomViewEdgeFigure extends ViewEdgeFigure {
    	/*
    	@Override @SuppressWarnings({ "deprecation", "restriction" })
        public void paintFigure(final Graphics graphics) {
            if (!isActive()) {
                return;
            }
            final EObject element = resolveSemanticElement();
            if (element != null && DEdge.class.isInstance(element)) {
                final DEdge viewEdge = (DEdge) element;
                DDiagram diagram = viewEdge.getParentDiagram();
                if ((!viewEdge.isIsMockEdge() && viewEdge.isVisible()) || (diagram != null && diagram.isIsInShowingMode())) {
                    ShowingViewUtil.initGraphicsForVisibleAndInvisibleElements(this, graphics, (View) getModel());
                    try {
                        super.paintFigure(graphics);
                        
                        //graphics.setForegroundColor(ColorConstants{0, 120, 215, 255});
                        graphics.setBackgroundColor(ColorConstants.black);
                        graphics.setLineWidth(5);
                        Point c = bounds.getCenter();
                        graphics.fillOval(c.x - 5,
                        		c.y - 5,
                        		10,
                        		10);
                        graphics.restoreState();
                    } finally {
                        graphics.popState();
                    }
                }
            }
        }
    	*/    	
    }

	public DNode getTargetEditPart() {
		if (getEdge() instanceof Edge && getEdge().getTarget() instanceof Node)
			if(getEdge().getTarget().getElement() instanceof DNode)
				return (DNode) getEdge().getTarget().getElement();
		return null;
	}
}
