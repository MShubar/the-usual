package com.theusual;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import com.theusual.services.AzureStorageService;

@SpringBootTest
@TestPropertySource(properties = {
    "azure.storage.account-name=test-account",
    "azure.storage.account-key=test-key",
    "azure.storage.container-name=test-container"
})
class TheusualApplicationTests {

    @MockBean
    private AzureStorageService azureStorageService;

    @Test
    void contextLoads() {
    }
}
