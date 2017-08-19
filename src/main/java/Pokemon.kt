class Pokemon {
    lateinit var types: Array<String>
        private set
    lateinit var capabilities: Capabilities
        private set
    lateinit var basicAbilities: Array<String>
        private set
    lateinit var genderRatio: GenderRatio
        private set
    lateinit var habitats: Array<String>
        private set
    lateinit var eggGroups: Array<String>
        private set
    // map of level to list of moves
    lateinit var levelUpMoves: Map<String, List<String>>
        private set
    // map of pokemon name to evolution conditions
    lateinit var evolvesTo: Map<String, Evolution>
        private set
    lateinit var machineMoves: Array<String>
        private set
    lateinit var weight: Weight
        private set
    lateinit var diets: Array<String>
        private set
    lateinit var evolvesFrom: String // pokemon name
        private set
    // map of move name to preconditions
    lateinit var tutorMoves: Map<String, HeartScaleRequirement>
        private set
    // map of skill name to skill
    lateinit var skills: Map<String, Skill>
        private set
    var stage = 0
        private set
    lateinit var stats: Stats
        private set
    lateinit var name: String
        private set
    lateinit var highAbilities: Array<String>
        private set
    lateinit var advancedAbilities: Array<String>
        private set
    lateinit var height: Height
        private set
}

