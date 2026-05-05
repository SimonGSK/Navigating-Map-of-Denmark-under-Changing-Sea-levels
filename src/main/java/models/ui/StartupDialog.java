package models.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class StartupDialog {
    public record MapChoice(String osmPath, String hcPath, String binPath) {}

    private MapChoice result = null;

    public MapChoice show() {
        Stage dialog = new Stage();
        dialog.setTitle("Select a map");
        dialog.setResizable(false);

        Button bornholm = new Button("Bornholm");
        Button samso = new Button("Samsø");
        Button tuna = new Button("Tunø");

        bornholm.setMaxWidth(Double.MAX_VALUE);
        samso.setMaxWidth(Double.MAX_VALUE);
        tuna.setMaxWidth(Double.MAX_VALUE);

        bornholm.setOnAction(e -> {
            result = new MapChoice("bornholm/bornholm.osm", "bornholm/bornholm.hc", "/data/bornholm/bornholm.bin");
            dialog.close();
        });
        samso.setOnAction(e -> {
            result = new MapChoice("samso/samso.osm", "samso/samso.hc", "/data/samso/samso.bin");
            dialog.close();
        });
        tuna.setOnAction(e -> {
            result = new MapChoice("tuna/tuna.osm", "tuna/tuna.hc", "/data/tuna/tuna.bin");
            dialog.close();
        });

        Label orLabel = new Label("- or load your own files -");
        orLabel.setStyle("-fx-text-fill: gray;");

        Button customButton = new Button("Browse for .osm and .hc files...");
        customButton.setMaxWidth(Double.MAX_VALUE);

        final File[] osmFile = {null};
        final File[] hcFile = {null};

        customButton.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select .osm file");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("OSM files", "*.osm"));
            osmFile[0] = fc.showOpenDialog(dialog);

            if (osmFile[0] != null) {
                fc.setTitle("Select .hc file");
                fc.getExtensionFilters().clear();
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("HC files", "*.hc"));
                hcFile[0] = fc.showOpenDialog(dialog);
            }
            if (osmFile[0] != null && hcFile[0] != null) {
                result = new MapChoice(osmFile[0].getAbsolutePath(), hcFile[0].getAbsolutePath(), null);
                dialog.close();
            }
        });
        VBox layout = new VBox(10, new Label("Choose a map to load"), bornholm, samso, tuna, orLabel, customButton);
        layout.setPadding(new Insets(20));
        layout.setPrefWidth(280);

        dialog.setScene(new Scene(layout));
        dialog.showAndWait();

        return result;


    }
}
