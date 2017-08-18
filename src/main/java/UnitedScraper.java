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
        parsePokedex();
    }

    static private List<String> getPokedexPages() throws IOException {
        return getPokedexPages(12, 745);
    }


    static private List<String> getPokedexPages(int startPageNumber, int endPageNumber) throws IOException {
        final String pokedexPath = "src\\main\\resources\\pokedex.pdf";
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

    static private void parsePokedex() throws IOException {
        List<String> speciesPages = getPokedexPages();
        SpeciesParser speciesParser = new SpeciesParser();
        JSONObject speciesObject = speciesParser.parse(speciesPages);
        String speciesData = speciesObject.toString(2);
        List<String> lines = Collections.singletonList(speciesData);
        Path file = Paths.get("out/species.json");
        Files.write(file, lines, Charset.forName("UTF-8"));
    }
}
