package com.bytes.fightr.common.model.data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DataTest {

	private DataInterface<DataImpl> d1;
	
	@Before
	public void setup() {
		d1 = new DataInterface<>();
		DataImpl data = new DataImpl();
		data.setData("Hello");
		data.setName("World");
		d1.data = data;
	}
	
	@Test
	public void toJsonValueTest() {
		Gson gson = new Gson();
		
		String json = gson.toJson(d1);
		Assert.assertNotNull(json);
	}

	@Test
	public void fromJsonValueTest() {
		
		Gson gson = new Gson();
		String json = gson.toJson(d1);

		DataInterface<DataImpl> d2 = gson.fromJson(json, new TypeToken<DataInterface<DataImpl>>() {}.getType());
		
		Assert.assertEquals(d2.data.getData(), d1.data.getData());
		
	}

}
