package hr.tvz.tim2.webserver.common.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class DateDeserializer extends JsonDeserializer<Date> {
    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        var text = jsonParser.getText();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            return formatter.parse(text);
        } catch (ParseException e) {
            log.error("Error while parsing date: {}", text);
        }

        return null;
    }
}
