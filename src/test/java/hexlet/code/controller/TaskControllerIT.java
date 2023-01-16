package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.StatusDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Status;
import hexlet.code.model.Task;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.StatusRepository;
import hexlet.code.repository.TaskRepository;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static hexlet.code.controller.StatusController.STATUS_CONTROLLER_PATH;
import static hexlet.code.controller.TaskController.ID;
import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static hexlet.code.utils.TestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class TaskControllerIT {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private TestUtils utils;

    @AfterEach
    public void clear() {
        utils.tearDown();
    }


    @Test
    public void testCreateTask() throws Exception {
        utils.regDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);

        final var statusDto = new StatusDto("new");

        final var statusPostRequest = post(STATUS_CONTROLLER_PATH)
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON);
        final Status status = fromJson((utils.perform(statusPostRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse())
                .getContentAsString(), new TypeReference<>(){});


        final var labelDto = new LabelDto("bug");

        final var labelPostRequest = post(LABEL_CONTROLLER_PATH)
                .content(asJson(labelDto))
                .contentType(APPLICATION_JSON);
        final Label label = fromJson((utils.perform(labelPostRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse())
                .getContentAsString(), new TypeReference<>(){});
        Set<Long> labels = new HashSet<Long>();
        labels.add(label.getId());


        final var taskDto = new TaskDto("task", "description", status.getId(), expectedUser.getId(), labels);

        final var taskPostRequest = post(TASK_CONTROLLER_PATH)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON);
        final var response = utils.perform(taskPostRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final Task task = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertTrue(taskRepository.existsById(status.getId()));
        assertEquals(task.getName(), taskDto.getName());
        assertEquals(task.getDescription(), taskDto.getDescription());
        assertEquals(task.getExecutor().getId(), taskDto.getExecutorId());
        assertEquals(task.getTaskStatus().getId(), taskDto.getTaskStatusId());
        assertEquals(task.getLabels().stream().map(Label::getId).collect(Collectors.toSet()), taskDto.getLabelIds());
    }


    @Test
    public void testUpdateTask() throws Exception {
        utils.regDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);

        final var statusDto = new StatusDto("new");

        final var statusPostRequest = post(STATUS_CONTROLLER_PATH)
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON);
        utils.perform(statusPostRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
        final Status status = fromJson((utils.perform(statusPostRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse())
                .getContentAsString(), new TypeReference<>(){});


        final var labelDto = new LabelDto("bug");

        final var labelPostRequest = post(LABEL_CONTROLLER_PATH)
                .content(asJson(labelDto))
                .contentType(APPLICATION_JSON);
        final Label label = fromJson((utils.perform(labelPostRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse())
                .getContentAsString(), new TypeReference<>(){});
        Set<Long> labels = new HashSet<Long>();
        labels.add(label.getId());


        final var taskDto = new TaskDto("task", "description", status.getId(), expectedUser.getId(), labels);

        final var taskPostRequest = post(TASK_CONTROLLER_PATH)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON);
        final var postTaskResponse = utils.perform(taskPostRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
        final Task postedTask = fromJson(postTaskResponse.getContentAsString(), new TypeReference<>() {
        });


        final var newTaskDto = new TaskDto("task1", "description2", status.getId(), expectedUser.getId(), labels);

        final var putRequest = put(TASK_CONTROLLER_PATH + ID, postedTask.getId())
                .content(asJson(newTaskDto))
                .contentType(APPLICATION_JSON);
        final var response = utils.perform(putRequest, TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final Task task = fromJson(response.getContentAsString(), new TypeReference<>() {
        });


        assertEquals(task.getName(), newTaskDto.getName());
        assertEquals(task.getDescription(), newTaskDto.getDescription());
        assertNotEquals(task.getName(), taskDto.getName());
        assertNotEquals(task.getDescription(), taskDto.getDescription());
    }


    @Test
    public void testGetAllTasks() throws Exception {
        utils.regDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);

        final var statusDto = new StatusDto("new");

        final var statusPostRequest = post(STATUS_CONTROLLER_PATH)
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON);
        final Status status = fromJson((utils.perform(statusPostRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse())
                .getContentAsString(), new TypeReference<>(){});


        final var labelDto = new LabelDto("bug");

        final var labelPostRequest = post(LABEL_CONTROLLER_PATH)
                .content(asJson(labelDto))
                .contentType(APPLICATION_JSON);
        final Label label = fromJson((utils.perform(labelPostRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse())
                .getContentAsString(), new TypeReference<>(){});
        Set<Long> labels = new HashSet<Long>();
        labels.add(label.getId());


        final var taskDto = new TaskDto("task", "description", status.getId(), expectedUser.getId(), labels);

        final var taskPostRequest = post(TASK_CONTROLLER_PATH)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON);
        utils.perform(taskPostRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();


        final var response = utils.perform(get(TASK_CONTROLLER_PATH), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(tasks.size()).isEqualTo(1);
    }



    @Test
    public void testDeleteTask() throws Exception {

        utils.regDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);

        final var statusDto = new StatusDto("new");

        final var statusPostRequest = post(STATUS_CONTROLLER_PATH)
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON);
        final Status status = fromJson((utils.perform(statusPostRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse())
                .getContentAsString(), new TypeReference<>(){});


        final var labelDto = new LabelDto("bug");

        final var labelPostRequest = post(LABEL_CONTROLLER_PATH)
                .content(asJson(labelDto))
                .contentType(APPLICATION_JSON);
        final Label label = fromJson((utils.perform(labelPostRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse())
                .getContentAsString(), new TypeReference<>(){});
        Set<Long> labels = new HashSet<Long>();
        labels.add(label.getId());


        final var taskDto = new TaskDto("task", "description", status.getId(), expectedUser.getId(), labels);

        final var taskPostRequest = post(TASK_CONTROLLER_PATH)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON);
        final var postTaskResponse = utils.perform(taskPostRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
        final Task postedTask = fromJson(postTaskResponse.getContentAsString(), new TypeReference<>() {
        });


        final var deleteRequest = delete(TASK_CONTROLLER_PATH + ID, postedTask.getId());
        utils.perform(deleteRequest, TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();


        assertEquals(0, taskRepository.count());

    }



    @Test
    public void testDeleteLabelWithTask() throws Exception {
        utils.regDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);

        final var statusDto = new StatusDto("new");

        final var statusPostRequest = post(STATUS_CONTROLLER_PATH)
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON);
        final Status status = fromJson((utils.perform(statusPostRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse())
                .getContentAsString(), new TypeReference<>(){});


        final var labelDto = new LabelDto("bug");

        final var labelPostRequest = post(LABEL_CONTROLLER_PATH)
                .content(asJson(labelDto))
                .contentType(APPLICATION_JSON);
        final Label label = fromJson((utils.perform(labelPostRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse())
                .getContentAsString(), new TypeReference<>(){});
        Set<Long> labels = new HashSet<Long>();
        labels.add(label.getId());


        final var taskDto = new TaskDto("task", "description", status.getId(), expectedUser.getId(), labels);

        final var taskPostRequest = post(TASK_CONTROLLER_PATH)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON);
        utils.perform(taskPostRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();


        utils.perform(delete(LABEL_CONTROLLER_PATH + LabelController.ID,
                                labelRepository.findAll().get(0).getId()),
                        TEST_USERNAME)
                .andExpect(status().is(422));

        assertEquals(1, labelRepository.count());


    }


    @Test
    public void testDeleteStatusWithTask() throws Exception {
        statusRepository.deleteAll();
        utils.regDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);

        final var statusDto = new StatusDto("new");

        final var statusPostRequest = post(STATUS_CONTROLLER_PATH)
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON);
        final Status status = fromJson((utils.perform(statusPostRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse())
                .getContentAsString(), new TypeReference<>(){});


        final var labelDto = new LabelDto("bug");

        final var labelPostRequest = post(LABEL_CONTROLLER_PATH)
                .content(asJson(labelDto))
                .contentType(APPLICATION_JSON);
        final Label label = fromJson((utils.perform(labelPostRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse())
                .getContentAsString(), new TypeReference<>(){});
        Set<Long> labels = new HashSet<Long>();
        labels.add(label.getId());


        final var taskDto = new TaskDto("task", "description", status.getId(), expectedUser.getId(), labels);

        final var taskPostRequest = post(TASK_CONTROLLER_PATH)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON);
        utils.perform(taskPostRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();


        utils.perform(delete(STATUS_CONTROLLER_PATH + StatusController.ID,
                                statusRepository.findAll().get(0).getId()),
                        TEST_USERNAME)
                .andExpect(status().is(422));
        assertEquals(1, statusRepository.count());

    }

    @Test
    public void testDeleteTaskExecutor() throws Exception {
        statusRepository.deleteAll();
        utils.regDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);

        final var statusDto = new StatusDto("new");

        final var statusPostRequest = post(STATUS_CONTROLLER_PATH)
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON);
        final Status status = fromJson((utils.perform(statusPostRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse())
                .getContentAsString(), new TypeReference<>(){});


        final var labelDto = new LabelDto("bug");

        final var labelPostRequest = post(LABEL_CONTROLLER_PATH)
                .content(asJson(labelDto))
                .contentType(APPLICATION_JSON);
        final Label label = fromJson((utils.perform(labelPostRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse())
                .getContentAsString(), new TypeReference<>(){});
        Set<Long> labels = new HashSet<Long>();
        labels.add(label.getId());


        final var taskDto = new TaskDto("task", "description", status.getId(), expectedUser.getId(), labels);

        final var taskPostRequest = post(TASK_CONTROLLER_PATH)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON);
        utils.perform(taskPostRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();


        utils.perform(delete(USER_CONTROLLER_PATH + StatusController.ID,
                                userRepository.findAll().get(0).getId()),
                        TEST_USERNAME)
                .andExpect(status().is(422));
        assertEquals(1, userRepository.count());

    }


    @Test
    public void testAllTasksFiltration() throws Exception {
        utils.regDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);

        final var statusDto = new StatusDto("new");

        final var statusPostRequest = post(STATUS_CONTROLLER_PATH)
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON);
        final Status status = fromJson((utils.perform(statusPostRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse())
                .getContentAsString(), new TypeReference<>(){});


        final var labelDto = new LabelDto("bug");

        final var labelPostRequest = post(LABEL_CONTROLLER_PATH)
                .content(asJson(labelDto))
                .contentType(APPLICATION_JSON);
        final Label label = fromJson((utils.perform(labelPostRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse())
                .getContentAsString(), new TypeReference<>(){});
        Set<Long> labels = new HashSet<Long>();
        labels.add(label.getId());


        final var firstTaskDto = new TaskDto("task", "description", status.getId(), expectedUser.getId(), labels);

        final var taskPostRequest = post(TASK_CONTROLLER_PATH)
                .content(asJson(firstTaskDto))
                .contentType(APPLICATION_JSON);
        utils.perform(taskPostRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final var secondTaskDto = new TaskDto("task", "description", status.getId(), expectedUser.getId(), new HashSet<>());

        final var secondTaskPostRequest = post(TASK_CONTROLLER_PATH)
                .content(asJson(secondTaskDto))
                .contentType(APPLICATION_JSON);
        utils.perform(secondTaskPostRequest, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();



        final var response = utils.perform(get(TASK_CONTROLLER_PATH
                        + "?labels="
                        + labelRepository.findAll().get(0).getId()), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(2, taskRepository.count());
        assertThat(tasks.size()).isEqualTo(1);
    }

}
