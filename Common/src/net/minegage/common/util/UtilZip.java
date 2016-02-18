package net.minegage.common.util;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class UtilZip {
	
	private static final int BUFFER = 2048;
	private static final int COMPRESSION = ZipEntry.DEFLATED;
	
	private static ZipOutputStream zipOut;
	
	public static void compress(File zipFile, File... compress) throws IOException {
		FileOutputStream fileOut = null;
		
		try {
			zipFile.getParentFile()
					.mkdirs();
			zipFile.createNewFile();
			
			fileOut = new FileOutputStream(zipFile);
			
			zipOut = new ZipOutputStream(fileOut);
			zipOut.setLevel(COMPRESSION);
			
			for (File file : compress) {
				if (file.isDirectory()) {
					writeDirToZip(file, file.getName() + "/");
				} else {
					writeFileToZip(file, "");
				}
			}
			
			zipOut.close();
		} finally {
			if (zipOut != null) {
				zipOut.close();
			}
			if (fileOut != null) {
				fileOut.close();
			}
		}
	}
	
	public static void extract(File zipped, File outDir) throws IOException {
		outDir.mkdirs();
		
		FileInputStream fileIn = null;
		ZipInputStream zipIn = null;
		BufferedOutputStream buffOut = null;
		FileOutputStream fileOut = null;
		
		try {
			fileIn = new FileInputStream(zipped);
			zipIn = new ZipInputStream(fileIn);
			
			byte data[] = new byte[BUFFER];
			ZipEntry ze = null;
			while (( ze = zipIn.getNextEntry() ) != null) {
				
				if (ze.isDirectory()) {
					File f = new File(outDir, ze.getName());
					f.mkdir();
					zipIn.closeEntry();
					continue;
				}
				File file = new File(outDir, ze.getName());
				file.getParentFile()
						.mkdirs();
				fileOut = new FileOutputStream(file);
				
				int count;
				buffOut = new BufferedOutputStream(fileOut, BUFFER);
				while (( count = zipIn.read(data, 0, BUFFER) ) != -1) {
					buffOut.write(data, 0, count);
				}
				buffOut.flush();
				buffOut.close();
			}
			
			zipIn.close();
		} finally {
			if (fileOut != null) {
				fileOut.close();
			}
			if (buffOut != null) {
				buffOut.close();
			}
			if (zipIn != null) {
				zipIn.close();
			}
			if (fileIn != null) {
				fileIn.close();
			}
		}
	}
	
	private static void writeDirToZip(File dir, String zipEntryPath) throws IOException {
		String[] dirList = dir.list();
		
		for (int i = 0; i < dirList.length; i++) {
			File f = new File(dir, dirList[i]);
			if (f.isDirectory()) {
				writeDirToZip(f, zipEntryPath + f.getName() + "/");
				continue;
			}
			writeFileToZip(f, zipEntryPath);
			
		}
	}
	
	private static void writeFileToZip(File file, String zipEntryPath) throws IOException {
		byte[] readBuffer = new byte[BUFFER];
		int bytesIn = 0;
		
		FileInputStream fis = new FileInputStream(file);
		ZipEntry anEntry = new ZipEntry(zipEntryPath + file.getName());
		zipOut.putNextEntry(anEntry);
		
		while (( bytesIn = fis.read(readBuffer) ) != -1) {
			zipOut.write(readBuffer, 0, bytesIn);
		}
		zipOut.closeEntry();
		fis.close();
	}
	
}
