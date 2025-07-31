package com.mockAi.MOCAI.mapper;

import com.mockAi.MOCAI.Dtos.Request.RegisterRequestDto;
import com.mockAi.MOCAI.Entites.AppUser;

public class AuthMapper {

    public static AppUser toEntity(RegisterRequestDto dto) {
        return new AppUser(dto.getEmail(), dto.getPassword(), dto.getPassword());
    }

}
