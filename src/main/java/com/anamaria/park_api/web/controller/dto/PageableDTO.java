package com.anamaria.park_api.web.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class PageableDTO { //essa classe pode ser usada com a listagem de qualquer objeto se estiver usando paginação

    private List content = new ArrayList<>();
    private boolean first;
    private boolean last;

    @JsonProperty("pages")
    private int number;
    private int size;

    @JsonProperty("pagesElements")
    private int numberOfElements;
    private int totalPages;
    private int totalElements;
}
