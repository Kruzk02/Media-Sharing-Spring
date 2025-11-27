package com.app.module.pin.application.service;

import com.app.module.pin.application.dto.PinRequest;
import com.app.module.pin.domain.Pin;
import com.app.shared.type.DetailsType;
import com.app.shared.type.SortType;
import java.io.IOException;
import java.util.List;

public interface PinService {

  List<Pin> getAllPins(SortType sortType, int limit, int offset);

  List<Pin> getAllPinsByHashtag(String tag, int limit, int offset);

  Pin save(PinRequest pinRequest);

  Pin update(Long id, PinRequest pinRequest);

  Pin findById(Long id, DetailsType detailsType);

  List<Pin> findPinByUserId(Long userId, int limit, int offset);

  void delete(Long id) throws IOException;
}
