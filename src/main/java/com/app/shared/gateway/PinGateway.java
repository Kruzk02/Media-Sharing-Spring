package com.app.shared.gateway;

import com.app.shared.dto.response.PinDTO;
import java.util.List;
import org.springframework.modulith.NamedInterface;

@NamedInterface
public interface PinGateway {
  List<PinDTO> getPinsByIds(List<Long> ids);

  PinDTO getPinById(Long pinId);
}
