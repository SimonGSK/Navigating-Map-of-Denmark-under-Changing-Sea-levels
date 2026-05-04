package models.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;

import javafx.scene.control.*;
import java.util.HashMap;
import java.util.function.Function;

public class UserInterface {
    private boolean isInitialized = false;
    private final AppController appController;
    private final UserControlCollection controlCollection = new UserControlCollection(new HashMap<>(),new HashMap<>(),new HashMap<>());
    private final BorderPane appLayout = new BorderPane();
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
    }

    public void init() {
        if (isInitialized) {
            return;
        }

        Button mapStateToggle = buttonToggleMapState();
        Button heightCurvesToggle = buttonToggleHeightCurves();
        LabelledButtonGroup zoomButtonGroup = buttonGroupZoomControl();
        LabelledSlider seaLevelSlider = sliderSeaLevel(appController.getAppData().getHeightCurveData().getMaxHeight());

        controlCollection.buttonList.put("MapStateToggle",mapStateToggle);
        controlCollection.buttonList.put("HeightCurveToggle",heightCurvesToggle);
        controlCollection.buttonGroupList.put("ZoomButtonGroup",zoomButtonGroup);
        controlCollection.sliderList.put("SeaLevelSlider", seaLevelSlider);

        HBox controlPanel = new HBox(mapStateToggle,heightCurvesToggle);
        controlPanel.getChildren().addAll(seaLevelSlider.toNodes());
        controlPanel.getChildren().addAll(zoomButtonGroup.toNodes());
        controlPanel.setPadding(new Insets(8));
        controlPanel.setAlignment(Pos.CENTER_LEFT);

        BorderPane layout = new BorderPane();
        layout.setCenter(new StackPane(appController.imageView, appController.getEventHandler().getMapMouseEventComponent()));
        layout.setBottom(controlPanel);
        controlPanel.setStyle("--fx-background-color: white");

        isInitialized = true;
    }

    public BorderPane getLayout() {
        return appLayout;
    }

    public record UserControlCollection(HashMap<String,Button> buttonList, HashMap<String,LabelledButtonGroup> buttonGroupList, HashMap<String,LabelledSlider> sliderList) { }

    private Button buttonToggleMapState() {
        Function<MapState,String> buttonLabel = (MapState mapState) -> switch (mapState) {
            case osm -> "Show elevation map";
            default -> "Show normal map";
        };

        Button button = new Button(buttonLabel.apply(mapState));
        button.setOnAction(e -> {
            mapState = mapState == MapState.osm ? MapState.elevation : MapState.osm;
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

    public boolean isShowHeightCurves() {
        return showHeightCurves;
    }

    private record LabelledSlider(Label label, Slider slider) {
        public Node[] toNodes() {
            Node[] nodes = new Node[2];
            nodes[0] = label;
            nodes[1] = slider;
            return nodes;
        }

        public void addTo(Pane container) {
            container.getChildren().addAll(toNodes());
        }
    };

    private record LabelledButtonGroup(Label label, Button... buttons) {
        public Node[] toNodes() {
            Node[] nodes = new Node[1 + (buttons == null ? 0 : buttons.length)];
            nodes[0] = label;
            if (buttons != null && buttons.length > 0) {
                // Using System.arraycopy instead of for-loop to copy entries from button into nodes
                System.arraycopy(buttons, 0, nodes, 1, buttons.length);
            }
            return nodes;
        }

        public void addTo(Pane container) {
            container.getChildren().addAll(toNodes());
        }
    }

    public UserControlCollection getControlCollection() {
        return controlCollection;
    }
}
