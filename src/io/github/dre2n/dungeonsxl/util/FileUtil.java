package io.github.dre2n.dungeonsxl.util;

import io.github.dre2n.dungeonsxl.DungeonsXL;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class FileUtil {
	
	public static ArrayList<File> getFilesForFolder(File folder) {
		ArrayList<File> files = new ArrayList<File>();
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				ArrayList<File> childFolderFiles = new ArrayList<File>();
				childFolderFiles = getFilesForFolder(file);
				files.addAll(childFolderFiles);
			} else {
				files.add(file);
			}
		}
		return files;
	}
	
	private static final String[] excludedFiles = {"config.yml", "uid.dat", "DXLData.data"};
	
	public static void copyDirectory(File sourceLocation, File targetLocation) {
		if (sourceLocation.isDirectory()) {
			if ( !targetLocation.exists()) {
				targetLocation.mkdir();
			}
			
			String[] children = sourceLocation.list();
			for (String element : children) {
				boolean isOk = true;
				
				for (String excluded : excludedFiles) {
					if (element.contains(excluded)) {
						isOk = false;
						break;
					}
				}
				
				if (isOk) {
					copyDirectory(new File(sourceLocation, element), new File(targetLocation, element));
				}
			}
			
		} else {
			try {
				if ( !targetLocation.getParentFile().exists()) {
					
					new File(targetLocation.getParentFile().getAbsolutePath()).mkdirs();
					targetLocation.createNewFile();
					
				} else if ( !targetLocation.exists()) {
					
					targetLocation.createNewFile();
				}
				
				InputStream in = new FileInputStream(sourceLocation);
				OutputStream out = new FileOutputStream(targetLocation);
				
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				
				in.close();
				out.close();
				
			} catch (Exception e) {
				if (e.getMessage().contains("Zugriff") || e.getMessage().contains("Access")) {
					DungeonsXL.getPlugin().log("Error: " + e.getMessage() + " // Access denied");
				} else {
					DungeonsXL.getPlugin().log("Error: " + e.getMessage());
				}
			}
		}
	}
	
	public static void deletenotusingfiles(File directory) {
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.getName().equalsIgnoreCase("uid.dat") || file.getName().contains(".id_")) {
				file.delete();
			}
		}
	}
	
	public static boolean removeDirectory(File directory) {
		if (directory.isDirectory()) {
			for (File f : directory.listFiles()) {
				if ( !removeDirectory(f)) {
					return false;
				}
			}
		}
		return directory.delete();
	}
	
	public static void copyFile(File in, File out) throws IOException {
		FileChannel inChannel = null;
		FileChannel outChannel = null;
		try {
			inChannel = new FileInputStream(in).getChannel();
			outChannel = new FileOutputStream(out).getChannel();
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} catch (IOException e) {
			throw e;
		} finally {
			if (inChannel != null) {
				inChannel.close();
			}
			if (outChannel != null) {
				outChannel.close();
			}
		}
	}
	
}
