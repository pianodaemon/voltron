package voltron.cli;

import java.util.Optional;
import lombok.Getter;

@Getter
public class GlobalConfig {

    private static class GlobalConfigHolder {

        private static final GlobalConfig INSTANCE = new GlobalConfig();
    }

    public static GlobalConfig getInstance() throws Exception {
        if (Optional.ofNullable(GlobalConfigHolder.INSTANCE.getSrvOriginal()).isEmpty() || Optional.ofNullable(GlobalConfigHolder.INSTANCE.getSrvMadeUp()).isEmpty()) {
            throw new Exception("Original and Made up server files has not been set");
        }
        return GlobalConfigHolder.INSTANCE;
    }
    private final String srvOriginal = System.getenv("SERVER_ORIGINAL");
    private final String srvMadeUp = System.getenv("SERVER_MADE_UP");
}
