package com.app.shared.dto.response;

import java.util.List;

public record CursorPage<T>(List<T> data, String cursor, boolean hasNext) { }
