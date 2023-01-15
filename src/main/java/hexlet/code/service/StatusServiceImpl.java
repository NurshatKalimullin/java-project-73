package hexlet.code.service;

import hexlet.code.dto.StatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.Status;
import hexlet.code.model.User;
import hexlet.code.repository.StatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@AllArgsConstructor
public class StatusServiceImpl implements StatusService {

    private final StatusRepository statusRepository;


    @Override
    public Status createNewStatus(StatusDto statusDto) {
        final Status status = new Status();
        status.setName(statusDto.getName());
        return statusRepository.save(status);
    }

    @Override
    public Status updateStatus(long id, StatusDto statusDto) {
        final Status statusToUpdate = statusRepository.findById(id).get();
        statusToUpdate.setName(statusDto.getName());
        return statusRepository.save(statusToUpdate);
    }

}
