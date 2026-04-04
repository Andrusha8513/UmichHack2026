package com.user_service.user_service;

import com.example.support_module.jwt.*;
import com.example.support_module.redis.RedisEmailService;
import com.example.support_module.redis.RedisJwtService;


import com.user_service.user_service.dto.*;
import com.user_service.user_service.dto.mapping.UniversityMapper;
import com.user_service.user_service.dto.mapping.UserMapper;
import com.user_service.user_service.exeptionHandler.TooManyRequestsException;
import com.user_service.user_service.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final RedisJwtService redisJwtService;
    private final KafkaProducer kafkaProducer;
    private final RedisEmailService redisEmailService;
    private final UniversityMapper universityMapper;



    /**
     * Регистрирует нового пользователя.
     * Проверяет уникальность email и длину пароля,
     * устанавливает роль ROLE_USER, кодирует пароль,
     * генерирует код подтверждения, сохраняет пользователя в БД,
     * сохраняет код в Redis и отправляет письмо с кодом и данные профиля через Kafka.
     *
     * @param userDto DTO с данными для регистрации
     * @throws IllegalArgumentException если email уже существует или пароль короче 8 символов
     */
    @Transactional
    public void createUsers(UserRegistrationDTO userDto) {
        if (userRepository.findByEmail((userDto.getEmail())).isPresent()) {
            throw new IllegalArgumentException("Пользователь с такой почтой уже существует");
        }
        if (userDto.getPassword().length() < 8) {
            throw new IllegalArgumentException("Пароль должен быть длинней восьми символом");
        }
        Users users = userMapper.toEntity(userDto);
        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        if (userDto.getRole() != null) {
            if (userDto.getRole().equals(Role.ROLE_STUDENT)) {
                users.setRoles(Set.of(Role.ROLE_STUDENT));
            }
        }


        users.setPassword(passwordEncoder.encode(users.getPassword()));
        users.setConfirmationCode(code);
        userRepository.save(users);

        try {
            redisEmailService.saveEmailConfirmation(code);
        } catch (Exception e) {
            log.error("ОШИБКА ОТПРАВКИ В REDIS: ", e);
        }

        EmailRequestDto emailRequestDto = new EmailRequestDto();
        emailRequestDto.setTo(users.getEmail());
        emailRequestDto.setCode(users.getConfirmationCode());
        emailRequestDto.setType(EmailRequestDto.EmailType.CONFIRMATION);
        try {
            kafkaProducer.sendEmailToKafka(emailRequestDto);
        } catch (Exception e) {
            log.error("ОШИБКА ОТПРАВКИ В KAFKA: ", e);
        }


        ProfileDto profileDto = new ProfileDto();
        profileDto.setId(users.getId());
        profileDto.setName(userDto.getName());
        profileDto.setSecondName(userDto.getSecondName());
        profileDto.setEmail(users.getEmail());
        profileDto.setSurName(userDto.getSurName());
        kafkaProducer.sendPrivetProfileToKafka(profileDto);

    }

    @Transactional
    public void createUniversity(UniversityRegistrationDto universityRegistrationDto) {
        if (userRepository.findByEmail((universityRegistrationDto.email())).isPresent()) {
            throw new IllegalArgumentException("Пользователь с такой почтой уже существует");
        }
        if (universityRegistrationDto.password().length() < 8) {
            throw new IllegalArgumentException("Пароль должен быть длинней восьми символом");
        }
        Users users = universityMapper.toEntity(universityRegistrationDto);
        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        if (universityRegistrationDto.role() != null) {
            if (universityRegistrationDto.role().equals(Role.ROLE_UNIVERSITY)) {
                users.setRoles(Set.of(Role.ROLE_UNIVERSITY));
            }
        }


        users.setPassword(passwordEncoder.encode(users.getPassword()));
        users.setConfirmationCode(code);
        userRepository.save(users);

        try {
            redisEmailService.saveEmailConfirmation(code);
        } catch (Exception e) {
            log.error("ОШИБКА ОТПРАВКИ В REDIS: ", e);
        }

        EmailRequestDto emailRequestDto = new EmailRequestDto();
        emailRequestDto.setTo(users.getEmail());
        emailRequestDto.setCode(users.getConfirmationCode());
        emailRequestDto.setType(EmailRequestDto.EmailType.CONFIRMATION);
        try {
            kafkaProducer.sendEmailToKafka(emailRequestDto);
        } catch (Exception e) {
            log.error("ОШИБКА ОТПРАВКИ В KAFKA: ", e);
        }


        UniversityDto  universityDto = universityMapper.toDto(users);
        universityDto.setUniversity(universityRegistrationDto.university());
        kafkaProducer.sendPrivetUniversityToKafka(universityDto);

    }

    /**
     * Подтверждает регистрацию пользователя по коду.
     * Ищет пользователя с указанным кодом, проверяет активность кода в Redis,
     * активирует аккаунт (enable = true, accountNonLocked = true),
     * удаляет код из БД и Redis.
     *
     * @param code код подтверждения
     * @return true, если подтверждение успешно, иначе false
     */
    @Transactional
    public boolean confirmRegistration(String code) {
        Optional<Users> usersOptional = userRepository.findByConfirmationCode(code);

        if (usersOptional.isPresent() && redisEmailService.isCodeAlive(code)) {
            Users users = usersOptional.get();
            users.setEnable(true);
            users.setAccountNonLocked(true);
            users.setConfirmationCode(null);
            userRepository.save(users);
            redisEmailService.deleteConfirmationCode(code);
            return true;
        }
        return false;
    }

    /**
     * Аутентифицирует пользователя (вход в систему).
     * Проверяет email и пароль, убеждается, что аккаунт активирован и не заблокирован.
     * Генерирует JWT-токены (access и refresh), сохраняет refresh-токен в БД.
     *
     * @param userCredentialsDto учётные данные (email и пароль)
     * @return DTO с access и refresh токенами
     * @throws AuthenticationException  если email или пароль неверны
     * @throws IllegalArgumentException если аккаунт не активирован
     */
    public JwtAuthenticationDto singIn(UserCredentialsDto userCredentialsDto) throws AuthenticationException {
        Users users = findByCredentials(userCredentialsDto);
        if (users.getEnable() == true && users.isAccountNonLocked() && jwtService.validateJwtToken(users.getRefreshToken())) {
            return jwtService.refreshBaseToken(users.getId(), users.getEmail(), users.getRoles(), true, true, users.getRefreshToken());
        }

        if (users.getEnable() == true && users.isAccountNonLocked()) {
            JwtAuthenticationDto jwtAuthenticationDto = jwtService.generateAuthToken(users.getId(), users.getEmail(), users.getRoles(), true, true);
            users.setRefreshToken(jwtAuthenticationDto.getRefreshToken());
            userRepository.save(users);
            return jwtAuthenticationDto;
        } else {
            throw new IllegalArgumentException("Пользователь не подтвердил почту!");
        }
    }

    /**
     * Вспомогательный метод для поиска пользователя по email и проверки пароля.
     *
     * @param userCredentialsDto учётные данные
     * @return найденный пользователь
     * @throws AuthenticationException если пользователь не найден или пароль неверен
     */
    private Users findByCredentials(UserCredentialsDto userCredentialsDto) throws AuthenticationException {
        Optional<Users> optionalUsers = userRepository.findByEmail(userCredentialsDto.getEmail());
        if (optionalUsers.isPresent()) {
            Users users = optionalUsers.get();
            if (passwordEncoder.matches(userCredentialsDto.getPassword(), users.getPassword())) {
                return users;
            }
        }
        throw new AuthenticationException("Почта или пароль неверны");
    }

    /**
     * Обновляет access-токен по действующему refresh-токену.
     * Проверяет валидность refresh-токена, находит пользователя по email из токена
     * и генерирует новую пару токенов.
     *
     * @param refreshTokenDto DTO с refresh-токеном
     * @return новые токены
     * @throws Exception если токен недействителен
     */
    public JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto) throws Exception {
        String refreshToken = refreshTokenDto.getRefreshToken();
        if (refreshToken != null && jwtService.validateJwtToken(refreshToken)) {
            Users users = findByEmail(jwtService.getEmailFromToken(refreshToken));
            return jwtService.refreshBaseToken(users.getId(), users.getEmail(), users.getRoles(), users.getEnable(), users.isAccountNonLocked(), refreshToken);
        }
        throw new AuthenticationException("Недействительный рефреш токен");
    }

    /**
     * Выполняет выход из системы: помещает переданный access-токен в чёрный список Redis
     * на оставшееся время его жизни.
     *
     * @param accessToken access-токен
     */
    public void logout(String accessToken) {
        long ttl = jwtService.getTimeFromToken(accessToken);
        if (ttl > 0) {
            redisJwtService.saveTokenToBlackList(accessToken, ttl);
        }
    }

    /**
     * Полный выход из системы: удаляет refresh-токен пользователя из БД
     * и добавляет access-токен в чёрный список Redis.
     *
     * @param userId      идентификатор пользователя
     * @param accessToken текущий access-токен
     */
    @Transactional
    public void fullLogout(Long userId, String accessToken) {
        Users users = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        users.setRefreshToken(null);
        userRepository.save(users);

        long ttl = jwtService.getTimeFromToken(accessToken);

        redisJwtService.saveTokenToBlackList(accessToken, ttl);
    }


    /**
     * Обновляет refresh-токен (альтернативный метод).
     * Получает email из токена, проверяет, что токен совпадает с сохранённым в БД,
     * генерирует новые токены и сохраняет новый refresh.
     *
     * @param refreshToken старый refresh-токен
     * @return новые токены
     * @throws AuthenticationException если токен недействителен
     */
    //надо мб допилить, на скорую руку писал
    public JwtAuthenticationDto updateRefreshToken(String refreshToken) throws AuthenticationException {
        String email = jwtService.getEmailFromToken(refreshToken);

        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        if (!jwtService.validateJwtToken(users.getEmail())) {
            throw new AuthenticationException("Недействительный рефреш токен");
        }

        if (!refreshToken.equals(users.getRefreshToken())) {
            throw new AuthenticationException("Неверный refresh токен");
        }
        JwtAuthenticationDto newTokens = new JwtAuthenticationDto();

        String tokenAccess = String.valueOf(jwtService.generateAuthToken(users.getId(), users.getEmail(), users.getRoles(), users.getEnable(), users.isAccountNonLocked()));
        String refreshRefreshToken = String.valueOf(jwtService.refreshRefreshToken(users.getEmail()));
        newTokens.setToken(tokenAccess);
        newTokens.setRefreshToken(refreshRefreshToken);

        users.setRefreshToken(newTokens.getRefreshToken());
        userRepository.save(users);
        return newTokens;
    }

    /**
     * Повторно отправляет код подтверждения для активации аккаунта.
     * Проверяет лимит запросов, убеждается, что аккаунт ещё не активирован,
     * генерирует новый код, сохраняет его в Redis и БД, отправляет письмо через Kafka.
     *
     * @param email email пользователя
     * @throws IllegalArgumentException если пользователь не найден или аккаунт уже активирован
     * @throws TooManyRequestsException если превышен лимит запросов
     */
    @Transactional
    public void resendConfirmationCode(String email) {
        checkEmailRateLimit(email);
        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с такой почтой не найден"));

        if (users.getEnable()) {
            throw new IllegalArgumentException("Аккаунт уже активирован");
        }
        if (users.getConfirmationCode() != null) {
            redisEmailService.deleteConfirmationCode(users.getConfirmationCode());
        }

        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        redisEmailService.saveEmailConfirmation(code);

        users.setConfirmationCode(code);
        userRepository.save(users);

        EmailRequestDto emailRequestDto = new EmailRequestDto();
        emailRequestDto.setTo(users.getEmail());
        emailRequestDto.setCode(users.getConfirmationCode());
        emailRequestDto.setType(EmailRequestDto.EmailType.CONFIRMATION);
        kafkaProducer.sendEmailToKafka(emailRequestDto);
    }

    /**
     * Проверяет, не превышен ли лимит запросов на отправку писем (не более 3).
     * Использует счётчик в Redis.
     *
     * @param email email пользователя
     * @throws TooManyRequestsException если лимит превышен
     */
    private void checkEmailRateLimit(String email) {
        long count = redisEmailService.incrementEmailCount(email);
        if (count > 3) {
            throw new TooManyRequestsException("Слишком много запросов. Попробуйте через 15 минут.");
        }
    }

    /**
     * Инициирует сброс пароля: генерирует код, сохраняет его в БД и Redis,
     * отправляет письмо с кодом.
     *
     * @param email email пользователя
     * @throws IllegalArgumentException если пользователь не найден
     */
    @Transactional
    public void sendPasswordResetCode(String email) {
        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с такой почтой не найден"));

        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

//        LocalDateTime time = LocalDateTime.now().plusMinutes(15);
//        users.setPasswordResetCodeExpiryDate(time);

        users.setPasswordResetCode(code);
        userRepository.save(users);

        redisEmailService.saveResetCode(code);

        EmailRequestDto emailRequestDto = new EmailRequestDto();
        emailRequestDto.setTo(users.getEmail());
        emailRequestDto.setCode(users.getPasswordResetCode());
        emailRequestDto.setType(EmailRequestDto.EmailType.PASSWORD_RESET);
        kafkaProducer.sendEmailToKafka(emailRequestDto);
    }

    /**
     * Сбрасывает пароль с использованием кода подтверждения.
     * Проверяет код и его срок действия, обновляет пароль (хеширует), очищает временные поля.
     *
     * @param email       email пользователя
     * @param code        код подтверждения
     * @param newPassword новый пароль
     * @throws IllegalArgumentException если пользователь не найден, код неверен или истёк,
     *                                  либо пароль слишком короткий
     */
    @Transactional
    public void resetPasswordWithCode(String email, String code, String newPassword) {
        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с такой почтой не найден"));

        if (users.getPasswordResetCode() == null || !users.getPasswordResetCode().equals(code) || LocalDateTime.now().isAfter(users.getPasswordResetCodeExpiryDate())) {
            throw new IllegalArgumentException("Неверный или просроченный  код для сброса пароля");
        }
        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("Пароля не должен быть короче восьми символов!");
        }

        redisEmailService.deleteResetCode(code);
        users.setPassword(passwordEncoder.encode(newPassword));
        users.setPasswordResetCode(null);
        users.setPasswordResetCodeExpiryDate(null);
        userRepository.save(users);
    }

    /**
     * Начинает процесс смены email. Генерирует код, сохраняет pendingEmail и код,
     * отправляет письмо на старый адрес.
     *
     * @param email    старый email
     * @param newEmail новый email
     * @throws IllegalArgumentException если пользователь не найден или новый email уже занят
     */
    @Transactional
    public void sendEmailResetCode(String email, String newEmail) {

        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        if (userRepository.findByEmail(newEmail).isPresent()) {
            throw new IllegalArgumentException("Почта уже занята");
        }
        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        users.setEmailChangeCode(code);
        users.setPendingEmail(newEmail);

        userRepository.save(users);

        EmailRequestDto emailRequestDto = new EmailRequestDto();
        emailRequestDto.setTo(users.getEmail());
        emailRequestDto.setCode(users.getConfirmationCode());
        emailRequestDto.setType(EmailRequestDto.EmailType.CONFIRMATION);
        kafkaProducer.sendEmailToKafka(emailRequestDto);
    }

    /**
     * Повторно отправляет код для смены email (если пользователь не получил предыдущее письмо).
     * Проверяет лимит, генерирует новый код, обновляет в БД и Redis.
     *
     * @param pendingEmail новый (ожидающий подтверждения) email
     * @throws IllegalArgumentException если почта не найдена
     * @throws TooManyRequestsException если превышен лимит запросов
     */
    @Transactional
    public void resendEmailResetCode(String pendingEmail) {
        checkEmailRateLimit(pendingEmail);
        Users users = userRepository.findByPendingEmail(pendingEmail)
                .orElseThrow(() -> new IllegalArgumentException("Почта не найдена"));

        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

//        LocalDateTime time = LocalDateTime.now().plusMinutes(15);
//        users.setTtlEmailCode(time);

        redisEmailService.saveEmailConfirmation(code);

        users.setEmailChangeCode(code);
        userRepository.save(users);

        EmailRequestDto emailRequestDto = new EmailRequestDto();
        emailRequestDto.setTo(users.getEmail());
        emailRequestDto.setCode(users.getConfirmationCode());
        emailRequestDto.setType(EmailRequestDto.EmailType.CONFIRMATION);
        kafkaProducer.sendEmailToKafka(emailRequestDto);
    }

    /**
     * Подтверждает смену email. Проверяет код, обновляет email пользователя на pendingEmail,
     * очищает временные поля и отправляет обновлённый профиль в Kafka.
     *
     * @param id   идентификатор пользователя
     * @param code код подтверждения
     * @throws IllegalArgumentException если пользователь не найден или код неверен/истёк
     */
    @Transactional
    public void confirmEmailChange(Long id, String code) {
        Users users = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с таким id" + id + " не найден"));

        if (users.getEmailChangeCode() == null || !users.getEmailChangeCode().equals(code) || LocalDateTime.now().isAfter(users.getTtlEmailCode())) {
            throw new IllegalArgumentException("Неверный или истёкший код подтверждения.");
        }

        users.setEmail(users.getPendingEmail());

        users.setTtlEmailCode(null);
        users.setPendingEmail(null);
        users.setEmailChangeCode(null);
        userRepository.save(users);

        ProfileDto profileDto = new ProfileDto();
        profileDto.setEmail(users.getEmail());
        kafkaProducer.sendPrivetProfileToKafka(profileDto);

    }

    /**
     * Обновляет пароль пользователя (для авторизованных пользователей).
     * Требует текущий пароль для проверки, затем сохраняет новый (хешированный).
     *
     * @param id             идентификатор пользователя
     * @param newPassword    новый пароль
     * @param currenPassword текущий пароль
     * @throws IllegalArgumentException если пользователь не найден,
     *                                  текущий пароль не указан или неверен, новый пароль слишком короткий
     */
    @Transactional
    public void updateUserPassword(Long id,
                                   String newPassword,
                                   String currenPassword) {
        Users users = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с таким id" + id + " не найден"));

        if (newPassword != null && !newPassword.isEmpty()) {
            if (currenPassword == null || currenPassword.isEmpty()) {
                throw new IllegalArgumentException("Должен предоставлен быть текущий пароль");
            }
            if (newPassword.length() < 8) {
                throw new IllegalArgumentException("Пароль не должен быть короче 8 символов");
            }
            if (!passwordEncoder.matches(currenPassword, users.getPassword())) {
                throw new IllegalArgumentException("Текущий пароль не верен");
            }
            users.setPassword(passwordEncoder.encode(newPassword));
        }
        userRepository.save(users);
    }

    /**
     * Вспомогательный метод для поиска пользователя по email.
     *
     * @param email email
     * @return пользователь
     * @throws Exception если пользователь не найден
     */
    private Users findByEmail(String email) throws Exception {
        return userRepository.findByEmail(email).orElseThrow(() -> new Exception(String.format("Пользователя с такой почтой %s не найдено", email)));
    }

    /**
     * Обновляет роли пользователя. После сохранения генерирует новые токены
     * и сохраняет их.
     *
     * @param id      идентификатор пользователя
     * @param newRole новый набор ролей
     * @throws IllegalArgumentException если пользователь не найден или роли пусты
     */
    @Transactional
    public void updateRoles(Long id, Set<Role> newRole) {
        Users users = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователя с таким " + id + " не найденно"));

        if (newRole == null || newRole.isEmpty()) {
            throw new IllegalArgumentException("Роли не могут быть пустыми");
        }
        users.setRoles(newRole);
        jwtService.refreshBaseToken(users.getId(), users.getEmail(), users.getRoles(), users.getEnable(), users.isAccountNonLocked(), users.getRefreshToken());
        userRepository.save(users);

    }

    /**
     * Возвращает список всех пользователей в виде DTO.
     *
     * @return список UserRegistrationDTO
     */
    public List<UserRegistrationDTO> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    /**
     * Изменяет статус аккаунта (enable). При деактивации сбрасывает refresh-токен
     * и добавляет пользователя в чёрный список Redis.
     *
     * @param id               идентификатор пользователя
     * @param newAccountStatus новый статус (true – активирован, false – деактивирован)
     * @throws UsernameNotFoundException если пользователь не найден
     */
    @Transactional
    public void changeAccountStatus(Long id, boolean newAccountStatus) {
        Users users = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        users.setEnable(newAccountStatus);
        users.setRefreshToken(null);
        userRepository.save(users);

        redisJwtService.blockUserId(id);
    }

    /**
     * Блокирует или разблокирует аккаунт (accountNonLocked).
     * Сбрасывает refresh-токен и управляет чёрным списком (блокировка/разблокировка в Redis).
     *
     * @param id               идентификатор пользователя
     * @param newAccountStatus true – разблокирован, false – заблокирован
     * @throws UsernameNotFoundException если пользователь не найден
     */
    @Transactional
    public void accountBlocking(Long id, boolean newAccountStatus) {
        Users users = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));


        users.setAccountNonLocked(newAccountStatus);
        users.setRefreshToken(null);
        userRepository.save(users);

        if (!newAccountStatus) {
            redisJwtService.blockUserId(id);
        }
        if (newAccountStatus) {
            redisJwtService.unblockUserId(id);
        }
    }

    /**
     * Обновляет имя пользователя и отправляет изменённый профиль в Kafka.
     *
     * @param id      идентификатор пользователя
     * @param newName новое имя
     * @throws UsernameNotFoundException если пользователь не найден
     */
    @Transactional
    public void updateName(Long id, String newName) {
        Users users = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        users.setName(newName);
        userRepository.save(users);

        ProfileDto profileDto = userMapper.toTestProfileDto(users);
        kafkaProducer.sendPrivetProfileToKafka(profileDto);

        kafkaProducer.sendPrivetProfileToKafka(profileDto);
    }

    /**
     * Обновляет фамилию пользователя и отправляет изменённый профиль в Kafka.
     *
     * @param id            идентификатор пользователя
     * @param newSecondName новая фамилия
     * @throws UsernameNotFoundException если пользователь не найден
     */
    @Transactional
    public void updateSecondName(Long id, String newSecondName) {
        Users users = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        users.setSecondName(newSecondName);
        userRepository.save(users);

        ProfileDto profileDto = userMapper.toTestProfileDto(users);
        kafkaProducer.sendPrivetProfileToKafka(profileDto);
    }


    @Transactional
    public void updateSurName(Long id, String newSurName) {
        Users users = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        users.setSurName(newSurName);
        userRepository.save(users);

        ProfileDto profileDto = userMapper.toTestProfileDto(users);
        kafkaProducer.sendPrivetProfileToKafka(profileDto);
    }


}