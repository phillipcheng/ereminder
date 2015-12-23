package org.cld.util.test;

import org.cld.util.reload.ClassReloadFactory;
import org.junit.Test;

public class TestReload {
	
	@Test
	public void testReload(){
		ClassReloadFactory crf;
		crf = new ClassReloadFactory(new String[]{}, new String[]{"target/cld-util-1.0.0.jar"});
		crf.reload();
	}
}
