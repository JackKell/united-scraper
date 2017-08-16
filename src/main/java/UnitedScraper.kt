import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper

import java.io.File
import java.io.IOException
import java.util.ArrayList

object UnitedScraper {
    @Throws(IOException::class)
    @JvmStatic fun main(args: Array<String>) {
        parsePokedex()
    }

    @Throws(IOException::class)
    private fun getPages(document: PDDocument, startPageNumber: Int, endPageNumber: Int): List<String> {
        val pages = ArrayList<String>()
        val stripper = PDFTextStripper()
        for (pageNumber in startPageNumber..endPageNumber - 1) {
            if (pageNumber == 682) {
                continue
            }
            stripper.startPage = pageNumber
            stripper.endPage = pageNumber
            val page = stripper.getText(document)
            pages.add(page)
        }
        return pages
    }

    @Throws(IOException::class)
    private fun parsePokedex() {
        val pokedexPath = "src\\main\\resources\\pokedex.pdf"
        val file = File(pokedexPath)
        val pokedex = PDDocument.load(file)
        //        12 - 745
        val speciesPages = getPages(pokedex, 12, 745)
        val speciesParser = SpeciesParser()
        for (speciesPage in speciesPages) {
            speciesParser.parse(speciesPage)
        }
        pokedex.close()
    }
}
