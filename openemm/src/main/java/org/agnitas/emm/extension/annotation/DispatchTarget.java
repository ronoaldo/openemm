package org.agnitas.emm.extension.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.agnitas.emm.extension.AnnotatedDispatchingEmmFeatureExtension;

/**
 * Annotation used by AnnotatedDispatchingEmmFeatureExtension to
 * mark methods as targets for the dispatching logic.
 * 
 * An annotated method is invoked, when the value of the request 
 * parameter &quot;method&quot; matches the &quot;attribute&quot; of the
 * annotation.
 * 
 * @author md
 *
 * @see AnnotatedDispatchingEmmFeatureExtension
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DispatchTarget {
	/** Dispatch name. */
	String name();
}
