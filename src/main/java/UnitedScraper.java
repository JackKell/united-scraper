import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UnitedScraper {
    public static void main(String[] args) throws IOException {
        parsePokedex();
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
        List<String> speciesPages = getPokedexPages(12, 745);
        SpeciesParser speciesParser = new SpeciesParser();
        for (String speciesPage: speciesPages) {
            speciesParser.parse(speciesPage);
        }
    }
}
