package models.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class AppSettings {
    private static AppSettings INSTANCE;

    private final ObjectProperty<UserMode> userMode = new SimpleObjectProperty<>(UserMode.menu);
    private final ObjectProperty<MapState> mapState = new SimpleObjectProperty<>(MapState.osm);
    private final ObjectProperty<Boolean> isHeightCurvesVisible = new SimpleObjectProperty<>(false);
    private final ObjectProperty<Boolean> isViewportDebug = new SimpleObjectProperty<>(false);
    private final ObjectProperty<Boolean> isBoundingBoxDebug = new SimpleObjectProperty<>(false);
    private final ObjectProperty<Boolean> isPathfindingDebug = new SimpleObjectProperty<>(false);

    private AppSettings() {};

    public static AppSettings getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AppSettings();
        }
        return INSTANCE;
    }

    // --- Enums ---
    public enum MapState {
        osm,
        elevation,
    }

    public enum UserMode {
        menu,
        explore,
        select;
    }

    // --- Getters & Setters ---
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

    // --- ObjectProperty getters ---
    public ObjectProperty<MapState> mapStateProperty() {
        return mapState;
    }
    public ObjectProperty<UserMode> userModeProperty() {
        return userMode;
    }
    public ObjectProperty<Boolean> isHeightCurvesVisibleProperty() {
        return isHeightCurvesVisible;
    }
    public ObjectProperty<Boolean> isViewportDebugProperty() {
        return isViewportDebug;
    }
    public ObjectProperty<Boolean> isBoundingBoxDebugProperty() {
        return isBoundingBoxDebug;
    }
    public ObjectProperty<Boolean> isPathfindingDebugProperty() {
        return isPathfindingDebug;
    }

    // --- Debugging ---
    public boolean isPathfindingDebug() {
        return isPathfindingDebug.get();
    }
    public void setPathfindingDebug(boolean isPathfindingDebug) {
        this.isPathfindingDebug.set(isPathfindingDebug);
    }
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
}
