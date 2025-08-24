package com.anamaria.park_api.web.controller.dto.mapper;

import com.anamaria.park_api.web.controller.dto.PageableDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageableMapper {

    public static PageableDTO toPageableDTO(Page page){
        return new ModelMapper().map(page, PageableDTO.class);
    }
}
