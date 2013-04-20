package org.agnitas.beans.factory.impl;

import javax.servlet.http.HttpSession;

import org.agnitas.beans.factory.PreviewImageGeneratorFactory;
import org.agnitas.cms.utils.preview.PreviewImageGenerator;
import org.springframework.context.ApplicationContext;

public class PreviewImageGeneratorFactoryImpl implements PreviewImageGeneratorFactory {

	@Override
	public PreviewImageGenerator createPreviewImageGenerator(ApplicationContext applicationContext,
			HttpSession session, int previewMaxWidth, int previewMaxHeight) {
		return new PreviewImageGenerator(applicationContext, session, previewMaxWidth, previewMaxHeight);
	}

}
