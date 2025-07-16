package app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.exception.InvalidTelegramTokenException;
import app.exception.LinkingCompletedException;
import app.model.TelegramLinkingToken;
import app.model.User;
import app.repository.TelegramLinkingTokenRepository;
import app.repository.UserRepository;
import app.service.impl.TelegramAccountLinkingServiceImpl;
import app.util.TelegramLinkingTokenUtils;
import app.util.UserUtils;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TelegramAccountLinkingServiceTests {

    @InjectMocks
    private TelegramAccountLinkingServiceImpl telegramAccountLinkingService;

    @Mock
    private TelegramLinkingTokenRepository telegramLinkingTokenRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("Should return existing unused and unexpired token for user")
    void generateLinkingTokenForUser_ExistingTokenSuccess() {
        User user = UserUtils.createExpectedTestUserOwner();
        TelegramLinkingToken existingToken = TelegramLinkingTokenUtils
                .createExpectedTelegramLinkingToken();
        existingToken.setUser(user);
        existingToken.setUsed(false);
        existingToken.setExpiresAt(LocalDateTime.now().plusMinutes(10));

        when(telegramLinkingTokenRepository.findByUserIdAndIsUsedFalseAndExpiresAtAfter(
                eq(user.getId()), any(LocalDateTime.class)))
                .thenReturn(Optional.of(existingToken));

        String actualToken = telegramAccountLinkingService.generateLinkingTokenForUser(user);

        assertThat(actualToken).isEqualTo(existingToken.getToken());
    }

    @Test
    @DisplayName("Should generate and save new token if no existing valid token")
    void generateLinkingTokenForUser_NewTokenSuccess() {
        User user = UserUtils.createExpectedTestUserOwner();

        when(telegramLinkingTokenRepository.findByUserIdAndIsUsedFalseAndExpiresAtAfter(
                eq(user.getId()), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        when(telegramLinkingTokenRepository.save(any(TelegramLinkingToken.class)))
                .thenAnswer(invocation -> {
                    TelegramLinkingToken savedToken = invocation.getArgument(0);
                    savedToken.setId(200L);
                    return savedToken;
                });

        String actualToken = telegramAccountLinkingService.generateLinkingTokenForUser(user);

        assertThat(actualToken).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("Should successfully link account (new link) and throw LinkingCompletedException")
    void processIncomingLinkingToken_NewLinkSuccess() {
        String receivedToken = "valid_test_token";

        User userToLink = UserUtils.createExpectedTestUserOwner();
        userToLink.setTelegramChatId(null);
        userToLink.setTelegramUserId(null);

        TelegramLinkingToken token = TelegramLinkingTokenUtils
                .createExpectedTelegramLinkingToken();
        token.setToken(receivedToken);
        token.setUser(userToLink);
        token.setUsed(false);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(10));

        when(telegramLinkingTokenRepository.findByToken(receivedToken))
                .thenReturn(Optional.of(token));
        String telegramChatId = "new_telegram_chat_id";

        when(userRepository.findByTelegramChatId(telegramChatId))
                .thenReturn(Optional.empty());

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(telegramLinkingTokenRepository.save(any(TelegramLinkingToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        String telegramUserId = "new_telegram_user_id";

        LinkingCompletedException thrown = assertThrows(
                LinkingCompletedException.class,
                () -> telegramAccountLinkingService.processIncomingLinkingToken(
                        receivedToken, telegramUserId, telegramChatId),
                "Expected LinkingCompletedException for new link success"
        );
        assertThat(thrown.getMessage()).isEqualTo("Great! Your account is connected!");
    }

    @Test
    @DisplayName("Should link account (existing user, chat ID updated) "
            + "and throw LinkingCompletedException")
    void processIncomingLinkingToken_ExistingUserChatIdUpdateSuccess() {
        String receivedToken = "valid_test_token";
        String telegramUserId = "existing_telegram_user_id";
        String oldTelegramChatId = "old_telegram_chat_id";

        User userToLink = UserUtils.createExpectedTestUserOwner();
        userToLink.setTelegramChatId(oldTelegramChatId);
        userToLink.setTelegramUserId(telegramUserId);

        TelegramLinkingToken token = TelegramLinkingTokenUtils
                .createExpectedTelegramLinkingToken();
        token.setToken(receivedToken);
        token.setUser(userToLink);
        token.setUsed(false);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(10));

        when(telegramLinkingTokenRepository.findByToken(receivedToken))
                .thenReturn(Optional.of(token));

        String newTelegramChatId = "new_telegram_chat_id_different";
        when(userRepository.findByTelegramChatId(newTelegramChatId))
                .thenReturn(Optional.of(userToLink));

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(telegramLinkingTokenRepository.save(any(TelegramLinkingToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LinkingCompletedException thrown = assertThrows(
                LinkingCompletedException.class,
                () -> telegramAccountLinkingService.processIncomingLinkingToken(
                        receivedToken, telegramUserId, newTelegramChatId),
                "Expected LinkingCompletedException for chat ID update success"
        );
        assertThat(thrown.getMessage())
                .isEqualTo("Your account has been linked already. No action required.");

        assertThat(userToLink.getTelegramChatId()).isEqualTo(newTelegramChatId);
    }

    @Test
    @DisplayName("Should link account (existing user, chat ID same) "
            + "and throw LinkingCompletedException")
    void processIncomingLinkingToken_ExistingUserChatIdSameSuccess() {
        String receivedToken = "valid_test_token";
        String telegramUserId = "existing_telegram_user_id";
        String existingTelegramChatId = "existing_telegram_chat_id";

        User userToLink = UserUtils.createExpectedTestUserOwner();
        userToLink.setTelegramChatId(existingTelegramChatId);
        userToLink.setTelegramUserId(telegramUserId);

        TelegramLinkingToken token = TelegramLinkingTokenUtils.createExpectedTelegramLinkingToken();
        token.setToken(receivedToken);
        token.setUser(userToLink);
        token.setUsed(false);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(10));

        when(telegramLinkingTokenRepository.findByToken(receivedToken))
                .thenReturn(Optional.of(token));

        when(userRepository.findByTelegramChatId(existingTelegramChatId))
                .thenReturn(Optional.of(userToLink));

        when(telegramLinkingTokenRepository.save(any(TelegramLinkingToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LinkingCompletedException thrown = assertThrows(
                LinkingCompletedException.class,
                () -> telegramAccountLinkingService.processIncomingLinkingToken(
                        receivedToken, telegramUserId, existingTelegramChatId),
                "Expected LinkingCompletedException for same chat ID success"
        );
        assertThat(thrown.getMessage())
                .isEqualTo("Your account has been linked already. No action required.");

        assertThat(userToLink.getTelegramChatId()).isEqualTo(existingTelegramChatId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw InvalidTelegramTokenException if token not found")
    void processIncomingLinkingToken_TokenNotFound() {
        String receivedToken = "non_existent_token";
        String telegramUserId = "some_id";
        String telegramChatId = "some_chat_id";

        when(telegramLinkingTokenRepository.findByToken(receivedToken))
                .thenReturn(Optional.empty());

        InvalidTelegramTokenException thrown = assertThrows(
                InvalidTelegramTokenException.class,
                () -> telegramAccountLinkingService.processIncomingLinkingToken(
                        receivedToken, telegramUserId, telegramChatId),
                "Expected InvalidTelegramTokenException when token not found"
        );
        assertThat(thrown.getMessage()).isEqualTo("Telegram linking token is not valid.");

        verify(userRepository, never()).findByTelegramChatId(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(telegramLinkingTokenRepository, never()).save(any(TelegramLinkingToken.class));
    }

    @Test
    @DisplayName("Should throw InvalidTelegramTokenException if token already used")
    void processIncomingLinkingToken_TokenAlreadyUsed() {
        String receivedToken = "used_token";

        TelegramLinkingToken token = TelegramLinkingTokenUtils.createExpectedTelegramLinkingToken();
        token.setToken(receivedToken);
        token.setUsed(true);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(10));

        when(telegramLinkingTokenRepository.findByToken(receivedToken))
                .thenReturn(Optional.of(token));
        String telegramUserId = "some_id";
        String telegramChatId = "some_chat_id";
        InvalidTelegramTokenException thrown = assertThrows(
                InvalidTelegramTokenException.class,
                () -> telegramAccountLinkingService.processIncomingLinkingToken(
                        receivedToken, telegramUserId, telegramChatId),
                "Expected InvalidTelegramTokenException when token is used"
        );
        assertThat(thrown.getMessage()).isEqualTo("Telegram linking token is used.");

        verify(userRepository, never()).findByTelegramChatId(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(telegramLinkingTokenRepository, never()).save(any(TelegramLinkingToken.class));
    }

    @Test
    @DisplayName("Should throw InvalidTelegramTokenException if token expired and mark as used")
    void processIncomingLinkingToken_TokenExpired() {
        String receivedToken = "expired_token";

        TelegramLinkingToken token = TelegramLinkingTokenUtils.createExpectedTelegramLinkingToken();
        token.setToken(receivedToken);
        token.setUsed(false);
        token.setExpiresAt(LocalDateTime.now().minusMinutes(10));

        when(telegramLinkingTokenRepository.findByToken(receivedToken))
                .thenReturn(Optional.of(token));
        when(telegramLinkingTokenRepository.save(any(TelegramLinkingToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        String telegramUserId = "some_id";
        String telegramChatId = "some_chat_id";
        InvalidTelegramTokenException thrown = assertThrows(
                InvalidTelegramTokenException.class,
                () -> telegramAccountLinkingService.processIncomingLinkingToken(
                        receivedToken, telegramUserId, telegramChatId),
                "Expected InvalidTelegramTokenException when token is expired"
        );
        assertThat(thrown.getMessage()).isEqualTo("Telegram linking token is expired.");

        assertThat(token.isUsed()).isTrue();
        verify(telegramLinkingTokenRepository, times(1)).save(token);
        verify(userRepository, never()).findByTelegramChatId(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException if Telegram account already linked to another user")
    void processIncomingLinkingToken_AlreadyLinkedToAnotherUser() {
        String telegramUserId = "existing_telegram_user_id";
        String telegramChatId = "existing_telegram_chat_id";

        User userToLink = UserUtils.createExpectedTestUserOwner();
        userToLink.setTelegramChatId(null);
        userToLink.setTelegramUserId(null);

        User anotherUser = UserUtils.createCustomerUser(
                200L, "another@example.com",
                "Another", "User",
                "+1234567890", telegramUserId);
        anotherUser.setTelegramChatId(telegramChatId);
        String receivedToken = "valid_token";
        TelegramLinkingToken token = TelegramLinkingTokenUtils
                .createExpectedTelegramLinkingToken();
        token.setToken(receivedToken);
        token.setUser(userToLink);
        token.setUsed(false);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(10));

        when(telegramLinkingTokenRepository.findByToken(receivedToken))
                .thenReturn(Optional.of(token));

        when(userRepository.findByTelegramChatId(telegramChatId))
                .thenReturn(Optional.of(anotherUser));

        when(telegramLinkingTokenRepository.save(any(TelegramLinkingToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> telegramAccountLinkingService.processIncomingLinkingToken(
                        receivedToken, telegramUserId, telegramChatId),
                "Expected RuntimeException when Telegram account "
                        + "is linked to another user"
        );
        assertThat(thrown.getMessage())
                .isEqualTo("Your Telegram account is already "
                        + "linked to another user. Please contact support.");

        assertThat(token.isUsed()).isTrue();
        verify(telegramLinkingTokenRepository, times(1)).save(token);
        verify(userRepository, never()).save(userToLink);
    }
}
