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

class SpeciesParser {
    final List<String> problemSpecies = asList(
            "Pumpkaboo", // Too many forms
            "Gourgeist", // Too many forms
            "Rotom normal form", // Too many forms
            "Rotom appliance forms" // Too many forms
    );

    SpeciesParser() {
    }

    JSONObject parse(String page) {
        String cleanedPage = cleanPage(page);
//        System.out.println(cleanedPage);
        JSONObject species = new JSONObject();
        String name = parseName(cleanedPage);
//        System.out.println(name);
        species.put("name", name);
        if (problemSpecies.contains(name)) {
            return species;
        }
        species.put("evolutionChain", parseEvolutionChain(cleanedPage));
        species.put("highAbilities", parseHighAbilities(cleanedPage));
        species.put("advancedAbilities", parseAdvancedAbilities(cleanedPage));
        species.put("basicAbilities", parseBasicAbilities(cleanedPage));
        species.put("tutorMoves", parseTutorMoves(cleanedPage));
        species.put("eggMoves", parseEggMoves(cleanedPage));
        species.put("machineMoves", parseMachineMoves(cleanedPage));
        species.put("genderRatio", parseGenderRatio(cleanedPage));
        species.put("levelUpMoves", parseLevelUpMoves(cleanedPage));
        species.put("sky", parseNamedInteger(cleanedPage, "Sky"));
        species.put("swim", parseNamedInteger(cleanedPage, "Swim"));
        species.put("overland", parseNamedInteger(cleanedPage, "Overland"));
        species.put("levitate", parseNamedInteger(cleanedPage, "Levitate"));
        species.put("power", parseNamedInteger(cleanedPage, "Power"));
        species.put("burrow", parseNamedInteger(cleanedPage, "Burrow"));
        species.put("types", parseSlashSeparatedList(cleanedPage, "Type"));
        species.put("jump", parseJump(cleanedPage));
        species.put("eggGroups", parseSlashSeparatedList(cleanedPage, "Egg Group"));
        species.put("averageHatchRate", parseNamedInteger(cleanedPage, "Average Hatch Rate:"));
        species.put("stats", parseStats(cleanedPage));
        species.put("skills", parseSkills(cleanedPage));
        species.put("height", parseHeight(cleanedPage));
        species.put("weight", parseWeight(cleanedPage));
        species.put("diet", parseCommaSeparatedList(cleanedPage, "Diet"));
        species.put("habitat", parseCommaSeparatedList(cleanedPage, "Habitat"));
        System.out.println(species);
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
        return cleanedPage;
    }

    private String parseName(String text) {
        String name = text.substring(text.indexOf('\n') + 1, text.indexOf("Base")).trim();
        name = name.substring(0, 1) + name.substring(1).toLowerCase();
        return name;
    }

    private Map<String, Object> parseHeight(String page) {
        Map<String, Object> height = new HashMap<String, Object>();
        final String heightRegex = "Height\\s*:\\s*(\\d*' \\d*\") \\/ (\\d*.\\d*)m \\((\\w*)\\)";
        Pattern heightPattern = Pattern.compile(heightRegex);
        Matcher heightMatcher = heightPattern.matcher(page);
        if (heightMatcher.find()) {
            String ft = heightMatcher.group(1);
            float m = parseFloat(heightMatcher.group(2));
            String size = heightMatcher.group(3);
            height.put("ft", ft);
            height.put("m", m);
            height.put("size", size);
            return height;
        } else {
            return null;
        }
    }

    private Map<String, Object> parseWeight(String page) {
        Map<String, Object> weight = new HashMap<String, Object>();
        String weightRegex = "Weight\\s*:\\s*(\\d*.\\d*) lbs\\. \\/ (\\d*.\\d*)kg \\((\\w*)\\)";
        ;
        Pattern weightPattern = Pattern.compile(weightRegex);
        Matcher weightMatcher = weightPattern.matcher(page);
        if (weightMatcher.find()) {
            float lbs = parseFloat(weightMatcher.group(1));
            float kg = parseFloat(weightMatcher.group(2));
            int weightClass = parseInt(weightMatcher.group(3));
            weight.put("lbs", lbs);
            weight.put("kg", kg);
            weight.put("class", weightClass);
            return weight;
        } else {
            return null;
        }
    }

    private Map<String, Integer> parseJump(String page) {
        Map<String, Integer> jump = new HashMap<>();
        String p = "Jump\\s*(\\d*)\\/(\\d*)";
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(page);
        if (matcher.find()) {
            jump.put("long", parseInt(matcher.group(1)));
            jump.put("high", parseInt(matcher.group(2)));
            return jump;
        } else {
            return null;
        }
    }

    private Integer parseNamedInteger(String text, String name) {
        final String p = name + "\\s*(\\d+)";
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return parseInt(matcher.group(1));
        } else {
            return 0;
        }
    }

    private List<String> parseSlashSeparatedList(String text, String name) {
        List<String> items = new ArrayList<>();
        String regex = name + "\\s*:\\s*(\\w*)(?:\\s*\\/\\s*(\\w*))?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String item1 = matcher.group(1);
            String item2 = matcher.group(2);
            items.add(item1);
            if (item2 != null) items.add(item2);
        } else {
            throw new Error("No match was found in:\n" + text + "\n For regex string " + regex);
        }
        return items;
    }

    private List<String> parseCommaSeparatedList(String text, String name) {
        List<String> items;
        String p = name + "\\s*:\\s*((?:\\w+)(?:,\\s*\\w+)*)";
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(text);
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
        Map<String, Integer> stats = new HashMap<>();
        stats.put("hp", parseNamedInteger(page, "HP:"));
        stats.put("attack", parseNamedInteger(page, "Attack:"));
        stats.put("defense", parseNamedInteger(page, "Defense:"));
        stats.put("specialAttack", parseNamedInteger(page, "Special Attack:"));
        stats.put("specialDefense", parseNamedInteger(page, "Special Defense:"));
        stats.put("speed", parseNamedInteger(page, "Speed:"));
        return stats;
    }

    private Map<String, Object> parseSkill(String page, String name) {
        Map<String, Object> skill = new HashMap<>();
        String p = name + "\\s*((\\d*)d(\\d*)(?:\\+(\\d*))?)";
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(page);
        if (matcher.find()) {
            String value = matcher.group(1);
            int diceNumber = parseInt(matcher.group(2));
            int diceFaces = parseInt(matcher.group(3));
            String buffString = matcher.group(4);
            int buff;
            if (buffString == null) {
                buff = 0;
            } else {
                buff = parseInt(buffString);
            }
            skill.put("value", value);
            skill.put("diceNumber", diceNumber);
            skill.put("diceFaces", diceFaces);
            skill.put("buff", buff);
            return skill;
        } else {
            return null;
        }
    }

    private Map<String, Map<String, Object>> parseSkills(String page) {
        Map<String, Map<String, Object>> skills = new HashMap<>();
        skills.put("athletics", parseSkill(page, "Athl"));
        skills.put("acrobatics", parseSkill(page, "Acro"));
        skills.put("combat", parseSkill(page, "Combat"));
        skills.put("stealth", parseSkill(page, "Stealth"));
        skills.put("perception", parseSkill(page, "Percep"));
        skills.put("focus", parseSkill(page, "Focus"));
        return skills;
    }

    private Map<String, Object> parseGenderRatio(String page) {
        Map<String, Object> genderRatio = new HashMap<>();
        genderRatio.put("male", 0.0f);
        genderRatio.put("female", 0.0f);
        genderRatio.put("gendered", false);
        genderRatio.put("hermaphrodite", false);
        String genderRegex = "Gender Ratio\\s*:\\s*(.*)\r\n";
        Pattern genderPattern = Pattern.compile(genderRegex);
        Matcher genderMatcher = genderPattern.matcher(page);
        if (genderMatcher.find()) {
            String result = genderMatcher.group(1);
            String ratioRegex = "([0-9\\.]+)%\\s*M\\s*\\/\\s*([0-9\\.]+)%\\s*F";
            Pattern ratioPattern = Pattern.compile(ratioRegex);
            Matcher ratioMatcher = ratioPattern.matcher(result);
            if (ratioMatcher.find()) {
                float maleRatio = parseFloat(ratioMatcher.group(1));
                float femaleRatio = parseFloat(ratioMatcher.group(2));
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
            } else {
                System.out.println(result);
                return null;
            }
        } else {
            return null;
        }
    }

    private List<Map<String, Object>> parseLevelUpMoves(String page) {
        List<Map<String, Object>> levelUpMoves = new ArrayList<>();
        String levelUpMoveRegex = "(\\d+)\\s+(['a-zA-Z- ]+)\\s+-";
        Pattern levelUpMovePattern = Pattern.compile(levelUpMoveRegex);
        Matcher levelUpMoveMatcher = levelUpMovePattern.matcher(page);
        while (levelUpMoveMatcher.find()) {
            Map<String, Object> levelUpMove = new HashMap<>();
            int level = parseInt(levelUpMoveMatcher.group(1));
            String move = levelUpMoveMatcher.group(2);
            levelUpMove.put("move", move);
            levelUpMove.put("level", level);
            levelUpMoves.add(levelUpMove);
        }
        return levelUpMoves;
    }

    private List<String> parseMachineMoves(String page) {
        List<String> machineMoves = new ArrayList<>();
        String p = "TM\\/HM Move List\\s*([\\s\\d\\w,-]*?)(?:Egg|Tutor)";
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(page);
        String machineMoveRegex = "\\w*? ([a-zA-Z -]+)";
        Pattern machineMovePattern = Pattern.compile(machineMoveRegex);
        if (matcher.find()) {
            String result = matcher.group(1);
            result = result.replaceAll("\r\n", "");
            if (result.equals("None") || result.isEmpty()) return null;
            List<String> machineMoveStrings = asList(result.trim().split(","));
            for (String machineMoveString : machineMoveStrings) {
                Matcher machineMoveMatcher = machineMovePattern.matcher(machineMoveString.trim());
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

    private List<Map<String, Object>> parseTutorMoves(String page) {
        List<Map<String, Object>> tutorMoves = new ArrayList<Map<String, Object>>();
        String p = "Tutor Move List\r\n((?:[A-Za-z -]+)(?:,\\s(?:\r\n)?[A-Za-z-]+(?: (?:\r\n)?[A-Za-z-]+)?(?: (?:\r\n)?\\(N\\))?)+)";
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(page);
        String tutorMoveRegex = "([A-Za-z-]*(?: [A-Za-z-]*)?)(?: (\\(N\\)))?";
        Pattern tutorMovePattern = Pattern.compile(tutorMoveRegex);
        if (matcher.find()) {
            String result = matcher.group(1);
            result = result.replaceAll("\r\n", "");
            List<String> tutorMoveStrings = asList(result.trim().split(","));
            for (String tutorMoveString : tutorMoveStrings) {
                Matcher tutorMoveMatcher = tutorMovePattern.matcher(tutorMoveString.trim());
                if (tutorMoveMatcher.find()) {
                    Map<String, Object> tutorMove = new HashMap<String, Object>();
                    String move = tutorMoveMatcher.group(1);
                    Boolean isHeartScaleMove = tutorMoveMatcher.group(2) != null;
                    tutorMove.put("move", move);
                    tutorMove.put("heartScaleMove", isHeartScaleMove);
                    tutorMoves.add(tutorMove);
                } else {
                    throw new Error("Tutor move: \"" + tutorMoveString.trim() + "\" was not parsed correctly");
                }
            }
            return tutorMoves;
        }
        return null;
    }

    private List<String> parseEggMoves(String page) {
        List<String> eggMoves;
        String p = "Egg Move List\\s*([-a-zA-Z,\\s]+)Tutor Move List";
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(page);
        if (matcher.find()) {
            String result = matcher.group(1);
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
        List<String> basicAbilities = new ArrayList<String>();
        String p = "Basic\\s*Ability\\s*\\d+\\s*:\\s*(.*)\\s";
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(page);
        while (matcher.find()) {
            basicAbilities.add(matcher.group(1));
        }
        return basicAbilities;
    }

    private List<String> parseAdvancedAbilities(String page) {
        List<String> advancedAbilities = new ArrayList<String>();
        String p = "Adv\\s*Ability\\s*\\d+\\s*:\\s*(.*)\\s";
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(page);
        while (matcher.find()) {
            advancedAbilities.add(matcher.group(1));
        }
        return advancedAbilities;
    }

    private List<String> parseHighAbilities(String page) {
        List<String> highAbilities;
        String p = "High\\s*Ability\\s*:\\s*((?:.*\\s*\\/)*\\s*.*)";
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(page);
        if (matcher.find()) {
            String result = matcher.group(1);
            highAbilities = asList(result.split("\\/"));
            for (int i = 0; i < highAbilities.size(); i++) {
                highAbilities.set(i, highAbilities.get(i).trim());
            }
            return highAbilities;
        }
        return null;
    }

    private List<Map<String, Object>> parseEvolutionChain(String page) {
        List<Map<String, Object>> evolutionChain = new ArrayList<>();
        String p = "(\\d) +- +([\\w-]*) *(.*)";
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(page);
        while (matcher.find()) {
            Map<String, Object> evolution = new HashMap<>();
            int stage = parseInt(matcher.group(1));
            String pokemon = matcher.group(2);
            String conditions = matcher.group(3).replaceAll(" (?= )", " ").trim();
            evolution.put("stage", stage);
            evolution.put("pokemon", pokemon);
            evolution.put("conditions", parseEvolutionCondition(conditions));
            evolutionChain.add(evolution);
        }
        return evolutionChain;
    }

    private Integer parseMinLevelCondition(String conditions) {
        String minLevelRegex = "M\\w*m (\\d+)";
        Pattern minLevelPattern = Pattern.compile(minLevelRegex);
        Matcher minLevelMatcher = minLevelPattern.matcher(conditions);
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
        String heldItemRegex = "Holding +(.*)? +(?=M\\w*m)";
        Pattern heldItemPattern = Pattern.compile(heldItemRegex);
        Matcher heldItemMatcher = heldItemPattern.matcher(conditions);
        if (heldItemMatcher.find()) {
            return heldItemMatcher.group(1).trim();
        } else {
            return null;
        }
    }

    private String parseUseItemCondition(String conditions) {
        String useItemRegex = "\\w* Stone";
        Pattern useItemPattern = Pattern.compile(useItemRegex);
        Matcher heldItemMatcher = useItemPattern.matcher(conditions);
        if (heldItemMatcher.find()) {
            String match = heldItemMatcher.group(0).trim();
            if (!match.contains("Oval")) {
                return match;
            }
        }
        return null;
    }

    private String parseLearnCondition(String conditions) {
        String learnRegex = "Learn +(\\S*(?: \\S*)?)";
        Pattern learnPattern = Pattern.compile(learnRegex);
        Matcher learnMatcher = learnPattern.matcher(conditions);
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
        Map<String, Object> evolutionCondition = new HashMap<>();
        if (conditions.trim().isEmpty()) return evolutionCondition;
        evolutionCondition.put("minLevel", parseMinLevelCondition(conditions));
        evolutionCondition.put("gender", parseGenderCondition(conditions));
        evolutionCondition.put("timeOfDay", parseTimeOfDayCondition(conditions));
        evolutionCondition.put("heldItem", parseHeldItemCondition(conditions));
        evolutionCondition.put("useItem", parseUseItemCondition(conditions));
        evolutionCondition.put("learnMove", parseLearnCondition(conditions));
        return evolutionCondition;
    }

    private int parseStage(String page) {
        return 0;
    }
}
