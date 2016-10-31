package com.infinityworks.common.lang;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class StringExtrasTest {
    @Test
    public void returnsFalseIfNull() throws Exception {
        assertThat(StringExtras.isNullOrEmpty(null), is(true));
    }

    @Test
    public void returnsFalseIfEmpty() throws Exception {
        assertThat(StringExtras.isNullOrEmpty(""), is(true));
    }

    @Test
    public void returnsTrueIfNonNullAndPresent() throws Exception {
        assertThat(StringExtras.isNullOrEmpty("sdfsdf"), is(false));
    }

    @Test(expected = InvocationTargetException.class)
    public void shouldNotBeInstantiable() throws Exception {
        Constructor<StringExtras> constructor = StringExtras.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
