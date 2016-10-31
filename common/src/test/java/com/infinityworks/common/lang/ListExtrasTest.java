package com.infinityworks.common.lang;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ListExtrasTest {

    @Test
    public void noneNullReturnsFalseIfSomeElementIsNull() throws Exception {
        List<Integer> data = asList(1, 4, null);

        boolean isValid = ListExtras.noneNull(data);

        assertThat(isValid, is(false));
    }

    @Test
    public void noneNullReturnsFalseIfListIsNull() throws Exception {
        assertThat(ListExtras.noneNull(null), is(false));
    }

    @Test
    public void noneNullReturnsTrueIfAllElementPresent() throws Exception {
        List<Integer> data = asList(1, 4, 2);

        boolean isValid = ListExtras.noneNull(data);

        assertThat(isValid, is(true));
    }

    @Test
    public void noneNullReturnsTrueIfEmptyList() throws Exception {
        List<Integer> data = emptyList();

        boolean isValid = ListExtras.noneNull(data);

        assertThat(isValid, is(true));
    }

    @Test
    public void isNullOrEmptyReturnsTrueIfListIsNull() throws Exception {
        assertThat(ListExtras.isNullOrEmpty(null), is(true));
    }

    @Test
    public void isNullOrEmptyReturnsTrueIfListIsEmpty() throws Exception {
        List<Integer> data = emptyList();

        boolean isValid = ListExtras.isNullOrEmpty(data);

        assertThat(isValid, is(true));
    }

    @Test
    public void isNullOrEmptyReturnsFalseIfListHasElements() throws Exception {
        List<Integer> data = asList(1, 8, 4);

        boolean isValid = ListExtras.isNullOrEmpty(data);

        assertThat(isValid, is(false));
    }

    @Test(expected = InvocationTargetException.class)
    public void shouldNotBeInstantiable() throws Exception {
        Constructor<ListExtras> constructor = ListExtras.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}