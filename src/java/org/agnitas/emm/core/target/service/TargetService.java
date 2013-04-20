package org.agnitas.emm.core.target.service;

import java.util.Set;

import org.agnitas.beans.Mailing;

public interface TargetService {
	public boolean hasMailingDeletedTargetGroups( Mailing mailing);
    public Set<Integer> getTargetIdsFromExpression(Mailing mailing);
}
