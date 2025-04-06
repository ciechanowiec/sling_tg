package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.telegram.api.TGChatID;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.math.NumberUtils;
import org.telegram.telegrambots.meta.api.objects.message.MaybeInaccessibleMessage;

@EqualsAndHashCode
class TGChatIDBasic implements TGChatID {

    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    static final TGChatID UNKNOWN = new TGChatID() {

        @Override
        public String asString() {
            return String.valueOf(asLong());
        }

        @Override
        public long asLong() {
            return NumberUtils.LONG_ZERO;
        }
    };

    private final long chatId;

    TGChatIDBasic(MaybeInaccessibleMessage message) {
        chatId = message.getChatId();
    }

    TGChatIDBasic(long chatId) {
        this.chatId = chatId;
    }

    @Override
    public String asString() {
        return String.valueOf(chatId);
    }

    @Override
    public long asLong() {
        return chatId;
    }
}
