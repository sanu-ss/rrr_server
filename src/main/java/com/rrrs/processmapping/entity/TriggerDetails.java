
package com.rrrs.processmapping.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TriggerDetails {

	private ProcessTrigger processTrigger;
	private String message;
	
}
