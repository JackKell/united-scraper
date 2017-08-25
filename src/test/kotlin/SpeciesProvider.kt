import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import model.Specie
import parser.SpeciesParser

object SpeciesProvider {

    private val species: Map<String, Specie> by lazy {
        val pages = UnitedScraperApp.getSpeciePages()
        val parser = SpeciesParser()
        val jsonObject = parser.parse(pages)
        val moshi = Moshi.Builder().build()
        val type = Types.newParameterizedType(Map::class.java, String::class.java, Specie::class.java)
        val adapter: JsonAdapter<Map<String, Specie>> = moshi.adapter(type)
        adapter.fromJson(jsonObject.toString())!!
    }

    fun Get(name: String) = species[name]
}