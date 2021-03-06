package parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;

public class SpeciesParser extends PageParser {
    private final List<String> problemSpecies = asList(
            "Pumpkaboo", // Too many forms
            "Gourgeist" // Too many forms
    );

    // A list of a the special capabilities
    // Naturewalk and Mountable are not included in this list because they require there own parser
    private final List<String> possibleSpecialCapabilities = asList("Alluring", "Amorphous", "Aura Reader", "Aura Pulse",
            "Blindsense", "Bloom", "Blender", "Chilled", "Darkvision", "Dead Silent", "Delta model.Evolution", "Dream Mist",
            "Dream Reader", "Egg Warmer", "Firestarter", "Fortune", "Fountain", "Freezer", "Gather Unown", "Gilled",
            "Glow", "Groundshaper", "Guster", "Heart Gift", "Heater", "Herb Growth", "Honey Gather", "Illusionist",
            "Inflatable", "Invisibility", "Juicer", "Keystone Warp", "Letter Press", "Living Weapon", "Magnetic",
            "Marsupial", "Materializer", "Milk Collection", "Mindlock", "Mushroom Harvest", "Mushroom Harvest",
            "Pack Mon", "Pearl Creation", "Phasing", "Planter", "Premonition", "Reach", "Shadow Meld", "Shapeshifter",
            "Shrinkable", "Soulless", "Split model.Evolution", "Sprouter", "Stealth", "Telekinetic", "Telepath", "Threaded",
            "Tracker", "Tremorsense", "Underdog", "Volatile Bomb", "Wallclimber", "Weathershape", "Wielder", "Wired",
            "X-Ray Vision", "Zapper"
    );

    public JSONObject parse(List<String> pages) {
        JSONObject species = new JSONObject();
        for (String page: pages) {
            String cleanedPage = clean(page);
//            System.out.println(cleanedPage);
            final Map<String, Object> specie = new HashMap<>();
            String name = parseName(cleanedPage);
//            System.out.println(name);
            specie.put("name", name);
            if (problemSpecies.contains(name) || name.contains("form") && !name.equals("Castform")) {
//                System.out.println(specie);
                species.put(name, specie);
            } else {
                specie.put("megaEvolutions", parseMegaEvolutions(cleanedPage));
                int stage = parseStage(cleanedPage, name);
                specie.put("stage", parseStage(cleanedPage, name));
                specie.put("evolvesFrom", parseEvolvesFrom(cleanedPage, stage));
                specie.put("evolvesTo", parseEvolvesTo(cleanedPage, stage));
                specie.put("capabilities", parseCapabilities(cleanedPage));
                specie.put("highAbilities", parseLabeledDelimitedList("High\\s*Ability\\s*:", "/", page));
                specie.put("advancedAbilities", parseAdvancedAbilities(cleanedPage));
                specie.put("basicAbilities", parseBasicAbilities(cleanedPage));
                specie.put("tutorMoves", parseTutorMoves(cleanedPage));
                specie.put("eggMoves", parseEggMoves(cleanedPage));
                specie.put("machineMoves", parseMachineMoves(cleanedPage));
                specie.put("genderRatio", parseGenderRatio(cleanedPage));
                specie.put("levelUpMoves", parseLevelUpMoves(cleanedPage));
                specie.put("types", parseLabeledDelimitedList("Type *:", "/", cleanedPage));
                specie.put("eggGroups", parseLabeledDelimitedList("Egg Group *:", "/", cleanedPage));
                specie.put("averageHatchRate", parseLabeledInteger("Average Hatch Rate:", cleanedPage));
                specie.put("stats", parseStats(cleanedPage));
                specie.put("skills", parseSkills(cleanedPage));
                specie.put("height", parseHeight(cleanedPage));
                specie.put("weight", parseWeight(cleanedPage));
                specie.put("diets", parseLabeledDelimitedList("Diet *:", ",", cleanedPage));
                specie.put("habitats", parseLabeledDelimitedList("Habitat *:", ",", cleanedPage));
                species.put(name, specie);
//                System.out.println(specie);
            }
        }
//        System.out.println(species);
        return species;
    }

    protected String clean(String page) {
        String cleanedPage = page;
        // Remove page numbers
        cleanedPage = cleanedPage.replaceAll("^\\d{2,3}\\s+", "");
        // Connect words the are separated by a newline
        cleanedPage = cleanedPage.replaceAll("(\\w)-[\r\n]+(\\w)", "$1$2");
        // Fix typo for Drilbur
        cleanedPage = cleanedPage.replace('!', '1');
        // Replace special characters
        cleanedPage = cleanSpecialCharacters(cleanedPage);
        // Remove "add" typos in egg group (Phantump, Trevenant, and others)
        cleanedPage = cleanedPage.replaceAll(" and ", " / ");
        // Put all stages of evolution onto 1 line (For example of Shelmet)
        cleanedPage = cleanedPage.replaceAll("(\\d +- +[\\S]+.*)[\r\n]+ ( \\D)", "$1$2");
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
        cleanedPage = cleanedPage.replaceAll("(High Ability:\\s*[\\w ]*)[\r\n]+\\s*High Abilities: ([\\w ]*)", "$1 / $2");
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
        // Fix typo in Kricketune for "model.Jump 3," to be "model.Jump 3/3,"
        cleanedPage = cleanedPage.replaceAll("Jump 3,", "model.Jump 3/3");
        // Fix typo in Bayleef for "Ath " to be "Athl"
        cleanedPage = cleanedPage.replaceAll("Ath ", "Athl");
        // Fix typo in Servine for "Percep," to be "Percep"
        cleanedPage = cleanedPage.replaceAll("Percep,", "Percep");
        // Fix typo in Arbok for "Carnviore." to be "Carnviore"
        cleanedPage = cleanedPage.replaceAll("Carnviore\\.", "Carnviore");
        // Fix typo in "QUIILLADIN" to be "Quilladin"
        cleanedPage = cleanedPage.replaceAll("QUIILLADIN", "Quilladin");
        // Fix typo "NIDORAN (F)" to be "NIDORAN F"
        cleanedPage = cleanedPage.replaceAll("NIDORAN \\(F\\)", "NIDORANF");
        cleanedPage = cleanedPage.replaceAll("Nidoran F", "Nidoranf");
        // Fix typo "NIDORAN (M)" to be "NIDORAN M"
        cleanedPage = cleanedPage.replaceAll("NIDORAN \\(M\\)", "NIDORANM");
        cleanedPage = cleanedPage.replaceAll("Nidoran M", "Nidoranm");
        // Fix "Mime Jr." to be "Mime jr."
        cleanedPage = cleanedPage.replaceAll("[Mm][Ii][Mm][Ee] [Jj][Rr]", "Mimejr");
        cleanedPage = cleanedPage.replaceAll("M[Rr]\\. M[Ii][Mm][Ee]", "Mr.mime");
        // Fix typo on Poliwhirl page
        cleanedPage = cleanedPage.replaceAll("3 - Holding", "3 - Politoed Holding");
        // Fix typo on Silcoon page for Habitat
        cleanedPage = cleanedPage.replaceAll("(Habitat *: *)[\r\n]+", "$1");
        // Fix Porygon-Z to have a "Diet:" instead of a "Biology:"
        cleanedPage = cleanedPage.replaceAll("Biology *:", "Diet :");
        return cleanedPage;
    }

    private String parseName(String page) {
        final String nameRegex = "^(.*)";
        final Matcher nameMatcher = Pattern.compile(nameRegex).matcher(page);
        if (nameMatcher.find()) {
            String name = nameMatcher.group(1).trim();
            name = name.substring(0, 1) + name.substring(1).toLowerCase();
            return name;
        }
        throw new Error("All species should have a name");
    }

    private Map<String, Object> parseHeight(String page) {
        final Map<String, Object> height = new HashMap<>();
        final String heightRegex = "Height\\s*:\\s*(\\d*' \\d*\") / (\\d*.\\d*) *m \\((\\w*)\\)";
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
        final String weightRegex = "Weight\\s*:\\s*(\\d*.\\d*) *lbs\\. / (\\d*.\\d*) *kg \\((\\w*)\\)";
        final Matcher weightMatcher = Pattern.compile(weightRegex).matcher(page);
        if (weightMatcher.find()) {
            final float lbs = parseFloat(weightMatcher.group(1));
            final float kg = parseFloat(weightMatcher.group(2));
            final int weightClass = parseInt(weightMatcher.group(3));
            weight.put("lbs", lbs);
            weight.put("kg", kg);
            weight.put("weightClass", weightClass);
            return weight;
        } else {
            throw new Error("ERROR: All pokemon species should have a weight to parse\n" + page);
        }
    }

    private Map<String, Integer> parseJump(String page) {
        final Map<String, Integer> jump = new HashMap<>();
        final String jumpRegex = "Jump\\s*(\\d*)/(\\d*)";
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

    private Map<String, Integer> parseStats(String page) {
        final Map<String, Integer> stats = new HashMap<>();
        stats.put("hp", parseLabeledInteger("HP:", page));
        stats.put("attack", parseLabeledInteger("Attack:", page));
        stats.put("defense", parseLabeledInteger("Defense:", page));
        stats.put("specialAttack", parseLabeledInteger("Special Attack:", page));
        stats.put("specialDefense", parseLabeledInteger("Special Defense:", page));
        stats.put("speed", parseLabeledInteger("Speed:", page));
        return stats;
    }

    private Map<String, Object> parseSkill(String name, String page) {
        final Map<String, Object> skill = new HashMap<>();
        final String skillRegex = name + "\\s*((\\d+)d(\\d+)(?:\\+(\\d+))?)";
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
        skills.put("athletics", parseSkill("Athl", page));
        skills.put("acrobatics", parseSkill("Acro", page));
        skills.put("combat", parseSkill("Combat", page));
        skills.put("stealth", parseSkill("Stealth", page));
        skills.put("perception", parseSkill("Percep", page));
        skills.put("focus", parseSkill("Focus", page));
        return skills;
    }

    private Map<String, Object> parseGenderRatio(String page) {
        final Map<String, Object> genderRatio = new HashMap<>();
        final String genderInformation = parseLabeledString("Gender Ratio *:", page);
        if (genderInformation != null) {
            final String ratioRegex = "([\\d.]+)%\\s*M\\s*(?:/)?\\s*([\\d.]+)%\\s*F";
            final Matcher ratioMatcher = Pattern.compile(ratioRegex).matcher(genderInformation);
            if (ratioMatcher.find()) {
                final float maleRatio = parseFloat(ratioMatcher.group(1));
                final float femaleRatio = parseFloat(ratioMatcher.group(2));
                genderRatio.put("male", maleRatio);
                genderRatio.put("female", femaleRatio);
                genderRatio.put("gendered", true);
                return genderRatio;
            } else if (genderInformation.equals("Hermaphrodite")) {
                genderRatio.put("hermaphrodite", true);
                return genderRatio;
            } else if (genderInformation.equals("No Gender") || genderInformation.equals("Genderless")) {
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

    @Nullable
    private List<String> parseMachineMoves(String page) {
        final List<String> machineMoves = new ArrayList<>();
        final String machineMoveListRegex = "TM/HM Move List\\s*([\\s\\d\\w,-]*?)(?:Egg|Tutor)";
        final Matcher machineMoveListMatcher = Pattern.compile(machineMoveListRegex).matcher(page);
        final String machineMoveRegex = "\\w*? ([a-zA-Z -]+)";
        final Pattern machineMovePattern = Pattern.compile(machineMoveRegex);
        if (machineMoveListMatcher.find()) {
            String delimitedMachineMoveList = machineMoveListMatcher.group(1);
            delimitedMachineMoveList = consolidateLines(delimitedMachineMoveList);
            if (delimitedMachineMoveList.equals("None") || delimitedMachineMoveList.isEmpty()) return null;
            final List<String> machineMoveStrings = parseDelimitedList(delimitedMachineMoveList, ",");
            Matcher machineMoveMatcher;
            for (String machineMoveString : machineMoveStrings) {
                machineMoveMatcher = machineMovePattern.matcher(machineMoveString.trim());
                if (machineMoveMatcher.find()) {
                    final String machineMoveName = machineMoveMatcher.group(1);
                    machineMoves.add(machineMoveName);
                } else {
                    throw new Error("Machine move: \"" + machineMoveString.trim() + "\" was not parsed correctly");
                }
            }
            return machineMoves;
        }
        return null;
    }

    @Nullable
    private Map<String, Object> parseTutorMoves(String page) {
        final Map<String, Object> tutorMoves = new HashMap<>();
        final String tutorMoveListRegex = "Tutor *Move *List\\s+([\\w\\W]*?)(?=Mega Evolution|$)";
        final Matcher tutorMoveListMatcher = Pattern.compile(tutorMoveListRegex).matcher(page);
        if (tutorMoveListMatcher.find()) {
            String delimitedTutorMoveList = tutorMoveListMatcher.group(1);
            delimitedTutorMoveList = consolidateLines(delimitedTutorMoveList);
            final List<String> tutorMoveStrings = parseDelimitedList(delimitedTutorMoveList, ",");
            final String tutorMoveRegex = "([a-zA-Z- ]+)(?: *(\\( *N *\\)))?";
            final Pattern tutorMovePattern = Pattern.compile(tutorMoveRegex);
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

    @Nullable
    private List<String> parseEggMoves(String page) {
        final String eggMoveListRegex = "Egg Move List\\s*([-a-zA-Z,\\s]+)Tutor Move List";
        final Matcher eggMoveListMatcher = Pattern.compile(eggMoveListRegex).matcher(page);
        if (eggMoveListMatcher.find()) {
            String delimitedEggMoveList = eggMoveListMatcher.group(1);
            delimitedEggMoveList = consolidateLines(delimitedEggMoveList);
            if (delimitedEggMoveList.isEmpty()) return null;
            final List<String> eggMoves = parseDelimitedList(delimitedEggMoveList, ",");
            if (eggMoves.isEmpty()) return null;
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

    @Nullable
    private String parseInteractCondition(String conditions) {
        final String interactRegex = "[Ii]nteract with (\\w*)";
        final Matcher interactMatcher = Pattern.compile(interactRegex).matcher(conditions);
        if (interactMatcher.find()) {
            return interactMatcher.group(1);
        }
        return null;
    }

    @Nullable
    private Integer parseMinLevelCondition(String conditions) {
        final String minLevelRegex = "M\\w*m (\\d+)";
        final Matcher minLevelMatcher = Pattern.compile(minLevelRegex).matcher(conditions);
        if (minLevelMatcher.find()) {
            return parseInt(minLevelMatcher.group(1));
        } else {
            return null;
        }
    }

    @Nullable
    private String parseGenderCondition(String conditions) {
        if (conditions.toLowerCase().contains("female")) return "female";
        else if (conditions.toLowerCase().contains("male")) return "male";
        else return null;
    }

    @Nullable
    private String parseHeldItemCondition(String conditions) {
        final String heldItemRegex = "Holding +(.*)? +(?=M\\w*m)";
        final Matcher heldItemMatcher = Pattern.compile(heldItemRegex).matcher(conditions);
        if (heldItemMatcher.find()) {
            return heldItemMatcher.group(1).trim();
        }
        return null;
    }

    @Nullable
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

    @Nullable
    private String parseLearnCondition(String conditions) {
        final String learnRegex = "Learn +(\\S*(?: \\S*)?)";
        final Matcher learnMatcher = Pattern.compile(learnRegex).matcher(conditions);
        if (learnMatcher.find()) {
            return learnMatcher.group(1);
        }
        return null;
    }

    @Nullable
    private String parseTimeOfDayCondition(String conditions) {
        if (conditions.toLowerCase().contains("night")) return "night";
        else if (conditions.toLowerCase().contains("day")) return "day";
        else return null;
    }

    @Nullable
    private Map<String, Object> parseEvolutionCondition(String conditions) {
        final Map<String, Object> evolutionCondition = new HashMap<>();
        if (conditions.trim().isEmpty()) return null;
        evolutionCondition.put("level", parseMinLevelCondition(conditions));
        evolutionCondition.put("gender", parseGenderCondition(conditions));
        evolutionCondition.put("timeOfDay", parseTimeOfDayCondition(conditions));
        evolutionCondition.put("heldItem", parseHeldItemCondition(conditions));
        evolutionCondition.put("useItem", parseUseItemCondition(conditions));
        evolutionCondition.put("learnMove", parseLearnCondition(conditions));
        evolutionCondition.put("interact", parseInteractCondition(conditions));
        return evolutionCondition;
    }

    @NotNull
    private String parseCapabilityInformation(String page) {
        final String capabilityInformationRegex = "Capability List\\s+([\\w\\W]+?)(?=Skill List)";
        final Matcher capabilityInformationMatcher = Pattern.compile(capabilityInformationRegex).matcher(page);
        if (capabilityInformationMatcher.find()) {
            return consolidateLines(capabilityInformationMatcher.group(1));
        }
        return "";
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
        capabilities.put("sky", parseLabeledInteger("Sky", capabilityInformation));
        capabilities.put("swim", parseLabeledInteger("Swim", capabilityInformation));
        capabilities.put("overland", parseLabeledInteger("Overland", capabilityInformation));
        capabilities.put("levitate", parseLabeledInteger("Levitate", capabilityInformation));
        capabilities.put("power", parseLabeledInteger("Power", capabilityInformation));
        capabilities.put("burrow", parseLabeledInteger("Burrow", capabilityInformation));
        final Map<String, Integer> jump = parseJump(capabilityInformation);
        capabilities.put("highJump", jump.get("high"));
        capabilities.put("longJump", jump.get("long"));
        capabilities.put("mountable", parseLabeledInteger("Mountable", capabilityInformation));
        capabilities.put("naturewalk", parseNaturewalk(capabilityInformation));
        return capabilities;
    }

    @NotNull
    private Integer parseStage(String page, String pokemonName) {
        final String stageRegex = "(\\d) +- +" + pokemonName;
        final Matcher stageMatcher = Pattern.compile(stageRegex).matcher(page);
        if (stageMatcher.find()) {
            return parseInt(stageMatcher.group(1));
        }
        throw new Error("All pokemon species must have stage to parse\n" + page);
    }

    @Nullable
    private List<String> parseMegaTypes(String types) {
        if (types.trim().equals("Unchanged")) return null;
        return parseDelimitedList(types, "/");
    }

    @Nullable
    private Integer parseMegaEvolutionStat(String statsString, String stat) {
        final String statRegex = "(-?\\d+) " + stat;
        final Matcher statMatcher = Pattern.compile(statRegex).matcher(statsString);
        if (statMatcher.find()) {
            return parseInt(statMatcher.group(1));
        }
        return null;
    }

    private Map<String, Integer> parseMegaEvolutionStats(String statsString) {
        final Map<String, Integer> stats = new HashMap<>();
        stats.put("attack", parseMegaEvolutionStat(statsString, "Atk"));
        stats.put("defense", parseMegaEvolutionStat(statsString,"Def"));
        stats.put("specialAttack", parseMegaEvolutionStat(statsString,"Sp. Atk"));
        stats.put("specialDefense", parseMegaEvolutionStat(statsString,"Sp. Def"));
        stats.put("speed", parseMegaEvolutionStat(statsString,"Speed"));
        return stats;
    }

    @Nullable
    private List<Map<String, Object>> parseMegaEvolutions(String page) {
        final List<Map<String, Object>> megaEvolutions = new ArrayList<>();
        final String megaEvolutionRegex = "Mega *Evolution *(.*)?\\s*" +
                "Type: *(.*)\\s*" +
                "Ability: *(.*)\\s*" +
                "Stats: ((?:.+)\\s*(?:(?:.+)\\s*)?(?:[^M]+)?)";
        final Matcher megaEvolutionMatcher = Pattern.compile(megaEvolutionRegex).matcher(page);
        Map<String, Object> megaEvolution;
        while(megaEvolutionMatcher.find()) {
            final String typesString = megaEvolutionMatcher.group(2);
            final String statsString = consolidateLines(megaEvolutionMatcher.group(4));
            String name = megaEvolutionMatcher.group(1);
            name = name.isEmpty() ? null : name;
            final List<String> types = parseMegaTypes(typesString);
            final String ability = megaEvolutionMatcher.group(3);
            final Map<String, Integer> stats = parseMegaEvolutionStats(statsString);
            megaEvolution = new HashMap<>();
            megaEvolution.put("name", name);
            megaEvolution.put("types", types);
            megaEvolution.put("ability", ability);
            megaEvolution.put("stats", stats);
            megaEvolutions.add(megaEvolution);
        }
        if (megaEvolutions.isEmpty()) return null;
        return megaEvolutions;
    }

    @Nullable
    private List<String> parseNaturewalk(String capabilityInformation) {
        final String naturewalkRegex = "[Nn]aturewalk *\\((.*)\\)";
        final Matcher naturewalkMatcher = Pattern.compile(naturewalkRegex).matcher(capabilityInformation);
        if (naturewalkMatcher.find()) {
            return parseDelimitedList(naturewalkMatcher.group(1), ",");
        }
        return null;
    }

    @Nullable
    private String parseEvolvesFrom(String page, int currentStage) {
        final String evolvesFromRegex = (currentStage - 1) + " *- *([\\w-]*)";
        final Matcher evolvesFromMatcher = Pattern.compile(evolvesFromRegex).matcher(page);
        if (evolvesFromMatcher.find()) {
            return evolvesFromMatcher.group(1);
        }
        return null;
    }

    private String parseEvolutionTrigger(Map<String, Object> conditions) {
        if (conditions == null) return null;
        if (conditions.get("interact") != null) return "interact";
        else if (conditions.get("useItem") != null) return "useItem";
        else if (conditions.get("level") != null) return "level";
        else return null;
    }

    @Nullable
    private Map<String, Object> parseEvolvesTo(String page, int currentStage) {
        final String evolvesToRegex = (currentStage + 1) + " *- *([\\w-]*) *(.*)";
        final Matcher evolvesToMatcher = Pattern.compile(evolvesToRegex).matcher(page);
        final Map<String, Object> evolvesTo = new HashMap<>();
        while (evolvesToMatcher.find()) {
            final Map<String, Object> evolution = new HashMap<>();
            final String name = evolvesToMatcher.group(1).trim();
            final Map<String, Object> conditions = parseEvolutionCondition(evolvesToMatcher.group(2));
            final String trigger = parseEvolutionTrigger(conditions);
            evolution.put("name", name);
            evolution.put("conditions", conditions);
            evolution.put("trigger", trigger);
            evolvesTo.put(name, evolution);
        }
        if (evolvesTo.isEmpty()) return null;
        return evolvesTo;
    }
}
