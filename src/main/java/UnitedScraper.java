import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnitedScraper {
    public static void main(String[] args) throws IOException {
        parseSpecies();
        parseAbilities();
        parseMoves();
        parseCapabilities();
        parseEdges();
    }

    @SuppressWarnings("WeakerAccess")
    static List<String> getSpeciePages() throws IOException {
        return getSpeciePages(12, 745);
    }

    @SuppressWarnings("WeakerAccess")
    static List<String> getSpeciePages(int startPageNumber, int endPageNumber) throws IOException {
        final String pokedexPath = "src" + File.separatorChar + "main" + File.separatorChar + "resources" + File.separatorChar + "pokedex.pdf";
        File file = new File(pokedexPath);
        PDDocument pokedex = PDDocument.load(file);
        List<String> pages = new ArrayList<>();
        PDFTextStripper stripper = new PDFTextStripper();
        // Start at page 12 with Bulbasaur
        for (int pageNumber = startPageNumber; pageNumber <= endPageNumber; pageNumber++) {
            // Skip the legendaries page
            if (pageNumber == 682) {
                continue;
            }
            stripper.setStartPage(pageNumber);
            stripper.setEndPage(pageNumber);
            String page = stripper.getText(pokedex);
            pages.add(page);
        }
        pokedex.close();
        return pages;
    }

    static private String getCoreText(int start, int end) throws IOException {
        final String corePath = "src" + File.separatorChar + "main" + File.separatorChar + "resources" + File.separatorChar + "core.pdf";
        final File file = new File(corePath);
        final PDDocument core = PDDocument.load(file);
        final PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(start);
        stripper.setEndPage(end);
        final String text = stripper.getText(core);
        core.close();
        return text;
    }

    static private String getMovesText() throws IOException {
        return getCoreText(346, 435);
    }

    static private String getAbilitiesText() throws IOException {
        return getCoreText(311, 336);
    }

    static private String getCapabilitiesText() throws IOException {
        return getCoreText(303, 308);
    }

    static private String getEdgesText() throws IOException {
        return getCoreText(52, 56);
    }

    static private void saveJSONObject(JSONObject jsonObject, String path) throws IOException {
        final String objectData = jsonObject.toString(2);
        final List<String> lines = Collections.singletonList(objectData);
        final Path file = Paths.get(path);
        Files.write(file, lines, Charset.forName("UTF-8"));
    }

    static private void parseSpecies() throws IOException {
        final List<String> speciesPages = getSpeciePages();
        final SpeciesParser speciesParser = new SpeciesParser();
        final JSONObject speciesObject = speciesParser.parse(speciesPages);
        saveJSONObject(speciesObject, "out/species.json");
    }

    static private void parseAbilities() throws IOException {
        final String abilitiesText = getAbilitiesText();
        final AbilitiesParser abilitiesParser = new AbilitiesParser();
        final JSONObject abilitiesObject = abilitiesParser.parse(abilitiesText);
        saveJSONObject(abilitiesObject, "out/abilities.json");
    }

    static private void parseMoves() throws IOException {
        final String movesText = getMovesText();
        final MovesParser movesParser = new MovesParser();
        final JSONObject movesObject = movesParser.parse(movesText);
        saveJSONObject(movesObject, "out/moves.json");
    }

    static private void parseCapabilities() throws IOException {
        final String capabilitiesText = getCapabilitiesText();
        final CapabilitiesParser capabilitiesParser = new CapabilitiesParser();
        final JSONObject capabilitiesObject = capabilitiesParser.parse(capabilitiesText);
        saveJSONObject(capabilitiesObject, "out/capabilities.json");
    }

    static private void parseEdges() throws IOException {
        final String edgesText = getEdgesText();
        final EdgesParser edgesParser = new EdgesParser();
        final JSONObject edgesObject = edgesParser.parse(edgesText);
        saveJSONObject(edgesObject, "out/edges.json");
    }
}
