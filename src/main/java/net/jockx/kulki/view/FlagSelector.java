package net.jockx.kulki.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.function.Consumer;

public final class FlagSelector {
    public record Lang(Locale locale, String imagePath) {
    }

    private static final Lang[] LANGUAGES = {
        new Lang(new Locale("en"), "flag-gb.png"),
        new Lang(new Locale("pl"), "flag-pl.png"),
        new Lang(new Locale("es"), "flag-es.png"),
        new Lang(new Locale("de"), "flag-de.png"),
        new Lang(new Locale("zh"), "flag-cn.png"),
        new Lang(new Locale("ja"), "flag-jp.png"),
        new Lang(new Locale("pt", "BR"), "flag-br.png"),
        new Lang(new Locale("uk"), "flag-ua.png"),
    };

    public static HBox createFlagSelector(Consumer<Locale> onLanguageChange) {
        HBox flags = new HBox(4);
        flags.setAlignment(Pos.CENTER);
        for (Lang lang : LANGUAGES) {
            try (InputStream is = FlagSelector.class.getClassLoader().getResourceAsStream(lang.imagePath())) {
                ImageView iv = new ImageView(new Image(is));
                iv.setFitHeight(12);
                iv.setPreserveRatio(true);
                Button fb = new Button();
                fb.setGraphic(iv);
                fb.getStyleClass().add("flag-button");
                fb.setFocusTraversable(false);
                fb.setOnAction(e -> onLanguageChange.accept(lang.locale()));
                flags.getChildren().add(fb);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to load flag: " + lang.imagePath(), ex);
            }
        }
        return flags;
    }
}
