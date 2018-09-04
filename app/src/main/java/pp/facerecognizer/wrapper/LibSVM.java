package pp.facerecognizer.wrapper;

import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.Log;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.opencv.ml.Ml;
import org.opencv.ml.SVM;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import pp.facerecognizer.Classifier;
import pp.facerecognizer.env.FileUtils;

/**
 * Created by yctung on 9/26/17.
 * This is a java wrapper of LibSVM
 */

public class LibSVM {
    private String LOG_TAG = "LibSVM";
    private String DATA_PATH = FileUtils.ROOT + File.separator + FileUtils.DATA_FILE;
    private String MODEL_PATH = FileUtils.ROOT + File.separator + FileUtils.MODEL_FILE;

    public void train(int label, ArrayList<float[]> list) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            float[] array = list.get(i);
            builder.append(label);
            for (int j = 0; j < array.length; j++) {
                builder.append(":").append(array[j]);
            }
            if (i < list.size() - 1) builder.append(System.lineSeparator());
        }
        FileUtils.appendText(builder.toString(), FileUtils.DATA_FILE);

        train();
    }

    public void  train() {
        //获取训练数据
        ArrayList<String> dataList = null;
        try {
            dataList = FileUtils.readData(FileUtils.DATA_FILE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (dataList == null) {
            return;
        }
        Mat trainingData = new Mat(dataList.size(), 512, CvType.CV_32FC1);
        Mat trainingLabels = new Mat(dataList.size(), 1, CvType.CV_32SC1);
        for(int i = 0; i < dataList.size(); i ++){
            String[] tempString = dataList.get(i).split(":");
            for (int j = 0; j < tempString.length - 1; j ++) {
                trainingData.put(i, j, Float.parseFloat(tempString[j + 1]));
            }
            trainingLabels.put(i, 0, Float.parseFloat(tempString[0]));
        }

        //配置SVM训练器参数
        SVM svm = SVM.create();
        svm.setType(SVM.C_SVC);
        svm.setKernel(SVM.RBF);
        svm.setGamma(150);
        svm.setC(1);
        TermCriteria termCriteria = new TermCriteria(TermCriteria.EPS, 1000, 1e-6);
        svm.setTermCriteria(termCriteria);

        //训练
        svm.train(trainingData, Ml.ROW_SAMPLE, trainingLabels);

        //保存模型
        svm.save(MODEL_PATH);


    }

    public Pair<Integer, Float> predict(FloatBuffer buffer) {
        SVM svm = SVM.load(MODEL_PATH);

        ArrayList<String> dataList = null;
        try {
            dataList = FileUtils.readData("data");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }




        Mat testDatas = new Mat(1,512,CvType.CV_32FC1);
        float[] preData = new float[512];
        for(int i=0; i < 512; i ++) {
            float temp = buffer.get(i);
            preData[i] = temp;
            testDatas.put(0, i, temp);
        }
//
//        float[] osjl = new float[dataList.size()];
//        float tempSum = 0.0F;
//        float minOSJL = 2000.0F;
//        int minIndex = 0;
//        for (int i = 0; i < dataList.size(); i++) {
//            String[] tempString = dataList.get(i).split(":");
//            for (int j = 0; j < tempString.length -1; j ++){
//                tempSum = tempSum + (float) Math.pow(Float.parseFloat(tempString[j + 1]) - preData[j], 2);
//            }
//            osjl[i] = (float) Math.sqrt(tempSum);
//            if (osjl[i] <= minOSJL) {
//                minOSJL = osjl[i];
//                minIndex = i;
//            }
//            tempSum = 0.0F;
//        }
//
//        Arrays.sort(osjl);

        float prob = svm.predict(testDatas);
        ArrayList<String> labelName = null;
        try {
            labelName =  FileUtils.readLabel(FileUtils.LABEL_FILE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Mat result = new Mat();
        svm.predict(testDatas, result, 1);

        int index = (int)(prob);
        String[] tempString = dataList.get(index).split(":");
        float[] labelData = new float[512];
        float sum = 0.0F;
        float sum_mul = 0.0F;
        float self_sum_one = 0.0F;
        float self_sum_two = 0.0F;


//        for (int i = 0; i < tempString.length - 1; i++) {
//            sum = sum + (float) Math.pow(Float.parseFloat(tempString[i + 1]) - preData[i], 2);
//            sum_mul = sum_mul + Float.parseFloat(tempString[i + 1]) * preData[i];
//            self_sum_one = self_sum_one + (float) Math.pow(Float.parseFloat(tempString[i + 1]), 2);
//            self_sum_two = self_sum_two + (float) Math.pow(preData[i], 2);
//        }
//
//        float consin = sum_mul / (float) (Math.sqrt(self_sum_one) * Math.sqrt(self_sum_two));
//
//        prob = (float) Math.sqrt(sum);
//        prob = consin - prob;


//        if(prob < -0.2F){
//            prob = 0.0F;
//        }

//        return new Pair<>(minIndex, minOSJL);
        return new Pair<>(index, prob);
    }

    // singleton for the easy access
    private static LibSVM svm;
    public static LibSVM getInstance() {
        if (svm == null) {
            svm = new LibSVM();
        }
        return svm;
    }

    public LibSVM() {


    }
}
