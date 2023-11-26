package com.showroom.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Response {

	private boolean isSuccess = false;

	private List<String> errMssg = new ArrayList<>();

	private Object responseData;


}
