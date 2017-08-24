import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class NaturesParser extends TextBlockParser{
    JSONObject parse(String text) {
        final String cleanText = clean(text);
//        System.out.println(cleanText);
        JSONObject natures = new JSONObject();
        final String natureRegex = "\\d+ (\\w*) (\\w*) (\\w*)";
        final Matcher natureMatcher = Pattern.compile(natureRegex).matcher(cleanText);
        while (natureMatcher.find()) {
            final Map<String, String> nature = new HashMap<>();
            final String name = natureMatcher.group(1);
            nature.put("name", name);
            nature.put("increment", natureMatcher.group(2));
            nature.put("decrement", natureMatcher.group(3));
            natures.put(name, nature);
        }
        return natures;
    }

    protected String clean(String text) {
        String cleanText = text;
        // Remove headers
        cleanText = cleanText.replaceAll("Pokémon Nature Chart\\s+", "");
        cleanText = cleanText.replaceAll("Raise\\s+", "");
        cleanText = cleanText.replaceAll("Lower\\s+", "");
        cleanText = cleanText.replaceAll("Value\\s+", "");
        cleanText = cleanText.replaceAll("Nature\\s+", "");
        // Remove Extra text
        cleanText = cleanText.replaceAll("\\*These Natures[\\w\\W]*", "");
        // Remove *
        cleanText = cleanText.replaceAll("\\*", "");
        // Rename stats
        cleanText = cleanText.replaceAll("HP", "hp");
        cleanText = cleanText.replaceAll("Attack", "attack");
        cleanText = cleanText.replaceAll("Special Atk\\.", "specialAttack");
        cleanText = cleanText.replaceAll("Defense", "defense");
        cleanText = cleanText.replaceAll("Special Def\\.", "specialDefense");
        cleanText = cleanText.replaceAll("Speed", "speed");
        return cleanText;
    }
}
