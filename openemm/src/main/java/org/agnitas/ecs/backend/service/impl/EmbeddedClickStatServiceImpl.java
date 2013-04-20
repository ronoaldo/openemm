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

package org.agnitas.ecs.backend.service.impl;

import org.agnitas.ecs.backend.beans.ClickStatColor;
import org.agnitas.ecs.backend.beans.ClickStatInfo;
import org.agnitas.ecs.backend.dao.EmbeddedClickStatDao;
import org.agnitas.ecs.backend.service.EmbeddedClickStatService;
import org.agnitas.preview.Preview;
import org.agnitas.preview.PreviewFactory;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Implementation of {@link EmbeddedClickStatService} interface
 *
 * @author Vyacheslav Stepanov
 */
public class EmbeddedClickStatServiceImpl implements EmbeddedClickStatService {

	private EmbeddedClickStatDao ecsDao;
	protected PreviewFactory previewFactory;

	public String getMailingContent(int mailingId, int recipientId) {
		Preview preview =  previewFactory.createPreview();
		String output = preview.makePreviewForHeatmap(mailingId, recipientId);
		String content = output == null ? "" : output;
		preview.done();
		return content;
	}

	public String addStatsInfo(String content, int mode, int mailingId, int companyId) {
		String finalHtml = content;
		// get click statistics and color values for stat-labels
		Collection<ClickStatColor> rangeColors = ecsDao.getClickStatColors(companyId);
		ClickStatInfo clickStatInfo = ecsDao.getClickStatInfo(companyId, mailingId, mode);
		// create hidden elements containing clicks stats - to be used by javascript to
		// create clicks stat labels above the links
		if(clickStatInfo != null) {
			for(Integer urlId : clickStatInfo.getClicks().keySet()) {
				int clicks = clickStatInfo.getClicks().get(urlId);
				double percent = clickStatInfo.getPercentClicks().get(urlId);
				String color = getColorForPercent(percent, rangeColors);
				String statInfo = createClickStatInfo(urlId, clicks, percent, color);
				finalHtml = finalHtml + statInfo;
			}
		}
		finalHtml = finalHtml + createNullColorInfoElement(rangeColors);
		return finalHtml;
	}

	/**
	 * Method generates hidden field that carries link click statistics information.
	 * Such hidden fields are used for creating click-stat-labels by javascript
	 * of ecs-page (statLabelAdjuster.js)
	 * ("id" carries urld id; "value" carries click number + percent value;
	 * "name" carries color value)
	 *
	 * @param urlId   link url id
	 * @param clicks  number of clicks
	 * @param percent percent value of clicks
	 * @param color   color that will be used for click-stat-label
	 * @return hidden field in a form of String
	 */
	private String createClickStatInfo(int urlId, int clicks, double percent, String color) {
		// format percent value to be in a form XX.XX
		BigDecimal bigDecimal = new BigDecimal(percent);
		double percentFormatted = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		// in links URLs the coded URL id is used that's why we need to code it
		// here also to be able to get statistics information from hidden input
		// for the link url
		String codedUrlId = Long.toString(urlId, 36);
		// create hidden input element that carries information about link clicking statistics
		return "<input id='info-" + codedUrlId + "' type='hidden' value='" + clicks +
				" (" + percentFormatted + "%)' name='#" + color + "'/>";
	}

	/**
	 * Method constructs hidden input element that carries information
	 * about color for 0% percentage values (used by javascript to set color
	 * for link-labels that have 0 clicks done)
	 *
	 * @param clickStatColors color values for percentage values
	 * @return String containing HTML of hidden input
	 */
	private String createNullColorInfoElement(Collection<ClickStatColor> clickStatColors) {
		String nullColor = getColorForPercent(0, clickStatColors);
		return "<input id='info-null-color' type='hidden' value='#" + nullColor + "'>";
	}

	/**
	 * Method finds color for percentage value
	 *
	 * @param percent	 percentage value
	 * @param rangeColors collection of color values for differnet percentages
	 * @return color in a HEX string if rangeColors has color for this percent,
	 *         DEFAULT_STAT_LINK_COLOR in other case
	 */
	private String getColorForPercent(double percent, Collection<ClickStatColor> rangeColors) {
		String color = DEFAULT_STAT_LABEL_COLOR;
		if(rangeColors == null || rangeColors.isEmpty()) {
			return DEFAULT_STAT_LABEL_COLOR;
		}
		for(ClickStatColor rangeColor : rangeColors) {
			if(percent >= rangeColor.getRangeStart() && percent <= rangeColor.getRangeEnd()) {
				color = rangeColor.getColor();
				if(rangeColor.getRangeStart() == percent) {
					break;
				}
			}
		}
		return color;
	}

	public void setEmbeddedClickStatDao(EmbeddedClickStatDao ecsDao) {
		this.ecsDao = ecsDao;
	}

	public void setPreviewFactory(PreviewFactory previewFactory) {
		this.previewFactory = previewFactory;
	}
}
