package com.surest.api.dto;

import java.util.List;

import com.surest.api.model.Member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberPaginatedResponse {

	private List<Member> data;
    private long totalElements;
    private int totalPages;
    private int currentPage;
}
