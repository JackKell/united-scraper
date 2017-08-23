import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class AbilitiesParser extends BaseParser {
    JSONObject parse(String abilitiesText) {
        final String cleanText = clean(abilitiesText);
//        System.out.println(cleanText);
        JSONObject abilities = new JSONObject();
        final String abilityRegex = "Ability: *(.+)\\s[\\s\\w\\W]*?(?=Ability:|$)";
        final Matcher abilityMatcher = Pattern.compile(abilityRegex).matcher(cleanText);
        while (abilityMatcher.find()) {
            final String abilityText = abilityMatcher.group(0);
            final String abilityName = abilityMatcher.group(1);
            final Map<String, Object> ability = new HashMap<>();
            ability.put("name", abilityName);
            ability.put("trigger", parseTrigger(abilityText));
            ability.put("effect", parseEffect(abilityText));
            ability.put("frequency", parseFrequency(abilityText));
            ability.put("actionType", parseActionType(abilityText));
            abilities.put(abilityName, ability);
        }
        return abilities;
    }

    private String parseActionType(String abilityText) {
        final String actionTypeRegex = "Ability:.*\\s.*- ([\\w, ]*)";
        final Matcher actionTypeMatcher = Pattern.compile(actionTypeRegex).matcher(abilityText);
        if (actionTypeMatcher.find()) {
            return actionTypeMatcher.group(1);
        }
        return null;
    }

    private String parseFrequency(String abilityText) {
        final String frequencyRegex = "Ability:.*\\s+([\\w\\-]*)";
        final Matcher frequencyMatcher = Pattern.compile(frequencyRegex).matcher(abilityText);
        if (frequencyMatcher.find()) {
            return frequencyMatcher.group(1);
        }
        throw new Error("All abilities should have an ability frequency: " + abilityText);
    }

    private String parseEffect(String abilityText) {
        final String effectRegex = "Effect: ([\\w\\W]*)";
        final Matcher effectMatcher = Pattern.compile(effectRegex).matcher(abilityText);
        if (effectMatcher.find()) {
            String effect = effectMatcher.group(1);
            effect = effect.replaceAll("\r\n", "").trim().replaceAll("  ", " ");
            return effect;
        }
        return null;
    }

    private String parseTrigger(String abilityText) {
        final String triggerRegex = "Trigger: ([\\w\\s-]*)\\n(?=[\\w ]*Effect:)";
        final Matcher triggerMatcher = Pattern.compile(triggerRegex).matcher(abilityText);
        if (triggerMatcher.find()) {
            String trigger = triggerMatcher.group(1);
            trigger = trigger.replaceAll("\r\n", "").trim().replaceAll("  ", " ");
            return trigger;
        }
        return null;
    }

    protected String clean(String text) {
        String cleanText = text;
        // Remove all "Indices and Reference"
        cleanText = cleanText.replaceAll("Indices and Reference\r\n", "");
        // Remove all page numbers
        cleanText = cleanText.replaceAll("\\d{3}\r\n", "");
        // Remove all list range titles
        cleanText = cleanText.replaceAll("Ability List: *\\w–\\w\r\n", "");
        // Fix typo of "At Will" to be "At-Will"
        cleanText = cleanText.replaceAll("At Will", "At-Will");
        // Change all special characters
        cleanText = cleanSpecialCharacters(cleanText);
        return cleanText;
    }
}
