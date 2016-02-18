package net.minegage.common.util;


import net.minegage.common.log.L;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class UtilFile {
	
	public static File getServerAssets() {
		String s = File.separator;
		return new File(s + "home" + s + "minecraft");
	}
	
	public static String read(String url, int timeout) {
		HttpURLConnection conn = null;
		BufferedReader reader = null;
		
		try {
			URL urlConn = new URL(url);
			conn = (HttpURLConnection) urlConn.openConnection();
			
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-length", "0");
			
			conn.setUseCaches(false);
			conn.setAllowUserInteraction(false);
			
			conn.setConnectTimeout(timeout);
			conn.setReadTimeout(timeout);
			conn.connect();
			
			int status = conn.getResponseCode();
			if (status == HttpURLConnection.HTTP_ACCEPTED || status == HttpURLConnection.HTTP_CREATED) {
				reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuilder sb = new StringBuilder();
				
				String line;
				while (( line = reader.readLine() ) != null) {
					sb.append(line + "\n");
				}
				
				reader.close();
				return sb.toString();
			}
			
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		
		return null;
	}
	
	public static void downloadToDir(String fileURL, String dirPath) {
		InputStream in = null;
		BufferedInputStream bIn = null;
		
		FileOutputStream out = null;
		BufferedOutputStream bOut = null;
		
		try {
			URLConnection conn = new URL(fileURL).openConnection();
			
			in = conn.getInputStream();
			bIn = new BufferedInputStream(in);
			
			String fileName = fileURL.substring(fileURL.lastIndexOf(File.separator), fileURL.length() - 1);
			out = new FileOutputStream(dirPath + File.separator + fileName);
			bOut = new BufferedOutputStream(out);
			
			byte[] buffer = new byte[8096];
			int count = 0;
			while (( count = bIn.read(buffer, 0, buffer.length) ) != -1) {
				bOut.write(buffer, 0, count);
			}
			
		} catch (IOException ex) {
			L.error(ex, "Unable to download file from \"" + fileURL + "\" to folder \"" + dirPath + "\"");
		} finally {
			if (bIn != null) {
				try {
					bIn.close();
				} catch (IOException ex1) {
					ex1.printStackTrace();
				}
			}
			if (bOut != null) {
				try {
					bOut.close();
				} catch (IOException ex1) {
					ex1.printStackTrace();
				}
			}
		}
	}
	
	private static final int COMPRESSION_LEVEL = ZipOutputStream.DEFLATED;
	private static final int BUFFER_SIZE = 1024 * 2;
	
	@Deprecated
	/**
	 * Zips a file into a directory
	 * 
	 * @param toZipDir
	 *        The directory to be zipped
	 * @param outDir
	 *        The directory which the file should go into
	 * @return The zipped file
	 */
	public static File zipToDir(File toZipDir, File outDir) {
		if (!toZipDir.isDirectory()) {
			throw new IllegalArgumentException("Input directory \"" + toZipDir.getAbsolutePath() + "\" is not a directory");
		}
		
		if (outDir.exists() && !outDir.isDirectory()) {
			throw new IllegalArgumentException("Output directory \"" + outDir.getAbsolutePath() + "\" is not a directory");
		}
		
		outDir.mkdirs();
		File out = new File(outDir, toZipDir.getName() + ".zip");
		
		try {
			out.createNewFile();
		} catch (IOException ex1) {
			ex1.printStackTrace();
			return null;
		}
		
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ZipOutputStream zOut = null;
		
		try {
			fos = new FileOutputStream(out.getAbsolutePath());
			bos = new BufferedOutputStream(fos);
			zOut = new ZipOutputStream(bos);
			zOut.setLevel(COMPRESSION_LEVEL);
			zipEntry(toZipDir, toZipDir.getAbsolutePath(), zOut);
			zOut.finish();
			
			return out;
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if (zOut != null) {
				try {
					zOut.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	private static void zipEntry(File sourceFile, String sourcePath, ZipOutputStream zos) throws IOException {
		if (sourceFile.isDirectory()) {
			if (sourceFile.getName()
					.equalsIgnoreCase(".metadata")) {
				return;
			}
			File[] fileArray = sourceFile.listFiles();
			for (int i = 0; i < fileArray.length; i++) {
				zipEntry(fileArray[i], sourcePath, zos);
			}
		} else {
			BufferedInputStream bis = null;
			try {
				
				String sourceFilePath = sourceFile.getPath();
				
				int left = sourcePath.length() + 1;
				int right = sourceFilePath.length();
				
				if (left > sourceFilePath.length()) {
					left = sourceFilePath.length();
				}
				
				String entryName = sourceFilePath.substring(left, right);
				L.d("source path " + sourceFilePath);
				L.d("entry '" + entryName + "'");
				L.d("left '" + left + "', right '" + right + "'");
				
				bis = new BufferedInputStream(new FileInputStream(sourceFile));
				ZipEntry zentry = new ZipEntry(entryName);
				zentry.setTime(sourceFile.lastModified());
				zos.putNextEntry(zentry);
				
				byte[] buffer = new byte[BUFFER_SIZE];
				int cnt = 0;
				while (( cnt = bis.read(buffer, 0, BUFFER_SIZE) ) != -1) {
					zos.write(buffer, 0, cnt);
				}
				zos.closeEntry();
			} finally {
				if (bis != null) {
					bis.close();
				}
			}
		}
	}
	
	@Deprecated
	/**
	 * Unzips a file into the specified directory
	 * 
	 * @param zipped
	 *        The zipped file or folder
	 * @param outDir
	 *        The directory where the contents of the zipped file should be unzipped into. If the
	 *        zipped file is, for example, a world folder, this should be "../world dir/world name"
	 */
	public static void unzipToDir(File zipped, File outDir) {
		if (outDir.exists() && !outDir.isDirectory()) {
			throw new IllegalArgumentException("Output directory \"" + outDir.getAbsolutePath() + "\" is a file");
		}
		
		outDir.mkdirs();
		
		FileInputStream fis = null;
		ZipInputStream zis = null;
		ZipEntry zentry = null;
		
		try {
			fis = new FileInputStream(zipped);
			zis = new ZipInputStream(fis);
			
			while (( zentry = zis.getNextEntry() ) != null) {
				String fileNameToUnzip = zentry.getName();
				
				File targetFile = new File(outDir, fileNameToUnzip);
				
				if (zentry.isDirectory()) {
					File f = new File(targetFile.getAbsolutePath());
					f.mkdir();
				} else {
					File f = new File(targetFile.getParent());
					f.mkdir();
					unzipEntry(zis, targetFile);
				}
			}
			
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		} finally {
			if (zis != null) {
				try {
					zis.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	private static File unzipEntry(ZipInputStream zis, File targetFile) throws IOException {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(targetFile);
			
			byte[] buffer = new byte[BUFFER_SIZE];
			int length = 0;
			while (( length = zis.read(buffer) ) != -1) {
				fos.write(buffer, 0, length);
			}
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
		return targetFile;
	}
	
	
	
	
}
