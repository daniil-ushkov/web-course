package ru.itmo.wp.model.repository;

import ru.itmo.wp.model.domain.Talk;

import java.util.List;
import java.util.Map;

public interface TalkRepository {
    void save(Talk talk);
    List<Talk> findAll();
    List<Talk> findBy(Map<String, Object> map);
}
