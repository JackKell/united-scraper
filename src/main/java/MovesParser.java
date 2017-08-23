import org.json.JSONObject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;

class MovesParser extends BaseParser {
    // Recoil is not included in the list below because it is the only mechanic with a value
    final List<String> possibleMechanics = asList("Aura", "Berry", "Blessing", "Coat", "Dash", "Double Strike", "Environ",
            "Execute", "Exhaust", "Fling", "Friendly", "Five Strike", "Groundsource", "Hazard", "Illusion", "Interrupt",
            "Pass", "Pledge", "Powder", "Priority", "Push", "Reaction", "Set-up", "Shield", "Smite", "Social", "Sonic",
            "Spirit Surge", "Trigger", "Vortex", "Weather", "Weight Class"
    );

    JSONObject parse(String movesText) {
        final String cleanText = clean(movesText);
//        System.out.println(cleanText);
        final JSONObject moves = new JSONObject();
        final String moveRegex = "Move: ([\\w \\-]*)\\s*[\\w \\s\\W]*?(?=Move:|$)";
        final Matcher moveMatcher = Pattern.compile(moveRegex).matcher(cleanText);
        while (moveMatcher.find()) {
            final String moveText = moveMatcher.group(0);
//            System.out.println(moveText);
            final String moveName = moveMatcher.group(1).trim();
            final Map<String, Object> move = new HashMap<>();
            move.put("name", moveName);
            move.put("type", parseType(moveText));
            move.put("frequency", parseFrequency(moveText));
            move.put("accuracyCheck", parseAccuracyCheck(moveText));
            move.put("damageBase", parseDamageBase(moveText));
            move.put("class", parseClass(moveText));
            final String rangeInformation = parseLabeledString("Range:", moveText);
            move.put("attackOptions", parseAttackOptions(rangeInformation));
            move.put("mechanics", parseMechanics(rangeInformation));
            move.put("recoil", parseRecoil(rangeInformation));
            move.put("contestType", parseContestType(moveText));
            move.put("contestEffect", parseContestEffect(moveText));
            move.put("special", parseSpecial(moveText));
            move.put("setup", parseSetup(moveText));
            move.put("effect", parseEffect(moveText));
            moves.put(moveName, move);
        }
        return moves;
    }

    private String parseType(String moveText) {
        final String type = parseLabeledString("Type:", moveText);
        if (type != null) {
            return type;
        }
        throw new Error("All moves should have a type\n" + moveText);
    }

    private Map<String, Object> parseFrequency(String moveText) {
        final String frequencyRegex = "Frequency: +([\\w\\-]+)(?: [Xx](\\d))?";
        final Matcher frequencyMatcher = Pattern.compile(frequencyRegex).matcher(moveText);
        if (frequencyMatcher.find()) {
            Map<String, Object> frequency = new HashMap<>();
            final String type = frequencyMatcher.group(1);
            frequency.put("type", type);
            final String limit = frequencyMatcher.group(2);
            if (limit != null) {
                frequency.put("limit", parseInt(limit));
            } else if (type.equals("Daily") || type.equals("Scene")) {
                frequency.put("limit", 1);
            }
            return frequency;
        }
        throw new Error("All moves should have a frequency\n" + moveText);
    }

    private Integer parseAccuracyCheck(String moveText) {
        final String accuracyCheckString = parseLabeledString("AC:", moveText);
        if (accuracyCheckString != null) {
            if (accuracyCheckString.equals("None") | accuracyCheckString.equals("See Effect")) {
                return null;
            } else {
                return parseInt(accuracyCheckString);
            }
        }
        return null;
    }

    private Integer parseDamageBase(String moveText) {
        return parseLabeledInteger("Damage Base", moveText);
    }

    private String parseClass(String moveText) {
        return parseLabeledString("Class:", moveText);
    }

    private List<Map<String, Object>> parseAttackOptions(String rangeInformation) {
        if (rangeInformation == null) {
            return null;
        }
        final List<String> rangeOnlyTypes = asList("Line", "Burst", "Cone", "Single");
        final List<Map<String, Object>> attackOptions = new ArrayList<>();
        final String attackOptionRegex = "(Line|Single|Cone|Burst|Blast|Self|Field)(?: (\\d{1,2}))?(?: (\\d{1,2}))?";
        final Matcher attackOptionMatcher = Pattern.compile(attackOptionRegex).matcher(rangeInformation);
        while (attackOptionMatcher.find()) {
            final Map<String, Object> attackOption = new HashMap<>();
            final String type = attackOptionMatcher.group(1).trim();
            Integer size = null;
            Integer range = null;
            if (rangeOnlyTypes.contains(type)) {
                range = parseInt(attackOptionMatcher.group(2));
            } else if (type.equals("Blast")) {
                size = parseInt(attackOptionMatcher.group(2));
                range = parseInt(attackOptionMatcher.group(3));
            }
            attackOption.put("type", type);
            attackOption.put("size", size);
            attackOption.put("range", range);
            attackOptions.add(attackOption);
        }
        if (attackOptions.isEmpty()) return null;
        return attackOptions;
    }

    private Float parseRecoil(String rangeInformation) {
        if (rangeInformation == null) return null;
        final String recoilRegex = "Recoil (\\d+)/(\\d+)";
        final Matcher recoilMatcher = Pattern.compile(recoilRegex).matcher(rangeInformation);
        if (recoilMatcher.find()) {
            return parseFloat(recoilMatcher.group(1)) / parseFloat(recoilMatcher.group(2));
        }
        return null;
    }

    private List<String> parseMechanics(String rangeInformation) {
        if (rangeInformation == null) return null;
        final List<String> mechanics = new ArrayList<>();
        for (String possibleMechanic : possibleMechanics) {
            if (rangeInformation.contains(possibleMechanic)) {
                mechanics.add(possibleMechanic);
            }
        }
        if (mechanics.isEmpty()) return null;
        return mechanics;
    }

    private String parseContestType(String moveText) {
        return parseLabeledString("Contest Type:", moveText);
    }

    private String parseContestEffect(String moveText) {
        return parseLabeledString("Contest Effect:", moveText);
    }

    // TODO: make Special an object
    private String parseSpecial(String moveText) {
        return parseLabeledString("Special:", moveText);
    }

    private String parseSetup(String moveText) {
        final String setupRegex = "Set-Up Effect: ((?:(?:.*)\\s)*?)(?=Resolution Effect:)";
        final Matcher setupMatcher = Pattern.compile(setupRegex).matcher(moveText);
        if (setupMatcher.find()) {
            final String effect = consolidateLines(setupMatcher.group(1));
            return effect;
        }
        return null;
    }

    private String parseEffect(String moveText) {
        final String effectRegex = "(?:[\r\n]+|Resolution )Effect: ((?:(?:.*)\\s)*?)(?=Contest Type:)";
        final Matcher effectMatcher = Pattern.compile(effectRegex).matcher(moveText);
        if (effectMatcher.find()) {
            final String effect = consolidateLines(effectMatcher.group(1));
            if (effect.equals("None")) return null;
            return effect;
        }
        return null;
    }

    protected String clean(String text) {
        String cleanText = text;
        // Remove all "Indices and Reference"
        cleanText = cleanText.replaceAll("Indices and Reference\\s+", "");
        // Remove all page numbers
        cleanText = cleanText.replaceAll("\\d{3}[\r\n]+", "");
        // Remove all Type Moves titles
        cleanText = cleanText.replaceAll("\\w* Moves[\r\n]+", "");
        cleanText = cleanSpecialCharacters(cleanText);
        // Fix werid "--" to be "None"
        cleanText = cleanText.replaceAll("--", "None");
        // Fix typo in "Land's Wrath"
        cleanText = cleanText.replaceAll("None\\.", "None");
        // Fix order mistake
        cleanText = cleanText.replaceAll("Melee, Dash, 1 Target", "Melee, 1 Target, Dash");
        // Fix Petal Dance, Outrage, and Thrash Range Description from "Melee, all adjacent foes, Smite" to "Burst 1, Smite"
        cleanText = cleanText.replaceAll("Melee, all adjacent foes, Smite", "All Cardinally Adjacent Targets, Smite");
        // Fix Shadow Claw, Steamroller, Leaf Blade, Cut, False Swipe, Slash, Cross Poison, Aqua Tail
        cleanText = cleanText.replaceAll("Melee, Pass", "Melee, 1 Target, Pass");
        // Fix Disable and Spite change the attack type from 1 Target to Self
        cleanText = cleanText.replaceAll("Range: 1 Target, Trigger", "Range: Self, Trigger");
        // Fix Solar Beam by removing the 1 Target and only having Line 6
        cleanText = cleanText.replaceAll("Range: Line 6, 1 Target", "Range: Line 6,");
        // Fix Mirror Coat
        cleanText = cleanText.replaceAll("Range: Any, 1 Target, Reaction", "Range: Self, Reaction");
        // Change the Keyword "1 Target" to be "Single Target"
        cleanText = cleanText.replaceAll("1 Target", "Single Target");
        // Change the Keyword "Melee" to be "1"
        cleanText = cleanText.replaceAll("Range: +Melee", "Range: 1");
        // Reformat Single Target
        final String singleTargetName = "Single";
        cleanText = cleanText.replaceAll("(\\d+), Single Target", singleTargetName + " $1");
        // Reformat Ranged Blast
        cleanText = cleanText.replaceAll("(\\d+), Ranged Blast (\\d+)", "Blast $2 $1");
        // Reformat Close Blast
        cleanText = cleanText.replaceAll("Close (Blast \\d+)", "$1 1");
        // Reformat All Cardinally Adjacent Targets
        cleanText = cleanText.replaceAll("All Cardinally Adjacent Targets", "CAT");
        // Fix lone range values (e.g. Spider Web)
        cleanText = cleanText.replaceAll("(Range: )(\\d+[\r\n]+)", "$1" + singleTargetName + " $2");
        // Reformat "range, Blast size" to be "Blast size range"
        cleanText = cleanText.replaceAll("(\\d+), (Blast \\d+)", "$2 $1");
        return cleanText;
    }
}
