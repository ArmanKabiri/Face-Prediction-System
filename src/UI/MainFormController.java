/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import DataMining.FaceClassifier;
import DataMining.FaceFeatures;
import DataMining.FaceLikeHood;
import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

/**
 *
 * @author arman
 */
public class MainFormController implements Initializable {

    @FXML
    private ImageView img_main;
    @FXML
    private ImageView img_alternative1;
    @FXML
    private ImageView img_alternative2;
    @FXML
    private ImageView img_alternative3;
    @FXML
    private ComboBox<String> combo_gender;
    @FXML
    private ComboBox<String> combo_colorPicker_eye;
    @FXML
    private ComboBox<String> combo_colorPicker_hair;
    @FXML
    private ComboBox<String> combo_colorPicker_skin;
    @FXML
    private CheckBox chb_gender;
    @FXML
    private CheckBox chb_eyeColor;
    @FXML
    private CheckBox chb_hairColor;
    @FXML
    private CheckBox chb_skinColor;
    @FXML
    private CheckBox chb_beardModel;
    @FXML
    private ComboBox<String> combo_beardModel;
    @FXML
    private CheckBox chb_hairModel;
    @FXML
    private ComboBox<String> combo_hairModel;
    @FXML
    private CheckBox chb_glasses;
    @FXML
    private CheckBox chb_age;
    @FXML
    private CheckBox chb_hairLong;
    @FXML
    private CheckBox chb_mustacheLong;
    @FXML
    private CheckBox chb_beardLong;
    @FXML
    private CheckBox chb_noseSize;
    @FXML
    private Slider slider_noseSize;
    @FXML
    private Slider slider_beardLong;
    @FXML
    private Slider slider_mustacheLong;
    @FXML
    private Slider slider_hairLong;
    @FXML
    private Slider slider_age;
    @FXML
    private ComboBox<String> combo_glasses;
    @FXML
    private ToggleButton toggleBtn_switchOn;
    @FXML
    private Label lbl_mainProbability;
    @FXML
    private Label lbl_alternativeProbability1;
    @FXML
    private Label lbl_alternativeProbability2;
    @FXML
    private Label lbl_alternativeProbability3;

    private boolean isClassifierMchineOn = false;
    private ChangeListener featureChangeListener;
    private FaceClassifier faceClassifier;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            faceClassifier = new FaceClassifier();
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Exeption in Classifier initialization");
            alert.show();
            Logger.getLogger(MainFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
        initializeMouseHandler();
        initializeColorPickers();
        initialOnChangeListeners();
    }

    @FXML
    private void img_click_replaceAlternativeWithMainImage(MouseEvent event) {
        String alternativeImgID = ((ImageView) event.getSource()).getId();

        ImageView aImg = (ImageView) img_main.getParent().lookup("#" + alternativeImgID);
        Image mainImage = img_main.getImage();
        img_main.setImage(aImg.getImage());
        aImg.setImage(mainImage);

        Label aLabel = (Label) img_main.getParent().lookup("#lbl_alternativeProbability" + alternativeImgID.substring(alternativeImgID.length() - 1, alternativeImgID.length()));
        String mainProb = lbl_mainProbability.getText();
        lbl_mainProbability.setText(aLabel.getText());
        aLabel.setText(mainProb);
    }

    private void initializeColorPickers() {

        Callback<ListView<String>, ListCell<String>> factory = new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> list) {
                return new ColorRectCell();
            }
        };

        combo_colorPicker_eye.setCellFactory(factory);
        combo_colorPicker_eye.setButtonCell(factory.call(null));

        combo_colorPicker_hair.setCellFactory(factory);
        combo_colorPicker_hair.setButtonCell(factory.call(null));

        combo_colorPicker_skin.setCellFactory(factory);
        combo_colorPicker_skin.setButtonCell(factory.call(null));

    }

    private void initialOnChangeListeners() {

        featureChangeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
            if (isClassifierMchineOn) {
                FaceFeatures newFace = generateFaceFeatures();
                ArrayList<FaceLikeHood> imageNames = null;
                try {
                    imageNames = faceClassifier.classify(newFace);
                } catch (Exception ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Exeption in Classification");
                    alert.show();
                    Logger.getLogger(MainFormController.class.getName()).log(Level.SEVERE, null, ex);
                }
                setImages(imageNames);
            }
        };

        combo_colorPicker_eye.valueProperty().addListener(featureChangeListener);
        combo_colorPicker_hair.valueProperty().addListener(featureChangeListener);
        combo_colorPicker_skin.valueProperty().addListener(featureChangeListener);
        combo_gender.valueProperty().addListener(featureChangeListener);
        combo_glasses.valueProperty().addListener(featureChangeListener);
        combo_beardModel.valueProperty().addListener(featureChangeListener);
        combo_hairModel.valueProperty().addListener(featureChangeListener);

        chb_age.selectedProperty().addListener(featureChangeListener);
        chb_beardLong.selectedProperty().addListener(featureChangeListener);
        chb_beardModel.selectedProperty().addListener(featureChangeListener);
        chb_eyeColor.selectedProperty().addListener(featureChangeListener);
        chb_gender.selectedProperty().addListener(featureChangeListener);
        chb_glasses.selectedProperty().addListener(featureChangeListener);
        chb_hairColor.selectedProperty().addListener(featureChangeListener);
        chb_hairLong.selectedProperty().addListener(featureChangeListener);
        chb_hairModel.selectedProperty().addListener(featureChangeListener);
        chb_mustacheLong.selectedProperty().addListener(featureChangeListener);
        chb_noseSize.selectedProperty().addListener(featureChangeListener);
        chb_skinColor.selectedProperty().addListener(featureChangeListener);

        slider_age.valueProperty().addListener(featureChangeListener);
        slider_beardLong.valueProperty().addListener(featureChangeListener);
        slider_hairLong.valueProperty().addListener(featureChangeListener);
        slider_mustacheLong.valueProperty().addListener(featureChangeListener);
        slider_noseSize.valueProperty().addListener(featureChangeListener);

    }

    private FaceFeatures generateFaceFeatures() {

        FaceFeatures face = new FaceFeatures();

        face.age = chb_age.isSelected() ? (int) slider_age.getValue() : -1;
        face.beardLong = chb_beardLong.isSelected() ? (int) slider_beardLong.getValue() : -1;
        face.hairLong = chb_hairLong.isSelected() ? (int) slider_hairLong.getValue() : -1;
        face.mustacheLong = chb_mustacheLong.isSelected() ? (int) slider_mustacheLong.getValue() : -1;
        face.noseSize = chb_noseSize.isSelected() ? (int) slider_noseSize.getValue() : -1;

        face.beardModel = chb_beardModel.isSelected() ? FaceFeatures.BeardModel.valueOf(combo_beardModel.getValue()) : FaceFeatures.BeardModel.valueOf("unknown");
        face.gender = chb_gender.isSelected() ? FaceFeatures.Gender.valueOf(combo_gender.getValue()) : FaceFeatures.Gender.valueOf("unknown");
        face.glasses = chb_glasses.isSelected() ? FaceFeatures.Glasses.valueOf(combo_glasses.getValue()) : FaceFeatures.Glasses.valueOf("unknown");
        face.hairModel = chb_hairModel.isSelected() ? FaceFeatures.HairModel.valueOf(combo_hairModel.getValue()) : FaceFeatures.HairModel.valueOf("unknown");

        face.eyeColor = chb_eyeColor.isSelected() ? mapUiColorToDBColor(combo_colorPicker_eye.getValue()) : "";
        face.hairColor = chb_hairColor.isSelected() ? mapUiColorToDBColor(combo_colorPicker_hair.getValue()) : "";
        face.skinColor = chb_skinColor.isSelected() ? mapUiColorToDBColor(combo_colorPicker_skin.getValue()) : "";

        return face;
    }

    private String mapUiColorToDBColor(String uiColor) {
        if (uiColor.equals("black")) {
            return "black";
        } else if (uiColor.equals("SkyBlue")) {
            return "blue";
        } else if (uiColor.equals("LightSteelBlue")) {
            return "gray";
        } else if (uiColor.equals("LightSteelBlue")) {
            return "gray";
        } else if (uiColor.equals("SaddleBrown")) {
            return "brown";
        } else if (uiColor.equals("SeaGreen")) {
            return "green";
        } else if (uiColor.equals("white")) {
            return "white";
        } else if (uiColor.equals("PaleGoldenRod")) {
            return "golden";
        } else if (uiColor.equals("AntiqueWhite")) {
            return "white";
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Chosen Color is Wrong");
            alert.show();
            return null;
        }
    }

    private void setImages(ArrayList<FaceLikeHood> faces) {
        try {
            String path = "images/";

            File fm = new File(path + faces.get(0).name + ".jpg");
            File f1 = new File(path + faces.get(1).name + ".jpg");
            File f2 = new File(path + faces.get(2).name + ".jpg");
            File f3 = new File(path + faces.get(3).name + ".jpg");
            Image im = new Image(fm.toURI().toString());
            Image i1 = new Image(f1.toURI().toString());
            Image i2 = new Image(f2.toURI().toString());
            Image i3 = new Image(f3.toURI().toString());
            img_main.setImage(im);
            img_alternative1.setImage(i1);
            img_alternative2.setImage(i2);
            img_alternative3.setImage(i3);
            DecimalFormat twoDForm = new DecimalFormat("##.##");
            for (int i = 0; i < 4; i++) {
                if (i == 0) {
                    double probability = Double.valueOf(twoDForm.format(faces.get(0).probability * 100));
                    lbl_mainProbability.setText(probability + " % ");
                }
                else{
                    double probability = Double.valueOf(twoDForm.format(faces.get(i).probability * 100));
                    Label aLabel = (Label) lbl_mainProbability.getParent().lookup("#lbl_alternativeProbability" + i);
                    aLabel.setText(probability + " % ");
                }
            }

        } catch (Exception x) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Image not found");
            alert.show();
            Logger.getLogger(MainFormController.class.getName()).log(Level.SEVERE, null, x);
        }
    }

    @FXML
    private void onClick_toggleBtn_SwitchOn(ActionEvent event) {
        isClassifierMchineOn = toggleBtn_switchOn.isSelected();
        if (isClassifierMchineOn) {
            FaceFeatures newFace = generateFaceFeatures();
            ArrayList<FaceLikeHood> imageNames = null;
            try {
                imageNames = faceClassifier.classify(newFace);
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Exeption in Classification");
                alert.show();
                Logger.getLogger(MainFormController.class.getName()).log(Level.SEVERE, null, ex);
            }
            setImages(imageNames);
        }
    }

    private void initializeMouseHandler() {

    }
}
