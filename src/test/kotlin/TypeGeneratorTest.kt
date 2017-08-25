import generator.Type
import generator.TypeGenerator
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.json.JSONObject
import kotlin.test.assertEquals

object TypeGeneratorTest: Spek({
    describe("a type generator") {
        val generator = TypeGenerator()

        it("should map a single type (Grass) with only its appropriate attacking advantages/disadvantages") {
            val type = Type("Grass",
                    superEffectiveAgainst = hashSetOf("Water", "Ground", "Rock"),
                    notVeryEffectiveAgainst = hashSetOf("Fire", "Grass", "Poison", "Flying", "Bug", "Dragon", "Steel"))
            generator.addType(type)
            val json = generator.generate()
            val types = json.get("types") as JSONObject
            val grass = types["Grass"] as JSONObject
            val values = grass["attacking"] as JSONObject
            assertEquals(0.5f, values["Fire"])
            assertEquals(1.5f, values["Water"])
            assertEquals(0.5f, values["Grass"])
            assertEquals(0.5f, values["Poison"])
            assertEquals(1.5f, values["Ground"])
            assertEquals(0.5f, values["Flying"])
            assertEquals(0.5f, values["Bug"])
            assertEquals(1.5f, values["Rock"])
            assertEquals(0.5f, values["Dragon"])
            assertEquals(0.5f, values["Steel"])
        }
    }
})