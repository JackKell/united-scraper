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

    private static String getCoreText(int pageNumber) throws IOException {
        return getCoreText(pageNumber, pageNumber);
    }

    private static String getCoreText(int start, int end) throws IOException {
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

    private static String getMovesText() throws IOException {
        return getCoreText(346, 435);
    }

    private static String getAbilitiesText() throws IOException {
        return getCoreText(311, 336);
    }

    private static String getCapabilitiesText() throws IOException {
        return getCoreText(303, 308);
    }

    private static String getEdgesText() throws IOException {
        return getCoreText(52, 56);
    }

    private static String getContestEffectsText() throws IOException {
        return getCoreText(266);
    }

    private static String getUsefulChartsText() throws IOException {
        return getCoreText(497);
    }

    private static String getNaturesText() throws IOException {
        return getCoreText(502);
    }

    private static void saveJSONObject(JSONObject jsonObject, String path) throws IOException {
        final String objectData = jsonObject.toString(2);
        final List<String> lines = Collections.singletonList(objectData);
        final Path file = Paths.get(path);
        Files.write(file, lines, Charset.forName("UTF-8"));
    }

    private static void parseTextBlockData(String data, TextBlockParser textBlockParser, String outputPath) throws IOException {
        final JSONObject jsonObject = textBlockParser.parse(data);
        saveJSONObject(jsonObject, outputPath);
    }

    private static void parseSpecies() throws IOException {
        final List<String> speciesPages = getSpeciePages();
        final SpeciesParser speciesParser = new SpeciesParser();
        final JSONObject speciesObject = speciesParser.parse(speciesPages);
        saveJSONObject(speciesObject, "out" + separator + "species.json");
    }

    private static void parseAbilities() throws IOException {
        parseTextBlockData(getAbilitiesText(), new AbilitiesParser(), "out" + separator + "abilities.json");
    }

    private static void parseMoves() throws IOException {
        parseTextBlockData(getMovesText(), new MovesParser(), "out" + separator + "moves.json");
    }

    private static void parseCapabilities() throws IOException {
        parseTextBlockData(getCapabilitiesText(), new CapabilitiesParser(), "out" + separator + "capabilities.json");
    }

    private static void parseEdges() throws IOException {
        parseTextBlockData(getEdgesText(), new EdgesParser(), "out" + separator + "edges.json");
    }

    private static void parseContestEffects() throws IOException {
        parseTextBlockData(getContestEffectsText(), new ContestEffectsParser(), "out" + separator + "contestEffects.json");
    }

    private static void parseExperience() throws IOException {
        parseTextBlockData(getUsefulChartsText(), new ExperienceParser(), "out" + separator + "experience.json");
    }

    private static void parseNatures() throws IOException {
        parseTextBlockData(getNaturesText(), new NaturesParser(), "out" + separator + "natures.json");
    }

    private static void parseDamageBases() throws IOException {
        parseTextBlockData(getUsefulChartsText(), new DamageBaseParser(), "out" + separator + "damageBases.json");
    }

    private static void generateTypes() throws IOException {
        JSONObject jsonObject = new TypeGenerator().generate();
        saveJSONObject(jsonObject, "out" + separator + "types.json");
    }
}
