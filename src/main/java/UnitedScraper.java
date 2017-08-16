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

    static private List<String> getPages(PDDocument document, int startPageNumber, int endPageNumber) throws IOException {
        List<String> pages = new ArrayList<String>();
        PDFTextStripper stripper = new PDFTextStripper();
        for (int pageNumber = startPageNumber; pageNumber < endPageNumber; pageNumber++) {
            if (pageNumber == 682) {
                continue;
            }
            stripper.setStartPage(pageNumber);
            stripper.setEndPage(pageNumber);
            String page = stripper.getText(document);
            pages.add(page);
        }
        return pages;
    }

    static private void parsePokedex() throws IOException {
        final String pokedexPath = "src\\main\\resources\\pokedex.pdf";
        File file = new File(pokedexPath);
        PDDocument pokedex = PDDocument.load(file);
//        12 - 745
        List<String> speciesPages = getPages(pokedex, 12, 745);
        SpeciesParser speciesParser = new SpeciesParser();
        for (String speciesPage: speciesPages) {
            speciesParser.parse(speciesPage);
        }
        pokedex.close();
    }
}
