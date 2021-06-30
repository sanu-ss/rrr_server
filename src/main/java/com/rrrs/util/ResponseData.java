package com.rrrs.util;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
public class ResponseData implements Serializable{
	private String responseMessage;
}
