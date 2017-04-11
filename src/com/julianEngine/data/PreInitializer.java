package com.julianEngine.data;

import java.io.Serializable;

public interface PreInitializer extends Serializable{
	public String getName();
	public void preInit();
}
