import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class EdgesParser extends TextBlockParser {
    public JSONObject parse(String edgesText) {
        final String cleanText = clean(edgesText);
//        System.out.println(cleanText);
        final JSONObject edges = new JSONObject();
        final String edgeRegex = "Edge: (.*)\\s+[\\w\\W]*?(?=Edge:|$)";
        final Matcher edgeMatcher = Pattern.compile(edgeRegex).matcher(cleanText);
        while (edgeMatcher.find()) {
            final String edgeText = edgeMatcher.group(0);
            final String edgeName = edgeMatcher.group(1);
            final Map<String, Object> edge = new HashMap<>();
            edge.put("name", edgeName);
            edge.put("prerequisites", parsePrerequisites(edgeText));
            edge.put("effect", parseEffect(edgeText));
            edges.put(edgeName, edge);
        }
        return edges;
    }

    // TODO: Make prerequisites an object
    private String parsePrerequisites(String edgeText) {
        final String prerequisitesRegex = "Prerequisites: ([\\w\\W]*?)(?=Effect:)";
        final Matcher prerequisitesMatcher = Pattern.compile(prerequisitesRegex).matcher(edgeText);
        if (prerequisitesMatcher.find()) {
            return consolidateLines(prerequisitesMatcher.group(1));
        }
        return null;
    }

    private String parseEffect(String edgeText) {
        final String effectRegex = "Effect: ([\\w\\W]*?)(?=Edge:|$)";
        final Matcher effectMatcher = Pattern.compile(effectRegex).matcher(edgeText);
        if (effectMatcher.find()) {
            return consolidateLines(effectMatcher.group(1));
        }
        return null;
    }

    protected String clean(String edgesText) {
        String cleanText = edgesText;
        // Remove page subject headers and page numbers
        cleanText = cleanText.replaceAll("Skills, Edges, Feats\\s+\\d{2}\\s+", "");
        // Remove Edges description
        cleanText = cleanText.replaceAll("Edges[\\w\\W\\s]*(?=Skill Edges)", "");
        // Remove Edge Titles
        cleanText = cleanText.replaceAll("\\w* Edges\\s+", "");
        // Remove Cast's note for Skill Stunt Edge
        cleanText = cleanText.replaceAll("Cast.*Note[\\w\\W]*(?=Categoric Inclination)", "");
        // Add "Edge: " in front of each edge name to make parsing easier
        cleanText = cleanText.replaceAll("(.*\\s+(?=Prerequisites))", "Edge: $1");
        // Fix all cases of "Prerequisite:" to "Prerequisites:"
        cleanText = cleanText.replaceAll("Prerequisite:", "Prerequisites:");
        cleanText = cleanSpecialCharacters(cleanText);
        // Remove Static Frequency because all edges have static frequency by default
        cleanText = cleanText.replaceAll("Static\\s+", "");
        return cleanText;
    }
}
