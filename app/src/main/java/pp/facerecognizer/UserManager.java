package pp.facerecognizer;

import java.io.FileNotFoundException;
import java.util.List;

import pp.facerecognizer.env.FileUtils;
import pp.facerecognizer.wrapper.LibSVM;

public class UserManager {
    private List<String> classNames;

    public UserManager() {
        try {
            classNames = FileUtils.readLabel(FileUtils.LABEL_FILE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int addPerson(String name) {
        FileUtils.appendText(name, FileUtils.LABEL_FILE);
        classNames.add(name);

        return classNames.size();
    }

    public int deletePerson(String studentNumber) {
        for(int i = 0; i < classNames.size(); i++) {
            if (classNames.get(i).equals(studentNumber)) {
                classNames.remove(i);
                FileUtils.deletePerson(classNames, i);
                i--;
            }
        }
        return classNames.size();
    }

    public List<String> getClassNames() {
        return classNames;
    }

    public void train() {
        LibSVM libSVM = new LibSVM();
        libSVM.train();
    }
}
