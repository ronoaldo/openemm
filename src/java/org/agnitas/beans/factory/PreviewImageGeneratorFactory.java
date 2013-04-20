package org.agnitas.beans.factory;

import javax.servlet.http.HttpSession;

import org.agnitas.cms.utils.preview.PreviewImageGenerator;
import org.springframework.context.ApplicationContext;

public interface PreviewImageGeneratorFactory {
	PreviewImageGenerator createPreviewImageGenerator(ApplicationContext applicationContext,
			 HttpSession session, final int previewMaxWidth, final int previewMaxHeight);
}
