package models.ui;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;

import javafx.scene.control.*;

import java.util.HashMap;
import java.util.function.Function;

/**
 * Builds and manages the JavaFX UI layout.
 */
public class UserInterface {
    private boolean isInitialized = false;
    private final AppController appController;
    private final UserControlCollection controlCollection = new UserControlCollection(new HashMap<>(),new HashMap<>(),new HashMap<>());
    private final BorderPane appLayout = new BorderPane();
    private final AppSettings appSettings = AppSettings.getInstance();

    /**
     * @param appController controller used for callbacks
     */
    public UserInterface(AppController appController) {
        this.appController = appController;
    }

    /**
     * Initializes controls and layout.
     */
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
        Node pathfindingIndicator = labelPathfindingIndicator();
        HBox commandGroup = new HBox(viewportIndicator, boundingBoxIndicator, pathfindingIndicator);

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

        StackPane mapPane = new StackPane(
                appController.imageView,
                appController.getEventHandler().getMapMouseEventComponent()
        );

        // TOP LEFT HINTS *******
        Label selectModeHint = applyHintLabelStyle(new Label("Press [Esc] to reset"));
        selectModeHint.visibleProperty().bind(appSettings.userModeProperty().isEqualTo(AppSettings.UserMode.select));
        selectModeHint.managedProperty().bind(selectModeHint.visibleProperty());
        StackPane.setAlignment(selectModeHint, Pos.TOP_LEFT);
        StackPane.setMargin(selectModeHint, new Insets(12, 0, 0, 12));
        mapPane.getChildren().add(selectModeHint);

        Label exploreModeHint = applyHintLabelStyle(new Label("Press [S] to enter selection mode"));
        exploreModeHint.visibleProperty().bind(appSettings.userModeProperty().isEqualTo(AppSettings.UserMode.explore));
        exploreModeHint.managedProperty().bind(exploreModeHint.visibleProperty());
        StackPane.setAlignment(exploreModeHint, Pos.TOP_LEFT);
        StackPane.setMargin(exploreModeHint, new Insets(12, 0, 0, 12));
        mapPane.getChildren().add(exploreModeHint);
        // TOP LEFT HINTS *******

        mapPane.setMinSize(0, 0);

        appController.imageView.fitWidthProperty().bind(mapPane.widthProperty());
        appController.imageView.fitHeightProperty().bind(mapPane.heightProperty());
        appController.imageView.setPreserveRatio(false);
        appLayout.setCenter(mapPane);

        appLayout.setBottom(controlPanel);

        controlPanel.setStyle("--fx-background-color: white");

        isInitialized = true;
    }

    /**
     * Applies shared styling to hint labels.
     * @param label label to style
     * @return styled label
     */
    private Label applyHintLabelStyle(Label label) {
        label.setStyle("""
        -fx-background-color: rgba(0,0,0,0.55);
        -fx-text-fill: white;
        -fx-padding: 6 10 6 10;
        -fx-background-radius: 6;
        -fx-font-size: 13px;
    """);
        return label;
    }

    /**
     * @return root layout node
     */
    public BorderPane getLayout() {
        return appLayout;
    }

    /**
     * Holder for grouped UI controls.
     * @param buttonList buttons by key
     * @param buttonGroupList button groups by key
     * @param sliderList sliders by key
     */
    public record UserControlCollection(HashMap<String,Button> buttonList,
                                        HashMap<String,LabelledButtonGroup> buttonGroupList,
                                        HashMap<String,LabelledSlider> sliderList) { }

    /**
     * @return status label for current user mode
     */
    private Label labelUserModeIndicator() {
        Function<AppSettings.UserMode, String> labelText = (userMode) -> switch (userMode) {
            case menu -> "Menu";
            case select -> "Selection Mode";
            case explore -> "Exploration Mode";
        };

        Label label = new Label(labelText.apply(appSettings.getUserMode()));

        appSettings.userModeProperty().addListener((obs,oldVal,newVal) -> {
            label.setText(labelText.apply(appSettings.getUserMode()));
        });

        return label;
    }

    /**
     * @return label showing viewport debug state
     */
    private Label labelViewportIndicator() {
        Function<Boolean, String> labelText = (Boolean isViewportDebug) -> {
            if (isViewportDebug) {
                return "Viewport Debug ON [V]";
            }
            return "Viewport Debug OFF [V]";
        };

        return getLabel(labelText, appSettings.isViewportDebugProperty());
    }

    /**
     * @return label showing bounding box debug state
     */
    private Label labelBoundingBoxIndicator() {

        Function<Boolean, String> labelText = (Boolean isBoundingBox) -> {
            if (isBoundingBox) {
                return "BoundingBox Debug ON [B]";
            }
            return "BoundingBox Debug OFF [B]";
        };

        return getLabel(labelText, appSettings.isBoundingBoxDebugProperty());
    }

    /**
     * @return label showing pathfinding debug state
     */
    private Label labelPathfindingIndicator() {

        Function<Boolean, String> labelText = (Boolean isPathfindingDebug) -> {
            if (isPathfindingDebug) {
                return "Pathfinding Debug ON [P]";
            }
            return "Pathfinding Debug OFF [P]";
        };

        return getLabel(labelText, appSettings.isPathfindingDebugProperty());
    }

    /**
     * Builds a label that follows a boolean setting.
     * @param labelText mapping from value to label text
     * @param isDebug property to observe
     * @return bound label
     */
    private Label getLabel(Function<Boolean, String> labelText, ObjectProperty<Boolean> isDebug) {
        Label label = new Label(labelText.apply(isDebug.get()));
        label.setStyle(isDebug.get() ? "-fx-font-weight: bold;" : "");

        isDebug.addListener((obs, oldVal, newVal) -> {
            label.setText(labelText.apply(isDebug.get()));
            label.setStyle(isDebug.get() ? "-fx-font-weight: bold;" : "");
        });

        return label;
    }

    /**
     * @return label for pathfinding start node
     */
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

    /**
     * @return label for pathfinding end node
     */
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

    /**
     * @return button that toggles map state
     */
    private Button buttonToggleMapState() {
        Function<AppSettings.MapState,String> buttonLabel = (AppSettings.MapState state) -> switch (state) {
            case osm -> "Show elevation map";
            default -> "Show normal map";
        };

        Button button = new Button(buttonLabel.apply(appSettings.getMapState()));
        button.setOnAction(e -> {
            AppSettings.MapState newState = appSettings.getMapState() == AppSettings.MapState.osm
                    ? AppSettings.MapState.elevation
                    : AppSettings.MapState.osm;
            appSettings.setMapState(newState);
            button.setText(buttonLabel.apply(newState));
            appController.handleDraw();
        });

        return button;
    }

    /**
     * @return button that toggles height curve visibility
     */
    private Button buttonToggleHeightCurves() {
        Function<Boolean,String> buttonLabel = (Boolean showHeightCurve) -> {
            if (showHeightCurve) {
                return "Hide height curves";
            }
            return "Show height curves";
        };

        Button button = new Button(buttonLabel.apply(appSettings.getIsHeightCurvesVisible()));
        button.setOnAction(e -> {
            boolean nowVisible = !appSettings.getIsHeightCurvesVisible();
            appSettings.setIsHeightCurvesVisible(nowVisible);
            button.setText(buttonLabel.apply(nowVisible));
            appController.handleDraw();
        });

        return button;
    }

    /**
     * @param maxHeight maximum height for the slider
     * @return labeled slider for sea level
     */
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

    /**
     * @return labeled zoom control buttons
     */
    private LabelledButtonGroup buttonGroupZoomControl() {
        Label label = new Label();
        appController.getSuperAffine().scaleX().addListener((obs,oldVal,newVal) -> {
            label.setText(String.format("Zoom: %.1fx",appController.getZoomLevel()));
        });

        Button buttonZoomOut = new Button("-");
        buttonZoomOut.setOnAction(e -> appController.handleZoom(1/1.5, appController.getWIDTH() / 2.0, appController.getHEIGHT() / 2.0));

        Button buttonZoomIn = new Button("+");
        buttonZoomIn.setOnAction(e -> appController.handleZoom(1.5, appController.getWIDTH() / 2.0, appController.getHEIGHT() / 2.0));

        return new LabelledButtonGroup(label,buttonZoomOut,buttonZoomIn);
    }

    /**
     * Labeled slider tuple.
     * @param label label node
     * @param slider slider node
     */
    private record LabelledSlider(Label label, Slider slider) {
        /**
         * @return nodes in display order
         */
        public Node[] toNodes() {
            Node[] nodes = new Node[2];
            nodes[0] = label;
            nodes[1] = slider;
            return nodes;
        }
    };

    /**
     * Labeled button group tuple.
     * @param label label node
     * @param buttons button nodes
     */
    private record LabelledButtonGroup(Label label, Button... buttons) {
        /**
         * @return nodes in display order
         */
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
}
