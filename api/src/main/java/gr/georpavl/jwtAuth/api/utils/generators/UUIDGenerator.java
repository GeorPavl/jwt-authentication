package gr.georpavl.jwtAuth.api.utils.generators;

import com.fasterxml.uuid.Generators;
import java.util.UUID;

public class UUIDGenerator {

  private UUIDGenerator() {}

  public static UUID generateUUID() {
    return Generators.timeBasedEpochGenerator().generate();
  }
}
