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

package org.agnitas.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.StringUtils;

public class ZipUtilities {
	/**
	 * Zip a bytearray
	 * 
	 * @param data
	 * @return
	 * @throws IOException 
	 */
	public static byte[] zip(byte[] data, String entryFileName) throws IOException {
		ZipOutputStream zipOutputStream = null;
		
		try {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			zipOutputStream = new ZipOutputStream(new BufferedOutputStream(outStream));
			
			if (data != null) {
				addFileDataToOpenZipFileStream(data, entryFileName, zipOutputStream);
			}

			closeZipOutputStream(zipOutputStream);
			zipOutputStream = null;
			
			outStream.close();
			return outStream.toByteArray();
		}
		catch (IOException e) {
			throw e;
		}
		finally {
			if (zipOutputStream != null) {
				try {
					zipOutputStream.close();
				}
				catch (Exception e) {
				}
				zipOutputStream = null;
			}
		}
	}
	
	/**
	 * Compress a file or recursively compress all files of a folder.
	 * The compressed file will be placed in the same directory as the data.
	 * 
	 * @param sourceFile
	 * @return
	 * @throws IOException
	 */
	public static File zipFile(File sourceFile) throws IOException {
		File zippedFile = new File(sourceFile.getAbsolutePath() + ".zip");
		zipFile(sourceFile, zippedFile);
		return zippedFile;
	}
	
	/**
	 * Compress a file or recursively compress all files of a folder.
	 * 
	 * @param sourceFile
	 * @param destinationZipFile
	 * @throws IOException
	 */
	public static void zipFile(File sourceFile, File destinationZipFile) throws IOException {
		ZipOutputStream zipOutputStream = null;
		
		if (!sourceFile.exists())
			throw new IOException("SourceFile does not exist");
			
		if (destinationZipFile.exists())
			throw new IOException("DestinationFile already exists");
		
		try {
			zipOutputStream = openNewZipOutputStream(destinationZipFile);
			
			addFileToOpenZipFileStream(sourceFile, zipOutputStream);

			closeZipOutputStream(zipOutputStream);
			zipOutputStream = null;
		}
		catch (IOException e) {
			if (destinationZipFile.exists()) {
				if (zipOutputStream != null) {
					try {
						zipOutputStream.close();
					}
					catch (Exception ex) {
					}
					zipOutputStream = null;
				}	
				destinationZipFile.delete();
			}
			throw e;
		}
		finally {
			if (zipOutputStream != null) {
				try {
					zipOutputStream.close();
				}
				catch (Exception e) {
				}
				zipOutputStream = null;
			}
		}
	}
	
	/**
	 * Compress a file or recursively compress all files of a folder.
	 * This starts a new relative path.
	 * 
	 * @param sourceFile
	 * @param destinationZipFileSream
	 * @throws IOException
	 */
	public static void addFileToOpenZipFileStream(File sourceFile, ZipOutputStream destinationZipFileSream) throws IOException {
		addFileToOpenZipFileStream(sourceFile, File.separator, destinationZipFileSream);
	}
	
	/**
	 * Compress a file or recursively compress all files of a folder.
	 * 
	 * @param sourceFile
	 * @param destinationZipFileSream
	 * @throws IOException
	 */
	public static void addFileToOpenZipFileStream(File sourceFile, String relativeDirPath, ZipOutputStream destinationZipFileSream) throws IOException {
		BufferedInputStream bufferedFileInputStream = null;
		
		if (!sourceFile.exists())
			throw new IOException("SourceFile does not exist");
			
		if (destinationZipFileSream == null)
			throw new IOException("DestinationStream is not ready");
		
		if (relativeDirPath == null
				|| (!relativeDirPath.endsWith("/")
				&& !relativeDirPath.endsWith("\\")))
			throw new IOException("RelativeDirPath is invalid");
		
		try {
			if (!sourceFile.isDirectory()) {
				ZipEntry entry = new ZipEntry(relativeDirPath + sourceFile.getName());
				entry.setTime(sourceFile.lastModified());
				destinationZipFileSream.putNextEntry(entry);
				
				bufferedFileInputStream = new BufferedInputStream(new FileInputStream(sourceFile));
				byte[] bufferArray = new byte[1024];
				int byteBufferFillLength = bufferedFileInputStream.read(bufferArray);
				while (byteBufferFillLength > -1) {
					destinationZipFileSream.write(bufferArray, 0, byteBufferFillLength);
					byteBufferFillLength = bufferedFileInputStream.read(bufferArray);
				}
				bufferedFileInputStream.close();
				bufferedFileInputStream = null;
				
				destinationZipFileSream.flush();
				destinationZipFileSream.closeEntry();
			}
			else {	
				for (File sourceSubFile : sourceFile.listFiles()) {
					addFileToOpenZipFileStream(sourceSubFile, relativeDirPath + sourceFile.getName() + File.separator, destinationZipFileSream);
				}
			}
		}
		catch (IOException e) {
			throw e;
		}
		finally {			
			if (bufferedFileInputStream != null) {
				try {
					bufferedFileInputStream.close();
				}
				catch (Exception e) {
				}
				bufferedFileInputStream = null;
			}
		}
	}
	
	/**
	 * Add data to an open ZipOutputStream as a virtual file
	 * 
	 * @param fileData
	 * @param filename
	 * @param destinationZipFileSream
	 * @throws IOException
	 */
	public static void addFileDataToOpenZipFileStream(byte[] fileData, String filename, ZipOutputStream destinationZipFileSream) throws IOException {
		addFileDataToOpenZipFileStream(fileData, File.separator, filename, destinationZipFileSream);
	}
	
	/**
	 * Open new ZipOutputStream based on a file to write into
	 * 
	 * @param destinationZipFile
	 * @return
	 * @throws IOException
	 */
	public static ZipOutputStream openNewZipOutputStream(File destinationZipFile) throws IOException {
		if (destinationZipFile.exists())
			throw new IOException("DestinationFile already exists");
		else if (!destinationZipFile.getParentFile().exists())
			throw new IOException("DestinationDirectory does not exist");
		
		try {
			return new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(destinationZipFile)));
		}
		catch (IOException e) {
			if (destinationZipFile.exists()) {	
				destinationZipFile.delete();
			}
			throw e;
		}
	}
	
	/**
	 * Open new ZipOutputStream based on a OutputStream to write into
	 * 
	 * @param destinationZipStream
	 * @return
	 * @throws IOException
	 */
	public static ZipOutputStream openNewZipOutputStream(OutputStream destinationZipStream) throws IOException {
		if (destinationZipStream == null)
			throw new IOException("DestinationStream is missing");
		
		return new ZipOutputStream(new BufferedOutputStream(destinationZipStream));
	}
	
	/**
	 * Close an open ZipOutputStream
	 * 
	 * @param zipOutputStream
	 * @throws IOException
	 */
	public static void closeZipOutputStream(ZipOutputStream zipOutputStream) throws IOException {
		try {
			zipOutputStream.finish();
			zipOutputStream.flush();
			zipOutputStream.close();
			zipOutputStream = null;
		}
		catch (IOException e) {
			throw e;
		}
		finally {
			if (zipOutputStream != null) {
				try {
					zipOutputStream.close();
				}
				catch (Exception e) {
				}
				zipOutputStream = null;
			}
		}
	}
	
	/**
	 * Close an open ZipOutputStream without errormessages
	 * 
	 * @param zipOutputStream
	 * @throws IOException
	 */
	public static void closeZipOutputStreamQuietly(ZipOutputStream zipOutputStream) {
		try {
			zipOutputStream.finish();
			zipOutputStream.flush();
			zipOutputStream.close();
			zipOutputStream = null;
		}
		catch (IOException e) {
		}
		finally {
			if (zipOutputStream != null) {
				try {
					zipOutputStream.close();
				}
				catch (Exception e) {
				}
				zipOutputStream = null;
			}
		}
	}
	
	/**
	 * Add data to an open ZipOutputStream as a virtual file
	 * 
	 * @param fileData
	 * @param relativeDirPath
	 * @param filename
	 * @param destinationZipFileSream
	 * @throws IOException
	 */
	public static void addFileDataToOpenZipFileStream(byte[] fileData, String relativeDirPath, String filename, ZipOutputStream destinationZipFileSream) throws IOException {
		if (fileData == null)
			throw new IOException("FileData is missing");
		
		if (StringUtils.isEmpty(filename) || filename.trim().length() == 0)
			throw new IOException("Filename is missing");
			
		if (destinationZipFileSream == null)
			throw new IOException("DestinationStream is not ready");
		
		if (relativeDirPath == null
				|| (!relativeDirPath.endsWith("/")
				&& !relativeDirPath.endsWith("\\")))
			throw new IOException("RelativeDirPath is invalid");
		
		ZipEntry entry = new ZipEntry(relativeDirPath + filename);
		entry.setTime(new Date().getTime());
		destinationZipFileSream.putNextEntry(entry);
		
		destinationZipFileSream.write(fileData);
		
		destinationZipFileSream.flush();
		destinationZipFileSream.closeEntry();
	}
	
	/**
	 * Compress a file or recursively compress all files of a folder and add the zpped data to an existing file.
	 * All existing entries in the zipped file will be copied in the new one.
	 * 
	 * @param sourceFile
	 * @return
	 * @throws IOException
	 */
	public static void addFileToExistingzipFile(File sourceFile, File zipFile) throws IOException {
		ZipOutputStream zipOutputStream = openExistingZipFileForExtension(zipFile);
		try {
			addFileToOpenZipFileStream(sourceFile, zipOutputStream);
		}
		finally {
			closeZipOutputStream(zipOutputStream);
		}
	}
	
	/**
	 * Compress a file or recursively compress all files of a folder and add the zpped data to an existing file.
	 * All existing entries in the zipped file will be copied in the new one.
	 * 
	 * @param sourceFile
	 * @return
	 * @throws IOException
	 */
	public static void addFileToExistingzipFile(List<File> sourceFiles, File zipFile) throws IOException {
		ZipOutputStream zipOutputStream = openExistingZipFileForExtension(zipFile);
		try {
			for (File file : sourceFiles) {
				addFileToOpenZipFileStream(file, zipOutputStream);
			}
		}
		finally {
			closeZipOutputStream(zipOutputStream);
		}
	}

	/**
	 * Open an existing Zip file for adding new entries or create a new Zip file if it does not exist yet.
	 * @param file
	 * @return
	 * @throws SystemException
	 */
	public static ZipOutputStream openExistingZipFileForExtensionOrCreateNewZipFile(File zipFile) throws IOException {
		if (zipFile.exists())
			return ZipUtilities.openExistingZipFileForExtension(zipFile);
		else
			return ZipUtilities.openNewZipOutputStream(zipFile);
	}
	
	/**
	 * Open an existing Zip file for adding new entries.
	 * All existing entries in the zipped file will be copied in the new one.
	 * 
	 * @param zipFile
	 * @return
	 * @throws IOException 
	 * @throws ZipException 
	 */
	public static ZipOutputStream openExistingZipFileForExtension(File zipFile) throws IOException {
		// Rename source Zip file (Attention: the String path and name of the zipFile are preserved
		File originalFileTemp = new File(zipFile.getParentFile().getAbsolutePath() + "/" + String.valueOf(System.currentTimeMillis()));
		zipFile.renameTo(originalFileTemp);
		
		ZipFile sourceZipFile = new ZipFile(originalFileTemp);
		ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
		
		BufferedInputStream bufferedInputStream = null;
		
		try {
			// copy entries
			Enumeration<? extends ZipEntry> srcEntries = sourceZipFile.entries();
			while (srcEntries.hasMoreElements()) {
				ZipEntry sourceZipFileEntry = srcEntries.nextElement();
				zipOutputStream.putNextEntry(sourceZipFileEntry);
			
				bufferedInputStream = new BufferedInputStream(sourceZipFile.getInputStream(sourceZipFileEntry));
			
				byte[] bufferArray = new byte[1024];
				int byteBufferFillLength = bufferedInputStream.read(bufferArray);
				while (byteBufferFillLength > -1) {
					zipOutputStream.write(bufferArray, 0, byteBufferFillLength);
					byteBufferFillLength = bufferedInputStream.read(bufferArray);
				}
				
				zipOutputStream.closeEntry();
			
				bufferedInputStream.close();
				bufferedInputStream = null;
			}
			
			zipOutputStream.flush();
			sourceZipFile.close();
			originalFileTemp.delete();
			
			return zipOutputStream;
		}
		catch (IOException e) {
			// delete existing Zip file
			if (zipFile.exists()) {
				if (zipOutputStream != null) {
					try {
						zipOutputStream.close();
					}
					catch (Exception ex) {
					}
					zipOutputStream = null;
				}	
				zipFile.delete();
			}
			
			// revert renaming of source Zip file
			originalFileTemp.renameTo(zipFile);
			throw e;
		}
		finally {
			if (bufferedInputStream != null) {
				try {
					bufferedInputStream.close();
				}
				catch (Exception e) {
				}
				bufferedInputStream = null;
			}
		}
	}
	
	/**
	 * Readou of a Zip file
	 * @param zipFile
	 * @return all file entries
	 * @throws IOException
	 */
	public static Map<String, byte[]> readExistingZipFile(File zipFile) throws IOException {
		Map<String, byte[]> returnMap = new HashMap<String, byte[]>();
		
		ZipFile sourceZipFile = null;
		BufferedInputStream bufferedInputStream = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		
		try {
			sourceZipFile = new ZipFile(zipFile);
			
			// readout of all entries
			Enumeration<? extends ZipEntry> srcEntries = sourceZipFile.entries();
			while (srcEntries.hasMoreElements()) {
				ZipEntry sourceZipFileEntry = srcEntries.nextElement();
			
				bufferedInputStream = new BufferedInputStream(sourceZipFile.getInputStream(sourceZipFileEntry));
				byteArrayOutputStream = new ByteArrayOutputStream();
			
				byte[] bufferArray = new byte[1024];
				int byteBufferFillLength = bufferedInputStream.read(bufferArray);
				while (byteBufferFillLength > -1) {
					byteArrayOutputStream.write(bufferArray, 0, byteBufferFillLength);
					byteBufferFillLength = bufferedInputStream.read(bufferArray);
				}
				
				returnMap.put(sourceZipFileEntry.getName(), byteArrayOutputStream.toByteArray());
			
				byteArrayOutputStream.close();
				byteArrayOutputStream = null;
				
				bufferedInputStream.close();
				bufferedInputStream = null;
			}
			
			sourceZipFile.close();
			
			return returnMap;
		}
		finally {
			if (bufferedInputStream != null) {
				try {
					bufferedInputStream.close();
				}
				catch (Exception e) {
				}
				bufferedInputStream = null;
			}
			
			if (byteArrayOutputStream != null) {
				try {
					byteArrayOutputStream.close();
				}
				catch (Exception e) {
				}
				byteArrayOutputStream = null;
			}
			
			if (sourceZipFile != null) {
				try {
					sourceZipFile.close();
				}
				catch (Exception e) {
				}
				sourceZipFile = null;
			}
		}
	}
}
