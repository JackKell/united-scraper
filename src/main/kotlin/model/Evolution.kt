package model

class Evolution {
    lateinit var name: String
        private set
    lateinit var trigger: String
        private set
    lateinit var conditions: EvolutionConditions
        private set
}
