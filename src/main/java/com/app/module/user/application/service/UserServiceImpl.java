package com.app.module.user.application.service;

import com.app.module.media.dao.MediaDao;
import com.app.module.media.model.Media;
import com.app.module.media.model.MediaType;
import com.app.module.user.application.dto.request.LoginUserRequest;
import com.app.module.user.application.dto.request.RegisterUserRequest;
import com.app.module.user.application.dto.request.UpdateUserRequest;
import com.app.module.user.application.exception.UserAlreadyExistsException;
import com.app.module.user.application.exception.UserNotFoundException;
import com.app.module.user.domain.entity.User;
import com.app.module.user.domain.entity.VerificationToken;
import com.app.module.user.domain.status.Gender;
import com.app.module.user.infrastructure.role.RoleDao;
import com.app.module.user.infrastructure.user.UserDao;
import com.app.module.user.internal.UserValidator;
import com.app.shared.annotations.NoLogging;
import com.app.shared.event.VerificationEmailEvent;
import com.app.shared.exception.sub.FileNotFoundException;
import com.app.shared.storage.FileManager;
import com.app.shared.storage.MediaManager;
import com.app.shared.type.Status;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletionException;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * User service class responsible for user related operations such as registration, login, and
 * retrieval.
 *
 * <p>This class interacts with the UserDaoImpl for data access and utilizes ModelMapper for mapping
 * between DTOs and entity object.
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserDao userDao;
  private final RoleDao roleDao;
  private final MediaDao mediaDao;
  private final VerificationTokenService verificationTokenService;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final ApplicationEventPublisher events;

  /**
   * Registers a new user based on the provided registerDTO.
   *
   * <p>Maps the RegisterDTO to a User entity. encodes the password, and saves the userDao.
   *
   * @param request The RegisterRequestDTO object containing user registration information.
   * @return The registered User entity.
   */
  @NoLogging
  @Override
  public User register(RegisterUserRequest request) {
    final String username = request.username();
    final String email = request.email();
    final String password = request.password();

    UserValidator.validateUsername(username);
    UserValidator.validateEmail(email);
    UserValidator.validatePassword(password);

    if (userDao.findUserByEmail(email) != null) {
      throw new UserAlreadyExistsException("Email is already taken.");
    }

    if (userDao.findUserByUsername(username) != null) {
      throw new UserAlreadyExistsException("Username is already taken.");
    }

    User user =
        userDao.register(
            User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .gender(Gender.OTHER)
                .media(getDefaultProfilePicturePath())
                .roles(Arrays.asList(roleDao.findByName("ROLE_USER")))
                .enable(false)
                .build());

    VerificationToken verificationToken = verificationTokenService.generateVerificationToken(user);
    events.publishEvent(
        new VerificationEmailEvent(user.getEmail(), verificationToken.getToken(), Instant.now()));

    return user;
  }

  /**
   * Authenticates a user based on provided loginDTO.
   *
   * <p>Use the authenticationManager to authenticate the user and sets the authentication in the
   * SecurityContextHolder.
   *
   * @param request The LoginUserRequest object containing user login credentials.
   * @return The authentication User entity.
   */
  @NoLogging
  @Override
  public User login(LoginUserRequest request) {
    try {
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(request.username(), request.password()));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      return userDao.login(request.username());
    } catch (AuthenticationException e) {
      throw new BadCredentialsException("Invalid username or password");
    }
  }

  /**
   * Retrieves a user details by username.
   *
   * @param username The username of the user to retrieve.
   * @return The User entity corresponding to the provided username.
   */
  @Override
  public User findFullUserByUsername(String username) {
    return userDao.findFullUserByUsername(username);
  }

  /**
   * Update existing user details
   *
   * @param request The DTO object containing user info to update.
   * @return The User entity
   */
  @Override
  public User update(UpdateUserRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();

    User user = userDao.findFullUserByUsername(username);

    if (user == null) {
      throw new UserNotFoundException("User not found with a username: " + username);
    }

    if (request.email() != null
        && !request.email().equals(user.getEmail())
        && userDao.findUserByEmail(request.email()) != null) {
      throw new UserAlreadyExistsException("Email is already taken.");
    }

    if (request.username() != null
        && !request.username().equals(user.getUsername())
        && userDao.findUserByUsername(request.username()) != null) {
      throw new UserAlreadyExistsException("Username is already taken.");
    }

    user.setUsername(request.username() != null ? request.username() : user.getUsername());
    user.setEmail(request.email() != null ? request.email() : user.getEmail());
    user.setPassword(
        request.password() != null
            ? passwordEncoder.encode(request.password())
            : user.getPassword());
    user.setBio(request.bio() != null ? request.bio() : user.getBio());
    user.setGender(request.gender() != null ? request.gender() : user.getGender());

    if (request.profilePicture() != null && !request.profilePicture().isEmpty()) {
      saveProfilePicture(user, request.profilePicture());
    } else {
      user.setMedia(user.getMedia());
    }

    return userDao.update(user);
  }

  private void saveProfilePicture(User user, MultipartFile profilePicture) {
    Media existingMedia = mediaDao.findById(user.getMedia().getId());

    String filename = MediaManager.generateUniqueFilename(profilePicture.getOriginalFilename());
    String extension = MediaManager.getFileExtension(profilePicture.getOriginalFilename());

    Media media;
    if (Objects.equals(existingMedia.getUrl(), getDefaultProfilePicturePath().getUrl())) {
      media =
          mediaDao.save(
              Media.builder()
                  .url(filename)
                  .mediaType(MediaType.fromExtension(extension))
                  .status(Status.PENDING)
                  .build());
    } else {
      String oldExtension = MediaManager.getFileExtension(existingMedia.getUrl());

      media =
          mediaDao.update(
              existingMedia.getId(),
              Media.builder()
                  .id(existingMedia.getId())
                  .url(filename)
                  .mediaType(MediaType.fromExtension(extension))
                  .status(Status.PENDING)
                  .build());
      FileManager.delete(existingMedia.getUrl(), oldExtension);
    }

    FileManager.save(profilePicture, filename, extension)
        .thenRunAsync(() -> mediaDao.updateStatus(media.getId(), Status.READY))
        .exceptionally(
            err -> {
              mediaDao.updateStatus(media.getId(), Status.FAILED);
              throw new CompletionException(err);
            });
    user.setMedia(media);
  }

  /**
   * Verify user account
   *
   * @param token The token to verify.
   */
  @Override
  public void verifyAccount(String token) {
    verificationTokenService.verifyAccount(token);
  }

  @Override
  public void resendVerifyToken() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();

    User user = userDao.findUserByUsername(username);
    VerificationToken verificationToken = verificationTokenService.generateVerificationToken(user);
    events.publishEvent(
        new VerificationEmailEvent(user.getEmail(), verificationToken.getToken(), Instant.now()));
  }

  private Media getDefaultProfilePicturePath() {
    Resource defaultProfilePic = new FileSystemResource("image/default_profile_picture.png");
    if (defaultProfilePic.exists()) {
      return Media.builder().id(1L).url(defaultProfilePic.getFilename()).build();
    } else {
      throw new FileNotFoundException("File not found");
    }
  }
}
