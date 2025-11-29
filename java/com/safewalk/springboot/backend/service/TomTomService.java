package com.safewalk.springboot.backend.service;

import java.util.List;
import java.util.Map;

public interface TomTomService {
    List<Map<String, Object>> getRoutes(String source, String destination, int alternatives);
}
