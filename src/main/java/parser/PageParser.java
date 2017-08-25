package parser;

import org.json.JSONObject;

import java.util.List;

abstract class PageParser extends BaseParser{
    abstract JSONObject parse(List<String> texts);
}
