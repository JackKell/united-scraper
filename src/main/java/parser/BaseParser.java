package parser;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

abstract class BaseParser {
    protected abstract String clean(String text);

    final String cleanSpecialCharacters(String text) {
        String cleanText = text;
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

    final String parseLabeledString(String label, String text) {
        final String nameRegex = label + " *(.+)";
        final Matcher nameMatcher = Pattern.compile(nameRegex).matcher(text);
        if (nameMatcher.find()) {
            return nameMatcher.group(1).trim();
        }
        return null;
    }

    final Integer parseLabeledInteger(String label, String text) {
        final String nameRegex = label + " *(\\d+)";
        final Matcher nameMatcher = Pattern.compile(nameRegex).matcher(text);
        if (nameMatcher.find()) {
            return parseInt(nameMatcher.group(1).trim());
        }
        return null;
    }

    // Remove all of the new line regardless of the platform
    final String consolidateLines(String text) {
        return text
                .replaceAll("\r", "")
                .replaceAll("\n", "")
                .trim();
    }

    // Remove redundant spaces
    final String consolidateSpaces(String text) {
        return text
                .replaceAll(" {2}", " ")
                .trim();
    }

    final String consolidateLinesAndSpaces(String text) {
        return consolidateSpaces(consolidateLines(text));
    }

    final List<String> parseDelimitedList(String text, String delimiter) {
        return Arrays.stream(text.split(delimiter))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    final List<String> parseLabeledDelimitedList(String label, String delimiter, String text) {
        final String delimitedList = parseLabeledString(label, text);
        return parseDelimitedList(delimitedList, delimiter);
    }
}
