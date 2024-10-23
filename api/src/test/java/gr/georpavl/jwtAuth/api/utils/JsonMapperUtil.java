package gr.georpavl.jwtAuth.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JsonMapperUtil {

  private final ObjectMapper objectMapper;

  public String convertToJsonString(Object objectToConvert) throws JsonProcessingException {
    return objectMapper.writeValueAsString(objectToConvert);
  }

  public <T> T convertJsonToObject(String jsonString, Class<T> classToConvert) throws Exception {
    return objectMapper.readValue(jsonString, classToConvert);
  }

  public <T> List<T> convertJsonToListOfObjects(String jsonString)
      throws Exception {
    return objectMapper.readValue(jsonString, new TypeReference<>() {});
  }
}
