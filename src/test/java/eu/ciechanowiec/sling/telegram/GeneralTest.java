package eu.ciechanowiec.sling.telegram;

import eu.ciechanowiec.conditional.Conditional;
import eu.ciechanowiec.sling.rocket.jcr.path.OccupiedJCRPathException;
import eu.ciechanowiec.sling.rocket.jcr.path.ParentJCRPath;
import eu.ciechanowiec.sling.rocket.jcr.path.TargetJCRPath;
import eu.ciechanowiec.sling.rocket.test.TestEnvironment;
import eu.ciechanowiec.sling.telegram.api.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
        String withoutBinariesTag = "register-update-without-binaries";
        TGRootUpdatesReceiver tgRootUpdatesReceiver = Optional.ofNullable(
                Conditional.conditional(testInfo.getTags().contains(withoutBinariesTag))
                           .onTrue(() -> (TGRootUpdatesReceiver) tgUpdate -> {
                               TGUpdate tgUpdateRegistered = updatesRegistrar.register(tgUpdate, false);
                               log.debug("Registered {}", tgUpdateRegistered);
                           })
                           .onFalse(() -> (TGRootUpdatesReceiver) tgUpdate -> {
                               TGUpdate tgUpdateRegistered = updatesRegistrar.register(tgUpdate);
                               log.debug("Registered {}", tgUpdateRegistered);
                           })
                           .get(TGRootUpdatesReceiver.class)
        ).orElseThrow();
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
                () -> assertEquals(firstTGMessageID.asInt(), Integer.parseInt(firstTGMessageID.asString()))
        );
        Stream.concat(firstTGMessages.all().stream(), secondTGMessages.all().stream())
                .forEach(
                        tgMessage ->
                                assertAll(
                                        () -> assertTrue(tgMessage.tgDocuments().all().isEmpty()),
                                        () -> assertTrue(tgMessage.tgPhotos().all().isEmpty()),
                                        () -> assertTrue(tgMessage.tgVideos().all().isEmpty()),
                                        () -> assertTrue(tgMessage.tgAudios().all().isEmpty()),
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
                () -> assertEquals("/start", tgCommands.of("/start", true).literal()),
                () -> assertEquals("/none", tgCommands.of("/hidden", true).literal()),
                () -> assertEquals("/hidden", tgCommands.of("/hidden", false).literal()),
                () -> assertEquals("/none", tgCommands.of("/unknown", false).literal())
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

    @Test
    void documents() {
        SendDocument sendDocument = new SendDocument(
                chatID, new InputFile(loadResourceIntoFile("documentus.pdf"))
        );
        Message messageSent = firstBot.tgIOGate().execute(sendDocument);
        Update update = new Update();
        update.setMessage(messageSent);
        firstBot.tgIOGate().consumeAsync(update).join();
        List<TGMessage> messages = tgChats.getOrCreate(() -> new TGChatIDBasic(Long.parseLong(chatID)), firstBot)
                .tgMessages()
                .all();
        TGDocument tgDocument = messages.getFirst().tgDocuments().all().stream().findFirst().orElseThrow();
        assertAll(
                () -> assertEquals(1, messages.size()),
                () -> assertEquals(1, messages.getFirst().tgDocuments().all().size()),
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
                        new TGUpdateBasic(update, firstBot, fullResourceAccess)
                                .tgMessage()
                                .tgDocuments()
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
    void videos() {
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
        TGVideo tgVideo = messages.getFirst().tgVideos().all().stream().findFirst().orElseThrow();
        assertAll(
                () -> assertEquals(1, messages.size()),
                () -> assertEquals(1, messages.getFirst().tgVideos().all().size()),
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
                                .tgVideos()
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
    void audios() {
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
        TGAudio tgAudio = messages.getFirst().tgAudios().all().stream().findFirst().orElseThrow();
        assertAll(
                () -> assertEquals(1, messages.size()),
                () -> assertEquals(1, messages.getFirst().tgAudios().all().size()),
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
                                .tgAudios()
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
        SendMessage firstSendMessage = new SendMessage(chatID, "/start");
        Message firstMessageSent = firstBot.tgIOGate().execute(firstSendMessage);
        Update firstUpdate = new Update();
        firstUpdate.setMessage(firstMessageSent);
        firstBot.tgIOGate().consumeAsync(firstUpdate).join();
        TGCommand inMemoryCommand = new TGUpdateBasic(
                firstUpdate, firstBot, fullResourceAccess
        ).tgMessage().tgCommand();
        TGCommand jcrCommand = tgChats.getOrCreate(() -> new TGChatIDBasic(Long.parseLong(chatID)), firstBot)
                .tgMessages()
                .all()
                .getFirst()
                .tgCommand();
        assertAll(
                () -> assertEquals("/start", inMemoryCommand.literal()),
                () -> assertEquals("/none", jcrCommand.literal()),
                () -> assertTrue(inMemoryCommand.isStart()),
                () -> assertFalse(jcrCommand.isStart())
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
                                .tgAudios()
                                .all()
                                .stream()
                                .findFirst()
                                .orElseThrow()
                                .asset()
                                .isEmpty()
                ),
                () -> assertTrue(
                        tgUpdateAfterRegistration.tgMessage()
                                .tgAudios()
                                .all()
                                .stream()
                                .findFirst()
                                .orElseThrow()
                                .asset()
                                .isPresent()
                )
        );
    }

    @SneakyThrows
    @Test
    @Tag("register-update-without-binaries")
    void registerUpdateWithoutBinaries() {
        SendPhoto sendPhoto = new SendPhoto(chatID, new InputFile(loadResourceIntoFile("1.jpeg")));
        SendDocument sendDocument = new SendDocument(
                chatID, new InputFile(loadResourceIntoFile("documentus.pdf"))
        );
        SendVideo sendVideo = new SendVideo(
                chatID, new InputFile(loadResourceIntoFile("morning.mp4"))
        );
        SendAudio sendAudio = new SendAudio(
                chatID, new InputFile(loadResourceIntoFile("time-forward.mp3"))
        );
        Message sentPhoto = firstBot.tgIOGate().execute(sendPhoto);
        Message sentDocument = firstBot.tgIOGate().execute(sendDocument);
        Message sentVideo = firstBot.tgIOGate().execute(sendVideo);
        Message sentAudio = firstBot.tgIOGate().execute(sendAudio);
        Update updatePhoto = new Update();
        updatePhoto.setMessage(sentPhoto);
        Update updateDocument = new Update();
        updateDocument.setMessage(sentDocument);
        Update updateVideo = new Update();
        updateVideo.setMessage(sentVideo);
        Update updateAudio = new Update();
        updateAudio.setMessage(sentAudio);
        CompletableFuture<Void> updatesFutures = CompletableFuture.allOf(
                firstBot.tgIOGate()
                        .consumeAsync(List.of(updatePhoto, updateDocument, updateVideo, updateAudio))
                        .toArray(new CompletableFuture[0])
        );
        updatesFutures.join();
        List<TGMessage> messages = tgChats.getOrCreate(() -> new TGChatIDBasic(Long.parseLong(chatID)), firstBot)
                .tgMessages()
                .all();
        messages.forEach(
                tgMessage -> {
                    tgMessage.tgPhotos().all().forEach(tgAsset -> assertTrue(tgAsset.tgFile().retrieve().isEmpty()));
                    tgMessage.tgDocuments().all().forEach(tgAsset -> assertTrue(tgAsset.tgFile().retrieve().isEmpty()));
                    tgMessage.tgVideos().all().forEach(tgAsset -> assertTrue(tgAsset.tgFile().retrieve().isEmpty()));
                    tgMessage.tgAudios().all().forEach(tgAsset -> assertTrue(tgAsset.tgFile().retrieve().isEmpty()));
                }
        );
        assertEquals(4, messages.size());
    }
}
