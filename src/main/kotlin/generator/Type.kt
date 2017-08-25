package generator

/**
 * A type (e.g. flying) that has weaknesses (e.g. electric),
 * effectiveness (e.g. fighting), immunities (e.g. ground),
 * and resistances (e.g. grass)
 *
 * Types can also have effect immunities (e.g. electric cannot be paralyzed)
 * and mechanic immunities (e.g. grass cannot be affected by spores),
 * and mechanic immunity exceptions (e.g. ghost can be affected by all social moves)
 */
data class Type(val name: String,
                val superEffectiveAgainst: HashSet<String> = hashSetOf(),
                val notVeryEffectiveAgainst: HashSet<String> = hashSetOf(),
                val ineffectiveAgainst: HashSet<String> = hashSetOf(),
                val immuneTo: HashSet<String> = hashSetOf(),
                val resistantTo: HashSet<String> = hashSetOf(),
                val weakTo: HashSet<String> = hashSetOf(),
                val effectImmunities: List<String> = emptyList(),
                val mechanicImmunities: List<String> = emptyList(),
                val immunityExceptions: List<String> = emptyList())