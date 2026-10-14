package com.loopin.controller;

import com.loopin.model.ItemRequest;
import com.loopin.service.RequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit Test for RequestController testing Presentation Layer endpoints
 * using JUnit 5 and Mockito.
 */
class RequestControllerTest {

    private RequestService requestService;
    private RequestController requestController;

    @BeforeEach
    void setUp() {
        requestService = Mockito.mock(RequestService.class);
        requestController = new RequestController(requestService);
    }

    @Test
    @DisplayName("GET /api/requests returns 200 OK and list of requests")
    void shouldReturnAllRequests() {
        ItemRequest req1 = new ItemRequest(1L, "TI-84 Graphing Calculator", "Academics",
                "Alex Morgan", "alex.m@campus.edu", LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5), 1, "OPEN", "Exam prep");

        when(requestService.getAllRequests()).thenReturn(List.of(req1));

        ResponseEntity<List<ItemRequest>> response = requestController.getAllRequests();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getItemName()).isEqualTo("TI-84 Graphing Calculator");
    }

    @Test
    @DisplayName("POST /api/requests creates request and returns 201 CREATED status")
    void shouldCreateRequestAndReturnCreated() {
        ItemRequest input = new ItemRequest(null, "White Lab Coat", "Academics",
                "Priya Sharma", "priya.s@campus.ac.in", LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(4), 2, "OPEN", "Chemistry lab");

        ItemRequest saved = new ItemRequest(2L, "White Lab Coat", "Academics",
                "Priya Sharma", "priya.s@campus.ac.in", LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(4), 2, "OPEN", "Chemistry lab");

        when(requestService.createRequest(any(ItemRequest.class))).thenReturn(saved);

        ResponseEntity<ItemRequest> response = requestController.createRequest(input);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(2L);
        assertThat(response.getBody().getItemName()).isEqualTo("White Lab Coat");
        assertThat(response.getBody().getStatus()).isEqualTo("OPEN");
    }

    @Test
    @DisplayName("PUT /api/requests/{id}/status updates status and returns 200 OK")
    void shouldUpdateStatusAndReturnOk() {
        ItemRequest updated = new ItemRequest(1L, "TI-84 Graphing Calculator", "Academics",
                "Alex Morgan", "alex.m@campus.edu", LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5), 1, "FULFILLED", "Exam prep");

        when(requestService.updateRequestStatus(1L, "FULFILLED")).thenReturn(updated);

        ResponseEntity<ItemRequest> response = requestController.updateStatus(1L, "FULFILLED");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("FULFILLED");
    }
}
