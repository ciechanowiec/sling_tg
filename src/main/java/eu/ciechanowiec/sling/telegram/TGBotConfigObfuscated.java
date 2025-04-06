package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.jcr.path.TargetJCRPath;
import eu.ciechanowiec.sling.telegram.api.TGBotHome;
import eu.ciechanowiec.sling.telegram.api.TGBotID;
import eu.ciechanowiec.sling.telegram.api.TGBotToken;
import java.net.URI;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.TelegramUrl;

@ToString
@Getter
@Slf4j
@SuppressWarnings("ClassWithTooManyFields")
class TGBotConfigObfuscated {

    @ToString.Exclude
    private final TGBotToken tgBotToken;
    private final TGBotID tgBotID;
    private final String name;
    private final String description;
    private final String shortDescription;
    private final TGBotHome tgBotHome;
    private final TelegramUrl telegramUrl;

    TGBotConfigObfuscated(TGBotConfig source) {
        this.tgBotToken = source::token;
        this.tgBotID = new TGBotID() {

            @Override
            public String get() {
                return source.id();
            }

            @Override
            public String toString() {
                return String.format("{%s=%s}", TGBotID.class.getName(), get());
            }
        };
        this.name = source.name();
        this.description = source.description();
        this.shortDescription = source.short$_$description();
        this.tgBotHome = new TGBotHome() {

            @Override
            public String get() {
                return new TargetJCRPath(source.jcr_home()).get();
            }

            @Override
            public String toString() {
                return String.format("{%s=%s}", TGBotHome.class.getName(), get());
            }
        };
        URI parsedTelegramUrl = URI.create(source.telegram_url());
        this.telegramUrl = new TelegramUrl(
            parsedTelegramUrl.getScheme(), parsedTelegramUrl.getHost(), parsedTelegramUrl.getPort(), false
        );
        log.debug("Initialized {}", this);
    }
}
