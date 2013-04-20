package org.agnitas.emm.springws.security;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.springframework.ws.soap.security.xwss.callback.DefaultTimestampValidator;

import com.sun.xml.wss.impl.callback.TimestampValidationCallback;

public class SpringDigestPasswordValidationCallbackHandler extends org.springframework.ws.soap.security.xwss.callback.SpringDigestPasswordValidationCallbackHandler {
	/**
	 * org.springframework.ws.soap.security.xwss.callback.SpringDigestPasswordValidationCallbackHandler missed return fix
	 */
	@Override
	protected void handleInternal(Callback callback) throws IOException,
			UnsupportedCallbackException {
        if (callback instanceof TimestampValidationCallback) {
            TimestampValidationCallback timestampCallback = (TimestampValidationCallback) callback;
            timestampCallback.setValidator(new DefaultTimestampValidator());
            return;
        }
        super.handleInternal(callback);
	}

}
