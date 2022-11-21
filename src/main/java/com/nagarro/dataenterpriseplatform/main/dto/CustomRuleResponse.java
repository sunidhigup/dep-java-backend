package com.nagarro.dataenterpriseplatform.main.dto;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomRuleResponse {
	private String id;

	private String client_id;

	private String batch_id;

	private String rulename;

	private String argvalue;

	private String argkey;

	private String type;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomRuleResponse other = (CustomRuleResponse) obj;
		return Objects.equals(rulename, other.rulename);
	}

	@Override
	public int hashCode() {
		return Objects.hash(rulename);
	}

}
