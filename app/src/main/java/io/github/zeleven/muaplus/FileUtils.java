package io.github.zeleven.muaplus;
/*
* 基本的文件操作，增删重命名
 */
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileUtils {
    private static String className = FileUtils.class.getName();

    /**
     * 列出特定目录下的文件
     * @param filesPath file path used to list files.
     * @return list which contain files.
     */
    public static List<FileEntity> listFiles(String filesPath) {
        File file = new File(filesPath);
        if (!file.exists()) {
            file.mkdirs();
            return new ArrayList<>();
        }
        final ArrayList<FileEntity> entityList = new ArrayList<>();
        file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                boolean isAccept;
                String fileName = pathname.getName();
                isAccept = fileName.endsWith(".md") || fileName.endsWith(".markdown")
                        || fileName.endsWith(".mdown");
                if (isAccept) {
                    FileEntity entity = new FileEntity();
                    entity.setName(pathname.getName());
                    entity.setLastModified(pathname.lastModified());
                    entity.setAbsolutePath(pathname.getAbsolutePath());
                    entityList.add(entity);
                }
                return isAccept;
            }
        });
        Collections.sort(entityList, new Comparator<FileEntity>() {
            @Override
            public int compare(FileEntity o1, FileEntity o2) {
                return Long.compare(o2.getLastModified(), o1.getLastModified());
            }
        });
        return entityList;
    }

    /**
     * 搜索特定目录下的文件
     * @param filesPath filepath be searched
     * @param query search key word
     * @return search result
     */
    public static List<FileEntity> searchFiles(String filesPath, final String query) {
        File file = new File(filesPath);
        if (!file.exists()) {
            file.mkdirs();
            return new ArrayList<>();
        }
        final ArrayList<FileEntity> entityList = new ArrayList<>();
        file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                boolean isAccept;
                String fileName = pathname.getName();
                isAccept = fileName.contains(query) && (fileName.endsWith(".md")
                        || fileName.endsWith(".markdown") || fileName.endsWith(".mdown"));
                if (isAccept) {
                    FileEntity entity = new FileEntity();
                    entity.setName(pathname.getName());
                    entity.setLastModified(pathname.lastModified());
                    entity.setAbsolutePath(pathname.getAbsolutePath());
                    entityList.add(entity);
                }
                return isAccept;
            }
        });
        Collections.sort(entityList, new Comparator<FileEntity>() {
            @Override
            public int compare(FileEntity o1, FileEntity o2) {
                return Long.compare(o2.getLastModified(), o1.getLastModified());
            }
        });
        return entityList;
    }

    /**
     * 保存内容到特定文件
     * @param filePath file path indicate the file which be written content.
     * @param content
     * @return if save success, return true, otherwise return false.
     */
    public static boolean saveFile(String filePath, String content) {
        boolean success;
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            saveContent(file, content);
            success = true;
        } else {
            success = false;
        }
        return success;
    }

    /**
     * 重命名文件
     * @param oldFile the file which be renamed.
     * @param newFile target file.
     */
    public static void renameFile(Context context, File oldFile, File newFile) {
        if (!oldFile.exists()) {
            Log.i(className, "File not found.");
        } else {
            if (newFile.exists()) {
                Toast.makeText(context, R.string.toast_file_name_exists, Toast.LENGTH_SHORT).show();
            } else {
                oldFile.renameTo(newFile);
                Toast.makeText(context, R.string.toast_saved, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 文件写入类
     * @param file
     * @param content
     */
    public static void saveContent(File file, String content) {
        try {
            FileWriter fileWriter;
            fileWriter = new FileWriter(file.getAbsolutePath());
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get 文件名--去除扩展名
     * @param fileName
     * @return
     */
    public static String stripExtension(String fileName) {
        if (fileName == null) {
            return "";
        }

        // Get position of last '.'.
        int pos = fileName.lastIndexOf(".");
        // If there wasn't any '.' just return the string as is.
        if (pos == -1) {
            return fileName;
        }

        // Otherwise return the string, up to the dot.
        return fileName.substring(0, pos);
    }

    /**
     * 从特定路径读取文件内容
     * @param pathname pathname of file
     * @param lineBreak indicate whether should include line break in content.
     * @return
     */
    public static String readContentFromPath(String pathname, boolean lineBreak) {
        return readContent(new File(pathname), lineBreak);
    }

    /**
     * Read content from specified file.
     * @param file file used to read content.
     * @param lineBreak indicate whether should include line break in content.
     * @return
     */
    public static String readContentFromFile(File file, boolean lineBreak) {
        return readContent(file, lineBreak);
    }

    private static String readContent(File file, boolean lineBreak) {
        StringBuilder content = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
                if (lineBreak) {
                    content.append("\n");
                }
            }
        } catch (FileNotFoundException e) {
            Log.e(className, e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(className, e.getMessage());
            e.printStackTrace();
        }
        return content.toString();
    }

    /**
     * 删除文件
     * @param file
     * @return
     */
    public static boolean deleteFile(File file) {
        boolean result = false;
        if (file.exists()) {
            result = file.delete();
            Log.i(className, "Delete success.");
        } else {
            Log.i(className, "File not found.");
        }
        return result;
    }
//    获取创建日期？，然鹅咩有调用
//    public static Date getCreationDate(String filePath) {
//        Path path = Paths.get(filePath);
//        BasicFileAttributes attr = null;
//        try {
//            attr = Files.readAttributes(path, BasicFileAttributes.class);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return new Date(attr.creationTime().toMillis());
//    }
    /*
    获取文件大小，以B/KB返回
     */
    public static String getFileSize(String path){
        File file = new File(path);
        if ( file.exists()&& file.isFile()){
            return Long.toString( file.length() )  + 'B';
        }
        return "0B";
    }
}
