package sacm.design.extensions.parts;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

public class CustomRelationshipAnchor extends AbstractConnectionAnchor{
	
	public CustomRelationshipAnchor(final IFigure owner) {
        super(owner);
    }
	
	@Override
	public Point getLocation(Point reference) {
		IFigure owner = getOwner();
		if(owner != null ) {
			Rectangle area = owner.getBounds();
			if(reference.x >= area.right()) {
				return area.getRight();
			} else {
				return area.getLeft();
			}
		}
		else return getReferencePoint();		
	}

}
