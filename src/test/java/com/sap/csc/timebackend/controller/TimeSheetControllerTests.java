package com.sap.csc.timebackend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cloud.sdk.odatav2.connectivity.ODataException;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.workforcetimesheet.TimeSheetEntry;
import com.sap.csc.timebackend.exceptions.InvalidTimeException;
import com.sap.csc.timebackend.exceptions.SAPBadRequestException;
import com.sap.csc.timebackend.exceptions.TotalTravelTimeTooLongException;
import com.sap.csc.timebackend.model.Day;
import com.sap.csc.timebackend.model.TaskType;
import com.sap.csc.timebackend.repository.TimeSheetRepository;
import com.sap.csc.timebackend.security.AuthenticationFacade;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TimeSheetControllerTests {

    private static final String ENDPOINT = "/timesheet?dateFrom=05-03-2018&dateTo=09-03-2018";
    private static final String FULL_WEEK_IN_TIME_ENTRIES = "fullWeekInTimeEntries.json";
    private static final String fifthMarch = "2018-03-05";
    private static final String ninthMarch = "2018-03-09";
    private final TestRestTemplate restTemplate = new TestRestTemplate();
    private final HttpHeaders headers = new HttpHeaders();
    private final ObjectMapper mapper = new ObjectMapper();
    @LocalServerPort
    private int port;
    @MockBean
    private TimeSheetRepository timeSheetRepository;

    @Autowired
    private TimeSheetController timeSheetController;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Before
    public void setUp() throws IOException, ODataException {
        final List<TimeSheetEntry> entries = readTimeEntriesFromFile(FULL_WEEK_IN_TIME_ENTRIES);
        when(timeSheetRepository
                .getByDateAndUser(LocalDate.parse(fifthMarch), LocalDate.parse(ninthMarch), authenticationFacade.getLoggedUser().getId()))
                .thenReturn(entries);
    }

    private List<TimeSheetEntry> readTimeEntriesFromFile(String file) throws IOException {
        final TimeSheetEntry[] timeSheetEntries = mapper
                .readValue(getFile(file), TimeSheetEntry[].class);

        final List<TimeSheetEntry> entries = asList(timeSheetEntries);

        entries.forEach(t -> {
            Objects.requireNonNull(t.getTimeSheetDataFields()).setControllingArea(t.getTimeSheetDataFields().getCustomField("ControllingArea"));
            t.getTimeSheetDataFields().setReceiverCostCenter(t.getTimeSheetDataFields().getCustomField("ReceiverCostCenter"));
            t.getTimeSheetDataFields().setTimeSheetTaskType(t.getTimeSheetDataFields().getCustomField("TimeSheetTaskType"));
            t.getTimeSheetDataFields().setTimeSheetTaskLevel(t.getTimeSheetDataFields().getCustomField("TimeSheetTaskLevel"));
            t.getTimeSheetDataFields().setTimeSheetTaskComponent(t.getTimeSheetDataFields().getCustomField("TimeSheetTaskComponent"));
            t.getTimeSheetDataFields().setPurchaseOrderItem(t.getTimeSheetDataFields().getCustomField("PurchaseOrderItem"));
            t.getTimeSheetDataFields().setRecordedHours(new BigDecimal((String) Objects.requireNonNull(t.getTimeSheetDataFields().getCustomField("RecordedHours"))));
            t.getTimeSheetDataFields().setRecordedQuantity(new BigDecimal((String) Objects.requireNonNull(t.getTimeSheetDataFields().getCustomField("RecordedQuantity"))));
            t.getTimeSheetDataFields().setHoursUnitOfMeasure(t.getTimeSheetDataFields().getCustomField("HoursUnitOfMeasure"));
        });
        return entries;
    }


    @Test
    public void getFullWeek() {
        final ResponseEntity<String> response = responseForGetEndpoint(ENDPOINT);

        assertEquals(response.getStatusCode(), HttpStatus.OK);


    }


    @Test
    public void numberOfRetrievedDaysIsCorrect() throws IOException {
        final ResponseEntity<String> response = responseForGetEndpoint(ENDPOINT);
        final JsonNode jsonResponseTree = mapper.readTree(response.getBody());

        assertEquals(5, jsonResponseTree.size());
    }

    @Test
    public void everyDayHasCorrectDate() throws IOException {
        final ResponseEntity<String> response = responseForGetEndpoint(ENDPOINT);

        final JsonNode jsonResponseTree = mapper.readTree(response.getBody());
        LocalDate date = LocalDate.parse("2018-03-05");
        for (JsonNode jsonNode : jsonResponseTree) {
            assertEquals(LocalDate.parse(jsonNode.get("date").textValue()), date);
            date = date.plusDays(1);
        }
    }

    @Test
    public void validateTasks() throws IOException {
        final ResponseEntity<String> response = responseForGetEndpoint(ENDPOINT);

        final JsonNode jsonResponseTree = mapper.readTree(response.getBody());

        jsonResponseTree.forEach(day -> {
            assertTrue(!day.get("taskTime").get("first").get("taskType").get("abbr").asText().equals(TaskType.DFLT.getAbbr()));
            assertTrue(!day.get("taskTime").get("second").get("taskType").get("abbr").asText().equals(TaskType.DFLT.getAbbr()));
        });
    }

    @Test
    public void validateBreak() throws IOException {
        final ResponseEntity<String> response = responseForGetEndpoint(ENDPOINT);

        final JsonNode jsonResponseTree = mapper.readTree(response.getBody());
        jsonResponseTree.forEach(day -> assertTrue(day.get("breakTime").get("taskType").get("abbr").asText().equals(TaskType.MISC.getAbbr())));
    }

    @Test
    public void validateTravel() throws IOException {
        final ResponseEntity<String> response = responseForGetEndpoint(ENDPOINT);

        final JsonNode jsonResponseTree = mapper.readTree(response.getBody());

        jsonResponseTree.forEach(day -> {
            assertTrue(day.get("travelTime").get("first").get("taskType").get("abbr").asText().equals(TaskType.TRAV.getAbbr()));
            assertTrue(day.get("travelTime").get("second").get("taskType").get("abbr").asText().equals(TaskType.TRAV.getAbbr()));
        });

    }

    @Test
    public void validateTotalTravelDuration() throws IOException {
        final ResponseEntity<String> response = responseForGetEndpoint(ENDPOINT);

        final JsonNode jsonResponseTree = mapper.readTree(response.getBody());

        jsonResponseTree.forEach(day -> assertEquals(day.get("travelTime").get("first").get("duration").asInt() +
                        day.get("travelTime").get("second").get("duration").asInt(),
                day.get("totalTravelTime").asInt()));
    }

    @Test
    public void dateToBeforeDateFromResultsBadRequest() {
        final ResponseEntity<String> response = responseForGetEndpoint("/timesheet" +
                "?dateFrom=05-03-2018&dateTo=01-03-2018");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());


    }

    @Test(expected = NullPointerException.class)
    public void createOrUpdateNullValueParameter() {
        timeSheetController.createOrUpdate(null);
    }

    @Test(expected = NullPointerException.class)
    public void createOrUpdateArrayOfNull() {
        final List<Day> days = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            days.add(null);
        }
        timeSheetController.createOrUpdate(days);
    }

    @Test(expected = SAPBadRequestException.class)
    @SuppressWarnings("unchecked")
    public void missingTaskTypeShouldThrowException() throws IOException {

        final Day dayWithoutTaskType = mapper
                .readValue(getFile("dayWithMissingTaskType.json"), Day.class);
        timeSheetController.createOrUpdate(Collections.singletonList(dayWithoutTaskType));
    }

    @Test(expected = InvalidTimeException.class)
    public void lowerStartTimeThanExpectedThrowsInvalidTimeException() throws IOException {

        final Day dayWithInvalidFirstTaskStartTime = mapper
                .readValue(getFile("dayWithInvalidTaskStartTime.json"), Day.class);

        timeSheetController.createOrUpdate(Collections.singletonList(dayWithInvalidFirstTaskStartTime));
    }

    @Test(expected = InvalidTimeException.class)
    public void biggerEndTimeThanExpectedThrowsInvalidTimeException() throws IOException {
        final Day dayWithInvalidSecondTaskEndTime = mapper
                .readValue(getFile("dayWithInvalidTaskEndTime.json"), Day.class);

        timeSheetController.createOrUpdate(Collections.singletonList(dayWithInvalidSecondTaskEndTime));
    }

    @Test(expected = TotalTravelTimeTooLongException.class)
    public void totalTravelTimeBiggerThanFirstTaskStartThrowsException() throws IOException {
        final Day dayWithBigTotalTravelTime = mapper
                .readValue(getFile("dayWithTotalTravelTimeExceedingFirstTaskStartTime.json"), Day.class);
        timeSheetController.createOrUpdate(Collections.singletonList(dayWithBigTotalTravelTime));

    }


    @Test(expected = TotalTravelTimeTooLongException.class)
    public void totalTravelTimeBiggerThanSecondTaskEndThrowsException() throws IOException {
        final Day dayWithBigTotalTravelTime = mapper
                .readValue(getFile("dayWithTotalTravelTimeExceedingSecondTaskEndTime.json"), Day.class);
        timeSheetController.createOrUpdate(Collections.singletonList(dayWithBigTotalTravelTime));

    }


    private ResponseEntity<String> responseForGetEndpoint(String url) {
        final HttpEntity<String> entity = new HttpEntity<>(null, headers);

        return restTemplate
                .exchange(createURLWithPort(url),
                        HttpMethod.GET, entity,
                        String.class);
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    private File getFile(String fileName) {
        return new File(Objects.requireNonNull(getClass().getClassLoader().getResource(fileName)).getFile());
    }

}
