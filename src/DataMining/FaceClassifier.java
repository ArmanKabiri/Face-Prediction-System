/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataMining;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.apache.commons.lang3.ArrayUtils;
import weka.classifiers.Classifier;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author arman
 */
public class FaceClassifier {

    private Classifier classifier;
    private Instances trainingSet;

    public FaceClassifier() throws FileNotFoundException, IOException, Exception {
        classifier = (Classifier) new weka.classifiers.functions.MultilayerPerceptron();
        FileReader reader = new FileReader(new File("src/metaData/TrainingData.arff"));
        trainingSet = new Instances(reader);
        trainingSet.setClassIndex(0);
        classifier.buildClassifier(trainingSet);
    }

    @SuppressWarnings("empty-statement")
    public ArrayList<FaceLikeHood> classify(FaceFeatures newFace) throws Exception {

        Instances acceptableInstances = new Instances(trainingSet, trainingSet.numInstances());

        Instance testInstance = new DenseInstance(trainingSet.numAttributes());
        testInstance.setDataset(trainingSet);
        createTestInstance(testInstance, newFace);

        //There is a Problem Here
        //num of instances:15   outPut in Double:17 !!!
        //it seems that 2 last instances are fake
        double[] tempResult = classifier.distributionForInstance(testInstance);
        ArrayList<Double> resultArray = new ArrayList<>(Arrays.asList(ArrayUtils.toObject(tempResult)));

//        Evaluation e=new Evaluation(trainingSet);
//        double[] tempResult1 = e.evaluateModel(classifier, trainingSet);
        if (newFace.gender != FaceFeatures.Gender.unknown) {    //filter by gender
            for (int i = 0; i < trainingSet.size(); i++) {
                if (trainingSet.get(i).stringValue(trainingSet.attribute("gender")).equals(newFace.gender.toString())) {
                    acceptableInstances.add(trainingSet.get(i));
                } else {
                    resultArray.set(i, null);
                }
            }
            while (resultArray.remove(null));
        } else {
            acceptableInstances.addAll(trainingSet);
        }

        sortInstancesBaseOnComparator(acceptableInstances, resultArray);
        Collections.sort(resultArray);
        Collections.reverse(resultArray);
        resultArray = new ArrayList<>(resultArray.subList(0, 4));
        ArrayList<FaceLikeHood> resultClasses = get4MostLikelyClass(acceptableInstances, resultArray);

        return resultClasses;

    }

    private void sortInstancesBaseOnComparator(Instances instances, ArrayList<Double> distributions) {
        instances.sort((Instance o1, Instance o2) -> {
            int index1, index2;
            index1 = instances.indexOf(o1);
            index2 = instances.indexOf(o2);
            if (distributions.get(index1) == distributions.get(index2)) {
                return 0;
            } else if (distributions.get(index1) > distributions.get(index2)) {
                return -1;
            } else {
                return 1;
            }
        });
    }

    private ArrayList<FaceLikeHood> get4MostLikelyClass(Instances instances, ArrayList<Double> values) {
        ArrayList<FaceLikeHood> result = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (i < instances.numInstances()) {
                String faceClass = instances.instance(i).stringValue(instances.attribute("class"));
                result.add(new FaceLikeHood(faceClass, values.get(i)));
            } else {
                result.add(new FaceLikeHood("", 0));
            }
        }
        return result;
    }

    private void createTestInstance(Instance testInstance, FaceFeatures face) {
        if (face.gender != FaceFeatures.Gender.unknown) {
            testInstance.setValue(trainingSet.attribute("gender"), face.gender.toString());
        }
        if (face.eyeColor != "") {
            testInstance.setValue(trainingSet.attribute("eye_color"), face.eyeColor);
        }
        if (face.hairColor != "") {
            testInstance.setValue(trainingSet.attribute("hair_color"), face.hairColor);
        }
        if (face.skinColor != "") {
            testInstance.setValue(trainingSet.attribute("skin_color"), face.skinColor);
        }
        if (face.beardModel != FaceFeatures.BeardModel.unknown) {
            testInstance.setValue(trainingSet.attribute("beard_model"), face.beardModel.toString());
        }
        if (face.hairModel != FaceFeatures.HairModel.unknown) {
            testInstance.setValue(trainingSet.attribute("hair_model"), face.hairModel.toString());
        }
        if (face.glasses != FaceFeatures.Glasses.unknown) {
            testInstance.setValue(trainingSet.attribute("glasses"), face.glasses.toString());
        }
        if (face.age != -1) {
            testInstance.setValue(trainingSet.attribute("age"), face.age);
        }
        if (face.hairLong != -1) {
            testInstance.setValue(trainingSet.attribute("hair_long"), face.hairLong);
        }
        if (face.mustacheLong != -1) {
            testInstance.setValue(trainingSet.attribute("mustache_long"), face.mustacheLong);
        }
        if (face.beardLong != -1) {
            testInstance.setValue(trainingSet.attribute("beard_long"), face.beardLong);
        }
        if (face.noseSize != -1) {
            testInstance.setValue(trainingSet.attribute("nose_size"), face.noseSize);
        }
    }

}
