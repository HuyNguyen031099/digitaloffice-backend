package com.datn.doffice.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchRequestDTO {
	
	private String beginDate;
	
	private String endDate;
	
	private String name;
	
	private List<String> keywords;
}
