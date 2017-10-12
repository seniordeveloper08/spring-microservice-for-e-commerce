package com.inventory.service;

import com.inventory.domain.Category;
import com.inventory.domain.Item;
import com.inventory.domain.ReduceInfo;
import com.inventory.exception.InventoryLackingException;
import com.inventory.repository.InventoryMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class InventoryServiceTest {

    @InjectMocks
    InventoryService sut;

    @Mock
    InventoryMapper inventoryMapper;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        when(inventoryMapper.selectItemById(1)).thenReturn(new Item(1, "Hoge", 10000, 10, "desc", null, new Category(1, "Bar")));
        when(inventoryMapper.selectItemById(null)).thenReturn(null);
        when(inventoryMapper.selectItemsByCategoryId(1)).thenReturn(Arrays.asList(new Item(), new Item()));
        when(inventoryMapper.selectItemsByCategoryId(null)).thenReturn(Arrays.asList(new Item(), new Item(), new Item(), new Item()));
    }

    @Test
    public void testItems() throws Exception {
        assertThat(sut.items().size(), is(4));
    }

    @Test
    public void testItems_validValue() throws Exception {
        assertThat(sut.items(1).size(), is(2));
    }

    @Test
    public void testItems_invalidValue() throws Exception {
        assertThat(sut.items(Integer.MAX_VALUE).size(), is(0));
    }

    @Test
    public void testItem_validValue() throws Exception {
        final Item item = sut.item(1);
        assertThat(item.getId(), is(1));
        assertThat(item.getName(), is("Hoge"));
        assertThat(item.getCategory(), samePropertyValuesAs(new Category(1, "Bar")));

        assertThat(sut.item(2), is(nullValue()));
    }

    @Test
    public void testItem_invalidValue() throws Exception {
        assertThat(sut.item(Integer.MAX_VALUE), is(nullValue()));
    }

    @Test
    public void testReduce_validValue() throws Exception {
        final Item item = sut.item(1);
        assertThat(item.getUnit(), is(10));
        sut.reduce(new ReduceInfo(1, 5));
    }

    @Test
    public void testReduce_invalidValue() throws Exception {
        expectedException.expect(InventoryLackingException.class);
        final Item item = sut.item(1);
        assertThat(item.getUnit(), is(10));
        sut.reduce(new ReduceInfo(1, 30));
    }

    @Test
    public void testReduce_invalidBoundaryValue() throws Exception {
        final Item item = sut.item(1);
        assertThat(item.getUnit(), is(10));
        sut.reduce(new ReduceInfo(1, 10));
    }
}
