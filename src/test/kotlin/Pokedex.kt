import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

object Pokedex {

    private val pokemon: Map<String, Pokemon> by lazy {
        val pages = UnitedScraper.getPokedexPages(12, 745)
        val parser = SpeciesParser()
        val jsonObject = parser.parse(pages)
        val moshi = Moshi.Builder().build()
        val type = Types.newParameterizedType(Map::class.java, String::class.java, Pokemon::class.java)
        val adapter: JsonAdapter<Map<String, Pokemon>> = moshi.adapter(type)
        adapter.fromJson(jsonObject.toString())!!
    }

    fun Get(name: String) = pokemon[name]
}