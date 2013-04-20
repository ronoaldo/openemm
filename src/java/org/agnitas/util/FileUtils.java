package org.agnitas.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;

/**
 * Utility class dealing with files and streams.
 * 
 * @author md
 */
public class FileUtils {
	
	/** Logger used by this class. */
	private static final transient Logger logger = Logger.getLogger( FileUtils.class);
	
	/**
	 * Exception indicating, that a given ZIP entry was not found in ZIP file.
	 * 
	 * @author md
	 */
	public static class ZipEntryNotFoundException extends Exception {
		/** Name of the ZIP entry. */
		private final String zipEntryName;
		
		/**
		 * Creates a new exception.
		 * 
		 * @param zipEntryName name of the ZIP entry
		 */
		public ZipEntryNotFoundException( String zipEntryName) {
			super( "ZIP entry not found: " + zipEntryName);
			
			this.zipEntryName = zipEntryName;
		}
		
		/**
		 * Returns the name of the ZIP entry.
		 * 
		 * @return name of the ZIP entry
		 */
		public String getZipEntryName() {
			return this.zipEntryName;
		}
	}
	
	/**
	 * Extracts a given entry from a ZIP file to a temporary file.
	 * The temporary file must be handled by the caller. No "deleteOnExit()" is called
	 * to that file.
	 * 
	 * @param zipFile ZIP file to use
	 * @param entryName name of the ZIP entry to extract
	 * @param tempFilePrefix prefix for the temporary file
	 * 
	 * @return the temporary file
	 * 
	 * @throws IOException on errors during extraction of ZIP entry
	 * @throws ZipEntryNotFoundException when the ZIP entry does not exist in the ZIP file
	 */
	public static File extractZipEntryToTemporaryFile( ZipFile zipFile, String entryName, String tempFilePrefix) throws IOException, ZipEntryNotFoundException {
		
		if( logger.isDebugEnabled()) {
			logger.debug( "Transferring data from ZIP entry to temporary file");
		}
		
		File file = File.createTempFile( tempFilePrefix, null);
		extractZipEntryToFile( zipFile, entryName, file);
		
		return file;
	}
	
	/**
	 * Extracts an entry of a given ZIP file to a specific destination file.
	 * 
	 * @param zipFile ZIP file to use
	 * @param entryName name of the ZIP entry
	 * @param destinationFile file to extract to
	 * 
	 * @throws ZipEntryNotFoundException when the ZIP entry was not found in the ZIP file
	 * @throws IOException on errors during extraction
	 */
	public static void extractZipEntryToFile( ZipFile zipFile, String entryName, File destinationFile) throws ZipEntryNotFoundException, IOException {
		
		if( logger.isDebugEnabled()) {
			logger.debug( "Transferring ZIP entry " + entryName + " from ZIP file " + zipFile.getName() + " to " + destinationFile.getAbsolutePath());
		}
		
		ZipEntry zipEntry = zipFile.getEntry( entryName);
		
		if( zipEntry == null) {
			logger.info( "ZIP entry not found: " + entryName);
			
			throw new ZipEntryNotFoundException( entryName);
		}
		
		InputStream inputStream = zipFile.getInputStream( zipEntry);
		
		try {
			streamToFile( inputStream, zipEntry.getSize(), destinationFile);
		} finally {
			inputStream.close();
		}
	}
	
	public static void extractZipEntryToStream(  ZipFile zipFile, String entryName, OutputStream outputStream) throws ZipEntryNotFoundException, IOException {
		
		if( logger.isDebugEnabled()) {
			logger.debug( "Transferring ZIP entry " + entryName + " from ZIP file " + zipFile.getName() + " to stream");
		}
		
		ZipEntry zipEntry = zipFile.getEntry( entryName);
		
		if( zipEntry == null) {
			logger.info( "ZIP entry not found: " + entryName);
			
			throw new ZipEntryNotFoundException( entryName);
		}
		
		InputStream inputStream = zipFile.getInputStream( zipEntry);
		
		try {
			streamToStream( inputStream, zipEntry.getSize(), outputStream);
		} finally {
			inputStream.close();
		}
	}
	
	/**		
	 		byte[] buffer = new byte[16384];
			long remaining = lengthOfData;
			int read;
			while( remaining > 0) {
				read = inputStream.read( buffer);
				remaining -= read;
				outputStream.write( buffer, 0, read);
			}

	 * Writes data from an InputStream to a temporary file. The size of data to
	 * be transferred must be known in order to use this method. The caller must handle
	 * the temporary file. No "deleteOnExit()" is called to that file.
	 * The caller also has to handle the InputStream. It is not closed by this method.
	 * 
	 * @param inputStream InputStream to read from
	 * @param lengthOfData number of bytes to be transferred
	 * @param tempFilePrefix prefix of the temporary file
	 * 
	 * @return the temporary file containing the data
	 * 
	 * @throws IOException on errors reading from the stream
	 */
	public static File streamToTemporaryFile( InputStream inputStream, long lengthOfData, String tempFilePrefix) throws IOException {
		if( logger.isDebugEnabled()) {
			logger.debug( "Transferring data from stream to temporary file");
		}
		
		File file = File.createTempFile( tempFilePrefix, null);
		streamToFile( inputStream, lengthOfData, file);
		
		return file;
	}
	
	/**
	 * Writes data from an InputStream to a temporary file. The size of data to
	 * be transferred must be known in order to use this method. 
	 * The caller has to handle the InputStream. It is not closed by this method.
	 * 
	 * @param inputStream InputStream to read from
	 * @param lengthOfData number of bytes to be transferred
	 * @param file file to write data
	 * 
	 * @throws IOException on errors reading from the stream
	 */
	public static void streamToFile( InputStream inputStream, long lengthOfData, File file) throws IOException {
		
		if( logger.isDebugEnabled()) {
			logger.debug( "Transferring " + lengthOfData + " bytes from stream to file " + file.getAbsolutePath());
		}
		
		FileOutputStream outputStream = new FileOutputStream( file);
		
		try {
			streamToStream( inputStream, lengthOfData, outputStream);
		} finally {
			outputStream.close();
		}
	}
	
	public static void streamToStream( InputStream inputStream, long lengthOfData, OutputStream outputStream) throws IOException {
		byte[] buffer = new byte[16384];		// Data buffer
		long remaining = lengthOfData;			// remaining bytes to read
		int read;								// bytes read in iteration
		
		while( remaining > 0) {
			read = inputStream.read( buffer);
			remaining -= read;
			outputStream.write( buffer, 0, read);
		}
	}
	
	public static void copyFileToDirectory( File sourceFile, File destinationDirectory) throws IOException {
		if( !destinationDirectory.isDirectory()) 
			throw new IllegalArgumentException( "destination is not a directory: " + destinationDirectory.getAbsolutePath());
		
		File destinationFile = new File( destinationDirectory.getCanonicalPath() + File.separator + sourceFile.getName());
		FileInputStream inputStream = new FileInputStream( sourceFile);
		
		try {
			streamToFile( inputStream, sourceFile.length(), destinationFile);
		} catch( IOException e) {
			logger.error( "Error copying file  (" + sourceFile.getAbsolutePath() + " to directory " + destinationDirectory.getAbsolutePath() + ")", e);
			throw e;
		} finally {
			inputStream.close();
		}
	}

	public static void createPathToFile( File file) {
		File parent = file.getParentFile();
		
		if( logger.isDebugEnabled()) {
			logger.debug( "Creating directory structure " + parent.getAbsolutePath() + " for file " + file.getAbsolutePath());
		}
		
		parent.mkdirs();
	}
	
	public static void createPath( String path) {
		File file = new File( path);
		
		if( logger.isDebugEnabled()) {
			logger.debug( "Creating directory structure for " + file.getAbsolutePath());
		}
		
		file.mkdirs();
	}
	
	public static String removeTrailingSeparator( String path) {
		String correctedPath = path;
		
		while( correctedPath.endsWith( File.separator))
			correctedPath = correctedPath.substring( 0, correctedPath.lastIndexOf( File.separator));
		
		if( logger.isDebugEnabled()) {
			logger.debug( "Corrected path " + path + " to " + correctedPath);
		}
		
		return correctedPath;
	}

	public static boolean removeRecursively( String name) {
		File file = new File( name);
		
		return removeRecursively( file);
	}
	
	public static boolean removeRecursively( File file) {
		if( file.isDirectory()) {
			File[] containedFiles = file.listFiles();
			
			for( File containedFile : containedFiles) {
				if( !removeRecursively( containedFile))
					return false;
			}
		}
		
		return file.delete();
	}
}
