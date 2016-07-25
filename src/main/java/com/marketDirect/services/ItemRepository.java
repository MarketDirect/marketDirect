package com.marketDirect.services;

import com.marketDirect.entities.Item;
import com.marketDirect.entities.Vendor;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by michaeldelli-gatti on 7/19/16.
 */
public interface ItemRepository extends CrudRepository<Item, Integer> {
    Iterable<Item> findByVendor(Vendor vendor);
    Iterable<Item> findByNameLike(String search);
    Iterable<Item> findByCategory(String category);
    Item findByName(String name);
}
