package sacm.design.extensions.parts;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineDecoration;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.draw2d.ui.figures.IOvalAnchorableFigure;
import org.eclipse.gmf.runtime.gef.ui.figures.DefaultSizeNodeFigure;
import org.eclipse.gmf.runtime.gef.ui.figures.NodeFigure;
import org.eclipse.gmf.runtime.gef.ui.figures.SlidableOvalAnchor;
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
		 
		 DBorderedNodeFigure nodeFigure = new OvalBorderedNodeFigure(createMainFigure());
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
			paintAsCited(graphics, area);
			break;
		case AXIOMATIC:
			paintDefeated(graphics, area);
			break;
		case DEFEATED:
			paintDefeated(graphics, area);
			break;
		case NEEDS_SUPPORT:
			paintDefeated(graphics, area);
			break;
		default:
			break;
		}
		
	}

	private void paintDefeated(Graphics graphics, Rectangle area) {
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
		shape.setReferencePoint(new Point(p.x - area.x, p.y - area.y));
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
		
	}
	
	private void paintAsCited(Graphics graphics, Rectangle area) {
		graphics.translate(area.getCenter());
		
		PolylineDecoration[] shape = new PolylineDecoration[3];
		shape[0] = new PolylineDecoration();
		shape[0].setScale(4, 4);
		shape[0].setLineWidth(2);
		shape[0].setTemplate(createPointList(
				new Point(-2, 1),
				new Point(-2, 3),
				new Point(2, 3),
				new Point(2, 1)
				));
		
		shape[1] = new PolylineDecoration();
		shape[1].setScale(4, 4);
		shape[1].setLineWidth(2);
		shape[1].setTemplate(createPointList(
				new Point(-2, 0),
				new Point(2, 0)
				));
		
		shape[2] = new PolylineDecoration();
		shape[2].setScale(4, 4);
		shape[2].setLineWidth(2);
		shape[2].setTemplate(createPointList(
				new Point(-2, -1),
				new Point(-2, -3),
				new Point(2, -3),
				new Point(2, -1)
				));
		
		shape[0].paint(graphics);
		shape[1].paint(graphics);
		shape[2].paint(graphics);
	}
	
	private PointList createPointList(Point... points) {
		PointList list = new PointList(points.length);
		for(Point p : points)
			list.addPoint(p);
		return list;
	}
	
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
		
		@Override
		protected ConnectionAnchor createConnectionAnchor(Point p) {
			Point temp = p.getCopy();
			translateToRelative(temp);
			return new SlidableOvalAnchor(this, SlidableOvalAnchor.getAnchorRelativeLocation(p, bounds));			
		}
		
		@Override
		public ConnectionAnchor getSourceConnectionAnchorAt(Point p) {
			return createConnectionAnchor(p);
		}
		
	}
}
