package com.github.linghun91.dungeonsxl.util;

import java.io.*;
import java.nio.file.*;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Utility class for file operations
 * @author linghun91
 */
public class FileUtil {
    
    /**
     * Copy directory recursively
     */
    public static void copyDirectory(File source, File target) throws IOException {
        if (!source.exists()) {
            throw new IOException("Source directory does not exist: " + source);
        }
        
        if (!target.exists()) {
            target.mkdirs();
        }
        
        Path sourcePath = source.toPath();
        Path targetPath = target.toPath();
        
        Files.walk(sourcePath)
            .forEach(src -> {
                try {
                    Path dst = targetPath.resolve(sourcePath.relativize(src));
                    if (Files.isDirectory(src)) {
                        Files.createDirectories(dst);
                    } else {
                        Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
    }
    
    /**
     * Delete directory recursively
     */
    public static void deleteDirectory(File directory) throws IOException {
        if (!directory.exists()) return;
        
        Path path = directory.toPath();
        Files.walk(path)
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
    }
    
    /**
     * Create zip archive of directory
     */
    public static void zipDirectory(File source, File zipFile) throws IOException {
        if (!source.exists()) {
            throw new IOException("Source directory does not exist: " + source);
        }
        
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            
            Path sourcePath = source.toPath();
            Files.walk(sourcePath)
                .filter(path -> !Files.isDirectory(path))
                .forEach(path -> {
                    try {
                        String zipEntryName = sourcePath.relativize(path).toString();
                        ZipEntry zipEntry = new ZipEntry(zipEntryName);
                        zos.putNextEntry(zipEntry);
                        Files.copy(path, zos);
                        zos.closeEntry();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
        }
    }
    
    /**
     * Get size of directory in bytes
     */
    public static long getDirectorySize(File directory) throws IOException {
        if (!directory.exists()) return 0;
        
        return Files.walk(directory.toPath())
            .filter(path -> Files.isRegularFile(path))
            .mapToLong(path -> {
                try {
                    return Files.size(path);
                } catch (IOException e) {
                    return 0;
                }
            })
            .sum();
    }
    
    /**
     * Ensure directory exists
     */
    public static void ensureDirectory(File directory) {
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
    
    /**
     * Check if file name is valid
     */
    public static boolean isValidFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) return false;
        return fileName.matches("[a-zA-Z0-9_\\-]+");
    }
}
