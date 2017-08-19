import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CapabilitiesParser {
    JSONObject parse(String capabilitiesText) {
        final String cleanText = clean(capabilitiesText);
        System.out.println(cleanText);
        JSONObject capabilities = new JSONObject();
        final String capabilityRegex = "([\\w ]*): +((?:.*\r\n)*?)(?=[\\w ]*:)";
        final Matcher capabilityMatcher = Pattern.compile(capabilityRegex).matcher(cleanText);
        while (capabilityMatcher.find()) {
            final String capabilityText = capabilityMatcher.group(0);
            final String name = capabilityMatcher.group(1).trim();
            final String description = capabilityMatcher.group(2).replaceAll("\r\n", "").trim();
            final Map<String, Object> capability = new HashMap<>();
            capability.put("name", name);
            capability.put("description", description);
            capabilities.put(name, capability);
        }
        return capabilities;
    }

    private String clean(String text) {
        String cleanText = text;
        // Remove all "Indices and Reference"
        cleanText = cleanText.replaceAll("Indices and Reference\r\n", "");
        // Remove all page numbers
        cleanText = cleanText.replaceAll("\\d{3}\r\n", "");
        // Remove all list range titles
        cleanText = cleanText.replaceAll("Chapter 10:(.*\\s)*?(?=Alluring:)", "");
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
