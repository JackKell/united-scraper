package parser;

import org.json.JSONObject;

public abstract class TextBlockParser extends BaseParser{
    public abstract JSONObject parse(String text);
}
