package pp.facerecognizer.env;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileUtils {
    private static final Logger LOGGER = new Logger();
    public static final String ROOT =
            Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "facerecognizer";

    public static final String DATA_FILE = "data";
    public static final String MODEL_FILE = "model.xml";
    public static final String LABEL_FILE = "label";
    public static final String PWD_FILE = "password";

    /**
     * Saves a Bitmap object to disk for analysis.
     *
     * @param bitmap The bitmap to save.
     * @param filename The location to save the bitmap to.
     */
    public static void saveBitmap(final Bitmap bitmap, final String filename) {
        LOGGER.i("Saving %dx%d bitmap to %s.", bitmap.getWidth(), bitmap.getHeight(), ROOT);
        final File myDir = new File(ROOT);

        if (!myDir.mkdirs()) {
            LOGGER.i("Make dir failed");
        }

        final File file = new File(myDir, filename);
        if (file.exists()) {
            file.delete();
        }
        try {
            final FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 99, out);
            out.flush();
            out.close();
        } catch (final Exception e) {
            LOGGER.e(e, "Exception!");
        }
    }

    public static void copyAsset(AssetManager mgr, String filename) {
        InputStream in = null;
        OutputStream out = null;

        try {
            File file = new File(ROOT + File.separator + filename);
            if (!file.exists()) {
                file.createNewFile();
            }

            in = mgr.open(filename);
            out = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int read;
            while((read = in.read(buffer)) != -1){
                out.write(buffer, 0, read);
            }
        } catch (Exception e) {
            LOGGER.e(e, "Excetion!");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOGGER.e(e, "IOExcetion!");
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    LOGGER.e(e, "IOExcetion!");
                }
            }
        }
    }

    public static void appendText(String text, String filename) {
        try(FileWriter fw = new FileWriter(ROOT + File.separator + filename, true);
            PrintWriter out = new PrintWriter(new BufferedWriter(fw))) {
            out.println(text);
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
            LOGGER.e(e, "IOException!");
        }
    }

    public static ArrayList<String> readLabel(String filename) throws FileNotFoundException{
        Scanner s = new Scanner(new File(ROOT + File.separator + filename));
        ArrayList<String> list = new ArrayList<>();
        while (s.hasNextLine()){
            list.add(s.nextLine());
        }
        s.close();

        return list;
    }

    public static ArrayList<String> readData(String filename) throws FileNotFoundException{
        Scanner s = new Scanner(new File(ROOT + File.separator + filename));
        ArrayList<String> list = new ArrayList<>();
        while (s.hasNextLine()){
            list.add(s.nextLine());
        }
        s.close();

        return list;
    }

    private static void writeForDelete(ArrayList<String> data, String filename, boolean flag){
        try(FileWriter fw = new FileWriter(ROOT + File.separator + filename, true);
            PrintWriter out = new PrintWriter(new BufferedWriter(fw))) {
            for (int i = 0; i < data.size(); i ++) {
                if (flag){
                    String tempTextData = i + "";
                    String[] tempData = data.get(i).split(":");
                    tempData[0] = i + "";
                    for (int t = 1; t < tempData.length; t ++) {
                        tempTextData = tempTextData + ":" + tempData[t];
                    }
                    out.println(tempTextData);
                }else {
                    out.println(data.get(i));
                }
            }
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
            LOGGER.e(e, "IOException!");
        }
    }


    public static void deletePerson(List classStudentNumber, int studentNumber) {
        ArrayList<String> dataList = null;
        try {
            dataList =  readData(FileUtils.DATA_FILE);
            for (int i = 0; i < dataList.size(); i++) {
                if (new Integer(dataList.get(i).split(":")[0]) == studentNumber) {
                    dataList.remove(i);
                }

            }
            File dataFile = new File(ROOT + File.separator + FileUtils.DATA_FILE);
            File labelFile = new File(ROOT + File.separator + FileUtils.LABEL_FILE);
            if (dataFile.exists()) {
                dataFile.delete();
                dataFile.createNewFile();
            }
            if (labelFile.exists()) {
                labelFile.delete();
                labelFile.createNewFile();
            }
            writeForDelete((ArrayList<String>) classStudentNumber,FileUtils.LABEL_FILE, false);
            writeForDelete(dataList, FileUtils.DATA_FILE, true);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String readPwd(String filename){
        String test="";
        try(Scanner s = new Scanner(new File(ROOT + File.separator + filename))){
            test =s.nextLine();
        }catch (FileNotFoundException f){
            LOGGER.e(f, "IOException!");
        }
        return test;
    }
}
