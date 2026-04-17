package com.app.shared.gateway;

import com.app.shared.dto.response.PinDTO;
import org.springframework.modulith.NamedInterface;

import java.util.List;

@NamedInterface
public interface PinGateway {
  List<PinDTO> getPinsByIds(List<Long> ids);

  PinDTO getPinById(Long pinId);
}
