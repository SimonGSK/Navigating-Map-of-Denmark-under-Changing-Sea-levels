package models.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;

import javafx.scene.control.*;
import models.pathfinding.PathfindingObject;

import java.util.HashMap;
import java.util.function.Function;

public class UserInterface {
    private boolean isInitialized = false;
    private final AppController appController;
    private final UserControlCollection controlCollection = new UserControlCollection(new HashMap<>(),new HashMap<>(),new HashMap<>());
    private final BorderPane appLayout = new BorderPane();
    private boolean showHeightCurves = false;
    private MapState mapState = MapState.osm;
    private final ObjectProperty<UserMode> userMode = new SimpleObjectProperty<>(UserMode.menu);
    private final ObjectProperty<Boolean> isViewportDebug = new SimpleObjectProperty<>(false);
    private final ObjectProperty<Boolean> isBoundingBoxDebug = new SimpleObjectProperty<>(false);

    public boolean isViewportDebug() {
        return isViewportDebug.get();
    }

    public void setViewportDebug(boolean isViewportDebug) {
        this.isViewportDebug.set(isViewportDebug);
    }

    public boolean isBoundingBoxDebug() {
        return isBoundingBoxDebug.get();
    }

    public void setBoundingBoxDebug(boolean isBoundingBoxDebug) {
        this.isBoundingBoxDebug.set(isBoundingBoxDebug);
    }

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
        this.userMode.set(userMode);
    }

    public UserMode getUserMode() {
        return this.userMode.get();
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
        double maxHeight = appController.getAppData().getHeightCurveData() != null ? appController.getAppData().getHeightCurveData().getMaxHeight() : 0.0;
        LabelledSlider seaLevelSlider = sliderSeaLevel(maxHeight);

        controlCollection.buttonList.put("MapStateToggle",mapStateToggle);
        controlCollection.buttonList.put("HeightCurveToggle",heightCurvesToggle);
        controlCollection.buttonGroupList.put("ZoomButtonGroup",zoomButtonGroup);
        controlCollection.sliderList.put("SeaLevelSlider", seaLevelSlider);

        HBox selectedLocationsIndicator = new HBox(labelPathfindingStartLocation(), labelPathfindingEndLocation());
        selectedLocationsIndicator.setSpacing(4.0);

        Node viewportIndicator = labelViewportIndicator();
        Node boundingBoxIndicator = labelBoundingBoxIndicator();
        HBox commandGroup = new HBox(viewportIndicator, boundingBoxIndicator);

        commandGroup.setSpacing(12.0);

        Node userModeIndicator = labelUserModeIndicator();

        Region spacer1 = new Region();
        Region spacer2 = new Region();

        HBox.setHgrow(spacer1,Priority.ALWAYS);
        HBox.setHgrow(spacer2,Priority.ALWAYS);

        HBox statusPanel = new HBox(userModeIndicator, spacer1, commandGroup, spacer2, selectedLocationsIndicator);
        statusPanel.setSpacing(8.0);
        statusPanel.setPadding(new Insets(8));
        statusPanel.setAlignment(Pos.CENTER);

        Region spacer3 = new Region();
        Region spacer4 = new Region();
        HBox.setHgrow(spacer3,Priority.ALWAYS);
        HBox.setHgrow(spacer4,Priority.ALWAYS);

        HBox controlPanel = new HBox();
        controlPanel.setSpacing(8.0);
        controlPanel.setPadding(new Insets(8));
        controlPanel.setAlignment(Pos.CENTER_LEFT);
        if (appController.getAppData().getHeightCurveData() != null) {
            controlPanel.getChildren().addAll(mapStateToggle, heightCurvesToggle, spacer3);
            controlPanel.getChildren().addAll(seaLevelSlider.toNodes());
            controlPanel.getChildren().add(spacer4);
        }
        controlPanel.getChildren().addAll(zoomButtonGroup.toNodes());


        appLayout.setTop(statusPanel);
        appLayout.setCenter(new StackPane(appController.imageView,
                                          appController.getEventHandler().getMapMouseEventComponent()));
        appLayout.setBottom(controlPanel);

        controlPanel.setStyle("--fx-background-color: white");

        isInitialized = true;
    }

    public BorderPane getLayout() {
        return appLayout;
    }

    public record UserControlCollection(HashMap<String,Button> buttonList,
                                        HashMap<String,LabelledButtonGroup> buttonGroupList,
                                        HashMap<String,LabelledSlider> sliderList) { }

    private Label labelUserModeIndicator() {
        Function<UserMode, String> labelText = (UserMode userMode) -> switch (userMode) {
            case menu -> "Menu";
            case select -> "Selection Mode";
            case explore -> "Exploration Mode";
        };

        Label label = new Label(labelText.apply(userMode.get()));

        userMode.addListener((obs,oldVal,newVal) -> {
            label.setText(labelText.apply(userMode.get()));
        });

        return label;
    };

    private Label labelViewportIndicator() {

        Function<Boolean, String> labelText = (Boolean isViewportDebug) -> {
            if (isViewportDebug) {
                return "Viewport Debug ON [V]";
            }
            return "Viewport Debug OFF [V]";
        };

        return getLabel(labelText, isViewportDebug);
    };

    private Label labelBoundingBoxIndicator() {

        Function<Boolean, String> labelText = (Boolean isBoundingBox) -> {
            if (isBoundingBox) {
                return "BoundingBox Debug ON [B]";
            }
            return "BoundingBox Debug OFF [B]";
        };

        return getLabel(labelText, isBoundingBoxDebug);
    };

    private Label getLabel(Function<Boolean, String> labelText, ObjectProperty<Boolean> isDebug) {
        Label label = new Label(labelText.apply(isDebug.get()));
        label.setStyle(isDebug.get() ? "-fx-font-weight: bold;" : "");

        isDebug.addListener((obs, oldVal, newVal) -> {
            label.setText(labelText.apply(isDebug.get()));
            label.setStyle(isDebug.get() ? "-fx-font-weight: bold;" : "");
        });

        return label;
    };

    private Label labelPathfindingStartLocation() {
        Function<models.osm.Node, String> labelText = (node) -> {
            if (node == null) {
                return "-- --";
            }
            return node.toString();
        };

        Label label = new Label("A: " + labelText.apply(appController.getPathfindingObject().getStartNode()));

        appController.getPathfindingObject().getStartNodeProperty().addListener((obs,oldVal,newVal) -> {
            label.setText("A: " + labelText.apply(appController.getPathfindingObject().getStartNode()));
        });

        return label;
    }

    private Label labelPathfindingEndLocation() {
        Function<models.osm.Node, String> labelText = (node) -> {
            if (node == null) {
                return "-- --";
            }
            return node.toString();
        };

        Label label = new Label("B: " + labelText.apply(appController.getPathfindingObject().getEndNode()));

        appController.getPathfindingObject().getEndNodeProperty().addListener((obs,oldVal,newVal) -> {
            label.setText("B: " + labelText.apply(appController.getPathfindingObject().getEndNode()));
        });

        return label;
    }

    private Button buttonToggleMapState() {
        Function<MapState,String> buttonLabel = (MapState mapState) -> switch (mapState) {
            case osm -> "Show elevation map";
            default -> "Show normal map";
        };

        Button button = new Button(buttonLabel.apply(mapState));
        button.setOnAction(e -> {
            mapState = mapState == MapState.osm ? MapState.elevation : MapState.osm;
            button.setText(buttonLabel.apply(mapState));
            appController.handleDraw();
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
            appController.handleDraw();
        });

        return button;
    };

    private LabelledSlider sliderSeaLevel(double maxHeight) {
        if (maxHeight <= 0) maxHeight = 1.0; // prevent empty slider range
        Slider slider = new Slider(0,maxHeight,0);

        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(Math.ceil(maxHeight / 100) * 10);
        slider.setPrefWidth(300);

        Label label = new Label("Sea level: 0m");

        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            float level = newVal.floatValue();
            label.setText(String.format("Sea level: %.0fm", level));
            appController.updateSeaLevel(level);
            appController.handleDraw();
        });

        return new LabelledSlider(label,slider);
    }

    private LabelledButtonGroup buttonGroupZoomControl() {
        Label label = new Label();
        appController.getSuperAffine().scaleX().addListener((obs,oldVal,newVal) -> {
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
    }

    public UserControlCollection getControlCollection() {
        return controlCollection;
    }
}
