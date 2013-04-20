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
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.target.impl;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.ListIterator;

import org.agnitas.target.TargetNode;
import org.agnitas.target.TargetRepresentation;

/**
 *
 * @author  mhe
 */
public class TargetRepresentationImpl implements TargetRepresentation {

    protected ArrayList<TargetNode> allNodes=null;

    private static final long serialVersionUID = -5118626285211811379L;

    /** Creates a new instance of TargetRepresentation */
    public TargetRepresentationImpl() {
        allNodes=new ArrayList<TargetNode>();
    }

    public String generateSQL() {
        boolean isFirst = true;
    	StringBuffer tmpString=new StringBuffer("");
        TargetNode tmpNode=null;
        ListIterator<TargetNode> aIt=allNodes.listIterator();

        while(aIt.hasNext()) {
        	tmpNode=aIt.next();
        	if (isFirst) {
        		tmpNode.setChainOperator(TargetNode.CHAIN_OPERATOR_NONE);
        		isFirst = false;
        	}
            tmpString.append(tmpNode.generateSQL());
        }
         if( "".equals(tmpString.toString())) {
        	return tmpString.toString();
        }
        return "("+tmpString.toString()+")";
    }

    public String generateBsh() {
        StringBuffer tmpString=new StringBuffer("");
        TargetNode tmpNode=null;
        ListIterator<TargetNode> aIt=allNodes.listIterator();
        while(aIt.hasNext()) {
            tmpNode=aIt.next();
            tmpString.append(tmpNode.generateBsh());
        }
        return tmpString.toString();
    }

    public boolean checkBracketBalance() {
        int balance=0;
        TargetNode tmpNode=null;
        ListIterator<TargetNode> aIt=allNodes.listIterator();
        while(aIt.hasNext()) {
            tmpNode=aIt.next();
            if(tmpNode.isOpenBracketBefore()) {
                balance++;
            }
            if(tmpNode.isCloseBracketAfter()) {
                balance--;
            }
            if(balance<0) {
                return false;
            }
        }
        if(balance!=0) {
            return false;
        }
        return true;
    }

    public void addNode(TargetNode aNode) {
        if(aNode!=null) {
            allNodes.add(aNode);
        }
    }

    public void setNode(int idx, TargetNode aNode) {
        if(aNode!=null) {
            allNodes.add(idx, aNode);
        }
    }

    public boolean deleteNode(int index) {
        allNodes.remove(index);
        if(index == 0 && allNodes.size() > 0) {
        	((TargetNode) allNodes.get(index)).setChainOperator(TargetNode.CHAIN_OPERATOR_NONE);
        }
        return true;
    }

    public ArrayList<TargetNode> getAllNodes() {
        return allNodes;
    }

    private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField allFields=null;
        allFields=in.readFields();
        
        this.allNodes=(ArrayList<TargetNode>)allFields.get("allNodes", new ArrayList<TargetNode>());
    }
}
