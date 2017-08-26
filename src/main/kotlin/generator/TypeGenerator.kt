package generator

import org.json.JSONObject

/**
 * A generator for arranging and mapping types to one another.
 *
 * It does NOT automatically resolve conflicts between types,
 * explicitly added types will overwrite existing types,
 * but pre-existing types will not be implicitly overwritten.
 *
 * For example:
 *
 * Grass {superEffectiveAgainst["Water"]} // Explicitly add 'Grass' first
 * {
 *  "types" {
 *      "Grass": {
 *          "attacking": {
 *              "Water": 1.5
 *          }
 *       },
 *       "Water": { // 'Water' is added implicitly
 *          "defending": {
 *              "Grass": 1.5
 *          }
 *       }
 *  }
 * }
 *
 * Water {resistantTo["Grass"]} // 'Water' is being explicitly added second here
 * {
 *  "types" {
 *      "Grass": {
 *          "attacking": {
 *              "Water": 1.5 // the previous explicit value stays the same
 *          }
 *       },
 *       "Water": {
 *          "defending": {
 *              "Grass": 0.5 // the value here was changed
 *          }
 *       }
 *  }
 * }
 *
 *
 * Example result:
 *
 * {
 *  "types": {
 *      "Flying": {
 *          "attacking": {
 *              "Electric": 0.5,
 *              "Ground": 1.0,
 *              "Fighting": 1.5,
 *              ...
 *          },
 *          "defending": {
 *              "Ground": 0.0,
 *              "Fighting": 0.5,
 *              "Electric": 1.5,
 *              ...
 *          }
 *      },
 *      "Electric": {
 *          "effectImmunities": ["Paralyze"]
 *      },
 *      "Grass": {
 *          "mechanicImmunities": ["Spore"]
 *      },
 *      "Ghost": {
 *          "immunityExceptions": ["Social"]
 *      }
 */
class TypeGenerator {

    // default types
    private val types: HashMap<String, Type> = hashMapOf(
            "Normal" to Type("Normal",
                    notVeryEffectiveAgainst = hashSetOf("Rock", "Steel"),
                    ineffectiveAgainst = hashSetOf("Ghost"),
                    weakTo = hashSetOf("Fighting"),
                    immuneTo = hashSetOf("Ghost")),
            "Fire" to Type("Fire",
                    superEffectiveAgainst = hashSetOf("Grass", "Ice", "Bug", "Steel"),
                    notVeryEffectiveAgainst = hashSetOf("Fire", "Water", "Rock", "Dragon"),
                    weakTo = hashSetOf("Water", "Ground", "Rock"),
                    resistantTo = hashSetOf("Fire", "Grass", "Ice", "Bug", "Steel", "Fairy"),
                    effectImmunities = listOf("Burn")),
            "Water" to Type("Water",
                    superEffectiveAgainst = hashSetOf("Fire", "Ground", "Rock"),
                    notVeryEffectiveAgainst = hashSetOf("Water", "Grass", "Dragon"),
                    weakTo = hashSetOf("Electric", "Grass"),
                    resistantTo = hashSetOf("Fire", "Water", "Ice", "Steel")),
            "Electric" to Type("Electric",
                    superEffectiveAgainst = hashSetOf("Water", "Flying"),
                    notVeryEffectiveAgainst = hashSetOf("Electric", "Grass"),
                    ineffectiveAgainst = hashSetOf("Ground"),
                    weakTo = hashSetOf("Ground"),
                    resistantTo = hashSetOf("Electric", "Flying", "Steel"),
                    effectImmunities = listOf("Paralysis")),
            "Grass" to Type("Grass",
                    superEffectiveAgainst = hashSetOf("Water", "Ground", "Rock"),
                    notVeryEffectiveAgainst = hashSetOf("Fire", "Grass", "Poison", "flying", "Bug", "Dragon", "Steel"),
                    weakTo = hashSetOf("Fire", "Ice", "Poison", "Flying", "Bug"),
                    resistantTo = hashSetOf("Water", "Electric", "Grass", "Ground"),
                    mechanicImmunities = listOf("Powder")),
            "Ice" to Type("Ice",
                    superEffectiveAgainst = hashSetOf("Grass", "Ground", "Flying", "Dragon"),
                    notVeryEffectiveAgainst = hashSetOf("Fire", "Water", "Ice", "Steel"),
                    weakTo = hashSetOf("Fire", "Fighting", "Rock", "Steel"),
                    resistantTo = hashSetOf("Ice"),
                    effectImmunities = listOf("Frozen")),
            "Fighting" to Type("Fighting",
                    superEffectiveAgainst = hashSetOf("Normal", "Ice", "Dark", "Steel"),
                    notVeryEffectiveAgainst = hashSetOf("Poison", "Flying", "Psychic", "Bug", "Fairy"),
                    ineffectiveAgainst = hashSetOf("Ghost"),
                    weakTo = hashSetOf("Flying", "Psychic", "Fairy"),
                    resistantTo = hashSetOf("Bug", "Rock", "Dark")),
            "Poison" to Type("Poison",
                    superEffectiveAgainst = hashSetOf("Grass", "Fairy"),
                    notVeryEffectiveAgainst = hashSetOf("Poison", "Ground", "Rock", "Ghost"),
                    ineffectiveAgainst = hashSetOf("Steel"),
                    weakTo = hashSetOf("Ground", "Psychic"),
                    resistantTo = hashSetOf("Grass", "Fighting", "Poison", "Bug", "Fairy"),
                    effectImmunities = listOf("Poison")),
            "Ground" to Type("Ground",
                    superEffectiveAgainst = hashSetOf("Fire", "Electric", "Poison", "Rock", "Steel"),
                    notVeryEffectiveAgainst = hashSetOf("Grass", "Bug"),
                    ineffectiveAgainst = hashSetOf("Flying"),
                    weakTo = hashSetOf("Water", "Grass", "Ice"),
                    resistantTo = hashSetOf("Poison", "Rock"),
                    immuneTo = hashSetOf("Electric")),
            "Flying" to Type("Flying",
                    superEffectiveAgainst = hashSetOf("Grass", "Fighting", "Bug"),
                    notVeryEffectiveAgainst = hashSetOf("Electric", "Rock", "Steel"),
                    weakTo = hashSetOf("Electric", "Ice", "Rock"),
                    resistantTo = hashSetOf("Grass", "Fighting", "Bug"),
                    immuneTo = hashSetOf("Ground")),
            "Psychic" to Type("Psychic",
                    superEffectiveAgainst = hashSetOf("Fighting", "Poison"),
                    notVeryEffectiveAgainst = hashSetOf("Psychic", "Steel"),
                    ineffectiveAgainst = hashSetOf("Dark"),
                    weakTo = hashSetOf("Bug", "Ghost", "Dark"),
                    resistantTo = hashSetOf("Fighting", "Psychic")),
            "Bug" to Type("Bug",
                    superEffectiveAgainst = hashSetOf("Grass", "Psychic", "Dark"),
                    notVeryEffectiveAgainst = hashSetOf("Fire", "Fighting", "Poison", "Flying", "Ghost", "Steel", "Fairy"),
                    weakTo = hashSetOf("Fire", "Flying", "Rock"),
                    resistantTo = hashSetOf("Grass", "Fighting", "Ground")),
            "Rock" to Type("Rock",
                    superEffectiveAgainst = hashSetOf("Fire", "Ice", "Flying", "Bug"),
                    notVeryEffectiveAgainst = hashSetOf("Fighting", "Ground", "Steel"),
                    weakTo = hashSetOf("Water", "Grass", "Fighting", "Ground", "Steel"),
                    resistantTo = hashSetOf("Normal", "Fire", "Poison", "Flying")),
            "Ghost" to Type("Ghost",
                    superEffectiveAgainst = hashSetOf("Psychic", "Ghost"),
                    notVeryEffectiveAgainst = hashSetOf("Dark"),
                    ineffectiveAgainst = hashSetOf("Normal"),
                    weakTo = hashSetOf("Ghost", "Dark"),
                    resistantTo = hashSetOf("Poison", "Bug"),
                    immuneTo = hashSetOf("Normal", "Fighting"),
                    mechanicImmunities = listOf("Stuck", "Trapped"),
                    immunityExceptions = listOf("Social")),
            "Dragon" to Type("Dragon",
                    superEffectiveAgainst = hashSetOf("Dragon"),
                    notVeryEffectiveAgainst = hashSetOf("Steel"),
                    ineffectiveAgainst = hashSetOf("Fairy"),
                    weakTo = hashSetOf("Ice", "Dragon", "Fairy"),
                    resistantTo = hashSetOf("Fire", "Water", "Electric", "Grass")),
            "Dark" to Type("Dark",
                    superEffectiveAgainst = hashSetOf("Psychic", "Ghost"),
                    notVeryEffectiveAgainst = hashSetOf("Fighting", "Dark", "Fairy"),
                    weakTo = hashSetOf("Fighting", "Bug", "Fairy"),
                    resistantTo = hashSetOf("Ghost", "Dark"),
                    immuneTo = hashSetOf("Psychic")),
            "Steel" to Type("Steel",
                    superEffectiveAgainst = hashSetOf("Ice", "Rock", "Fairy"),
                    notVeryEffectiveAgainst = hashSetOf("Fire", "Water", "Electric", "Steel"),
                    weakTo = hashSetOf("Fire", "Fighting", "Ground"),
                    resistantTo = hashSetOf("Normal", "Grass", "Ice", "Flying", "Psychic", "Bug", "Rock", "Dragon", "Steel", "Fairy"),
                    immuneTo = hashSetOf("Poison"),
                    effectImmunities = listOf("Poison")),
            "Fairy" to Type("Fairy",
                    superEffectiveAgainst = hashSetOf("Fighting", "Dragon", "Dark"),
                    notVeryEffectiveAgainst = hashSetOf("Fire", "Poison", "Steel"),
                    weakTo = hashSetOf("Poison", "Steel"),
                    resistantTo = hashSetOf("Fighting", "Bug", "Dark"),
                    immuneTo = hashSetOf("Dragon"))
    )

    /**
     * Add a type to the generator to be mapped to other types.
     * If a type does not exist, it will be added to the JSON.
     * Explicitly adding a type that was already referenced
     * will overwrite any data filled in by other types.
     */
    fun addType(typeToAdd: Type) {
            // types are added as we go, so retain a list of pre-existing types
            val preexisting = hashMapOf<String, Boolean>()
            typeToAdd.superEffectiveAgainst
                    .union(typeToAdd.notVeryEffectiveAgainst)
                    .union(typeToAdd.ineffectiveAgainst)
                    .union(typeToAdd.immuneTo)
                    .union(typeToAdd.resistantTo)
                    .union(typeToAdd.weakTo)
                    .forEach { preexisting.put(it, types.contains(it) ) }

            // for all of the types that this type mentions,
            // check them and ensure they mention this type inversely

        typeToAdd.superEffectiveAgainst.forEach {
                val type = types[it]
                if (type != null) {
                    if (preexisting[it] != true) {
                        type.weakTo.add(typeToAdd.name)
                    }
                } else {
                    types.put(it, Type(it).also { t -> t.weakTo.add(typeToAdd.name) })
                }
            }

        typeToAdd.notVeryEffectiveAgainst.forEach {
                val type = types[it]
                if (type != null) {
                    if (preexisting[it] != true) {
                        type.resistantTo.add(typeToAdd.name)
                    }
                } else {
                    types.put(it, Type(it).also { t -> t.resistantTo.add(typeToAdd.name) })
                }
            }

        typeToAdd.ineffectiveAgainst.forEach {
                val type = types[it]
                if (type != null) {
                    if (preexisting[it] != true) {
                        type.immuneTo.add(typeToAdd.name)
                    }
                } else {
                    types.put(it, Type(it).also { t -> t.immuneTo.add(typeToAdd.name) })
                }
            }

        typeToAdd.immuneTo.forEach {
                val type = types[it]
                if (type != null) {
                    if (preexisting[it] != true) {
                        type.ineffectiveAgainst.add(typeToAdd.name)
                    }
                } else {
                    types.put(it, Type(it).also { t -> t.ineffectiveAgainst.add(typeToAdd.name) })
                }
            }

        typeToAdd.resistantTo.forEach {
                val type = types[it]
                if (type != null) {
                    if (preexisting[it] != true) {
                        type.notVeryEffectiveAgainst.add(typeToAdd.name)
                    }
                } else {
                    types.put(it, Type(it).also { t -> t.notVeryEffectiveAgainst.add(typeToAdd.name) })
                }
            }

            typeToAdd.weakTo.forEach {
                val type = types[it]
                if (type != null) {
                    if (preexisting[it] != true) {
                        type.superEffectiveAgainst.add(typeToAdd.name)
                    }
                } else {
                    types.put(it, Type(it).also { t -> t.superEffectiveAgainst.add(typeToAdd.name) })
                }
            }

            types.put(typeToAdd.name, typeToAdd)
    }

    /**
     * Generate the types json
     *
     * @return the [JSONObject] that contains all of the types mapped appropriately
     */
    fun generate(): JSONObject {
        val jsonObject = JSONObject()
        val map = hashMapOf<String, Any>()
        types.forEach { entry ->
            entry.run {
                val result = hashMapOf<String, Any>()

                val attacking = hashMapOf<String, Float>()
                value.superEffectiveAgainst.forEach { attacking.put(it, 1.5f) }
                value.notVeryEffectiveAgainst.forEach { attacking.put(it, 0.5f) }
                value.ineffectiveAgainst.forEach { attacking.put(it, 0.0f) }
                val defending = hashMapOf<String, Float>()
                value.weakTo.forEach { defending.put(it, 1.5f) }
                value.resistantTo.forEach { defending.put(it, 0.5f) }
                value.immuneTo.forEach { defending.put(it, 0.0f) }

                types.forEach {
                    attacking.putIfAbsent(it.key, 1.0f)
                    defending.putIfAbsent(it.key, 1.0f)
                }

                result.put(ATTACKING, attacking)
                result.put(DEFENDING, defending)
                result.put(EFFECT_IMMUNITIES, value.effectImmunities)
                result.put(MECHANIC_IMMUNITIES, value.mechanicImmunities)
                result.put(IMMUNITY_EXCEPTIONS, value.immunityExceptions)
                map.put(key, result)
            }
        }
        jsonObject.put(TYPES, map)
        return jsonObject
    }

    companion object {
        /** JSON Keys **/
        val TYPES = "types"
        val ATTACKING = "attacking"
        val DEFENDING = "defending"
        val EFFECT_IMMUNITIES = "effectImmunities"
        val MECHANIC_IMMUNITIES = "mechanicImmunities"
        val IMMUNITY_EXCEPTIONS = "immunityExceptions"
    }
}