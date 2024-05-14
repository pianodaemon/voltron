package voltron.cli;

import java.util.Optional;
import lombok.Getter;

@Getter
public class GlobalConfig {

    private static class GlobalConfigHolder {

        private static final GlobalConfig INSTANCE = new GlobalConfig();
    }

    public static GlobalConfig getInstance() throws Exception {
        if (GlobalConfigHolder.INSTANCE.getSrvOriginal().isEmpty() || GlobalConfigHolder.INSTANCE.getSrvMadeUp().isEmpty()) {
            throw new Exception("Original and Made up server files has not been set");
        }
        return GlobalConfigHolder.INSTANCE;
    }
    private final Optional<String> srvOriginal = Optional.ofNullable(System.getenv("SERVER_ORIGINAL"));
    private final Optional<String> srvMadeUp = Optional.ofNullable(System.getenv("SERVER_MADE_UP"));
}
