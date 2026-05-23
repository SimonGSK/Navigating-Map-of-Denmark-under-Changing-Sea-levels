package models.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Global UI settings with observable properties.
 */
public class AppSettings {
    private static AppSettings INSTANCE;

    private final ObjectProperty<UserMode> userMode = new SimpleObjectProperty<>(UserMode.menu);
    private final ObjectProperty<MapState> mapState = new SimpleObjectProperty<>(MapState.osm);
    private final ObjectProperty<Boolean> isHeightCurvesVisible = new SimpleObjectProperty<>(false);
    private final ObjectProperty<Boolean> isViewportDebug = new SimpleObjectProperty<>(false);
    private final ObjectProperty<Boolean> isBoundingBoxDebug = new SimpleObjectProperty<>(false);
    private final ObjectProperty<Boolean> isPathfindingDebug = new SimpleObjectProperty<>(false);

    private AppSettings() {};

    /**
     * @return singleton instance
     */
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

    /**
     * @return current map state
     */
    public MapState getMapState() {
        return mapState.get();
    }

    /**
     * Sets the current map state.
     * @param mapState new map state
     */
    public void setMapState(MapState mapState) {
        this.mapState.set(mapState);
    }

    /**
     * @return current user mode
     */
    public UserMode getUserMode() {
        return userMode.get();
    }

    /**
     * Sets the current user mode.
     * @param userMode new user mode
     */
    public void setUserMode(UserMode userMode) {
        this.userMode.set(userMode);
    }

    /**
     * @return true if height curves are visible
     */
    public Boolean getIsHeightCurvesVisible() {
        return isHeightCurvesVisible.get();
    }

    /**
     * Sets height curve visibility.
     * @param b true to show
     */
    public void setIsHeightCurvesVisible(boolean b) {
        isHeightCurvesVisible.set(b);
    }

    /**
     * @return user mode property
     */
    public ObjectProperty<UserMode> userModeProperty() {
        return userMode;
    }

    /**
     * @return viewport debug property
     */
    public ObjectProperty<Boolean> isViewportDebugProperty() {
        return isViewportDebug;
    }

    /**
     * @return bounding box debug property
     */
    public ObjectProperty<Boolean> isBoundingBoxDebugProperty() {
        return isBoundingBoxDebug;
    }

    /**
     * @return pathfinding debug property
     */
    public ObjectProperty<Boolean> isPathfindingDebugProperty() {
        return isPathfindingDebug;
    }

    /**
     * @return true if pathfinding debug is enabled
     */
    public boolean isPathfindingDebug() {
        return isPathfindingDebug.get();
    }

    /**
     * Sets pathfinding debug mode.
     * @param isPathfindingDebug true to enable
     */
    public void setPathfindingDebug(boolean isPathfindingDebug) {
        this.isPathfindingDebug.set(isPathfindingDebug);
    }

    /**
     * @return true if viewport debug is enabled
     */
    public boolean isViewportDebug() {
        return isViewportDebug.get();
    }

    /**
     * Sets viewport debug mode.
     * @param isViewportDebug true to enable
     */
    public void setViewportDebug(boolean isViewportDebug) {
        this.isViewportDebug.set(isViewportDebug);
    }

    /**
     * @return true if bounding box debug is enabled
     */
    public boolean isBoundingBoxDebug() {
        return isBoundingBoxDebug.get();
    }

    /**
     * Sets bounding box debug mode.
     * @param isBoundingBoxDebug true to enable
     */
    public void setBoundingBoxDebug(boolean isBoundingBoxDebug) {
        this.isBoundingBoxDebug.set(isBoundingBoxDebug);
    }
}
