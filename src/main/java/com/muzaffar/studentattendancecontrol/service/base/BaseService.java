package com.muzaffar.studentattendancecontrol.service.base;

import java.util.List;

public interface BaseService<T, R> {
    Integer add(T t);
    List<R> getList();
    List<R> getList(Integer id);
    R get(Integer id);
    void delete(Integer id);
    R update(Integer id, T t);
}
