package ch.epfl.rechor;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The interface represents a JSON document.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public sealed interface Json permits Json.JArray, Json.JObject, Json.JString, Json.JNumber {

    /**
     * Represents a JSON array.
     *
     * @param jsonArray list of JSON values which are the elements of the array.
     */
    record JArray (List<Json> jsonArray) implements Json {
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[");
            jsonArray.forEach(sb::append);
            sb.append("]");
            return sb.toString();
        }
    }

    /**
     * Represents a JSON object.
     *
     * @param jsonObject map which associates Json values to java strings.
     */
    record JObject (Map <Json,String> jsonObject) implements Json {
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("{");
            Set<Map.Entry <Json,String>> pairs = jsonObject.entrySet();
            pairs.forEach(pair -> {
                if (!(sb.length() == 1)) {
                    sb.append(",");
                }
                Json object = pair.getKey();
                String str = pair.getValue();
                sb.append(object).append(":").append(str);
            });
            return sb.append("}").toString();
        }
    }

    /**
     * Represents a JSON string.
     *
     * @param jsonString java string representing a JSON string.
     */
    record JString (String jsonString) implements Json {
        @Override
        public String toString() {
            return "\"" + jsonString + "\"";
        }

    }

    /**
     * Represents a JSON number.
     *
     * @param jsonNumber java double representing the JSON number.
     */
    record JNumber (double jsonNumber) implements Json {
        @Override
        public String toString() {
            return Double.toString(jsonNumber);
        }
    }
}
