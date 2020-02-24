package sacm.design.extensions.parts;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.XYAnchor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.Request;
import org.eclipse.gmf.runtime.gef.ui.figures.DefaultSizeNodeFigure;
import org.eclipse.gmf.runtime.gef.ui.figures.NodeFigure;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.sirius.diagram.DDiagramElement;
import org.eclipse.sirius.diagram.DNode;
import org.eclipse.sirius.diagram.impl.DNodeImpl;
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


@SuppressWarnings("restriction")
public class CustomRelationshipNode extends DNodeEditPart{
	
	private AssertedRelationship modelInstance = null;

	private NodeFigure nodePlate;

	public CustomRelationshipNode(View view) {
		super(view);
		if (view.getElement() instanceof DNode) {
			DNodeImpl node = (DNodeImpl)view.getElement();
			EObject target = node.getTarget();
			if (target instanceof AssertedRelationship)
				modelInstance = (AssertedRelationship) target;
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
		//final PointList list = createRelationshipShape();
		 
        DBorderedNodeFigure nodeFigure = new DBorderedNodeFigure(createMainFigure()) {
        	//final PointList shape = list;
        	
        	@Override
    	    public void paint(Graphics graphics) {
    			if (getLocalBackgroundColor() != null)
    				graphics.setBackgroundColor(getLocalBackgroundColor());
    			if (getLocalForegroundColor() != null)
    				graphics.setForegroundColor(getLocalForegroundColor());
    			if (font != null)
    				graphics.setFont(font);

    			graphics.pushState();
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
        };
        nodeFigure.getBorderItemContainer().add(new FoldingToggleImageFigure(this));
        nodeFigure.getBorderItemContainer().setClippingStrategy(new FoldingToggleAwareClippingStrategy());
        return nodeFigure;
    }

    protected void paintRotatedRelationship(Graphics graphics, Rectangle area) {
		switch (getAssertionDeclaration()) {
		case ASSERTED:
			paintAsserted(graphics, area);
			break;
		case ASSUMED:
			paintAssumed(graphics, area);
			break;
		case AS_CITED:
			break;
		case AXIOMATIC:
			break;
		case DEFEATED:
			paintDefeated(graphics, area);
			break;
		case NEEDS_SUPPORT:
			break;
		default:
			break;
		}
		
	}

	private void paintDefeated(Graphics graphics, Rectangle area) {
		graphics.pushState();
		graphics.setBackgroundColor(ColorConstants.black);
		graphics.setForegroundColor(ColorConstants.black);
		graphics.setLineWidth(2);
		Point center = area.getCenter();
		int diameter = (int)(Math.min(area.width, area.height));
		double sqrt_diameter = Math.sqrt(Math.PI*diameter);
		
		graphics.translate(center);		
		PolygonDecoration shape = new PolygonDecoration();
		shape.setTemplate(createPointList(
			new PrecisionPoint(-sqrt_diameter, -sqrt_diameter),
			new Point(0,0),
			new PrecisionPoint(+sqrt_diameter, +sqrt_diameter),
			new Point(0,0),
			new PrecisionPoint(+sqrt_diameter, -sqrt_diameter),
			new Point(0,0),
			new PrecisionPoint(-sqrt_diameter, +sqrt_diameter),
			new Point(0,0)
			));
		shape.setScale(1, 1);
		Point p = determineTargetDirection();
		shape.setReferencePoint(new Point(p.y - area.y, p.x - area.x));		
		//shape.setLocation(new Point(area.x, area.y));//;new Point(center.x - diameter/2, center.y - diameter/2));
		shape.paint(graphics);
	}

	private Point determineTargetDirection() {
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

	private void paintAsserted(Graphics graphics, Rectangle area) {
		int diameter = (int)(Math.min(area.width, area.height)/1.5);
		Point center = area.getCenter();
		graphics.fillOval(center.x - diameter/2, center.y - diameter/2, diameter, diameter);
	}
	
	private void paintAssumed(Graphics graphics, Rectangle area) {
		int diameter = (int)(Math.min(area.width, area.height)/1.5);
		Point center = area.getCenter();
		graphics.fillOval(center.x - diameter/2, center.y - diameter/2, diameter, diameter);
	}
	
	private PointList createPointList(Point... points) {
		PointList list = new PointList(points.length);
		for(Point p : points)
			list.addPoint(p);
		return list;
	}

	private PolygonDecoration createRelationshipShape(Rectangle area) {
    	PolygonDecoration shape = new PolygonDecoration();
    	PointList pointList = new PointList(4);
		pointList.addPoint(-1,-1);
		pointList.addPoint(-1,1);
		pointList.addPoint(1,1);
		pointList.addPoint(1,-1);
    	shape.setTemplate(pointList);
    	shape.setScale(4, 4);
    	
    	PolygonDecoration shape2 = new PolygonDecoration();
    	pointList = new PointList(4);
		pointList.addPoint(-2,-2);
		pointList.addPoint(-2,0);
		pointList.addPoint(0,0);
		pointList.addPoint(0,-2);
    	shape2.setTemplate(pointList);
    	shape2.setScale(4, 4);
    	shape.add(shape2);
    	
		return shape;
	}

	@Override
    public ConnectionAnchor getTargetConnectionAnchor(final Request request) {
		return new XYAnchor(this.getBorderedFigure().getBounds().getCenter());
    }
	
	@Override
    public ConnectionAnchor getSourceConnectionAnchor(final Request request) {
		return new XYAnchor(this.getBorderedFigure().getBounds().getCenter());        
    }
}
