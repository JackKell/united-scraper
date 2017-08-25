import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

object SpecieTest : Spek({
    given("bulbasaur") {
        val bulbasaur = SpeciesProvider.Get("Bulbasaur")!!

        it("should be named Bulbasaur") {
            assertEquals("Bulbasaur", bulbasaur.name)
        }

        describe("base stats") {
            it("should have 5 HP") {
                assertEquals(5, bulbasaur.stats.hp)
            }
            it("should have 5 Attack") {
                assertEquals(5, bulbasaur.stats.attack)
            }
            it("should have 5 Defense") {
                assertEquals(5, bulbasaur.stats.defense)
            }
            it("should have 7 Special Attack") {
                assertEquals(7, bulbasaur.stats.specialAttack)
            }
            it("should have 7 Special Defense") {
                assertEquals(7, bulbasaur.stats.specialDefense)
            }
            it("should have 5 Speed") {
                assertEquals(5, bulbasaur.stats.speed)
            }
        }

        it("should be grass and poison type") {
            assertTrue(bulbasaur.types.contains("Grass"))
            assertTrue(bulbasaur.types.contains("Poison"))
        }

        it("should have the confidence and photosynthesis basic abilities") {
            assertTrue(bulbasaur.basicAbilities.contains("Confidence"))
            assertTrue(bulbasaur.basicAbilities.contains("Photosynthesis"))
        }

        it("should have chlorophyll and leaf guard as advanced abilities") {
            assertTrue(bulbasaur.advancedAbilities.contains("Chlorophyll"))
            assertTrue(bulbasaur.advancedAbilities.contains("Leaf Guard"))
        }

        it("should have Courage as a high ability") {
            assertTrue(bulbasaur.highAbilities.contains("Courage"))
        }

        describe("evolution") {
            it("should be a stage 1 pokemon") {
                assertEquals(1, bulbasaur.stage)
            }
            it("should not evolve from anything") {
                assertNull(bulbasaur.evolvesFrom)
            }
            it("should evolve to Ivysaur") {
                assertTrue(bulbasaur.evolvesTo.containsKey("Ivysaur"))
            }
            it("should trigger evolution by leveling up") {
                assertEquals("level", bulbasaur.evolvesTo["Ivysaur"]?.trigger)
            }
            it("should evolve at level 15") {
                assertEquals(15, bulbasaur.evolvesTo["Ivysaur"]?.conditions?.level)
            }
        }

        describe("height") {
            it("should be small") {
                assertEquals("Small", bulbasaur.height.size)
            }
            it("should be .7m tall") {
                assertEquals(.7f, bulbasaur.height.m)
            }
            it("""should be 2' 4"""") {
                assertEquals("""2' 4"""", bulbasaur.height.ft)
            }
        }

        describe("weight") {
            it("should be 15.2 lbs") {
                assertEquals(15.2f, bulbasaur.weight.lbs)
            }
            it("should be 6.9kg") {
                assertEquals(6.9f, bulbasaur.weight.kg)
            }
            it("should be in weight class 1") {
                assertEquals(1, bulbasaur.weight.`class`)
            }
        }

        describe("breeding information") {
            it("should be gendered") {
                assertTrue(bulbasaur.genderRatio.gendered == true)
            }
            it("should have a male percent ratio of 87.5%") {
                assertEquals(87.5f, bulbasaur.genderRatio.male)
            }
            it("should have a female percent ratio of 12.5%") {
                assertEquals(12.5f, bulbasaur.genderRatio.female)
            }
            it("should be in the monster and plant egg groups") {
                assertTrue(bulbasaur.eggGroups.contains("Monster"))
                assertTrue(bulbasaur.eggGroups.contains("Plant"))
            }
            it("should have an average hatch rate of 10") {
                assertEquals(10, bulbasaur.averageHatchRate)
            }
        }

        it("should have a herbivore and phototroph diet") {
            assertTrue(bulbasaur.diets.contains("Herbivore"))
            assertTrue(bulbasaur.diets.contains("Phototroph"))
        }

        it("should live in the forest, grassland, and rainforest") {
            assertTrue(bulbasaur.habitats.contains("Forest"))
            assertTrue(bulbasaur.habitats.contains("Grassland"))
            assertTrue(bulbasaur.habitats.contains("Rainforest"))
        }

        describe("capabilities") {
            it("should have an overland of 5") {
                assertEquals(5, bulbasaur.capabilities.overland)
            }
            it("should have a swim of 3") {
                assertEquals(3, bulbasaur.capabilities.swim)
            }
            it("should have a long jump of 0") {
                assertEquals(0, bulbasaur.capabilities.jump.long)
            }
            it("should have a high jump of 2") {
                assertEquals(2, bulbasaur.capabilities.jump.high)
            }
            it("should have a power of 2") {
                assertEquals(2, bulbasaur.capabilities.power)
            }
            it("should be able to naturewalk in grassland and forest areas") {
                assertTrue(bulbasaur.capabilities.naturewalk.contains("Grassland"))
                assertTrue(bulbasaur.capabilities.naturewalk.contains("Forest"))
            }
            it("should have underdog as a special capability") {
                assertTrue(bulbasaur.capabilities.specialCapabilities.contains("Underdog"))
            }
        }

        describe("skills") {
            it("should have 3d6+2 athletics") {
                val athletics = bulbasaur.skills["athletics"]!!
                assertEquals("3d6+2", athletics.value)
                assertEquals(3, athletics.numberOfDice)
                assertEquals(6, athletics.diceFaces)
                assertEquals(2, athletics.buff)
            }
            it("should have 2d6 acrobatics") {
                val acrobatics = bulbasaur.skills["acrobatics"]!!
                assertEquals("2d6", acrobatics.value)
                assertEquals(2, acrobatics.numberOfDice)
                assertEquals(6, acrobatics.diceFaces)
                assertEquals(0, acrobatics.buff)
            }
            it("should have 2d6 combat") {
                val combat = bulbasaur.skills["combat"]!!
                assertEquals("2d6", combat.value)
                assertEquals(2, combat.numberOfDice)
                assertEquals(6, combat.diceFaces)
                assertEquals(0, combat.buff)
            }
            it("should have 2d6 stealth") {
                val stealth = bulbasaur.skills["stealth"]!!
                assertEquals("2d6", stealth.value)
                assertEquals(2, stealth.numberOfDice)
                assertEquals(6, stealth.diceFaces)
                assertEquals(0, stealth.buff)
            }
            it("should have 2d6 perception") {
                val perception = bulbasaur.skills["perception"]!!
                assertEquals("2d6", perception.value)
                assertEquals(2, perception.numberOfDice)
                assertEquals(6, perception.diceFaces)
                assertEquals(0, perception.buff)
            }
            it("should have 2d6+1 focus") {
                val focus = bulbasaur.skills["focus"]!!
                assertEquals("2d6+1", focus.value)
                assertEquals(2, focus.numberOfDice)
                assertEquals(6, focus.diceFaces)
                assertEquals(1, focus.buff)
            }
        }

        describe("level up moves") {
            it("should learn tackle at level 1") {
                assertTrue(bulbasaur.levelUpMoves["1"]!!.contains("Tackle"))
            }
            it("should learn growl at level 3") {
                assertTrue(bulbasaur.levelUpMoves["3"]!!.contains("Growl"))
            }
            it("should learn leech seed at level 7") {
                assertTrue(bulbasaur.levelUpMoves["7"]!!.contains("Leech Seed"))
            }
            it("should learn vine whip at level 9") {
                assertTrue(bulbasaur.levelUpMoves["9"]!!.contains("Vine Whip"))
            }
            it("should learn poison powder at level 13") {
                assertTrue(bulbasaur.levelUpMoves["13"]!!.contains("Poison Powder"))
            }
            it("should learn sleep powder at level 13") {
                assertTrue(bulbasaur.levelUpMoves["13"]!!.contains("Sleep Powder"))
            }
            it("should learn take down at level 15") {
                assertTrue(bulbasaur.levelUpMoves["15"]!!.contains("Take Down"))
            }
            it("should learn razor leaf at level 19") {
                assertTrue(bulbasaur.levelUpMoves["19"]!!.contains("Razor Leaf"))
            }
            it("should learn sweet scent at level 21") {
                assertTrue(bulbasaur.levelUpMoves["21"]!!.contains("Sweet Scent"))
            }
            it("should learn growth at level 25") {
                assertTrue(bulbasaur.levelUpMoves["25"]!!.contains("Growth"))
            }
            it("should learn double-edge at level 27") {
                assertTrue(bulbasaur.levelUpMoves["27"]!!.contains("Double-Edge"))
            }
            it("should learn worry seed at level 31") {
                assertTrue(bulbasaur.levelUpMoves["31"]!!.contains("Worry Seed"))
            }
            it("should learn synthesis at level 33") {
                assertTrue(bulbasaur.levelUpMoves["33"]!!.contains("Synthesis"))
            }
            it("should learn seed bomb at level 37") {
                assertTrue(bulbasaur.levelUpMoves["37"]!!.contains("Seed Bomb"))
            }
        }

        describe("machine moves") {
            it("should be able to learn cut") {
                assertTrue(bulbasaur.machineMoves.contains("Cut"))
            }
            it("should be able to learn strength") {
                assertTrue(bulbasaur.machineMoves.contains("Strength"))
            }
            it("should be able to learn toxic") {
                assertTrue(bulbasaur.machineMoves.contains("Toxic"))
            }
            it("should be able to learn venoshock") {
                assertTrue(bulbasaur.machineMoves.contains("Venoshock"))
            }
            it("should be able to learn hidden power") {
                assertTrue(bulbasaur.machineMoves.contains("Hidden Power"))
            }
            it("should be able to learn sunny day") {
                assertTrue(bulbasaur.machineMoves.contains("Sunny Day"))
            }
            it("should be able to learn light screen") {
                assertTrue(bulbasaur.machineMoves.contains("Light Screen"))
            }
            it("should be able to learn protect") {
                assertTrue(bulbasaur.machineMoves.contains("Protect"))
            }
            it("should be able to learn safeguard") {
                assertTrue(bulbasaur.machineMoves.contains("Safeguard"))
            }
            it("should be able to learn frustration") {
                assertTrue(bulbasaur.machineMoves.contains("Frustration"))
            }
            it("should be able to learn solar beam") {
                assertTrue(bulbasaur.machineMoves.contains("Solar Beam"))
            }
            it("should be able to learn return") {
                assertTrue(bulbasaur.machineMoves.contains("Return"))
            }
            it("should be able to learn double team") {
                assertTrue(bulbasaur.machineMoves.contains("Double Team"))
            }
            it("should be able to learn sludge bomb") {
                assertTrue(bulbasaur.machineMoves.contains("Sludge Bomb"))
            }
            it("should be able to learn facade") {
                assertTrue(bulbasaur.machineMoves.contains("Facade"))
            }
            it("should be able to learn rest") {
                assertTrue(bulbasaur.machineMoves.contains("Rest"))
            }
            it("should be able to learn attract") {
                assertTrue(bulbasaur.machineMoves.contains("Attract"))
            }
            it("should be able to learn round") {
                assertTrue(bulbasaur.machineMoves.contains("Round"))
            }
            it("should be able to learn echoed voice") {
                assertTrue(bulbasaur.machineMoves.contains("Echoed Voice"))
            }
            it("should be able to learn energy ball") {
                assertTrue(bulbasaur.machineMoves.contains("Energy Ball"))
            }
            it("should be able to learn flash") {
                assertTrue(bulbasaur.machineMoves.contains("Flash"))
            }
            it("should be able to learn swords dance") {
                assertTrue(bulbasaur.machineMoves.contains("Swords Dance"))
            }
            it("should be able to learn grass knot") {
                assertTrue(bulbasaur.machineMoves.contains("Grass Knot"))
            }
            it("should be able to learn swagger") {
                assertTrue(bulbasaur.machineMoves.contains("Swagger"))
            }
            it("should be able to learn sleep talk") {
                assertTrue(bulbasaur.machineMoves.contains("Sleep Talk"))
            }
            it("should be able to learn substitute") {
                assertTrue(bulbasaur.machineMoves.contains("Substitute"))
            }
            it("should be able to learn rock smash") {
                assertTrue(bulbasaur.machineMoves.contains("Rock Smash"))
            }
            it("should be able to learn nature power") {
                assertTrue(bulbasaur.machineMoves.contains("Nature Power"))
            }
            it("should be able to learn confide") {
                assertTrue(bulbasaur.machineMoves.contains("Confide"))
            }
        }

        // todo: egg move list

        describe("tutor move list") {
            it("can be taught bind") {
                assertTrue(bulbasaur.tutorMoves.containsKey("Bind"))
            }
            it("can be taught body slam") {
                assertTrue(bulbasaur.tutorMoves.containsKey("Body Slam"))
            }
            it("can be taught bullet seed") {
                assertTrue(bulbasaur.tutorMoves.containsKey("Bullet Seed"))
            }
            it("can be taught defense curl") {
                assertTrue(bulbasaur.tutorMoves.containsKey("Defense Curl"))
            }
            it("can be taught fury cutter") {
                assertTrue(bulbasaur.tutorMoves.containsKey("Fury Cutter"))
            }
            it("can be taught giga drain") {
                assertTrue(bulbasaur.tutorMoves.containsKey("Giga Drain"))
            }
            it("can be taught grass pledge") {
                assertTrue(bulbasaur.tutorMoves.containsKey("Grass Pledge"))
            }
            it("can be taught knock off") {
                assertTrue(bulbasaur.tutorMoves.containsKey("Knock Off"))
            }
            it("can be taught mud-slap") {
                assertTrue(bulbasaur.tutorMoves.containsKey("Mud-Slap"))
            }
            it("can be taught natural gift") {
                assertTrue(bulbasaur.tutorMoves.containsKey("Natural Gift"))
            }
            it("can be taught secret power") {
                assertTrue(bulbasaur.tutorMoves.containsKey("Secret Power"))
            }
            it("can be taught seed bomb") {
                assertTrue(bulbasaur.tutorMoves.containsKey("Seed Bomb"))
            }
            it("can be taught sleep talk") {
                assertTrue(bulbasaur.tutorMoves.containsKey("Sleep Talk"))
            }
            it("can be taught snore") {
                assertTrue(bulbasaur.tutorMoves.containsKey("Snore"))
            }
            it("can be taught string shot") {
                assertTrue(bulbasaur.tutorMoves.containsKey("String Shot"))
            }
            it("can be taught synthesis") {
                assertTrue(bulbasaur.tutorMoves.containsKey("Synthesis"))
            }
            it("can be taught worry seed") {
                assertTrue(bulbasaur.tutorMoves.containsKey("Worry Seed"))
            }
        }

    }
})