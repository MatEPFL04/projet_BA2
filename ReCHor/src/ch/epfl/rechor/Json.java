package ch.epfl.rechor;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Json {

    record JArray (List<Json> jsonArray) implements Json {
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[");
            jsonArray.forEach(sb::append);
            sb.append("]");
            return sb.toString();
 
        }
    }
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
    record JString (String jsonString) implements Json {
        @Override
        public String toString() {
            return "\"" + jsonString + "\"";
        }

    }
    record JNumber (double jsonNumber) implements Json {
        @Override
        public String toString() {
            return Double.toString(jsonNumber);
        }

    }
}
