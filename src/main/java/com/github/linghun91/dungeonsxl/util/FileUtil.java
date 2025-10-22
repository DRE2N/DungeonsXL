package com.github.linghun91.dungeonsxl.util;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Utility class for file and directory operations.
 *
 * @author linghun91
 */
public final class FileUtil {

    private FileUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Copies a directory recursively.
     *
     * @param source The source directory
     * @param target The target directory
     * @return true if successful
     */
    public static boolean copyDirectory(Path source, Path target) {
        try {
            Files.walkFileTree(source, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path targetDir = target.resolve(source.relativize(dir));
                    Files.createDirectories(targetDir);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path targetFile = target.resolve(source.relativize(file));
                    Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }
            });
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a directory recursively.
     *
     * @param directory The directory to delete
     * @return true if successful
     */
    public static boolean deleteDirectory(Path directory) {
        if (!Files.exists(directory)) {
            return true;
        }

        try {
            Files.walk(directory)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets the size of a directory in bytes.
     *
     * @param directory The directory
     * @return The total size in bytes
     */
    public static long getDirectorySize(Path directory) {
        if (!Files.exists(directory)) {
            return 0;
        }

        try {
            return Files.walk(directory)
                    .filter(Files::isRegularFile)
                    .mapToLong(path -> {
                        try {
                            return Files.size(path);
                        } catch (IOException e) {
                            return 0;
                        }
                    })
                    .sum();
        } catch (IOException e) {
            return 0;
        }
    }

    /**
     * Zips a directory.
     *
     * @param sourceDir The source directory
     * @param zipFile The output zip file
     * @return true if successful
     */
    public static boolean zipDirectory(Path sourceDir, Path zipFile) {
        try {
            Files.createDirectories(zipFile.getParent());

            try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile))) {
                Files.walk(sourceDir)
                        .filter(path -> !Files.isDirectory(path))
                        .forEach(path -> {
                            try {
                                String entryName = sourceDir.relativize(path).toString();
                                ZipEntry zipEntry = new ZipEntry(entryName);
                                zos.putNextEntry(zipEntry);
                                Files.copy(path, zos);
                                zos.closeEntry();
                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        });
            }
            return true;
        } catch (IOException | UncheckedIOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Unzips a file to a directory.
     *
     * @param zipFile The zip file
     * @param targetDir The target directory
     * @return true if successful
     */
    public static boolean unzip(Path zipFile, Path targetDir) {
        try {
            Files.createDirectories(targetDir);

            try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile))) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    Path targetPath = targetDir.resolve(entry.getName());

                    // Security check: prevent zip slip
                    if (!targetPath.normalize().startsWith(targetDir.normalize())) {
                        throw new IOException("Bad zip entry: " + entry.getName());
                    }

                    if (entry.isDirectory()) {
                        Files.createDirectories(targetPath);
                    } else {
                        Files.createDirectories(targetPath.getParent());
                        Files.copy(zis, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                    zis.closeEntry();
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if a directory is empty.
     *
     * @param directory The directory to check
     * @return true if empty or doesn't exist
     */
    public static boolean isEmpty(Path directory) {
        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            return true;
        }

        try (var entries = Files.newDirectoryStream(directory)) {
            return !entries.iterator().hasNext();
        } catch (IOException e) {
            return true;
        }
    }

    /**
     * Creates a directory if it doesn't exist.
     *
     * @param directory The directory path
     * @return true if created or already exists
     */
    public static boolean ensureDirectory(Path directory) {
        try {
            Files.createDirectories(directory);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Moves a directory to a new location.
     *
     * @param source The source directory
     * @param target The target location
     * @return true if successful
     */
    public static boolean moveDirectory(Path source, Path target) {
        try {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            // If atomic move fails, try copy and delete
            if (copyDirectory(source, target)) {
                return deleteDirectory(source);
            }
            return false;
        }
    }

    /**
     * Reads all lines from a file.
     *
     * @param file The file path
     * @return Array of lines, or empty array if error
     */
    public static String[] readLines(Path file) {
        try {
            return Files.readAllLines(file).toArray(new String[0]);
        } catch (IOException e) {
            return new String[0];
        }
    }

    /**
     * Writes lines to a file.
     *
     * @param file The file path
     * @param lines The lines to write
     * @return true if successful
     */
    public static boolean writeLines(Path file, String... lines) {
        try {
            Files.createDirectories(file.getParent());
            Files.write(file, java.util.Arrays.asList(lines));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Formats a file size in human-readable format.
     *
     * @param bytes The size in bytes
     * @return Formatted string (e.g., "1.5 MB")
     */
    public static String formatSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024));
        } else {
            return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
        }
    }

    /**
     * Gets the file extension.
     *
     * @param path The file path
     * @return The extension without dot, or empty string
     */
    public static String getExtension(Path path) {
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex > 0 ? fileName.substring(dotIndex + 1) : "";
    }

    /**
     * Gets the file name without extension.
     *
     * @param path The file path
     * @return The name without extension
     */
    public static String getNameWithoutExtension(Path path) {
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
    }
}
