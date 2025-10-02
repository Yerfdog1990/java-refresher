package com.baeldung.lju.mockito;

import com.baeldung.lju.persistence.repository.CampaignRepository;
import com.baeldung.lju.service.impl.DefaultCampaignService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class CampaignServiceMockLifecycleUnitTest {
    // @Mock and @InjectMocks
    /*
    Mockito provides a set of useful annotations that allow us to further simplify the code.
    For example, we can annotate the mocked dependency with @Mock and the tested component with @InjectMocks:

    @Mock
    private CampaignRepository repository;

    @InjectMocks
    private DefaultCampaignService service;

    Now, we can give Mockito full control over the mock lifecycle by enabling these annotations.
    A simple way to do it would be to call MockitoAnnotations.openMocks() from a non-static context:
 */

    @Mock
    CampaignRepository mockedRepository;

    @InjectMocks
    DefaultCampaignService service;

    {
        // MockitoAnnotations.openMocks(this);
    }
    @Test
    void whenClosingACampaignWhichIsNotFound_thenReturnEmpty() {
        Mockito.when(mockedRepository.findById(1L)).thenReturn(Optional.empty());

        assertTrue(service.closeCampaign(1L).isEmpty());
    }


    // Mockito’s JUnit Extension
    /*
    Mockito has a dedicated JUnit 5 extension that allows us to enable these annotations more elegantly without manually calling MockitoAnnotations.openMocks().
    To use this extension, we need to add the mockito-junit-jupiter dependency to our pom.xml:
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-junit-jupiter</artifactId>
            <version>5.11.0</version>
            <scope>test</scope>
        </dependency>

    After that, we can enable the annotations by adding @ExtendWith(MockitoExtension.class) on top of our test class:
    MockitoAnnotations.openMocks() is no longer needed and can be removed/commented out.
    If we re-run the tests, everything will work as expected.
     */


    // Common Mocking Library Features

}
