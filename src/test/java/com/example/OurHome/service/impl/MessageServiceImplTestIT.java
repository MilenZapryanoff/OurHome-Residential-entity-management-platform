package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.*;
import com.example.OurHome.model.enums.CityName;
import com.example.OurHome.repo.CityRepository;
import com.example.OurHome.repo.MessageRepository;
import com.example.OurHome.repo.RoleRepository;
import com.example.OurHome.repo.UserRepository;
import com.example.OurHome.service.MessageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MessageServiceImplTestIT {


    @Autowired
    private MessageService messageServiceToTest;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        messageRepository.deleteAll();
        userRepository.deleteAll();
    }


    @AfterEach
    void tearDown() {
        messageRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testFindMessageById() {
        createTestMessage();

        Long id = null;
        List<Message> allMessages = messageRepository.findAll();
        for (Message message : allMessages) {
            id = message.getId();
        }

        messageServiceToTest.findMessageById(id);

        Optional<Message> message = messageRepository.findById(id);

        assertEquals(id, message.get().getId());
    }

    @Test
    void testSendRegistrationMessageToUser() {
        UserEntity user = createTestOwner();

        Long id = null;
        List<UserEntity> all = userRepository.findAll();
        for (UserEntity userEntity : all) {
            id = userEntity.getId();
        }

        messageServiceToTest.sendRegistrationMessageToUser(user);

        Optional<UserEntity> userEntity = userRepository.findById(id);

        assertFalse(userEntity.get().getReceivedMessages().isEmpty());
    }

    @Test
    void testSendMessage() {
        UserEntity user = createTestOwner();
        UserEntity manager = createTestManager();

        List<UserEntity> allUsers = userRepository.findAll();
        Long managerId = allUsers.get(1).getId();

        messageServiceToTest.sendMessage(manager, user, "test");

        Optional<UserEntity> message = userRepository.findById(managerId);

        assertFalse(message.get().getReceivedMessages().isEmpty());
    }

    @Test
    void testReadMessage() {
        createTestMessage();
        List<Message> allMessages = messageRepository.findAll();
        Long messageId = allMessages.get(0).getId();

        messageServiceToTest.readMessage(messageId);

        Optional<Message> expectedMessage = messageRepository.findById(messageId);

        assertTrue(expectedMessage.get().isRead());
    }

    @Test
    void testArchiveMessage() {
        createTestMessage();
        List<Message> allMessages = messageRepository.findAll();
        Long messageId = allMessages.get(0).getId();

        messageServiceToTest.archiveMessage(messageId);

        Optional<Message> expectedMessage = messageRepository.findById(messageId);

        assertTrue(expectedMessage.get().isArchived());
    }

    @Test
    void testDeleteMessage() {
        createTestMessage();
        List<Message> allMessages = messageRepository.findAll();
        Long messageId = allMessages.get(0).getId();

        messageServiceToTest.deleteMessage(messageId);

        Optional<Message> expectedMessage = messageRepository.findById(messageId);

        assertTrue(expectedMessage.isEmpty());
    }

    @Test
    void testDeleteAllMessages() {
        UserEntity userEntity = createTestOwner();

        Message message1 = new Message();
        message1.setArchived(true);
        message1.setDate(LocalDate.parse("2023-01-01"));
        message1.setTime(Time.valueOf(LocalTime.now()));
        message1.setText("Test message");
        message1.setReceiver(userEntity);
        messageRepository.save(message1);

        Message message2 = new Message();
        message2.setArchived(true);
        message2.setDate(LocalDate.parse("2023-01-01"));
        message2.setTime(Time.valueOf(LocalTime.now()));
        message2.setText("Test message");
        message2.setReceiver(userEntity);
        messageRepository.save(message2);

        List<UserEntity> allUsers = userRepository.findAll();
        Long userId = allUsers.get(0).getId();

        messageServiceToTest.deleteAllMessages(userId);

        List<Message> allMessages = messageRepository.findAll();

        assertTrue(allMessages.isEmpty());
    }

    @Test
    void testReadAllMessages() {
        UserEntity userEntity = createTestOwner();

        Message message1 = new Message();
        message1.setRead(false);
        message1.setDate(LocalDate.parse("2023-01-01"));
        message1.setTime(Time.valueOf(LocalTime.now()));
        message1.setText("Test message");
        message1.setReceiver(userEntity);
        messageRepository.save(message1);

        Message message2 = new Message();
        message1.setRead(false);
        message2.setDate(LocalDate.parse("2023-01-01"));
        message2.setTime(Time.valueOf(LocalTime.now()));
        message2.setText("Test message");
        message2.setReceiver(userEntity);
        messageRepository.save(message2);

        List<UserEntity> allUsers = userRepository.findAll();
        Long userId = allUsers.get(0).getId();

        messageServiceToTest.readAllMessages(userId);

        List<Message> allMessages = messageRepository.findAll();

        assertTrue(allMessages.get(0).isRead());
        assertTrue(allMessages.get(1).isRead());
    }


    @Test
    void testArchiveAllMessages() {
        UserEntity userEntity = createTestOwner();

        Message message1 = new Message();
        message1.setArchived(false);
        message1.setDate(LocalDate.parse("2023-01-01"));
        message1.setTime(Time.valueOf(LocalTime.now()));
        message1.setText("Test message");
        message1.setReceiver(userEntity);
        messageRepository.save(message1);

        Message message2 = new Message();
        message1.setArchived(false);
        message2.setDate(LocalDate.parse("2023-01-01"));
        message2.setTime(Time.valueOf(LocalTime.now()));
        message2.setText("Test message");
        message2.setReceiver(userEntity);
        messageRepository.save(message2);

        List<UserEntity> allUsers = userRepository.findAll();
        Long userId = allUsers.get(0).getId();

        messageServiceToTest.archiveAllMessages(userId);

        List<Message> allMessages = messageRepository.findAll();

        assertTrue(allMessages.get(0).isArchived());
        assertTrue(allMessages.get(1).isArchived());
    }

    @Test
    void testPropertyModificationMessageToManager() {
        Property property = createTestData();

        List<UserEntity> allUsers = userRepository.findAll();
        Long managerId = allUsers.get(0).getId();

        messageServiceToTest.propertyModificationMessageToManager(property);

        List<Message> allMessages = messageRepository.findAll();


        assertFalse(allMessages.isEmpty());
        assertEquals(managerId, allMessages.get(0).getReceiver().getId());
    }

    @Test
    void testPropertyModificationMessageToResident() {
        Property property = createTestData();

        List<UserEntity> allUsers = userRepository.findAll();
        Long userId = allUsers.get(1).getId();

        messageServiceToTest.propertyModificationMessageToResident(property);

        List<Message> allMessages = messageRepository.findAll();

        assertFalse(allMessages.isEmpty());
        assertEquals(userId, allMessages.get(0).getReceiver().getId());
    }

    @Test
    void testPropertyApprovedMessage() {
        Property property = createTestData();

        List<UserEntity> allUsers = userRepository.findAll();
        Long userId = allUsers.get(1).getId();

        messageServiceToTest.propertyApprovedMessage(property);

        List<Message> allMessages = messageRepository.findAll();

        assertFalse(allMessages.isEmpty());
        assertEquals(userId, allMessages.get(0).getReceiver().getId());
    }

    @Test
    void testPropertyRejectedMessage() {
        Property property = createTestData();

        List<UserEntity> allUsers = userRepository.findAll();
        Long userId = allUsers.get(1).getId();

        messageServiceToTest.propertyRejectedMessage(property);

        List<Message> allMessages = messageRepository.findAll();

        assertFalse(allMessages.isEmpty());
        assertEquals(userId, allMessages.get(0).getReceiver().getId());
    }

    @Test
    void testPropertyDeletedMessageToOwner() {
        Property property = createTestData();

        List<UserEntity> allUsers = userRepository.findAll();
        Long userId = allUsers.get(1).getId();

        messageServiceToTest.propertyDeletedMessageToOwner(property);

        List<Message> allMessages = messageRepository.findAll();

        assertFalse(allMessages.isEmpty());
        assertEquals(userId, allMessages.get(0).getReceiver().getId());
    }

    @Test
    void testPropertyDeletedMessageToManager() {
        Property property = createTestData();

        List<UserEntity> allUsers = userRepository.findAll();
        Long managerId = allUsers.get(0).getId();

        messageServiceToTest.propertyDeletedMessageToManager(property);

        List<Message> allMessages = messageRepository.findAll();

        assertFalse(allMessages.isEmpty());
        assertEquals(managerId, allMessages.get(0).getReceiver().getId());
    }

    @Test
    void testNewModeratorMessage() {
        Property property = createTestData();

        List<UserEntity> allUsers = userRepository.findAll();
        Long userId = allUsers.get(1).getId();

        messageServiceToTest.newModeratorMessage(property.getOwner(), property.getResidentialEntity());

        List<Message> allMessages = messageRepository.findAll();

        assertFalse(allMessages.isEmpty());
        assertEquals(userId, allMessages.get(0).getReceiver().getId());
    }

    @Test
    void testPropertyRegistrationMessageToManager() {
        Property property = createTestData();

        List<UserEntity> allUsers = userRepository.findAll();
        Long managerId = allUsers.get(0).getId();

        messageServiceToTest.propertyRegistrationMessageToManager(property.getResidentialEntity());

        List<Message> allMessages = messageRepository.findAll();

        assertFalse(allMessages.isEmpty());
        assertEquals(managerId, allMessages.get(0).getReceiver().getId());
    }

    private UserEntity createTestOwner() {
        Role role = roleRepository.findRoleByName("RESIDENT");
        roleRepository.save(role);

        UserEntity user = new UserEntity();
        user.setEmail("test@mail.bg");
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setUsername("tester");
        user.setPassword("testPassword");
        user.setPhoneNumber("0666666666");
        user.setRole(role);
        userRepository.save(user);
        return user;
    }

    private UserEntity createTestManager() {
        UserEntity manager = new UserEntity();
        Role role = roleRepository.findRoleByName("MANAGER");
        roleRepository.save(role);
        manager.setEmail("test@test.test");
        manager.setFirstName("Test");
        manager.setLastName("Test");
        manager.setUsername("testerManager");
        manager.setPassword("testPassword");
        manager.setPhoneNumber("0777777777");
        manager.setRole(role);
        userRepository.save(manager);
        return manager;
    }

    private void createTestMessage() {
        UserEntity user = createTestOwner();
        Message message = new Message();
        message.setRead(false);
        message.setArchived(false);
        message.setDate(LocalDate.parse("2023-01-01"));
        message.setTime(Time.valueOf(LocalTime.now()));
        message.setText("Test message");
        message.setReceiver(user);
        messageRepository.save(message);
    }

    private Property createTestData() {
        UserEntity manager = createTestManager();

        ResidentialEntity residentialEntity = new ResidentialEntity();
        residentialEntity.setManager(manager);
        residentialEntity.setId(100L);
        residentialEntity.setAccessCode("test");
        residentialEntity.setCity(cityRepository.findByName(CityName.valueOf("София")));

        UserEntity owner = createTestOwner();

        Property property = new Property();
        property.setNumber(String.valueOf(1));
        property.setFloor(String.valueOf(1));
        property.setNumberOfAdults(1);
        property.setNumberOfChildren(1);
        property.setNumberOfPets(1);
        property.setOwner(owner);
        property.setResidentialEntity(residentialEntity);
        return property;
    }
}