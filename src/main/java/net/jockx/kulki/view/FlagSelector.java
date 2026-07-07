package net.jockx.kulki.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.Locale;
import java.util.function.Consumer;

public final class FlagSelector {
    public record Lang(Locale locale, String imagePath) {
    }

    private static final Lang[] LANGUAGES = {
        new Lang(Locale.of("en"), "flag-gb.png"),
        new Lang(Locale.of("pl"), "flag-pl.png"),
        new Lang(Locale.of("es"), "flag-es.png"),
        new Lang(Locale.of("de"), "flag-de.png"),
        new Lang(Locale.of("zh"), "flag-cn.png"),
        new Lang(Locale.of("ja"), "flag-jp.png"),
        new Lang(Locale.of("pt", "BR"), "flag-br.png"),
        new Lang(Locale.of("uk"), "flag-ua.png"),
    };

    public static HBox createFlagSelector(Consumer<Locale> onLanguageChange) {
        HBox flags = new HBox(4);
        flags.setAlignment(Pos.CENTER);
        for (Lang lang : LANGUAGES) {
            var is = FlagSelector.class.getClassLoader().getResourceAsStream(lang.imagePath());
            ImageView iv = new ImageView(new Image(is));
            iv.setFitHeight(12);
            iv.setPreserveRatio(true);
            Button fb = new Button();
            fb.setGraphic(iv);
            fb.getStyleClass().add("flag-button");
            fb.setFocusTraversable(false);
            fb.setOnAction(_ -> onLanguageChange.accept(lang.locale()));
            flags.getChildren().add(fb);
        }
        return flags;
    }
}
