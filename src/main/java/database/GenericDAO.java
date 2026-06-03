package database;

import java.util.List;

public interface GenericDAO<T> {
    void save(T entity);
    T findById(String id);
    List<T> findAll();
    void update(T entity);
    void delete(String id);
}
