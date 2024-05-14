package voltron.cli;

import java.util.Optional;
import lombok.Getter;

@Getter
public class GlobalConfig {

    private static class GlobalConfigHolder {

        private static final GlobalConfig INSTANCE = new GlobalConfig();
    }

    public static GlobalConfig getInstance() {
        return GlobalConfigHolder.INSTANCE;
    }
    private final Optional<String> srvOriginal = Optional.ofNullable(System.getenv("SERVER_ORIGINAL"));
    private final Optional<String> srvMadeUp = Optional.ofNullable(System.getenv("SERVER_MADE_UP"));
}
