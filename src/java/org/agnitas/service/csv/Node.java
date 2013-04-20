/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2009 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/

package org.agnitas.service.csv;

/**
 * Node used by <code>BeanBuilder</code> to track the pushing/popping of bean properties.
 * <p>
 * A node is primarily defined by:
 * <ul>
 * <li>context - the target of the node, ie the JavaBean/collection onto which node operations will be applied</li>
 * <li>name - the name of the node, ie the JavaBean property name dictating how node operations will be applied to the
 * <i>parent</i> context</li>
 * </p>
 * <p>
 * Nodes have basic operations performed on them:
 * <ul>
 * <li>A text value can be set - this will typically affect the node's value although for nodes representing beans or
 * collections this will not be the case</li>
 * <li>A value can be retrieved - for JavaBean/collection nodes this will be the target bean or collection, for other nodes
 * this will probably be dependent on the textual value previously set</li>
 * <li>A value can be applied to the context - for JavaBean nodes this will set a property of the JavaBean, for collections
 * this will add a value to that collection</li>
 * </ul>
 * </p>
 * <p>
 * The methods of a node correspond to BeanBuilder events as follows:
 * <ul>
 * <li>push - an appropriate node type is created</li>
 * <li>apply - the text value is set on the node (and an appropriate value created if necessary)</li>
 * <li>pop - the node is popped from the head of the stack and its value is applied to <i>its parent node</i></li>
 * </ul>
 * </p>
 *
 * @author Viktor Gema
 */
public abstract class Node {

    private final Object context;

    private final String name;

    protected Node(Object context, String name) {
        this.context = context;
        this.name = name;
    }

    public Object getContext() {
        return context;
    }

    public String getName() {
        return name;
    }

    /**
     * Sets the value of this node via a text value.
     *
     * @param text The text value used to set this node's value
     * @throws Exception Thrown if the text could not be applied to this node, eg if the text is not in a suitable format
     */
    public abstract void setText(String text) throws Exception;

    /**
     * The value of this node.
     * <p>
     * This may have been set by a call to {@link #setText}.
     * </p>
     *
     * @return Returns the value of this node, may be <code>null</code>
     */
    public abstract Object getValue();

    /**
     * Applies a named value to this node.
     * <p>
     * This represents a child node being popped after it has finished being constructed. For example, a JavaBean might apply a
     * property value like this whereas a Collection might simply add the value, regardless of the name.
     * </p>
     *
     * @param name  The name of the child node that was popped
     * @param value The value of the child node
     * @throws Exception Thrown if the named value can't be applied to this node
     */
    public abstract void apply(String name, Object value) throws Exception;
}
