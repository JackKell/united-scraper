import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class EdgesParser {
    JSONObject parse(String edgesText) {
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
            edge.put("parseEffect", parseEffect(edgeText));
            edges.put(edgeName, edge);
        }
        return edges;
    }

    private String parsePrerequisites(String edgeText) {
        return null;
    }

    private String parseEffect(String edgeText) {
        return null;
    }

    private String clean(String edgesText) {
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
        // Change all special characters
        cleanText = cleanText.replaceAll("–", "-");
        cleanText = cleanText.replaceAll(",", ",");
        cleanText = cleanText.replaceAll("“", "\"");
        cleanText = cleanText.replaceAll("”", "\"");
        cleanText = cleanText.replaceAll("’", "'");
        cleanText = cleanText.replaceAll("¼", "1/4");
        cleanText = cleanText.replaceAll("½", "1/2");
        cleanText = cleanText.replaceAll("¾", "3/4");
        return cleanText;
    }
}
