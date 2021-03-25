package ru.itmo.wp.model.service;

import ru.itmo.wp.model.domain.Talk;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.repository.TalkRepository;
import ru.itmo.wp.model.repository.impl.TalkRepositoryImpl;

import java.util.*;
import java.util.stream.Collectors;

public class TalkService {
    TalkRepository talkRepository = new TalkRepositoryImpl();

    public List<Talk> findAll() {
        return talkRepository.findAll();
    }

    public List<Talk> findByUser(User user) {
        List<Talk> talks = talkRepository.findBy(Map.of("sourceUserId", user.getId()));
        Set<Long> talkIds = talks.stream().map(Talk::getId).collect(Collectors.toSet());
        List<Talk> targetTalks = talkRepository.findBy(Map.of("targetUserId", user.getId())).stream()
                .filter(talk -> !talkIds.contains(talk.getId())).collect(Collectors.toList());
        talks.addAll(targetTalks);
        talks.sort(Comparator.comparing(Talk::getCreationTime).reversed());
        return talks;
    }


    public void save(Talk talk) {
        talkRepository.save(talk);
    }
}
