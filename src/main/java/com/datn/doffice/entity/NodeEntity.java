package com.datn.doffice.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NodeEntity {
	
	private Object object;
	
	private String label;
	
	private List<Object> children;
}
