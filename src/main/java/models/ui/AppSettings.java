package models.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class AppSettings {
    private static AppSettings INSTANCE;
    private final ObjectProperty<UserMode> userMode = new SimpleObjectProperty<>(UserMode.menu);
    private final ObjectProperty<MapState> mapState = new SimpleObjectProperty<>(MapState.osm);
    private final ObjectProperty<Boolean> isHeightCurvesVisible = new SimpleObjectProperty<>(false);

    private AppSettings() {};

    public static AppSettings getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AppSettings();
        }
        return INSTANCE;
    }

    // Enums
    public enum MapState {
        osm,
        elevation,
    }

    public enum UserMode {
        menu,
        explore,
        select
    }

    // Getters & setters
    public MapState getMapState() {
        return mapState.get();
    }

    public void setMapState(MapState mapState) {
        this.mapState.set(mapState);
    }

    public UserMode getUserMode() {
        return userMode.get();
    }

    public void setUserMode(UserMode userMode) {
        this.userMode.set(userMode);
    }

    public Boolean getIsHeightCurvesVisible() {
        return isHeightCurvesVisible.get();
    }

    public void setIsHeightCurvesVisible(boolean b) {
        isHeightCurvesVisible.set(b);
    }

    // ObjectProperty-getters
    public ObjectProperty<MapState> mapStateProperty() {
        return mapState;
    }

    public ObjectProperty<UserMode> userModeProperty() {
        return userMode;
    }

    public ObjectProperty<Boolean> isHeightCurvesVisibleProperty() {
        return isHeightCurvesVisible;
    }
}
