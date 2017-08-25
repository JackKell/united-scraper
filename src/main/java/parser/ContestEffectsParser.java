package parser;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContestEffectsParser extends TextBlockParser{
    public JSONObject parse(String contestEffectsText) {
        final String cleanText = clean(contestEffectsText);
//        System.out.println(cleanText);
        JSONObject contestEffects = new JSONObject();
        final String contestEffectRegex = "(.*): +(\\wd\\w) +- +([\\w\\W]*?)(?=.*:)";
        final Matcher contestEffectMatcher = Pattern.compile(contestEffectRegex).matcher(cleanText);
        while (contestEffectMatcher.find()) {
            final Map<String, Object> contestEffect = new HashMap<>();
            final String name = contestEffectMatcher.group(1);
            contestEffect.put("name", name);
            contestEffect.put("roll", parseRoll(contestEffectMatcher.group(2)));
            contestEffect.put("effect", consolidateLines(contestEffectMatcher.group(3)));
            contestEffects.put(name, contestEffect);
        }
        return contestEffects;
    }

    private Map<String, String> parseRoll(String rollText) {
        final Map<String, String> roll = new HashMap<>();
        roll.put("value", rollText);
        roll.put("numberOfDice", String.valueOf(rollText.charAt(0)));
        roll.put("diceFaces", String.valueOf(rollText.charAt(2)));
        return roll;
    }

    protected String clean(String text) {
        String cleanText = text;
        cleanText = cleanSpecialCharacters(cleanText);
        // Remove header information
        cleanText = cleanText.replaceAll("Pokémon Contests\\s+266\\s+Contest Effects\\s+", "");
        return cleanText;
    }
}
