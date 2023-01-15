package hexlet.code.service;

import hexlet.code.dto.StatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.Status;
import hexlet.code.model.User;

public interface StatusService {

    Status createNewStatus(StatusDto statusDto);

    Status updateStatus(long id, StatusDto dto);
}
