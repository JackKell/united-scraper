import generator.TypeGenerator;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.JSONObject;
import parser.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.io.File.separator;

public class UnitedScraperApp {
    public static void main(String[] args) throws IOException {
        parseSpecies();
        parseAbilities();
        parseMoves();
        parseCapabilities();
        parseEdges();
        parseContestEffects();
        parseExperience();
        parseNatures();
        parseDamageBases();
        generateTypes();
    }

    @SuppressWarnings("WeakerAccess")
    static List<String> getSpeciePages() throws IOException {
        return getSpeciePages(12, 745);
    }

    @SuppressWarnings("WeakerAccess")
    static List<String> getSpeciePages(int startPageNumber, int endPageNumber) throws IOException {
        final String speciesDocumentPath = "src" + separator + "main" + separator + "resources" + separator + "species.pdf";
        File file = new File(speciesDocumentPath);
        PDDocument speciesDocument = PDDocument.load(file);
        List<String> pages = new ArrayList<>();
        PDFTextStripper stripper = new PDFTextStripper();
        for (int pageNumber = startPageNumber; pageNumber <= endPageNumber; pageNumber++) {
            // Skip the legendary description page
            if (pageNumber == 682) {
                continue;
            }
            stripper.setStartPage(pageNumber);
            stripper.setEndPage(pageNumber);
            String page = stripper.getText(speciesDocument);
            pages.add(page);
        }
        speciesDocument.close();
        return pages;
    }

    static private String getCoreText(int pageNumber) throws IOException {
        return getCoreText(pageNumber, pageNumber);
    }

    static private String getCoreText(int start, int end) throws IOException {
        final String corePath = "src" + separator + "main" + separator + "resources" + separator + "core.pdf";
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

    static private String getContestEffectsText() throws IOException {
        return getCoreText(266);
    }

    static private String getUsefulChartsText() throws IOException {
        return getCoreText(497);
    }

    static private String getNaturesText() throws IOException {
        return getCoreText(502);
    }

    static private void saveJSONObject(JSONObject jsonObject, String path) throws IOException {
        final String objectData = jsonObject.toString(2);
        final List<String> lines = Collections.singletonList(objectData);
        final Path file = Paths.get(path);
        Files.write(file, lines, Charset.forName("UTF-8"));
    }

    static private void parseTextBlockData(String data, TextBlockParser textBlockParser, String outputPath) throws IOException {
        final JSONObject jsonObject = textBlockParser.parse(data);
        saveJSONObject(jsonObject, outputPath);
    }

    static private void parseSpecies() throws IOException {
        final List<String> speciesPages = getSpeciePages();
        final SpeciesParser speciesParser = new SpeciesParser();
        final JSONObject speciesObject = speciesParser.parse(speciesPages);
        saveJSONObject(speciesObject, "out" + separator + "species.json");
    }

    static private void parseAbilities() throws IOException {
        parseTextBlockData(getAbilitiesText(), new AbilitiesParser(), "out" + separator + "abilities.json");
    }

    static private void parseMoves() throws IOException {
        parseTextBlockData(getMovesText(), new MovesParser(), "out" + separator + "moves.json");
    }

    static private void parseCapabilities() throws IOException {
        parseTextBlockData(getCapabilitiesText(), new CapabilitiesParser(), "out" + separator + "capabilities.json");
    }

    static private void parseEdges() throws IOException {
        parseTextBlockData(getEdgesText(), new EdgesParser(), "out" + separator + "edges.json");
    }

    static private void parseContestEffects() throws IOException {
        parseTextBlockData(getContestEffectsText(), new ContestEffectsParser(), "out" + separator + "contestEffects.json");
    }

    static private void parseExperience() throws IOException {
        parseTextBlockData(getUsefulChartsText(), new ExperienceParser(), "out" + separator + "experience.json");
    }

    static private void parseNatures() throws IOException {
        parseTextBlockData(getNaturesText(), new NaturesParser(), "out" + separator + "natures.json");
    }

    static private void parseDamageBases() throws IOException {
        parseTextBlockData(getUsefulChartsText(), new DamageBaseParser(), "out" + separator + "damageBases.json");
    }

        static private void generateTypes() throws IOException {
        JSONObject jsonObject = new TypeGenerator().generate();
        saveJSONObject(jsonObject, "out" + separator + "types.json");
    }
}
