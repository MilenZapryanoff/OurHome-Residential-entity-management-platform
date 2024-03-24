package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.Expense;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.repo.FinancialRepository;
import com.example.OurHome.service.ResidentialEntityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinancialServiceImplTest {

    private FinancialServiceImpl serviceToTest;
    @Mock
    private FinancialRepository mockFinancialRepository;

    @Mock
    private ResidentialEntityService mockResidentialEntityService;
    @Mock
    private ModelMapper mockModelMapper;
    @Mock
    private PasswordEncoder mockPasswordEncoder;

    FinancialServiceImplTest(ResidentialEntityService mockResidentialEntityService) {
        this.mockResidentialEntityService = mockResidentialEntityService;
    }

    @BeforeEach
    void setUp() {
        serviceToTest = new FinancialServiceImpl(mockFinancialRepository, mockModelMapper, mockPasswordEncoder, mockResidentialEntityService);
    }

    @Test
    void findById() {
        Expense expense = new Expense();
        expense.setAmount(BigDecimal.valueOf(99));
        when(mockFinancialRepository.findById(1L)).thenReturn(Optional.of(expense));

        Expense expectedExpense = serviceToTest.findById(1L);

        assertEquals(BigDecimal.valueOf(99), expectedExpense.getAmount());
    }

    @Test
    public void testSaveDocumentValidImage() throws IOException {
        Long expenseId = 1L;
        ResidentialEntity residentialEntity = new ResidentialEntity();
        residentialEntity.setId(1010L);
        Expense expense = new Expense();
        expense.setId(expenseId);
        expense.setDescription("testExpense");
        expense.setResidentialEntity(residentialEntity);

        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        when(mockFinancialRepository.findById(expenseId)).thenReturn(Optional.of(expense));

        String documentPath = serviceToTest.saveDocument(file, expenseId);

        assertNotNull(documentPath);
        assertTrue(documentPath.startsWith("/documents/"));
    }

    @Test
    public void testSaveDocumentInvalidImageType() {
        Long expenseId = 1L;
        ResidentialEntity residentialEntity = new ResidentialEntity();
        residentialEntity.setId(1010L);
        Expense expense = new Expense();
        expense.setId(expenseId);
        expense.setDescription("testExpense");
        expense.setResidentialEntity(residentialEntity);

        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.txt",
                "text/plain",
                "test image content".getBytes()
        );

        when(mockFinancialRepository.findById(expenseId)).thenReturn(Optional.of(expense));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> serviceToTest.saveDocument(file, expenseId));

        assertEquals("Invalid file type!", exception.getMessage());

        verify(mockFinancialRepository, times(1)).findById(expenseId);
        verify(mockPasswordEncoder, never()).encode(anyString());
    }

    @Test
    public void testSaveDocumentFileSizeExceedsLimit() {
        Long expenseId = 1L;
        ResidentialEntity residentialEntity = new ResidentialEntity();
        residentialEntity.setId(1010L);
        Expense expense = new Expense();
        expense.setId(expenseId);
        expense.setDescription("testExpense");
        expense.setResidentialEntity(residentialEntity);

        // Create a file with size exceeding the limit (e.g., 5MB)
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                new byte[5 * 1024 * 1024] // 5MB content
        );

        when(mockFinancialRepository.findById(expenseId)).thenReturn(Optional.of(expense));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> serviceToTest.saveDocument(file, expenseId));

        assertEquals("File size exceeds the allowed limit (3MB)", exception.getMessage());

        verify(mockFinancialRepository, times(1)).findById(expenseId);
    }
    @Test
    public void testSaveDocumentEmptyFile() {
        Long expenseId = 1L;
        ResidentialEntity residentialEntity = new ResidentialEntity();
        residentialEntity.setId(1010L);
        Expense expense = new Expense();
        expense.setId(expenseId);
        expense.setDescription("testExpense");
        expense.setResidentialEntity(residentialEntity);

        // Create an empty file
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "",
                "image/jpeg",
                new byte[0] // Empty content
        );

        when(mockFinancialRepository.findById(expenseId)).thenReturn(Optional.of(expense));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> serviceToTest.saveDocument(file, expenseId));

        assertEquals("File is null or empty!", exception.getMessage());

        verify(mockFinancialRepository, times(1)).findById(expenseId);
    }
}