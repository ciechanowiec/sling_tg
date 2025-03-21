package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.sling.rocket.jcr.path.OccupiedJCRPathException;
import eu.ciechanowiec.sling.rocket.jcr.path.ParentJCRPath;
import eu.ciechanowiec.sling.rocket.jcr.path.TargetJCRPath;
import eu.ciechanowiec.sling.rocket.test.TestEnvironment;
import eu.ciechanowiec.sling.telegram.api.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SuppressWarnings({
        "VariableDeclarationUsageDistance", "MethodLength", "squid:S2925", "ClassFanOutComplexity",
        "OverlyCoupledClass", "ClassDataAbstractionCoupling", "MultipleStringLiterals", "PMD.ExcessiveImports",
        "PMD.CouplingBetweenObjects", "PMD.AvoidDuplicateLiterals"
})
class GeneralTest extends TestEnvironment {

    private String chatID;
    private TGBot firstBot;
    private TGBot secondBot;
    private TGChats tgChats;

    GeneralTest() {
        super(ResourceResolverType.JCR_OAK);
    }

    @BeforeEach
    void setup(TestInfo testInfo) {
        String firstBotToken = Optional.ofNullable(System.getProperty("sling.firstBotToken")).orElseThrow();
        String secondBotToken = Optional.ofNullable(System.getProperty("sling.secondBotToken")).orElseThrow();
        chatID = Optional.ofNullable(System.getProperty("sling.chatID")).orElseThrow();
        tgChats = context.registerInjectActivateService(TGChatsBasic.class);
        String withoutCommandsTag = "without-commands";
        if (testInfo.getTags().contains(withoutCommandsTag)) {
            context.registerInjectActivateService(TGCommandsBasic.class);
        } else {
            Map<String, String[]> commandsConfig = Map.of(
                    "tg-commands.mappings", new String[]{
                            "/start###Starts the bot###true",
                            "/examplus###Example command###true",
                            "/bad-boolean###Invalid boolean###tttruee",
                            "/invalid-command###Isn't recognized###",
                            "/hidden###Hidded command###false"
                    }
            );
            context.registerInjectActivateService(TGCommandsBasic.class, commandsConfig);
        }
        TGUpdatesRegistrarBasic updatesRegistrar = context.registerInjectActivateService(TGUpdatesRegistrarBasic.class);
        TGRootUpdatesReceiver tgRootUpdatesReceiver = tgUpdate -> {
                               TGUpdate tgUpdateRegistered = updatesRegistrar.register(tgUpdate);
                               log.debug("Registered {}", tgUpdateRegistered);
                           };
        context.registerService(TGRootUpdatesReceiver.class, tgRootUpdatesReceiver);
        context.registerInjectActivateService(TGBotRegistrarBasic.class);
        Map<String, String> firstBotProps = Map.of(
                "token", firstBotToken,
                "id", "lukus",
                "name", "Lukus Stonus",
                "description", "Lukus Descriptionus Fullus",
                "short-description", "Lukus Descriptionus Shortus",
                "jcr.home", "/content/homes/lukus"
        );
        Map<String, String> secondBotProps = Map.of(
                "token", secondBotToken,
                "id", "munus",
                "name", "Munus Bustus",
                "description", "Munus Descriptionus Fullus",
                "short-description", "Munus Descriptionus Shortus",
                "jcr.home", "/content/homes/munus"
        );
        firstBot = context.registerInjectActivateService(TGBotBasic.class, firstBotProps);
        secondBot = context.registerInjectActivateService(TGBotBasic.class, secondBotProps);
    }

    @Test
    @SneakyThrows
    @SuppressWarnings("squid:S2925")
    void sendBasicMessages() {
        SendMessage firstSendMessageA = new SendMessage(chatID, "First bot message A");
        SendMessage firstSendMessageB = new SendMessage(chatID, "First bot message B");
        SendMessage secondSendMessageA = new SendMessage(chatID, "Second bot message A");
        SendMessage secondSendMessageB = new SendMessage(chatID, "Second bot message B");
        Message firstMessageASent = firstBot.tgIOGate().execute(firstSendMessageA);
        Message secondMessageASent = secondBot.tgIOGate().execute(secondSendMessageA);
        TimeUnit.SECONDS.sleep(NumberUtils.INTEGER_ONE); // We need some distance between messages to sort them
        Message firstMessageBSent = firstBot.tgIOGate().execute(firstSendMessageB);
        Message secondMessageBSent = secondBot.tgIOGate().execute(secondSendMessageB);
        Update firstUpdateA = new Update();
        Update firstUpdateB = new Update();
        Update secondUpdateA = new Update();
        Update secondUpdateB = new Update();
        firstUpdateA.setMessage(firstMessageASent);
        firstUpdateB.setMessage(firstMessageBSent);
        secondUpdateA.setMessage(secondMessageASent);
        secondUpdateB.setMessage(secondMessageBSent);
        CompletableFuture<Void> updatesFutures = CompletableFuture.allOf(
                ArrayUtils.addAll(
                        firstBot.tgIOGate()
                                .consumeAsync(List.of(firstUpdateA, firstUpdateB))
                                .toArray(new CompletableFuture[0]),
                        secondBot.tgIOGate()
                                .consumeAsync(List.of(secondUpdateA, secondUpdateB))
                                .toArray(new CompletableFuture[0])
                )
        );
        updatesFutures.join();
        TGMessages firstTGMessages = tgChats.getOrCreate(() -> new TGChatIDBasic(Long.parseLong(chatID)), firstBot)
                                            .tgMessages();
        TGMessages secondTGMessages = tgChats.getOrCreate(() -> new TGChatIDBasic(Long.parseLong(chatID)), secondBot)
                                     .tgMessages();

        List<String> firstBotTexts = firstTGMessages.all()
                .stream()
                .map(TGMessage::tgText)
                .map(TGText::get)
                .toList();
        List<String> secondBotTexts = secondTGMessages.all()
                .stream()
                .map(TGMessage::tgText)
                .map(TGText::get)
                .toList();
        TGMessageID firstTGMessageID = firstTGMessages.all().getFirst().tgMessageID();
        TGMessageID secondTGMessageID = secondTGMessages.all().getFirst().tgMessageID();
        assertAll(
                () -> assertEquals(List.of("First bot message A", "First bot message B"), firstBotTexts),
                () -> assertEquals(List.of("Second bot message A", "Second bot message B"), secondBotTexts),
                () -> assertNotEquals(firstTGMessageID.asInt(), secondTGMessageID.asInt()),
                () -> assertTrue(firstTGMessageID.asInt() > NumberUtils.INTEGER_ZERO),
                () -> assertTrue(secondTGMessageID.asInt() > NumberUtils.INTEGER_ZERO),
                () -> assertEquals(firstTGMessageID.asInt(), (int) firstTGMessageID.asLong()),
                () -> assertEquals(firstTGMessageID.asInt(), Integer.parseInt(firstTGMessageID.asString())),
                () -> assertTrue(firstTGMessages.hasAny()),
                () -> assertTrue(secondTGMessages.hasAny())
        );
        Stream.concat(firstTGMessages.all().stream(), secondTGMessages.all().stream())
                .forEach(
                        tgMessage ->
                                assertAll(
                                        () -> assertTrue(tgMessage.tgDocument().isEmpty()),
                                        () -> assertTrue(tgMessage.tgPhotos().all().isEmpty()),
                                        () -> assertTrue(tgMessage.tgVideo().isEmpty()),
                                        () -> assertTrue(tgMessage.tgAudio().isEmpty()),
                                        () -> assertTrue(tgMessage.tgActivationStatus().isActive()),
                                        () -> assertTrue(tgMessage.tgActor().isBot()),
                                        () -> assertFalse(tgMessage.tgActor().firstName().isEmpty()),
                                        () -> assertFalse(tgMessage.tgActor().userName().isEmpty()),
                                        () -> assertTrue(tgMessage.tgActor().lastName().isEmpty()),
                                        () -> assertTrue(tgMessage.tgActor().id() > NumberUtils.INTEGER_ONE)
                                )
                );
    }

    @SneakyThrows
    @Test
    void activationStatus() {
        SendMessage firstSendMessageA = new SendMessage(chatID, "First bot message A");
        SendMessage firstSendMessageB = new SendMessage(chatID, "First bot message B");
        Message firstMessageASent = firstBot.tgIOGate().execute(firstSendMessageA);
        TimeUnit.SECONDS.sleep(NumberUtils.INTEGER_ONE); // We need some distance between messages to sort them
        Message firstMessageBSent = firstBot.tgIOGate().execute(firstSendMessageB);
        Update firstUpdateA = new Update();
        Update firstUpdateB = new Update();
        firstUpdateA.setMessage(firstMessageASent);
        firstUpdateB.setMessage(firstMessageBSent);
        CompletableFuture<Void> updatesFutures = CompletableFuture.allOf(
                firstBot.tgIOGate()
                        .consumeAsync(List.of(firstUpdateA, firstUpdateB))
                        .toArray(new CompletableFuture[0])
        );
        updatesFutures.join();
        TGMessages firstTGMessages = tgChats.getOrCreate(() -> new TGChatIDBasic(Long.parseLong(chatID)), firstBot)
                .tgMessages();
        assertEquals(2, firstTGMessages.all().size());
        firstTGMessages.all().forEach(
                tgMessage -> assertTrue(tgMessage.tgActivationStatus().isActive())
        );
        IntStream.rangeClosed(0, 1)
                .forEach(
                        i -> firstTGMessages.all().stream()
                                .map(tgMessage -> tgMessage.tgActivationStatus().deactivate())
                                .forEach(tgActivationStatus -> assertFalse(tgActivationStatus.isActive()))
                );
        firstTGMessages.all()
                .forEach(tgMessage -> assertFalse(tgMessage.tgActivationStatus().isActive()));
        IntStream.rangeClosed(0, 1)
                .forEach(
                        i -> firstTGMessages.all().stream()
                                .map(tgMessage -> tgMessage.tgActivationStatus().activate())
                                .forEach(tgActivationStatus -> assertTrue(tgActivationStatus.isActive()))
                );
        firstTGMessages.all()
                .forEach(tgMessage -> assertTrue(tgMessage.tgActivationStatus().isActive()));
        firstTGMessages.deactivateAll();
        firstTGMessages.all()
                .forEach(tgMessage -> assertFalse(tgMessage.tgActivationStatus().isActive()));
    }

    @SneakyThrows
    @Test
    @SuppressWarnings("PMD.NcssCount")
    void onlyActiveRetrieval() {
        TGMessages tgMessages = tgChats.getOrCreate(() -> new TGChatIDBasic(Long.parseLong(chatID)), firstBot)
                .tgMessages();
        assertFalse(tgMessages.hasAny());
        SendMessage sendMessageA = new SendMessage(chatID, "First bot message A");
        SendMessage sendMessageB = new SendMessage(chatID, "First bot message B");
        Message messageASent = firstBot.tgIOGate().execute(sendMessageA);
        TimeUnit.SECONDS.sleep(NumberUtils.INTEGER_ONE); // We need some distance between messages to sort them
        Message messageBSent = firstBot.tgIOGate().execute(sendMessageB);
        Update updateA = new Update();
        Update updateB = new Update();
        updateA.setMessage(messageASent);
        updateB.setMessage(messageBSent);
        CompletableFuture<Void> updatesFuturesPartOne = CompletableFuture.allOf(
                firstBot.tgIOGate()
                        .consumeAsync(List.of(updateA, updateB))
                        .toArray(new CompletableFuture[0])
        );
        updatesFuturesPartOne.join();
        assertEquals(2, tgMessages.all().size());
        tgMessages.all().forEach(
                tgMessage -> assertTrue(tgMessage.tgActivationStatus().isActive())
        );
        tgMessages.deactivateAll();
        assertTrue(tgMessages.active().isEmpty());
        SendMessage sendMessageC = new SendMessage(chatID, "First bot message C");
        SendMessage sendMessageD = new SendMessage(chatID, "First bot message D");
        Message messageCSent = firstBot.tgIOGate().execute(sendMessageC);
        TimeUnit.SECONDS.sleep(NumberUtils.INTEGER_ONE); // We need some distance between messages to sort them
        Message messageDSent = firstBot.tgIOGate().execute(sendMessageD);
        Update updateC = new Update();
        Update updateD = new Update();
        updateC.setMessage(messageCSent);
        updateD.setMessage(messageDSent);
        CompletableFuture<Void> updatesFuturesPartTwo = CompletableFuture.allOf(
                firstBot.tgIOGate()
                        .consumeAsync(List.of(updateC, updateD))
                        .toArray(new CompletableFuture[0])
        );
        updatesFuturesPartTwo.join();
        assertAll(
                () -> assertEquals(2, tgMessages.active().size()),
                () -> assertEquals("First bot message C", tgMessages.active().getFirst().tgText().get()),
                () -> assertEquals("First bot message D", tgMessages.active().get(1).tgText().get())
        );
    }

    @Test
    void photos() {
        SendPhoto sendPhoto = new SendPhoto(chatID, new InputFile(loadResourceIntoFile("1.jpeg")));
        Message messageSent = firstBot.tgIOGate().execute(sendPhoto);
        Update update = new Update();
        update.setMessage(messageSent);
        CompletableFuture<Void> completableFuture = firstBot.tgIOGate().consumeAsync(update);
        completableFuture.join();
        PhotosFromTGUpdate photosFromTGUpdate = new PhotosFromTGUpdate(() -> update, firstBot.tgIOGate());
        Collection<File> photosUnique = photosFromTGUpdate.retrieve(true);
        Collection<File> photosNonUnique = photosFromTGUpdate.retrieve(false);
        assertAll(
                () -> assertEquals(3, photosUnique.size()),
                () -> assertEquals(5, photosNonUnique.size())
        );
        List<TGMessage> messages = tgChats.getOrCreate(() -> new TGChatIDBasic(Long.parseLong(chatID)), firstBot)
                                          .tgMessages()
                                          .all();
        TGPhoto tgPhoto = messages.getFirst().tgPhotos().all().stream().findFirst().orElseThrow();
        assertAll(
                () -> assertEquals(1, messages.size()),
                () -> assertEquals(3, messages.getFirst().tgPhotos().all().size()),
                () -> assertTrue(tgPhoto.tgFile().retrieve().orElseThrow().exists()),
                () -> assertTrue(tgPhoto.tgMetadata().originalFileName().isEmpty()),
                () -> assertEquals("image/jpeg", tgPhoto.tgMetadata().mimeType()),
                () -> assertEquals(StringUtils.EMPTY, tgPhoto.tgMetadata().all().get(TGMetadata.PN_ORIGINAL_FILE_NAME)),
                () -> assertFalse(
                        tgPhoto.asset().orElseThrow().jcrPath().get().isBlank()
                ),
                () -> assertFalse(
                        tgPhoto.asset().orElseThrow().jcrUUID().isBlank()
                ),
                () -> assertTrue(
                        new TGUpdateBasic(update, firstBot, fullResourceAccess)
                                .tgMessage()
                                .tgPhotos()
                                .all()
                                .stream()
                                .findFirst()
                                .orElseThrow()
                                .asset()
                                .isEmpty()
                )
        );
    }

    @Test
    void tgHome() {
        assertAll(
                () -> assertEquals("/content/homes/lukus", firstBot.tgBotHome().get()),
                () -> assertEquals("/content/homes/munus", secondBot.tgBotHome().get())
        );
    }

    @Test
    @Tag("without-commands")
    void withoutCommands() {
        assertTrue(Optional.ofNullable(
                context.getService(TGCommands.class)
        ).orElseThrow().all(false).isEmpty());
    }

    @Test
    void withCommands() {
        TGCommands tgCommands = Optional.ofNullable(context.getService(TGCommands.class)).orElseThrow();
        assertAll(
                () -> assertEquals(2, tgCommands.all(true).size()),
                () -> assertEquals(2, tgCommands.setMyCommands(Locale.ENGLISH).orElseThrow().getCommands().size()),
                () -> assertEquals(3, tgCommands.all(false).size()),
                () -> assertEquals(TGCommand.START_LITERAL, tgCommands.of(TGCommand.START_LITERAL, true).literal()),
                () -> assertEquals(TGCommand.NONE_LITERAL, tgCommands.of("/hidden", true).literal()),
                () -> assertEquals("/hidden", tgCommands.of("/hidden", false).literal()),
                () -> assertEquals(TGCommand.NONE_LITERAL, tgCommands.of("/unknown", false).literal())
        );
    }

    @Test
    void updateWithNoChatID() {
        Update update = new Update();
        TGChatID tgChatID = new TGUpdateBasic(update, firstBot, fullResourceAccess).tgChatID();
        assertAll(
                () -> assertEquals(TGChatIDBasic.UNKNOWN, tgChatID),
                () -> assertEquals(NumberUtils.LONG_ZERO, tgChatID.asLong()),
                () -> assertEquals(String.valueOf(NumberUtils.LONG_ZERO), tgChatID.asString())
        );
    }

    @Test
    @SneakyThrows
    void updateWithChatID() {
        SendMessage sendMessage = new SendMessage(chatID, "First bot message");
        Message messageSent = firstBot.tgIOGate().execute(sendMessage);
        Update update = new Update();
        update.setMessage(messageSent);
        TGChatID tgChatID = new TGUpdateBasic(update, firstBot, fullResourceAccess).tgChatID();
        assertAll(
                () -> assertEquals(Long.parseLong(chatID), tgChatID.asLong()),
                () -> assertEquals(chatID, tgChatID.asString())
        );
    }

    @SneakyThrows
    @Test
    @SuppressWarnings("MagicNumber")
    void document() {
        int fileSizeMB = 22;
        File tooBigFile = File.createTempFile("22MB_File", ".txt");
        String content = "A".repeat(1024); // 1 KB of data
        FileUtils.writeStringToFile(tooBigFile, content.repeat(fileSizeMB * 1024), "UTF-8");
        SendDocument sendUsualDocument = new SendDocument(
                chatID, new InputFile(loadResourceIntoFile("documentus.pdf"))
        );
        SendDocument sendTooBigDocument = new SendDocument(
                chatID, new InputFile(tooBigFile)
        );
        Message usualMessageSent = firstBot.tgIOGate().execute(sendUsualDocument);
        Message tooBigMessageSent = firstBot.tgIOGate().execute(sendTooBigDocument);
        Update usualUpdate = new Update();
        Update tooBigUpdate = new Update();
        usualUpdate.setMessage(usualMessageSent);
        tooBigUpdate.setMessage(tooBigMessageSent);
        firstBot.tgIOGate().consumeAsync(usualUpdate).join();
        firstBot.tgIOGate().consumeAsync(tooBigUpdate).join();
        List<TGMessage> messages = tgChats.getOrCreate(() -> new TGChatIDBasic(Long.parseLong(chatID)), firstBot)
                .tgMessages()
                .all();
        TGDocument tgDocument = messages.getFirst().tgDocument().orElseThrow();
        assertAll(
                () -> assertEquals(2, messages.size()),
                () -> assertTrue(messages.getFirst().tgDocument().isPresent()),
                () -> assertTrue(messages.getLast().tgDocument().isEmpty()),
                () -> assertTrue(tgDocument.tgFile().retrieve().orElseThrow().exists()),
                () -> assertTrue(tgDocument.tgMetadata().originalFileName().startsWith("jcr-binary_")),
                () -> assertEquals("application/pdf", tgDocument.tgMetadata().mimeType()),
                () -> assertTrue(
                        tgDocument.tgMetadata().all().get(TGMetadata.PN_ORIGINAL_FILE_NAME).startsWith("jcr-binary_")
                ),
                () -> assertFalse(
                        tgDocument.asset().orElseThrow().jcrPath().get().isBlank()
                ),
                () -> assertFalse(
                        tgDocument.asset().orElseThrow().jcrUUID().isBlank()
                ),
                () -> assertTrue(
                        new TGUpdateBasic(usualUpdate, firstBot, fullResourceAccess)
                                .tgMessage()
                                .tgDocument()
                                .orElseThrow()
                                .asset()
                                .isEmpty()
                )
        );
    }

    @Test
    void video() {
        SendVideo sendVideo = new SendVideo(
                chatID, new InputFile(loadResourceIntoFile("morning.mp4"))
        );
        Message messageSent = firstBot.tgIOGate().execute(sendVideo);
        Update update = new Update();
        update.setMessage(messageSent);
        firstBot.tgIOGate().consumeAsync(update).join();
        List<TGMessage> messages = tgChats.getOrCreate(() -> new TGChatIDBasic(Long.parseLong(chatID)), firstBot)
                .tgMessages()
                .all();
        TGVideo tgVideo = messages.getFirst().tgVideo().orElseThrow();
        assertAll(
                () -> assertEquals(1, messages.size()),
                () -> assertTrue(messages.getFirst().tgVideo().isPresent()),
                () -> assertTrue(tgVideo.tgFile().retrieve().orElseThrow().exists()),
                () -> assertTrue(tgVideo.tgMetadata().originalFileName().startsWith("jcr-binary_")),
                () -> assertEquals("video/mp4", tgVideo.tgMetadata().mimeType()),
                () -> assertTrue(
                        tgVideo.tgMetadata().all().get(TGMetadata.PN_ORIGINAL_FILE_NAME).startsWith("jcr-binary_")
                ),
                () -> assertFalse(
                        tgVideo.asset().orElseThrow().jcrPath().get().isBlank()
                ),
                () -> assertFalse(
                        tgVideo.asset().orElseThrow().jcrUUID().isBlank()
                ),
                () -> assertTrue(
                        new TGUpdateBasic(update, firstBot, fullResourceAccess)
                                .tgMessage()
                                .tgVideo()
                                .orElseThrow()
                                .asset()
                                .isEmpty()
                )
        );
    }

    @Test
    void audio() {
        SendAudio sendAudio = new SendAudio(
                chatID, new InputFile(loadResourceIntoFile("time-forward.mp3"))
        );
        Message messageSent = firstBot.tgIOGate().execute(sendAudio);
        Update update = new Update();
        update.setMessage(messageSent);
        firstBot.tgIOGate().consumeAsync(update).join();
        List<TGMessage> messages = tgChats.getOrCreate(() -> new TGChatIDBasic(Long.parseLong(chatID)), firstBot)
                .tgMessages()
                .all();
        TGAudio tgAudio = messages.getFirst().tgAudio().orElseThrow();
        assertAll(
                () -> assertEquals(1, messages.size()),
                () -> assertTrue(messages.getFirst().tgAudio().isPresent()),
                () -> assertTrue(tgAudio.tgFile().retrieve().orElseThrow().exists()),
                () -> assertTrue(tgAudio.tgMetadata().originalFileName().startsWith("jcr-binary_")),
                () -> assertEquals("audio/mpeg", tgAudio.tgMetadata().mimeType()),
                () -> assertTrue(
                        tgAudio.tgMetadata().all().get(TGMetadata.PN_ORIGINAL_FILE_NAME).startsWith("jcr-binary_")
                ),
                () -> assertFalse(
                        tgAudio.asset().orElseThrow().jcrPath().get().isBlank()
                ),
                () -> assertFalse(
                        tgAudio.asset().orElseThrow().jcrUUID().isBlank()
                ),
                () -> assertTrue(
                        new TGUpdateBasic(update, firstBot, fullResourceAccess)
                                .tgMessage()
                                .tgAudio()
                                .orElseThrow()
                                .asset()
                                .isEmpty()
                )
        );
    }

    @Test
    void voice() {
        SendVoice sendVoice = new SendVoice(
                chatID, new InputFile(loadResourceIntoFile("time-forward.mp3"))
        );
        Message messageSent = firstBot.tgIOGate().execute(sendVoice);
        Update update = new Update();
        update.setMessage(messageSent);
        firstBot.tgIOGate().consumeAsync(update).join();
        List<TGMessage> messages = tgChats.getOrCreate(() -> new TGChatIDBasic(Long.parseLong(chatID)), firstBot)
                .tgMessages()
                .all();
        TGVoice tgVoice = messages.getFirst().tgVoice().orElseThrow();
        assertAll(
                () -> assertEquals(1, messages.size()),
                () -> assertTrue(messages.getFirst().tgVoice().isPresent()),
                () -> assertTrue(tgVoice.tgFile().retrieve().orElseThrow().exists()),
                () -> assertTrue(tgVoice.tgMetadata().originalFileName().isEmpty()),
                () -> assertEquals("audio/ogg", tgVoice.tgMetadata().mimeType()),
                () -> assertTrue(
                        tgVoice.tgMetadata().all().get(TGMetadata.PN_ORIGINAL_FILE_NAME).isEmpty()
                ),
                () -> assertFalse(
                        tgVoice.asset().orElseThrow().jcrPath().get().isBlank()
                ),
                () -> assertFalse(
                        tgVoice.asset().orElseThrow().jcrUUID().isBlank()
                ),
                () -> assertTrue(
                        new TGUpdateBasic(update, firstBot, fullResourceAccess)
                                .tgMessage()
                                .tgVoice()
                                .orElseThrow()
                                .asset()
                                .isEmpty()
                )
        );
    }

    @Test
    void sendMediaGroup() {
        InputMediaPhoto photoOne = new InputMediaPhoto(loadResourceIntoFile("1.jpeg"), "1.jpeg");
        InputMediaPhoto photoTwo = new InputMediaPhoto(loadResourceIntoFile("2.jpeg"), "2.jpeg");
        InputMediaVideo video = new InputMediaVideo(loadResourceIntoFile("morning.mp4"), "morning.mp4");
        SendMediaGroup sendMediaGroup = new SendMediaGroup(chatID, List.of(photoOne, photoTwo, video));
        List<Message> messagesSent = firstBot.tgIOGate().execute(sendMediaGroup);
        List<Update> updates = messagesSent.stream()
                .map(message -> {
                    Update update = new Update();
                    update.setMessage(message);
                    return update;
                })
                .toList();
        CompletableFuture<Void> consumption = CompletableFuture.allOf(firstBot.tgIOGate()
                .consumeAsync(updates)
                .toArray(new CompletableFuture[0]));
        consumption.join();
        List<TGMessage> messages = tgChats.getOrCreate(() -> new TGChatIDBasic(Long.parseLong(chatID)), firstBot)
                .tgMessages()
                .all();
        assertEquals(3, messages.size());
    }

    @SneakyThrows
    @Test
    void consumeListOfUpdates() {
        SendMessage firstSendMessageA = new SendMessage(chatID, "First bot message A");
        SendMessage firstSendMessageB = new SendMessage(chatID, "First bot message B");
        Message firstMessageASent = firstBot.tgIOGate().execute(firstSendMessageA);
        Message firstMessageBSent = firstBot.tgIOGate().execute(firstSendMessageB);
        Update firstUpdateA = new Update();
        Update firstUpdateB = new Update();
        firstUpdateA.setMessage(firstMessageASent);
        firstUpdateB.setMessage(firstMessageBSent);
        firstBot.tgIOGate().consume(List.of(firstUpdateA, firstUpdateB));
        TimeUnit.SECONDS.sleep(2); // Wait until consumption is over
        List<TGMessage> messages = tgChats.getOrCreate(() -> new TGChatIDBasic(Long.parseLong(chatID)), firstBot)
                .tgMessages()
                .all();
        assertEquals(2, messages.size());
    }

    @SneakyThrows
    @Test
    void sendingDate() {
        SendMessage firstSendMessage = new SendMessage(chatID, "Textus examplus");
        Message firstMessageASent = firstBot.tgIOGate().execute(firstSendMessage);
        Update firstUpdate = new Update();
        firstUpdate.setMessage(firstMessageASent);
        firstBot.tgIOGate().consumeAsync(firstUpdate).join();
        TGSendingDate tgSendingDate = tgChats.getOrCreate(() -> new TGChatIDBasic(Long.parseLong(chatID)), firstBot)
                .tgMessages()
                .all()
                .getFirst()
                .tgSendingDate();
        String rawPath = "/content/telegram/sending-date";
        context.build()
               .resource(rawPath)
               .commit();
        ParentJCRPath sendingJCRPathParent = new ParentJCRPath(new TargetJCRPath(rawPath));
        tgSendingDate.save(sendingJCRPathParent);
        assertAll(
                () -> assertThrows(OccupiedJCRPathException.class, () -> tgSendingDate.save(sendingJCRPathParent))
        );
    }

    @SneakyThrows
    @Test
    void command() {
        String hiddenLiteral = "/hidden";
        SendMessage sendMessageStart = new SendMessage(chatID, TGCommand.START_LITERAL);
        SendMessage sendMessageHidden = new SendMessage(chatID, hiddenLiteral);
        TGCommand hiddenCommand = Objects.requireNonNull(context.getService(TGCommands.class)).of(hiddenLiteral, false);
        Message messageStart = firstBot.tgIOGate().execute(sendMessageStart);
        Message messageHidden = firstBot.tgIOGate().execute(sendMessageHidden);
        Update updateStart = new Update();
        Update updateHidden = new Update();
        updateStart.setMessage(messageStart);
        updateHidden.setMessage(messageHidden);
        firstBot.tgIOGate().consumeAsync(updateStart).join();
        firstBot.tgIOGate().consumeAsync(updateHidden).join();
        TGCommand startInMemoryCommand = new TGUpdateBasic(
                updateStart, firstBot, fullResourceAccess
        ).tgMessage().tgCommand();
        TGCommand hiddenInMemoryCommand = new TGUpdateBasic(
                updateHidden, firstBot, fullResourceAccess
        ).tgMessage().tgCommand();
        TGCommand startJCRCommand = tgChats.getOrCreate(() -> new TGChatIDBasic(Long.parseLong(chatID)), firstBot)
                .tgMessages()
                .all()
                .stream()
                .filter(tgMessage -> tgMessage.tgText().get().equals(TGCommand.START_LITERAL))
                .toList()
                .getFirst()
                .tgCommand();
        assertAll(
                () -> assertEquals(TGCommand.START_LITERAL, startInMemoryCommand.literal()),
                () -> assertEquals(TGCommand.NONE_LITERAL, hiddenInMemoryCommand.literal()),
                () -> assertEquals(TGCommand.NONE_LITERAL, startJCRCommand.literal()),
                () -> assertEquals(hiddenLiteral, messageHidden.getText()),
                () -> assertEquals(hiddenLiteral, hiddenCommand.literal()),
                () -> assertTrue(startInMemoryCommand.isStart()),
                () -> assertFalse(startJCRCommand.isStart()),
                () -> assertFalse(startInMemoryCommand.isNone()),
                () -> assertTrue(startJCRCommand.isNone())
        );
    }

    @Test
    void tgChatPath() {
        TGChat firstTgChat = tgChats.getOrCreate(() -> new TGChatIDBasic(Long.parseLong(chatID)), firstBot);
        TGChat secondTgChat = tgChats.getOrCreate(() -> new TGChatIDBasic(Long.parseLong(chatID)), secondBot);
        assertAll(
                () -> assertEquals(
                        new TargetJCRPath(
                                new ParentJCRPath(new TargetJCRPath("/content/telegram/chats/lukus")), chatID
                        ), firstTgChat.jcrPath()
                ),
                () -> assertEquals(
                        new TargetJCRPath(
                                new ParentJCRPath(new TargetJCRPath("/content/telegram/chats/munus")), chatID
                        ), secondTgChat.jcrPath()
                )
        );
    }

    @SneakyThrows
    @Test
    void registerUpdate() {
        SendAudio sendAudio = new SendAudio(
                chatID, new InputFile(loadResourceIntoFile("time-forward.mp3"))
        );
        Message messageSent = firstBot.tgIOGate().execute(sendAudio);
        Update update = new Update();
        update.setMessage(messageSent);
        TGUpdate tgUpdateBeforeRegistration = new TGUpdateBasic(update, firstBot, fullResourceAccess);
        TGUpdatesRegistrar registrar = Optional.ofNullable(context.getService(TGUpdatesRegistrar.class)).orElseThrow();
        TGUpdate tgUpdateAfterRegistration = registrar.register(tgUpdateBeforeRegistration);
        assertAll(
                () -> assertNotEquals(tgUpdateBeforeRegistration, tgUpdateAfterRegistration),
                () -> assertEquals(
                        tgUpdateBeforeRegistration.originalUpdate(), tgUpdateAfterRegistration.originalUpdate()
                ),
                () -> assertNotEquals(
                        tgUpdateBeforeRegistration.tgMessage(),
                        tgUpdateAfterRegistration.tgMessage()
                ),
                () -> assertTrue(
                        tgUpdateBeforeRegistration.tgMessage()
                                .tgAudio()
                                .orElseThrow()
                                .asset()
                                .isEmpty()
                ),
                () -> assertTrue(
                        tgUpdateAfterRegistration.tgMessage()
                                .tgAudio()
                                .orElseThrow()
                                .asset()
                                .isPresent()
                )
        );
    }
}
