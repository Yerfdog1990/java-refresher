package com.baeldung.lmock.service.defaultimpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.baeldung.lmock.domain.model.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.baeldung.lmock.service.TaskService;
import com.baeldung.lmock.service.reports.ReportBuilder;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class DefaultReportsServiceUnitTest {

    @Mock
    private TaskService taskService;

    @Mock
    private ReportBuilder<String> reportBuilder;

    @InjectMocks
    private DefaultReportsService defaultReportsService;

    @Test
    void givenDefaultReportsService_whenGenerateManagerReportWithoutStubbingVoid_thenReturnReports() {
        // given
        ReportBuilder<String> reportBuilder = Mockito.mock(ReportBuilder.class);

        // when
        String generatedManagerReport = defaultReportsService.generateManagerReport(reportBuilder);

        // then
        assertNull(generatedManagerReport);
    }

    /*
    3.2. Stubbing Void Methods
    As we’ve seen, we don’t necessarily need to stub void methods. If a stub isn’t defined, Mockito will simply skip the method body, and the test continues without issues.
    However, there are still valid reasons to stub a void method:

    It makes it clearer in the test that the code under test is expected to go through that method call
    With Mockito, we can work with “partial mocks” (a.k.a “spies”, which will be covered in a future lesson), and we may need to explicitly stub void methods to avoid invoking their real behavior
    If the method is later changed to return a value, the test won’t compile until we adapt the stubbing
    If we remove this method from the implementation and use strict stubs, the test fails with UnnecessaryStubbingException
    Let’s now open the DefaultReportsServiceUnitTest and add a test for the generateManagerReport method.

    In short, this method takes a ReportBuilder as input and builds a report that only includes data relevant to managers.
    Internally, it calls several builder methods, each returning void, to selectively include different report sections.
    These calls don’t return data; they mutate the internal state of the builder to produce the final report:
     */
    @Test
    void givenDefaultReportsService_whenGenerateManagerReportWithStubbingVoid_thenReturnReports() {
        // given
        List<Task> relevantTasks = new ArrayList<>();
        when(taskService.searchTasks(null, null)).thenReturn(relevantTasks);
        //ReportBuilder<String> reportBuilder = Mockito.mock(ReportBuilder.class);
        doNothing().when(reportBuilder).addTasksData(relevantTasks);
        doNothing().when(reportBuilder).addCampaignsData(relevantTasks);
        doNothing().when(reportBuilder).addWorkersData(relevantTasks);
        when(reportBuilder.obtainReport()).thenReturn("report results");

        // when
        String generatedManagerReport = defaultReportsService.generateManagerReport(reportBuilder);

        // then
        assertEquals("report results", generatedManagerReport);
    }
}
