package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.telegram.api.TGCommand;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

record TGCommandBasic(String literal, String description, boolean isListable) implements TGCommand {

    static final TGCommand NONE = new TGCommandBasic("/none", "No command", false);

    @Override
    public BotCommand botCommand() {
        return new BotCommand(literal, description);
    }

    @Override
    public boolean isStart() {
        return literal.equals("/start");
    }
}
