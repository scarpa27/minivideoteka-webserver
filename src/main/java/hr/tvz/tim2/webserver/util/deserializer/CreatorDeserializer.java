package hr.tvz.tim2.webserver.util.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hr.tvz.tim2.webserver.movie.entities.CompanyEntity;
import hr.tvz.tim2.webserver.movie.entities.CreatorEntity;
import hr.tvz.tim2.webserver.movie.entities.PersonEntity;

import java.io.IOException;

public class CreatorDeserializer extends JsonDeserializer<CreatorEntity> {
    @Override
    public CreatorEntity deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode rootNode = mapper.readTree(jsonParser);

//        System.out.println(rootNode);

        var type = rootNode.get("@type").asText();
        if (type.equals("Person")) {
            var name = rootNode.get("name").asText();
            var url = rootNode.get("url").asText();

            var id = url.split("/")[2];

            return new PersonEntity(id, name);
        } else if (type.equals("Organization")) {
            var url = rootNode.get("url").asText();
            var id = url.split("/")[2];
            return new CompanyEntity(id);
        }

        return null;
    }
}
