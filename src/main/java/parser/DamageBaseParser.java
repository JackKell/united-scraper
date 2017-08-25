package parser;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

public class DamageBaseParser extends TextBlockParser{
    @Override
    public JSONObject parse(String text) {
        final String cleanText = clean(text);
        final JSONObject damageBases = new JSONObject();
        for (int damageBaseNumber = 1; damageBaseNumber <= 28; damageBaseNumber++) {
            final String damageBaseRegex = damageBaseNumber + " ((\\d)d(\\d+)\\+(\\d+))[\\w\\W]*?" + damageBaseNumber + " (\\d+) / (\\d+) / (\\d+)";
            final Matcher damageBaseMatcher = Pattern.compile(damageBaseRegex).matcher(cleanText);
            if (damageBaseMatcher.find()) {
                final Map<String, Object> damageBase = new HashMap<>();
                damageBase.put("damageBase", damageBaseNumber);
                damageBase.put("value", damageBaseMatcher.group(1));
                damageBase.put("numberOfDice", parseInt(damageBaseMatcher.group(2)));
                damageBase.put("diceFaces", parseInt(damageBaseMatcher.group(3)));
                damageBase.put("buff", parseInt(damageBaseMatcher.group(4)));
                damageBase.put("min", parseInt(damageBaseMatcher.group(5)));
                damageBase.put("average", parseInt(damageBaseMatcher.group(6)));
                damageBase.put("max", parseInt(damageBaseMatcher.group(7)));
                damageBases.put(Integer.toString(damageBaseNumber), damageBase);
            }
        }
        return damageBases;
    }

    @Override
    protected String clean(String text) {
        String cleanText = text;
        cleanText = cleanSpecialCharacters(cleanText);
        cleanText = cleanText.replaceAll("Useful Charts[\\w\\W]*Damage Charts\\s+", "");
        cleanText = cleanText.replaceAll("Rolled Damage\\s+", "");
        cleanText = cleanText.replaceAll("Damage\\s+", "");
        cleanText = cleanText.replaceAll("Base\\s+", "");
        cleanText = cleanText.replaceAll("Actual\\s+", "");
        cleanText = cleanText.replaceAll("Set ", "");
        return cleanText;
    }
}
