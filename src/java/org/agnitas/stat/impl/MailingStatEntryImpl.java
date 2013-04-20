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

package org.agnitas.stat.impl;

import java.util.Hashtable;

import org.agnitas.stat.MailingStatEntry;

public class MailingStatEntryImpl implements MailingStatEntry {

        private static final long serialVersionUID = 3164284570044619518L;
		protected int opened;
        protected int optouts;
        protected int bounces;
        protected int totalMails;
        protected int totalClicks;        
        protected int totalClicksNetto;
        protected int totalClickSubscribers;
        protected Hashtable clickStatValues;
        
        /**
         * Holds value of property totalDeepClicksNetto.
         */
        protected int totalDeepClicksNetto;
        
        /**
         * Holds value of property targetName.
         */
        protected String targetName;
        
        /**
         * Holds value of property maxblue.
         */
        protected int maxblue;
        
        /**
         * Holds value of property maxNRblue.
         */
        protected int maxNRblue;
        
        public MailingStatEntryImpl() {
            
        }
        
        public int getOpened() {
            return this.opened;
        }
        
        public void setOpened(int opened) {
            this.opened = opened;
        }
        
        public int getOptouts() {
            return this.optouts;
        }
        
        public void setOptouts(int optouts) {
            this.optouts = optouts;
        }
        
        public int getBounces() {
            return this.bounces;
        }
        
        public void setBounces(int bounces) {
            this.bounces = bounces;
        }
        
        public int getTotalMails() {
            return this.totalMails;
        }
        
        public void setTotalMails(int totalMails) {
            this.totalMails = totalMails;
        }
        
        public Hashtable getClickStatValues() {
            return this.clickStatValues;
        }
        
        public void setClickStatValues(Hashtable clickStatValues) {
            this.clickStatValues = clickStatValues;
        }
        
        public int getTotalClicks() {
            return this.totalClicks;
        }
        
        public void setTotalClicks(int totalClicks) {
            this.totalClicks = totalClicks;
        }
        
        public int getTotalClickSubscribers() {
            return this.totalClickSubscribers;
        }
        
        public void setTotalClickSubscribers(int totalClickSubscribers) {
            this.totalClickSubscribers = totalClickSubscribers;
        }
        
        public int getTotalClicksNetto() {
            return this.totalClicksNetto;
        }
        
        public void setTotalClicksNetto(int totalClicksNetto) {
            this.totalClicksNetto = totalClicksNetto;
        }
        
        /**
         * Getter for property targetName.
         * @return Value of property targetName.
         */
        public String getTargetName() {
            return this.targetName;
        }
        
        /**
         * Setter for property targetName.
         * @param targetName New value of property targetName.
         */
        public void setTargetName(String targetName) {
            this.targetName = targetName;
        }
        
        /**
         * Getter for property maxblue.
         * @return Value of property maxblue.
         */
        public int getMaxblue() {
            return this.maxblue;
        }
        
        /**
         * Setter for property maxblue.
         * @param maxblue New value of property maxblue.
         */
        public void setMaxblue(int maxblue) {
            this.maxblue = maxblue;
        }
        
        /**
         * Getter for property maxNRblue.
         * @return Value of property maxNRblue.
         */
        public int getMaxNRblue() {
            return this.maxNRblue;
        }
        
        /**
         * Setter for property maxNRblue.
         * @param maxNRblue New value of property maxNRblue.
         */
        public void setMaxNRblue(int maxNRblue) {
            this.maxNRblue = maxNRblue;
        }
}