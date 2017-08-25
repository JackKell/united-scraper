package generator

import org.json.JSONObject

/**
 * A generator for arranging and mapping types to one another.
 *
 * It does NOT automatically resolve conflicts between types,
 * newly added types will overwrite existing types.
 *
 * For example:
 *
 * Grass {superEffectiveAgainst["Water"]}
 * Water {resistantTo["Grass"]} // 'Water' is being added second here
 *
 * Results to:
 *
 * {
 *  "types" {
 *      "Grass": {
 *          "attacking": {
 *              "Water": 0.5
 *          }
 *       },
 *       "Water": {
 *          "defending": {
 *              "Grass": 0.5
 *          }
 *       }
 *  }
 * }
 *
 *
 * Example data set:
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

    private val types: HashMap<String, Type> = hashMapOf()

    /**
     * Add a type to the generator to be mapped to other types.
     * If a type does not exist, it will be added to the JSON.
     */
    fun addType(type: Type) {
        // for all of the types that this type mentions,
        // check them and ensure they mention this type reversely

        type.superEffectiveAgainst
                .forEach {
                    types[it]?.weakTo?.add(it)
                }

        type.notVeryEffectiveAgainst
                .forEach {
                    types[it]?.resistantTo?.add(it)
                }

        type.ineffectiveAgainst
                .forEach {
                    types[it]?.immuneTo?.add(it)
                }

        type.immuneTo
                .forEach {
                    types[it]?.ineffectiveAgainst?.add(it)
                }

        type.resistantTo
                .forEach {
                    types[it]?.notVeryEffectiveAgainst?.add(it)
                }

        type.weakTo
                .forEach {
                    types[it]?.superEffectiveAgainst?.add(it)
                }

        types.put(type.name, type)
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
                val defending = hashMapOf<String, Float>()
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