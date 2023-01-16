package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.StatusDto;
import hexlet.code.model.Status;
import hexlet.code.repository.StatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.controller.StatusController.ID;
import static hexlet.code.controller.StatusController.STATUS_CONTROLLER_PATH;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class StatusControllerIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private TestUtils utils;

    @AfterEach
    public void clear() {
        utils.tearDown();
    }


    @Test
    public void testCreateStatus() throws Exception {
        utils.regDefaultUser();

        final var statusDto = new StatusDto("new");

        final var postRequest = post(STATUS_CONTROLLER_PATH)
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON);

        final var response = utils.perform(postRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final Status expectedStatus = statusRepository.findAll().get(0);

        final Status status = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertTrue(statusRepository.existsById(status.getId()));
        assertEquals(expectedStatus.getId(), status.getId());
        assertEquals(expectedStatus.getName(), status.getName());
    }


    @Test
    public void testGetStatusById() throws Exception {
        utils.regDefaultUser();

        final var statusDto = new StatusDto("new");

        final var postRequest = post(STATUS_CONTROLLER_PATH)
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON);

        utils.perform(postRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();


        final var response = utils.perform(get(STATUS_CONTROLLER_PATH + ID,
                                statusRepository.findAll().get(0).getId()),
                        TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final Status status = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(statusDto.getName(), status.getName());
    }


    @Test
    public void testCreatedStatusFails() throws Exception {
        utils.regDefaultUser();

        final var statusDto = new StatusDto("");

        final var postRequest = post(STATUS_CONTROLLER_PATH)
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON);

        utils.perform(postRequest, TEST_USERNAME)
                .andExpect(status().is(422));

        assertEquals(0, statusRepository.count());
    }


    @Test
    public void testUpdateStatus() throws Exception {
        utils.regDefaultUser();

        final var postRequest = post(STATUS_CONTROLLER_PATH)
                .content(asJson(new StatusDto("new")))
                .contentType(APPLICATION_JSON);
        utils.perform(postRequest, TEST_USERNAME);
        Status createdStatus = statusRepository.findAll().get(0);

        final var statusDto = new StatusDto("verified");

        final var putRequest = put(STATUS_CONTROLLER_PATH + ID, createdStatus.getId())
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON);
        final var response = utils.perform(putRequest, TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        createdStatus = statusRepository.findAll().get(0);

        final Status status = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(createdStatus.getId(), status.getId());
        assertEquals(createdStatus.getName(), statusDto.getName());
    }

    @Test
    public void getAllStatuses() throws Exception {
        utils.regDefaultUser();

        final var statusDto = new StatusDto("verified");

        final var postRequest = post(STATUS_CONTROLLER_PATH)
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON);
        utils.perform(postRequest, TEST_USERNAME)
                .andExpect(status().isCreated());

        final var response = utils.perform(get(STATUS_CONTROLLER_PATH), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Status> statuses = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(statuses.size()).isEqualTo(1);
    }


    @Test
    public void deleteStatus() throws Exception {
        utils.regDefaultUser();

        final var statusDto = new StatusDto("verified");

        final var postRequest = post(STATUS_CONTROLLER_PATH)
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON);
        utils.perform(postRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        utils.perform(delete(STATUS_CONTROLLER_PATH + ID,
                statusRepository.findAll().get(0).getId()),
                TEST_USERNAME)
                .andExpect(status().isOk());

        assertEquals(0, statusRepository.count());
    }

}
