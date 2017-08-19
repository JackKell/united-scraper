import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertEquals

object PokemonTest: Spek({
    given("a pokemon") {
        val bulbasaur = Pokedex.Get("Bulbasaur")!!

        it("should be named Bulbasaur") {
            assertEquals("Bulbasaur", bulbasaur.name)
        }
    }
})