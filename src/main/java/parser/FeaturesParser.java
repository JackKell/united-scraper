package parser;

import org.json.JSONObject;

public class FeaturesParser extends TextBlockParser {
    @Override
    public JSONObject parse(String text) {
        final String cleanText = clean(text);
        System.out.println(cleanText);
        final JSONObject features = new JSONObject();
        final String featureRegex;
        return features;
    }

    @Override
    protected String clean(String text) {
        String cleanText = text;
        cleanText = cleanSpecialCharacters(cleanText);
        // Remove page headers
        cleanText = cleanText.replaceAll("Skills, Edges, Feats\\s+\\d+\\s+", "");
        cleanText = cleanText.replaceAll("Trainer Classes\\s+\\d+\\s+", "");
        cleanText = cleanText.replaceAll("Orders, Training Features, and Trainer Classes[\\w\\W]*?(?=Trickster Orders)", "");
        // Remove flavor text
        cleanText = cleanText.replaceAll("General Features[\\w\\W]*?(?=Command Versatility)", "");
        cleanText = cleanText.replaceAll("Training Features:[\\w\\W]*?(?=Agility Training)", "");
        cleanText = cleanText.replaceAll("Pokémon Raising and Battling Features\\s+", "");
        cleanText = cleanText.replaceAll("Pokémon Training and Order Features[\\w\\W]*(?=Commander's Voice)", "");
        cleanText = cleanText.replaceAll("[Stratagem] Features are special Orders which[\\w\\W]*(?=Ravager Orders)", "");
        // Add "Feature: " in front of all feature names to make parsing easier
        cleanText = cleanText.replaceAll("([\\w'\\- é]+[\\r\\n]+Prerequisites:)", "Feature: $1");
        cleanText = cleanText.replaceAll("([\\w'\\- é]+[\\r\\n]+\\[)", "Feature: $1");
        // Add "Frequency: " label and "Action Type: " label
        cleanText = cleanText.replaceAll("([\\w \\-]*) - ([\\w \\-]*[\\r\\n]+)", "Frequency: $1\nAction Type: $2");
        cleanText = cleanText.replaceAll("Static[\\r\\n]+", "Frequency: Static\n");
        // Add "Tags: " label
        cleanText = cleanText.replaceAll("(\\[.*[\\r\\n]+)", "Tags: $1");
//        cleanText =
        return cleanText;
    }
}
