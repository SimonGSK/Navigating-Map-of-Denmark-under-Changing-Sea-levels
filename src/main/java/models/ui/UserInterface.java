package models.ui;

import javafx.scene.control.Button;
import javafx.scene.control.Slider;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class UserInterface {
    private final AppController appController;
    private final userControlCollection controlCollection = new userControlCollection(new LinkedList<>(),new LinkedList<>(),new LinkedList<>());
    private boolean showHeightCurves = false;
    private MapState mapState = MapState.osm;
    private UserMode userMode = UserMode.menu;

    public enum MapState {
        osm,
        elevation,
    }

    public enum UserMode {
        menu,
        explore,
        select
    }

    public MapState getMapState() {
        return mapState;
    }

    public void setUserMode(UserMode userMode) {
        this.userMode = userMode;
    }

    public UserMode getUserMode() {
        return userMode;
    }

    public UserInterface(AppController appController) {
        this.appController = appController;
        init();
    }

    private void init() {
        controlCollection.buttonList.put("MapStateToggle", buttonToggleMapState());
        controlCollection.buttonList.put("HeightCurveToggle",buttonToggleHeightCurves());
        controlCollection.buttonGroupList.put("ZoomButtonGroup",buttonGroupZoomControl());
        controlCollection.sliderList.put("SeaLevelSlider", sliderSeaLevel(appController.getAppData().getHeightCurveData().getMaxHeight()));
    }

    private record userControlCollection(HashMap<String,Button> buttonList, HashMap<String,LabelledButtonGroup> buttonGroupList, HashMap<String,LabelledSlider> sliderList) {   }

    private Button buttonToggleMapState() {
        Function<MapState,String> buttonLabel = (MapState mapState) -> switch (mapState) {
            case osm -> "Show elevation map";
            default -> "Show normal map";
        };

        Button button = new Button(buttonLabel.apply(mapState));
        button.setOnAction(e -> {
            if (Objects.requireNonNull(mapState) == MapState.osm) {
                mapState = MapState.elevation;
            } else {
                mapState = MapState.osm;
            }
            ;
            button.setText(buttonLabel.apply(mapState));
            appController.update();
        });

        return button;
    };

    private Button buttonToggleHeightCurves() {
        Function<Boolean,String> buttonLabel = (Boolean showHeightCurve) -> {
            if (showHeightCurve) {
                return "Hide height curves";
            }
            return "Show height curves";
        };

        Button button = new Button(buttonLabel.apply(showHeightCurves));
        button.setOnAction(e -> {
            showHeightCurves = !showHeightCurves;
            button.setText(buttonLabel.apply(showHeightCurves));
            appController.update();
        });

        return button;
    };

    private LabelledSlider sliderSeaLevel(double maxHeight) {
        Slider slider = new Slider(0,maxHeight,0);

        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(Math.ceil(maxHeight / 100) * 10);
        slider.setPrefWidth(300);

        Label label = new Label("Sea level: 0m");

        slider.valueProperty().addListener((obsVal, oldVal, newVal) -> {
            float level = newVal.floatValue();
            label.setText(String.format("Sea level: %.0fm", level));
            appController.updateSeaLevel(level);
            appController.update();
        });

        return new LabelledSlider(label,slider);
    }

    private LabelledButtonGroup buttonGroupZoomControl() {
        Label label = new Label();
        appController.getSuperAffine().scaleX().addListener((obsVal,oldVal,newVal) -> {
            label.setText(String.format("Zoom: %.1fx",appController.getZoomLevel()));
        });

        Button buttonZoomOut = new Button("-");
        buttonZoomOut.setOnAction(e -> appController.handleZoom(1/1.5, DrawingApp.getWIDTH() / 2.0, DrawingApp.getHEIGHT() / 2.0));

        Button buttonZoomIn = new Button("+");
        buttonZoomIn.setOnAction(e -> appController.handleZoom(1.5, DrawingApp.getWIDTH() / 2.0, DrawingApp.getHEIGHT() / 2.0));

        return new LabelledButtonGroup(label,buttonZoomOut,buttonZoomIn);
    }

    private record LabelledSlider(Label label, Slider slider) {};

    private record LabelledButtonGroup(Label label, Button... button) { }

    private Map<String,Button> getButtonMap() {
        return buttonMap;
    }
}
