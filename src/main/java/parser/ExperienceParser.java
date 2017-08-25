package parser;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

public class ExperienceParser extends TextBlockParser{
    @Override
    public JSONObject parse(String text) {
        final String cleanText = clean(text);
//        System.out.println(cleanText);
        JSONObject experience = new JSONObject();
        final String experienceLevelRegex = "(\\d+) (\\d+)";
        final Matcher experienceLevelMatcher = Pattern.compile(experienceLevelRegex).matcher(cleanText);
        while (experienceLevelMatcher.find()) {
            String level = experienceLevelMatcher.group(1);
            Integer experienceNeeded = parseInt(experienceLevelMatcher.group(2));
            experience.put(level, experienceNeeded);
        }
        return experience;
    }

    @Override
    protected String clean(String text) {
        String cleanText = text;
        // Change special characters
        cleanText = cleanSpecialCharacters(cleanText);
        // Remove headers
        cleanText = cleanText.replaceAll("Useful Charts\\s+", "");
        cleanText = cleanText.replaceAll("Pokémon Experience Chart\\s+", "");
        cleanText = cleanText.replaceAll("Level Exp\\s+", "");
        cleanText = cleanText.replaceAll("Needed\\s+", "");
        // Remove damage base information
        cleanText = cleanText.replaceAll("Damage Charts[\\w\\W]*", "");
        // Remove ,
        cleanText = cleanText.replaceAll(",", "");
        return cleanText;
    }
}
