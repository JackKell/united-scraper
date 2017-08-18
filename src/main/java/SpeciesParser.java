import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import org.json.JSONObject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;

class SpeciesParser {
    final List<String> problemSpecies = asList(
            "Pumpkaboo", // Too many forms
            "Gourgeist" // Too many forms
    );

    // A list of a the special capabilities
    // Naturewalk and Mountable are not included in this list because they require there own parsers
    final List<String> possibleSpecialCapabilities = asList("Alluring", "Amorphous", "Aura Reader", "Aura Pulse",
            "Blindsense", "Bloom", "Blender", "Chilled", "Darkvision", "Dead Silent", "Delta Evolution", "Dream Mist",
            "Dream Reader", "Egg Warmer", "Firestarter", "Fortune", "Fountain", "Freezer", "Gather Unown", "Gilled",
            "Glow", "Groundshaper", "Guster", "Heart Gift", "Heater", "Herb Growth", "Honey Gather", "Illusionist",
            "Inflatable", "Invisibility", "Juicer", "Keystone Warp", "Letter Press", "Living Weapon", "Magnetic",
            "Marsupial", "Materializer", "Milk Collection", "Mindlock", "Mushroom Harvest", "Mushroom Harvest",
            "Pack Mon", "Pearl Creation", "Phasing", "Planter", "Premonition", "Reach", "Shadow Meld", "Shapeshifter",
            "Shrinkable", "Soulless", "Split Evolution", "Sprouter", "Stealth", "Telekinetic", "Telepath", "Threaded",
            "Tracker", "Tremorsense", "Underdog", "Volatile Bomb", "Wallclimber", "Weathershape", "Wielder", "Wired",
            "X-Ray Vision", "Zapper"
    );

    SpeciesParser() {}

    JSONObject parse(List<String> pages) {
        JSONObject species = new JSONObject();
        for (String page: pages) {
            String cleanedPage = cleanPage(page);
//            System.out.println(cleanedPage);
            Map<String, Object> specie = new HashMap<>();
            String name = parseName(cleanedPage);
//          System.out.println(name);
            specie.put("name", name);
            if (problemSpecies.contains(name) || name.contains("form") && !name.equals("Castform")) {
                System.out.println(specie);
                species.put(name, specie);
            } else {
//                specie.put("stage", parseStage(cleanedPage, name));
                specie.put("capabilities", parseCapabilities(cleanedPage));
//                specie.put("evolutionChain", parseEvolutionChain(cleanedPage));
//                specie.put("highAbilities", parseHighAbilities(cleanedPage));
//                specie.put("advancedAbilities", parseAdvancedAbilities(cleanedPage));
//                specie.put("basicAbilities", parseBasicAbilities(cleanedPage));
//                specie.put("tutorMoves", parseTutorMoves(cleanedPage));
//                specie.put("eggMoves", parseEggMoves(cleanedPage));
//                specie.put("machineMoves", parseMachineMoves(cleanedPage));
//                specie.put("genderRatio", parseGenderRatio(cleanedPage));
//                specie.put("levelUpMoves", parseLevelUpMoves(cleanedPage));
//                specie.put("types", parseSlashSeparatedList(cleanedPage, "Type"));
//                specie.put("eggGroups", parseSlashSeparatedList(cleanedPage, "Egg Group"));
//                specie.put("averageHatchRate", parseNamedInteger(cleanedPage, "Average Hatch Rate:"));
//                specie.put("stats", parseStats(cleanedPage));
//                specie.put("skills", parseSkills(cleanedPage));
//                specie.put("height", parseHeight(cleanedPage));
//                specie.put("weight", parseWeight(cleanedPage));
//                specie.put("diets", parseCommaSeparatedList(cleanedPage, "Diet"));
//                specie.put("habitats", parseCommaSeparatedList(cleanedPage, "Habitat"));
                species.put(name, specie);
                System.out.println(specie);
            }
        }
//        System.out.println(species);
        return species;
    }

    private String cleanPage(String page) {
        String cleanedPage = page;
        // Connect words the are separated by a newline
        cleanedPage = cleanedPage.replaceAll("(\\w)-\r\n(\\w)", "$1$2");
        // Fix typo for Drilbur
        cleanedPage = cleanedPage.replace('!', '1');
        // Fix ’ to be a ' on all pages (Like in the move Forest's Curse verses Forest’s Curse)
        cleanedPage = cleanedPage.replace('’', '\'');
        // Remove "add" typos in egg group (Phantump, Trevenant, and others)
        cleanedPage = cleanedPage.replaceAll(" and ", " / ");
        // Fix ” to be a " on all pages (Like on Bulbasaur)
        cleanedPage = cleanedPage.replace('”', '\"');
        // Put all stages of evolution onto 1 line (For example of Shelmet)
        cleanedPage = cleanedPage.replaceAll("(\\d +- +[\\S]+.*)\r\n ( \\D)", "$1$2");
        // Fix é to be e
        cleanedPage = cleanedPage.replaceAll("é", "e");
        // Fix É to be E
        cleanedPage = cleanedPage.replaceAll("É", "E");
        // Fix Thunderstone to be Thunder Stone
        cleanedPage = cleanedPage.replaceAll("Thunderstone", "Thunder Stone");
        // Fix Scizor name to remove the "."
        cleanedPage = cleanedPage.replaceAll("\\. SCIZOR", "Scizor");
        // Change Porygon-Z to be Porygon-z
        cleanedPage = cleanedPage.replaceAll("n-Z", "n-z");
        // Change "Min." to "Minimum" for Mantyke and Mantine
        cleanedPage = cleanedPage.replaceAll("Min\\.", "Minimum");
        // Fix Typo "wiht a" to "with"
        cleanedPage = cleanedPage.replaceAll("wiht a", "with");
        // Fix Empoleon, Marshtomp, SandSlash, and Grotle who has both "High Ability" and "High Abilities"
        cleanedPage = cleanedPage.replaceAll("(High Ability:\\s*[\\w ]*)\r\n\\s*High Abilities: ([\\w ]*)", "$1 / $2");
        // Fix typo in Sligoo to be Sliggoo
        cleanedPage = cleanedPage.replaceAll("Sligoo", "Sliggoo");
        // Fix typo for "A3" to be A3 Surf (Nidoqueen, Exploud)
        cleanedPage = cleanedPage.replaceAll("A3(?! Surf)", "A3 Surf,");
        // Fix typo for "A2" to be A2 Fly (Pelipper, Braviary, Tropius, and Rufflet)
        cleanedPage = cleanedPage.replaceAll("A2(?! Fly)", "A2 Fly,");
        // Change Ditto Diet to be more parseable
        cleanedPage = cleanedPage.replaceAll("Diet can change with its form", "Ditto");
        // Fix typo in Snivy ". lbs." to be " lbs."
        cleanedPage = cleanedPage.replaceAll("\\. lbs\\.", " lbs.");
        // Fix typo in Kricketune for "Jump 3," to be "Jump 3/3,"
        cleanedPage = cleanedPage.replaceAll("Jump 3,", "Jump 3/3");
        // Fix typo in Bayleef for "Ath " to be "Athl"
        cleanedPage = cleanedPage.replaceAll("Ath ", "Athl");
        // Fix typo in Servine for "Percep," to be "Percep"
        cleanedPage = cleanedPage.replaceAll("Percep,", "Percep");
        return cleanedPage;
    }

    private String parseName(String text) {
        String name = text.substring(text.indexOf('\n') + 1, text.indexOf("Base")).trim();
        name = name.substring(0, 1) + name.substring(1).toLowerCase();
        return name;
    }

    private Map<String, Object> parseHeight(String page) {
        final Map<String, Object> height = new HashMap<>();
        final String heightRegex = "Height\\s*:\\s*(\\d*' \\d*\") \\/ (\\d*.\\d*) *m \\((\\w*)\\)";
        final Matcher heightMatcher = Pattern.compile(heightRegex).matcher(page);
        if (heightMatcher.find()) {
            final String ft = heightMatcher.group(1);
            final float m = parseFloat(heightMatcher.group(2));
            final String size = heightMatcher.group(3);
            height.put("ft", ft);
            height.put("m", m);
            height.put("size", size);
            return height;
        } else {
            throw new Error("ERROR: All pokemon species should have a height to parse\n" + page);
        }
    }

    private Map<String, Object> parseWeight(String page) {
        final Map<String, Object> weight = new HashMap<>();
        final String weightRegex = "Weight\\s*:\\s*(\\d*.\\d*) *lbs\\. \\/ (\\d*.\\d*) *kg \\((\\w*)\\)";
        final Matcher weightMatcher = Pattern.compile(weightRegex).matcher(page);
        if (weightMatcher.find()) {
            final float lbs = parseFloat(weightMatcher.group(1));
            final float kg = parseFloat(weightMatcher.group(2));
            final int weightClass = parseInt(weightMatcher.group(3));
            weight.put("lbs", lbs);
            weight.put("kg", kg);
            weight.put("class", weightClass);
            return weight;
        } else {
            throw new Error("ERROR: All pokemon species should have a weight to parse\n" + page);
        }
    }

    private Map<String, Integer> parseJump(String page) {
        final Map<String, Integer> jump = new HashMap<>();
        final String jumpRegex = "Jump\\s*(\\d*)\\/(\\d*)";
        final Matcher jumpMatcher = Pattern.compile(jumpRegex).matcher(page);
        if (jumpMatcher.find()) {
            final int longJump = parseInt(jumpMatcher.group(1));
            final int highJump = parseInt(jumpMatcher.group(2));
            jump.put("long", longJump);
            jump.put("high", highJump);
            return jump;
        } else {
            throw new Error("ERROR: All pokemon species should have a jump to parse\n" + page);
        }
    }

    private Integer parseNamedInteger(String text, String name) {
        final String namedIntegerRegex = name + "\\s*(\\d+)";
        Matcher namedIntegerMatcher = Pattern.compile(namedIntegerRegex).matcher(text);
        if (namedIntegerMatcher.find()) {
            return parseInt(namedIntegerMatcher.group(1));
        }
        return null;
    }

    private List<String> parseSlashSeparatedList(String text, String name) {
        final List<String> items = new ArrayList<>();
        final String namedSlashSeparatedListRegex = name + "\\s*:\\s*(\\w*)(?:\\s*\\/\\s*(\\w*))?";
        final Matcher matcher = Pattern.compile(namedSlashSeparatedListRegex).matcher(text);
        if (matcher.find()) {
            final String item1 = matcher.group(1);
            final String item2 = matcher.group(2);
            items.add(item1);
            if (item2 != null) items.add(item2);
        } else {
            throw new Error("No match was found in:\n" + text + "\n For regex string " + namedSlashSeparatedListRegex);
        }
        return items;
    }

    private List<String> parseCommaSeparatedList(String text, String name) {
        final List<String> items;
        final String p = name + " *:(.*)";
        final Matcher matcher = Pattern.compile(p).matcher(text);
        if (matcher.find()) {
            String itemsString = matcher.group(1);
            items = asList(itemsString.split(","));
            for (int i = 0; i < items.size(); i++) {
                items.set(i, items.get(i).trim());
            }
            return items;
        } else {
            return null;
        }
    }

    private Map<String, Integer> parseStats(String page) {
        final Map<String, Integer> stats = new HashMap<>();
        stats.put("hp", parseNamedInteger(page, "HP:"));
        stats.put("attack", parseNamedInteger(page, "Attack:"));
        stats.put("defense", parseNamedInteger(page, "Defense:"));
        stats.put("specialAttack", parseNamedInteger(page, "Special Attack:"));
        stats.put("specialDefense", parseNamedInteger(page, "Special Defense:"));
        stats.put("speed", parseNamedInteger(page, "Speed:"));
        return stats;
    }

    private Map<String, Object> parseSkill(String page, String name) {
        final Map<String, Object> skill = new HashMap<>();
        final String skillRegex = name + "\\s*((\\d*)d(\\d*)(?:\\+(\\d*))?)";
        final Matcher matcher = Pattern.compile(skillRegex).matcher(page);
        if (matcher.find()) {
            final String value = matcher.group(1);
            final int diceNumber = parseInt(matcher.group(2));
            final int diceFaces = parseInt(matcher.group(3));
            final String buffString = matcher.group(4);
            final int buff;
            if (buffString == null) {
                buff = 0;
            } else {
                buff = parseInt(buffString);
            }
            skill.put("value", value);
            skill.put("numberOfDice", diceNumber);
            skill.put("diceFaces", diceFaces);
            skill.put("buff", buff);
            return skill;
        } else {
            throw new Error("ERROR: All pokemon species should have a " + name + " to parse\n" + page);
        }
    }

    private Map<String, Map<String, Object>> parseSkills(String page) {
        final Map<String, Map<String, Object>> skills = new HashMap<>();
        skills.put("athletics", parseSkill(page, "Athl"));
        skills.put("acrobatics", parseSkill(page, "Acro"));
        skills.put("combat", parseSkill(page, "Combat"));
        skills.put("stealth", parseSkill(page, "Stealth"));
        skills.put("perception", parseSkill(page, "Percep"));
        skills.put("focus", parseSkill(page, "Focus"));
        return skills;
    }

    private Map<String, Object> parseGenderRatio(String page) {
        final Map<String, Object> genderRatio = new HashMap<>();
        final String genderRegex = "Gender Ratio\\s*:\\s*(.*)\r\n";
        final Matcher genderMatcher = Pattern.compile(genderRegex).matcher(page);
        if (genderMatcher.find()) {
            final String result = genderMatcher.group(1);
            final String ratioRegex = "([\\d\\.]+)%\\s*M\\s*(?:\\/)?\\s*([\\d\\.]+)%\\s*F";
            final Matcher ratioMatcher = Pattern.compile(ratioRegex).matcher(result);
            if (ratioMatcher.find()) {
                final float maleRatio = parseFloat(ratioMatcher.group(1));
                final float femaleRatio = parseFloat(ratioMatcher.group(2));
                genderRatio.put("male", maleRatio);
                genderRatio.put("female", femaleRatio);
                genderRatio.put("gendered", true);
                return genderRatio;
            } else if (result.equals("Hermaphrodite")) {
                genderRatio.put("hermaphrodite", true);
                return genderRatio;
            } else if (result.equals("No Gender") || result.equals("Genderless")) {
                genderRatio.put("gendered", false);
                return genderRatio;
            }
        }
        throw new Error("ERROR: All pokemon species should have a gender ratio to parse\n" + page);
    }

    private Map<String, List<String>> parseLevelUpMoves(String page) {
        final Map<String, List<String>> levelUpMoves = new HashMap<>();
        final String levelUpMoveRegex = "(\\d+)\\s+(['a-zA-Z- ]+)\\s+-";
        final Matcher levelUpMoveMatcher = Pattern.compile(levelUpMoveRegex).matcher(page);
        while (levelUpMoveMatcher.find()) {
            final String level = levelUpMoveMatcher.group(1).trim();
            final String move = levelUpMoveMatcher.group(2).trim();
            // Java 8 is the best and I love it for the reason below <3
            levelUpMoves.computeIfAbsent(level, v -> new ArrayList<>()).add(move);
        }
        return levelUpMoves;
    }

    private List<String> parseMachineMoves(String page) {
        final List<String> machineMoves = new ArrayList<>();
        final String machineMoveListRegex = "TM\\/HM Move List\\s*([\\s\\d\\w,-]*?)(?:Egg|Tutor)";
        final Matcher machineMoveListMatcher = Pattern.compile(machineMoveListRegex).matcher(page);
        final String machineMoveRegex = "\\w*? ([a-zA-Z -]+)";
        final Pattern machineMovePattern = Pattern.compile(machineMoveRegex);
        if (machineMoveListMatcher.find()) {
            String result = machineMoveListMatcher.group(1);
            result = result.replaceAll("\r\n", "");
            if (result.equals("None") || result.isEmpty()) return null;
            final List<String> machineMoveStrings = asList(result.trim().split(","));
            Matcher machineMoveMatcher;
            for (String machineMoveString : machineMoveStrings) {
                machineMoveMatcher = machineMovePattern.matcher(machineMoveString.trim());
                if (machineMoveMatcher.find()) {
                    machineMoves.add(machineMoveMatcher.group(1));
                } else {
                    throw new Error("Machine move: \"" + machineMoveString.trim() + "\" was not parsed correctly");
                }
            }
            return machineMoves;
        }
        return null;
    }

    private Map<String, Object> parseTutorMoves(String page) {
        String cleanPage = page.replaceAll("Mega Evolution(.*\r\n)*", "");
        final Map<String, Object> tutorMoves = new HashMap<>();
        final String tutorMoveListRegex = "Tutor *Move *List *\r\n((?:[a-zA-Z- ,\\(\\)]+\r\n)+)(?:(?=Mega Evolution)|)";
        final Matcher tutorMoveListMatcher = Pattern.compile(tutorMoveListRegex).matcher(cleanPage);
        final String tutorMoveRegex = "([a-zA-Z- ]+)(?: *(\\( *N *\\)))?";
        final Pattern tutorMovePattern = Pattern.compile(tutorMoveRegex);
        if (tutorMoveListMatcher.find()) {
            String result = tutorMoveListMatcher.group(1);
            result = result.replaceAll("\r\n", "");
            final List<String> tutorMoveStrings = asList(result.trim().split(","));
            Matcher tutorMoveMatcher;
            for (String tutorMoveString : tutorMoveStrings) {
                tutorMoveMatcher = tutorMovePattern.matcher(tutorMoveString.trim());
                if (tutorMoveMatcher.find()) {
                    final Map<String, Object> tutorMove = new HashMap<>();
                    final String move = tutorMoveMatcher.group(1);
                    final Boolean isHeartScaleMove = tutorMoveMatcher.group(2) != null;
                    if (isHeartScaleMove) tutorMove.put("heartScaleMove", true);
                    tutorMoves.put(move, tutorMove);
                } else {
                    throw new Error("Tutor move: \"" + tutorMoveString.trim() + "\" was not parsed correctly");
                }
            }
            return tutorMoves;
        }
        return null;
    }

    private List<String> parseEggMoves(String page) {
        final List<String> eggMoves;
        final String eggMoveListRegex = "Egg Move List\\s*([-a-zA-Z,\\s]+)Tutor Move List";
        final Matcher eggMoveListMatcher = Pattern.compile(eggMoveListRegex).matcher(page);
        if (eggMoveListMatcher.find()) {
            String result = eggMoveListMatcher.group(1);
            result = result.replaceAll("\r\n", "");
            eggMoves = asList(result.trim().split(","));
            for (int i = 0; i < eggMoves.size(); i++) {
                eggMoves.set(i, eggMoves.get(i).trim());
            }
            return eggMoves;
        }
        return null;
    }

    private List<String> parseBasicAbilities(String page) {
        final List<String> basicAbilities = new ArrayList<>();
        final String basicAbilityRegex = "Basic\\s*Ability\\s*\\d+\\s*:\\s*(.*)\\s";
        final Matcher matcher = Pattern.compile(basicAbilityRegex).matcher(page);
        while (matcher.find()) {
            basicAbilities.add(matcher.group(1));
        }
        return basicAbilities;
    }

    private List<String> parseAdvancedAbilities(String page) {
        final List<String> advancedAbilities = new ArrayList<>();
        final String advancedAbilityRegex = "Adv\\s*Ability\\s*\\d+\\s*:\\s*(.*)\\s";
        final Matcher matcher = Pattern.compile(advancedAbilityRegex).matcher(page);
        while (matcher.find()) {
            advancedAbilities.add(matcher.group(1));
        }
        return advancedAbilities;
    }

    private List<String> parseHighAbilities(String page) {
        final List<String> highAbilities;
        final String highAbilityRegex = "High\\s*Ability\\s*:\\s*((?:.*\\s*\\/)*\\s*.*)";
        final Matcher matcher = Pattern.compile(highAbilityRegex).matcher(page);
        if (matcher.find()) {
            final String result = matcher.group(1);
            highAbilities = asList(result.split("\\/"));
            for (int i = 0; i < highAbilities.size(); i++) {
                highAbilities.set(i, highAbilities.get(i).trim());
            }
            return highAbilities;
        }
        return null;
    }

    private List<Map<String, Object>> parseEvolutionChain(String page) {
        final List<Map<String, Object>> evolutionChain = new ArrayList<>();
        final String evolutionStageRegex = "(\\d) +- +([\\w-]*) *(.*)";
        final Matcher matcher = Pattern.compile(evolutionStageRegex).matcher(page);
        while (matcher.find()) {
            final Map<String, Object> evolution = new HashMap<>();
            final int stage = parseInt(matcher.group(1));
            final String pokemon = matcher.group(2);
            final String conditions = matcher.group(3).replaceAll(" (?= )", " ").trim();
            evolution.put("stage", stage);
            evolution.put("pokemon", pokemon);
            evolution.put("conditions", parseEvolutionCondition(conditions));
            evolutionChain.add(evolution);
        }
        return evolutionChain;
    }

    private String parseInteractCondition(String conditions) {
        final String interactRegex = "[Ii]nteract with (\\w*)";
        final Matcher interactMatcher = Pattern.compile(interactRegex).matcher(conditions);
        if (interactMatcher.find()) {
            return interactMatcher.group(1);
        }
        return null;
    }

    private Integer parseMinLevelCondition(String conditions) {
        final String minLevelRegex = "M\\w*m (\\d+)";
        final Matcher minLevelMatcher = Pattern.compile(minLevelRegex).matcher(conditions);
        if (minLevelMatcher.find()) {
            return parseInt(minLevelMatcher.group(1));
        } else {
            return null;
        }
    }

    private String parseGenderCondition(String conditions) {
        if (conditions.toLowerCase().contains("female")) return "female";
        else if (conditions.toLowerCase().contains("male")) return "male";
        else return null;
    }

    private String parseHeldItemCondition(String conditions) {
        final String heldItemRegex = "Holding +(.*)? +(?=M\\w*m)";
        final Matcher heldItemMatcher = Pattern.compile(heldItemRegex).matcher(conditions);
        if (heldItemMatcher.find()) {
            return heldItemMatcher.group(1).trim();
        }
        return null;
    }

    private String parseUseItemCondition(String conditions) {
        final String useItemRegex = "\\w* Stone";
        final Matcher heldItemMatcher = Pattern.compile(useItemRegex).matcher(conditions);
        if (heldItemMatcher.find()) {
            final String result = heldItemMatcher.group(0).trim();
            if (!result.contains("Oval")) {
                return result;
            }
        }
        return null;
    }

    private String parseLearnCondition(String conditions) {
        final String learnRegex = "Learn +(\\S*(?: \\S*)?)";
        final Matcher learnMatcher = Pattern.compile(learnRegex).matcher(conditions);
        if (learnMatcher.find()) {
            return learnMatcher.group(1);
        }
        return null;
    }

    private String parseTimeOfDayCondition(String conditions) {
        if (conditions.toLowerCase().contains("night")) return "night";
        else if (conditions.toLowerCase().contains("day")) return "day";
        else return null;
    }

    private Map<String, Object> parseEvolutionCondition(String conditions) {
        final Map<String, Object> evolutionCondition = new HashMap<>();
        if (conditions.trim().isEmpty()) return evolutionCondition;
        evolutionCondition.put("minLevel", parseMinLevelCondition(conditions));
        evolutionCondition.put("gender", parseGenderCondition(conditions));
        evolutionCondition.put("timeOfDay", parseTimeOfDayCondition(conditions));
        evolutionCondition.put("heldItem", parseHeldItemCondition(conditions));
        evolutionCondition.put("useItem", parseUseItemCondition(conditions));
        evolutionCondition.put("learnMove", parseLearnCondition(conditions));
        evolutionCondition.put("interact", parseInteractCondition(conditions));
        return evolutionCondition;
    }

    private String parseCapabilityInformation(String page) {
        final String capabilityInformationRegex = "Capability List *\r\n([\\w\\W]+?) *Skill List";
        final Matcher capabilityInformationMatcher = Pattern.compile(capabilityInformationRegex).matcher(page);
        if (capabilityInformationMatcher.find()) {
            return capabilityInformationMatcher.group(1).replaceAll("\r\n", "");
        } else {
            return "";
        }
    }

    private List<String> parseSpecialCapabilities(String capabilityInformation) {
        final List<String> specialCapabilities = new ArrayList<>();
        for(String possibleSpecialCapability : possibleSpecialCapabilities) {
            if (capabilityInformation.contains(possibleSpecialCapability)) {
                specialCapabilities.add(possibleSpecialCapability);
            }
        }
        return specialCapabilities;
    }

    private Map<String, Object> parseCapabilities(String page) {
        final Map<String, Object> capabilities = new HashMap<>();
        final String capabilityInformation = parseCapabilityInformation(page);
        final List<String> specialCapabilities = parseSpecialCapabilities(capabilityInformation);
        capabilities.put("specialCapabilities", specialCapabilities);
        capabilities.put("sky", parseNamedInteger(capabilityInformation, "Sky"));
        capabilities.put("swim", parseNamedInteger(capabilityInformation, "Swim"));
        capabilities.put("overland", parseNamedInteger(capabilityInformation, "Overland"));
        capabilities.put("levitate", parseNamedInteger(capabilityInformation, "Levitate"));
        capabilities.put("power", parseNamedInteger(capabilityInformation, "Power"));
        capabilities.put("burrow", parseNamedInteger(capabilityInformation, "Burrow"));
        capabilities.put("jump", parseJump(capabilityInformation));
        capabilities.put("mountable", parseNamedInteger(capabilityInformation, "Mountable"));
        capabilities.put("naturewalk", parseNaturewalk(capabilityInformation));
        return capabilities;
    }

    private Integer parseStage(String page, String pokemonName) {
        final String stageRegex = "(\\d) +- +" + pokemonName;
        final Matcher stageMatcher = Pattern.compile(stageRegex).matcher(page);
        if (stageMatcher.find()) {
            return parseInt(stageMatcher.group(1));
        }
        return null;
    }

    private List<Map<String, Object>> parseMegaEvolutions(String page) {
        final List<Map<String, Object>> megaEvolutions = new ArrayList<>();
        return megaEvolutions;
    }

    private List<String> parseNaturewalk(String capabilityInformation) {
        final String naturewalkRegex = "[Nn]aturewalk *\\((.*)\\)";
        final Matcher naturewalkMatcher = Pattern.compile(naturewalkRegex).matcher(capabilityInformation);
        if (naturewalkMatcher.find()) {
            // Trim all of the elements in a list of strings
            // Credit: https://stackoverflow.com/questions/36430727/whats-the-best-way-to-trim-all-elements-in-a-liststring
            return Arrays.stream(naturewalkMatcher.group(1)
                    .split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
        }
        return null;
    }


}
