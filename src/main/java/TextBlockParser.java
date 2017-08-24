import org.json.JSONObject;

abstract class TextBlockParser extends BaseParser{
    abstract JSONObject parse(String text);
}
