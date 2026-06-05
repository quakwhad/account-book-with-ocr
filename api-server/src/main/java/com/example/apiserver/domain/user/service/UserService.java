package com.example.apiserver.domain.user.service;

import com.example.apiserver.domain.user.dto.UserRequestDto;
import com.example.apiserver.domain.user.dto.UserResponseDto;
import com.example.apiserver.domain.user.entity.User;
import com.example.apiserver.domain.user.repository.UserRepository;
import com.example.apiserver.global.exception.CustomException;
import com.example.apiserver.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponseDto createUser(UserRequestDto dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new CustomException(ErrorCode.USER_DUPLICATED);
        }
        User user = User.builder()
                .name(dto.name())
                .email(dto.email())
                .build();
        return UserResponseDto.from(userRepository.save(user));
    }

    public UserResponseDto getUser(Long id) {
        return UserResponseDto.from(findById(id));
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponseDto::from)
                .toList();
    }

    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto dto) {
        User user = findById(id);
        user.update(dto.name(), dto.email());
        return UserResponseDto.from(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.delete(findById(id));
    }

    private User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
