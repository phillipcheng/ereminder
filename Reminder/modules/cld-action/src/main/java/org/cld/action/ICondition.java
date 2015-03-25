package org.cld.action;

import java.util.Map;

public interface ICondition {
	public boolean eval(Map<String, Object> params);
}
