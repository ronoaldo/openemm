package org.agnitas.target.impl;

import org.agnitas.target.Target;
import org.agnitas.target.TargetFactory;
import org.agnitas.target.TargetRepresentationFactory;

public class TargetFactoryImpl implements TargetFactory {

    protected TargetRepresentationFactory targetRepresentationFactory;

	@Override
	public Target newTarget() {
        TargetImpl target = new TargetImpl();
        target.setTargetStructure(targetRepresentationFactory.newTargetRepresentation());
        return target;
	}

    public void setTargetRepresentationFactory(TargetRepresentationFactory targetRepresentationFactory) {
        this.targetRepresentationFactory = targetRepresentationFactory;
    }
}
