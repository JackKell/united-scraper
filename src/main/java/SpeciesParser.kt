import org.json.JSONObject

import java.util.ArrayList
import java.util.HashMap
import java.util.regex.Matcher
import java.util.regex.Pattern

import java.lang.Float.parseFloat
import java.lang.Integer.parseInt
import java.util.Arrays.asList

internal class SpeciesParser {
    val problemSpecies = asList(
            "Pumpkaboo", // Too many forms
            "Gourgeist", // Too many forms
            "Rotom normal form", // Too many forms
            "Rotom appliance forms" // Too many forms
    )

    fun parse(page: String): JSONObject {
        val cleanedPage = cleanPage(page)
        //        System.out.println(cleanedPage);
        val species = JSONObject()
        val name = parseName(cleanedPage)
        //        System.out.println(name);
        species.put("name", name)
        if (problemSpecies.contains(name)) {
            return species
        }
        species.put("evolutionChain", parseEvolutionChain(cleanedPage))
        species.put("highAbilities", parseHighAbilities(cleanedPage))
        species.put("advancedAbilities", parseAdvancedAbilities(cleanedPage))
        species.put("basicAbilities", parseBasicAbilities(cleanedPage))
        species.put("tutorMoves", parseTutorMoves(cleanedPage))
        species.put("eggMoves", parseEggMoves(cleanedPage))
        species.put("machineMoves", parseMachineMoves(cleanedPage))
        species.put("genderRatio", parseGenderRatio(cleanedPage))
        species.put("levelUpMoves", parseLevelUpMoves(cleanedPage))
        species.put("sky", parseNamedInteger(cleanedPage, "Sky"))
        species.put("swim", parseNamedInteger(cleanedPage, "Swim"))
        species.put("overland", parseNamedInteger(cleanedPage, "Overland"))
        species.put("levitate", parseNamedInteger(cleanedPage, "Levitate"))
        species.put("power", parseNamedInteger(cleanedPage, "Power"))
        species.put("burrow", parseNamedInteger(cleanedPage, "Burrow"))
        species.put("types", parseSlashSeparatedList(cleanedPage, "Type"))
        species.put("jump", parseJump(cleanedPage))
        species.put("eggGroups", parseSlashSeparatedList(cleanedPage, "Egg Group"))
        species.put("averageHatchRate", parseNamedInteger(cleanedPage, "Average Hatch Rate:"))
        species.put("stats", parseStats(cleanedPage))
        species.put("skills", parseSkills(cleanedPage))
        species.put("height", parseHeight(cleanedPage))
        species.put("weight", parseWeight(cleanedPage))
        species.put("diet", parseCommaSeparatedList(cleanedPage, "Diet"))
        species.put("habitat", parseCommaSeparatedList(cleanedPage, "Habitat"))
        println(species)
        return species
    }

    private fun cleanPage(page: String): String {
        var cleanedPage = page
        // Connect words the are separated by a newline
        cleanedPage = cleanedPage.replace("(\\w)-\r\n(\\w)".toRegex(), "$1$2")
        // Fix typo for Drilbur
        cleanedPage = cleanedPage.replace('!', '1')
        // Fix ’ to be a ' on all pages (Like in the move Forest's Curse verses Forest’s Curse)
        cleanedPage = cleanedPage.replace('’', '\'')
        // Remove "add" typos in egg group (Phantump, Trevenant, and others)
        cleanedPage = cleanedPage.replace(" and ".toRegex(), " / ")
        // Fix ” to be a " on all pages (Like on Bulbasaur)
        cleanedPage = cleanedPage.replace('”', '\"')
        // Put all stages of evolution onto 1 line (For example of Shelmet)
        cleanedPage = cleanedPage.replace("(\\d +- +[\\S]+.*)\r\n ( \\D)".toRegex(), "$1$2")
        // Fix é to be e
        cleanedPage = cleanedPage.replace("é".toRegex(), "e")
        // Fix É to be E
        cleanedPage = cleanedPage.replace("É".toRegex(), "E")
        // Fix Thunderstone to be Thunder Stone
        cleanedPage = cleanedPage.replace("Thunderstone".toRegex(), "Thunder Stone")
        // Fix Scizor name to remove the "."
        cleanedPage = cleanedPage.replace("\\. SCIZOR".toRegex(), "Scizor")
        // Change Porygon-Z to be Porygon-z
        cleanedPage = cleanedPage.replace("n-Z".toRegex(), "n-z")
        // Change "Min." to "Minimum" for Mantyke and Mantine
        cleanedPage = cleanedPage.replace("Min\\.".toRegex(), "Minimum")
        // Fix Typo "wiht a" to "with"
        cleanedPage = cleanedPage.replace("wiht a".toRegex(), "with")
        // Fix Empoleon, Marshtomp, SandSlash, and Grotle who has both "High Ability" and "High Abilities"
        cleanedPage = cleanedPage.replace("(High Ability:\\s*[\\w ]*)\r\n\\s*High Abilities: ([\\w ]*)".toRegex(), "$1 / $2")
        return cleanedPage
    }

    private fun parseName(text: String): String {
        var name = text.substring(text.indexOf('\n') + 1, text.indexOf("Base")).trim { it <= ' ' }
        name = name.substring(0, 1) + name.substring(1).toLowerCase()
        return name
    }

    private fun parseHeight(page: String): Map<String, Any>? {
        val height = HashMap<String, Any>()
        val heightRegex = "Height\\s*:\\s*(\\d*' \\d*\") \\/ (\\d*.\\d*)m \\((\\w*)\\)"
        val heightPattern = Pattern.compile(heightRegex)
        val heightMatcher = heightPattern.matcher(page)
        if (heightMatcher.find()) {
            val ft = heightMatcher.group(1)
            val m = parseFloat(heightMatcher.group(2))
            val size = heightMatcher.group(3)
            height.put("ft", ft)
            height.put("m", m)
            height.put("size", size)
            return height
        } else {
            return null
        }
    }

    private fun parseWeight(page: String): Map<String, Any>? {
        val weight = HashMap<String, Any>()
        val weightRegex = "Weight\\s*:\\s*(\\d*.\\d*) lbs\\. \\/ (\\d*.\\d*)kg \\((\\w*)\\)"
        val weightPattern = Pattern.compile(weightRegex)
        val weightMatcher = weightPattern.matcher(page)
        if (weightMatcher.find()) {
            val lbs = parseFloat(weightMatcher.group(1))
            val kg = parseFloat(weightMatcher.group(2))
            val weightClass = parseInt(weightMatcher.group(3))
            weight.put("lbs", lbs)
            weight.put("kg", kg)
            weight.put("class", weightClass)
            return weight
        } else {
            return null
        }
    }

    private fun parseJump(page: String): Map<String, Int>? {
        val jump = HashMap<String, Int>()
        val p = "Jump\\s*(\\d*)\\/(\\d*)"
        val pattern = Pattern.compile(p)
        val matcher = pattern.matcher(page)
        if (matcher.find()) {
            jump.put("long", parseInt(matcher.group(1)))
            jump.put("high", parseInt(matcher.group(2)))
            return jump
        } else {
            return null
        }
    }

    private fun parseNamedInteger(text: String, name: String): Int {
        val p = "$name\\s*(\\d+)"
        val pattern = Pattern.compile(p)
        val matcher = pattern.matcher(text)
        if (matcher.find()) {
            return parseInt(matcher.group(1))
        } else {
            return 0
        }
    }

    private fun parseSlashSeparatedList(text: String, name: String): List<String> {
        val items = ArrayList<String>()
        val regex = "$name\\s*:\\s*(\\w*)(?:\\s*\\/\\s*(\\w*))?"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(text)
        if (matcher.find()) {
            val item1 = matcher.group(1)
            val item2 = matcher.group(2)
            items.add(item1)
            if (item2 != null) items.add(item2)
        } else {
            throw Error("No match was found in:\n$text\n For regex string $regex")
        }
        return items
    }

    private fun parseCommaSeparatedList(text: String, name: String): List<String>? {
        val items: MutableList<String>
        val p = "$name\\s*:\\s*((?:\\w+)(?:,\\s*\\w+)*)"
        val pattern = Pattern.compile(p)
        val matcher = pattern.matcher(text)
        if (matcher.find()) {
            val itemsString = matcher.group(1)
            items = asList(*itemsString.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
            for (i in items.indices) {
                items[i] = items[i].trim { it <= ' ' }
            }
            return items
        } else {
            return null
        }
    }

    private fun parseStats(page: String): Map<String, Int> {
        val stats = HashMap<String, Int>()
        stats.put("hp", parseNamedInteger(page, "HP:"))
        stats.put("attack", parseNamedInteger(page, "Attack:"))
        stats.put("defense", parseNamedInteger(page, "Defense:"))
        stats.put("specialAttack", parseNamedInteger(page, "Special Attack:"))
        stats.put("specialDefense", parseNamedInteger(page, "Special Defense:"))
        stats.put("speed", parseNamedInteger(page, "Speed:"))
        return stats
    }

    private fun parseSkill(page: String, name: String): Map<String, Any>? {
        val skill = HashMap<String, Any>()
        val p = "$name\\s*((\\d*)d(\\d*)(?:\\+(\\d*))?)"
        val pattern = Pattern.compile(p)
        val matcher = pattern.matcher(page)
        if (matcher.find()) {
            val value = matcher.group(1)
            val diceNumber = parseInt(matcher.group(2))
            val diceFaces = parseInt(matcher.group(3))
            val buffString = matcher.group(4)
            val buff: Int
            if (buffString == null) {
                buff = 0
            } else {
                buff = parseInt(buffString)
            }
            skill.put("value", value)
            skill.put("diceNumber", diceNumber)
            skill.put("diceFaces", diceFaces)
            skill.put("buff", buff)
            return skill
        } else {
            return null
        }
    }

    private fun parseSkills(page: String): Map<String, Map<String, Any>> {
        val skills = HashMap<String, Map<String, Any>>()
        skills.put("athletics", parseSkill(page, "Athl"))
        skills.put("acrobatics", parseSkill(page, "Acro"))
        skills.put("combat", parseSkill(page, "Combat"))
        skills.put("stealth", parseSkill(page, "Stealth"))
        skills.put("perception", parseSkill(page, "Percep"))
        skills.put("focus", parseSkill(page, "Focus"))
        return skills
    }

    private fun parseGenderRatio(page: String): Map<String, Any>? {
        val genderRatio = HashMap<String, Any>()
        genderRatio.put("male", 0.0f)
        genderRatio.put("female", 0.0f)
        genderRatio.put("gendered", false)
        genderRatio.put("hermaphrodite", false)
        val genderRegex = "Gender Ratio\\s*:\\s*(.*)\r\n"
        val genderPattern = Pattern.compile(genderRegex)
        val genderMatcher = genderPattern.matcher(page)
        if (genderMatcher.find()) {
            val result = genderMatcher.group(1)
            val ratioRegex = "([0-9\\.]+)%\\s*M\\s*\\/\\s*([0-9\\.]+)%\\s*F"
            val ratioPattern = Pattern.compile(ratioRegex)
            val ratioMatcher = ratioPattern.matcher(result)
            if (ratioMatcher.find()) {
                val maleRatio = parseFloat(ratioMatcher.group(1))
                val femaleRatio = parseFloat(ratioMatcher.group(2))
                genderRatio.put("male", maleRatio)
                genderRatio.put("female", femaleRatio)
                genderRatio.put("gendered", true)
                return genderRatio
            } else if (result == "Hermaphrodite") {
                genderRatio.put("hermaphrodite", true)
                return genderRatio
            } else if (result == "No Gender" || result == "Genderless") {
                genderRatio.put("gendered", false)
                return genderRatio
            } else {
                println(result)
                return null
            }
        } else {
            return null
        }
    }

    private fun parseLevelUpMoves(page: String): List<Map<String, Any>> {
        val levelUpMoves = ArrayList<Map<String, Any>>()
        val levelUpMoveRegex = "(\\d+)\\s+(['a-zA-Z- ]+)\\s+-"
        val levelUpMovePattern = Pattern.compile(levelUpMoveRegex)
        val levelUpMoveMatcher = levelUpMovePattern.matcher(page)
        while (levelUpMoveMatcher.find()) {
            val levelUpMove = HashMap<String, Any>()
            val level = parseInt(levelUpMoveMatcher.group(1))
            val move = levelUpMoveMatcher.group(2)
            levelUpMove.put("move", move)
            levelUpMove.put("level", level)
            levelUpMoves.add(levelUpMove)
        }
        return levelUpMoves
    }

    private fun parseMachineMoves(page: String): List<String>? {
        val machineMoves = ArrayList<String>()
        val p = "TM\\/HM Move List\\s*([\\s\\d\\w,-]*?)(?:Egg|Tutor)"
        val pattern = Pattern.compile(p)
        val matcher = pattern.matcher(page)
        val machineMoveRegex = "\\w*? ([a-zA-Z -]+)"
        val machineMovePattern = Pattern.compile(machineMoveRegex)
        if (matcher.find()) {
            var result = matcher.group(1)
            result = result.replace("\r\n".toRegex(), "")
            if (result == "None" || result.isEmpty()) return null
            val machineMoveStrings = asList(*result.trim { it <= ' ' }.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
            for (machineMoveString in machineMoveStrings) {
                val machineMoveMatcher = machineMovePattern.matcher(machineMoveString.trim { it <= ' ' })
                if (machineMoveMatcher.find()) {
                    machineMoves.add(machineMoveMatcher.group(1))
                } else {
                    throw Error("Machine move: \"" + machineMoveString.trim { it <= ' ' } + "\" was not parsed correctly")
                }
            }
            return machineMoves
        }
        return null
    }

    private fun parseTutorMoves(page: String): List<Map<String, Any>>? {
        val tutorMoves = ArrayList<Map<String, Any>>()
        val p = "Tutor Move List\r\n((?:[A-Za-z -]+)(?:,\\s(?:\r\n)?[A-Za-z-]+(?: (?:\r\n)?[A-Za-z-]+)?(?: (?:\r\n)?\\(N\\))?)+)"
        val pattern = Pattern.compile(p)
        val matcher = pattern.matcher(page)
        val tutorMoveRegex = "([A-Za-z-]*(?: [A-Za-z-]*)?)(?: (\\(N\\)))?"
        val tutorMovePattern = Pattern.compile(tutorMoveRegex)
        if (matcher.find()) {
            var result = matcher.group(1)
            result = result.replace("\r\n".toRegex(), "")
            val tutorMoveStrings = asList(*result.trim { it <= ' ' }.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
            for (tutorMoveString in tutorMoveStrings) {
                val tutorMoveMatcher = tutorMovePattern.matcher(tutorMoveString.trim { it <= ' ' })
                if (tutorMoveMatcher.find()) {
                    val tutorMove = HashMap<String, Any>()
                    val move = tutorMoveMatcher.group(1)
                    val isHeartScaleMove = tutorMoveMatcher.group(2) != null
                    tutorMove.put("move", move)
                    tutorMove.put("heartScaleMove", isHeartScaleMove)
                    tutorMoves.add(tutorMove)
                } else {
                    throw Error("Tutor move: \"" + tutorMoveString.trim { it <= ' ' } + "\" was not parsed correctly")
                }
            }
            return tutorMoves
        }
        return null
    }

    private fun parseEggMoves(page: String): List<String>? {
        val eggMoves: MutableList<String>
        val p = "Egg Move List\\s*([-a-zA-Z,\\s]+)Tutor Move List"
        val pattern = Pattern.compile(p)
        val matcher = pattern.matcher(page)
        if (matcher.find()) {
            var result = matcher.group(1)
            result = result.replace("\r\n".toRegex(), "")
            eggMoves = asList(*result.trim { it <= ' ' }.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
            for (i in eggMoves.indices) {
                eggMoves[i] = eggMoves[i].trim { it <= ' ' }
            }
            return eggMoves
        }
        return null
    }

    private fun parseBasicAbilities(page: String): List<String> {
        val basicAbilities = ArrayList<String>()
        val p = "Basic\\s*Ability\\s*\\d+\\s*:\\s*(.*)\\s"
        val pattern = Pattern.compile(p)
        val matcher = pattern.matcher(page)
        while (matcher.find()) {
            basicAbilities.add(matcher.group(1))
        }
        return basicAbilities
    }

    private fun parseAdvancedAbilities(page: String): List<String> {
        val advancedAbilities = ArrayList<String>()
        val p = "Adv\\s*Ability\\s*\\d+\\s*:\\s*(.*)\\s"
        val pattern = Pattern.compile(p)
        val matcher = pattern.matcher(page)
        while (matcher.find()) {
            advancedAbilities.add(matcher.group(1))
        }
        return advancedAbilities
    }

    private fun parseHighAbilities(page: String): List<String>? {
        val highAbilities: MutableList<String>
        val p = "High\\s*Ability\\s*:\\s*((?:.*\\s*\\/)*\\s*.*)"
        val pattern = Pattern.compile(p)
        val matcher = pattern.matcher(page)
        if (matcher.find()) {
            val result = matcher.group(1)
            highAbilities = asList(*result.split("\\/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
            for (i in highAbilities.indices) {
                highAbilities[i] = highAbilities[i].trim { it <= ' ' }
            }
            return highAbilities
        }
        return null
    }

    private fun parseEvolutionChain(page: String): List<Map<String, Any>> {
        val evolutionChain = ArrayList<Map<String, Any>>()
        val p = "(\\d) +- +([\\w-]*) *(.*)"
        val pattern = Pattern.compile(p)
        val matcher = pattern.matcher(page)
        while (matcher.find()) {
            val evolution = HashMap<String, Any>()
            val stage = parseInt(matcher.group(1))
            val pokemon = matcher.group(2)
            val conditions = matcher.group(3).replace(" (?= )".toRegex(), " ").trim { it <= ' ' }
            evolution.put("stage", stage)
            evolution.put("pokemon", pokemon)
            evolution.put("conditions", parseEvolutionCondition(conditions))
            evolutionChain.add(evolution)
        }
        return evolutionChain
    }

    private fun parseMinLevelCondition(conditions: String): Int? {
        val minLevelRegex = "M\\w*m (\\d+)"
        val minLevelPattern = Pattern.compile(minLevelRegex)
        val minLevelMatcher = minLevelPattern.matcher(conditions)
        if (minLevelMatcher.find()) {
            return parseInt(minLevelMatcher.group(1))
        } else {
            return null
        }
    }

    private fun parseGenderCondition(conditions: String): String? {
        if (conditions.toLowerCase().contains("female"))
            return "female"
        else if (conditions.toLowerCase().contains("male"))
            return "male"
        else
            return null
    }

    private fun parseHeldItemCondition(conditions: String): String? {
        val heldItemRegex = "Holding +(.*)? +(?=M\\w*m)"
        val heldItemPattern = Pattern.compile(heldItemRegex)
        val heldItemMatcher = heldItemPattern.matcher(conditions)
        if (heldItemMatcher.find()) {
            return heldItemMatcher.group(1).trim { it <= ' ' }
        } else {
            return null
        }
    }

    private fun parseUseItemCondition(conditions: String): String? {
        val useItemRegex = "\\w* Stone"
        val useItemPattern = Pattern.compile(useItemRegex)
        val heldItemMatcher = useItemPattern.matcher(conditions)
        if (heldItemMatcher.find()) {
            val match = heldItemMatcher.group(0).trim { it <= ' ' }
            if (!match.contains("Oval")) {
                return match
            }
        }
        return null
    }

    private fun parseLearnCondition(conditions: String): String? {
        val learnRegex = "Learn +(\\S*(?: \\S*)?)"
        val learnPattern = Pattern.compile(learnRegex)
        val learnMatcher = learnPattern.matcher(conditions)
        if (learnMatcher.find()) {
            return learnMatcher.group(1)
        }
        return null
    }

    private fun parseTimeOfDayCondition(conditions: String): String? {
        if (conditions.toLowerCase().contains("night"))
            return "night"
        else if (conditions.toLowerCase().contains("day"))
            return "day"
        else
            return null
    }

    private fun parseEvolutionCondition(conditions: String): Map<String, Any> {
        val evolutionCondition = HashMap<String, Any>()
        if (conditions.trim { it <= ' ' }.isEmpty()) return evolutionCondition
        evolutionCondition.put("minLevel", parseMinLevelCondition(conditions))
        evolutionCondition.put("gender", parseGenderCondition(conditions))
        evolutionCondition.put("timeOfDay", parseTimeOfDayCondition(conditions))
        evolutionCondition.put("heldItem", parseHeldItemCondition(conditions))
        evolutionCondition.put("useItem", parseUseItemCondition(conditions))
        evolutionCondition.put("learnMove", parseLearnCondition(conditions))
        return evolutionCondition
    }

    private fun parseStage(page: String): Int {
        return 0
    }
}
