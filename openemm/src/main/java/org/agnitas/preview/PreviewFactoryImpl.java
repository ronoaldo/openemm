package org.agnitas.preview;

public class PreviewFactoryImpl implements PreviewFactory {

	private static PreviewFactory instance;
	private Preview preview;
	
	private PreviewFactoryImpl() {
		
	}

	public static PreviewFactory createInstance() {
		if ( instance == null ) {
			instance = new PreviewFactoryImpl();
		}
		return instance;
	}
	
	public Preview createPreview() {
		if( preview == null) {
			preview = new PreviewImpl();
		}
		return preview;
	}

}
