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

package org.agnitas.cms.utils.preview;

import javax.swing.text.*;
import javax.swing.text.html.*;
import java.util.*;
import java.util.Map;

/**
 * @author Vyacheslav Stepanov
 */
public class CmsEditorKit extends HTMLEditorKit {

	private final ViewFactory factory = new CmsHTMLFactory();
	private Map<Element, Boolean> loadMap = new HashMap<Element, Boolean>();

	@Override
	public ViewFactory getViewFactory() {
		return factory;
	}

	public void onImagesLoaded() {
	}

	public int getImageCount() {
		return loadMap.size();
	}

	public class CmsHTMLFactory extends HTMLFactory {
		@Override
		public View create(Element elem) {
			View view = super.create(elem);
			final Element element = elem;
			if(view instanceof ImageView) {
				final CmsImageView imageView = new CmsImageView(elem) {
					@Override
					public void onImageLoaded() {
						loadMap.put(element, true);
						if(allImagesLoaded()) {
							onImagesLoaded();
						}
					}
				};
				loadMap.put(element, false);
				return imageView;
			} else {
				return view;
			}
		}

		private boolean allImagesLoaded() {
			for(Boolean imageLoaded : loadMap.values()) {
				if(!imageLoaded) {
					return false;
				}
			}
			return true;
		}
	}
}
