import java.io.*;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileIO
{
    public static void clearDir(String path)
    {
        if (!isDir(path))
            return;
        if (emptyDir(path))
            return;
        String[] files = getFiles(path);
        for (String file: files)
        {
            String fullpath = path + file;
            File file1 = new File(fullpath);
            file1.delete();
        }
        System.out.println("Directory " + path + " clear!");
    }

    public static boolean emptyDir(String path)
    {
        File dir = new File(path);
        return dir.list().length == 0;
    }

    public static String[] getFiles(String path)
    {
        File dir = new File(path);
        return dir.list();
    }

    public static String getMtime(String filename)
    {
        File file = new File(filename);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(file.lastModified());
    }

    public static boolean isFile(String filename)
    {
        File file = new File(filename);
        return file.isFile();
    }

    public static boolean isDir(String path)
    {
        File dir = new File(path);
        return dir.isDirectory();
    }

    public static boolean mkDir(String path)
    {
        File newDir = new File(path);
        return newDir.mkdir();
    }

    public static String readFile(String filename)
    {
        StringBuilder sb = new StringBuilder();
        try(Scanner sc = new Scanner(new File(filename)))
        {
            while (sc.hasNextLine())
                sb.append(sc.nextLine());
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static LinkedList<StringBuilder> readMdFile(String filename)
    {
        LinkedList<StringBuilder> mdLines = new LinkedList<>();
        int c;
        int flag = 0;
        StringBuilder sb = new StringBuilder();
        try(FileReader fr = new FileReader(filename))
        {
            while ((c = fr.read()) != -1)
            {
                if (flag == 1 && c != '\n')
                {
                    if (sb.charAt(sb.length() - 1) != '\n')
                        sb.append('\n');
                    mdLines.add(sb);
                    sb = new StringBuilder();
                    flag = 0;
                }
                else if (flag == 0 && c == '\n')
                    flag = 1;
                sb.append((char)c);
            }
            if (sb.length() > 0)
            {
                if (sb.charAt(sb.length() - 1) != '\n')
                    sb.append('\n');
                mdLines.add(sb);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        for (int i = 0; i < mdLines.size(); i++)
        {
            if (Convert.isEmptyLine(mdLines.get(i)))
                mdLines.remove(i--);
        }
        return mdLines;
    }

    public static void removeFile(String filename)
    {
        if (!isFile(filename))
            return;
        File file = new File(filename);
        file.delete();
    }

    public static void writeFile(String filename, String content)
    {
        try(FileWriter fr = new FileWriter(filename))
        {
            fr.write(content);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        System.out.println("Create file: " + filename + " successfully!");
    }

    public static void unzip(File zipFilePath, String destDir)
    {
        File dir = new File(destDir);
        // create output directory if it doesn't exist
        if(!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                String fileName = ze.getName();
                File newFile = new File(destDir + File.separator + fileName);
                //create directories for sub directories in zip
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}