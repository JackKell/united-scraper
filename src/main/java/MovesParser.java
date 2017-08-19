import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import org.json.JSONObject;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

class MovesParser {
    JSONObject parse(String movesText) {
        final String cleanText = clean(movesText);
//        System.out.println(cleanText);
        JSONObject moves = new JSONObject();
        final String moveRegex = "Move: ([\\w \\-]*)\\s*[\\w \\s\\W]*?(?=Move:|$)";
        final Matcher moveMatcher = Pattern.compile(moveRegex).matcher(cleanText);
        while (moveMatcher.find()) {
            final String moveText = moveMatcher.group(0);
            final String moveName = moveMatcher.group(1).trim();
            final Map<String, Object> move = new HashMap<>();
            move.put("name", moveName);
            move.put("type", parseType(moveText));
            move.put("frequency", parseFrequency(moveText));
            move.put("accuracyCheck", parseAccuracyCheck(moveText));
            move.put("damageBase", parseDamageBase(moveText));
            move.put("class", parseClass(moveText));
            move.put("range", parseRange(moveText));
            move.put("contestType", parseContestType(moveText));
            move.put("contestEffect", parseContestEffect(moveText));
            move.put("special", parseSpecial(moveText));
            move.put("effect", parseEffect(moveText));
            moves.put(moveName, move);
        }
        return moves;
    }

    private String parseNamedString(String name, String text) {
        final String nameRegex = name + " *(.+)";
        final Matcher nameMatcher = Pattern.compile(nameRegex).matcher(text);
        if (nameMatcher.find()) {
            return nameMatcher.group(1).trim();
        }
        return null;
    }

    private Integer parseNamedInteger(String name, String text) {
        final String nameRegex = name + " *(\\d+)";
        final Matcher nameMatcher = Pattern.compile(nameRegex).matcher(text);
        if (nameMatcher.find()) {
            return parseInt(nameMatcher.group(1).trim());
        }
        return null;
    }

    private String parseType(String moveText) {
        final String type = parseNamedString("Type:", moveText);
        if (type != null) {
            return type;
        }
        throw new Error("All moves should have a type\n" + moveText);
    }

    // TODO: make frequency an object
    private String parseFrequency(String moveText) {
        final String frequency = parseNamedString("Frequency:", moveText);
        if (frequency != null) {
            return frequency;
        }
        throw new Error("All moves should have a frequency\n" + moveText);
    }

    private Integer parseAccuracyCheck(String moveText) {
        final String accuracyCheckString = parseNamedString("AC:", moveText);
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
        return parseNamedInteger("Damage Base", moveText);
    }

    private String parseClass(String moveText) {
        return parseNamedString("Class:", moveText);
    }

    // TODO: make range an object
    private String parseRange(String moveText) {
        return parseNamedString("Range:", moveText);
    }

    private String parseContestType(String moveText) {
        return parseNamedString("Contest Type:", moveText);
    }

    private String parseContestEffect(String moveText) {
        return parseNamedString("Contest Effect:", moveText);
    }

    // TODO: make Special an object
    private String parseSpecial(String moveText) {
        return parseNamedString("Special:", moveText);
    }

    // TODO: make Effect a simple object to handle Set-Up Effect and Resolution Effect (e.g. Geomancy)
    private String parseEffect(String moveText) {
        final String effectRegex = "\r\nEffect: ((?:(?:.*)\\s)*?)(?=Contest Type:)";
        final Matcher effectMatcher = Pattern.compile(effectRegex).matcher(moveText);
        if (effectMatcher.find()) {
            final String effect = effectMatcher.group(1).replaceAll("\r\n", "").trim();
            if (effect.equals("None")) return null;
            return effect;
        }
        return null;
    }

    private String clean(String text) {
        String cleanText = text;
        // Remove all "Indices and Reference"
        cleanText = cleanText.replaceAll("Indices and Reference\r\n", "");
        // Remove all page numbers
        cleanText = cleanText.replaceAll("\\d{3}\r\n", "");
        // Remove all Type Moves titles
        cleanText = cleanText.replaceAll("\\w* Moves\r\n", "");
        // Change all special characters
        cleanText = cleanText.replaceAll("–", "-");
        cleanText = cleanText.replaceAll(",", ",");
        cleanText = cleanText.replaceAll("“", "\"");
        cleanText = cleanText.replaceAll("”", "\"");
        cleanText = cleanText.replaceAll("’", "'");
        cleanText = cleanText.replaceAll("¼", "1/4");
        cleanText = cleanText.replaceAll("½", "1/2");
        cleanText = cleanText.replaceAll("¾", "3/4");
        cleanText = cleanText.replaceAll("\\u2019", "'");
        // Fix werid "--" to be "None"
        cleanText = cleanText.replaceAll("--", "None");
        // Fix typo in "Land's Wrath"
        cleanText = cleanText.replaceAll("None\\.", "None");
        return cleanText;
    }
}
