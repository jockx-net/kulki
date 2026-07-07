package net.jockx.kulki.model;

import net.jockx.kulki.util.AppConfigDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreBoard {
    private static final Logger log = LoggerFactory.getLogger(ScoreBoard.class);
    private static final int MAX_ENTRIES = 10;
    private static final String SCORES_FILE = "kulki.scores";

    private final List<Entry> entries;
    private final Path scoresPath;
    private Entry highlightedEntry;

    public record Entry(String name, int score) implements Comparable<Entry> {
        @Override
        public int compareTo(Entry o) {
            return Integer.compare(o.score, score);
        }
    }

    public ScoreBoard() {
        entries = new ArrayList<>();
        scoresPath = AppConfigDir.get().resolve(SCORES_FILE);
        load();
    }

    public List<Entry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public boolean isHighScore(int score) {
        return entries.size() < MAX_ENTRIES || score > entries.getLast().score();
    }

    public void addAndHighlight(String name, int score) {
        Entry entry = new Entry(name, score);
        entries.add(entry);
        entries.sort(null);
        if (entries.size() > MAX_ENTRIES) {
            entries.subList(MAX_ENTRIES, entries.size()).clear();
        }
        highlightedEntry = null;
        for (Entry e : entries) {
            if (e == entry) {
                highlightedEntry = e;
                break;
            }
        }
        save();
    }

    public void highlightOnly(String name, int score) {
        highlightedEntry = new Entry(name, score);
    }

    public Entry getHighlightedEntry() {
        return highlightedEntry;
    }

    public void clearHighlight() {
        highlightedEntry = null;
    }

    private void load() {
        if (!Files.exists(scoresPath)) {
            createDefaults();
            save();
            return;
        }
        try {
            List<String> lines = Files.readAllLines(scoresPath);
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    entries.add(new Entry(parts[0], Integer.parseInt(parts[1])));
                }
            }
            entries.sort(null);
        } catch (IOException e) {
            log.warn("Failed to load scores file, starting with empty board", e);
        }
    }

    private void createDefaults() {
        int[] scores = {1000, 995, 800, 650, 500, 350, 250, 150, 100, 50};
        String[] names = {"Bob", "ACE", "Flynn", "Cris", "Dani", "Evan", "Faye", "Gina", "Hugo", "AAA"};
        for (int i = 0; i < scores.length; i++) {
            entries.add(new Entry(names[i], scores[i]));
        }
    }

    private void save() {
        try {
            Files.createDirectories(scoresPath.getParent());
            List<String> lines = entries.stream()
                    .map(e -> e.name() + "|" + e.score())
                    .toList();
            Files.write(scoresPath, lines);
        } catch (IOException e) {
            log.warn("Failed to save scores file", e);
        }
    }

}
