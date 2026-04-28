package models.ui;

import javafx.scene.control.Button;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class UserInterface {
    private final AppController appController;
    private Map<String,Button> buttonMap = new HashMap<>();
    private MapState mapState = MapState.osm;
    private UserMode userMode = UserMode.menu;

    public enum MapState {
        osm,
        heightCurves,
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

    public UserInterface(AppController appController) {
        this.appController = appController;
        init();
    }

    private void init() {
        buttonMap.put("MapToggle",mapStateToggleButton());
    }

    private Button mapStateToggleButton() {
        Function<MapState,String> buttonLabel = (MapState mapState) -> switch (mapState) {
            case osm -> "Show elevation map";
            default -> "Show normal map";
        };

        Button button = new Button(buttonLabel.apply(mapState));
        button.setOnAction(e -> {
            switch (mapState) {
                case osm -> mapState = MapState.heightCurves;
                default -> mapState = MapState.osm;
            };
            button.setText(buttonLabel.apply(mapState));
            appController.update();
        });

        return button;
    }

    private void getMapStateToggleButtonLable() {

    }
}
