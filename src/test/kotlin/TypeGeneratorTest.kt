import generator.Type
import generator.TypeGenerator
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.json.JSONArray
import org.json.JSONObject
import kotlin.test.assertEquals
import kotlin.test.assertTrue

object TypeGeneratorTest: Spek({
    describe("a type generator") {
        val generator = TypeGenerator()

        it("should map a single type (Ghost) with only its appropriate attacking advantages/disadvantages") {
            val type = Type("Ghost",
                    superEffectiveAgainst = hashSetOf("Psychic", "Ghost"),
                    notVeryEffectiveAgainst = hashSetOf("Dark"),
                    ineffectiveAgainst = hashSetOf("Normal"))
            generator.addType(type)
            val json = generator.generate()
            val types = json["types"] as JSONObject
            val grass = types["Ghost"] as JSONObject
            val values = grass["attacking"] as JSONObject
            assertEquals(1.5f, values["Psychic"])
            assertEquals(1.5f, values["Ghost"])
            assertEquals(0.5f, values["Dark"])
            assertEquals(0.0f, values["Normal"])
            assertEquals(4, values.length())
        }

        it("should map a single type (Ghost) with only its appropriate defending advantages/disadvantages") {
            val type = Type("Ghost",
                    immuneTo= hashSetOf("Normal", "Fighting"),
                    resistantTo = hashSetOf("Poison", "Bug"),
                    weakTo = hashSetOf("Ghost", "Dark"))
            generator.addType(type)
            val json = generator.generate()
            val types = json["types"] as JSONObject
            val grass = types["Ghost"] as JSONObject
            val values = grass["defending"] as JSONObject
            assertEquals(0.0f, values["Normal"])
            assertEquals(0.0f, values["Fighting"])
            assertEquals(0.5f, values["Poison"])
            assertEquals(0.5f, values["Bug"])
            assertEquals(1.5f, values["Ghost"])
            assertEquals(1.5f, values["Dark"])
            assertEquals(6, values.length())
        }

        it("should map types explicitly and not override other types") {
            // this is just an example, real types should not contradict one another
            generator.addType(Type("Aluminum", superEffectiveAgainst = hashSetOf("Food")))
            generator.addType(Type("Food", immuneTo = hashSetOf("Aluminum")))
            val json = generator.generate()
            val types = json["types"] as JSONObject
            val aluminum = types["Aluminum"] as JSONObject
            val attacking = aluminum["attacking"] as JSONObject
            assertEquals(1.5f, attacking["Food"])
            val food = types["Food"] as JSONObject
            val defending = food["defending"] as JSONObject
            assertEquals(0.0f, defending["Aluminum"])
        }

        it("should map the effect immunities") {
            generator.addType(Type("Electric", effectImmunities = listOf("Paralyze")))
            val json = generator.generate()
            val types = json["types"] as JSONObject
            val electric = types["Electric"] as JSONObject
            val effectImmunities = electric["effectImmunities"] as JSONArray
            assertTrue(effectImmunities.contains("Paralyze"))
            assertEquals(1, effectImmunities.length())
        }

        it("should map the mechanic immunities") {
            generator.addType(Type("Grass", mechanicImmunities = listOf("Spore")))
            val json = generator.generate()
            val types = json["types"] as JSONObject
            val electric = types["Grass"] as JSONObject
            val mechanicImmunities = electric["mechanicImmunities"] as JSONArray
            assertTrue(mechanicImmunities.contains("Spore"))
            assertEquals(1, mechanicImmunities.length())
        }

        it("should map the immunity exceptions") {
            generator.addType(Type("Ghost", immunityExceptions = listOf("Social")))
            val json = generator.generate()
            val types = json["types"] as JSONObject
            val electric = types["Ghost"] as JSONObject
            val immunityExceptions = electric["immunityExceptions"] as JSONArray
            assertTrue(immunityExceptions.contains("Social"))
            assertEquals(1, immunityExceptions.length())
        }
    }
})